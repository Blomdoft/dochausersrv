package com.hauser.dochausersrv.rest;

import com.hauser.dochausersrv.repository.FileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ImportController {

    private static final Logger LOG = LoggerFactory.getLogger(ImportController.class);

    @Autowired
    private FileRepository fileRepository;

    @PostMapping("/dochausersrv/import")
    public ResponseEntity<?> importFile(@RequestParam("file") MultipartFile file ) {
        try {
            fileRepository.importFile(file);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok("File uploaded successfully.");
    }
}
