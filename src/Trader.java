import ext.intra.NR7;

public class Trader {
    public static void main(String[] args) {
        NR7.calculate(args[0], Integer.parseInt(args[1]));
        System.out.println("Report Complete.");
    }
}