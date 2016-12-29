package com.javachina.service;

import com.blade.jdbc.core.Take;
import com.blade.jdbc.model.Paginator;
import com.javachina.model.Topic;

import java.util.List;
import java.util.Map;

public interface TopicService {
	
	Topic getTopic(Integer tid);
	
	List<Integer> topicIds();
	
	Map<String, Object> getTopicMap(Topic topic, boolean isDetail);
	
	List<Map<String, Object>> getTopicList(Take take);
	
	Paginator<Map<String, Object>> getPageList(Take queryParam);

	Integer save(Integer uid, Integer nid, String title, String content, Integer isTop);

	Integer update(Integer tid, Integer nid, String title, String content);
	
	boolean comment(Integer uid, Integer to_uid, Integer tid, String content, String ua);
	
	boolean delete(Integer tid);
	
	boolean refreshWeight();
	
	boolean updateWeight(Integer tid);
	
	boolean updateWeight(Integer tid, Integer loves, Integer favorites, Integer comment, Integer sinks, Integer create_time);

	Integer getTopics(Integer uid);

	Integer getLastCreateTime(Integer uid);

	Integer getLastUpdateTime(Integer uid);

	Paginator<Map<String, Object>> getHotTopic(Integer nid, Integer page, Integer count);

	Paginator<Map<String, Object>> getRecentTopic(Integer nid, Integer page, Integer count);

	void essence(Integer tid, Integer count);
	
}
