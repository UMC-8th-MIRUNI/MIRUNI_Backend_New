package com.miruni.backend.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommonErrorCode implements ErrorCode {

    // ===== 공통 에러 (4xx) =====
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_001", "잘못된 요청입니다."),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "COMMON_002", "입력값 검증에 실패했습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON_003", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON_004", "접근 권한이 없습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_005", "요청한 리소스를 찾을 수 없습니다."),
    NOT_SUPPORTED_METHOD_ERROR(HttpStatus.METHOD_NOT_ALLOWED, "COMMON_006", "지원하지 않는 HTTP 메서드입니다."),
    NOT_SUPPORTED_URI_ERROR(HttpStatus.NOT_FOUND, "COMMON_007", "지원하지 않는 URI입니다."),
    NOT_SUPPORTED_MEDIA_TYPE_ERROR(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "COMMON_008", "지원하지 않는 미디어 타입입니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON_009", "유효하지 않은 값입니다."),

    // ===== 서버 에러 (5xx) =====
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_001", "서버 내부 오류가 발생했습니다."),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_002", "데이터베이스 오류가 발생했습니다."),
    EXTERNAL_API_ERROR(HttpStatus.BAD_GATEWAY, "SERVER_003", "외부 API 호출에 실패했습니다."),

    // == 이미지 파일 에러  == //
    FILE_IS_EMPTY(HttpStatus.BAD_REQUEST, "FILE_001", "파일이 비어있습니다."),
    NOT_SUPPORTED_TYPE_ERROR(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "FILE_002", "지원하지 않는 형식의 타입입니다."),
    NOT_FOUND_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "FILE_003", "파일 확장자가 없습니다."),
    FILE_SIZE_EXCEED_LIMIT(HttpStatus.BAD_REQUEST, "FILE_004", "파일크기가 최대 제한을 초과했습니다."),
    NOT_IMAGE_CONTENT_TYPE(HttpStatus.BAD_REQUEST, "FILE_005", "이미지 파일만 업로드 가능합니다."),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_006", "파일 업로드에 실패했습니다."),
    FILE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_007", "파일 삭제에 실패했습니다."),
    INVALID_FILE_EXTENSION(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "FILE_008", "지원하지 않는 미디어타입입니다."),
    GENERATED_PRESIGNED_URL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_009", "presigned URL을 생성하는 데 실패했습니다.")
    ;


    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
