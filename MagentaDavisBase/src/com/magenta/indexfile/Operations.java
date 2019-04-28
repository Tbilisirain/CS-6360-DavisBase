package com.magenta.indexfile;

import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Operations {
	Page p;
	public static final int pageSize = 512;
	public static final String datePattern = "yyyy-MM-dd_HH:mm:ss";
	 
	
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
	public static void sortCellArray(RandomAccessFile file,int page) {
		byte num = getCellNumber(file,page);
		int[] keyArray = getKeyArray(file,page);
		short[] cellArray = getCellArray(file,page);
		int ltmp;
		short rtmp;
		for(int i = 1;i<num;i++) {
			for(int j = i;j>0;j--) {
				if(keyArray[j]<keyArray[j-1]) {
					ltmp = keyArray[j];
					keyArray[j] = keyArray[j-1];
					keyArray[j-1] = ltmp;
					rtmp = cellArray[j];
					cellArray[j] = cellArray[j-1];
					cellArray[j-1] = rtmp;
				}
			}
		}
		try {
			file.seek((page-1)*pageSize+12);
			for(int i = 0;i<num;i++) {
				file.writeShort(cellArray[i]);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	private static short[] getCellArray(RandomAccessFile file, int page) {
		int num = new Integer(getCellNumber(file,page));
		short [] array = new short[num];
		try {
			file.seek((page-1)*pageSize+12);
			for(int i = 0;i<num;i++) {
				array[i] = file.readShort();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		// TODO Auto-generated method stub
		return array;
	}
	private static int[] getKeyArray(RandomAccessFile file, int page) {
		int num = new Integer(getCellNumber(file,page));
		int [] array = new int[num];
		try {
			file.seek((page-1)*pageSize);
			byte pageType = file.readByte();
			byte offset = 0;
			switch(pageType) {
			case 0x0D:
				offset = 2;
				break;
			case 0x05:
				offset = 4;
				break;
				default:
					offset = 2;
					break;
			}
			for(int i = 0;i<num;i++) {
				long loc = getCellLocation(file,page,i);
				file.seek(loc+offset);
				array[i] = file.readInt();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return array;
	}
	public static void insertLeafCell(RandomAccessFile file,int page, int offset,short plsize,int key, byte[] stc,String [] vals) {
		try {
			String s;
			file.seek((page-1)*pageSize+offset);
			file.writeShort(plsize);
			file.writeInt(key);
			int col = vals.length-1;
			file.writeByte(col);
			file.write(stc);
			for(int i = 1;i<vals.length;i++) {
				switch(stc[i-1]) {
				case 0x00:
					file.writeByte(0);
					break;
				case 0x01:
					file.writeShort(0);
					break;
				case 0x02:
					file.writeInt(0);
					break;
				case 0x03:
					file.writeLong(0);
					break;
				case 0x04:
					file.write(new Byte(vals[i]));
					break;
				case 0x05:
					file.writeShort(new Short(vals[i]));
					break;
				case 0x06:
					file.writeInt(new Integer(vals[i]));
					break;
				case 0x07:
					file.writeLong(new Long(vals[i]));
					break;
				case 0x08:
					file.writeFloat(new Float(vals[i]));
					break;
				case 0x09:
					file.writeDouble(new Double(vals[i]));
					break;
				case 0x0A:
					s = vals[i];
					Date temp = new SimpleDateFormat(datePattern).parse(s.substring(1, s.length()-1));
					long time = temp.getTime();
					file.writeLong(time);
					break;
				case 0x0B:
					s = vals[i];
					s = s.substring(1,s.length()-1);
					s = s+"_00:00:00";
					Date temp2 = new SimpleDateFormat(datePattern).parse(s);
					long time2 = temp2.getTime();
					file.writeLong(time2);
					break;
					default:
						file.writeBytes(vals[i]);
						break;
					
				}
			}
			int n = getCellNumber(file,page);
			byte tmp = (byte) (n+1);
			setCellNumber(file,page,tmp);
			file.seek((page-1)*pageSize+12+n*2);
			file.writeShort(offset);
			file.seek((page-1)*pageSize+2);
			int content = file.readShort();
			if(content>=offset||content==0) {
				file.seek((page-1)*pageSize+2);
				file.writeShort(offset);
				
			}
					
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	public static void updateLeafCell(RandomAccessFile file,int page, int offset,short plsize,int key, byte[] stc,String [] vals) {
		try {
			String s;
			file.seek((page-1)*pageSize+offset);
			file.writeShort(plsize);
			file.writeInt(key);
			int col = vals.length-1;
			file.writeByte(col);
			file.write(stc);
			for(int i = 1;i<vals.length;i++) {
				switch(stc[i-1]) {
				case 0x00:
					file.writeByte(0);
					break;
				case 0x01:
					file.writeShort(0);
					break;
				case 0x02:
					file.writeInt(0);
					break;
				case 0x03:
					file.writeLong(0);
					break;
				case 0x04:
					file.write(new Byte(vals[i]));
					break;
				case 0x05:
					file.writeShort(new Short(vals[i]));
					break;
				case 0x06:
					file.writeInt(new Integer(vals[i]));
					break;
				case 0x07:
					file.writeLong(new Long(vals[i]));
					break;
				case 0x08:
					file.writeFloat(new Float(vals[i]));
					break;
				case 0x09:
					file.writeDouble(new Double(vals[i]));
					break;
				case 0x0A:
					s = vals[i];
					Date temp = new SimpleDateFormat(datePattern).parse(s.substring(1, s.length()-1));
					long time = temp.getTime();
					file.writeLong(time);
					break;
				case 0x0B:
					s = vals[i];
					s = s.substring(1,s.length()-1);
					s = s+"_00:00:00";
					Date temp2 = new SimpleDateFormat(datePattern).parse(s);
					long time2 = temp2.getTime();
					file.writeLong(time2);
					break;
					default:
						file.writeBytes(vals[i]);
						break;
					
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}



	// Additional methods

}
