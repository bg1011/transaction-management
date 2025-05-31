package com.hsbc.transaction.controller;

import org.springframework.data.domain.Sort;

/**
 * Base controller class providing common functionality for all controllers.
 */
public class BaseController {

    /**
     * Helper method to parse sort parameters.
     * Expected format: 'field,asc|desc'
     *
     * @param sort The sort parameter string
     * @return Sort object
     * @throws IllegalArgumentException if the sort parameter format is invalid
     */
    protected Sort parseSort(String sort) {
        String[] parts = sort.split(",");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid sort parameter format. Expected format: 'field,asc|desc'");
        }
        return Sort.by(Sort.Direction.fromString(parts[1]), parts[0]);
    }
}
