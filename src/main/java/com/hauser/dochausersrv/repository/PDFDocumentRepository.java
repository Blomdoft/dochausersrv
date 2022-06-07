package com.hauser.dochausersrv.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hauser.dochausersrv.model.PDFDocument;
import com.hauser.dochausersrv.model.SearchDocResult;
import org.apache.lucene.search.TermQuery;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
public class PDFDocumentRepository {

    private static final Logger LOG = LoggerFactory.getLogger(PDFDocumentRepository.class);

    private static final String index = "dochauser";

    @Autowired
    RestHighLevelClient highLevelClient;

    @Value("${archivedirectorycut}")
    private String archiveDirectoryCutPoint;

    ObjectMapper jsonMapper;

    public PDFDocumentRepository() {
        jsonMapper = new ObjectMapper();
        jsonMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    }


    /**
     * Search for documents, uses basic Elastic Search magic to find anyhting that was stored in the "text" field of our PDF documents
     * @param queryStrings all search strings
     * @param start Paging capability, how many entries will be skipped
     * @return Total number of hits and all the documents found
     */
    public SearchDocResult findDocumentsByText(String[] queryStrings, String [] tags, long start) {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // experiment, find partial words
        StringBuffer sbuf = new StringBuffer();
        Arrays.stream(queryStrings).forEach(str -> {
                    if (str.length() > 0) {
                        sbuf.append(str).append("* ");
                    }
                }
        );
        String queryString = sbuf.toString();

        System.out.println("QueryString: " + queryString);

        QueryBuilder textSearchQueryBuilder = null;
        if (queryString.trim().length() > 0) {
            textSearchQueryBuilder = QueryBuilders.matchQuery("text", queryString);
        } else {
            textSearchQueryBuilder = QueryBuilders.matchAllQuery();
        }

        BoolQueryBuilder bqb = QueryBuilders.boolQuery().must(textSearchQueryBuilder);
        for (String tag : tags) {
            bqb = bqb.must(QueryBuilders.termQuery("tags.tagname", tag.toLowerCase()));
        }
        searchSourceBuilder.query(bqb);

        searchSourceBuilder.sort(new FieldSortBuilder("timestamp").order(SortOrder.DESC));
        searchSourceBuilder.size(100);
        searchSourceBuilder.from((int) start);

        SearchRequest req = new SearchRequest()
                .indices(index)
                .source(searchSourceBuilder);
        System.out.println(req.toString());
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
                doc.setDirectory(doc.getDirectory().substring(doc.getDirectory().indexOf(archiveDirectoryCutPoint) + 1));
                doc.getThumbnails().forEach(thumbNail -> {
                    thumbNail.setImgdirectory(thumbNail.getImgdirectory().substring(thumbNail.getImgdirectory().indexOf(archiveDirectoryCutPoint) + 1));
                });
            });

            return new SearchDocResult(results, response.getHits().getTotalHits().value);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new SearchDocResult(Collections.emptyList(), 0);

    }

    /**
     * Add a tag to a document, if the tag exists, nothing happens
     * @param documentId PDF Document ID
     * @param tagName the new tag to add
     * @return the updated document if one was updated
     */
    public PDFDocument addTag(String documentId, String tagName) {
        try {
            GetRequest getRequest = new GetRequest(index, documentId);
            GetResponse getResponse = highLevelClient.get(getRequest, RequestOptions.DEFAULT);
            Map<String, Object> result = getResponse.getSourceAsMap();
            List<Map<String, String>> tags = (List<Map<String, String>>) result.get("tags");

            if (!tags.stream().map(m -> m.get("tagname")).toList().contains(tagName)) {
                HashMap<String, String> newTag = new HashMap<>();
                newTag.put("tagname", tagName);
                tags.add(newTag);

                // store as updated document
                UpdateRequest updateRequest = new UpdateRequest("dochauser", documentId);
                updateRequest.doc(result);
                updateRequest.fetchSource(true);
                UpdateResponse updateResponse = highLevelClient.update(updateRequest, RequestOptions.DEFAULT);

                if (updateResponse.getResult() != DocWriteResponse.Result.UPDATED) {
                    throw new RuntimeException("Update failed");
                }

                try {
                    return jsonMapper.readValue(updateResponse.getGetResult().sourceAsString(), PDFDocument.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException("Unable to analyse result from Elastic Search", e);
                }
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to communicate with Elastic Search", e);
        }
    }


    /**
     * Delete a tag from a document, if the tag does not exist, nothing happnes
     * @param documentId PDF Document ID
     * @param tagName the tag to remove
     * @return the updated document if one was updated
     */
    public PDFDocument deleteTag(String documentId, String tagName) {
        try {
            GetRequest getRequest = new GetRequest(index, documentId);
            GetResponse getReponse = highLevelClient.get(getRequest, RequestOptions.DEFAULT);
            Map<String, Object> result = getReponse.getSourceAsMap();
            List<Map<String, String>> tags = (List<Map<String, String>>) result.get("tags");
            if (tags.stream().map(m -> m.get("tagname")).toList().contains(tagName)) {
                List<Map<String, String>> resultList = tags.stream().filter(m -> !m.get("tagname").equals(tagName)).toList();
                result.put("tags", resultList);

                // store as updated document
                UpdateRequest updateRequest = new UpdateRequest("dochauser", documentId);
                updateRequest.doc(result);
                updateRequest.fetchSource(true);
                UpdateResponse updateResponse = highLevelClient.update(updateRequest, RequestOptions.DEFAULT);

                if (updateResponse.getResult() != DocWriteResponse.Result.UPDATED) {
                    throw new RuntimeException("Remove failed");
                }

                try {
                    return jsonMapper.readValue(updateResponse.getGetResult().sourceAsString(), PDFDocument.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException("Unable to analyse result from Elastic Search", e);
                }
            } else {
                return null;
            }

        } catch (IOException e) {
            throw new RuntimeException("Unable to communicate with Elastic Search", e);
        }
    }

    /**
     * Remove a given tag from all documents in the repository
     * @param tagName Name of the tag to remove
     */
    public void removeTagFromAllDocuments(String tagName) {

    }

}
