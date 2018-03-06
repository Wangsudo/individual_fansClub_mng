package com.soccer.service;

import com.soccer.dao.YoukuAuthDao;
import com.soccer.model.YoukuAuth;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service("youkuAuthService")
public class YoukuAuthService {

	@Resource(name = "youkuAuthDao")
	private YoukuAuthDao youkuAuthDao;

	@Transactional(readOnly=true)
	public YoukuAuth findYoukuAuth() throws Exception{
			return  youkuAuthDao.findYoukuAuth();
	}

	@Transactional
	public void save(YoukuAuth one) throws Exception{
		youkuAuthDao.saveOrUpdate(one);
	}

}
