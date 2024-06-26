package bot.tools;

import java.util.ArrayList;

public class GroupSplitter {
    public static ArrayList<Integer> getSplitGroupNumbers(Integer totalGroupSize) {
        ArrayList<Integer> groupSizes = new ArrayList<>();
        if (totalGroupSize % 2 == 0) {
            int oneGroupSize = totalGroupSize / 2;
            groupSizes.add(oneGroupSize);
            groupSizes.add(oneGroupSize);
        } else {
            int oneGroupSize = (totalGroupSize / 2) + 1;
            int otherGroupSize = totalGroupSize / 2;
            groupSizes.add(oneGroupSize);
            groupSizes.add(otherGroupSize);
        }
        System.out.println("[GroupSplitter] Total: " + totalGroupSize + ", Split: " + groupSizes);
        return groupSizes;
    }
}
