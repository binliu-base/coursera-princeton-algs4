 import java.util.Iterator;
// import java.util.NoSuchElementException;
 import edu.princeton.cs.algs4.StdRandom;
  
public class RandomizedQueue<Item> implements Iterable<Item> {
      private int size;
      private Item [] rqArrays ;
      
   public RandomizedQueue()                 // construct an empty randomized queue
   {
       size = 0;
       rqArrays = (Item []) new Object[1];
   }   
   public boolean isEmpty()                 // is the randomized queue empty?
   {
       return size == 0 ;
   }

   public int size()                        // return the number of items on the randomized queue
   {
       return size;
   }
   
   public void enqueue(Item item)           // add the item
   {
       validateItem(item);
//       rqArrays[size++] = item;
//       if (size == rqArrays.length){
//           resize( 2 * rqArrays.length );
//       }       
       if (size + 1 >  rqArrays.length)
           resize( 2 * rqArrays.length );
       rqArrays[size++] = item;
   }
       
   public Item dequeue()                    // remove and return a random item
   {
       validateArraySize();
       int delIndex = StdRandom.uniform(0, size-1);
       Item delItem = rqArrays[delIndex];
       switchItem(delIndex, size - 1, rqArrays);
       size--;       
       rqArrays[size] = null;
       
       if (size > 0 && size == rqArrays.length/4){
           resize(rqArrays.length/2);
       }
       return delItem;   
   }
   
   public Item sample()                     // return a random item (but do not remove it)
   {
       validateArraySize();
       int iRand = StdRandom.uniform(0, size);
       return rqArrays[iRand];
   }
   
   public Iterator<Item> iterator()         // return an independent iterator over items in random order
   {
       return new RandomIterator();
   }
   
   private void validateItem(Item item){
       if (item == null ) throw new java.lang.IllegalArgumentException("the item is null!") ;              
   }

   private void validateArraySize(){
       if (size == 0 ) throw new java.util.NoSuchElementException("the RandomizeQueue  is empty!");              
   }   
   
   private void resize(int cap){
       Item [] tmpArrays = (Item []) new Object[cap];
       for (int i = 0; i < size; i++)
           tmpArrays[i] = rqArrays[i];
       rqArrays = tmpArrays;
   }
   
   private class RandomIterator implements Iterator <Item>
   {
       private int rank;
       private Item [] iterArrays;
       
       public RandomIterator(){
           rank = size;
           iterArrays = (Item []) new Object[rank];
           for (int i =0; i< size; i++)
               iterArrays[i] = rqArrays[i];               
       }

      @Override
       public boolean hasNext(){
           return (rank > 0);           
       }
       
       @Override
       public Item next(){
           if(rank ==0) throw new java.util.NoSuchElementException("there are no more items!");
           int iRand = StdRandom.uniform(0, rank);
           rank--;           
           Item item = iterArrays[iRand];           
           switchItem(iRand, rank, iterArrays);
           return item;
       }  
       
       public void remove() {
            throw new java.lang.UnsupportedOperationException();
        }  
       
   }
   
      private void switchItem(int i, int j, Item [] a)
      {
         Item tmpItem = a[i];
         a[i] = a[j];
         a[j] = tmpItem;
      }        
   
   public static void main(String[] args) {
       // unit testing (optional)
       RandomizedQueue<String> rq = new RandomizedQueue<String>();
       rq.enqueue("a");
       rq.enqueue("b");
       rq.enqueue("c");
       rq.enqueue("d");
       rq.enqueue("e");
       rq.enqueue("f");
       rq.enqueue("g");
       rq.dequeue();
       Iterator<String> iter1 = rq.iterator();
       Iterator<String> iter2 = rq.iterator();
       while (iter1.hasNext()) {
           System.out.print(iter1.next() + ",");
       }
       System.out.println();

       while (iter2.hasNext()) {
           System.out.print(iter2.next() + ",");
       }
       System.out.println();
   }
          
}