create table tbl_reply (
    boardId Long,
    rno Long not null auto_increment,
    content varchar2(10000) not null,
    writer varchar2(10000) not null,
    regdate date default sysdate,
    parentId Long default null,
    primary key(boardId, rno) 
    );

alter table tbl_reply add foreign key (boardId) references tbl_board(boardId)
on delete cascade;

alter table tbl_reply as o add foreign key (parentId) references tbl_reply as i(rno)
on delete cascade;

create sequence tbl_reply_seq START WITH 1 MINVALUE 0;