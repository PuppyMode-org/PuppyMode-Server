package umc.puppymode.service.CalendarService;

import org.springframework.stereotype.Service;
import umc.puppymode.domain.DrinkHistory;
import umc.puppymode.domain.DrinkHistoryItem;
import umc.puppymode.domain.DrinkingAppointment;
import umc.puppymode.domain.enums.AppointmentStatus;
import umc.puppymode.repository.DrinkHistoryItemRepository;
import umc.puppymode.repository.DrinkHistoryRepository;
import umc.puppymode.repository.DrinkingAppointmentRepository;
import umc.puppymode.repository.FeedRepository;
import umc.puppymode.web.dto.CalendarDTO.CalendarListResponseDTO;
import umc.puppymode.web.dto.CalendarDTO.CalendarResponseDTO.*;
import umc.puppymode.web.dto.CalendarDTO.DrinkHistoryItemDTO;
import umc.puppymode.web.dto.CalendarDTO.FeedDTO;
import umc.puppymode.web.dto.DrinkResponseDTO.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CalendarQueryServiceImpl implements CalendarQueryService{
    private final DrinkHistoryRepository drinkHistoryRepository;
    private final DrinkHistoryItemRepository drinkHistoryItemRepository;
    private final FeedRepository feedRepository;
    private final DrinkingAppointmentRepository drinkingAppointmentRepository;

    public CalendarQueryServiceImpl(DrinkHistoryRepository drinkHistoryRepository,
                                    DrinkHistoryItemRepository drinkHistoryItemRepository,
                                    FeedRepository feedRepository, DrinkingAppointmentRepository drinkingAppointmentRepository) {
        this.drinkHistoryRepository = drinkHistoryRepository;
        this.drinkHistoryItemRepository = drinkHistoryItemRepository;
        this.feedRepository = feedRepository;
        this.drinkingAppointmentRepository = drinkingAppointmentRepository;
    }


    @Override
    public List<CalendarListResponseDTO> getCalendar(Long userId, String month) {
        List<DrinkHistory> drinkHistories = drinkHistoryRepository.findDrinkHistoriesByUserAndMonth(userId, month);
        List<DrinkingAppointment> appointments = drinkingAppointmentRepository.findAppointmentsByUserAndMonth(userId, month);

        Map<LocalDate, CalendarListResponseDTO> drinkStatusMap = new HashMap<>();
        YearMonth targetMonth = YearMonth.parse(month);
        int lastDay = targetMonth.lengthOfMonth();

        for (DrinkHistory history : drinkHistories) {
            LocalDate date = history.getDrinkDate();

            List<DrinkHistoryItem> drinkHistoryItems =
                    Optional.ofNullable(drinkHistoryItemRepository.findByHistory_DrinkHistoryId(history.getDrinkHistoryId()))
                            .orElse(Collections.emptyList());

            float totalDrinkAmount = history.getDrinkAmount();
            float safetyValue = drinkHistoryItems.stream()
                    .map(item -> Optional.ofNullable(item.getSafetyValue()).orElse(0f))
                    .reduce(0f, Float::sum);
            float maxValue = drinkHistoryItems.stream()
                    .map(item -> Optional.ofNullable(item.getMaxValue()).orElse(0f))
                    .reduce(0f, Float::sum);
            boolean hasHangover = Optional.ofNullable(history.getHangovers()).map(list -> !list.isEmpty()).orElse(false);

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

            Optional<DrinkingAppointment> appointment = appointments.stream()
                    .filter(app -> app.getDateTime().toLocalDate().equals(date))
                    .findFirst();

            String appointmentTime = null;
            Long appointmentId = null;
            if (appointment.isPresent()) {
                appointmentTime = calculateAppointmentTime(appointment.get());
                appointmentId = appointment.get().getAppointmentId();
            }

            drinkStatusMap.put(date, CalendarListResponseDTO.builder()
                    .drinkDate(date)
                    .status(status)
                    .drinkHistoryId(history.getDrinkHistoryId())
                    .historyStatus(historyStatus)
                    .appointmentId(appointmentId)
                    .appointmentTime(appointmentTime)
                    .build());
        }

        for (DrinkingAppointment appointment : appointments) {
            LocalDate date = appointment.getDateTime().toLocalDate();

            drinkStatusMap.putIfAbsent(date, CalendarListResponseDTO.builder()
                    .drinkDate(date)
                    .status("건강 포기한 날")
                    .build());

            String appointmentTime = calculateAppointmentTime(appointment);
            Long appointmentId = appointment.getAppointmentId();

            CalendarListResponseDTO existingDTO = drinkStatusMap.get(date);
            if (existingDTO != null) {
                existingDTO.setAppointmentTime(appointmentTime);
                existingDTO.setAppointmentId(appointmentId);
            }
        }

        for (int day = 1; day <= lastDay; day++) {
            LocalDate date = LocalDate.of(targetMonth.getYear(), targetMonth.getMonth(), day);
            if (!drinkStatusMap.containsKey(date)) {
                drinkStatusMap.put(date, CalendarListResponseDTO.builder()
                        .drinkDate(date)
                        .status("건강 챙긴 날")
                        .build());
            }
        }

        return drinkStatusMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }


    // appointmentTime 계산 로직
    private String calculateAppointmentTime(DrinkingAppointment appointment) {
        LocalDateTime startTime = appointment.getDrinkingStartTime();
        LocalDateTime endTime = appointment.getUpdatedAt();

        if (appointment.getStatus() == AppointmentStatus.COMPLETED && endTime != null) {
            String startFormatted = String.format("%02d:%02d", startTime.getHour(), startTime.getMinute());
            Duration duration = Duration.between(startTime, endTime);
            long hours = duration.toHours();
            long minutes = duration.toMinutes() % 60;
            String endFormatted = String.format("%02d:%02d", endTime.getHour(), endTime.getMinute());

            return String.format("%s ~ %s (%dh %dm)", startFormatted, endFormatted, hours, minutes);
        } else {
            // 완료되지 않은 약속의 경우 시작 시간만 표시
            String startFormatted = String.format("%02d:%02d", appointment.getDateTime().getHour(), appointment.getDateTime().getMinute());
            return startFormatted + " ~";
        }
    }

    @Override
    public List<CalendarDetailDTO> getCalendarDetail(Long userId, Long drinkHistoryId) {
        DrinkHistory drinkHistory = drinkHistoryRepository.findDrinkHistoryDetail(userId, drinkHistoryId);
        if (drinkHistory == null) {
            throw new IllegalArgumentException("해당 기록을 찾을 수 없습니다.");
        }

        List<DrinkHistoryItemDTO> drinkItems = drinkHistoryItemRepository.findDrinkItemsByHistoryId(drinkHistoryId);
        FeedDTO feed = feedRepository.findFeedByHistoryId(drinkHistoryId);
        List<HangoverResponseDTO> hangoverItems = drinkHistory.getHangovers().stream()
                .map(hangover -> new HangoverResponseDTO(
                        hangover.getHangoverId(),
                        hangover.getHangoverName(),
                        hangover.getImageUrl()
                ))
                .collect(Collectors.toList());

        float totalSafetyValue = 0f;
        float totalMaxValue = 0f;

        for (DrinkHistoryItemDTO item : drinkItems) {
            totalSafetyValue += item.getSafetyValue();
            totalMaxValue += item.getMaxValue();
        }

        return List.of(
                CalendarDetailDTO.builder()
                        .drinkHistoryId(drinkHistory.getDrinkHistoryId())
                        .drinkDate(drinkHistory.getDrinkDate().toString())
                        .drinkAmount(drinkHistory.getDrinkAmount())
                        .totalSafetyValue(totalSafetyValue)
                        .totalMaxValue(totalMaxValue)
                        .drinkItems(drinkItems)
                        .feed(feed)
                        .hangoverItems(hangoverItems)
                        .build()
        );
    }
}
