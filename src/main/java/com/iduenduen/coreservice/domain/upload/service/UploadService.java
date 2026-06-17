package com.iduenduen.coreservice.domain.upload.service;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.iduenduen.coreservice.common.exception.GeneralException;
import com.iduenduen.coreservice.common.status.ErrorStatus;
import com.iduenduen.coreservice.domain.upload.dto.PresignedUrlRequest;
import com.iduenduen.coreservice.domain.upload.dto.PresignedUrlResponse;

import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
public class UploadService {

    private static final int EXPIRES_IN_SECONDS = 300;
    private static final Set<String> CERT_ALLOWED_TYPES = Set.of("application/pdf");
    private static final Set<String> PROFILE_ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    private final S3Presigner s3Presigner;
    private final String bucket;

    public UploadService(S3Presigner s3Presigner, @Value("${cloud.aws.s3.bucket:}") String bucket) {
        this.s3Presigner = s3Presigner;
        this.bucket = bucket;
    }

    public PresignedUrlResponse generatePresignedUrl(PresignedUrlRequest request) {
        validateRequest(request);

        String key = request.getFileType() + "/" + UUID.randomUUID() + "_" + request.getFileName();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(request.getContentType())
                .build();

        PresignedPutObjectRequest presigned = s3Presigner.presignPutObject(
                PutObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofSeconds(EXPIRES_IN_SECONDS))
                        .putObjectRequest(putObjectRequest)
                        .build());

        String fileUrl = "https://" + bucket + ".s3.ap-northeast-2.amazonaws.com/" + key;

        return PresignedUrlResponse.builder()
                .uploadUrl(presigned.url().toString())
                .fileUrl(fileUrl)
                .expiresIn(EXPIRES_IN_SECONDS)
                .build();
    }

    private void validateRequest(PresignedUrlRequest request) {
        if (request.getFileType() == null || request.getContentType() == null || request.getFileName() == null) {
            throw new GeneralException(ErrorStatus.BAD_REQUEST);
        }
        Set<String> allowed = switch (request.getFileType()) {
            case "cert" -> CERT_ALLOWED_TYPES;
            case "profile" -> PROFILE_ALLOWED_TYPES;
            default -> throw new GeneralException(ErrorStatus.BAD_REQUEST);
        };
        if (!allowed.contains(request.getContentType())) {
            throw new GeneralException(ErrorStatus.INVALID_FILE_TYPE);
        }
    }
}
