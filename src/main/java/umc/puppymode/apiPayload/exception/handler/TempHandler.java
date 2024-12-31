package umc.puppymode.apiPayload.exception.handler;

import umc.puppymode.apiPayload.code.BaseErrorCode;
import umc.puppymode.apiPayload.exception.GeneralException;

public class TempHandler extends GeneralException {

    public TempHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
