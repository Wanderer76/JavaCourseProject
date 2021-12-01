package com.company;

public class Task {
    private final String name;
    private final int score;

    public Task(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    @Override
    public String toString() {
        return "Задание: " + name+" - "+score;
    }
}
