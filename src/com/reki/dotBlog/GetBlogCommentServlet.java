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

@WebServlet("/GetBlogCommentServlet")
public class GetBlogCommentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public GetBlogCommentServlet() {
        super();
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONArray jsonArray = new JSONArray();
        JSONObject jsonIn = new JSONObject();
        int start, count;
        Long blogID;
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
 		start = jsonIn.getInt("start");
 		count = jsonIn.getInt("count");
 		
 		String sql = "select CP_ID, CP_PublisherID, CP_Content, CP_Date from t_commentpublish"
 				+ " where CP_BlogID = ? order by CP_Date asc limit ?, ?";
 		
        Connection con = DBConnectionHandler.getConnection();
         
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            
            ps.setLong(1, blogID);
            ps.setInt(2, start);
            ps.setInt(3, count);
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
            	long commentID = rs.getLong("CP_ID");
				long publisherID = rs.getLong("CP_PublisherID");
				sql = "select UI_Name, UI_Avatar from t_userinfo where UI_ID = ?";
		        ps = con.prepareStatement(sql);
		        ps.setLong(1, publisherID);
		        
		        ResultSet userInfo = ps.executeQuery();
		        JSONObject jsonOut = new JSONObject();
		        if(userInfo.next()) {
		        	jsonOut.put("commentID", commentID);
	        		jsonOut.put("commentDate", rs.getDate("CP_Date").toString());
		        	jsonOut.put("commentContent", rs.getString("CP_Content"));
		        	jsonOut.put("publisherName", userInfo.getString("UI_Name"));
		        	jsonOut.put("publisherAvatar", userInfo.getString("UI_Avatar"));
		        }
		        
		        sql = "select count(RP_ID) as replyCount from t_replypublish"
		        		+ " where RP_CommentID = ?";
		        ps = con.prepareStatement(sql);
		        ps.setLong(1, commentID);
		        
		        ResultSet replyInfo = ps.executeQuery();
		        if(replyInfo.next()) {
		        	jsonOut.put("commentReplyCount", replyInfo.getInt("replyCount"));
		        } else {
		        	jsonOut.put("commentReplyCount", 0);
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
