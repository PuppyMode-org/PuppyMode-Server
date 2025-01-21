package umc.puppymode.service.AwsS3Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import umc.puppymode.apiPayload.code.status.ErrorStatus;
import umc.puppymode.apiPayload.exception.GeneralException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AwsS3ServiceImpl implements AwsS3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;

    @Override
    // S3 버킷에 이미지를 업로드
    public List<String> uploadImages(List<MultipartFile> multipartFiles) {

        // S3 업로드 완료한 이미지들의 접근 가능한 url 담을 리스트
        List<String> imageUrls = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {

            if (multipartFile == null || multipartFile.isEmpty()) {
                throw new GeneralException(ErrorStatus.INVALID_FILE_UPLOAD);
            }
            // 고유한 UUID 사용하여 새로운 파일 이름 생성 (동일 파일명일 경우 대비)
            String s3FileName = createUniqueName(multipartFile.getOriginalFilename());

            try {
                ObjectMetadata objectMetadata = new ObjectMetadata();
                objectMetadata.setContentType(multipartFile.getContentType());
                objectMetadata.setContentLength(multipartFile.getSize());

                amazonS3.putObject(new PutObjectRequest(bucket, s3FileName, multipartFile.getInputStream(), objectMetadata));

            } catch (IOException e) {
                throw new GeneralException(ErrorStatus.FILE_UPLOAD_FAILED);
            }
            imageUrls.add("https://d1le4wcgenmery.cloudfront.net/" + s3FileName);
        }
        return imageUrls;
    }

    @Override
    // S3 버킷에서 이미지 삭제
    public void deleteImage(String imageUrl) {

        // signed url을 포함한 전체 url에서 이미지 이름만 분리
        String imageName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, imageName);
        try {
            amazonS3.deleteObject(deleteObjectRequest);
        } catch (AmazonS3Exception e) {
            if (e.getStatusCode() == 404) {
                throw new GeneralException(ErrorStatus.IMAGE_NOT_FOUND);
            }
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.FILE_DELETE_FAILED);
        }
    }

    // UUID 사용하여 새로운 파일 이름 생성
    private String createUniqueName(String fileName) {
        return UUID.randomUUID() + fileName;
    }
}
