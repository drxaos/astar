package org.example.search;

public interface Scorer<T> {
    double computeCost(T from, T to);
}