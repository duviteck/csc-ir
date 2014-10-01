package searcher;

import model.InvertedIndex;

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
            invertedIndex = InvertedIndex.readIndexFromFile(invertedIndexPath);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Can't load specified index file");
            e.printStackTrace();
            return;
        }

        Searcher searcher = new Searcher(invertedIndex);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the query");
        while (true) {
            String query;
            try {
                query = scanner.nextLine();
            } catch (NoSuchElementException e) {
                break;
            }
            if (query == null || query.isEmpty()) {
                break;
            }

            String result = searcher.processQuery(query);
            System.out.println("\t" + result);
        }
    }

    private static void printUsage() {
        System.out.println("Usage: inverted_index");
    }
}
