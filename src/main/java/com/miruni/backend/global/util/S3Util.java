package com.miruni.backend.global.util;

import com.miruni.backend.global.exception.BaseException;
import com.miruni.backend.global.exception.CommonErrorCode;
import com.miruni.backend.global.properties.S3Properties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Util {

    private final S3Presigner s3Presigner;
    private final S3Properties s3Properties;
    private final S3Client s3Client;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;


    public String uploadFile(MultipartFile file, String directory){
        if(directory == null || directory.contains("..")){
            throw BaseException.type(CommonErrorCode.INVALID_INPUT_VALUE);
        }

        String extension = getExtension(file.getOriginalFilename());
        String contentType = file.getContentType();
        validateFile(file, extension, contentType);

        String fileName = UUID.randomUUID() + extension;
        String key = directory + "/" + fileName;

        try{
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(s3Properties.bucket())
                    .key(key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );
            log.info("File uploaded successfully: {}", key);
            return key;
        } catch(IOException | S3Exception e){
            log.error("File upload failed: {}", key, e);
            throw BaseException.type(CommonErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    public String generatePresignedUrl(String key, int durationMinutes){
        try{
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(s3Properties.bucket())
                    .key(key)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(durationMinutes))
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);

            return presignedRequest.url().toString();

        }catch (S3Exception e){
            log.error("PresignedGetObjectRequest failed: {}", key, e);
            throw BaseException.type(CommonErrorCode.GENERATED_PRESIGNED_URL_FAILED);
        }
    }

    public void deleteFile(String key){
        try{
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(s3Properties.bucket())
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            log.info("File deleted successfully: {}", key);
        } catch(S3Exception e){
            log.error("DeleteObject failed: {}", key, e);
            throw BaseException.type(CommonErrorCode.FILE_DELETE_FAILED);
        }
    }

    public String getPublicUrl(String key){
        if (key == null) return null;
        return s3Properties.bucket() + "/" + key;
    }

    private void validateFile(MultipartFile file, String extension, String contentType){
        if(file == null || file.isEmpty()){
            throw BaseException.type(CommonErrorCode.FILE_IS_EMPTY);
        }

        if(file.getSize() > MAX_FILE_SIZE){
            throw BaseException.type(CommonErrorCode.FILE_SIZE_EXCEED_LIMIT);
        }

        if(extension.isEmpty()){
            throw BaseException.type(CommonErrorCode.NOT_FOUND_FILE_EXTENSION);
        }

        if(extension.length() <= 1){
            throw BaseException.type(CommonErrorCode.INVALID_FILE_EXTENSION);
        }

        String ext = extension.substring(1);
        if(!ALLOWED_EXTENSIONS.contains(ext)){
            throw BaseException.type(CommonErrorCode.INVALID_FILE_EXTENSION);
        }

        if(contentType != null && !contentType.startsWith("image/")){
            throw BaseException.type(CommonErrorCode.NOT_IMAGE_CONTENT_TYPE);
        }
    }

    private String getExtension(String filename){
        if (filename == null || !filename.contains(".")){
            return "";
        }
        return filename.substring(filename.lastIndexOf(".")).toLowerCase();
    }
}
