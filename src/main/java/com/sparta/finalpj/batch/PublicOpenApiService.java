package com.sparta.finalpj.batch;

import com.sparta.finalpj.domain.CompanyInfo;
import com.sparta.finalpj.repository.CompanyInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PublicOpenApiService {

//    @Value("${spring.open.api.service.key}")
//    String serviceKey;

    private final CompanyInfoRepository companyInfoRepository;

//    private int pageNo = 0;
//
//    public ArrayList<PublicOpenApiResponseDto> getOpenApiAllList() throws IOException, ParseException {
//        pageNo++;
//        if(pageNo == 156) return null;
//
//        System.out.println("pageNo" + pageNo);
//        // 1. URL을 만들기 위한 StringBuilder
//        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1160100/service/GetCorpBasicInfoService/getCorpOutline"); /*URL*/
//        // 2. 오픈 API의요청 규격에 맞는 파라미터 생성, 발급받은 인증키
//        urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + serviceKey); /*Service Key(필수)*/
//        urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(pageNo), "UTF-8")); /*페이지번호(필수)*/
//        urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("500", "UTF-8")); /*한 페이지 결과 수(필수)*/
//        urlBuilder.append("&" + URLEncoder.encode("resultType", "UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /* 결과형식(xml/json)(필수) */
//        urlBuilder.append("&" + URLEncoder.encode("basDt", "UTF-8") + "=" + URLEncoder.encode("20200509", "UTF-8")); /*기준일자 (필수)*/
////        urlBuilder.append("&" + URLEncoder.encode("corpNm", "UTF-8") + "=" + URLEncoder.encode(requestDto.getCompanyName(), "UTF-8")); /*법인(法人)의 명칭*/
//
//        // 3. URL 객체 생성
//        URL url = new URL(urlBuilder.toString());
//        // 4. 요청하고자 하는 URL과 통신하기 위한 Connection 객체 생성
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        // 5. 통신을 위한 메소드 SET
//        conn.setRequestMethod("GET");
//        // 6. 통신을 위한 Content-type SET
//        conn.setRequestProperty("Content-type", "application/json");
//        // 7. 통신 응답 코드 확인
//        log.info("############ Response code: " + conn.getResponseCode());
//        // 8. 전달받은 데이터를 BufferedReader 객체로 저장
//        BufferedReader rd;
//
//        // getResponseCode가 200이상 300이하일때는 정상적으로 작동
//        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
//            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//        } else {
//            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
//        }
//        // 9. 저장된 데이터를 라인별로 읽어 StringBuilder 객체로 저장
//        StringBuilder sb = new StringBuilder();
//        String line;
//        while ((line = rd.readLine()) != null) {
//            sb.append(line);
//        }
//
//        // 1) Json parser를 만들어 만들어진 문자열 데이터를 객체화 한다
//        JSONParser parser = new JSONParser();
//        JSONObject obj = (JSONObject) parser.parse(sb.toString());
//
//        // 2-1) Top레벨 단계인 response 키를 가지고 데이터를 파싱한다
//        JSONObject parse_response = (JSONObject) obj.get("response");
//        // 2-2) response 로 부터 body 찾아온다
//        JSONObject parse_body = (JSONObject) parse_response.get("body");
//        // 2-3) body 로 부터 items 받아온다
//        JSONObject parse_items = (JSONObject) parse_body.get("items");
//        // 2-4) items로 부터 itemlist 를 받아오기 itemlist : 뒤에 [ 로 시작하므로 jsonarray이다
//        JSONArray parse_item = (JSONArray) parse_items.get("item");
//
//        JSONObject info; // parse_item은 배열형태이기 때문에 하나씩 데이터를 하나씩 가져올때 사용한다
//
//        // 3) 파싱한 데이터를 담을 리스트 및 변수
//        ArrayList<PublicOpenApiResponseDto> responseDtoList = new ArrayList<>();
//        String crno = "";
//        String companyName = "";
//        String companyAddress = "";
//
//        for (int i = 0; i < parse_item.size(); i++) {
//            info = (JSONObject) parse_item.get(i);
//            crno = info.get("crno").toString();
//            companyName = info.get("corpNm").toString();
//            companyAddress = info.get("enpBsadr").toString();
//
//            responseDtoList.add(
//                    PublicOpenApiResponseDto.builder()
//                            .crno(crno)
//                            .companyName(companyName)
//                            .companyAddress(companyAddress)
//                            .build()
//            );
//
//        }
//
//        // 10. 객체 해제
//        rd.close();
//        conn.disconnect();
//
//        return responseDtoList;
//    }

    public void deleteAll() {
        log.info(" ####### deleteAll START >>>>>>>>");
        companyInfoRepository.deleteAll();
    }

    public void saveAll(List<CompanyInfo> companyInfos) {
        for (int i = 0; i < companyInfos.size(); i++) {

            CompanyInfo companyInfo = CompanyInfo.builder()
                    .crno(companyInfos.get(i).getCrno())
                    .companyName(companyInfos.get(i).getCompanyName())
                    .companyAddress(companyInfos.get(i).getCompanyAddress())
                    .build();
            companyInfoRepository.save(companyInfo);
        }
    }
}