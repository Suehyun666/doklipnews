package project.doklipnews.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import project.doklipnews.entity.Article;
import project.doklipnews.repository.ArticleRepository;

import java.util.List;

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

    // 특정 기사 조회
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

    // 기사 삭제
    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }

    public Article findById(Long id) {
        articleRepository.findById(id).orElseThrow(() -> new RuntimeException("Article not found"));
        return articleRepository.findById(id).get();
    }
}
