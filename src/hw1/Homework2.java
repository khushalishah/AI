package hw1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;

public class Homework2 {

	int algo = 0; //BFS - 0, DFS - 1, SA - 2
	String inputFilePath = "E://Assignments//Sem 1//AI//input.txt";
	String outputFilePath = "E://Assignments//Sem 1//AI//output.txt";
	int noOfRows = 0;
	int noOfLizards = 0;
	Queue<LinkedHashSet<Integer>> queue = new LinkedList<>();
	LinkedHashSet<Integer> trees = new LinkedHashSet<>();
	boolean isDense = false;
	int totalPlaces = 0;
	static long startTime = 0;

	public static void main(String args[]) {
		startTime = System.nanoTime();
		Homework hw = new Homework();
		hw.readInput();
		hw.runAlgo();
	}

	void readInput() {
		//read input file
		try {
			BufferedReader br = new BufferedReader(new FileReader(inputFilePath));
			try {
				String line = br.readLine();

				//get type of algorithm
				if(line.trim().equalsIgnoreCase("DFS"))
					algo=1;
				else if(line.trim().equalsIgnoreCase("SA"))
					algo=2;

				//get number of rows and columns
				line = br.readLine();
				noOfRows = Integer.parseInt(line.trim());

				//get number of lizards
				line = br.readLine();
				noOfLizards = Integer.parseInt(line.trim());

				line = br.readLine();    

				//get matrix
				for(int row=0;row<noOfRows;row++) {

					for(int col=0;col<noOfRows;col++) {
						int bit = Integer.parseInt(String.valueOf(line.charAt(col)));
						//check if it is a tree
						if(bit == 2) {
							trees.add(row*noOfRows+col);
						}
					}
					line = br.readLine();

				}

				//insert first node to queue
				queue.add(new LinkedHashSet<Integer>());

			}catch(Exception e) {
				e.printStackTrace();
			}finally {
				br.close();
			}
		}catch(FileNotFoundException fe) {
			System.out.println("File Not Found");
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	void runAlgo() {

		int noOfTrees = trees.size();

		if(noOfTrees == 0 && noOfLizards>noOfRows) {
			//test case will fail
			writeToOutputFile(false,null);
		}else {
			//check if search space is dense or sparse
			totalPlaces = (int) Math.pow(noOfRows, 2);
			if(totalPlaces-noOfTrees-noOfLizards < totalPlaces/2) {
				isDense = true;
			}

			if(algo == 0) {
				runBFS();
			}else if (algo==1) {
				runDFS();
			}else {
				runSA();
			}
		}
	}

	void writeToOutputFile(boolean result,LinkedHashSet<Integer> finalNode) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath, false));
			if(result == true) {
				System.out.println("OK");
				writer.write("OK");
				writer.newLine();

				//print matrix to file
				for(int i=0;i<totalPlaces;i++) {
					if(finalNode.contains(i)) {
						writer.write("1");
					}else if(trees.contains(i)){
						writer.write("2");
					}else {
						writer.write("0");
					}

					if(i%noOfRows==noOfRows-1) {
						writer.newLine();
					}
				}

			}else {
				writer.write("FAIL");
				System.out.println("FAIL");
			}
			writer.close();
			long totalTime = System.nanoTime()-startTime;
			System.out.println("Running Time : "+totalTime+" ns");
			System.out.println("Running Time : "+(totalTime/1000000)+" ms");
			System.out.println("Running Time : "+(totalTime/1000000000)+" s");
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	void runBFS() {
		boolean solFound = false;
		mainLoop : while (!queue.isEmpty()) {
			//System.out.println("BFS...");
			LinkedHashSet<Integer> currentNode = queue.remove();
			//System.out.println(currentNode);
			boolean isSet = false;
			int start = 0;
			if(currentNode.size()>0) {
				start = (int) currentNode.toArray()[currentNode.size()-1]+1;
			}
			for(int i=start;i<totalPlaces;i++) {
				if(isSafe(i,currentNode)) {
					//place lizard here
					LinkedHashSet<Integer> newNode = new LinkedHashSet<>(currentNode);
					newNode.add(i);
					//System.out.println(newNode);
					//System.out.println("Node Size :"+newNode.size());
					isSet = true;

					//check if goal state has reached or not
					if(newNode.size()==noOfLizards) {
						//goal state has reached
						solFound = true;
						writeToOutputFile(true, newNode);
						break mainLoop;
					}
					//add new node to queue
					queue.add(newNode);
					//System.out.println("Queue Size: "+queue.size());

				}
				if(isSet && !isDense)
					if(i%noOfRows == noOfRows-1) 
						break;

			}
		}
		if(!solFound) {writeToOutputFile(false, null);}
	}

