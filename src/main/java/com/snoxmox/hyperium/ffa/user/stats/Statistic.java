package com.snoxmox.hyperium.ffa.user.stats;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Statistic implements Serializable {

    private int kills = 0;
    private int deaths = 0;
    private int assists = 0;


    public void setAssists(final int assists) {
        this.assists = assists;
    }

    public void setDeaths(final int deaths) {
        this.deaths = deaths;
    }

    public void setKills(final int kills) {
        this.kills = kills;
    }

    public int getKills() {
        return this.kills;
    }

    public int getDeaths() {
        return this.deaths;
    }

    public int getAssists() {
        return this.assists;
    }
}
