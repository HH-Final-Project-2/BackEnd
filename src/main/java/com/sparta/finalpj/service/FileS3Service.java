package com.sparta.finalpj.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.sparta.finalpj.exception.CustomException;
import com.sparta.finalpj.exception.ErrorCode;
import com.sparta.finalpj.exception.FileConvertException;
import com.sparta.finalpj.exception.RemoveFileException;
import com.sparta.finalpj.shared.MutipartToFileConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Objects;
import java.util.UUID;

@Service
@Component
@Slf4j
@RequiredArgsConstructor
public class FileS3Service {
    @Value("${aws.s3.bucket.name}")
    private String BUCKET;
    @Value("${aws.s3.path.url}")
    private String BUCKET_PATH;
    private final AmazonS3 amazonS3;
    private final MutipartToFileConverter mutipartToFileConverter;

    public String uploadFile(MultipartFile images
    ) throws IOException {

        File licenseFile = mutipartToFileConverter.convert(images)
                .orElseThrow(FileConvertException::new);


        String now = Instant
                .now().atZone(ZoneId.of("Asia/Seoul")).toString()
                .replace("T", "-")
                .replace("Z", "")
                .replace("[Asia/Seoul]", "");
        String fileName = BUCKET_PATH + now + UUID.randomUUID() + "."
                + Objects.toString(images.getOriginalFilename()).split("\\.")[1];

        System.out.println("fileName = " + fileName);

        ObjectMetadata objMeta = new ObjectMetadata();
        objMeta.setContentType(images.getContentType());
        objMeta.setContentLength(images.getInputStream().available());

        //PutObjectRequest는 Aws S3 버킷에 업로드할 객체 메타 데이터와 파일 데이터로 이루어져있다.
        amazonS3.putObject(BUCKET, fileName, images.getInputStream(),objMeta);
        removeNewFile(licenseFile);

        return amazonS3.getUrl(BUCKET, fileName).toString();

    }

    public void removeNewFile(File targetFile) {
        if (!targetFile.delete()) {
            throw new RemoveFileException();
        }
    }

}

