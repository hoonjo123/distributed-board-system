package joney.board.comment.controller;

import joney.board.comment.service.CommentService;
import joney.board.comment.service.CommentServiceV2;
import joney.board.comment.service.request.CommentCreateRequest;
import joney.board.comment.service.request.CommentCreateRequestV2;
import joney.board.comment.service.response.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentControllerV2 {

    private final CommentServiceV2 commentService;

    //조회
    @GetMapping("/v2/comments/{commentId}")
    public CommentResponse read(
            @PathVariable("commentId") Long commentId
    ) {
        return commentService.read(commentId);
    }

    //생성
    @PostMapping("/v2/comments")
    public CommentResponse create(@RequestBody CommentCreateRequestV2 request) {
        return commentService.create(request);
    }

    //삭제
    @DeleteMapping("/v2/comments/{commentId}")
    public void delete(@PathVariable("commentId") Long commentId) {
        commentService.delete(commentId);
    }

}
