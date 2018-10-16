/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package midtermnorthwind;

import java.sql.ResultSet;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 *
 * @author daniela
 */
public class MidTermNorthWind extends Application {
    
    //menu bar, menu and menuItems
     private final MenuBar menuBar= new MenuBar();
     private final Menu fileMenu= new Menu("File");
     private final MenuItem exit= new MenuItem("Exit");
     
     private final Menu menuOrders= new Menu("Orders");
     private final MenuItem menuItemTotal= new MenuItem("Total");
     private final MenuItem menuItemDetail= new MenuItem("Details");
     
     private final Menu menuCustomer= new Menu("Customers");
     private final MenuItem menuItemCustomer= new Menu("State");
     
     private final Menu menuEmployee= new Menu("Employees");
     private final MenuItem menuItemBirthday= new MenuItem("Birthday");
      
    private final  Button submitButton= new Button("Submit"); 
     private final  Button clearButton= new Button("Clear");
      
    private HBox hboxTop;
    private VBox vboxTop;
    private String input; 
    private TextField inputTextField;
      
    private TableView table= new TableView();
    private ObservableList<ObservableList> data= FXCollections.observableArrayList();
    private DataBase database; 
    private  Pane root = new Pane();
    @Override
    public void start(Stage primaryStage) {
       
        //Menu bar
        fileMenu.getItems().add(exit);
        menuOrders.getItems().addAll(menuItemTotal,menuItemDetail);
        menuCustomer.getItems().addAll(menuItemCustomer);
        menuEmployee.getItems().addAll(menuItemBirthday);
        menuBar.getMenus().addAll(fileMenu,menuOrders,menuCustomer,menuEmployee);
        
        //handler all menu action events
        EventHandler<ActionEvent> menuHandler= new EventHandler<ActionEvent>(){
          public void handle(ActionEvent ae){
            String name=((MenuItem)ae.getTarget()).getText();
            if (name.equals("Exit")){Platform.exit();}
             else if(name.equals("Total")){total(); }
                else if(name.equals("Details")){showDetail(); }
                else if(name.equals("State")){showCustomerNameByState(); }
                   else if(name.equals("Birthday")){showBirthDay();}
            }
        };
        //set actions for the menu items
        exit.setOnAction(menuHandler);
        menuItemTotal.setOnAction(menuHandler);
        menuItemDetail.setOnAction(menuHandler);
        menuItemCustomer.setOnAction(menuHandler);
        menuEmployee.setOnAction(menuHandler);
        
        
        //set scenes
        Scene scene = new Scene(root, 600, 600);
         root.getChildren().addAll(menuBar);
        primaryStage.setTitle("NorthWind");
        primaryStage.setScene(scene);
        primaryStage.show();
        //initilize database
       database=new DataBase(); 
     
        
    }
      
