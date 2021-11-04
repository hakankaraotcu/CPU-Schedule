import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintStream;

public class Schedule {

	public static void main(String[] args) throws IOException {
		
		String whichAlgorithm = args[0];
		int quantumTime = 10;
		int count = 0;
		int lineNumber = 0;
		
		LineNumberReader lr = new LineNumberReader(new FileReader("schedule.txt"));
		BufferedReader br = new BufferedReader(new FileReader("schedule.txt"));
		PrintStream outFile = new PrintStream(new File("output.txt"));
		
		while(lr.readLine() != null) {
			lineNumber++;
		}
		
		String[] taskName = new String[lineNumber];
		int[] arrivalTime = new int[lineNumber];
		int[] priority = new int[lineNumber];
		int[] cpuBurst = new int[lineNumber];
		
		lr.close();
		
		String currentLine;
		
		while((currentLine = br.readLine()) != null) {
			String[] values = currentLine.split(", ");
			String name = values[0];
			int at = Integer.parseInt(values[1]);
			int pr = Integer.parseInt(values[2]);
			int cpu = Integer.parseInt(values[3]);
			
			taskName[count] = name;
			arrivalTime[count] = at;
			priority[count] = pr;
			cpuBurst[count] = cpu;
			count++;
		}
		
		br.close();
		
		if("FCFS".equalsIgnoreCase(whichAlgorithm)) {
			fcfs(outFile,taskName,arrivalTime,priority,cpuBurst);
		}else if("SJF".equalsIgnoreCase(whichAlgorithm)) {
			sjf(outFile,taskName,arrivalTime,priority,cpuBurst);
		}else if("PRÝ".equalsIgnoreCase(whichAlgorithm)) {
			pri(outFile,taskName,arrivalTime,priority,cpuBurst);
		}else if("RR".equalsIgnoreCase(whichAlgorithm)) {
			rr(outFile,taskName,arrivalTime,priority,cpuBurst,quantumTime);
		}else if("PRÝ-RR".equalsIgnoreCase(whichAlgorithm)) {
			priRr(outFile,taskName,arrivalTime,priority,cpuBurst,quantumTime);
		}
		
	}

	public static void fcfs(PrintStream outFile, String[] taskName,int[] arrivalTime,int[] priority,int[] cpuBurst) {
		
		outFile.println("\nFirst Come First Serve Scheduling");
		
		float totalWaitingTime = 0;
		float totalTurnaroundTime = 0;
		int[] waitingTime = new int[taskName.length];
		int[] completionTime = new int[taskName.length];
		int[] turnaroundTime = new int[taskName.length];
		int temp;
		String temp1;
		
		for(int i = 0;i < arrivalTime.length;i++) {
			for(int j = i + 1;j < arrivalTime.length;j++) {
				if(arrivalTime[i] > arrivalTime[j]) {
					temp1 = taskName[i];
					taskName[i] = taskName[j];
					taskName[j] = temp1;
					
					temp = arrivalTime[i];
					arrivalTime[i] = arrivalTime[j];
					arrivalTime[j] = temp;
					
					temp = priority[i];
					priority[i] = priority[j];
					priority[j] = temp;
					
					temp = cpuBurst[i];
					cpuBurst[i] = cpuBurst[j];
					cpuBurst[j] = temp;
				}
			}
		}
		
		for(int i = 0;i < arrivalTime.length;i++) {
			if(i == 0) {
				completionTime[i] = arrivalTime[i] + cpuBurst[i];
				turnaroundTime[i] = completionTime[i] - arrivalTime[i];
				waitingTime[i] = 0;
			}
			else {
				completionTime[i] = completionTime[i-1] + cpuBurst[i];
				turnaroundTime[i] = completionTime[i] - arrivalTime[i];
				waitingTime[i] = turnaroundTime[i] - cpuBurst[i];
			}
		}
		
		for(int i = 0;i < arrivalTime.length;i++) {
			outFile.println("\nWill run Name: " + taskName[i] + "\nPriority: " + priority[i] + "\nBurst: " + cpuBurst[i]);
			outFile.printf("\nTask %s finished\n",taskName[i]);
		}
		for(int i = 0;i < arrivalTime.length;i++) {
			outFile.printf("\n%s >> waiting time: %d turnaround time: %d",taskName[i],waitingTime[i],turnaroundTime[i]);
			totalWaitingTime += waitingTime[i];
			totalTurnaroundTime += turnaroundTime[i];
		}
		outFile.printf("\nAverage Waiting Time: %.1f Average Turnaround Time %.1f",totalWaitingTime/taskName.length,totalTurnaroundTime/taskName.length);
		outFile.close();
	}
	
