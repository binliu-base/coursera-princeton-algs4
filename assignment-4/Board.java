/******************************************************************************
 *  Compilation:  javac Board.java
 *  Execution:    java Board
 *  Dependencies: none
 *
 ******************************************************************************/

import java.util.ArrayList;
    
public class Board{

     private final int[][] blocks;
     private int size;
    
     public Board(int[][] blocks) {
         this.size = blocks.length;  
         this.blocks = new int[size][size];         
         for (int i = 0; i < size; i++)
             for (int j = 0; j < size; j++)
                  this.blocks[i][j] = blocks[i][j];
    }


   // board dimension n    
    public int dimension(){
        return size;
    }              
    
    // number of blocks out of place
    public int hamming(){
        int ham = 0;
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++){
                if (blocks[i][j] != 0){
                    int target = i * size + j + 1;
                    if(this.blocks[i][j] != target )                    
                        ham++;
                }
        }
        return ham;
    }   
    
    // sum of Manhattan distances between blocks and goal    
    public int manhattan(){
        int moves = 0;
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++){
               if (blocks[i][j] == 0) continue;                
               int goalI = (this.blocks[i][j] - 1)  / size  ;
               int goalJ = (this.blocks[i][j] - 1)  % size  ;
               moves += Math.abs(goalI - i) + Math.abs(goalJ - j);               
        }
        return moves;
    }   

    // is this board the goal board?
    public boolean isGoal()                
    {
        // return hamming() == 0;        
        return manhattan() == 0;
    }

    // a board that is obtained by exchanging any pair of blocks    
    public Board twin()
    {
//        int[][] twinBoard = Arrays.copyOfRange(blocks, 0, blocks.length);
       Board twinBoard = new Board(blocks);        

        for (int i = 0; i < size ; i++) {
            for (int j = 0; j < size -1 ; j++) {
                if (twinBoard.blocks[i][j] != 0 && twinBoard.blocks[i][j + 1] != 0) {
                    twinBoard.exch(i, j, i, j + 1);
                    return twinBoard;
                }
            }
        }
        return twinBoard;        
    }        
    
    public boolean equals(Object y)       {
        // does this board equal y?
        if (y == this) return true;
        if (y == null || y.getClass() != this.getClass()) return false;
        if (size != ((Board) y).size) return false;
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                if (blocks[i][j] != ((Board) y).blocks[i][j])
                    return false;
        return true;
    }


    public Iterable<Board> neighbors()     // all neighboring boards    
    {
        // all neighboring boards
        int blankI = 0, blankJ = 0; boolean found = false;
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                if (blocks[i][j] == 0) {
                    blankI = i;
                    blankJ = j;
                    found = true;
                    break;
                } 

        if (!found) throw new java.lang.IllegalArgumentException("No blank block!");

        ArrayList<Board> neighbors = new ArrayList<Board>();
        if (blankI > 0) {
            Board neighbor = new Board(blocks);
            neighbor.exch(blankI, blankJ, blankI - 1, blankJ);
            neighbors.add(neighbor);
        }
        if (blankI < size - 1) {
            Board neighbor = new Board(blocks);
            neighbor.exch(blankI, blankJ, blankI + 1, blankJ);
            neighbors.add(neighbor);
        }
        if (blankJ > 0) {
            Board neighbor = new Board(blocks);
            neighbor.exch(blankI, blankJ, blankI, blankJ - 1);
            neighbors.add(neighbor);
        }
        if (blankJ < size - 1) {
            Board neighbor = new Board(blocks);
            neighbor.exch(blankI, blankJ, blankI, blankJ + 1);
            neighbors.add(neighbor);
        }
        return neighbors;        
    }

    private void exch(int i, int j, int i2, int j2) {

        int t = blocks[i][j]; blocks[i][j] = blocks[i2][j2]; blocks[i2][j2] = t;

    } 

    public String toString() {
        // string representation of this board (in the output format specified below)
        StringBuilder sb = new StringBuilder();
        sb.append(size).append('\n');
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++)
                sb.append(' ').append(blocks[i][j]).append(' ');
            sb.append('\n');
        }
        return sb.toString();
    }

    /**
     * Unit tests the board data type.
     */
    public static void main(String[] args) {
        /* YOUR CODE HERE */
    }       
}