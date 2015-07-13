package System;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.util.Random;

import Cells.Queue;
import Cells.Segment;
import Cells.emptyCM;
import Cells.eventTable;
import Cells.jobCM;
import Cells.jobTable;
import Cells.SystemFile;
import LinkedLists.EventLinkedList;
import LinkedLists.JobLinkedList;
import System.Peripherals.Disk;
import System.Peripherals.Printer;
import System.Peripherals.Reader;

public class Simulator {
	
	int TIME;	//time of the simulation, initializes with zero.
	
	CPU cpu;						//Central processing unit of the system
	CentralMemory centralMemory;	//Central memory of the system
	Disk disk;
	Reader reader;
	Printer printer;
	
	JobLinkedList jobLinkedList;	//List of jobs in the simulation
	EventLinkedList eventLinkedList;//List of events for each job in the system 
	BufferedReader jobsParameters;	//Gets parameters for all jobs from a text file
	BufferedReader systemParameters;//Gets parameters for all parts of system from a text file
	String line;					//Contains parameters for one job
	
	/* Constructor of the Simulator class */
	public Simulator() throws IOException{
		TIME = 0;	//Initializes TIME with zero
		jobsParameters = new BufferedReader(new FileReader(new File("jobTable.txt")));
		systemParameters = new BufferedReader(new FileReader(new File("machineDescription.txt")));
		
		//machine is built
		constructMachine(systemParameters);
		
		disk.diskPartitionTable.displayFiles();
		
		initializeSystem();
	}
	
	void constructMachine(BufferedReader systemParameters) throws IOException{
		
		String memory = systemParameters.readLine();
		String[] mem_split = memory.split("\\s+");
		int memory_size = Integer.parseInt(mem_split[0]);
		int memory_relocation_time = Integer.parseInt(mem_split[1]);
		centralMemory = new CentralMemory(memory_size, memory_relocation_time);	//Initializing CM with its size
		
		String cpu_params = systemParameters.readLine();
		String[] cpu_split = cpu_params.split("\\s+");
		int multiprogramming_level = Integer.parseInt(cpu_split[0]);
		int max_wait_queue = Integer.parseInt(cpu_split[1]);
		int max_lenght_queue = Integer.parseInt(cpu_split[2]);
		int overhead_time = Integer.parseInt(cpu_split[3]);
		int time_slice = Integer.parseInt(cpu_split[4]);
		cpu = new CPU(multiprogramming_level, max_wait_queue, max_lenght_queue, overhead_time, time_slice);
		
		String disk_params = systemParameters.readLine();
		String[] disk_split = disk_params.split("\\s+");
		disk = new Disk(Integer.parseInt(disk_split[0]), Integer.parseInt(disk_split[1]), Integer.parseInt(disk_split[2]));
		
		String reader_params = systemParameters.readLine();
		String[] reader_split = reader_params.split("\\s+");
		reader = new Reader(Integer.parseInt(reader_split[0]), Integer.parseInt(reader_split[1]), Integer.parseInt(reader_split[2]));
		
		String printer_params = systemParameters.readLine();
		String[] printer_split = printer_params.split("\\s+");
		printer = new Printer(Integer.parseInt(printer_split[0]), Integer.parseInt(printer_split[1]), Integer.parseInt(printer_split[2]));
		
		String disk_files = systemParameters.readLine();
		while(disk_files != null){
			String[] file_split = disk_files.split("\\s+");
			SystemFile new_file = new SystemFile(Integer.parseInt(file_split[0]), file_split[1], 
					Integer.parseInt(file_split[2]), Integer.parseInt(file_split[3]), 
					Boolean.parseBoolean(file_split[4]));
			disk.diskPartitionTable.insertFile(new_file);
			
			disk_files = systemParameters.readLine();
		}
		
		jobLinkedList = new JobLinkedList();
		eventLinkedList = new EventLinkedList();
	}
	
	/* Returns if there are no more job parameters in the list of jobs in the system */
	public boolean endOfJobs(){
		return jobLinkedList.isEmpty();
	}
	
