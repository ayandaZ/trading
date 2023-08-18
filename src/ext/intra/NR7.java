package ext.intra;

import ext.utils.Utils;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class NR7 {
    private final static Map<String, List<Double>> all = new HashMap<>();
    private final static Map<String, List<String>> prevDayDetails = new HashMap<>();

    private NR7() {}

    private static void processReports(String baseDir) {
        List<File> files = getLast7Days(baseDir);
        boolean firstDay = true;
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
            if(firstDay){
                /* Populating prevDayDetails */
                for (String line : lines) {
                    String[] elem = line.split(",");
                    String key = elem[0];
                    String high = elem[3];
                    String low = elem[4];
                    String price = elem[6];
                    prevDayDetails.put(key, Arrays.asList(high, low, price));
                }
                firstDay = false;
            }
        }
    }

    public static void calculate(String baseDir, int minDelta) {
        processReports(baseDir);
        List<String> stocks = new ArrayList<>();
        for (Map.Entry<String, List<Double>> entry : all.entrySet()) {
            List<Double> orig = entry.getValue();
            List<Double> sortedlist = new ArrayList<>(orig);
            Collections.sort(sortedlist);
            double min = sortedlist.get(0);
            if (min == orig.get(0) && min == minDelta) {
                stocks.add(entry.getKey());
            }
        }

        System.out.println("Potential Intra-Day Stocks in Ascending order");
        System.out.println("=============================================");

        /*Adding final result */
        Map<String, Double> stocksWithLastBuy = new HashMap<>();
        for (String stock : stocks) {
            List<String> prevDayDetail = prevDayDetails.get(stock);
            String key = stock + ",\t\t High=" + prevDayDetail.get(0) + ",\t Low=" + prevDayDetail.get(1);
            Double value = Double.parseDouble(prevDayDetail.get(2));
            stocksWithLastBuy.put(key, value);
        }

        /* Sorting */
        Map<String, Double> sortedMapAsc = sortByValue(stocksWithLastBuy);

        /* Display */
        printMap(sortedMapAsc);
    }

    private static List<File> getLast7Days(String base) {
        File directory = new File(base);
        File[] files = directory.listFiles();
        Objects.requireNonNull(files, "Cannot read from directory, files is null");
        Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
        return new ArrayList<>(Arrays.asList(files).subList(0, 7));
    }

    private static double getDelta(double high, double low) {
        double delta = ((high - low) / low) * 100;
        return Math.round(delta);
    }

    private static LinkedHashMap<String, Double> sortByValue(Map<String, Double> unSortMap)
    {
        List<Map.Entry<String, Double>> list = new LinkedList<>(unSortMap.entrySet());

        // Sorting the list based on values
        list.sort((o1, o2) -> o1.getValue().compareTo(o2.getValue()) == 0
                ? o1.getKey().compareTo(o2.getKey())
                : o1.getValue().compareTo(o2.getValue()));
        return list.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, LinkedHashMap::new));

    }

    private static void printMap(Map<String, Double> map)
    {
        AtomicInteger i= new AtomicInteger(1);
        map.forEach((key, value) -> System.out.println(i.getAndIncrement() + ". " + key + "\tLTB=" + value));
    }
}