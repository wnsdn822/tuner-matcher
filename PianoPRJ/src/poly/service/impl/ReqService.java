package poly.service.impl;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import poly.dto.ReqDTO;
import poly.persistance.mapper.IReqMapper;
import poly.service.IReqService;

@Service("ReqService")
public class ReqService implements IReqService {

	@Resource(name = "ReqMapper")
	private IReqMapper reqMapper;

	@Override
	public int insertReq(ReqDTO rDTO) throws Exception {
		return reqMapper.insertReq(rDTO);
	}

	@Override
	public List<ReqDTO> getPublicReqList(String user_seq) throws Exception {
		return reqMapper.getPublicReqList(user_seq);
		
	}

	@Override
	public ReqDTO getReqDetail(String req_seq) throws Exception {
		ReqDTO rDTO =reqMapper.getReqDetail(req_seq);
		return rDTO;
	}

}