	/* Returns the job linked list */
	public JobLinkedList getJobLinkedList(){
		return jobLinkedList;
	}
	
	/* Method creates object job of jobTable class with attributes passed by text file */ 
	void createJob(String line){
		String[] splited = line.split("\\s+");
		
		jobTable job;
		
		/* The order of the inputs must be respected in the input file */
		try{
			int jobID = Integer.parseInt(splited[0]);
			int arrival_time = Integer.parseInt(splited[1]);
			int total_time = Integer.parseInt(splited[2]);
			int number_requests = Integer.parseInt(splited[3]);
			
			int[] peripherals = new int[number_requests];
			int i = 0;
			while(i < number_requests){
				 peripherals[i] = Integer.parseInt(splited[4 + i]);
				 i++;
			}
			
			int[] record_lenghts = new int[number_requests];
			int j = 0;
			while(j < number_requests){
				 record_lenghts[j] = Integer.parseInt(splited[4 + i + j]);
				 j++;
			}
			
			int priority = Integer.parseInt(splited[4 + i + j]);
			
			int number_of_files = Integer.parseInt(splited[5 + i + j]);
			int[] files_to_access = new int[number_of_files];
			for(int l = 0; l < number_of_files; l++){
				files_to_access[l] = Integer.parseInt(splited[6 + i + j + l]);
			}
			
			/* create job with attributes passed in the input text file 
			 * values for flag and time remaining are, by default, 
			 * initialized in false and zero.*/
			job = new jobTable(jobID, arrival_time, total_time, number_requests, 
					peripherals, record_lenghts, false, total_time/number_requests, 
					priority, number_of_files, files_to_access);
			
			/* now, the segments are inserted */
			for(int k = 6 + i + j + number_of_files; k < splited.length; k = k + 2){
				job.segmentList.insert(new Segment(Integer.parseInt(splited[k]), Integer.parseInt(splited[k + 1]), jobID));
			}
			
			jobLinkedList.insertLink(job);
			
		} catch (NumberFormatException e) {
			System.err.println(e);
		}
	}
	
	/* This method creates the event object for a job */
	void createEventForJob(jobTable job){
		int event_time = job.get_arrival_time(); //event 1 is scheduled to occur at the time job arrives
		eventTable event = new eventTable(1, event_time, job); //creates event cell
		
		eventLinkedList.insertLink(event); //insert new cell into the list
	}
	
	/* This method initializes system by reading first job parameters */
	public void initializeSystem() throws IOException{
		line = jobsParameters.readLine();
		if(line != null){ //if line contains job parameters, continue
			createJob(line); //creates job cell for the first job and add it into the linked list
			createEventForJob(jobLinkedList.getFirstJob()); //creates event 1 for the first job
			
			System.out.println("Job " + jobLinkedList.getFirstJob().get_job_id() + " arrived system.");
			
			jobLinkedList.getFirstJob().retrieveData();
		}
	}
	
	/* Gets next event (with the lesser event_time) in the eventLinkedList 
	 * and execute method according to its event identifier */
	public void selectEvent() throws IOException{
		//if(TIME < 287){
		System.out.println("\n----------------------------------------------------------------------------------------");
		eventTable nextEvent = eventLinkedList.getNextEvent();
		if(nextEvent != null){
			int event_identifier = nextEvent.get_event_identifier();
			
			/* set time of the simulation at time of execution of the next event */
			if(TIME <= nextEvent.get_event_time())
				TIME = nextEvent.get_event_time();
			else
				nextEvent.set_event_time(TIME);
			
			
			System.out.println("\nTime now: " + TIME + "ns | Executed event: " + event_identifier + " | Job ID: " + nextEvent.get_event_job().get_job_id());
			executeEvent(event_identifier, nextEvent);
			centralMemory.displayStatus();
			eventLinkedList.displayStatus();	//display status of the event list
			cpu.multiprogramming.display_segs_in_cpu();
		}
		//}
	}
	
