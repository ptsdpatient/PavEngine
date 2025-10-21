package com.pavengine.app;

import java.util.Iterator;

public class Range implements Iterable<Integer> {
    private final int start, end, step;

    private Range(int start, int end, int step) {
        this.start = start;
        this.end = end;
        this.step = step;
    }

    // Python-style range methods
    public static Range range(int size) {
        return new Range(0, size, 1);
    }

    public static Range range(int start, int end) {
        return new Range(start, end, 1);
    }

    public static Range range(int start, int end, int step) {
        return new Range(start, end, step);
    }

    @Override
    public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {
            private int current = start;

            @Override
            public boolean hasNext() {
                return step > 0 ? current < end : current > end;
            }

            @Override
            public Integer next() {
                int val = current;
                current += step;
                return val;
            }
        };
    }
}
