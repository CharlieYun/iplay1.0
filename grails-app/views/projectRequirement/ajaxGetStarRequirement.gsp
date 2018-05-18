<div style="width: 98%;margin-left: 1%" >
	数据需求：
</div>
<g:if test="${viewAction=='view' }">
	<div style="width: 98%;margin-left: 1%" >
		<h3>影响力</h3>
		<div style="background-color: #E6E4E4;margin-top: 5px;">
			<g:demandCheckTest groupName="influence" disabled="disabled" name="ids" values="${codeRequirementIds }"/>
		</div>
		<h3>口碑评估</h3>
		<div style="background-color: #E6E4E4;margin-top: 5px;">
			<g:demandCheckTest groupName="reputation" disabled="disabled" name="ids" values="${codeRequirementIds }"/>
		</div>
	</div>
</g:if>
<g:else>
	<div style="width: 98%;margin-left: 1%" >
		<h3>影响力</h3>
		<div style="background-color: #E6E4E4;margin-top: 5px;">
			<g:demandCheckTest groupName="influence" name="ids"  values="${codeRequirementIds }"/>
		</div>
		<h3>口碑评估</h3>
		<div style="background-color: #E6E4E4;margin-top: 5px;">
			<g:demandCheckTest groupName="reputation" name="ids"  values="${codeRequirementIds }"/>
		</div>
	</div>
</g:else>