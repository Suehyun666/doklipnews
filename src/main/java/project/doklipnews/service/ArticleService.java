package project.doklipnews.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import project.doklipnews.entity.Article;
import project.doklipnews.repository.ArticleRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final FileService fileService;

    public ArticleService(ArticleRepository articleRepository, FileService fileService) {
        this.articleRepository = articleRepository;
        this.fileService = fileService;
    }

    // 새 기사 저장
    public Article createArticle(Article article, MultipartFile imageFile) {
        try {
            // 이미지 파일이 있으면 업로드하고 경로 저장
            if (imageFile != null && !imageFile.isEmpty()) {
                String imageUrl = fileService.uploadFile(imageFile);
                article.setImageUrl(imageUrl); // 이미지 URL 설정
            }
        } catch (Exception e) {
            e.printStackTrace(); // 예외 처리
        }

        return articleRepository.save(article);
    }

    // 전체 기사 목록 조회
    public List<Article> getArticlesByCategory(String category) {
        if (category != null) {
            return articleRepository.findByCategoryOrderByCreatedAtDesc(category);
        } else {
            return articleRepository.findAllByOrderByCreatedAtDesc();
        }
    }

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

    // 카테고리별 추천 기사 조회
    public Page<Article> findFeaturedArticlesByCategory(String category, Pageable pageable) {
        return articleRepository.findByCategoryAndFeaturedTrue(category, pageable);
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