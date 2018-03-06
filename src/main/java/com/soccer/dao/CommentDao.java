/**
 * 
 */
package com.soccer.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.soccer.model.Comment;
import com.soccer.util.PageResult;
import com.soccer.util.Search;
import com.soccer.util.StringUtil;

/**
 * @author 002195
 * 
 */
@Repository("commentDao")
public class CommentDao extends BaseDao<Comment> {
	public PageResult<Comment> findByPage(Search search) throws Exception {
		PageResult<Comment> ret = new PageResult<Comment>();
		ret.setCurrentPage(search.getCurrentPage());
		ret.setPageSize(search.getPageSize());
		List<Object> params = new ArrayList<Object>();
		
		String hql = "from Comment u where 1=1 ";
			
			if(search.getPlayerId()!=null){
				hql += " and u.player.id = ? ";
				params.add(search.getPlayerId());
			}
			// 评论类型
			if(search.getType()!=null){
				hql += " and u.type = ? ";
				params.add(search.getType());
			}
			
			if(search.getId()!=null){
				hql += " and u.itemId = ? ";
				params.add(search.getId());
			}
			
			//模拟查询
			if (!StringUtil.isEmpty(search.getSnippet())) {
				hql += " and (u.player.name like ? escape '/')";
				params.add("%" + StringUtil.escapeSQLLike(search.getSnippet().trim()) + "%");
			}
			
	        // 查询条件
	        if (search.getCondition() != null) {
	            hql += search.getCondition();
	        }
	        
			if(search.getOrderby()!=null){
				hql+=" order by "+search.getOrderby();
			}else{
				hql+=" order by u.id desc";
			}
		
		this.findPageList(hql, params,ret);
	    return ret;
	}      
	
}
