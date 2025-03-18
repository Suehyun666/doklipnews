package project.doklipnews.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("/uploads")
@Controller
public class FileController {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    // MIME 타입 매핑 추가
    private static final Map<String, String> MIME_TYPES = new HashMap<>();
    static {
        // 웹 이미지 포맷
        MIME_TYPES.put("avif", "image/avif");
        MIME_TYPES.put("webp", "image/webp");
        MIME_TYPES.put("png", "image/png");
        MIME_TYPES.put("jpg", "image/jpeg");
        MIME_TYPES.put("jpeg", "image/jpeg");
        MIME_TYPES.put("gif", "image/gif");
        MIME_TYPES.put("svg", "image/svg+xml");
        MIME_TYPES.put("ico", "image/x-icon");
        MIME_TYPES.put("bmp", "image/bmp");
        MIME_TYPES.put("tiff", "image/tiff");
        MIME_TYPES.put("tif", "image/tiff");

        // 비디오 포맷
        MIME_TYPES.put("mp4", "video/mp4");
        MIME_TYPES.put("webm", "video/webm");
        MIME_TYPES.put("ogg", "video/ogg");
        MIME_TYPES.put("mov", "video/quicktime");

        // 문서 포맷
        MIME_TYPES.put("pdf", "application/pdf");
    }

    @GetMapping("/{imageName}")
    public ResponseEntity<Resource> getImage(@PathVariable String imageName,
                                             HttpServletRequest request) {
        try {
            // 경로 순회 방지
            if (imageName.contains("..")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // 기본 경로 사용 (상대 경로가 되지 않도록 uploadDir 환경 변수 사용)
            Path imagePath = Paths.get(uploadDir, imageName).normalize();
            Resource resource = new FileSystemResource(imagePath);

            // 이미지가 존재하는지 확인
            if (!resource.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // MIME 타입 결정
            String contentType = null;

            // 파일 확장자 추출
            String filename = imagePath.getFileName().toString();
            String extension = "";
            int i = filename.lastIndexOf('.');
            if (i > 0) {
                extension = filename.substring(i + 1).toLowerCase();
            }

            // 커스텀 MIME 타입 맵에서 확인
            if (MIME_TYPES.containsKey(extension)) {
                contentType = MIME_TYPES.get(extension);
            } else {
                // 기본 MIME 타입 감지 시도
                try {
                    contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
                } catch (IOException ex) {
                    System.err.println("MIME 타입 감지 실패: " + ex.getMessage());
                }
            }

            // MIME 타입을 결정할 수 없으면 기본값 사용
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            // 응답 헤더 설정 및 리소스 반환
            return ResponseEntity.ok()
                    .header(HttpHeaders.CACHE_CONTROL, "max-age=604800")
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);

        } catch (Exception e) {
            // 예외 발생 시 로깅
            System.err.println("이미지 제공 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}