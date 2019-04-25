package com.magenta.prompt;

import java.io.RandomAccessFile;
import java.util.Scanner;

import com.magenta.service.MagentaDavisBaseService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MagentaDavisBasePrompt {

	private String prompt = "davisql> ";
	private String version = "v1.0b";
	private String copyright = "©2019 Magenta Group";
	private boolean isExit = false;
	
	private long pageSize = 512; 

	private Scanner scanner = new Scanner(System.in).useDelimiter(";");
	static String currentDatabase = "user_data";
	
	private MagentaDavisBaseService magentaDavisBaseService;
	
	public MagentaDavisBasePrompt() {
		magentaDavisBaseService = new MagentaDavisBaseService();
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
		
		System.out.println("INSERT INTO TABLE (<column_list>) <table_name> VALUES (<value_list>);");
		System.out.println("\tInsert a new record into the indicated table.\n");
		
		System.out.println("DELETE FROM TABLE <table_name> [WHERE row_id = <key_value>];");
		System.out.println("\tDelete a single row/record from <table_name> given the row_id primary key.\n");
		
		System.out.println("UPDATE TABLE <table_name> SET <column_name> = <value> [WHERE <condition>];");
		System.out.println("\tModify records data whose optional <condition> is\n");
		
		System.out.println("SELECT <column_list> FROM <table_name> [WHERE <condition>];");
		System.out.println("\tDisplay table records whose optional <condition>");
		System.out.println("\tis <column_name> = <value>.\n");
		
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
		
		ArrayList<String> commandTokens = new ArrayList<String>(Arrays.asList(userCommand.split(" ")));
		
		switch (commandTokens.get(0)) {
			case "show":
				System.out.println("CASE: SHOW");
				try {
					parseShowCommand(userCommand, commandTokens);
				} catch (Exception e) {
					System.out.println(e);
				}
				break;
			case "create":
				System.out.println("CASE: CREATE");
				try {
					parseCreateCommand(userCommand, commandTokens);
				} catch (Exception e) {
					System.out.println(e);
				}
				break;
			case "drop":
				System.out.println("CASE: DROP");
				try {
					parseDropCommand(userCommand, commandTokens);
				} catch (Exception e) {
					System.out.println(e);
				}
				break;
			case "insert":
				System.out.println("CASE: INSERT");
				try {
					parseInsertCommand(userCommand, commandTokens);
				} catch (Exception e) {
					System.out.println(e);
				}
				break;
			case "delete":
				System.out.println("CASE: DELETE");
				try {
					parseDeleteCommand(userCommand, commandTokens);
				} catch (Exception e) {
					System.out.println(e);
				}
				break;
			case "update":
				System.out.println("CASE: UPDATE");
				try {
					parseUpdateCommand(userCommand, commandTokens);
				} catch (Exception e) {
					System.out.println(e);
				}
				break;
			case "select":
				System.out.println("CASE: SELECT");
				try {
					parseQueryCommand(userCommand, commandTokens);
				} catch (Exception e) {
					System.out.println(e);
				}
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
			default:
				System.out.println("I didn't understand the command: \"" + userCommand + "\"");
				break;
		}
		return;
	}
	
	private void parseShowCommand(String userCommand, List<String> commandTokens) throws Exception{
		if (commandTokens.size() == 2 && commandTokens.get(1).equals("tables")) {
			List<String> columnList = new ArrayList<String>(Arrays.asList("table_name"));
			String tableName = "davisbase_tables";
			magentaDavisBaseService.executeQuery(columnList, tableName, null);
		} else {
			throw new Exception("IllegalSyntaxError: " + "I didn't understand the command: \"" + userCommand + "\"");
		}
	}
	
	private void parseCreateCommand(String userCommand, List<String> commandTokens) throws Exception{
		
	}
	
	private void parseDropCommand(String userCommand, List<String> commandTokens) throws Exception{
		
	}
	
	private void parseInsertCommand(String userCommand, List<String> commandTokens) throws Exception{
		
	}
	
	private void parseDeleteCommand(String userCommand, List<String> commandTokens) throws Exception{
		
	}
	
	private void parseUpdateCommand(String userCommand, List<String> commandTokens) throws Exception{
		
	}
	
	private void parseQueryCommand(String userCommand, List<String> commandTokens) throws Exception{
		
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
