package com.magenta.tablefile;

public class Header {
	byte treeType;
	byte noOfCells;
	byte Start;
	byte reference;// Leaf: Pointer to sibling. Interior: Pointer to rightmost child. May need to change type
	//Define Number of columns and datatypes in the header?
	byte []locations;
	
	 public void Header(){
		
	}
	 public void getLocations() {
		 
	 }
	 public void getType() {
		 
	 }
	

}
