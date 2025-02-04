package umc.puppymode.service.CalenderService;

import org.springframework.stereotype.Service;
import umc.puppymode.domain.DrinkHistory;
import umc.puppymode.domain.DrinkHistoryItem;
import umc.puppymode.repository.DrinkHistoryItemRepository;
import umc.puppymode.repository.DrinkHistoryRepository;
import umc.puppymode.repository.DrinkingAppointmentRepository;
import umc.puppymode.repository.FeedRepository;
import umc.puppymode.web.dto.CalenderDTO.CalenderListResponseDTO;
import umc.puppymode.web.dto.CalenderDTO.CalenderResponseDTO.*;
import umc.puppymode.web.dto.CalenderDTO.DrinkHistoryItemDTO;
import umc.puppymode.web.dto.CalenderDTO.FeedDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CalenderQueryServiceImpl implements CalenderQueryService{
    private final DrinkHistoryRepository drinkHistoryRepository;
    private final DrinkHistoryItemRepository drinkHistoryItemRepository;
    private final FeedRepository feedRepository;
    private final DrinkingAppointmentRepository drinkingAppointmentRepository;

    public CalenderQueryServiceImpl(DrinkHistoryRepository drinkHistoryRepository,
                                    DrinkHistoryItemRepository drinkHistoryItemRepository,
                                    FeedRepository feedRepository, DrinkingAppointmentRepository drinkingAppointmentRepository) {
        this.drinkHistoryRepository = drinkHistoryRepository;
        this.drinkHistoryItemRepository = drinkHistoryItemRepository;
        this.feedRepository = feedRepository;
        this.drinkingAppointmentRepository = drinkingAppointmentRepository;
    }


    @Override
    public List<CalenderListResponseDTO> getCalender(Long userId, String month) {
        List<DrinkHistory> drinkHistories = drinkHistoryRepository.findDrinkHistoriesByUserAndMonth(userId, month);
        List<LocalDateTime> appointments = drinkingAppointmentRepository.findAppointmentsByUserAndMonth(userId, month);

        Map<LocalDate, CalenderListResponseDTO> drinkStatusMap = new HashMap<>();

        // 음주 기록이 있는 경우
        for (DrinkHistory history : drinkHistories) {
            List<DrinkHistoryItem> drinkHistoryItems = drinkHistoryItemRepository.findByHistory_DrinkHistoryId(history.getDrinkHistoryId());

            float totalDrinkAmount = history.getDrinkAmount();
            float safetyValue = drinkHistoryItems.stream().map(DrinkHistoryItem::getSafetyValue).reduce(0f, Float::sum);
            float maxValue = drinkHistoryItems.stream().map(DrinkHistoryItem::getMaxValue).reduce(0f, Float::sum);
            boolean hasHangover = !history.getHangovers().isEmpty();

            String status;
            String historyStatus;

            if (totalDrinkAmount <= safetyValue) {
                status = hasHangover ? "술 힘들게 마신 날 " : "술 예쁘게 마신 날";
                historyStatus = hasHangover ? "주량 조절 필요" : "주량 조절 성공";
            } else if (totalDrinkAmount <= maxValue) {
                status = hasHangover ? "강아지가 된 날" : "술 힘들게 마신 날";
                historyStatus = hasHangover ? "주량 조절 실패" : "주량 조절 필요";
            } else {
                status = hasHangover ? "강아지가 된 날" : "술 힘들게 마신 날";
                historyStatus = hasHangover ? "주량 조절 실패" : "주량 조절 필요";
            }

            LocalDate date = history.getDrinkDate();

            // 음주 기록에 대한 정보 업데이트
            CalenderListResponseDTO responseDTO = CalenderListResponseDTO.builder()
                    .drinkHistoryId(history.getDrinkHistoryId())
                    .drinkDate(date)
                    .status(status)
                    .historyStatus(historyStatus)
                    .build();

            drinkStatusMap.put(date, responseDTO);
        }

        // 술 약속만 있는 경우 처리
        for (LocalDateTime appointment : appointments) {
            LocalDate date = appointment.toLocalDate();

            // 술 약속이 있는 경우 상태 업데이트
            drinkStatusMap.putIfAbsent(date, CalenderListResponseDTO.builder()
                    .drinkDate(date)
                    .status("건강 포기한 날")
                    .historyStatus("술 약속 있음")
                    .build());
        }

        // 음주 기록과 술 약속 모두 없는 경우 처리
        for (int day = 1; day <= YearMonth.parse(month).lengthOfMonth(); day++) {
            LocalDate date = LocalDate.of(Integer.parseInt(month.split("-")[0]), Integer.parseInt(month.split("-")[1]), day);

            if (!drinkStatusMap.containsKey(date)) {
                drinkStatusMap.put(date, CalenderListResponseDTO.builder()
                        .drinkDate(date)
                        .status("건강 챙긴 날")
                        .build());
            }
        }

//        return new ArrayList<>(drinkStatusMap.values());

        // 날짜 기준 오름차순 정렬
        return drinkStatusMap.entrySet().stream()
                .sorted(Map.Entry.<LocalDate, CalenderListResponseDTO>comparingByKey())  // 날짜 오름차순 정렬
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
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
    }
}
