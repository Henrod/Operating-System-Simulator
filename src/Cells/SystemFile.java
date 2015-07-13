package Cells;

import LinkedLists.QueueLinkedList;

/* This class represents the files in the disk */
public class SystemFile {
	public int ownerID; 	//ID of the job that owns this file
	public String name;	//name of the file
	public int fileID;
	public int size;	
	public boolean public_file;	/* true if the file is available to every user 
							*  false if only available for the owner */
	
	public SystemFile next_file;		//next file in the list
	public QueueLinkedList fileQueue;	//queue that contains jobs wanting to access this file
	public boolean busy;	//true if the file is being read by a job, false otherwise
	
	public SystemFile(int ownerID, String name, int fileID, int size, boolean public_file){
		this.ownerID = ownerID;
		this.name = name;
		this.fileID = fileID;
		this.size = size;
		this.public_file = public_file;
		this.next_file = null;
		fileQueue = new QueueLinkedList(1000, 5);
		this.busy = false;
	}
	
	public void read_file(){
		this.busy = true;
	}
	public void release_file(){
		this.busy = false;
	}
}
