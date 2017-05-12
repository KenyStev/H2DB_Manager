/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package h2db_manager;

import java.sql.*;
import javax.swing.tree.TreeNode;

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
        
        ResultSet rs = stmt.executeQuery("select SCHEMA_OWNER from information_schema.schemata WHERE SCHEMA_NAME = 'PUBLIC';");
        if(rs.next() && rs.getString("SCHEMA_OWNER").equals(user.toUpperCase()))
            return stmt.executeQuery("select SCHEMA_NAME from information_schema.schemata;");
//            return stmt.executeQuery("select SCHEMA_NAME from information_schema.schemata where SCHEMA_NAME != 'PUBLIC';");
        
        return stmt.executeQuery("select SCHEMA_NAME from information_schema.schemata WHERE SCHEMA_OWNER = '"+user.toUpperCase()+"';");
    }

    static ResultSet getTablesFor(Connection connection, String user) throws SQLException {
        Statement stmt = connection.createStatement();
        
        ResultSet rs = stmt.executeQuery("select SCHEMA_OWNER from information_schema.schemata WHERE SCHEMA_NAME = 'PUBLIC';");
        if(rs.next() && rs.getString("SCHEMA_OWNER").equals(user.toUpperCase()))
            return stmt.executeQuery("select S.SCHEMA_NAME, t.TABLE_NAME from information_schema.schemata S\n" +
"      inner join information_schema.tables T on T.TABLE_SCHEMA = S.SCHEMA_NAME\n" +
"        WHERE SCHEMA_NAME != 'PUBLIC';");
        
        return stmt.executeQuery("select S.SCHEMA_NAME, t.TABLE_NAME from information_schema.schemata S\n" +
"      inner join information_schema.tables T on T.TABLE_SCHEMA = S.SCHEMA_NAME\n" +
"        WHERE SCHEMA_OWNER = '"+user.toUpperCase()+"';");
    }

    static ResultSet getIndexesFor(Connection connection, String user) throws SQLException {
        Statement stmt = connection.createStatement();
        
        ResultSet rs = stmt.executeQuery("select SCHEMA_OWNER from information_schema.schemata WHERE SCHEMA_NAME = 'PUBLIC';");
        if(rs.next() && rs.getString("SCHEMA_OWNER").equals(user.toUpperCase()))
            return stmt.executeQuery("select S.SCHEMA_NAME, I.INDEX_NAME from information_schema.schemata S\n" +
"      inner join information_schema.indexes I on I.TABLE_SCHEMA = S.SCHEMA_NAME \n" +
"          WHERE SCHEMA_NAME != 'PUBLIC';");
        
        return stmt.executeQuery("select S.SCHEMA_NAME, I.INDEX_NAME from information_schema.schemata S\n" +
"      inner join information_schema.indexes I on I.TABLE_SCHEMA = S.SCHEMA_NAME \n" +
"          WHERE SCHEMA_OWNER = '"+user.toUpperCase()+"';");
    }

    static ResultSet getColumnsForTable(Connection connection, String schema, String tableName) throws SQLException {
        Statement stmt = connection.createStatement();
        return stmt.executeQuery("select C.COLUMN_NAME, C.TYPE_NAME,C.IS_NULLABLE,C.ORDINAL_POSITION from information_schema.columns C\n" +
"where table_SCHEMA = '"+schema+"' AND TABLE_NAME = '"+tableName+"';");
    }

    static ResultSet getColumnsForIndex(Connection connection, String schema, String indexName) throws SQLException {
        Statement stmt = connection.createStatement();
        return stmt.executeQuery("select TABLE_SCHEMA, INDEX_NAME, COLUMN_NAME, ASC_OR_DESC \n" +
"from information_schema.indexes where table_SCHEMA = '"+schema+"'\n" +
"and index_name = '"+indexName+"';");
    }

    static String getUserOwnerOfSchema(ConnectionData conn, String schema) throws SQLException {
        Statement stmt = conn.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery("select SCHEMA_OWNER from information_schema.schemata WHERE SCHEMA_NAME = '"+schema+"';");
        if(rs.next())
            return rs.getString("SCHEMA_OWNER");
        return "";
    }

    static ResultSet getDDLForTable(Connection connection, String schema, String table) throws SQLException {
        Statement stmt = connection.createStatement();
        return stmt.executeQuery("select SQL from information_schema.tables \n" +
"where TABLE_SCHEMA != 'INFORMATION_SCHEMA' \n" +
"AND TABLE_SCHEMA = '"+schema+"' \n" +
"AND TABLE_NAME = '"+table+"';");
    }

    static ResultSet getDDLForIndex(Connection connection, String schema, String index) throws SQLException {
        Statement stmt = connection.createStatement();
        return stmt.executeQuery("select SQL from information_schema.indexes \n" +
"where TABLE_SCHEMA != 'INFORMATION_SCHEMA' \n" +
"AND TABLE_SCHEMA = '"+schema+"' \n" +
"AND INDEX_NAME = '"+index+"';");
    }

    static Statement executeQuery(Connection connection, String sql) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute(sql);
        return stmt;
    }

    static ResultSet getDataFromTable(Connection connection, String schema, String table) throws SQLException {
        Statement stmt = connection.createStatement();
        return stmt.executeQuery("select * from "+schema+"."+table+";");
    }

    static ResultSet getFunctionsFor(Connection connection, String user) throws SQLException {
        Statement stmt = connection.createStatement();
        
//        ResultSet rs = stmt.executeQuery("select SCHEMA_OWNER from information_schema.schemata WHERE SCHEMA_NAME = 'PUBLIC';");
//        if(rs.next() && rs.getString("SCHEMA_OWNER").equals(user.toUpperCase()))
//            return stmt.executeQuery("select S.SCHEMA_NAME, I.INDEX_NAME from information_schema.schemata S\n" +
//"      inner join information_schema.indexes I on I.TABLE_SCHEMA = S.SCHEMA_NAME \n" +
//"          WHERE SCHEMA_NAME != 'PUBLIC';");
        
        return stmt.executeQuery("select FA.ALIAS_SCHEMA, fa.ALIAS_NAME from information_schema.function_aliases FA\n" +
"INNER JOIN INFORMATION_SCHEMA.SCHEMATA S\n" +
"ON FA.ALIAS_SCHEMA = S.SCHEMA_NAME\n" +
"WHERE S.SCHEMA_OWNER = '"+user.toUpperCase()+"';");
    }

    static ResultSet getDDLForFunction(Connection connection, String schema, String alias_name) throws SQLException {
        Statement stmt = connection.createStatement();
        return stmt.executeQuery("select ALIAS_NAME, SOURCE from information_schema.function_aliases \n" +
"WHERE ALIAS_SCHEMA = '"+schema.toUpperCase()+"' \n" +
"AND ALIAS_NAME = '"+alias_name.toUpperCase()+"';");
    }

    static ResultSet getViews(Connection connection) throws SQLException {
        Statement stmt = connection.createStatement();
        return stmt.executeQuery("select TABLE_SCHEMA, TABLE_NAME from information_schema.views;");
    }

    static ResultSet getColumnsForFuncion(Connection connection, String toString, String toString0) throws SQLException {
        Statement stmt = connection.createStatement();
        return stmt.executeQuery("select * from information_schema.function_columns \n" +
"where alias_schema = 'PUBLIC' \n" +
"AND ALIAS_NAME = 'GET_DDL_FROM_TABLE';");
    }
    
    static ResultSet getColumnsForView(Connection connection, String schema, String table) throws SQLException {
        Statement stmt = connection.createStatement();
        return stmt.executeQuery("show columns from "+table+" FROM "+schema+";");
    }

    static ResultSet getDataFromView(Connection connection, String schema, String table) throws SQLException {
        Statement stmt = connection.createStatement();
        return stmt.executeQuery("select * from "+schema+"."+table+";");
    }

    static ResultSet getDDLForView(Connection connection, String schema, String table) throws SQLException {
        Statement stmt = connection.createStatement();
        return stmt.executeQuery("select VIEW_DEFINITION from information_schema.views\n" +
"where table_schema = '"+schema+"'\n" +
"AND TABLE_NAME = '"+table+"';");
    }

    static ResultSet getUsersFor(Connection connection) throws SQLException {
        Statement stmt = connection.createStatement();
        return stmt.executeQuery("select * from information_schema.users;");
    }
    
}
