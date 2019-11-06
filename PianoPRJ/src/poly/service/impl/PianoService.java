package poly.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import poly.dto.PianoDTO;
import poly.persistance.mapper.IPianoMapper;
import poly.service.IPianoService;

@Service("PianoService")
public class PianoService implements IPianoService{

	@Resource(name="PianoMapper")
	private IPianoMapper pianoMapper;
	
	@Override
	public int insertPiano(PianoDTO pDTO) throws Exception {
		return pianoMapper.insertPiano(pDTO);
	}

}