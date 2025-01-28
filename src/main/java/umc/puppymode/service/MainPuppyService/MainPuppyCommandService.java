package umc.puppymode.service.MainPuppyService;

import umc.puppymode.web.dto.MainPuppyDTO.MainPuppyResDTO;

public interface MainPuppyCommandService {
    
    // 랜덤으로 강아지를 선택
    MainPuppyResDTO.RandomPuppyViewDTO getRandomPuppy(Long userId);
    // 강아지의 이름을 수정
    String updatePuppyName(Long userId, Long puppyId, String newPuppyName);
    // 강아지 놀아주기 실행
    MainPuppyResDTO.PlayResDTO platWithPuppy(Long userId, Long puppyId);
    // 사용자의 강아지 객체 삭제
    String deletePuppy(Long userId);
}
