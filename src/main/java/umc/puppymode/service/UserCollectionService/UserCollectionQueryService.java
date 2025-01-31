package umc.puppymode.service.UserCollectionService;

import umc.puppymode.web.dto.UserCollectionDTO.UserCollectionResDTO;

public interface UserCollectionQueryService {

    // 사용자의 컬렉션 조회
    UserCollectionResDTO.UserCollectionListViewDTO getUserCollections(Long userId);
}
