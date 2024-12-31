package umc.puppymode.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import umc.puppymode.apiPayload.ApiResponse;
import umc.puppymode.converter.TempConverter;
import umc.puppymode.service.TempService.TempQueryService;
import umc.puppymode.web.dto.TempResponseDTO;

@RestController
@RequestMapping("/temp")
@RequiredArgsConstructor
public class TempController {

    private final TempQueryService tempQueryService;

    @Operation(summary = "테스트용 임시 API", description = "테스트 목적으로 작성된 임시 API입니다 :)")
    @GetMapping("/test")
    public ApiResponse<TempResponseDTO.TempTestDTO> testAPI() {
        return ApiResponse.onSuccess(TempConverter.toTempTestDTO());
    }

    @Operation(summary = "테스트용 임시 예외 처리 API",
            description = "테스트 목적으로 작성된 예외 처리용 임시 API입니다. `flag` 값을 기반으로 예외 처리가 실행됩니다.")
    @GetMapping("/exception")
    public ApiResponse<TempResponseDTO.TempExceptionDTO> exceptionAPI(@RequestParam Integer flag) {
        tempQueryService.CheckFlag(flag);
        return ApiResponse.onSuccess(TempConverter.toTempExceptionDTO(flag));
    }
}
