package hw2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class RandomAgent {
	
	char[][]board;
	int boardSize = 0;
	int typesOfFruit = 0;
	double remTime = 0.0;
	String inputFilePath = "E://Assignments//Sem 1//AI//input2.txt";
	String outputFilePath = "E://Assignments//Sem 1//AI//output2.txt";
	long startTime=0;
	String bestMove = "";
	ArrayList<Integer> moves = new ArrayList<>();
	
	public RandomAgent() {
		// TODO Auto-generated constructor stub
		startTime = System.currentTimeMillis();
		readInput();
		run();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RandomAgent agent = new RandomAgent();
		
		
	}
	
	void run() {
			int randomMove = moves.get((int) new Random().nextInt(moves.size()));
			int row = randomMove/boardSize;
			int col = randomMove%boardSize;
			writeToOutputFile(row,col);
	}
	
	void readInput() {

		//read input file
		try {
			BufferedReader br = new BufferedReader(new FileReader(inputFilePath));
			try {
				String line = br.readLine();


				//get size of board
				boardSize = Integer.parseInt(line.trim());

				//get types of fruit
				line = br.readLine();
				typesOfFruit = Integer.parseInt(line.trim());

				//get remaining time
				line = br.readLine();
				remTime = Double.parseDouble(line.trim());

				//initialize board
				board = new char[boardSize][boardSize];

				line = br.readLine();    

				//get matrix
				for(int row=0;row<boardSize;row++) {

					for(int col=0;col<boardSize;col++) {
						board[row][col] = line.charAt(col);
						if(board[row][col] != '*') {
							moves.add(row*boardSize+col);
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

	
	void writeToOutputFile(int row,int col) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath, false));
			System.out.println("Random Move : "+getMove(row, col));
			writer.write(getMove(row, col));
			writer.newLine();
			HashSet<Integer> set = new HashSet<>();
			findAdjacentFruits(board[row][col], row, col, board, set);
			selectFruit(board, set);


			//print matrix to file
			for(int i=0;i<boardSize;i++) {
				for(int j=0;j<boardSize;j++) {
					writer.write(board[i][j]);
				}
				writer.newLine();
			}
			writer.close();
			long totalTime = System.currentTimeMillis()-startTime;
			System.out.println("Running Time : "+totalTime+" ns");
			System.out.println("Running Time : "+(totalTime/1000000)+" ms");
			System.out.println("Running Time : "+(totalTime/1000000000)+" s");
		}catch(Exception e) {
			e.printStackTrace();
		}

	}
	
	String getMove(int row,int col) {
		return (char) (col+65) + "" + (row+1);
	}
	
	HashSet<Integer> findAdjacentFruits(char fruit,int row,int col,char[][] node,HashSet<Integer> set) {
		int num = row*boardSize+col;
		HashSet<Integer> adjFruits = new HashSet<>();
		if(!set.contains(num)) {
			set.add(num);
			adjFruits.add(num);
			//check upper element
			if(row!=0) {
				if(node[row-1][col]==fruit) {
					adjFruits.addAll(findAdjacentFruits(fruit, row-1, col, node,set));
				}
			}
			//check left element
			if(col!=0) {
				if(node[row][col-1]== fruit) {
					adjFruits.addAll(findAdjacentFruits(fruit, row, col-1, node,set));
				}
			}
			//check right element
			if(col!=boardSize-1) {
				if(node[row][col+1] == fruit) {
					adjFruits.addAll(findAdjacentFruits(fruit, row, col+1, node,set));
				}
			}
			//check lower element
			if(row!=boardSize-1) {
				if(node[row+1][col] == fruit) {
					adjFruits.addAll(findAdjacentFruits(fruit, row+1, col, node,set));
				}
			}
		}
		return adjFruits;
	}

	//select fruit and remove adjacent fruits
	void selectFruit(char[][] node,HashSet<Integer> set) {
		HashSet<Integer> cols = new HashSet<>();
		for(int fruit:set) {
			int row = fruit/boardSize;
			int col = fruit%boardSize;
			cols.add(col);
			node[row][col] = '*';
		}
		applyGravity(node,cols);
	}

	//this function will apply gravity to board
	void applyGravity(char[][] node,HashSet<Integer> cols) {
		Queue<Character> numbers = new LinkedList<>();
		for(int col:cols) {
			int noOfStars = 0;
			int startRow = -1;
			for(int row=node.length-1;row>=0;row--) {
				if(node[row][col] == '*') {
					if(startRow == -1) {
						startRow = row;
					}
					noOfStars++;
				}else if(noOfStars!=0) {
					numbers.add(node[row][col]);
				}
			}

			for(int i=startRow;i>=0;i--) {
				if(!numbers.isEmpty())
					node[i][col] = numbers.poll();
				else
					node[i][col] = '*';
			}

		}
	}



}
