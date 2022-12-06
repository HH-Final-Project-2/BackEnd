package com.sparta.finalpj.service;

import com.google.cloud.vision.v1.*;
import com.sparta.finalpj.controller.response.ResponseDto;
import com.sparta.finalpj.controller.response.ocr.OcrResponseDto;
import com.sparta.finalpj.domain.Member;
import com.sparta.finalpj.exception.CustomException;
import com.sparta.finalpj.exception.CustomResponseBody;
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

                // 1. 휴대폰 번호
                if (text.contains("-") && text.contains("010") || text.contains("82") || text.contains("+82")) {

                    // ex) Mobile. 010-0000-0000
                    if (text.contains("Mobile.")) {
                        String phoneStr = text.substring(text.indexOf("Mobile.")).replace("Mobile.", "").trim();
                        phoneNum = phoneStr.substring(0, phoneStr.indexOf("-", 5) + 5);

                    // ex) Mobile 010-0000-0000
                    } else if (text.contains("Mobile")) {
                        String phoneStr = text.substring(text.indexOf("Mobile")).replace("Mobile", "").trim();
                        phoneNum = phoneStr.substring(0, phoneStr.indexOf("-", 5) + 5);

                    // ex) M. 010-0000-0000
                    } else if (text.contains("M.") ) {
                        String phoneStr1 = text.substring(text.indexOf("M.")).replace("M.", "").trim();
                        String phoneStr2 = phoneStr1.substring(0, phoneStr1.indexOf("-", 5) + 5);
                        if (phoneStr2.length() < 15) {
                            phoneNum = phoneStr2;
                        }

                    // ex) M 010-0000-0000
                    } else if (text.contains("M")) {
                        String phoneStr1 = text.substring(text.indexOf("M")).replace("M", "").trim();
                        String phoneStr2 = phoneStr1.substring(0, phoneStr1.indexOf("-", 5) + 5);
                        if (phoneStr2.length() < 15) {
                            phoneNum = phoneStr2;
                        }
                    } else {
                        phoneNum = text.trim();
                    }

                // 1. 휴대폰 번호
                } else if (text.contains(" ") && text.contains("010") || text.contains("82") || text.contains("+82")) {

                    // ex) Mobile. 010 0000 0000
                    if (text.contains("Mobile.")) {
                        String phoneStr = text.substring(text.indexOf("Mobile.")).replace("Mobile.", "").trim();
                        phoneNum = phoneStr.substring(0, phoneStr.indexOf(" ", 5) + 5).replace(" ", "-");

                    // ex) Mobile 010 0000 0000
                    } else if (text.contains("Mobile")) {
                        String phoneStr = text.substring(text.indexOf("Mobile")).replace("Mobile", "").trim();
                        phoneNum = phoneStr.substring(0, phoneStr.indexOf(" ", 5) + 5).replace(" ", "-");

                    // ex) M. 010 0000 0000
                    } else if (text.contains("M.")) {
                        String phoneStr1 = text.substring(text.indexOf("M.")).replace("M.", "").trim();
                        String phoneStr2 = phoneStr1.substring(0, phoneStr1.indexOf(" ", 5) + 5).replace(" ", "-");
                        if (phoneStr2.length() < 15) {
                            phoneNum = phoneStr2;
                        }

                    // ex) M 010 0000 0000
                    } else if (text.contains("M")) {
                        String phoneStr1 = text.substring(text.indexOf("M")).replace("M", "").trim();
                        String phoneStr2 = phoneStr1.substring(0, phoneStr1.indexOf(" ", 5) + 5).replace(" ", "-");
                        if (phoneStr2.length() < 15) {
                            phoneNum = phoneStr2;
                        }
                        
                    } else {
                        phoneNum = text.trim();
                    }
                }

                // 2. 회사번호
                if (text.contains("T") && (text.contains("-") || text.contains(" "))) {

                    // ex) Tel. 070-0000-0000 / Tel. 070 0000 0000
                    if (text.contains("Tel.") && (text.contains("-") || text.contains(" "))) {
                        String telStr = text.substring(text.indexOf("Tel.")).replace("Tel.", "").trim();
                        if (text.contains("-")) {
                            tel = telStr.substring(0, telStr.indexOf("-", 5) + 5);
                        } else if (telStr.contains(" ") && telStr.contains("0")) {
                            tel = telStr.substring(0, telStr.indexOf(" ", 5) + 5).replace(" ", "-");
                        }

                    // ex) TEL. 070-0000-0000 / TEL. 070 0000 0000
                    } else if (text.contains("TEL.") && (text.contains("-") || text.contains(" "))) {
                        String telStr = text.substring(text.indexOf("TEL.")).replace("TEL.", "").trim();
                        if (text.contains("-")) {
                            tel = telStr.substring(0, telStr.indexOf("-", 5) + 5);
                        } else if (telStr.contains(" ") && telStr.contains("0")) {
                            tel = telStr.substring(0, telStr.indexOf(" ", 5) + 5).replace(" ", "-");
                        }

                    // ex) Tel 070-0000-0000 / Tel 070 0000 0000
                    }  else if (text.contains("Tel") && (text.contains("-") || text.contains(" "))) {
                        String telStr = text.substring(text.indexOf("Tel")).replace("Tel", "").trim();
                        if (text.contains("-")) {
                            tel = telStr.substring(0, telStr.indexOf("-", 5) + 5);
                        } else if (telStr.contains(" ") && telStr.contains("0")) {
                            tel = telStr.substring(0, telStr.indexOf(" ", 5) + 5).replace(" ", "-");
                        }

                    // ex) TEL 070-0000-0000 / TEL 070 0000 0000
                    } else if (text.contains("TEL") && (text.contains("-") || text.contains(" "))) {
                        String telStr = text.substring(text.indexOf("TEL")).replace("TEL", "").trim();
                        if (text.contains("-")) {
                            tel = telStr.substring(0, telStr.indexOf("-", 5) + 5);

                        } else if (telStr.contains(" ") && telStr.contains("0")) {
                            tel = telStr.substring(0, telStr.indexOf(" ", 5) + 5).replace(" ", "-");
                        }

                    // ex) T. 070-0000-0000 / T. 070 0000 0000
                    } else if (text.contains("T.") && (text.contains("-") || text.contains(" "))) {
                        String telStr = text.substring(text.indexOf("T.")).replace("T.", "").trim();
                        if (telStr.contains("-")) {
                            tel = telStr.substring(0, telStr.indexOf("-", 5) + 5);
                        } else if (telStr.contains(" ") && telStr.contains("0")) {
                            tel = telStr.substring(0, telStr.indexOf(" ", 5) + 5).replace(" ", "-");;
                        }

                    // ex) T 070-0000-0000 / T 070 0000 0000
                    } else {
                        String telStr = text.substring(text.indexOf("T")).replace("T", "").trim();
                        if (text.contains("-")) {
                            tel = telStr.substring(0, telStr.indexOf("-", 5) + 5);
                        } else if (telStr.contains(" ") && telStr.contains("0")) {
                            tel = telStr.substring(0, telStr.indexOf(" ", 5) + 5).replace(" ", "-");;
                        }
                    }
                }

                // 3. 팩스(fax)
                 if (text.contains("F") && (text.contains("-") || text.contains(" ") || text.contains("."))) {

                    if (text.contains("Fax.") && (text.contains("-") || text.contains(" ") || text.contains("."))) {
                        String faxStr = text.substring(text.indexOf("Fax.")).replace("Fax.", "").trim();
                        if (faxStr.contains("-")) {
                            fax = faxStr.substring(0, faxStr.indexOf("-", 5) + 5);
                        } else if (faxStr.contains(" ") && faxStr.contains("0")) {
                            fax = faxStr.substring(0, faxStr.indexOf(" ", 5) + 5);
                        } else if (faxStr.contains(".")) {
                            fax = faxStr.substring(0, faxStr.indexOf(".", 5) + 5);
                        }

                    } else if (text.contains("Fax") && (text.contains("-") || text.contains(" ") || text.contains("."))) {
                        String faxStr = text.substring(text.indexOf("Fax")).replace("Fax", "").trim();
                        if (faxStr.contains("-")) {
                            fax = faxStr.substring(0, faxStr.indexOf("-", 5) + 5);
                        } else if (faxStr.contains(" ") && faxStr.contains("0")) {
                            fax = faxStr.substring(0, faxStr.indexOf(" ", 5) + 5);
                        } else if (faxStr.contains(".")) {
                            fax = faxStr.substring(0, faxStr.indexOf(".", 5) + 5);
                        }

                    } else if (text.contains("FAX") && (text.contains("-") || text.contains(" ") || text.contains("."))) {
                        String faxStr = text.substring(text.indexOf("FAX")).replace("FAX", "").trim();
                        if (faxStr.contains("-")) {
                            fax = faxStr.substring(0, faxStr.indexOf("-", 5) + 5);
                        } else if (faxStr.contains(" ") && faxStr.contains("0")) {
                            fax = faxStr.substring(0, faxStr.indexOf(" ", 5) + 5);
                        } else if (faxStr.contains(".")) {
                            fax = faxStr.substring(0, faxStr.indexOf(".", 5) + 5);
                        }

                    } else if (text.contains("FAX.") && (text.contains("-") || text.contains(" ") || text.contains("."))) {
                        String faxStr = text.substring(text.indexOf("FAX.")).replace("FAX.", "").trim();
                        if (faxStr.contains("-")) {
                            fax = faxStr.substring(0, faxStr.indexOf("-", 5) + 5);
                        } else if (faxStr.contains(" ") && faxStr.contains("0")) {
                            fax = faxStr.substring(0, faxStr.indexOf(" ", 5) + 5);
                        } else if (faxStr.contains(".")) {
                            fax = faxStr.substring(0, faxStr.indexOf(".", 5) + 5);
                        }

                    } else if (text.contains("F.") && (text.contains("-") || text.contains(" ") || text.contains("."))) {
                        String faxStr = text.substring(text.indexOf("F.")).replace("F.", "").trim();
                        if (text.contains("-")) {
                            fax = faxStr.substring(0, faxStr.indexOf("-", 5) + 5);
                        } else if (faxStr.contains(" ") && faxStr.contains("0")) {
                            fax = faxStr.substring(0, faxStr.indexOf(" ", 5) + 5);
                        } else if (faxStr.contains(".")) {
                            fax = faxStr.substring(0, faxStr.indexOf(".", 5) + 5);
                        }

                    } else if (text.contains("F,") && (text.contains("-") || text.contains(" ") || text.contains("."))) {
                        String faxStr = text.substring(text.indexOf("F,")).replace("F,", "").trim();
                        if (text.contains("-")) {
                            fax = faxStr.substring(0, faxStr.indexOf("-", 5) + 5);
                        } else if (faxStr.contains(" ") && faxStr.contains("0")) {
                            fax = faxStr.substring(0, faxStr.indexOf(" ", 5) + 5);
                        } else if (faxStr.contains(".")) {
                            fax = faxStr.substring(0, faxStr.indexOf(".", 5) + 5);
                        }

                    } else {
                        String faxStr = text.substring(text.indexOf("F")).replace("F", "").trim();
                        if (faxStr.contains("-")) {
                            fax = faxStr.substring(0, faxStr.indexOf("-", 5) + 5);
                        } else if (faxStr.contains(" ") && faxStr.contains("0")) {
                            fax = faxStr.substring(0, faxStr.indexOf(" ", 5) + 5);
                        } else if (faxStr.contains(".")) {
                            fax = faxStr.substring(0, faxStr.indexOf(".", 5) + 5);
                        }
                    }
                }

                // 4. 이메일
                if (text.contains("@")) {
                    String emailStr = text.substring(text.indexOf("E"));
                    if (emailStr.contains(".com") && (emailStr.contains("E-mail.") || emailStr.contains("E-mail"))) {
                        email = emailStr.contains("E-mail.")
                                ? emailStr.substring(emailStr.indexOf("E-mail."), emailStr.indexOf(".com") + 4).replace("E-mail.", " ").trim()
                                : emailStr.substring(emailStr.indexOf("E-mail"), emailStr.indexOf(".com") + 4).replace("E-mail", " ").trim();

                    } else if (emailStr.contains(".kr") && (emailStr.contains("E-mail.") || emailStr.contains("E-mail"))) {
                        email = emailStr.contains("E-mail.")
                                ? emailStr.substring(emailStr.indexOf("E-mail."), emailStr.indexOf(".kr") + 3).replace("E-mail.", " ").trim()
                                : emailStr.substring(emailStr.indexOf("E-mail"), emailStr.indexOf(".kr") + 3).replace("E-mail", " ").trim();

                    }else if (emailStr.contains(".com") && (emailStr.contains("Email.") || emailStr.contains("Email"))) {
                        email = emailStr.contains("Email.")
                                ? emailStr.substring(emailStr.indexOf("Email."), emailStr.indexOf(".com") + 4).replace("Email.", " ").trim()
                                : emailStr.substring(emailStr.indexOf("Email"), emailStr.indexOf(".com") + 4).replace("Email", " ").trim();

                    } else if (emailStr.contains(".kr") && (emailStr.contains("Email.") || emailStr.contains("Email"))) {
                        email = emailStr.contains("Email.")
                                ? emailStr.substring(emailStr.indexOf("Email."), emailStr.indexOf(".kr") + 3).replace("Email.", " ").trim()
                                : emailStr.substring(emailStr.indexOf("Email"), emailStr.indexOf(".kr") + 3).replace("Email", " ").trim();

                    } else if (emailStr.contains(".com") && (emailStr.contains("E.") || emailStr.contains("E"))) {
                        email = emailStr.contains("E.")
                                ? emailStr.substring(emailStr.indexOf("E."), emailStr.indexOf(".com") + 4).replace("E.", " ").trim()
                                : emailStr.substring(emailStr.indexOf("E"), emailStr.indexOf(".com") + 4).replace("E", " ").trim();

                    } else if (emailStr.contains(".kr") && (text.contains("E.") || emailStr.contains("E"))) {
                        email = emailStr.contains("E.")
                                ? emailStr.substring(emailStr.indexOf("E."), emailStr.indexOf(".kr") + 3).replace("E.", " ").trim()
                                : emailStr.substring(emailStr.indexOf("E"), emailStr.indexOf(".kr") + 3).replace("E", " ").trim();

                    } else {
                        email = emailStr.trim();
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
        } catch (StringIndexOutOfBoundsException e) {
            return ResponseDto.fail(new CustomResponseBody(ErrorCode.NOT_FOUND_TEXT));
        }
    }
}