package wedatalab.bulletinboard.mapper;

// import java.util.HashMap;
import java.util.List;
import org.springframework.stereotype.Repository;
import wedatalab.bulletinboard.domain.Reply;

@Repository
public interface ReplyMapper {
    
    // 댓글 조회
    List<Reply> getReply(Long boardId);

    // 댓글 boardId 넘기기
    Reply getBoardId(Long boardId);

    // 댓글 작성
	void uploadReply(Reply reply);
    
    // 댓글 수정
	void updateReply(Reply reply);
    
    // 댓글 삭제
    void deleteReply(Long rno);

    // 대댓글 작성
    void uploadReplyReply(Reply reply);

    // 대댓글 조회
    List<Reply> getReplyReply(Long boardId);

    // 대댓글 수정
    void updateReplyReply(Reply reply);

    // 대댓글 삭제
    void deleteReplyReply(Long rno);

    // 게시글, 댓글 조회
    List<Reply> getBoardAndReply(Long boardId);

}