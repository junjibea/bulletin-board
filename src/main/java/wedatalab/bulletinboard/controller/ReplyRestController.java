package wedatalab.bulletinboard.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import wedatalab.bulletinboard.domain.Board;
import wedatalab.bulletinboard.domain.Chat;
import wedatalab.bulletinboard.domain.Gongji;
import wedatalab.bulletinboard.domain.Reply;
import wedatalab.bulletinboard.service.BoardService;
import wedatalab.bulletinboard.service.ReplyService;

@RequestMapping("/board/**")
@RestController
@RequiredArgsConstructor
public class ReplyRestController {
    
    private final ReplyService replyService;
    private final BoardService boardService;

    @PostMapping("/ajaxUpdateReply")
    public List<Reply> updateReply(Reply reply, Long boardId) {
        replyService.updateReply(reply);
        return replyService.getBoardAndReply(boardId);
    }

    @PostMapping("/ajaxUploadReply")
    public ArrayList<Object> uploadReply(Reply reply, Long boardId) {
        replyService.uploadReply(reply);
        ArrayList<Object> all_Data_List = new ArrayList<Object>(); // 전송할 모든 데이터 담을 리스트
        List<Reply> board_Reply_List = replyService.getBoardAndReply(boardId);
        ArrayList<HashMap<String,String>> orgFileName = boardService.getOrgFileNameList(boardId);
        ArrayList<HashMap<String,String>> storedFileName = boardService.getStoredFileNameList(boardId);
        List<Integer> file_no = boardService.getOrgFileNum(boardId);
        all_Data_List.add(board_Reply_List);
        all_Data_List.add(orgFileName);
        all_Data_List.add(storedFileName);
        all_Data_List.add(file_no);
        return all_Data_List;
    }

    @PostMapping("/ajaxUploadReplyReply")
    public ArrayList<Object> uploadReplyReply(Reply reply, Long boardId) {
        replyService.uploadReplyReply(reply);
        ArrayList<Object> all_Data_List = new ArrayList<Object>(); // 전송할 모든 데이터 담을 리스트
        List<Reply> board_Reply_List = replyService.getBoardAndReply(boardId);
        ArrayList<HashMap<String,String>> orgFileName = boardService.getOrgFileNameList(boardId);
        ArrayList<HashMap<String,String>> storedFileName = boardService.getStoredFileNameList(boardId);
        List<Integer> file_no = boardService.getOrgFileNum(boardId);
        all_Data_List.add(board_Reply_List);
        all_Data_List.add(orgFileName);
        all_Data_List.add(storedFileName);
        all_Data_List.add(file_no);
        return all_Data_List;
    }

    // 제목 검색
    @GetMapping("/ajaxSearchTitle")
    public List<Board> searchTitle(String searchWord) {
        return boardService.searchTitle(searchWord);
    }
    
    // 제목 검색
    @GetMapping("/ajaxSearchName")
    public List<Board> searchName(String searchWord) {
        return boardService.searchName(searchWord);
    }

    // 공지 등록
    @GetMapping("/ajaxInsertGongji")
    public ArrayList<Object> insertGongji(Gongji gongji, Chat chat) {
        // 이미 등록된 공지가 있으면 그 공지 내용을 바꾸기
        if(boardService.countGongji(gongji)>0) {
            boardService.updateGongji(gongji);
        }
        // 등록된 공지가 없으면 새로 공지 내용 등록하기
        else {
            boardService.insertGongji(gongji);
        }
        ArrayList<Object> gongji_Date_Chat_List = new ArrayList<Object>(); // 새로 등록한 공지 텍스트와 채팅 내용 담을 리스트
        String gongji_content = boardService.selectGongji(chat).getGongji_content();
        List<String> dateList = boardService.getAllMessageDate(chat);
        List<Chat> list = boardService.getMyMessage(chat); // 채팅 내용을 받아와 리스트에 넣기
        gongji_Date_Chat_List.add(gongji_content);
        gongji_Date_Chat_List.add(dateList);
        gongji_Date_Chat_List.add(list);
        return gongji_Date_Chat_List;
    }

    // 채팅 보내기
    @PostMapping("/ajaxUploadChat")
    public ArrayList<Object> uploadChat(Chat chat) {
        boardService.uploadChat(chat);
        ArrayList<Object> date_Chat_List = new ArrayList<Object>(); // 채팅 내용 담을 리스트
        List<String> dateList = boardService.getAllMessageDate(chat);
        List<Chat> chatList = boardService.getMyMessage(chat);
        date_Chat_List.add(dateList);
        date_Chat_List.add(chatList);
        return date_Chat_List;
    }

    @GetMapping("/ajaxGetView")
    public ArrayList<Object> viewBoard(Model model, Long boardId) {
        // 로그인 처리 나중에

        // 조회수 카운트
        boardService.viewCount(boardId);
        ArrayList<Object> all_Data_List = new ArrayList<Object>(); // 전송할 모든 데이터 담을 리스트
        // 메인 페이지에서 넘겨받은 boardId 와 글번호가 일치하는 게시글 데이터를 halo 모델 객체에 담기
        Board halo = boardService.getBoard(boardId);
        // view 페이지에 파일 띄우기
        ArrayList<HashMap<String,String>> orgFileName = boardService.getOrgFileNameList(boardId);
        ArrayList<HashMap<String,String>> storedFileName = boardService.getStoredFileNameList(boardId);
        List<Integer> file_no = boardService.getOrgFileNum(boardId);
        all_Data_List.add(halo);
        all_Data_List.add(orgFileName);
        all_Data_List.add(storedFileName);
        all_Data_List.add(file_no);
        return all_Data_List;

    }
   
}