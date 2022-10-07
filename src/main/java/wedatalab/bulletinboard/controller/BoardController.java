package wedatalab.bulletinboard.controller;

import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import wedatalab.bulletinboard.domain.Board;
import wedatalab.bulletinboard.domain.Chat;
import wedatalab.bulletinboard.domain.Gongji;
import wedatalab.bulletinboard.domain.Member;
import wedatalab.bulletinboard.domain.Pagination;
import wedatalab.bulletinboard.domain.Reply;
import wedatalab.bulletinboard.service.BoardService;
import wedatalab.bulletinboard.service.ReplyService;


@Controller

@RequestMapping("/board/**")

@RequiredArgsConstructor
public class BoardController {

    private final BoardService service;
    private final ReplyService replyService;

    // 로그인
    @GetMapping("/login")
    public String loginPage() {
        return "boards/login";
    }
    
    // 로그인
    @PostMapping("/login")
    public ModelAndView getLogin(HttpServletRequest request, Member member, String memberId, String PW, Model model) {
        if(service.countId(memberId)==0) {
            model.addAttribute("loginFailed", "존재하지 않는 아이디입니다.");
            ModelAndView MAV = new ModelAndView();
            MAV.setViewName("boards/login");
            return MAV;
        } else {
            String hex = null;
            try {
                String salt = service.getMemberSalt(memberId);
                String pw_with_salt = PW + salt;
                MessageDigest msg = MessageDigest.getInstance("SHA-512");
                msg.update(pw_with_salt.getBytes());
                hex = String.format("%128x", new BigInteger(1, msg.digest()));
                String realPW = service.getMemberPW(memberId); // 암호화 : orginal pw + salt
                if (realPW.equals(hex)) {
                    System.out.println("로그인 성공");
                    service.loggedIn(memberId);
                    HttpSession session = request.getSession();
                    session.setAttribute("memberId", memberId); // 세션값 설정
                    ModelAndView MAV = new ModelAndView();
                    MAV.setViewName("redirect:/board/main");
                    return MAV;
                } else {
                    model.addAttribute("wrongPW", "비밀번호가 틀렸습니다.");
                    ModelAndView MAV = new ModelAndView();
                    MAV.setViewName("boards/login");
                    return MAV;
                }
            } catch (Exception e) {
                ModelAndView MAV = new ModelAndView();
                return MAV;
            }
        }
    }

    // 비밀번호 찾기
    @GetMapping("/findpw")
    public String findpwForm() {
        return "boards/findPW";
    }

    @PostMapping("/findpw")
    public String findpw(Model model, String memberId) {
        model.addAttribute("foundpw", service.getMemberPW(memberId));
        return "boards/findPW";
    }

    // 회원가입
    @GetMapping("/join")
    public String SignUpForm() {
        return "boards/SignUp";
    }

