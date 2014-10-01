package searcher;

import com.sun.tools.javac.util.Pair;
import model.InvertedIndex;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.morphology.russian.RussianAnalyzer;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by duviteck. 27 Sep 2014.
 */
public class Searcher {

    private enum QueryType {
        AND("AND"), OR("OR");

        public final String value;

        QueryType (String value) {
            this.value = value;
        }
    }

    private final InvertedIndex invertedIndex;

    public Searcher (InvertedIndex invertedIndex) {
        this.invertedIndex = invertedIndex;
    }

    public String processQuery(String queryLine) {
        if (queryLine == null || queryLine.isEmpty()) {
            return null;
        }
        queryLine = queryLine.trim();

        Pair<QueryType, List<String>> query = parseQuery(queryLine);
        if (query == null) {
            return "incorrect query";
        }

        List<String> resultedFiles = processParsedQuery(query);
        return buildResultMessage(resultedFiles);
    }

    private Pair<QueryType, List<String>> parseQuery(String queryLine) {
        if (queryLine.isEmpty()) {
            return null;
        }

        String[] tokens = queryLine.split("[\\s]+");    // only tokens between empty spaces
        // number of tokens should be odd: (tokens.length / 2 + 1) terms + (tokens.length / 2) AND/OR operators
        if (tokens.length % 2 == 0) {
            return null;
        }

        // check that all operators are equal
        QueryType queryType = (tokens.length == 1) ? QueryType.AND
                : (QueryType.AND.value.equals(tokens[1])) ? QueryType.AND
                : (QueryType.OR.value.equals(tokens[1])) ? QueryType.OR : null;
        if (queryType == null) {
            return null;
        }
        for (int i = 3; i < tokens.length; i += 2) {
            if (!queryType.value.equals(tokens[i])) {
                return null;
            }
        }

        // normalize all terms
        List<String> terms = new ArrayList<>(tokens.length / 2 + 1);
        for (int i = 0; i < tokens.length; i += 2) {
            terms.add(tokens[i]);
        }
        List<String> normalizeTerms = normalizeTerms(terms);
        if (normalizeTerms == null) {
            return null;
        }

        return new Pair<>(queryType, normalizeTerms);
    }

    private List<String> normalizeTerms(List<String> terms) {
        try {
            RussianAnalyzer analyzer = new RussianAnalyzer();
            List<String> res = new ArrayList<>(terms.size());

            for (String term : terms) {
                TokenStream tokenStream = analyzer.tokenStream(null, new StringReader(term));
                tokenStream.incrementToken();
                String normalizedTerm = tokenStream.getAttribute(TermAttribute.class).term();
                System.out.println(normalizedTerm);
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

    private List<String> processParsedQuery(Pair<QueryType, List<String>> query) {
        QueryType queryType = query.fst;
        if (queryType == null) {
            return null;
        }
        List<String> terms = query.snd;
        int termsCount = terms.size();

        List<Integer> res = invertedIndex.getIndexesForTerm(terms.get(0));
        for (int i = 1; i < termsCount; i++) {
            List<Integer> termIndexes = invertedIndex.getIndexesForTerm(terms.get(i));
            res = (queryType == QueryType.AND) ? mergeAnd(res, termIndexes) : mergeOr(res, termIndexes);
        }

        return invertedIndex.getFilesForIndexes(res);
    }

    private List<Integer> mergeAnd(List<Integer> l1, List<Integer> l2) {
        int l1Len = l1.size();
        int l2Len = l2.size();
        int l1Index = 0;
        int l2Index = 0;
        List<Integer> res = new ArrayList<>(Math.min(l1Len, l2Len));

        while ((l1Index < l1Len) && (l2Index < l2Len)) {
            int l1Cur = l1.get(l1Index);
            int l2Cur = l2.get(l2Index);
            if (l1Cur < l2Cur) {
                l1Index++;
            } else if (l1Cur > l2Cur) {
                l2Index++;
            } else {
                res.add(l1Cur);
                l1Index++;
                l2Index++;
            }
        }
        return res;
    }

    private List<Integer> mergeOr(List<Integer> l1, List<Integer> l2) {
        int l1Len = l1.size();
        int l2Len = l2.size();
        int l1Index = 0;
        int l2Index = 0;
        List<Integer> res = new ArrayList<>(l1Len + l2Len);

        while ((l1Index < l1Len) && (l2Index < l2Len)) {
            int l1Cur = l1.get(l1Index);
            int l2Cur = l2.get(l2Index);
            if (l1Cur < l2Cur) {
                res.add(l1Cur);
                l1Index++;
            } else if (l1Cur > l2Cur) {
                res.add(l2Cur);
                l2Index++;
            } else {
                res.add(l1Cur);
                l1Index++;
                l2Index++;
            }
        }
        if (l1Index < l1Len) {
            for (int i = l1Index; i < l1Len; i++) {
                res.add(l1.get(i));
            }
        }
        if (l2Index < l2Len) {
            for (int i = l2Index; i < l2Len; i++) {
                res.add(l2.get(i));
            }
        }
        return res;
    }

    private String buildResultMessage(List<String> resultedFiles) {
        if (resultedFiles == null || resultedFiles.isEmpty()) {
            return "nothing found";
        } else {
            switch (resultedFiles.size()) {
                case 1:
                    return "found in " + resultedFiles.get(0);
                case 2:
                    return "found in " + resultedFiles.get(0) + " and " + resultedFiles.get(1);
                default:
                    return "found in " + resultedFiles.get(0) + ", " + resultedFiles.get(1) +
                            " and " + (resultedFiles.size() - 2) + " more";
            }
        }
    }
}
