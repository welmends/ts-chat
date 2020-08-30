package application.ui.constants;

import javafx.scene.control.ContentDisplay;
import javafx.scene.paint.Color;

public class ConfigConstants {
	// Sleep
	public static Integer THREAD_SLEEP_TIME_MILLIS = 500;
	
	// Colors
	public static Color COLOR_ONLINE   = Color.GREEN;
	public static Color COLOR_OFFLINE  = Color.RED;
	public static Color COLOR_UNKNOWN  = Color.GRAY;
	
	// Texts
	public static String CHAT_LABEL_TEXT = "Rooms List";
	public static String ROOM_BUTTON_TEXT = "Enter";
	
	// Styles
	public static String CHAT_LABEL_STYLE = "-fx-font-family: 'Arial'; -fx-font-size: 14pt; -fx-font-weight:bold; -fx-text-fill: #555555;";
	public static String TITLED_PANE_STYLE = "-fx-font-family: 'Arial'; -fx-font-size: 10pt; -fx-font-weight:bold; -fx-text-fill: #555555;";
	public static String BUTTON_STYLE = "-fx-font-family: 'Arial'; -fx-font-size: 8pt; -fx-font-weight:bold; -fx-text-fill: #555555;";
	
	// Specific Constants
	public static Double CONTACT_BUTTON_PREF_WIDTH = 195.0;
	public static Double ROOM_BUTTON_GRAPHIC_MARGIN_RIGHT = 10.0;
	public static ContentDisplay ROOM_BUTTON_CONTENT_DISPLAY = ContentDisplay.RIGHT;
	
	
}
