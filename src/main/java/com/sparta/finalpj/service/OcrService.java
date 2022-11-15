package com.sparta.finalpj.service;

import com.google.cloud.vision.v1.*;
import com.sparta.finalpj.controller.request.card.CardRequestDto;
import com.sparta.finalpj.controller.response.ResponseDto;
import com.sparta.finalpj.controller.response.ocr.OcrResponseDto;
import com.sparta.finalpj.domain.Card;
import com.sparta.finalpj.domain.CardImage;
import com.sparta.finalpj.domain.Member;
import com.sparta.finalpj.exception.CustomException;
import com.sparta.finalpj.exception.ErrorCode;
import com.sparta.finalpj.repository.CardImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OcrService {

    @Value("${cloud.gcp.storage.bucket.filePath}")
    String bucketFilePath;

    private final CommonService commonService;
    private final CardService cardService;
    private final GoogleCloudUploadService googleCloudUploadService;
    private final CardImageRepository cardImageRepository;

    public ResponseDto<?> detectTextGcs(MultipartFile cardImg, HttpServletRequest request) throws IOException {
        // 1. 로그인 확인
        commonService.loginCheck(request);

        // 2. Token validation => member 생성
        Member member = commonService.validateMember(request);
        if (null == member) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        // 3.파일 업로드
        googleCloudUploadService.upload(cardImg);

        // Google Storage 경로
        String fileName = cardImg.getOriginalFilename();
        String filePath = bucketFilePath + fileName;

        // 4.OCR
        return detectTextGcs(filePath, fileName, member);
    }

    // Google 클라우드 저장소의 지정된 원격 이미지에서 텍스트를 검색
    public ResponseDto<?> detectTextGcs(String gcsPath, String fileName, Member member) throws IOException {
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

            // 명함 데이터 담는 변수
            String email = ""; // 이메일
            String phoneNum = ""; // 폰번호
            String tel = ""; // 회사 번호
            String fax = ""; // 팩스

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    log.error("Error: %s%n", res.getError().getMessage());
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

            // TODO: 필요한 형식 더 추가하기
            // parsing 
            for (int i = 0; i < txt.length; i++) {
                // 휴대폰 번호 (M)
                if (txt[i].contains("-") && txt[i].contains("M")) {
                    phoneNum = txt[i].substring(txt[i].indexOf("M"), txt[i].indexOf("M")+15).replace("M", " ").trim();
                    log.info("===========phone1=========");
                    log.debug(phoneNum);
                    System.out.println("===========phone1=========");
                    System.out.println(phoneNum);

                } else if (txt[i].contains("-") && txt[i].contains("010") || txt[i].contains("82")) {
                    System.out.println("===========phone2=========");
                    phoneNum = txt[i].trim();
                }

                // companyTel (T)
                if (txt[i].contains("-") && txt[i].contains("T")) {
                    tel =  txt[i].substring(txt[i].indexOf("T"), txt[i].indexOf("T")+15).replace("T", " ").trim();
                    System.out.println("==========companyTel1==========");
                    System.out.println(tel);
                }

                // fax (F)
                if (txt[i].contains("-") && txt[i].contains("F")) {
                    fax = txt[i].replace("F", " ").trim();
                    System.out.println("==========fax1==========");
                    System.out.println(fax);

                    if (txt[i].contains("-") || txt[i].contains("F")) {
                        fax = txt[i].replace("F", " ").trim();
                        System.out.println("==========fax2==========");
                    }
                }
                // 이메일
                if (txt[i].contains("@")) {
                    System.out.println("==========email1==========");
                    System.out.println(email);
                    email = txt[i];
                }
                // Todo: 회사 주소 유효성 검사 체크, 경우의 수 확인 ( 도로명, 번지)

            }
            // 1.명함 이미지 정보 저장
            CardImage cardImage = CardImage.builder()
                    .member(member)
                    .cardImgName(fileName)
                    .cardImgUrl(gcsPath)
                    .build();
            cardImageRepository.save(cardImage);

            // 2. 클라이언트에게 던져줄 정보
            CardRequestDto cardRequestDto = CardRequestDto.builder()
                    .email(email)
                    .phoneNum(phoneNum)
                    .tel(tel)
                    .fax(fax)
                    .build();
            return ResponseDto.success(cardRequestDto);
        }
    }
}