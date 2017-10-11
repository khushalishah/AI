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
	Queue<Node> queue = new LinkedList<>();
	HashSet<Integer> set = new HashSet<>();
	long startTime=0;
	int depthLimit = 3;

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
		queue.add(new Node(board,0,"",true,0));
		while (!queue.isEmpty()) {
			Node node = queue.remove();
			if(node.getDepth()!=depthLimit) {
				char[][] current = node.getBoard();
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
							queue.add(new Node(current, node.isOpponent?node.getCost()+adjFruits.size():node.getCost()-adjFruits.size(), getMove(row, col), node.isOpponent()?false:true,node.getDepth()+1));

						}
					}
				}
			}else {
				queue.add(node);
				break;
			}
		}
		runMinMax();
	}
	
	//run min max algorithm on leaf nodes
	void runMinMax() {
		
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

	String getMove(int row,int col) {
		return (char) (col+64) + "" + (row+1);
	}

	class Node{
		char[][] board;
		int cost;
		String move;
		boolean isOpponent;
		int depth;

		public int getDepth() {
			return depth;
		}

		public void setDepth(int depth) {
			this.depth = depth;
		}

		public Node(char[][] board, int cost, String move, boolean isOpponent,int depth) {
			super();
			this.board = board;
			this.cost = cost;
			this.move = move;
			this.isOpponent = isOpponent;
			this.depth = depth;
		}

		public boolean isOpponent() {
			return isOpponent;
		}
		public void setOpponent(boolean isOpponent) {
			this.isOpponent = isOpponent;
		}
		public String getMove() {
			return move;
		}
		public void setMove(String move) {
			this.move = move;
		}
		public char[][] getBoard() {
			return board;
		}
		public void setBoard(char[][] board) {
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
