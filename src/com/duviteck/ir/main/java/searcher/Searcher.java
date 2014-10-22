package searcher;

import model.CoordinateIndex;
import model.CoordinateQuery;
import model.FilePositionsIndex;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

/**
 * Created by duviteck. 27 Sep 2014.
 */
public class Searcher {
    private final CoordinateIndex searchIndex;

    public Searcher (CoordinateIndex searchIndex) {
        this.searchIndex = searchIndex;
    }

    public Pair<String, Long> processQuery(CoordinateQuery query) {
        long startTime = System.currentTimeMillis();
        List<String> resultedFiles = processParsedQuery(query);
        long endTime = System.currentTimeMillis();
        return Pair.of(buildResultMessage(resultedFiles), endTime - startTime);
    }

    private List<String> processParsedQuery(CoordinateQuery query) {
        int termsCount = query.separators.size();

        FilePositionsIndex res = searchIndex.getFilePositionsIndexForTerm(query.terms.get(0));
        for (int i = 1; i < termsCount; i++) {
            FilePositionsIndex termFilePositionsIndex = searchIndex.getFilePositionsIndexForTerm(query.terms.get(i));
            CoordinateQuery.CoordinateSeparator separator = query.separators.get(i - 1);
            res = merge(res, termFilePositionsIndex, separator);
        }

        return searchIndex.getFilesForIndexes(res.getFileIds());
    }

    private FilePositionsIndex merge(FilePositionsIndex i1, FilePositionsIndex i2, CoordinateQuery.CoordinateSeparator separator) {
        int l1Len = i1.size();
        int l2Len = i2.size();
        int l1Index = 0;
        int l2Index = 0;
        FilePositionsIndex res = new FilePositionsIndex();

        while ((l1Index < l1Len) && (l2Index < l2Len)) {
            int l1Cur = i1.getFileId(l1Index);
            int l2Cur = i2.getFileId(l2Index);
            if (l1Cur < l2Cur) {
                l1Index++;
            } else if (l1Cur > l2Cur) {
                l2Index++;
            } else {
                mergeFilePositions(res, l1Cur, i1.getInFilePositions(l1Cur), i2.getInFilePositions(l2Cur), separator);
                l1Index++;
                l2Index++;
            }
        }
        return res;
    }

    private void mergeFilePositions(FilePositionsIndex index, int fileId,
                                    List<Integer> inFilePositions1,
                                    List<Integer> inFilePositions2,
                                    CoordinateQuery.CoordinateSeparator separator) {
        TreeSet<Integer> resultSet = new TreeSet<>();
        if (separator.back > 0) {
            mergeNegativeShift(resultSet, inFilePositions1, inFilePositions2, separator.back);
        }
        if (separator.forward > 0) {
            mergePositiveShift(resultSet, inFilePositions1, inFilePositions2, separator.forward);
        }
        for (int inFilePosition : resultSet) {
            index.addFilePosition(fileId, inFilePosition);
        }
    }

    private void mergeNegativeShift(Set<Integer> resultSet, List<Integer> l1, List<Integer> l2, int shift) {
        int l1Len = l1.size();
        int l2Len = l2.size();
        int l1Index = 0;
        int l2Index = 0;

        while ((l1Index < l1Len) && (l2Index < l2Len)) {
            int l1Cur = l1.get(l1Index);
            int l2Cur = l2.get(l2Index);
            if (l2Cur > l1Cur) {
                l1Index++;
            } else {
                if (l1Cur - l2Cur <= shift) {
                    resultSet.add(l2Cur);
                }
                l2Index++;
            }
        }
    }

    private void mergePositiveShift(Set<Integer> resultSet, List<Integer> l1, List<Integer> l2, int shift) {
        int l1Len = l1.size();
        int l2Len = l2.size();
        int l1Index = 0;
        int l2Index = 0;

        while ((l1Index < l1Len) && (l2Index < l2Len)) {
            int l1Cur = l1.get(l1Index);
            int l2Cur = l2.get(l2Index);
            if (l1Cur > l2Cur) {
                l2Index++;
            } else {
                if (l2Cur - l1Cur <= shift) {
                    resultSet.add(l2Cur);
                    l2Index++;
                } else {
                    l1Index++;
                }
            }
        }
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
