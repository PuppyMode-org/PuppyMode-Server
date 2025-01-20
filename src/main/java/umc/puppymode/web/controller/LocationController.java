package umc.puppymode.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import umc.puppymode.apiPayload.ApiResponse;
import umc.puppymode.web.dto.LocationRequestDTO;
import umc.puppymode.web.dto.LocationResponseDTO;

@RestController
@RequestMapping("/locations")
public class LocationController {

    @PostMapping
    @Operation(summary = "사용자 현재 위치 조회 API", description = "사용자의 현재 위치의 위도와 경도를 반환합니다.")
    public ApiResponse<LocationResponseDTO> getCurrentLocation(@RequestBody @Valid LocationRequestDTO request) {

        // 데이터 반환
        LocationResponseDTO response = new LocationResponseDTO(
                request.getLatitude(),
                request.getLongitude()
        );

        return ApiResponse.onSuccess(response, "SUCCESS_GET_CURRENT_LOCATION", "사용자 현재 위치 조회 성공");
    }
}
