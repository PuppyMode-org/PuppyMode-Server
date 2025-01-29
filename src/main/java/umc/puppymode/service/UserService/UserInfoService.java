package umc.puppymode.service.UserService;

import umc.puppymode.web.dto.UserInfoResponseDTO;

public interface UserInfoService {
    UserInfoResponseDTO getUserInfo(Long userId);
}
