/**
 * 
 */
package com.soccer.dao;

import com.soccer.model.Message;
import com.soccer.model.MessageTank;
import com.soccer.util.PageResult;
import com.soccer.util.Search;
import com.soccer.util.StringUtil;
import org.springframework.stereotype.Repository;

import java.util.*;


/**
 * @author Feng Jianli
 */
@Repository("messageDao")
public class MessageDao extends BaseDao<Message> {
	public PageResult<Message> findByPage(Search search) throws Exception {
		PageResult<Message> ret = new PageResult<Message>();
		ret.setCurrentPage(search.getCurrentPage());
		ret.setPageSize(search.getPageSize());
		List<Object> params = new ArrayList<Object>();
		
		String hql = "from Message u where 1=1 ";
			if(search.getType()!=null){
				hql += " and u.messageType =?";
				params.add(search.getType());
			}
			//标题
			if (!StringUtil.isEmpty(search.getTitle())) {
				hql += " and u.title like ? escape '/'";
				params.add("%" + StringUtil.escapeSQLLike(search.getTitle().trim()) + "%");
			}
			
			//给某球员的信
			if(search.getPlayerId()!=null){
				hql += " and exists (select 1 From MessageTank t where t.messageId = u.id and t.player.id= ?)";
				params.add(search.getPlayerId());
			}
			
			//发布的球员
			if (search.getTeamId()!=null ) {
				hql += " and u.team.id = ?";
				params.add(search.getTeamId());
	     	}
			
			//发布的球队
			if (!StringUtil.isEmpty(search.getTeamTitle()) ) {
				hql += " and u.team.teamTitle like ? escape '/' ";
				params.add("%" + StringUtil.escapeSQLLike(search.getTeamTitle().trim()) + "%");
	     	}

	        // 查询条件
	        if (search.getCondition() != null) {
	            hql += search.getCondition();
	        }
	        
			//是否启用， 1启用， 0不启用。
			if(search.getIsEnabled()!=null){
				hql += " and u.isEnabled =?";
				params.add(search.getIsEnabled());
			}
			
			// 发布时间
	        if (search.getFromDate() != null) {
	            hql += " and u.modifyTime <= ?";
	            params.add(search.getFromDate());
	        }
	        
	      //发布结束时间
			if (search.getToDate() != null) {
				hql += " and u.modifyTime <= ?";
				Calendar toDate = Calendar.getInstance();
			 	toDate.setTime(search.getToDate() );
			 	toDate.add(Calendar.DATE, 1);
				params.add(toDate.getTime());
			}
	        
			if(search.getOrderby()!=null){
				hql+=" order by "+search.getOrderby();
			}else{
				hql+=" order by u.opTime desc";
			}
		
		this.findPageList(hql, params,ret);
	    return ret;
	}      
	
	public void confirm(Collection<MessageTank> entities){
		 for (Iterator<MessageTank> localIterator = entities.iterator(); localIterator.hasNext(); ) { 
	    	Object entity = localIterator.next();
	        getSession().saveOrUpdate(entity);
        }
	}
	public int toggleEnable(Long id){
		String sql="update message u set u.is_enabled = ifnull(u.is_enabled^3,2) where u.id=?";
		return this.excuteSqlUpdate(sql.toString(), id);
	}

/*	public Integer getTotalActivity(Long playerId) {
		String sql = "select ifnull(count(*),0) from message_tank t join message m on t.message_id = m.id  where m.message_type = 2 and t.player_id =?";
		BigInteger cnt = (BigInteger) this.uniqueResultSql(sql, playerId);
		 return cnt.intValue();
	}

	public Integer getPartipateNum(Long playerId) {
		String sql = "select ifnull(count(*),0) from message_tank t join message m on t.message_id = m.id  where m.message_type = 2 and t.confirm_status = 1 and ifnull(t.audit_status,0)!=2 and t.player_id =?";
		BigInteger cnt = (BigInteger) this.uniqueResultSql(sql, playerId);
		return cnt.intValue();
	}

	public Integer getCheatNum(Long playerId) {
		String sql = "select ifnull(count(*),0) from message_tank t join message m on t.message_id = m.id  where m.message_type = 2 and t.confirm_status = 1 and t.audit_status = 2  and t.player_id =?";
		BigInteger cnt = (BigInteger) this.uniqueResultSql(sql, playerId);
		return cnt.intValue();
	}*/
}
