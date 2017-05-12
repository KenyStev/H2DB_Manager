/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package h2db_manager;

import java.sql.Connection;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 * @author Kenystev
 */
class DDL_Templates {

    static String getDDLForCreateSchema(ConnectionData connection) {
        return "CREATE SCHEMA [ IF NOT EXISTS ] <schema_name> AUTHORIZATION "+connection.getUser().toUpperCase()+";";
    }

    static String getDDLForCreateTable(ConnectionData conn,String schema) {        
        return "CREATE [ CACHED | MEMORY ] [ TEMP | [ GLOBAL | LOCAL ] TEMPORARY ] TABLE \n"
                + "[ IF NOT EXISTS ] "+((schema.length()>0)?schema+".":schema)+"<name> (\n"
                + "[ ( { columnDefinition | constraint } [,...] ) ]\n" +
                "[ ENGINE tableEngineName ]\n" +
                "[ WITH tableEngineParamName [,...] ]\n" +
                "[ NOT PERSISTENT ] [ TRANSACTIONAL ]"
                + ");";
    }

    static String getDDLForCreateView(ConnectionData get, String schema) {
        return "CREATE [ OR REPLACE ] [ FORCE ] VIEW [ IF NOT EXISTS ] "+((schema.length()>0)?schema+".":schema)+"<newViewName>\n" +
               "[ ( columnName [,...] ) ] AS {select};";
    }

    static String getDDLForCreateUser(ConnectionData get) {
        return "CREATE USER [ IF NOT EXISTS ] <newUserName>\n" +
               "{ PASSWORD string | SALT bytes HASH bytes } [ ADMIN ];";
    }

    static String getDDLForCreateFunction(ConnectionData get, String schema) {
        return "CREATE ALIAS "+((schema.length()>0)?schema+".":schema)+"<aliasName> AS $$\n" +
                "    String <aliasFuncName>(String str, String sourceSet, String replacementSet){\n" +
                "        return \"\";\n" +
                "    }\n" +
                "$$;";
    }

    static String getDDLForCreateIndex(ConnectionData get, String schema, String table) {
        return "CREATE\n" +
                "{ [ UNIQUE ] [ HASH | SPATIAL] INDEX [ [ IF NOT EXISTS ] <newIndexName> ]\n" +
                "| PRIMARY KEY [ HASH ] }\n" +
                "ON "+((schema.length()>0)?schema+".":schema)+((table.length()>0)?table.toUpperCase():"<tableName>")+" ( indexColumn [,...] );";
    }

    static String getDDLForDropSchema(String schema) {
        return "DROP SCHEMA [ IF EXISTS ] "+((schema.length()>0)?schema:"<schemaName>")+";";
    }

    static String getDDLForDropTable(String table, String schema) {
        return "DROP TABLE [ IF EXISTS ] "+((schema.length()>0)?schema.toUpperCase()+".":schema)+((table.length()>0)?table.toUpperCase():"<tableName>")+" [,...] [ RESTRICT | CASCADE ];";
    }

    static String getDDLForDropFunction(String func, String schema) {
        return "DROP ALIAS [ IF EXISTS ] "+((schema.length()>0)?schema.toUpperCase()+".":"<schemaName>")+((func.length()>0)?func.toUpperCase():"<existingFunctionAliasName>")+";";
    }

    static String getDDLForDropView(String view, String schema) {
        return "DROP VIEW [ IF EXISTS ] "+((schema.length()>0)?schema.toUpperCase()+".":"<schemaName>")+((view.length()>0)?view.toUpperCase():"<viewName>")+" [ RESTRICT | CASCADE ];";
    }

    static String getDDLForDropIndex(String index, String schema) {
        return "DROP INDEX [ IF EXISTS ] "+((schema.length()>0)?schema.toUpperCase()+".":"<schemaName>")+((index.length()>0)?index.toUpperCase():"<indexName>")+";";
    }

    static String getDDLForDropUser(String user) {
        return "DROP USER [ IF EXISTS ] "+((user.length()>0)?user.toUpperCase():"<userName>")+";";
    }
    
}
