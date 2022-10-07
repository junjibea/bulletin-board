package wedatalab.bulletinboard.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wedatalab.bulletinboard.domain.Board;
import wedatalab.bulletinboard.domain.Chat;
import wedatalab.bulletinboard.domain.Gongji;
import wedatalab.bulletinboard.domain.Member;
import wedatalab.bulletinboard.domain.Pagination;
import wedatalab.bulletinboard.mapper.BoardMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {
    private final BoardMapper boardMapper;

    Pagination pagination;
    Member member;
    Chat chat;
    
    // 회원가입
    public void signUp(Member member) {
        boardMapper.signUp(member);
    }
    
    // checkId
    public int countId(String memberId) {
        return boardMapper.countId(memberId);
    }

    // getMemberId
    public List<String> getMemberId() {
        return boardMapper.getMemberId();
    }

    // getMemberPW
    public String getMemberPW(String memberId) {
        return boardMapper.getMemberPW(memberId);
    }

    // getMemberSalt
    public String getMemberSalt(String memberId) {
        return boardMapper.getMemberSalt(memberId);
    }

    // loggedIn
    public void loggedIn(String memberId) {
        boardMapper.loggedIn(memberId);
    }

    // logout
    public void logout(String memberId) {
        boardMapper.logout(memberId);
    }

    // loggedIn
    public String getLoggedIn(String memberId) {
        return boardMapper.getLoggedIn(memberId);
    }
    
    // 게시글 수 반환
    public int boardCount() {
        return boardMapper.boardCount();
    }

    // main 에서 게시글 리스트 띄우기
    public List<Board> boardList(Pagination pagination) {
        return boardMapper.getList(pagination);
    }

    // view 에서 글번호가 일치하는 게시글의 모든 속성 띄우기
    public Board getBoard(Long boardId) {
        return boardMapper.getBoard(boardId);
    }

    // 넘기는 파라미터 여러 개니까 클래스로 받음
    @Transactional
    public void uploadBoard(Board board) {
        boardMapper.uploadBoard(board);
    }

    // 두개 이상의 쿼리를 하나의 커넥션으로 묶어 db에 전송
    // 에러 발생 시 자동으로 rollback
    @Transactional
    public void updateBoard(Board board) {
        boardMapper.updateBoard(board);
    }

    // 게시글 삭제
    @Transactional
    public void deleteBoard(Long boardId) {
        boardMapper.deleteBoard(boardId);
    }

    // 조회수
    public void viewCount(Long boardId) {
        boardMapper.viewCount(boardId);
    }

    // 게시글, 댓글 조회
    public List<Board> getBoardAndReply(Long boardId) {
        return boardMapper.getBoardAndReply(boardId);
    }

    // 검색한 단어와 제목이 일치하는 게시글 리스트 main 에 띄우기
    public List<Board> searchTitle(String searchWord) {
        return boardMapper.searchTitle(searchWord);
    }

    // 검색한 단어와 내용이 일치하는 게시글 리스트 main 에 띄우기
    public List<Board> searchName(String searchWord) {
        return boardMapper.searchName(searchWord);
    }

    // 파일 업로드
    public void uploadFile(HashMap<String,ArrayList<String>> commandMap) {
        boardMapper.uploadFile(commandMap);
    }

    // 사용자에게 보일 파일 이름 띄우기
    public ArrayList<HashMap<String,String>> getOrgFileName(Long file_no) {
        return boardMapper.getOrgFileName(file_no);
    }
    
    // 사용자에게 보일 파일 이름 리스트 띄우기
    public ArrayList<HashMap<String,String>> getOrgFileNameList(Long boardId) {
        return boardMapper.getOrgFileNameList(boardId);
    }

    // 파일 번호 띄우기
    public List<Integer> getOrgFileNum(Long boardId) {
        return boardMapper.getOrgFileNum(boardId);
    }
    
    // 파일 경로 가져오기
    public ArrayList<HashMap<String,String>> getFilePath(Long file_no) {
        return boardMapper.getFilePath(file_no);
    }

    // 서버,DB 에 저장한 파일 이름 띄우기
    public ArrayList<HashMap<String,String>> getStoredFileName(Long file_no) {
        return boardMapper.getStoredFileName(file_no);
    }
    
    // 서버,DB 에 저장한 파일 이름 띄우기
    public ArrayList<HashMap<String,String>> getStoredFileNameList(Long boardId) {
        return boardMapper.getStoredFileNameList(boardId);
    }

    // 파일 수정
    public void updateFile(HashMap<String, HashMap<String,String>> commandMap) {
        boardMapper.updateFile(commandMap);
    }
    
    // 파일 삭제
    public void deleteFile(Long boardId) {
        boardMapper.deleteFile(boardId);
    }

    // chat
    public void uploadChat(Chat chat) {
        boardMapper.uploadChat(chat);
    }

    // getSentTime
    public String getSentTime(Long chat_no) {
        return boardMapper.getSentTime(chat_no);
    }

    // CHAT 내가 보낸 메시지 내용, 보낸 시간, 보낸 일자 띄우기
    public List<Chat> getMyMessage(Chat chat) {
        return boardMapper.getMyMessage(chat);
    }

    // getMessageTime
    public List<String> getMessageTime(Chat chat) {
        return boardMapper.getMessageTime(chat);
    }

    // CHAT 내가 보낸 메시지 일자 띄우기
    // public List<String> getMessageDate(Chat chat) {
    //     return boardMapper.getMessageDate(chat);
    // }

    // getCounterPartMessage
    // public List<String> getCounterPartMessage(Chat chat) {
    //     return boardMapper.getCounterPartMessage(chat);
    // }

    // CHAT 주고받은 메시지 일자 띄우기
    public List<String> getAllMessageDate(Chat chat) {
        return boardMapper.getAllMessageDate(chat);
    }

    // CHAT 내가 받은 메시지 모아보기
    public List<Chat> getRecentMessage(Chat chat) {
        return boardMapper.getRecentMessage(chat);
    }

    // CHAT 내게 메시지를 보낸 사람 리스트
    public List<String> getAllSenders(String receiver) {
        return boardMapper.getAllSenders(receiver);
    }

    // chat
    public void insertGongji(Gongji gongji) {
        boardMapper.insertGongji(gongji);
    }

    // chat
    // public String selectGongji(Gongji gongji) {
    //     return boardMapper.selectGongji(gongji);
    // }

    // chat
    public Gongji selectGongji(Chat chat) {
        return boardMapper.selectGongji(chat);
    }

    // chat
    public int countGongji(Gongji gongji) {
        return boardMapper.countGongji(gongji);
    }

    // chat
    public void updateGongji(Gongji gongji) {
        boardMapper.updateGongji(gongji);
    }

}
