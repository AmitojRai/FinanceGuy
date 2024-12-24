package com.financeguy.controller;
import com.financeguy.DatabaseHelper;
import com.financeguy.model.Expense;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller class for MainView.fxml. Handles user interaction and updates the UI components.
 */
public class MainViewController {
    @FXML
    private TableView<Expense> expenseTable;
    @FXML
    private TableColumn<Expense, String> categoryColumn;
    @FXML
    private TableColumn<Expense, String> descriptionColumn;
    @FXML
    private TableColumn<Expense, Double> amountColumn;
    @FXML
    private TableColumn<Expense, LocalDate> dateColumn;
    @FXML
    private PieChart expensePieChart;
    @FXML
    private Label totalAmountLabel;
    @FXML
    private ComboBox<String> categoryFilter;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;

    //Model for the displayed expenses
    private ObservableList<Expense> expenseData = FXCollections.observableArrayList();

    //Sets up table columns, loads data and initializes filters and the chart.
    @FXML
    private void initialize() {
        //initialize the expense table columns
        categoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        amountColumn.setCellValueFactory(cellData -> cellData.getValue().amountProperty().asObject());
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());

        //load the expenses from the database
        loadExpensesFromDatabase();

        //put the expense data in the table
        expenseTable.setItems(expenseData);

        //initialize the category filter
        initializeCategoryFilter();

