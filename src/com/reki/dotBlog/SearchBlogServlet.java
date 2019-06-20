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

@WebServlet("/SearchBlogServlet")
public class SearchBlogServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public SearchBlogServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JSONObject jsonIn = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        String searchWord;
        int category, start, count;
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
 		
 		searchWord = "%" + jsonIn.getString("searchWord") + "%";
 		start = jsonIn.getInt("start");
 		count = jsonIn.getInt("count");
 		category = jsonIn.getInt("category");
 		String sql;
 		if(category != 100) {
 			sql = "select BP_ID, BP_PublisherID, BP_Title, BP_Content, BP_Date from t_blogpublish"
 					+ " where BP_Category = ? and BP_Status = 1 and"
 					+ " (BP_Title like ? or BP_Content like ?) limit ?, ?";
 		} else {
 			sql = "select BP_ID, BP_PublisherID, BP_Title, BP_Content, BP_Date from t_blogpublish"
 					+ " where BP_Status = 1 and (BP_Title like ?"
 					+ " or BP_Content like ?) limit ?, ?";
 		}
 
        
        Connection con = DBConnectionHandler.getConnection();
         
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            
            if(category != 100) {
            	ps.setInt(1, category);
                ps.setString(2, searchWord);
                ps.setString(3, searchWord);
                ps.setInt(4, start);
                ps.setInt(5, count);
            } else {
                ps.setString(1, searchWord);
                ps.setString(2, searchWord);
                ps.setInt(3, start);
                ps.setInt(4, count);
            }
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
            	JSONObject jsonOut = new JSONObject();
            	JSONArray tempJSONArray = new JSONArray(rs.getString("BP_Content"));
            	JSONObject jsonObject = tempJSONArray.getJSONObject(0);
            	String totalSub;
            	String content = jsonObject.getString("data");
	        	if(content.length() > 30) {
	        		totalSub = content.substring(0, 30) + "...";
	        	} else {
	        		totalSub = content;
	        	}
	        	jsonOut.put("blogID", rs.getLong("BP_ID"));
	        	jsonOut.put("publisherID", rs.getLong("BP_PublisherID"));
	        	jsonOut.put("blogTitle", rs.getString("BP_Title"));
	        	jsonOut.put("blogContent", totalSub);
	        	jsonOut.put("blogDate", rs.getDate("BP_Date").toString());
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
