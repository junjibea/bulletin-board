package wedatalab.bulletinboard.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Member {
    private String memberId;
    private String PW;
    private Long member_no;
    private String loggedIn;
    private String salt;
}
