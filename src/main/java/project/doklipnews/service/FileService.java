package project.doklipnews.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    // 기본 이미지 경로
    private static final String DEFAULT_IMAGE = "/images/default.jpg";

    // 파일 업로드 처리
    public String uploadFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return DEFAULT_IMAGE; // 이미지가 없으면 기본 이미지 경로를 반환
        }

        // 업로드할 디렉토리가 없으면 생성
        File uploadDirPath = new File(uploadDir);
        if (!uploadDirPath.exists()) {
            uploadDirPath.mkdir();
        }

        // 원본 파일 이름을 기반으로 새 파일 이름 생성 (중복 방지)
        String originalFileName = file.getOriginalFilename();
        String newFileName = UUID.randomUUID().toString() + "_" + originalFileName;

        // 파일을 업로드 디렉토리에 저장
        Path path = Paths.get(uploadDir, newFileName);
        Files.copy(file.getInputStream(), path);

        return "/uploads/" + newFileName; // 저장된 파일 이름을 반환
    }
}
