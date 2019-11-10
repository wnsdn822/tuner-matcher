package poly.persistance.mapper;

import java.util.List;

import config.Mapper;
import poly.dto.ReqDTO;

@Mapper("ReqMapper")
public interface IReqMapper {

	int insertReq(ReqDTO rDTO) throws Exception;

	List<ReqDTO> getPublicReqList(String user_seq) throws Exception;
	
}