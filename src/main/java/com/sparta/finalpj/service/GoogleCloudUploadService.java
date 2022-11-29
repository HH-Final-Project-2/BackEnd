package com.sparta.finalpj.service;

import com.google.cloud.storage.*;
import com.sparta.finalpj.exception.CustomException;
import com.sparta.finalpj.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;

@RequiredArgsConstructor
@Component
public class GoogleCloudUploadService {

    @Value("${cloud.gcp.storage.bucket.name}")
    String bucketName;

    // get service by env var GOOGLE_APPLICATION_CREDENTIALS. Json file generated in API & Services -> Service account key
    private static Storage storage = StorageOptions.getDefaultInstance().getService();

    public String upload(String path, MultipartFile file, String fileName) {

        // 명함 사진 업로드
        if(path.equals("card")) {
            try {
                BlobInfo blobInfo = storage.create(
                        BlobInfo.newBuilder(bucketName, "card/" + fileName).build(), //get original file name
                        file.getBytes() // the file0
                );

                // 3. OCR 실행
                return blobInfo.getMediaLink(); //blobInfo.getName() => Return file url
            } catch (IllegalStateException | IOException e) {
                //todo: exception Test 해보기
                throw new CustomException(ErrorCode.UPLOAD_FAIL_TO_GOOGLE);
            }
        }
        else {
            try {
                BlobInfo blobInfo = storage.create(
                        BlobInfo.newBuilder(bucketName, "community/" + fileName).build(), //get original file name
                        file.getBytes() // the file0
                );

                // 3. image url
                return blobInfo.getMediaLink(); //blobInfo.getName() => Return file url
            } catch (IllegalStateException | IOException e) {
                //todo: exception Test 해보기
                throw new CustomException(ErrorCode.UPLOAD_FAIL_TO_GOOGLE);
            }
        }
//
//        try {
//            BlobInfo blobInfo = storage.create(
//                    BlobInfo.newBuilder(bucketName, "card/" + fileName).build(), //get original file name
//                    file.getBytes() // the file0
//            );
//            String imgUrl = blobInfo.getMediaLink();
//
//            // 3. OCR 실행
//            return ocrService.readFileInfo(fileName, imgUrl); //blobInfo.getName() => Return file url
//        } catch (IllegalStateException | IOException e) {
//            //todo: exception Test 해보기
//            throw new CustomException(ErrorCode.UPLOAD_FAIL_TO_GOOGLE);
//        }
    }
}