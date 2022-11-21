import ext.intra.NR7;

public class Trader {
    public static void main(String[] args) {

        if(args.length == 3 && args[0].equals("nr7")) {
            NR7.calculate(args[1], Integer.parseInt(args[2]));
            System.out.println("Report Complete.");
        } else{
            printUsage();
        }
    }

    private static void printUsage() {
        String str = "HELP:\njava -cp Trader.jar Trader nr7 \"D:\\Trading\\NR7Zips\" 4";
        System.out.println(str);
    }
}