package searcher;

import model.InvertedIndex;
import org.apache.commons.lang3.tuple.Pair;
import utils.Logger;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Created by duviteck. 27 Sep 2014.
 */
public class SearcherRunner {

    public static void main(String[] args) {
        if (args == null || args.length != 1) {
            printUsage();
            return;
        }

        String invertedIndexPath = args[0];
        InvertedIndex invertedIndex;
        try {
            Logger.log("Loading index from file...");
            invertedIndex = InvertedIndex.readIndexFromFile(invertedIndexPath);
        } catch (IOException e) {
            System.out.println("Can't load specified index file");
            e.printStackTrace();
            return;
        } catch (ClassNotFoundException e) {
            System.out.println("Can't load specified index file");
            e.printStackTrace();
            return;
        }
        Logger.log("Index is loaded");

        Searcher searcher = new Searcher(invertedIndex);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the query:");
        while (true) {
            String query;
            try {
                query = scanner.nextLine();
            } catch (NoSuchElementException e) {
                break;
            }
            if (query == null || query.isEmpty()) {
                System.out.println("Searcher is stopped");
                break;
            }

            Pair<String, String> result = searcher.processQuery(query);
            if (result != null) {
                System.out.println("\t" + result.getLeft());
                Logger.log("\t" + result.getRight());
            }
        }
    }

    private static void printUsage() {
        System.out.println("Usage: inverted_index_file_path");
    }
}
