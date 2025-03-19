package project.doklipnews.mapper;

import org.springframework.stereotype.Component;
import project.doklipnews.controller.dto.ArticleDetailDTO;
import project.doklipnews.controller.dto.ArticleListDTO;
import project.doklipnews.entity.Article;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ArticleMapper {

    // Article 엔티티를 ArticleListDTO로 변환
    public static ArticleListDTO toListDTO(Article article) {
        if (article == null) {
            return null;
        }

        ArticleListDTO dto = new ArticleListDTO();
        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setAuthor(article.getAuthor());
        dto.setCreatedAt(article.getCreatedAt());
        dto.setCategory(article.getCategory());
        dto.setImageUrl(article.getImageUrl());
        dto.setViewCount(article.getViewCount());
        dto.setLikeCount(article.getLikeCount());
        dto.setSummary(article.getSummary());
        return dto;
    }

    // Article 엔티티를 ArticleDetailDTO로 변환
    public static ArticleDetailDTO toDetailDTO(Article article) {
        if (article == null) {
            return null;
        }

        ArticleDetailDTO dto = new ArticleDetailDTO(
                article.getId(),
                article.getTitle(),
                article.getAuthor(),
                article.getCreatedAt(),
                article.getCategory(),
                article.getImageUrl(),
                article.getViewCount(),
                article.getLikeCount(),
                article.getContent()
        );

        return dto;
    }

    // Article 엔티티 리스트를 ArticleListDTO 리스트로 변환
    public static List<ArticleListDTO> toListDTOs(List<Article> articles) {
        if (articles == null) {
            return null;
        }

        return articles.stream()
                .map(ArticleMapper::toListDTO)
                .collect(Collectors.toList());
    }
}