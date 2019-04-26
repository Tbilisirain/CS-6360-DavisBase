package com.magenta.indexfile;

import java.io.RandomAccessFile;

public class Operations {
	Page p;
	public static final int pageSize = 512;
	 
	public void addIndex() {
		
	}
	public static int createNewLeafPage(RandomAccessFile file) {
		 int num_pages = 0;
		 try {
			 num_pages = (int)(file.length()/new Long(pageSize));
			 num_pages = num_pages+1;
			 file.setLength(pageSize*num_pages);
			 file.seek((num_pages-1)*pageSize);
			 file.writeByte(0x0D);
		 }
		 catch(Exception e) {
			 e.printStackTrace();
		 }
		 return num_pages;
		 
		 // Initialize and if one page fills up, call this method
	 }
	public static int createInteriorPage(RandomAccessFile file) {
		int num_pages = 0;
		try {
			num_pages = (int)(file.length()/new Long(pageSize));
			 num_pages = num_pages+1;
			 file.setLength(pageSize*num_pages);
			 file.seek((num_pages-1)*pageSize);
			 file.writeByte(0x05);
			
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return num_pages;
	}
	public static byte getPageType(RandomAccessFile file,int page) {
		byte type = 0x05;
		try {
			file.seek((page-1)*pageSize);
			type = file.readByte();
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return type;
	}

	// Additional methods

}
