package poly.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import poly.dto.DealDTO;
import poly.dto.PianoDTO;
import poly.dto.ReqDTO;
import poly.dto.RescheduleDTO;
import poly.dto.ReviewDTO;
import poly.dto.UserDTO;
import poly.service.IDealService;
import poly.service.IPianoService;
import poly.service.IReqService;
import poly.service.IReviewService;
import poly.service.IUserService;
import poly.service.IWeatherService;
import poly.util.CmmUtil;
import poly.util.Pagination;
import poly.util.SessionUtil;

@Controller
@RequestMapping(value = "deal/")
public class DealController {
	
	private Logger log = Logger.getLogger(this.getClass());
	
	@Resource(name = "ReqService")
	private IReqService reqService;

	@Resource(name="UserService")
	private IUserService userService;
	
	@Resource(name = "DealService")
	private IDealService dealService;
	
	@Resource(name = "PianoService")
	private IPianoService pianoService;
	
	@Resource(name="ReviewService")
	private IReviewService reviewService;
	
	@Resource(name = "WeatherService")
	IWeatherService weatherService;
	
	@RequestMapping(value = "PlaceBid")
	public String PlaceBid(HttpServletRequest request, ModelMap model, HttpSession session, @ModelAttribute DealDTO dDTO) throws Exception {
		String user_seq = (String)session.getAttribute("user_seq");
		
		if(SessionUtil.verify(session, "1", model)!=null) {
			model = SessionUtil.verify(session, "1", model);
			return "/redirect";
		}
		
		String deal_type = request.getParameter("deal_type");
		
		ReqDTO rDTO = reqService.getReqDetail(dDTO.getReq_seq());
		dDTO.setRequester_seq(rDTO.getUser_seq());
		dDTO.setDeal_state("0");
		dDTO.setTuner_seq(user_seq);
		
		int res = dealService.insertDeal(dDTO);
		String type = "";
		String url = "";
		if(deal_type.equals("0")) {
			type = "입찰";
			url = "/deal/TunerBidList.do";
		}else {
			type = "견적 등록";
			url = "/req/PrivateReqDetail.do?req_seq="+dDTO.getReq_seq();
		}
		
		String msg;
		if(res>0) {
			msg = type + "에 성공하였습니다";
		}else {
			msg = type + "에 실패했습니다";
		}
		model.addAttribute("msg", msg);
		model.addAttribute("url", url);
		return "/redirect";
		
		
	}
	
	//-----------조율사입찰---------------
	
	@RequestMapping(value = "TunerBidList")
	public String TunerBidList(HttpServletRequest request, ModelMap model, HttpSession session,
			@RequestParam(defaultValue = "1")int page) throws Exception{
		String user_seq = (String)session.getAttribute("user_seq");

		if(SessionUtil.verify(session, "1", model)!=null) {
			model = SessionUtil.verify(session, "1", model);
			return "/redirect";
		}
		
		// 페이징
		int listCnt = dealService.getBiddingListCnt(user_seq);
		Pagination pg = new Pagination(listCnt, page);
		
		int start = pg.getStartIndex() + 1;
		int end = pg.getStartIndex() + pg.getPageSize();
		model.addAttribute("pg", pg);
		
		
		List<DealDTO> dList = dealService.getBiddingList(user_seq, start, end);
		if(dList==null) {
			dList = new ArrayList<>();
		}
		
		model.addAttribute("dList", dList);
		
		return "/deal/TunerBidList";
	}
	
	@RequestMapping(value="BidDetail")
	public String BidDetail(HttpServletRequest request, ModelMap model, HttpSession session) throws Exception{
		
		if(SessionUtil.verify(session, "1", model)!=null) {
			model = SessionUtil.verify(session, "1", model);
			return "/redirect";
		}
		
		String back = request.getParameter("back");
		if(back!=null) {
			model.addAttribute("back", "/deal/TunerPastDeals.do");
		}
		String deal_seq = request.getParameter("deal_seq");
		DealDTO dDTO = dealService.getDealDetail(deal_seq);
		
		if (dDTO == null) {
			model.addAttribute("msg", "존재하지 않는 입찰입니다");
			model.addAttribute("url", "/index.do");
			return "/redirect";
		}
		ReqDTO rDTO = reqService.getReqDetail(dDTO.getReq_seq());
		PianoDTO pDTO = pianoService.getPianoEditInfo(rDTO.getPiano_seq(), null);
		model.addAttribute("dDTO",dDTO);
		model.addAttribute("rDTO",rDTO);
		model.addAttribute("pDTO",pDTO);
		
		return "/deal/BidDetail";
		
	}