	private static void sjf(PrintStream outFile,String[] taskName, int[] arrivalTime, int[] priority, int[] cpuBurst) {

		outFile.println("\nShortest Job First Scheduling");
		
		float totalWaitingTime = 0;
		float totalTurnaroundTime = 0;
		int[] turnaroundTime = new int[taskName.length];
		int[] waitingTime = new int[taskName.length];
		int[] completionTime = new int[taskName.length];
		boolean[] complete = new boolean[taskName.length];
		int startTime = 0;
		int total = 0;
		
		for(int i = 0;i < taskName.length;i++) {
			complete[i] = false;
		}
		
		while(total < taskName.length) {
			int j = cpuBurst.length;
			int minBurst = Integer.MAX_VALUE; 
			
			for(int i = 0;i < cpuBurst.length;i++) {
				if((arrivalTime[i] <= startTime) && (complete[i] == false) && (cpuBurst[i] < minBurst)) {
					minBurst = cpuBurst[i];
					j = i;
				}
			}
			
			if(j == cpuBurst.length) {
				startTime++;
			}
			
			else {
				completionTime[j] = startTime + cpuBurst[j];
				if(total == 0) {
					turnaroundTime[j] = completionTime[j] - arrivalTime[j];
					waitingTime[j] = 0;
				}else {
					turnaroundTime[j] = completionTime[j] - arrivalTime[j];
					waitingTime[j] = turnaroundTime[j] - cpuBurst[j];
				}
				startTime += cpuBurst[j];
				complete[j] = true;
				total++;
				outFile.println("\nWill run Name: " + taskName[j] + "\nPriority: " + priority[j] + "\nBurst: " + cpuBurst[j]);
				outFile.printf("\nTask %s finished\n",taskName[j]);
				}
		}
		for(int i = 0;i < arrivalTime.length;i++) {
			outFile.printf("\n%s >> waiting time: %d turnaround time: %d",taskName[i],waitingTime[i],turnaroundTime[i]);
			totalWaitingTime += waitingTime[i];
			totalTurnaroundTime += turnaroundTime[i];
		}
		outFile.printf("\nAverage Waiting Time: %.1f Average Turnaround Time %.1f",totalWaitingTime/taskName.length,totalTurnaroundTime/taskName.length);
		outFile.close();
	}
	
	private static void pri(PrintStream outFile, String[] taskName, int[] arrivalTime, int[] priority, int[] cpuBurst) {
		
		outFile.println("\nPriority Scheduling");
		
		float totalWaitingTime = 0;
		float totalTurnaroundTime = 0;
		int[] turnaroundTime = new int[taskName.length];
		int[] waitingTime = new int[taskName.length];
		int[] completionTime = new int[taskName.length];
		boolean[] complete = new boolean[taskName.length];
		int startTime = 0;
		int total = 0;
		
		for(int i = 0;i < taskName.length;i++) {
			complete[i] = false;
		}
		
		while(total < taskName.length) {
			int j = priority.length;
			int maxPriority = Integer.MIN_VALUE; 
			int minArrivalTime = Integer.MAX_VALUE;
			
			for(int i = 0;i < priority.length;i++) {
				if((arrivalTime[i] <= startTime) && (complete[i] == false)) {
					if(priority[i] > maxPriority) {
						maxPriority = priority[i];
						minArrivalTime = arrivalTime[i];
						j = i;
					}
					else if(priority[i] == maxPriority) {
						if(arrivalTime[i] < minArrivalTime) {
							maxPriority = priority[i];
							minArrivalTime= arrivalTime[i];
							j = i;
						}
					}
				}
			}
			
			if(j == priority.length) {
				startTime++;
			}
			else {
				completionTime[j] = startTime + cpuBurst[j];
				if(total == 0) {
					turnaroundTime[j] = completionTime[j] - arrivalTime[j];
					waitingTime[j] = 0;
				}
				else {
					turnaroundTime[j] = completionTime[j] - arrivalTime[j];
					waitingTime[j] = turnaroundTime[j] - cpuBurst[j];
				}
				startTime += cpuBurst[j];
				complete[j] = true;
				total++;
				
				outFile.println("\nWill run Name: " + taskName[j] + "\nPriority: " + priority[j] + "\nBurst: " + cpuBurst[j]);
				outFile.printf("\nTask %s finished\n",taskName[j]);
				}
		}
		for(int i = 0;i < arrivalTime.length;i++) {
			outFile.printf("\n%s >> waiting time: %d turnaround time: %d",taskName[i],waitingTime[i],turnaroundTime[i]);
			totalWaitingTime += waitingTime[i];
			totalTurnaroundTime += turnaroundTime[i];
		}
		outFile.printf("\nAverage Waiting Time: %.1f Average Turnaround Time %.1f",totalWaitingTime/taskName.length,totalTurnaroundTime/taskName.length);
		outFile.close();
		
	}
	
