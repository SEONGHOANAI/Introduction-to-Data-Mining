import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

public class A1_G13_t1 {

    public static void main(String[] args) throws IOException {
        BufferedReader file = new BufferedReader(new FileReader(args[0]));
        // key: Item, value: Item's frequency
        HashMap<String, Integer> items = new HashMap<>();
        // num: number of items in the file
        int num = 0;
        List<List<String>> database = new LinkedList<>();
        String temp;
        // count items
        while ((temp = file.readLine()) != null) {
            num++;
            String[] line = temp.split(",");
            database.add(Stream.of(line).sorted().toList());
            for(String item: line) {
                if (!items.containsKey(item)) {
                    items.put(item, 1);
                }
                else {
                    items.replace(item, items.get(item) + 1);
                }
            }
        }
        file.close();
        // absolute minimum support
        final int transaction_num = num;
        final int support = (int) (Double.parseDouble(args[1]) * transaction_num);
        List<HashMap.Entry<String, Integer>> itemlist= new LinkedList<>(items.entrySet());
        // sorted item list
        itemlist.sort(HashMap.Entry.comparingByValue());
        List<HashMap.Entry<String, Integer>> list = new LinkedList<>(itemlist.stream().filter(x -> x.getValue() >= support).toList());
        list.sort(HashMap.Entry.comparingByKey());
        /*==========frequent 1-itemset==========*/
        List<List<String>> itemset1 = list.stream().map(x-> Collections.singletonList(x.getKey())).toList();
        List<Integer> frequency1 = list.stream().map(Map.Entry::getValue).toList();
        /*==========frequent itemset from Apriori algorithm==========*/
        List<List<String>> freqitemsets = new LinkedList<>();
        List<Integer> frequencies = new LinkedList<>();
        // implementation
        List<List<String>> itemset = itemset1;
        List<Integer> frequency = frequency1;
        while (!itemset.isEmpty()) {
            freqitemsets.addAll(itemset);
            frequencies.addAll(frequency);
            // candidate generation
            List<List<String>> candidates = apriori_gen(itemset);
            int[] counts = new int[candidates.size()];
            if (!candidates.isEmpty()) {
                // hashed candidates
                HashMap<List<String>, Integer> hashed = new HashMap<>();
                for (int i = 0; i < candidates.size(); i++) {
                    hashed.put(candidates.get(i), i);
                }
                for (List<String> t: database) {
                    List<List<String>> subsets = subset_gen(t, candidates.get(0).size());
                    // for each subset of the transaction, count if it exists in the candidates
                    for(List<String> subset: subsets) {
                        if(hashed.containsKey(subset)) counts[hashed.get(subset)]++;
                    }
                }
            }
            // next frequent k-itemset
            itemset = new LinkedList<>();
            frequency = new LinkedList<>();
            for (int i = 0; i < candidates.size(); i++) {
                if (counts[i] >= support) {
                    itemset.add(candidates.get(i));
                    frequency.add(counts[i]);
                }
            }
        }
        /*==========ordered frequent itemset==========*/
        HashMap<List<String>, Integer> ordered = new HashMap<>();
        for (int i = 0; i < freqitemsets.size(); i++) {
            ordered.put(freqitemsets.get(i), frequencies.get(i));
        }
        List<HashMap.Entry<List<String>, Integer>> orderedList= new LinkedList<>(ordered.entrySet());
        // sorted item list
        orderedList.sort(HashMap.Entry.comparingByValue());
        /*==========displaying the result==========*/
        for (HashMap.Entry<List<String>, Integer> it: orderedList) {
            int s = it.getKey().size();
            for (int i = 0; i < s; i++) {
                System.out.print(it.getKey().get(i));
                if (i < s-1) {
                    System.out.print(", ");
                }
            }
            System.out.println(" " + String.format("%.8f", (double) it.getValue()/transaction_num));
        }
    }
    public static List<List<String>> apriori_gen(List<List<String>> itemsets) {
        List<List<String>> candidates = new LinkedList<>();
        // self-join
        for (int i = 0; i < itemsets.size(); i++) {
            List<String> set1 = itemsets.get(i);
            String end1 = set1.get(set1.size()-1);
            List<String> prefix1 = get_prefix(set1);
            for (int j = i+1; j < itemsets.size(); j++) {
                List<String> set2 = itemsets.get(j);
                String end2 = set2.get(set2.size()-1);
                List<String> prefix2 = get_prefix(set2);
                if (prefix1.equals(prefix2)) {
                    prefix2.add(end1);
                    prefix2.add(end2);
                    candidates.add(prefix2);
                } else break;
            }
        }
        List<List<String>> pruned = new LinkedList<>();
        // prune
        int l = itemsets.get(0).size();
        for(List<String> c: candidates) {
            // subset in order
            List<List<String>> subsets = subset_gen(c, l);
            int i = 0;
            for (List<String> subset: subsets) {
                if (!itemsets.contains(subset)) {
                    i = 1;
                    break;
                }
            }
            if (i != 1) pruned.add(c);
        }
        return pruned;
    }
    public static List<String> get_prefix(List<String> itemset) {
        // returns prefix of the itemset
        if (itemset.size() != 1) {
            return new LinkedList<>(itemset.subList(0, itemset.size()-1));
        }
        else {
            return new LinkedList<>();
        }
    }
    // l-size subset generation function from the set
    public static List<List<String>> subset_gen(List<String> set, int l) {
        List<List<String>> subsets = new LinkedList<>();
        List<String> start = new LinkedList<>();
        subset_gen_aux(subsets, start, set, l, 0);
        return subsets;
    }
    public static void subset_gen_aux(List<List<String>> subsets, List<String> start, List<String> set, int l, int s) {
        for (int i = s; i <= set.size() - l; i++) {
            List<String> temp = new LinkedList<>(start);
            temp.add(set.get(i));
            if (l > 1) {
                subset_gen_aux(subsets, temp, set, l-1, i + 1);
            } else subsets.add(temp);
        }
    }
}