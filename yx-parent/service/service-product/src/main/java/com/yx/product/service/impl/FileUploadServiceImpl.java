package com.yx.product.service.impl;

import com.yx.product.service.FileUploadService;
import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Value("${minio.endpoint}")
    private String minioUrl;

    @Value("${minio.accessKey}")
    private String minioAccessKey;

    @Value("${minio.secretKey}")
    private String minioSecretKey;

    @Value("${minio.bucketName}")
    private String minioBucketName;

    /**
     * 上传文件
     *
     * @param file 文件
     * @return {@link String}
     */
    @Override
    public String uploadFile(MultipartFile file) {
        try {
            // Initialize Minio client
            MinioClient minioClient = new MinioClient(minioUrl, minioAccessKey, minioSecretKey);
            // Check if the bucket exists, if not create it
            boolean bucketExists = minioClient.bucketExists(minioBucketName);
            if (!bucketExists) {
                minioClient.makeBucket(minioBucketName);
            }
            PutObjectOptions options = new PutObjectOptions(file.getSize(), -1);
            options.setContentType(file.getContentType());
            // Upload file to Minio
            minioClient.putObject(minioBucketName, file.getOriginalFilename(), file.getInputStream(), options);

            return minioUrl + "/" + minioBucketName + "/" + file.getOriginalFilename();
        } catch (Exception e) {
            System.out.println("Error Message:" + e.getMessage());
        }
        return null;
    }
}
