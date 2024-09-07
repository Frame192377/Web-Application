import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DatabaseManager {

    private static final String URL = "jdbc:mysql://localhost:3307/studentdb";  // URL ของฐานข้อมูล
    private static final String USER = "root";  // ชื่อผู้ใช้งานฐานข้อมูล
    private static final String PASSWORD = "UBU_Fbs192377";  // รหัสผ่านของฐานข้อมูล

    // เชื่อมต่อกับฐานข้อมูล
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");  // โหลดไดรเวอร์ MySQL
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // เพิ่มข้อมูลนักเรียนใหม่
    public static void insertStudent(model.Student student) throws SQLException {
        String query = "INSERT INTO students (id, name, age) VALUES (?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, student.getId());
            stmt.setString(2, student.getName());
            stmt.setInt(3, student.getAge());
            stmt.executeUpdate();
        }
    }

    // อัปเดตข้อมูลนักเรียน
    public static void updateStudent(model.Student student) throws SQLException {
        String query = "UPDATE students SET name = ?, age = ? WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, student.getName());
            stmt.setInt(2, student.getAge());
            stmt.setString(3, student.getId());
            stmt.executeUpdate();
        }
    }

    // ค้นหานักเรียนจาก ID
    public static model.Student findStudentById(String id) throws SQLException {
        String query = "SELECT * FROM students WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new model.Student(rs.getString("id"), rs.getString("name"), rs.getInt("age"));
                }
            }
        }
        return null;  // ถ้าไม่เจอนักเรียน
    }

    // ดึงรายชื่อนักเรียนทั้งหมด
    public static ArrayList<model.Student> getAllStudents() throws SQLException {
        ArrayList<model.Student> studentList = new ArrayList<>();
        String query = "SELECT * FROM students";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                studentList.add(new model.Student(rs.getString("id"), rs.getString("name"), rs.getInt("age")));
            }
        }
        return studentList;
    }
}