	private static void rr(PrintStream outFile, String[] taskName, int[] arrivalTime, int[] priority, int[] cpuBurst,int quantumTime) {
		
		outFile.println("\nRound-Robin Scheduling");
		
		float totalWaitingTime = 0;
		float totalTurnaroundTime = 0;
		int[] turnaroundTime = new int[taskName.length];
		int[] waitingTime = new int[taskName.length];
		int[] completionTime = new int[taskName.length];
		String[] copyTaskName = new String[taskName.length];
		int[] copyArrivalTime = new int[taskName.length];
		int[] copyPriority = new int[taskName.length];
		int[] copyCpuBurst = new int[taskName.length];
		int[] tempArrivalTime = new int[taskName.length];
		int[] tempCpuBurst = new int[taskName.length];
		boolean[] complete = new boolean[taskName.length];
		int startTime = 0;
		int total = 0;
		
		for(int i = 0;i < taskName.length;i++) {
			complete[i] = false;
			copyTaskName[i] = taskName[i];
			copyArrivalTime[i] = arrivalTime[i];
			copyPriority[i] = priority[i];
			copyCpuBurst[i] = cpuBurst[i];
		}
		
		int temp;
		String temp1;
		
		for(int i = 0;i < arrivalTime.length;i++) {
			for(int j = i + 1;j < arrivalTime.length;j++) {
				if(copyArrivalTime[i] > copyArrivalTime[j]) {
					temp1 = copyTaskName[i];
					copyTaskName[i] = copyTaskName[j];
					copyTaskName[j] = temp1;
					
					temp = copyArrivalTime[i];
					copyArrivalTime[i] = copyArrivalTime[j];
					copyArrivalTime[j] = temp;
					
					temp = copyPriority[i];
					copyPriority[i] = copyPriority[j];
					copyPriority[j] = temp;
					
					temp = copyCpuBurst[i];
					copyCpuBurst[i] = copyCpuBurst[j];
					copyCpuBurst[j] = temp;
				}
			}
		}
		
		for(int i = 0;i < taskName.length;i++) {
			tempArrivalTime[i] = copyArrivalTime[i];
			tempCpuBurst[i] = copyCpuBurst[i];
		}
		
		while(true) {
			for(int i = 0;i < taskName.length;i++) {
				if(tempArrivalTime[i] <= startTime && complete[i] == false) {
					if(tempArrivalTime[i] <= quantumTime) {
						if(tempCpuBurst[i] > quantumTime) {
							tempCpuBurst[i] -= quantumTime;
							tempArrivalTime[i] += quantumTime;
							startTime += quantumTime;
							outFile.println("\nWill run Name: " + copyTaskName[i] + "\nPriority: " + copyPriority[i] + "\nBurst: " + copyCpuBurst[i]);
							outFile.printf("\nThere is a context switch, so now %s is going to ready queue\n",copyTaskName[i]);
						}
						else {
							startTime += tempCpuBurst[i];
							completionTime[i] = startTime;
							turnaroundTime[i] = completionTime[i] - copyArrivalTime[i];
							waitingTime[i] = turnaroundTime[i] - copyCpuBurst[i];
							complete[i] = true;
							outFile.println("\nWill run Name: " + copyTaskName[i] + "\nPriority: " + copyPriority[i] + "\nBurst: " + copyCpuBurst[i]);
							outFile.printf("\nTask %s finished\n",copyTaskName[i]);
							
						}
					}
					else {
						for(int j = 0;j < taskName.length;j++) {
							if(tempArrivalTime[j] < tempArrivalTime[i] && complete[j] == false) {
								if(tempCpuBurst[j] > quantumTime) {
									tempCpuBurst[j] -= quantumTime;
									tempArrivalTime[j] += quantumTime;
									startTime += quantumTime;
									outFile.println("\nWill run Name: " + copyTaskName[j] + "\nPriority: " + copyPriority[j] + "\nBurst: " + copyCpuBurst[j]);
									outFile.printf("\nThere is a context switch, so now %s is going to ready queue\n",copyTaskName[j]);
									
								}
								else {
									startTime += tempCpuBurst[j];
									completionTime[j] = startTime;
									turnaroundTime[j] = completionTime[j] - copyArrivalTime[j];
									waitingTime[j] = turnaroundTime[j] - copyCpuBurst[j];
									complete[j] = true;
									outFile.println("\nWill run Name: " + copyTaskName[j] + "\nPriority: " + copyPriority[j] + "\nBurst: " + copyCpuBurst[j]);
									outFile.printf("\nTask %s finished\n",copyTaskName[j]);
								}
							}
						}
						if(tempCpuBurst[i] > quantumTime) {
							tempCpuBurst[i] -= quantumTime;
							tempArrivalTime[i] += quantumTime;
							startTime += quantumTime;
							outFile.println("\nWill run Name: " + copyTaskName[i] + "\nPriority: " + copyPriority[i] + "\nBurst: " + copyCpuBurst[i]);
							outFile.printf("\nThere is a context switch, so now %s is going to ready queue\n",copyTaskName[i]);
						}
						else {
							startTime += tempCpuBurst[i];
							completionTime[i] = startTime;
							turnaroundTime[i] = completionTime[i] - copyArrivalTime[i];
							waitingTime[i] = turnaroundTime[i] - copyCpuBurst[i];
							complete[i] = true;
							outFile.println("\nWill run Name: " + copyTaskName[i] + "\nPriority: " + copyPriority[i] + "\nBurst: " + copyCpuBurst[i]);
							outFile.printf("\nTask %s finished\n",copyTaskName[i]);
						}
					}
				}else if(tempArrivalTime[i] > startTime) {
					startTime++;
					i--;
				}
			}
			for(int i = 0;i < taskName.length;i++) {
				if(complete[i] == true) total++;
			}
			if(total == taskName.length) break;
			total = 0;
		}
		for(int i = 0;i < taskName.length;i++) {
			outFile.printf("\n%s >> waiting time: %d turnaround time: %d",copyTaskName[i],waitingTime[i],turnaroundTime[i]);
			totalWaitingTime += waitingTime[i];
			totalTurnaroundTime += turnaroundTime[i];
		}
		outFile.printf("\nAverage Waiting Time: %.1f Average Turnaround Time %.1f",totalWaitingTime/taskName.length,totalTurnaroundTime/taskName.length);
		outFile.close();
	}

