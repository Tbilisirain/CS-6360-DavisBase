package com.magenta.prompt;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Scanner;
import java.util.Set;

import com.magenta.persistance.TableColumnSetting;
import com.magenta.service.MagentaDavisBaseService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class MagentaDavisBasePrompt {

	private String prompt = "davisql> ";
	private String version = "v1.0b";
	private String copyright = "©2019 Magenta Group";
	private boolean isExit = false;
	
	private long pageSize = 512; 

	private Scanner scanner = new Scanner(System.in).useDelimiter(";");
	public static String currentDatabase = "user_data";
	
	private MagentaDavisBaseService magentaDavisBaseService;
	
	private Set<String> supportType;
	
	public MagentaDavisBasePrompt() {
		this.magentaDavisBaseService = new MagentaDavisBaseService();
		this.supportType = new HashSet<String>(Arrays.asList(
				"tinynt", "smallint", "int", 
				"bigint", "long", "float", 
				"real", "year", "time", 
				"datetime", "date", "text"));
	}
	
    public void prompt() {

		splashScreen();
		
		String userCommand = ""; 

		while(!isExit) {
			System.out.print(prompt);
			userCommand = scanner.next().replace("\n", "").replace("\r", "").trim().toLowerCase();
			userCommand = userCommand.replace("\t", "");
			try {
				parseUserCommand(userCommand);
			} catch (Exception e) {
				System.out.println(e);
			}
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
	
	public static String line(String s,int num) {
		String a = "";
		for(int i=0;i<num;i++) {
			a += s;
		}
		return a;
	}
	public static boolean tableExists(String tablename){
		tablename = tablename+".tbl";
		
		try {
			File dataDir = new File("data");
			String[] oldTableFiles;
			oldTableFiles = dataDir.list();
			for (int i=0; i<oldTableFiles.length; i++) {
				if(oldTableFiles[i].equals(tablename))
					return true;
			}
		}
		catch (SecurityException se) {
			System.out.println("Unable to create data container directory");
			System.out.println(se);
		}

		return false;
	}
	public static String[] parserEquation(String equ){
		String comparator[] = new String[3];
		String temp[] = new String[2];
		if(equ.contains("=")) {
			temp = equ.split("=");
			comparator[0] = temp[0].trim();
			comparator[1] = "=";
			comparator[2] = temp[1].trim();
		}

		if(equ.contains("<")) {
			temp = equ.split("<");
			comparator[0] = temp[0].trim();
			comparator[1] = "<";
			comparator[2] = temp[1].trim();
	
		}
		
		if(equ.contains(">")) {
			temp = equ.split(">");
			comparator[0] = temp[0].trim();
			comparator[1] = ">";
			comparator[2] = temp[1].trim();
		}
		
		if(equ.contains("<=")) {
			temp = equ.split("<=");
			comparator[0] = temp[0].trim();
			comparator[1] = "<=";
			comparator[2] = temp[1].trim();
		}

		if(equ.contains(">=")) {
			temp = equ.split(">=");
			comparator[0] = temp[0].trim();
			comparator[1] = ">=";
			comparator[2] = temp[1].trim();
		}
		//System.out.println(comparator[1]);
		return comparator;
	}
	
	public void help() {
		System.out.println(line("*",80));
		
		System.out.println("SUPPORTED COMMANDS\n");
		System.out.println("All commands below are case insensitive\n");
		
		System.out.println("SHOW TABLES;");
		System.out.println("\tDisplay the names of all tables.\n");
		
		System.out.println("CREATE TABLE <table_name> (");
		System.out.println("\t<column_name1> <data_type1> [PRIMARY KEY] [NOT NULL] [UNIQUE], ");
		System.out.println("\t<column_name2> <data_type2> [PRIMARY KEY] [NOT NULL] [UNIQUE], ");
		System.out.println("\t...");
		System.out.println(");");
		System.out.println("\tCreate a table schema.\n");
		
		System.out.println("DROP TABLE <table_name>;");
		System.out.println("\tRemove table data (i.e. all records) and its schema.\n");
		
		System.out.println("CREATE INDEX ON <table_name> (<column_list>);");
		System.out.println("\tCreate an index table from <table_name> using <column_list> as a key.\n");
		
		System.out.println("INSERT INTO TABLE (<column_list>) <table_name> VALUES (<value_list>);");
		System.out.println("\tInsert a new record into the indicated table.\n");
		
		System.out.println("DELETE FROM TABLE <table_name> WHERE <condition>;");
		System.out.println("\tDelete a single row/record from <table_name> given the row_id primary key.\n");
		
		System.out.println("UPDATE TABLE <table_name> SET <column_name> = <value> WHERE <condition>;");
		System.out.println("\tModify records data whose optional <condition> is\n");
		
		System.out.println("SELECT <column_list> FROM <table_name> WHERE [NOT] <condition>;");
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
		
	private void parseUserCommand (String userCommand) throws Exception {
		
		ArrayList<String> commandTokens = new ArrayList<String>(Arrays.asList(userCommand.split(" ")));
		
		switch (commandTokens.get(0)) {
			case "show":
				System.out.println("CASE: SHOW");
				parseShowCommand(userCommand, commandTokens);
				break;
			case "create":
				System.out.println("CASE: CREATE");
				parseCreateCommand(userCommand, commandTokens);
				break;
			case "drop":
				System.out.println("CASE: DROP");
				parseDropCommand(userCommand, commandTokens);
				break;
			case "insert":
				System.out.println("CASE: INSERT");
				parseInsertCommand(userCommand, commandTokens);
				break;
			case "delete":
				System.out.println("CASE: DELETE");
				parseDeleteCommand(userCommand, commandTokens);
				break;
			case "update":
				System.out.println("CASE: UPDATE");
				parseUpdateCommand(userCommand, commandTokens);
				break;
			case "select":
				System.out.println("CASE: SELECT");
				parseQueryCommand(userCommand, commandTokens);
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
	
	private void parseShowCommand(String userCommand, List<String> commandTokens) throws Exception {
		if (commandTokens.size() == 2 && commandTokens.get(1).equals("tables")) {
			List<String> columnList = new ArrayList<String>(Arrays.asList("table_name"));
			String tableName = "davisbase_tables";
			magentaDavisBaseService.executeQuery(columnList, tableName, null, false);
		} else {
			throw new Exception("IllegalSyntaxError: " + "I didn't understand the command: \"" + userCommand + "\"");
		}
	}
	
	private void parseCreateCommand(String userCommand, List<String> commandTokens) throws Exception{
		if (commandTokens.size() == 3 && commandTokens.get(1).equals("index")) {
			String tableName = commandTokens.get(2);
			magentaDavisBaseService.createIndex(tableName);
		} else if (commandTokens.size() > 3 && 
				userCommand.indexOf(')') > userCommand.indexOf('(') && 
				userCommand.indexOf('(') >= 0) {
			String tableName = commandTokens.get(2);
			String columnSettingString = userCommand.substring(userCommand.indexOf('(') + 1, userCommand.indexOf(')')).trim().replaceAll(" +", " ");
			String[] columns = columnSettingString.split(",");
			List<TableColumnSetting> columnSettings = new ArrayList<>();
			for (String col : columns) {
				col = col.trim();
				if (col != null && col.length() != 0) {
					String[] temp = col.split(" ");
					if (temp.length >= 2 && temp.length <= 7) {
						String columnName = temp[0];
						String dataType = temp[1];
						if (this.supportType.contains(dataType)) {
							boolean isPrimaryKey = false;
							boolean isNotNull = false;
							boolean isUnique = false;
							for (int i = 2; i < temp.length; i++) {
								if (temp[i].equals("primary") && i + 1 < temp.length && temp[i + 1].equals("key")) {
									isPrimaryKey = true;
									i++;
								} else if (temp[i].equals("not") && i + 1 < temp.length && temp[i + 1].equals("null")) {
									isNotNull = true;
									i++;
								} else if (temp[i].equals("unique")) {
									isUnique = true;
								} else {
									throw new Exception("IllegalSyntaxError: Unknown Table Constraint in \"" + col + "\"");
								}
							}
							columnSettings.add(new TableColumnSetting(columnName, dataType, isPrimaryKey, isNotNull, isUnique));
						} else {
							throw new Exception("DataTypeError: Unsupport Data Type: " + dataType);
						}
					} else {
						throw new Exception("IllegalSyntaxError: " + "I didn't understand the command: \"" + userCommand + "\"");
					}
				}
			}
			magentaDavisBaseService.createTable(tableName, columnSettings);
		} else {
			throw new Exception("IllegalSyntaxError: " + "I didn't understand the command: \"" + userCommand + "\"");
		}
	}
	
	private void parseDropCommand(String userCommand, List<String> commandTokens) throws Exception{
		if (commandTokens.size() == 3 && commandTokens.get(1).equals("table")) {
			String tableName = commandTokens.get(2);
			magentaDavisBaseService.dropTable(tableName);
		} else {
			throw new Exception("IllegalSyntaxError: " + "I didn't understand the command: \"" + userCommand + "\"");
		}
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
