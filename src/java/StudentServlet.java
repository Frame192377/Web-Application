import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Student;

@WebServlet(urlPatterns = {"/StudentServlet"})
public class StudentServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response, ArrayList<Student> searchResult)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Student List</title>");
            out.println("</head>");
            out.println("<body>");

            // ตรวจสอบว่ามีผลการค้นหาหรือไม่
            if (searchResult != null && !searchResult.isEmpty()) {
                out.println("<h2>Search Result:</h2>");
                for (Student student : searchResult) {
                    out.println("<p>" + student.getId() + " - " + student.getName() + " - " + student.getAge() + "</p>");
                }
            } else if (searchResult != null) {
                out.println("<h2>No student found with the given ID.</h2>");
                out.println("<a href='index.html'>Add New Student</a>");
            }

            out.println("<h1>Student List</h1>");
            ArrayList<Student> studentList = DatabaseManager.getAllStudents();
            if (studentList.isEmpty()) {
                out.println("<p>No students available. Please add new students.</p>");
            } else {
                out.println("<ul>");
                for (Student student : studentList) {
                    out.println("<li>" + student.getId() + " - " + student.getName() + " - " + student.getAge()
                            + " <a href='StudentServlet?action=edit&id=" + student.getId() + "'>Edit</a></li>");
                }
                out.println("</ul>");
            }
            out.println("<a href='index.html'>Add New Student</a>");
            out.println("</body>");
            out.println("</html>");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        String searchId = request.getParameter("searchId");

        if (searchId != null && !searchId.isEmpty()) {
            try {
                Student student = DatabaseManager.findStudentById(searchId);
                ArrayList<Student> searchResult = new ArrayList<>();
                if (student != null) {
                    searchResult.add(student);
                }
                processRequest(request, response, searchResult); // แสดงผลเฉพาะผลลัพธ์การค้นหา
            } catch (SQLException e) {
                throw new ServletException(e);
            }
        } else if (action != null && action.equals("edit")) {
            // การแก้ไขนักเรียน
            String id = request.getParameter("id");
            try {
                Student student = DatabaseManager.findStudentById(id);
                if (student != null) {
                    showEditForm(response, student);
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Student not found");
                }
            } catch (SQLException e) {
                throw new ServletException(e);
            }
        } else {
            processRequest(request, response, null); // แสดงรายการนักเรียนทั้งหมด
        }
    }

    // แสดงฟอร์มแก้ไขนักเรียน
    private void showEditForm(HttpServletResponse response, Student student) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Edit Student</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Edit Student</h1>");
            out.println("<form action='StudentServlet' method='post'>");
            out.println("<input type='hidden' name='action' value='update'>");
            out.println("<input type='hidden' name='oldId' value='" + student.getId() + "'>");
            out.println("<label for='id'>ID:</label>");
            out.println("<input type='text' id='id' name='id' value='" + student.getId() + "'><br>");
            out.println("<label for='name'>Name:</label>");
            out.println("<input type='text' id='name' name='name' value='" + student.getName() + "'><br>");
            out.println("<label for='age'>Age:</label>");
            out.println("<input type='number' id='age' name='age' value='" + student.getAge() + "'><br>");
            out.println("<input type='submit' value='Update Student'>");
            out.println("</form>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // จัดการ POST Requests (เพิ่มและอัปเดตข้อมูลนักเรียน)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action != null && action.equals("update")) {
            try {
                // อัปเดตข้อมูลนักเรียน
                updateStudent(request, response);
            } catch (SQLException ex) {
                Logger.getLogger(StudentServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            // เพิ่มข้อมูลนักเรียนใหม่
            String name = request.getParameter("name");
            String ageStr = request.getParameter("age");
            String id = request.getParameter("id");

            try {
                int age = Integer.parseInt(ageStr);  // แปลง age ให้เป็น int
                Student newStudent = new Student(id, name, age);
                DatabaseManager.insertStudent(newStudent);  // เพิ่มข้อมูลนักเรียน
                response.sendRedirect("StudentServlet");  // กลับไปที่หน้ารายการนักเรียน
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid age value");
            } catch (SQLException e) {
                throw new ServletException(e);
            }
        }
    }

    // ฟังก์ชันสำหรับอัปเดตข้อมูลนักเรียน
    private void updateStudent(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        String oldId = request.getParameter("oldId");
        String newId = request.getParameter("id");
        String name = request.getParameter("name");
        String ageStr = request.getParameter("age");

        try {
            int age = Integer.parseInt(ageStr);  // แปลง age ให้เป็น int
            Student student = DatabaseManager.findStudentById(oldId);  // ค้นหานักเรียนโดยใช้ oldId
            if (student != null) {
                student.setId(newId);  // อัปเดต ID
                student.setName(name);  // อัปเดตชื่อ
                student.setAge(age);  // อัปเดตอายุ
                DatabaseManager.updateStudent(student);  // อัปเดตในฐานข้อมูล
                response.sendRedirect("StudentServlet");  // กลับไปที่หน้ารายการนักเรียน
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Student not found");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid age format");
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        }
    }
}
