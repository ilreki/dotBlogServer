package com.reki.dotBlog;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnectionHandler {
	 
    public static Connection getConnection() {
        Connection con = null;
        try {
        	Class.forName("com.mysql.cj.jdbc.Driver").newInstance();//Mysql Connection
//          con = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "Reki7354");//mysql database
            String url="jdbc:mysql://localhost:3306/db_dotblog?serverTimezone=Asia/Shanghai&user=root&password=Reki7354&characterEncoding=utf-8&useUnicode=true";
			con=DriverManager.getConnection(url);
        } 
        /*catch (ClassNotFoundException ex) {
            Logger.getLogger(DBConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(DBConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException e) {
        	Logger.getLogger(DBConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IllegalAccessException e) {
			Logger.getLogger(DBConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
		} */
        catch (Exception e) {
			Logger.getLogger(DBConnectionHandler.class.getName()).log(Level.SEVERE, null, e);
		}
        return con;
    }
}
