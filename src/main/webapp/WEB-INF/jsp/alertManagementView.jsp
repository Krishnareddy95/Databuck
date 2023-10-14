<%@page import="com.databuck.service.RBACController"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp" />
<head>
	<link rel="stylesheet" href="./assets/jwf/content/JwfSpaInfra.css">
	<script src="./assets/jwf/scripts/JwfSpaInfra.js"></script>
</head>
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<link rel="stylesheet" href="./assets/css/custom-style.css" type="text/css" />
<style>
	table.dataTable tbody th,
	table.dataTable tbody td {
		white-space: nowrap;
	}

	.dataTables_scrollBody {
		overflow-y: auto !important;
   }

	table#SearchTable {
	  width: 100%;
	  border: 1px solid #dddddd;
	}

	table#SearchTable tr td,table#dateTable tr td {
	  border: 0px solid #dddddd;
	  text-align: left;
	  padding: 8px !important;
	  width: 25%;
	  valign: top;
	}

	table#SearchTable td label{
		font-family: "Open Sans",sans-serif;
	}
	table#dateTable td label{
	    font-weight: bold;
		font-family: "Open Sans",sans-serif;
	}

	table#SearchTable td span , table#dateTable td span{
		font-size: 13.5;
	}

	#table_global_rules input[type="checkbox"], #table_global_thresholds input[type="checkbox"],
		#table_ref_rules input[type="checkbox"] {
		width: 15px;
		height: 15px;
		box-shadow: 0 0 0 0px grey;
		cursor: pointer;
	}

	#table_global_rules tr[data-linkedrule='Y'] {
		color: #0099e6 !important;
	}

	div#AvailableProjects {
		margin-top: 5px;
		border: 2px solid #dddddd;
		height: 130px;
		padding: 7px;
		overflow-x: hidden;
		overflow-y: auto;
	}
	div#AvailableEvents {
		margin-top: 5px;
		border: 2px solid #dddddd;
		height: 130px;
		width: 300px;
		padding: 7px;
		overflow-x: hidden;
		overflow-y: auto;
	}
	div#AvailableTasks {
		margin-top: 5px;
		border: 2px solid #dddddd;
		height: 130px;
		width: 280px;
		padding: 7px;
		overflow-x: hidden;
		overflow-y: auto;
	}

	div#AvailableProjects label {
		font-family: "Open Sans",sans-serif;
		font-weight: normal !important;
	}

	.DataBtns {
		width: 100% !important;
		text-align: center !important;
		font-size: 12.5px !important;
		letter-spacing: 2px;
		margin-top: 17px;
	}
	
	h1{
	text-align: left;
	}

</style>


<!--============= BEGIN CONTENT BODY============= -->


<!--*****************      BEGIN CONTENT **********************-->

