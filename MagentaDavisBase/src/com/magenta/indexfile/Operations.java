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
	public static int findMiddleKey(RandomAccessFile file, int page) {
		int val = 0;
		try {
			file.seek((page-1)*pageSize);
			byte pageType = file.readByte();
			int numCells = getCellNumber(file,page);
			int mid = (int)Math.ceil((double)numCells/2);
			long loc = getCellLocation(file,page,mid-1);
			file.seek(loc);
			switch(pageType) {
			case 0x05:
				file.readInt();
				val = file.readInt();
				break;
			case 0x0D:
				file.readShort();
				val = file.readShort();
			}
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return val;
	}
	public static byte getCellNumber(RandomAccessFile file,int page) {
		byte val = 0;
		try {
			file.seek((page-1)*pageSize+1);
			val = file.readByte();
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return val;
	}
	public static long getCellLocation(RandomAccessFile file, int page, int id) {
		long loc = 0;
		try {
			file.seek((page-1)*pageSize+12+id*2);
			short offset = file.readShort();
			long orig = (page-1)*pageSize;
			loc = orig+offset;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return loc;
	}

	// Additional methods

}
