import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
	
   private final WordNet wordnet;

   public Outcast(WordNet wordnet)         // constructor takes a WordNet object
   {
	   this.wordnet = wordnet;	   
   }
   
   public String outcast(String[] nouns)   // given an array of WordNet nouns, return an outcas
   {

       int MAXDIST = 0;	   
	   int distance = 0;
       String outcast = nouns[0];	   

	   for ( String s : nouns ) {
		   for (String v : nouns) {
			   if (s.equals(v)) continue; 
			   distance += wordnet.distance(s, v);
		   }
//		   System.out.println("s= " + s + ", distance=" + distance);
		   if (distance > MAXDIST) {
			   MAXDIST = distance;
			   outcast = s;	
		   }	
		   distance = 0;		   
	   }
       
	   return outcast;
	   
   }

   public static void main(String[] args) {
	    WordNet wordnet = new WordNet(args[0], args[1]);
	    Outcast outcast = new Outcast(wordnet);
	    for (int t = 2; t < args.length; t++) {
	        In in = new In(args[t]);
	        String[] nouns = in.readAllStrings();
	        StdOut.println(args[t] + ": " + outcast.outcast(nouns));
	    }
	}   

}
