package umc.puppymode.service.UserCollectionService;

import umc.puppymode.domain.HangoverItem;
import umc.puppymode.domain.PuppyItem;
import umc.puppymode.domain.User;

import java.util.List;

public interface UserCollectionCommandService {

    // 경험 숙취 증상 목록에 따른 해당 유저의 컬렉션 업데이트
    void updateCollection(User user, List<HangoverItem> hangoverItems);

    // 보상 아이템 획득
    void getRewardItem(PuppyItem puppyItem, Long userId);
}
