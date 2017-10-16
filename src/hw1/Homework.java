package hw1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;
import java.util.TreeSet;

public class Homework {

	int algo = 0; //BFS - 0, DFS - 1, SA - 2
	String inputFilePath = "E://Assignments//Sem 1//AI//input.txt";
	String outputFilePath = "E://Assignments//Sem 1//AI//output.txt";
	int noOfRows = 0;
	int noOfLizards = 0;
	Queue<TreeSet<Integer>> queue = new LinkedList<>();
	Stack<TreeSet<Integer>> stack = new Stack<>();
	TreeSet<Integer> trees = new TreeSet<>();
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

			//select algorithm to run
			if(algo == 0) {
				runBFS();
			}else if (algo==1) {
				runDFS();
			}else {
				runSA();
			}
		}
	}

	void writeToOutputFile(boolean result,TreeSet<Integer> finalNode) {
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
		//insert first node to queue
		queue.add(new TreeSet<Integer>());
		
		if(trees.size()==0) {
			runNQueenBFS();
		}else {
			boolean solFound = false;
			mainLoop : while (!queue.isEmpty()) {
				TreeSet<Integer> currentNode = queue.remove();
				boolean isSet = false;
				int start = 0;
				if(currentNode.size()>0) {
					start = currentNode.last()+1;
				}
				for(int i=start;i<totalPlaces;i++) {
					if(isSafe(i,currentNode)) {
						//place lizard here
						TreeSet<Integer> newNode = new TreeSet<>(currentNode);
						newNode.add(i);
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

					}
					//check density of trees
					if(isSet && !isDense)
						if(i%noOfRows == noOfRows-1) 
							break;

				}
			}
			if(!solFound) {writeToOutputFile(false, null);}
		}
	}

	void runDFS() {
		boolean solFound = false;
		//add node to stack
		stack.add(new TreeSet<Integer>());
		if(trees.size()==0) {
			runNQueenDFS();
		}else {
			mainLoop : while (!stack.isEmpty()) {
				TreeSet<Integer> currentNode = stack.pop();
				boolean isSet = false;
				int start = 0;
				if(currentNode.size()>0) {
					start = currentNode.last()+1;
				}
				for(int i=start;i<totalPlaces;i++) {
					if(isSafe(i,currentNode)) {
						//place lizard here
						TreeSet<Integer> newNode = new TreeSet<>(currentNode);
						newNode.add(i);
						isSet = true;

						//check if goal state has reached or not
						if(newNode.size()==noOfLizards) {
							//goal state has reached
							solFound = true;
							writeToOutputFile(true, newNode);
							break mainLoop;
						}
						//add new node to queue
						stack.add(newNode);

					}
					//check density of trees
					if(isSet && !isDense)
						if(i%noOfRows == noOfRows-1) 
							break;

				}
			}
		if(!solFound) {writeToOutputFile(false, null);}
		}

	}

	void runSA() {
		//place lizards randomly in zoo
		TreeSet<Integer> currentSolution = generateRandomPositions();
		double startingTemp = 5;		//starting temperature
		double temp = startingTemp;
		int iterations = 1;
		int delta=0;
		double probability,random;
		int nextStateConflicts = 0,currentStateConflicts=0;
		long startTime = System.currentTimeMillis();
		do {
			//calculate neighbor node
			TreeSet<Integer> nextSolution = generateNextSolution(currentSolution);
			nextStateConflicts  = calculateCost(nextSolution);
			currentStateConflicts = calculateCost(currentSolution);
			delta = currentStateConflicts-nextStateConflicts;
			probability = Math.exp(delta/temp);
			random = Math.random();

			if(delta>0) {
				//accept solution
				currentSolution = nextSolution;
			}else if(random<=probability) {
				//accept solution with probability
				currentSolution = nextSolution;
			}
			
			iterations++;
			temp = startingTemp/(Math.log(iterations+3));
		}while(temp>0 && nextStateConflicts != 0 && System.currentTimeMillis()-startTime<=285000);
		if(nextStateConflicts==0) {
			writeToOutputFile(true, currentSolution);
		}else {
			writeToOutputFile(false, null);
		}
	}

	TreeSet<Integer> generateRandomPositions() {
		int placedLizards = 0;
		TreeSet<Integer> queens = new TreeSet<>();
		while(placedLizards != noOfLizards) {
			int pos = (int)(new Random().nextInt(totalPlaces)); 
			if(!trees.contains(pos) && !queens.contains(pos)) {
				queens.add(pos);
				placedLizards++;
			}
		}
		return queens;
	}

	TreeSet<Integer> generateNextSolution(TreeSet<Integer> currentSolution){
		//randomly pick one lizard
		TreeSet<Integer> nextSol = new TreeSet<>(currentSolution);
		int lizard = new Random().nextInt(noOfLizards);
		int i=0;
		int oldPos=0;
		for(int q:nextSol) {
			if(i==lizard) {
				oldPos = q;
				break;
			}
			i++;
		}
		nextSol.remove(oldPos);
		//pick one position randomly
		boolean repeat = true;
		while(repeat) {
			int pos = new Random().nextInt(totalPlaces);
			if(!trees.contains(pos) && !nextSol.contains(pos) && pos!=oldPos) {
				nextSol.add(pos);
				repeat = false;
			}
		}

		return nextSol;
	}

	int calculateCost(TreeSet<Integer> queens) {
		int cost = 0;
		//check in row
		for(int q:queens) {
			int row = q/noOfRows;
			int col = q%noOfRows;

			//check conflicts in left row
			for(int i=col-1;i>=0;i--) {
				int k = noOfRows*row + i;
				if(trees.contains(k)) {
					break;
				}else if(queens.contains(k)) {
					cost++;
				}
			}

			//check conflicts in right row
			for(int i=col+1;i<noOfRows;i++) {
				int k = noOfRows*row + i;
				if(trees.contains(k)) {
					break;
				}else if(queens.contains(k)) {
					cost++;
				}
			}

			//check conflict in upper column
			for(int i=row-1;i>=0;i--) {
				int k = noOfRows*i + col;
				if(trees.contains(k)) {
					break;
				}else if(queens.contains(k)) {
					cost++;
				}
			}

			//check conflicts in lower column
			for(int i=row+1;i<noOfRows;i++) {
				int k = noOfRows*i + col;
				if(trees.contains(k)) {
					break;
				}else if(queens.contains(k)) {
					cost++;
				}
			}

			//check conflicts in left upper diagonal
			for(int i=row-1,j=col-1;i>=0 && j>=0;i--,j--) {
				int k = noOfRows*i+j;
				if(trees.contains(k)) {
					break;
				}else if(queens.contains(k)) {
					cost++;
				}
			}

			//check conflicts in left lower diagonal
			for(int i=row+1,j=col+1;i<noOfRows && j<noOfRows;i++,j++) {
				int k = noOfRows*i+j;
				if(trees.contains(k)) {
					break;
				}else if(queens.contains(k)) {
					cost++;
				}
			}

			//check conflicts in right upper diagonal
			for(int i=row-1,j=col+1;i>=0 && j<noOfRows;i--,j++) {
				int k = noOfRows*i+j;
				if(trees.contains(k)) {
					break;
				}else if(queens.contains(k)) {
					cost++;
				}
			}

			//check conflicts in right lower diagonal
			for(int i=row+1,j=col-1;i<noOfRows && j>=0;i++,j--) {
				int k = noOfRows*i+j;
				if(trees.contains(k)) {
					break;
				}else if(queens.contains(k)) {
					cost++;
				}
			}
		}
		return cost;
	}


	boolean isSafe(int pos,TreeSet<Integer> currentNode) {
		if(!trees.contains(pos)) {
			int col = pos%noOfRows;
			int row = pos/noOfRows;
			//check for row
			for(int j=col-1;j>=0;j--) {
				int k = noOfRows*row + j;
				//check if there is a lizard
				if(currentNode.contains(k)) {
					return false;
				}else if(trees.contains(k)) {
					break;
				}
			}

			//check for column
			for(int j=row-1;j>=0;j--) {
				int k = noOfRows*j + col;
				//check if there is lizard or tree
				if(currentNode.contains(k)) {
					return false;
				}else if(trees.contains(k)) {
					break;
				}
			}

			//check for left diagonal
			for(int i=row-1,j=col-1;i>=0 && j>=0;i--,j--) {
				int k = noOfRows*i + j;
				//check if there is lizard or tree
				if(currentNode.contains(k)) {
					return false;
				}else if(trees.contains(k)) {
					break;
				}
			}

			//check for right diagonal
			for(int i=row-1,j=col+1;i>=0 && j<noOfRows;i--,j++) {
				int k = noOfRows*i + j;
				//check if there is lizard or tree
				if(currentNode.contains(k)) {
					return false;
				}else if(trees.contains(k)) {
					break;
				}
			}

		}else 
			return false;

		return true;
	}

	void runNQueenBFS() {
		boolean solFound = false;
		mainLoop : while(!queue.isEmpty()) {
			TreeSet<Integer> currentNode = queue.poll();
			int lastRow = -1;
			if(currentNode.size()!=0) {
				lastRow = currentNode.last()/noOfRows;
			}
			for(int i=0;i<noOfRows;i++) {
				int k = noOfRows*(lastRow+1)+i;
				if(isSafeForNQueen(k,currentNode)) {
					TreeSet<Integer> newNode = new TreeSet<>(currentNode);
					newNode.add(k);

					//check if goal state has reached or not
					if(newNode.size()==noOfLizards) {
						//goal state has reached
						solFound = true;
						writeToOutputFile(true, newNode);
						break mainLoop;
					}
					//add new node to queue
					queue.add(newNode);
				}
			}
		}
		if(!solFound) {writeToOutputFile(false, null);}

	}

	void runNQueenDFS() {
		boolean solFound = false;
		mainLoop : while(!stack.isEmpty()) {
			TreeSet<Integer> currentNode = stack.pop();
			//System.out.println(currentNode);
			int lastRow = -1;
			if(currentNode.size()!=0) {
				lastRow = currentNode.last()/noOfRows;
			}
			for(int i=0;i<noOfRows;i++) {
				int k = noOfRows*(lastRow+1)+i;
				if(isSafeForNQueen(k,currentNode)) {
					TreeSet<Integer> newNode = new TreeSet<>(currentNode);
					newNode.add(k);
					//System.out.println(newNode);

					//check if goal state has reached or not
					if(newNode.size()==noOfLizards) {
						//goal state has reached
						solFound = true;
						writeToOutputFile(true, newNode);
						break mainLoop;
					}
					//add new node to queue
					stack.add(newNode);
				}
			}
		}
		if(!solFound) {writeToOutputFile(false, null);}

	}

	boolean isSafeForNQueen(int pos,TreeSet<Integer> currentNode) {
		//search here when there is no tree in whole desert. (N queen)

		int col = pos%noOfRows;
		int row = pos/noOfRows;

		//check for column
		for(int j=0;j<row;j++) {
			int k = col + noOfRows*j;
			//check if there is lizard
			if(currentNode.contains(k)) {
				return false;
			}
		}

		//check for left diagonal
		for(int i=row,j=col;i>=0 && j>=0;i--,j--) {
			int k = noOfRows*i + j;
			if(currentNode.contains(k)) {
				return false;
			}
		}


		//check for right diagonal
		for(int i=row,j=col;i>=0 && j<noOfRows;i--,j++) {
			int k= noOfRows*i+j;
			if(currentNode.contains(k)) {
				return false;
			}
		}
		return true;
	}

}
