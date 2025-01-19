package umc.puppymode.service.MainPuppyService;

import umc.puppymode.web.dto.MainPuppyDTO.MainPuppyResDTO;

public interface MainPuppyQueryService {

    // 사용자의 강아지 정보를 조회
    MainPuppyResDTO.UserPuppyViewDTO getUserPuppy(Long userId);
}
