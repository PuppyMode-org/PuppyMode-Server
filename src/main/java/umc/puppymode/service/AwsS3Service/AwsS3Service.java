package umc.puppymode.service.AwsS3Service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AwsS3Service {

    // S3 버킷에 이미지를 업로드
    List<String> uploadImages(List<MultipartFile> multipartFiles);
    // S3 버킷에서 이미지 삭제
    void deleteImage(String imageUrl);
}
