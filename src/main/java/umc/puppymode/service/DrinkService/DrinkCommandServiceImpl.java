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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    @Transactional
    public DrinksRecordResponseDTO postDrinksRecord(Long userId, DrinkRecordDTO drinkRecordDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        // 기록 추가
        DrinkHistory drinkHistory = new DrinkHistory();
        drinkHistory.setUser(user);
        drinkHistory.setDrinkDate(drinkRecordDTO.getDrinkDate());
        drinkHistory.setDrinkAmount(drinkRecordDTO.getAlcoholTolerance().stream()
                .map(tolerance -> convertToMl(tolerance.getDrinkItemId(), tolerance.getUnit(), tolerance.getValue()))
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

        // 안전 주량 및 치사량 계산
        List<DrinkHistoryItem> historyItems = drinkHistoryItemRepository.findByHistory_User_UserId(userId);
        Map<Long, Float> safetyLevels = new HashMap<>();
        Map<Long, Float> maxCapacities = new HashMap<>();

        for (DrinkHistoryItem item : historyItems) {
            Long drinkItemId = item.getItem().getItemId();
            float amountMl = convertToMl(drinkItemId, item.getUnit(), item.getValue());
            // 숙취를 처음 느낀 기록을 기준으로 안전 주량 설정
            if (!drinkHistory.getHangovers().isEmpty()) {
                safetyLevels.put(drinkItemId, Math.min(safetyLevels.getOrDefault(drinkItemId, Float.MAX_VALUE), amountMl));
            }
            // 가장 많이 마신 기록을 기준으로 치사량 설정
            maxCapacities.put(drinkItemId, Math.max(maxCapacities.getOrDefault(drinkItemId, 0f), amountMl));
        }

        // 계산된 안전 주량과 치사량을 DrinkHistoryItem에 설정 후 저장
        for (DrinkHistoryItem item : historyItems) {
            Long drinkItemId = item.getItem().getItemId();

            // 안전 주량과 치사량을 해당 항목에 설정
            if (safetyLevels.containsKey(drinkItemId)) {
                item.setSafetyValue(safetyLevels.get(drinkItemId));
            }
            if (maxCapacities.containsKey(drinkItemId)) {
                item.setMaxValue(maxCapacities.get(drinkItemId));
            }

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
        recordResponseDTO.setPuppyLevel(puppy.getPuppyLevel().getPuppyLevel());
        recordResponseDTO.setPuppyLevelName(puppy.getPuppyLevel().getLevelName());
        recordResponseDTO.setPuppyPercent(puppy.getPuppyExp());

        // 획득 먹이 10개 중 랜덤
        Random RANDOM = new Random();
        FeedingItem[] types = FeedingItem.values();
        FeedingItem type = types[RANDOM.nextInt(types.length)];

        Feed feed = new Feed();
        feed.setDrinkHistory(drinkHistory);
        feed.setFeedingType(type.getDescription());
        feed.setFeedImageUrl(type.getImageUrl());

        recordResponseDTO.setFeedType(type.getDescription());
        recordResponseDTO.setFeedImageUrl(type.getImageUrl());

        feedRepository.save(feed);

        return recordResponseDTO;
    }
    private float convertToMl(Long drinkItemId, String unit, float value) {
        if (drinkItemId == 1) { // 소주
            switch (unit) {
                case "잔": return value * 50;
                case "병": return value * 360;
            }
        } else if (drinkItemId == 2) { // 맥주
            switch (unit) {
                case "잔": return value * 300;
                case "병": return value * 500;
            }
        }
        return value;
    }

    @Override
    public FeedResponseDTO postFeed(Long userId) {
        Puppy puppy = puppyRepository.findByUserId(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.PUPPY_NOT_FOUND));

        // 현재 레벨의 최소 ~ 최대 경험치 차이를 기준으로 5% 증가
        Integer fivePercentExp = (int) ((puppy.getPuppyLevel().getLevelMaxExp() - puppy.getPuppyLevel().getLevelMinExp()) * 0.05);
        puppy.updatePuppyExp(fivePercentExp);

        // 현재 경험치 비율 계산 (0~100%)
        int puppyPercent = (int) (((double) (puppy.getPuppyExp() - puppy.getPuppyLevel().getLevelMinExp()) /
                (puppy.getPuppyLevel().getLevelMaxExp() - puppy.getPuppyLevel().getLevelMinExp())) * 100);

        // DTO 생성 및 반환
        return FeedResponseDTO.builder()
                .puppyName(puppy.getPuppyName())
                .puppyExp(puppy.getPuppyExp())
                .puppyLevel(puppy.getPuppyLevel().getPuppyLevel())
                .puppyLevelName(puppy.getPuppyLevel().getLevelName())
                .puppyPercent(puppyPercent)
                .build();
    }
}
