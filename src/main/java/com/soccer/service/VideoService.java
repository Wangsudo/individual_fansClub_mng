package com.soccer.service;

import com.alibaba.fastjson.JSONObject;
import com.soccer.dao.VideoDao;
import com.soccer.dao.YoukuAuthDao;
import com.soccer.model.Video;
import com.soccer.model.YoukuAuth;
import com.soccer.util.DateUtils;
import com.soccer.util.HttpRequest;
import com.soccer.util.PageResult;
import com.soccer.util.Search;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author Feng Jianli
 *
 */
@Service("videoService")
public class VideoService extends BaseService<Video>{

	@Resource(name = "videoDao")
	private VideoDao videoDao;

	@Resource(name = "youkuAuthDao")
	private YoukuAuthDao youkuAuthDao;

	private static final Pattern pattern = Pattern.compile("http:\\/\\/player\\.youku\\.com\\/\\S*(sid|embed)\\/([^/]+)('|\"|\\/v.swf)");

	@Transactional(readOnly=true)
	public PageResult<Video> findByPage(Search search) throws Exception {
		return videoDao.findByPage(search);
	}
	
	@Transactional
	public int toggleEnable(Long id){
		return videoDao.toggleEnable(id);
	}

	@Transactional
	public String getYoukuAccessToken(){
		String accessToken = "";
		YoukuAuth auth = youkuAuthDao.findYoukuAuth();
		if(auth!=null) {
			if (auth.getExpires_date().before(new Date())) {
				String refresh_token = auth.getRefresh_token();
				YoukuAuth newAuth = getYoukuAuth(refresh_token);
				if (newAuth != null) {
					BeanUtils.copyProperties(newAuth,auth);
					youkuAuthDao.saveOrUpdate(auth);
				}
			}
		}
		if(auth!=null){
			accessToken = auth.getAccess_token();
		}else{
			accessToken = "";
		}
		return accessToken;
	}

	private YoukuAuth getYoukuAuth(String refresh_token) {
		YoukuAuth obj = null;
		try {
			String jsonStr = HttpRequest.sendPost("https://api.youku.com/oauth2/token.json", "client_id=937f9355701f8383&grant_type=refresh_token&refresh_token="+refresh_token);
			obj = JSONObject.parseObject(jsonStr,YoukuAuth.class);
			Date expires_date = DateUtils.addSeconds(new Date(), obj.getExpires_in()-100);
			obj.setExpires_date(expires_date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}

	@Transactional
	public void saveVideo(Video video) {


		Matcher matcher = pattern.matcher(video.getVideoDiv());
		String video_id =null ;
		if(matcher.find() ){
			video_id = matcher.group(2);
		}
		String jsonStr = HttpRequest.sendPost("https://api.youku.com/videos/show.json?", "client_id=937f9355701f8383&video_id="+video_id);
		if(jsonStr!=null){
			JSONObject jsonObject = JSONObject.parseObject(jsonStr);
			String thumbNail = jsonObject.getString("thumbnail");
			video.setScreenshot(thumbNail);
		}

		videoDao.saveOrUpdate(video);
	}
}
