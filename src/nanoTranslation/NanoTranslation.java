package nanoTranslation;

import java.io.*;
import java.util.*;

import ui.*;

public class NanoTranslation {
	private static HashSet<DocumentWindowController> windowControllers;
	
	public static void main(String[] args) {
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		Icons.loadIcons();
		
		windowControllers = new HashSet<>();
		
		newWindowController();
	}
	
	public static void newWindowController() {
		windowControllers.add(new DocumentWindowController());
	}
	
	public static void newWindowController(File file) {
		windowControllers.add(new DocumentWindowController(file));
	}
	
	public static void removeWindowController(DocumentWindowController windowController) {
		windowControllers.remove(windowController);
		
		if (windowControllers.isEmpty()) {
			System.exit(0);
		}
	}
	
}
