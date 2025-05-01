package com.jmhreif;

import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;

public class App {
    public static void main(String[] args) {
        AppProperties.loadProperties();

        checkAlive();

        // Create a new Neo4j driver instance
        try (var driver = GraphDatabase.driver(
                System.getProperty("NEO4J_URI"),
                AuthTokens.basic(
                    System.getProperty("NEO4J_USERNAME"),
                    System.getProperty("NEO4J_PASSWORD"))
                    )
        ) {
            driver.verifyConnectivity();
            
            int count = getCount(driver);
            System.out.println("Count: " + count);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void checkAlive() {
        System.out.println("Hello World!");
    }

    private static int getCount(Driver driver) {
        var result = driver.executableQuery(
            "RETURN COUNT {()} AS count"
            ).execute();

        // Result handling
        var count = result.records().get(0).get("count").asInt();
        return count;
    }

}
