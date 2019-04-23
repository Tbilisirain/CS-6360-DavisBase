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
	public void getHeader() {
		
	}
	public void getData() { // Relevant record
		
	}
	
	

}