        //initialize PieChart and the total amount
        updatePieChart();
        updateTotalAmount();
    }



     //loads expenses from the database into the expense data list.
    private void loadExpensesFromDatabase() {
        expenseData.clear();
        expenseData.addAll(DatabaseHelper.getAllExpenses());
    }

    //Collects different categories from the database for users to filter through
    private void initializeCategoryFilter() {
        //Get a set of distinct and normalized categories from all expenses
        List<String> distinctCategories = DatabaseHelper.getAllExpenses().stream()
                .map(Expense::getCategory)
                .distinct()
                .collect(Collectors.toList());

        //Capitalize the first letter
        List<String> displayCategories = distinctCategories.stream()
                .map(this::capitalizeFirstLetter)
                .collect(Collectors.toList());

        //Add an "All" filter option at the top
        displayCategories.add(0, "All");

        //Setting the default category to "All" and making the ComboBox
        categoryFilter.setItems(FXCollections.observableArrayList(displayCategories));
        categoryFilter.setValue("All");  // Default is to show all categories
    }



     //Updates the PieChart based on the current expenses.
    private void updatePieChart() {
        //clears existing pie chart slices
        expensePieChart.getData().clear();

        //group expenses by the normalized category field
        Map<String, Double> categoryTotals = expenseData.stream()
                .collect(Collectors.groupingBy(
                        Expense::getCategory, Collectors.summingDouble(Expense::getAmount)
                ));

        //convert each category field to a capitalized string for display aesthetic
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            String displayCategory = capitalizeFirstLetter(entry.getKey());
            PieChart.Data slice = new PieChart.Data(displayCategory, entry.getValue());
            expensePieChart.getData().add(slice);
        }
    }



     //Gets the sum of all expenses displayed in the expenseData and shows it in the total amount label
    private void updateTotalAmount() {
        double total = expenseData.stream().mapToDouble(Expense::getAmount).sum();
        totalAmountLabel.setText(String.format("Total Expenses: $%.2f", total));
    }

    //capitalizes the first letter of a string. For aesthetic purposes
    private String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    //Method to handle "Add Expense" button
    @FXML
    private void handleAddExpense() {
        //opens the user input dialog and will return a new expense if successful or nothing if it is canceled
        Expense newExpense = showExpenseDialog(null);
        if (newExpense != null) {
            //add the new expense to the database
            DatabaseHelper.addExpense(newExpense);
            //refreshes the UI and updates the components
            loadExpensesFromDatabase();
            initializeCategoryFilter();
            updatePieChart();
            updateTotalAmount();
        }
    }

    //method that helps with adding/editing expenses using a dialog
    private Expense showExpenseDialog(Expense existingExpense) {
        Dialog<Expense> dialog = new Dialog<>();

        if (existingExpense == null) {
            dialog.setTitle("Add Expense");
        } else {
            dialog.setTitle("Edit Expense");
        }

        ButtonType submitButtonType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);

        //creating text fields for the category, description, amount and date
        TextField categoryField = new TextField();
        TextField descriptionField = new TextField();
        TextField amountField = new TextField();
        DatePicker datePicker = new DatePicker(LocalDate.now());

        //If editing an existing expense then we can pre-fill the fields
        if (existingExpense != null) {
            categoryField.setText(existingExpense.getCategory());
            descriptionField.setText(existingExpense.getDescription());
            amountField.setText(String.valueOf(existingExpense.getAmount()));
            datePicker.setValue(existingExpense.getDate());
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Category:"), 0, 0);
        grid.add(categoryField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionField, 1, 1);
        grid.add(new Label("Amount:"), 0, 2);
        grid.add(amountField, 1, 2);
        grid.add(new Label("Date:"), 0, 3);
        grid.add(datePicker, 1, 3);

        dialog.getDialogPane().setContent(grid);

        //convert result to an expense
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == submitButtonType) {
                String category = categoryField.getText();
                String description = descriptionField.getText();
                double amount;
                try {
                    amount = Double.parseDouble(amountField.getText());
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Amount", null, "Please enter a valid number for the amount.");
                    return null;
                }
                LocalDate date = datePicker.getValue();

                //if we are adding a new expense
                if (existingExpense == null) {
                    return new Expense(category, description, amount, date);
                } else {
                    //if we are editing, preserve the expense’s ID
                    return new Expense(existingExpense.getId(), category, description, amount, date);
                }
            }
            return null;
        });

        Optional<Expense> result = dialog.showAndWait();
        return result.orElse(null);
    }



   //handles the edit expense button
    @FXML
    private void handleEditExpense() {
        Expense selectedExpense = expenseTable.getSelectionModel().getSelectedItem();
        //if we do not have an expense selected, we return an error prompt
        if (selectedExpense == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", null, "No Expense Selected. Please select an expense to edit.");
            return;
        }
        //pass the existing expense to the dialog
        Expense updatedExpense = showExpenseDialog(selectedExpense);
        if (updatedExpense != null) {
            //update the expense in the database
            DatabaseHelper.updateExpense(updatedExpense);
            loadExpensesFromDatabase();
            initializeCategoryFilter();
            updatePieChart();
            updateTotalAmount();
        }
    }



   //handles the delete expense button action
    @FXML
    private void handleDeleteExpense() {
        Expense selectedExpense = expenseTable.getSelectionModel().getSelectedItem();
        //if we do not have an expense selected, we return an error prompt
        if (selectedExpense == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", null, "No Expense Selected. Please select an expense to delete.");
            return;
        }

        //Alert messages that we show the user to confirm that they want to proceed with deletion
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Delete Expense");
        confirmationAlert.setHeaderText("Are you sure you want to delete this expense?");
        confirmationAlert.setContentText(selectedExpense.getDescription());

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            //delete the expense by ID
            DatabaseHelper.deleteExpense(selectedExpense.getId());
            loadExpensesFromDatabase();
            initializeCategoryFilter();
            updatePieChart();
            updateTotalAmount();
        }
    }

   //handles when we click Load Sample Data under the file section
    @FXML
    private void handleLoadSampleData() {
        //confirm with the user before loading sample data
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Load Sample Data");
        //set header text and body text separately
        confirmationAlert.setHeaderText("Are you sure you want to load sample data?");
        confirmationAlert.setContentText("This will add sample expenses to your current data.");

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            //load the sample data
            DatabaseHelper.insertSampleData();
            //refresh the data in the table and charts and update UI components
            loadExpensesFromDatabase();
            initializeCategoryFilter();
            updatePieChart();
            updateTotalAmount();

            //inform the user the data has been loaded
            showAlert(Alert.AlertType.INFORMATION, "Sample Data Loaded", null, "Sample data has been loaded successfully.");
        }
    }


     //handles the "Clear All Data" action.
    @FXML
    private void handleClearAllData() {
        //confirm with the user before we clear all existing data
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Clear All Data");
        confirmationAlert.setHeaderText("Are you sure you want to clear all data?");
        confirmationAlert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            //clear all the data from the database
            boolean success = DatabaseHelper.clearDatabase();
            if (success) {
                //refresh and update the UI
                loadExpensesFromDatabase();
                initializeCategoryFilter();
                updatePieChart();
                updateTotalAmount();

                //inform the user the data has been cleared
                showAlert(Alert.AlertType.INFORMATION, "Data Cleared", null, "All expense data has been cleared.");
            } else {
                //in the case of failure, inform the user of the failure
                showAlert(Alert.AlertType.ERROR, "Error", "Data Clearing Failed", "There was an error clearing the expense data.");
            }
        }
    }


     //handles the "Exit" action.
    @FXML
    private void handleExit() {
        //closes the application
        Stage stage = (Stage) expenseTable.getScene().getWindow();
        stage.close();
    }

    //Shows an alert message for warnings and to provide the user with information
    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        if (header != null && !header.isEmpty()) {
            alert.setHeaderText(header);
        } else {
            alert.setHeaderText(null);
        }
        alert.setContentText(content);
        alert.showAndWait();
    }


    //handles filtering the expenses by categories and date. Updates UI accordingly.
    @FXML
    private void handleFilter() {
        //loading the selected category value and date range values
        String selectedCategory = categoryFilter.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        //Get all the expenses from Data Base
        ObservableList<Expense> allExpenses = FXCollections.observableArrayList(DatabaseHelper.getAllExpenses());

        //filtering by category with an exception to if “All” is selected
        if (selectedCategory != null && !selectedCategory.isEmpty() && !selectedCategory.equals("All")) {
            String normalizedCategory = selectedCategory.toLowerCase();
            allExpenses = allExpenses.filtered(e -> e.getCategory().equals(normalizedCategory));
        }

        //filtering by the start date
        if (startDate != null) {
            allExpenses = allExpenses.filtered(e -> !e.getDate().isBefore(startDate));
        }

        //filtering by the end date
        if (endDate != null) {
            allExpenses = allExpenses.filtered(e -> !e.getDate().isAfter(endDate));
        }

        //Update the expense data with the final filtered options
        expenseData.setAll(allExpenses);

        updatePieChart();
        updateTotalAmount();
    }

}
