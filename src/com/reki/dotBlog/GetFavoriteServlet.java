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

import org.json.JSONObject;

@WebServlet("/GetFavoriteServlet")
public class GetFavoriteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public GetFavoriteServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject jsonOut = new JSONObject();
        JSONObject jsonIn = new JSONObject();
      	long blogID, userID;
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
        userID = jsonIn.getLong("userID");
 
        String sql = "select BF_ID from t_blogfavorite where BF_UserID = ? and BF_BlogID = ?";
        Connection con = DBConnectionHandler.getConnection();
         
        try {
            PreparedStatement ps = con.prepareStatement(sql);
           
            ps.setLong(1, userID);
            ps.setLong(2, blogID);
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
				jsonOut.put("favoriteID", rs.getLong("BF_ID"));
			} else {
				jsonOut.put("favoriteID", -1l);
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonOut.toString());
	}

}
