package hw1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Random;

public class SA1 {

	int noOfTrees = 0;
	int noOfLizards = 0;
	int noOfRows = 0;
	int noOfCols = 0;
	int[][] matrix;
	String inputFilePath = "E://Assignments//Sem 1//AI//input.txt";
	String outputFilePath = "E://Assignments//Sem 1//AI//output.txt";
	static long startTime = 0;

	public static void main(String args[]) {
		startTime = System.nanoTime();
		SA1 sa = new SA1();
		sa.readInput();
		sa.runAlgo();
	}

	void readInput() {
		//read input file
		try {
			BufferedReader br = new BufferedReader(new FileReader(inputFilePath));
			try {
				String line = br.readLine();

				//get number of rows and columns
				line = br.readLine();
				noOfRows = Integer.parseInt(line.trim());
				noOfCols = noOfRows;

				//get number of lizards
				line = br.readLine();
				noOfLizards = Integer.parseInt(line.trim());

				line = br.readLine();    
				matrix = new int[noOfRows][noOfRows];

				//get matrix
				for(int row=0;row<noOfRows;row++) {
					for(int col=0;col<noOfRows;col++) {
						int bit = Integer.parseInt(String.valueOf(line.charAt(col)));
						matrix[row][col] = bit;
						//check if it is a tree
						if(bit == 2) {
							noOfTrees++;
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

	void writeToOutputFile(boolean result,int[][] finalNode) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath, false));
			if(result == true) {
				System.out.println("OK");
				writer.write("OK");
				writer.newLine();

				printMatrix(finalNode);
				
				for(int row=0;row<noOfRows;row++) {
					for(int col=0;col<noOfRows;col++) {
						writer.write(""+finalNode[row][col]);
					}
					writer.newLine();
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
	
	void runAlgo() {
		if(noOfTrees == 0 && noOfLizards > noOfRows) {
			//test case will fail
			writeToOutputFile(false, null);
		}else {
			runSA();
		}
	}
	
	void runSA() {
		generateRandomPositions();
		int d = 5;
		double temp = d;
		int noOfIterations = 1;
		int cost = 0;
		double probability,random;
		int nextStateConflicts = 0,currentStateConflicts=0;
		long startTime = System.currentTimeMillis();
		do {
			int[][] nextSolution = generateNextSolution();
			currentStateConflicts = calculateCost(matrix);
			nextStateConflicts  = calculateCost(nextSolution);
			cost = currentStateConflicts - nextStateConflicts;
			probability = Math.exp(cost/temp);
			random = Math.random();
			if(cost > 0) {
				matrix = nextSolution;
			}else if(probability > random) {
				matrix = nextSolution;
			}
			noOfIterations++;
			temp = d / (Math.log(noOfIterations + 3));
		}while(temp > 0 && nextStateConflicts != 0 && System.currentTimeMillis() - startTime <= 295000);
		
		if(nextStateConflicts==0) {
			writeToOutputFile(true, matrix);
		}else {
			writeToOutputFile(false, null);
		}
	}

	void generateRandomPositions() {
		int placedLizards = 0;
		for(int i=0; i<noOfRows; i++) {
			int col = (int)(new Random().nextInt(noOfRows));
			if(matrix[i][col]==0) {
				matrix[i][col]=1;
				placedLizards++;
			}
		}
		while(placedLizards != noOfLizards) {
			int row = (int)(new Random().nextInt(noOfRows));
			int col = (int)(new Random().nextInt(noOfRows)); 
			if(matrix[row][col]==0) {
				matrix[row][col]=1;
				placedLizards++;
			}
		}
	}

	
	int[][] generateNextSolution(){
		//randomly pick one lizard
		int[][] next = new int[noOfRows][noOfRows];
		for(int i = 0; i < noOfRows; i++)
			next[i] = matrix[i].clone();
		boolean repeat = true;
		while(repeat) {
			int row = new Random().nextInt(noOfRows);
			for(int i=0;i<noOfCols;i++) {
				if(next[row][i]==1) {
					int newrow = new Random().nextInt(noOfRows);
					int newcol = new Random().nextInt(noOfCols);
					if(newcol!=i && newrow != row && next[newrow][newcol]==0) {
						next[newrow][newcol] = 1;
						next[row][i] = 0;
						repeat = false;
						break;
					}
				}
			}
		}
		return next;
	}
	
	/*int[][] generateNextSolution(){
		//randomly pick one lizard
		int[][] next = new int[noOfRows][noOfRows];
		for(int i = 0; i < noOfRows; i++)
			next[i] = matrix[i].clone();
		boolean repeat = true;
		while(repeat) {
			int row = new Random().nextInt(noOfRows);
			for(int i=0;i<noOfCols;i++) {
				if(next[row][i]==1) {
					int col = new Random().nextInt(noOfCols);
					if(col!=i && next[row][col]==0) {
						next[row][col] = 1;
						next[row][i] = 0;
						repeat = false;
						break;
					}
				}
			}
		}
		return next;
	}*/

	int calculateCost(int[][] node) {
		int cost = 0;
		//check in left row
		for(int row=0;row<noOfRows;row++) {
			for(int col=0;col<noOfRows;col++) {
				if(node[row][col]==1) {
					//check attacks for lizards
					//left row
					for(int i=col-1;i>=0;i--) {
						if(node[row][i]==2) {
							break;
						}else if(node[row][i]==1) {
							cost++;
						}
					}

					//right row
					for(int i=col+1;i<noOfRows;i++) {
						if(node[row][i]==2) {
							break;
						}else if(node[row][i]==1) {
							cost++;
						}
					}

					//upper column
					for(int i=row-1;i>=0;i--) {
						if(node[i][col]==2) {
							break;
						}else if(node[i][col]==1) {
							cost++;
						}
					}

					//lower column
					for(int i=row+1;i<noOfRows;i++) {
						if(node[i][col]==2) {
							break;
						}else if(node[i][col]==1) {
							cost++;
						}
					}

					//for left upper column
					for(int i=row-1,j=col-1;i>=0 && j>=0;i--,j--) {
						if(node[i][j]==2) {
							break;
						}else if(node[i][j]==1) {
							cost++;
						}
					}

					//for left lower column
					for(int i=row+1,j=col+1;i<noOfRows && j<noOfRows;i++,j++) {
						if(node[i][j]==2) {
							break;
						}else if(node[i][j]==1) {
							cost++;
						}
					}

					//for right upper column
					for(int i=row-1,j=col+1;i>=0 && j<noOfRows;i--,j++) {
						if(node[i][j]==2) {
							break;
						}else if(node[i][j]==1) {
							cost++;
						}
					}

					//for right lower column
					for(int i=row+1,j=col-1;i<noOfRows && j>=0;i++,j--) {
						if(node[i][j]==2) {
							break;
						}else if(node[i][j]==1) {
							cost++;
						}
					}
				}
			}
		}
		return cost;
	}
	
	void printMatrix(int[][] node) {
		for(int i=0;i<noOfRows;i++) {
			for(int j=0;j<noOfRows;j++) {
				System.out.print(node[i][j]);
			}
			System.out.println();
		}
	}
}
