package com.volzhin.auction.service.image.s3;

import com.volzhin.auction.entity.Image;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@Service
public interface S3Service {

    Image uploadFile(MultipartFile file, String folder);

    List<Image> uploadFiles(List<MultipartFile> files, String folder);

    void deleteFile(String key);

    InputStream downloadFile(String key);
}