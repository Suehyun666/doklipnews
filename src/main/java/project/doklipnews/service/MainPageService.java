package project.doklipnews.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import project.doklipnews.controller.dto.ArticleListDTO;
import project.doklipnews.controller.dto.CategoryPageData;
import project.doklipnews.controller.dto.MainPageData;
import project.doklipnews.entity.Article;
import project.doklipnews.mapper.ArticleMapper;
import project.doklipnews.repository.ArticleRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MainPageService {

    private final ArticleRepository articleRepository;

    @Cacheable(value = "mainPageData", key = "#category ?: 'all'")
    public MainPageData getMainPageData(String category) {
        if (category == null || category.isEmpty()) {
            return getAllCategoryMainPageData();
        } else {
            return getCategoryMainPageData(category);
        }
    }

    private MainPageData getAllCategoryMainPageData() {
        // 헤드라인 기사
        List<ArticleListDTO> headlines = articleRepository.findDTOByFeaturedTrue(PageRequest.of(0, 1)).getContent();

        // 인기 기사들
        List<ArticleListDTO> trending = articleRepository.findTop10DTOByOrderByViewCountDesc(PageRequest.of(0, 10));
        List<ArticleListDTO> mostLiked = articleRepository.findTop10DTOByOrderByLikeCountDesc(PageRequest.of(0, 10));
        List<ArticleListDTO> latest = articleRepository.findTop10DTOByOrderByCreatedAtDesc(PageRequest.of(0, 10));

        // 오피니언 기사 (Entity 반환하므로 매퍼 사용)
        List<Article> opinionEntities = articleRepository.findByCategory("opinion", PageRequest.of(0, 4)).getContent();
        List<ArticleListDTO> opinion = ArticleMapper.toListDTOs(opinionEntities);

        return MainPageData.builder()
                .headline(headlines.isEmpty() ? null : headlines.get(0))
                .trendingArticles(trending)
                .mostLikedArticles(mostLiked)
                .latestArticles(latest)
                .opinionArticles(opinion)
                .build();
    }

    private MainPageData getCategoryMainPageData(String category) {
        // 헤드라인은 전체에서
        List<ArticleListDTO> headlines = articleRepository.findDTOByFeaturedTrue(PageRequest.of(0, 1)).getContent();

        // 카테고리별 데이터
        List<ArticleListDTO> trending = articleRepository.findTop10DTOByCategoryOrderByViewCountDesc(category, PageRequest.of(0, 10));
        List<ArticleListDTO> latest = articleRepository.findTop10DTOByCategoryOrderByCreatedAtDesc(category, PageRequest.of(0, 10));

        // 전체 좋아요 기사
        List<ArticleListDTO> mostLiked = articleRepository.findTop10DTOByOrderByLikeCountDesc(PageRequest.of(0, 10));

        // 오피니언
        List<Article> opinionEntities = articleRepository.findByCategory("opinion", PageRequest.of(0, 4)).getContent();
        List<ArticleListDTO> opinion = ArticleMapper.toListDTOs(opinionEntities);

        return MainPageData.builder()
                .headline(headlines.isEmpty() ? null : headlines.get(0))
                .trendingArticles(trending)
                .mostLikedArticles(mostLiked)
                .latestArticles(latest)
                .opinionArticles(opinion)
                .build();
    }

    // 카테고리 페이지용 데이터
    @Cacheable(value = "categoryPageData", key = "#category + '_' + #page + '_' + #size")
    public CategoryPageData getCategoryPageData(String category, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);

        // 메인 기사들
        var articlesPage = articleRepository.findArticleListByCategory(category, pageable);

        // 관련 기사들
        List<ArticleListDTO> trending = articleRepository.findTop10DTOByCategoryOrderByViewCountDesc(category, PageRequest.of(0, 5));
        List<ArticleListDTO> latest = articleRepository.findTop10DTOByCategoryOrderByCreatedAtDesc(category, PageRequest.of(0, 5));

        // 오피니언
        List<Article> opinionEntities = articleRepository.findByCategory("column", PageRequest.of(0, 4)).getContent();
        List<ArticleListDTO> opinion = ArticleMapper.toListDTOs(opinionEntities);

        return CategoryPageData.builder()
                .articles(articlesPage.getContent())
                .totalPages(articlesPage.getTotalPages())
                .trendingArticles(trending)
                .latestArticles(latest)
                .opinionArticles(opinion)
                .build();
    }
}