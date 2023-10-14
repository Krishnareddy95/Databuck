<head>
<style>
td.details-control {
	background:
		url('./assets/img/details_open.png')
		no-repeat center center;
	cursor: pointer;
}

td.shown {
	background:
		url('./assets/img/details_close.png')
		no-repeat center center;
}
	table.dataTable tbody th,
	table.dataTable tbody td {
		white-space: nowrap;
	}

	.dataTables_scrollBody{
            overflow-y: hidden !important;
   	 	}
</style>
</head>
<%@page import="com.databuck.service.RBACController"%>
<%@page import="com.databuck.bean.ListApplications"%>
<%@page import="java.util.List"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<jsp:include page="container.jsp" />
<!-- BEGIN CONTENT -->
<div class="page-content-wrapper">
	<!-- BEGIN CONTENT BODY -->
	<div class="page-content">
		<!-- BEGIN PAGE TITLE-->
		<!-- END PAGE TITLE-->
		<!-- END PAGE HEADER-->

		<div class="row">
			<div class="col-md-12">
				<div class="portlet light bordered">
				
					<div style="color:blue; padding-bottom:10px; padding-left:5px">
					<b>Note:</b> The execution timings of all jobs are in <b>UTC</b> timezone.
					</div>
					<!-- BEGIN EXAMPLE TABLE PORTLET-->
					<div class="portlet light bordered">
						<div class="portlet-title">
							<div class="caption font-red-sunglo">
								<span class="caption-subject bold "> Running
									Template/Validation Jobs </span>
							</div>
						</div>
						<div class="portlet-body" >
							<table  id="runningJobsTable_Id"
								class="table table-striped table-bordered table-hover dataTable no-footer"
								style="width: 100%;">
								<thead>
									<tr>
										<th>Task Type</th>
										<th>Validation Id/ Template Id</th>
										<th>Name</th>
										<th>Status</th>
										<th>Deploy Mode</th>										
										<th>Unique Id</th>
										<th>Process Id</th>
										<th>SparkApp Id</th>
										<th>Project Name</th>
										<th>Start Time</th>
										<th>Duration</th>
										<th>Overtime Job</th>
										<th>Triggered By Host</th>
										<th>Actions</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="runningappslistobj" items="${runningappslist}">
										<tr>
											<td>${runningappslistobj.taskType}</td>
											<td>${runningappslistobj.applicationId}</td>
											<td>${runningappslistobj.applicationName}</td>
											<td>${runningappslistobj.status}</td>
											<td>${runningappslistobj.deployMode}</td>											
											<td>${runningappslistobj.uniqueId}</td>
											<td>${runningappslistobj.processId}</td>
											<td>${runningappslistobj.sparkAppId}</td>
											<td>${runningappslistobj.projectName}</td>
											<td>${runningappslistobj.startTime}</td>
											<td>${runningappslistobj.fullDuration}</td>
											<td><c:choose>
													<c:when
														test="${runningappslistobj.jobDurationStatus == 'OVERTIME'}">
														<span class="label label-danger label-sm">Yes</span>
													</c:when>
													<c:otherwise>
														<span class="label label-success label-sm">No</span>
													</c:otherwise>
												</c:choose></td>
											<td>${runningappslistobj.triggeredByHost}</td>
											<td>
												<a href="javascript:killJob('${runningappslistobj.applicationId}', '${runningappslistobj.taskType}', '${runningappslistobj.processId}', '${runningappslistobj.deployMode}', '${runningappslistobj.sparkAppId}', '${runningappslistobj.uniqueId}')" data-toggle="confirmation" data-singleton="true">
                                                	<span class="label label-danger label-sm">Kill</span>
                                                </a>
											</td>
										</tr>

									</c:forEach>
								</tbody>

							</table>
						</div>
					</div>

					<div class="portlet light bordered">
						<div class="portlet-title">
							<div class="caption font-red-sunglo">
								<span class="caption-subject bold "> Queued Jobs </span> <span
									style="font-size: small;"><a
									 href="#" data-toggle="confirmation" data-singleton="true" class="clearQueuedJobsClass">(Clear Queue)</a></span>
							</div>
						</div>
						<div class="portlet-body">
							<table id="queuedJobsTable_Id"
								class="table table-striped table-bordered table-hover dataTable no-footer"
								style="width: 100%;">
								<thead>
									<tr>
										<th>Task Type</th>
										<th>Validation Id/ Template Id</th>
										<th>Name</th>
										<th>Unique Id</th>
										<th>Status</th>
										<th>Project Name</th>
										<th>Start Time</th>
										<th>Queued Duration</th>
										<th>Actions</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="queuedJobslistobj" items="${queuedJobslist}">
										<tr>
											<td>${queuedJobslistobj.taskType}</td>
											<td>${queuedJobslistobj.applicationId}</td>
											<td>${queuedJobslistobj.applicationName}</td>
											<td>${queuedJobslistobj.uniqueId}</td>
											<td>${queuedJobslistobj.status}</td>
											<td>${queuedJobslistobj.projectName}</td>
											<td>${queuedJobslistobj.startTime}</td>
											<td>${queuedJobslistobj.fullDuration}</td>
											<td>
												<a href="javascript:deleteJobFromQueue('${queuedJobslistobj.applicationId}', '${queuedJobslistobj.taskType}', '${queuedJobslistobj.uniqueId}')" data-toggle="confirmation" data-singleton="true">
                                                	<span class="label label-danger label-sm">Delete</span>
                                                </a>

											</td>
										</tr>

									</c:forEach>
								</tbody>

							</table>
						</div>
					</div>
					
					
					<div class="portlet light bordered">
						<div class="portlet-title">
							<div class="caption font-red-sunglo">
								<span class="caption-subject bold "> Completed Jobs </span>
							</div>
						</div>
						<div class="portlet-body">
							<table id="completedJobsTable_Id"
								class="table table-striped table-bordered table-hover dataTable no-footer"
								style="width: 100%;">
								<thead>
									<tr>
										<th>Task Type</th>
										<th>Validation Id/ Template Id</th>
										<th>Name</th>
										<th>Unique Id</th>
										<th>Status</th>
										<th>Deploy Mode</th>
										<th>Process Id</th>
										<th>SparkApp Id</th>
										<th>Triggered By Host</th>
										<th>Project Name</th>
										<th>Start Time</th>
										<th>End Time</th>
										<th>Duration</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="completedAppsListObj" items="${completedAppsList}">
										<tr>
											<td>${completedAppsListObj.taskType}</td>
											<td>${completedAppsListObj.applicationId}</td>
											<td>${completedAppsListObj.applicationName}</td>
											<td>${completedAppsListObj.uniqueId}</td>
											<td>${completedAppsListObj.status}</td>
											<td>${completedAppsListObj.deployMode}</td>
											<td>${completedAppsListObj.processId}</td>
											<td>${completedAppsListObj.sparkAppId}</td>
											<td>${completedAppsListObj.triggeredByHost}</td>
											<td>${completedAppsListObj.projectName}</td>
											<td>${completedAppsListObj.startTime}</td>
											<td>${completedAppsListObj.endTime}</td>
											<td>${completedAppsListObj.fullDuration}</td>
										</tr>
									</c:forEach>
								</tbody>

							</table>
						</div>
					</div>
					
					<div class="portlet light bordered">
						<div class="portlet-title">
							<div class="caption font-red-sunglo">
								<span class="caption-subject bold "> Schema Jobs </span>
							</div>
						</div>
						<div class="portlet-body">
							<table id="schemaJobListTable_Id"
								class="table table-striped table-bordered table-hover dataTable no-footer"
								style="width: 100%;">
								<thead>
									<tr>
										<th>&nbsp;&nbsp;&nbsp;&nbsp;</th>
										<th>Schema Id</th>
										<th>Schema Name</th>
										<th>Unique Id</th>
										<th>Status</th>
										<th>Deploy Mode</th>
                                        <th>Process Id</th>
                                        <th>SparkApp Id</th>
                                        <th>Project Name</th>
										<th>Created At</th>
										<th>Start Time</th>
										<th>End Time</th>
                                        <th>Duration</th>
                                        <th>Triggered By Host</th>
										<th>Action</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="schemaJobObj" items="${schemaJobsList}">
										<tr id="sch_${schemaJobObj.queueId}">
											<td class="details-control" id="sch_${schemaJobObj.queueId}"></td>
											<td>${schemaJobObj.idDataSchema}</td>
											<td>${schemaJobObj.schemaName}</td>
											<td>${schemaJobObj.uniqueId}</td>
											<td>${schemaJobObj.status}</td>
											<td>${schemaJobObj.deployMode}</td>
                                            <td>${schemaJobObj.processId}</td>
                                            <td>${schemaJobObj.sparkAppId}</td>
                                            <td>${schemaJobObj.projectName}</td>
											<td>${schemaJobObj.createdAt}</td>
											<td>${schemaJobObj.startTime}</td>
											<td>${schemaJobObj.endTime}</td>
                                            <td>${schemaJobObj.fullDuration}</td>
                                            <td>${schemaJobObj.triggeredByHost}</td>
											<td>
											<c:if test="${schemaJobObj.status != 'killed' && schemaJobObj.status != 'completed' && schemaJobObj.status != 'failed'}">
                                                <a href="javascript:killJob('${schemaJobObj.idDataSchema}', 'connection', '${schemaJobObj.processId}', '${schemaJobObj.deployMode}', '${schemaJobObj.sparkAppId}', '${schemaJobObj.uniqueId}')" data-toggle="confirmation" data-singleton="true">
                                                	<span class="label label-danger label-sm">Kill</span>
                                                </a>
                                            </c:if>
											</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</div>
					</div>

					<div class="portlet light bordered">
						<div class="portlet-title">
							<div class="caption font-red-sunglo">
								<span class="caption-subject bold "> AppGroup Jobs </span>
							</div>
						</div>
						<div class="portlet-body">
							<table id="appgroupJobListTable_Id"
								class="table table-striped table-bordered table-hover dataTable no-footer"
								style="width: 100%;">
								<thead>
									<tr>
										<th>&nbsp;&nbsp;&nbsp;&nbsp;</th>
										<th>AppGroup Id</th>
										<th>AppGroup Name</th>
										<th>Unique Id</th>
										<th>Status</th>
										<th>Deploy Mode</th>
                                        <th>Process Id</th>
                                        <th>SparkApp Id</th>
										<th>Created At</th>
										<th>Start Time</th>
										<th>End Time</th>
                                        <th>Duration</th>
                                        <th>Triggered By Host</th>
										<th>Action</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="appGroupJobObj" items="${appGroupJobsList}">
										<tr id="appgrp_${appGroupJobObj.queueId}">
											<td class="details-control"
												id="appgrp_${appGroupJobObj.queueId}"></td>
											<td>${appGroupJobObj.idAppGroup}</td>
											<td>${appGroupJobObj.appGroupName}</td>
											<td>${appGroupJobObj.uniqueId}</td>
											<td>${appGroupJobObj.status}</td>
											<td>${appGroupJobObj.deployMode}</td>
                                            <td>${appGroupJobObj.processId}</td>
                                            <td>${appGroupJobObj.sparkAppId}</td>
											<td>${appGroupJobObj.createdAt}</td>
											<td>${appGroupJobObj.startTime}</td>
											<td>${appGroupJobObj.endTime}</td>
                                            <td>${appGroupJobObj.fullDuration}</td>
                                            <td>${appGroupJobObj.triggeredByHost}</td>
                                            <td>
												<c:if test="${appGroupJobObj.status != 'killed' && appGroupJobObj.status != 'completed' && appGroupJobObj.status != 'failed'}">
												<a href="javascript:killJob('${appGroupJobObj.idAppGroup}', 'appgroup', '${appGroupJobObj.processId}', '${appGroupJobObj.deployMode}', '${appGroupJobObj.sparkAppId}', '${appGroupJobObj.uniqueId}')" data-toggle="confirmation" data-singleton="true">
                                                	<span class="label label-danger label-sm">Kill</span>
                                                </a>
                                                </c:if>
                                            </td>
										</tr>
									</c:forEach>
								</tbody>

							</table>
						</div>
					</div>
					
					<div class="portlet light bordered">
						<div class="portlet-title">
							<div class="caption font-red-sunglo">
								<span class="caption-subject bold "> Project Jobs </span>
							</div>
						</div>
						<div class="portlet-body">
							<table id="projectJobListTable_Id"
								class="table table-striped table-bordered table-hover dataTable no-footer"
								style="width: 100%;">
								<thead>
									<tr>
										<th>&nbsp;&nbsp;&nbsp;&nbsp;</th>
										<th>Project Id</th>
										<th>Project Name</th>
										<th>Unique Id</th>
										<th>Status</th>
										<th>Deploy Mode</th>
                                        <th>Process Id</th>
                                        <th>SparkApp Id</th>
										<th>Created At</th>
										<th>Start Time</th>
										<th>End Time</th>
                                        <th>Duration</th>
                                        <th>Triggered By Host</th>
										<th>Action</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="projectJobObj" items="${projectJobsList}">
										<tr id="prj_${projectJobObj.queueId}">
											<td class="details-control" id="prj_${projectJobObj.queueId}"></td>
											<td>${projectJobObj.projectId}</td>
											<td>${projectJobObj.projectName}</td>
											<td>${projectJobObj.uniqueId}</td>
											<td>${projectJobObj.status}</td>
											<td>${projectJobObj.deployMode}</td>
                                            <td>${projectJobObj.processId}</td>
                                            <td>${projectJobObj.sparkAppId}</td>
											<td>${projectJobObj.createdAt}</td>
											<td>${projectJobObj.startTime}</td>
											<td>${projectJobObj.endTime}</td>
                                            <td>${projectJobObj.fullDuration}</td>
                                            <td>${projectJobObj.triggeredByHost}</td>
											<td>
											<c:if test="${projectJobObj.status != 'killed' && projectJobObj.status != 'completed' && projectJobObj.status != 'failed'}">
                                                <a href="javascript:killJob('${projectJobObj.projectId}', 'project', '${projectJobObj.processId}', '${projectJobObj.deployMode}', '${projectJobObj.sparkAppId}', '${projectJobObj.uniqueId}')" data-toggle="confirmation" data-singleton="true">
                                                	<span class="label label-danger label-sm">Kill</span>
                                                </a>
                                            </c:if>
											</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</div>    						
					</div>

					<div class="portlet light bordered">
	                    <div class="portlet-title">
	                        <div class="caption font-red-sunglo">
	                            <span class="caption-subject bold "> Domain Jobs </span>
	                        </div>
	                    </div>
	                    <div class="portlet-body">
	                        <table id="domainJobListTable_Id"
	                            class="table table-striped table-bordered table-hover dataTable no-footer"
	                            style="width: 100%;">
	                            <thead>
	                                <tr>
	                                    <th>&nbsp;&nbsp;&nbsp;&nbsp;</th>
	                                    <th>Domain Id</th>
	                                    <th>Domain Name</th>
	                                    <th>Unique Id</th>
	                                    <th>Status</th>
	                                    <th>Deploy Mode</th>
	                                    <th>Process Id</th>
	                                    <th>SparkApp Id</th>
	                                    <th>Created At</th>
	                                    <th>Start Time</th>
	                                    <th>End Time</th>
	                                    <th>Duration</th>
	                                    <th>Triggered By Host</th>
	                                    <th>Action</th>
	                                </tr>
	                            </thead>
	                            <tbody>
	                                <c:forEach var="domainJobObj" items="${domainJobsList}">
	                                    <tr id="dmn_${domainJobObj.queueId}">
	                                        <td class="details-control" id="dmn_${domainJobObj.queueId}"></td>
	                                        <td>${domainJobObj.domainId}</td>
	                                        <td>${domainJobObj.domainName}</td>
	                                        <td>${domainJobObj.uniqueId}</td>
	                                        <td>${domainJobObj.status}</td>
	                                        <td>${domainJobObj.deployMode}</td>
	                                        <td>${domainJobObj.processId}</td>
	                                        <td>${domainJobObj.sparkAppId}</td>
	                                        <td>${domainJobObj.createdAt}</td>
	                                        <td>${domainJobObj.startTime}</td>
	                                        <td>${domainJobObj.endTime}</td>
	                                        <td>${domainJobObj.fullDuration}</td>
	                                        <td>${domainJobObj.triggeredByHost}</td>
	                                        <td>
	                                        <c:if test="${domainJobObj.status != 'killed' && domainJobObj.status != 'completed' && domainJobObj.status != 'failed'}">
	                                           <a href="javascript:killJob('${domainJobObj.domainId}', 'domain', '${domainJobObj.processId}', '${domainJobObj.deployMode}', '${domainJobObj.sparkAppId}', '${domainJobObj.uniqueId}')" data-toggle="confirmation" data-singleton="true">
                                               	<span class="label label-danger label-sm">Kill</span>
                                               </a>
	                                        </c:if>
	                                        </td>
	                                    </tr>
	                                </c:forEach>
	                            </tbody>
	                        </table>
	                    </div>
	                </div>

				<!-- END EXAMPLE TABLE PORTLET-->
				<!-- Image loader -->
				<div id="propModal" class="modal" role="dialog" style="top: 200px;">
					<div class="modal-dialog" style="width:400px">
						<!-- Modal content-->
						<div class="modal-content">
							<div class="modal-body">
								<img alt="" src="./assets/img/reload.gif" width='32px' height='30px'
								style="padding-right:10px;" /><b>Please wait kill job is in progress..</b>
							</div>
						</div>

					</div>
				</div>
				<!-- Image loader -->
			</div>
		</div>
	</div>
