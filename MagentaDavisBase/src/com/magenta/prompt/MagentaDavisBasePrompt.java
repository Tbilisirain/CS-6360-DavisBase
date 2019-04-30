package com.magenta.prompt;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.Scanner;
import java.util.Set;

import com.magenta.service.MagentaDavisBaseService;
import com.magenta.persistance.TableColumnSetting;
import com.magenta.prompt.Init;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class MagentaDavisBasePrompt {

	private static String prompt = "davisql> ";
	private static String version = "v1.0b";
	private static String copyright = "©2019 Magenta Group";
	private static boolean isExit = false;
	
	private long pageSize = 512; 

	private static Scanner scanner = new Scanner(System.in).useDelimiter(";");
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
	
	public  void prompt() {
    	Init.init();

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

	public static void splashScreen() {
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
	
	public static void help() {
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
	
	public static String getVersion() {
		return version;
	}
	
	public static String getCopyright() {
		return copyright;
	}
	
	public static void displayVersion() {
		System.out.println("DavisBaseLite Version " + getVersion());
		System.out.println(getCopyright());
		return;
	}
		
	public static void parseUserCommand (String userCommand) throws Exception {
		
		ArrayList<String> commandTokens = new ArrayList<String>(Arrays.asList(userCommand.replaceAll(" +", " ").split(" ")));
		
		switch (commandTokens.get(0)) {
			case "show":
				System.out.println("CASE: SHOW");
				com.magenta.tablefile.Page.showTables();
				break;
			case "create":
				System.out.println("CASE: CREATE");
				com.magenta.tablefile.Page.parseCreate(userCommand);
				break;
			case "drop":
				System.out.println("CASE: DROP");
				com.magenta.tablefile.Page.dropTable(userCommand);
				break;
			case "insert":
				System.out.println("CASE: INSERT");
				com.magenta.tablefile.Page.parseInsertString(userCommand);
				break;
			case "delete":
				System.out.println("CASE: DELETE");
				com.magenta.tablefile.Page.parseDeleteString(userCommand);
				break;
			case "update":
				System.out.println("CASE: UPDATE");
				com.magenta.tablefile.Page.parseDeleteString(userCommand);
				break;
			case "select":
				System.out.println("CASE: SELECT");
				parseQueryString(userCommand);
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
				if (col != null) {
					col = col.trim();
					if (col.length() > 0) {
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
		if (commandTokens.size() >= 7 && commandTokens.get(1).equals("into") && commandTokens.get(2).equals("table")) {
			int columnStringStart = userCommand.indexOf('(');
			int columnStringEnd = userCommand.indexOf(')');
			String columnListString = userCommand.substring(columnStringStart + 1, columnStringEnd).trim();
			
			int valueStringStart = userCommand.lastIndexOf('(');
			int valueStringEnd = userCommand.lastIndexOf(')');
			String valueListString = userCommand.substring(valueStringStart + 1, valueStringEnd).trim();
			
			String tableName = null;
			String tableNameString = userCommand.substring(columnStringEnd + 1, valueStringStart).trim();
			String[] tableNameStringArray = tableNameString.split(" ");
			if (tableNameStringArray.length == 2 && tableNameStringArray[1].equals("values")) {
				tableName = tableNameStringArray[0];
			} else {
				throw new Exception("IllegalSyntaxError: " + "I didn't understand the command: \"" + userCommand + "\"");
			}
			Map<String, String> columnValueMap = new HashMap<>();
			if (columnListString.split(",").length == valueListString.split(",").length) {
				String[] cols = columnListString.split(",");
				String[] vals = valueListString.split(",");
				for (int i = 0; i < cols.length; i++) {
					if (cols[i] != null && vals[i] != null) {
						String column = cols[i].trim();
						String value = vals[i].trim();
						if (column.length() > 0 && value.length() > 0) {
							columnValueMap.put(column, value);
						} else {
							throw new Exception("IllegalSyntaxError: Column List or Value List Syntax Error. ");
						}
					} else {
						throw new Exception("IllegalSyntaxError: Column List or Value List Syntax Error. ");
					}
				}
			} else {
				throw new Exception("IllegalSyntaxError: Insuffient Column or Value. ");
			}
			magentaDavisBaseService.insertIntoTable(tableName, columnValueMap);
		} else {
			throw new Exception("IllegalSyntaxError: " + "I didn't understand the command: \"" + userCommand + "\"");
		}
	}
	
	private void parseDeleteCommand(String userCommand, List<String> commandTokens) throws Exception{
		if (commandTokens.size() == 6 && commandTokens.get(1).equals("from") && commandTokens.get(2).equals("table") && commandTokens.get(4).equals("where")) {
			String tableName = commandTokens.get(3);
			String condition = commandTokens.get(5);
			magentaDavisBaseService.deleteFromTable(tableName, condition);
		} else {
			throw new Exception("IllegalSyntaxError: " + "I didn't understand the command: \"" + userCommand + "\"");
		}
	}
	
	private void parseUpdateCommand(String userCommand, List<String> commandTokens) throws Exception{
		if (commandTokens.size() >= 7 && commandTokens.get(1).equals("table") && commandTokens.get(3).equals("set") && commandTokens.get(commandTokens.size() - 2).equals("where")) {
			String tableName = commandTokens.get(2);
			String condition = commandTokens.get(commandTokens.size() - 1);
			String equalCondition = "";
			String columnName = null;
			String value = null;
			for (int i = 4; i <= commandTokens.size() - 3; i++) {
				equalCondition = equalCondition + commandTokens.get(i);
			}
			if (equalCondition.split("=").length == 2) {
				columnName = equalCondition.split("=")[0].trim();
				value = equalCondition.split("=")[1].trim();
			} else {
				throw new Exception("IllegalSyntaxError: Equal Condition Syntax Error. ");
			}
			magentaDavisBaseService.updateTable(tableName, columnName, value, condition);
		} else {
			throw new Exception("IllegalSyntaxError: " + "I didn't understand the command: \"" + userCommand + "\"");
		}
	}
	
	private void parseQueryCommand(String userCommand, List<String> commandTokens) throws Exception{
		if (commandTokens.size() >= 6) {
			String condition = commandTokens.get(commandTokens.size() - 1);
			boolean isNot = commandTokens.get(commandTokens.size() - 2).equals("not");
			int selectIndex = userCommand.indexOf("select");
			int fromIndex = userCommand.indexOf("from");
			int whereIndex = userCommand.indexOf("where");
			String tableName = userCommand.substring(fromIndex + 4, whereIndex).trim();
			String columnListString = userCommand.substring(selectIndex + 6, fromIndex).trim();
			List<String> columnList = new ArrayList<>();
			for (String col : columnListString.split(",")) {
				if (col != null) {
					col = col.trim();
					if (col.length() > 0) {
						columnList.add(col);
					}
				}
			}
			magentaDavisBaseService.executeQuery(columnList, tableName, condition, isNot);
		} else {
			throw new Exception("IllegalSyntaxError: " + "I didn't understand the command: \"" + userCommand + "\"");
		}
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
	  public static void parseQueryString(String queryString) {
			System.out.println("STUB: Calling the method to process the command");
			System.out.println("Parsing the string:\"" + queryString + "\"");
			
			String[] cmp;
			String[] column;
			String[] temp = queryString.split("where");
			if(temp.length > 1){
				String tmp = temp[1].trim();
				cmp = parserEquation(tmp);
			}
			else{
				cmp = new String[0];
			}
			String[] select = temp[0].split("from");
			String tableName = select[1].trim();
			String cols = select[0].replace("select", "").trim();
			if(cols.contains("*")){
				column = new String[1];
				column[0] = "*";
			}
			else{
				column = cols.split(",");
				for(int i = 0; i < column.length; i++)
					column[i] = column[i].trim();
			}
			
			if(!tableExists(tableName)){
				System.out.println("Table "+tableName+" does not exist.");
			}
			else
			{
			    com.magenta.tablefile.Page.select(tableName, column, cmp);
			}
		}
}
