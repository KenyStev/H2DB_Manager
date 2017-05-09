/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package h2db_manager;

import java.sql.*;

/**
 *
 * @author Kenystev
 */
public class H2DB_Manager {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
    }

    static Connection getConnection(String url, String user, String pass) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:tcp:"+url, user,pass);
        return conn;
    }
    
}
