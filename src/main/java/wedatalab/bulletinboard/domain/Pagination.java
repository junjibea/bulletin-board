package wedatalab.bulletinboard.domain;

public class Pagination {
        public Long boardId;
        
        // 한 페이지당 게시글 수
        public int pageSize = 10;
    
        // 한 블럭(range)당 페이지 수
        public int rangeSize = 10;
        
        // 현재 페이지
        public int curPage;
         
        // 현재 블럭(range)
        public int curRange;
         
        // 총 게시글 수
        public int listCnt;
         
        // 총 페이지 수
        public int pageCnt;
         
        // 총 블럭(range) 수
        public int rangeCnt;
         
        // 끝 페이지
        public int endPage;

        // 마지막 블럭(range)
        public int endRange;
}