	/* This method calls a method according to the event identifier */
	void executeEvent(int event_identifier, eventTable nextEvent) throws IOException{
		switch (event_identifier){
		case 1:
			event1(nextEvent);
			break;
		case 2:
			event2(nextEvent);
			break;
		case 3:
			event3(nextEvent);
			break;
		case 4:
			event4(nextEvent);
			break;
		case 5:
			event5(nextEvent);
			break;
		case 6:
			event6(nextEvent);
			break;
		case 7:
			event7(nextEvent);
			break;
		default:
			System.out.println("Invalid event");
		}
	}
	
	/* This event sets next event_identifier = 2 for the job and adds next job into event list*/ 
	void event1(eventTable event) throws IOException{
		event.set_event_identifier(2);
		event.set_event_time(TIME);
		/* gets next line of job and add it into the system*/
		line = jobsParameters.readLine();
		if(line != null){ 		//if line contains job parameters, continue
			createJob(line); 	//creates job cell for the first job and add it into the linked list
			createEventForJob(jobLinkedList.getFirstJob()); //creates event 1 for the first job and add it into the event linked list
			
			System.out.println("Job " + jobLinkedList.getFirstJob().get_job_id() + " arrived system");
			
			jobLinkedList.getFirstJob().retrieveData();	 //prints data
		} else {
			System.out.println("No more jobs to be added in the system");
		}
	}
	
	/* This method allocates memory for job, if there is sufficient, otherwise put it in the queue */
	void event2(eventTable event) throws IOException{
		
		/* choose a random segment to be allocated */
		Random generator = new Random();
		int seg_id = generator.nextInt(event.get_event_job().segmentList.number_of_segments());
		int seg_space = event.get_event_job().segmentList.search(seg_id).get_space();
		Segment current_segment = new Segment(seg_id, seg_space, event.get_event_job().get_job_id());
		
		/* if segment is already allocated in the CM */
		if(centralMemory.cmJobLinkedList.segment_already_allocated(event.get_event_job(), seg_id)){
			System.out.println("Segment " + seg_id + " from Job " +
		event.get_event_job().get_job_id() + " already allocated in the CM");
			event.set_event_identifier(3);
			event.set_event_segment(current_segment);
		} else {
		
			if(centralMemory.allocateSegment(event.get_event_job(), seg_id)){ //if there is enough space to allocate
				
				event.set_event_identifier(3);
				event.set_event_time(TIME + centralMemory.get_relocation_time());
				event.set_event_segment(current_segment);
				
				System.out.println("Segment " + seg_id + " from Job " + event.get_event_job().get_job_id() + " successfully allocated in the CM");
				
			} else {	//there is not enough space for the job
				if(!event.get_in_queue()){	//if event is not in a queue
					jobTable job = event.get_event_job();
					
					//creates cell in the queue list
					Queue queue = new Queue(job, job.get_priority(), TIME);
		
					//computes overhead time
					event.set_event_time(TIME + centralMemory.get_relocation_time());
					
					//insert new cell in the queue list
					centralMemory.cmQueueLinkedList.insertLink(queue);
					event.set_in_queue(true);
					System.out.println("Not enough space for allocation. Segment from Job " + job.get_job_id() + " added into CM queue");
				}
			}
			
		}
		//centralMemory.displayStatus();
	}
	
