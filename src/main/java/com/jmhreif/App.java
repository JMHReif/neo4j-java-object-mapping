package com.jmhreif;

import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.AuthTokens;

public class App {
    public static void main(String[] args) {
        AppProperties.loadProperties();

        // Create a new Neo4j driver instance
        try (var driver = GraphDatabase.driver(
                System.getProperty("NEO4J_URI"),
                AuthTokens.basic(
                    System.getProperty("NEO4J_USERNAME"),
                    System.getProperty("NEO4J_PASSWORD"))
                    )
        ) {
            driver.verifyConnectivity();

            // Return orders mapped to Order domain record
            System.out.println("\n Querying orders...");
            var orders = driver.executableQuery("""
                MATCH (o:Order) 
                RETURN o AS order
                LIMIT 3;
                """)
                .execute()
                .records()
                .stream()
                .map(record -> record.get("order").as(Order.class))
                .toList();
            for (var order : orders) {
                System.out.println(order);
            }

            // Return products mapped to Product domain record
            System.out.println("\n Querying order summary...");
            var orderSummaries = driver.executableQuery("""
                MATCH (o:Order)-[r2:ORDERS]->(p:Product)
                WITH o, collect(p.productName) as products
                RETURN o { orderID: o.orderID,
                    orderDate: o.orderDate,
                    products: products,
                    items: size(products)
                } AS order
                LIMIT 3;
                """)
                .execute()
                .records()
                .stream()
                .map(record -> record.get("order").as(OrderedProducts.class))
                .toList();
            for (var orderInfo : orderSummaries) {
                System.out.println(orderInfo);
            }

            // Return ordered products mapped to OrderInvoice domain record
            System.out.println("\n Querying ordered products...");
            var orderedProducts = driver.executableQuery("""
                MATCH (c:Customer)-[r:PURCHASED]->(o:Order)-[r2:ORDERS]->(p:Product) 
                WITH c, r, o, sum(r2.quantity*p.unitPrice) as orderTotal
                RETURN o { orderID: o.orderID,
                           companyName: c.companyName,
                           orderDate: o.orderDate,
                           orderTotal: orderTotal,
                           lineItems: COLLECT {
                               MATCH (o)-[r3:ORDERS]->(p2:Product) 
                               RETURN p2 { productName: p2.productName, 
                                           quantity: r3.quantity, 
                                           itemTotal: r3.quantity * p2.unitPrice }
                                }
                    }
                LIMIT 3;
                """)
                .execute()
                .records()
                .stream()
                .map(record -> record.get("o").as(OrderInvoice.class))
                .toList();
            for (var invoice : orderedProducts) {
                System.out.println(invoice);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
