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

@WebServlet("/SendReportServlet")
public class SendReportServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public SendReportServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject jsonOut = new JSONObject();
        JSONObject jsonIn = new JSONObject();
        Long user_id, reportContentID;
        String type, reportReason;
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
 		
 		user_id = jsonIn.getLong("userID");
 		reportContentID = jsonIn.getLong("reportContentID");
 		type = jsonIn.getString("type");
 		reportReason = jsonIn.getString("reportReason");
 
 		String sql;
 		
 		switch (type) {
			case "blog":
				sql = "insert into t_blogreport(BR_ReporterID, BR_BlogID, BR_Reason)"
		        		+ " values(?, ?, ?)";
				break;
			case "comment":
				sql = "insert into t_commentreport(CR_ReporterID, CR_CommentID, CR_Reason)"
		        		+ " values(?, ?, ?)";
				break;
			case "reply":
				sql = "insert into t_replyreport(RR_ReporterID, RR_ReplyID, RR_Reason)"
		        		+ " values(?, ?, ?)";
				break;
			default:
				return;
		}
        Connection con = DBConnectionHandler.getConnection();
         
        try {
            PreparedStatement ps = con.prepareStatement(sql);
           
            ps.setLong(1, user_id);
            ps.setLong(2, reportContentID);
            ps.setString(3, reportReason);

            
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
