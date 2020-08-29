package application.ui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import application.ts.TupleSpace;
import application.ui.constants.ConfigConstants;
import application.ui.constants.ImageConstants;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

public class ConfigController extends Thread implements Initializable  {
	
	// FXML Variables
	@FXML HBox mainHBox;
	@FXML Button add_btn;
	@FXML Button power_btn;
	@FXML Circle on_circle;
	@FXML Circle off_circle;
	@FXML TextField add_tf;
	@FXML ScrollPane contactsScrollPane;
	@FXML VBox contactsVBoxOnScroll;
	
	// COM Variables
	private TupleSpace ts;
	
	// Controllers
	private ChatController chat;
	
	// Variables
	private List<Button> contactsButtons;
	
	public void loadFromParent(TupleSpace ts, ChatController chat) {
		this.ts = ts;
		this.chat = chat;
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// Initialize Objects
		contactsButtons   = new ArrayList<Button>();
		
		setupComponents();
		setAddBtnPressedBehavior();
		setPowerBtnPressedBehavior();
		setVBoxScrollsBehavior();
	}
	
	@Override
	public void run() {
		return;
	}
	
	private void setupComponents() {
		on_circle.setFill(ConfigConstants.COLOR_ONLINE);
		off_circle.setFill(ConfigConstants.COLOR_UNKNOWN);
		
		add_btn.setGraphic(ImageConstants.ADD_BTN_ICON);
		power_btn.setGraphic(ImageConstants.POWER_BTN_ICON);
	}
	
	private void setAddBtnPressedBehavior() {
		add_btn.setOnAction((event)->{
			String new_contact_nickname = add_tf.getText();
			if(new_contact_nickname.equals("")) {
				return;
			}
			
			HBox h = new HBox();
			Button b = new Button();
			
			b.setText(new_contact_nickname);
			b.setPrefWidth(ConfigConstants.CONTACT_BUTTON_PREF_WIDTH);
			setContactBtnPressedBehavior(b);
			
			h.setPadding(ConfigConstants.PADDING_CONTACT_HBOX);
			h.getChildren().addAll(b);
			
			contactsButtons.add(b);

	        contactsVBoxOnScroll.getChildren().addAll(h);
	        contactsVBoxOnScroll.applyCss();
	        contactsVBoxOnScroll.layout();
	        
	        add_tf.setText("");
        });
    }
	
	private void setContactBtnPressedBehavior(Button b) {
		b.setOnAction((event)->{
    		// Select contact
			for (int i=0; i<contactsButtons.size(); i++) {
				if(contactsButtons.get(i).equals(b)) {
					ts.set_contact_nickname(contactsButtons.get(i).getText());
					chat.chatLabel.setText(ts.get_contact_nickname());
					chat.clearChat();
					chat.loadChat();
					chat.disableChatTextField(false);
	        		break;
				}
			}
        });
	}
	
	private void setPowerBtnPressedBehavior() {
		power_btn.setOnAction((event)->{
        	if (ts.has_connection()) {
        		getOffline();
        	} else {
        		getOnline();
        	}
        });
    }
	
	private void setVBoxScrollsBehavior() {
		contactsVBoxOnScroll.heightProperty().addListener(new ChangeListener<Number>() {

	        @Override
	        public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
	        	if(arg1.intValue()!=0) {
	        		contactsScrollPane.setVvalue(1.0);
	        	}
	        }
	        
		});
	}
	
	private void getOnline() {
		on_circle.setFill(ConfigConstants.COLOR_ONLINE);
		off_circle.setFill(ConfigConstants.COLOR_UNKNOWN);
	}
	
	private void getOffline() {
		on_circle.setFill(ConfigConstants.COLOR_UNKNOWN);
		off_circle.setFill(ConfigConstants.COLOR_OFFLINE);
	}
}