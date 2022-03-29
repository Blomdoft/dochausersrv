package com.hauser.dochausersrv.model;

import java.util.List;
import java.util.Objects;

public class SearchDocResult {

    private List<PDFDocument> documents;
    private long hitCount;

    public SearchDocResult(List<PDFDocument> documents, long hitCount) {
        this.documents = documents;
        this.hitCount = hitCount;
    }

    public SearchDocResult() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchDocResult that = (SearchDocResult) o;
        return hitCount == that.hitCount && Objects.equals(documents, that.documents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(documents, hitCount);
    }

    public List<PDFDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<PDFDocument> documents) {
        this.documents = documents;
    }

    public long getHitCount() {
        return hitCount;
    }

    public void setHitCount(int hitCount) {
        this.hitCount = hitCount;
    }

}
