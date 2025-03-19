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
    private String summary;

    public ArticleDetailDTO() {

    }
    // 생성자
    public ArticleDetailDTO(Long id, String title, String author, LocalDateTime createdAt,
                            String category, String imageUrl, Long viewCount, Long likeCount, String content) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.createdAt = createdAt;
        this.category = category;
        this.imageUrl = imageUrl;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.content = content;
    }
}
