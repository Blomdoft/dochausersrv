package com.hauser.dochausersrv.model;

import java.util.Arrays;
import java.util.Objects;

public class SearchDocRequest {
    private final SearchAggregation aggregation;
    private final long skip;
    private final SearchMode mode;
    private final String from;
    private final String to;
    private final String[] queryTerms;

    public SearchDocRequest(SearchAggregation aggregation, long skip, SearchMode mode, String from, String to, String[] queryTerms) {
        this.aggregation = aggregation;
        this.skip = skip;
        this.mode = mode;
        this.from = from;
        this.to = to;
        this.queryTerms = queryTerms;
    }

    public SearchAggregation getAggregation() {
        return aggregation;
    }

    public SearchMode getMode() {
        return mode;
    }

    public String getFrom() {
        return from;
    }

     public String getTo() {
        return to;
    }

    public String[] getQueryTerms() {
        return queryTerms;
    }

    public long getSkip() {
        return skip;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchDocRequest that = (SearchDocRequest) o;
        return skip == that.skip && aggregation == that.aggregation && mode == that.mode && Objects.equals(from, that.from) && Objects.equals(to, that.to) && Arrays.equals(queryTerms, that.queryTerms);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(aggregation, skip, mode, from, to);
        result = 31 * result + Arrays.hashCode(queryTerms);
        return result;
    }

    @Override
    public String toString() {
        return "SearchDocRequest{" +
                "aggregation=" + aggregation +
                ", skip=" + skip +
                ", mode=" + mode +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", queryTerms=" + Arrays.toString(queryTerms) +
                '}';
    }
}