	/* this method executes job in the cpu */
	void event3(eventTable event){
		
		/* gets from event which segment will execute now, this one was decided in event 2 */
		int segment_id = event.get_event_segment().get_id();
		
		if(cpu.multiprogramming.support_more_segs()){	//if cpu has less process than maximum, continue
			jobTable job = event.get_event_job();
			
			//if job is not executing in the cpu, add it; else, continues
			if(!cpu.multiprogramming.get_seg_being_executed(event.get_event_segment())){
				cpu.executeSegment(event.get_event_segment());	//insert job in the multiprogramming list
			}
			
			if(job.get_record_count() > 0){	//still have time to be processed
				int time_of_execution = 0;	//time to remain on cpu
				//if jobs wasn't interrupted before, time of execution = request_time
				if(!job.get_flag()){
					time_of_execution = job.get_request_time();
				} else {	//if it was interrupted, time of execution = time_remaining
					time_of_execution = job.get_time_remaining();
					job.set_flag(false);	//not interrupted anymore
				}
				//if the job finishes its execution before cpu time slice, decrement job count, set event in 4
				if(cpu.get_time_slice() >= time_of_execution){
					job.decrement_record_count();
					event.set_event_identifier(4);
					event.set_event_time(TIME + time_of_execution);
					System.out.println("Segment " + event.get_event_segment().get_id() + " from Job " + event.get_event_job().get_job_id() + " being executed by CPU for " + time_of_execution + "ns");
				} else {	//else, doesn't decrement nor change event id, and the job is interrupted
					event.set_event_time(TIME + cpu.get_time_slice());
					job.set_time_remaining(time_of_execution - cpu.get_time_slice());	//set time remaining to finishes request time
					job.set_flag(true);
					System.out.println("Segment " + event.get_event_segment().get_id() + " from Job " + event.get_event_job().get_job_id() + " being executed by CPU for " + cpu.get_time_slice() + "ns");
				}				
			} else {	//all time to be processed have already passed
				event.set_event_identifier(7);
				event.set_event_time(TIME);
				System.out.println("Job " + job.get_job_id() + " already executed in the CPU");
			}
		} else {	//if it is occupied...
			if(cpu.multiprogramming.get_seg_being_executed(event.get_event_segment())){ //if is occupied executing this job, continues
				if(event.get_event_job().get_record_count() > 0){	//still have time to be processed
					int time_of_execution = 0;
					
					if(event.get_event_job().get_flag()){	//if job was interrupted, gets time_remaining
						time_of_execution = event.get_event_job().get_time_remaining();
						event.get_event_job().set_flag(false);
					} else {	//else, get time between requests
						time_of_execution = event.get_event_job().get_request_time();
					}
					
					if(cpu.get_time_slice() >= time_of_execution){	//there is enough time to finish job slice
						event.get_event_job().decrement_record_count();
						event.set_event_identifier(4);
						event.set_event_time(TIME + time_of_execution);						
						System.out.println("Segment " + event.get_event_segment().get_id() + " from Job " + event.get_event_job().get_job_id() + " being executed by CPU for " + time_of_execution + "ns");
					} else {	//keep on same event id and execute until cpu time slice
						event.set_event_time(TIME + cpu.get_time_slice());
						event.get_event_job().set_time_remaining(time_of_execution - cpu.get_time_slice());	//set time remaining to finishes request time
						event.get_event_job().set_flag(true);	//it is interrupted
						System.out.println("Segment " + event.get_event_segment().get_id() + " from Job " + event.get_event_job().get_job_id() + " being executed by CPU for " + cpu.get_time_slice() + "ns");
					}
				} else {	//all time to be processed have already passed
					event.set_event_identifier(7);
					event.set_event_time(TIME);
					System.out.println("Job " + event.get_event_job().get_job_id() + " already executed in the CPU");	
				}
			} 
			
			//if is executing another job, add this one to the queue
			else {
				
				if(!event.get_in_queue()){	//if event is not in a queue
					jobTable job = event.get_event_job();
					System.out.println("CPU is busy, job" + job.get_job_id() + " added into CPU queue");
					Queue cpuQueue = new Queue(job, job.get_priority(), TIME);
					cpu.cpuQueueLinkedList.insertLink(cpuQueue);	//insert job in the queue
					event.set_in_queue(true);	//event is in the queue	
				}	
			}
		}
	}
	
