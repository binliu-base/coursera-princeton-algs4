import java.util.Iterator;
//  import java.util.NoSuchElementException;
  
public class Deque<Item> implements Iterable<Item> {
    
   private int size;
   private Node firstNd, lastNd;
   private class Node{
       Node prevNd;
       Node nextNd;
       Item item;
   }
   
   public Deque()                           // construct an empty deque
   {
       size = 0;
       firstNd = null;
       lastNd = null;
   }
   
   public boolean isEmpty()                 // is the deque empty?
   {
       return (size == 0) ;       
   }
   
   public int size()                        // return the number of items on the deque
   {
       return size;
   }
   
   public void addFirst(Item item)          // add the item to the front
   {
        ifItemIsNull(item);
        Node newNode = new Node();
        newNode.item = item;
        
        if (size == 0){
            newNode.prevNd = null;
            newNode.nextNd = null;
            firstNd = newNode;
            lastNd = newNode;
        }
        else{
            newNode.nextNd = firstNd;
            newNode.prevNd = null;
            firstNd.prevNd = newNode;
            firstNd = newNode;
        }
        size ++;
   }
   
   public void addLast(Item item)           // add the item to the end
   {
        ifItemIsNull(item);
        Node newNode = new Node();
        newNode.item = item;
        if (size == 0){
            newNode.prevNd = null;
            newNode.nextNd = null;
            firstNd = newNode;
            lastNd = newNode;
        }
        else{
            newNode.nextNd = null;
            newNode.prevNd = lastNd;
            lastNd.nextNd = newNode;
            lastNd = newNode;
        }
        size ++;
   }
   
   public Item removeFirst()                // remove and return the item from the front
   {
       checkListSize();
       Item returnItem = null;
       if (size == 1){
           returnItem = firstNd.item;
           firstNd = null;
           lastNd = null;
       }
       else{
           Node oldNd = firstNd;
           returnItem = oldNd.item;
           firstNd = firstNd.nextNd;
           firstNd.prevNd = null; 
           oldNd = null;           
       }
       size--;
       return returnItem;
   }       
   
   public Item removeLast()                 // remove and return the item from the end
   {
       checkListSize();
       Item returnItem = null;
       if (size == 1){
           returnItem = firstNd.item;
           firstNd = null;
           lastNd = null;
       }
       else{
           Node oldNd = lastNd;
           returnItem = oldNd.item;
           lastNd = lastNd.prevNd;
           lastNd.nextNd = null;
           oldNd = null;           
       }
       size--;
       return returnItem;  
   }
       
   public Iterator<Item> iterator()         // return an iterator over items in order from front to end
   {
     // return an iterator over items in order from front to end
     return new ListIterator();       
   }
   
   private void ifItemIsNull(Item item){
        if ( item == null) {
            throw new java.lang.IllegalArgumentException("Item must be not null !");
        }        
   }
   
   private void checkListSize(){
       if (size == 0) throw new java.util.NoSuchElementException("The deque is empyt !");
   }

   private class ListIterator implements Iterator<Item> { 
       private Node curr = firstNd;
       @Override
       public boolean hasNext() {
           // TODO Auto-generated method stub
           return curr != null;
       }
       @Override
       public Item next() {
           // TODO Auto-generated method stub
           if (curr == null)
               throw new java.util.NoSuchElementException("there are no more items!");
           Item item = curr.item;
           curr = curr.nextNd;
           return item;
       }
       
        public void remove() {
            throw new java.lang.UnsupportedOperationException();
        }       
   }  

    public static void main(String[] args) {
        // unit testing (optional)
        Deque<String> queue = new Deque<String>();
        System.out.println(queue.size);
        queue.addFirst("a");
        queue.addFirst("b");
        queue.addLast("c");
        queue.addFirst("d");
        queue.addLast("e");
        System.out.println(queue.size);
        Iterator<String> iter = queue.iterator();
        while (iter.hasNext()) {
            System.out.println(iter.next());
        }
    }
   
}   