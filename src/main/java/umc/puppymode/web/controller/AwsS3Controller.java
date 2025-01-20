package umc.puppymode.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import umc.puppymode.apiPayload.ApiResponse;
import umc.puppymode.apiPayload.code.status.ErrorStatus;
import umc.puppymode.apiPayload.exception.handler.TempHandler;
import umc.puppymode.service.AwsS3Service.AwsS3Service;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/images")
public class AwsS3Controller {

    private final AwsS3Service awsS3Service;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "AWS S3 이미지 업로드 API", description = "S3 버킷에 이미지를 업로드합니다. 반환값은 접근 가능한 이미지의 url입니다.")
    public ApiResponse<List<String>> uploadImage(@RequestPart(value = "uploadFiles", required = false) List<MultipartFile> multipartFiles) {

        if (multipartFiles == null || multipartFiles.isEmpty()) {
            throw new TempHandler(ErrorStatus.EMPTY_FILE_LIST);
        }
        // 유효한 파일이 있는 경우 uploadImages 실행
        return ApiResponse.onSuccess(awsS3Service.uploadImages(multipartFiles));
    }

    @DeleteMapping
    @Operation(summary = "AWS S3 이미지 삭제 API", description = "S3 버킷에서 이미지를 삭제합니다.")
    public ApiResponse<String> deleteImageFromCapsule(@RequestParam String imageUrl) {

        awsS3Service.deleteImage(imageUrl);
        return ApiResponse.onSuccess("이미지가 성공적으로 삭제되었습니다.");
    }
}