</div>
<script src="./assets/global/plugins/jquery.min.js"
	type="text/javascript">
</script>
<script
	src="./assets/global/plugins/jquery.dataTables.min.js"
	type="text/javascript">
</script>

<script>
$(document).ready(function() {
	$('#runningJobsTable_Id').dataTable({
	  	order: [[ 9, "desc" ]],
	  	"scrollX": true
    });
	$('#queuedJobsTable_Id').dataTable({
	  	order: [[ 6, "asc" ]],
	  	"scrollX": true
	});
	$('#completedJobsTable_Id').dataTable({
	  	order: [[ 11, "desc" ]],
	  	"scrollX": true
	});
	$('#appgroupJobListTable_Id').dataTable({
	  	order: [[ 8, "desc" ]],
	  	"scrollX": true
	});
	$('#schemaJobListTable_Id').dataTable({
	  	order: [[ 9, "desc" ]],
	  	"scrollX": true
	});
	$('#projectJobListTable_Id').dataTable({
	  	order: [[ 8, "desc" ]],
	  	"scrollX": true
	});
	$('#domainJobListTable_Id').dataTable({
    	  	order: [[ 8, "desc" ]],
    	  	"scrollX": true
    });
	
	$('.table').on('page.dt', function() {
		  setTimeout(
		    function() {
		      $("[data-toggle=confirmation]").confirmation({container:"body",btnOkClass:"btn btn-sm btn-success",btnCancelClass:"btn btn-sm btn-danger",onConfirm:function(event, element) { element.trigger('confirm'); }});
		    }, 500);
	});
	 
    $('.clearQueuedJobsClass').on('confirmed.bs.confirmation', function () {
        		clearAllQueuedJobs();
    });
    
});

