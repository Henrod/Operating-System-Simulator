package LinkedLists;

import Cells.SystemFile;

/* this class has a list of all files current on the disk */
public class DiskPartitionTable {
	SystemFile first_file;
	
	public DiskPartitionTable() {
		first_file = null;
	}
	
	public void insertFile(SystemFile file){
		file.next_file = first_file;
		first_file = file;
	}
	
	public void displayFiles(){
		System.out.println("Files in the System: ");
		for(SystemFile current = first_file; current != null; current = current.next_file)
			System.out.println("	Job owner: " + current.ownerID + " - Name: " + current.name + 
					" - File ID: " + current.fileID + " - Size: " + current.size/1000000 + " MB - Public: " + current.public_file);
		System.out.println("\n");
	}
	
	//gets file from its ID
	public SystemFile searchFile(int file_number){
		for(SystemFile current = first_file; current != null; current = current.next_file)
			if(current.fileID == file_number)
				return current;
		
		return null;
	}
}
