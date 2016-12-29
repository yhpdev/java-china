package com.javachina.controller;

import com.blade.Blade;
import com.blade.ioc.annotation.Inject;
import com.blade.jdbc.core.Take;
import com.blade.jdbc.model.Paginator;
import com.blade.kit.DateKit;
import com.blade.kit.FileKit;
import com.blade.kit.PatternKit;
import com.blade.kit.StringKit;
import com.blade.kit.json.JSONObject;
import com.blade.mvc.annotation.Controller;
import com.blade.mvc.annotation.PathParam;
import com.blade.mvc.annotation.Route;
import com.blade.mvc.http.HttpMethod;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.blade.mvc.multipart.FileItem;
import com.blade.mvc.view.ModelAndView;
import com.javachina.Constant;
import com.javachina.Types;
import com.javachina.kit.FamousDay;
import com.javachina.kit.FamousKit;
import com.javachina.kit.SessionKit;
import com.javachina.kit.Utils;
import com.javachina.model.LoginUser;
import com.javachina.model.Node;
import com.javachina.service.FavoriteService;
import com.javachina.service.NodeService;
import com.javachina.service.NoticeService;
import com.javachina.service.TopicService;
import com.redfin.sitemapgenerator.ChangeFreq;
import com.redfin.sitemapgenerator.WebSitemapGenerator;
import com.redfin.sitemapgenerator.WebSitemapUrl;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Controller("/")
public class IndexController extends BaseController {

	@Inject
	private TopicService topicService;
	
	@Inject
	private NodeService nodeService;
	
	@Inject
	private NoticeService noticeService;
	
	@Inject
	private FavoriteService favoriteService;
	
	/**
	 * 首页热门
	 */
	@Route(value = "/", method = HttpMethod.GET)
	public ModelAndView show_home(Request request, Response response){
		
		this.putData(request);
		
		// 帖子
		String tab = request.query("tab");
		Integer page = request.queryAsInt("p");
		Integer nid = null;
		
		if(StringKit.isNotBlank(tab)){
			Take np = new Take(Node.class);
			np.eq("is_del", 0).eq("slug", tab);
			Node node = nodeService.getNode(np);
			if(null != node){
				nid = node.getNid();
				request.attribute("tab", tab);
				request.attribute("node_name", node.getTitle());
			}
		}
		
		Paginator<Map<String, Object>> topicPage = topicService.getHotTopic(nid, page, 20);
		request.attribute("topicPage", topicPage);
		
		// 最热帖子
		List<Map<String, Object>> hot_topics = topicService.getHotTopic(null, 1, 10).getList();
		request.attribute("hot_topics", hot_topics);
		
		// 最热门的8个节点
		Take np = new Take(Node.class);
		np.eq("is_del", 0).notEq("pid", 0).page(1, 8, "topics desc");
		List<Node> hot_nodes = nodeService.getNodeList(np);
		request.attribute("hot_nodes", hot_nodes);
		
		return this.getView("home");
	}
	
	/**
	 * 最新
	 */
	@Route(value = "/recent", method = HttpMethod.GET)
	public ModelAndView show_recent(Request request, Response response){
		
		this.putData(request);
		
		// 帖子
		String tab = request.query("tab");
		Integer page = request.queryAsInt("p");
		Integer nid = null;
		
		if(StringKit.isNotBlank(tab)){
			Take np = new Take(Node.class);
			np.eq("is_del", 0).eq("slug", tab);
			Node node = nodeService.getNode(np);
			if(null != node){
				nid = node.getNid();
				request.attribute("tab", tab);
				request.attribute("node_name", node.getTitle());
			}
		}
		
		Paginator<Map<String, Object>> topicPage = topicService.getRecentTopic(nid, page, 15);
		request.attribute("topicPage", topicPage);
				
		// 最热帖子
		List<Map<String, Object>> hot_topics = topicService.getHotTopic(null, 1, 10).getList();
		request.attribute("hot_topics", hot_topics);
		
		// 最热门的10个节点
		Take np = new Take(Node.class);
		np.eq("is_del", 0).notEq("pid", 0).page(1, 8, "topics desc");
		List<Node> hot_nodes = nodeService.getNodeList(np);
		request.attribute("hot_nodes", hot_nodes);
		
		return this.getView("recent");
	}
	
	private void putData(Request request){
		// 读取节点列表
		List<Map<String, Object>> nodes = nodeService.getNodeList();
		request.attribute("nodes", nodes);
		
		// 每日格言
		FamousDay famousDay = FamousKit.getTodayFamous();
		Constant.VIEW_CONTEXT.set("famousDay", famousDay);
	}
	
