package assignment1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

public class Main {

    private static final int INITIAL_CAPACITY = 17;
    private static final String DOCUMENT_DIRECTORY = "bbc";
    private static final String STOP_WORDS_FILE = "stop_words_en.txt";
    private static final String DELIMITERS_FILE = "delimiters.txt";
    private static final String SEARCH_FILE = "1000.txt";

    public static void main(String[] args) throws IOException {

        List<String> searchWords = Files.readAllLines(Path.of(SEARCH_FILE));

        System.out.println("\n========== PERFORMANCE EXPERIMENTS ==========\n");

        System.out.printf(
                "%-14s %-14s %-12s %-18s %-18s %-18s %-18s %-18s%n",
                "Hash",
                "Collision",
                "Load",
                "Collision",
                "Indexing",
                "Average",
                "Minimum",
                "Maximum"
        );

        System.out.printf(
                "%-14s %-14s %-12s %-18s %-18s %-18s %-18s %-18s%n",
                "Function",
                "Method",
                "Factor",
                "Count",
                "Time (ms)",
                "Search (ms)",
                "Search (ms)",
                "Search (ms)"
        );

        System.out.println("------------------------------------------------------------------------------------------------------------------------------------");

        runExperiment("SSF", "LP", 0.50, searchWords);
        runExperiment("SSF", "DH", 0.50, searchWords);

        runExperiment("PAF", "LP", 0.50, searchWords);
        runExperiment("PAF", "DH", 0.50, searchWords);

        runExperiment("SSF", "LP", 0.80, searchWords);
        runExperiment("SSF", "DH", 0.80, searchWords);

        runExperiment("PAF", "LP", 0.80, searchWords);
        runExperiment("PAF", "DH", 0.80, searchWords);

        System.out.println("------------------------------------------------------------------------------------------------------------------------------------");

        System.out.println("\n========== EXPERIMENT SETTINGS ==========");

        System.out.printf("%-25s : %d%n", "Initial Capacity", INITIAL_CAPACITY);
        System.out.printf("%-25s : %s%n", "Document Directory", DOCUMENT_DIRECTORY);
        System.out.printf("%-25s : %s%n", "Search File", SEARCH_FILE);
        System.out.printf("%-25s : %d%n", "Number of Search Words", searchWords.size());
        System.out.printf("%-25s : %s%n", "Load Factors", "50%, 80%");
        System.out.printf("%-25s : %s%n", "Hash Functions", "SSF, PAF");
        System.out.printf("%-25s : %s%n", "Collision Methods", "LP, DH");

        System.out.println("=========================================");
        System.out.println("\n========== EXPERIMENTS COMPLETED ==========");

    }

    
    private static void runExperiment( String hashFunction, String collisionMethod, double loadFactor, List<String> searchWords) throws IOException {

        InvertedIndex index = new InvertedIndex( INITIAL_CAPACITY, loadFactor,  hashFunction,  collisionMethod );

        DocumentProcessor processor = new DocumentProcessor( STOP_WORDS_FILE, DELIMITERS_FILE, index );

        long indexingStart = System.nanoTime();

        processor.processDirectory(DOCUMENT_DIRECTORY);

        long indexingEnd = System.nanoTime();

        double indexingTime = (indexingEnd - indexingStart) / 1_000_000.0;

        int collisionCount = index.getCollisionCount();

        long totalSearchTime = 0;
        long minSearchTime = Long.MAX_VALUE;
        long maxSearchTime = Long.MIN_VALUE;
        int searchedCount = 0;

        for (String word : searchWords)
        {
            word = word.trim().toLowerCase(Locale.ROOT);

            if (word.isEmpty())
            {
                continue;
            }

            long searchStart = System.nanoTime();

            index.get(word);

            long searchEnd = System.nanoTime();

            long elapsedTime = searchEnd - searchStart;

            totalSearchTime += elapsedTime;

            if (elapsedTime < minSearchTime)
            {
                minSearchTime = elapsedTime;
            }

            if (elapsedTime > maxSearchTime)
            {
                maxSearchTime = elapsedTime;
            }

            searchedCount++;
        }

        if (searchedCount == 0)
        {
            throw new IllegalStateException(
                    "No search words were found in the search file."
            );
        }

        if (searchedCount != 1000)
        {
            throw new IllegalStateException("Expected 1000 searches, but performed " + searchedCount
            );
        }

        double averageSearchTime = (double) totalSearchTime / searchedCount / 1_000_000.0;

        double minimumSearchTime = minSearchTime / 1_000_000.0;

        double maximumSearchTime = maxSearchTime / 1_000_000.0;

        System.out.printf( Locale.US,
                "%-14s %-14s %-12.2f %-18s %-18.4f %-18.7f %-18.7f %-18.7f%n",
                hashFunction,
                collisionMethod,
                loadFactor,
                collisionCount,
                indexingTime,
                averageSearchTime,
                minimumSearchTime,
                maximumSearchTime
        );

    }
    
    
}
