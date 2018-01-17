import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;
import java.util.Stack;
import edu.princeton.cs.algs4.Queue;

public class Solver {

    private SearchNode result;

    private static class SearchNode implements Comparable<SearchNode> {
        private final Board board;
        private final int moves;
        private final int priority;        
        private final SearchNode previous;        
        
        private SearchNode(Board board, SearchNode previous) {
            this.board = board;
            this.previous = previous;
            this.moves = (this.previous == null) ? 0 : this.previous.moves + 1;
            this.priority = this.board.manhattan() + this.moves;
        }
        
        @Override        
        public int compareTo(SearchNode that) {
            return this.priority - that.priority; 
        }
    }    

    public Solver(Board initial){
        result = initial.isGoal() ? new SearchNode(initial, null) : solve(initial, initial.twin());      
    }

    private SearchNode solve(Board initial, Board twin) {
        MinPQ<SearchNode> mainPQ = new MinPQ<SearchNode>();
        // This PQ is used for detecting infeasible puzzles.
        // It keeps track of boards that lead to the goal board if we modify
        // the initial board by swapping any pair of adjacent (non-blank) blocks in the same row.
        MinPQ<SearchNode> twinPQ = new MinPQ<SearchNode>();

        // First, insert the initial search node (the initial board, 0 moves, and a null previous search node)
        // into a priority queue.
        mainPQ.insert(new SearchNode(initial, null));
        twinPQ.insert(new SearchNode(twin, null));

        // Repeat until the search node dequeued corresponds to a goal board.
        while (true) {
            SearchNode sn = step(mainPQ);
            if (sn.board.isGoal()) {
                return sn;
            }

            if (step(twinPQ).board.isGoal()) {
                return null;
            }
        }
    }    

    private SearchNode step(MinPQ<SearchNode> pq) {
        // Then, delete from the priority queue the search node with the minimum priority,
        // and insert onto the priority queue all neighboring search nodes
        // (those that can be reached in one move from the dequeued search node).
        SearchNode least = pq.delMin();
        for (Board neighbor : least.board.neighbors()) {
            // A critical optimization.
            // Best-first search has one annoying feature: search nodes corresponding to the same board
            // are enqueued on the priority queue many times.
            // To reduce unnecessary exploration of useless search nodes, when considering the neighbors of
            // a search node, don't enqueue a neighbor if its board is the same as the board of the previous search node.
            if (least.previous == null || !neighbor.equals(least.previous.board)) {
                pq.insert(new SearchNode(neighbor, least));
            }
        }
        return least;
    }    
        
    /**
     * @return is the initial board solvable?
     */
    public boolean isSolvable() {
        return (result != null);
    }

    public int moves()   {
        return (result != null) ? result.moves : -1;
    }

    /**
     * @return sequence of boards in a shortest solution; null if no solution.
     */
    public Iterable<Board> solution() {
        if (result == null) {
            return null;
        }

        Stack<Board> solution = new Stack<Board>();
        for (SearchNode sn = result; sn != null; sn = sn.previous) {
            solution.push(sn.board);
        }
        return reverse(solution);
    }

    private Queue<Board> reverse(Stack<Board> stack){
        Queue<Board> arrCopy = new Queue<Board>();
        while(!stack.empty()){
            arrCopy.enqueue((Board)stack.pop());
        }
        return arrCopy;
    }

    /**
     * Solve a slider puzzle (given below).
     *
     * @param args the filename containing board description.
     */
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int N = in.readInt();
        int[][] blocks = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                blocks[i][j] = in.readInt();
            }
        }
        Board initial = new Board(blocks);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable()) {
            StdOut.println("No solution possible");
        } else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution()) {
                StdOut.println(board);
            }
        }
    }
}
