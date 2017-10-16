package hw2;

import java.util.HashSet;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		char[][] node = {{'*','1','0'},{'*','2','2'},{'*','0','2'}};
		HashSet<Integer> set = new HashSet<>();
		set.add(4);
		set.add(5);
		set.add(8);
		new Test().selectFruit(node,set);
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
		printMatrix(node);
		applyGravity(node,cols);
		printMatrix(node);
	}

	//this function will apply gravity to board
	void applyGravity(char[][] node,HashSet<Integer> cols) {
		for(int col:cols) {
			int noOfStars = 0;
			int startRow = -1,endrow=-1;
			for(int row=node.length-1;row>=0;row--) {
				if(node[row][col] == '*') {
					if(startRow == -1) {
						startRow = row;
					}
					noOfStars++;
				}else if(noOfStars!=0) {
					endrow=row;
					break;
				}
			}

			if(endrow!=-1)
				for(int i=startRow;i>=endrow && i-noOfStars>=0;i--) {
					char temp = node[i][col];
 					node[i][col] = node[i-noOfStars][col];
					node[i-noOfStars][col] = temp;
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
