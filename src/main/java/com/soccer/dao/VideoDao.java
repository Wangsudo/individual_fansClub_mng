/**
 * 
 */
package com.soccer.dao;

import com.soccer.model.Video;
import com.soccer.util.PageResult;
import com.soccer.util.Search;
import com.soccer.util.StringUtil;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * @author Feng Jianli
 */
@Repository("videoDao")
public class VideoDao extends BaseDao<Video> {
	public PageResult<Video> findByPage(Search search) throws Exception {
		PageResult<Video> ret = new PageResult<Video>();
		ret.setCurrentPage(search.getCurrentPage());
		ret.setPageSize(search.getPageSize());
		List<Object> params = new ArrayList<Object>();
		String hql = "select new com.soccer.model.Video( u.id as id,u.player,u.screenshot,u.videoDiv,u.title,u.comment,u.auditStatus,"
				+ "u.isEnabled,u.createTime,u.likes,u.viewTimes,"
				+ "(select count(c) from Comment c where  u.id = c.itemId and c.type=2) as commentCount) from Video u left join u.player.team t where 1=1 ";

			if (!StringUtil.isEmpty(search.getTitle())) {
				hql += " and u.title like ? escape '/'";
				params.add("%" + StringUtil.escapeSQLLike(search.getTitle().trim()) + "%");
			}
			//所属球队
			if (!StringUtil.isEmpty(search.getTeamTitle()) ) {
				hql += " and u.player.team.teamTitle like ? escape '/' ";
				params.add("%" + StringUtil.escapeSQLLike(search.getTeamTitle().trim()) + "%");
	     	}
			//发布人（可能是球员或者管理员）
			if (!StringUtil.isEmpty(search.getName())) {
				hql += " and (u.player.name like ? escape '/')";
				params.add("%" + StringUtil.escapeSQLLike(search.getName().trim()) + "%");
			}
			
			//模拟查询
			if (!StringUtil.isEmpty(search.getSnippet())) {
				hql += " and ( t.teamTitle like ? escape '/' or u.player.name like ? escape '/' or  u.title like ? escape '/')";
				params.add("%" + StringUtil.escapeSQLLike(search.getSnippet().trim()) + "%");
				params.add("%" + StringUtil.escapeSQLLike(search.getSnippet().trim()) + "%");
				params.add("%" + StringUtil.escapeSQLLike(search.getSnippet().trim()) + "%");
			}
			
			//所属球员(指定id)
			if(search.getPlayerId()!=null){
				hql += " and u.player.id = ? ";
				params.add(search.getPlayerId());
			}
			
			//类型  1为球员上传， 2为管理员上传
			if (search.getType()!=null) {
				if(search.getType() == 1){
					hql += " and u.player.id is not null ";
				}else{
					hql += " and u.player.id is null ";
				}
			}
			
			// 可见范围 
			if(search.getViewType()!=null  ){
				hql += " and u.viewType = ?";
				params.add(search.getViewType());
			}
			
			// 上传的球队类型
			if(search.getTeamType()!=null && search.getTeamType()!=0 ){
				hql += " and u.player.team.teamType = ?";
				params.add(search.getTeamType());
			}
			
			//是否启用， 1启用， 0不启用。
			if(search.getIsEnabled()!=null){
				hql += " and u.isEnabled =?";
				params.add(search.getIsEnabled());
			}
			
			//审核状态
			if(search.getStatus()!=null  ){
				hql += " and u.auditStatus = ?";
				params.add(search.getStatus());
			}
			
			//编缉开始时间
			if (search.getFromDate() != null ) {
				hql += " and u.createTime >= ?";
				params.add(search.getFromDate());
			}
			
			//编缉结束时间
			if (search.getToDate() != null) {
				hql += " and u.createTime <= ?";
				Calendar toDate = Calendar.getInstance();
			 	toDate.setTime(search.getToDate() );
			 	toDate.add(Calendar.DATE, 1);
				params.add(toDate.getTime());
			}	
			if(search.getOrderby()!=null){
				hql+=" order by u."+search.getOrderby();
			}
		
		this.findPageList(hql, params,ret);
	    return ret;
	}

	public int toggleEnable(Long id){
		String sql="update football_video u set u.is_enabled = ifnull(u.is_enabled^3,2) where u.id=?";
		return this.excuteSqlUpdate(sql.toString(), id);
	}
}
