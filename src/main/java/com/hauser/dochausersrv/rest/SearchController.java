package com.hauser.dochausersrv.rest;

import com.hauser.dochausersrv.model.SearchDocRequest;
import com.hauser.dochausersrv.model.SearchDocResult;
import com.hauser.dochausersrv.repository.PDFDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class SearchController {

    @Autowired
    private PDFDocumentRepository pdfDocumentRepository;

    @PostMapping("/dochausersrv/search")
    public SearchDocResult searchDocuments(@RequestBody SearchDocRequest request) {
        System.out.println("New Reqiest:" + request);
        return pdfDocumentRepository.findDocumentsByText(
                request.getQueryTerms(), request.getSkip()
                );
    }
}
