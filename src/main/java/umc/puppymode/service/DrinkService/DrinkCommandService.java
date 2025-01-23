package umc.puppymode.service.DrinkService;

import umc.puppymode.web.dto.DrinkRequestDTO.*;
import umc.puppymode.web.dto.DrinkResponseDTO.*;

public interface DrinkCommandService {
    DrinksRecordResponseDTO postDrinksRecord(Long userId, DrinkRecordDTO drinkRecordDTO);
}
