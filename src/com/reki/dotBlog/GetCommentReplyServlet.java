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

@WebServlet("/GetCommentReplyServlet")
public class GetCommentReplyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public GetCommentReplyServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONArray jsonArray = new JSONArray();
        JSONObject jsonIn = new JSONObject();
        int start, count;
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
 		start = jsonIn.getInt("start");
 		count = jsonIn.getInt("count");
 		
 		String sql = "select RP_ID, RP_PublisherID, RP_Content, RP_Date from t_replypublish"
 				+ " where RP_CommentID = ? order by RP_Date asc limit ?, ?";
 		
        Connection con = DBConnectionHandler.getConnection();
         
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            
            ps.setLong(1, commentID);
            ps.setInt(2, start);
            ps.setInt(3, count);
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
				long publisherID = rs.getLong("RP_PublisherID");
				sql = "select UI_Name, UI_Avatar from t_userinfo where UI_ID = ?";
		        ps = con.prepareStatement(sql);
		        ps.setLong(1, publisherID);
		        
		        ResultSet userInfo = ps.executeQuery();
		        JSONObject jsonOut = new JSONObject();
		        if(userInfo.next()) {
		        	jsonOut.put("replyID", rs.getLong("RP_ID"));
	        		jsonOut.put("replyDate", rs.getDate("RP_Date").toString());
		        	jsonOut.put("replyContent", rs.getString("RP_Content"));
		        	jsonOut.put("publisherID", rs.getLong("RP_PublisherID"));
		        	jsonOut.put("publisherName", userInfo.getString("UI_Name"));
		        	jsonOut.put("publisherAvatar", userInfo.getString("UI_Avatar"));
		        }
		        
		       	jsonArray.put(jsonOut);
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonArray.toString());
	}

}
