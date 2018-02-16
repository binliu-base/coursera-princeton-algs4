import java.awt.Color;

import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;

public class SeamCarver {

//    private Picture  picture;
    private int[][] picture;           // int array as intermediate representation of the picture
    private int width, height;    
    private double[][] energyMatrix;  
    private int[] edgeTo;    
    private double[] distTo;
    private boolean isTransposed;  // is pic currently transposed?    
    
    private static final double BORDER_ENERGY = 1000.0;    
    
    // create a seam carver object based on the given picture	
    public SeamCarver(Picture picture){ 
      if (picture == null) 
            throw new IllegalArgumentException("Wrong argument, it is null !");

      width = picture.width();
      height = picture.height();
      this.picture = new int[height][width];     
      
      for (int y = 0; y < height ; y++) {
         	for (int x = 0; x < width; x++) {
               this.picture[y][x] = picture.get(x, y).getRGB();			
    	}
      }
      
      computeEnergy();	  
      isTransposed = false;      
    }

    private void initSearch() {
//	  computeEnergy();
  	  computeEdgeToDistTo();  	  
    }
    
    private void computeEnergy() {
    	
        energyMatrix = new double[height][width];    	 
        for (int y = 0; y < height; y++)
       	 for (int x = 0; x < width; x++) {
               energyMatrix[y][x] = energy(x, y);
       	 }
    }
    
    private void computeEdgeToDistTo() {
    	
        this.distTo = new double[width * height];
        this.edgeTo = new int[width * height];  

        for (int col = 0; col < width; col++) {
           for (int row = 0; row < height; row++) {
              if (row == 0) 
                 distTo[convert2Dto1D(row, col)] = 0;
              else 
                 distTo[convert2Dto1D(row,col)] = Double.POSITIVE_INFINITY; 
              edgeTo[convert2Dto1D(row, col)] = -1;
           }
        }    	
    }

    //width of current picture  
    public int width() {
//  	  return this.picture[0].length;
    	return width;
    }                              

    //height of current picture
    public int height() {
//   	  return this.picture.length;
    	return height;
    }
    
    // current picture
    public Picture picture() {
//      return new Picture(picture);
      Picture seamedPicture = new Picture(width, height);
      if (isTransposed) transpose(height, width);

      // transfer back to picture
      for (int c = 0; c < width; c++)
        for (int r = 0; r < height; r++)
        	seamedPicture.set(c, r, new Color(picture[r][c]));
      return seamedPicture;    	
    }
    
    //energy of pixel at col (x) and y(row)   
    public double energy(int x, int y) {    	
//        if(isTransposed) transpose(height, width);
        
        // if (x < 0 || y < 0 || x > width() - 1 || y > height() - 1) 
        if (x < 0 || y < 0 || x > width - 1 || y > height - 1)       
        	throw new java.lang.IllegalArgumentException("Wrong Argument!");
        
        if(isBorder(x, y)) return BORDER_ENERGY;
        
        double gradient = gradient(picture[y - 1][x], picture[y + 1][x]) + gradient(picture[y][x - 1], picture[y][x + 1]); 
        return Math.sqrt(gradient);         
    }

    // helper for compute energy
    private double gradient(int x, int y) {
        int r = ((x>>16)&0x0ff) - ((y>>16)&0x0ff);
        int g = ((x>>8)&0x0ff) - ((y>>8)&0x0ff);
        int b = (x&0x0ff) - (y&0x0ff);
        return r * r  + g * g  + b * b;
    }
    
     private boolean isBorder(int col, int row) {
         // if (col == 0 || col == width()-1) return true;
         // if (row == 0 || row == height() - 1) return true;
         if (col == 0 || col == width-1) return true;
         if (row == 0 || row == height - 1) return true;      
         return false;
     }   

     private int convert2Dto1D(int row, int col) {
         return width * row + col ;
     }
      
     private int getX(int x) {
    	return x % width;
     }
      
     private int getY(int x) {
     	return x / width;
     }
           
      // sequence of indices for vertical seam      
      public int[] findVerticalSeam() {
   	    if (isTransposed) 
   	    	transpose(height, width);  // transpose back if pic is transposed
        return findSeam();   	    	
      }         
      
      // sequence of indices for horizontal seam          
      public  int[] findHorizontalSeam() {
          if (!isTransposed) 
        	  transpose(width, height); // transpose pic if not already transposed
          int[] seam = findSeam();
          transpose(width, height);
          return seam;
     }   

