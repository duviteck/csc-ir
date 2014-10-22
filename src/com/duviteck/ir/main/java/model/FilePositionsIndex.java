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

    public FilePositionsIndex() {
        fileIds = new ArrayList<>();
        inFilePositions = new ArrayList<>();
    }

    public void addFilePosition(int fileId, int position) {
        if (fileIds.size() > 0 && fileIds.get(fileIds.size() - 1) == fileId) {
            inFilePositions.get(fileIds.size() - 1).add(position);
        } else {
            fileIds.add(fileId);
            List<Integer> positionsList = new ArrayList<>();
            positionsList.add(position);
            inFilePositions.add(positionsList);
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

    public List<Integer> getFileIds() {
        return fileIds;
    }

    public int getFileId(int index) {
        return fileIds.get(index);
    }

    public List<Integer> getInFilePositions(int fileId) {
        return inFilePositions.get(fileIds.indexOf(fileId));
    }

    public int size() {
        return fileIds.size();
    }
}
