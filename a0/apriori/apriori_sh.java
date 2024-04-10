import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class apriori_sh {
    private List<List<String>> transactions;
    private Map<Set<String>, Integer> itemsets;

    public apriori_sh() {
        transactions = new ArrayList<>();
        itemsets = new HashMap<>();
    }

    public void loadTransactionsFromCSV(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {
            List<String> transaction = Arrays.asList(line.split(","));
            transactions.add(transaction);
        }
        reader.close();
    }

    public void generateFrequentItemsets(double minSupport) {
        // Count item occurrences
        for (List<String> transaction : transactions) {
            for (String item : transaction) {
                Set<String> itemset = new HashSet<>();
                itemset.add(item);
                itemsets.put(itemset, itemsets.getOrDefault(itemset, 0) + 1);
            }
        }

        // Prune infrequent itemsets
        Iterator<Map.Entry<Set<String>, Integer>> iterator = itemsets.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Set<String>, Integer> entry = iterator.next();
            if (entry.getValue() < minSupport * transactions.size()) {
                iterator.remove();
            }
        }

        // Generate frequent itemsets of larger sizes
        int k = 1;
        while (!itemsets.isEmpty()) {
            Map<Set<String>, Integer> newCandidates = new HashMap<>();
            for (Set<String> itemset1 : itemsets.keySet()) {
                for (Set<String> itemset2 : itemsets.keySet()) {
                    if (itemset1.size() == k && itemset2.size() == k && !itemset1.equals(itemset2)) {
                        Set<String> newCandidate = new HashSet<>(itemset1);
                        newCandidate.addAll(itemset2);
                        if (!hasInfrequentSubset(newCandidate, k - 1)) {
                            newCandidates.put(newCandidate, 0);
                        }
                    }
                }
            }

            // Count occurrences of new candidates
            for (List<String> transaction : transactions) {
                for (Set<String> candidate : newCandidates.keySet()) {
                    if (transaction.containsAll(candidate)) {
                        newCandidates.put(candidate, newCandidates.get(candidate) + 1);
                    }
                }
            }

            // Prune infrequent candidates
            iterator = newCandidates.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Set<String>, Integer> entry = iterator.next();
                if (entry.getValue() < minSupport * transactions.size()) {
                    iterator.remove();
                }
            }

            itemsets.clear();
            itemsets.putAll(newCandidates);
            k++;
        }
    }

    private boolean hasInfrequentSubset(Set<String> itemset, int k) {
        if (k == 0) return true;
        for (String item : itemset) {
            Set<String> subset = new HashSet<>(itemset);
            subset.remove(item);
            if (!itemsets.containsKey(subset)) {
                return true;
            }
        }
        return false;
    }

    public void printFrequentItemsetsSorted() {
        List<Map.Entry<Set<String>, Integer>> sortedItemsets = new ArrayList<>(itemsets.entrySet());
        sortedItemsets.sort(Comparator.comparingInt(Map.Entry::getValue));

        System.out.println("Frequent Itemsets (sorted by support value):");
        for (Map.Entry<Set<String>, Integer> entry : sortedItemsets) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java apriori_sh <input_file_name.csv> <minimum_support>");
            return;
        }

        String inputFile = args[0];
        double minSupport = Double.parseDouble(args[1]);

        apriori_sh apriori = new apriori_sh();
        try {
            apriori.loadTransactionsFromCSV(inputFile);
            apriori.generateFrequentItemsets(minSupport); // Minimum support
            apriori.printFrequentItemsetsSorted();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