	/* release cpu, execute next in the queue and go to next event*/
	void event4(eventTable event){
		cpu.finishSegment(event.get_event_segment());
		
		//remove segment from memory
		jobCM removedJob = centralMemory.cmJobLinkedList.removeSegmentCM(event.get_event_segment());
		emptyCM restoredSpace = new emptyCM(removedJob.get_initial_address(), removedJob.get_final_address());
		centralMemory.cmEmptyLinkedList.restoreEmptySpace(restoredSpace);
		
		event.set_event_identifier(5);
		event.set_event_time(TIME + cpu.get_overhead_time());
		
		System.out.println("Segment " + event.get_event_segment().get_id() + " from Job " + event.get_event_job().get_job_id() + " released CPU and CM to execute I/O process");
		
		Queue jobsInCPUQueue = cpu.cpuQueueLinkedList.getFirstLink();
		if(jobsInCPUQueue != null){
			jobTable job = jobsInCPUQueue.get_job();
			Segment waiting_segment = eventLinkedList.getEventFromJob(job).get_event_segment();
			
			cpu.cpuQueueLinkedList.removeLink(job, TIME);	//removed from queue
			cpu.executeSegment(waiting_segment);
			eventLinkedList.getEventFromJob(job).set_in_queue(false); //not in a queue anymore
			eventLinkedList.getEventFromJob(job).set_event_time(TIME);//will execute now
			
			System.out.println("Segment " + waiting_segment.get_id() + " from Job " + job.get_job_id() + " waiting in the CPU queue was successfully allocated in the CPU");
		}
		
		//centralMemory.displayStatus();
	}
	
	/* this method select which peripheral will be used for I/O */
	void event5(eventTable event){
		int peripheral = event.get_event_job().get_record_count(); //the position of the peripheral in the array at this time
		switch(event.get_event_job().get_peripherals()[peripheral]){//gets the code of the peripheral used this time
			case 0:
				event5forDisk(event);
				break;
			case 1:
				event5forReader(event);
				break;
			case 2:
				event5forPrinter(event);
				break;
		}
	}
	
