package searcher;

import indexer.IndexerRunner;
import model.CoordinateIndex;
import model.CoordinateQuery;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import parser.CoordinateParser;
import utils.Logger;

import java.io.IOException;

/**
 * Created by duviteck. 22 Oct 2014.
 */
public class SearcherTest {
    @Test
    public void testPlaton() {
        CoordinateIndex searchIndex;
        try {
            Logger.log("Loading index from file...");
            searchIndex = CoordinateIndex.readIndexFromFile("index.inv");
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

        String query = "платон /100 сократ";

        Searcher searcher = new Searcher(searchIndex);
        long parseTime;
        long searchTime = 0;

        Pair<CoordinateQuery, Long> parsedQuery = CoordinateParser.parseWithTimeLog(query);
        parseTime = parsedQuery.getRight();

        if (parsedQuery.getLeft() != null) {
            Pair<String, Long> result = searcher.processQuery(parsedQuery.getLeft());
            System.out.println("\t" + result.getLeft());
            searchTime = result.getRight();
        } else {
            System.out.println("\t" + "incorrect query");
        }

    }
}
