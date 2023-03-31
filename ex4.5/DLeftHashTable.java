package edu.ucalgary.oop;
import java.util.LinkedList;

public class DLeftHashTable {
    private int buckets;
    private LinkedList<Pair>[] leftTable;
    private LinkedList<Pair>[] rightTable;

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
        int leftIndex = Math.floorMod(key.hashCode(), buckets);
        int rightIndex = Math.floorMod(key.hashCode() * 31, buckets);

        int leftSize = leftTable[leftIndex].size();
        int rightSize = rightTable[rightIndex].size();

        if (leftSize <= rightSize) {
            leftTable[leftIndex].add(new Pair(key, value));
        } else {
            rightTable[rightIndex].add(new Pair(key, value));
        }
    }

    public Integer lookup(String key) {
        int leftIndex = Math.floorMod(key.hashCode(), buckets);
        int rightIndex = Math.floorMod(key.hashCode() * 31, buckets);

        for (Pair entry : leftTable[leftIndex]) {
            if (entry.getKey().equals(key)) {
                return entry.getValue();
            }
        }

        for (Pair entry : rightTable[rightIndex]) {
            if (entry.getKey().equals(key)) {
                return entry.getValue();
            }
        }

        return null;
    }

    private static class Pair {
        private final String key;
        private final int value;

        public Pair(String key, int value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public int getValue() {
            return value;
        }
    }
}
