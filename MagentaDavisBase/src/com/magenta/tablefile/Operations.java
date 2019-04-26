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

}
