package indexer;

import model.InvertedIndex;
import utils.Logger;

import java.io.IOException;

/**
 * Created by duviteck. 27 Sep 2014.
 */
public class IndexerRunner {

    public static void main(String[] args) {
        if (args == null || args.length != 2) {
            printUsage();
            return;
        }

        String folderName = args[0];
        String outputFile = args[1];

        Indexer indexer = new Indexer(folderName);
        try {
            InvertedIndex invertedIndex = indexer.build();

            Logger.log("Dumping index into file...");
            invertedIndex.writeIndexToFile(outputFile);
            Logger.log("File is saved");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printUsage() {
        System.out.println("Usage: directory_to_index output_index_file_path");
    }
}