$('#schemaJobListTable_Id').on('click','td.shown', function () {
	  var td = $(this).closest('td');
	   var row_id = $(this).attr("id");
	   
	   // Get the Inner row id
	   var innerTableId=row_id+"_inner_row_Id";
	   
	   // Hide the row
	    $('#'+innerTableId).addClass("hidden");
	   
	   // Change the icon
	   td.removeClass('shown');
	   td.addClass('details-control');
	   
	   // Remove the row
	   $('#schemaJobListTable_Id tbody').find('#'+innerTableId).remove();
	   
} );

$('#schemaJobListTable_Id').on('click','td.details-control', function () {
	   var td = $(this).closest('td');
	   var row_id = $(this).attr("id");
	   var idDataSchema = $(this).closest("tr").find('td:eq(1)').text();
	   var uniqueId = $(this).closest("tr").find('td:eq(3)').text();
	   
	   td.removeClass('details-control');
	   td.addClass('shown');
	   
	   var innerRowId=row_id+"_inner_row_Id";
	   var innerTableId=row_id+"_inner_table_Id";
	   
	   console.log("idDataSchema: "+idDataSchema);
	   console.log("uniqueId: "+uniqueId);
	   
	   var formdata = {
			   idDataSchema : idDataSchema,
		   uniqueId : uniqueId
	   };
	   
	   var innerRows = "";
	   var newRowData = "";
	   $.ajax({
			url : './getSchemaJobAssociatedTemplates',
			type : 'POST',
			headers: { 'token':$("#token").val()},
			datatype : 'json',
			data : formdata,
			success : function(obj) {
						$(obj).each(
						   function(i, item) {
							innerRows = innerRows + "			<tr>";
							innerRows = innerRows + "				<td>"+item.applicationId+"</td>";
							innerRows = innerRows + "				<td>"+item.applicationName+"</td>";
							innerRows = innerRows + "				<td>"+item.uniqueId+"</td>";
							innerRows = innerRows + "				<td>"+item.status+"</td>";
							innerRows = innerRows + "				<td>"+item.processId+"</td>";
							innerRows = innerRows + "				<td>"+item.sparkAppId+"</td>";
							innerRows = innerRows + "				<td>"+formatDate(item.startTime)+"</td>";
							innerRows = innerRows + "			</tr>";
						});
				
				   // Prepare inner table
				   newRowData = "<tr id=\""+innerRowId+"\""; 
				   newRowData = newRowData + "style=\"background-color: #ecf8ec\" >";
				   newRowData = newRowData + "<td colspan=\"14\">";
				   newRowData = newRowData + "	<table id=\""+innerTableId+"\" ";
				   newRowData = newRowData + "		class=\"table table-striped table-bordered table-hover dataTable no-footer\"";
				   newRowData = newRowData + "		style=\"width: 100%;\">";
				   newRowData = newRowData + "		<thead>";
				   newRowData = newRowData + "			<tr>";
				   newRowData = newRowData + "				<th>Template Id</th>";
				   newRowData = newRowData + "				<th>Name</th>";
				   newRowData = newRowData + "				<th>unique Id</th>";
				   newRowData = newRowData + "				<th>Status</th>";
				   newRowData = newRowData + "				<th>Process Id</th>";
				   newRowData = newRowData + "				<th>SparkApp Id</th>";
				   newRowData = newRowData + "				<th>Start Time</th>";
				   newRowData = newRowData + "			</tr>";
				   newRowData = newRowData + "		</thead>";
				   newRowData = newRowData + "		<tbody>";
				   newRowData = newRowData + innerRows
				   newRowData = newRowData + "		</tbody>";
				   newRowData = newRowData + "	</table>";
				   newRowData = newRowData + "</td>";
				   newRowData = newRowData + "</tr>";
				   
				 //Add Inner table in new row
				   $('#schemaJobListTable_Id tbody').find('#'+row_id).after(newRowData);
				   
				   $('#'+innerTableId).dataTable({
					  	order: [[ 1, "desc" ]],
					  	"scrollX": true
				   });
			},
			error : function() {
			},
		});
});

