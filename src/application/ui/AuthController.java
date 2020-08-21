package application.ui;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import application.com.mom.MOM;
import application.ui.constants.AuthConstants;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
    private MOM mom;
	
	// Variables
    private Stage stage = null;
    private HashMap<String, String> credentials = new HashMap<String, String>();
    
    private ChatController chat;
    private ConfigController config;
    
    public AuthController(MOM mom, ChatController chat, ConfigController config) {
    	this.mom = mom;
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
        	String nickname     = credentials.get(AuthConstants.HASHCODE_NICKNAME);
        	String ip_address   = credentials.get(AuthConstants.HASHCODE_IPADDRESS);
        	Integer port_number = Integer.valueOf(credentials.get(AuthConstants.HASHCODE_PORTNUMBER));
        	String mom_url      = AuthConstants.MOM_URL_HEADER + ip_address + AuthConstants.MOM_DEFAULT_PORT;
        	
        	mom.setup(mom_url, nickname, ip_address, port_number);
        	
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
    		credentials.put(AuthConstants.HASHCODE_NICKNAME, AuthConstants.DEFAULT_NICKNAME);
    	}else {
    		credentials.put(AuthConstants.HASHCODE_NICKNAME, nicknameTF.getText());
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