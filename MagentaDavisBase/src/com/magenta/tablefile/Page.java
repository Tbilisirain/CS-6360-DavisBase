package com.magenta.tablefile;
import java.io.RandomAccessFile;
import java.io.File;
import java.io.FileNotFoundException;
import com.magenta.prompt.MagentaDavisBasePrompt;
import com.magenta.indexfile.*;

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
	
		public static void show()
		{
			String[] cols = {"table_name"};
			String[] cmp = new String[0];
			String table = "davisbase_tables";
		
			//select("data\\catalog\\"+table+".tbl",table, cols, cmp);
		}

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
	public static  void parseCreate(String createString) {
		System.out.println("Create , Parsing the string");
		String [] tokens = createString.split(" ");
		if(tokens[1].compareTo("index")==0) {
			String col = tokens[4];
			String colName = col.substring(1, col.length()-1);
			com.magenta.indexfile.Page.createIndex(tokens[3], colName, "String");
			
		}
		else {
			if(tokens[1].compareTo("table")>0) {
				System.out.println("Wrong Syntax");
			}
			else {
				String tableName = tokens[2];
				String[] temp = createString.split(tableName);
				String cols = temp[1].trim();
				String [] createColumns = cols.substring(1, cols.length()-1).split(",");
				for(int i = 0;i<createColumns.length;i++) {
					createColumns[i] = createColumns[i].trim();
				}
				// Check if table name exists in if
				createTable(tableName,createColumns);
			}
		}
	}
	public static void createTable(String table, String[] col) {
		try {
		RandomAccessFile file = new RandomAccessFile("data/"+table+".tbl","rw");
		file.setLength((long)pageSize);
		file.seek(0);
		file.writeByte(0x0D);
		file.close();
		file = new RandomAccessFile("data/davisbase_tables.tbl","rw");
		int noOfPages = Operations.pages(file);
		int page = 1;
		for(int p = 1;p<=noOfPages;p++) {
			int rm = Operations.getRightMost(file, p);
			if(rm==0) {
				page = p;
			}
		}
		int[] keys = Operations.getKeyArray(file, page);
		int l = keys[0];
		for(int i = 0; i < keys.length; i++)
			if(keys[i]>l)
				l = keys[i];
		file.close();
		
		String[] values = {Integer.toString(l+1), table};
		insertInto("davisbase_tables", values);

		file = new RandomAccessFile("data/davisbase_columns.tbl", "rw");
		
		noOfPages = Operations.pages(file);
		page=1;
		for(int p = 1; p <= noOfPages; p++){
			int rm = Operations.getRightMost(file, p);
			if(rm == 0)
				page = p;
		}
		
		keys = Operations.getKeyArray(file, page);
		l = keys[0];
		for(int i = 0; i < keys.length; i++)
			if(keys[i]>l)
				l = keys[i];
		file.close();

		for(int i = 0; i < col.length; i++){
			l = l + 1;
			String[] token = col[i].split(" ");
			String col_name = token[0];
			String dt = token[1].toUpperCase();
			String pos = Integer.toString(i+1);
			String nullable;
			if(token.length > 2)
				nullable = "NO";
			else
				 nullable = "YES";
			String[] value = {Integer.toString(l), table, col_name, dt, pos, nullable};
			insertInto("davisbase_columns", value);
		}
		//Incomplete
		// Getting data from pages. Additional methods need to be defined to write in davisbase_tables and davisbase_columns
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		// Index file needs to be created based on which is set as primary key
	}
	public static void insertInto(String table, String[] values){
		try{
			RandomAccessFile file = new RandomAccessFile("data/"+table+".tbl", "rw");
			insertInto(file, table, values);
			file.close();

		}catch(Exception e){
			System.out.println(e);
		}
	}
	public static void insertInto(RandomAccessFile file, String table, String[] values){
		String[] dtype = Operations.getDataType(table);
		String[] nullable = Operations.getNullable(table);

		for(int i = 0; i < nullable.length; i++)
			if(values[i].equals("null") && nullable[i].equals("NO")){
				System.out.println("NULL-value constraint violation");
				System.out.println();
				return;
			}

		int key = new Integer(values[0]);
		int page = Operations.searchKeyPage(file, key);
		if(page != 0)
			if(Operations.hasKey(file, page, key)){
				System.out.println("Uniqueness constraint violation");
				return;
			}
		if(page == 0)
			page = 1;


		byte[] stc = new byte[dtype.length-1];
		short plSize = (short) Operations.calPayloadSize(table, values, stc);
		int cellSize = plSize + 6;
		int offset = Operations.checkLeafSpace(file, page, cellSize);


		if(offset != -1){
			Operations.insertLeafCell(file, page, offset, plSize, key, stc, values);
		}else{
			Operations.splitLeaf(file, page);
			insertInto(file, table, values);
		}
	}
	public static void parseInsertString(String insertString) {
		System.out.println("INSERT METHOD");
		System.out.println("Parsing the string:\"" + insertString + "\"");
		
		String[] tokens=insertString.split(" ");
		String table = tokens[2];
		String[] temp = insertString.split("values");
		String temporary=temp[1].trim();
		String[] insert_vals = temporary.substring(1, temporary.length()-1).split(",");
		for(int i = 0; i < insert_vals.length; i++)
			insert_vals[i] = insert_vals[i].trim();
		if(MagentaDavisBasePrompt.tableExists(table)){
			System.out.println("Table "+table+" does not exist.");
		}
		else
		{
			insertInto(table, insert_vals);
		}

	}
	public static void parseDeleteString(String deleteString) {
		System.out.println("DELETE METHOD");
		System.out.println("Parsing the string:\"" + deleteString + "\"");
		
		String[] tokens=deleteString.split(" ");
		String table = tokens[3];
		String[] temp = deleteString.split("where");
		String cmpTemp = temp[1];
		String[] cmp = MagentaDavisBasePrompt.parserEquation(cmpTemp);
		if(MagentaDavisBasePrompt.tableExists(table)){
			System.out.println("Table "+table+" does not exist.");
		}
		else
		{
			delete(table, cmp);
		}
	}
	public static void delete(String table, String[] cmp){
		try{
		int key = new Integer(cmp[2]);

		RandomAccessFile file = new RandomAccessFile("data/"+table+".tbl", "rw");
		int numPages = Operations.pages(file);
		int page = 0;
		for(int p = 1; p <= numPages; p++)
			if(Operations.hasKey(file, p, key)&Operations.getPageType(file, p)==0x0D){
				page = p;
				break;
			}
		
		if(page==0)
		{
			System.out.println("The given key value does not exist");
			return;
		}
		
		short[] cellsAddr = Operations.getCellArray(file, page);
		int k = 0;
		for(int i = 0; i < cellsAddr.length; i++)
		{
			long loc = Operations.getCellLocation(file, page, i);
			String[] vals = Operations.retrieveValues(file, loc);
			int x = new Integer(vals[0]);
			if(x!=key)
			{
				Operations.setCellOffset(file, page, k, cellsAddr[i]);
				k++;
			}
		}
		Operations.setCellNumber(file, page, (byte)k);
		
		}catch(Exception e)
		{
			System.out.println(e);
		}
		
	}
	public static void dropTable(String dropTableString) {
		System.out.println("DROP METHOD");
		System.out.println("Parsing the string:\"" + dropTableString + "\"");
		
		String[] tokens=dropTableString.split(" ");
		String tableName = tokens[2];
		if(!DavisBase.tableExists(tableName)){
			System.out.println("Table "+tableName+" does not exist.");
		}
		else
		{
			drop(tableName);
		}		

	}
	public static void drop(String table){
		try{
			
			RandomAccessFile file = new RandomAccessFile("data/davisbase_tables.tbl", "rw");
			int numOfPages = Operations.pages(file);
			for(int page = 1; page <= numOfPages; page ++){
				file.seek((page-1)*Operations.pageSize);
				byte fileType = file.readByte();
				if(fileType == 0x0D)
				{
					short[] cellsAddr = Operations.getCellArray(file, page);
					int k = 0;
					for(int i = 0; i < cellsAddr.length; i++)
					{
						long loc = Operations.getCellLocation(file, page, i);
						String[] vals = Operations.retrieveValues(file, loc);
						String tb = vals[1];
						if(!tb.equals(table))
						{
							Operations.setCellOffset(file, page, k, cellsAddr[i]);
							k++;
						}
					}
					Operations.setCellNumber(file, page, (byte)k);
				}
				else
					continue;
			}

			file = new RandomAccessFile("data/davisbase_columns.tbl", "rw");
			numOfPages = Operations.pages(file);
			for(int page = 1; page <= numOfPages; page ++){
				file.seek((page-1)*Operations.pageSize);
				byte fileType = file.readByte();
				if(fileType == 0x0D)
				{
					short[] cellsAddr = Operations.getCellArray(file, page);
					int k = 0;
					for(int i = 0; i < cellsAddr.length; i++)
					{
						long loc = Operations.getCellLocation(file, page, i);
						String[] vals = Operations.retrieveValues(file, loc);
						String tb = vals[1];
						if(!tb.equals(table))
						{
							Operations.setCellOffset(file, page, k, cellsAddr[i]);
							k++;
						}
					}
					Operations.setCellNumber(file, page, (byte)k);
				}
				else
					continue;
			}

			File anOldFile = new File("data", table+".tbl"); 
			anOldFile.delete();
		}catch(Exception e){
			System.out.println(e);
		}

	}
	

}
