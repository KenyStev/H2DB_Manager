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

    static ResultSet getSchemasFor(Connection conn, String user) throws SQLException {
        Statement stmt = conn.createStatement();
        return stmt.executeQuery("select SCHEMA_NAME from information_schema.schemata WHERE SCHEMA_OWNER = '"+user.toUpperCase()+"';");
    }

    static ResultSet getTablesFor(Connection connection, String user) throws SQLException {
        Statement stmt = connection.createStatement();
        return stmt.executeQuery("select S.SCHEMA_NAME, t.TABLE_NAME from information_schema.schemata S\n" +
"      inner join information_schema.tables T on T.TABLE_SCHEMA = S.SCHEMA_NAME\n" +
"        WHERE SCHEMA_OWNER = '"+user.toUpperCase()+"';");
    }
    
}