    // 회원가입
    @PostMapping("/join")
    public String SignUp(HttpServletResponse response, Member member, Model model) {
        String hex = null;
        try {
            if(member.getMemberId().length()>0 && member.getPW().length()>0){
                UUID uuid = UUID.randomUUID();
                String salt = uuid.toString().substring(0,4);
                String pw_no_salt = member.getPW()+salt;
                MessageDigest msg = MessageDigest.getInstance("SHA-512");
                msg.update(pw_no_salt.getBytes());
                hex = String.format("%128x", new BigInteger(1, msg.digest()));
                member.setPW(hex);
                member.setSalt(salt);
                service.signUp(member);
                return "redirect:/board/login";
            }else{
                response.setContentType("text/html; charset=euc-kr");
                PrintWriter out = response.getWriter();
                out.println("<script>alert('아이디 또는 비밀번호를 입력하세요.'); </script>");
                out.flush();
                return "boards/SignUp";
            }
        } catch (Exception e) {
            model.addAttribute("onlyUnique", "중복된 아이디를 사용할 수 없습니다.");
            return "boards/SignUp";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String memberId = (String)session.getAttribute("memberId");
        session.removeAttribute(memberId);
        service.logout(memberId);
        return "redirect:/board/login";
    }

    // 게시글 목록
    @GetMapping("/main")
    public String main(Model model, HttpServletRequest request, HttpServletResponse response,
    @RequestParam(defaultValue = "1") int curPage, 
    @RequestParam(defaultValue = "1") int curRange) {
        // 로그인
        HttpSession session = request.getSession();
        String memberId = (String)session.getAttribute("memberId"); // 로그인 시 입력한 아이디 가져오기
        model.addAttribute("memberId", memberId);
        String receiver = memberId;

        model.addAttribute("sender", service.getAllSenders(receiver));
        String isLoggedIn = service.getLoggedIn(memberId);
        if(isLoggedIn.equals("Y")) {
            // 게시글 페이징 처리
            Pagination pagination = new Pagination(); // pagination 객체 생성
            // 전체 게시글 개수
            pagination.listCnt = service.boardCount();
            // 한 페이지당 게시글 수 main.html 에서 쓸 수 있도록 모델 객체에 담기
            model.addAttribute("pageSize", pagination.pageSize);
            // 총 페이지 수 계산해 main.html 에서 쓸 수 있도록 모델 객체에 담기
            pagination.pageCnt = (int) Math.ceil(pagination.listCnt*1.0/pagination.pageSize);
            model.addAttribute("pageCnt", pagination.pageCnt);
            // 마지막 페이지
            pagination.endPage = pagination.pageCnt;
            model.addAttribute("endPage", pagination.endPage);
            // 총 블럭(range) 수
            pagination.rangeCnt = (int) Math.ceil(pagination.pageCnt*1.0/pagination.rangeSize);
            // 마지막 블럭 main.html 에서 쓸 수 있도록 모델 객체에 담기
            pagination.endRange = pagination.rangeCnt;
            model.addAttribute("endRange", pagination.endRange);

            // 현재 페이지가 마지막 페이지보다 작거나 같을 때
            if(pagination.endPage >= curPage){
                if((curPage*1/10+1)==curRange*1) {
                    // 현재 페이지
                    pagination.curPage = curPage;
                    model.addAttribute("curPage", pagination.curPage);
                    // 전체 게시글 데이터를 list 라는 모델 객체에 담기
                    model.addAttribute("list", service.boardList(pagination));

                    // 현재 블럭
                    pagination.curRange = curRange;
                    model.addAttribute("curRange", pagination.curRange);
                    return "boards/main";
                } else {
                    try {
                        response.setContentType("text/html; charset=euc-kr");
                        PrintWriter out = response.getWriter();
                        out.println("<script>alert('잘못된 페이지입니다.'); location.href='/board/main';</script>");
                        out.flush();
                        return "redirect:/board/login"; // 상관 X
                    } catch (IOException e) {
                        return "redirect:/board/login"; // 상관 X
                    }
                }
            } else {
                // 현재 페이지가 마지막 페이지보다 클 때
                try {
                    response.setContentType("text/html; charset=euc-kr");
                    PrintWriter out = response.getWriter();
                    out.println("<script>alert('잘못된 페이지입니다.'); location.href='/board/main';</script>");
                    out.flush();
                    return "redirect:/board/login"; // 상관 X
                } catch (IOException e) {
                    return "redirect:/board/login"; // 상관 X
                }
            }
        } else {
            try {
                response.setContentType("text/html; charset=euc-kr");
                PrintWriter out = response.getWriter();
                out.println("<script>alert('로그인이 필요한 서비스입니다.'); </script>");
                out.flush();
                return "boards/login";
            } catch (IOException e) {
                return "redirect:/board/login";
            }
        }
    }

    // 채팅하기
    @GetMapping("/chat")
    public String chatForm(HttpServletRequest request, Model model, Chat chat, String receiver, String gongjiText, Gongji gongji) {
        // 로그인
        HttpSession session = request.getSession();
        String memberId = (String)session.getAttribute("memberId"); // 로그인 시 입력한 아이디 가져오기
        model.addAttribute("memberId", memberId);
        model.addAttribute("name", receiver);
        model.addAttribute("gongjiText", service.selectGongji(chat));
        // 전체 채팅 데이터를 chat_list 라는 모델 객체에 담기
        model.addAttribute("my_chat_list", service.getMyMessage(chat));
        model.addAttribute("all_chat_date", service.getAllMessageDate(chat));
        List<String> sliced_chat_content = new ArrayList<String>();
        model.addAttribute("chat_content", sliced_chat_content);
        return "boards/chat";
    }

    @GetMapping("/edit")
    public String edit() {
        return "boards/editor";
    }

    @PostMapping("/chat")
    public String chat(Model model, Chat chat) {
        model.addAttribute("name", chat.getReceiver());
        service.uploadChat(chat);
        return "redirect:/board/chat?name="+chat.getReceiver()+"&sender="+chat.getSender()+"&receiver="+chat.getReceiver();
    }

    @PostMapping("/gongji")
    public String gongji(HttpServletRequest request, HttpServletResponse response, Model model, Gongji gongji) throws UnsupportedEncodingException {
        // 이미 등록된 공지가 있으면 그 공지 내용을 바꾸기
        if(service.countGongji(gongji)>0) {
            service.updateGongji(gongji);
        }
        // 등록된 공지가 없으면 새로 공지 내용 등록하기
        else {
            service.insertGongji(gongji);
        }
        // url 에 한글이 포함되어 있으면 encoding 해야 함
        String encodedGongjiText = URLEncoder.encode(gongji.getGongji_content(), "UTF-8");
        return "redirect:/board/chat?name="+gongji.getReceiver()+"&sender="+gongji.getSender()+"&receiver="+gongji.getReceiver()+"&gongjiText="+encodedGongjiText;
    }

    @GetMapping("/chats")
    public String chats(Model model, HttpServletRequest request, String receiver, Chat senderListChat) {
        model.addAttribute("receiver",receiver);
         // 리스트인 경우 ',' 포함
        if(senderListChat.getSender().contains(",")) {
            String[] senderList = senderListChat.getSender().split(",");
            // sender 리스트 안 각각의 sender 들과 주고받은 최신 메시지 가져오기
            for (String sender : senderList) {
                Chat chat = new Chat(sender, senderListChat.getReceiver());
                model.addAttribute("getRecentMessage",service.getRecentMessage(chat));
            }
        } else {
            // 사용자와 채팅을 주고받은 사람이 한명인 경우
            Chat chat = new Chat(senderListChat.getSender(), senderListChat.getReceiver());
            model.addAttribute("getRecentMessage",service.getRecentMessage(chat));
        }
        return "boards/MyChat";
    }

    // 게시글 상세보기
    @GetMapping("/view")
    public String viewBoard(Model model, HttpServletRequest request, HttpServletResponse response, Long boardId) {
        // 로그인 되지 않았을 경우 url 로 접근할 수 없도록
        HttpSession session = request.getSession();
        String memberId = (String)session.getAttribute("memberId");
        model.addAttribute("memberId", memberId);
        String isLoggedIn = service.getLoggedIn(memberId);
        if(isLoggedIn.equals("Y")) {
            // 조회수 카운트
            service.viewCount(boardId);
            // 메인 페이지에서 넘겨받은 boardId 와 글번호가 일치하는 게시글 데이터를 halo 모델 객체에 담기
            model.addAttribute("halo", service.getBoard(boardId));
            // 댓글 데이터를 replyList 모델 객체에 담기
            model.addAttribute("replyList", replyService.getReply(boardId));
            // 대댓글 데이터를 replyReplyList 모델 객체에 담기
            model.addAttribute("replyReplyList", replyService.getReplyReply(boardId));
            // 게시글, 댓글 데이터 모두 담기
            model.addAttribute("getBoardAndReply", replyService.getBoardAndReply(boardId));
            // view 페이지에 파일 띄우기
            model.addAttribute("orgFileName", service.getOrgFileNameList(boardId));
            model.addAttribute("storedFileName", service.getStoredFileNameList(boardId));
            model.addAttribute("file_no", service.getOrgFileNum(boardId));
            return "boards/view";
        } else {
            try {
                response.setContentType("text/html; charset=euc-kr");
                PrintWriter out = response.getWriter();
                out.println("<script>alert('로그인이 필요한 서비스입니다.'); </script>");
                out.flush();
                return "boards/login";
            } catch (IOException e) {
                return "redirect:/board/login";
            }
        }
    }

    // [게시글 작성 1단계] 새로운 게시글을 작성하는 틀 보여줌
    @GetMapping("/upload")
    public String uploadBoardForm(Model model, HttpServletRequest request, HttpServletResponse response) {
        // 로그인 되지 않았을 경우 url 로 접근할 수 없도록
        HttpSession session = request.getSession();
        String memberId = (String)session.getAttribute("memberId"); // 로그인 시 입력한 아이디 가져오기
        model.addAttribute("memberId", memberId);
        String isLoggedIn = service.getLoggedIn(memberId);
        if(isLoggedIn.equals("Y")) {
            return "/boards/upload";
        } else {
            try {
                response.setContentType("text/html; charset=euc-kr");
                PrintWriter out = response.getWriter();
                out.println("<script>alert('로그인이 필요한 서비스입니다.'); </script>");
                out.flush();
                return "boards/login";
            } catch (IOException e) {
                return "redirect:/board/login";
            }
        }
    }

    // [게시글 작성 2단계] 게시글 작성 버튼 누르면 게시글 작성하는 메소드 실행
    @PostMapping("/upload")
    public String uploadBoard(MultipartHttpServletRequest mtfRequest, HttpServletResponse response, Board board) throws IOException {
        if((!board.getContent().isEmpty())&&(!board.getTitle().isEmpty())){
            service.uploadBoard(board);
            Path path = Paths.get(System.getProperty("user.dir")+"//pic");
            MultipartFile checkUpload = mtfRequest.getFile("file"); // 파일 없이 게시글 업로드 하는 경우 !checkUpload.isEmpty() == false
            if(!checkUpload.isEmpty()) {
                // 파일과 함께 게시글 업로드하는 경우
                List<MultipartFile> multipartFile = mtfRequest.getFiles("file"); // 첨부하는 파일 리스트
                for (MultipartFile file : multipartFile){ // 첨부하는 파일 리스트 개수대로 업로드 반복
                    String filename = file.getOriginalFilename();
                    // 파일 확장자
                    String extension = filename.substring(filename.lastIndexOf("."), filename.length());
                    UUID uuid = UUID.randomUUID();
                    String storedFilename = uuid.toString() + extension;
                    String uploadPath = path +"\\"+ storedFilename;
                    // 파일을 저장하기 위한 파일 객체 생성
                    File files = new File(uploadPath);
                    try {
                        // 파일 저장
                        file.transferTo(files);
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Long boardId = board.getBoardId();
                    ArrayList<String> fileList = new ArrayList<String>();
                    HashMap<String,ArrayList<String>> commandMap = new HashMap<String, ArrayList<String>>();
                    fileList.add(boardId.toString());
                    fileList.add(filename);
                    fileList.add(storedFilename);
                    fileList.add(uploadPath);
                    commandMap.put("fileList", fileList);
                    service.uploadFile(commandMap);
                }
            }
            return "redirect:/board/main";
        } else {
            try {
                response.setContentType("text/html; charset=euc-kr");
                PrintWriter out = response.getWriter();
                out.println("<script>alert('제목 혹은 내용을 입력해주세요.'); location.href='/board/upload';</script>");
                out.flush();
                return "boards/login"; // 상관 X
            } catch (IOException e) {
                return "redirect:/board/login"; // 상관 X
            }
        }
    }

	@GetMapping("/download")
	public void download(HttpServletResponse response, Long file_no) {
        // db에서 읽어온 파일 정보를 변수에 저장
        ArrayList<HashMap<String, String>> orgFileNameList = service.getOrgFileName(file_no); // [ORG_FILE_NAME=test.jpg] 형태
        ArrayList<HashMap<String, String>> storedFileNameList = service.getStoredFileName(file_no); // [STORED_FILE_NAME=08db1611-a33d-44a7-b596.jpg] 형태
        ArrayList<HashMap<String, String>> filePathList = service.getFilePath(file_no);
        for (String key : orgFileNameList.get(0).keySet()) { // [ORG_FILE_NAME=test.jpg] 에서 [ORG_FILE_NAME] 에 해당
            String orgFileName = service.getOrgFileName(file_no).get(0).get(key); // [ORG_FILE_NAME] 라는 키에 해당하는 value 반환
            // 제목이 한글로 된 파일 다운 받을 수 있도록 URLEncoder.encode 사용
            try {
                orgFileName = URLEncoder.encode(orgFileName, "UTF-8");
            } catch (Exception e) {}
            for (String storedFileKey : storedFileNameList.get(0).keySet()) { // [STORED_FILE_NAME=test.jpg] 에서 [STORED_FILE_NAME] 에 해당
                String contentType = "image/jpg";
                for (String filePath : filePathList.get(0).keySet()) {
                    String path = service.getFilePath(file_no).get(0).get(filePath);
                    File file = new File(path);
                    Long fileLength = file.length();
                    // 파일의 크기와 같지 않을 경우 프로그램이 멈추지 않고 계속 실행되거나, 잘못된 정보가 다운로드 될 수 있다.

                    response.setHeader("Content-Disposition", "attachment; filename=\"" + orgFileName + "\";"); // 원래 파일 제목 - 사용자에게 uuid 는 알아보기 어렵기 때문에 알아보기 쉬운 원래 파일 제목을 보여줌
                    response.setHeader("Content-Transfer-Encoding", "binary");
                    response.setHeader("Content-Type", contentType);
                    response.setHeader("Content-Length", "" + fileLength);
                    response.setHeader("Pragma", "no-cache;");
                    response.setHeader("Expires", "-1;");

                    try(
                        FileInputStream fis = new FileInputStream(path);
                        OutputStream out = response.getOutputStream();
                    ){
                        int readCount = 0;
                        byte[] buffer = new byte[1024];
                        while((readCount = fis.read(buffer)) != -1){
                            out.write(buffer,0,readCount);
                        }
                    }catch(Exception ex){
                        throw new RuntimeException("file Save Error");
                    }
                }
            }
        }
    }

    // [게시글 수정 1단계] 게시글을 수정하는 틀 보여줌
    @GetMapping("/update")
    public String updateBoardForm(Model model, Long boardId, String writer, HttpServletRequest request, HttpServletResponse response) {
        // 로그인 되지 않았을 경우 url 로 접근할 수 없도록
        HttpSession session = request.getSession();
        String memberId = (String)session.getAttribute("memberId"); // 로그인 시 입력한 아이디 가져오기
        // String writer 파라미터가 아니라 컨트롤러 내에서 메소드로 받아오는 방법으로 구현하기
        model.addAttribute("memberId", memberId);
        String isLoggedIn = service.getLoggedIn(memberId);
        System.out.println("writer : "+writer); // 파라미터로 받아오지 않기
        if(isLoggedIn.equals("Y")) {
            if(writer.equals(memberId)) {
                model.addAttribute("update", service.getBoard(boardId));
                // update 페이지에 기존 파일 띄우기 (게시글 수정 전에도 이미 첨부되어 있는 파일들)
                model.addAttribute("orgFileName", service.getOrgFileNameList(boardId));
                model.addAttribute("storedFileName", service.getStoredFileNameList(boardId));
                model.addAttribute("orgFileNum", service.getOrgFileNum(boardId));
                return "/boards/update";
            } else {
                try {
                    // getmapping - update
                    response.setContentType("text/html; charset=euc-kr");
                    PrintWriter out = response.getWriter();
                    out.println("<script>alert('수정 권한이 없습니다.'); location.href='/board/view?boardId="+boardId+"';</script>");
                    out.flush();
                    return "redirect:/board/view?boardId="+boardId;
                } catch (IOException e) {
                    return "boards/view";
                }
            }
        } else {
            try {
                response.setContentType("text/html; charset=euc-kr");
                PrintWriter out = response.getWriter();
                out.println("<script>alert('로그인이 필요한 서비스입니다.'); </script>");
                out.flush();
                return "boards/login";
            } catch (IOException e) {
                return "redirect:/board/login";
            }
        }
    }

    // [게시글 수정 2단계] 게시글 수정 버튼 누르면 게시글 수정하는 메소드 실행
    @PostMapping("/update")
    public String updateBoard(Board board, MultipartHttpServletRequest mtfRequest, HttpServletResponse response, Long boardId) throws IOException {
        if((!board.getContent().isEmpty())&&(!board.getTitle().isEmpty())){
            service.updateBoard(board);
            MultipartFile checkUpload = mtfRequest.getFile("file"); // 새로 파일을 업로드 하는지 확인하기 위한 객체
            // 파일 없이 게시글 업로드 하는 경우 !checkUpload.isEmpty() == false -> 아래 코드 전체 실행하지 않음
            if(!checkUpload.isEmpty()){
                // 수정 페이지에서 파일이 새로 첨부된 경우
                List<MultipartFile> multipartFile = mtfRequest.getFiles("file"); // 첨부된 파일 리스트
                ArrayList<HashMap<String,String>> fileMaps = service.getStoredFileNameList(boardId); // hashmap 으로 이루어진 파일들 리스트
                if(!fileMaps.isEmpty()){
                    // 기존에 첨부된 파일이 있는 경우
                    // 기존 파일 삭제 시작
                    int fileCnt = fileMaps.size(); // 기존 파일 개수
                    for(int i=0; i<fileCnt; i++) { // 기존 파일 개수만큼 아래 삭제 메소드 반복
                        for ( String key : fileMaps.get(i).keySet() ) { // 파일 map 의 키 (stored_file_name)
                            String storedFileName = service.getStoredFileNameList(boardId).get(i).get(key);// 키에 해당하는 값 (서버에 저장된 파일 이름 - 확장자 포함)
                            Path path = Paths.get(System.getProperty("user.dir")+"//pic");
                            String String_path = path + "\\" + storedFileName;
                            File files = new File(String_path);
                            if(files.exists()) { // 파일이 존재하면
                                files.delete(); // 파일 삭제
                            }
                        }
                    }
                    service.deleteFile(boardId); // H2 데이터베이스에서 해당 게시글에 딸린 기존 파일 삭제
                    // 기존 파일 삭제 끝, 새 파일 업로드 시작
                    for (MultipartFile file : multipartFile){
                        Path path = Paths.get(System.getProperty("user.dir")+"//pic");
                        String filename = file.getOriginalFilename();
                        // 파일 확장자
                        String extension = filename.substring(filename.lastIndexOf("."), filename.length());
                        UUID uuid = UUID.randomUUID();
                        String storedFilename = uuid.toString() + extension;
                        String uploadPath = path  +"\\" + storedFilename;
                        File files = new File(uploadPath);
                        try {
                            // 파일 저장
                            file.transferTo(files);
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        ArrayList<String> fileList = new ArrayList<String>();
                        fileList.add(boardId.toString());
                        fileList.add(filename);
                        fileList.add(storedFilename);
                        fileList.add(uploadPath);
                        HashMap<String,ArrayList<String>> commandMap = new HashMap<String, ArrayList<String>>();
                        commandMap.put("fileList", fileList);
                        service.uploadFile(commandMap);
                    } // 새 파일 업로드 끝
                } else {
                    // 기존에 첨부된 파일이 없는데 새로 추가하는 경우
                    for (MultipartFile file : multipartFile){
                        Path path = Paths.get(System.getProperty("user.dir")+"//pic");  
                        String filename = file.getOriginalFilename();
                        // 파일 확장자
                        String extension = filename.substring(filename.lastIndexOf("."), filename.length());
                        UUID uuid = UUID.randomUUID();
                        String storedFilename = uuid.toString() + extension;
                        String uploadPath = path +"\\"+ storedFilename;
                        File files = new File(uploadPath);
                        try {
                            // 파일 저장
                            file.transferTo(files);
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        ArrayList<String> fileList = new ArrayList<String>();
                        fileList.add(boardId.toString());
                        fileList.add(filename);
                        fileList.add(storedFilename);
                        fileList.add(uploadPath);
                        HashMap<String,ArrayList<String>> commandMap = new HashMap<String, ArrayList<String>>();
                        commandMap.put("fileList", fileList);
                        service.uploadFile(commandMap);
                    }
                }
            }
            return "redirect:/board/main";
        } else {
            try {
                response.setContentType("text/html; charset=euc-kr");
                PrintWriter out = response.getWriter();
                String writer = board.getName();
                out.println("<script>alert('제목 혹은 내용을 입력해주세요.'); location.href='/board/update?boardId="+boardId+"&writer="+writer+"';</script>");
                out.flush();
                return "boards/login"; // 상관 X
            } catch (IOException e) {
                System.out.println("try 에러");
                return "redirect:/board/login"; // 상관 X
            }
        }

    }

    // 게시글 삭제하기
    @GetMapping("/delete")
    public String deleteBoard(Long boardId, String writer, Model model, HttpServletRequest request, HttpServletResponse response) {
        // 로그인 되지 않았을 경우 url 로 접근할 수 없도록
        HttpSession session = request.getSession();
        String memberId = (String)session.getAttribute("memberId"); // 로그인 시 입력한 아이디 가져오기
        model.addAttribute("memberId", memberId);
        String isLoggedIn = service.getLoggedIn(memberId);
        if(isLoggedIn.equals("Y")) {
            if(writer.equals(memberId)) {
                service.deleteBoard(boardId);
                ArrayList<HashMap<String,String>> fileMaps = service.getStoredFileNameList(boardId); // hashmap 으로 이루어진 파일들 리스트
                int fileCnt = fileMaps.size(); // 파일 개수
                for(int i=0; i<fileCnt; i++) { // 파일 개수만큼 반복
                    for ( String key : fileMaps.get(i).keySet() ) { // 파일 map 의 키 (stored_file_name)
                        String storedFileName = service.getStoredFileNameList(boardId).get(i).get(key); // 키에 해당하는 값 (서버에 저장된 파일 이름 - 확장자 포함)
                        Path path = Paths.get(System.getProperty("user.dir")+"//pic");
                        String String_path = path + "\\" + storedFileName;
                        File files = new File(String_path);
                        if(files.exists()) { // 파일이 존재하면
                            files.delete(); // 파일 삭제
                        }
                    }
                }
                service.deleteFile(boardId);
                return "redirect:/board/main";
            } else {
                try {
                    response.setContentType("text/html; charset=euc-kr");
                    PrintWriter out = response.getWriter();
                    out.println("<script>alert('삭제 권한이 없습니다.'); location.href='/board/view?boardId="+boardId+"';</script>");
                    out.flush();
                    return "redirect:/board/view?boardId="+boardId;
                } catch (IOException e) {
                    return "boards/view";
                }
            }
        } else {
            try {
                response.setContentType("text/html; charset=euc-kr");
                PrintWriter out = response.getWriter();
                out.println("<script>alert('로그인이 필요한 서비스입니다.'); </script>");
                out.flush();
                return "boards/login";
            } catch (IOException e) {
                return "redirect:/board/login";
            }
        }
    }

    // 댓글 작성하기
    @PostMapping("/view")
    public String uploadReply(Long boardId, Reply reply) {
        replyService.uploadReply(reply);

        return "redirect:/board/view?boardId="+boardId;
    }

    // 댓글 삭제하기
    @GetMapping("/deleteReply")
    public String deleteReply(Long boardId, Long rno, String writer, Model model, HttpServletRequest request, HttpServletResponse response) {
        // 로그인 되지 않았을 경우 url 로 접근할 수 없도록
        HttpSession session = request.getSession();
        String memberId = (String)session.getAttribute("memberId"); // 로그인 시 입력한 아이디 가져오기
        model.addAttribute("memberId", memberId);
        String isLoggedIn = service.getLoggedIn(memberId);
        if(isLoggedIn.equals("Y")) {
            if(writer.equals(memberId)) {
                replyService.deleteReply(rno);
                return "redirect:/board/view?boardId="+boardId;
            }else {
                try {
                    response.setContentType("text/html; charset=euc-kr");
                    PrintWriter out = response.getWriter();
                    out.println("<script>alert('삭제 권한이 없습니다.'); location.href='/board/view?boardId="+boardId+"';</script>");
                    out.flush();
                    return "redirect:/board/view?boardId="+boardId; // 상관 X
                } catch (IOException e) {
                    return "boards/view"; // 상관 X
                }
            }
        } else {
            try {
                response.setContentType("text/html; charset=euc-kr");
                PrintWriter out = response.getWriter();
                out.println("<script>alert('로그인이 필요한 서비스입니다.'); </script>");
                out.flush();
                return "boards/login";
            } catch (IOException e) {
                return "redirect:/board/login";
            }
        }
    }

    // 대댓글 작성하기
    @PostMapping("/uploadReplyReply")
    public String uploadReplyReply(Long boardId, Reply reply) {
        replyService.uploadReplyReply(reply);
        return "redirect:/board/view?boardId="+boardId;
    }
    
}