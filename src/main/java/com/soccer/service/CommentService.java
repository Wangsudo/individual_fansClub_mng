package com.soccer.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soccer.dao.CommentDao;
import com.soccer.model.Comment;
import com.soccer.util.PageResult;
import com.soccer.util.Search;

@Service("commentService")
public class CommentService  extends BaseService<Comment>{


	@Resource(name = "commentDao")
	private CommentDao commentDao;
	
	@Transactional(readOnly=true)
	public PageResult<Comment> findByPage(Search search) throws Exception {
		return commentDao.findByPage(search);
	}
	
}
