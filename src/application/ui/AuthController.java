package application.ui;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import application.ts.TupleSpace;
import application.ui.constants.AuthConstants;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AuthController implements Initializable {

	// FXML Variables
    @FXML private TextField nicknameTF;
    @FXML private TextField ipaddressTF;
    @FXML private TextField portnumberTF;
    @FXML private Button enterButton;
    
	// COM Variables
    private TupleSpace ts;
	
	// Variables
    private Stage stage = null;
    private HashMap<String, String> credentials = new HashMap<String, String>();
    
    private MainController main;
    private ChatController chat;
    private ConfigController config;
    
    public AuthController(TupleSpace ts, MainController main, ChatController chat, ConfigController config) {
    	this.ts = ts;
    	this.main = main;
    	this.chat = chat;
    	this.config = config;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	setEnterBtnPressedBehavior();
    }
	
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    private void closeStage() {
    	ts.start();
    	chat.start();
    	config.start();
        if(stage!=null) {
            stage.close();
        }
    }
    
    private void setEnterBtnPressedBehavior() {
    	enterButton.setOnAction((event)->{
        	disableComponents(true);
        	acquireCredentials();
        	String username     = credentials.get(AuthConstants.HASHCODE_USERNAME);
        	String ip_address   = credentials.get(AuthConstants.HASHCODE_IPADDRESS);
        	Integer port_number = Integer.valueOf(credentials.get(AuthConstants.HASHCODE_PORTNUMBER));
        	
        	ts.setup(ip_address, port_number, username);
        	if(!ts.connect()) {
        		main.closeApplication();
        		Alert alert = new Alert(Alert.AlertType.ERROR);
        		alert.setTitle("Connection Fail");
        		alert.setResizable(false);
        		alert.setHeaderText("Verify if the Apache River service is running!\nExiting the application...");
        		alert.showAndWait();
        		Platform.exit();
		        System.exit(0);
        	}else {
        		if(!ts.init_admin_tuple()) {
            		Alert alert = new Alert(Alert.AlertType.WARNING);
            		alert.setTitle("Invalid username");
            		alert.setResizable(false);
            		alert.setHeaderText("The username is invalid. Try another one.");
            		alert.showAndWait();
            		disableComponents(false);
            		return;
        		}else {
        			chat.chatLabelUser.setText(ts.get_user_name());
        		}
        	}
        	
        	closeStage();
        });
    }
    
    private void disableComponents(Boolean b) {
    	enterButton.setDisable(b);
    	nicknameTF.setDisable(b);
    	ipaddressTF.setDisable(b);
    	portnumberTF.setDisable(b);
    }
    
    private void acquireCredentials() {
    	credentials.clear();
    	if(nicknameTF.getText().equals("")) {
    		credentials.put(AuthConstants.HASHCODE_USERNAME, AuthConstants.DEFAULT_NICKNAME);
    	}else {
    		credentials.put(AuthConstants.HASHCODE_USERNAME, nicknameTF.getText());
    	}
    	if(ipaddressTF.getText().equals("")) {
    		credentials.put(AuthConstants.HASHCODE_IPADDRESS, AuthConstants.DEFAULT_IPADDRESS);
    	}else {
    		credentials.put(AuthConstants.HASHCODE_IPADDRESS, ipaddressTF.getText());
    	}
    	if(portnumberTF.getText().equals("")) {
    		credentials.put(AuthConstants.HASHCODE_PORTNUMBER, AuthConstants.DEFAULT_PORTNUMBER);
    	}else {
    		credentials.put(AuthConstants.HASHCODE_PORTNUMBER, portnumberTF.getText());
    	}
    	return;
    }
}