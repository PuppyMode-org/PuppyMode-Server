package umc.puppymode.service.CollectionService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.puppymode.apiPayload.code.status.ErrorStatus;
import umc.puppymode.apiPayload.exception.GeneralException;
import umc.puppymode.converter.CollectionConverter;
import umc.puppymode.domain.Collection;
import umc.puppymode.domain.User;
import umc.puppymode.repository.CollectionRepository;
import umc.puppymode.repository.UserRepository;
import umc.puppymode.web.dto.CollectionDTO.CollectionResDTO;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CollectionQueryServiceImpl implements CollectionQueryService {

    private final CollectionRepository collectionRepository;
    private final UserRepository userRepository;

    @Override
    // 사용자의 컬렉션 조회
    public CollectionResDTO.CollectionListViewDTO getCollections(Long userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        List<Collection> collections = collectionRepository.findByUserOrderByIsCompleted(user);

        return CollectionConverter.toCollectionListViewDTO(collections);
    }
}
