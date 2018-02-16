/**
 * @author Bin liu    02/16/2018
 *
 *  Seam Carving algorithm for image resizing
 *  
 *  Main Algorithms:  General-purpose shortest path algorithm is used (The proposition Q in <<Algorithms, 4th Edition>>  @P421)
 * 
 *  Links:
 *  Specification: http://coursera.cs.princeton.edu/algs4/assignments/seamCarving.html
 *             http://coursera.cs.princeton.edu/algs4/checklists/seamCarving.html
 *             http://algs4.cs.princeton.edu/windows/
 *  Compilation:   javac-algs4 SeamCarver.java
 *  Execution:     java-algs4 SeamCarver <name_of_image_file.png> with uncommented main method
 *  Execution only with installed algs4.jar and stdlib.jar
 *  
 *  TODO Optimization:  
 *  1. Note:  this version code is slow due to the bottleneck of creating Color objects.
 *    
 *  Reference:
 *  https://github.com/ChuntaoLu/Algorithms/blob/master/week8/seamCarving/src/SeamCarver.java
 *  https://www.programcreek.com/java-api-examples/index.php?source_dir=MOOC-master/Algorithms/seamCarving/SeamCarver.java
 */

import java.awt.Color;

import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;

public class SeamCarver {

    private Picture  picture;	
    private double[][] energyMatrix;  
    private int[] edgeTo;    
    private double[] distTo;
    
    private static final double BORDER_ENERGY = 1000.0;    
    private static final boolean VERTICAL = true;
    private static final boolean HORIZONTAL = false;
    
    // create a seam carver object based on the given picture	
    public SeamCarver(Picture picture){ 
      if (picture == null) 
            throw new IllegalArgumentException("Wrong argument, it is null !");
	  this.picture =  picture;
    }

    private void initSearch() {
       
    	computeEnergy();
    	
        this.distTo = new double[width() * height()];
        this.edgeTo = new int[width() * height()];    	 
        for (int col = 0; col < width(); col++) {
           for (int row = 0; row < height(); row++) {
              if (row == 0) 
                 distTo[convert2Dto1D(row, col)] = 0;
              else 
                 distTo[convert2Dto1D(row,col)] = Double.POSITIVE_INFINITY; 
              edgeTo[convert2Dto1D(row, col)] = -1;
           }
        }
    }
    
    private void computeEnergy() {
    	
        energyMatrix = new double[height()][width()];    	 
        for (int y = 0; y < height(); y++)
       	 for (int x = 0; x < width(); x++) {
       		   if(x == 4)
       			   x = 4;
               energyMatrix[y][x] = energy(x, y);
       	 }
    }

    //width of current picture  
    public int width() {
  	  return picture.width();
//        return this.energyMatrix[0].length;    	
    }                              

    //height of current picture
    public int height() {
   	  return picture.height();    	
//  	  return this.energyMatrix.length;
    }
    
    // current picture
    public Picture picture() {
      return new Picture(picture);     	
    }
    
    //energy of pixel at col (x) and y(row)   
    public double energy(int x, int y) {
        if (x < 0 || y < 0 || x > width() - 1 || y > height() - 1) 
        	throw new java.lang.IllegalArgumentException("Wrong Argument!");
        
        if(isBorder(x, y)) return BORDER_ENERGY;

        double gradient = gradient(picture.get(x-1, y), picture.get(x+1, y)) + gradient(picture.get(x, y-1), picture.get(x, y+1)); 
        return Math.sqrt(gradient);         
    }

    private double gradient(java.awt.Color x, java.awt.Color y) { 
        double r = x.getRed() - y.getRed(); 
        double g = x.getGreen() - y.getGreen(); 
        double b = x.getBlue() - y.getBlue(); 
        return r*r + g*g + b*b; 
    }
     
     private boolean isBorder(int col, int row) {
         if (col == 0 || col == width()-1) return true;
         if (row == 0 || row == height() - 1) return true;
         return false;
     }   

     private int convert2Dto1D(int row, int col) {
         return width() * row + col ;
     }
      
     private int getX(int x) {
    	return x % width();
     }
      
     private int getY(int x) {
     	return x / width();
     }
           
