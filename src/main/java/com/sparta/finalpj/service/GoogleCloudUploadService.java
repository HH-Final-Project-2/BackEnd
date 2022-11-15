package com.sparta.finalpj.service;

import com.google.cloud.storage.*;
import com.sparta.finalpj.exception.CustomException;
import com.sparta.finalpj.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class GoogleCloudUploadService {

    @Value("${cloud.gcp.storage.bucket.name}")
    String bucketName;

    // get service by env var GOOGLE_APPLICATION_CREDENTIALS. Json file generated in API & Services -> Service account key
    private static Storage storage = StorageOptions.getDefaultInstance().getService();

    public String upload(MultipartFile file) {
        try {
            BlobInfo blobInfo = storage.create(
                    //Todo: UUID 추가 (파일이름 중복)
                    BlobInfo.newBuilder(bucketName, file.getOriginalFilename()).build(), //get original file name
                    file.getBytes() // the file
            );
            return blobInfo.getMediaLink(); // Return file url
        } catch (IllegalStateException | IOException e) {
            //todo: exception Test 해보기
            throw new CustomException(ErrorCode.UPLOAD_FAIL_TO_GOOGLE);
        }
    }
}