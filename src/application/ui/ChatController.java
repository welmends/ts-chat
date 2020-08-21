package application.ui;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import application.com.P2P;
import application.com.mom.MOM;
import application.ui.constants.ChatConstants;
import application.ui.constants.ImageConstants;
import application.ui.utils.SoundUtils;
import application.ui.utils.StorageMessages;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ChatController extends Thread implements Initializable  {
	
	// FXML Variables
	@FXML VBox chatVBox;
	@FXML Label chatLabel; //*** USE THIS TO SHOW THE CONTACT NAME
	@FXML ImageView chatImageView;
	@FXML ScrollPane chatScrollPane;
	@FXML VBox chatVBoxOnScroll;
	@FXML TextField chatTextField;
	
	// COM Variables
	private MOM mom;
	private HashMap<String, P2P> p2ps;
	
	// Variables
	private SoundUtils soundUtils;
	private HashMap<String, StorageMessages> storage;
	
	public void loadFromParent(MOM mom, HashMap<String, P2P> p2ps) {
		this.mom = mom;
		this.p2ps = p2ps;
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// Initialize Objects
		this.soundUtils = new SoundUtils();
		this.storage = new HashMap<String, StorageMessages>();
		
		// Setup components
		setupComponents();
		
		// VBox Scrolls Down Behavior
		setVBoxScrollsBehavior();
		
		// TextField Enter Key Pressed Behavior
		setTextFieldKeyPressedBehavior();
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				Thread.sleep(ChatConstants.THREAD_SLEEP_TIME_MILLIS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if(mom.is__online()==false || p2ps.size()==0) {
				continue;
			}
			
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					if (mom.is__online() && p2ps.containsKey(mom.get_contact_nickname()) && p2ps.get(mom.get_contact_nickname()).is_connected()) {
						if(p2ps.get(mom.get_contact_nickname()).was_retrieved()==false) {
							p2ps.get(mom.get_contact_nickname()).set_retrieve_status(true);
			            	// Receive enqueued messages from MOM
							List<String> queue = mom.receiveQueue();
							for (int i=0; i<queue.size(); i++) {
								updateChatOnReceive(queue.get(i));
								
								// Store
				            	if (!storage.containsKey(mom.get_contact_nickname())) {
				            		storage.put(mom.get_contact_nickname(), new StorageMessages());
				            	}
				            	storage.get(mom.get_contact_nickname()).push_back(queue.get(i), "in");
							}
							queue.clear();
						}
						else if(p2ps.get(mom.get_contact_nickname()).chat_stack_full()) {
							// Receive Remotely
							String message_received = p2ps.get(mom.get_contact_nickname()).get_chat_msg();
							updateChatOnReceive(message_received);
							
							// Store
			            	if (!storage.containsKey(mom.get_contact_nickname())) {
			            		storage.put(mom.get_contact_nickname(), new StorageMessages());
			            	}
			            	storage.get(mom.get_contact_nickname()).push_back(message_received, "in");
						}
					}
				}
			});
		}
	}
	
	private void setupComponents() {
		chatLabel.setStyle(ChatConstants.STYLE_CHAT_LABEL);
		
		chatImageView.setImage(ImageConstants.CHAT_TOP_ICON);
		
		chatScrollPane.setStyle(ChatConstants.STYLE_SCROLL_PANE_CHAT);
		chatVBoxOnScroll.setStyle(ChatConstants.STYLE_VBOX_CHAT);
	}
	
	private void setVBoxScrollsBehavior() {
		chatVBoxOnScroll.heightProperty().addListener(new ChangeListener<Number>() {

	        @Override
	        public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
	        	if(arg1.intValue()!=0) {
	        		chatScrollPane.setVvalue(1.0);
	        	}
	        }
	        
		});
	}
	
	private void setTextFieldKeyPressedBehavior() {
		chatTextField.setOnKeyPressed(new EventHandler<KeyEvent>(){
			
	        @Override
	        public void handle(KeyEvent key){
	            if (key.getCode().equals(KeyCode.ENTER) && chatTextField.getText().length()>0){
	            	// Get text
	            	String message_send = chatTextField.getText();
	            			
	            	// Send Locally
	            	updateChatOnSend(message_send);
	            	
	                // Send Remotely
	            	if (mom.is__online() && p2ps.get(mom.get_contact_nickname()).is_connected()) {
	            		p2ps.get(mom.get_contact_nickname()).send_chat_msg_call(message_send);
	            	} else {
	            		mom.send(mom.get_contact_nickname(), message_send);
	            	}
	            	
	            	// Store
	            	if (!storage.containsKey(mom.get_contact_nickname())) {
	            		storage.put(mom.get_contact_nickname(), new StorageMessages());
	            	}
	            	storage.get(mom.get_contact_nickname()).push_back(message_send, "out");
	            }
	        }
	        
	    });
	}
	
	private void updateChatOnSend(String text_message) {
    	// Update chat components
        Label txt = new Label("");
        txt.setText(text_message+ChatConstants.SPACE_FOR_LABEL_TIME);
        txt.setWrapText(true);
        txt.setTextFill(ChatConstants.COLOR_LABEL_TEXT_SEND);
        txt.setStyle(ChatConstants.STYLE_LABEL_TEXT_SEND);
        txt.setPadding(ChatConstants.PADDING_LABEL_TEXT_SEND);
        txt.setAlignment(ChatConstants.ALIGNMENT_LABEL_TEXT_SEND);
        
        Label time = new Label(new SimpleDateFormat(ChatConstants.LABEL_TIME_SIMPLE_DATE_FORMAT).format(new Date()));
        time.setFont(ChatConstants.LABEL_TIME_FONT);
        time.setPadding(ChatConstants.PADDING_LABEL_TIME);
        time.setTextAlignment(ChatConstants.TEXT_ALIGNMENT_LABEL_TIME);
        
        StackPane sp = new StackPane();
        sp.setPadding(ChatConstants.PADDING_STACK_PANE_SEND);
        sp.getChildren().add(txt);
        sp.getChildren().add(time);
        StackPane.setAlignment(txt, ChatConstants.ALIGNMENT_STACK_PANE_SEND);
        StackPane.setAlignment(time, ChatConstants.ALIGNMENT_STACK_PANE_LABEL_TIME);
        
        // Send Locally
        soundUtils.playSendSound();
        chatVBoxOnScroll.getChildren().addAll(sp);
        
        // Find the width and height of the component before the Stage has been shown
        chatVBoxOnScroll.applyCss();
        chatVBoxOnScroll.layout();
        
        // Limit the component height
        sp.setMinHeight(sp.getHeight());
        
        // Clean chatTextField
        chatTextField.setText("");
	}
	
	private void updateChatOnReceive(String text_message) {
    	// Update chat components
		Label txt = new Label("");
        txt.setText(text_message+ChatConstants.SPACE_FOR_LABEL_TIME);
        txt.setWrapText(true);
        txt.setTextFill(ChatConstants.COLOR_LABEL_TEXT_RECEIVE);
        txt.setStyle(ChatConstants.STYLE_LABEL_TEXT_RECEIVE);
        txt.setPadding(ChatConstants.PADDING_LABEL_TEXT_RECEIVE);
        txt.setAlignment(ChatConstants.ALIGNMENT_LABEL_TEXT_RECEIVE);
    	
        Label time = new Label(new SimpleDateFormat(ChatConstants.LABEL_TIME_SIMPLE_DATE_FORMAT).format(new Date()));
        time.setFont(ChatConstants.LABEL_TIME_FONT);
        time.setPadding(ChatConstants.PADDING_LABEL_TIME);
        time.setTextAlignment(ChatConstants.TEXT_ALIGNMENT_LABEL_TIME);
        
        StackPane sp = new StackPane();
        sp.setPadding(ChatConstants.PADDING_STACK_PANE_RECEIVE);
        sp.getChildren().add(txt);
        sp.getChildren().add(time);
        StackPane.setAlignment(txt, ChatConstants.ALIGNMENT_STACK_PANE_RECEIVE);
        StackPane.setAlignment(time, ChatConstants.ALIGNMENT_STACK_PANE_LABEL_TIME);
        
        // Receive Locally
        soundUtils.playReceiveSound();
		chatVBoxOnScroll.getChildren().addAll(sp);
		
		// Find the width and height of the component before the Stage has been shown
		chatVBoxOnScroll.applyCss();
		chatVBoxOnScroll.layout();
        
        // Limit the component height
        sp.setMinHeight(sp.getHeight());
        
        // Adjust width of time label through padding
        time.setPadding(new Insets(0,sp.getWidth()-txt.getWidth()+6,2,0));
	}
	
	public void clearChat() {
		chatVBoxOnScroll.getChildren().clear();
	}
	
	public void loadChat() {
		if(storage.containsKey(mom.get_contact_nickname())) {
			StorageMessages stor = storage.get(mom.get_contact_nickname());
			for (int i=0; i<stor.messages.size(); i++) {
				if(stor.directions.get(i).equals("out")) {
					updateChatOnSend(stor.messages.get(i));
				} else {
					updateChatOnReceive(stor.messages.get(i));
				}
			}
		}
	}
}
