package bot.tools;

import java.util.ArrayList;

public class GroupSplitter {

    private static final int TWO_GROUPS = 2;

    public static ArrayList<Integer> splitIntoTwoGroups(int totalGroupSize) {
        ArrayList<Integer> groupSizes = new ArrayList<>();
        int firstGroupSize;
        int secondGroupSize;
        if (isEven(totalGroupSize)) {
            int eachGroupSize = totalGroupSize / TWO_GROUPS;
            firstGroupSize = eachGroupSize;
            secondGroupSize = eachGroupSize;
        } else {
            firstGroupSize = (totalGroupSize / TWO_GROUPS) + 1;
            secondGroupSize = totalGroupSize / TWO_GROUPS;
        }
        groupSizes.add(firstGroupSize);
        groupSizes.add(secondGroupSize);
        System.out.println("[GroupSplitter] Total: " + totalGroupSize + ", Split: " + groupSizes);
        return groupSizes;
    }

    private static boolean isEven(int totalGroupSize) {
        return totalGroupSize % 2 == 0;
    }
}