	/**
	 * 节点主题页
	 */
	@Route(value = "/go/:slug", method = HttpMethod.GET)
	public ModelAndView go(@PathParam("slug") String slug,
			Request request, Response response){
		
		LoginUser loginUser = SessionKit.getLoginUser();
		Take np = new Take(Node.class);
		np.eq("is_del", 0).eq("slug", slug);
		Node node = nodeService.getNode(np);
		if(null == node){
			// 不存在的节点
//			response.text("not found node.");
			response.go("/");
			return null;
		}
		
		if(null == loginUser){
			SessionKit.setCookie(response, Constant.JC_REFERRER_COOKIE, request.url());
		} else {
			// 查询是否收藏
			boolean is_favorite = favoriteService.isFavorite(Types.node.toString(), loginUser.getUid(), node.getNid());
			request.attribute("is_favorite", is_favorite);
		}
		
		Integer page = request.queryAsInt("page");
		
		Paginator<Map<String, Object>> topicPage = topicService.getRecentTopic(node.getNid(), page, 15);
		request.attribute("topicPage", topicPage);
		
		Map<String, Object> nodeMap = nodeService.getNodeDetail(null, node.getNid());
		request.attribute("node", nodeMap);
		
		return this.getView("node_detail");
	}
	
	
	/**
	 * 上传头像
	 */
	@Route(value = "/uploadimg", method = HttpMethod.POST)
	public void uploadimg(Request request, Response response){
		LoginUser user = SessionKit.getLoginUser();
		if(null == user){
			return;
		}
		FileItem[] fileItems = request.files();
		if(null != fileItems && fileItems.length > 0){
			
			FileItem fileItem = fileItems[0];
			
			String type = request.query("type");
			String suffix = FileKit.getExtension(fileItem.fileName());
			if(StringKit.isNotBlank(suffix)){
				suffix = "." + suffix;
			}
			if(!PatternKit.isImage(suffix)){
				return;
			}
			
			if(null == type){
				type = "temp";
			}
			
			String saveName = DateKit.dateFormat(new Date(), "yyyyMMddHHmmssSSS")  + "_" + StringKit.getRandomChar(10) + suffix;
			File file = new File(Blade.$().webRoot() + File.separator + Constant.UPLOAD_FOLDER + File.separator + saveName);
			
			try {

				Utils.copyFileUsingFileChannels(fileItem.file(), file);
				
				String filePath = Constant.UPLOAD_FOLDER + "/" + saveName;
				
				JSONObject res = new JSONObject();
				res.put("status", 200);
				res.put("savekey", filePath);
				res.put("savepath", filePath);
				res.put("url", Constant.SITE_URL + "/" + filePath);
				
				response.json(res.toString());
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	
	/**
	 * markdown页面
	 */
	@Route(value = "/markdown", method = HttpMethod.GET)
	public ModelAndView markdown(Request request, Response response){
		return this.getView("markdown");
	}
	
	/**
	 * about页面
	 */
	@Route(value = "/about", method = HttpMethod.GET)
	public ModelAndView about(Request request, Response response){
		return this.getView("about");
	}
	
	/**
	 * faq页面
	 */
	@Route(value = "/faq", method = HttpMethod.GET)
	public ModelAndView faq(Request request, Response response){
		String qa = request.query("aaa");
		System.out.println(qa);
		return this.getView("faq");
	}
	
	/**
	 * donate页面
	 */
	@Route(value = "/donate", method = HttpMethod.GET)
	public ModelAndView donate(Request request, Response response){
		return this.getView("donate");
	}
	
	/**
	 * robots.txt
	 */
	@Route(value = "/robots.txt", method = HttpMethod.GET)
	public ModelAndView robots(Request request, Response response){
		return this.getView("robots");
	}
	
	/**
	 * sitemap页面
	 */
	@Route(value = "/sitemap.xml", method = HttpMethod.GET)
	public void sitemap(Request request, Response response){
		try {
			WebSitemapGenerator wsg = new WebSitemapGenerator(Constant.SITE_URL);
			wsg.addUrl(new WebSitemapUrl.Options(Constant.SITE_URL).lastMod(new Date()).priority(0.8).changeFreq(ChangeFreq.ALWAYS).build());
			wsg.addUrl(new WebSitemapUrl.Options(Constant.SITE_URL + "/markdown").lastMod(new Date()).priority(0.8).changeFreq(ChangeFreq.MONTHLY).build());
			wsg.addUrl(new WebSitemapUrl.Options(Constant.SITE_URL + "/essence").lastMod(new Date()).priority(0.8).changeFreq(ChangeFreq.MONTHLY).build());
			wsg.addUrl(new WebSitemapUrl.Options(Constant.SITE_URL + "/signup").lastMod(new Date()).priority(0.8).changeFreq(ChangeFreq.MONTHLY).build());
			wsg.addUrl(new WebSitemapUrl.Options(Constant.SITE_URL + "/signin").lastMod(new Date()).priority(0.8).changeFreq(ChangeFreq.MONTHLY).build());
			wsg.addUrl(new WebSitemapUrl.Options(Constant.SITE_URL + "/faq").lastMod(new Date()).priority(0.8).changeFreq(ChangeFreq.MONTHLY).build());
			wsg.addUrl(new WebSitemapUrl.Options(Constant.SITE_URL + "/about").lastMod(new Date()).priority(0.8).changeFreq(ChangeFreq.MONTHLY).build());
			wsg.addUrl(new WebSitemapUrl.Options(Constant.SITE_URL + "/donate").lastMod(new Date()).priority(0.8).changeFreq(ChangeFreq.MONTHLY).build());
			
			List<Integer> tids = topicService.topicIds();
			for(Integer tid : tids){
				WebSitemapUrl url = new WebSitemapUrl.Options(Constant.SITE_URL + "/topic/" + tid).lastMod(new Date()).priority(0.8).changeFreq(ChangeFreq.DAILY).build();
				wsg.addUrl(url);
			}
			
			response.xml(wsg.writeAsStrings().get(0));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
}
