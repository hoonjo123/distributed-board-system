package joney.board.comment.service.response;

import joney.board.comment.entity.Comment;
import joney.board.comment.entity.CommentV2;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class CommentResponse {
    private Long commentId;
    private String content;
    private Long parentCommentId;
    private Long articleId;
    private Long writerId;
    private Boolean deleted;
    private String path;
    private LocalDateTime createdAt;

    public static CommentResponse from(Comment comment){
        CommentResponse commentResponse = new CommentResponse();
        commentResponse.commentId = comment.getCommentId();
        commentResponse.content = comment.getContent();
        commentResponse.parentCommentId = comment.getParentCommentId();
        commentResponse.articleId = comment.getArticleId();
        commentResponse.writerId = comment.getWriterId();
        commentResponse.deleted = comment.getDeleted();
        commentResponse.createdAt = comment.getCreatedAt();
        return commentResponse;
    }

    public static CommentResponse from(CommentV2 comment) {
        CommentResponse response = new CommentResponse();
        response.commentId = comment.getCommentId();
        response.content = comment.getContent();
        response.path = comment.getCommentPath().getPath();
        response.articleId = comment.getArticleId();
        response.writerId = comment.getWriterId();
        response.deleted = comment.getDeleted();
        response.createdAt = comment.getCreatedAt();
        return response;
    }
}
