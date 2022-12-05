package com.sparta.finalpj.service;

import com.google.cloud.vision.v1.*;
import com.sparta.finalpj.controller.response.ResponseDto;
import com.sparta.finalpj.controller.response.ocr.OcrResponseDto;
import com.sparta.finalpj.domain.Member;
import com.sparta.finalpj.exception.CustomException;
import com.sparta.finalpj.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OcrService {
    private final CommonService commonService;
    private final GoogleCloudUploadService googleCloudUploadService;

    @Value("${cloud.gcp.storage.bucket.filePath}")
    String bucketFilePath;

    public ResponseDto<?> readFileInfo(MultipartFile cardImg, HttpServletRequest request) throws IOException {
        // 1. 로그인 확인
        commonService.loginCheck(request);

        // 2. Token validation => member 생성
        Member member = commonService.validateMember(request);
        if (null == member) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        // *첨부파일이 없을 경우
        if (cardImg.isEmpty()) {
            throw new CustomException(ErrorCode.EMPTY_IMAGE_FILE);
        }

        UUID uuid = UUID.randomUUID();
        String fileName = uuid.toString() + "_" + cardImg.getOriginalFilename();

        String imgUrl = "";
        imgUrl = googleCloudUploadService.upload("card", cardImg, fileName);

        // Google Storage 경로
        String filePath = bucketFilePath + "card/" + fileName;


        return detectTextGcs(filePath, imgUrl);
    }

    // Google 클라우드 저장소의 지정된 원격 이미지에서 텍스트를 추출
    public ResponseDto<?> detectTextGcs(String gcsPath, String imgUrl) throws IOException {
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
                String text = txt[i];

                // 휴대폰 번호 (M)
                if (text.contains("-") && text.contains("010") || text.contains("82") || text.contains("+82")) {

                    if (text.contains("M.")) {
                        String phoneStr = text.substring(text.indexOf("M.")).replace("M.", "").trim();
                        phoneNum = phoneStr.substring(0, phoneStr.indexOf("-", 5) + 5);

                    } else if (text.contains("M")) {
                        String phoneStr = text.substring(text.indexOf("M")).replace("M", "").trim();
                        phoneNum = phoneStr.substring(0, phoneStr.indexOf("-", 5) + 5);

                    } else if (text.contains("Mobile.")) {
                        String phoneStr = text.substring(text.indexOf("Mobile.")).replace("Mobile.", "").trim();
                        phoneNum = phoneStr.substring(0, phoneStr.indexOf("-", 5) + 5);

                    }  else if (text.contains("Mobile")) {
                        String phoneStr = text.substring(text.indexOf("Mobile")).replace("Mobile", "").trim();
                        phoneNum = phoneStr.substring(0, phoneStr.indexOf("-", 5) + 5);

                    } else {
                        phoneNum = text.trim();
                    }
                }

                // companyTel (T)
                if (text.contains("T.") && (text.contains("-") || text.contains(" "))) {
                    String telStr = text.substring(text.indexOf("T.")).replace("T.", "").trim();
                    if (text.contains("-")) {
                        tel = telStr.substring(0, telStr.indexOf("-", 5) + 5);
                    } else {
                        tel = telStr.substring(0, telStr.indexOf(" ", 5) + 5);
                    }

                } else if (text.contains("T") && (text.contains("-") || text.contains(" "))) {
                    String telStr = text.substring(text.indexOf("T")).replace("T", "").trim();
                    if (text.contains("-")) {
                        tel = telStr.substring(0, telStr.indexOf("-", 5) + 5);
                    } else {
                        tel = telStr.substring(0, telStr.indexOf(" ", 5) + 5);
                    }

                } else if (text.contains("Tel.") && (text.contains("-") || text.contains(" "))) {
                    String telStr = text.substring(text.indexOf("Tel.")).replace("Tel.", "").trim();
                    if (text.contains("-")) {
                        tel = telStr.substring(0, telStr.indexOf("-", 5) + 5);
                    } else {
                        tel = telStr.substring(0, telStr.indexOf(" ", 5) + 5);
                    }

                } else if (text.contains("Tel") && (text.contains("-") || text.contains(" "))) {
                    String telStr = text.substring(text.indexOf("Tel")).replace("Tel", "").trim();
                    if (text.contains("-")) {
                        tel = telStr.substring(0, telStr.indexOf("-", 5) + 5);
                    } else {
                        tel = telStr.substring(0, telStr.indexOf(" ", 5) + 5);
                    }
                }

                // fax (F)
                if (text.contains("F.") && (text.contains("-") || text.contains(" ") || text.contains("."))) {
                    String faxStr = text.substring(text.indexOf("F.")).replace("F.", "").trim();
                    if (text.contains("-")) {
                        fax = faxStr.substring(0, faxStr.indexOf("-", 5) + 5);
                    } else if (text.contains(" ")) {
                        fax = faxStr.substring(0, faxStr.indexOf(" ", 5) + 5);
                    } else {
                        fax = faxStr.substring(0, faxStr.indexOf(".", 5) + 5);
                    }

                } else if (text.contains("F,") && (text.contains("-") || text.contains(" ") || text.contains("."))) {
                    String faxStr = text.substring(text.indexOf("F,")).replace("F", "").trim();
                    if (text.contains("-")) {
                        fax = faxStr.substring(0, faxStr.indexOf("-", 5) + 5);
                    } else if (text.contains(" ")) {
                        fax = faxStr.substring(0, faxStr.indexOf(" ", 5) + 5);
                    } else {
                        fax = faxStr.substring(0, faxStr.indexOf(".", 5) + 5);
                    }

                } else if (text.contains("F") && (text.contains("-") || text.contains(" ") || text.contains("."))) {
                    String faxStr = text.substring(text.indexOf("F")).replace("F", "").trim();
                    if (text.contains("-")) {
                        fax = faxStr.substring(0, faxStr.indexOf("-", 5) + 5);
                    } else if (text.contains(" ")) {
                        fax = faxStr.substring(0, faxStr.indexOf(" ", 5) + 5);
                    } else {
                        fax = faxStr.substring(0, faxStr.indexOf(".", 5) + 5);
                    }
                } else if (text.contains("Fax.") && (text.contains("-") || text.contains(" ") || text.contains("."))) {
                    String faxStr = text.substring(text.indexOf("Fax.")).replace("Fax.", "").trim();
                    if (text.contains("-")) {
                        fax = faxStr.substring(0, faxStr.indexOf("-", 5) + 5);
                    } else if (text.contains(" ")) {
                        fax = faxStr.substring(0, faxStr.indexOf(" ", 5) + 5);
                    } else {
                        fax = faxStr.substring(0, faxStr.indexOf(".", 5) + 5);
                    }

                } else if (text.contains("Fax") && (text.contains("-") || text.contains(" ") || text.contains("."))) {
                    String faxStr = text.substring(text.indexOf("Fax")).replace("Fax", "").trim();
                    if (text.contains("-")) {
                        fax = faxStr.substring(0, faxStr.indexOf("-", 5) + 5);
                    } else if (text.contains(" ")) {
                        fax = faxStr.substring(0, faxStr.indexOf(" ", 5) + 5);
                    } else {
                        fax = faxStr.substring(0, faxStr.indexOf(".", 5) + 5);
                    }
                }

                // 이메일
                if (text.contains("@")) {
                    if (text.contains(".com") && (text.contains("E.") || text.contains("E"))) {
                        email = text.contains("E.")
                                ? text.substring(text.indexOf("E."), text.indexOf(".com") + 4).replace("E.", " ").trim()
                                : text.substring(text.indexOf("E"), text.indexOf(".com") + 4).replace("E", " ").trim();

                    } else if (text.contains(".kr") && (text.contains("E.") || text.contains("E"))) {
                        email = text.contains("E.")
                                ? text.substring(text.indexOf("E."), text.indexOf(".kr") + 3).replace("E.", " ").trim()
                                : text.substring(text.indexOf("E"), text.indexOf(".kr") + 3).replace("E", " ").trim();

                    } else if (text.contains(".com") && (text.contains("Email.") || text.contains("Email"))) {
                        email = text.contains("Email.")
                                ? text.substring(text.indexOf("Email."), text.indexOf(".com") + 4).replace("Email.", " ").trim()
                                : text.substring(text.indexOf("Email"), text.indexOf(".com") + 4).replace("Email", " ").trim();

                    } else if (text.contains(".com") && (text.contains("Email.") || text.contains("Email"))) {
                        email = text.contains("Email.")
                                ? text.substring(text.indexOf("Email."), text.indexOf(".com") + 4).replace("Email.", " ").trim()
                                : text.substring(text.indexOf("Email"), text.indexOf(".com") + 4).replace("Email", " ").trim();

                    } else if (text.contains(".kr") && (text.contains("Email.") || text.contains("Email"))) {
                        email = text.contains("Email.")
                                ? text.substring(text.indexOf("Email."), text.indexOf(".kr") + 4).replace("Email.", " ").trim()
                                : text.substring(text.indexOf("Email"), text.indexOf(".kr") + 4).replace("Email", " ").trim();
                    } else {
                        email = text.trim();
                    }
                }
            }

            // 클라이언트에게 던져줄 정보
            OcrResponseDto ocrResponseDto = OcrResponseDto.builder()
                    .email(email)
                    .phoneNum(phoneNum)
                    .tel(tel)
                    .fax(fax)
                    .imgUrl(imgUrl)
                    .build();
            return ResponseDto.success(ocrResponseDto);
        }
    }
}