/**
 * 
 */
package com.soccer.dao;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.soccer.model.Menu;

/**
 * 
 * @author Feng Jianli
 * 
 */
@Repository("menuDao")
public class MenuDao extends BaseDao<Menu> {
	
	public List<Menu> findAll() {
		return this.findByHql("from Menu u where u.pcode is null or u.pcode =''", Collections.emptyList());
	}
      
}