	private static void priRr(PrintStream outFile, String[] taskName, int[] arrivalTime, int[] priority, int[] cpuBurst,int quantumTime) {
		
		outFile.println("\nRound-Robin with Priority Scheduling");
		
		float totalWaitingTime = 0;
		float totalTurnaroundTime = 0;
		int[] turnaroundTime = new int[taskName.length];
		int[] waitingTime = new int[taskName.length];
		int[] completionTime = new int[taskName.length];
		String[] copyTaskName = new String[taskName.length];
		int[] copyArrivalTime = new int[taskName.length];
		int[] copyPriority = new int[taskName.length];
		int[] copyCpuBurst = new int[taskName.length];
		int[] tempArrivalTime = new int[taskName.length];
		int[] tempCpuBurst = new int[taskName.length];
		int[] tempPriority = new int[taskName.length];
		boolean[] complete = new boolean[taskName.length];
		int startTime = 0;
		int total = 0;
		
		for(int i = 0;i < taskName.length;i++) {
			complete[i] = false;
			copyTaskName[i] = taskName[i];
			copyArrivalTime[i] = arrivalTime[i];
			copyPriority[i] = priority[i];
			copyCpuBurst[i] = cpuBurst[i];
			
		}
		
		int temp;
		String temp1;
		
		for(int i = 0;i < arrivalTime.length;i++) {
			for(int j = i + 1;j < arrivalTime.length;j++) {
				if(copyArrivalTime[i] > copyArrivalTime[j]) {
					temp1 = copyTaskName[i];
					copyTaskName[i] = copyTaskName[j];
					copyTaskName[j] = temp1;
					
					temp = copyArrivalTime[i];
					copyArrivalTime[i] = copyArrivalTime[j];
					copyArrivalTime[j] = temp;
					
					temp = copyPriority[i];
					copyPriority[i] = copyPriority[j];
					copyPriority[j] = temp;
					
					temp = copyCpuBurst[i];
					copyCpuBurst[i] = copyCpuBurst[j];
					copyCpuBurst[j] = temp;
				}
			}
		}
		
		for(int i = 0;i < taskName.length;i++) {
			tempArrivalTime[i] = copyArrivalTime[i];
			tempCpuBurst[i] = copyCpuBurst[i];
			tempPriority[i] = copyPriority[i];
		}
		while(true) {
			int maxPriority = Integer.MIN_VALUE; 
			
			for(int i = 0;i < priority.length;i++) {
				if(tempArrivalTime[i] <= startTime && complete[i] == false && tempPriority[i] > maxPriority) {
					maxPriority = tempPriority[i];
					int j = 0;
					for(j = 0;j < tempArrivalTime.length;j++) {
						if(tempArrivalTime[j] <= startTime && complete[j] == false && tempPriority[j] > maxPriority) {
							maxPriority = tempPriority[j];
							i = j;
						}
					}
					for(int k = 0;k < tempArrivalTime.length;k++) {
						if(tempArrivalTime[k] <= startTime && tempPriority[k] == maxPriority && complete[k] == false && k != i) {
							if(tempArrivalTime[k] < tempArrivalTime[i]) {
								if(tempCpuBurst[k] > quantumTime) {
									tempCpuBurst[k] -= quantumTime;
									tempArrivalTime[k] += quantumTime;
									startTime += quantumTime;
									outFile.println("\nWill run Name: " + copyTaskName[k] + "\nPriority: " + copyPriority[k] + "\nBurst: " + copyCpuBurst[k]);
									outFile.printf("\nThere is a context switch, so now %s is going to ready queue\n",copyTaskName[k]);
								}
								else {
									startTime += tempCpuBurst[k];
									completionTime[k] = startTime;
									turnaroundTime[k] = completionTime[k] - copyArrivalTime[k];
									waitingTime[k] = turnaroundTime[k] - copyCpuBurst[k];
									complete[k] = true;
									outFile.println("\nWill run Name: " + copyTaskName[k] + "\nPriority: " + copyPriority[k] + "\nBurst: " + copyCpuBurst[k]);
									outFile.printf("\nTask %s finished\n",copyTaskName[k]);
								}
							}
						}
					}
					if(tempCpuBurst[i] > quantumTime) {
						tempCpuBurst[i] -= quantumTime;
						tempArrivalTime[i] += quantumTime;
						startTime += quantumTime;
						outFile.println("\nWill run Name: " + copyTaskName[i] + "\nPriority: " + copyPriority[i] + "\nBurst: " + copyCpuBurst[i]);
						outFile.printf("\nThere is a context switch, so now %s is going to ready queue\n",copyTaskName[i]);
					}
					else {
						startTime += tempCpuBurst[i];
						completionTime[i] = startTime;
						turnaroundTime[i] = completionTime[i] - copyArrivalTime[i];
						waitingTime[i] = turnaroundTime[i] - copyCpuBurst[i];
						complete[i] = true;
						outFile.println("\nWill run Name: " + copyTaskName[i] + "\nPriority: " + copyPriority[i] + "\nBurst: " + copyCpuBurst[i]);
						outFile.printf("\nTask %s finished\n",copyTaskName[i]);
					}
				}
				else if(tempArrivalTime[i] > startTime){
					startTime++;
					i--;
				}
			}
			for(int i = 0;i < taskName.length;i++) {
				if(complete[i] == true) total++;
			}
			if(total == taskName.length) break;
			total = 0;
		}
		for(int i = 0;i < taskName.length;i++) {
			outFile.printf("\n%s >> waiting time: %d turnaround time: %d",copyTaskName[i],waitingTime[i],turnaroundTime[i]);
			totalWaitingTime += waitingTime[i];
			totalTurnaroundTime += turnaroundTime[i];
		}
		outFile.printf("\nAverage Waiting Time: %.1f Average Turnaround Time %.1f",totalWaitingTime/taskName.length,totalTurnaroundTime/taskName.length);
		outFile.close();
	}
	
}
