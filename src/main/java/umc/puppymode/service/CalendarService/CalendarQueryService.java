package umc.puppymode.service.CalendarService;

import umc.puppymode.web.dto.CalendarDTO.CalendarListResponseDTO;
import umc.puppymode.web.dto.CalendarDTO.CalendarResponseDTO.*;

import java.util.List;

public interface CalendarQueryService {
    List<CalendarListResponseDTO> getCalendar(Long userId, String month);
    List<CalendarDetailDTO> getCalendarDetail(Long userId, Long drinkHistoryId);
}
