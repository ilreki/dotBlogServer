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

@WebServlet("/GetTopBlogServlet")
public class GetTopBlogServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public GetTopBlogServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONArray jsonArray = new JSONArray();
        JSONObject jsonIn = new JSONObject();
        int params[] = new int[2];
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
 		params[0] = jsonIn.getInt("start");
 		params[1] = jsonIn.getInt("count");
 		
 		String sql = "select 3DTB_BlogID from t_3daytopblog limit ?, ?";
        Connection con = DBConnectionHandler.getConnection();
         
        try {
            PreparedStatement ps = con.prepareStatement(sql);
           
            ps.setInt(1, params[0]);
            ps.setInt(2, params[1]);
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
				long blogID = rs.getLong("3DTB_BlogID");
				sql = "select BP_ID, BP_PublisherID, BP_Title, BP_Content, BP_Date from t_blogpublish where BP_ID = ?";
		        ps = con.prepareStatement(sql);
		        ps.setLong(1, blogID);
		        
		        ResultSet blogInfo = ps.executeQuery();
		        
		        if(blogInfo.next()) {
		        	JSONObject jsonOut = new JSONObject();
		        	JSONArray tempJSONArray = new JSONArray(blogInfo.getString("BP_Content"));
	            	JSONObject jsonObject = tempJSONArray.getJSONObject(0);
	            	String totalSub;
	            	String content = jsonObject.getString("data");
		        	if(content.length() > 30) {
		        		totalSub = content.substring(0, 30) + "...";
		        	} else {
		        		totalSub = content;
		        	}
		        	jsonOut.put("blogID", blogInfo.getLong("BP_ID"));
		        	jsonOut.put("publisherID", blogInfo.getLong("BP_PublisherID"));
		        	jsonOut.put("blogTitle", blogInfo.getString("BP_Title"));
		        	jsonOut.put("blogContent", totalSub);
		        	jsonOut.put("blogDate", blogInfo.getDate("BP_Date").toString());
		        	jsonArray.put(jsonOut);
		        }
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonArray.toString());
	}

}
