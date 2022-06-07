package com.hauser.dochausersrv.rest;

import com.hauser.dochausersrv.model.SearchDocRequest;
import com.hauser.dochausersrv.model.SearchDocResult;
import com.hauser.dochausersrv.model.Tag;
import com.hauser.dochausersrv.repository.PDFDocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.stream.Collectors;


@RestController
public class SearchController {

    private static final Logger LOG = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    private PDFDocumentRepository pdfDocumentRepository;

    @PostMapping("/dochausersrv/search")
    public SearchDocResult searchDocuments(@RequestBody SearchDocRequest request) {
        LOG.info("New search request: " + request);
        return pdfDocumentRepository.findDocumentsByText(
                request.getQueryTerms(),
                Arrays.stream(request.getQueryTags()).map(Tag::getTagname).toArray(String[]::new), request.getSkip()
            );
    }

}
