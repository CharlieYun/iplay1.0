<div style="width: 98%;margin-left: 1%" >
	数据需求：
</div>
<g:if test="${viewAction=='view' }">
	<div style="width: 98%;margin-left: 1%" >
		<h3>电影票房</h3>
		<div style="background-color: #E6E4E4;margin-top: 5px;">
			<g:demandCheckTest groupName="movie_boxOffice" disabled="disabled" name="ids"  values="${codeRequirementIds }"/>
			<g:if test="${projectRequirement.cinemaIds != null }">
			 	<div class="form-group" id="cinemaDiv" style="display: none;width: 100%; ">
				    <label for="exampleInputName2" style="margin-left: 10px;">影院ID：</label>
			    	<input type="text" class="form-control" readonly="readonly" id="exampleInputName2" name="cinemaIds" value="${projectRequirement?.cinemaIds }">
			  	</div>
			</g:if>  
		</div>
		<h3>电影网络播放量</h3>
		<div style="background-color: #E6E4E4;margin-top: 5px;">
			<g:demandCheckTest groupName="teleplay_amount" disabled="disabled" name="ids"  values="${codeRequirementIds }"/>
		</div>
		<h3>影响力</h3>
		<div style="background-color: #E6E4E4;margin-top: 5px;">
			<g:demandCheckTest groupName="influence" disabled="disabled" name="ids"  values="${codeRequirementIds }"/>
		</div>
		<h3>口碑评估</h3>
		<div style="background-color: #E6E4E4;margin-top: 5px;">
			<g:demandCheckTest groupName="reputation" disabled="disabled" name="ids"  values="${codeRequirementIds }"/>
		</div>
		<h3>主创及表演评估</h3>
		<h3>导演:</h3>
		<div style="background-color: #E6E4E4;margin-top: 5px;">
			<g:demandCheckTest name="ids" disabled="disabled" groupName="direction"  values="${codeRequirementIds }"/>
		</div>
		<h3>编剧:</h3>
		<div style="background-color: #E6E4E4;margin-top: 5px;">
			<g:demandCheckTest name="ids" disabled="disabled" groupName="scriptwriter"  values="${codeRequirementIds }"/>
		</div>
		<h3>演员:</h3>
		<div style="background-color: #E6E4E4;margin-top: 5px;">
			<g:demandCheckTest name="ids" disabled="disabled" groupName="comedienne"  values="${codeRequirementIds }"/>
		</div>
	</div>	
</g:if>
<g:else>
	<div style="width: 98%;margin-left: 1%" >
		<h3>电影票房</h3>
		<div style="background-color: #E6E4E4;margin-top: 5px;">
			<g:demandCheckTest groupName="movie_boxOffice" name="ids" method="onclick='toJudgeIsCinema()'" values="${codeRequirementIds }"/>
			<g:if test="${projectRequirement == null }">
				 <div class="form-group" id="cinemaDiv" style="display: none;width: 100%; ">
				    <label for="exampleInputName2" style="margin-left: 10px;">影院ID：</label>
				    <input type="text" class="form-control" id="exampleInputName2" name="cinemaIds" placeholder="请输入影院的ID，以','分割">
				  </div>
			 </g:if>
			 <g:elseif test="${projectRequirement.cinemaIds == null }">
		 		<div class="form-group" id="cinemaDiv" style="display: none;width: 100%; ">
				    <label for="exampleInputName2" style="margin-left: 10px;">影院ID：</label>
				    <input type="text" class="form-control" id="exampleInputName2" name="cinemaIds" placeholder="请输入影院的ID，以','分割">
			 	</div>
			 </g:elseif> 
			 <g:else>
			 	<div class="form-group" id="cinemaDiv" style="width: 100%; ">
				    <label for="exampleInputName2" style="margin-left: 10px;">影院ID：</label>
				    <input type="text" class="form-control" id="exampleInputName2" name="cinemaIds" value="${projectRequirement?.cinemaIds }">
			 	</div>
			 </g:else>
		</div>
		<h3>电影网络播放量</h3>
		<div style="background-color: #E6E4E4;margin-top: 5px;">
			<g:demandCheckTest groupName="teleplay_amount" name="ids"  values="${codeRequirementIds }"/>
		</div>
		<h3>影响力</h3>
		<div style="background-color: #E6E4E4;margin-top: 5px;">
			<g:demandCheckTest groupName="influence" name="ids"  values="${codeRequirementIds }"/>
		</div>
		<h3>口碑评估</h3>
		<div style="background-color: #E6E4E4;margin-top: 5px;">
			<g:demandCheckTest groupName="reputation" name="ids"  values="${codeRequirementIds }"/>
		</div>
		<h3>主创及表演评估:</h3>
		<h3>导演:</h3>
		<div style="background-color: #E6E4E4;margin-top: 5px;">
			<g:demandCheckTest name="ids" groupName="direction" values="${codeRequirementIds }"/>
		</div>
		<h3>编剧:</h3>
		<div style="background-color: #E6E4E4;margin-top: 5px;">
			<g:demandCheckTest name="ids" groupName="scriptwriter" values="${codeRequirementIds }"/>
		</div>
		<h3>演员:</h3>
		<div style="background-color: #E6E4E4;margin-top: 5px;">
			<g:demandCheckTest name="ids" groupName="comedienne" values="${codeRequirementIds }"/>
		</div>
		
	</div>
</g:else>