package com.magenta.tablefile;

import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

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
		public static void setCellNumber(RandomAccessFile file, int page, byte num) {
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
		public static short[] getCellArray(RandomAccessFile file, int page) {
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
		public static int[] getKeyArray(RandomAccessFile file, int page) {
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
		public static boolean checkInteriorSpace(RandomAccessFile file, int page) {
			byte numCells = getCellNumber(file,page);
			if(numCells>30) {
				return true;
			}
			else {
				return false;
			}
		}
		public static int checkLeafSpace(RandomAccessFile file,int page,int size) {
			int val = -1;
			try {
				file.seek((page-1)*pageSize+2);
				int content = file.readShort();
				if(content==0) {
					return pageSize-size;
				}
				int numCells = getCellNumber(file,page);
				int space = content-20-2*numCells;
				if(size<space) {
					return content-size;
				}
				
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			return val;
		}
		public static int getParent(RandomAccessFile file , int page) {
			int val = 0;
			try {
				file.seek((page-1)*pageSize+8);
				val = file.readInt();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			return val;
		}
		public static void setParent(RandomAccessFile file,int page, int parent) {
			try {
				file.seek((page-1)*pageSize+8);
				file.writeInt(parent);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		public static int getRightMost(RandomAccessFile file,int page) {
			int rl =0;
			try {
				file.seek((page-1)*pageSize+4);
				rl = file.readInt();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			return rl;
		}
		public static void setRightMost(RandomAccessFile file, int page, int rightLeaf) {
			try {
				file.seek((page-1)*pageSize+4);
				file.writeInt(rightLeaf);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		public static boolean hasKey(RandomAccessFile file,int page, int key) {
			int [] keys = getKeyArray(file,page);
			for(int i:keys) {
				if(key==i) {
					return true;
				}
			}
			return false;
		}
		public static short getCellOffset(RandomAccessFile file, int page,int id) {
			short offset = 0;
			try {
				file.seek((page-1)*pageSize+12+id*2);
				offset = file.readShort();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			return offset;
		}
		public static long getPointerLoc(RandomAccessFile file, int page, int parent) {
			long val = 0;
			try {
				int numCells = new Integer(getCellNumber(file,parent));
				for(int i = 0;i<numCells;i++) {
					long loc = getCellLocation(file,parent,i);
					file.seek(loc);
					int childPage = file.readInt();
					if(childPage==page) {
						val = loc;
					}
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			return val;
		}
		public static void setPointerLoc(RandomAccessFile file,long loc,int parent, int page) {
			try {
				if(loc==0) {
					file.seek((parent-1)*pageSize+4);
					
				}
				else {
					file.seek(loc);
				}
				file.writeInt(page);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		public static void splitLeafPage(RandomAccessFile file,int curPage,int newPage) {
			try {
				int numCells = getCellNumber(file,curPage);
				int mid = (int) Math.ceil(numCells/2);
				int numCellA =mid - 1;
				int numCellB = numCells-numCellA;
				int content = 512;
				for(int i = numCellA;i<numCells;i++) {
					long loc = getCellLocation(file,curPage,i);
					file.seek(loc);
					int cellSize = file.readShort()+6;
					content = content-cellSize;
					file.seek(loc);
					byte[] cell = new byte[cellSize];
					file.read(cell);
					file.seek((newPage-1)*pageSize+content);
					file.write(cell);
					setCellOffset(file,newPage,i-numCellA,content);
				}
				file.seek((newPage-1)*pageSize+2);
				file.writeShort(content);
				short offset = getCellOffset(file,curPage,numCellA-1);
				file.seek((curPage-1)*pageSize+2);
				file.writeShort(offset);
				int rightMost = getRightMost(file,curPage);
				setRightMost(file,newPage,rightMost);
				setRightMost(file,curPage,newPage);
				int parent = getParent(file,curPage);
				setParent(file,newPage,parent);
				byte num = (byte) numCellA;
				setCellNumber(file,curPage,num);
				 num = (byte) numCellB;
				setCellNumber(file,newPage,num);
				
				
				
				
				
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		public static void splitInteriorPage(RandomAccessFile file,int curPage, int newPage) {
			try {
	int numCells = getCellNumber(file, curPage);
				
				int mid = (int) Math.ceil((double) numCells / 2);

				int numCellA = mid - 1;
				int numCellB = numCells - numCellA - 1;
				short content = 512;

				for(int i = numCellA+1; i < numCells; i++){
					long loc = getCellLocation(file, curPage, i);
					short cellSize = 8;
					content = (short)(content - cellSize);
					file.seek(loc);
					byte[] cell = new byte[cellSize];
					file.read(cell);
					file.seek((newPage-1)*pageSize+content);
					file.write(cell);
					file.seek(loc);
					int page = file.readInt();
					setParent(file, page, newPage);
					setCellOffset(file, newPage, i - (numCellA + 1), content);
				}
				
				int tmp = getRightMost(file, curPage);
				setRightMost(file, newPage, tmp);
				
				long midLoc = getCellLocation(file, curPage, mid - 1);
				file.seek(midLoc);
				tmp = file.readInt();
				setRightMost(file, curPage, tmp);
				
				file.seek((newPage-1)*pageSize+2);
				file.writeShort(content);
				
				short offset = getCellOffset(file, curPage, numCellA-1);
				file.seek((curPage-1)*pageSize+2);
				file.writeShort(offset);

				
				int parent = getParent(file, curPage);
				setParent(file, newPage, parent);
				
				byte num = (byte) numCellA;
				setCellNumber(file, curPage, num);
				num = (byte) numCellB;
				setCellNumber(file, newPage, num);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		public static void splitLeaf(RandomAccessFile file,int page) {
			int newPage = createNewLeafPage(file);
			int midKey = findMiddleKey(file,page);
			splitLeafPage(file,page,newPage);
			int parent = getParent(file,page);
			if(parent==0) {
				int rootPage = createInteriorPage(file);
				setParent(file,page,rootPage);
				setParent(file,newPage,rootPage);
				setRightMost(file,rootPage,newPage);
				insertInteriorCell(file,rootPage,page,midKey);
				
			}
			else {
				long ploc = getPointerLoc(file,page,parent);
				setPointerLoc(file,ploc,parent,newPage);
				insertInteriorCell(file,parent,page,midKey);
				sortCellArray(file,parent);
				while(checkInteriorSpace(file,parent)) {
					parent = splitInterior(file,parent);
				}
			}
		}
		public static int splitInterior(RandomAccessFile file, int page) {
			int newPage = createInteriorPage(file);
			int midKey = findMiddleKey(file,page);
			splitInteriorPage(file,page,newPage);
			int parent = getParent(file,page);
			if(parent == 0){
				int rootPage = createInteriorPage(file);
				setParent(file, page, rootPage);
				setParent(file, newPage, rootPage);
				setRightMost(file, rootPage, newPage);
				insertInteriorCell(file, rootPage, page, midKey);
				return rootPage;
			}else{
				long ploc = getPointerLoc(file, page, parent);
				setPointerLoc(file, ploc, parent, newPage);
				insertInteriorCell(file, parent, page, midKey);
				sortCellArray(file, parent);
				return parent;
			}
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
		public static String[] getColName(String table){ //tables=davisbase_tables
			String[] cols = new String[0];
			try{
				RandomAccessFile file = new RandomAccessFile("data/davisbase_columns.tbl", "rw");
				Buffer buffer = new Buffer();
				String[] columnName = {"rowid", "table_name", "column_name", "data_type", "ordinal_position", "is_nullable"};
				String[] cmp = {"table_name","=",table};
				filter(file, cmp, columnName, buffer);
				HashMap<Integer, String[]> content = buffer.content;
				ArrayList<String> array = new ArrayList<String>();
				for(String[] i : content.values()){
					array.add(i[2]);
				}
				int size=array.size();
				cols = array.toArray(new String[size]);
				file.close();
				return cols;
			}catch(Exception e){
				System.out.println(e);
			}
			return cols;
		}
		
		public static String[] getDataType(String table){
			String[] dataType = new String[0];
			try{
				RandomAccessFile file = new RandomAccessFile("data/davisbase_columns.tbl", "rw");
				Buffer buffer = new Buffer();
				String[] columnName = {"rowid", "table_name", "column_name", "data_type", "ordinal_position", "is_nullable"};
				String[] cmp = {"table_name","=",table};
				filter(file, cmp, columnName, buffer);
				HashMap<Integer, String[]> content = buffer.content;
				ArrayList<String> array = new ArrayList<String>();
				for(String[] x : content.values()){
					array.add(x[3]);
				}
				int size=array.size();
				dataType = array.toArray(new String[size]);
				file.close();
				return dataType;
			}catch(Exception e){
				System.out.println(e);
			}
			return dataType;
		}

		
		public static void filter(RandomAccessFile file, String[] cmp, String[] columnName, Buffer buffer){
			try{
				
				int numOfPages = pages(file);
				for(int page = 1; page <= numOfPages; page++){
					
					file.seek((page-1)*pageSize);
					byte pageType = file.readByte();
					if(pageType == 0x0D)
					{
						byte numOfCells = getCellNumber(file, page); //accesses file header to get number of cells.

						for(int i=0; i < numOfCells; i++){
							
							long loc = getCellLocation(file, page, i);	
							String[] vals = retrieveValues(file, loc);
							int rowid=Integer.parseInt(vals[0]);

							boolean check = cmpCheck(vals, rowid, cmp, columnName);
							
							if(check)
								buffer.add_vals(rowid, vals);
						}
					}
					else
						continue;
				}

				buffer.columnName = columnName;
				buffer.format = new int[columnName.length];

			}catch(Exception e){
				System.out.println("Error at filter");
				e.printStackTrace();
			}

		}

			public static String[] retrieveValues(RandomAccessFile file, long loc){
			
			String[] values = null;
			try{
				
				SimpleDateFormat dateFormat = new SimpleDateFormat (datePattern);

				file.seek(loc+2);
				int key = file.readInt();
				int num_cols = file.readByte();
				
				byte[] stc = new byte[num_cols];
				file.read(stc);
				
				values = new String[num_cols+1];
				
				values[0] = Integer.toString(key);
				
				for(int i=1; i <= num_cols; i++){
					switch(stc[i-1]){
						case 0x00:  file.readByte();
						            values[i] = "null";
									break;

						case 0x01:  file.readShort();
						            values[i] = "null";
									break;

						case 0x02:  file.readInt();
						            values[i] = "null";
									break;

						case 0x03:  file.readLong();
						            values[i] = "null";
									break;

						case 0x04:  values[i] = Integer.toString(file.readByte());
									break;

						case 0x05:  values[i] = Integer.toString(file.readShort());
									break;

						case 0x06:  values[i] = Integer.toString(file.readInt());
									break;

						case 0x07:  values[i] = Long.toString(file.readLong());
									break;

						case 0x08:  values[i] = String.valueOf(file.readFloat());
									break;

						case 0x09:  values[i] = String.valueOf(file.readDouble());
									break;

						case 0x0A:  Long temp = file.readLong();
									Date dateTime = new Date(temp);
									values[i] = dateFormat.format(dateTime);
									break;

						case 0x0B:  temp = file.readLong();
									Date date = new Date(temp);
									values[i] = dateFormat.format(date).substring(0,10);
									break;

						default:    int len = new Integer(stc[i-1]-0x0C);
									byte[] bytes = new byte[len];
									file.read(bytes);
									values[i] = new String(bytes);
									break;
					}
				}

			}catch(Exception e){
				System.out.println(e);
			}

			return values;
		}
			
		public static int calPayloadSize(String table, String[] vals, byte[] stc){
			String[] dataType = getDataType(table);
			int size =dataType.length;
			for(int i = 1; i < dataType.length; i++){
				stc[i - 1]= getStc(vals[i], dataType[i]);
				size = size + feildLength(stc[i - 1]);
			}
			return size;
		}
		
		public static byte getStc(String value, String dataType){
			if(value.equals("null")){
				switch(dataType){
					case "TINYINT":     return 0x00;
					case "SMALLINT":    return 0x01;
					case "INT":			return 0x02;
					case "BIGINT":      return 0x03;
					case "REAL":        return 0x02;
					case "DOUBLE":      return 0x03;
					case "DATETIME":    return 0x03;
					case "DATE":        return 0x03;
					case "TEXT":        return 0x03;
					default:			return 0x00;
				}							
			}else{
				switch(dataType){
					case "TINYINT":     return 0x04;
					case "SMALLINT":    return 0x05;
					case "INT":			return 0x06;
					case "BIGINT":      return 0x07;
					case "REAL":        return 0x08;
					case "DOUBLE":      return 0x09;
					case "DATETIME":    return 0x0A;
					case "DATE":        return 0x0B;
					case "TEXT":        return (byte)(value.length()+0x0C);
					default:			return 0x00;
				}
			}
		}
		
	    public static short feildLength(byte stc){
			switch(stc){
				case 0x00: return 1;
				case 0x01: return 2;
				case 0x02: return 4;
				case 0x03: return 8;
				case 0x04: return 1;
				case 0x05: return 2;
				case 0x06: return 4;
				case 0x07: return 8;
				case 0x08: return 4;
				case 0x09: return 8;
				case 0x0A: return 8;
				case 0x0B: return 8;
				default:   return (short)(stc - 0x0C);
			}
		}


		
	public static int searchKeyPage(RandomAccessFile file, int key){
			int val = 1;
			try{
				int numPages = pages(file);
				for(int page = 1; page <= numPages; page++){
					file.seek((page - 1)*pageSize);
					byte pageType = file.readByte();
					if(pageType == 0x0D){
						int[] keys = getKeyArray(file, page);
						if(keys.length == 0)
							return 0;
						int rm = getRightMost(file, page);
						if(keys[0] <= key && key <= keys[keys.length - 1]){
							return page;
						}else if(rm == 0 && keys[keys.length - 1] < key){
							return page;
						}
					}
				}
			}catch(Exception e){
				System.out.println(e);
			}

			return val;
		}

		
		public static String[] getNullable(String table){
			String[] nullable = new String[0];
			try{
				RandomAccessFile file = new RandomAccessFile("data/davisbase_columns.tbl", "rw");
				Buffer buffer = new Buffer();
				String[] columnName = {"rowid", "table_name", "column_name", "data_type", "ordinal_position", "is_nullable"};
				String[] cmp = {"table_name","=",table};
				filter(file, cmp, columnName, buffer);
				HashMap<Integer, String[]> content = buffer.content;
				ArrayList<String> array = new ArrayList<String>();
				for(String[] i : content.values()){
					array.add(i[5]);
				}
				int size=array.size();
				nullable = array.toArray(new String[size]);
				file.close();
				return nullable;
			}catch(Exception e){
				System.out.println(e);
			}
			return nullable;
		}


		public static void filter(RandomAccessFile file, String[] cmp, String[] columnName, String[] type, Buffer buffer){
			try{
				
				int numOfPages = pages(file);
				
				for(int page = 1; page <= numOfPages; page++){
					
					file.seek((page-1)*pageSize);
					byte pageType = file.readByte();
					
						if(pageType == 0x0D){
							
						byte numOfCells = getCellNumber(file, page);

						 for(int i=0; i < numOfCells; i++){
							long loc = getCellLocation(file, page, i);
							String[] vals = retrieveValues(file, loc);
							int rowid=Integer.parseInt(vals[0]);
							
							for(int j=0; j < type.length; j++)
								if(type[j].equals("DATE") || type[j].equals("DATETIME"))
									vals[j] = "'"+vals[j]+"'";
							
							boolean check = cmpCheck(vals, rowid , cmp, columnName);

							
							for(int j=0; j < type.length; j++)
								if(type[j].equals("DATE") || type[j].equals("DATETIME"))
									vals[j] = vals[j].substring(1, vals[j].length()-1);

							if(check)
								buffer.add_vals(rowid, vals);
						 }
					   }
					    else
							continue;
				}

				buffer.columnName = columnName;
				buffer.format = new int[columnName.length];

			}catch(Exception e){
				System.out.println("Error at filter");
				e.printStackTrace();
			}

		}

		
		public static boolean cmpCheck(String[] values, int rowid, String[] cmp, String[] columnName){

			boolean check = false;
			
			if(cmp.length == 0){
				check = true;
			}
			else{
				int colPos = 1;
				for(int i = 0; i < columnName.length; i++){
					if(columnName[i].equals(cmp[0])){
						colPos = i + 1;
						break;
					}
				}
				
				if(colPos == 1){
					int val = Integer.parseInt(cmp[2]);
					String operator = cmp[1];
					switch(operator){
						case "=": if(rowid == val) 
									check = true;
								  else
								  	check = false;
								  break;
						case ">": if(rowid > val) 
									check = true;
								  else
								  	check = false;
								  break;
						case ">=": if(rowid >= val) 
							        check = true;
						          else
						  	        check = false;	
						          break;
						case "<": if(rowid < val) 
									check = true;
								  else
								  	check = false;
								  break;
						case "<=": if(rowid <= val) 
									check = true;
								  else
								  	check = false;	
								  break;
						case "!=": if(rowid != val)  
									check = true;
								  else
								  	check = false;	
								  break;						  							  							  							
					}
				}else{
					if(cmp[2].equals(values[colPos-1]))
						check = true;
					else
						check = false;
				}
			}
			return check;
		}
		
			



}
