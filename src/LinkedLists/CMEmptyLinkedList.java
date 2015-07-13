package LinkedLists;

import Cells.emptyCM;

public class CMEmptyLinkedList {
	emptyCM firstLink; //head of the list
	
	public emptyCM getFirstEmptyCM(){
		return firstLink;
	}
	
	/* Constructor of the list 
	 * initializes with a emptyCM with the size of the CM*/
	public CMEmptyLinkedList(int size){
		firstLink = new emptyCM(0, size);
		firstLink.nextEmptyCM = null;
	}
	
	/* Verifies if the list is empty */
	public boolean isEmpty(){
		return firstLink == null;
	}
	
	/* insert emptyCM as a firstLink in the list */
	public void insertLink(emptyCM newEmptyCM){
		newEmptyCM.nextEmptyCM = firstLink;
		firstLink = newEmptyCM;
	}
	
	public emptyCM removeEmptyCM(emptyCM job){
		emptyCM current = firstLink;
		emptyCM previous = firstLink;
		
		while(current != job){
			if(current.nextEmptyCM == null){
				return null;
			} else {
				previous = current;
				current = current.nextEmptyCM;
			}
		}
		
		if(current == firstLink){
			firstLink = firstLink.nextEmptyCM;
		} else {
			previous.nextEmptyCM = current.nextEmptyCM;
		}
		
		return current;
	}
	
	/* Search for a empty space large enough to allocate job */
	public emptyCM verifyEmptySpace(int space){
		emptyCM current = firstLink;
		int empty_space;
		while(current != null){
			empty_space = current.get_final_address() - current.get_initial_address() + 1;
			
			if(empty_space >= space)
				return current;
			
			current = current.nextEmptyCM;
		}
		
		return null;
	}
	
	/* This method frees space when a job is removed form the CM */
	public void restoreEmptySpace(emptyCM restoredSpace){
		/* if the space can be merged with other already in the list, do it */
		if(mergeEmptySpaces(restoredSpace)) 
			return;
		else{
			/* if none exist, creates a new cell of empty */
			emptyCM newEmpty = new emptyCM(restoredSpace.get_initial_address(), restoredSpace.get_final_address());
			insertLink(newEmpty);
		}
	}
	/* check is there is empty spaces in sequence which can be merged into one or more */
	boolean mergeEmptySpaces(emptyCM restoredSpace){
		emptyCM current = firstLink;
		
		while(current != null){
			/* search for empty spaces which final address is one before initial address of restored space */
			if(current.get_final_address() == restoredSpace.get_initial_address() - 1){
				current.set_final_address(restoredSpace.get_final_address()); //then merge empty spaces
				/* continue the process with the new empty space created */
				if(mergeEmptySpaces(current)) //if it is possible to merge this with another cell, remove this cell and keep only the merged one
					removeEmptyCM(current);
				return true;
			} 
			/* search for empty spaces which initial address is one after final address of restored space */
			if(current.get_initial_address() == restoredSpace.get_final_address() + 1){
				current.set_initial_address(restoredSpace.get_initial_address()); //then merge empty spaces
				/* continue the process with the new empty space created */
				if(mergeEmptySpaces(current)) //if it is possible to merge this with another cell, remove this cell and keep only the merged one
					removeEmptyCM(current);
				return true;
			}		
			current = current.nextEmptyCM;
		}
		return false;
	}
	
}
