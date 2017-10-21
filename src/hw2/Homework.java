package hw2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class Homework {

	char[][]board;
	int boardSize = 0;
	int typesOfFruit = 0;
	double remTime = 0.0;
	String inputFilePath = "E://Assignments//Sem 1//AI//input1.txt";
	String outputFilePath = "E://Assignments//Sem 1//AI//output1.txt";
	String calibrationFilePath = "E://Assignments//Sem 1//AI//calibration.txt";
	long startTime=0;
	int depthLimit = Integer.MAX_VALUE;

	public Homework() {
		// TODO Auto-generated constructor stub
		startTime = System.currentTimeMillis();
		readInput();
		readCalibrationFile();
		buildGameTree();
	}

	public static void main(String args[]) {
		Homework hw = new Homework();

	}

	void readCalibrationFile() {
		//decide depth of search tree
		try {
			BufferedReader br = new BufferedReader(new FileReader(calibrationFilePath));
			try {
				long nodes = Long.parseLong(br.readLine());
				nodes = (long)(nodes/60*(remTime/2))*26*26;
				long bnodes = (long) (nodes/Math.pow(boardSize, 2));
				depthLimit = 0;
				do {
					depthLimit++;
					bnodes = bnodes/boardSize;
				}while(bnodes/boardSize>0);
				if(typesOfFruit>4 && depthLimit>1)
					depthLimit--;
				if(typesOfFruit>5 && boardSize>15 && depthLimit>2)
					depthLimit = 2;
				System.out.println("Depth Limit : "+depthLimit);


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
		Node node = maxValue(new Node(board,0,""), 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
		System.out.println("Best Move : "+node.getMove());
		System.out.println("Cost : "+node.getCost());
		writeToOutputFile(node.getMove());
	}

	void writeToOutputFile(String move) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath, false));
			writer.write(move);
			writer.newLine();
			int col = (int) move.charAt(0)-65;
			int row = Integer.parseInt(move.substring(1))-1;
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
			System.out.println("Running Time : "+totalTime+" ms");
			System.out.println("Running Time : "+(totalTime/1000)+" s");
		}catch(Exception e) {
			e.printStackTrace();
		}

	}

	Node maxValue(Node node,int depth,double alpha,double beta) {
		if(depth > depthLimit) {
			node.setCost(evaluate(node));
			return node;
		}

		char[][] current = node.getBoard();
		HashSet<Integer> set = new HashSet<>();
		Node minNode=null;
		Node bestMove = null;

		for(int row=0;row<boardSize;row++) {
			int rownum = row*boardSize;
			for(int col=0;col<boardSize;col++) {
				char fruit = current[row][col];
				if(fruit != '*' && !set.contains(rownum+col)) {
					HashSet<Integer> adjFruits = findAdjacentFruits(fruit,row,col,current,set);
					char [][] temp = new char[current.length][];
					for(int i = 0; i < current.length; i++)
						temp[i] = current[i].clone();
					selectFruit(temp,adjFruits);

					if(depth==0) {
						node.setMove(getMove(row, col));
					}
					minNode = minValue(new Node(temp, node.getCost()+adjFruits.size(), node.getMove()),depth+1,alpha,beta);

					if ((bestMove == null) || (bestMove.getCost() < minNode.getCost())) {
						bestMove = minNode;
						bestMove.move = getMove(row, col);
					}
					if (minNode.getCost() > alpha) {
						alpha = minNode.getCost();
						bestMove = minNode;
					}
					if (beta <= alpha) {
						bestMove.cost = beta;
						bestMove.move = "";
						return bestMove; // pruning
					}

				}
			}
		}
		if(set.size()==0) {
			//board has no fruit
			return node;
		}
		return bestMove;
	}

	Node minValue(Node node,int depth,double alpha,double beta) {
		if(depth > depthLimit) {
			node.setCost(evaluate(node));
			return node;
		}

		char[][] current = node.getBoard();
		HashSet<Integer> set = new HashSet<>();
		Node maxNode = null;
		Node bestMove = null;

		for(int row=0;row<boardSize;row++) {
			int rownum = row*boardSize;
			for(int col=0;col<boardSize;col++) {
				char fruit = current[row][col];
				if(fruit != '*' && !set.contains(rownum+col)) {
					HashSet<Integer> adjFruits = findAdjacentFruits(fruit,row,col,current,set);

					char [][] temp = new char[current.length][];
					for(int i = 0; i < current.length; i++)
						temp[i] = current[i].clone();
					selectFruit(temp,adjFruits);

					maxNode = maxValue(new Node(temp, node.getCost()-adjFruits.size(), node.getMove()),depth+1,alpha,beta);

					if ((bestMove == null) || (bestMove.getCost() > maxNode.getCost())) {
						bestMove = maxNode;
						bestMove.move = getMove(row, col);
					}
					if (maxNode.getCost() < beta) {
						beta = maxNode.getCost();
						bestMove = maxNode;
					}
					if (beta <= alpha) {
						bestMove.cost = alpha;
						bestMove.move = "";
						return bestMove; // pruning
					}

				}
			}
		}
		if(set.size()==0) {
			//board has no fruit
			return node;
		}
		return bestMove;
	}

	double evaluate(Node node) {
		char[][] prob = node.getBoard();
		int totalFruits = 0;
		HashSet<Character> set = new HashSet<>();
		for(int row=0;row<prob.length;row++) {
			for(int col=0;col<prob.length;col++) {
				if(prob[row][col]!='*') {
					totalFruits++;
					set.add(prob[row][col]);
				}
			}
		}

		//System.out.println("Move : "+node.getMove()+"  Evaluation : "+(node.getCost()+((totalFruits/set.size())/2)));
		if(set.size()>0)
			return node.getCost()+((totalFruits/set.size())/2.0);
		else
			return node.getCost();
	}

	//gives adjacent fruits in row or column

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

	void printMatrix(char[][] node) {
		for(int i=0;i<node.length;i++) {
			for(int j=0;j<node.length;j++) {
				System.out.print(node[i][j]);
			}
			System.out.println();
		}
	}

	String getMove(int row,int col) {
		return (char) (col+65) + "" + (row+1);
	}

	public class Node{
		char[][] board;
		double cost;
		String move;

		public Node(char[][] board, double cost, String move) {
			super();
			this.board = board;
			this.cost = cost;
			this.move = move;
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
		public double getCost() {
			return cost;
		}
		public void setCost(double cost) {
			this.cost = cost;
		}

		int calculateCost(int[][] board) {
			int totalCost=0;
			return totalCost;
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			System.out.println("Cost : "+this.getCost());
			System.out.println("Move : "+this.getMove());
			printMatrix(this.getBoard());
			return super.toString();
		}
	}
}
