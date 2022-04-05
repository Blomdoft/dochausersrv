package com.hauser.dochausersrv.rest;

import com.hauser.dochausersrv.model.PDFDocument;
import com.hauser.dochausersrv.model.Tag;
import com.hauser.dochausersrv.repository.PDFDocumentRepository;
import com.hauser.dochausersrv.repository.TagRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
public class TagController {

    private static final Logger LOG = LoggerFactory.getLogger(TagController.class);

    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private PDFDocumentRepository pdfDocumentRepository;

    // Tag Repository, working on the general tag list

    @GetMapping("/dochausersrv/tag")
    public List<Tag> getAllTags() {
        LOG.debug("Request for all tags");
        List<Tag> allTags = tagRepository.getAllTags();
        LOG.debug("Returning for all tags, number of tags: " + allTags.size());
        return allTags;
    }

    @PutMapping("/dochausersrv/tag/{tagName}")
    public void addTag(@PathVariable(value = "tagName") String tagName) {
        LOG.info("Request to add tag: " + tagName);
        tagRepository.addTag(tagName);
    }

    @DeleteMapping("/dochausersrv/tag/{tagName}")
    public void deleteTag(@PathVariable(value = "tagName") String tagName) {
        LOG.info("Request to remove tag: " + tagName);
        tagRepository.deleteTag(tagName);
    }

    // PDF document Tags, adding and removing tags from documents

    @PutMapping("/dochausersrv/document/{documentId}/{tagName}")
    public PDFDocument addTagToDocument(@PathVariable(value = "documentId") String documentId, @PathVariable(value = "tagName") String tagName) {
        LOG.info("Request to add tag " + tagName + " to document " + documentId);
        return pdfDocumentRepository.addTag(documentId, tagName);
    }

    @DeleteMapping("/dochausersrv/document/{documentId}/{tagName}")
    public PDFDocument deleteTagFromDocument(@PathVariable(value = "documentId") String documentId, @PathVariable(value = "tagName") String tagName) {
        LOG.info("Request to remove tag " + tagName + " from document " + documentId);
        return pdfDocumentRepository.deleteTag(documentId, tagName);
    }

    @PutMapping("/dochausersrv/tag")
    public void setTags(@RequestBody Tag[] newTags) {
        LOG.info("Request to replace tags with new list " + Arrays.toString(newTags));
        tagRepository.setTags(Arrays.stream(newTags).map(Tag::getTagname).toArray(String[]::new));
    }

}
