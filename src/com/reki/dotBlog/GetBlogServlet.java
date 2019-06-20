package com.reki.dotBlog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.org.apache.bcel.internal.generic.NEW;

@WebServlet("/GetBlogServlet")
public class GetBlogServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public GetBlogServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject jsonOut = new JSONObject();
        JSONObject jsonIn = new JSONObject();
      	long blogID;
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
        blogID = jsonIn.getLong("blogID");
 
        String sql = "select BP_ID, BP_PublisherID, BP_Title, BP_Content, BP_Date from t_blogpublish where BP_ID = ? and BP_Status = ?";
        Connection con = DBConnectionHandler.getConnection();
         
        try {
            PreparedStatement ps = con.prepareStatement(sql);
           
            ps.setLong(1, blogID);
            ps.setInt(2, 1);
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
				long userID = rs.getLong("BP_PublisherID");
				sql = "select UI_ID, UI_Name, UI_Avatar from t_userinfo where UI_ID = ?";
		        ps = con.prepareStatement(sql);
		        ps.setLong(1, userID);
		        
		        ResultSet userInfo = ps.executeQuery();
		        
		        if(userInfo.next()) {
		        	jsonOut.put("blogID", blogID);
		        	jsonOut.put("publisherID", userID);
		        	jsonOut.put("publisherName", userInfo.getString("UI_Name"));
		        	jsonOut.put("publisherAvatar", userInfo.getString("UI_Avatar"));
		        	jsonOut.put("blogTitle", rs.getString("BP_Title"));
		        	jsonOut.put("blogContent", rs.getString("BP_Content").toString());
		        	jsonOut.put("blogDate", rs.getDate("BP_Date").toString());
		        }
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonOut.toString());
	}

}
