package ext.intra;

import ext.utils.Utils;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class NR7 {
    private static Map<String, List<Double>> all = new HashMap<>();
    private static String baseDir = "D:\\Trading\\NR7Zips";

    private NR7() {

    }

    private static void processReports() {
        List<File> files = getlast7Days(baseDir);
        System.out.println("Files size=" + files.size());
        for (File file : files) {
            List<String> lines = Utils.readFile(file);
            for (String line : lines) {
                String[] elem = line.split(",");
                String key = elem[0];
                String high = elem[3];
                String low = elem[4];
                String series = elem[1];

                if (series.equals("EQ")) {
                    double delta = getDelta(Double.parseDouble(high), Double.parseDouble(low));
                    if (all.containsKey(key)) {
                        List<Double> value = all.get(key);

                        value.add(delta);
                    } else {
                        List<Double> value = new ArrayList<>();
                        value.add(delta);
                        all.put(key, value);
                    }
                }

            }
        }
        System.out.println(all);
    }

    public static void calculate() {
        processReports();
        List<String> stocks = new ArrayList<>();
        for (Map.Entry<String, List<Double>> entry : all.entrySet()) {
            List orig = entry.getValue();
            List<Double> sortedlist = new ArrayList<>(orig);
            Collections.sort(sortedlist);
            double min = sortedlist.get(0);
            if (min == (double) orig.get(0) && min == 3.0) {
                stocks.add(entry.getKey());
            }
        }

        System.out.println("Potential Intra-Day Stocks in Ascending order");

        List<File> files = getlast7Days(baseDir);
        File file = files.get(0);
        List<String> lines = Utils.readFile(file);
        Map<String, Double> priceMap = new HashMap<>();
        for (String line : lines) {
            String[] elem = line.split(",");
            String key = elem[0];
            Double price = Double.parseDouble(elem[6]);
            priceMap.put(key, price);
        }
        Map<String, Double> stocksWithLastBuy = new HashMap<>();
        for (String elem : stocks) {
            stocksWithLastBuy.put(elem, priceMap.get(elem));
        }

        Map<String, Double> sortedMapAsc = sortByValue(stocksWithLastBuy, true);
        printMap(sortedMapAsc);
    }

    private static List<File> getlast7Days(String base) {
        File directory = new File(base);
        List<File> result = new ArrayList<>();
        File[] files = directory.listFiles();
        Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
        for (int i = 0; i < 7; i++) {
            result.add(files[i]);
        }
        return result;
    }

    private static double getDelta(double high, double low) {
        double delta = ((high - low) / low) * 100;
        return Math.round(delta);
    }

    private static LinkedHashMap<String, Double> sortByValue(Map<String, Double> unsortMap, final boolean order)
    {
        List<Map.Entry<String, Double>> list = new LinkedList<>(unsortMap.entrySet());

        // Sorting the list based on values
        list.sort((o1, o2) -> order ? o1.getValue().compareTo(o2.getValue()) == 0
                ? o1.getKey().compareTo(o2.getKey())
                : o1.getValue().compareTo(o2.getValue()) : o2.getValue().compareTo(o1.getValue()) == 0
                ? o2.getKey().compareTo(o1.getKey())
                : o2.getValue().compareTo(o1.getValue()));
        LinkedHashMap<String, Double> collect = list.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, LinkedHashMap::new));
        return collect;

    }

    private static void printMap(Map<String, Double> map)
    {
        AtomicInteger i= new AtomicInteger(1);
        map.forEach((key, value) -> System.out.println(i.getAndIncrement() + ". " + key + "\t" + value));
    }
}
