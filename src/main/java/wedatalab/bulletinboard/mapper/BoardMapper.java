package wedatalab.bulletinboard.mapper;

import org.springframework.stereotype.Repository;

import wedatalab.bulletinboard.domain.Board;
import wedatalab.bulletinboard.domain.Chat;
import wedatalab.bulletinboard.domain.Gongji;
import wedatalab.bulletinboard.domain.Member;
import wedatalab.bulletinboard.domain.Pagination;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public interface BoardMapper {

    // 회원가입
    void signUp(Member member);
    
    // checkId
    int countId(String memberId);
    
    // getMemberId
    List<String> getMemberId();

    // getMemberPW
    String getMemberPW(String memberId);

    // getMemberSalt
    String getMemberSalt(String memberId);

    // loggedIn
    void loggedIn(String memberId);

    // logout
    void logout(String memberId);

    // loggedIn
    String getLoggedIn(String memberId);

    // 게시글 개수
    int boardCount(); // return 값이 int 가 나오는 메소드

    // main 에서 게시글 리스트 띄우기
    List<Board> getList(Pagination pagination);

    // 게시글 상세보기
    Board getBoard(Long boardId);

    // 게시글 업로드
    void uploadBoard(Board board);

    // 게시글 업데이트
    void updateBoard(Board board);

    // 게시글 삭제
    void deleteBoard(Long boardId);

    // 조회수
    void viewCount(Long boardId);

    // 게시글, 댓글 정보 모두 받아오기
    List<Board> getBoardAndReply(Long boardId);

    // 제목 검색
    List<Board> searchTitle(String searchWord);
    
    // 내용 검색
    List<Board> searchName(String searchWord);

    // 파일 업로드
    void uploadFile(HashMap<String,ArrayList<String>> commandMap);

    // 사용자에게 보일 파일 이름 띄우기
    ArrayList<HashMap<String,String>> getOrgFileName(Long file_no);
    
    // 사용자에게 보일 파일 이름 리스트 띄우기
    ArrayList<HashMap<String,String>> getOrgFileNameList(Long boardId);

    // 파일 번호 띄우기
    List<Integer> getOrgFileNum(Long boardId);
    
    // 파일 경로 가져오기
    ArrayList<HashMap<String,String>> getFilePath(Long file_no);

    // 서버,DB 에 저장한 파일 이름 띄우기
    ArrayList<HashMap<String,String>> getStoredFileName(Long file_no);
    
    // 서버,DB 에 저장한 파일 이름 리스트 띄우기
    ArrayList<HashMap<String,String>> getStoredFileNameList(Long boardId);

    // 파일 수정
    void updateFile(HashMap<String, HashMap<String,String>> commandMap);

    // 파일 삭제
    void deleteFile(Long boardId);
    
    // chat
    void uploadChat(Chat chat);

    // getSentTime
    String getSentTime(Long chat_no);

    // CHAT 내가 보낸 메시지 내용, 보낸 시간, 보낸 일자 띄우기
    List<Chat> getMyMessage(Chat chat);

    // getMessageTime
    List<String> getMessageTime(Chat chat);

    // CHAT 내가 보낸 메시지 일자 띄우기
    List<String> getMessageDate(Chat chat);

    // getCounterPartMessage
    // List<String> getCounterPartMessage(Chat chat);

    // CHAT 주고받은 메시지 일자 띄우기
    List<String> getAllMessageDate(Chat chat);

    // CHAT 내가 받은 메시지 모아보기
    List<Chat> getRecentMessage(Chat chat);

    // CHAT 내게 메시지를 보낸 사람 리스트
    List<String> getAllSenders(String receiver);

    // chat
    void insertGongji(Gongji gongji);

    // chat
    // String selectGongji(Gongji gongji);

    // chat
    Gongji selectGongji(Chat chat);

    // chat
    int countGongji(Gongji gongji);

    // chat
    void updateGongji(Gongji gongji);

}