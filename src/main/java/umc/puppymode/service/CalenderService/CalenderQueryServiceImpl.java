package umc.puppymode.service.CalenderService;

import org.springframework.stereotype.Service;
import umc.puppymode.domain.DrinkHistory;
import umc.puppymode.repository.DrinkHistoryItemRepository;
import umc.puppymode.repository.DrinkHistoryRepository;
import umc.puppymode.repository.FeedRepository;
import umc.puppymode.web.dto.CalenderDTO.CalenderListResponseDTO;
import umc.puppymode.web.dto.CalenderDTO.CalenderResponseDTO.*;
import umc.puppymode.web.dto.CalenderDTO.DrinkHistoryItemDTO;
import umc.puppymode.web.dto.CalenderDTO.FeedDTO;

import java.util.List;

@Service
public class CalenderQueryServiceImpl implements CalenderQueryService{
    private final DrinkHistoryRepository drinkHistoryRepository;
    private final DrinkHistoryItemRepository drinkHistoryItemRepository;
    private final FeedRepository feedRepository;

    public CalenderQueryServiceImpl(DrinkHistoryRepository drinkHistoryRepository,
                                    DrinkHistoryItemRepository drinkHistoryItemRepository,
                                    FeedRepository feedRepository) {
        this.drinkHistoryRepository = drinkHistoryRepository;
        this.drinkHistoryItemRepository = drinkHistoryItemRepository;
        this.feedRepository = feedRepository;
    }


    @Override
    public List<CalenderListResponseDTO> getCalender(Long userId, String month) {
        return drinkHistoryRepository.findDrinkDatesByUserAndMonth(userId, month);
    }

    @Override
    public List<CalenderDetailDTO> getCalenderDetail(Long userId, Long drinkHistoryId) {
        DrinkHistory drinkHistory = drinkHistoryRepository.findDrinkHistoryDetail(userId, drinkHistoryId);
        if (drinkHistory == null) {
            throw new IllegalArgumentException("해당 기록을 찾을 수 없습니다.");
        }

        List<DrinkHistoryItemDTO> drinkItems = drinkHistoryItemRepository.findDrinkItemsByHistoryId(drinkHistoryId);
        FeedDTO feed = feedRepository.findFeedByHistoryId(drinkHistoryId);

        return List.of(
                CalenderDetailDTO.builder()
                        .drinkHistoryId(drinkHistory.getDrinkHistoryId())
                        .drinkDate(drinkHistory.getDrinkDate().toString())
                        .drinkAmount(drinkHistory.getDrinkAmount())
                        .drinkItems(drinkItems)
                        .feed(feed)
                        .build()
        );
    }}
