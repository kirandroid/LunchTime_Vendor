package Controller;
import bll.Student;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.NumberValidator;
import com.jfoenix.validation.RegexValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import dao.StudentDao;
import dao.VendorDao;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.*;
import java.util.ResourceBundle;

public class TopupUserController  implements  Initializable{

    Alert a = new Alert(Alert.AlertType.NONE);


    @FXML
    private StackPane rootStackPane;

    @FXML
    private AnchorPane userOrderPane;

    @FXML
    private TableView<Student> userTable;

    @FXML
    private TableColumn<Student, Integer> id;

    @FXML
    private TableColumn<Student, String > firstName;

    @FXML
    private TableColumn<Student, String> lastName;

    @FXML
    private TableColumn<Student, String> phoneNumber;

    @FXML
    private TableColumn<Student, Integer> balance;

    @FXML
    private TableColumn<Student, String> email;

    @FXML
    private JFXTextField txtFirstName;

    @FXML
    private JFXTextField txtphoneNumber;

    @FXML
    private JFXTextField txtAddBalance;

    @FXML
    private JFXTextField txtLastName;

    @FXML
    private JFXTextField txtEmail;

    @FXML
    private JFXTextField txtSearch;

    @FXML
    private JFXTextField txtCurrentBalance;

    @FXML
    private Circle profilePictureView;

    private  int userId;

    ObservableList<Student> oblist = FXCollections.observableArrayList();

     boolean firstNameIsEmpty=false;
     boolean firstNameIsValid=true;

     boolean lastNameIsEmpty=false;
     boolean lastNameIsValid=true;

     boolean emailIsEmpty=false;
     boolean emailIsValid=true;

     boolean phoneIsEmpty=false;
   boolean phoneIsValid=true;

    boolean addBalanceIsEmpty=false;
     boolean addBalanceIsValid=true;

    @FXML
    void loadAllUsers(KeyEvent event) {
        if (txtSearch.getText().isEmpty()) {
            userTable.getItems().clear();
            loadUser();
        }
    }



