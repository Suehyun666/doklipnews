package project.doklipnews.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;

@Service
public class FileService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    // 기본 이미지 경로
    private static final String DEFAULT_IMAGE = "/images/default.jpg";

    // 이미지 최적화 설정
    private static final int MAX_WIDTH = 1200;
    private static final float COMPRESSION_QUALITY = 0.7f; // 70% 품질로 압축률 강화
    private static final boolean CONVERT_TO_WEBP = true; // WebP 변환 활성화

    // 파일 업로드 처리
    public String uploadFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return DEFAULT_IMAGE;
        }

        File uploadDirPath = new File(uploadDir);
        if (!uploadDirPath.exists()) {
            uploadDirPath.mkdirs(); // 상위 디렉토리 포함하여 생성
        }

        String originalFileName = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFileName);

        // 이미지 파일인지 확인
        boolean isImage = isImageFile(fileExtension);

        // 파일명 생성 (WebP 변환 고려)
        String newFileName;
        if (isImage && CONVERT_TO_WEBP) {
            newFileName = UUID.randomUUID().toString() + ".webp";
        } else {
            newFileName = UUID.randomUUID().toString() + "." + fileExtension;
        }

        // 파일 경로
        Path fullPath = Paths.get(uploadDir, newFileName);

        // 이미지 최적화 처리
        if (isImage) {
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            if (originalImage == null) {
                // 읽을 수 없는 이미지인 경우 원본 저장
                Files.copy(file.getInputStream(), fullPath);
                return "/uploads/" + newFileName;
            }

            // 이미지 리사이징 (필요한 경우)
            BufferedImage processedImage = resizeImageIfNeeded(originalImage);

            // WebP 변환 또는 압축 저장
            if (CONVERT_TO_WEBP) {
                boolean success = saveAsWebP(processedImage, fullPath.toFile());
                if (!success) {
                    // WebP 저장 실패 시 JPG로 대체
                    String jpgFileName = UUID.randomUUID().toString() + ".jpg";
                    Path jpgPath = Paths.get(uploadDir, jpgFileName);
                    saveCompressedImage(processedImage, "jpg", jpgPath.toFile());
                    return "/uploads/" + jpgFileName;
                }
            } else {
                saveCompressedImage(processedImage, fileExtension, fullPath.toFile());
            }
        } else {
            // 이미지가 아닌 경우 그대로 저장
            Files.copy(file.getInputStream(), fullPath);
        }

        return "/uploads/" + newFileName;
    }

    // 파일 확장자 추출
    private String getFileExtension(String fileName) {
        if (fileName == null) return "";
        int lastDotIndex = fileName.lastIndexOf(".");
        return lastDotIndex > 0 ? fileName.substring(lastDotIndex + 1).toLowerCase() : "";
    }

    // 이미지 파일 여부 확인
    private boolean isImageFile(String extension) {
        return extension.equals("jpg") || extension.equals("jpeg") ||
                extension.equals("png") || extension.equals("gif") ||
                extension.equals("webp") || extension.equals("bmp");
    }

    // 이미지 리사이징 (필요한 경우)
    private BufferedImage resizeImageIfNeeded(BufferedImage image) {
        // 원본 이미지가 최대 너비보다 크면 리사이징
        if (image.getWidth() > MAX_WIDTH) {
            float ratio = (float) MAX_WIDTH / image.getWidth();
            int newHeight = (int) (image.getHeight() * ratio);

            // 알파 채널 지원을 위한 타입 조정
            int type = image.getTransparency() == Transparency.OPAQUE ?
                    BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;

            BufferedImage resizedImage = new BufferedImage(MAX_WIDTH, newHeight, type);
            Graphics2D g = resizedImage.createGraphics();

            // 이미지 품질 개선을 위한 렌더링 힌트 설정
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g.drawImage(image, 0, 0, MAX_WIDTH, newHeight, null);
            g.dispose();

            return resizedImage;
        }

        return image; // 이미 적정 크기이면 원본 반환
    }

    // 압축된 이미지 저장 (JPEG/PNG 등)
    private void saveCompressedImage(BufferedImage image, String formatName, File output) throws IOException {
        if (formatName.equals("jpg") || formatName.equals("jpeg")) {
            // JPEG 압축 최적화
            try (FileOutputStream out = new FileOutputStream(output)) {
                Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
                if (!writers.hasNext()) throw new IOException("No JPEG writer found");

                ImageWriter writer = writers.next();
                ImageOutputStream ios = ImageIO.createImageOutputStream(out);
                writer.setOutput(ios);

                JPEGImageWriteParam param = new JPEGImageWriteParam(Locale.getDefault());
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(COMPRESSION_QUALITY);

                writer.write(null, new IIOImage(image, null, null), param);

                ios.close();
                writer.dispose();
            }
        } else if (formatName.equals("png")) {
            // PNG 압축 (PNG는 무손실이므로 압축 품질 설정이 다름)
            try (FileOutputStream out = new FileOutputStream(output)) {
                Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("png");
                if (!writers.hasNext()) throw new IOException("No PNG writer found");

                ImageWriter writer = writers.next();
                ImageOutputStream ios = ImageIO.createImageOutputStream(out);
                writer.setOutput(ios);

                ImageWriteParam param = writer.getDefaultWriteParam();

                writer.write(null, new IIOImage(image, null, null), param);

                ios.close();
                writer.dispose();
            }
        } else {
            // 기타 포맷은 기본 저장
            ImageIO.write(image, formatName, output);
        }
    }

    // WebP 형식으로 저장
    private boolean saveAsWebP(BufferedImage image, File output) {
        try {
            // 1. WebP 라이브러리 사용하여 인코딩 시도
            return encodeImageToWebP(image, output);
        } catch (Exception e) {
            System.err.println("WebP 인코딩 실패: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // WebP 인코딩 구현
    private boolean encodeImageToWebP(BufferedImage image, File output) {
        try {
            // --- 방법 1: ImageIO 사용 (만약 WebP 플러그인이 제대로 등록된 경우) ---
            if (isWebPWriterAvailable()) {
                return ImageIO.write(image, "webp", output);
            }

            // --- 방법 2: 외부 라이브러리 luciad-webp 사용 ---
            /* 아래는 luciad-webp 라이브러리 사용 예제입니다.
               실제 구현 시에는 라이브러리 문서를 참고하세요. */
            try {
                // luciad-webp 라이브러리 예시
                /*
                WebPWriteParam writeParam = new WebPWriteParam(Locale.getDefault());
                writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                writeParam.setCompressionQuality(COMPRESSION_QUALITY);

                WebPWriter writer = new WebPWriter();
                ImageOutputStream ios = ImageIO.createImageOutputStream(output);
                writer.setOutput(ios);
                writer.write(null, new IIOImage(image, null, null), writeParam);
                ios.close();
                */

                // 실제 구현에서는 사용 중인 라이브러리에 맞게 코드를 수정하세요
                throw new UnsupportedOperationException("WebP 라이브러리 구현 필요");
            } catch (UnsupportedOperationException e) {
                // 라이브러리 구현 부재 시 외부 프로세스 호출 시도
                return convertToWebPUsingExternalProcess(image, output);
            }
        } catch (Exception e) {
            System.err.println("WebP 인코딩 모든 방법 실패: " + e.getMessage());
            return false;
        }
    }

    // WebP 라이터 사용 가능 여부 확인
    private boolean isWebPWriterAvailable() {
        try {
            Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("webp");
            return iter.hasNext();
        } catch (Exception e) {
            return false;
        }
    }

    // 외부 프로세스 사용하여 WebP 변환
    private boolean convertToWebPUsingExternalProcess(BufferedImage image, File output) throws IOException {
        // 1. 임시 파일로 이미지 저장
        File tempFile = File.createTempFile("temp_", ".png");
        try {
            ImageIO.write(image, "png", tempFile);

            // 2. cwebp 명령 실행
            // 서버에 cwebp가 설치되어 있어야 함
            ProcessBuilder pb = new ProcessBuilder(
                    "cwebp",
                    "-q", String.valueOf((int)(COMPRESSION_QUALITY * 100)),
                    tempFile.getAbsolutePath(),
                    "-o", output.getAbsolutePath()
            );

            Process process = pb.start();
            int exitCode = process.waitFor();

            return exitCode == 0;
        } catch (InterruptedException | IOException e) {
            System.err.println("외부 프로세스 WebP 변환 실패: " + e.getMessage());
            return false;
        } finally {
            // 임시 파일 삭제
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
    }
}