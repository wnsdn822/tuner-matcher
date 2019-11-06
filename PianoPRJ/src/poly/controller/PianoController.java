package poly.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import poly.dto.PianoDTO;
import poly.service.IPianoService;
@RequestMapping(value="piano/")

@Controller
public class PianoController {

	private Logger log = Logger.getLogger(this.getClass());
	
	@Resource(name="PianoService")
	private IPianoService pianoService;
	
	
	@RequestMapping(value="AddPiano")
	public String AddPiano(HttpServletRequest request, ModelMap model, HttpSession session) {
		
		return "/piano/AddPiano";
	}
	
	@RequestMapping(value="DoAddPiano")
	public String DoAddPiano(HttpServletRequest request, ModelMap model, HttpSession session, @ModelAttribute PianoDTO pDTO) throws Exception{
		pDTO.setPiano_photo_dir("dummy");
		String user_seq = (String)session.getAttribute("user_seq");
		pDTO.setOwner_seq(user_seq);
		
		int res;
		res = pianoService.insertPiano(pDTO);
		String msg = "";
		String url = "/index.do";
		if(res>0) {
			msg = "피아노 등록에 성공했습니다";
		}else {
			msg = "피아노 등록에 실패했습니다";
		}
		model.addAttribute("msg", msg);
		model.addAttribute("url", url);
		
		return "/redirect";
		
		
	}
}