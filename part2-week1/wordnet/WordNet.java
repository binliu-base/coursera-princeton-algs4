import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.DirectedCycle;

import java.util.HashMap;
import java.util.Map;

/*************************************************************************
 *  First week coursera course Java Algorithms part 2
 *  WordNet
 *  @Date 2018.02.03
 *  @Author Bin Liu
 *  
 *  Links:
 *  Specification: http://coursera.cs.princeton.edu/algs4/assignments/wordnet.html
 *             http://coursera.cs.princeton.edu/algs4/checklists/wordnet.html
 *             http://algs4.cs.princeton.edu/windows/
 *  Compilation:   javac-algs4 WordNet.java with uncommented main method
 *  Execution:     java-algs4 WordNet <name of Princeton formatted synset file> 
 *                   <name of Princeton formatted hypernyms file> 
 *  Execution only with installed algs4.jar and stdlib.jar
 *  
 *  Representing of WordNet
 *
 *************************************************************************/

public class WordNet {

      private Map<Integer, String> idTable;  //mapping from id to synset
	  private Map<String, Bag<Integer>> nounTable;  //mapping from synset to id  
       
        private SAP sap;       

       //constructor takes the name of the two input files
	   public WordNet(String synsets, String hypernyms) {
           if (synsets == null || hypernyms == null) throw new java.lang.IllegalArgumentException();
           
           idTable = new HashMap<Integer, String>();
           nounTable = new HashMap<String, Bag<Integer>>();           
           createMaps(synsets);
           createSAP(hypernyms);
        
	   }
       

	   // returns all WordNet nouns
	   public Iterable<String> nouns(){
		   return nounTable.keySet();
	   }

	   // is the word a WordNet noun?
	   public boolean isNoun(String word){
		    if(word == null) throw new IllegalArgumentException();		   
	        return nounTable.containsKey(word);
	   }

	   // distance between nounA and nounB (defined below)
	   public int distance(String nounA, String nounB) {
		   
  	       if (!isNoun(nounA) || !isNoun(nounB)) throw new java.lang.IllegalArgumentException("No such nouns in WordNet!");
		   return sap.length(nounTable.get(nounA), nounTable.get(nounB));
   
	   }

	   // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
	   // in a shortest ancestral path (defined below)
	   public String sap(String nounA, String nounB){
  	       if (!isNoun(nounA) || !isNoun(nounB)) throw new java.lang.IllegalArgumentException("No such nouns in WordNet!");		   
		   
		   int ancestorId = sap.ancestor(nounTable.get(nounA), nounTable.get(nounB));
   	       String valueFields[] = idTable.get(ancestorId).split(",");		   
		   return valueFields[0];
	   }

	   private boolean isRootedDAG(Digraph graph) {
		   
	     // check cycle		   
	     DirectedCycle cycleFinder  = new DirectedCycle(graph);
	     if(cycleFinder.hasCycle()) {
	    	 return false;
	     }
	     
	    int roots  = 0;
        // Find dangling nodes (noun node with a zero out  degree)
	    for (int v  = 0; v  < graph.V(); v++) { 
	    	if(!graph.adj(v).iterator().hasNext()) roots++;    	
	    }
	    
	    return roots == 1 ? true : false;   
	    
	   }
	   
	   private void createMaps(String synsets) {

		   In in = new In(synsets);
           while (in.hasNextLine()) {
			  String line = in.readLine();			  
			  if (line == null)  continue;			  
              String[] fields = line.split(",");              
              for (int i = 0; i < fields.length; i++) {
                  fields[i] = fields[i].trim();
               }            
              
              int id = Integer.parseInt(fields[0]);
              String synsetDefinition = fields[1] + "," + fields[2];              
              idTable.put(id, synsetDefinition);
              
              String synonyms [] = fields[1].split(" ");              
              for (int i = 0; i < synonyms.length; i++) {
                  synonyms[i] = synonyms[i].trim();
                  Bag<Integer> bag = nounTable.get(synonyms[i]);
                  if (bag == null) {
                      Bag<Integer> newBag = new Bag<Integer>();
                      newBag.add(id);
                      nounTable.put(synonyms[i], newBag);
                   }
                   else {
                      bag.add(id);
                   }
              }
           }
   
	   }
	   
	   private void createSAP(String hypernyms) {
		   
           In in = new In(hypernyms);
		   Digraph graph = new Digraph(idTable.size());           
           while (in.hasNextLine()) {
 			  String line = in.readLine(); 
			  if (line == null)  continue; 			  
              String[] fields = line.split(",");
              for (int i = 0; i < fields.length; i++) {
                  fields[i] = fields[i].trim();
               }              
             
              for (int i = 1; i < fields.length; i++) {
                  graph.addEdge(Integer.parseInt(fields[0]), Integer.parseInt(fields[i]));
               }                 
           }
           
           // check the graph is valid rooted DAG or not
           if (!isRootedDAG(graph))
               throw new IllegalArgumentException("The input does not correspond to a rooted DAG!");
           
           sap = new SAP(graph);

	   }
	   
	   // do unit testing of this class
	   public static void main(String[] args) {
		   
		  WordNet wordNet = new WordNet(args[0], args[1]);
//	  	  StdOut.println(wordNet.isNoun("a"));
//	      StdOut.println(wordNet.sap("a", "b"));
//	      StdOut.println(wordNet.sap("b", "f"));
//	      StdOut.println(wordNet.distance("b", "f"));
		  
	      StdOut.println(wordNet.distance("James_Wilson", "nebula"));		  
		  
	   }	   
}
