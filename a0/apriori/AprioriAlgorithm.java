import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class AprioriAlgorithm {
    private List<List<String>> transactions;
    private Map<Set<String>, Integer> itemsets;

    public AprioriAlgorithm() {
        transactions = new ArrayList<>();
        itemsets = new LinkedHashMap<>(); // Using LinkedHashMap to maintain insertion order
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
        Map<Set<String>, Integer> candidateSets = new HashMap<>();
        // Count item occurrences
        for (List<String> transaction : transactions) {
            for (String item : transaction) {
                Set<String> itemset = new HashSet<>();
                itemset.add(item);
                candidateSets.put(itemset, candidateSets.getOrDefault(itemset, 0) + 1);
            }
        }

        // Prune infrequent itemsets
        candidateSets.entrySet().removeIf(entry -> entry.getValue() < minSupport * transactions.size());

        itemsets.putAll(candidateSets);

        int k = 2; // Start with size 2
        while (!candidateSets.isEmpty()) {
            Map<Set<String>, Integer> newCandidates = generateCandidates(candidateSets, k);

            // Count occurrences of new candidates
            for (List<String> transaction : transactions) {
                for (Set<String> candidate : newCandidates.keySet()) {
                    if (transaction.containsAll(candidate)) {
                        newCandidates.put(candidate, newCandidates.get(candidate) + 1);
                    }
                }
            }

            // Prune infrequent candidates
            newCandidates.entrySet().removeIf(entry -> entry.getValue() < minSupport * transactions.size());

            itemsets.putAll(newCandidates);
            candidateSets = newCandidates;
            k++;
        }
    }

    private Map<Set<String>, Integer> generateCandidates(Map<Set<String>, Integer> prevCandidates, int k) {
        Map<Set<String>, Integer> candidates = new HashMap<>();
        for (Map.Entry<Set<String>, Integer> entry1 : prevCandidates.entrySet()) {
            for (Map.Entry<Set<String>, Integer> entry2 : prevCandidates.entrySet()) {
                Set<String> candidate = new HashSet<>(entry1.getKey());
                candidate.addAll(entry2.getKey());
                if (candidate.size() == k && !hasInfrequentSubset(candidate)) {
                    candidates.put(candidate, 0);
                }
            }
        }
        return candidates;
    }

    private boolean hasInfrequentSubset(Set<String> itemset) {
        List<Set<String>> subsets = generateSubsets(itemset);
        for (Set<String> subset : subsets) {
            if (!itemsets.containsKey(subset)) {
                return true;
            }
        }
        return false;
    }

    private List<Set<String>> generateSubsets(Set<String> itemset) {
        List<Set<String>> subsets = new ArrayList<>();
        String[] items = itemset.toArray(new String[0]);
        int n = items.length;

        for (int i = 1; i < (1 << n); i++) {
            Set<String> subset = new HashSet<>();
            for (int j = 0; j < n; j++) {
                if ((i & (1 << j)) > 0) {
                    subset.add(items[j]);
                }
            }
            subsets.add(subset);
        }
        return subsets;
    }

    public void printFrequentItemsetsSorted(double totalTransactions) {
        List<Map.Entry<Set<String>, Integer>> sortedItemsets = new ArrayList<>(itemsets.entrySet());
        sortedItemsets.sort(Comparator.comparingInt(Map.Entry::getValue));

        System.out.println("Frequent Itemsets (sorted by support ratio):");
        for (Map.Entry<Set<String>, Integer> entry : sortedItemsets) {
            double supportRatio = (double) entry.getValue() / totalTransactions;
            System.out.println(entry.getKey() + " : " + supportRatio);
        }
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java AprioriAlgorithm <input_file_name.csv> <minimum_support>");
            return;
        }

        String inputFile = args[0];
        double minSupport = Double.parseDouble(args[1]);

        AprioriAlgorithm apriori = new AprioriAlgorithm();
        try {
            apriori.loadTransactionsFromCSV(inputFile);
            double totalTransactions = apriori.transactions.size();
            apriori.generateFrequentItemsets(minSupport); // Minimum support
            apriori.printFrequentItemsetsSorted(totalTransactions);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