<div class="page-content-wrapper">
	<!-- BEGIN CONTENT BODY -->
	<div class="page-content">
		<!-- BEGIN PAGE TITLE-->
		<!-- END PAGE TITLE-->
		<!-- END PAGE HEADER-->

		<div class="row">
			<div class="col-md-12">

				<!--****         BEGIN EXAMPLE TABLE PORTLET       **********-->

				<div class="portlet light bordered">
					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold "> Alert Inbox </span>
						</div>
					</div>
					<div class="col-md-7">
					<table id='SearchTable' >
						<tr>
							<td rowspan="2" colspan="2" valign="top">
							<label><b>Project Name*</b></label>
							<div style="float: right;">
									<img style="cursor: pointer;" id="select-all-projects" src="./assets/img/select-all.png" title="Select All" width="25" height="25">
									<img style="cursor: pointer;" id="clear-all-projects" src="./assets/img/clear-all.png" title="Clear All" width="25" height="25">
								</div>
								<br>
								<div id="AvailableProjects"></div>
							</td>
							<td rowspan="2" colspan="2" valign="top">
							<label><b>Event Name*</b></label>
							<div style="float: right;">
							<img style="cursor: pointer;" id="select-all-events" src="./assets/img/select-all.png" title="Select All" width="25" height="25">
							<img style="cursor: pointer;" id="clear-all-events" src="./assets/img/clear-all.png" title="Clear All" width="25" height="25">
								
								</div>
								<div id="AvailableEvents"></div>
							</td>
							<td rowspan="2" colspan="2" valign="top">
							<label><b>Task Name*</b></label>
							<div style="float: right;">							
							<img style="cursor: pointer;" id="select-all-tasks" src="./assets/img/select-all.png" title="Select All" width="25" height="25">
							<img style="cursor: pointer;" id="clear-all-tasks" src="./assets/img/clear-all.png" title="Clear All" width="25" height="25">
								</div>
								<div id="AvailableTasks"></div>
							</td>
					</table>
					</div>
					<div class="col-md-5">
					<table id='dateTable'>
					<tr>
					<td>
					<label>From Date</label></br>
					 <input class="form-control" id='FromDate' name='date' type='text' value="${toDate}">
					    </td>
							<td>
								<label>To Date</label></br>
								<input class="form-control" id='ToDate' name='date' type='text' value="${fromDate}">
							</td>
						
						</tr>
						<tr>
							<td colspan="2">
								<label>Search Alert Event</label></br>
								<input class="form-control" id='SearchText' type='text'>
							</td>
						</tr>
					</tr>
					</table>
					</div>
					<div class="col-sm-4 col-sm-offset-4 text-center">
					
					<button id='SearchAlertEvent' class="btn btn-primary DataBtns">Search Alert Event</button>	
                   </div>

					<br/>

					<div class="portlet-body">
						<table class="table table-striped table-bordered table-hover" id="alertLogs" style="width: 100%;">
							<thead>
								<tr>
									<th>Alert LogId</th>
									<th>Execution Date</th>
									<th>Run</th>
									<th>Task Id</th>
									<th>Task Name</th>
									<th>Task Unique Id</th>
									<th>Project Name</th>
									<th>Event Name</th>
									<th>Alert Message</th>
									<th>Publish Date</th>
									<th>View</th>
								</tr>
							</thead>
						</table>
					</div>

				</div>
			</div>
		<!-- The Modal -->
						<div class="modal" id="myModal">
							<div class="modal-dialog"
								style="display: inline-block; position: fixed; top: 0; bottom: 0; left: 0; right: 0; height: 500px; margin: auto; overflow:auto;">
								<div class="modal-content">

									<!-- Modal Header -->
									<div class="modal-header">
										<h4 class="modal-title"><b>Alert Event Details</b></h4>
										<button type="button" class="close" data-dismiss="modal">&times;</button>
									</div>

						          <!-- Modal body -->
					               	<div class="modal-body" id="newValues">

					           	</div>

						       
						       </div>

						<!-- Modal footer -->
									<div class="modal-footer">
										<button type="button" class="btn btn-danger"
											data-dismiss="modal">Cancel</button>
									</div>
									
								</div>
							</div>
						</div>
		</div>
	</div>
</div>
<jsp:include page="footer.jsp" />
<head>
<script src="./assets/global/plugins/jquery.min.js" type="text/javascript"></script>
<script src="./assets/global/plugins/jquery.dataTables.min.js"	type="text/javascript"></script>

<script src="./assets/global/plugins/jquery-ui.min.js"></script>
<script src="./assets/global/plugins/bootstrap-multiselect.js"></script>
<link rel="stylesheet" href="./assets/global/plugins/bootstrap-multiselect.css">

<link rel="stylesheet" href="./assets/jwf/content/JwfSpaInfra.css">
<script src="./assets/jwf/scripts/JwfSpaInfra.js"></script>
<script type="text/javascript" src="./assets/global/plugins/bootstrap-datepicker.min.js"></script>
<script type="text/javascript" src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<link rel="stylesheet" href="./assets/global/plugins/bootstrap-datepicker3.css" />


