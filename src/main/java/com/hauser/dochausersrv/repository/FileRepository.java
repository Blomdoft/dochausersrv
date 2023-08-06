package com.hauser.dochausersrv.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
public class FileRepository {

    private static final Logger LOG = LoggerFactory.getLogger(FileRepository.class);

    @Value("${import.path}")
    private String importPath;

    public void importFile(MultipartFile file) throws IOException {
        final UUID fileId = UUID.randomUUID();
        final String fileName = importPath + fileId + ".pdf";
        LOG.info("Uploading file " + file.getOriginalFilename() + " with name " + fileName);
        file.transferTo(new File(fileName));
    }
}
