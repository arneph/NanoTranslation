package model;

import java.io.*;

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
		
		String fileContents = "";
		
		try {
			BufferedReader reader = new BufferedReader(
										new InputStreamReader(
											new FileInputStream(file), 
											"UTF-8"));
			
			String line = null;
			while ((line = reader.readLine()) != null) {
				while (line.startsWith("\t")) line = line.substring(1);
				fileContents += line + "\n";
			}
			
			reader.close();
			
		}catch (Exception e) {
			return;
		}
		
		String[] lines = fileContents.split("\n");
		
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			
			if (line.startsWith("<project-folder>") && 
				line.endsWith("</project-folder>")) {
				projectFolderPath = line.substring(16, line.length() - 17);
				
			}else if (line.startsWith("<language-file>") && 
					  line.endsWith("</language-file>")) {
				languageFilePath = line.substring(15, line.length() - 16);
				
			}else if (line.equals("<languages>")) {
				for (i++; i < lines.length; i++) {
					line = lines[i];
					
					if (line.startsWith("<language>") && 
						line.endsWith("</language>")) {
						translations.addLanguage(line.substring(10, line.length() - 11));
						
					}else if (line.equals("</languages>")) {
						break;
					}
				}
				
			}else if (line.equals("<entries>")) {
				for (i++; i < lines.length; i++) {
					line = lines[i];
					
					if (line.startsWith("<entry>")) {
						String key = null;
						
						for (i++; i < lines.length; i++) {
							line = lines[i];
							
							if (line.startsWith("<key>") && 
								line.endsWith("</key>")) {
								key = line.substring(5, line.length() - 6);
								
								if (key.length() == 0) break;
								if (translations.getIndexOfKeyOfEntry(key) != -1) break;
								
								translations.addEntry(key);
								
							}else if (line.equals("<translation>")) {
								String language = null;
								String translation = null;
								
								for (i++; i < lines.length; i++) {
									line = lines[i];
									
									if (line.startsWith("<language>") && 
										line.endsWith("</language>")) {
										language = line.substring(10, line.length() - 11);
										
									}else if (line.startsWith("<value>") && 
											  line.endsWith("</value>")) {
										translation = line.substring(7, line.length() - 8);
										
									}else if (line.equals("</translation>")) {
										break;
									}
								}
								
								if (key != null && language != null && translation != null) {
									int index = translations.getIndexOfKeyOfEntry(key);
									
									translations.setTranslationForLanguageOfEntryAtIndex(index, language, translation);
								}
								
							}else if (line.startsWith("<information>") && 
									  line.endsWith("</information>")) {
								String information = line.substring(13, line.length() - 14);
								
								if (key != null) {
									int index = translations.getIndexOfKeyOfEntry(key);
									
									translations.setInformationOfEntryAtIndex(index, information);
								}
								
							}else if (line.equals("</entry>")) {
								break;
							}
						}
						
					}else if (line.equals("</entries>")) {
						break;
					}
				}
			}
		}
	}
	
	public void writeToFile(File file) {
		String fileContents = "";
		
		fileContents += "<project-folder>" + projectFolderPath + "</project-folder>\n";
		fileContents += "<language-file>" + languageFilePath + "</language-file>\n";
		fileContents += "<languages>\n";
		
		for (String language : translations.getLanguages()) {
			fileContents += "\t<language>" + language + "</language>\n";
		}
		
		fileContents += "</languages>\n";
		fileContents += "<entries>\n";
		
		for (int i = 0; i < translations.getNumberOfEntries(); i++) {
			String key = translations.getKeyOfEntryAtIndex(i);
			String information = translations.getInformationOfEntryAtIndex(i);
			
			fileContents += "\t<entry>\n";
			fileContents += "\t\t<key>" + key + "</key>\n";
			
			for (String language : translations.getLanguages()) {
				String translation = translations.getTranslationForLanguageOfEntryAtIndex(i, language);
				
				fileContents += "\t\t<translation>\n";
				fileContents += "\t\t\t<language>" + language + "</language>\n";
				fileContents += "\t\t\t<value>" + translation + "</value>\n";
				fileContents += "\t\t</translation>";
			}
			
			fileContents += "\t\t<information>" + information + "</information>\n";
			fileContents += "\t</entry>\n";
		}
		
		fileContents += "</entries>";
		
		try{
			Writer writer = new BufferedWriter(
								new OutputStreamWriter(
									new FileOutputStream(file), 
									"UTF-8"));
			
			writer.write(fileContents);
			
			writer.flush();
			writer.close();
		}catch (Exception ex) {}
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
