package project.doklipnews.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
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
            return DEFAULT_IMAGE;
        }

        File uploadDirPath = new File(uploadDir);
        if (!uploadDirPath.exists()) {
            uploadDirPath.mkdir();
        }

        String originalFileName = file.getOriginalFilename();
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toLowerCase();
        String newFileName = UUID.randomUUID().toString() + "." + fileExtension;

        // 파일 경로
        Path fullPath = Paths.get(uploadDir, newFileName);

        // 이미지 최적화 (압축)
        if (fileExtension.equals("jpg") || fileExtension.equals("jpeg") || fileExtension.equals("png")) {
            BufferedImage originalImage = ImageIO.read(file.getInputStream());

            // 이미지 크기 제한 (너무 큰 경우 리사이징)
            int maxWidth = 1200;
            if (originalImage.getWidth() > maxWidth) {
                float ratio = (float) maxWidth / originalImage.getWidth();
                int newHeight = (int) (originalImage.getHeight() * ratio);

                BufferedImage resizedImage = new BufferedImage(maxWidth, newHeight, originalImage.getType());
                Graphics2D g = resizedImage.createGraphics();
                g.drawImage(originalImage, 0, 0, maxWidth, newHeight, null);
                g.dispose();

                // 압축 품질 설정
                ImageWriter writer = ImageIO.getImageWritersByFormatName(fileExtension).next();
                ImageWriteParam param = writer.getDefaultWriteParam();

                if (param.canWriteCompressed()) {
                    param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                    param.setCompressionQuality(0.8f); // 70% 품질
                }

                try (FileOutputStream output = new FileOutputStream(fullPath.toFile())) {
                    ImageOutputStream ios = ImageIO.createImageOutputStream(output);
                    writer.setOutput(ios);
                    writer.write(null, new IIOImage(resizedImage, null, null), param);
                    ios.close();
                    writer.dispose();
                }
            } else {
                // 작은 이미지는 그대로 저장하되 압축만 적용
                Files.copy(file.getInputStream(), fullPath);
            }
        } else {
            // 이미지가 아닌 경우 그대로 저장
            Files.copy(file.getInputStream(), fullPath);
        }

        return "/uploads/" + newFileName;
    }
}
