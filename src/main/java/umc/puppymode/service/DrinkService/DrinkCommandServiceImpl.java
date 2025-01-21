package umc.puppymode.service.DrinkService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.puppymode.apiPayload.code.status.ErrorStatus;
import umc.puppymode.apiPayload.exception.GeneralException;
import umc.puppymode.domain.*;
import umc.puppymode.domain.enums.FeedingItem;
import umc.puppymode.domain.enums.PuppyType;
import umc.puppymode.repository.*;
import umc.puppymode.web.dto.DrinkRequestDTO.*;
import umc.puppymode.web.dto.DrinkResponseDTO.*;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class DrinkCommandServiceImpl implements DrinkCommandService {
    private final UserRepository userRepository;
    private final DrinkItemRepository drinkItemRepository;
    private final DrinkHistoryRepository drinkHistoryRepository;
    private final DrinkHistoryItemRepository drinkHistoryItemRepository;
    private final HangoverRepository hangoverRepository;
    private final PuppyRepository puppyRepository;
    private final FeedRepository feedRepository;

    @Override
    public DrinksRecordResponseDTO postDrinksRecord(Long userId, DrinkRecordDTO drinkRecordDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        // 기록 추가
        DrinkHistory drinkHistory = new DrinkHistory();
        drinkHistory.setUser(user);
        drinkHistory.setDrinkDate(drinkRecordDTO.getDrinkDate());
        // 마신 양 계산해서 합(잔, 병 합치기 필요)
        drinkHistory.setDrinkAmount(drinkRecordDTO.getAlcoholTolerance().stream()
                .map(AlcoholTolerance::getValue)
                .reduce(0.0f, Float::sum));
        drinkHistoryRepository.save(drinkHistory);

        // 숙취 목록 추가
        List<HangoverItem> hangoverItems = hangoverRepository.findAllById(drinkRecordDTO.getHangoverOptions());
        if (!hangoverItems.isEmpty()) {
            drinkHistory.setHangovers(hangoverItems);
        }

        // 주종 별 기록 추가
        for (AlcoholTolerance tolerance : drinkRecordDTO.getAlcoholTolerance()) {
            DrinkHistoryItem item = new DrinkHistoryItem();
            DrinkItem drinkItem = drinkItemRepository.findById(tolerance.getDrinkItemId())
                    .orElseThrow(() -> new GeneralException(ErrorStatus.DRINK_ITEM_NOT_FOUND));;
            item.setHistory(drinkHistory);
            item.setItem(drinkItem);
            item.setUnit(tolerance.getUnit());
            item.setValue(tolerance.getValue());
            drinkHistoryItemRepository.save(item);
        }

        DrinksRecordResponseDTO recordResponseDTO = new DrinksRecordResponseDTO();
        // 숙취 개수에 따라 message 다르게
        if (drinkRecordDTO.getHangoverOptions().toArray().length == 0) {
            recordResponseDTO.setMessage("주량을 잘 조절해서 마셨네요!");
        } else if (drinkRecordDTO.getHangoverOptions().toArray().length <= 2) {
            recordResponseDTO.setMessage("다음에는 꼭 주량을 지키도록 노력해 주세요!");
        } else {
            recordResponseDTO.setMessage("건강을 생각해서 다음에는 꼭 주량을 지켜주세요!");
        }

        Puppy puppy = puppyRepository.findByUserId(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));;
        recordResponseDTO.setPuppyLevel(Long.valueOf(puppy.getPuppyLevel().getPuppyLevel()));
        recordResponseDTO.setPuppyName(puppy.getPuppyName());
        recordResponseDTO.setPuppyPercent(puppy.getPuppyExp());

        // 획득 먹이 10개 중 랜덤
        Random RANDOM = new Random();
        FeedingItem[] types = FeedingItem.values();
        FeedingItem type = types[RANDOM.nextInt(types.length)];

        Feed feed = new Feed();
        feed.setDrinkHistory(drinkHistory);
        feed.setPuppy(puppy);
        feed.setFeedingType(type.getDescription());
        feed.setFeedImageUrl(type.getImageUrl());

        recordResponseDTO.setFeedType(type.getDescription());
        recordResponseDTO.setFeedImageUrl(type.getImageUrl());

        feedRepository.save(feed);

        return recordResponseDTO;
    }
}
