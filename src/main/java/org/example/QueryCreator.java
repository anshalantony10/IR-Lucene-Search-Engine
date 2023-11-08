package org.example;

import org.example.model.QueryModel;

public class QueryCreator {
    public static QueryModel getQuery(String query) {
        return new QueryModel("1", query);
    }
}
