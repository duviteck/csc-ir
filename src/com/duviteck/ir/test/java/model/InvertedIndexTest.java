package model;

import org.junit.Test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by duviteck. 28 Sep 2014.
 */
public class InvertedIndexTest {
    private static final String SERIALIZED_INDEX_FILE_NAME = "serialized_index.tmp";

    @Test
    public void testSerialization() {
        List<String> fileNames = Arrays.asList("f1.txt", "f2.txt", "f3.txt");
        Map<String, List<Integer>> termIndexesMap = new HashMap<String, List<Integer>>();
        termIndexesMap.put("aaa", Arrays.asList(0, 1));
        termIndexesMap.put("bb", Arrays.asList(1));
        termIndexesMap.put("c", Arrays.asList(0, 1, 2));
        termIndexesMap.put("dddd", Arrays.asList(1, 2));

        InvertedIndex originIndex = new InvertedIndex(fileNames, termIndexesMap);
        try {
            originIndex.writeIndexToFile(SERIALIZED_INDEX_FILE_NAME);
            InvertedIndex newIndex = InvertedIndex.readIndexFromFile(SERIALIZED_INDEX_FILE_NAME);
            assertEquals(originIndex, newIndex);
        } catch (IOException e) {
            e.printStackTrace();
            fail("Can't write index to file or read it from file");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            fail();
        }
    }
}
