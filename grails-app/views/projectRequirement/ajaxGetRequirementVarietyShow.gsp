<g:if test="${viewAction=='view' }">
<div style="width: 98%;margin-left: 1%" >
数据需求:<br>

	<h3>收视率:</h3>
	<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="ratings" disabled="disabled" key="ratings" values="${codeRequirementIds }"  />
	</div>
	<h3>市场份额:</h3>
	<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="portion" disabled="disabled" key="portion" values="${codeRequirementIds }"  />
	</div>
	<%--
	综艺观众构成：<br>
	<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="teleplayAudience" disabled="disabled" key="teleplay_audience" values="${codeRequirementIds }" />
	</div>
	--%>
	<h3>网络播放量：</h3>
	<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="entertainmentAmount" disabled="disabled" key="entertainment_amount" values="${codeRequirementIds }" />
	</div>
	<h3>影响力：</h3>
	<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="influence" disabled="disabled" key="influence" values="${codeRequirementIds }" />
	</div>
	<h3>口碑评估：</h3>
	<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="reputation" disabled="disabled" key="reputation" values="${codeRequirementIds }" />
	</div>
	<h3>主创及表演评估：</h3>
	<h3>主持人：</h3>
	<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="hoster" disabled="disabled" key="hoster" values="${codeRequirementIds }" />
	</div>
	<h3>嘉宾：</h3>
	<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="guester" disabled="disabled" key="guester" values="${codeRequirementIds }" />
	</div>
	<%--<h3>主创及表演评估：</h3>
	<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="entertainmentWritten" disabled="disabled" key="entertainment_written" values="${codeRequirementIds }" />
	</div>
--%></div>
</g:if>
<g:elseif test="${viewAction=='edit' }">
<div style="width: 98%;margin-left: 1%" >
数据需求:<br>
	
	<h3>收视率:</h3>
	<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="ratings" key="ratings" values="${codeRequirementIds }"  />
	</div>
	<h3>市场份额:</h3>
	<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="portion" key="portion" values="${codeRequirementIds }"  />
	</div>
	网络播放量：<br>
	<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="entertainmentAmount" key="entertainment_amount" values="${codeRequirementIds }" />
	</div>
	<h3>影响力：</h3>
	<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="influence" key="influence" values="${codeRequirementIds }" />
	</div>
	<h3>口碑评估：</h3>
	<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="reputation" key="reputation" values="${codeRequirementIds }" />
	</div>
	<h3>主创及表演评估：</h3>
	<h3>主持人：</h3>
	<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="hoster" key="hoster" values="${codeRequirementIds }" />
	</div>
	<h3>嘉宾：</h3>
	<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="guester" key="guester" values="${codeRequirementIds }" />
	</div>
	<%--<h3>主创及表演评估：</h3>
	<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="entertainmentWritten" key="entertainment_written" values="${codeRequirementIds }" />
	</div>
--%></div>
</g:elseif>
<g:else>
<div style="width: 98%;margin-left: 1%" >
数据需求:<br>
	
	<h3>收视率:</h3>
	<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="ratings" key="ratings" values="${codeRequirementIds }"  />
	</div>
	<h3>市场份额:</h3>
	<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="portion" key="portion" values="${codeRequirementIds }"  />
	</div>
	<h3>网络播放量：</h3>
	<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="entertainmentAmount" key="entertainment_amount"  />
	</div>
	<h3>影响力：</h3>
	<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="influence" key="influence" />
	</div>
	<h3>口碑评估：</h3>
	<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="reputation" key="reputation" />
	</div>
	<h3>主创及表演评估：</h3>
	<h3>主持人：</h3>
	<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="hoster" key="hoster" />
	</div>
	<h3>嘉宾：</h3>
	<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="guester" key="guester" />
	</div>
	<%--<h3>主创及表演评估：</h3>
	<div style="background-color: #E6E4E4;margin-top: 5px;">
		<g:demandCheck name="entertainmentWritten" key="entertainment_written" />
	</div>
--%></div>
</g:else>