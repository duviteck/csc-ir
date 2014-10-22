package searcher;

import model.CoordinateIndex;
import model.CoordinateQuery;
import org.apache.commons.lang3.tuple.Pair;
import parser.CoordinateParser;
import utils.Logger;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Created by duviteck. 27 Sep 2014.
 */
public class SearcherRunner {
    private static final String INCORRECT_QUERY_LOG = "incorrect query";

    public static void main(String[] args) {
        if (args == null || args.length != 1) {
            printUsage();
            return;
        }

        String serializedIndexPath = args[0];
        CoordinateIndex searchIndex;
        try {
            Logger.log("Loading index from file...");
            searchIndex = CoordinateIndex.readIndexFromFile(serializedIndexPath);
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

        Searcher searcher = new Searcher(searchIndex);
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

            long parseTime;
            long searchTime = 0;

            Pair<CoordinateQuery, Long> parsedQuery = CoordinateParser.parseWithTimeLog(query);
            parseTime = parsedQuery.getRight();

            if (parsedQuery.getLeft() != null) {
                Pair<String, Long> result = searcher.processQuery(parsedQuery.getLeft());
                System.out.println("\t" + result.getLeft());
                searchTime = result.getRight();
            } else {
                System.out.println("\t" + INCORRECT_QUERY_LOG);
            }

            Logger.log("\t" + parseLog(parseTime));
            Logger.log("\t" + searchLog(searchTime));
        }
    }

    private static void printUsage() {
        System.out.println("Usage: inverted_index_file_path");
    }

    private static String parseLog(long parseTime) {
        return String.format("parsing: %d ms", parseTime);
    }

    private static String searchLog(long searchTime) {
        return String.format("search: %d ms", searchTime);
    }
}
