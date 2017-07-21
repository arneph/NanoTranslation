package model;

import java.io.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;

public class Document {
	private String projectFolderPath;
	private String languageFilePath;
	private Translations translations;
	
	public Document() {
		projectFolderPath = "";
		languageFilePath = "";
		translations = new Translations();
	}
	
	public Document(File file) {
		projectFolderPath = "";
		languageFilePath = "";
		translations = new Translations();
		
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    factory.setValidating(true);
		    
		    DocumentBuilder builder = factory.newDocumentBuilder();
		    
		    org.w3c.dom.Document document = builder.parse(file);
		    
		    Node root = document.getChildNodes().item(0);
		    
		    if (root.getNodeName().equals("nano-translation") == false) {
		    	return;
		    }
		    
		    for (int i = 0; i < root.getChildNodes().getLength(); i++) {
		    	Node node = root.getChildNodes().item(i);
		    	
		    	if (node.getNodeName().equals("project-folder")) {
		    		projectFolderPath = node.getFirstChild().getNodeValue();
		    		
		    		if (projectFolderPath == null) {
		    			projectFolderPath = "";
		    		}
		    		
		    	}else if (node.getNodeName().equals("language-file")) {
					languageFilePath = node.getFirstChild().getNodeValue();
					
					if (languageFilePath == null) {
						languageFilePath = "";
					}
					
				}else if (node.getNodeName().equals("languages")) {
					Node languagesNode = node;
					
					for (int j = 0; j < languagesNode.getChildNodes().getLength(); j++) {
						node = languagesNode.getChildNodes().item(j);
						
						if (!node.getNodeName().equals("language")) continue;
						
						String language = node.getFirstChild().getNodeValue();
						
						if (language == null || language.equals("")) {
							continue;
						}
						
						if (translations.getIndexOfLanguage(language) == -1) {
							translations.addLanguage(language);
						}
					}
					
				}else if (node.getNodeName().equals("entries")) {
					Node translationsNode = node;
					
					for (int j = 0; j < translationsNode.getChildNodes().getLength(); j++) {
						node = translationsNode.getChildNodes().item(j);
						
						if (!node.getNodeName().equals("entry")) continue;
						
						Node entry = node;
						
						String key = null;
						
						for (int k = 0; k < entry.getChildNodes().getLength(); k++) {
							node = entry.getChildNodes().item(k);
							
							if (node.getNodeName().equals("key")) {
								key = node.getFirstChild().getNodeValue();
								
								if (translations.getIndexOfKeyOfEntry(key) != -1) {
									break;
								}else{
									translations.addEntry(key);
								}
								
							}else if (node.getNodeName().equals("translation")) {
								int index = translations.getIndexOfKeyOfEntry(key);
								String language = node.getAttributes().getNamedItem("language").getNodeValue();
								
								if (index == -1 || translations.getIndexOfLanguage(language) == -1) continue;
								
								translations.setTranslationForLanguageOfEntryAtIndex(index, language, node.getFirstChild().getNodeValue());
								
							}else if (node.getNodeName().equals("information")) {
								int index = translations.getIndexOfKeyOfEntry(key);
								
								if (index == -1) continue;
								
								translations.setInformationOfEntryAtIndex(index, node.getFirstChild().getNodeValue());
							}
						}
					}
				}
		    }
		    
		}catch (Exception e) {}
	}
	
	public void writeToFile(File file) {
		projectFolderPath = "Hello!\n<a></a>\nBye!";
		languageFilePath = "Hi!!!";
		
	    try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			
			org.w3c.dom.Document document = builder.newDocument();
			
			Element nanoTranslation = document.createElement("nano-translation");
			
			Element projectFolderElement = document.createElement("project-folder");
			projectFolderElement.appendChild(document.createTextNode(projectFolderPath));
			nanoTranslation.appendChild(projectFolderElement);
			
			Element languageFileElement = document.createElement("language-file");
			languageFileElement.appendChild(document.createTextNode(languageFilePath));
			nanoTranslation.appendChild(languageFileElement);
			
			Element languagesElement = document.createElement("languages");
			
			for (int i = 0; i < translations.getNumberOfLanguages(); i++) {
				Element languageElement = document.createElement("language");
				languageElement.appendChild(document.createTextNode(translations.getLanguageAtIndex(i)));
				languagesElement.appendChild(languageElement);
			}
			
			nanoTranslation.appendChild(languagesElement);
			
			Element entriesElement = document.createElement("entries");
			
			for (int i = 0; i < translations.getNumberOfEntries(); i++) {
				Element entryElement = document.createElement("entry");
				entriesElement.appendChild(entryElement);
				
				Element keyElement = document.createElement("key");
				keyElement.appendChild(document.createTextNode(translations.getKeyOfEntryAtIndex(i)));
				entryElement.appendChild(keyElement);
				
				for (int j = 0; j < translations.getNumberOfLanguages(); j++) {
					Element translationElement = document.createElement("translation");
					translationElement.setAttribute("language", translations.getLanguageAtIndex(j));
					translationElement.appendChild(document.createTextNode(translations.getTranslationForLanguageOfEntryAtIndex(i, j)));
					entryElement.appendChild(translationElement);
				}
				
				Element informationElement = document.createElement("information");
				informationElement.appendChild(document.createTextNode(translations.getInformationOfEntryAtIndex(i)));
				entryElement.appendChild(informationElement);
			}
			
			nanoTranslation.appendChild(entriesElement);
			
			document.appendChild(nanoTranslation);
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			
			DOMSource source = new DOMSource(document);
			
			transformer.transform(source, new StreamResult(file));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public File getProjectFolder() {
		return new File(projectFolderPath);
	}
	
	public String getProjectFolderPath() {
		return projectFolderPath;
	}
	
	public void setProjectFolder(File projectFolder) {
		if (projectFolder == null) {
			this.projectFolderPath = "";
		}else{
			this.projectFolderPath = projectFolder.getPath();			
		}
	}
	
	public void setProjectFolderPath(String projectFolderPath) {
		if (projectFolderPath == null) {
			this.projectFolderPath = "";
		}else{
			this.projectFolderPath = projectFolderPath;			
		}
	}
	
	public File getLanguageFile() {
		return new File(languageFilePath);
	}
	
	public String getLanguageFilePath() {
		return languageFilePath;
	}
	
	public void setLanguageFile(File languageFile) {
		if (languageFile == null) {
			this.languageFilePath = "";
		}else{
			this.languageFilePath = languageFile.getPath();
		}
	}
	
	public void setLanguageFilePath(String languageFilePath) {
		if (languageFilePath == null) {
			this.languageFilePath = "";
		}else{
			this.languageFilePath = languageFilePath;			
		}
	}
	
	public Translations getTranslations() {
		return translations;
	}
	
	public void setTranslations(Translations translations) {
		this.translations = translations;
	}
	
	public boolean equals(Document d) {
		if (d == null) return false;
		
		if (getProjectFolderPath().equals(d.getProjectFolderPath()) == false) return false;
		if (getLanguageFilePath().equals(d.getLanguageFilePath()) == false) return false;
		if (getTranslations().equals(d.getTranslations()) == false) return false;
		
		return true;
	}
	
	public Document clone() {
		Document d = new Document();
		
		d.setProjectFolderPath(getProjectFolderPath());
		d.setLanguageFilePath(getLanguageFilePath());
		d.setTranslations(getTranslations().clone());
		
		return d;
	}
	
}
