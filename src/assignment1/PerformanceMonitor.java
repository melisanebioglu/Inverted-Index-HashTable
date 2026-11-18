package assignment1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class PerformanceMonitor {

    public static double measureIndexingTime( DocumentProcessor processor, String directoryPath)
            throws IOException {

        long startTime = System.nanoTime();

        processor.processDirectory(directoryPath);

        long endTime = System.nanoTime();

        return (endTime - startTime) / 1_000_000.0;
    }


    public static double measureSearchTime( InvertedIndex index, String word) {

        long startTime = System.nanoTime();

        index.get(word);

        long endTime = System.nanoTime();

        return (endTime - startTime) / 1_000_000.0;
    }


    public static double[] measureSearchTimes( InvertedIndex index, String filePath) throws IOException {

        List<String> words = Files.readAllLines(Path.of(filePath));

        double[] searchTimes = new double[words.size()];

        for (int i = 0; i < words.size(); i++) 
        {

            String word = words.get(i).trim();

            searchTimes[i] = measureSearchTime(index, word);
        }

        return searchTimes;
    }


    public static double[] calculateSearchStatistics( double[] searchTimes) {

        if (searchTimes == null || searchTimes.length == 0)
        {
            throw new IllegalArgumentException("Search times can't be empty!");
        }

        double total = 0;
        double min = searchTimes[0];
        double max = searchTimes[0];

        for (int i = 0; i < searchTimes.length; i++)
        {

            total += searchTimes[i];

            if (searchTimes[i] < min) 
            {
                min = searchTimes[i];
            }

            if (searchTimes[i] > max) 
            {
                max = searchTimes[i];
            }
        }

        double average = total / searchTimes.length;

        return new double[] {average, min, max};
    }

    
    
    
}
