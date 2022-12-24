import java.util.Iterator;
import java.util.NoSuchElementException;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;


public class RandomizedQueue<Item> implements Iterable<Item> {
    private int size;
    private Item[] queue;
    
    // construct an empty randomized queue
    public RandomizedQueue() {
        queue = (Item[]) new Object[1];
        size = 0;
    }

    private void resize(int max)
    { 
        Item[] temp = (Item[]) new Object[max];
        int j = 0;
        for (int i = 0; i < queue.length; i++) {
            if (queue[i] != null) {
                temp[j] = queue[i];
                j++;
            }
        }
        queue = temp;
    }

    // is the randomized queue empty?
    public boolean isEmpty() {
        return size == 0;
    }

    // return the number of items on the randomized queue
    public int size() {
        return this.size;
    }

    // add the item
    public void enqueue(Item item) {
        if (item == null) { throw new IllegalArgumentException(); }

        if (size == queue.length) resize(2*queue.length);

        int i = 0;
        while (i != -1) {
            if (queue[i] == null) {
                queue[i] = item;
                size++;
                i = -1;
            }
            else {
                i++;
            }
        }
    }

    // remove and return a random item
    public Item dequeue() {
        if (isEmpty()) { throw new java.util.NoSuchElementException(); }
        while (true) {
            int i = StdRandom.uniformInt(0, queue.length);
            Item result = queue[i];
            if (result != null) { 
                queue[i] = null;
                size--;
                if (size > 0 && size == queue.length/4) resize(queue.length/2);
                return result;
            }
        }
    }

    // return a random item (but do not remove it)
    public Item sample() {
        if (isEmpty()) { throw new java.util.NoSuchElementException(); }
        while (true) {
            int i = StdRandom.uniformInt(0, queue.length);
            Item result = queue[i];
            if (result != null) return result;
        }
    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {
        return new RandomizedQueueIterator();
    }

    private class RandomizedQueueIterator implements Iterator<Item> {
        private RandomizedQueue temp;

        public RandomizedQueueIterator() {
            temp = new RandomizedQueue<Item>();

            //copy values of global queue into temp
            temp.queue = new Object[queue.length];
            for (int i = 0; i < queue.length; i++) {
                temp.queue[i] = queue[i];
                if (temp.queue[i] != null) {
                    temp.size++;
                }
            }
        }

        public boolean hasNext(){
            return !temp.isEmpty();
        }
    
        public Item next(){
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return (Item) temp.dequeue();
        }

        public void remove(){
            throw new UnsupportedOperationException();
        }
    }

    // unit testing (required)
    public static void main(String[] args) {
        // // TEST 1
        // String test1 = "Object instantiation completed successfully: ";
        
        // RandomizedQueue<String> test = new RandomizedQueue<String>();
        // test.enqueue("test1");
        // boolean test1Result = test.sample() == "test1";
        // StdOut.println(test1 + test1Result);

        // // TEST 2
        // String test2 = "Array resizes when full: ";
        
        // test.enqueue("test2");
        // boolean test2Result = test.size == 2;
        // StdOut.println(test2 + test2Result);

        // // TEST 3
        // String test3 = "Array resizes when many items are dequeued: ";
        // test.enqueue("test3"); test.enqueue("test4");
        // test.dequeue(); test.dequeue(); test.dequeue(); test.dequeue();
        // boolean test3Result = test.size == 0;
        // StdOut.println(test3 + test3Result);

        // // TEST 4
        // String test4 = "Iterator works as expected: ";

        // test.enqueue("1") ; test.enqueue("2") ; test.enqueue("3") ; test.enqueue("4");
        // StringBuilder test4IteratedString = new StringBuilder();
        // for (String value : test) {
        //     test4IteratedString.append(value);
        // }
        // StdOut.println(test4 + test4IteratedString);

        // TEST 5
        RandomizedQueue<Integer> queue = new RandomizedQueue<>();
        queue.enqueue(29);
        queue.enqueue(17);
        StringBuilder testString = new StringBuilder();
        for (int value : queue) {
            testString.append(value);
        }
        StdOut.println(testString);
        queue.enqueue(19);
        queue.enqueue(41);
        queue.enqueue(38);
        queue.enqueue(40);
        StringBuilder newTestString = new StringBuilder();
        for (int value : queue) {
            newTestString.append(value);
        }
        StdOut.println(newTestString);
    }

}