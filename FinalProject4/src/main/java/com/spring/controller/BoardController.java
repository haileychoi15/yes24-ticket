package com.spring.controller;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.spring.common.FileManager;
import com.spring.common.MyUtil;
import com.spring.model.FaqVO;
import com.spring.model.NoticeVO;
import com.spring.service.InterBoardService;

@Controller
public class BoardController {
	
	@Autowired
	private InterBoardService service;

	@Autowired
	private FileManager fileManager;
	
	// 고객센터 페이지로 이동
	@RequestMapping(value = "/qna.action", produces="text/plain;charset=UTF-8", method = RequestMethod.GET)
	public String qna() {
		
		return "qna/qna.tiles1";
	}
	
	// FAQ 
	@ResponseBody
	@RequestMapping(value = "/faq.action", produces="text/plain;charset=UTF-8")
//	@RequestMapping(value = "/faq.action", produces="text/plain;charset=UTF-8", method = RequestMethod.GET)
	public String qna(HttpServletRequest request) {
		
		String category = request.getParameter("category");
		String searchWord = request.getParameter("searchWord");
		
		System.out.println(category +"category");
		System.out.println(searchWord +"searchWord");
		
	// 	if(category == "0") {
		if(category.equals("0")) {
			category = "";
		}
		
		if(searchWord == null || searchWord.trim().isEmpty()) {
			searchWord = ""; 
		}
		
		HashMap<String, String> paraMap = new HashMap<>();
		paraMap.put("category", category);
		paraMap.put("searchWord", searchWord);
		
		
		List<FaqVO> faqList = service.faqList(paraMap);
		
		JSONArray jsonArr = new JSONArray();

		for(FaqVO faqvo : faqList) {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("category", faqvo.getFaq_cate_name());
			jsonObj.put("fk_userid", faqvo.getFk_userid());
			jsonObj.put("content", faqvo.getContent());
			jsonObj.put("subject", faqvo.getSubject());
			jsonObj.put("status", faqvo.getStatus());
			jsonObj.put("regDate", faqvo.getRegDate());


			jsonArr.put(jsonObj);
		}

		
		return jsonArr.toString();
		
		
	}
	
	
	// 고객센터 페이지로 이동
	@RequestMapping(value = "/noticeMain.action", produces="text/plain;charset=UTF-8")
	public ModelAndView noticeMain(HttpServletRequest request, ModelAndView mav) {
		
		String page = request.getParameter("page");
		/*
		int totalCount = service.getTotalNoticeCount(paraMap);
		
		int sizePerPage = 10;
		int totalPage = (int) Math.ceil((double)totalCount / sizePerPage);

		request.setAttribute("totalPage", totalPage);
		System.out.println(totalPage +":totalPage");*/
		
	//	return "notice/notice.tiles1";
		mav.addObject("page", page);
		mav.setViewName("notice/notice.tiles1");
		return mav;
	}
	
