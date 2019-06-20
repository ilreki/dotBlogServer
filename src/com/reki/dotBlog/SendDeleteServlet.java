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

@WebServlet("/SendDeleteServlet")
public class SendDeleteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public SendDeleteServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject jsonOut = new JSONObject();
        JSONObject jsonIn = new JSONObject();
        Long deleteID;
        String type;
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
 		
 		deleteID = jsonIn.getLong("deleteID");
 		type = jsonIn.getString("type");
 
 		String sql;
 		
 		switch (type) {
			case "blog":
				sql = "delete from t_blogpublish where BP_ID = ?";
				break;
			case "comment":
				sql = "delete from t_commentpublish where CP_ID = ?";
				break;
			case "reply":
				sql = "delete from t_replypublish where RP_ID = ?";
				break;
			default:
				return;
		}
        Connection con = DBConnectionHandler.getConnection();
         
        try {
            PreparedStatement ps = con.prepareStatement(sql);
           
            ps.setLong(1, deleteID);

            
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
