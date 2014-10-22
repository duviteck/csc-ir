package model;

import java.util.List;

/**
 * Created by duviteck. 21 Oct 2014.
 */
public class CoordinateQuery {
    public List<String> terms;
    public List<CoordinateSeparator> separators;

    public CoordinateQuery(List<String> terms, List<CoordinateSeparator> separators) {
        this.terms = terms;
        this.separators = separators;
    }

    public static class CoordinateSeparator {
        public final int forward;
        public final int back;

        public CoordinateSeparator(int forward, int back) {
            this.forward = forward;
            this.back = back;
        }

        public static CoordinateSeparator parse(String line) {
            if (line.charAt(0) != '/' || line.length() < 2) {
                return null;
            }

            boolean hasForward = false;
            boolean hasBack = false;
            boolean hasSign = false;
            switch (line.charAt(1)) {
                case '+':
                    hasForward = true;
                    hasSign = true;
                    break;
                case '-':
                    hasBack = true;
                    hasSign = true;
                    break;
                default:
                    hasForward = true;
                    hasBack = true;
            }

            String lineNumber = line.substring(hasSign ? 2 : 1);
            int val;
            try {
                val = Integer.parseInt(lineNumber);
            } catch (NumberFormatException ex) {
                return null;
            }

            return new CoordinateSeparator(hasForward ? val : 0, hasBack ? val : 0);
        }
    }
}
