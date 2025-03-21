package project.doklipnews.controller.dto;
import lombok.Data;

@Data
public class CommentDTO {
    private String nickname;
    private String password;
    private String content;
    private Long parentId; // 대댓글 작성 시 사용
}
