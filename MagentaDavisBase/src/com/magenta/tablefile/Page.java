package com.magenta.tablefile;

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
	public void dropDatabase() {
		
	}
	public void createDatabase() {
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
