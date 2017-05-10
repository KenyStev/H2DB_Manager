/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package h2db_manager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

/**
 *
 * @author Kenystev
 */
public class ConnectionData {

    private final String connectionName;
    private final Connection connection;
    private DefaultMutableTreeNode treeNode;
    private final String user;
    private HashMap<String, DefaultMutableTreeNode> schemasMap;

    ConnectionData(String cn,String user, Connection conn) {
        connectionName = cn;
        connection = conn;
        this.user = user;
        
        schemasMap = new HashMap<String,DefaultMutableTreeNode>();
        
        treeNode = new DefaultMutableTreeNode(cn);
        this.initDataBaseObjects();
    }

    public MutableTreeNode getTreeNode() {
        return treeNode;
    }

    void Close() throws SQLException {
        connection.close();
    }
    
    public static DefaultTableModel buildTableModel(ResultSet rs)
        throws SQLException {

        ResultSetMetaData metaData = rs.getMetaData();

        // names of columns
        Vector<String> columnNames = new Vector<String>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }

        // data of the table
        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        while (rs.next()) {
            Vector<Object> vector = new Vector<Object>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(rs.getObject(columnIndex));
            }
            data.add(vector);
        }

        return new DefaultTableModel(data, columnNames);

    }

        private void initDataBaseObjects() {
            initSchemas();
            initTables();
            initIndexes();
        }

    public Connection getConnection(){
        return connection;
    }
    
    public String getUser(){
        return user;
    }

    private void initSchemas() {
        DefaultMutableTreeNode schemas = new DefaultMutableTreeNode("Schemas");
        treeNode.add(schemas);
        try(ResultSet rs = H2DB_Manager.getSchemasFor(connection,user);) {
            while (rs.next()) {
                String schemaName = rs.getString("SCHEMA_NAME");
                schemasMap.put(schemaName,new DefaultMutableTreeNode(schemaName));
                schemas.add(schemasMap.get(schemaName));
                
                System.out.println(rs.getString("SCHEMA_NAME"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConnectionData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initTables() {
        DefaultMutableTreeNode tables = null;
        try(ResultSet rs = H2DB_Manager.getTablesFor(connection,user);) {
            String schemaName = "";
            while (rs.next()) {
                String nextSchemaName = rs.getString("SCHEMA_NAME");
                DefaultMutableTreeNode schema = schemasMap.get(nextSchemaName);
                if(!schemaName.equals(nextSchemaName))
                {
                    schemaName = nextSchemaName;
                    tables = new DefaultMutableTreeNode("Tables");
                    schema.add(tables);
                }
                if(tables!=null)
                    tables.add(new DefaultMutableTreeNode(rs.getString("TABLE_NAME")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConnectionData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initIndexes() {
        DefaultMutableTreeNode indexes = null;
        try(ResultSet rs = H2DB_Manager.getIndexesFor(connection,user);) {
            String schemaName = "";
            while (rs.next()) {
                String nextSchemaName = rs.getString("SCHEMA_NAME");
                DefaultMutableTreeNode schema = schemasMap.get(nextSchemaName);
                if(!schemaName.equals(nextSchemaName))
                {
                    schemaName = nextSchemaName;
                    indexes = new DefaultMutableTreeNode("Indexes");
                    schema.add(indexes);
                }
                if(indexes!=null)
                    indexes.add(new DefaultMutableTreeNode(rs.getString("INDEX_NAME")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConnectionData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
