package assignment1;

import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DocumentProcessor {

    private Set<String> stopWords;
    private InvertedIndex invertedIndex;
    private String delimiters;

    public DocumentProcessor(String stopWordsFile, String delimitersFile, InvertedIndex invertedIndex) throws IOException {

        this.invertedIndex = invertedIndex;

        stopWords = new HashSet<>();

        loadStopWords(stopWordsFile);
        loadDelimiters(delimitersFile);
    }

    
    private void loadStopWords(String fileName) throws IOException {

        for (String word : Files.readAllLines(Path.of(fileName)))
        {
            stopWords.add(word.trim().toLowerCase(Locale.ROOT));
        }
    }

    
    private void loadDelimiters(String fileName) throws IOException {

        String fileContent = Files.readString(Path.of(fileName));

        int start = fileContent.indexOf("String DELIMITERS");

        if (start == -1)
        {
            throw new IOException("DELIMITERS declaration not found!");
        }

        int equalsIndex = fileContent.indexOf('=', start);

        if (equalsIndex == -1)
        {
            throw new IOException("DELIMITERS assignment not found!");
        }

        String expression = "";

        boolean inString = false;
        boolean escaped = false;
        boolean inLineComment = false;
        boolean inBlockComment = false;

        for (int i = equalsIndex + 1; i < fileContent.length(); i++)
        {
            char c = fileContent.charAt(i);

            char next;

            if (i + 1 < fileContent.length())
            {
                next = fileContent.charAt(i + 1);
            }
            else
            {
                next = '\0';
            }

            if (inLineComment)
            {
                if (c == '\n')
                {
                    inLineComment = false;
                }

                continue;
            }

            if (inBlockComment)
            {
                if (c == '*' && next == '/')
                {
                    inBlockComment = false;
                    i++;
                }

                continue;
            }

            if (inString)
            {
                expression = expression + c;

                if (escaped)
                {
                    escaped = false;
                }
                else if (c == '\\')
                {
                    escaped = true;
                }
                else if (c == '"')
                {
                    inString = false;
                }

                continue;
            }

            if (c == '/' && next == '/')
            {
                inLineComment = true;
                i++;
                continue;
            }

            if (c == '/' && next == '*')
            {
                inBlockComment = true;
                i++;
                continue;
            }

            if (c == '"')
            {
                inString = true;
                expression = expression + c;
                continue;
            }

            if (c == ';')
            {
                break;
            }
        }

        Pattern stringPattern = Pattern.compile( "\"((?:\\\\.|[^\"\\\\])*)\"" );

        Matcher matcher = stringPattern.matcher(expression);

        String result = "";

        while (matcher.find())
        {
            result = result + decodeJavaString(matcher.group(1));
        }

        if (result.length() == 0)
        {
            throw new IOException("Could not extract delimiters!");
        }

        delimiters = result;

        Pattern.compile(delimiters);
    }

    
    private String decodeJavaString(String value) {

        String decoded = "";

        for (int i = 0; i < value.length(); i++) 
        {
            char c = value.charAt(i);

            if (c == '\\' && i + 1 < value.length())
            {
                char next = value.charAt(++i);

                switch (next)
                {
                    case 'r':
                        decoded = decoded + '\r';
                        break;

                    case 'n':
                        decoded = decoded + '\n';
                        break;

                    case 't':
                        decoded = decoded + '\t';
                        break;

                    case '\\':
                        decoded = decoded + '\\';
                        break;

                    case '"':
                        decoded = decoded + '"';
                        break;

                    default:
                        decoded = decoded + '\\' + next;
                        break;
                }
            }
            else
            {
                decoded = decoded + c;
            }
        }

        return decoded;
    }

    
    public void processDocument(String filePath, String documentName) throws IOException {

        String text = Files.readString(Path.of(filePath));

        String[] words = text.split(delimiters);

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
            throw new IOException("Can't read directory: " + directoryPath);
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
    }
    
    
}
