package services;

import controller.DBConnection;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import model.Student;
import org.json.JSONObject;

public class GetStudentService extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        
        // Extract student ID from the URL path
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.startsWith("/")) {
            String studentId = pathInfo.substring(1);
            studentId = URLDecoder.decode(studentId, "UTF-8");
            
            // Create a DBConnection object and fetch student data
            DBConnection dbConnection = new DBConnection();
            Student student = dbConnection.getStudent(studentId);
            
            // Prepare the response in JSON format
            PrintWriter out = response.getWriter();
            if (student != null) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", student.getId());
                jsonObject.put("name", student.getName());
                jsonObject.put("age", student.getAge());
                
                out.print(jsonObject.toString());
            } else {
                // Send an empty JSON object if student not found
                out.print("{}");
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Servlet to get student details";
    }
}
