
package com.soccer.dao;
import com.soccer.util.PageResult;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
@Repository("baseDao")
public class BaseDao<T>  {
	@Autowired
	private SessionFactory sessionFactory;

    public Session getSession() {
        //需要开启事物，才能得到CurrentSession
        return sessionFactory.getCurrentSession();
    }
	  
	public void save(T entity){
		getSession().save(entity);
	}
	
	public void delete(T entity){
		getSession().delete(entity);
	}
	
	public void update(T entity){
		getSession().update(entity);
	}
	
	public void saveOrUpdate(T entity) {
		getSession().saveOrUpdate(entity);
	}
	
	public void merge(T entity) {
		getSession().merge(entity);
	}
	/**
	 * 获得Dao对于的实体类
	 * 
	 * @return
	 */
/*	protected  Class<T> getEntityClass(){
	 Class<T> entityClass = null;
//	        Type t = getClass().getGenericSuperclass();
//	        if(t instanceof ParameterizedType){
//	            Type[] p = ((ParameterizedType)t).getActualTypeArguments();
//	            entityClass = (Class<T>)p[0];
//	        }
	        
        Type genType = getClass().getGenericSuperclass();   
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();   
        entityClass =  (Class)params[0];  
        return entityClass;
	}*/
	
	public void del(Class clazz,Long id ){
		T obj = this.findById(clazz,id);
		this.delete(obj);
	}
	
	public void del(Class clazz,List<Long> ids ){
		for(Long id:ids){
			del(clazz,id);
		}
	}
	
	public void saveOrUpdate(Collection<T> entities){
	    for (Iterator<T> localIterator = entities.iterator(); localIterator.hasNext(); ) { 
	    	Object entity = localIterator.next();
	        getSession().saveOrUpdate(entity);
        }
	}
	
	public void save(Collection<T> entities){
	    for (Iterator<T> localIterator = entities.iterator(); localIterator.hasNext(); ) { 
	    	Object entity = localIterator.next();
	        getSession().save(entity);
        }
	}
	
	/**
	 * 根据主键查找对象
	 * @param clazz
	 * @param id
	 * @return
	 */
	public T findById(Class clazz, long id) {
		return (T)getSession().get(clazz, id);
	}

	@SuppressWarnings({ "unchecked" })
	public List findByHql(final String queryString, final List<Object> params){
		final Query query = getSession().createQuery(queryString);
		// query.setCacheable(true);
		if(params!=null && params.size()>0){
			for (int i=0;i<params.size();i++) {
				query.setParameter(i, params.get(i));
			}
		}
		return query.list();
	}
	
	@SuppressWarnings({ "unchecked" })
	public List findBySql(final String queryString, Object ... params){
		final SQLQuery query = getSession().createSQLQuery(queryString);
		// query.setCacheable(true);
		if(params!=null && params.length>0){
			for (int i=0;i<params.length;i++) {
				query.setParameter(i, params[i]);
			}
		}
		return query.list();
	}
	
	@SuppressWarnings({ "unchecked" })
	public Object uniqueResultSql(final String queryString, Object ... params){
		final SQLQuery query = getSession().createSQLQuery(queryString);
		// query.setCacheable(true);
		if(params!=null && params.length>0){
			for (int i=0;i<params.length;i++) {
				query.setParameter(i, params[i]);
			}
		}
		return query.uniqueResult();
	}
	
	
	public List find(final String queryString, Object ... params ){
		final Query query = getSession().createQuery(queryString);
		// query.setCacheable(true);
		if(params!=null && params.length>0){
			for (int i=0;i<params.length;i++) {
				query.setParameter(i, params[i]);
			}
		}
		return query.list();
	}
	
	/**
	 * 
	 * @param queryString
	 * @param params
	 * @return
	 */
	public int excuteUpdate(String queryString,List<Object> params){
		 Query query = getSession().createQuery(queryString);
		 if(params!=null && params.size()>0){
				for (int i=0;i<params.size();i++) {
					query.setParameter(i, params.get(i));
				}
		 }
		 return query.executeUpdate(); 
	}
	
	public int excuteUpdate(String queryString,Object ... params){
		 Query query = getSession().createQuery(queryString);
		 if(params!=null && params.length>0){
				for (int i=0;i<params.length;i++) {
					query.setParameter(i, params[i]);
				}
		}
		 return query.executeUpdate(); 
	}
	
	public int excuteSqlUpdate(String queryString,Object ... params){
		 Query query = getSession().createSQLQuery(queryString);
		 if(params!=null && params.length>0){
				for (int i=0;i<params.length;i++) {
					query.setParameter(i, params[i]);
				}
		}
		 return query.executeUpdate(); 
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void findPageList(final String queryString, final List<Object> params, final PageResult pageResult) throws Exception {
			final Query query = getSession().createQuery(queryString);
			String queryStringCount = queryString.substring(queryString.lastIndexOf("from "));
			final Query queryCount = getSession().createQuery("select count(*) " +queryStringCount);
			// query.setCacheable(true);
			if(params!=null && params.size()>0){
				for (int i = 0; i < params.size(); i++) {
					if (params.get(i) instanceof Map){
						Map map = (HashMap)params.get(i);
						String key = (String)map.keySet().iterator().next();
						query.setParameterList(key, (List)map.get(key));
						queryCount.setParameterList(key, (List)map.get(key));
					}else{
						query.setParameter(i, params.get(i));
						queryCount.setParameter(i, params.get(i));
					}
				}
			}
			final int start = pageResult.getCurrentPage() <= 0 ? 0 : (pageResult.getCurrentPage() - 1) * pageResult.getPageSize();
			query.setFirstResult(start);
			query.setMaxResults(pageResult.getPageSize());
			List<T> list = query.list();
			Object count = queryCount.uniqueResult();
			pageResult.setList(list);
			pageResult.setTotalRecord(Integer.valueOf(count.toString()));
			pageResult.setTotalPage(pageResult.getTotalPage());
			
	}
	
	
	

}