    @FXML
    void btnSearch(MouseEvent event) {
        try {
            StudentDao sd= (StudentDao) Naming.lookup("rmi://localhost/HelloStudent");
            ResultSet rs= sd.searchUser(txtSearch.getText());
            userTable.getItems().clear();
            while(rs.next()){
                oblist.add(new Student(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("phone_number"),
                        rs.getInt("balance"),
                        rs.getString("email")
                ));
            }
            if(txtSearch.getText().isEmpty()){
                loadUser();

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    void clearAllField(){
        txtFirstName.clear();
        txtLastName.clear();
        txtCurrentBalance.clear();
        txtphoneNumber.clear();
        txtEmail.clear();
        txtAddBalance.clear();
    }


    @FXML
    void btnClear(MouseEvent event) {
        clearAllField();
    }

    @FXML
    void btnUpdate(MouseEvent event) {
        if(!firstNameIsEmpty && firstNameIsValid && !lastNameIsEmpty && lastNameIsValid &&
            !phoneIsEmpty && phoneIsValid && !emailIsEmpty && emailIsValid &&
                !addBalanceIsEmpty && addBalanceIsValid){

            Platform.runLater(() -> {
                JFXDialogLayout content = new JFXDialogLayout();
                content.setHeading(new Text("Confirmation"));
                content.setBody(new Text("Are you sure you want to update the User?"));
                JFXButton yesButton = new JFXButton("Yes");
                JFXButton cancelButton = new JFXButton("Cancel");
                JFXDialog dialog = new JFXDialog(rootStackPane, content, JFXDialog.DialogTransition.CENTER);

                JFXDialogLayout content1 = new JFXDialogLayout();
                content1.setHeading(new Text("Successfull"));
                content1.setBody(new Text("User updated successfully."));
                JFXButton yesButton1 = new JFXButton("OK");
                JFXDialog dialog1 = new JFXDialog(rootStackPane, content1, JFXDialog.DialogTransition.CENTER);
                yesButton1.setOnAction(event1 -> {
                    dialog1.close();

                    try {
                        StackPane pane = FXMLLoader.load(getClass().getResource("../View/topupUser.fxml"));
                        userOrderPane.getChildren().setAll(pane);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                });
                content1.setActions(yesButton1);

                cancelButton.setOnAction(eventClose -> dialog.close());

                yesButton.setOnAction(eventConfirm ->{
                    try{
                        StudentDao sd= (StudentDao) Naming.lookup("rmi://localhost/HelloStudent");
                        sd.updateUser(
                                txtFirstName.getText(),
                                txtLastName.getText(),
                                txtEmail.getText(),
                                txtphoneNumber.getText(),
                                Integer.parseInt(txtCurrentBalance.getText()),
                                Integer.parseInt(txtAddBalance.getText()),
                                userId);
                        userTable.getItems().clear();
                        loadUser();
                        dialog1.show();
                        //clearAllField();

                    }catch(Exception e){
                        System.out.println(e);
                    }
                    dialog.close();
                });
                content.setActions(cancelButton, yesButton);
                dialog.show();
            });
        }
    }



    @FXML
    void btnLogout(MouseEvent event) throws IOException {
        StackPane pane = FXMLLoader.load(getClass().getResource("../View/Login.fxml"));
        userOrderPane.getChildren().setAll(pane);

    }

    @FXML
    void btnSalesDetails(ActionEvent event) throws IOException {
        StackPane pane = FXMLLoader.load(getClass().getResource("../View/salesDetail.fxml"));
        userOrderPane.getChildren().setAll(pane);

    }

    @FXML
    void myProfile(MouseEvent event) throws IOException {
        StackPane pane = FXMLLoader.load(getClass().getResource("../View/myProfile.fxml"));
        userOrderPane.getChildren().setAll(pane);

    }

    @FXML
    void btnTopUpUser(MouseEvent event) throws IOException {
        StackPane pane = FXMLLoader.load(getClass().getResource("../View/topupUser.fxml"));
        userOrderPane.getChildren().setAll(pane);

    }


    @FXML
    void btnAddFood(ActionEvent event) throws IOException {
        System.out.println("Add food button is pressed");
        StackPane pane = FXMLLoader.load(getClass().getResource("../View/addFoodItems.fxml"));
        userOrderPane.getChildren().setAll(pane);


    }

    @FXML
    void btnUserOrder(ActionEvent event) throws IOException {
        System.out.println("User Order Button is pressed.");
        StackPane pane = FXMLLoader.load(getClass().getResource("../View/VendorDashboard.fxml"));
        userOrderPane.getChildren().setAll(pane);
    }

    void loadUser(){
        try{
            StudentDao sd= (StudentDao) Naming.lookup("rmi://localhost/HelloStudent");
            ResultSet  rs= sd.getAllUser();
            while(rs.next()){
                oblist.add(new Student(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("phone_number"),
                        rs.getInt("balance"),
                        rs.getString("email")
                        ));
            }
            id.setCellValueFactory(new PropertyValueFactory<>("id"));
            firstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
            lastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
            phoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
            balance.setCellValueFactory(new PropertyValueFactory<>("balance"));
            email.setCellValueFactory(new PropertyValueFactory<>("email"));
            userTable.setItems(oblist);

            userTable.setOnMouseClicked(e ->{
                Platform.runLater(()->{
                    txtFirstName.requestFocus();

                });
                txtFirstName.setText(String.valueOf(userTable.getSelectionModel().getSelectedItem().getFirstName()));
                txtLastName.setText(String.valueOf(userTable.getSelectionModel().getSelectedItem().getLastName()));
                txtphoneNumber.setText(String.valueOf(userTable.getSelectionModel().getSelectedItem().getPhoneNumber()));
                txtCurrentBalance.setText(String.valueOf(userTable.getSelectionModel().getSelectedItem().getBalance()));
                txtEmail.setText(String.valueOf(userTable.getSelectionModel().getSelectedItem().getEmail()));
                userId=userTable.getSelectionModel().getSelectedItem().getId();
                txtAddBalance.setText(String.valueOf(0));
            });
        }catch(Exception e){
            System.out.println(e);
        }
    }
    void getVendorInfo(){
        try{
            VendorDao vd= (VendorDao) Naming.lookup("rmi://localhost/HelloServer");
            ResultSet getInfo=vd.getInfo(LoginController.id);
            while(getInfo.next()){
                try{
                    String imagePath= getInfo.getString("picture");
                    profilePictureView.setFill(new ImagePattern(new Image(imagePath)));

                }catch(Exception e){
                    System.out.println(e);
                }
            }
        }catch(Exception e){
            System.out.println("Exception: "+e);
        }
    }

    private void fieldValidators(){

        //Field Required validator for firstName
        RequiredFieldValidator firstNameRequiredFieldValidator = new RequiredFieldValidator();
        txtFirstName.getValidators().add(firstNameRequiredFieldValidator);
        firstNameRequiredFieldValidator.setMessage("Please enter firstName!");
        txtFirstName.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (txtFirstName.validate()) {
                    System.out.println("firstName not empty");
                    firstNameIsEmpty = false;
                } else {
                    System.out.println("firstName empty");
                    firstNameIsEmpty = true;
                }

            }
        });
        txtFirstName.textProperty().addListener((observable, oldValue, newValue) -> txtFirstName.validate());

        //Field Required validator for lastName
        RequiredFieldValidator lastNameRequiredFieldValidator = new RequiredFieldValidator();
        txtLastName.getValidators().add(firstNameRequiredFieldValidator);
        firstNameRequiredFieldValidator.setMessage("Please enter firstName!");
        txtLastName.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (txtLastName.validate()) {
                    System.out.println("lastName not empty");
                    lastNameIsEmpty = false;
                } else {
                    System.out.println("lastName empty");
                    lastNameIsEmpty = true;
                }

            }
        });
        txtFirstName.textProperty().addListener((observable, oldValue, newValue) -> txtFirstName.validate());


        // firstnameValidator
        RegexValidator firstName = new RegexValidator();
        firstName.setRegexPattern("[a-zA-Z]+");
        txtFirstName.setValidators(firstName);
        firstName.setMessage("First Name is invalid!");
        txtFirstName.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (txtFirstName.validate()) {
                    System.out.println("First Name valid");
                    firstNameIsValid = true;
                } else {
                    System.out.println("First Name not valid");
                    firstNameIsValid = false;
                }
            }
        });
        txtFirstName.textProperty().addListener((observable, oldValue, newValue) -> txtFirstName.validate());


        // lastameValidator
        RegexValidator lastName = new RegexValidator();
        lastName.setRegexPattern("[a-zA-Z]+");
        txtLastName.setValidators(lastName);
        lastName.setMessage("Last Name is invalid!");
        txtLastName.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (txtLastName.validate()) {
                    System.out.println("Last Name valid");
                    lastNameIsValid = true;
                } else {
                    System.out.println("Last Name not valid");
                    lastNameIsValid = false;
                }
            }
        });
        txtLastName.textProperty().addListener((observable, oldValue, newValue) -> txtLastName.validate());




        //Field Required validator for email
        RequiredFieldValidator emailRequiredFieldValidator = new RequiredFieldValidator();
        txtEmail.getValidators().add(emailRequiredFieldValidator);
        emailRequiredFieldValidator.setMessage("Please enter an email!");
        txtEmail.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (txtEmail.validate()) {

                    System.out.println("Email not empty");
                    emailIsEmpty = false;
                } else {
                    System.out.println("Email empty");
                    emailIsEmpty = true;
                }

            }
        });
        txtEmail.textProperty().addListener((observable, oldValue, newValue) -> txtEmail.validate());

        //Field Required validator for phone
        RequiredFieldValidator phoneRequiredFieldValidator = new RequiredFieldValidator();
        txtphoneNumber.getValidators().add(phoneRequiredFieldValidator);
        phoneRequiredFieldValidator.setMessage("Please enter a phone!");
        txtphoneNumber.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (txtphoneNumber.validate()) {

                    System.out.println("Phone not empty");
                    phoneIsEmpty = false;
                } else {
                    System.out.println("Phone empty");
                    phoneIsEmpty = true;
                }

            }
        });
        txtphoneNumber.textProperty().addListener((observable, oldValue, newValue) -> txtphoneNumber.validate());


        //Field Required validator for add balance
        RequiredFieldValidator addBalanceRequiredFieldValidator = new RequiredFieldValidator();
        txtAddBalance.getValidators().add(addBalanceRequiredFieldValidator);
        addBalanceRequiredFieldValidator.setMessage("Please enter a balance!");
        txtAddBalance.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (txtAddBalance.validate()) {

                    System.out.println("Add Balance not empty");
                    addBalanceIsEmpty = false;
                } else {
                    System.out.println("Add Balance empty");
                    addBalanceIsEmpty = true;
                }

            }
        });
        txtAddBalance.textProperty().addListener((observable, oldValue, newValue) -> txtAddBalance.validate());


