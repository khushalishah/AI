package hw1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.Queue;

public class Homework4 {

    Queue<int[][]> queue = new LinkedList<>();
    static int algo = 0;
    static int noOfRows = 0;
    static int noOfLizards = 0;
    static int noOfTrees = 0;
    int[][] currentNode;

    public static void main(String args[]) {
        Homework4 hw = new Homework4();
        hw.readInput("E://Khushali//input.txt");

        if(noOfTrees == 0 && noOfLizards>noOfRows) {
            //test case will fail
            hw.writeToOutputFile(false,null);
        }else {
            //check if search space is dense or sparse

            if(algo == 0) {
                hw.runBFS();
            }else if (algo==1) {
                hw.runDFS();
            }else {
                hw.runSA();
            }
        }
    }

    void readInput(String filePath) {
        //read input file
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            try {
                String line = br.readLine();

                //get type of algorithm
                /*if(line.trim().equalsIgnoreCase("DFS"))
                    algo=1;
                else if(line.trim().equalsIgnoreCase("SA"))
                    algo=2;*/

                //get number of rows and columns
                line = br.readLine();
                noOfRows = Integer.parseInt(line.trim());

                //get number of lizards
                line = br.readLine();
                noOfLizards = Integer.parseInt(line.trim());

                line = br.readLine();    
                int[][] input = new int[noOfRows][noOfRows];
                int row=0;

                //get matrix
                for(int i=0;i<noOfRows;i++) {

                    for(int col=0;col<noOfRows;col++) {
                        int bit = Integer.parseInt(String.valueOf(line.charAt(col)));
                        input[row][col] = bit;
                        //check if it is a tree
                        if(bit == 2) {
                            noOfTrees++;
                        }
                    }
                    row++;
                    line = br.readLine();

                }

                //insert first node to queue
                queue.add(input);

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

    void runBFS() {
        boolean result = false;
        
        while (!queue.isEmpty()) {
            System.out.println("BFS...");
            currentNode = queue.remove();
            

            int totalPlacedLizards = 0;
            for(int row=0;row<noOfRows;row++) {
                boolean nodeAdded = false;
                for(int col=0;col<noOfRows;col++) {
                    if(currentNode[row][col]==1) {
                        totalPlacedLizards++;
                    }
                    if(isSafe(row,col)) {
                        int[][] newNode = new int[noOfRows][noOfRows];
                        for(int i = 0; i < currentNode.length; i++)
                            newNode[i] = currentNode[i].clone();
                        newNode[row][col] = 1;
                        queue.add(newNode);
                        nodeAdded = true;
                        newNode = null;
                    }
                }
                //check if desert is dense or sparse and take actions according to it.
                int totalSearchSpace = (int) Math.pow(noOfRows, 2);
                if(nodeAdded && totalSearchSpace-noOfTrees>totalSearchSpace/2) {
                    break;
                }

            }
            //check if we have reached goal status
            if(totalPlacedLizards == noOfLizards) {
                //stop algorithm we have reached goal status
                System.out.println("OK");
                result=true;
                new Homework4().writeToOutputFile(true,currentNode);

                //comment below code
                for(int i=0;i<noOfRows;i++) {
                    for(int j=0;j<noOfRows;j++) {
                        System.out.print(currentNode[i][j]);
                    }
                    System.out.println();
                }
                
                break;
            }
        }
        if(!result) {
            System.out.println("FAIL");
            new Homework4().writeToOutputFile(false,null);
        }
    }

    void runDFS() {}

    void runSA() {}

    boolean isSafe(int row,int col) {
        if(currentNode[row][col]!=0) {
            //it is a tree
            return false;
        }else {
            //check in row
            //int tempCol = -1;
            for (int i=0;i<noOfRows;i++) {
                if(currentNode[row][i] == 1)
                    return false;
                /*if(currentNode[row][i]==2) {
                    //if there is a tree in row
                    tempCol = i;
                }*/
            }

            //check in column
            for(int i=0;i<noOfRows;i++) {
                if(currentNode[i][col]==1)
                    return false;
            }

            //check left top diagonal
            for(int i=row,j=col;i>=0 && j>=0;i--,j--) {
                if(currentNode[i][j] == 1)
                    return false;
            }

            //check right top diagonal
            for(int i=row,j=col;i>=0 && j<noOfRows;i--,j++) {
                if(currentNode[i][j] == 1)
                    return false;
            }

            //check left bottom diagonal
            for(int i=row,j=col;i<noOfRows && j>=0;i++,j--) {
                if(currentNode[i][j] == 1)
                    return false;
            }

            //check right bottom diagonal
            for(int i=row,j=col;i<noOfRows && j<noOfRows;i++,j++){
                if(currentNode[i][j] == 1)
                    return false;
            }


            return true;
        }
    }

    void writeToOutputFile(boolean result,int [][] finalNode) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("E://Khushali//output.txt", false));
            if(result == true) {
                writer.write("OK");

                for(int row=0;row<noOfRows;row++) {
                    StringBuilder sb = new StringBuilder();
                    for(int col=0;col<noOfRows;col++) {
                        sb.append(finalNode[row][col]);
                    }
                    writer.newLine();
                    writer.write(sb.toString());
                }

            }else {
                writer.write("FAIL");
            }
            writer.close();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}




