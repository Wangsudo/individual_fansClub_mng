/**
 * 
 */
package com.soccer.dao;

import com.soccer.model.YoukuAuth;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 
 * @author Feng Jianli
 * 
 */
@Repository("youkuAuthDao")
public class YoukuAuthDao extends BaseDao<YoukuAuth> {
	
	public YoukuAuth findYoukuAuth() {
		List<YoukuAuth> youkuAuths = this.find("from YoukuAuth");
		if(youkuAuths.size()>0){
			return youkuAuths.get(0);
		}
		return null;
	}
      
}
