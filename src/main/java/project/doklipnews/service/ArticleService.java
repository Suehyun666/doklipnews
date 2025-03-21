package project.doklipnews.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import project.doklipnews.controller.dto.ArticleDetailDTO;
import project.doklipnews.controller.dto.ArticleListDTO;
import project.doklipnews.entity.Article;
import project.doklipnews.mapper.ArticleMapper;
import project.doklipnews.repository.ArticleRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final FileService fileService;
    private final SummaryService summaryService;

    public ArticleService(ArticleRepository articleRepository, FileService fileService, SummaryService summaryService) {
        this.articleRepository = articleRepository;
        this.fileService = fileService;
        this.summaryService = summaryService;
    }

    // 새 기사 저장 (기존 그대로 유지)
    public Article createArticle(Article article, MultipartFile imageFile) {
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                String imageUrl = fileService.uploadFile(imageFile);
                article.setImageUrl(imageUrl);
            }
            if (article.getContent() != null && !article.getContent().isEmpty()) {
                String summary = summaryService.generateSummary(article.getContent());
                article.setSummary(summary);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return articleRepository.save(article);
    }

    // ============ DTO 반환 메서드들 (성능 개선) ============

    // 페이징 처리된 모든 기사 조회 (DTO 반환)
    public Page<ArticleListDTO> findAllDTO(Pageable pageable) {
        return articleRepository.findAllArticleList(pageable);
    }

    // 페이징 처리된 카테고리별 기사 조회 (DTO 반환)
    public Page<ArticleListDTO> findByCategoryDTO(String category, Pageable pageable) {
        return articleRepository.findArticleListByCategory(category, pageable);
    }

    // 인기 기사 조회 (조회수 기준, DTO 반환)
    public List<ArticleListDTO> findTrendingArticlesDTO() {
        Pageable pageable = PageRequest.of(0, 10);
        return articleRepository.findTop10DTOByOrderByViewCountDesc(pageable);
    }

    // 카테고리별 인기 기사 조회 (조회수 기준, DTO 반환)
    public List<ArticleListDTO> findTrendingArticlesByCategoryDTO(String category) {
        Pageable pageable = PageRequest.of(0, 10);
        return articleRepository.findTop10DTOByCategoryOrderByViewCountDesc(category, pageable);
    }

    // 추천 기사 조회 (DTO 반환)
    public Page<ArticleListDTO> findFeaturedArticlesDTO(Pageable pageable) {
        return articleRepository.findDTOByFeaturedTrue(pageable);
    }

    // 최신 기사 조회 (DTO 반환)
    public List<ArticleListDTO> findLatestArticlesDTO() {
        Pageable pageable = PageRequest.of(0, 10);
        return articleRepository.findTop10DTOByOrderByCreatedAtDesc(pageable);
    }

    // 카테고리별 최신 기사 조회 (DTO 반환)
    public List<ArticleListDTO> findLatestArticlesByCategoryDTO(String category) {
        Pageable pageable = PageRequest.of(0, 10);
        return articleRepository.findTop10DTOByCategoryOrderByCreatedAtDesc(category, pageable);
    }

    // 좋아요 기준 인기 기사 조회 (DTO 반환)
    public List<ArticleListDTO> findMostLikedArticlesDTO() {
        Pageable pageable = PageRequest.of(0, 10);
        return articleRepository.findTop10DTOByOrderByLikeCountDesc(pageable);
    }

    // 특정 기사 상세 조회 (DTO 반환, 조회수 증가 포함)
    public ArticleDetailDTO findByIdDTO(Long id) {
        Optional<Article> articleOpt = articleRepository.findById(id);
        if (articleOpt.isPresent()) {
            Article article = articleOpt.get();
            // 조회수 증가
            article.incrementViewCount();
            articleRepository.save(article);
            // DTO로 변환
            return ArticleMapper.toDetailDTO(article);
        } else {
            throw new RuntimeException("Article not found");
        }
    }

    // ============ 기존 엔티티 반환 메서드들 (하위 호환성 유지) ============

    // 페이징 처리된 모든 기사 조회
    public Page<Article> findAll(Pageable pageable) {
        return articleRepository.findAll(pageable);
    }

    // 페이징 처리된 카테고리별 기사 조회
    public Page<Article> findByCategory(String category, Pageable pageable) {
        return articleRepository.findByCategory(category, pageable);
    }

    // 인기 기사 조회 (조회수 기준)
    public List<Article> findTrendingArticles() {
        return articleRepository.findTop10ByOrderByViewCountDesc();
    }

    // 카테고리별 인기 기사 조회 (조회수 기준)
    public List<Article> findTrendingArticlesByCategory(String category) {
        return articleRepository.findTop10ByCategoryOrderByViewCountDesc(category);
    }

    // 추천 기사 조회
    public Page<Article> findFeaturedArticles(Pageable pageable) {
        return articleRepository.findByFeaturedTrue(pageable);
    }

    // 최신 기사 조회
    public List<Article> findLatestArticles() {
        return articleRepository.findTop10ByOrderByCreatedAtDesc();
    }

    // 카테고리별 최신 기사 조회
    public List<Article> findLatestArticlesByCategory(String category) {
        return articleRepository.findTop10ByCategoryOrderByCreatedAtDesc(category);
    }

    // 좋아요 기준 인기 기사 조회
    public List<Article> findMostLikedArticles() {
        return articleRepository.findTop10ByOrderByLikeCountDesc();
    }

    // 특정 기사 조회 (조회수 증가 포함)
    public Article findById(Long id) {
        Optional<Article> articleOpt = articleRepository.findById(id);
        if (articleOpt.isPresent()) {
            Article article = articleOpt.get();
            // 조회수 증가
            article.incrementViewCount();
            articleRepository.save(article);
            return article;
        } else {
            throw new RuntimeException("Article not found");
        }
    }

    // 특정 기사 조회 (조회수 증가 없음)
    public Article getArticleById(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found"));
    }

    // 기사 수정
    public Article updateArticle(Long id, Article article, MultipartFile imageFile) {
        Article existingArticle = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found"));

        // Update article fields
        existingArticle.setTitle(article.getTitle());
        existingArticle.setContent(article.getContent());
        existingArticle.setAuthor(article.getAuthor());
        existingArticle.setCategory(article.getCategory());
    
        // Update featured status if provided
        if (article.getFeatured() != null) {
            existingArticle.setFeatured(article.getFeatured());
        }

        // Handle image file if provided
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String imageUrl = fileService.uploadFile(imageFile);
                existingArticle.setImageUrl(imageUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 내용이 변경되었을 경우에만 요약 다시 생성

        String summary = summaryService.generateSummary(article.getContent());
        existingArticle.setSummary(summary);

        // Save the updated article
        return articleRepository.save(existingArticle);
    }

    // 기사 좋아요 증가
    public Article likeArticle(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found"));
        article.incrementLikeCount();
        return articleRepository.save(article);
    }

    // 기사 좋아요 취소
    public Article unlikeArticle(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found"));
        article.decrementLikeCount();
        return articleRepository.save(article);
    }

    // 기사 삭제
    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }

    @Transactional
    public Article setAsFeatured(Long id) {
        // 벌크 업데이트를 통해 모든 featured 기사를 한 번에 false로 설정
        articleRepository.updateAllFeaturedToFalse();

        // 새 featured 기사 설정
        Article newFeaturedArticle = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found"));
        newFeaturedArticle.setFeatured(true);
        return articleRepository.save(newFeaturedArticle);
    }
}