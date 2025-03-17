package project.doklipnews.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import project.doklipnews.entity.Article;
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
     * 키워드로 기사 검색 (제목 또는 내용에 키워드가 포함된 기사)
     *
     * @param keyword  검색 키워드
     * @param pageable 페이징 정보
     * @return 검색 결과
     */
    public Page<Article> searchArticles(String keyword, Pageable pageable) {
        return articleRepository.searchByTitleOrContent(keyword, pageable);
    }

    /**
     * 키워드와 카테고리로 기사 검색
     *
     * @param keyword  검색 키워드
     * @param category 카테고리
     * @param pageable 페이징 정보
     * @return 검색 결과
     */
    public Page<Article> searchArticlesByCategory(String keyword, String category, Pageable pageable) {
        return articleRepository.searchByTitleOrContentAndCategory(keyword, category, pageable);
    }

    /**
     * 메인 검색 결과 기사 반환 (첫 번째 검색 결과)
     *
     * @param keyword  검색 키워드
     * @param category 카테고리 (선택)
     * @return 메인 검색 결과 기사 또는 null
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
     *
     * @param keyword  검색 키워드
     * @param category 카테고리 (선택)
     * @param limit    가져올 기사 수
     * @return 이미지가 있는 최신 기사 목록
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
     * 메인 기사와 이미지 그리드 기사를 제외한 검색 결과 리스트 가져오기
     *
     * @param keyword    검색 키워드
     * @param category   카테고리 (선택)
     * @param excludeIds 제외할 기사 ID 목록
     * @param limit      가져올 기사 수
     * @return 검색 결과 리스트
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
     * 키워드와 관련된, 특정 카테고리의 최신 의견/칼럼 기사 가져오기
     *
     * @param keyword 검색 키워드
     * @param limit   가져올 기사 수
     * @return 검색 결과 의견/칼럼 기사 목록
     */
    public List<Article> getOpinionArticles(String keyword, int limit) {
        // 칼럼 카테고리를 "column"으로 가정
        Pageable pageable = PageRequest.of(0, limit);
        return articleRepository.searchByTitleOrContentAndCategory(keyword, "column", pageable)
                .getContent();
    }

    /**
     * 검색 결과 총 개수 가져오기
     *
     * @param keyword  검색 키워드
     * @param category 카테고리 (선택)
     * @return 검색 결과 총 개수
     */
    public long getTotalSearchResultCount(String keyword, String category) {
        Pageable pageable = PageRequest.of(0, 1); // 한 개만 가져와서 총 개수만 확인

        if (category != null && !category.isEmpty()) {
            return articleRepository.searchByTitleOrContentAndCategory(keyword, category, pageable).getTotalElements();
        } else {
            return articleRepository.searchByTitleOrContent(keyword, pageable).getTotalElements();
        }
    }
}