	@RequestMapping(value="BidCancel")
	public String BidCancel(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model) throws Exception{
		if(SessionUtil.verify(session, "[12]", model)!=null) {
			model.addAttribute("msg", "비정상적인 접근입니다.");
			model.addAttribute("url", "/index.do");
			return "/redirect";
		}
		String deal_seq = request.getParameter("deal_seq");
		String req_seq = request.getParameter("req_seq");
		int res = dealService.bidCancel(deal_seq);
		String msg = "";
		String url = "";
		String what = "입찰 취소";
		String user_type = (String) session.getAttribute("user_type");
		if (user_type.equals("2")) {
			msg = "입착을 취소하였습니다.";
			url = "/deal/AdminDealDetail.do?deal_seq="+ deal_seq;
		} else {

			if (req_seq != null) {
				url = "/req/PrivateReqDetail.do?req_seq=" + req_seq;
				what = "견적 삭제";
			} else {
				url = "/deal/TunerBidList.do";
			}

			if (res > 0) {
				msg = what + "에 성공했습니다";
			} else {
				msg = what + "에 실패했습니다";
			}
		}
		model.addAttribute("msg", msg);
		
		model.addAttribute("url", url);
		
		return "/redirect";
	}
	
	// 조율사 조율현황
	@RequestMapping(value = "TunerDealList")
	public String TunerDealList(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model,
			@RequestParam(defaultValue = "1")int page)
			throws Exception {
		log.info(this.getClass().getName() + ".TunerDealList start");
		
		if (SessionUtil.verify(session, "1", model) != null) {
			model = SessionUtil.verify(session, "1", model);
			return "/redirect";
		}
		String user_seq = (String)session.getAttribute("user_seq");
		
		// 페이징
		int listCnt = dealService.getTunerDealListCnt(user_seq);
		Pagination pg = new Pagination(listCnt, page);
		
		int start = pg.getStartIndex() + 1;
		int end = pg.getStartIndex() + pg.getPageSize();
		model.addAttribute("pg", pg);
		
		List<DealDTO> dList = dealService.getTunerDealList(user_seq, start, end);
		if(dList==null) {
			dList = new ArrayList<DealDTO>();
		}
		
		
		model.addAttribute("dList", dList);
		
		log.info(this.getClass().getName() + ".TunerDealList end");
		return "/deal/TunerDealList";
	}
	
	
	//-------------조율사 과거내역------------
	
	@RequestMapping(value="TunerPastDeals")
	public String TunerPastDeals(HttpServletRequest request, HttpServletResponse response
	, HttpSession session, ModelMap model, @RequestParam(defaultValue = "1")int page) throws Exception{
		if(SessionUtil.verify(session, "1", model)!=null) {
			model = SessionUtil.verify(session, "1", model);
			return "/redirect";
		}
		String tuner_seq = (String)session.getAttribute("user_seq");
		
		// 페이징
		int listCnt = dealService.getPastDealsCnt(tuner_seq);
		Pagination pg = new Pagination(listCnt, page);

		int start = pg.getStartIndex() + 1;
		int end = pg.getStartIndex() + pg.getPageSize();
		model.addAttribute("pg", pg);
		
		
		
		List<DealDTO> dList = dealService.getPastDeals(tuner_seq, start, end);
		if(dList==null) {
			dList = new ArrayList<DealDTO>();
		}
		
		model.addAttribute("dList", dList);
		
		return "/deal/TunerPastDeals";
		
	}
	

	@RequestMapping(value = "TunerDealDetail")
	public String TunerDealDetail(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".TunerDealDetail start");

		if (SessionUtil.verify(session, "1", model) != null) {
			model = SessionUtil.verify(session, "1", model);
			return "/redirect";
		}
		
		String back = request.getParameter("back");
		log.info("back : " + back);
		String deal_seq = request.getParameter("deal_seq");
		DealDTO dDTO = dealService.getDealDetail(deal_seq);
		
