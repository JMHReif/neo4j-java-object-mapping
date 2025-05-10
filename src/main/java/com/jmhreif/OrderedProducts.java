package com.jmhreif;

import java.time.ZonedDateTime;
import java.util.List;

public record OrderedProducts(
        Integer orderID,
        ZonedDateTime orderDate,
        List<String> products) {
    
}
