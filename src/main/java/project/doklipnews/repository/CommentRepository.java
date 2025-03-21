package project.doklipnews.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.doklipnews.entity.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 특정 기사의 최상위 댓글 목록 조회 (삭제되지 않은 댓글만) - 오래된 순으로 정렬
    @Query("SELECT c FROM Comment c WHERE c.article.id = :articleId AND c.parentId IS NULL AND c.deleted = false ORDER BY c.createdAt ASC")
    List<Comment> findByArticleIdAndParentIdIsNullAndDeletedFalseOrderByCreatedAtAsc(@Param("articleId") Long articleId);

    // 기존 메소드도 하위 호환성을 위해 유지 (필요한 경우)
    @Query("SELECT c FROM Comment c WHERE c.article.id = :articleId AND c.parentId IS NULL AND c.deleted = false ORDER BY c.createdAt DESC")
    List<Comment> findByArticleIdAndParentIdIsNullAndDeletedFalseOrderByCreatedAtDesc(@Param("articleId") Long articleId);

    // 특정 댓글의 대댓글 목록 조회 (삭제되지 않은 댓글만)
    @Query("SELECT c FROM Comment c WHERE c.parentId = :parentId AND c.deleted = false ORDER BY c.createdAt ASC")
    List<Comment> findByParentIdAndDeletedFalseOrderByCreatedAtAsc(@Param("parentId") Long parentId);

    // 특정 기사의 모든 댓글 수 조회 (삭제되지 않은 댓글만)
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.article.id = :articleId AND c.deleted = false")
    long countByArticleIdAndDeletedFalse(@Param("articleId") Long articleId);

}
