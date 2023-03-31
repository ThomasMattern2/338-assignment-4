package dtts;
import java.util.BitSet;
import java.util.Objects;

public class BloomFilter {
    private BitSet bitSet;
    private int bitSetSize;
    private int refreshCount;
    private int insertions;

    public BloomFilter(int bitSetSize, int refreshCount) {
        this.bitSetSize = bitSetSize;
        this.refreshCount = refreshCount;
        this.bitSet = new BitSet(bitSetSize);
        this.insertions = 0;
    }

    public void record(String s) {
        int h1 = Math.abs(hash1(s)) % bitSetSize;
        int h2 = Math.abs(hash2(s)) % bitSetSize;
        int h3 = Math.abs(hash3(s)) % bitSetSize;

        bitSet.set(h1);
        bitSet.set(h2);
        bitSet.set(h3);

        insertions++;
        if (insertions >= refreshCount) {
            System.out.println("Refreshing Bloom Filter...");
            bitSet.clear();
            insertions = 0;
        }
    }

    public boolean lookup(String s) {
        int h1 = Math.abs(hash1(s)) % bitSetSize;
        int h2 = Math.abs(hash2(s)) % bitSetSize;
        int h3 = Math.abs(hash3(s)) % bitSetSize;

        return bitSet.get(h1) && bitSet.get(h2) && bitSet.get(h3);
    }

    private int hash1(String s) {
        return Objects.hashCode(s);
    }

    private int hash2(String s) {
        int hash = 0;
        for (int i = 0; i < s.length(); i++) {
            hash = 31 * hash + s.charAt(i);
        }
        return hash;
    }

    private int hash3(String s) {
        int hash = 0;
        int magic = 131;
        for (int i = 0; i < s.length(); i++) {
            hash = magic * hash + s.charAt(i);
        }
        return hash;
    }
}
