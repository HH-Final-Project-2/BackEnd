package com.sparta.finalpj.service;

import com.google.cloud.vision.v1.*;
import com.sparta.finalpj.controller.response.ResponseDto;
import com.sparta.finalpj.controller.response.ocr.OcrResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OcrService {
    private final GoogleCloudUploadService googleCloudUploadService;
    public ResponseDto<?> detectTextGcs(MultipartFile cardImg) throws IOException {
        googleCloudUploadService.upload(cardImg);

        // TODO(developer): Replace these variables before running the sample.
        String filePath = "gs://bobo_file_bucket/" + cardImg.getOriginalFilename();
        return detectTextGcs(filePath);
    }

    // Detects text in the specified remote image on Google Cloud Storage.
    public ResponseDto<?> detectTextGcs(String gcsPath) throws IOException {
        List<AnnotateImageRequest> requests = new ArrayList<>();

        ImageSource imgSource = ImageSource.newBuilder().setGcsImageUri(gcsPath).build();
        Image img = Image.newBuilder().setSource(imgSource).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);

        // Initialize client that will be used to send requests. This client only needs to be created
        // once, and can be reused for multiple requests. After completing all of your requests, call
        // the "close" method on the client to safely clean up any remaining background resources.
        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();
            ArrayList<Object> originList = new ArrayList<>();

            // 명함 OCR 데이터 담는 변수
            String email = ""; // 이메일
            String phoneNum = ""; // 폰번호
            String tel = ""; // 회사 번호
            String fax = ""; // 팩스

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    System.out.format("Error: %s%n", res.getError().getMessage());
                    throw new IllegalArgumentException("실패");
                }

                // For full list of available annotations, see http://g.co/cloud/vision/docs
                for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
                    //System.out.format("Text: %s%n", annotation.getDescription());
                    // System.out.format("Position : %s%n", annotation.getBoundingPoly());
                    originList.add(annotation.getDescription());

                }
            }
//            System.out.println("@@@@@@@@@@@ " + originList.get(0));
            String[] txt = originList.get(0).toString().split("\\n");
//            System.out.println("=====================");
//            System.out.println(txt[0]);

            for (int i = 0; i < txt.length; i++) {
                //System.out.println(i+"번째 : " + txt[i]);
                // 휴대폰 번호 (M)
                if (txt[i].contains("-") && txt[i].contains("M")) {
                    phoneNum = txt[i].replace("M", " ").trim();
                    System.out.println("===========phone=========");
                    System.out.println(phoneNum);

                    if (txt[i].contains("-") || txt[i].contains("M")) {
                        phoneNum = txt[i].replace("M", " ").trim();
                    }
                }
                // companyTel (T)
                if (txt[i].contains("-") && txt[i].contains("T")) {
                    tel = txt[i].replace("T", " ").trim();
                    System.out.println("==========companyTel==========");
                    System.out.println(tel);

                    if (txt[i].contains("-") || txt[i].contains("T")) {
                        tel = txt[i].replace("T", " ").trim();
                    }
                }
                // fax (F)
                if (txt[i].contains("-") && txt[i].contains("F")) {
                    fax = txt[i].replace("F", " ").trim();
                    System.out.println("==========fax==========");
                    System.out.println(fax);
                    if (txt[i].contains("-") || txt[i].contains("F")) {
                        fax = txt[i].replace("F", " ").trim();
                    }
                }
                // 이메일
                if (txt[i].contains("@")) {
                    System.out.println("---------email---------");
                    System.out.println(email);
                    email = txt[i];
                }
                // To-do: 회사 주소 유효성 검사 체크, 경우의 수 확인 ( 도로명, 번지)

            }
            OcrResponseDto ocrList = OcrResponseDto.builder()
                    .email(email)
                    .phoneNum(phoneNum)
                    .tel(tel)
                    .fax(fax)
                    .build();
            return ResponseDto.success(ocrList);
        }
    }
}