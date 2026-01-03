package com.snoxmox.hyperium.ffa.clan.war;

import com.snoxmox.hyperium.ffa.clan.war.War;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class WarService {

    private final Map<String, War> warByClan = new ConcurrentHashMap<>();

    public boolean hasAnyWar(final String clanTag) {
        return this.warByClan.containsKey(clanTag.toUpperCase());
    }

    public Optional<War> getWar(final String clanTag) {
        return Optional.ofNullable(this.warByClan.get(clanTag.toUpperCase()));
    }

    public Optional<War> getWarBetween(final String tagA, final String tagB) {
        final War warA = this.warByClan.get(tagA.toUpperCase());
        if (warA != null && warA.isConflict(tagA, tagB)) return Optional.of(warA);

        final War warB = this.warByClan.get(tagB.toUpperCase());
        if (warB != null && warB.isConflict(tagA, tagB)) return Optional.of(warB);

        return Optional.empty();
    }

    public War declareWar(final String attackerTag, final String defenderTag, final int targetPoints) {
        final War war = new War(attackerTag.toUpperCase(), defenderTag.toUpperCase(), targetPoints);
        this.warByClan.put(attackerTag.toUpperCase(), war);
        this.warByClan.put(defenderTag.toUpperCase(), war);
        return war;
    }

    public void joinWar(final War war, final String newAllyTag, final boolean sideA) {
        war.addClan(newAllyTag, sideA);
        this.warByClan.put(newAllyTag.toUpperCase(), war);
    }

    public void acceptWar(final War war) {
        war.setState(War.State.ACTIVE);
    }

    public void finishWar(final War war) {
        war.setState(War.State.FINISHED);
        for (final String clan : war.getTeamA()) {
            this.warByClan.remove(clan);
        }
        for (final String clan : war.getTeamB()) {
            this.warByClan.remove(clan);
        }
    }
}