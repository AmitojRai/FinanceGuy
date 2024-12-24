package com.financeguy.controller;
import com.financeguy.model.Expense;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDate;

/**
 * This is a controller class that manages adding a new Expense.
 * It is linked to the AddExpenseView.fxml
 */

public class AddExpenseController {
    //creating text fields to enter expense information
    @FXML private TextField categoryField;
    @FXML private TextField descriptionField;
    @FXML private TextField amountField;
    //DatePicker to allow us to select the date of an expense
    @FXML private DatePicker datePicker;

    private Expense expense;
    private boolean isConfirmed = false;

    //sets default date on the DatePicker to the current date
    @FXML
    private void initialize() {
        datePicker.setValue(LocalDate.now());
    }

    //Method runs when a user clicks "Add Expense" button and creates an expense
    @FXML
    private void handleAdd() {
        try {
            //collecting the user input from the text fields
            String category = categoryField.getText();
            String description = descriptionField.getText();
            double amount = Double.parseDouble(amountField.getText());
            LocalDate date = datePicker.getValue();

            //Making sure the fields are filled and the amount is greater than 0
            if (category.isEmpty() || description.isEmpty() || amount <= 0 || date == null) {
                showAlert("Invalid Input", "Please fill all fields correctly."); //else error message
                return;
            }
            //creating expense object based on user the user input
            expense = new Expense(category, description, amount, date);
            isConfirmed = true;

            //close the dialog
            Stage stage = (Stage) categoryField.getScene().getWindow();
            stage.close();
        } catch (NumberFormatException e) {
            //If the amount field does not receive a valid input of type double we return this message
            showAlert("Invalid Amount", "Please enter a valid number for amount.");
        }
    }

    //method for when user clicks cancel button
    @FXML
    private void handleCancel() {
       //closes the dialog box
        Stage stage = (Stage) categoryField.getScene().getWindow();
        stage.close();
    }

    //boolean method to check whether the user confirmed adding the expense
    public boolean isConfirmed() {
        return isConfirmed;
    }

    //getter method to return expense
    public Expense getExpense() {
        return expense;
    }

    //Method that shows error prompts for invalid inputs
    private void showAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        //attach alert to window that holds the category field
        alert.initOwner(categoryField.getScene().getWindow());
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
