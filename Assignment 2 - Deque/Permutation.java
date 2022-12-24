import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class Permutation {
    public static void main(String[] args) {
        int k = Integer.valueOf(args[0]);
        RandomizedQueue<String> randomizedQueue = new RandomizedQueue<String>();
        
        while(!StdIn.isEmpty()) {
            String inputString = StdIn.readString();
            randomizedQueue.enqueue(inputString);
        }

        for (int i = 0; i < k; i++) {
            String chosenValue = randomizedQueue.dequeue();
            StdOut.println(chosenValue);
        }
    }
}
