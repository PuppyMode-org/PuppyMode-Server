package umc.puppymode.service.FcmService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.puppymode.apiPayload.exception.GeneralException;
import umc.puppymode.domain.Token;
import umc.puppymode.domain.User;
import umc.puppymode.repository.TokenRepository;
import umc.puppymode.domain.enums.TokenType;
import umc.puppymode.apiPayload.code.status.ErrorStatus;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FcmQueryServiceImpl implements FcmQueryService {

    private final TokenRepository tokenRepository;

    // 모든 FCM 토큰 조회
    @Override
    public List<String> getAllFcmTokensWithNotification() {
        return tokenRepository.findAllFcmTokensWithNotification();
    }

    // FCM 토큰으로 사용자 정보 조회
    @Override
    public User getUserByFcmToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new GeneralException(ErrorStatus.FIREBASE_MISSING_TOKEN);
        }

        Token tokenEntity = tokenRepository.findByTokenAndTokenType(token, TokenType.FCM)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        return tokenEntity.getUser();
    }
}
