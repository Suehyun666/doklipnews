package project.doklipnews.controller.dto;

import java.util.List;

@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class MainPageData {
    private ArticleListDTO headline;
    private List<ArticleListDTO> trendingArticles;
    private List<ArticleListDTO> mostLikedArticles;
    private List<ArticleListDTO> latestArticles;
    private List<ArticleListDTO> opinionArticles;
}