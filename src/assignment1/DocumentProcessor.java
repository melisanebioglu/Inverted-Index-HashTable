package assignment1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.io.File;

public class DocumentProcessor {

    private Set<String> stopWords;
    private InvertedIndex invertedIndex;

    
    private static final String DELIMITERS = "[-+=" +
            " " +        // space
            "\r\n " +    // carriage return and line feed
            "1234567890" + // numbers
            "’'\"" +       // apostrophe
            "(){}<>\\[\\]" + // brackets
            ":" +        // colon
            "," +        // comma
            "‒–—―" +     // dashes
            "…" +        // ellipsis
            "!" +        // exclamation mark
            "." +        // full stop/period
            "«»" +       // guillemets
            "-‐" +       // hyphen
            "?" +        // question mark
            "‘’“”" +     // quotation marks
            ";" +        // semicolon
            "/" +        // slash/stroke
            "⁄" +        // solidus
            "␠" +        // space symbol
            "·" +        // interpunct
            "&" +        // ampersand
            "@" +        // at sign
            "*" +        // asterisk
            "\\\\" +     // backslash
            "•" +        // bullet
            "^" +        // caret
            "¤¢$€£¥₩₪" + // currency
            "†‡" +       // dagger
            "°" +        // degree
            "¡" +        // inverted exclamation point
            "¿" +        // inverted question mark
            "¬" +        // negation
            "#" +        // number sign
            "№" +        // numero sign
            "%‰‱" +    // percent signs
            "¶" +        // pilcrow
            "′" +        // prime
            "§" +        // section sign
            "~" +        // tilde
            "¨" +        // umlaut/diaeresis
            "_" +        // underscore
            "|¦" +       // vertical/broken bar
            "⁂" +        // asterism
            "☞" +        // index/fist
            "∴" +        // therefore sign
            "‽" +        // interrobang
            "※" +        // reference mark
            "]";

    
    public DocumentProcessor(String stopWordsFile, InvertedIndex invertedIndex)
            throws IOException {

        this.invertedIndex = invertedIndex;
        stopWords = new HashSet<>();

        loadStopWords(stopWordsFile);
    }

    
    private void loadStopWords(String fileName) throws IOException {

        for (String word : Files.readAllLines(Path.of(fileName))) 
        {
            stopWords.add(word.trim().toLowerCase(Locale.ROOT));
        }
    }
    
    
    public void processDocument(String filePath, String documentName)
            throws IOException {

        String text = Files.readString(Path.of(filePath));

        String[] words = text.split(DELIMITERS);

        for (String word : words) 
        {

            String cleanedWord = word.toLowerCase(Locale.ROOT);

            if (!cleanedWord.isEmpty() && !stopWords.contains(cleanedWord))
            {

                invertedIndex.put(cleanedWord, documentName);
            }
        }
    }
    
    
    public void processDirectory(String directoryPath) throws IOException {
    	
    	File directory = new File(directoryPath);

        if (!directory.isDirectory()) 
        {
            throw new IOException("Directory not found: " + directoryPath);
        }

        File[] files = directory.listFiles();

        if (files == null) 
        {
            throw new IOException("Cannot read directory: " + directoryPath);
        }

        for (File file : files) 
        {

            if (file.isDirectory()) 
            {

                processDirectory(file.getPath());

            } 
            else if (file.isFile() && file.getName().toLowerCase(Locale.ROOT).endsWith(".txt"))
            {
                String documentName = file.getPath();

                processDocument(file.getPath(), documentName);
            }
        }  
        
        System.out.println("Directory finished: " + directoryPath);
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
}
