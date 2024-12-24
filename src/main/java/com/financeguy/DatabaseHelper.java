package com.financeguy;

import com.financeguy.model.Expense;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//This class will manage all the database operations like initializing tables, adding/deleting expenses.
public class DatabaseHelper {

    //path to SQLite database file
    private static final String DB_URL = "jdbc:sqlite:financeguy.db";

    //making the connection to the SQLite database
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }


    //Creates the expense table if it does not already exist
    public static void initializeDatabase() {
        //using SQL statement to create table named "expenses" with specific columns if they do not exist
        String createTableSQL = "CREATE TABLE IF NOT EXISTS expenses (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "category TEXT NOT NULL," +
                "description TEXT," +
                "amount REAL NOT NULL," +
                "date TEXT NOT NULL" +
                ");";

        //make sure the connection and statement are closed automatically.
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            //if any issues print the error details
            e.printStackTrace();
        }
    }

    //getter method to retrieve all the expenses from the database
    public static List<Expense> getAllExpenses() {
        //Create a list to store the collected expense objects
        List<Expense> expenses = new ArrayList<>();

        //SQL query to select all columns from the "expenses" table
        String query = "SELECT * FROM expenses";
        //Make sure Connection, Statement and ResultSet are closed
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            //while loop to loop through each row in the result set
            while (rs.next()) {
                //converting the row data into expense object
                Expense expense = new Expense(
                        rs.getInt("id"),
                        rs.getString("category"),
                        rs.getString("description"),
                        rs.getDouble("amount"),
                        LocalDate.parse(rs.getString("date")) //convert text date to LocalDate
                );
                expenses.add(expense); //add the built expense object to the "expenses" list
            }
        } catch (SQLException e) {
            //catch any sql errors and print them
            e.printStackTrace();
        }
        return expenses; //return list
    }


    //add expense to database
    public static void addExpense(Expense expense) {
        //insert a new row with the specified columns
        String insertSQL = "INSERT INTO expenses (category, description, amount, date) VALUES (?, ?, ?, ?)";
        //automatically close the Connection and PreparedStatement
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            //set each parameter in the insert SQL statement with values from expense object
            pstmt.setString(1, expense.getCategory());
            pstmt.setString(2, expense.getDescription());
            pstmt.setDouble(3, expense.getAmount());
            pstmt.setString(4, expense.getDate().toString());
            pstmt.executeUpdate(); //executing the insert statement to add a new row in "expenses" table
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //clears all the data from the expense table
    public static boolean clearDatabase() {
        //delete all rows from the table
        String deleteSQL = "DELETE FROM expenses";
        //automatic cleanup
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(deleteSQL);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //inserts sample data into the expense table
    public static void insertSampleData() {
        List<Expense> sampleExpenses = new ArrayList<>();
        //making sample expenses of three different categories
        sampleExpenses.add(new Expense("Food", "Lunch at cafe", 12.50, LocalDate.now().minusDays(1)));
        sampleExpenses.add(new Expense("Travel", "Taxi ride", 25.00, LocalDate.now().minusDays(2)));
        sampleExpenses.add(new Expense("Entertainment", "Netflix Bill", 15.00, LocalDate.now().minusDays(3)));

        //adding sample expenses into the database
        for (Expense expense : sampleExpenses) {
            addExpense(expense);
        }
    }

    //updates existing expenses in the expense table
    public static void updateExpense(Expense expense) {
        //update an existing row and match it by ID
        String updateSQL = "UPDATE expenses SET category = ?, description = ?, amount = ?, date = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {

            //Bind the fields of the expense object to the corresponding parameter in the update statement
            pstmt.setString(1, expense.getCategory());
            pstmt.setString(2, expense.getDescription());
            pstmt.setDouble(3, expense.getAmount());
            pstmt.setString(4, expense.getDate().toString());
            pstmt.setInt(5, expense.getId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //deletes a single expense from the database given the unique ID it possesses
    public static void deleteExpense(int id) {
        //Removes single matching row by ID
        String deleteSQL = "DELETE FROM expenses WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {

            //bounds the ID
            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
