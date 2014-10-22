package model;

import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by duviteck. 21 Oct 2014.
 */
public class CoordinateIndexTest {
    private static final String SERIALIZED_INDEX_FILE_NAME = "serialized_index.tmp";

    @Test
    public void testSerialization() {
        List<String> fileNames = Arrays.asList("f1.txt", "f2.txt", "f3.txt");
        Map<String, FilePositionsIndex> termIndexesMap = new HashMap<>();

        // term "aaa"
        FilePositionsIndex filePositionsIndexAAA = new FilePositionsIndex();
        filePositionsIndexAAA.addFilePosition(0, 1);
        filePositionsIndexAAA.addFilePosition(0, 2);
        filePositionsIndexAAA.addFilePosition(1, 0);
        termIndexesMap.put("aaa", filePositionsIndexAAA);

        // term "bb"
        FilePositionsIndex filePositionsIndexBB = new FilePositionsIndex();
        filePositionsIndexBB.addFilePosition(0, 0);
        filePositionsIndexBB.addFilePosition(1, 1);
        filePositionsIndexBB.addFilePosition(1, 2);
        termIndexesMap.put("bb", filePositionsIndexBB);

        // term "c"
        FilePositionsIndex filePositionsIndexC = new FilePositionsIndex();
        filePositionsIndexC.addFilePosition(0, 3);
        filePositionsIndexC.addFilePosition(1, 3);
        filePositionsIndexC.addFilePosition(2, 3);
        termIndexesMap.put("c", filePositionsIndexC);

        // term "dddd"
        FilePositionsIndex filePositionsIndexDDDD = new FilePositionsIndex();
        filePositionsIndexDDDD.addFilePosition(0, 4);
        filePositionsIndexDDDD.addFilePosition(1, 4);
        filePositionsIndexDDDD.addFilePosition(1, 5);
        termIndexesMap.put("dddd", filePositionsIndexDDDD);

        CoordinateIndex originIndex = new CoordinateIndex(fileNames, termIndexesMap);
        try {
            originIndex.writeIndexToFile(SERIALIZED_INDEX_FILE_NAME);
            CoordinateIndex newIndex = CoordinateIndex.readIndexFromFile(SERIALIZED_INDEX_FILE_NAME);
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
