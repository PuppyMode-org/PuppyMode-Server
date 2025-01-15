package umc.puppymode.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import umc.puppymode.apiPayload.ApiResponse;
import umc.puppymode.service.DrinkService.DrinkQueryService;
import umc.puppymode.web.dto.DrinkResponseDTO;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class DrinkController {

    private final DrinkQueryService drinkQueryService;

    @GetMapping("hangover")
    @Operation(summary = "숙취 목록 조회 API", description = "숙취 목록을 조회하는 API입니다.")
    public ResponseEntity<ApiResponse<List<DrinkResponseDTO.HangoverResponseDTO>>> getHangovers() {
        List<DrinkResponseDTO.HangoverResponseDTO> responseDTO = drinkQueryService.getAllHangovers();
        return ResponseEntity.ok(ApiResponse.onSuccess(responseDTO));
    }
}
