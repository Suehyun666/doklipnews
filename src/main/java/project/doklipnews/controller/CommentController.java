package project.doklipnews.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import project.doklipnews.controller.dto.CommentDTO;
import project.doklipnews.entity.Article;
import project.doklipnews.entity.Comment;
import project.doklipnews.repository.ArticleRepository;
import project.doklipnews.repository.CommentRepository;
import project.doklipnews.service.CommentService;

@Controller
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CommentService commentService;

    // 댓글 작성
    @PostMapping("/articles/{articleId}/comments")
    public String createComment(@PathVariable Long articleId,
                                @ModelAttribute CommentDTO commentDTO,
                                RedirectAttributes redirectAttributes) {

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "기사를 찾을 수 없습니다."));

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(commentDTO.getPassword());

        Comment comment = Comment.builder()
                .article(article)
                .nickname(commentDTO.getNickname())
                .password(encodedPassword)
                .content(commentDTO.getContent())
                .build();

        commentRepository.save(comment);

        redirectAttributes.addFlashAttribute("message", "댓글이 등록되었습니다.");
        return "redirect:/articles/" + articleId;
    }

    // 댓글 삭제 (비밀번호 확인 후)
    @PostMapping("/articles/{articleId}/comments/{commentId}/delete")
    public String deleteComment(@PathVariable Long articleId,
                                @PathVariable Long commentId,
                                @RequestParam String password,
                                RedirectAttributes redirectAttributes) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."));

        // 비밀번호 검증
        if (!passwordEncoder.matches(password, comment.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "redirect:/articles/" + articleId;
        }

        // 소프트 삭제 (실제로 삭제하지 않고 deleted 플래그만 설정)
        comment.setDeleted(true);
        commentRepository.save(comment);

        redirectAttributes.addFlashAttribute("message", "댓글이 삭제되었습니다.");
        return "redirect:/articles/" + articleId;
    }

    // 대댓글 작성 (나중에 구현)
    @PostMapping("/articles/{articleId}/comments/{commentId}/replies")
    public String createReply(@PathVariable Long articleId,
                              @PathVariable Long commentId,
                              @ModelAttribute project.doklipnews.controller.dto.CommentDTO commentDTO,
                              RedirectAttributes redirectAttributes) {

        // 구현 예정
        redirectAttributes.addFlashAttribute("message", "현재 대댓글 기능은 개발 중입니다.");
        return "redirect:/articles/" + articleId;
    }
}