      // helper for find seam
      private int[] findSeam(){
    	  initSearch();    	  
          for (int y = 0; y < height - 1; y++) {
              for (int x = 0; x < width; x++) {
            	  
                  int s = convert2Dto1D(y, x);
                  // relax down-left vertex if it exists                                 
                  if (x -1 >= 0) {
                      relax(s, convert2Dto1D(y+1, x-1));                      
                  }

                  // relax down-middle vertex if it exists
                  relax(s, convert2Dto1D(y+1, x));                         
                  
                  // relax down-right vertex if it exists
                  // if (x + 1< width()) {
                  if (x + 1< width) {                  
                      relax(s, convert2Dto1D(y+1, x+1));                                    
                  }
                  
              }
          }	
          
	       // search for min distTo. It's a beginning of the seam with smallest energy
	       double min = Double.POSITIVE_INFINITY;
	       int end  = -1;
          
	       for (int col= 0; col < width; col++) {
	           if (min > distTo[convert2Dto1D(height - 1, col)]) {
	              min =  distTo[convert2Dto1D(height - 1, col)];
	              end = convert2Dto1D(height - 1, col);
	           }
	       }
	       
          assert end != -1;
	     
    	  // int[] seam = new int[height()];    	  
        int[] seam = new int[height];                 
          while (end >= 0) {
              seam[getY(end)] = getX(end);              
              end = edgeTo[end];
          }
          
          releaseMemory();          
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
      
      private void releaseMemory() {

          this.edgeTo = null;
          this.distTo = null;      	  
    	  
      }
    
    private void checkPicture() {
        // if (width() < 1 || height() < 1) {
        if (width < 1 || height < 1) {      
            throw new IllegalArgumentException("Incorrect picture size");
        }
    }    

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam){

//    	checkSeam(seam, VERTICAL);
//    	checkPicture();
//
//          Picture updatedPicture = new Picture(width() - 1,height());        
//          for (int y = 0; y < height(); y++) {
//              // copy the upper part of the picture and picEnergy
//              for (int x = 0; x < seam[y]; x++) {
//                 updatedPicture.set(x, y, picture.get(x, y));
//              }
//              // copy the bottom part of the picEnergy
//              for (int x = seam[y] + 1; x < width(); x++) {
//                 updatedPicture.set(x - 1, y, picture.get(x, y));
//              }  
//           }
//          
//          picture = updatedPicture;        
//          this.computeEnergy();
    	
        if (isTransposed) 
        	transpose(height, width);
        removeSeam(seam, height, width);
        width--;    	
        computeEnergy();
    }
    
    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam){

    	// trace();   
        if (!isTransposed) 
        	transpose(width, height);
        removeSeam(seam, height, width);
        width--;
        transpose(width, height);
        computeEnergy();        
     	
    }       

    // helper for horizontal seam
    private void transpose(int h, int w) {
        int[][] transposedPic = new int[h][w];
        double [][] transposedEnergy = new double[h][w];
        for (int r = 0; r < h; r++) {
            for (int c = 0; c < w; c++) {
                transposedPic[r][c] = this.picture[c][r];
                transposedEnergy[r][c] = this.energyMatrix[c][r];
            }
        }
        this.picture = transposedPic;
        this.energyMatrix = transposedEnergy;
        this.width =  this.picture[0].length;
        this.height = this.picture.length;          
        isTransposed = !isTransposed;
    }

    // helper for remove seam
    // suppose the picture is not transposed
    private void removeSeam(int[] a, int h, int w) {

        handleRemoveSeamExceptions(a, h, w);

//        for (int r = 0; r < h; r++) {
//            if (a[r] < w - 1) {
//                System.arraycopy(picture[r], a[r] + 1, picture[r], a[r], w - a[r] - 1);
//                System.arraycopy(energyMatrix[r], a[r] + 1, energyMatrix[r], a[r], w - a[r] - 1);
//            }
//        }
//        // only the energy of the seam element and its left element changes
//        for (int r = 1; r < h - 1; r++) {
//            int x = a[r];
//            if (x > 0) energyMatrix[r][x - 1] = (int) energy(x - 1, r);
//            if (x < w - 1) energyMatrix[r][x] = (int) energy(x, r );
//        }
        
        int [][] updatedPicture = new int[h][w - 1]; 
        double [][] updatedEnergyMatrix = new double[h][w - 1]; 
        for (int y = 0; y < height; y++) {
            // copy the upper part of the picture and picEnergy
            for (int x = 0; x < a[y]; x++) {
             // updatedPicture.set(x, y, picture.get(x, y));
            	updatedPicture[y][x] = picture[y][x];
            	updatedEnergyMatrix[y][x] = energyMatrix[y][x];
            	
            }
            
            if (y==5)
            	y=5;
            // copy the bottom part of the picEnergy
            for (int x = a[y] + 1; x < width; x++) {
              //updatedPicture.set(x - 1, y, picture.(x, y));
            	updatedPicture[y][x-1] = picture[y][x]; 
            	updatedEnergyMatrix[y][x-1] = energyMatrix[y][x];            	
            }  
         }
        
        picture = updatedPicture;   
        energyMatrix = updatedEnergyMatrix;
//        this.width =  this.picture[0].length;
//        this.height = this.picture.length;        
        
    }        

    
    // helper for remove seam
    private void handleRemoveSeamExceptions(int a[], int h, int w) {
    	
    	if(a == null)
            throw new IllegalArgumentException("called with a null argument."); 
    	
        if (width < 1 || height < 1) throw new IllegalArgumentException();
        if (a.length != h) throw new IllegalArgumentException();

        int prev = a[0];        
        for (int x : a) {
            if (x < 0 || x >= w) throw new IllegalArgumentException();

            if (Math.abs(x - prev) > 1) {
                throw new IllegalArgumentException("Seam is incorrect: neighbour values shoud not differ more than 1");
            }
            prev = x;             
        }
    }
    
//    private void trace(){
//    	
//        StdOut.printf("Printing energy calculated for each pixel.\n");        
//
//        for (int row = 0; row < height(); row++) {
//            for (int col = 0; col < width(); col++)
//                StdOut.printf("%9.2f ", energy(col, row)); 
//            StdOut.println();
//        }
//        
////        StdOut.printf("Printing edgeTo array for each pixel.\n"); 
////        
////        for (int row = 0; row < height(); row++) {
////            for (int col = 0; col < width(); col++) {
////            	int v = convert2Dto1D(row, col);
////                StdOut.printf("%9d ", edgeTo[v]);
////            }
////            StdOut.println();
////        }
////        
////        StdOut.printf("Printing distTo array for each pixel.\n"); 
////        
////        for (int row = 0; row < height(); row++) {
////            for (int col = 0; col < width(); col++) {
////            	int v = convert2Dto1D(row, col);
////                StdOut.printf("%9.2f ", distTo[v]);
////            }
////            StdOut.println();
////        } 
//        
//        StdOut.println();
//        StdOut.println();        
//    }

}
