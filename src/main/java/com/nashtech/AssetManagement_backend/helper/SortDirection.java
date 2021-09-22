package com.nashtech.AssetManagement_backend.helper;

import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;


public class SortDirection {

    public SortDirection() {
    }

    public Sort.Direction getSortDirection(String direction) {
        if (direction.equals("asc")) {
            return Sort.Direction.ASC;
        } else if (direction.equals("desc")) {
            return Sort.Direction.DESC;
        }
        return Sort.Direction.ASC;
    }

    public List<Sort.Order> getSortOrders(String[] sort){
        List<Sort.Order> orders = new ArrayList<Sort.Order>();

        if (sort[0].contains(",")) {
            // will sort more than 2 fields
            // sortOrder="field, direction"
            for (String sortOrder : sort) {
                String[] _sort = sortOrder.split(",");

                orders.add(new Sort.Order(this.getSortDirection(_sort[1]), _sort[0]));
            }
        } else {
            // sort=[field, direction]
            orders.add(new Sort.Order(this.getSortDirection(sort[1]), sort[0]));
        }
        return orders;
    }

}
