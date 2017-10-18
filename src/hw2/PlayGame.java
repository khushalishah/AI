package hw2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashSet;

public class PlayGame {

	int p1Score = 0, p2Score = 0;
	double p1Time = 300.0, p2Time = 300.0;
	char[][] board;
	String inputFilePath1 = "E://Assignments//Sem 1//AI//input1.txt";
	String outputFilePath1 = "E://Assignments//Sem 1//AI//output1.txt";
	String inputFilePath2 = "E://Assignments//Sem 1//AI//input2.txt";
	String outputFilePath2 = "E://Assignments//Sem 1//AI//output2.txt";
	String gameFilePath = "E://Assignments//Sem 1//AI//game.txt";
	int boardSize = 0;
	int fruitTypes = 0;

	public PlayGame(int size,int typesOfFruit) {
		// TODO Auto-generated constructor stub
		initializaBoard();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PlayGame game = new PlayGame(10,5);
		game.run();
	}

	void writeInputFile(String filename,double time) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename, false));
			writer.write(boardSize+"");
			writer.newLine();
			writer.write(fruitTypes+"");
			writer.newLine();
			writer.write(time+"");
			writer.newLine();


			//print matrix to file
			for(int i=0;i<boardSize;i++) {
				for(int j=0;j<boardSize;j++) {
					writer.write(board[i][j]);
				}
				writer.newLine();
			}
			writer.close();
		}catch(Exception e) {
			e.printStackTrace();
		}

	}

	void run() {
		boolean opponent = false;
		boolean timeOut = false;
		while(!isGameOver()) {
			if(!opponent) {
				if(p1Time<0) {
					timeOut = true;
					break;
				}

				writeInputFile(inputFilePath1,p1Time);
				long startTime = System.currentTimeMillis();
				new Homework();
				p1Time = p1Time - (System.currentTimeMillis()- startTime)/1000;
				System.out.println(System.currentTimeMillis()-startTime);
				readOutputFile(outputFilePath1, 0);
				opponent = true;
			}else {
				if(p2Time<0)
					break;
				writeInputFile(inputFilePath2, p2Time);
				long startTime = System.currentTimeMillis();
				new MinMaxAgent();
				//new RandomAgent();
				p2Time -= (System.currentTimeMillis() - startTime)/1000;
				System.out.println(System.currentTimeMillis()-startTime);
				readOutputFile(outputFilePath2, 1);
				opponent = false;
			}
		}
		System.out.println("P1 Score : "+p1Score);
		System.out.println("P2 Score : "+p2Score);
		if(timeOut) {
			System.out.println("P1 : Timeout");
			System.out.println("P2 is winner");
		}else {
			if(p1Score>p2Score) {
				System.out.println("P1 is winner");
			}else if(p2Score>p1Score){
				System.out.println("P2 is winner");
			}else {
				if(p1Time>p2Time) {
					System.out.println("P1 is winner");
				}else{
					System.out.println("P2 is winner");
				}
			}
		}
	}

	void readOutputFile(String filename,int player) {
		//read input file
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			try {
				String line = br.readLine();
				int col = (int) line.charAt(0)-65;
				int row = Integer.parseInt(line.substring(1))-1;
				HashSet<Integer> set = new HashSet<>();
				findAdjacentFruits(board[row][col], row, col, board, set);

				if(player == 0) {
					p1Score += set.size();
				}else {
					p2Score += set.size();
				}

				line = br.readLine();    

				//get matrix
				for(int i=0;i<boardSize;i++) {

					for(int j=0;j<boardSize;j++) {
						board[i][j] = line.charAt(j);
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


	boolean isGameOver() {
		boolean isGameOver = true;
		for(int row=0;row<board.length;row++) {
			for(int col=0;col<board.length;col++) {
				if(board[row][col]!='*') {
					isGameOver = false;
					break;
				}
			}
		}
		return isGameOver;
	}

	void initializaBoard(){
		//read input file
		try {
			BufferedReader br = new BufferedReader(new FileReader(gameFilePath));
			try {
				String line = br.readLine();


				//get size of board
				boardSize = Integer.parseInt(line.trim());

				//get types of fruit
				line = br.readLine();
				fruitTypes = Integer.parseInt(line.trim());

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


}