$('#appgroupJobListTable_Id').on('click','td.shown', function () {
	   var td = $(this).closest('td');
	   var row_id = $(this).attr("id");
	   
	   // Get the Inner row id
	   var innerTableId=row_id+"_inner_row_Id";
	   
	   // Hide the row
	    $('#'+innerTableId).addClass("hidden");
	   
	   // Change the icon
	   td.removeClass('shown');
	   td.addClass('details-control');
	   
	   // Remove the row
	   $('#appgroupJobListTable_Id tbody').find('#'+innerTableId).remove();
	   
} );

$('#appgroupJobListTable_Id').on('click','td.details-control', function () {
	   var td = $(this).closest('td');
	   var row_id = $(this).attr("id");
	   var idAppGroup = $(this).closest("tr").find('td:eq(1)').text();
	   var uniqueId = $(this).closest("tr").find('td:eq(3)').text();
	   
	   td.removeClass('details-control');
	   td.addClass('shown');
	   
	   var innerRowId=row_id+"_inner_row_Id";
	   var innerTableId=row_id+"_inner_table_Id";
	   
	   console.log("idAppGroup: "+idAppGroup);
	   console.log("uniqueId: "+uniqueId);
	   
	   var formdata = {
		   idAppGroup : idAppGroup,
		   uniqueId : uniqueId
	   };
	   
	   var innerRows = "";
	   var newRowData = "";
	   $.ajax({
			url : './getAppGroupAssociatedValidations',
			type : 'POST',
			headers: { 'token':$("#token").val()},
			datatype : 'json',
			data : formdata,
			success : function(obj) {
						$(obj).each(
						   function(i, item) {
							innerRows = innerRows + "			<tr>";
							innerRows = innerRows + "				<td>"+item.applicationId+"</td>";
							innerRows = innerRows + "				<td>"+item.applicationName+"</td>";
							innerRows = innerRows + "				<td>"+item.uniqueId+"</td>";
							innerRows = innerRows + "				<td>"+item.status+"</td>";
							innerRows = innerRows + "				<td>"+item.processId+"</td>";
							innerRows = innerRows + "				<td>"+item.sparkAppId+"</td>";
							innerRows = innerRows + "				<td>"+formatDate(item.startTime)+"</td>";
							innerRows = innerRows + "			</tr>";
						});
				
				   // Prepare inner table
				   newRowData = "<tr id=\""+innerRowId+"\""; 
				   newRowData = newRowData + "style=\"background-color: #ecf8ec\" >";
				   newRowData = newRowData + "<td colspan=\"13\">";
				   newRowData = newRowData + "	<table id=\""+innerTableId+"\" ";
				   newRowData = newRowData + "		class=\"table table-striped table-bordered table-hover dataTable no-footer\"";
				   newRowData = newRowData + "		style=\"width: 100%;\">";
				   newRowData = newRowData + "		<thead>";
				   newRowData = newRowData + "			<tr>";
				   newRowData = newRowData + "				<th>Validation Id</th>";
				   newRowData = newRowData + "				<th>Name</th>";
				   newRowData = newRowData + "				<th>Unique Id</th>";
				   newRowData = newRowData + "				<th>Status</th>";
				   newRowData = newRowData + "				<th>Process Id</th>";
				   newRowData = newRowData + "				<th>SparkApp Id</th>";
				   newRowData = newRowData + "				<th>Start Time</th>";
				   newRowData = newRowData + "			</tr>";
				   newRowData = newRowData + "		</thead>";
				   newRowData = newRowData + "		<tbody>";
				   newRowData = newRowData + innerRows
				   newRowData = newRowData + "		</tbody>";
				   newRowData = newRowData + "	</table>";
				   newRowData = newRowData + "</td>";
				   newRowData = newRowData + "</tr>";
				   
				 //Add Inner table in new row
				   $('#appgroupJobListTable_Id tbody').find('#'+row_id).after(newRowData);
				   
				   $('#'+innerTableId).dataTable({
					  	order: [[ 1, "desc" ]],
					  	"scrollX": true
				   });
			},
			error : function() {
			},
		});
} );

