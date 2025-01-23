package umc.puppymode.apiPayload.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import umc.puppymode.apiPayload.code.BaseCode;
import umc.puppymode.apiPayload.code.ReasonDTO;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {

    _OK(HttpStatus.OK, "COMMON200", "성공입니다."),

    //술 약속
    APPOINTMENT_DELETE_SUCCESS(HttpStatus.OK, "SUCCESS_DELETE_APPOINTMENT", "술 약속 삭제 성공"),
    APPOINTMENT_GET_SUCCESS(HttpStatus.OK, "SUCCESS_GET_APPOINTMENT", "술 약속 조회 성공"),
    APPOINTMENT_POST_SUCCESS(HttpStatus.OK, "SUCCESS_POST_APPOINTMENT", "술 약속 설정 성공"),
    APPOINTMENT_STATUS_GET_SUCCESS(HttpStatus.OK, "SUCCESS_GET_APPOINTMENT_STATUS", "술 약속 및 음주 상태 조회 성공"),
    APPOINTMENT_RESCHEDULED_PATCH_SUCCESS(HttpStatus.OK, "SUCCESS_PUT_APPOINTMENT_RESCHEDULED", "술 약속 미루기 성공");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDTO getReason() {
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .build();
    }

    @Override
    public ReasonDTO getReasonHttpStatus() {
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .httpStatus(httpStatus)
                .build()
                ;
    }
}
