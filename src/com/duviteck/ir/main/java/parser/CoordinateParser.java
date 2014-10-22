package parser;

import model.CoordinateQuery;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.morphology.russian.RussianAnalyzer;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by duviteck. 22 Oct 2014.
 */
public class CoordinateParser {

    public static CoordinateQuery parse(String query) {
        if (query.isEmpty()) {
            return null;
        }

        String[] tokens = query.split("[\\s]+");    // only tokens between empty spaces
        // number of tokens should be odd: (tokens.length / 2 + 1) terms + (tokens.length / 2) /(+|-)N operators
        if (tokens.length % 2 == 0) {
            return null;
        }

        // parse all coordinate separators
        List<CoordinateQuery.CoordinateSeparator> separators = new ArrayList<>(tokens.length / 2);
        for (int i = 1; i < tokens.length; i += 2) {
            CoordinateQuery.CoordinateSeparator separator = CoordinateQuery.CoordinateSeparator.parse(tokens[i]);
            if (separator == null) {
                return null;
            }
            separators.add(separator);
        }

        // normalize all terms
        List<String> terms = new ArrayList<>(tokens.length / 2 + 1);
        for (int i = 0; i < tokens.length; i += 2) {
            terms.add(tokens[i]);
        }
        List<String> normalizedTerms = normalizeTerms(terms);
        if (normalizedTerms == null) {
            return null;
        }

        return new CoordinateQuery(normalizedTerms, separators);
    }

    private static List<String> normalizeTerms(List<String> terms) {
        try {
            RussianAnalyzer analyzer = new RussianAnalyzer();
            List<String> res = new ArrayList<>(terms.size());

            for (String term : terms) {
                TokenStream tokenStream = analyzer.tokenStream(null, new StringReader(term));
                tokenStream.incrementToken();
                String normalizedTerm = tokenStream.getAttribute(TermAttribute.class).term();
//                System.out.println(normalizedTerm);
                res.add(normalizedTerm);
                tokenStream.close();
            }

            return res;
        } catch (IOException e) {
            System.out.println("Unknown error during normalizing query");
            e.printStackTrace();
            return null;
        }
    }

    public static Pair<CoordinateQuery, Long> parseWithTimeLog(String query) {
        long startTime = System.currentTimeMillis();
        CoordinateQuery parsedQuery = parse(query);
        long endTime = System.currentTimeMillis();
        return Pair.of(parsedQuery, endTime - startTime);
    }
}