		if (dDTO == null) {
			model.addAttribute("msg", "존재하지 않는 거래입니다");
			model.addAttribute("url", "/deal/TunerPastDeals.do");
			return "/redirect";
		}
		
		RescheduleDTO resDTO = null;
		if(dDTO.getDeal_state().equals("10")) {
			resDTO = dealService.getRescheduleInfo(deal_seq);
			model.addAttribute("resDTO", resDTO);
		}
		
		
		ReqDTO rDTO = reqService.getReqDetail(dDTO.getReq_seq());
		PianoDTO pDTO = pianoService.getPianoDetail(rDTO.getPiano_seq(), null);
		UserDTO uDTO = userService.getUserInfo(dDTO.getRequester_seq());
		ReviewDTO revDTO = reviewService.getDealReview(deal_seq);
		
		if(back!=null) {
			log.info("back is not null");
			if(back.equals("schedule")) {
				model.addAttribute("back", "/myPage/TunerSchedule.do");
			}else if(back.equals("main")){
				model.addAttribute("back", "/main.do");
			}else {
			model.addAttribute("back", "/deal/TunerPastDeals.do");
			}
		}
		
		
		model.addAttribute("revDTO", revDTO);
		model.addAttribute("uDTO", uDTO);
		model.addAttribute("dDTO", dDTO);
		model.addAttribute("rDTO", rDTO);
		model.addAttribute("pDTO", pDTO);
		
		
		log.info(this.getClass().getName() + ".TunerDealDetail end");
		return "/deal/TunerDealDetail";
	}
	
	@RequestMapping(value = "TunerDealCancel")
	public String TunerDealCancel(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".TunerDealCancel start");
		if (SessionUtil.verify(session, "1", model) != null) {
			model = SessionUtil.verify(session, "1", model);
			return "/redirect";
		}
		
		String user_seq = (String)session.getAttribute("user_seq");
		String deal_seq = request.getParameter("deal_seq");
		int user_type = 1;
		
		int res = dealService.dealCancel(deal_seq, user_seq, user_type);
		String url = "";
		String msg = "";
		if(res>0) {
			msg = "거래를 취소하였습니다";
			url="/deal/TunerDealList.do";
		}else {
			msg = "거래 취소에 실패했습니다";
			url="/deal/TunerDealDetail.do?deal_seq=" + deal_seq;
		}
		model.addAttribute("msg", msg);
		model.addAttribute("url", url);
		
		log.info(this.getClass().getName() + ".TunerDealCancel end");
		return "/redirect";
	}
	
	@RequestMapping(value = "TunerDealConfirm")
	public String TunerDealConfirm(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".TunerDealConfirm start");
		if (SessionUtil.verify(session, "1", model) != null) {
			model = SessionUtil.verify(session, "1", model);
			return "/redirect";
		}
		
		String user_seq = (String)session.getAttribute("user_seq");
		String deal_seq = request.getParameter("deal_seq");
		int user_type = 1;
		
		int res = dealService.dealConfirm(deal_seq, user_seq, user_type);
		String url = "";
		String msg = "";
		if(res>0) {
			msg = "거래를 완료하였습니다";
			url="/deal/TunerDealDetail.do?deal_seq=" + deal_seq;
		}else {
			msg = "거래 완료에 실패했습니다";
			url="/deal/TunerDealDetail.do?deal_seq=" + deal_seq;
		}
		model.addAttribute("msg", msg);
		model.addAttribute("url", url);
		
		log.info(this.getClass().getName() + ".TunerDealConfirm end");
		return "/redirect";
	}
	
	
	//----------------사용자---------------------
	
	//사용자 입찰
	@RequestMapping(value = "AuctionOff")
	public String AuctionOff(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".AuctionOff start");
		if (SessionUtil.verify(session, "0", model) != null) {
			model = SessionUtil.verify(session, "0", model);
			return "/redirect";
		}
		
		String user_seq = (String)session.getAttribute("user_seq");
		String deal_seq = request.getParameter("deal_seq");
		String req_seq = dealService.getDealDetail(deal_seq).getReq_seq();
		// 세션 사용자와 요청자 번호의 일치 여부 확인
		int res = 0;
		res = dealService.auctionOff(deal_seq, req_seq, user_seq);
		String url = "";
		String msg = "";
		if(res>0) {
			reqService.auctionOff(req_seq);
			msg = "채택에 성공했습니다";
			url="/deal/UserDealList.do";
		}else {
			msg = "채택에 실패했습니다";
			url="/deal/ReqBidDetail.do?deal_seq=" + deal_seq;
		}
		log.info(this.getClass().getName() + ".AuctionOff end");
		model.addAttribute("msg", msg);
		model.addAttribute("url", url);
		return "/redirect";
	}
	
	@RequestMapping(value = "UserDealList")
	public String UserDealList(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model,
			@RequestParam(defaultValue = "1") int page)
			throws Exception {
		log.info(this.getClass().getName() + ".UserDealList start");
		
		if (SessionUtil.verify(session, "0", model) != null) {
			model = SessionUtil.verify(session, "0", model);
			return "/redirect";
		}
		
		String user_seq = (String)session.getAttribute("user_seq");
		
		// 페이징
		int listCnt = dealService.getUserDealListCnt(user_seq);
		Pagination pg = new Pagination(listCnt, page);
		
		DealDTO dDTO = new DealDTO();
		dDTO.setStartIndex(pg.getStartIndex());
		dDTO.setCntPerPage(pg.getPageSize());
		dDTO.setRequester_seq(user_seq);
		
		List<DealDTO> dList = dealService.getUserDealList(dDTO);
		if(dList==null) {
			dList = new ArrayList<DealDTO>();
		}

		model.addAttribute("pg", pg);
		model.addAttribute("dList", dList);
		
		log.info(this.getClass().getName() + ".UserDealList end");
		return "/deal/UserDealList";
	}
	
	@RequestMapping(value = "UserDealDetail")
	public String UserDealDetail(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".UserDealDetail start");

		if (SessionUtil.verify(session, "0", model) != null) {
			model = SessionUtil.verify(session, "0", model);
			return "/redirect";
		}
		
		String deal_seq = request.getParameter("deal_seq");
		String user_seq = (String) session.getAttribute("user_seq");
		DealDTO dDTO = dealService.getDealDetail(deal_seq);
		
		if (dDTO == null) {
			model.addAttribute("msg", "존재하지 않는 거래입니다");
			model.addAttribute("url", "/deal/UserDealList.do");
			return "/redirect";
		}
		
		if(!user_seq.equals(dDTO.getRequester_seq())) {
			model.addAttribute("msg", "비정상적인 접근입니다.");
			model.addAttribute("url", "/index.do");
			return "/redirect";
		}
		
		RescheduleDTO resDTO = null;
		if(dDTO.getDeal_state().equals("10")) {
			resDTO = dealService.getRescheduleInfo(deal_seq);
			model.addAttribute("resDTO", resDTO);
		}
		
		ReqDTO rDTO = reqService.getReqDetail(dDTO.getReq_seq());
		PianoDTO pDTO = pianoService.getPianoDetail(rDTO.getPiano_seq(), null);
		UserDTO uDTO = userService.getUserInfo(dDTO.getTuner_seq());
		ReviewDTO revDTO = reviewService.getDealReview(deal_seq);
		
		String scrollDown = request.getParameter("scrollDown");
		model.addAttribute("scrollDown", scrollDown);
		
		model.addAttribute("revDTO", revDTO);
		model.addAttribute("uDTO", uDTO);
		model.addAttribute("dDTO", dDTO);
		model.addAttribute("rDTO", rDTO);
		model.addAttribute("pDTO", pDTO);
		
		
		log.info(this.getClass().getName() + ".UserDealDetail end");
		return "/deal/UserDealDetail";
	}
	
	@RequestMapping(value = "UserDealCancel")
	public String UserDealCancel(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".UserDealCancel start");
		if (SessionUtil.verify(session, "0", model) != null) {
			model = SessionUtil.verify(session, "0", model);
			return "/redirect";
		}
		
		String user_seq = (String)session.getAttribute("user_seq");
		String deal_seq = request.getParameter("deal_seq");
		int user_type = 0;
		
		int res = dealService.dealCancel(deal_seq, user_seq, user_type);
		String url = "";
		String msg = "";
		if(res>0) {
			msg = "거래를 취소하였습니다";
			url="/deal/UserDealList.do";
		}else {
			msg = "거래 취소에 실패했습니다";
			url="/deal/UserDealDetail.do?deal_seq=" + deal_seq;
		}
		model.addAttribute("msg", msg);
		model.addAttribute("url", url);
		
		log.info(this.getClass().getName() + ".UserDealCancel end");
		return "/redirect";
	}
	
	@RequestMapping(value = "UserDealConfirm")
	public String UserDealConfirm(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".UserDealConfirm start");
		if (SessionUtil.verify(session, "0", model) != null) {
			model = SessionUtil.verify(session, "0", model);
			return "/redirect";
		}
		
		String user_seq = (String)session.getAttribute("user_seq");
		String deal_seq = request.getParameter("deal_seq");
		int user_type = 0;
		
		int res = dealService.dealConfirm(deal_seq, user_seq, user_type);
		String url = "";
		String msg = "";
		if(res>0) {
			msg = "거래를 완료하였습니다. 리뷰 등록은 서비스 품질 향상에 도움이 됩니다.";
			url="/deal/UserDealDetail.do?deal_seq=" + deal_seq + "&scrollDown=1";
		}else {
			msg = "거래 완료에 실패했습니다";
			url="/deal/UserDealDetail.do?deal_seq=" + deal_seq;
		}
		model.addAttribute("msg", msg);
		model.addAttribute("url", url);
		
		log.info(this.getClass().getName() + ".UserDealConfirm end");
		return "/redirect";
	}
	
	// ----------------------------1:1---------------------------------
	
	// 견적 거절
	@RequestMapping(value = "DeclineDeal")
	public String DeclineDeal(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".DeclineDeal start");
		if (SessionUtil.verify(session, "0", model) != null) {
			model = SessionUtil.verify(session, "0", model);
			return "/redirect";
		}
		
		String user_seq = (String)session.getAttribute("user_seq");
		String deal_seq = request.getParameter("deal_seq");
		String req_seq = dealService.getDealDetail(deal_seq).getReq_seq();
		// 세션 사용자와 요청자 번호의 일치 여부 확인
		int res = 0;
		res = dealService.declineDeal(deal_seq, req_seq, user_seq);
		String url = "";
		String msg = "";
		if(res>0) {
			reqService.auctionOff(req_seq);
			msg = "거절하였습니다";
			url="/deal/UserDealList.do";
		}else {
			msg = "거절에 실패했습니다";
			url="/req/PrivateReqDetail.do?req_seq=" + req_seq;
		}
		log.info(this.getClass().getName() + ".DeclineDeal end");
		model.addAttribute("msg", msg);
		model.addAttribute("url", url);
		return "/redirect";
	}
	
	// --------------------일정변경-----------------------
	@RequestMapping(value = "Reschedule")
	public String Reschedule(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".Reschedule start");
		if (SessionUtil.verify(session, model) != null) {
			model = SessionUtil.verify(session, model);
			return "/redirect";
		}
		
		
		String user_seq = (String)session.getAttribute("user_seq");
		String user_type = (String)session.getAttribute("user_type");
		String deal_seq = request.getParameter("deal_seq");
		DealDTO dDTO = dealService.getDealDetail(deal_seq);
		
		// 정상적인 접근인지 확인
		if (!user_type.equals("2")) {
			if (user_type.equals("0") && user_seq.equals(dDTO.getRequester_seq())) {
				log.info("user_type : " + user_type);
				log.info("requester_seq : " + dDTO.getRequester_seq());
			} else if (user_type.equals("1") && user_seq.equals(dDTO.getTuner_seq())) {
				log.info("user_type : " + user_type);
				log.info("tuner_seq : " + dDTO.getTuner_seq());
			} else {
				model.addAttribute("url", "/index.do");
				model.addAttribute("msg", "비정상적인 접근입니다.");
				return "/redirect";
			}
		}
		
		
		ReqDTO rDTO = reqService.getReqDetail(dDTO.getReq_seq());
		PianoDTO pDTO = pianoService.getPianoDetail(rDTO.getPiano_seq(), null);
		
		Map<String, String> weatherMap = weatherService.getWeather(pDTO.getSgg_code());
		model.addAttribute("weatherMap", weatherMap);
		
		Map<String, List<String>> prefDates = reqService.parseDates(rDTO.getPref_date());
		model.addAttribute("rDTO", rDTO);
		model.addAttribute("prefDates", prefDates);
		model.addAttribute("deal_seq", deal_seq);
		model.addAttribute("prev_date", dDTO.getPossible_date());
		
		log.info(this.getClass().getName() + ".Reschedule end");
		return "/deal/RescheduleForm";
	}
	
	@RequestMapping(value = "RescheduleProc")
	public String RescheduleProc(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model, @ModelAttribute RescheduleDTO rDTO)
			throws Exception {
		log.info(this.getClass().getName() + ".RescheduleProc start");

		if (SessionUtil.verify(session, model) != null) {
			model = SessionUtil.verify(session, model);
			return "/redirect";
		}
		
		
		String user_seq = (String)session.getAttribute("user_seq");
		String user_type = (String)session.getAttribute("user_type");
		String deal_seq = request.getParameter("deal_seq");
		DealDTO dDTO = dealService.getDealDetail(deal_seq);
		
		
		
		// 정상적인 접근인지 확인
		int res = 0;
		String url = "";
		String msg = "";
		
		if(user_type.equals("2")) {
			res = dealService.updateDate(rDTO);
			url = "/deal/AdminDealDetail.do?deal_seq=" + deal_seq;
			if(res>0) {
				msg = "일시를 변경하였습니다.";
			}else {
				msg = "일시 변경에 실패했습니다";
			}
		}else {
			if (user_type.equals("0") && user_seq.equals(dDTO.getRequester_seq())) {
				log.info("user_type : " + user_type);
				log.info("requester_seq : " + dDTO.getRequester_seq());
			} else if (user_type.equals("1") && user_seq.equals(dDTO.getTuner_seq())) {
				log.info("user_type : " + user_type);
				log.info("tuner_seq : " + dDTO.getTuner_seq());
			} else {
				model.addAttribute("url", "/index.do");
				model.addAttribute("msg", "비정상적인 접근입니다.");
				return "/redirect";
			}
			
			rDTO.setRequester_seq(user_seq);
			rDTO.setRequester_type(user_type);
			
			res = 0;
			
			try {
				res = dealService.insertReschedule(rDTO);
			} catch (Exception e) {
				log.info(e.toString());
			}

			String userTypeName = user_type.equals("0") ? "User" : "Tuner"; 
			if(res>0) {
				dealService.setDealState(deal_seq, "10");
				msg = "일정 변경 요청을 하였습니다.";
				url= String.format("/deal/%sDealDetail.do?deal_seq=%s", userTypeName, deal_seq);
			}else {
				msg = "일정 변경 요청에 실패했습니다";
				url= String.format("/deal/%sDealDetail.do?deal_seq=%s", userTypeName, deal_seq);
			}
			
		}
		model.addAttribute("msg", msg);
		model.addAttribute("url", url);
		log.info(this.getClass().getName() + ".RescheduleProc end");
		return "/redirect";
		

	}
	
	// --------------------- 관리자 ----------------------
	
	@RequestMapping(value = "AdminDealDetail")
	public String AdminDealDetail(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".AdminDealDetail start");
		
		if (SessionUtil.verify(session, "2", model) != null) {
			model = SessionUtil.verify(session, "2", model);
			return "/redirect";
		}
		
		String deal_seq = request.getParameter("deal_seq");
		DealDTO dDTO = dealService.getDealDetail(deal_seq);
		String back = request.getParameter("back");
		model.addAttribute("url", "/deal/AdminDealList.do");
		if(CmmUtil.nvl(back).equals("ReviewList")) {
			model.addAttribute("back", "/review/ReviewList.do");
		}else if(CmmUtil.nvl(back).equals("TunerDetail")){
			String tuner_seq = request.getParameter("tuner_seq");
			model.addAttribute("back","/user/TunerDetail.do?tuner_seq=" + tuner_seq);
		}else if(CmmUtil.nvl(back).equals("deal")){
			model.addAttribute("back", "/deal/DealList.do");
		}else {
			model.addAttribute("back", "/deal/DealList.do");
		}
		
		if (dDTO == null) {
			model.addAttribute("msg", "존재하지 않는 거래입니다");
			return "/redirect";
		}

		ReqDTO rDTO = reqService.getReqDetail(dDTO.getReq_seq());
		PianoDTO pDTO = pianoService.getPianoDetail(rDTO.getPiano_seq(), null);
		UserDTO tDTO = userService.getUserInfo(dDTO.getTuner_seq());
		UserDTO uDTO = userService.getUserInfo(dDTO.getRequester_seq());
		ReviewDTO revDTO = reviewService.getDealReview(deal_seq);
		
		model.addAttribute("revDTO", revDTO);
		model.addAttribute("uDTO", uDTO);
		model.addAttribute("tDTO", tDTO);
		model.addAttribute("dDTO", dDTO);
		model.addAttribute("rDTO", rDTO);
		model.addAttribute("pDTO", pDTO);
		
		
		log.info(this.getClass().getName() + ".AdminDealDetail end");
		return "/deal/AdminDealDetail";
	}
	
	@RequestMapping(value = "DealList")
	public String DealList(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model,
			@RequestParam(defaultValue = "1") int page)
			throws Exception {
		log.info(this.getClass().getName() + ".DealList start");
		
		if (SessionUtil.verify(session, "2", model) != null) {
			model = SessionUtil.verify(session, "2", model);
			return "/redirect";
		}
		
		// 페이징
		int listCnt = dealService.getDealListCnt();
		Pagination pg = new Pagination(listCnt, page);

		int start = pg.getStartIndex() + 1;
		int end = pg.getStartIndex() + pg.getPageSize();
		model.addAttribute("pg", pg);
		
		List<DealDTO> dList = dealService.getDealList(start, end);
		if(dList==null) {
			dList = new ArrayList<DealDTO>();
		}

		model.addAttribute("pg", pg);
		model.addAttribute("dList", dList);
		
		log.info(this.getClass().getName() + ".DealList end");
		return "/deal/DealList";
	}

	@RequestMapping(value = "RescheduleResponse")
	public String RescheduleResponse(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".RescheduleResponse start");
		if (SessionUtil.verify(session, "[01]", model) != null) {
			model = SessionUtil.verify(session, "[01]", model);
			return "/redirect";
		}
		
		String user_seq = (String) session.getAttribute("user_seq");
		String user_type = (String) session.getAttribute("user_type");
		String userTypeName = user_type.equals("0") ? "User" : "Tuner";
		
		String resp = request.getParameter("resp");
		String respKor = resp.equals("1") ? "수락" : "거절";
		
		String deal_seq = request.getParameter("deal_seq");
		
		RescheduleDTO resDTO = dealService.getRescheduleInfo(deal_seq);
		
		if(resDTO==null) {
			log.info("invalid 1");
			model.addAttribute("msg", "비정상적인 접근입니다.");
			model.addAttribute("url", "/index.do");
			return "/redirect";
		}
		
		DealDTO dDTO = dealService.getDealDetail(deal_seq);
		
		if(resDTO.getRequester_type().equals(user_type)) {
			log.info("invalid 2");
			model.addAttribute("msg", "비정상적인 접근입니다.");
			model.addAttribute("url", "/index.do");
			return "/redirect";
		}
		
		if(user_type.equals("0")) {
			if(!dDTO.getRequester_seq().equals(user_seq)) {
				log.info("invalid 3");
				model.addAttribute("msg", "비정상적인 접근입니다.");
				model.addAttribute("url", "/index.do");
				return "/redirect";
			}
		}else {
			if(!dDTO.getTuner_seq().equals(user_seq)) {
				log.info("invalid 4");
				model.addAttribute("msg", "비정상적인 접근입니다.");
				model.addAttribute("url", "/index.do");
				return "/redirect";
			}
		}
		
		
		int res = 0;
		String msg = "";
		String url = String.format("/deal/%sDealDetail.do?deal_seq=%s", userTypeName, deal_seq);
		
		try {
			res = dealService.rescheduleRespond(resDTO, resp);
		} catch (Exception e) {
			log.info(e.toString());
		}
		
		if(res>0) {
			msg = "일정 변경 요청을 " +respKor + "하였습니다";
		}else {
			msg = "일정 변경 요청 " +respKor + "에 실패하였습니다";
		}
		
		model.addAttribute("msg", msg);
		model.addAttribute("url", url);
		
		
		log.info(this.getClass().getName() + ".RescheduleResponse end");
		return "/redirect";
	}
	
}
