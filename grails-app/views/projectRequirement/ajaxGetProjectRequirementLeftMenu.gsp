		
		<a href="/iplay/project/projectExecute">
			<img alt="" src="../images/projectHome.png"  />
		</a>
		<button type="button" style="margin-top: 40px;" onclick="javascript:window.location.href='${ grailsApplication.config.grails.app.name}/projectRequirement/projectRequirementList?projectId=${params.projectId }'" class="btn btn-primary">项目所有需求</button>
		<br />
		<g:if test="${session?.isTechincalSupportAccount == false}">
			<button type="button" style="margin-top: 10px;" onclick="javascript:window.location.href='${ grailsApplication.config.grails.app.name}/projectRequirement/projectRequirementForMyCreate?projectId=${params.projectId }'"  class="btn btn-primary">属于我的需求</button>
			<br />
			<button type="button" style="margin-top: 10px;" onclick="javascript:window.location.href='${ grailsApplication.config.grails.app.name}/projectRequirement/projectRequirementForUnderway?projectId=${params.projectId }'"  class="btn btn-primary">进行中的需求</button>
			<br />
			<button type="button" style="margin-top: 10px;" onclick="javascript:window.location.href='${ grailsApplication.config.grails.app.name}/projectRequirement/projectRequirementForOver?projectId=${params.projectId }'"  class="btn btn-primary">已完成的需求</button>
			<br />
			<button type="button" style="margin-top: 10px;" onclick="javascript:window.location.href='${ grailsApplication.config.grails.app.name}/projectRequirement/projectRequirementForRepeatRun?projectId=${params.projectId }'"  class="btn btn-primary">重跑中的需求</button>
			<br />
			<button type="button" style="margin-top: 40px;" onclick="javascript:window.location.href='${ grailsApplication.config.grails.app.name}/projectRequirement/projectRequirementTemplate?projectId=${params.projectId }'"  class="btn btn-primary">数据需求模板</button>
			<br />
		</g:if>
		<g:else>
			<button type="button" style="margin-top: 10px;" onclick="javascript:window.location.href='${ grailsApplication.config.grails.app.name}/projectRequirement/projectRequirementForUnderway?projectId=${params.projectId }'"  class="btn btn-primary">进行中的需求</button>
			<br />
			<button type="button" style="margin-top: 10px;" onclick="javascript:window.location.href='${ grailsApplication.config.grails.app.name}/projectRequirement/projectRequirementForOver?projectId=${params.projectId }'"  class="btn btn-primary">已完成的需求</button>
			<br />
			<button type="button" style="margin-top: 40px;" onclick="javascript:window.location.href='${ grailsApplication.config.grails.app.name}/projectRequirement/projectRequirementForCheck?projectId=${params.projectId }'"  class="btn btn-primary">待审核的需求</button>
		<br />
		</g:else>
