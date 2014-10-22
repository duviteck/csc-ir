package indexer;

import model.CoordinateIndex;
import model.FilePositionsIndex;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.morphology.russian.RussianAnalyzer;
import utils.Logger;

import java.io.*;
import java.util.*;

/**
 * Created by duviteck. 27 Sep 2014.
 */
public class Indexer {
    private final String directoryToIndexFilename;

    public Indexer(String directoryToIndexFilename) {
        this.directoryToIndexFilename = directoryToIndexFilename;
    }

    public CoordinateIndex build() throws IOException {
        long start = System.currentTimeMillis();

        File directoryToIndex = new File(directoryToIndexFilename);
        if (!checkDirectory(directoryToIndex)) {
            throw new IllegalArgumentException("Specified directory doesn't exist");
        }

        List<String> fileNames = traverseDirectory(directoryToIndex);
        if (fileNames.isEmpty()) {
            throw new IllegalArgumentException("No files found in specified directory");
        }

        Map<String, FilePositionsIndex> termIndexesMap = new HashMap<>();
        int filesCount = fileNames.size();
        for (int i = 0; i < filesCount; i++) {
            indexFile(fileNames.get(i), i, termIndexesMap);
        }

        CoordinateIndex res = new CoordinateIndex(fileNames, termIndexesMap);

        long end = System.currentTimeMillis();
        Logger.log("Index is built in " + (end - start) + " ms");
        Logger.log("Total files indexed: " + fileNames.size());
        Logger.log("Total terms indexed: " + termIndexesMap.keySet().size());

        return res;
    }

    private boolean checkDirectory(File directory) {
        return (directory.exists() && directory.isDirectory());
    }

    // Traverse specified directory and return names of all inner files, including files in sub-folders
    private List<String> traverseDirectory(File directory) throws IOException {
        List<String> fileNames = new ArrayList<>();
        Queue<File> filesOrder = new ArrayDeque<>();
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

    private void indexFile(String fileToIndex, int fileNumber, Map<String, FilePositionsIndex> termIndexesMap) throws IOException {
        long start = System.currentTimeMillis();

        RussianAnalyzer analyzer = new RussianAnalyzer();
        TokenStream tokenStream = analyzer.tokenStream(null, new FileReader(fileToIndex));

        int tokenNumber = 0;
        while (tokenStream.incrementToken()) {
            String term = tokenStream.getAttribute(TermAttribute.class).term();

            FilePositionsIndex filePositionsIndex = termIndexesMap.get(term);
            if (filePositionsIndex == null) {
                FilePositionsIndex newFilePositionsIndex = new FilePositionsIndex();
                newFilePositionsIndex.addFilePosition(fileNumber, tokenNumber);
                termIndexesMap.put(term, newFilePositionsIndex);
            } else {
                filePositionsIndex.addFilePosition(fileNumber, tokenNumber);
            }

            tokenNumber++;
        }
        tokenStream.close();

        long end = System.currentTimeMillis();
        Logger.log("Indexed file: " + fileToIndex + " (" + (end - start) + "ms)");
    }
}
