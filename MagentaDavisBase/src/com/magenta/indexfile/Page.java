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
	Header h;
	byte noOfRowIds;
	byte indexType;
	byte indexValue;
	byte[] rowids;
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



}