$('#projectJobListTable_Id').on('click','td.shown', function () {
	  var td = $(this).closest('td');
	   var row_id = $(this).attr("id");
	   
	   // Get the Inner row id
	   var innerTableId=row_id+"_inner_row_Id";
	   
	   // Hide the row
	    $('#'+innerTableId).addClass("hidden");
	   
	   // Change the icon
	   td.removeClass('shown');
	   td.addClass('details-control');
	   
	   // Remove the row
	   $('#projectJobListTable_Id tbody').find('#'+innerTableId).remove();
	   
} );

$('#projectJobListTable_Id').on('click','td.details-control', function () {
	   var td = $(this).closest('td');
	   var row_id = $(this).attr("id");
	   var projectId = $(this).closest("tr").find('td:eq(1)').text();
	   var uniqueId = $(this).closest("tr").find('td:eq(3)').text();
	   
	   td.removeClass('details-control');
	   td.addClass('shown');
	   
	   var innerRowId=row_id+"_inner_row_Id";
	   var innerTableId=row_id+"_inner_table_Id";
	   
	   console.log("projectId: "+projectId);
	   console.log("uniqueId: "+uniqueId);
	   
	   var formdata = {
		   projectId : projectId,
		   uniqueId : uniqueId
	   };
	   
	   var innerRows = "";
	   var newRowData = "";
	   $.ajax({
			url : './getProjectJobAssociatedConnections',
			type : 'POST',
			headers: { 'token':$("#token").val()},
			datatype : 'json',
			data : formdata,
			success : function(obj) {
						$(obj).each(
						   function(i, item) {
							innerRows = innerRows + "			<tr>";
							innerRows = innerRows + "				<td>"+item.idDataSchema+"</td>";
							innerRows = innerRows + "				<td>"+item.schemaName+"</td>";
							innerRows = innerRows + "				<td>"+item.uniqueId+"</td>";
							innerRows = innerRows + "				<td>"+item.status+"</td>";
							innerRows = innerRows + "				<td>"+item.processId+"</td>";
							innerRows = innerRows + "				<td>"+item.sparkAppId+"</td>";
							innerRows = innerRows + "				<td>"+formatDate(item.startTime)+"</td>";
							innerRows = innerRows + "			</tr>";
						});
				
				   // Prepare inner table
				   newRowData = "<tr id=\""+innerRowId+"\""; 
				   newRowData = newRowData + "style=\"background-color: #ecf8ec\" >";
				   newRowData = newRowData + "<td colspan=\"14\">";
				   newRowData = newRowData + "	<table id=\""+innerTableId+"\" ";
				   newRowData = newRowData + "		class=\"table table-striped table-bordered table-hover dataTable no-footer\"";
				   newRowData = newRowData + "		style=\"width: 100%;\">";
				   newRowData = newRowData + "		<thead>";
				   newRowData = newRowData + "			<tr>";
				   newRowData = newRowData + "				<th>Schema Id</th>";
				   newRowData = newRowData + "				<th>Schema Name</th>";
				   newRowData = newRowData + "				<th>unique Id</th>";
				   newRowData = newRowData + "				<th>Status</th>";
				   newRowData = newRowData + "				<th>Process Id</th>";
				   newRowData = newRowData + "				<th>SparkApp Id</th>";
				   newRowData = newRowData + "				<th>Start Time</th>";
				   newRowData = newRowData + "			</tr>";
				   newRowData = newRowData + "		</thead>";
				   newRowData = newRowData + "		<tbody>";
				   newRowData = newRowData + innerRows
				   newRowData = newRowData + "		</tbody>";
				   newRowData = newRowData + "	</table>";
				   newRowData = newRowData + "</td>";
				   newRowData = newRowData + "</tr>";
				   
				 //Add Inner table in new row
				   $('#projectJobListTable_Id tbody').find('#'+row_id).after(newRowData);
				   
				   $('#'+innerTableId).dataTable({
					  	order: [[ 1, "desc" ]],
					  	"scrollX": true
				   });
			},
			error : function() {
			},
		});
});

