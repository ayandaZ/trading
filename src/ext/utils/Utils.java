package ext.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Utils {
    public static List<String> readFile(File file) {
        List<String> lines = null;
        try {
            lines = FileUtils.readLines(file);
            lines.remove(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return lines;
    }
}
