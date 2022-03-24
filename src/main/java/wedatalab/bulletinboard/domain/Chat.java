package wedatalab.bulletinboard.domain;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Chat {
    private Long chat_no;
    private String sender;
    private String receiver;
    private String chat_content;
    private Date chat_regdate;
    private String sliced_chat_regdate;
    private String sliced_chat_time;
}
