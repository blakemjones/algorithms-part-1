import java.util.Iterator;
import java.util.NoSuchElementException;
import edu.princeton.cs.algs4.StdOut;

public class Deque<Item>  implements Iterable<Item> 
{
    private class Node {
        Item item;
        Node next;
        Node previous;
    }

    private Node first;
    private Node last;
    private int size;

    // construct an empty deque
    public Deque() {
        first = new Node();
        last = first;
        size = 0;
    }

    // is the deque empty?
    public boolean isEmpty() {
        return first.item == null;
    }

    // return the number of items on the deque
    public int size() {
        return this.size;
    }

    // add the item to the front
    public void addFirst(Item item) {
        if (this.isEmpty()) {
            first.item = item;
            size++;
        }
        else {
            Node newFirstNode = new Node();
            newFirstNode.item = item;
            first.next = newFirstNode;
            newFirstNode.previous = first;
            first = newFirstNode;
            size++;
        }
    }

    // add the item to the back
    public void addLast(Item item) {
        if (this.isEmpty()) {
            last.item = item;
            size++;
        }
        else {
            Node newLastNode = new Node();
            newLastNode.item = item;
            last.previous = newLastNode;
            newLastNode.next = last;
            last = newLastNode;
            size++;
        }
        
    }

    // remove and return the item from the front
    public Item removeFirst() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        if (this.size == 1) {
            Item result = this.first.item;
            this.first = new Node(); this.last = this.first;
            size--;
            return result;
        }
        Item result = this.first.item;
        Node newFirst = this.first.previous;
        first.previous = null;
        newFirst.next = null;
        size--;
        first = newFirst;
        return result;
    }

    // remove and return the item from the back
    public Item removeLast() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        if (this.size == 1) {
            Item result = this.last.item;
            this.last = new Node(); this.first = this.last;
            size--;
            return result;
        }
        Item result = this.last.item;
        Node newLast = this.last.next;
        last.next = null;
        newLast.previous = null;
        size--;
        last = newLast;
        return result;
    }

    //return an iterator over items in order from front to back
    public Iterator<Item> iterator() {
        return new DequeIterator();
    }

    private class DequeIterator implements Iterator<Item> {
        private Node current = last;

        public boolean hasNext(){
            return current != null;
        }
    
        public Item next(){
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Item item = this.current.item;
            current = current.next;
            return item;
        }

        public void remove(){
            throw new UnsupportedOperationException();
        }
    }

    // unit testing (required)
    public static void main(String[] args) {
        //TEST 1
        String test1 = "Object instantiation completed successfully: ";
        
        Deque<String> test = new Deque<String>();
        test.addFirst("test1");

        boolean test1Result = test.first.item == test.last.item && test.size == 1;
        StdOut.println(test1 + test1Result);

        //TEST 2
        String test2 = "Adding nodes to front and back leads to expected structure: ";

        test.addFirst("test2");
        test.addLast("test3");

        boolean test2Result = test.first.item == "test2" && test.last.item == "test3" && test.last.next.item == "test1";
        StdOut.println(test2 + test2Result);
        
        //TEST 3
        String test3 = "Iterator works as expected: ";
        
        String test3ExpectedString = "test3test1test2";
        StringBuilder test3IteratedString = new StringBuilder();
        for (String value : test) {
            test3IteratedString.append(value);
        }
        boolean test3Result = test3ExpectedString.equals(test3IteratedString.toString());
        StdOut.println(test3 + test3Result);

        //TEST 4
        String test4 = "Removing items works as expected: ";

        test.removeFirst();

        boolean test4Result = test.size == 2 && test.first.item == "test1";
        StdOut.println(test4 + test4Result);

        // TEST 5
        Deque<Integer> deque = new Deque<>();
        deque.addFirst(1);
        deque.removeLast();
        deque.addFirst(3);
        deque.addFirst(4);
        deque.removeLast();
    }
}