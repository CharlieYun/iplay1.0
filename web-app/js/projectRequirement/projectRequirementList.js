function viewProjectRequirement(id){
		var url=projectURI.defcon+'/projectRequirement/viewProjectRequirement?projectRequirementId='+id;
		window.open(url, 'addwindow', 'height=800, width=680, top=40, left='+ (window.screen.availWidth-10-1000)/2+', toolbar=no, menubar=no, scrollbars=yes, resizable=no, location=no, status=no')   //该句写成一行代码
	}
	function toShowProjectRequirement(){
		var url=projectURI.defcon+'/projectRequirement/addProjectRequirement?projectId='+$("#projectId").val();
		window.open(url, 'addwindow', 'height=800, width=680, top=40, left='+ (window.screen.availWidth-10-1000)/2+', toolbar=no, menubar=no, scrollbars=yes, resizable=no, location=no, status=no')   //该句写成一行代码
	}
	function upload(id){
		window.open(projectURI.defcon+'/projectRequirement/uploadProjectRequirement?requirement_id='+id, 'editwindow', 'height=200, width=680, top=40, left='+ (window.screen.availWidth-10-1000)/2+', toolbar=no, menubar=no, scrollbars=yes, resizable=no, location=no, status=no')   //该句写成一行代码
		
	}
	function delProjectRequirementInfo(id){
		if(confirm("确定要删除此条记录吗？")){
			var that = this ;
			$.ajax({
				type:"post",
				url:projectURI.defcon+"/projectRequirement/delProjectRequirementInfo",
				data: {project_requirement_id:id},
				success:function(msg){
					alert(msg);
					location.reload();
				}
			});
		}
	}
	$(function(){
		$.ajax({
			type:"post",
			url:projectURI.defcon+"/projectRequirement/ajaxGetProjectRequirementLeftMenu?projectId="+$("#projectId").val(),
			async:false,
			success:function(data){
				$("#leftMenu").html(data);
			}
		})	
	})
	function saveForProjectRequirementTemplate(project_requirement_id){
		if(confirm("确定要将此需求保存为模板么？")){
			var that = this ;
			$.ajax({
				type:"post",
				url:projectURI.defcon+"/projectRequirement/saveForProjectRequirementTemplate",
				data: {project_requirement_id:project_requirement_id},
				success:function(msg){
					alert(msg);
					location.reload();
				}
			});
		}
	}

	// 技术人员驳回请求
	function overruleProjectRequirement(id){
		var url = projectURI.defcon+"/projectRequirement/overruleProjectRequirement?projectRequirementId="+id ;
		window.open(url, 'overRuleWindow', 'height=600, width=680, top=40, left='+ (window.screen.availWidth-10-1000)/2+', toolbar=no, menubar=no, scrollbars=yes, resizable=no, location=no, status=no')   //该句写成一行代码
	}

	// 查看技术人员驳回请求的原因
	function viewOverruleProjectRequirement(id){
		var url = projectURI.defcon+"/projectRequirement/viewOverruleProjectRequirement?projectRequirementId="+id ;
		window.open(url, 'overRuleWindow', 'height=600, width=680, top=40, left='+ (window.screen.availWidth-10-1000)/2+', toolbar=no, menubar=no, scrollbars=yes, resizable=no, location=no, status=no')   //该句写成一行代码
	}

	// 重跑需求
	function againProjectRequirement(id){
		var url = projectURI.defcon+"/projectRequirement/againProjectRequirement?projectRequirementId="+id ;
		window.open(url, 'againProjectRequirementWindow', 'height=600, width=680, top=40, left='+ (window.screen.availWidth-10-1000)/2+', toolbar=no, menubar=no, scrollbars=yes, resizable=no, location=no, status=no')   //该句写成一行代码
	}

	// 查看重跑原因
	function viewAgainProjectRequirement(id){
		var url = projectURI.defcon+"/projectRequirement/viewAgainProjectRequirement?projectRequirementId="+id ;
		window.open(url, 'viewAgainWindow', 'height=600, width=680, top=40, left='+ (window.screen.availWidth-10-1000)/2+', toolbar=no, menubar=no, scrollbars=yes, resizable=no, location=no, status=no')   //该句写成一行代码
	}
	
	//编辑需求信息
	function editProjectRequirement(requirementId){
		var url=projectURI.defcon+"/projectRequirement/editProjectRequirement?requirementId="+requirementId;
		window.open(url, 'addwindow', 'height=800, width=680, top=40, left='+ (window.screen.availWidth-10-1000)/2+', toolbar=no, menubar=no, scrollbars=yes, resizable=no, location=no, status=no')   //该句写成一行代码
	}
	
	function updateProjectRequirementStatus(id,status,message){
		if(confirm(message)){
			var that = this ;
			$.ajax({
				type:"post",
				url:projectURI.defcon+"/projectRequirement/updateProjectRequirementStatus",
				data: {projectRequirementId:id,status},
				success:function(msg){
					alert(msg);
					location.reload();
				}
			});
		}
	}
	function checkBoxValue(){
		var chk_value =[]; 
		$('input[name="requirement_ids"]:checked').each(function(){ 
			//alert($(this).val())
			chk_value.push($(this).val()); 
		});
		
		if(chk_value.length==0){
			alert('你还没有选择任何内容！');
			return ;
		}
		window.location.href = projectURI.defcon+"/projectRequirement/getZip?requirement_ids="+chk_value ;
	}