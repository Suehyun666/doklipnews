package project.doklipnews.controller.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ArticleListDTO {
    private Long id;
    private String title;
    private String author;
    private LocalDateTime createdAt;
    private String summary;
    private String category;
    private String imageUrl;
    private Long viewCount;
    private Long likeCount;

    // 기본 생성자
    public ArticleListDTO() {
    }

    // Repository JPQL 쿼리용 생성자
    public ArticleListDTO(Long id, String title, String author, LocalDateTime createdAt,
                          String category, String imageUrl, Long viewCount, Long likeCount) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.createdAt = createdAt;
        this.category = category;
        this.imageUrl = imageUrl;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
    }

    // 요약 포함 생성자
    public ArticleListDTO(Long id, String title, String author, LocalDateTime createdAt,
                          String category, String imageUrl, Long viewCount, Long likeCount, String summary) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.createdAt = createdAt;
        this.category = category;
        this.imageUrl = imageUrl;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.summary = summary;
    }
}