<g:if test="${viewAction=='view' }">
<div style="width: 98%;margin-left: 1%" >
数据需求:
<h3>收视率:</h3>
	<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="ratings" disabled="disabled" key="ratings" values="${codeRequirementIds }"  />
	</div>
<h3>市场份额:</h3>
	<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="portion" disabled="disabled" key="portion" values="${codeRequirementIds }"  />
	</div>
<%--
电视观众构成:
	<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="teleplayAudience" disabled="disabled" key="teleplay_audience" values="${codeRequirementIds }"  />
	</div>
		--%>
<h3>网络播放量:</h3>
	<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="teleplayAmount" disabled="disabled" key="teleplay_amount" values="${codeRequirementIds }"  />
	</div>
<h3>影响力:</h3>
	<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="influence" disabled="disabled" key="influence" values="${codeRequirementIds }"  />
	</div>
<h3>口碑评估:</h3>
	<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="reputation" disabled="disabled" key="reputation" values="${codeRequirementIds }"  />
	</div>
<h3>主创及表演评估:</h3>
<h3>导演:</h3>
<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="direction" disabled="disabled" key="direction" values="${codeRequirementIds }" />
	</div>
<h3>编剧:</h3>
<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="scriptwriter" disabled="disabled" key="scriptwriter" values="${codeRequirementIds }" />
	</div>
<h3>演员:</h3>
<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="comedienne" disabled="disabled" key="comedienne" values="${codeRequirementIds }" />
	</div>
<%--<h3>主创及表演评估:</h3>
	<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="written" disabled="disabled" key="written" values="${codeRequirementIds }"  />
	</div>
--%></div>
</g:if>
<g:elseif test="${viewAction=='edit' }">
<div style="width: 98%;margin-left: 1%" >
数据需求:
<h3>收视率:</h3>
	<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="ratings"  key="ratings" values="${codeRequirementIds }"  />
	</div>
<h3>市场份额:</h3>
	<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="portion" key="portion" values="${codeRequirementIds }"  />
	</div>
<%--
电视观众构成:
	<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="teleplayAudience" key="teleplay_audience" />
	</div>
	--%>
<h3>网络播放量:</h3>
	<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="teleplayAmount" key="teleplay_amount" values="${codeRequirementIds }" />
	</div>
<h3>影响力:</h3>
	<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="influence" key="influence" values="${codeRequirementIds }" />
	</div>
<h3>口碑评估:</h3>
	<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="reputation" key="reputation" values="${codeRequirementIds }" />
	</div>
	
<h3>主创及表演评估:</h3>
<h3>导演:</h3>
<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="direction" key="direction" values="${codeRequirementIds }" />
	</div>
<h3>编剧:</h3>
<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="scriptwriter" key="scriptwriter" values="${codeRequirementIds }" />
	</div>
<h3>演员:</h3>
<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="comedienne" key="comedienne" values="${codeRequirementIds }" />
	</div>
	
<%--<h3>主创及表演评估:</h3>
	<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="written" key="written" values="${codeRequirementIds }" />
	</div>
</div>
--%></g:elseif>
<g:else>
<div style="width: 98%;margin-left: 1%" >
数据需求:
<h3>收视率:</h3>
	<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="ratings" key="ratings" values="${codeRequirementIds }"  />
	</div>
<h3>市场份额:</h3>
	<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="portion" key="portion" values="${codeRequirementIds }"  />
	</div>
<%--	
电视观众构成:
	<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="teleplayAudience" key="teleplay_audience" />
	</div>
	--%>
<h3>网络播放量:</h3>
	<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="teleplayAmount" key="teleplay_amount" />
	</div>
<h3>影响力:</h3>
	<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="influence" key="influence" />
	</div>
<h3>口碑评估:</h3>
	<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="reputation" key="reputation" />
	</div>
<h3>主创及表演评估:</h3>
<h3>导演:</h3>
<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="direction" key="direction" />
	</div>
<h3>编剧:</h3>
<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="scriptwriter" key="scriptwriter" />
	</div>
<h3>演员:</h3>
<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="comedienne" key="comedienne" />
	</div>

<%--<h3>主创及表演评估:</h3>
	<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="written" key="written" />
	</div>
--%></div>
</g:else>