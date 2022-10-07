package wedatalab.bulletinboard.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Gongji {
    private Long gongji_no;
    private String sender;
    private String receiver;
    private String gongji_content;

}