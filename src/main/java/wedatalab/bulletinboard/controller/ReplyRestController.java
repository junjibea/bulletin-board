package wedatalab.bulletinboard.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import wedatalab.bulletinboard.domain.Board;
import wedatalab.bulletinboard.domain.Reply;
import wedatalab.bulletinboard.service.BoardService;
import wedatalab.bulletinboard.service.ReplyService;

@RequestMapping("/board/**")
@RestController
@RequiredArgsConstructor
public class ReplyRestController {
    
    private final ReplyService replyService;
    private final BoardService boardService;

    @PostMapping("/ajaxUpdateReply")
    public List<Reply> updateReply(Reply reply, Long boardId) {
        replyService.updateReply(reply);
        return replyService.getBoardAndReply(boardId);
    }

    @PostMapping("/ajaxUploadReply")
    public List<Reply> uploadReply(Reply reply, Long boardId) {
        replyService.uploadReply(reply);
        return replyService.getBoardAndReply(boardId);
    }

    @PostMapping("/ajaxUploadReplyReply")
    public List<Reply> uploadReplyReply(Reply reply, Long boardId) {
        replyService.uploadReplyReply(reply);
        return replyService.getBoardAndReply(boardId);
    }

    // 제목 검색
    @GetMapping("/ajaxSearchTitle")
    public List<Board> searchTitle(String searchWord) {
        return boardService.searchTitle(searchWord);
    }
    
    // 제목 검색
    @GetMapping("/ajaxSearchName")
    public List<Board> searchName(String searchWord) {
        return boardService.searchName(searchWord);
    }
   
}
