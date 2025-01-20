package umc.puppymode.apiPayload.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import umc.puppymode.apiPayload.code.BaseErrorCode;
import umc.puppymode.apiPayload.code.ErrorReasonDTO;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _BAD_REQUEST_SAME_STATE(HttpStatus.BAD_REQUEST, "COMMON4002", "수정하려는 데이터가 현재 상태와 동일합니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // 사용자 관련 예외 처리
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "USER404", "해당 ID를 가진 사용자를 찾을 수 없습니다."),

    // Firebase 관련 예외 처리
    FIREBASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FIREBASE500", "Firebase 서버 오류"),
    FIREBASE_MESSAGE_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FIREBASE501", "FCM 메시지 전송 실패"),
    FIREBASE_MISSING_TOKEN(HttpStatus.BAD_REQUEST, "FIREBASE502", "FCM 토큰을 찾을 수 없습니다."),
    FIREBASE_SCHEDULE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FIREBASE503", "푸시 알림 스케줄 오류"),
    FIREBASE_MESSAGE_SCHEDULE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FIREBASE504", "FCM 알림 예약 실패"),

    // 위치 관련 예외 처리
    LOCATION_NOT_IN_RANGE(HttpStatus.BAD_REQUEST, "LOCATION400", "약속 장소와의 거리가 1km 이내가 아닙니다."),
    DISTANCE_CALCULATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "LOCATION402", "거리 계산 중 오류가 발생했습니다."),

    // 약속 시간 관련 예외 처리
    APPOINTMENT_TIME_MISMATCH(HttpStatus.BAD_REQUEST, "APPOINTMENT400", "약속 시간이 현재 시간과 다릅니다."),
    APPOINTMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "APPOINTMENT404", "해당 ID의 약속을 찾을 수 없습니다."),

    // 요청 데이터가 유효하지 않은 경우
    INVALID_REQUEST_DATA(HttpStatus.BAD_REQUEST, "COMMON4001", "잘못된 요청 데이터입니다."),


    // For test
    TEMP_EXCEPTION(HttpStatus.BAD_REQUEST, "TEMP4001", "이거는 테스트"),

    // 강아지 관련 예외 처리
    PUPPY_NOT_FOUND(HttpStatus.NOT_FOUND, "PUPPY404", "해당 ID를 가진 강아지를 찾을 수 없습니다."),
    UNAUTHORIZED_PUPPY_ACCESS(HttpStatus.UNAUTHORIZED, "PUPPY401", "강아지에 대한 접근 권한이 없습니다."),

    // 이미지 관련 예외 처리
    EMPTY_FILE_LIST(HttpStatus.BAD_REQUEST, "IMAGE4001", "요청 파일 목록이 비어있습니다."),
    INVALID_FILE_UPLOAD(HttpStatus.BAD_REQUEST, "IMAGE4002", "파일이 비어있거나 유효하지 않습니다."),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AWS500", "파일 업로드에 실패했습니다")

    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build()
                ;
    }
}
