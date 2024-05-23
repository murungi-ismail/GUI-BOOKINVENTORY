package vu.booksinventory;
import java.sql.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

public class Booksinventory {

    public static void main(String[] args) {
        try {
            new LibrarySystem();
        } catch (SQLException ex) {
            Logger.getLogger(Booksinventory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

class LibrarySystem extends JFrame {

    private Connection conn;
    private JTable table;
    private JTextField bookIDField, titleField, authorField, yearField;

    public LibrarySystem() throws SQLException {
        // Establish a connection to the database
        conn = DriverManager.getConnection("jdbc:ucanaccess://C:/Users/Lenovo/Desktop/librarydatabase.accdb");

        // Create the GUI components
        setTitle("Library System");
        setLayout(new BorderLayout());

        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(4, 2));
        formPanel.add(new JLabel("Book ID:"));
        bookIDField = new JTextField();
        formPanel.add(bookIDField);
        formPanel.add(new JLabel("Title:"));
        titleField = new JTextField();
        formPanel.add(titleField);
        formPanel.add(new JLabel("Author:"));
        authorField = new JTextField();
        formPanel.add(authorField);
        formPanel.add(new JLabel("Year:"));
        yearField = new JTextField();
        formPanel.add(yearField);

        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton addBookButton = new JButton("Add Book");
        addBookButton.addActionListener(new AddBookListener());
        buttonPanel.add(addBookButton);
        JButton deleteBookButton = new JButton("Delete Book");
        deleteBookButton.addActionListener(new DeleteBookListener());
        buttonPanel.add(deleteBookButton);
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new RefreshListener());
        buttonPanel.add(refreshButton);

        // Table panel
        table = new JTable();
        JScrollPane tableScrollPane = new JScrollPane(table);

        // Add components to the frame
        add(formPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(tableScrollPane, BorderLayout.SOUTH);

        // Set the size and visibility of the frame
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        // Load the book data into the table
        loadBookData();
    }

    private void loadBookData() {
        try {
            // Retrieve and display all book records in the table
            String sql = "SELECT * FROM Books";
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(sql);

            // Create table model to hold data
            DefaultTableModel model = new DefaultTableModel(new String[]{"Book ID", "Title", "Author", "Year"}, 0);
            while (result.next()) {
                int bookID = result.getInt("BookID");
                String title = result.getString("Title");
                String author = result.getString("Author");
                int year = result.getInt("Year");
                model.addRow(new Object[]{bookID, title, author, year});
            }
            table.setModel(model);
        } catch (SQLException ex) {
            Logger.getLogger(Booksinventory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private class AddBookListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                // Insert a new book record into the database
                int bookID = Integer.parseInt(bookIDField.getText());
                String title = titleField.getText();
                String author = authorField.getText();
                int year = Integer.parseInt(yearField.getText());
                String sql = "INSERT INTO Books (BookID, Title, Author, Year) VALUES (?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, bookID);
                stmt.setString(2, title);
                stmt.setString(3, author);
                stmt.setInt(4, year);
                stmt.executeUpdate();
                // Clear the form fields
                bookIDField.setText("");
                titleField.setText("");
                authorField.setText("");
                yearField.setText("");
                // Refresh the table data
                loadBookData();
            } catch (SQLException ex) {
                Logger.getLogger(Booksinventory.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter valid data in all fields.");
            }
        }
    }

    private class DeleteBookListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                // Delete the selected book record from the database
                int bookID = Integer.parseInt(bookIDField.getText());
                String sql = "DELETE FROM Books WHERE BookID = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, bookID);
                stmt.executeUpdate();
                // Clear the form fields
                bookIDField.setText("");
                titleField.setText("");
                authorField.setText("");
                yearField.setText("");
                // Refresh the table data
                loadBookData();
            } catch (SQLException ex) {
                Logger.getLogger(Booksinventory.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter a valid Book ID.");
            }
        }
    }

    private class RefreshListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Reload the book data into the table
            loadBookData();
 }
}
}