<script type="text/javascript">
var ManageAlertInboxView = {
		SelectedProjectId: ${SelectedProjectId},
		ViewPageData: {},
		IndexOfRecord: -1,
		setup: function() {
			$('#SearchAlertEvent').on('click', ManageAlertInboxView.getPaginatedAlertInboxList);
			$('#select-all-projects').on('click', ManageAlertInboxView.handleActionClicks);
			$('#clear-all-projects').on('click', ManageAlertInboxView.handleActionClicks);
			$('#select-all-events').on('click', ManageAlertInboxView.handleActionClicks1);
			$('#clear-all-events').on('click', ManageAlertInboxView.handleActionClicks1);
			$('#select-all-tasks').on('click', ManageAlertInboxView.handleActionClicks2);
			$('#clear-all-tasks').on('click', ManageAlertInboxView.handleActionClicks2);
		},

		getPaginatedAlertInboxList :  function(oEvent) {
			var sViewTable = '#alertLogs', oSearchParameters = ManageAlertInboxView.getSearchParameters();

			if(!checkInputText()){
				alert('Vulnerability in submitted data, not allowed to submit!');
				return;
			}

			if (Object.keys(oSearchParameters).length > 0) {
				JwfAjaxWrapper({
					WaitMsg: 'Loading data Alert event list',
					Url: 'getDatabuckAlertLog',
					Headers:$("#token").val(),
					Data: oSearchParameters,
					CallBackFunction: function(oResponse) {
						console.log(oResponse);
						ManageAlertInboxView.ViewPageData = oResponse;
						PageCtrl.debug.log('Server response got is', ManageAlertInboxView.ViewPageData);

						fillAvailableProjects(ManageAlertInboxView.ViewPageData.AllProjectList, ManageAlertInboxView.ViewPageData.SelectedProjectId);
						fillAvailableEvents(ManageAlertInboxView.ViewPageData.alertEventNames);
					    fillAvailableTask(ManageAlertInboxView.ViewPageData.taskNames);
						$(sViewTable).DataTable ({
							"data" : ManageAlertInboxView.ViewPageData.ViewAlertEventDataList,
							"columns": [
								{ "data": "alertLogId" },
				                   { "data": "jobExecutionDate"},
				                   { "data": "jobRunNumber" },
				                   { "data": "taskId" },
				                   { "data": "taskName" },
				                   { "data": "taskUniqueId" },
				                   { "data": "projectName" },
				                   { "data": "eventName" },
				                   { "data": "alertMessage" },
				                   { "data": "publishDate" },
				                   { "data": "action",
				                 	  "render": function(data, type, row, meta){
				                        let msg = (row.alertMessage).replace("\n","");
				                        let msg1 =(row.executionErrors).replace(/["']/g, "");
				                        msg = msg.replace(/["']/g, "");
				                           data = "<a onClick=\"javascript:viewAlertEvent("+row.alertLogId+",\'"+msg+"\',\'"+msg1+"\')\" class=\"fa fa-eye\"></a>";
				                       return data;
				                 	  }
				     
				                   },
				           ],
							scrollX: true,
							scrollCollapse: true,
							scrollY: '400px',
							autoWidth: false,
							lengthMenu: [ 200, 100, 50 ],
							order: [[ 0, 'desc' ]],
							destroy: true,
							orderClasses: false,
							drawCallback: function( oSettings ) {
								$(sViewTable + ' a').off('click', ManageAlertInboxView.handleActionClicks).on('click', ManageAlertInboxView.handleActionClicks);
								$(sViewTable + ' button').off('click', ManageAlertInboxView.handleActionClicks).on('click', ManageAlertInboxView.handleActionClicks);
							}
						})
					}
				});
			}



			function fillAvailableProjects(aProjectList, nSelectedProjectId) {
				var sCheckBoxesHtml = '', sCheckBoxTmpl = "", nNoOfCheckBoxes = $('#AvailableProjects').find('input:checkbox').length, nOfProjects = aProjectList.length;

				if (nNoOfCheckBoxes !== nOfProjects) {
					aProjectList.forEach( function(oDataRow, nIndex) {
						sCheckBoxTmpl = "<input type='checkbox' id='project-id-{1}' {3}><label for='project-id-{1}'>&nbsp;&nbsp;{2}</label></br>";

						sCheckBoxTmpl = UTIL.replaceAll(sCheckBoxTmpl, '{1}', oDataRow.idProject);
						sCheckBoxTmpl = sCheckBoxTmpl.replace('{2}', oDataRow.projectName);
						sCheckBoxTmpl = sCheckBoxTmpl.replace('{3}', ( (oDataRow.idProject === nSelectedProjectId) ? 'checked' : '') );

						sCheckBoxesHtml = sCheckBoxesHtml + sCheckBoxTmpl;
					});
					$('#AvailableProjects').html(sCheckBoxesHtml);
					$('#AvailableProjects input[type="checkbox"]').off('click', ManageAlertInboxView.handleActionClicks).on('click', ManageAlertInboxView.handleActionClicks);
				}
			}
			
			function fillAvailableEvents(aEventList) {
				var sCheckBoxesHtml = '', sCheckBoxTmpl = "", nNoOfCheckBoxes = $('#AvailableEvents').find('input:checkbox').length, nOfEvents = aEventList.length;

				if (nNoOfCheckBoxes !== nOfEvents) {
					aEventList.forEach( function(oDataRow, Index) {
						sCheckBoxTmpl = "<input type='checkbox' id='event-id-{1}' name='SearchByEvent' value="+oDataRow.eventId+" class='a'><label for='event-id-{1}'>&nbsp;&nbsp;{2}</label></br>";
                     
						sCheckBoxTmpl = UTIL.replaceAll(sCheckBoxTmpl, '{1}',Index );
						sCheckBoxTmpl = sCheckBoxTmpl.replace('{2}', oDataRow.eventName);
						//sCheckBoxTmpl = sCheckBoxTmpl.replace('{3}', onlyOne(this));

						sCheckBoxesHtml = sCheckBoxesHtml + sCheckBoxTmpl;
					});
					$('#AvailableEvents').html(sCheckBoxesHtml);
					$('#AvailableEvents input[type="checkbox"]').off('click', ManageAlertInboxView.handleActionClicks1).on('click', ManageAlertInboxView.handleActionClicks1);
				}
			}
			
			function fillAvailableTask(aTaskList) {
				var sCheckBoxesHtml = '', sCheckBoxTmpl = "", nNoOfCheckBoxes = $('#AvailableTasks').find('input:checkbox').length, nOfEvents = aTaskList.length;

				if (nNoOfCheckBoxes !== nOfEvents) {
					aTaskList.forEach( function(oDataRow, Index) {
						sCheckBoxTmpl = "<input type='checkbox' id='event-id-{1}' name='SearchByTask' value='"+oDataRow+"'><label for='event-id-{1}'>&nbsp;&nbsp;{2}</label></br>";
                     
						sCheckBoxTmpl = UTIL.replaceAll(sCheckBoxTmpl, '{1}',Index );
						sCheckBoxTmpl = sCheckBoxTmpl.replace('{2}', oDataRow);
					//	sCheckBoxTmpl = sCheckBoxTmpl.replace('{3}', ( (oDataRow.idProject === nSelectedProjectId) ? 'checked' : '') );

						sCheckBoxesHtml = sCheckBoxesHtml + sCheckBoxTmpl;
					});
					$('#AvailableTasks').html(sCheckBoxesHtml);
					$('#AvailableTasks input[type="checkbox"]').off('click', ManageAlertInboxView.handleActionClicks2).on('click', ManageAlertInboxView.handleActionClicks2);
				}
			}
		
		},

		handleActionClicks : function(oEvent) {
			var oTarget = oEvent.target, oTargetParent = oTarget.parentElement, sTagName = oTarget.tagName, sInputType = oTarget.type,
				lBulkAction = false, nIndexOfRecord = -1, sTargetId = '', nActionCategory = (sTagName.toUpperCase() === 'I' || sTagName.toUpperCase() === 'SPAN') ? 0 : 1;

			sTargetId = (sTagName.toUpperCase() === 'I' || sTagName.toUpperCase() === 'SPAN') ? oTargetParent.id : oTarget.id;
              
				if (sTagName.toUpperCase() === 'IMG') {
					lBulkAction = (sTargetId === 'select-all-projects') ? true : false;
					$('#AvailableProjects').find('input:checkbox').prop('checked', lBulkAction);
			/*		
					lBulkAction = (sTargetId === 'select-all-events') ? true : false;
					$('#AvailableEvents').find('input:checkbox').prop('checked', lBulkAction);
					
					lBulkAction = (sTargetId === 'select-all-tasks') ? true : false;
					$('#AvailableTasks').find('input:checkbox').prop('checked', lBulkAction);    */
				}
			
		},
		handleActionClicks1 : function(oEvent) {
			var oTarget = oEvent.target, oTargetParent = oTarget.parentElement, sTagName = oTarget.tagName, sInputType = oTarget.type,
				lBulkAction = false, nIndexOfRecord = -1, sTargetId = '', nActionCategory = (sTagName.toUpperCase() === 'I' || sTagName.toUpperCase() === 'SPAN') ? 0 : 1;

			sTargetId = (sTagName.toUpperCase() === 'I' || sTagName.toUpperCase() === 'SPAN') ? oTargetParent.id : oTarget.id;
              
				if (sTagName.toUpperCase() === 'IMG') {
					lBulkAction = (sTargetId === 'select-all-events') ? true : false;
					$('#AvailableEvents').find('input:checkbox').prop('checked', lBulkAction);
				
				}
			
		},
		handleActionClicks2 : function(oEvent) {
			var oTarget = oEvent.target, oTargetParent = oTarget.parentElement, sTagName = oTarget.tagName, sInputType = oTarget.type,
				lBulkAction = false, nIndexOfRecord = -1, sTargetId = '', nActionCategory = (sTagName.toUpperCase() === 'I' || sTagName.toUpperCase() === 'SPAN') ? 0 : 1;

			sTargetId = (sTagName.toUpperCase() === 'I' || sTagName.toUpperCase() === 'SPAN') ? oTargetParent.id : oTarget.id;
              
				if (sTagName.toUpperCase() === 'IMG') {
				
					lBulkAction = (sTargetId === 'select-all-tasks') ? true : false;
					$('#AvailableTasks').find('input:checkbox').prop('checked', lBulkAction);  
				}
			
		},


		getSearchParameters: function() {
			var oRetValue = { "EventIds": "","TaskName": "", "FromDate": "", "ToDate": "", "ProjectIds": "", "SearchText": "" };
			var aSelectedProjectIds = [] ,aSelectedEvents = [] ,aSelectedTasks = [],lPageLoadCall =  ( $('#AvailableProjects').html().length === 0 ) ? true : false;

			oRetValue.FromDate = $("#FromDate").val();
			oRetValue.ToDate = $("#ToDate").val();
			oRetValue.SearchText = $('#SearchText').val();

			$('#AvailableProjects').find('input:checkbox').each( function() {
				if ( $(this).prop('checked') ) {
					aSelectedProjectIds.push( $(this).attr("id").split('-')[2] );
				}
			});
		
			if (aSelectedProjectIds.length > 0) {
				oRetValue.ProjectIds = aSelectedProjectIds.join();

			} else if (lPageLoadCall) {
				oRetValue.ProjectIds = ManageAlertInboxView.SelectedProjectId;

			} else {
				Modal.confirmDialog(0, 'Incomplete Selection',	'Please select at least one or more projects to search validations', ['Ok'] );
				oRetValue = {};
			}
			
			$('#AvailableEvents').find('input:checkbox').each( function() {
				if ( $(this).prop('checked') ) {
					aSelectedEvents.push( $(this).attr("value"));
				}	
			});
			if (aSelectedEvents.length > 0) {
				oRetValue.EventIds = aSelectedEvents.join();
			}
			
			$('#AvailableTasks').find('input:checkbox').each( function() {
				if ( $(this).prop('checked') ) {
					aSelectedTasks.push( $(this).attr("value"));
				}	
			});
			if (aSelectedTasks.length > 0) {
				oRetValue.TaskName = aSelectedTasks.join();
			}
			
			
			PageCtrl.debug.log('getSearchParameters', oRetValue);

			return oRetValue;
		}

}

function viewAlertEvent(alertLogId,alertMessage,errorMessage){
    var object=[];
    var keys = ["AlertLogId","AlertMessage","ErrorMessage"];
    var values = [alertLogId,alertMessage,errorMessage];
    for (let i = 0; i < keys.length; i++){
       object.push(keys[i] +"  :  "+ values[i]);
    } 
    $('#newValues').html("<h1 style='text-align: left';><b style= color:blue;>"+keys[0]+"</b> :  "+values[0]+"</h1><h1 style='text-align: left';><b style= color:blue;>"+keys[1]+"</b> :  "+values[1]+"</h1><h1 style='text-align: left';><b style= color:blue;>"+keys[2]+"</b> :  "+values[2]+"</h1>");    	
        	
    $("#myModal").modal("show");   
}


$(document).ready(function(){
	var date_input=$('input[name="date"]'); //our date input has the name "date"
	var container=$('.bootstrap-iso form').length>0 ? $('.bootstrap-iso form').parent() : "body";
	date_input.datepicker({
		format: 'yyyy-mm-dd',
		container: container,
		todayHighlight: true,
		autoclose: true,
		endDate: "today"
	})

	initializeJwfSpaInfra();
	ManageAlertInboxView.setup();
	ManageAlertInboxView.getPaginatedAlertInboxList();

});


</script>