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

@WebServlet("/GetCommentServlet")
public class GetCommentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public GetCommentServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject jsonOut = new JSONObject();
        JSONObject jsonIn = new JSONObject();
        Long commentID;
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
 		commentID = jsonIn.getLong("commentID");
 		
 		String sql = "select CP_PublisherID, CP_Content, CP_Date from t_commentpublish"
 				+ " where CP_ID = ?";
 		
        Connection con = DBConnectionHandler.getConnection();
         
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            
            ps.setLong(1, commentID);
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
				long publisherID = rs.getLong("CP_PublisherID");
				sql = "select UI_Name, UI_Avatar from t_userinfo where UI_ID = ?";
		        ps = con.prepareStatement(sql);
		        ps.setLong(1, publisherID);
		        
		        ResultSet userInfo = ps.executeQuery();
		        if(userInfo.next()) {
		        	jsonOut.put("commentID", commentID);
		        	jsonOut.put("publisherID", rs.getLong("CP_PublisherID"));
	        		jsonOut.put("commentDate", rs.getDate("CP_Date").toString());
		        	jsonOut.put("commentContent", rs.getString("CP_Content"));
		        	jsonOut.put("publisherName", userInfo.getString("UI_Name"));
		        	jsonOut.put("publisherAvatar", userInfo.getString("UI_Avatar"));
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
