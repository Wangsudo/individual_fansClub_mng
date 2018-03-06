package com.soccer.service;

import com.soccer.dao.NewsDao;
import com.soccer.model.News;
import com.soccer.util.PageResult;
import com.soccer.util.Search;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author Feng Jianli
 *
 */
@Service("newsService")
public class NewsService extends BaseService<News>{

	private static final Pattern pattern = Pattern.compile("<img src=\"(.*?)\"");

	@Resource(name = "newsDao")
	private NewsDao newsDao;
	
	@Transactional(readOnly=true)
	public PageResult<News> findByPage(Search search) throws Exception {
		PageResult<News> ret =  newsDao.findByPage(search);
		for(News news :ret.getList()){
			String thumbnail = null;
			// <img src="/formalPic/ueditor/20180113193931130-200x200.jpg"
			Matcher matcher = pattern.matcher(news.getContent());
			if(matcher.find() ){
				thumbnail = matcher.group(1);
				news.setThumbnail(thumbnail);
			}
		}
		return ret;
	}

	@Transactional
	public void setTop(Long id) throws Exception {
		BigInteger maxSort = (BigInteger) newsDao.uniqueResultSql("select IFNULL(max(n.sort),0)+1 from football_news n where n.id !=?",id );
		String sql = "update football_news u set u.sort = ? where u.id = ?";
		newsDao.excuteSqlUpdate(sql,maxSort.intValue(),id);
	}
}