	// 공지사항 페이지로 이동
	@ResponseBody
	@RequestMapping(value="/notice.action", method= {RequestMethod.GET}, produces="text/plain;charset=UTF-8")
//	public ModelAndView notice(HttpServletRequest request, ModelAndView mav) {
	public String notice(HttpServletRequest request) {
		
		List<NoticeVO> noticeList = null; 

		String searchWord = request.getParameter("searchWord");
		String str_currentShowPageNo = request.getParameter("page");
		String category = request.getParameter("category");
	//	System.out.println("page : "+str_currentShowPageNo);
	//	System.out.println("searchWord : "+searchWord);
		
		// 검색어가 없을 때는(null) 검색어를 ""로 변환
		if(searchWord == null || searchWord.trim().isEmpty()) {
			searchWord = "";
		}
		
		String order = "";
		switch (category) {
		case "1":
			order = "regDate";
			break;
		case "2":
			order = "ticketopenday";
			break;
		case "3":
			order = "readCount";
			break;
		default:
			break;
		}

		HashMap<String, String> paraMap = new HashMap<>();
		paraMap.put("searchWord", searchWord);
		paraMap.put("order", order);


		int totalCount = 0; 		// 총 게시물 건수(totalCount)
		int sizePerPage = 10;		// 한 페이지당 보여줄 게시물 건수 (select 태그로 값을 선택할 수도 있지만, 이번에는 무조건 10개로 고정한다.)
		int currentShowPageNo = 1;	// 현재 보여주는 페이지 번호로서, 초기치로는 1페이지로 설정한다. (아무것도 주지 않으면 1페이지로 고정이다.)
		int totalPage = 0;			// 총 페이지수(웹브라우저 상에 보여줄 총 페이지 개수, 페이지바)
		int startRno = 0;			// 시작 행번호
		int endRno = 0;				// 끝 행번호

		// 총 게시물 건수(totalCount)
		totalCount = service.getTotalNoticeCount(paraMap);
		
		totalPage = (int) Math.ceil((double)totalCount / sizePerPage);

		// str_currentShowPageNo 가 없다면 초기화면을 보여준다.
		if(str_currentShowPageNo == null) {
			currentShowPageNo = 1;
		}
		else {
			try {
				currentShowPageNo = Integer.parseInt(str_currentShowPageNo);

				if(currentShowPageNo <= 0 || currentShowPageNo > totalPage) {
					currentShowPageNo = 1;
				}

			} catch (NumberFormatException e) {
				currentShowPageNo = 1;
			}
		}

		startRno = ((currentShowPageNo - 1 ) * sizePerPage) + 1;
		endRno = startRno + sizePerPage - 1; 


		paraMap.put("startRno", String.valueOf(startRno));
		paraMap.put("endRno", String.valueOf(endRno));

		// == 페이징 처리한 글목록 보여주기(검색이 있든지, 검색이 없든지 모두 다 포함한 것) == //
		noticeList = service.noticeListWithPaging(paraMap);
		
	//	if(!"".equals(searchWord)) {
	//		mav.addObject("paraMap", paraMap);
	//	}

		// === #119. 페이지바 만들기 === //
		String pageBar = "<ul style='list-style: none;'>";

		int blockSize = 10;

		int loop = 1;
		
		int pageNo = ((currentShowPageNo - 1)/blockSize) * blockSize + 1;

	//	mav.addObject("totalCount", totalCount);

	//	String gobackURL = MyUtil.getCurrentURL(request);
	//	System.out.println("~~~~~ 확인용 gobackURL : " + gobackURL);
	//	mav.addObject("gobackURL", gobackURL);

		HttpSession session = request.getSession();
		session.setAttribute("readCountPermission", "yes"); // 조회수증가권한의 값을 yes 로 세션에 저장한다.

		/*
		   session 에  "readCountPermission" 키값으로 저장된 value값은 "yes" 이다.
		   session 에  "readCountPermission" 키값에 해당하는 value값 "yes"를 얻으려면 
		      반드시 웹브라우저에서 주소창에 "/list.action" 이라고 입력해야만 얻어올 수 있다. 
		 */

	//	session.setAttribute("gobackURL", gobackURL);

		request.setAttribute("totalPage", totalPage);
		System.out.println(totalPage +":totalPage");
		
		JSONArray jsonArr = new JSONArray();

		for(NoticeVO notivo : noticeList) {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("notice_id", notivo.getNotice_id());
			jsonObj.put("category", notivo.getNo_cate_name());
			jsonObj.put("fk_userid", notivo.getFk_userid());
			jsonObj.put("ticketopenday", notivo.getTicketopenday());
			jsonObj.put("subject", notivo.getSubject());
			jsonObj.put("readCount", notivo.getReadCount());
			jsonObj.put("status", notivo.getStatus());
			jsonObj.put("regDate", notivo.getRegDate());
			jsonObj.put("fileName", notivo.getFileName());
			jsonObj.put("totalCount", totalCount);
			jsonObj.put("totalPage", totalPage);
			jsonObj.put("page", str_currentShowPageNo);

			jsonArr.put(jsonObj);
		}

		return jsonArr.toString();
	}
	
	
	// 공지사항 글 1개 보기 페이지로 이동
	@RequestMapping(value = "/noticeView.action", produces="text/plain;charset=UTF-8", method = RequestMethod.GET)
	public ModelAndView noticeView(ModelAndView mav, HttpServletRequest request) {
		
		String seq = request.getParameter("seq");
		
	//	String gobackURL = request.getParameter("gobackURL");
	//	mav.addObject("gobackURL", gobackURL);
		
		NoticeVO notivo = null;
		
		HttpSession session = request.getSession();
	
		/*
		// 위의 글목록보기 #69. 에서 session.setAttribute("readCountPermission", "yes"); 해두었다.
		if("yes".equals(session.getAttribute("readCountPermission"))) {
			// 글목록보기를 클릭한 다음에 특정글을 조회해온 경우이다. 

			notivo = service.getNoticeView(seq, userid); 
			// 글 조회수 증가와 함께 글 1개를 조회를 해주는 것
			// 서비스단에서는 글 내용을 select 하는 것과 조회수를 update 하는 것이 동시에 일어나야 한다.

			session.removeAttribute("readCountPermission"); 
			// 중요함!! session 에 저장된 readCountPermission 을 삭제한다.
		}
		else {
			// 웹브라우저에서 새로고침(F5) 을 클릭한 경우이다.
			// (글목록보기를 클릭을 안하고 특정글을 조회해온 경우)

			notivo = service.getNoticeViewWithNoAddCount(seq); 
			// 글 조회수 증가는 없고 단순히 글 1개 조회만을 해주는 것이다. 
			// 유저가 새로고침을 하는 경우에는 , DML 을 쏙 뺀 select 만 해주어야 한다. 
		}
		*/
		
		notivo = service.getNoticeViewWithNoAddCount(seq); 
		
		if(notivo == null) {
			// 존재하지 않는 글번호의 글내용을 조회하려는 경우
			String msg = "존재하지 않는 글번호입니다.";
			String loc = "javascript:history.back()";

			mav.addObject("msg", msg);
			mav.addObject("loc", loc);

			mav.setViewName("msg");

		}
		else {
		//	String gobackURL = (String) session.getAttribute("gobackURL");
		//	System.out.println("gobackURL : "+gobackURL);
		//	mav.addObject("gobackURL", gobackURL);
			mav.addObject("notivo", notivo); // 글 1개 boardvo 를 뷰단으로 넘겨준다. 
			mav.setViewName("notice/noticeView.tiles1");
		//	mav.addObject("gobackURL", gobackURL);
		}

		return mav;
	}
	
	
	
}
