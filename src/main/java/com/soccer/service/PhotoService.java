package com.soccer.service;

import com.soccer.dao.PhotoDao;
import com.soccer.model.PhotoGroup;
import com.soccer.util.PageResult;
import com.soccer.util.Search;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 
 * @author Feng Jianli
 *
 */
@Service("photoService")
public class PhotoService extends BaseService<PhotoGroup>{

	@Resource(name = "photoDao")
	private PhotoDao photoDao;
	
	@Transactional(readOnly=true)
	public PageResult<PhotoGroup> findByPage(Search search) throws Exception {
		return photoDao.findByPage(search);
	}

	@Transactional
	public int toggleEnable(Long id){
		return photoDao.toggleEnable(id);
	}
	
}
