package hw2;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Calibrate {
	
	String calibrationFilePath = "E://Assignments//Sem 1//AI//calibration.txt";
	Queue<Node> queue = new LinkedList<>();
	long startTime = 0;
	int depth = 0;
	long totalNodes = 0;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Calibrate calibrate = new Calibrate();
		char[][] b26 = calibrate.initializaBoard(26);
		System.out.println("Board Size : 26");
		calibrate.runAlgo(b26);
	}
	
	char[][] initializaBoard(int size){
		char[][] board = new char[size][size];
		
		for(int i=0;i<board.length;i++) {
			for(int j=0;j<board.length;j++) {
				int col = new Random().nextInt(10);
				board[i][j] = (char) (col+'0');
			}
		}
		return board;
	}
	
	void runAlgo(char[][] board) {
		totalNodes = 0;
		queue.clear();
		startTime = System.currentTimeMillis();
		queue.add(new Node(board, 0));
		while(!queue.isEmpty() && (System.currentTimeMillis()-startTime<=60000)) {
			Node node = queue.poll();
			totalNodes++;
			char[][] current = node.getBoard();
			
			for(int row=0;row<current.length;row++) {
				for(int col=0;col<current.length;col++) {
					queue.add(new Node(board, node.getDepth()+1));
					depth = node.getDepth()+1;
				}
			}
		}
		writeToOutputFile(totalNodes);
	}
	
	void writeToOutputFile(long nodes) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(calibrationFilePath, false));
			writer.write(""+nodes);
			writer.close();
		}catch(Exception e) {
			e.printStackTrace();
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
	
	public class Node{
		char[][] board;
		int depth;

		public Node(char[][] board, int depth) {
			super();
			this.board = board;
			this.depth = depth;
		}

		public int getDepth() {
			return depth;
		}



		public void setDepth(int depth) {
			this.depth = depth;
		}



		public char[][] getBoard() {
			return board;
		}
		public void setBoard(char[][] board) {
			this.board = board;
		}

		int calculateCost(int[][] board) {
			int totalCost=0;
			return totalCost;
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			printMatrix(this.getBoard());
			return super.toString();
		}
	}

}
