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

@WebServlet("/SendCommentServlet")
public class SendCommentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public SendCommentServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject jsonOut = new JSONObject();
        JSONObject jsonIn = new JSONObject();
        Long userID, blogID, toSomeoneID;
        String content;
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
 		
 		userID = jsonIn.getLong("userID");
 		blogID = jsonIn.getLong("blogID");
 		toSomeoneID = jsonIn.getLong("toSomeoneID");
 		content = jsonIn.getString("content");
 
        String sql = "insert into t_commentpublish(CP_PublisherID, CP_BlogID, CP_ToSomeoneID, CP_Content)"
        		+ " values(?, ?, ?, ?)";
        Connection con = DBConnectionHandler.getConnection();
         
        try {
            PreparedStatement ps = con.prepareStatement(sql);
           
            ps.setLong(1, userID);
            ps.setLong(2, blogID);
            ps.setLong(3, toSomeoneID);
            ps.setString(4, content);
            
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
