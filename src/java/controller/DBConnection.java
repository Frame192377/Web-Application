package controller;

import java.sql.*;
import model.Student;

/**
 *
 * @author Tael-
 */
public class DBConnection {

    private static final String DB_URL = "jdbc:mysql://localhost:3307/studentdb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "UBU_Fbs192377";

    // Insert a new student record
    public boolean insertNewStudent(Student student) {
        boolean result = false;
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String query = "INSERT INTO student (id, name, age) VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, student.getId());
                statement.setString(2, student.getName());
                statement.setInt(3, student.getAge());

                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    result = true;
                }
            }
        } catch (ClassNotFoundException | SQLException exception) {
            System.out.println(exception);
        }

        return result;
    }

    // Retrieve a student record by ID
    public Student getStudent(String id) {
        Student student = null;
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String query = "SELECT * FROM student WHERE id=?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, id);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        student = new Student();
                        student.setId(resultSet.getString("id").trim());
                        student.setName(resultSet.getString("name").trim());
                        student.setAge(resultSet.getInt("age"));
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException exception) {
            System.out.println(exception);
        }

        return student;
    }
}
