package umc.puppymode.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import umc.puppymode.apiPayload.ApiResponse;
import umc.puppymode.service.CollectionService.CollectionQueryService;
import umc.puppymode.service.UserService.UserAuthService;
import umc.puppymode.web.dto.CollectionDTO.CollectionResDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/collections")
public class CollectionController {

    private final CollectionQueryService collectionQueryService;
    private final UserAuthService userAuthService;

    @GetMapping
    @Operation(summary = "컬렉션 조회 API", description = "컬렉션을 조회하는 API 입니다.")
    public ApiResponse<CollectionResDTO.CollectionListViewDTO> getCollections() {

        Long userId = userAuthService.getCurrentUserId();
        CollectionResDTO.CollectionListViewDTO collectionListViewDTO = collectionQueryService.getCollections(userId);
        return ApiResponse.onSuccess(collectionListViewDTO);
    }
}
