package com.sparta.finalpj.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    //MEMBER
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "M001", "해당 유저를 찾을 수 없습니다."),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND.value(),"M002","해당 이메일을 찾을 수 없습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND.value(),"M002","해당 게시글을 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND.value(),"M002","해당 댓글을 찾을 수 없습니다."),
    PASSWORD_NOT_CORRECT(HttpStatus.BAD_REQUEST.value(),"M003","비밀번호가 틀렸습니다."),
    NOT_AUTHOR(HttpStatus.BAD_REQUEST.value(), "M004", "작성자가 아닙니다."),

    //Token
    ACCESS_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND.value(),"M005","액세스 토큰이 없습니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND.value(),"M006","리프레시 토큰이 없습니다."),
    REFRESH_TOKEN_NOT_FOUND_IN_SERVER(HttpStatus.NOT_FOUND.value(),"M006","서버에 존재하지 않는 리프레시 토큰입니다."),

    TOKEN_IS_EXPIRED(HttpStatus.BAD_REQUEST.value(), "M007", "만료된 액세스 토큰 입니다."),
    REFRESH_TOKEN_IS_EXPIRED(HttpStatus.BAD_REQUEST.value(), "M008", "만료된 리프레시 토큰 입니다."),
    INVALID_TOKEN_DELETE(HttpStatus.BAD_REQUEST.value(), "M009", "유효하지 않은 토큰 입니다. 토큰을 삭제합니다"),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST.value(), "M009", "유효하지 않은 토큰 입니다."),

    // Validation
    DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST.value(),"M010","이미 사용되고 있는 이메일입니다."),
    DUPLICATED_NICKNAME(HttpStatus.BAD_REQUEST.value(),"M011","이미 사용되고 있는 닉네임입니다."),
    INVALID_MEMBER_INFO(HttpStatus.BAD_REQUEST.value(),"M012" ,"잘못된 사용자 정보입니다."),
    UNAUTHORIZED(HttpStatus.BAD_REQUEST.value(),"M013" ,"로그인이 필요합니다."),


    /*
    403 UNAUTHORIZED : 인증되지 않은 사용자
     */
    UNAUTHORIZATION_MEMBER(HttpStatus.UNAUTHORIZED.value(), "M014","회원만 사용가능합니다."),
    INVALID_AUTH_MEMBER_DELETE(HttpStatus.UNAUTHORIZED.value(), "M015","작성자 본인만 삭제가 가능합니다."),
    INVALID_AUTH_MEMBER_UPDATE(HttpStatus.UNAUTHORIZED.value(), "M016","작성자 본인만 수정이 가능합니다."),

    NULL_INPUT_ERROR(HttpStatus.BAD_REQUEST.value(), "M017", "필수 입력사항이 모두 입력되지 않았습니다."),
    SIGNUP_USERNAME_FORM_ERROR(HttpStatus.BAD_REQUEST.value(), "M018", "이름 형식을 맞춰주세요."),
    SIGNUP_EMAIL_FORM_ERROR(HttpStatus.BAD_REQUEST.value(), "M019", "EMAIL 형식을 맞춰주세요"),
    SIGNUP_MEMBER_ID_FORM_ERROR(HttpStatus.BAD_REQUEST.value(), "M020", "memberId 형식을 맞춰주세요"),
    SIGNUP_MEMBER_ID_DUPLICATE_ERROR(HttpStatus.BAD_REQUEST.value(), "M021", "memberId 가 중복됩니다"),
    SIGNUP_NICKNAME_FORM_ERROR(HttpStatus.BAD_REQUEST.value(), "M022", "nickname 형식을 맞춰주세요"),
    SIGNUP_PASSWORD_CHECK_ERROR(HttpStatus.BAD_REQUEST.value(), "M023", "password 와 passwordCheck 가 다릅니다"),
    SIGNUP_PASSWORD_FORM_ERROR(HttpStatus.BAD_REQUEST.value(), "M024", "password 형식을 맞춰주세요"),
    AWS_S3_UPLOAD_FAIL(HttpStatus.BAD_REQUEST.value(), "M024", "S3 사진 업로드 실패"),
    NOT_FOUND_CARD(HttpStatus.BAD_REQUEST.value(), "M025", "존재하지 않는 명함입니다."),

    EMPTY(HttpStatus.BAD_REQUEST.value(), "M027", "multipart 파일이 비어있습니다."),
    CONVERT_FAIL(HttpStatus.BAD_REQUEST.value(), "M026", "multipart을 파일로 변환하지 못하였습니다."),
    REMOVE_FAIL(HttpStatus.BAD_REQUEST.value(), "M027", "파일 제거를 실패하였습니다."),
    UPLOAD_FAIL_TO_GOOGLE(HttpStatus.BAD_REQUEST.value(), "M028", "Google Cloud에 파일을 업로드하지 못했습니다."),
    EMPTY_IMAGE_FILE(HttpStatus.INTERNAL_SERVER_ERROR.value(), "M029", "파일을 첨부해주세요."),
    CARDINFO_UPDATE_FAIL(HttpStatus.BAD_REQUEST.value(), "M30", "명함정보 등록을 실패했습니다."),
    NOT_FOUND_CARDINFO(HttpStatus.INTERNAL_SERVER_ERROR.value(), "M031", "명함정보 등록을 실패했습니다."),
    NOT_FOUND_CALNFO(HttpStatus.INTERNAL_SERVER_ERROR.value(), "M032", "캘린더 정보가 없습니다.")
    ;

    private final int httpStatus;
    private final String code;
    private final String message;

//    public static Object builder() {
//    }
}
