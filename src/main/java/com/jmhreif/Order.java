package com.jmhreif;

import java.time.ZonedDateTime;

public record Order(Integer orderID,
                    ZonedDateTime orderDate,
                    ZonedDateTime shippedDate) {
}