$('#domainJobListTable_Id').on('click','td.shown', function () {
	  var td = $(this).closest('td');
	   var row_id = $(this).attr("id");

	   // Get the Inner row id
	   var innerTableId=row_id+"_inner_row_Id";

	   // Hide the row
	    $('#'+innerTableId).addClass("hidden");

	   // Change the icon
	   td.removeClass('shown');
	   td.addClass('details-control');

	   // Remove the row
	   $('#domainJobListTable_Id tbody').find('#'+innerTableId).remove();

} );

$('#domainJobListTable_Id').on('click','td.details-control', function () {
	   var td = $(this).closest('td');
	   var row_id = $(this).attr("id");
	   var domainId = $(this).closest("tr").find('td:eq(1)').text();
	   var uniqueId = $(this).closest("tr").find('td:eq(3)').text();

	   td.removeClass('details-control');
	   td.addClass('shown');

	   var innerRowId=row_id+"_inner_row_Id";
	   var innerTableId=row_id+"_inner_table_Id";

	   console.log("domainId: "+domainId);
	   console.log("uniqueId: "+uniqueId);

	   var formdata = {
		   domainId : domainId,
		   uniqueId : uniqueId
	   };

	   var innerRows = "";
	   var newRowData = "";
	   $.ajax({
			url : './getDomainJobAssociatedProjects',
			type : 'POST',
			headers: { 'token':$("#token").val()},
			datatype : 'json',
			data : formdata,
			success : function(obj) {
						$(obj).each(
						   function(i, item) {
							innerRows = innerRows + "			<tr>";
							innerRows = innerRows + "				<td>"+item.projectId+"</td>";
							innerRows = innerRows + "				<td>"+item.projectName+"</td>";
							innerRows = innerRows + "				<td>"+item.uniqueId+"</td>";
							innerRows = innerRows + "				<td>"+item.status+"</td>";
							innerRows = innerRows + "				<td>"+item.processId+"</td>";
							innerRows = innerRows + "				<td>"+item.sparkAppId+"</td>";
							innerRows = innerRows + "				<td>"+formatDate(item.startTime)+"</td>";
							innerRows = innerRows + "			</tr>";
						});

				   // Prepare inner table
				   newRowData = "<tr id=\""+innerRowId+"\"";
				   newRowData = newRowData + "style=\"background-color: #ecf8ec\" >";
				   newRowData = newRowData + "<td colspan=\"14\">";
				   newRowData = newRowData + "	<table id=\""+innerTableId+"\" ";
				   newRowData = newRowData + "		class=\"table table-striped table-bordered table-hover dataTable no-footer\"";
				   newRowData = newRowData + "		style=\"width: 100%;\">";
				   newRowData = newRowData + "		<thead>";
				   newRowData = newRowData + "			<tr>";
				   newRowData = newRowData + "				<th>Project Id</th>";
				   newRowData = newRowData + "				<th>Project Name</th>";
				   newRowData = newRowData + "				<th>unique Id</th>";
				   newRowData = newRowData + "				<th>Status</th>";
				   newRowData = newRowData + "				<th>Process Id</th>";
				   newRowData = newRowData + "				<th>SparkApp Id</th>";
				   newRowData = newRowData + "				<th>Start Time</th>";
				   newRowData = newRowData + "			</tr>";
				   newRowData = newRowData + "		</thead>";
				   newRowData = newRowData + "		<tbody>";
				   newRowData = newRowData + innerRows
				   newRowData = newRowData + "		</tbody>";
				   newRowData = newRowData + "	</table>";
				   newRowData = newRowData + "</td>";
				   newRowData = newRowData + "</tr>";

				 //Add Inner table in new row
				   $('#domainJobListTable_Id tbody').find('#'+row_id).after(newRowData);

				   $('#'+innerTableId).dataTable({
					  	order: [[ 1, "desc" ]],
					  	"scrollX": true
				   });
			},
			error : function() {
			},
		});
});

