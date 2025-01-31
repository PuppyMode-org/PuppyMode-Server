package umc.puppymode.service.UserCollectionService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.puppymode.apiPayload.code.status.ErrorStatus;
import umc.puppymode.apiPayload.exception.GeneralException;
import umc.puppymode.converter.UserCollectionConverter;
import umc.puppymode.domain.User;
import umc.puppymode.domain.mapping.UserCollection;
import umc.puppymode.repository.UserCollectionRepository;
import umc.puppymode.repository.UserRepository;
import umc.puppymode.web.dto.UserCollectionDTO.UserCollectionResDTO;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserCollectionQueryServiceImpl implements UserCollectionQueryService {

    private final UserRepository userRepository;
    private final UserCollectionRepository userCollectionRepository;

    @Override
    // 사용자의 컬렉션 조회
    public UserCollectionResDTO.UserCollectionListViewDTO getUserCollections(Long userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        List<UserCollection> userCollections = userCollectionRepository.findByUserOrderByIsCompleted(user);
        return UserCollectionConverter.toUserCollectionListViewDTO(userCollections);
    }
}
