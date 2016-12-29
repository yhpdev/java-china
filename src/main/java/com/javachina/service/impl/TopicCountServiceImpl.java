package com.javachina.service.impl;

import com.blade.ioc.annotation.Service;
import com.blade.jdbc.ActiveRecord;
import com.blade.kit.DateKit;
import com.blade.kit.StringKit;
import com.javachina.config.DBConfig;
import com.javachina.model.TopicCount;
import com.javachina.service.TopicCountService;

@Service
public class TopicCountServiceImpl implements TopicCountService {

	private ActiveRecord activeRecord = DBConfig.activeRecord;

	@Override
	public boolean update(String type, Integer tid, int count) {
		if(StringKit.isBlank(type) || null == tid){
			return false;
		}
		TopicCount topicCount = this.getCount(tid);
		if(null != topicCount){
			String sql = String.format("update t_topiccount set %s = (%s + "+count+") where tid = " + tid, type, type);
			activeRecord.execute(sql);
			return true;
		}
		return false;
	}

	@Override
	public TopicCount getCount(Integer tid) {
		if(null == tid){
			return null;
		}
		return activeRecord.byId(TopicCount.class, tid);
	}

	@Override
	public boolean save(Integer tid, Integer create_time) {
		try {
			if(null == tid || tid < 1){
				return false;
			}
			TopicCount topicCount = new TopicCount();
			topicCount.setTid(tid);
			topicCount.setViews(0);
			topicCount.setFavorites(0);
			topicCount.setComments(0);
			topicCount.setSinks(0);
			topicCount.setCreate_time(DateKit.getCurrentUnixTime());
			activeRecord.insert(topicCount);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
