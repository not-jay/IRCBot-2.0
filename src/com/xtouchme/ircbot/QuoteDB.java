package com.xtouchme.ircbot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class QuoteDB {

	private static QuoteDB quoteDB = null;
	public static HashMap<String, String> db;
	public File quoteFile;
	
	private QuoteDB() {}
	
	public static synchronized QuoteDB getQuoteDB() {
		if(quoteDB == null) {
			quoteDB = new QuoteDB();
			try {
				quoteDB.initialize();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return quoteDB;
	}
	
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
	
	public void setFile(String file) {
		quoteFile = new File(file);
	}
	
	public void initialize() throws FileNotFoundException {
		db = new HashMap<String, String>();
		setFile("quoteFile.txt");
		load();
	}
	
	public void addToDB(String identifier, String quote) {
		db.put(identifier, quote);
		save();
	}
	
	public void removeFromDB(String identifier) {
		db.remove(identifier);
		save();
	}
	
	public String retrieveQuote(String identifier) {
		if(db.containsKey(identifier)) return db.get(identifier);
		return "No quote found for "+identifier;
	}
	
	public String listKeys() {
		String keys = "";
		
		for(String s : db.keySet()) {
			keys += ", "+s;
		}
		
		return keys.substring(2);
	}
	
	@SuppressWarnings("unchecked")
	public void load() throws FileNotFoundException {
		if(!quoteFile.exists()) return;
		
		FileInputStream input = new FileInputStream(quoteFile);		
		try {
			ObjectInputStream reader = new ObjectInputStream(input);
			db = (HashMap<String, String>)reader.readObject();
			
			System.out.println("-- Start of Quotes --");
			for(int i = 0; i < db.size(); i++) {
				System.out.print(db.keySet().toArray()[i] + " - ");
				System.out.println(db.values().toArray()[i]);
			}
			System.out.println("--  End of Quotes  --");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void save() {
		if(!quoteFile.exists()) {
			try {
				quoteFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		FileOutputStream out = null;
		ObjectOutputStream writer = null;
		
		try {
			out = new FileOutputStream(quoteFile);
			writer = new ObjectOutputStream(out);
			writer.writeObject(db);
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
