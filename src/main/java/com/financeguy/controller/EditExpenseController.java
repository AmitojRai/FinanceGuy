package com.financeguy.controller;
import com.financeguy.model.Expense;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDate;

/**
 * This is a controller class that manages editing an expense.
 * Links to EditExpenseView.fxml
 */
public class EditExpenseController {
    //text fields for editing expense information
    @FXML private TextField categoryField;
    @FXML private TextField descriptionField;
    @FXML private TextField amountField;
    //DatePicker to edit expense info
    @FXML private DatePicker datePicker;

    private Expense expense;
    //boolean method to check whether the user has confirmed an edit
    private boolean isConfirmed = false;


    //Method to handle when user clicks the "save" button
    //Updates the expense object
    @FXML
    private void handleSave() {
        try {
            //collect user input from the text fields and DatePicker
            String category = categoryField.getText();
            String description = descriptionField.getText();
            double amount = Double.parseDouble(amountField.getText());
            LocalDate date = datePicker.getValue();

            //Condition to check if fields are empty and amount if less than 0.
            if (category.isEmpty() || description.isEmpty() || amount <= 0 || date == null) {
                //show error prompt if condition passes
                showAlert("Invalid Input", "Please fill all fields correctly.");
                return;
            }

            //update expense object
            expense.setCategory(category);
            expense.setDescription(description);
            expense.setAmount(amount);
            expense.setDate(date);

            isConfirmed = true;

            //close dialog
            Stage stage = (Stage) categoryField.getScene().getWindow();
            stage.close();
        } catch (NumberFormatException e) {
            //If the amount text field has an invalid date type show this alert (Should be a double value)
            showAlert("Invalid Amount", "Please enter a valid number for amount.");
        }
    }

    //closes dialog when user clicks cancel
    @FXML
    private void handleCancel() {
        Stage stage = (Stage) categoryField.getScene().getWindow();
        stage.close();
    }

    //Method that shows error prompts
    private void showAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(categoryField.getScene().getWindow());
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
