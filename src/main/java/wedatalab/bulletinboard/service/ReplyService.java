package wedatalab.bulletinboard.service;


import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import wedatalab.bulletinboard.domain.Reply;
import wedatalab.bulletinboard.mapper.ReplyMapper;

@Service
@RequiredArgsConstructor
public class ReplyService {
    private final ReplyMapper replyMapper;

    // 댓글 리스트 가져오기
    // 게시글 상세보기 페이지에서 댓글을 보여주기 위해 사용
    public List<Reply> getReply(Long boardId) {
        return replyMapper.getReply(boardId);
    }

    // 댓글 boardId 넘기기
    // 댓글 작성 버튼을 눌러 db에 넣은 후 
    // 게시글 상세보기 화면으로 새로고침을 할 때 boardId 를 넘겨주기 위해 사용
    public Reply getBoardId(Long boardId) {
        return replyMapper.getBoardId(boardId);
    }

    // 댓글 업로드
    public void uploadReply(Reply reply) {
        replyMapper.uploadReply(reply);
    }

    // 댓글 업데이트
    public void updateReply(Reply reply) {
        // final HashMap<String, String> resources = new HashMap<String, String>();
        // {{
        //     resources.put("replyContent", replyContent);
        //     resources.put("boardId", Long.toString(boardId));
        //     resources.put("rno", Long.toString(rno));
        //     resources.put("writer", writer);
        // }};
        replyMapper.updateReply(reply);
    }

    // 댓글 삭제
    public void deleteReply(Long rno) {
        replyMapper.deleteReply(rno);
    }

    // 대댓글 작성
    public void uploadReplyReply(Reply reply) {
        replyMapper.uploadReplyReply(reply);
    }

    // 대댓글 조회
    public List<Reply> getReplyReply(Long boardId) {
        return replyMapper.getReplyReply(boardId);
    }

    // 대댓글 업데이트
    public void updateReplyReply(Reply reply) {
        replyMapper.updateReplyReply(reply);
    }

    // 대댓글 삭제
    public void deleteReplyReply(Long rno) {
        replyMapper.deleteReplyReply(rno);
    }

    // 게시글, 댓글 조회
    public List<Reply> getBoardAndReply(Long boardId) {
        return replyMapper.getBoardAndReply(boardId);
    }

}