	void runDFS() {}

	void runSA() {}

	boolean isSafe(int pos,LinkedHashSet<Integer> currentNode) {
		if(trees.size() !=0) {
			if(!trees.contains(pos)) {
				int col = pos%noOfRows;
				int row = pos/noOfRows;
				boolean flag = true;
				//check for row
				for(int j=0;j<col;j++) {
					int k = noOfRows*row + j;
					//check if there is a lizard
					if(currentNode.contains(k)) {
						flag = false;
					}else if(trees.contains(k)) {
						flag = true;
					}
				}
				if(!flag)
					return false;

				//check for column
				flag = true;
				for(int j=0;j<row;j++) {
					int k = col + noOfRows*j;
					//check if there is lizard or tree
					if(currentNode.contains(k)) {
						flag = false;
					}else if(trees.contains(k)) {
						flag = true;
					}
				}
				if(!flag)
					return false;

				//check for left diagonal
				if(col != 0) {
					flag = true;
					int d = 0,e=0;
					if(row<col) {e=col-row;}else {d=row-col;}
					for(int i=d,j=e;i<row && j<col;j++,i++) {
						int k = noOfRows*i + j;
						//check if there is lizard or tree
						if(currentNode.contains(k)) {
							flag = false;
						}else if(trees.contains(k)) {
							flag = true;
						}
					}
					if(!flag)
						return false;
				}

				//check for right diagonal
				if(col != noOfRows-1) {
					flag = true;
					int diff=noOfRows-1-col;
					int d=row-diff;
					int e=col+diff;
					for(int i=d,j=e;j>col && i<row;j--,i++) {
						int k = noOfRows*i + j;
						//check if there is lizard or tree
						if(currentNode.contains(k)) {
							flag = false;
						}else if(trees.contains(k)) {
							flag = true;
						}
					}
					if(!flag)
						return false;
				}

			}else 
				return false;
		}else {
			//search here when there is no tree in whole desert. (N queen)

			int col = pos%noOfRows;
			int row = pos/noOfRows;
			//check for row
			for(int j=0;j<col;j++) {
				int k = noOfRows*row + j;
				//check if there is a lizard
				if(currentNode.contains(k)) {
					return false;
				}
			}


			//check for column
			for(int j=0;j<row;j++) {
				int k = col + noOfRows*j;
				//check if there is lizard or tree
				if(currentNode.contains(k)) {
					return false;
				}
			}

			//check for left diagonal
			if(col != 0) {
				int d = 0,e=0;
				if(row<col) {e=col-row;}else {d=row-col;}
				for(int i=d,j=e;i<row && j<col;j++,i++) {
					int k = noOfRows*i + j;
					//check if there is lizard or tree
					if(currentNode.contains(k)) {
						return false;
					}
				}
			}


			//check for right diagonal
			if(col != noOfRows-1) {
				int diff=noOfRows-1-col;
				int d=row-diff;
				int e=col+diff;
				for(int i=d,j=e;j>col && i<row;j--,i++) {
					int k = noOfRows*i + j;
					//check if there is lizard or tree
					if(currentNode.contains(k)) {
						return false;
					}
				}
			}


		}
		return true;
	}

}
