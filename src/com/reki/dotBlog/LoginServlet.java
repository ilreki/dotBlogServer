package com.reki.dotBlog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;
import org.json.JSONObject;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public LoginServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JSONObject jsonOut = new JSONObject();
        JSONObject jsonIn = new JSONObject();
        String params[] = new String[2];
        if(request.getParameter("name") == null) {
        	// 读取请求内容
     		BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"utf-8"));
     		String line = "";
     		StringBuilder sb = new StringBuilder();
     		while ((line = br.readLine()) != null) {
     			sb.append(line + "\n");
     		}
     		System.out.println("sb=======" + sb.toString());
     		//将json字符串转换为json对象
     		try {
     			jsonIn = new JSONObject(sb.toString());
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
     		
     		params[0] = jsonIn.getString("name");
     		params[1] = jsonIn.getString("password");
        } else {
        	params[0] = request.getParameter("name");
     		params[1] = request.getParameter("password");
		}  
        
 
        String sql = "SELECT u_name, u_name FROM T_UserInfo where u_name=? and u_pwd=?";
        Connection con = DBConnectionHandler.getConnection();
         
        try {
            PreparedStatement ps = con.prepareStatement(sql);
           
            ps.setString(1, params[0]);
            ps.setString(2, params[1]);
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                jsonOut.put("info", "success");
            } else {
                jsonOut.put("info", "fail");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonOut.toString());
	}

}
