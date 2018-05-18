<div style="width: 98%;margin-left: 1%" >
	数据需求：
</div>
<g:if test="${viewAction=='view' }">
	<textarea style="width: 98%; margin-left:1%; height: 400px;" readonly="readonly" id="special_requirement_mes" name="special_requirement_mes">${projectRequirement?.special_requirement_mes }</textarea>
</g:if>
<g:else>
	<textarea style="width: 98%; margin-left:1%; height: 400px;" id="special_requirement_mes" name="special_requirement_mes">${projectRequirement?.special_requirement_mes }</textarea>
</g:else>
