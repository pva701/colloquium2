package ru.ifmo.md.colloquium2;

/**
 * Created by pva701 on 11.11.14.
 */
public class Candidate {
    private int id;
    private String name;
    private int votes;
    public Candidate(String name, int votes) {
        this.name = name;
        this.votes = votes;
    }


    public Candidate(int id, String name, int votes) {
        this.id = id;
        this.name = name;
        this.votes = votes;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getVotes() {
        return votes;
    }
}
