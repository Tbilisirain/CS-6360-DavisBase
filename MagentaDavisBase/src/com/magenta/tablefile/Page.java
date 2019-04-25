package com.magenta.tablefile;
import java.io.RandomAccessFile;
import java.io.File;
import java.io.FileNotFoundException;
import com.magenta.prompt.MagentaDavisBasePrompt;

public class Page {
	Header h;
	//Leaf level info
	public static final int pageSize = 512;
	/*
	This file should contain table related methods namely:
	show()
	showDatabase()
	drop(), params : table, database
	dropDatabase(), params: database
	createDatabase(), params: database
	getPayload(), params: file, location as explained by professor today.
	createTable(), params: table, cols
	insert(), params: table, values
	*/
	byte noOfColumns;
	byte dataTypes[];
	byte Payload[];// Need to define the payload size based on size of the page.

	MagentaDavisBasePrompt magentaDavisBase = new MagentaDavisBasePrompt();
	
	public void Page() {
		
	}
	
	// DDL and DML  methods would be defined here. Page related methods will be called from  Operations.java file
	
	public void show() {
		
	}
	public void showDatabase() {
		
	}
	// Parameters: Need to figure representation
	public void drop() {
		
	}
	
	public static void dropDatabase(String database)
	{
		File f= new File("data\\"+database);
		String[] listDir = f.list();
		
		for(String i:listDir)
		{
			if(i.equals("catalog") || i.equals("user_data"))
				continue;
			drop(i,database);
		}
		File dropFile = new File("data", database); 
		dropFile.delete();
	}


	public void createDatabase(String database) {
			try 
		{
			File db = new File("data\\"+database);
			
			if(db.exists())
			{
				System.out.println("Database already exists");
				return;
			}
			db.mkdir();
			magentaDavisBase.currentDatabase=database;
			
			System.out.println("Database "+database+" created successfully.");
		}
		catch (SecurityException se) 
		{
			System.out.println("Unable to create catalog directory :"+se);			
		}

	}
	public void getPayload() {
		
	}
	public void createTable() {
		// Index file needs to be created based on which is set as primary key
	}
	public void insert() {
		// Inserting into table file and the index file
	}
	

}
