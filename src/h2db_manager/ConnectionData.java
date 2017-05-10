/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package h2db_manager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    private void initDataBaseObjects() {
        initSchemas();
        initTables();
        initIndexes();
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
        
    }
    
}
