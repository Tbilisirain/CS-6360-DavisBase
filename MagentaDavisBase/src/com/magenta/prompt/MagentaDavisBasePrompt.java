package com.magenta.prompt;

import java.io.RandomAccessFile;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;

public class MagentaDavisBasePrompt {

	private String prompt = "davisql> ";
	private String version = "v1.0b";
	private String copyright = "©2019 Magenta Group";
	private boolean isExit = false;
	
	private long pageSize = 512; 

	private Scanner scanner = new Scanner(System.in).useDelimiter(";");
	
	public MagentaDavisBasePrompt() {
		
	}
	
    public void prompt() {

		splashScreen();
		
		String userCommand = ""; 

		while(!isExit) {
			System.out.print(prompt);
			userCommand = scanner.next().replace("\n", " ").replace("\r", "").trim().toLowerCase();
			parseUserCommand(userCommand);
		}
		System.out.println("Exiting...");
		return;
	}

	private void splashScreen() {
		System.out.println(line("-",80));
        System.out.println("Welcome to MagentaDavisBaseLite");
		System.out.println("DavisBaseLite Version " + getVersion());
		System.out.println(getCopyright());
		System.out.println("\nType \"help;\" to display supported commands.");
		System.out.println(line("-",80));
		return;
	}
	
	private String line(String s,int num) {
		String a = "";
		for(int i=0;i<num;i++) {
			a += s;
		}
		return a;
	}
	
	public void help() {
		System.out.println(line("*",80));
		
		System.out.println("SUPPORTED COMMANDS\n");
		System.out.println("All commands below are case insensitive\n");
		
		System.out.println("SHOW TABLES;");
		System.out.println("\tDisplay the names of all tables.\n");
		
		System.out.println("CREATE TABLE <table_name> (");
		System.out.println("\trow_id INT PRIMARY KEY, ");
		System.out.println("\t<column_name2> <data_type2> [NOT NULL], ");
		System.out.println("\t<column_name3> <data_type3> [NOT NULL], ");
		System.out.println("\t...");
		System.out.println(");");
		System.out.println("\tCreate a table schema.\n");
		
		System.out.println("DROP TABLE <table_name>;");
		System.out.println("\tRemove table data (i.e. all records) and its schema.\n");
		
		System.out.println("CREATE [UNIQUE] INDEX ON <table_name> (<column_list>);");
		System.out.println("\tCreate an index table from <table_name> using <column_list> as a key.\n");
		
		System.out.println("SELECT <column_list> FROM <table_name> [WHERE <condition>];");
		System.out.println("\tDisplay table records whose optional <condition>");
		System.out.println("\tis <column_name> = <value>.\n");
		
		System.out.println("UPDATE TABLE <table_name> SET <column_name> = <value> [WHERE <condition>];");
		System.out.println("\tModify records data whose optional <condition> is\n");
		
		System.out.println("VERSION;");
		System.out.println("\tDisplay the program version.\n");
		
		System.out.println("HELP;");
		System.out.println("\tDisplay this help information.\n");
		
		System.out.println("EXIT;");
		System.out.println("\tExit the program.\n");
		
		System.out.println(line("*",80));
		return;
	}
	
	public long getPageSize() {
		return pageSize;
	}

	public void setPageSize(long pageSize) {
		this.pageSize = pageSize;
	}
	
	public String getVersion() {
		return version;
	}
	
	public String getCopyright() {
		return copyright;
	}
	
	public void displayVersion() {
		System.out.println("DavisBaseLite Version " + getVersion());
		System.out.println(getCopyright());
		return;
	}
		
	private void parseUserCommand (String userCommand) {
		
		/* commandTokens is an array of Strings that contains one token per array element 
		 * The first token can be used to determine the type of command 
		 * The other tokens can be used to pass relevant parameters to each command-specific
		 * method inside each case statement */
		// String[] commandTokens = userCommand.split(" ");
		ArrayList<String> commandTokens = new ArrayList<String>(Arrays.asList(userCommand.split(" ")));
		
		switch (commandTokens.get(0)) {
			case "select":
				System.out.println("CASE: SELECT");
				parseQuery(userCommand);
				break;
			case "drop":
				System.out.println("CASE: DROP");
				dropTable(userCommand);
				break;
			case "create":
				System.out.println("CASE: CREATE");
				parseCreateTable(userCommand);
				break;
			case "update":
				System.out.println("CASE: UPDATE");
				parseUpdate(userCommand);
				break;
			case "help":
				help();
				break;
			case "version":
				displayVersion();
				break;
			case "exit":
				isExit = true;
				break;
			case "quit":
				isExit = true;
			default:
				System.out.println("I didn't understand the command: \"" + userCommand + "\"");
				break;
		}
		return;
	}
	

	/**
	 *  Stub method for dropping tables
	 *  @param dropTableString is a String of the user input
	 */
	public void dropTable(String dropTableString) {
		System.out.println("STUB: This is the dropTable method.");
		System.out.println("\tParsing the string:\"" + dropTableString + "\"");
		return;
	}
	
	/**
	 *  Stub method for executing queries
	 *  @param queryString is a String of the user input
	 */
	public void parseQuery(String queryString) {
		System.out.println("STUB: This is the parseQuery method");
		System.out.println("\tParsing the string:\"" + queryString + "\"");
		return;
	}

	/**
	 *  Stub method for updating records
	 *  @param updateString is a String of the user input
	 */
	public void parseUpdate(String updateString) {
		System.out.println("STUB: This is the dropTable method");
		System.out.println("Parsing the string:\"" + updateString + "\"");
		return;
	}

	
	/**
	 *  Stub method for creating new tables
	 *  @param queryString is a String of the user input
	 */
	public void parseCreateTable(String createTableString) {
		
		System.out.println("STUB: Calling your method to create a table");
		System.out.println("Parsing the string:\"" + createTableString + "\"");
		ArrayList<String> createTableTokens = new ArrayList<String>(Arrays.asList(createTableString.split(" ")));

		/* Define table file name */
		String tableFileName = createTableTokens.get(2) + ".tbl";

		/* YOUR CODE GOES HERE */
		
		/*  Code to create a .tbl file to contain table data */
		try {
			/*  Create RandomAccessFile tableFile in read-write mode.
			 *  Note that this doesn't create the table file in the correct directory structure
			 */
			RandomAccessFile tableFile = new RandomAccessFile(tableFileName, "rw");
			tableFile.setLength(pageSize);
			tableFile.seek(0);
			tableFile.writeInt(63);
		}
		catch(Exception e) {
			System.out.println(e);
		}
		
		/*  Code to insert a row in the davisbase_tables table 
		 *  i.e. database catalog meta-data 
		 */
		
		/*  Code to insert rows in the davisbase_columns table  
		 *  for each column in the new table 
		 *  i.e. database catalog meta-data 
		 */
	}
}
