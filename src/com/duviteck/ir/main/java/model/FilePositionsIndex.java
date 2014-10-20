package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by duviteck. 21 Oct 2014.
 */
public class FilePositionsIndex implements Serializable {
    private List<Integer> fileIds;
    private List<List<Integer>> inFilePositions;
    private transient int filesCount;

    public FilePositionsIndex() {
        fileIds = new ArrayList<>();
        inFilePositions = new ArrayList<>();
        filesCount = 0;
    }

    public void addFilePosition(int fileId, int position) {
        if (filesCount > 0 && fileIds.get(filesCount - 1) == fileId) {
            inFilePositions.get(filesCount - 1).add(position);
        } else {
            fileIds.add(fileId);
            List<Integer> positionsList = new ArrayList<>();
            positionsList.add(position);
            inFilePositions.add(positionsList);
            filesCount++;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FilePositionsIndex) {
            FilePositionsIndex newIndex = (FilePositionsIndex) obj;
            return (fileIds.equals(newIndex.fileIds))
                    && (inFilePositions.equals(newIndex.inFilePositions));
        } else {
            return false;
        }
    }
}