      // sequence of indices for vertical seam      
      public int[] findVerticalSeam() {

    	  initSearch();
          for (int y = 0; y < height() - 1; y++) {
              for (int x = 0; x < width(); x++) {
                  int v = convert2Dto1D(y, x);  

                  int w = -1; 
                  // relax down-left vertex if it exists                                 
                  if (x -1 >= 0) {
                	  w = convert2Dto1D(y+1, x-1);
                      relax(v, w);                      
                  }

                  // relax down-middle vertex if it exists
                  w = convert2Dto1D(y+1, x);
                  relax(v, w);                         
                  
                  // relax down-right vertex if it exists
                  if (x + 1< width()) {
                	  w = convert2Dto1D(y+1, x+1);
                      relax(v, w);                                    
                  }
                  
              }
          }	
          
	       // search for min distTo. It's a beginning of the seam with smallest energy
	       double min = Double.POSITIVE_INFINITY;
	       int end  = -1;
          
	       for (int col= 0; col < width(); col++) {
	    	   int w = convert2Dto1D(height() - 1, col);
	           if (min > distTo[w]) {
	              min =  distTo[w];
	              end = w;
	           }
	       }
	       
	       assert end != -1;
	     
    	  int[] seam = new int[height()];    	  
    	  int w = end;
          while (w>=0) {
              seam[getY(w)] = getX(w);              
              w = edgeTo[w];
          }
          
          this.edgeTo = null;
          this.distTo = null;          
      	  return seam; 
      	
      }         
      
      // sequence of indices for horizontal seam          
      public  int[] findHorizontalSeam() {
    	  Picture pictureOrig = this.picture;
    	  this.picture =  transposePicture();
          int[] seam  = findVerticalSeam();
    	  this.picture = pictureOrig; 
    	  computeEnergy();    	  
          return seam;	       
     }   

      //private void relax(int v, int w, double[] distTo, int[] edgeTo) {      
      private void relax(int v, int w) {
    	  
    	  int wx = getX(w);
    	  int wy = getY(w);
    	 
    	  if (distTo[w] > distTo[v] + energyMatrix[wy][wx]) {
  	         distTo[w] = distTo[v] + energyMatrix[wy][wx];
  	         edgeTo[w] = v;
  	      }
  	   }   
    
    private Picture transposePicture() {
    	Picture transposedPicture = new Picture(height(), width());
    	for (int row = 0; row < width(); row++) {
  			for (int col = 0; col < height() ; col++) {
  				transposedPicture.set(col, row,  picture.get(row, col));				
  	       }
    	}
    	
      return transposedPicture;
    }
    
    private void checkSeam(int[] seam, boolean isVertical) {
    	
    	if(seam == null)
            throw new IllegalArgumentException("called with a null argument.");

    	int seamSize, seamMaxValue;

        if (isVertical == VERTICAL) {
        	seamSize = height();
            seamMaxValue = width() - 1;
        } else {
        	seamSize = width();
            seamMaxValue = height() - 1;
        }
        
        if (seam.length != seamSize) {
            throw new IllegalArgumentException("Incorrect seam length");
        }        

        int prev = seam[0];
        for (int v : seam) {
            if (v < 0 || v > seamMaxValue) {
                throw new IllegalArgumentException("Seam's value is out of range: " + v);
            }

            if (Math.abs(v - prev) > 1) {
                throw new IllegalArgumentException("Seam is incorrect: neighbour values shoud not differ more than 1");
            }

            prev = v;            
        }
    }  

    private void checkPicture() {
        if (width() < 1 || height() < 1) {
            throw new IllegalArgumentException("Incorrect picture size");
        }
    }    

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam){

    	checkSeam(seam, VERTICAL);
    	checkPicture();

          Picture updatedPicture = new Picture(width() - 1,height());        
          for (int y = 0; y < height(); y++) {
              // copy the upper part of the picture and picEnergy
              for (int x = 0; x < seam[y]; x++) {
                 updatedPicture.set(x, y, picture.get(x, y));
              }
              // copy the bottom part of the picEnergy
              for (int x = seam[y] + 1; x < width(); x++) {
                 updatedPicture.set(x - 1, y, picture.get(x, y));
              }  
           }
          
          picture = updatedPicture;        
          this.computeEnergy();
    }
    
    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam){
        
        checkSeam(seam, HORIZONTAL);
    	checkPicture();
       
    	this.picture = transposePicture();  
      removeVerticalSeam(seam);
    	this.picture = transposePicture();
    	this.computeEnergy();
      
        // trace();       	
    }       
    
    private void trace(){
    	
        StdOut.printf("Printing energy calculated for each pixel.\n");        

        for (int row = 0; row < height(); row++) {
            for (int col = 0; col < width(); col++)
                StdOut.printf("%9.2f ", energy(col, row)); 
            StdOut.println();
        }
        
//        StdOut.printf("Printing edgeTo array for each pixel.\n"); 
//        
//        for (int row = 0; row < height(); row++) {
//            for (int col = 0; col < width(); col++) {
//            	int v = convert2Dto1D(row, col);
//                StdOut.printf("%9d ", edgeTo[v]);
//            }
//            StdOut.println();
//        }
//        
//        StdOut.printf("Printing distTo array for each pixel.\n"); 
//        
//        for (int row = 0; row < height(); row++) {
//            for (int col = 0; col < width(); col++) {
//            	int v = convert2Dto1D(row, col);
//                StdOut.printf("%9.2f ", distTo[v]);
//            }
//            StdOut.println();
//        } 
        
        StdOut.println();
        StdOut.println();        
    }

}
