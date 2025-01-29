package umc.puppymode.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import umc.puppymode.apiPayload.ApiResponse;
import umc.puppymode.service.UserService.UserAuthService;
import umc.puppymode.service.UserService.UserWithdrawService;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class UserWithdrawController {

    private final UserWithdrawService userWithdrawService;
    private final UserAuthService userAuthService;

    @DeleteMapping("/withdraw")
    @Operation(summary = "회원 탈퇴 API", description = "회원을 탈퇴하는 API입니다.")
    public ResponseEntity<?> withdraw(
            @Parameter(description = "카카오 Access Token")
            @RequestParam(required = false) Optional<String> accessToken
    ) {

        Long userId = userAuthService.getCurrentUserId();

        userWithdrawService.withdraw(userId);

        return ResponseEntity.ok(ApiResponse.onSuccess("USER_WITHDRAW_SUCCESS", "회원 탈퇴 성공", null));
    }
}