 //print out the total for all products in the order, taking into account quantities and discounts.
    private void total(){
        hboxTop= new HBox(); 
        vboxTop= new VBox(); 
        inputTextField= new TextField();
        validationNumbersOnly();
        database.setNewQuery("Select * from [Order Details]");
        populateTableView( database.getNewQuery(), table);
        
         submitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(checkIfEmptyTextField()){                   
                 }
                     else{ input=inputTextField.getText().trim();
                           database.setNewQuery("Select OrderID ,Sum((UnitPrice*Quantity)- (UnitPrice*Quantity*(Discount/100))) as Totals "
                              + "from [Order Details] where OrderID="+input+" group by OrderID "); 
                          populateTableView(database.getNewQuery(), table);
                       }
            }
        });
   
        clearButton.setOnAction(new EventHandler<ActionEvent>() {
             @Override
            public void handle(ActionEvent event) {
                inputTextField.clear();
                total();
            }
        });
  
        hboxTop.getChildren().addAll(new Label("Enter Order Number"), inputTextField,submitButton,clearButton);
        vboxTop.getChildren().addAll(menuBar,hboxTop,table);
        root.getChildren().addAll(vboxTop);
    
    }

    private void populateTableView(ResultSet resultSet, TableView table) {
         table.getItems().clear();
        table.getColumns().clear();
         try {
        
                 for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                     //We are using non property style for making dynamic table
                        final int j = i;
                        TableColumn col = new TableColumn(resultSet.getMetaData().getColumnName(i + 1));

                    // col.setCellValueFactory(TextFieldTableCell.forTableColumn());

                col.setCellValueFactory(
                    // implement Callback interface
                    new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() 
                    {
                        @Override
                        public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) 
                        {
                            if (param == null || param.getValue() == null || param.getValue().get(j) == null) 
                            {
                                return null;
                            }
                            return new SimpleStringProperty(param.getValue().get(j).toString());
                        }
                    }
                );

                table.getColumns().addAll(col);
               
            }

            /**
             * ******************************
             * Data added to ObservableList * 
             ******************************
             */
            while (resultSet.next()) {
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                    //Iterate Column
                    row.add(resultSet.getString(i));
                }
               
                data.add(row);

            }

            //FINALLY ADDED TO TableView
            table.setItems(data);
        } catch (Exception e) {
                 e.printStackTrace();
                 System.out.println("Error on Building Data");
            }
    }
 
     /*The program will ask the user for an order number, and then print the order date,
     freight charge, and all products and their quantity, unit price, and discount for the order.*/
    private void showDetail(){
         hboxTop= new HBox(); 
        vboxTop= new VBox(); 
        inputTextField= new TextField();
        validationNumbersOnly();
        database.setNewQuery("Select * from [Order Details]");
        populateTableView(database.getNewQuery(), table);
        submitButton.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
               if(checkIfEmptyTextField()){                   
          
                 }
                     else{  input=inputTextField.getText();
                            database.setNewQuery("Select Orders.OrderDate,Orders.Freight,  p.ProductName, od.Quantity, od.UnitPrice, od.Discount "
                            + "from [Order Details] od, Orders "
                            +"join Products p on p.productID=od.productID and Orders.OrderID=od.OrderID where od.OrderID= "+input);
                            populateTableView(database.getNewQuery(), table);
                         }
            }
        });
        
        clearButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
           public void handle(ActionEvent event) {
                 inputTextField.clear();
                 showDetail();
            }
        });
       
        hboxTop.getChildren().addAll(new Label("Enter Order Number"), inputTextField,submitButton,clearButton);
        vboxTop.getChildren().addAll(menuBar,hboxTop,table);
        root.getChildren().addAll(vboxTop);
    }
    // print out the names and cities of all customers in this state in order by city.
    private void showCustomerNameByState(){
         hboxTop= new HBox(); 
        vboxTop= new VBox(); 
         inputTextField= new TextField();
       validationLettersOnly();
        database.setNewQuery("Select  region from customers ");
        populateTableView(database.getNewQuery(), table);
        submitButton.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                 if(checkIfEmptyTextField()){                   
                 }
                else{ input=inputTextField.getText();
                      database.setNewQuery("Select companyName,contactName, city from customers where Region='"+input+"' order by city asc");
                      populateTableView(database.getNewQuery(), table);}
                 }
            });
        
         clearButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                 public void handle(ActionEvent event) {
                    inputTextField.clear();
                     showCustomerNameByState();
                 }
            });
        
   
        hboxTop.getChildren().addAll(new Label("Enter State"), inputTextField,submitButton,clearButton);
        vboxTop.getChildren().addAll(menuBar,hboxTop,table);
        root.getChildren().addAll(vboxTop);
    }
  //print out the first and last names (in alphabetical order by last name) of all employees who were born during that year.
    private void showBirthDay(){
         hboxTop= new HBox(); 
        vboxTop= new VBox(); 
       inputTextField= new TextField();
        validationNumbersOnly();
        database.setNewQuery("Select  * from employees ");
        populateTableView(database.getNewQuery(), table);
        submitButton.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                 if(checkIfEmptyTextField()){                   
          
                 }
                     else{  input=inputTextField.getText();
                            database.setNewQuery("Select year(birthdate) as Year ,firstName,lastName from employees WHERE year(birthdate)="+input+" order by lastName asc");
                            populateTableView(database.getNewQuery(), table);
                        }
            }
        });
       clearButton.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                inputTextField.clear();
                 showBirthDay();
            }
        });
        hboxTop.getChildren().addAll(new Label("Enter year"), inputTextField,submitButton,clearButton);
        vboxTop.getChildren().addAll(menuBar,hboxTop,table);
        root.getChildren().addAll(vboxTop);
    }
    
    private void validationNumbersOnly(){
                   
          inputTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                  if (!newValue.matches("\\d*")) {
                        inputTextField.setText(newValue.replaceAll("[^\\d]", ""));
                     }
        } );
    
    }
    
    private void validationLettersOnly(){
                   
               inputTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                          if (!newValue.matches("\\sa-zA-Z*")) {
                                 inputTextField.setText(newValue.replaceAll("[^\\sa-zA-Z]", ""));
                            }
                });
    }
    private boolean checkIfEmptyTextField(){
    
         if(inputTextField.getText().isEmpty()){
                inputTextField.setStyle(" -fx-prompt-text-fill: red; ");
                inputTextField.setPromptText("Cannot Be Empty");
            }
         else return false; 
        return true; 
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
