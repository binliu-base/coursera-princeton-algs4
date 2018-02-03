import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;


public class SAP {  

       private final Digraph graph;           // the underlying digraph
       private int ancestor = -1;
       
	   // constructor takes a digraph (not necessarily a DAG)
	   public SAP(Digraph G) {
           if (G == null ) throw new java.lang.IllegalArgumentException();  		   
		   graph = new Digraph(G);
	   }

	   public int length(int v, int w) {
			 validateInput(v, w);
			 ancestor = -1;
			 if(v == w) {
			 	ancestor = v;
			 	return 0;			 
			 }
	         int MINPATH = Integer.MAX_VALUE;        
	         
	         BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(graph, v);	         
	         BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(graph, w);

	         for (int i =0; i < graph.V(); i++) {
	        	 if(bfsV.hasPathTo(i) && bfsW.hasPathTo(i)){
	        		 int dist = bfsV.distTo(i) + bfsW.distTo(i);
	        		 if (dist < MINPATH) { 
	        			 MINPATH = dist;
	        			 ancestor = i;
	        		 }
	        	 }	        	 
	         }
	         
 		   return MINPATH == Integer.MAX_VALUE ? -1 : MINPATH;		   
	   }

	   // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
	   public int ancestor(int v, int w) {	   
		   length(v, w);
		   return ancestor;
	   }

	   public int length(Iterable<Integer> v, Iterable<Integer> w) {
           if (v == null || w == null) throw new IllegalArgumentException();	  

           ancestor = -1;           			   

	       for(int i : v) {
	           if(i < 0 || i > graph.V() - 1)
	                throw new IllegalArgumentException();
	       }
	       for(int i : w) {
	           if(i < 0 || i > graph.V() - 1)
	                throw new IllegalArgumentException();
	       }

	       BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(graph, v);	         
	       BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(graph, w);

           int MINPATH = Integer.MAX_VALUE;		   

	       for (int i =0; i < graph.V(); i++) {
	       	 if(bfsV.hasPathTo(i) && bfsW.hasPathTo(i)){
	       		 int dist = bfsV.distTo(i) + bfsW.distTo(i);
	       		 if (dist < MINPATH) { 
	       			MINPATH = dist;
	       			ancestor = i;	       			 
	       	 	 }	        	 
	         }
	       }
		   
 		   return MINPATH == Integer.MAX_VALUE ? -1 : MINPATH;		   
	   }   
//
//	   // a common ancestor that participates in shortest ancestral path; -1 if no such path
	   public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
	   		length(v, w);
	   		return ancestor;	
	   }
	   
	   private void validateInput(int v, int w) {
	        if (v < 0 || w < 0 || v > graph.V() -1|| w > graph.V()-1)
	            throw new java.lang.IllegalArgumentException();		   
	   }

	   //
	   // do unit testing of this class
	   public static void main(String[] args) {
		    In in = new In(args[0]);
		    Digraph G = new Digraph(in);
		    SAP sap = new SAP(G);
		    while (!StdIn.isEmpty()) {
		        int v = StdIn.readInt();
		        int w = StdIn.readInt();
		        int length   = sap.length(v, w);
		        int ancestor = sap.ancestor(v, w);
		        StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
		    }
		}	   
	   
	   
	}