//----------------------------------------------------------------------------------------------------------------------------------//

//        //Email Validator
        RegexValidator emailValidator = new RegexValidator();
        emailValidator.setRegexPattern("^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        txtEmail.setValidators(emailValidator);
        emailValidator.setMessage("Email is invalid!");
        txtEmail.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (txtEmail.validate()) {
                    System.out.println("Email valid");
                    emailIsValid = true;
                } else {
                    System.out.println("Email not valid");
                    emailIsValid = false;
                }
            }
        });
        txtEmail.textProperty().addListener((observable, oldValue, newValue) -> txtEmail.validate());


        //        //Phone length Validator
        RegexValidator phoneLengthValidator = new RegexValidator();
        phoneLengthValidator.setRegexPattern("^.{10}$");
        txtphoneNumber.setValidators(phoneLengthValidator);
        phoneLengthValidator.setMessage("Phone Number should be 10 characters long!");
        txtphoneNumber.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (txtphoneNumber.validate()) {
                    System.out.println("Phone valid");
                    phoneIsValid = true;
                } else {
                    System.out.println("Phone not valid");
                    phoneIsValid = false;
                }
            }
        });
        txtphoneNumber.textProperty().addListener((observable, oldValue, newValue) -> txtphoneNumber.validate());

//        //Field Required validator for phone
        NumberValidator phoneFieldValidator = new NumberValidator();
        txtphoneNumber.getValidators().add(phoneFieldValidator);
        phoneFieldValidator.setMessage("Only numbers accepted!");
        txtphoneNumber.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (txtphoneNumber.validate()) {
                    System.out.println("Phone valid");
                    phoneIsValid = true;
                } else {
                    System.out.println("Phone not valid");
                    phoneIsValid = false;
                }
            }
        });
        txtphoneNumber.textProperty().addListener((observable, oldValue, newValue) -> txtphoneNumber.validate());

        //        //Field Required validator for add balance
        NumberValidator addBalanceFieldValidator = new NumberValidator();
        txtAddBalance.getValidators().add(addBalanceFieldValidator);
        addBalanceFieldValidator.setMessage("Only numbers accepted!");
        txtAddBalance.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (txtAddBalance.validate()) {
                    System.out.println("Phone valid");
                    addBalanceIsValid = true;
                } else {
                    System.out.println("Phone not valid");
                    addBalanceIsValid = false;
                }
            }
        });
        txtAddBalance.textProperty().addListener((observable, oldValue, newValue) -> txtAddBalance.validate());





    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(()->{
            txtFirstName.requestFocus();

        });
        fieldValidators();


        loadUser();
        getVendorInfo();

    }
}