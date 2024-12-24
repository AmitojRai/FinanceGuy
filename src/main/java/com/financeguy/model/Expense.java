package com.financeguy.model;


import javafx.beans.property.*;
import java.time.LocalDate;

//class that manages an individual expense entry
public class Expense {

    private final IntegerProperty id; //unique id for expense
    private final StringProperty category;
    private final StringProperty description;
    private final DoubleProperty amount;
    private final ObjectProperty<LocalDate> date;


    //The full constructor that uses database ID
    public Expense(int id, String category, String description, double amount, LocalDate date) {
        //Assigning the id field
        this.id = new SimpleIntegerProperty(this, "id", id);
        this.category = new SimpleStringProperty(this, "category", normalizeCategory(category)); //using normalizeCategory to consider mismatch issues with input such as "Food" and "food" being read as separate
        this.description = new SimpleStringProperty(this, "description", description);
        this.amount = new SimpleDoubleProperty(this, "amount", amount);
        this.date = new SimpleObjectProperty<>(this, "date", date);
    }

    //constructor when we create an expense before ID
    public Expense(String category, String description, double amount, LocalDate date) {
        this.id = new SimpleIntegerProperty(this, "id");
        // CALL normalizeCategory(...) here as well
        this.category = new SimpleStringProperty(this, "category", normalizeCategory(category));
        this.description = new SimpleStringProperty(this, "description", description);
        this.amount = new SimpleDoubleProperty(this, "amount", amount);
        this.date = new SimpleObjectProperty<>(this, "date", date);
    }



    //lowercasing to avoid mismatches
    private String normalizeCategory(String category) {
        if (category == null) return "";
        return category.trim().toLowerCase();
    }

    //getter method for ID
    public int getId() {
        return id.get();
    }

    //getter method for the normalized category name
    public String getCategory() {
        return category.get();
    }

    //updates the category of the expense
    public void setCategory(String category) {
        this.category.set(normalizeCategory(category));
    }

    //returns the JavaFX string property for category
    public StringProperty categoryProperty() {
        return category;
    }

    // getter method to retrieve description of the expense
    public String getDescription() {
        return description.get();
    }

    //getter method to change description of the expense
    public void setDescription(String description) {
        this.description.set(description);
    }

    //string property for description
    public StringProperty descriptionProperty() {
        return description;
    }

    //getter method returns the amount of an expense
    public double getAmount() {
        return amount.get();
    }

    //changes the amount of an expense
    public void setAmount(double amount) {
        this.amount.set(amount);
    }

    //double property for amount of an expense
    public DoubleProperty amountProperty() {
        return amount;
    }

    //getter method that returns date of when an expense occured
    public LocalDate getDate() {
        return date.get();
    }

    //sets the date of an expense
    public void setDate(LocalDate date) {
        this.date.set(date);
    }

    //Object property for date
    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }


}
