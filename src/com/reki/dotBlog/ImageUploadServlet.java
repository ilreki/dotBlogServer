package com.reki.dotBlog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/ImageUploadServlet")
@MultipartConfig
public class ImageUploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public ImageUploadServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject jsonOut = new JSONObject();
        String storeDirectory = request.getSession().getServletContext().getRealPath("/img");
        System.out.println("root: " + storeDirectory);
        String fileType = "";
        String fileName = "";
        Long userID = 0l;
        InputStream iStream = null;
    	int index;
		byte[] bytes = new byte[1024];
        //如果不存在当前文件夹则新建
        File dir = new File(storeDirectory);
        if(!dir.exists()){
            dir.mkdirs();
        }
        try {
        	if(!ServletFileUpload.isMultipartContent(request)) {
        		System.out.println("not multipart content");
        		return;
        	}
        	for(Part part : request.getParts()) {
        		switch (part.getName()) {
				case "id":
					iStream = part.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
					StringBuilder sb = new StringBuilder();
						String line = null;
						try {
							while ((line = reader.readLine()) != null) {
								sb.append(line);
							}
						} catch (IOException e) {
						e.printStackTrace();
					} finally {
						try {
							iStream.close();
					   	} catch (IOException e) {
					   		e.printStackTrace();
					    }
					}
					userID = Long.parseLong(sb.toString());
					System.out.println(userID);
					break;
				case "image":
					fileType = part.getContentType();
					fileType = fileType.substring(fileType.indexOf("/") + 1, fileType.length());
					System.out.println(fileType);
					iStream = part.getInputStream();
				default:
					break;
				}
        	}
        	
        	String sql = "select PU_Name from t_pictureupload where PU_UploaderID = ? order by PU_ID desc";
            Connection con = DBConnectionHandler.getConnection();
            try {
                PreparedStatement ps = con.prepareStatement(sql);
               
                ps.setLong(1, userID);
                
                ResultSet rs = ps.executeQuery();
                
                if (rs.next()) {
    				String lastImageName = rs.getString("PU_Name");
    				
    				fileName = (Long.parseLong(lastImageName.substring(0, lastImageName.indexOf("."))) + 1) + "." + fileType;
    				System.out.println(fileName);
    				sql = "insert into t_pictureupload(PU_UploaderID, PU_Name) values(?, ?)";
    		        ps = con.prepareStatement(sql);
    		        ps.setLong(1, userID);
    		        ps.setString(2, fileName);
    		        
    		        ps.executeUpdate();
    			} else {
    				fileName = 1 + "." + fileType;
    				System.out.println(fileName);
    				sql = "insert into t_pictureupload(PU_UploaderID, PU_Name) values(?, ?)";
    		        ps = con.prepareStatement(sql);
    		        ps.setLong(1, userID);
    		        ps.setString(2, fileName);
    		        
    		        ps.executeUpdate();
    			}
            } catch (Exception e) {
                e.printStackTrace();
            }
    		//如果不存在当前文件夹则新建
        	dir = new File(storeDirectory + "/" + userID);
            System.out.println(dir.toString());
            if(!dir.exists()){
                dir.mkdirs();
            }
            FileOutputStream downloadFile = new FileOutputStream(storeDirectory + "/" + userID
            		+ "/" + fileName);
//            FileOutputStream downloadFile = new FileOutputStream(storeDirectory + "/file2.txt");
    		while ((index = iStream.read(bytes)) != -1) {
    			downloadFile.write(bytes, 0, index);
    			downloadFile.flush();
    		}
    		downloadFile.close();
    		iStream.close();
    		
    		jsonOut.put("path", fileName);
        } catch (Exception e) {
            e.printStackTrace();
            jsonOut.put("path", "error");
        } finally {
        	response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(jsonOut.toString());
		}
	}
}