</script>
<script>

	function killJob(taskId, taskType, processId, deployMode, sparkAppId, uniqueId) {
    		var form_data = {
    			taskId : taskId,
    			taskType : taskType,
    			processId : processId,
    			deployMode : deployMode,
    			sparkAppId : sparkAppId,
    			uniqueId : uniqueId
    		};

    		$.ajax({
    			type : 'POST',
    			headers: { 'token':$("#token").val()},
    			url : "./stopRunningJob",
    			data : form_data,
    			beforeSend: function () {
    				$(".modal").show();
    			},
    			success : function(message) {
    				var j_obj1 = $.parseJSON(message);

                    if (j_obj1.hasOwnProperty('success')) {
    					toastr.info("Task with id "+taskId+" is stopped !!");
    					setTimeout(
    						function () {
    							window.location.reload();
    						}, 1000);
                    }

                    if(j_obj1.hasOwnProperty('failure')){
                    	var msg = j_obj1.message;
                    	toastr.info("Failed to stop Task with id: "+taskId+"!");
                    }
    			},
    			complete : function(message){
    				$(".modal").hide();
    			}
    		});
    	}
	
	function deleteJobFromQueue(taskId, taskType, uniqueId) {
		var form_data = {
			taskId : taskId,
			taskType : taskType,
			uniqueId : uniqueId
		};

		$.ajax({
			type : 'POST',
			headers: { 'token':$("#token").val()},
			url : "./deleteJobFromQueue",
			data : form_data,
			success : function(message) {
				var j_obj1 = $.parseJSON(message);
                
                if (j_obj1.hasOwnProperty('success')) {
					window.location.reload();
                }
                
                if(j_obj1.hasOwnProperty('failure')){
                	var msg = j_obj1.message;
					alert(msg+"\nFailed to remove from quueue Task with id: "+taskId+" !!");
                }
			}
		});
	}
	
	function clearAllQueuedJobs() {
		$.ajax({
			type : 'GET',
			url : "./clearAllQueuedJobs",
			success : function(message) {
				var j_obj1 = $.parseJSON(message);
                
                if (j_obj1.hasOwnProperty('success')) {
					window.location.reload();
                }
                
                if(j_obj1.hasOwnProperty('failure')){
                	var msg = j_obj1.message;
					alert(msg+"\nFailed to clear the queue !!");
                }
			}
		});
	}
	
	function formatDate(inputDate) {
		var dt = new Date(inputDate);

		// ensure date comes as 01, 09 etc
		var DD = ("0" + dt.getDate()).slice(-2);
		// getMonth returns month from 0
		var MM = ("0" + (dt.getMonth() + 1)).slice(-2);
		var YYYY = dt.getFullYear();
		var hh = ("0" + dt.getHours()).slice(-2);
		var mm = ("0" + dt.getMinutes()).slice(-2);
		var ss = ("0" + dt.getSeconds()).slice(-2);
		var date_string = YYYY + "-" + MM + "-" + DD + " " + hh + ":" + mm + ":" + ss;
		return date_string;
	}
</script>
<!-- END CONTAINER -->
<jsp:include page="footer.jsp" />