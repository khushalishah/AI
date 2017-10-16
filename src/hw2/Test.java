package hw2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class Test {

	static char[][] node;
	int boardSize = 0;
	String inputFilePath = "E://Assignments//Sem 1//AI//input.txt";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Test test = new Test();
		test.readInput();
		HashSet<Integer> set = new HashSet<>();
		test.findAdjacentFruits('1', 0, 8, node, set);
		test.selectFruit(node, set);
		test.printMatrix(node);
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
				int typesOfFruit = Integer.parseInt(line.trim());

				//get remaining time
				line = br.readLine();
				double remTime = Double.parseDouble(line.trim());

				//initialize board
				node = new char[boardSize][boardSize];

				line = br.readLine();    

				//get matrix
				for(int row=0;row<boardSize;row++) {

					for(int col=0;col<boardSize;col++) {
						node[row][col] = line.charAt(col);
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


	void selectFruit(char[][] node,HashSet<Integer> set) {
		HashSet<Integer> cols = new HashSet<>();
		for(int fruit:set) {
			int row = fruit/node.length;
			int col = fruit%node.length;
			cols.add(col);
			node[row][col] = '*';
		}
		System.out.println(cols);
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



}
