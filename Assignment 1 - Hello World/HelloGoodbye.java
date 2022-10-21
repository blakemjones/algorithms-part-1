public class HelloGoodbye {
    public static void main(String[] args) {
        String helloNamePhrase = args.length < 2 ? "" : " " + args[0] + " and " + args[1];
        String goodbyeNamePhrase = args.length < 2 ? "" : " " + args[1] + " and " + args[0];
        System.out.println("Hello" + helloNamePhrase + ".");
        System.out.println("Goodbye" + goodbyeNamePhrase + ".");
    }
}
