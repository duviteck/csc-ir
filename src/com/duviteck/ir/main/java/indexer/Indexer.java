package indexer;

import model.InvertedIndex;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.morphology.russian.RussianAnalyzer;

import java.io.*;
import java.util.*;

/**
 * Created by duviteck. 27 Sep 2014.
 */
public class Indexer {
    private static final boolean LOG_ENABLED = true;

    private final String directoryToIndexFilename;

    public Indexer(String directoryToIndexFilename) {
        this.directoryToIndexFilename = directoryToIndexFilename;
    }

    public InvertedIndex build() throws IOException {
        long start = System.currentTimeMillis();

        File directoryToIndex = new File(directoryToIndexFilename);
        if (!checkDirectory(directoryToIndex)) {
            throw new IllegalArgumentException("Specified directory doesn't exist");
        }

        List<String> fileNames = traverseDirectory(directoryToIndex);
        if (fileNames.isEmpty()) {
            throw new IllegalArgumentException("No files found in specified directory");
        }

        Map<String, List<Integer>> termIndexesMap = new HashMap<String, List<Integer>>();
        int filesCount = fileNames.size();
        for (int i = 0; i < filesCount; i++) {
            indexFile(fileNames.get(i), i, termIndexesMap);
        }

        InvertedIndex res = new InvertedIndex(fileNames, termIndexesMap);

        if (LOG_ENABLED) {
            long end = System.currentTimeMillis();
            System.out.println("Index is built in " + (end - start) + " ms");
            System.out.println("Total files indexed: " + fileNames.size());
            System.out.println("Total terms indexed: " + termIndexesMap.keySet().size());
        }

        return res;
    }

    private boolean checkDirectory(File directory) {
        return (directory.exists() && directory.isDirectory());
    }

    // Traverse specified directory and return names of all inner files, including files in sub-folders
    private List<String> traverseDirectory(File directory) throws IOException {
        List<String> fileNames = new ArrayList<String>();
        Queue<File> filesOrder = new ArrayDeque<File>();
        filesOrder.add(directory);

        while (!filesOrder.isEmpty()) {
            File curFile = filesOrder.poll();
            if (curFile.isDirectory()) {
                File[] innerFiles = curFile.listFiles();
                for (File inner : innerFiles) {
                    filesOrder.add(inner);
                }
            } else {
                fileNames.add(curFile.getPath());
            }
        }

        return fileNames;
    }

    private void indexFile(String fileToIndex, int fileNumber, Map<String, List<Integer>> termIndexesMap) throws IOException {
        long start = System.currentTimeMillis();

        RussianAnalyzer analyzer = new RussianAnalyzer();
        TokenStream tokenStream = analyzer.tokenStream(null, new FileReader(fileToIndex));

        while (tokenStream.incrementToken()) {
            String term = tokenStream.getAttribute(TermAttribute.class).term();

            List<Integer> termIndexes = termIndexesMap.get(term);
            if (termIndexes == null) {
                List<Integer> indexes = new ArrayList<Integer>();
                indexes.add(fileNumber);
                termIndexesMap.put(term, indexes);
            } else {
                int lastValue = termIndexes.get(termIndexes.size() - 1);
                if (lastValue != fileNumber) {  // so term indexes don't have repeated values
                    termIndexes.add(fileNumber);
                }
            }
        }
        tokenStream.close();

        if (LOG_ENABLED) {
            long end = System.currentTimeMillis();
            System.out.println("Indexed file: " + fileToIndex + " (" + (end - start) + "ms)");
        }
    }
}
