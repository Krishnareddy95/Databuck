<%@page import="com.databuck.service.RBACController"%>
<%@page import="java.util.List"%>
<jsp:include page="header.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<jsp:include page="container.jsp" />

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="org.springframework.jdbc.support.rowset.SqlRowSet"%>
<%@page import="org.springframework.jdbc.support.rowset.SqlRowSetMetaData"%>

<style>
	table.dataTable tbody th,
	table.dataTable tbody td {
		white-space: nowrap;
	}

	.dataTables_scrollBody{
            overflow-y: hidden !important;
   	 	}
</style>

<!-- BEGIN CONTENT -->
<div class="page-content-wrapper">
	<!-- BEGIN CONTENT BODY -->
	<div class="page-content">
		<!-- BEGIN PAGE TITLE-->
		<!-- END PAGE TITLE-->
		<!-- END PAGE HEADER-->

		<div class="row">
			<div class="col-md-12">
				<!-- BEGIN EXAMPLE TABLE PORTLET-->
				<div class="portlet light bordered">
					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold "> Alert Events </span>
						</div>
					</div>

					<br><br>
					<div class="caption font-red-sunglo">
						<span class="caption-subject bold ">Alert Event Master </span>
						</div><br>
					
					<div class="portlet-body">
						<table
							class="table table-striped table-bordered  table-hover dataTable"
							id="AlertNotification"
							style="width: 100%;">
							<thead>
								<tr>
									<th>Id</th>
									<th>Name</th>
									<th>Module Name</th>
									<th>Communication Type</th>
									<th>Message Code</th>
									<th>Completion Message</th>
									<th>Subscribe</th>
									<th>Edit</th>
								</tr>
							</thead>
							<tbody></tbody>
						</table>
					</div>
						
					<br>
					<div class="caption font-red-sunglo">
						<span class="caption-subject bold ">Subscribed Details</span>
					</div><br>
					<div class="portlet-body">
						<table
							class="table table-striped table-bordered  table-hover dataTable"
							 id="AlertSubscriptions"
							style="width: 100%;">
							<thead>
								<tr>
									<th>Alert Sub Id</th>
									<th>Project Name</th>
									<th>Event Name</th>
									<th>Communication Mode</th>
									<th>Is Global Subscription</th>
									<th>Communication Values</th>
								</tr>
							</thead>
							<tbody>

					       </tbody>
						</table>
					</div>
				</div>
			</div>
				<!-- END EXAMPLE TABLE PORTLET-->
		</div>
	</div>
	
</div>
<script src="./assets/global/plugins/jquery.min.js"
	type="text/javascript"></script>
<script
	src="./assets/global/plugins/jquery.dataTables.min.js"
	type="text/javascript"></script>

<script>
$(document).ready(function() {
	var table;

	      table = $('#AlertNotification').dataTable({
	                                "bPaginate" : true,
                                    "order" : [ 0, 'asc' ],
                                    "bInfo" : true,
                                    "iDisplayStart" : 0,
                                    "bProcessing" : true,
                                    "bServerSide" : false,
                                    "bFilter" : true,
                                    'sScrollX' : true,
                          		 	"sAjaxSource" : path+"/getAllAlertEvents",
                          		 	"aoColumns": [
                                              { "data": "eventId" },
                                              { "data": "eventName" },
                                              { "data": "eventModuleName" },
                                              { "data": "eventCommunicationType" },
                                              { "data": "eventMessageCode" },
                                              { "data": "eventCompletionMessage" },
                                              { "data": "action",
                                            	  "render": function(data, type, row, meta){
                                                      data = "<a onClick=\"javascript:createAlertEventSubscriptions("+row.eventId+")\" class=\"fa fa-thumbs-up\"></a>";
                                                  return data;
                                            	  }
                                
                                              },
                                              { "data": "edit",  
                                            	  "render": function(data, type, row, meta){
                                                      data = "<a onClick=\"javascript:editAlertEventNotification("+row.eventId+")\" class=\"fa fa-edit\"></a>";
                                                  return data;
                                              }
                                              },
                                      ],
                          		 	"dom" : 'C<"clear">lfrtip',
                                    colVis : {
                                        "align" : "right",
                                        restore : "Restore",
                                        showAll : "Show all",
                                        showNone : "Show none",
                                        order : 'alpha'
                                    },
                                    "language" : {
                                        "infoFiltered" : ""
                                    },
                                    "dom" : 'Cf<"toolbar"">rtip',
                          	      });


      table2 = $('#AlertSubscriptions').dataTable({
      	                                "bPaginate" : true,
                                        "order" : [ 0, 'asc' ],
                                        "bInfo" : true,
                                        "iDisplayStart" : 0,
                                        "bProcessing" : true,
                                        "bServerSide" : false,
                                        "bFilter" : true,
                                        'sScrollX' : true,
                                		 	"sAjaxSource" : path+"/getAllSubscribedEvents",
                                		 	"aoColumns": [
                                                    { "data": "alertSubId" },
                                                    { "data": "projectName" },
                                                    { "data": "eventName" },
                                                    { "data": "commModeName" },
                                                    { "data": "isGlobalSubscription" },
                                                    { "data": "communicationValues" }

                                            ],
                                		 	"dom" : 'C<"clear">lfrtip',
                                            colVis : {
                                                "align" : "right",
                                                restore : "Restore",
                                                showAll : "Show all",
                                                showNone : "Show none",
                                                order : 'alpha'
                                            },
                                            "language" : {
                                                "infoFiltered" : ""
                                            },
                                            "dom" : 'Cf<"toolbar"">rtip',
                                	      });

      
});
</script>
<script type="text/javascript">

$(document).ready(function() {
	document.getElementById ("btnsave").addEventListener ("click", resetEmotes, false);
});

function createAlertEventSubscriptions(eventId){
    window.location.href = "alertEventSubscription?selectedEvent="+eventId+"";
}
function editAlertEventNotification(eventId){
	window.location.href = 'editAlertEventNotification?eventId='+eventId+'';
}

</script>

<!-- END CONTAINER -->
<jsp:include page="footer.jsp" />


