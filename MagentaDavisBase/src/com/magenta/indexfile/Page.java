package com.magenta.indexfile;
import com.magenta.tablefile.*;

import java.io.IOException;
import java.io.RandomAccessFile;
/*
This file will have followtng methods:
createPage(), params : File
createLeafPage(), params: File

*/
public class Page {
	
	public static int pageSize = 512;
	public void Page() {
		
	}
	public void getIndex() {
		
	}
	public void getRowIds() {
		
	}

	public static int createPage(RandomAccessFile file) {
		int pages = 0;
	
			try {
				pages = (int)(file.length()/(new Long(pageSize)));
				pages = pages + 1;
				file.setLength(pageSize * pages);
				file.seek((pages-1) * pageSize);
				file.writeByte(0x05);  
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		
		return pages;
	}

	public static int createLeafPage(RandomAccessFile file) {
		int pages = 0;
	
			try {
				pages = (int)(file.length()/(new Long(pageSize)));
				pages = pages + 1;
				file.setLength(pageSize * pages);
				file.seek((pages-1) * pageSize);
				file.writeByte(0x0D);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

		return pages;
	}
	public static void createIndex(String tableName,String colName,String dType) {
		int serialCode = 0;
		int recordSize = 0;
		if(dType.equalsIgnoreCase("int")) {
			recordSize = recordSize+4;
			serialCode = 0x06;
		}
		else if(dType.equalsIgnoreCase("tinyint")) {
			recordSize = recordSize+1;
			serialCode = 0x04;
		}
		else if(dType.equalsIgnoreCase("bigint")) {
			recordSize = recordSize+8;
			serialCode = 0x07;
		}
		else if(dType.equalsIgnoreCase("smallint")) {
			recordSize = recordSize+2;
			serialCode = 0x05;
		}
		else if(dType.equalsIgnoreCase("real")){
			recordSize = recordSize+4;
			serialCode = 0x08;
		}
		else if(dType.equalsIgnoreCase("double")) {
			recordSize = recordSize+8;
			serialCode = 0x09;
		}
		else if(dType.equalsIgnoreCase("datetime")) {
			recordSize = recordSize+8;
			serialCode = 0x0A;
		}
		else if(dType.equalsIgnoreCase("date")) {
			recordSize = recordSize+8;
			serialCode = 0x0B;
		}
		else if(dType.equalsIgnoreCase("text")) {
			serialCode = 0x0C;
		}
		int ordinalPos = 0;
		try {
			RandomAccessFile column = new RandomAccessFile("data/"+colName+".ndx","rw");
			column.setLength(pageSize);
			column.seek(0);
			column.writeByte(serialCode);
			int nullVal = 1;
			column.writeByte(nullVal);
			column.writeByte(ordinalPos);
			ordinalPos++;
			column.writeByte(0x00);
			column.writeByte(0x10);
			column.close();
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}



}
