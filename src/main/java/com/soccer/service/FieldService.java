package com.soccer.service;

import com.soccer.dao.FieldDao;
import com.soccer.model.Field;
import com.soccer.util.PageResult;
import com.soccer.util.Search;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 
 * @author Feng Jianli
 *
 */
@Service("fieldService")
public class FieldService extends BaseService<Field>{

	@Resource(name = "fieldDao")
	private FieldDao fieldDao;
	
	@Transactional(readOnly=true)
	public PageResult<Field> findByPage(Search search) throws Exception {
		return fieldDao.findByPage(search);
	}

	@Transactional(readOnly=true)
	public List searchField(String name,Integer pageSize){
		return fieldDao.searchField(name,pageSize);
	}
    
}
