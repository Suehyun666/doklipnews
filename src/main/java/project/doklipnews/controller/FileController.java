package project.doklipnews.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
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
import java.util.concurrent.TimeUnit;

@RequestMapping("/uploads")
@Controller
public class FileController {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    // 캐싱 설정
    private static final int CACHE_PERIOD_SECONDS = 604800; // 7일
    private static final int CACHE_PERIOD_LONG_SECONDS = 2592000; // 30일

    // MIME 타입 매핑
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

    @GetMapping("/{fileName:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String fileName,
                                            HttpServletRequest request) {
        try {
            // 경로 순회 방지
            if (fileName.contains("..")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // 파일 경로 생성
            Path filePath = Paths.get(uploadDir, fileName).normalize();
            Resource resource = new FileSystemResource(filePath);

            // 파일이 존재하는지 확인
            if (!resource.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // MIME 타입 결정
            String contentType = determineContentType(fileName, resource, request);

            // 캐시 제어 설정
            CacheControl cacheControl;

            // 이미지 및 정적 콘텐츠는 더 긴 캐시 기간 적용
            if (isStaticContent(fileName)) {
                cacheControl = CacheControl.maxAge(CACHE_PERIOD_LONG_SECONDS, TimeUnit.SECONDS)
                        .cachePublic();
            } else {
                cacheControl = CacheControl.maxAge(CACHE_PERIOD_SECONDS, TimeUnit.SECONDS)
                        .cachePublic();
            }

            // 요청된 컨텐츠에 ETag 추가 (리소스의 변경 감지)
            String eTag = generateETag(resource);

            // Brotli 또는 Gzip 압축 지원 확인
            String acceptEncoding = request.getHeader("Accept-Encoding");
            HttpHeaders headers = new HttpHeaders();

            if (acceptEncoding != null) {
                if (acceptEncoding.contains("br") && !isPreCompressedFormat(fileName)) {
                    // Brotli 압축이 서버에서 지원된다면 여기서 처리
                    // 현재 스프링에서 기본 지원하지 않으므로 별도 구현 필요
                } else if (acceptEncoding.contains("gzip") && !isPreCompressedFormat(fileName)) {
                    // Gzip 압축이 서버에서 지원된다면 여기서 처리
                    // 현재 스프링에서 기본 지원하지 않으므로 별도 구현 필요
                }
            }

            // 응답 헤더 설정 및 리소스 반환
            return ResponseEntity.ok()
                    .cacheControl(cacheControl)
                    .eTag(eTag)
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);

        } catch (Exception e) {
            // 예외 발생 시 로깅
            System.err.println("파일 제공 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // MIME 타입 결정
    private String determineContentType(String fileName, Resource resource, HttpServletRequest request) {
        // 파일 확장자 추출
        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i + 1).toLowerCase();
        }

        // 커스텀 MIME 타입 맵에서 확인
        if (MIME_TYPES.containsKey(extension)) {
            return MIME_TYPES.get(extension);
        }

        // 기본 MIME 타입 감지 시도
        try {
            String mimeType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            if (mimeType != null) {
                return mimeType;
            }
        } catch (IOException ex) {
            System.err.println("MIME 타입 감지 실패: " + ex.getMessage());
        }

        // 기본값 반환
        return "application/octet-stream";
    }

    // 정적 컨텐츠 여부 확인 (이미지, 비디오, 폰트 등)
    private boolean isStaticContent(String fileName) {
        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i + 1).toLowerCase();
        }

        return MIME_TYPES.containsKey(extension) ||
                extension.equals("css") ||
                extension.equals("js") ||
                extension.equals("woff") ||
                extension.equals("woff2") ||
                extension.equals("ttf");
    }

    // 이미 압축된 포맷인지 확인
    private boolean isPreCompressedFormat(String fileName) {
        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i + 1).toLowerCase();
        }

        // 이미 압축된 포맷 목록
        return extension.equals("jpg") ||
                extension.equals("jpeg") ||
                extension.equals("png") ||
                extension.equals("webp") ||
                extension.equals("avif") ||
                extension.equals("mp4") ||
                extension.equals("webm") ||
                extension.equals("zip") ||
                extension.equals("gz") ||
                extension.equals("pdf");
    }

    // ETag 생성 (파일 내용 기반)
    private String generateETag(Resource resource) {
        try {
            long contentLength = resource.contentLength();
            long lastModified = resource.lastModified();
            return String.format("\"%x-%x\"", contentLength, lastModified);
        } catch (IOException e) {
            // 오류 발생 시 fallback ETag 생성
            return String.format("\"%x\"", System.currentTimeMillis());
        }
    }
}