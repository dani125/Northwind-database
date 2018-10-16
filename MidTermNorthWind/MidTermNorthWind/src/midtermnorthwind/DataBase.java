/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package midtermnorthwind;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author daniela
 */
public class DataBase {
 private Statement statement;
     private ResultSet resultSet ;
     private String createNewQuery;
    public  DataBase(){
      initializeDB();
    
    
    }
    public void setNewQuery(String createNewQuery){
        this.createNewQuery=createNewQuery;
    }
 
    public ResultSet getNewQuery(){
         try {
             resultSet= statement.executeQuery(createNewQuery);
         } catch (Exception ex) {
            ex.printStackTrace();
        }
        return resultSet;
    }
    
     //initialize DB
      private void initializeDB() {
        try {
            // Load the JDBC driver
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            System.out.println("Driver loaded");

            // Establish a connection
            Connection connection = DriverManager.getConnection("jdbc:ucanaccess://C:/data/Northwind.mdb");


            System.out.println("Database connected");
            //get information from database
            
            statement = connection.createStatement();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
