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
    private String category;
    private String imageUrl;
    private Long viewCount;
    private Long likeCount;

    // 생성자
    public ArticleListDTO() {

    }
}