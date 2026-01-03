package com.snoxmox.hyperium.ffa.clan.war;

import java.util.HashSet;
import java.util.Set;

public class War {
    public enum State { PENDING, ACTIVE, FINISHED }

    private final Set<String> teamA = new HashSet<>();
    private final Set<String> teamB = new HashSet<>();

    private int targetPoints;

    private int pointsA;
    private int pointsB;

    private State state = State.PENDING;

    public War() {}

    public War(final String initiatorTag, final String targetTag, final int targetPoints) {
        this.teamA.add(initiatorTag);
        this.teamB.add(targetTag);
        this.targetPoints = targetPoints;
    }

    public Set<String> getTeamA() { return this.teamA; }
    public Set<String> getTeamB() { return this.teamB; }

    public String getPrimaryClanA() { return this.teamA.isEmpty() ? "?" : this.teamA.iterator().next(); }
    public String getPrimaryClanB() { return this.teamB.isEmpty() ? "?" : this.teamB.iterator().next(); }

    public int getTargetPoints() { return this.targetPoints; }
    public int getPointsA() { return this.pointsA; }
    public int getPointsB() { return this.pointsB; }
    public State getState() { return this.state; }

    public void setState(final State state) { this.state = state; }

    public void addClan(final String clanTag, final boolean sideA) {
        if (sideA) {
            this.teamA.add(clanTag.toUpperCase());
        } else {
            this.teamB.add(clanTag.toUpperCase());
        }
    }

    public void addPointToSide(final boolean sideA) {
        if (sideA) this.pointsA++;
        else this.pointsB++;
    }

    public boolean involves(final String clanTag) {
        final String tag = clanTag.toUpperCase();
        return this.teamA.contains(tag) || this.teamB.contains(tag);
    }

    public boolean isSideA(final String clanTag) {
        return this.teamA.contains(clanTag.toUpperCase());
    }

    public boolean isConflict(final String tag1, final String tag2) {
        final String t1 = tag1.toUpperCase();
        final String t2 = tag2.toUpperCase();

        final boolean t1InA = this.teamA.contains(t1);
        final boolean t1InB = this.teamB.contains(t1);
        final boolean t2InA = this.teamA.contains(t2);
        final boolean t2InB = this.teamB.contains(t2);

        return (t1InA && t2InB) || (t1InB && t2InA);
    }

    public boolean isWinner(final boolean sideA) {
        if (sideA) return this.pointsA >= this.targetPoints;
        else return this.pointsB >= this.targetPoints;
    }
}