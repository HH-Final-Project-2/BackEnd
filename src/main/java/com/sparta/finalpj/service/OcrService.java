package com.sparta.finalpj.service;

import com.google.cloud.vision.v1.*;
import com.sparta.finalpj.controller.response.ResponseDto;
import com.sparta.finalpj.controller.response.ocr.OcrResponseDto;
import com.sparta.finalpj.domain.CardImage;
import com.sparta.finalpj.domain.Member;
import com.sparta.finalpj.repository.CardImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OcrService {

    @Value("${cloud.gcp.storage.bucket.filePath}")
    String bucketFilePath;
    private final CardImageRepository cardImageRepository;



    public ResponseDto<?> readFileInfo(String cardImgName, Member member) throws IOException {

        // Google Storage 경로
        String filePath = bucketFilePath + cardImgName;

        // OCR
        return detectTextGcs(filePath, cardImgName, member);
    }

    // Google 클라우드 저장소의 지정된 원격 이미지에서 텍스트를 추출
    public ResponseDto<?> detectTextGcs(String gcsPath, String cardImgName, Member member) throws IOException {
        List<AnnotateImageRequest> requests = new ArrayList<>();

        ImageSource imgSource = ImageSource.newBuilder().setGcsImageUri(gcsPath).build();
        Image img = Image.newBuilder().setSource(imgSource).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);


        // 요청을 보내는 데 사용할 클라이언트를 초기화한다.
        // 이 클라이언트는 한 번만 생성하면 되며 여러 요청에 대해 재사용할 수 있다.
        // 모든 요청을 완료한 후 클라이언트에서 "close" 메소드를 호출하여 나머지 백그라운드 리소스를 안전하게 정리한다.
        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            // OCR로 추출된 데이터를 담을 List
            ArrayList<Object> originList = new ArrayList<>();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    System.out.format("Error: %s%n", res.getError().getMessage());
                    throw new IllegalArgumentException("실패");
                }

                // 사용가능한 annotations 전체 목록 참고 : http://g.co/cloud/vision/docs
                for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
                    // 데이터를 배열에 add
                    originList.add(annotation.getDescription());
                }
            }

            // 배열의 0번째 값에 모든 데이터들이 text형식으로 담긴다
            String[] txt = originList.get(0).toString().split("\\n");

            // 명함 데이터 담는 변수
            String email = ""; // 이메일
            String phoneNum = ""; // 폰번호
            String tel = ""; // 회사 번호
            String fax = ""; // 팩스

            // TODO: 필요한 형식 더 추가하기
            // parsing 
            for (int i = 0; i < txt.length; i++) {
                // 휴대폰 번호 (M)
                if (txt[i].contains("-") && txt[i].contains("010") || txt[i].contains("82")) {

                    if (txt[i].contains("M.")) {
                        phoneNum = txt[i].replace("M.", " ").trim().substring(0, 13);
                    } else if (txt[i].contains("M")) {
                        phoneNum = txt[i].replace("M", " ").trim().substring(0, 13);
                    } else {
                        phoneNum = txt[i].trim();
                    }
                }


                // companyTel (T)
                if (txt[i].contains("-") && txt[i].contains("T.")) {
                    String telA = txt[i].substring(txt[i].indexOf("T."));
                    if(telA.length() >= 15) {
                        tel = txt[i].substring(txt[i].indexOf("T."), txt[i].indexOf("T.")+15).replace("T.", " ").trim();
                    } else if(telA.length() < 15 || telA.length() >= 14) {
                        tel = txt[i].substring(txt[i].indexOf("T."), txt[i].indexOf("T.")+14).replace("T.", " ").trim();
                    } else {
                        tel = txt[i].substring(txt[i].indexOf("T."), telA.length()).replace("T.", " ").trim();
                    }
                } else if (txt[i].contains("-") && txt[i].contains("T")) {
                    String telA = txt[i].substring(txt[i].indexOf("T"));
                    if(telA.length() >= 15) {
                        tel = txt[i].substring(txt[i].indexOf("T"), txt[i].indexOf("T")+15).replace("T", " ").trim();
                    } else if(telA.length() < 15 || telA.length() >= 14) {
                        tel = txt[i].substring(txt[i].indexOf("T"), txt[i].indexOf("T")+14).replace("T", " ").trim();
                    } else {
                        tel = txt[i].substring(txt[i].indexOf("T"), telA.length()).replace("T", " ").trim();
                    }
                }

               // fax (F)
                if (txt[i].contains("-") && txt[i].contains("F.")) {
                    String faxA = txt[i].substring(txt[i].indexOf("F."));
                    if(faxA.length() >= 15) {
                        fax = txt[i].substring(txt[i].indexOf("F."), txt[i].indexOf("F.")+15).replace("F.", " ").trim();
                    } else if(faxA.length() < 15 || faxA.length() >= 14) {
                        fax = txt[i].substring(txt[i].indexOf("F."), txt[i].indexOf("F.")+14).replace("F.", " ").trim();
                    } else {
                        fax = txt[i].substring(txt[i].indexOf("F."), faxA.length()).replace("F.", " ").trim();
                    }
                } else if (txt[i].contains("-") && txt[i].contains("F,")) {
                    String faxA = txt[i].substring(txt[i].indexOf("F,"));
                    if(faxA.length() >= 15) {
                        fax = txt[i].substring(txt[i].indexOf("F,"), txt[i].indexOf("F,")+15).replace("F,", " ").trim();
                    } else if(faxA.length() < 15 || faxA.length() >= 14) {
                        fax = txt[i].substring(txt[i].indexOf("F,"), txt[i].indexOf("F,")+14).replace("F,", " ").trim();
                    } else {
                        fax = txt[i].substring(txt[i].indexOf("F,"), faxA.length()).replace("F,", " ").trim();
                    }
                } else if (txt[i].contains("-") && txt[i].contains("F")) {
                    String faxA = txt[i].substring(txt[i].indexOf("F"));
                    if(faxA.length() >= 15) {
                        fax = txt[i].substring(txt[i].indexOf("F"), txt[i].indexOf("F")+15).replace("F", " ").trim();
                    } else if(faxA.length() < 15 || faxA.length() >= 14) {
                        fax = txt[i].substring(txt[i].indexOf("F"), txt[i].indexOf("F")+14).replace("F", " ").trim();
                    } else {
                        fax = txt[i].substring(txt[i].indexOf("F"), faxA.length()).replace("F", " ").trim();
                    }
                }

                // 이메일
                if (txt[i].contains("@")) {
                    if (txt[i].contains("E.")) {
                        email = txt[i].substring(txt[i].indexOf("E."), txt[i].indexOf(".com")+4).replace("E.", " ").trim();
                    } else if (txt[i].contains("E")) {
                        email = txt[i].substring(txt[i].indexOf("E"), txt[i].indexOf(".com")+4).replace("E", " ").trim();
                    } else {
                        email = txt[i];
                    }
                }
            }

            // 1.명함 이미지 정보 저장
            CardImage cardImage = CardImage.builder()
                    .member(member)
                    .cardImgName(cardImgName)
                    .cardImgUrl(gcsPath)
                    .build();
            cardImageRepository.save(cardImage);

            // 2. 클라이언트에게 던져줄 정보
            OcrResponseDto ocrResponseDto = OcrResponseDto.builder()
                    .email(email)
                    .phoneNum(phoneNum)
                    .tel(tel)
                    .fax(fax)
                    .imgUrl(gcsPath)
                    .build();
            return ResponseDto.success(ocrResponseDto);
        }
    }
}