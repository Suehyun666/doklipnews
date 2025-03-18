package project.doklipnews.controller;

import jakarta.servlet.http.HttpServletRequest;
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

@RequestMapping("/uploads")
@Controller
public class FileController {
    @GetMapping("/{imageName}")
    public ResponseEntity<Resource> getImage(@PathVariable String imageName,
                                             HttpServletRequest request) {
        Path imagePath = Paths.get("uploads/" + imageName);
        Resource resource = new FileSystemResource(imagePath);

        // 이미지가 존재하는지 확인
        if (resource.exists()) {
            // 이미지 타입 동적 감지
            String contentType;
            try {
                contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            } catch (IOException ex) {
                contentType = "application/octet-stream";
            }

            // 캐싱 헤더 추가 (브라우저에서 1주일간 캐싱)
            return ResponseEntity.ok()
                    .header(HttpHeaders.CACHE_CONTROL, "max-age=604800")
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
