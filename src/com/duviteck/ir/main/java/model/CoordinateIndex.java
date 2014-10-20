package model;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by duviteck. 21 Oct 2014.
 */
public class CoordinateIndex {
    private final List<String> fileNames;
    private final Map<String, FilePositionsIndex> termIndexesMap;

    public CoordinateIndex(List<String> fileNames, Map<String, FilePositionsIndex> termIndexesMap) {
        this.fileNames = fileNames;
        this.termIndexesMap = termIndexesMap;
    }

    public FilePositionsIndex getFilePositionsIndexForTerm(String term) {
        return termIndexesMap.get(term);
    }

    public List<String> getFilesForIndexes(List<Integer> indexes) {
        if (indexes == null || indexes.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> files = new ArrayList<>(indexes.size());
        for (int index : indexes) {
            files.add(fileNames.get(index));
        }
        return files;
    }

    public void writeIndexToFile(String fileName) throws IOException {
        FileOutputStream fos = new FileOutputStream(new File(fileName));
        ObjectOutputStream ous = new ObjectOutputStream(fos);
        ous.writeObject(fileNames);
        ous.writeObject(termIndexesMap);
        ous.close();
    }

    public static CoordinateIndex readIndexFromFile(String fileName) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(new File(fileName));
        ObjectInputStream ois = new ObjectInputStream(fis);
        List<String> fileNames = (List<String>) ois.readObject();
        Map<String, FilePositionsIndex> termIndexesMap = (Map<String, FilePositionsIndex>) ois.readObject();
        return new CoordinateIndex(fileNames, termIndexesMap);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CoordinateIndex) {
            CoordinateIndex newIndex = (CoordinateIndex) obj;
            return (fileNames.equals(newIndex.fileNames))
                    && (termIndexesMap.equals(newIndex.termIndexesMap));
        } else {
            return false;
        }
    }
}
