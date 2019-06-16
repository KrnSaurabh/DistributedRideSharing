package com.miamioh.ridesharing.app.pso.utilities;

public class Pair<T> {

    private final T left;
    private final T right;

    Pair(T left, T right) {
        this.left = left;
        this.right = right;
    }

    T getLeft() {
        return left;
    }

    T getRight() {
        return right;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (!(object instanceof Pair)) {
            return false;
        }
        @SuppressWarnings("unchecked")
        Pair<T> pair = (Pair<T>) object;
        return this.left.equals(pair.getLeft())
                && this.right.equals(pair.getRight());
    }

    @Override
    public int hashCode() {
        return left.hashCode() ^ right.hashCode();
    }

    @Override
    public String toString() {
        return "(" + left.toString() + ", " + right.toString() + ")";
    }
}