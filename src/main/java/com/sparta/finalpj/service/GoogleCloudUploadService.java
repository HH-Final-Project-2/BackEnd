package com.sparta.finalpj.service;

import com.google.cloud.storage.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class GoogleCloudUploadService {
    // get service by env var GOOGLE_APPLICATION_CREDENTIALS. Json file generated in API & Services -> Service account key
    private static Storage storage = StorageOptions.getDefaultInstance().getService();
    @Value("${cloud.gcp.storage.bucket.name}")
    String bucketName;

    public String upload(MultipartFile file) {
        try {
            BlobInfo blobInfo = storage.create(
                    //Todo: UUID 추가 (파일이름 중복)
                    BlobInfo.newBuilder(bucketName, file.getOriginalFilename()).build(), //get original file name
                    file.getBytes() // the file
//                    BlobTargetOption.predefinedAcl(PredefinedAcl.PUBLIC_READ) // Set file permission
            );
            return blobInfo.getMediaLink(); // Return file url
        } catch (IllegalStateException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}