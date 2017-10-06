package hw2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import hw1.Homework4;

public class Homework {

	char[][]board;
	int boardSize = 0;
	int typesOfFruit = 0;
	double remTime = 0.0;
	String inputFilePath = "E://Assignments//Sem 1//AI//input.txt";
	String outputFilePath = "E://Assignments//Sem 1//AI//output.txt";
	Queue<char[][]> queue = new LinkedList<>();
	HashSet<Integer> set = new HashSet<>();
	long startTime=0;
	int depthLimit = 0;

	public static void main(String args[]) {
		Homework hw = new Homework();
		hw.startTime = System.currentTimeMillis();
		hw.readInput();
		hw.buildGameTree();
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

	void buildGameTree(){
		queue.add(board);
		while (!queue.isEmpty()) {
			char[][] current = queue.remove();
			set.clear();

			for(int row=0;row<boardSize;row++) {
				int rownum = row*boardSize;
				for(int col=0;col<boardSize;col++) {
					char fruit = current[row][col];
					if(fruit != '*' && !set.contains(rownum+col)) {
						HashSet<Integer> adjFruits = findAdjacentFruits(fruit,row,col,current);
						//System.out.println(set);
						//System.out.println(adjFruits);
						//printMatrix(current);
						selectFruit(current,adjFruits);
						//printMatrix(current);
						queue.add(current);

					}
				}
			}
		}
	}

	//gives adjacent fruits in row or column
	HashSet<Integer> findAdjacentFruits(char fruit,int row,int col,char[][] node) {
		int num = row*boardSize+col;
		HashSet<Integer> adjFruits = new HashSet<>();
		if(!set.contains(num)) {
			set.add(num);
			adjFruits.add(num);
			//check upper element
			if(row!=0) {
				if(node[row-1][col]==fruit) {
					adjFruits.addAll(findAdjacentFruits(fruit, row-1, col, node));
				}
			}
			//check left element
			if(col!=0) {
				if(node[row][col-1]== fruit) {
					adjFruits.addAll(findAdjacentFruits(fruit, row, col-1, node));
				}
			}
			//check right element
			if(col!=boardSize-1) {
				if(node[row][col+1] == fruit) {
					adjFruits.addAll(findAdjacentFruits(fruit, row, col+1, node));
				}
			}
			//check lower element
			if(row!=boardSize-1) {
				if(node[row+1][col] == fruit) {
					adjFruits.addAll(findAdjacentFruits(fruit, row+1, col, node));
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
		for(int col:cols) {
			int noOfStars = 0;
			int startRow = -1,endrow=-1;
			for(int row=boardSize-1;row>=0;row--) {
				if(node[row][col] == '*') {
					if(startRow == -1) {
						startRow = row;
					}
					noOfStars++;
					startRow = row;
				}else if(noOfStars!=0) {
					endrow=row;
					break;
				}
			}

			if(endrow!=-1)
				for(int i=startRow;i>=endrow;i--) {
					char temp = node[i][col];
					node[i][col] = node[i-noOfStars][col];
					node[i-noOfStars][col] = temp;
				}

		}
	}

	void printMatrix(char[][] node) {
		for(int i=0;i<boardSize;i++) {
			for(int j=0;j<boardSize;j++) {
				System.out.print(node[i][j]);
			}
			System.out.println();
		}
	}

	class Node{
		int[][] board;
		int cost;
		public int[][] getBoard() {
			return board;
		}
		public void setBoard(int[][] board) {
			this.board = board;
		}
		public int getCost() {
			return cost;
		}
		public void setCost(int cost) {
			this.cost = cost;
		}

		int calculateCost(int[][] board) {
			int totalCost=0;
			return totalCost;
		}
	}
}
