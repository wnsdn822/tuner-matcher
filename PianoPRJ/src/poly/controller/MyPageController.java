package poly.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import poly.dto.SggDTO;
import poly.dto.TunerDTO;
import poly.dto.UserDTO;
import poly.service.ISggService;
import poly.service.IUserService;
import poly.util.CmmUtil;
import poly.util.EncryptUtil;

@Controller
@RequestMapping("/myPage")
public class MyPageController {

	private Logger log = Logger.getLogger(this.getClass());
	@Resource(name="UserService")
	IUserService userService;
	
	@Resource(name="SggService")
	ISggService sggService;
	
	@RequestMapping(value="MyInfo")
	public String MyInfo(HttpServletRequest request, ModelMap model, HttpSession session) throws Exception{
		String user_seq = (String)session.getAttribute("user_seq");
		String user_type = (String)session.getAttribute("user_type");
		
		UserDTO uDTO = userService.getUserInfo(user_seq);
		model.addAttribute("uDTO", uDTO);
		if(user_type.equals("1")) {
			
			TunerDTO tDTO = userService.getTunerInfo(user_seq);
			model.addAttribute("tDTO", tDTO);
			List<SggDTO> sggList = new ArrayList<>();
			Map<String, ArrayList<String>> sggGrouped = sggService.getTunerSgg(user_seq);
			log.info("sggGrouped : " + sggGrouped);
			model.addAttribute("sggGrouped", sggGrouped);
		}
		return "/myPage/MyInfo";
	}
	
	@RequestMapping(value="MyInfoEdit")
	public String MyInfoEdit(HttpServletRequest request, ModelMap model, HttpSession session) throws Exception{
		String action = "/myPage/MyInfoEditForm.do";
		String back = "/myPage/MyInfoEdit.do";
		String forWhat = "update";
		model.addAttribute("action", action);
		model.addAttribute("back", back);
		model.addAttribute("forWhat", forWhat);
		
		return "/myPage/CheckPw";
	}
	
	@RequestMapping(value="DeleteAccount")
	public String DeleteAccount(HttpServletRequest request, ModelMap model, HttpSession session) throws Exception{
		String action = "/myPage/DeleteAccountProc.do";
		String back = "/myPage/DeleteAccount.do";
		String forWhat = "delete";
		model.addAttribute("action", action);
		model.addAttribute("back", back);
		model.addAttribute("forWhat", forWhat);
		
		return "/myPage/CheckPw";
		
	}
	
	@RequestMapping(value="DeleteAccountProc")
	public String DeleteAccountProc(HttpServletRequest request, ModelMap model, HttpSession session) throws Exception{
		String password = request.getParameter("password");
		String user_seq = (String)session.getAttribute("user_seq");
		password = EncryptUtil.encHashSHA256(password);
		int res = userService.pwCheck(user_seq, password);
		if(res==0) {
			model.addAttribute("msg", "암호가 올바르지 않습니다");
			model.addAttribute("url", "/myPage/DeleteAccount.do");
			return "/redirect";
		}else {
			int delRes = userService.deleteUser(user_seq);
			if(delRes>0) {
				model.addAttribute("msg", "회원 탈퇴에 성공했습니다.");
				model.addAttribute("url", "/index.do");
				session.invalidate();
				return "/redirect";
			}else {
				model.addAttribute("msg", "회원 탈퇴에 실패했습니다.");
				model.addAttribute("url", "/index.do");
				return "/redirect";
			}
			
		}
			
		}
		
		
		
		
		
	
	@RequestMapping(value="MyInfoEditForm")
	public String MyInfoEditForm(HttpServletRequest request, ModelMap model, HttpSession session) throws Exception{
		String password = request.getParameter("password");
		String user_seq = (String)session.getAttribute("user_seq");
		password = EncryptUtil.encHashSHA256(password);
		int res = userService.pwCheck(user_seq, password);
		if(res==0) {
			model.addAttribute("msg", "암호가 올바르지 않습니다");
			model.addAttribute("url", "/myPage/MyInfoEdit.do");
			return "/redirect";
		}
		
		UserDTO uDTO = new UserDTO();
		TunerDTO tDTO = null;
		uDTO.setUser_seq((String)session.getAttribute("user_seq"));
		uDTO = userService.getUserEditInfo(uDTO);
		model.addAttribute("uDTO", uDTO);
		
		String user_type = (String)session.getAttribute("user_type");
		if(user_type.equals("1")) {
			tDTO = userService.getTunerEditInfo(user_seq);
			model.addAttribute("tDTO", tDTO);
			List<SggDTO> sList = new ArrayList<>();
			sList = sggService.getSido();
			log.info("got sggservice");

			model.addAttribute("sList", sList);
			
			Map<String, ArrayList<String>> sggDTOList = sggService.getTunerSggCode(user_seq);
			model.addAttribute("sggDTOList", sggDTOList);
			return "/myPage/TunerInfoEdit";
		}
		
		return "/myPage/UserInfoEdit";
		
		
		
		
		
	}
	
	
}
