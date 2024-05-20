package com.yx.product.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author 97557
 */
public interface FileUploadService {

    /**
     * 上传文件
     *
     * @param file 文件
     * @return {@link String}
     */
    String uploadFile(MultipartFile file);
}
