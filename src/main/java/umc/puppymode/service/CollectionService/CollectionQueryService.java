package umc.puppymode.service.CollectionService;

import umc.puppymode.web.dto.CollectionDTO.CollectionResDTO;

public interface CollectionQueryService {

    // 사용자의 컬렉션 조회
    CollectionResDTO.CollectionListViewDTO getCollections(Long userId);
}
