CREATE TABLE tbl_board(
boardId Long auto_increment,
title varchar (10000) not null,
replyContent varchar (10000) not null,
name varchar (10000) not null,
read varchar (10000) not null default 0,
primary key(boardId)
);