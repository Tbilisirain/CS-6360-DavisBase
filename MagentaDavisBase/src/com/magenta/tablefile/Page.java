package com.magenta.tablefile;
import java.io.RandomAccessFile;
import java.io.File;
import java.io.FileNotFoundException;
import com.magenta.prompt.MagentaDavisBasePrompt;

public class Page {
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
	
	public static void showDatabase()
	{
		
		File f= new File("data");
		String[] listDir = f.list();
		
		for(String i:listDir)
		{
			if(i.equals("catalog") || i.equals("user_data"))
				continue;
			System.out.println(i);
		}	
	}
	// Parameters: Need to figure representation

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


	private static void drop(String i, String database) {
		// TODO Auto-generated method stub
		
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
	// Create string to be parsed.
	public void createTable(String table, String[] col) {
		try {
		RandomAccessFile file = new RandomAccessFile("data/"+table+".tbl","rw");
		file.setLength((long)pageSize);
		file.seek(0);
		file.writeByte(0x0D);
		file.close();
		file = new RandomAccessFile("data/davisbase_tables.tbl","rw");
		int noOfPages = Operations.pages(file);
		int page = 1;
		//Incomplete
		// Getting data from pages. Additional methods need to be defined to write in davisbase_tables and davisbase_columns
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		// Index file needs to be created based on which is set as primary key
	}
	public void insert() {
		// Inserting into table file and the index file
	}
	

}
