package model;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by duviteck. 27 Sep 2014.
 */
public class InvertedIndex {
    private final List<String> fileNames;
    private final Map<String, List<Integer>> termIndexesMap;

    public InvertedIndex(List<String> fileNames, Map<String, List<Integer>> termIndexesMap) {
        this.fileNames = fileNames;
        this.termIndexesMap = termIndexesMap;
    }

    public List<Integer> getIndexesForTerm(String term) {
        List<Integer> res = termIndexesMap.get(term);
        if (res == null) {
            res = Collections.emptyList();
        }
        return res;
    }

    public List<String> getFilesForIndexes(List<Integer> indexes) {
        if (indexes == null || indexes.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> files = new ArrayList<String>(indexes.size());
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

    public static InvertedIndex readIndexFromFile(String fileName) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(new File(fileName));
        ObjectInputStream ois = new ObjectInputStream(fis);
        List<String> fileNames = (List<String>) ois.readObject();
        Map<String, List<Integer>> termIndexesMap = (Map<String, List<Integer>>) ois.readObject();
        return new InvertedIndex(fileNames, termIndexesMap);
    }



    @Override
    public boolean equals(Object obj) {
        if (obj instanceof InvertedIndex) {
            InvertedIndex newIndex = (InvertedIndex) obj;
            return (fileNames.equals(newIndex.fileNames))
                    && (termIndexesMap.equals(newIndex.termIndexesMap));
        } else {
            return false;
        }
    }
}
