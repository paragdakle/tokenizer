package utils;

import java.util.Comparator;
import java.util.HashMap;

public class FrequencyComparator<T, V extends Comparable<V>> implements Comparator<T> {

    private HashMap<T, V> map;

    public FrequencyComparator(HashMap<T, V> map) {
        this.map = map;
    }

    @Override
    public int compare(T keyA, T keyB) {
        return map.get(keyA).compareTo(map.get(keyB));
    }
}
