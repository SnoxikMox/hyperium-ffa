package com.snoxmox.hyperium.ffa.configuration;

import eu.okaeri.configs.OkaeriConfig;

public class DatabaseConfiguration extends OkaeriConfig {

    public DatabaseType type = DatabaseType.FLAT;
    public String host = "localhost";
    public String port = "3306";
    public String database = "ffa";
    public String username = "root";
    public String password = "haslo";


    public enum DatabaseType {
        FLAT, MYSQL
    }

}


