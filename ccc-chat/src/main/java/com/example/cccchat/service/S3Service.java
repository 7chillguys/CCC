package com.example.cccchat.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    // ✅ Presigned URL을 통한 파일 업로드
    public String uploadFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";

        // ✅ UUID를 활용한 고유 파일명 생성
        String fileName = UUID.randomUUID() + extension;

        // ✅ 파일 메타데이터 설정
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        // ✅ S3에 파일 업로드 (퍼블릭 접근 없이 저장)
        PutObjectRequest putRequest = new PutObjectRequest(bucketName, fileName, file.getInputStream(), metadata);
        amazonS3.putObject(putRequest);

        // ✅ Presigned URL 생성 (유효 시간 1시간)
        return generatePresignedUrl(fileName);
    }

    // ✅ Presigned URL 생성 메서드
    public String generatePresignedUrl(String fileName) {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 3600 * 1000; // 1시간 후 만료
        expiration.setTime(expTimeMillis);

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, fileName)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(expiration);

        URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
        return url.toString();
    }

    // ✅ S3에서 파일 삭제
    public void deleteFile(String fileUrl) {
        try {
            String fileName = extractFileNameFromUrl(fileUrl);
            amazonS3.deleteObject(new DeleteObjectRequest(bucketName, fileName));
            System.out.println("✅ 파일 삭제 완료: " + fileName);
        } catch (Exception e) {
            System.err.println("🚨 파일 삭제 실패: " + e.getMessage());
            throw new RuntimeException("파일 삭제 중 오류 발생");
        }
    }

    // ✅ S3 URL에서 파일 이름 추출하는 메서드
    private String extractFileNameFromUrl(String fileUrl) {
        return fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
    }
}
