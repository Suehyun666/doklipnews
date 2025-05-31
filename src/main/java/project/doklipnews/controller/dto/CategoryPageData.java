package project.doklipnews.controller.dto;

import java.util.List;

@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class CategoryPageData {
    private List<ArticleListDTO> articles;
    private int totalPages;
    private List<ArticleListDTO> trendingArticles;
    private List<ArticleListDTO> latestArticles;
    private List<ArticleListDTO> opinionArticles;
}
