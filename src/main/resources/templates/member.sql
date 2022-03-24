CREATE TABLE TBL_MEMBER
(
    member_no NUMBER NOT NULL auto_increment,
    memberId VARCHAR(50) NOT NULL,
    PW VARCHAR(5000) NOT NULL,
    PRIMARY KEY(memberId)
);