	void event5forDisk(eventTable event){
		if(event.get_event_job().get_number_files() <= 0){	//no files to access
			event.set_event_identifier(6);
			event.set_event_time(TIME);
			
			System.out.println("Job " + event.get_event_job().get_job_id() + " has no files to access in disk. Releasing disk.");
		} else {	//there is files to access
			jobTable job = event.get_event_job();
			event.get_event_job().decrement_number_files();
			int file_number = job.get_files_to_access()[job.get_number_files()];	//gets file to be accessed now
			
			if(disk.diskPartitionTable.searchFile(file_number) != null){	//if this file exists in the system, continue
				SystemFile file = disk.diskPartitionTable.searchFile(file_number);
				
				if(disk.get_occupied()){ //if disk is already being used
					if(disk.get_job() == event.get_event_job()){ //if is occupied executing this job, continues
						
						event.set_event_identifier(6);
						event.set_event_time(TIME + disk.get_job().get_record_lengths()[disk.get_job().get_record_count()]);
						
						if(file.public_file){ //if file is public, continue
							if(file.busy){	//if this file is being read, add to queue
								Queue queue = new Queue(job, job.get_priority(), TIME);
								file.fileQueue.insertLink(queue);	//insert job to this file's queue
								event.set_in_queue(true);	//event is in the queue
								System.out.println("File " + file.name + " (ID " + file.fileID + ") is " +
										"being read, Job" + job.get_job_id() + " added into its queue");
							} else {	//if file is free, read it
								file.read_file();
								System.out.println("Job " + event.get_event_job().get_job_id() + 
										" started Disk I/O reading public file " + file.name + " (ID " + file.fileID + ")");
							}
						} else {	//if it is private...
							if(file.ownerID == job.get_job_id()){	//but this job is the owner, continue
								file.read_file();
								System.out.println("File " + file.name + " (ID " + file.fileID + ") is private" +
										"but Job " + job.get_job_id() + " is its owner. Started reading file.");
							} else {	//and job is not owner
								event.set_event_identifier(6);
								event.set_event_time(TIME);
								System.out.println("File " + file.name + " (ID " + file.fileID + ") is private" +
										" and Job " + job.get_job_id() + " isn't its owner. Nothing is done");
							}
						}
						
					} else {	//if is executing another job, add this one to the queue
						if(!event.get_in_queue()){	//if event is not in a queue	
							Queue queue = new Queue(job, job.get_priority(), TIME);
							disk.queueLinkedList.insertLink(queue);	//insert job in the queue
							event.set_in_queue(true);	//event is in the queue
							System.out.println("Disk is busy, Job" + job.get_job_id() + " is being added into the queue");
						}
					}
				} else {
					disk.executeJob(job);
					event.set_event_identifier(6);
					event.set_event_time(TIME + job.get_record_lengths()[job.get_record_count()]);
					
					if(file.public_file){ //if file is public, continue
						if(file.busy){	//if this file is being read, add to queue
							Queue queue = new Queue(job, job.get_priority(), TIME);
							file.fileQueue.insertLink(queue);	//insert job to this file's queue
							event.set_in_queue(true);	//event is in the queue
							System.out.println("Public file " + file.name + " (ID " + file.fileID + ") is " +
									"being read, Job" + job.get_job_id() + " added into its queue");
						} else {	//if file is free, read it
							file.read_file();
							System.out.println("Disk allocated to the job " + job.get_job_id() + ", " +
									"reading public file " + file.name + " (ID " + file.fileID + ")");
						}
					} else {
						if(file.ownerID == job.get_job_id()){	//but this job is the owner, continue
							file.read_file();
							System.out.println("File " + file.name + " (ID " + file.fileID + ") is private" +
									"but Job " + job.get_job_id() + " is its owner. Started reading file.");
						} else {	//and job is not owner
							event.set_event_identifier(6);
							event.set_event_time(TIME);
							System.out.println("File " + file.name + " (ID " + file.fileID + ") is private" +
									" and Job " + job.get_job_id() + " isn't its owner. Nothing is done");
						}
					}
				}
			} else {
				event.set_event_identifier(6);
				event.set_event_time(TIME);
				
				System.out.println("File number " + file_number + " doesn't exist in the system. No disk I/O happened" );
			}
		}
	}
	
	void event5forReader(eventTable event){
		if(reader.get_occupied()){ //if reader is already being used
			if(reader.get_job() == event.get_event_job()){ //if is occupied executing this job, continues
				
				event.set_event_identifier(6);
				event.set_event_time(TIME + reader.get_job().get_record_lengths()[reader.get_job().get_record_count()]);
				
				System.out.println("Job " + event.get_event_job().get_job_id() + " started Reader I/O");
			} else {	//if is executing another job, add this one to the queue
				if(!event.get_in_queue()){	//if event is not in a queue	
					jobTable job = event.get_event_job();
					Queue queue = new Queue(job, job.get_priority(), TIME);
					reader.queueLinkedList.insertLink(queue);	//insert job in the queue
					event.set_in_queue(true);	//event is in the queue
					System.out.println("Reader is busy, job" + job.get_job_id() + " is being added into the queue");
				}
			}
		} else {
			jobTable job = event.get_event_job();
			reader.executeJob(job);
			event.set_event_identifier(6);
			event.set_event_time(TIME + job.get_record_lengths()[job.get_record_count()]);
			
			System.out.println("Reader allocated to the job " + job.get_job_id());
		}
	}

	void event5forPrinter(eventTable event){
		if(printer.get_occupied()){ //if reader is already being used
			if(printer.get_job() == event.get_event_job()){ //if is occupied executing this job, continues
				
				event.set_event_identifier(6);
				event.set_event_time(TIME + printer.get_job().get_record_lengths()[printer.get_job().get_record_count()]);
				
				System.out.println("Job " + event.get_event_job().get_job_id() + " started Reader I/O");
			} else {	//if is executing another job, add this one to the queue
				if(!event.get_in_queue()){	//if event is not in a queue	
					jobTable job = event.get_event_job();
					Queue queue = new Queue(job, job.get_priority(), TIME);
					printer.queueLinkedList.insertLink(queue);	//insert job in the queue
					event.set_in_queue(true);	//event is in the queue
					System.out.println("Printer is busy, job" + job.get_job_id() + " is being added into the queue");
				}
			}
		} else {
			jobTable job = event.get_event_job();
			printer.executeJob(job);
			event.set_event_identifier(6);
			event.set_event_time(TIME + job.get_record_lengths()[job.get_record_count()]);
			
			System.out.println("Printer allocated to the job " + job.get_job_id());
		}
	}
	
