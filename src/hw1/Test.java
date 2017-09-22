package hw1;

import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeSet;

public class Test {
	
	int noOfRows = 0;
	TreeSet<Integer> trees = new TreeSet<>();
	
	public static void main(String args[]) {
		Test t = new Test();
		t.noOfRows = 7;
		TreeSet<Integer> tree = new TreeSet<>();
		tree.add(8);
		System.out.println(t.isSafeNew(15, tree ));
	}
	
	boolean isSafeNew(int pos,TreeSet<Integer> currentNode) {
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
					return true;
				}
			}

			//check for column
			for(int j=row-1;j>=0;j--) {
				int k = col + noOfRows*j;
				//check if there is lizard or tree
				if(currentNode.contains(k)) {
					return false;
				}else if(trees.contains(k)) {
					return true;
				}
			}

			//check for left diagonal
			for(int i=row-1,j=col-1;i>=0 && j>=0;i--,j--) {
				int k = noOfRows*i + j;
				//check if there is lizard or tree
				if(currentNode.contains(k)) {
					return false;
				}else if(trees.contains(k)) {
					return true;
				}
			}

			//check for right diagonal
			for(int i=row-1,j=col+1;i>=0 && j<noOfRows;i--,j++) {
				int k = noOfRows*i + j;
				//check if there is lizard or tree
				if(currentNode.contains(k)) {
					return false;
				}else if(trees.contains(k)) {
					return true;
				}
			}

		}else 
			return false;

		return true;
	}

	
	boolean isSafe(int pos,TreeSet<Integer> currentNode) {
		//if(trees.size() !=0) {
			if(!trees.contains(pos)) {
				int col = pos%noOfRows;
				int row = pos/noOfRows;
				boolean flag = true;
				//check for row
				for(int j=0;j<col;j++) {
					int k = noOfRows*row + j;
					//check if there is a lizard
					if(currentNode.contains(k)) {
						flag = false;
					}else if(trees.contains(k)) {
						flag = true;
					}
				}
				if(!flag)
					return false;

				//check for column
				flag = true;
				for(int j=0;j<row;j++) {
					int k = col + noOfRows*j;
					//check if there is lizard or tree
					if(currentNode.contains(k)) {
						flag = false;
					}else if(trees.contains(k)) {
						flag = true;
					}
				}
				if(!flag)
					return false;

				//check for left diagonal
				if(col!=0) {
				flag = true;
				int d = 0,e=0;
				if(row<col) {e=col-row;}else {d=row-col;}
				for(int i=d,j=e;i<row && j<col;j++,i++) {
					int k = noOfRows*i + j;
					//check if there is lizard or tree
					if(currentNode.contains(k)) {
						flag = false;
					}else if(trees.contains(k)) {
						flag = true;
					}
				}
				if(!flag)
					return false;
				}
				
				//check for right diagonal
				if(col != noOfRows-1) {
				flag = true;
				int diff=noOfRows-1-col;
				int d=row-diff;
				int e=col+diff;
				for(int i=d,j=e;j>col && i<row;j--,i++) {
					int k = noOfRows*i + j;
					//check if there is lizard or tree
					if(currentNode.contains(k)) {
						flag = false;
					}else if(trees.contains(k)) {
						flag = true;
					}
				}
				if(!flag)
					return false;
				}

			}else 
				return false;
		/*}else {
			//search here when there is no tree in whole desert.

		}*/
		return true;
	}
	
}
