package project.doklipnews.controller.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ArticleDetailDTO {
    private Long id;
    private String title;
    private String author;
    private LocalDateTime createdAt;
    private String category;
    private String imageUrl;
    private Long viewCount;
    private Long likeCount;
    private String content; // 상세 조회 시에만 필요

    // 생성자
}