	/* this method releases I/O peripheral from the  job */
	void event6(eventTable event){
		int peripheral = event.get_event_job().get_record_count(); //the position of the peripheral in the array at this time
		switch(event.get_event_job().get_peripherals()[peripheral]){//gets the code of the peripheral used this time
			case 0:
				event6forDisk(event);
				break;
			case 1:
				event6forReader(event);
				break;
			case 2:
				event6forPrinter(event);
				break;
		}
	}
	
	void event6forDisk(eventTable event){
		disk.finishJob();
		jobTable event_job = event.get_event_job();
		int file_number = event_job.get_files_to_access()[event_job.get_number_files()];	//gets file to be accessed now
		if(disk.diskPartitionTable.searchFile(file_number) != null)
			disk.diskPartitionTable.searchFile(file_number).release_file();	//release file
		
		//now, check if there is a job in the queue waiting for being allocated
		Queue jobsInQueue = disk.queueLinkedList.getFirstLink();
		if(jobsInQueue != null){
			jobTable job = jobsInQueue.get_job();
			
			disk.queueLinkedList.removeLink(job, TIME);	//removed from queue
			disk.executeJob(job);
			eventLinkedList.getEventFromJob(job).set_in_queue(false); //not in a queue anymore
			
			System.out.println("Job " + job.get_job_id() + " waiting in the Disk queue was successfully allocated in the Disk");
		}
		
		System.out.println("Job " + event.get_event_job().get_job_id() + " released Disk for returning to CPU");
		
		//return to event 2 for allocating a new segment 
		event.set_event_identifier(2);
		event.set_event_time(TIME + disk.get_overhead_time());
	}
	void event6forReader(eventTable event){
		reader.finishJob();
		
		//now, check if there is a job in the queue waiting for being allocated
		Queue jobsInQueue = reader.queueLinkedList.getFirstLink();
		if(jobsInQueue != null){
			jobTable job = jobsInQueue.get_job();
			
			reader.queueLinkedList.removeLink(job, TIME);	//removed from queue
			reader.executeJob(job);
			eventLinkedList.getEventFromJob(job).set_in_queue(false); //not in a queue anymore
			
			System.out.println("Job " + job.get_job_id() + " waiting in the Reader queue was successfully allocated in the Reader");
		}
		
		System.out.println("Job " + event.get_event_job().get_job_id() + " released Reader for returning to CPU");
		
		event.set_event_identifier(2);
		event.set_event_time(TIME + reader.get_overhead_time());
	}
	void event6forPrinter(eventTable event){
		printer.finishJob();
		
		//now, check if there is a job in the queue waiting for being allocated
		Queue jobsInQueue = printer.queueLinkedList.getFirstLink();
		if(jobsInQueue != null){
			jobTable job = jobsInQueue.get_job();
			
			printer.queueLinkedList.removeLink(job, TIME);	//removed from queue
			printer.executeJob(job);
			eventLinkedList.getEventFromJob(job).set_in_queue(false); //not in a queue anymore
			
			System.out.println("Job " + job.get_job_id() + " waiting in the Printer queue was successfully allocated in the Printer");
		}
		
		System.out.println("Job " + event.get_event_job().get_job_id() + " released Printer for returning to CPU");
		
		event.set_event_identifier(2);
		event.set_event_time(TIME + printer.get_overhead_time());
	}
	
