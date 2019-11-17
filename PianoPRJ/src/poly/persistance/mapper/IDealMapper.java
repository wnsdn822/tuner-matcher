package poly.persistance.mapper;

import java.util.List;

import config.Mapper;
import poly.dto.DealDTO;

@Mapper("DealMapper")
public interface IDealMapper {

	int insertDeal(DealDTO dDTO) throws Exception;

	List<DealDTO> getBiddingList(String user_seq) throws Exception;

	DealDTO getDealDetail(String deal_seq) throws Exception;

	int bidCancel(String deal_seq) throws Exception;

}
