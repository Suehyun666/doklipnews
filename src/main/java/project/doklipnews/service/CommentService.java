package project.doklipnews.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.doklipnews.entity.Comment;
import project.doklipnews.repository.CommentRepository;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    // 특정 기사의 댓글 목록 조회
    public List<Comment> getCommentsByArticleId(Long articleId) {
        return commentRepository.findByArticleIdAndParentIdIsNullAndDeletedFalseOrderByCreatedAtAsc(articleId);
    }

    // 특정 댓글의 대댓글 목록 조회
    public List<Comment> getRepliesByCommentId(Long commentId) {
        return commentRepository.findByParentIdAndDeletedFalseOrderByCreatedAtAsc(commentId);
    }

    // 특정 기사의 댓글 수 조회
    public List<Comment> countCommentsByArticleId(Long articleId) {
        return commentRepository.findByArticleIdAndParentIdIsNullAndDeletedFalseOrderByCreatedAtAsc(articleId);
    }
}
