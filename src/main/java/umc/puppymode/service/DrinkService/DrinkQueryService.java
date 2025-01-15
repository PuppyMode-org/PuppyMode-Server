package umc.puppymode.service.DrinkService;

import umc.puppymode.web.dto.DrinkResponseDTO.HangoverResponseDTO;

import java.util.List;

public interface DrinkQueryService {
   List<HangoverResponseDTO> getAllHangovers();
}
