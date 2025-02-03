package umc.puppymode.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import umc.puppymode.apiPayload.ApiResponse;
import umc.puppymode.service.AuthService.UserAuthService;
import umc.puppymode.service.UserCollectionService.UserCollectionQueryService;
import umc.puppymode.web.dto.UserCollectionDTO.UserCollectionResDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/collections")
public class UserCollectionController {

    private final UserCollectionQueryService userCollectionQueryService;
    private final UserAuthService userAuthService;

    @GetMapping
    @Operation(summary = "컬렉션 조회 API", description = "현재 유저의 컬렉션을 조회하는 API 입니다.")
    public ApiResponse<UserCollectionResDTO.UserCollectionListViewDTO> getUserCollections() {

        Long userId = userAuthService.getCurrentUserId();
        UserCollectionResDTO.UserCollectionListViewDTO userCollectionListViewDTO = userCollectionQueryService.getUserCollections(userId);
        return ApiResponse.onSuccess(userCollectionListViewDTO);
    }
}
