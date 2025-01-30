package umc.puppymode.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.puppymode.apiPayload.ApiResponse;
import umc.puppymode.service.CalenderService.CalenderQueryService;
import umc.puppymode.service.UserService.UserAuthService;
import umc.puppymode.web.dto.CalenderDTO.CalenderListResponseDTO;
import umc.puppymode.web.dto.CalenderDTO.CalenderResponseDTO.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/calender")
public class CalenderController {
    private final CalenderQueryService calenderQueryService;
    private final UserAuthService userAuthService;

    @GetMapping
    @Operation(summary = "캘린더 조회 API", description = "캘린더를 조회하는 API입니다.")
    public ResponseEntity<ApiResponse<List<CalenderListResponseDTO>>> getCalender(@RequestParam String month) {
        Long userId = userAuthService.getCurrentUserId();
        List<CalenderListResponseDTO> responseDTO = calenderQueryService.getCalender(userId, month);
        return ResponseEntity.ok(ApiResponse.onSuccess(responseDTO));
    }

    @GetMapping("/daily")
    @Operation(summary = "캘린더 상세 조회 API", description = "캘린더 상세를 조회하는 API입니다.")
    public ResponseEntity<ApiResponse<List<CalenderDetailDTO>>> getCalenderDetail(@RequestParam Long drinkHistoryId) {
        Long userId = userAuthService.getCurrentUserId();
        List<CalenderDetailDTO> responseDTO = calenderQueryService.getCalenderDetail(userId, drinkHistoryId);
        return ResponseEntity.ok(ApiResponse.onSuccess(responseDTO));
    }
}
