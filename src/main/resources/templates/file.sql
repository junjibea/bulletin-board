CREATE TABLE TBL_FILE
(
    FILE_NO NUMBER auto_increment,          --파일 번호
    boardId NUMBER NOT NULL,                --게시판 번호
    ORG_FILE_NAME VARCHAR2(260) NOT NULL,   --원본 파일 이름
    STORED_FILE_NAME VARCHAR2(260) NOT NULL,--변경된 파일 이름
    FILE_PATH VARCHAR2 NOT NULL,            --저장된 파일 경로
    REGDATE DATE DEFAULT SYSDATE NOT NULL,  --파일등록일
    PRIMARY KEY(FILE_NO)                    --기본키 FILE_NO
);

-- alter table TBL_FILE add foreign key (boardId) references tbl_board(boardId)
-- on delete cascade;