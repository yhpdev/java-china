package com.javachina.service;

import com.blade.jdbc.core.Take;
import com.blade.jdbc.model.Paginator;
import com.javachina.model.Node;

import java.util.List;
import java.util.Map;

public interface NodeService {
	
	Node getNode(Integer nid);
	
	Node getNode(Take take);
	
	Map<String, Object> getNodeDetail(Node node, Integer nid);
	
	List<Node> getNodeList(Take take);
	
	List<Map<String, Object>> getNodeList();
	
	Paginator<Map<String, Object>> getPageList(Take take);
	
	boolean save(Integer pid, String title, String description, String slug, String node_pic);
	
	boolean delete(Integer nid);
	
	boolean updateCount(Integer nid, String type, int count);

	boolean update(Integer nid, Integer pid, String title, String description, String node_slug, String node_pic);
}