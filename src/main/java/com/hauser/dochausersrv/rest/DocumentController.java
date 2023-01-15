package com.hauser.dochausersrv.rest;

import com.hauser.dochausersrv.model.PDFDocument;
import com.hauser.dochausersrv.repository.PDFDocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DocumentController {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentController.class);

    @Autowired
    private PDFDocumentRepository pdfDocumentRepository;

    @DeleteMapping("/dochausersrv/document/{documentId}")
    public PDFDocument deleteDocument(@PathVariable(value = "documentId") String documentId) {
        LOG.info("Request to remove document " + documentId);
        return pdfDocumentRepository.deleteDocument(documentId);
    }
}
