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

    @Column(nullable = false, length = 10000)
    private String content;

    @Column(nullable = false)
    private String author;

    private String category;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private String imageUrl;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
} 