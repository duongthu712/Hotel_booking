/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import dao.HotelPolicyDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import model.HotelPolicy;

/**
 *
 * @author Minh Thu
 */
@WebServlet(name = "HotelPolicyServlet", urlPatterns = {"/policies"}) 
public class HotelPolicyServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        HotelPolicyDAO policyDAO = new HotelPolicyDAO();
        List<HotelPolicy> fullPoliciesList = policyDAO.getAllActivePolicies();

        Map<String, List<HotelPolicy>> groupedPolicies = new LinkedHashMap<>();
        if (fullPoliciesList != null) {
            for (HotelPolicy p : fullPoliciesList) {
                String type = p.getPolicyType();
                if (!groupedPolicies.containsKey(type)) {
                    groupedPolicies.put(type, new ArrayList<>());
                }
                groupedPolicies.get(type).add(p);
            }
        }

        request.setAttribute("fullPoliciesList", fullPoliciesList);
        request.setAttribute("groupedPolicies", groupedPolicies);
        

        // Chuyển tiếp sang trang giao diện chi tiết chính sách riêng
        request.getRequestDispatcher("/view/public/policy.jsp").forward(request, response);
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
}