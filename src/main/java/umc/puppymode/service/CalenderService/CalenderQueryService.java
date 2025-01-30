package umc.puppymode.service.CalenderService;

import umc.puppymode.web.dto.CalenderDTO.CalenderListResponseDTO;
import umc.puppymode.web.dto.CalenderDTO.CalenderResponseDTO.*;

import java.util.List;

public interface CalenderQueryService {
    List<CalenderListResponseDTO> getCalender(Long userId, String month);
    List<CalenderDetailDTO> getCalenderDetail(Long userId, Long drinkHistoryId);
}
