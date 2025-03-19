package project.doklipnews.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import project.doklipnews.controller.dto.ArticleListDTO;
import project.doklipnews.entity.Article;
import project.doklipnews.mapper.ArticleMapper;
import project.doklipnews.repository.ArticleRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {

    private final ArticleRepository articleRepository;

    @Autowired
    public SearchService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    /**
     * 키워드로 기사 검색 (DTO 반환)
     */
    public Page<ArticleListDTO> searchArticlesDTO(String keyword, Pageable pageable) {
        return articleRepository.searchDTOByTitleOrContent(keyword, pageable);
    }

    /**
     * 키워드와 카테고리로 기사 검색 (DTO 반환)
     */
    public Page<ArticleListDTO> searchArticlesByCategoryDTO(String keyword, String category, Pageable pageable) {
        return articleRepository.searchDTOByTitleOrContentAndCategory(keyword, category, pageable);
    }

    /**
     * 메인 검색 결과 기사 반환 (DTO)
     */
    public ArticleListDTO getMainSearchArticleDTO(String keyword, String category) {
        Pageable pageable = PageRequest.of(0, 1);
        Page<ArticleListDTO> searchResult;

        if (category != null && !category.isEmpty()) {
            searchResult = articleRepository.searchDTOByTitleOrContentAndCategory(keyword, category, pageable);
        } else {
            searchResult = articleRepository.searchDTOByTitleOrContent(keyword, pageable);
        }

        return searchResult.hasContent() ? searchResult.getContent().get(0) : null;
    }

    // 기존 엔티티 반환 메서드들 (하위 호환성 유지)

    /**
     * 키워드로 기사 검색 (제목 또는 내용에 키워드가 포함된 기사)
     */
    public Page<Article> searchArticles(String keyword, Pageable pageable) {
        return articleRepository.searchByTitleOrContent(keyword, pageable);
    }

    /**
     * 키워드와 카테고리로 기사 검색
     */
    public Page<Article> searchArticlesByCategory(String keyword, String category, Pageable pageable) {
        return articleRepository.searchByTitleOrContentAndCategory(keyword, category, pageable);
    }

    /**
     * 메인 검색 결과 기사 반환 (첫 번째 검색 결과)
     */
    public Article getMainSearchArticle(String keyword, String category) {
        Pageable pageable = PageRequest.of(0, 1);
        Page<Article> searchResult;

        if (category != null && !category.isEmpty()) {
            searchResult = articleRepository.searchByTitleOrContentAndCategory(keyword, category, pageable);
        } else {
            searchResult = articleRepository.searchByTitleOrContent(keyword, pageable);
        }

        return searchResult.hasContent() ? searchResult.getContent().get(0) : null;
    }

    /**
     * 이미지가 있는 최신 기사 검색 결과 가져오기
     */
    public List<Article> getImageGridArticles(String keyword, String category, int limit) {
        Pageable pageable = PageRequest.of(0, limit);

        if (category != null && !category.isEmpty()) {
            return articleRepository.findRecentArticlesWithImagesByKeywordAndCategory(keyword, category, pageable);
        } else {
            return articleRepository.findRecentArticlesWithImagesByKeyword(keyword, pageable);
        }
    }

    /**
     * 이미지가 있는 최신 기사 검색 결과 가져오기 (DTO 반환)
     */
    public List<ArticleListDTO> getImageGridArticlesDTO(String keyword, String category, int limit) {
        List<Article> articles = getImageGridArticles(keyword, category, limit);
        return ArticleMapper.toListDTOs(articles);
    }

    /**
     * 메인 기사와 이미지 그리드 기사를 제외한 검색 결과 리스트 가져오기
     */
    public List<Article> getListArticles(String keyword, String category, List<Long> excludeIds, int limit) {
        Pageable pageable = PageRequest.of(0, limit);

        if (excludeIds == null || excludeIds.isEmpty()) {
            excludeIds = new ArrayList<>();
        }

        if (category != null && !category.isEmpty()) {
            return articleRepository.findArticlesByKeywordAndCategoryExcludingIds(keyword, category, excludeIds, pageable);
        } else {
            return articleRepository.findArticlesByKeywordExcludingIds(keyword, excludeIds, pageable);
        }
    }

    /**
     * 메인 기사와 이미지 그리드 기사를 제외한 검색 결과 리스트 가져오기 (DTO 반환)
     */
    public List<ArticleListDTO> getListArticlesDTO(String keyword, String category, List<Long> excludeIds, int limit) {
        List<Article> articles = getListArticles(keyword, category, excludeIds, limit);
        return ArticleMapper.toListDTOs(articles);
    }

    /**
     * 키워드와 관련된, 특정 카테고리의 최신 의견/칼럼 기사 가져오기
     */
    public List<Article> getOpinionArticles(String keyword, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return articleRepository.searchByTitleOrContentAndCategory(keyword, "column", pageable)
                .getContent();
    }

    /**
     * 키워드와 관련된, 특정 카테고리의 최신 의견/칼럼 기사 가져오기 (DTO 반환)
     */
    public List<ArticleListDTO> getOpinionArticlesDTO(String keyword, int limit) {
        List<Article> articles = getOpinionArticles(keyword, limit);
        return ArticleMapper.toListDTOs(articles);
    }

    /**
     * 검색 결과 총 개수 가져오기
     */
    public long getTotalSearchResultCount(String keyword, String category) {
        Pageable pageable = PageRequest.of(0, 1);

        if (category != null && !category.isEmpty()) {
            return articleRepository.searchByTitleOrContentAndCategory(keyword, category, pageable).getTotalElements();
        } else {
            return articleRepository.searchByTitleOrContent(keyword, pageable).getTotalElements();
        }
    }
}