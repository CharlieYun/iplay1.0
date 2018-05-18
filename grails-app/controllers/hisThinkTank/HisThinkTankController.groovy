package hisThinkTank

import com.iminer.biz.Movie
import com.iminer.biz.Teleplay


class HisThinkTankController {
	def marketdataService
	def hisThinkTankService 
	private final def teleplayPanel ="panel-824968"
	private final def networkPanel ="panel-499659"
	private final def moviePanel ="panel-999999"
	private final def active="active"
	private final def tpActive="tab-pane active"
	
	/**
	 * iplay登陆成功后默认页面
	 * @author HTYang
	 * @date 2015-12-31 下午2:54:40
	 * @return
	 */
    def index() {
		//电视剧
		if("1".equals(params.type)){
			params.objectType=5
			params.max = Math.min(params.max ? params.int('max') : 8, 100)
			def paramArr = []
			String select_hql = "from Teleplay as b where b.discern=? "
			String select_count_hql = "select count(*) as myCount From basic_teleplay_info as b where b.discern=? " ;
			
			paramArr.add(1)
			def where_select_hql = "and (b.teleplayAttributes=? or b.teleplayAttributes=?) "
			def where_select_count_hql = "and (b.teleplay_attributes=? or b.teleplay_attributes=?) "
			paramArr.add(0)
			paramArr.add(2)
			if(params.aid && !"all".equals(params.aid)){//包含查询时间
				where_select_hql += "and b.publishTime>=? and b.publishTime<=? "
				where_select_count_hql += "and b.publish_time>=? and b.publish_time<=? "
				paramArr.add(params.aid)
				paramArr.add(params.avalue)
			}
			where_select_hql += "order by b.lastUpdated desc,b.id desc "
			where_select_count_hql += "order by b.last_updated desc,b.id desc "
			def res=Teleplay.findAll(select_hql+where_select_hql,paramArr,[max:params.max,offset:params.offset?params.offset as int:0])
			
			def allCount = hisThinkTankService.getAllTeleplayCount(select_count_hql+where_select_count_hql, paramArr);
			
//			def resRes=Teleplay.findAll(select_hql,paramArr)
			def teleplayList = new ArrayList()
			res.each {
				teleplayList.add(marketdataService.getTeleplay(it))
			}
			[teleplayInstanceList: teleplayList, teleplayInstanceTotal: allCount,teleplayPanel:teleplayPanel,tactive:active,ttpActive:tpActive,ntpActive:"tab-pane",mtpActive:"tab-pane",ttype:params.type,params:params]
		}else if("2".equals(params.type)){
			params.objectType=5
			params.max = Math.min(params.max ? params.int('max') : 8, 100)
			def paramArr = []
			String select_hql = "from Teleplay as b where b.discern=? "
			paramArr.add(1)
			select_hql += "and (b.teleplayAttributes=? or b.teleplayAttributes=?) "
			paramArr.add(1)
			paramArr.add(2)
			if(params.aid && !"all".equals(params.aid)){//包含查询时间
				select_hql += "and b.publishTime>=? and b.publishTime<=? "
				paramArr.add(params.aid)
				paramArr.add(params.avalue)
			}
			select_hql += "order by b.lastUpdated desc,b.id desc "
			def res=Teleplay.findAll(select_hql,paramArr,[max:params.max,offset:params.offset?params.offset as int:0])
			def resRes=Teleplay.findAll(select_hql,paramArr)
			def teleplayList = new ArrayList()
			res.each {
				teleplayList.add(marketdataService.getTeleplay(it))
			}
			[teleplayInstanceList: teleplayList, teleplayInstanceTotal: resRes.size(),teleplayPanel:teleplayPanel,tactive:active,ttpActive:tpActive,ntpActive:"tab-pane",mtpActive:"tab-pane",ttype:params.type,params:params]
		}else{
			params.objectType=4
			params.max = Math.min(params.max ? params.int('max') : 8, 100)
			if(params.aid==null || params.aid=="all"){
				def res = Movie.startQuerylist().list(max:params.max,offset:params.offset?params.offset as int:0)
				def movieList = new ArrayList()
				res.each {
					movieList.add(marketdataService.getMovie(it))
				}
				[movieInstanceList: movieList, movieInstanceTotal: Movie.startQuerylist().count(),params:params,mactive:active,ttpActive:"tab-pane",ntpActive:"tab-pane",mtpActive:tpActive,ttype:params.type]
			}else{
				def res = Movie.findAll("from Movie as b where b.publishTime>=? and b.publishTime<=? order by b.lastUpdated desc,b.id desc",[params.aid,params.avalue],[max:params.max,offset:params.offset?params.offset as int:0])
				def resRes = Movie.findAll("from Movie as b where b.publishTime>=? and b.publishTime<=? order by b.lastUpdated desc,b.id desc",[params.aid,params.avalue])
				def movieList = new ArrayList()
				res.each {
					movieList.add(marketdataService.getMovie(it))
				}
				[movieInstanceList: movieList, movieInstanceTotal: resRes.size(),params:params,mactive:active,ttpActive:"tab-pane",ntpActive:"tab-pane",mtpActive:tpActive,ttype:params.type]
			}
		}
		
	}
		
	/**
	 * 显示详细信息页面
	 * @author HTYang
	 * @date 2015-12-31 下午2:54:59
	 * @return
	 */
	 def show() {
		 //电视剧信息
		 if(params.type=="1"){
			 def teleplayInstance = Teleplay.get(params.id)
			 if (!teleplayInstance) {
				 flash.message = message(code: 'default.not.found.message', args: [message(code: 'teleplay.label', default: 'Teleplay'), params.id])
				 redirect(action: "list")
				 return
			 }
			 params.active=active
			 def teleplay = marketdataService.getTeleplayInfo(teleplayInstance)
			 params.object=teleplay
			 [params:params]
//			 <tr>
//			 <td style="font-weight: bold;">网络首播平台</td>
//			 <td>${params.object?.premName }</td>
//			 <td style="font-weight: bold;">网络首播时间</td>
//			 <td>${params.object?.premDate }</td>
//		 	</tr>
		 }
		 
		 //网剧信息
		 if(params.type=="2"){
			 def teleplayInstance = Teleplay.get(params.id)
			 if (!teleplayInstance) {
				 flash.message = message(code: 'default.not.found.message', args: [message(code: 'teleplay.label', default: 'Teleplay'), params.id])
				 redirect(action: "list")
				 return
			 }
			 params.active=active
//			 def teleplay = marketdataService.getTeleplayInfo(teleplayInstance)
			 def teleplay= marketdataService.getNetPlayInfo(teleplayInstance)
			 params.object=teleplay
			 [params:params]
		 }
		 
		 //电影信息
		 if(params.type=="3"){
			 def movieInstance = Movie.get(params.id)
			 if (!movieInstance) {
				 flash.message = message(code: 'default.not.found.message', args: [message(code: 'movie.label', default: 'Movie'), params.id])
				 redirect(action: "movieList")
				 return
			 }
			 params.active=active
			 def movie = marketdataService.getMovieInfo(movieInstance)
			 params.object=movie
			 [params:params]
		 }
    }
}

