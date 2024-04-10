import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


class FPNode {
    String item;
    int count;
    FPNode parent;
    List<FPNode> children;
    FPNode nodeLink;

    public FPNode(String item, FPNode parent) {
        this.item = item;
        this.count = 1;
        this.parent = parent;
        this.children = new ArrayList<>();
        this.nodeLink = null;
    }

    public void incrementCount() {
        this.count++;
    }

    public void addChild(FPNode child) {
        this.children.add(child);
    }

    public FPNode findChild(String item) {
        for (FPNode child : this.children) {
            if (child.item.equals(item)) {
                return child;
            }
        }
        return null;
    }
}

class FPTree {
    FPNode root;
    Map<String, List<FPNode>> headerTable;

    public FPTree() {
        this.root = new FPNode("null", null);
        this.headerTable = new HashMap<>();
    }

    public void addTransaction(List<String> transaction) {
        FPNode currentNode = this.root;
        for (String item : transaction) {
            FPNode child = currentNode.findChild(item);
            if (child != null) {
                child.incrementCount();
            } else {
                FPNode newNode = new FPNode(item, currentNode);
                currentNode.addChild(newNode);
                currentNode = newNode;

                if (!headerTable.containsKey(item)) {
                    headerTable.put(item, new ArrayList<>());
                }
                headerTable.get(item).add(newNode);
            }
            currentNode = child != null ? child : currentNode;
        }
    }

    public List<Set<String>> mineFrequentItemsets(int minSupport) {
        List<Set<String>> result = new ArrayList<>();
        for (String item : headerTable.keySet()) {
            int count = headerTable.get(item).stream().mapToInt(node -> node.count).sum();
            if (count >= minSupport) {
                Set<String> itemset = new HashSet<>();
                itemset.add(item);
                result.add(itemset);
                mine(itemset, item, minSupport, result);
            }
        }
        return result;
    }

    private void mine(Set<String> basePattern, String item, int minSupport, List<Set<String>> frequentItemsets) {
        List<List<String>> conditionalPatternBase = new ArrayList<>();
        for (FPNode node : headerTable.get(item)) {
            int count = node.count;
            List<String> path = new ArrayList<>();
            FPNode parent = node.parent;
            while (!parent.item.equals("null")) {
                path.add(parent.item);
                parent = parent.parent;
            }
            for (int i = 0; i < count; i++) {
                conditionalPatternBase.add(path);
            }
        }

        FPTree conditionalTree = buildConditionalTree(conditionalPatternBase);
        for (String newItem : conditionalTree.headerTable.keySet()) {
            int totalCount = conditionalTree.headerTable.get(newItem).stream().mapToInt(n -> n.count).sum();
            if (totalCount >= minSupport) {
                Set<String> newPattern = new HashSet<>(basePattern);
                newPattern.add(newItem);
                frequentItemsets.add(newPattern);
                conditionalTree.mine(newPattern, newItem, minSupport, frequentItemsets);
            }
        }
    }

    private FPTree buildConditionalTree(List<List<String>> conditionalPatternBase) {
        FPTree tree = new FPTree();
        for (List<String> pattern : conditionalPatternBase) {
            tree.addTransaction(pattern);
        }
        return tree;
    }
}

public class FPGrowthExample {
    public static void main(String[] args) {
        FPTree tree = new FPTree();

        String line;
        try (BufferedReader br = new BufferedReader(new FileReader("../groceries.csv"))) {
            while ((line = br.readLine()) != null) {
                List<String> transaction = Arrays.asList(line.split(","));
                tree.addTransaction(transaction);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 빈발 항목 집합을 추출할 최소 지지도 정의
        int minSupport = 5;  // 예시로 2를 사용했지만, 실제 데이터셋에 맞게 조정이 필요합니다.

        List<Set<String>> frequentItemsets = tree.mineFrequentItemsets(minSupport);
        for (Set<String> itemset : frequentItemsets) {
            System.out.println("Frequent Itemset: " + itemset);
        }
    }
}
