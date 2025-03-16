package project.doklipnews.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

@Controller
@RequestMapping("/api/placeholder")
public class PlaceholderController {

    @GetMapping(value = "/{width}/{height}", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] getPlaceholderImage(@PathVariable int width, @PathVariable int height) throws IOException {
        // 이미지 사이즈가 너무 크지 않도록 제한
        width = Math.min(width, 1200);
        height = Math.min(height, 1200);

        // 이미지 생성
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();

        // 배경 색상 설정 (회색)
        graphics.setColor(new Color(200, 200, 200));
        graphics.fillRect(0, 0, width, height);

        // 텍스트 색상 설정 (어두운 회색)
        graphics.setColor(new Color(100, 100, 100));
        graphics.setFont(new Font("Arial", Font.BOLD, 20));

        // 텍스트 내용 설정
        String text = width + " x " + height;

        // 텍스트 위치 계산 (중앙에 위치)
        FontMetrics fontMetrics = graphics.getFontMetrics();
        int textWidth = fontMetrics.stringWidth(text);
        int textHeight = fontMetrics.getHeight();
        int x = (width - textWidth) / 2;
        int y = (height - textHeight) / 2 + fontMetrics.getAscent();

        // 텍스트 그리기
        graphics.drawString(text, x, y);
        graphics.dispose();

        // 이미지를 바이트 배열로 변환
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }
}