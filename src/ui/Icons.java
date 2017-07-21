package ui;

import javax.imageio.*;
import javax.swing.*;

public class Icons {
	private static ImageIcon appIcon;
	private static ImageIcon uncheckedIcon;
	private static ImageIcon checkedIcon;
	
	public static void loadIcons() {
		appIcon = null;
		uncheckedIcon = null;
		checkedIcon = null;
		
		try{
			appIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("images/appicon.png")));
			uncheckedIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("images/unchecked.png")));
			checkedIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("images/Checked.png")));
		}catch (Exception e) {}
	}
	
	public static ImageIcon appIcon() {
		return appIcon;
	}
	
	public static ImageIcon uncheckedIcon() {
		return uncheckedIcon;
	}
	
	public static ImageIcon checkecIcon() {
		return checkedIcon;
	}
	
}
