package project.doklipnews.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.nio.file.Path;
import java.nio.file.Paths;

@RequestMapping("/uploads")
@Controller
public class FileController {
    @GetMapping("/{imageName}")
    public ResponseEntity<Resource> getImage(@PathVariable String imageName) {
        Path imagePath = Paths.get("uploads/" + imageName);
        Resource resource = new FileSystemResource(imagePath);

        // 이미지가 존재하는지 확인
        if (resource.exists()) {
            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf("image/webp")) // WebP 이미지 타입 지정
                    .body(resource);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
