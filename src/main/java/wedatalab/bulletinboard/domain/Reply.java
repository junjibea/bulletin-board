package wedatalab.bulletinboard.domain; 

import java.sql.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reply {
	private Long boardId; // 댓글이 포함된 게시글 번호
	private int rno; // 댓글 번호
	@Getter
	private String replyContent; // 댓글 내용
	private String writer; // 댓글 쓴 사람
	private Long parentId; // 대댓글의 상위 댓글 번호
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Date regdate; // 댓글 쓴 날짜
}

// form 이 없어지고 ajax 가 생긴다