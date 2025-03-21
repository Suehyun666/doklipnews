package project.doklipnews.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column
    private String summary;

    @Column(nullable = false)
    private String author;

    private String category;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private String imageUrl;

    // 조회수 필드 추가
    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long viewCount = 0L;

    // 좋아요 수 필드 추가
    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long likeCount = 0L;

    // 추천 기사 여부
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean featured = false;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (viewCount == null) viewCount = 0L;
        if (likeCount == null) likeCount = 0L;
    }

    // 조회수 증가 메서드
    public void incrementViewCount() {
        this.viewCount++;
    }

    // 좋아요 증가 메서드
    public void incrementLikeCount() {
        this.likeCount++;
    }

    // 좋아요 감소 메서드
    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

}