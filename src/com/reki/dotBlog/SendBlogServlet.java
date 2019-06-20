package com.reki.dotBlog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

@WebServlet("/SendBlogServlet")
public class SendBlogServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public SendBlogServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject jsonOut = new JSONObject();
        JSONObject jsonIn = new JSONObject();
        Long user_id;
        int category;
        String title, content;
        // 读取请求内容
 		BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"utf-8"));
 		String line = "";
 		StringBuilder sb = new StringBuilder();
 		while ((line = br.readLine()) != null) {
 			sb.append(line + "\n");
 		}
 		try {
 			jsonIn = new JSONObject(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
 		
 		user_id = jsonIn.getLong("user_id");
 		title = jsonIn.getString("title");
 		content = jsonIn.getString("content");
 		category = jsonIn.getInt("category");
 
        String sql = "insert into t_blogpublish(BP_PublisherID, BP_Title, BP_Content, BP_Category)"
        		+ " values(?, ?, ?, ?)";
        Connection con = DBConnectionHandler.getConnection();
         
        try {
            PreparedStatement ps = con.prepareStatement(sql);
           
            ps.setLong(1, user_id);
            ps.setString(2, title);
            ps.setString(3, content);
            ps.setInt(4, category);
            
            ps.executeUpdate();
            jsonOut.put("result", "success");
        } catch (Exception e) {
            e.printStackTrace();
            jsonOut.put("result", "fail");
        }
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonOut.toString());
	}

}
