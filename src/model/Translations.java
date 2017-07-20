package model;

import java.util.*;

public class Translations {
	private class Entry implements Comparable<Entry> {
		public String key;
		public HashMap<String, String> translations;
		public String information;
		
		public int compareTo(Entry e) {
			if (e == null) {
				return key.compareTo(null);
			}else{
				return key.compareTo(e.key);				
			}
		}
	}
	
	private String[] languages;
	private Entry[] entries;
	
	public Translations() {
		languages = new String[0];
		entries = new Entry[0];
	}
	
	public int getNumberOfLanguages() {
		return languages.length;
	}
	
	public String getLanguageAtIndex(int index) {
		if (index < 0 || index >= languages.length) {
			throw new IllegalArgumentException();
		}
		
		return languages[index];
	}
	
	public int getIndexOfLanguage(String language) {
		for (int i = 0; i < languages.length; i++) {
			if (languages[i].equals(language)) {
				return i;
			}
		}
		
		return -1;
	}
	
	public String[] getLanguages() {
		return languages;
	}
	
	public void setLanguageAtIndex(int index, String language) {
		if (index < 0 || index >= languages.length || 
			language == null || language.equals("")) {
			throw new IllegalArgumentException();
		}
		
		if (languages[index].equals(language)) {
			return;
		}else if (getIndexOfLanguage(language) != -1) {
			throw new IllegalArgumentException();
		}
		
		String oldLanguage = languages[index];
		
		languages[index] = language;
		
		Arrays.sort(languages);
		
		for (Entry entry : entries) {
			entry.translations.put(language, entry.translations.remove(oldLanguage));
		}
	}
	
	public void addLanguage(String language, String languageTag) {
		if (language == null || language.equals("") || 
			languageTag == null || languageTag.equals("") || 
			getIndexOfLanguage(language) != -1) {
			throw new IllegalArgumentException();
		}
		
		int n = languages.length;
		
		languages = Arrays.copyOf(languages, n + 1);
		languages[n] = language;
		
		Arrays.sort(languages);
		
		for (Entry entry : entries) {
			entry.translations.put(language, "");
		}
	}
	
	public void removeLanguage(int index) {
		if (index < 0 || index >= languages.length) {
			throw new IllegalArgumentException();
		}

		String oldLanguage = languages[index];
		
		int n = languages.length;
		
		for (int i = index; i < n - 1; i++) {
			languages[index] = languages[index + 1];
		}
		
		languages = Arrays.copyOf(languages, n - 1);
		
		for (Entry entry : entries) {
			entry.translations.remove(oldLanguage);
		}
	}
	
	public int getNumberOfEntries() {
		return entries.length;
	}
	
	public String getKeyOfEntryAtIndex(int index) {
		if (index < 0 || index >= entries.length) {
			throw new IllegalArgumentException();
		}
		
		return entries[index].key;
	}
	
	public int getIndexOfKeyOfEntry(String key) {
		if (key == null || key.equals("")) {
			return -1;
		}
		
		return getIndexOfKey(key, 0, entries.length - 1);
	}
	
	private int getIndexOfKey(String key, int a, int b) {
		int m = a + (b - a) / 2;
		int r = entries[m].key.compareTo(key);
		
		if (r < 0) {
			if (m > a) {
				return getIndexOfKey(key, a, m - 1);
			}else{
				return -1;
			}
		}else if (r > 0) {
			if (m < b) {
				return getIndexOfKey(key, m + 1, b);
			}else{
				return - 1;
			}
		}else{
			return m;
		}
	}
	
	public String[] getKeys() {
		String[] keys = new String[entries.length];
		
		for (int i = 0; i < entries.length; i++) {
			keys[i] = entries[i].key;
		}
		
		return keys;
	}
	
	public void setKeyOfEntryAtIndex(int index, String key) {
		if (index < 0 || index >= entries.length || 
			key == null || key.equals("")) {
			throw new IllegalArgumentException();
		}
		
		if (entries[index].key.equals(key)) {
			return;
		}else if (getIndexOfKeyOfEntry(key) != -1) {
			throw new IllegalArgumentException();
		}
		
		entries[index].key = key;
		
		Arrays.sort(entries);
	}
	
	public String getTranslationForLanguageOfEntryAtIndex(int index, String language) {
		if (index < 0 || index >= entries.length || 
			getIndexOfLanguage(language) == -1) {
			throw new IllegalArgumentException();
		}
		
		return entries[index].translations.get(language);
	}
	
	public String getTranslationForLanguageOfEntryAtIndex(int entryIndex, int languageIndex) {
		if (entryIndex < 0 || entryIndex >= entries.length || 
			languageIndex < 0 || languageIndex >= languages.length) {
			throw new IllegalArgumentException();
		}
		
		return entries[entryIndex].translations.get(languages[languageIndex]);
	}
	
	public void setTranslationForLanguageOfEntryAtIndex(int index, String language, String translation) {
		if (index < 0 || index >= entries.length || 
			getIndexOfLanguage(language) == -1 || 
			translation == null) {
			throw new IllegalArgumentException();
		}
		
		entries[index].translations.put(language, translation);
	}

	public void setTranslationForLanguageOfEntryAtIndex(int entryIndex, int languageIndex, String translation) {
		if (entryIndex < 0 || entryIndex >= entries.length || 
			languageIndex < 0 || languageIndex >= languages.length || 
			translation == null) {
			throw new IllegalArgumentException();
		}
		
		entries[entryIndex].translations.put(languages[languageIndex], translation);
	}
	
	public String getInformationOfEntryAtIndex(int index) {
		if (index < 0 || index >= entries.length) {
			throw new IllegalArgumentException();
		}
		
		return entries[index].information;
	}
	
	public void setInformationOfEntryAtIndex(int index, String information) {
		if (index < 0 || index >= entries.length || 
			information == null) {
			throw new IllegalArgumentException();
		}
		
		entries[index].information = information;
	}
	
}
