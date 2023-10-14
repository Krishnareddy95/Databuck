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

#statusRefreshBtn {
	margin-bottom: 15px;
	float: right;
}
.dataTables_scrollBody{
            overflow-y: hidden !important;
   	 	}
</style>
</head>
<%@page import="com.databuck.service.RBACController"%>
<%@page import="com.databuck.bean.ListApplications"%>
<%@page import="com.databuck.bean.QuickStartTaskTracker"%>
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
					<!-- BEGIN EXAMPLE TABLE PORTLET-->
					<div class="portlet light bordered">
						<div class="portlet-title">
							<div class="caption font-red-sunglo">
								<span class="caption-subject bold "> QuickStart Tasks </span>
							</div>
							<br><br>
							
						<div id="qs_status_div" class="portlet light bordered" style="background-color: mintcream;">
							<div id="qs_msg_div">
								<span id="qs_msg_id"></span>
							</div>
						</div>
						
						<div class="portlet-body" >						
							<table  id="quickJobsTable_Id"
								class="table table-striped table-bordered table-hover dataTable no-footer"
								style="width: 100%;">
								<thead>
									<tr>
										<th>Connection Id</th>
										<th>Connection Name</th>
										<th>Schema Job Id</th>
										<th>Status</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="taskTrackerListobj" items="${taskTrackerList}">
										<tr>
											<td>${taskTrackerListobj.idDataSchema}</td>
											<td>${taskTrackerListobj.connectionName}</td>
											<td>${taskTrackerListobj.schemaJobId}</td>
											<td>${taskTrackerListobj.status}</td>
										</tr>
									</c:forEach>
								</tbody>

							</table>
						</div>
					</div>

				</div>
				<!-- END EXAMPLE TABLE PORTLET-->
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
$(document).ready(function(){	
	var data = "${taskTrackerList}";
	var qsSource = "${quickStartSource}";
	var qsMessage = "${qs_message}";
	console.log("data: "+data);
	$('#taskTrackerListId').val(data);
	$('#quickStartSourceId').val(qsSource);
	$('#qs_msg_id').html(qsMessage);
	
	$('#quickJobsTable_Id').dataTable({
		order : [ [ 0, "asc" ] ],
		"scrollX" : true
	});
});
</script>

<!-- <script> 
        // wait for the DOM to be loaded 
        $(document).ready(function() { 
        	$('#myForm').ajaxForm({
        		headers: {'token':$("#token").val()}
        	}); 
        }); 
 </script> -->

<!-- END CONTAINER -->
<jsp:include page="footer.jsp" />