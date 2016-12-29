package com.javachina.service.impl;

import com.blade.ioc.annotation.Service;
import com.blade.jdbc.ActiveRecord;
import com.blade.jdbc.core.Take;
import com.blade.jdbc.model.Paginator;
import com.blade.kit.CollectionKit;
import com.blade.kit.DateKit;
import com.blade.kit.FileKit;
import com.blade.kit.StringKit;
import com.javachina.ImageTypes;
import com.javachina.config.DBConfig;
import com.javachina.kit.QiniuKit;
import com.javachina.kit.Utils;
import com.javachina.model.Node;
import com.javachina.service.NodeService;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NodeServiceImpl implements NodeService {

	private ActiveRecord activeRecord = DBConfig.activeRecord;

	@Override
	public Node getNode(Integer nid) {
		Node node = activeRecord.byId(Node.class, nid);
		if(null != node && node.getIs_del() == 0){
			return node;
		}
		return null;
	}
	
	@Override
	public Node getNode(Take take) {
		return activeRecord.one(take);
	}
		
	@Override
	public List<Map<String, Object>> getNodeList() {
		List<Map<String, Object>> result= new ArrayList<Map<String,Object>>();
		Take np = new Take(Node.class);
		np.set("is_del", 0).set("pid", 0).orderby("topics desc");
		List<Node> parents = this.getNodeList(np);
		for(Node node : parents){
			Map<String, Object> nodeMap = this.getNodeDetail(node, null);
			if(null != nodeMap && !nodeMap.isEmpty()){
				Take cp = new Take(Node.class);
				cp.set("is_del", 0).set("pid", node.getNid()).orderby("topics desc");
				List<Node> nodes = this.getNodeList(cp);
				if(CollectionKit.isNotEmpty(nodes)){
					List<Map<String, Object>> items = new ArrayList<Map<String,Object>>();
					for(Node item : nodes){
						Map<String, Object> itemMap = this.getNodeDetail(item, null);
						if(null != itemMap && !itemMap.isEmpty()){
							items.add(itemMap);
						}
					}
					nodeMap.put("items", items);
				}
				result.add(nodeMap);
			}
		}
		return result;
	}
	
	@Override
	public List<Node> getNodeList(Take take) {
		if(null != take){
			return activeRecord.list(take);
		}
		return null;
	}
	
	@Override
	public Paginator<Map<String, Object>> getPageList(Take take) {
		if(null != take){
			Paginator<Node> nodePage = activeRecord.page(take);
			return this.getNodePageMap(nodePage);
		}
		return null;
	}
	
	private Paginator<Map<String, Object>> getNodePageMap(Paginator<Node> nodePage){
		
		long totalCount = nodePage.getTotal();
		int page = nodePage.getPages();
		int pageSize = nodePage.getLimit();
		Paginator<Map<String, Object>> result = new Paginator<Map<String,Object>>(totalCount, page, pageSize);
		
		List<Node> nodes = nodePage.getList();
		
		List<Map<String, Object>> nodeMaps = new ArrayList<Map<String,Object>>();
		if(null != nodes && nodes.size() > 0){
			for(Node node : nodes){
				Map<String, Object> map = this.getNodeDetail(node, null);
				if(null != map && !map.isEmpty()){
					nodeMaps.add(map);
				}
			}
		}
		
		result.setList(nodeMaps);
		
		return result;
	}
	
	@Override
	public boolean save(Integer pid, String title, String description, String slug, String node_pic) {
		try {
			Integer time = DateKit.getCurrentUnixTime();

			Node node = new Node();
			node.setPid(pid);
			node.setTitle(title);
			node.setDescription(description);
			node.setSlug(slug);
			node.setPic(node_pic);
			node.setCreate_time(time);
			node.setUpdate_time(time);

			activeRecord.insert(node);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public boolean delete(Integer nid) {
		if(null != nid){
			Node temp = new Node();
			temp.setNid(nid);
			temp.setIs_del(1);
			activeRecord.update(temp);
			return true;
		}
		return false;
	}

	@Override
	public Map<String, Object> getNodeDetail(Node node, Integer nid) {
		Map<String, Object> map = new HashMap<String, Object>();
		if(null == node){
			node = this.getNode(nid);
		}
		if(null != node){
			map.put("nid", node.getNid());
			map.put("node_name", node.getTitle());
			map.put("node_slug", node.getSlug());
			map.put("topics", node.getTopics());
			map.put("pid", node.getPid());
			
			if(node.getPid() > 0){
				Node parent = this.getNode(node.getPid());
				if(null != parent){
					map.put("parent_name", parent.getTitle());
				}
			}
			
			// 查询子节点数
			Integer childs = getNodeCount(node.getNid());
			map.put("childs", childs);
			map.put("description", node.getDescription());
			if(StringKit.isNotBlank(node.getPic())){
				String pic = Utils.getAvatar(node.getPic(), ImageTypes.small);
				map.put("pic", pic);
			}
		}
		return map;
	}
	
	private Integer getNodeCount(Integer nid){
		Node node = new Node();
		node.setPid(nid);
		node.setIs_del(0);
		return activeRecord.count(node);
	}

	@Override
	public boolean updateCount(Integer nid, String type, int count) {
		if(null != nid && StringKit.isNotBlank(type)){
			try {
				String sql = "update t_node set %s = (%s + "+count+") where nid = " + nid;
				activeRecord.execute(String.format(sql, type, type));
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public boolean update(Integer nid, Integer pid, String title, String description, String node_slug,
			String node_pic) {
		try {
			if(null == nid){
				return false;
			}
			
			StringBuffer updateSql = new StringBuffer("update t_node set update_time = ? ");
			List<Object> params = new ArrayList<Object>();
			params.add(DateKit.getCurrentUnixTime());
			
			if(null != pid){
				updateSql.append(", pid = ?");
				params.add(pid);
			}
			if(null != title){
				updateSql.append(", title = ?");
				params.add(title);
			}
			if(null != description){
				updateSql.append(", description = ?");
				params.add(description);
			}
			if(null != node_slug){
				updateSql.append(", slug = ?");
				params.add(node_slug);
			}
			
			File file = new File(node_pic);
			if(file.exists()){
				String ext = FileKit.getExtension(file.getName());
				if(StringKit.isBlank(ext)){
					ext = "png";
				}
				
				String key = "node/" + node_slug + "/" + StringKit.getRandomChar(4) + "/" + StringKit.getRandomNumber(4) + "." + ext;
				
				boolean flag = QiniuKit.upload(file, key);
				if(flag){
					updateSql.append(", pic = ?");
					params.add(key);
				}
			}
			
			updateSql.append(" where nid = ?");
			params.add(nid);

			activeRecord.execute(updateSql.toString(), params.toArray());

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
}
