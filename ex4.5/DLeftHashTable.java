package edu.ucalgary.oop;
import java.util.LinkedList;

public class DLeftHashTable {
    private int buckets;
    private LinkedList<Entry>[] leftTable;
    private LinkedList<Entry>[] rightTable;

    public DLeftHashTable(int buckets) {
        this.buckets = buckets;
        leftTable = new LinkedList[buckets];
        rightTable = new LinkedList[buckets];

        for (int i = 0; i < buckets; i++) {
            leftTable[i] = new LinkedList<>();
            rightTable[i] = new LinkedList<>();
        }
    }

    public void insert(String key, int value) {
        int leftIndex = hash1(key);
        int rightIndex = hash2(key);

        int leftSize = leftTable[leftIndex].size();
        int rightSize = rightTable[rightIndex].size();

        if (leftSize <= rightSize) {
            leftTable[leftIndex].add(new Entry(key, value));
        } else {
            rightTable[rightIndex].add(new Entry(key, value));
        }
    }

    public Integer lookup(String key) {
        int leftIndex = hash1(key);
        int rightIndex = hash2(key);

        for (Entry entry : leftTable[leftIndex]) {
            if (entry.key.equals(key)) {
                return entry.value;
            }
        }

        for (Entry entry : rightTable[rightIndex]) {
            if (entry.key.equals(key)) {
                return entry.value;
            }
        }

        return null;
    }

    private int hash1(String key) {
        return Math.floorMod(key.hashCode(), buckets);
    }

    private int hash2(String key) {
        return Math.floorMod(key.hashCode() * 31, buckets);
    }
    private static class Entry {
        String key;
        int value;

        public Entry(String key, int value) {
            this.key = key;
            this.value = value;
        }
    }
    public static void main(String[] args) {
        DLeftHashTable dLeftHashTable = new DLeftHashTable(10);

        // Insert some key-value pairs into the hash table
        dLeftHashTable.insert("apple", 1);
        dLeftHashTable.insert("banana", 2);
        dLeftHashTable.insert("cherry", 3);
        dLeftHashTable.insert("date", 4);
        dLeftHashTable.insert("fig", 5);

        // Lookup and print the values
        System.out.println("apple: " + dLeftHashTable.lookup("apple"));
        System.out.println("banana: " + dLeftHashTable.lookup("banana"));
        System.out.println("cherry: " + dLeftHashTable.lookup("cherry"));
        System.out.println("date: " + dLeftHashTable.lookup("date"));
        System.out.println("fig: " + dLeftHashTable.lookup("fig"));

        // Test with a key that doesn't exist
        System.out.println("grape: " + dLeftHashTable.lookup("grape"));
    }
}
