package umc.puppymode.service.FcmService;

import umc.puppymode.domain.User;

import java.util.List;

public interface FcmQueryService {

    List<String> getAllFcmTokensWithNotification();

    User getUserByFcmToken(String token);
}
