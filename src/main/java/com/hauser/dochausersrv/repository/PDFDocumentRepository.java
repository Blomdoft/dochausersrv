package com.hauser.dochausersrv.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hauser.dochausersrv.model.PDFDocument;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class PDFDocumentRepository {

    @Autowired
    RestHighLevelClient highLevelClient;

    @Value( "${archivedirectorycut}" )
    private String archiveDirectoryCutPoint;

    public List<PDFDocument> findDocumentsByText(String[] queryStrings, int start) {

        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // experiment, find partial words
        StringBuffer sbuf = new StringBuffer();
        Arrays.stream(queryStrings).forEach(str -> {
            if (str.length() > 0) {
                sbuf.append("*").append(str).append("* ");
            }
        }
        );
        String queryString = sbuf.toString();

        System.out.println("QueryString: " + queryString);

        if (queryString.trim().length() > 0) {
            searchSourceBuilder.query(QueryBuilders.matchQuery("text", queryString));
        } else {
            searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        }

        searchSourceBuilder.sort(new FieldSortBuilder("timestamp").order(SortOrder.DESC));
        searchSourceBuilder.size(100);

        SearchRequest req = new SearchRequest()
                .indices("dochauser")
                .source(searchSourceBuilder);
        try {
            SearchResponse response = highLevelClient.search(req, RequestOptions.DEFAULT);
            SearchHit[] hits = response.getHits().getHits();
            List<PDFDocument> results =
                    Arrays.stream(hits)
                            .map(hit -> {
                                try {
                                    return jsonMapper.readValue(hit.getSourceAsString(), PDFDocument.class);
                                } catch (JsonProcessingException e) {
                                    e.printStackTrace();
                                    return null;
                                }
                            }).toList();

            // clip the directories as received by the server to local ones
            results.forEach(doc -> {
                doc.setDirectory(doc.getDirectory().substring(doc.getDirectory().indexOf(archiveDirectoryCutPoint)+1));
                doc.getThumbnails().forEach(thumbNail -> {
                    thumbNail.setImgdirectory(thumbNail.getImgdirectory().substring(thumbNail.getImgdirectory().indexOf(archiveDirectoryCutPoint)+1));
                });
            });

            return results;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();

    }

}
