package com.reki.dotBlog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.print.DocFlavor.STRING;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/GetNotificationServlet")
public class GetNotificationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public GetNotificationServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONArray jsonArray = new JSONArray();
        JSONObject jsonIn = new JSONObject();
        int start, count;
        Long userID;
        String type;
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
 		type = jsonIn.getString("type");
 		start = jsonIn.getInt("start");
 		count = jsonIn.getInt("count");
 		
 		String sql = "";
 		
 		switch (type) {
	 		case "comment":
				sql = "select CP_BlogID, CP_PublisherID, CP_Content, CP_Date from t_commentpublish"
						+ " where CP_ToSomeoneID = ? order by CP_Date desc limit ?, ?";
				break;
			case "reply":
				sql = "select RP_CommentID, RP_PublisherID, RP_Content, RP_Date from t_replypublish"
						+ " where RP_ToSomeoneID = ? order by RP_Date desc limit ?, ?";
				break;
			default:
				return;
		}
 		
        Connection con = DBConnectionHandler.getConnection();
         
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            
            ps.setLong(1, userID);
            ps.setInt(2, start);
            ps.setInt(3, count);
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
				long publisherID;
				String content;
				switch (type) {
					case "comment":
						publisherID = rs.getLong("CP_PublisherID");
						content = rs.getString("CP_Content");
						break;
					case "reply":
						publisherID = rs.getLong("RP_PublisherID");
						content = rs.getString("RP_Content");
						break;
					default:
						return;
				}
				sql = "select UI_Name from t_userinfo where UI_ID = ?";
		        ps = con.prepareStatement(sql);
		        ps.setLong(1, publisherID);
		        
		        ResultSet notificationInfo = ps.executeQuery();
		        
		        if(notificationInfo.next()) {
		        	JSONObject jsonOut = new JSONObject();
		        	String contentSub;
		        	if(content.length() > 30) {
		        		contentSub = content.substring(0, 30) + "...";
		        	} else {
		        		contentSub = content;
		        	}
		        	switch (type) {
			        	case "comment":
			        		jsonOut.put("notificationID", rs.getLong("CP_BlogID"));
			        		jsonOut.put("notificationDate", rs.getDate("CP_Date").toString());
							break;
						case "reply":
							jsonOut.put("notificationID", rs.getLong("RP_CommentID"));
							jsonOut.put("notificationDate", rs.getDate("RP_Date").toString());
							break;
						default:
							return;
					}
		        	jsonOut.put("notificationName", notificationInfo.getString("UI_Name"));
		        	jsonOut.put("notificationContent", contentSub);
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