	void event7(eventTable event){
		//----------remove job from the system----------//
		jobLinkedList.removeJob(event.get_event_job());
		eventLinkedList.removeEvent(event);
		
		//----------remove job from the central memory----------//
		jobCM removedJob = centralMemory.cmJobLinkedList.removeSegmentCM(event.get_event_segment());
		/* transform the space, before occupied by a job, in a free space */
		emptyCM restoredSpace = new emptyCM(removedJob.get_initial_address(), removedJob.get_final_address());
		centralMemory.cmEmptyLinkedList.restoreEmptySpace(restoredSpace);
		
		System.out.println("Job " + event.get_event_job().get_job_id() + " finished execution in the system. " +
				"It released CPU and CM.\n");
		
		/* now, it is necessary to look in the CM queue, to check if there is a job to be allocated */
		Queue jobsInQueue = centralMemory.cmQueueLinkedList.getFirstLink();
		while(jobsInQueue != null){
			
			/* segment which is being allocated */
			Random generator = new Random();
			int seg_id = generator.nextInt(event.get_event_job().segmentList.number_of_segments());
			
			if(centralMemory.allocateSegment(jobsInQueue.get_job(), seg_id)){ //if there is enough space to allocate
				eventLinkedList.getEventFromJob(jobsInQueue.get_job()).set_event_identifier(3);
				eventLinkedList.getEventFromJob(jobsInQueue.get_job()).set_event_time(TIME + centralMemory.get_relocation_time());
				centralMemory.cmQueueLinkedList.removeLink(jobsInQueue.get_job(), TIME);
				eventLinkedList.getEventFromJob(jobsInQueue.get_job()).set_in_queue(false); //not in the queue anymore
				
				int space = jobsInQueue.get_job().segmentList.search(seg_id).get_space();
				Segment next_segment = new Segment(seg_id, space, jobsInQueue.get_job().get_job_id());
				eventLinkedList.getEventFromJob(jobsInQueue.get_job()).set_event_segment(next_segment);
				
				System.out.println("Segment " + seg_id + "from Job " + jobsInQueue.get_job().get_job_id() + " waiting in the CM queue was successfully allocated in the CM");
			}
			jobsInQueue = jobsInQueue.nextLink;
		}
		
		//centralMemory.displayStatus();
		
		//----------remove job from the CPU----------//
		cpu.finishSegment(event.get_event_segment());
		//now, check if there is a job in the cpu queue waiting for being executed
		Queue jobsInCPUQueue = cpu.cpuQueueLinkedList.getFirstLink();
		if(jobsInCPUQueue != null){
			jobTable job = jobsInCPUQueue.get_job();
			
			cpu.cpuQueueLinkedList.removeLink(job, TIME);	//removed from queue
			cpu.executeSegment(eventLinkedList.getEventFromJob(job).get_event_segment());
			eventLinkedList.getEventFromJob(job).set_in_queue(false); //not in a queue anymore
			
			System.out.println("Segment " + eventLinkedList.getEventFromJob(job).get_event_segment().get_id() + 
					" from Job " + job.get_job_id() + " waiting in the CPU queue was successfully " +
							"allocated in the CPU");
		}
		
	}
	
	
	public void printEstatistics(){
		System.out.println("\nEnd of simulation at TIME = " + TIME);
		System.out.println("Central memory queue estatistics -> ");
		centralMemory.cmQueueLinkedList.getListHeader().displayStatus(TIME);
		
		System.out.println("\nCPU queue estatistics -> ");
		cpu.cpuQueueLinkedList.getListHeader().displayStatus(TIME);
		
		System.out.println("\nDisk queue estatistics -> ");
		disk.queueLinkedList.getListHeader().displayStatus(TIME);
		
		System.out.println("\nReader queue estatistics -> ");
		reader.queueLinkedList.getListHeader().displayStatus(TIME);
		
		System.out.println("\nPrinter queue estatistics -> ");
		printer.queueLinkedList.getListHeader().displayStatus(TIME);
	}
}
