package com.helium.data;

import java.util.Arrays;

public final class FastIntMap<V> {

    private static final int DEFAULT_CAPACITY = 64;
    private static final float LOAD_FACTOR = 0.75f;

    private int[] keys;
    private boolean[] occupied;
    private Object[] values;
    private int size;
    private int capacity;
    private int threshold;

    public FastIntMap() {
        this(DEFAULT_CAPACITY);
    }

    public FastIntMap(int initialCapacity) {
        capacity = nextPowerOfTwo(initialCapacity);
        threshold = (int) (capacity * LOAD_FACTOR);
        keys = new int[capacity];
        values = new Object[capacity];
        occupied = new boolean[capacity];
    }

    @SuppressWarnings("unchecked")
    public V get(int key) {
        int index = indexOf(key);
        int i = index;
        while (occupied[i]) {
            if (keys[i] == key) {
                return (V) values[i];
            }
            i = (i + 1) & (capacity - 1);
            if (i == index) return null;
        }
        return null;
    }

    public void put(int key, V value) {
        if (size >= threshold) {
            resize();
        }

        int i = indexOf(key);
        while (occupied[i]) {
            if (keys[i] == key) {
                values[i] = value;
                return;
            }
            i = (i + 1) & (capacity - 1);
        }

        keys[i] = key;
        values[i] = value;
        occupied[i] = true;
        size++;
    }

    @SuppressWarnings("unchecked")
    public V remove(int key) {
        int i = indexOf(key);
        while (occupied[i]) {
            if (keys[i] == key) {
                V old = (V) values[i];
                occupied[i] = false;
                values[i] = null;
                size--;
                rehashFrom(i);
                return old;
            }
            i = (i + 1) & (capacity - 1);
        }
        return null;
    }

    public int size() {
        return size;
    }

    public void clear() {
        Arrays.fill(occupied, false);
        Arrays.fill(values, null);
        size = 0;
    }

    private int indexOf(int key) {
        return (key * 0x9E3779B9) & (capacity - 1);
    }

    @SuppressWarnings("unchecked")
    private void rehashFrom(int start) {
        int i = (start + 1) & (capacity - 1);
        while (occupied[i]) {
            int k = keys[i];
            Object v = values[i];
            occupied[i] = false;
            values[i] = null;
            size--;
            put(k, (V) v);
            i = (i + 1) & (capacity - 1);
        }
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        int oldCapacity = capacity;
        int[] oldKeys = keys;
        Object[] oldValues = values;
        boolean[] oldOccupied = occupied;

        capacity = oldCapacity << 1;
        threshold = (int) (capacity * LOAD_FACTOR);
        keys = new int[capacity];
        values = new Object[capacity];
        occupied = new boolean[capacity];
        size = 0;

        for (int i = 0; i < oldCapacity; i++) {
            if (oldOccupied[i]) {
                put(oldKeys[i], (V) oldValues[i]);
            }
        }
    }

    private static int nextPowerOfTwo(int value) {
        int v = value - 1;
        v |= v >> 1;
        v |= v >> 2;
        v |= v >> 4;
        v |= v >> 8;
        v |= v >> 16;
        return v + 1;
    }
}
