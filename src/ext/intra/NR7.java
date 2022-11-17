package ext.intra;

import ext.utils.Utils;

import java.io.File;
import java.util.*;

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
        List<String> result = new ArrayList<>();
        for (Map.Entry<String, List<Double>> entry : all.entrySet()) {
            List orig = entry.getValue();
            List<Double> sortedlist = new ArrayList<>(orig);
            Collections.sort(sortedlist);
            double min = sortedlist.get(0);
            if (min == (double) orig.get(0) && min == 3.0) {
                result.add(entry.getKey());
            }
        }
        int i = 1;
        System.out.println("Potential Intra-Day Stocks");
        for (String elem : result) {
            System.out.println(i++ + ". " + elem);
        }
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
}
