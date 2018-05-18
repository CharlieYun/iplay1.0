<g:if test="${teleplayInfo }">
	<div class="box">电视剧总数:${teleplayInfo.size() }</div>
	<table class="table table-striped table-bordered bootstrap-datatable responsive">
		<thead>
			<tr>
				<th>剧名</th>
				<th>分类</th>
				<th>上星首播平台</th>
				<th class="sort">
					上星首播时间
					<i class="glyphicon"></i>
				</th>
				<th>网络播放平台</th>
				<th>网络首播时间</th>
				<th class="sort">
					平均收视率
					<i class="glyphicon"></i>
				</th>
				<th class="sort">
					播放量
					<i class="glyphicon"></i>
				</th>
				<th class="sort">
					好评率
					<i class="glyphicon"></i>
				</th>
				<th>导演</th>
				<th>编剧</th>
				<th>演员</th>
			</tr>
		</thead>
		<tbody>
			<g:each in="${teleplayInfo }" var="it" status="i">
				<tr style="<g:if test="${i>10 }">display:none</g:if>" class="actors">
					<td><a href="${grailsApplication.config.grails.app.name }/hisThinkTank/show/${it?.id }?type=1&objectType=5">${it?.name }</a></td>
					<td>${it?.themeName }</td>
					<td>${it?.channelName }</td>
					<td>${it?.publishTime }</td>
					<td>${it?.premName }</td>
					<td>${it?.premDate }</td>
					<td>${it?.avgRateNum }</td>
					<td>${it?.totalPlayNum }</td>
					<td>${it?.positivePercent }</td>
					<td>${it?.director }</td>
					<td>${it?.scriptwriter }</td>
					<td>
						<g:if test="${it?.artistList }">
							<g:each in="${0..(it?.artistList.size() > 5?4:(it?.artistList.size()-1)) }" var="al" status="j">
								${it?.artistList[al]?.aName }/
							</g:each>
						</g:if>
					</td>
				</tr>
			</g:each>
			<g:if test="${teleplayInfo.size() > 11 }">
					<tr>
						<td colspan="12" style="text-align: center;">
							<a href="javascript:void(0)" onclick="showOrHideMoreActorInfo(this,'show')">电视剧信息<i class="glyphicon glyphicon-chevron-down"></i></a>
						</td>
					</tr>
				</g:if>
		</tbody>
	</table>
</g:if>
<g:else>
	<div style="width: 100%; height: 100%; text-align: center; line-height: 100px;">
		暂无数据!
	</div>
</g:else>