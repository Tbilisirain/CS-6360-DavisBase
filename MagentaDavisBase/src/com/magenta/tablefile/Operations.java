package com.magenta.tablefile;

import java.io.RandomAccessFile;

public class Operations {
	Page p;
	public static final int pageSize = 512;
	 public void addData() {
		 // Data being added to the page
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
	 // Parent page operations?
	 public void getHeader() {
			
		}
		public void getData() { // Relevant record
			
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
		public static short findPayloadSize(String [] values, String[] datatype) {
			int val = datatype.length;
			for(int i = 1;i<datatype.length;i++) {
				String dt = datatype[i];
				switch(dt) {
				case "TINYINT":
					val = val+1;
					break;
				case "SMALLINT":
					val = val+2;
					break;
				case "INT":
					val = val+4;
					break;
				case "BIGINT":
					val = val+8;
					break;
				case "REAL":
					val = val+4;
					break;
				case "DOUBLE":
					val = val+8;
					break;
				case "DATETIME":
					val = val+8;
					break;
				case "DATE":
					val = val+8;
					break;
				case "TEXT":
					String text = values[i];
					int len = text.length();
					val = val+len;
					break;
				default:
					break;
				}
			}
			return (short)val;
		}
		public static int pages(RandomAccessFile file){
			int num_pages = 0;
			try{
				num_pages = (int)(file.length()/(new Long(pageSize)));
			}catch(Exception e){
				System.out.println(e);
			}

			return num_pages;
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
		public static void insertInteriorCell(RandomAccessFile file, int page, int child, int key) {
			try {
				file.seek((page-1)*pageSize+2);
				short content = file.readShort();
				if(content==0) {
					content = 512;
				}
				content = (short) (content-8);
				file.seek((page-1)*pageSize+content);
				file.writeInt(child);
				file.writeInt(key);
				file.seek((page-1)*pageSize+2);
				file.writeShort(content);
				byte num = getCellNumber(file,page);
				setCellOffset(file,page, num, content);
				num = (byte) (num+1);
				setCellNumber(file,page,num);
				
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		private static void setCellNumber(RandomAccessFile file, int page, byte num) {
			try {
				file.seek((page-1)*pageSize+1);
				file.writeByte(num);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			// TODO Auto-generated method stub
			
		}
		public static void setCellOffset(RandomAccessFile file,int page, int id,int offset) {
			try {
				file.seek((page-1)*pageSize+12+id*2);
				file.writeShort(offset);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}


}
