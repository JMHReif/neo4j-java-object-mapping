package com.jmhreif;

import java.time.ZonedDateTime;
import java.util.List;

public record OrderInvoice(Integer orderID,
                             String companyName,
                             ZonedDateTime orderDate,
                             Double orderTotal,
                             List<LineItem> lineItems) {

}
