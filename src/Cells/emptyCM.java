package Cells;

/* This class represents a empty spaces in the central memory */
public class emptyCM {
	int initial_address;	//initial address of empty space in central memory
	int final_address;		//final address of empty space in central memory
	
	public emptyCM nextEmptyCM;
	
	public emptyCM(int initial_address, int final_address){
		this.initial_address = initial_address;
		this.final_address = final_address;
	}
	
	public int get_initial_address(){
		return initial_address;
	}
	public void set_initial_address(int initial_address){
		this.initial_address = initial_address;
	}
	
	public int get_final_address(){
		return final_address;
	}
	public void set_final_address(int final_address){
		this.final_address = final_address;
	}
}
