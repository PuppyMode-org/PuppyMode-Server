package umc.puppymode.service.UserCollectionService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.puppymode.apiPayload.code.status.ErrorStatus;
import umc.puppymode.apiPayload.exception.GeneralException;
import umc.puppymode.domain.HangoverItem;
import umc.puppymode.domain.Puppy;
import umc.puppymode.domain.PuppyItem;
import umc.puppymode.domain.User;
import umc.puppymode.domain.mapping.PuppyCustomization;
import umc.puppymode.domain.mapping.UserCollection;
import umc.puppymode.repository.PuppyCustomizationRepository;
import umc.puppymode.repository.PuppyRepository;
import umc.puppymode.repository.UserCollectionRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserCollectionCommandServiceImpl implements UserCollectionCommandService {

    private final UserCollectionRepository userCollectionRepository;
    private final PuppyRepository puppyRepository;
    private final PuppyCustomizationRepository puppyCustomizationRepository;

    @Override
    // 경험 숙취 증상 목록에 따른 해당 유저의 컬렉션 업데이트
    public void updateCollection(User user, List<HangoverItem> hangoverItems) {

        List<UserCollection> userCollections = userCollectionRepository.findByUser(user);

        // HangoverItem을 키 값으로 하는 Map 생성
        Map<HangoverItem, List<UserCollection>> userCollectionMap = userCollections.stream()
                .collect(Collectors.groupingBy(u -> u.getCollection().getHangoverItem()));

        // 숙취 증상에 해당하는 userCollection 목록에 대하여 달성 횟수 업데이트
        for (HangoverItem hangoverItem : hangoverItems) {
            List<UserCollection> collections = userCollectionMap.get(hangoverItem);
            if (collections != null) {
                for (UserCollection userCollection : collections) {
                    // 이미 완료된 컬렉션이 아닌 경우에만 수행
                    if (!userCollection.isCompleted()) {
                        if (userCollection.updateCurrentNumAndReturnIsCompleted()) {
                            // 조건 충족(요구 횟수 만족) 시 보상 아이템 획득
                            userCollection.setCompleted(true);
                            getRewardItem(userCollection.getCollection().getPuppyItem(), user.getUserId());
                        }
                    }
                }
            }
        }
    }

    @Override
    // 보상 아이템 획득
    public void getRewardItem(PuppyItem puppyItem, Long userId) {

        Puppy puppy = puppyRepository.findByUserId(userId).orElseThrow(() -> new GeneralException(ErrorStatus.NO_USERS_PUPPY));

        // puppyCustomization 객체 생성 (강아지가 아이템을 획득한 것을 의미)
        PuppyCustomization puppyCustomization = PuppyCustomization.builder()
                .puppy(puppy)
                .puppyItem(puppyItem)
                .puppyItemCategory(puppyItem.getCategory())
                .isEquipped(false)
                .build();

        puppyCustomizationRepository.save(puppyCustomization);
    }
}
