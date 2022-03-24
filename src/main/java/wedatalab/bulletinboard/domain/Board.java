package wedatalab.bulletinboard.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Board {
    private Long boardId;
    private String title;
    private String content;
    private String name;
    private LocalDateTime createDate;
    private int read;
    private String searchWord;
    private int searched_pageSize;
    
    private String org_file_name;
    private String stored_file_name;

    private int rno; // 댓글 번호
	private String replyContent; // 댓글 내용
	private String writer; // 댓글 쓴 사람
	private Long parentId; // 대댓글의 상위 댓글 번호
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Date regdate; // 댓글 쓴 날짜
}