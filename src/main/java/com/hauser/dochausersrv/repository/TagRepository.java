package com.hauser.dochausersrv.repository;

import com.hauser.dochausersrv.model.Tag;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class TagRepository {

    final private static String defaultTagDocument = "{ \"tags\" : [ { \"tagname\" : \"SCANNED\" } , { \"tagname\" : \"PROCESSED\" }] }";
    final private static String tagDocumentId = "TAG_REPOSITORY";
    final private static String tagIndex = "tags";

    private static final Logger LOG = LoggerFactory.getLogger(TagRepository.class);

    @Autowired
    RestHighLevelClient highLevelClient;

    private boolean existsStorageDocument() {
        GetRequest getRequest = new GetRequest(
                tagIndex, tagDocumentId);
        getRequest.fetchSourceContext(new FetchSourceContext(false));
        getRequest.storedFields("_none");
        try {
            return highLevelClient.exists(getRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            LOG.error("Unable to communicate proprly with Elastic Search", e);
            throw new RuntimeException("Unable to communicate properly with Elastic Search");
        }
    }

    private void initRepositoryIfNotPresent() {
        if (!existsStorageDocument()) {

            LOG.info("Creating a new tag storage document");

            CreateIndexRequest request = new CreateIndexRequest(tagIndex);

            try {
                CreateIndexResponse createIndexResponse = highLevelClient.indices().create(request, RequestOptions.DEFAULT);
                if (!createIndexResponse.isAcknowledged()) {
                    throw new RuntimeException("Unable to create tag index");
                }

                // index created, store our default document
                IndexRequest indexRequest = new IndexRequest(tagIndex);
                indexRequest.id(tagDocumentId);
                indexRequest.source(defaultTagDocument, XContentType.JSON);

                IndexResponse indexResponse = highLevelClient.index(indexRequest, RequestOptions.DEFAULT);
                if (indexResponse.getResult() != DocWriteResponse.Result.CREATED) {
                    LOG.info("Error creating initial index document for tags: " + indexResponse.getResult());
                    throw new RuntimeException("Error creating initial index document for tags: " + indexResponse.getResult());
                }

            } catch (IOException e) {
                LOG.error("Unable to communicate proprly with Elastic Search", e);
                throw new RuntimeException("Unable to communicate properly with Elastic Search");
            }
        }
    }


    /**
     * Return all Tags
     * @return Tags which are stored in the Taglist. Might not correspond with the Tags in the PDFDocuments
     */
    public List<Tag> getAllTags() {
        initRepositoryIfNotPresent();
        try {
            GetRequest getRequest = new GetRequest(tagIndex, tagDocumentId);
            GetResponse getResponse = highLevelClient.get(getRequest, RequestOptions.DEFAULT);
            Map<String, Object> result = getResponse.getSourceAsMap();
            List<Map<String, String>> tags = (List<Map<String, String>>) result.get("tags");
            return tags.stream().map(m -> m.get("tagname")).map(Tag::new).toList();
        } catch (IOException e) {
            LOG.error("Unable to communicate proprly with Elastic Search", e);
            throw new RuntimeException("Unable to communicate with Elastic Search" , e);
        }
    }

    public void setTags(String[] newtags) {
        initRepositoryIfNotPresent();
        try {
            GetRequest getRequest = new GetRequest(tagIndex, tagDocumentId);
            GetResponse getReponse = highLevelClient.get(getRequest, RequestOptions.DEFAULT);
            Map<String, Object> result = getReponse.getSourceAsMap();
            List<Map<String, String>> tags = Arrays.stream(newtags).map(tag -> Map.of("tagname", tag)).toList();
            result.put("tags", tags);
            // store as updated document
            UpdateRequest updateRequest = new UpdateRequest(tagIndex, tagDocumentId);
            updateRequest.doc(result);
            UpdateResponse updateResponse = highLevelClient.update(updateRequest, RequestOptions.DEFAULT);

            if (updateResponse.getResult() != DocWriteResponse.Result.UPDATED) {
                LOG.error("Unable to update the tag document on adding of tags " + newtags);
                throw new RuntimeException("Update failed");
            }
            LOG.info("Replaced a list of tags to the tag repository: " + newtags);

        } catch (IOException e) {
            throw new RuntimeException("Unable to communicate with Elastic Search" , e);
        }
    }

    public void addTag(String tagName) {
        initRepositoryIfNotPresent();
        try {
            GetRequest getRequest = new GetRequest(tagIndex, tagDocumentId);
            GetResponse getReponse = highLevelClient.get(getRequest, RequestOptions.DEFAULT);
            Map<String, Object> result = getReponse.getSourceAsMap();
            List<Map<String, String>> tags = (List<Map<String, String>>) result.get("tags");
            if (!tags.stream().map(m -> m.get("tagname")).toList().contains(tagName)) {
                HashMap<String, String> newTag = new HashMap<>();
                newTag.put("tagname", tagName);
                tags.add(newTag);

                // store as updated document
                UpdateRequest updateRequest = new UpdateRequest(tagIndex, tagDocumentId);
                updateRequest.doc(result);
                UpdateResponse updateResponse = highLevelClient.update(updateRequest, RequestOptions.DEFAULT);

                if (updateResponse.getResult() != DocWriteResponse.Result.UPDATED) {
                    LOG.error("Unable to update the tag document on adding of tag " + tagName);
                    throw new RuntimeException("Update failed");
                }
                LOG.info("Added a new tag to the tag repository: " + tagName);

            }

        } catch (IOException e) {
            throw new RuntimeException("Unable to communicate with Elastic Search" , e);
        }
    }

    public void deleteTag(String tagName) {
        initRepositoryIfNotPresent();
        try {
            GetRequest getRequest = new GetRequest(tagIndex, tagDocumentId);
            GetResponse getReponse = highLevelClient.get(getRequest, RequestOptions.DEFAULT);
            Map<String, Object> result = getReponse.getSourceAsMap();
            List<Map<String, String>> tags = (List<Map<String, String>>) result.get("tags");
            if (tags.stream().map(m -> m.get("tagname")).toList().contains(tagName)) {
                List<Map<String, String>> resultList = tags.stream().filter(m -> !m.get("tagname").equals(tagName)).toList();
                result.put("tags", resultList);

                // store as updated document
                UpdateRequest updateRequest = new UpdateRequest(tagIndex, tagDocumentId);
                updateRequest.doc(result);
                UpdateResponse updateResponse = highLevelClient.update(updateRequest, RequestOptions.DEFAULT);

                if (updateResponse.getResult() != DocWriteResponse.Result.UPDATED) {
                    LOG.error("Unable to update the tag document on removal of tag " + tagName);
                    throw new RuntimeException("Remove failed");
                }
                LOG.info("Removed a tag to the tag repository: " + tagName);

            }

        } catch (IOException e) {
            LOG.error("Unable to communicate proprly with Elastic Search", e);
            throw new RuntimeException("Unable to communicate with Elastic Search" , e);
        }
    }

}
