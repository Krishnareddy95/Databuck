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

	table#SearchTable tr td {
	  border: 0px solid #dddddd;
	  text-align: left;
	  padding: 8px !important;
	  width: 25%;
	  valign: top;
	}

	table#SearchTable td label {
		font-weight: bold;
		font-family: "Open Sans",sans-serif;
	}

	table#SearchTable td span {
		font-size: 13.5;
	}

	div#AvailableProjects {
		margin-top: 5px; 
		border: 2px solid #dddddd; 
		height: 130px; 
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
			
				<div class="cotainer">
								<div class="tabbable tabbable-custom boxless tabbable-reversed">
									<ul class="nav nav-tabs" style="border-bottom: 1px solid #ddd;">
										<li style="width: 8%"><a onClick="validateHrefVulnerability(this)"  href="executiveSummary"
												style="padding: 2px 2px;"><strong>Executive
													Summary</strong></a></li>

										<li class="active" style="width: 8.63%"><a onClick="validateHrefVulnerability(this)"  href="dashboard_View"
												style="padding: 2px 2px;"><strong>Detailed
													View</strong></a></li>

										<li style="width: 8%"><a onClick="validateHrefVulnerability(this)"  href="dqUniverse" style="padding: 2px 2px;"><strong>DQ
													Universe</strong></a></li>
										<li style="width: 8%"><a onClick="validateHrefVulnerability(this)"  href="locationInfo"
												style="padding: 2px 2px;"><strong>DQ
													Lineage</strong></a></li>
										<% boolean flag=false; flag=RBACController.rbac("MyViews", "R" , session); if
											(flag) { %>
											<li style="width: 8%"><a onClick="validateHrefVulnerability(this)"  href="myViews" style="padding: 2px 2px;"><strong>My
														Views</strong></a></li>
											<% } %>
									</ul>
								</div>
				</div>

				<!--****         BEGIN EXAMPLE TABLE PORTLET       **********-->

				<div class="portlet light bordered">
					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold "> Quality Validation Checks Dashboard 
							(<a onClick="validateHrefVulnerability(this)"  href="downloadCsv?tableName=dq_dashboard">Download
											csv</a>) </span>
						</div>
					</div>					

					<table id='SearchTable'>
						<tr>
							<td rowspan="2" valign="top">
								<label>Project Name*</label>
								<div style="float: right;">
									<img style="cursor: pointer;" id="select-all-projects" src="./assets/img/select-all.png" title="Select All" width="25" height="25">								
									<img style="cursor: pointer;" id="clear-all-projects" src="./assets/img/clear-all.png" title="Clear All" width="25" height="25">
								</div>								
								</br>
								<div id="AvailableProjects"></div>
							</td>
							<td>
								<label>From Date</label></br>
								<input class="form-control" id='FromDate' name='date' type='text' value="${toDate}">
							</td>
							<td>
								<label>To Date</label></br>
								<input class="form-control" id='ToDate' name='date' type='text' value="${fromDate}">
							</td>
							<td rowspan="2">
								<label>Search Results By</label></br>
								<input type="radio" id="ByProjectAndDate" name="SearchByOption" value="1" checked>
								
								<span>Project and Date Range</span><br>
								<input type="radio" id="ByProjectAndDate" name="SearchByOption" value="2">
								
								<span>Project and Search Text</span><br>
								<input type="radio" id="ByAll" name="SearchByOption" value="3">
								
								<span>Project, Date Range and Search Text</span></br>
								
								<button id='SearchResults' class="btn btn-primary DataBtns">Search Results</button>
							</td>
						</tr>
						<tr>
							<td colspan="2">
								<label>Search Text in Validation Check Name or Data Template Name</label></br>
								<input class="form-control" id='SearchText' type='text'>
							</td>
						</tr>
					</table>
					
					<br/>
					
					<div class="portlet-body">
						<table class="table table-striped table-bordered table-hover" id="resultsViewListTBL" style="width: 100%;">
							<thead>
								<tr>
											<th>Validation Id</th>
											<th>Date</th>
											<th>Run</th>
											<th>Test Run</th>
											<th>Connection Name</th>
											<th>Validation Check Name</th>
											<th>Template Id</th>
											<th>Template Name</th>
											<th>Project Name</th>
											<th>Aggregate DQI</th>
											<th>Record Count</th>
											<th>Record Count Status</th>
											<th>Null Count Status</th>
											<th>Primary Key Status</th>
											<th>User Selected Field Status</th>
											<th>Numerical Field Status</th>
											<th>Record Anomaly Status</th>
											<th>Data Drift Status</th>								
								</tr>
							</thead>
						</table>						
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
<link rel="stylesheet" href="./assets/global/plugins/bootstrap-datepicker3.css" />
		
<script type="text/javascript">
var ManageResultsView = {
		SelectedProjectId: ${SelectedProjectId},
		ViewPageData: {},
		IndexOfRecord: -1,
		setup: function() {
			$('#SearchResults').on('click', ManageResultsView.getPaginatedResultsList);
			$('div img').on('click', ManageResultsView.handleActionClicks);
			$('div img').on('click', ManageResultsView.handleActionClicks);			
		},
		
		getPaginatedResultsList:  function(oEvent) {
			var sViewTable = '#resultsViewListTBL', oSearchParameters = ManageResultsView.getSearchParameters();
			if(!checkInputText())
			{
				alert('Vulnerability in submitted data, not allowed to submit!');
			}
			else {
			if (Object.keys(oSearchParameters).length > 0) {
				JwfAjaxWrapper({
					WaitMsg: 'Loading results list',
					Url: 'getPaginatedResultsList',
					Headers:$("#token").val(),
					Data: oSearchParameters,
					CallBackFunction: function(oResponse) {
						ManageResultsView.ViewPageData = oResponse;
						
						PageCtrl.debug.log('Server response got is', ManageResultsView.ViewPageData);						
						
						processDataActionIcons(ManageResultsView.ViewPageData.ViewPageDataList);
						fillAvailableProjects(ManageResultsView.ViewPageData.AllProjectList, ManageResultsView.ViewPageData.SelectedProjectId);						
						
						$(sViewTable).DataTable ({
							"data" : ManageResultsView.ViewPageData.ViewPageDataList,
							"columns": [
								{ "data" : "IdApp" },
								{ "data" : "date" },
								{ "data" : "run" },
								{ "data" : "test_run" },
								{ "data" : "connectionName" },
								{ "data" : "validationCheckName" ,
									 render: function (data, type, row, meta) {
										 var idApp= row.IdApp;
										 var result = '<a onclick="validateHrefVulnerability(this)" href="dashboard_table?idApp='+idApp+'" class="nav-link"><span class="title"></span>'+data+'</a>';
										 result = result + '<br><a onClick="validateHrefVulnerability(this)" style="margin-right: 7px;" title="GlobalRule Discovery" class="btn-link" href="loadGlobalRuleDiscoveryForValidation?idApp=' + idApp + '&validationName=' + data + '&pageSource=results"><img src="./assets/img/svg/global.svg" alt="GlobalRule Discovery" style="width: 16px;" /></a>';
                         				 return result;
									 }
								},
								{ "data" : "idData" },							
								{ "data" : "sourceName" },
								{ "data" : "projectName" },
								{ "data" : "aggregateDQI" },
								{ "data" : "RecordCount" },
								{ "data" : "recordCountStatus" },
								{ "data" : "nullCountStatus" },
								{ "data" : "primaryKeyStatus" },
								{ "data" : "userSelectedFieldStatus" },
								{ "data" : "numericalFieldStatus" },
								{ "data" : "recordAnomalyStatus" },
								{ "data" : "dataDriftStatus" }
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
								$(sViewTable + ' a').off('click', ManageResultsView.handleActionClicks).on('click', ManageResultsView.handleActionClicks);
								$(sViewTable + ' button').off('click', ManageResultsView.handleActionClicks).on('click', ManageResultsView.handleActionClicks);
							}						
						})
					}
				});
			}
		}
			function processDataActionIcons(aResultDataList) {
				var sAnkerTestRunTmpl = '<span class="label label-success label-sm">Yes</span>',
				sAnekerSuccessTmpl = '<span class="label label-success label-sm">{0}</span>',
				sAnekerFailureTmpl = '<span class="label label-danger label-sm">{0}</span>',
				sAnekerNATmpl = '<span class="label label-warning label-sm">{0}</span>',
				sdataDriftStatusActionIcon = '', srecordAnomalyStatusActionIcon = '', snumericalFieldStatusActionIcon = '', suserSelectedFieldStatusActionIcon = '', sprimaryKeyStatusActionIcon = '', snullCountStatusActionIcon = '', sRecordCountStatusActionIcon = '', sTestRunActionIcon = '', oSecurityFlags =  ManageResultsView.ViewPageData.SecurityFlags;
				
				aResultDataList.forEach( function(oResultRecord, nIndex) {				
					oResultRecord.OrgValidationCheckName = oResultRecord.validationCheckName;
					if(oResultRecord.test_run == 'Y'){
						sTestRunActionIcon = sAnkerTestRunTmpl;
					}else {
						sTestRunActionIcon = '';
					}
					if(oResultRecord.recordCountStatus == 'passed'){
						sRecordCountStatusActionIcon = sAnekerSuccessTmpl.replace('{0}', oResultRecord.recordCountStatus);
					}else if(oResultRecord.recordCountStatus == 'failed'){
						sRecordCountStatusActionIcon = sAnekerFailureTmpl.replace('{0}', oResultRecord.recordCountStatus);
					}else {
						sRecordCountStatusActionIcon = sAnekerNATmpl.replace('{0}', 'NA');
					}
				
					if(oResultRecord.nullCountStatus == 'passed'){
						snullCountStatusActionIcon = sAnekerSuccessTmpl.replace('{0}', oResultRecord.nullCountStatus);
					}else if(oResultRecord.nullCountStatus == 'failed'){
						snullCountStatusActionIcon = sAnekerFailureTmpl.replace('{0}', oResultRecord.nullCountStatus);
					}else if(oResultRecord.nullCountStatus == 'NA'){
						snullCountStatusActionIcon = sAnekerNATmpl.replace('{0}', oResultRecord.nullCountStatus);
					}
						
					if(oResultRecord.primaryKeyStatus == 'passed'){
						sprimaryKeyStatusActionIcon = sAnekerSuccessTmpl.replace('{0}', oResultRecord.primaryKeyStatus);
					}else if(oResultRecord.primaryKeyStatus == 'failed'){
						sprimaryKeyStatusActionIcon = sAnekerFailureTmpl.replace('{0}', oResultRecord.primaryKeyStatus);
					}else if(oResultRecord.primaryKeyStatus == 'NA'){
						sprimaryKeyStatusActionIcon = sAnekerNATmpl.replace('{0}', oResultRecord.primaryKeyStatus);
					}
						
					if(oResultRecord.userSelectedFieldStatus == 'passed'){
						suserSelectedFieldStatusActionIcon = sAnekerSuccessTmpl.replace('{0}', oResultRecord.userSelectedFieldStatus);
					}else if(oResultRecord.userSelectedFieldStatus == 'failed'){
						suserSelectedFieldStatusActionIcon = sAnekerFailureTmpl.replace('{0}', oResultRecord.userSelectedFieldStatus);
					}else if(oResultRecord.userSelectedFieldStatus == 'NA'){
						suserSelectedFieldStatusActionIcon = sAnekerNATmpl.replace('{0}', oResultRecord.userSelectedFieldStatus);
					}
						
					if(oResultRecord.numericalFieldStatus == 'passed'){
						snumericalFieldStatusActionIcon = sAnekerSuccessTmpl.replace('{0}', oResultRecord.numericalFieldStatus);
					}else if(oResultRecord.numericalFieldStatus == 'failed'){
						snumericalFieldStatusActionIcon = sAnekerFailureTmpl.replace('{0}', oResultRecord.numericalFieldStatus);
					}else if(oResultRecord.numericalFieldStatus == 'NA'){
						snumericalFieldStatusActionIcon = sAnekerNATmpl.replace('{0}', oResultRecord.numericalFieldStatus);
					}
						
					if(oResultRecord.recordAnomalyStatus == 'passed'){
						srecordAnomalyStatusActionIcon = sAnekerSuccessTmpl.replace('{0}', oResultRecord.recordAnomalyStatus);
					}else if(oResultRecord.recordAnomalyStatus == 'failed'){
						srecordAnomalyStatusActionIcon = sAnekerFailureTmpl.replace('{0}', oResultRecord.recordAnomalyStatus);
					}else if(oResultRecord.recordAnomalyStatus == 'NA'){
						srecordAnomalyStatusActionIcon = sAnekerNATmpl.replace('{0}', oResultRecord.recordAnomalyStatus);
					}
						
					if(oResultRecord.dataDriftStatus == 'passed'){
						sdataDriftStatusActionIcon = sAnekerSuccessTmpl.replace('{0}', oResultRecord.dataDriftStatus);
					}else if(oResultRecord.dataDriftStatus == 'failed'){
						sdataDriftStatusActionIcon = sAnekerFailureTmpl.replace('{0}', oResultRecord.dataDriftStatus);
					}else{
						sdataDriftStatusActionIcon = sAnekerNATmpl.replace('{0}','NA');
					}
						
					oResultRecord.test_run = sTestRunActionIcon;
					oResultRecord.recordCountStatus = sRecordCountStatusActionIcon;
					oResultRecord.nullCountStatus = snullCountStatusActionIcon;
					oResultRecord.primaryKeyStatus = sprimaryKeyStatusActionIcon;
					oResultRecord.userSelectedFieldStatus = suserSelectedFieldStatusActionIcon;
					oResultRecord.numericalFieldStatus = snumericalFieldStatusActionIcon;
					oResultRecord.recordAnomalyStatus = srecordAnomalyStatusActionIcon;
					oResultRecord.dataDriftStatus = sdataDriftStatusActionIcon;
					
					
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
					$('#AvailableProjects input[type="checkbox"]').off('click', ManageResultsView.handleActionClicks).on('click', ManageResultsView.handleActionClicks);
				}					
			}			
		},
		
		handleActionClicks: function(oEvent) {
			var oTarget = oEvent.target, oTargetParent = oTarget.parentElement, sTagName = oTarget.tagName, sInputType = oTarget.type, 
				lBulkAction = false, nIndexOfRecord = -1, sTargetId = '', nActionCategory = (sTagName.toUpperCase() === 'I' || sTagName.toUpperCase() === 'SPAN') ? 0 : 1;
			
			sTargetId = (sTagName.toUpperCase() === 'I' || sTagName.toUpperCase() === 'SPAN') ? oTargetParent.id : oTarget.id;
			
			if (nActionCategory === 0) {
			
				nIndexOfRecord = UTIL.indexOfRecord(ManageResultsView.ViewPageData.ViewPageDataList, 'ValidationId', sTargetId.split('-')[2]);
				ManageResultsView.IndexOfRecord = nIndexOfRecord;

				if (sTargetId.indexOf('copy') > -1) {			
					ManageResultsView.copyAction( ManageResultsView.ViewPageData.ViewPageDataList[nIndexOfRecord] );
				}
				else if(sTargetId.indexOf('Status') > -1){
					ManageResultsView.changeStatusAction( ManageResultsView.ViewPageData.ViewPageDataList[nIndexOfRecord] );
				}
			} else {
			
				nIndexOfRecord = UTIL.indexOfRecord(ManageResultsView.ViewPageData.ViewPageDataList, 'ValidationId', sTargetId.split('-')[2]);
				
				if (sTagName.toUpperCase() === 'IMG') {
					lBulkAction = (sTargetId === 'select-all-projects') ? true : false;
					$('#AvailableProjects').find('input:checkbox').prop('checked', lBulkAction);
				}
			}				
		},
		getSearchParameters: function() {
			var oRetValue = { "SearchByOption": "", "FromDate": "", "ToDate": "", "ProjectIds": "", "SearchText": "" };
			var aSelectedProjectIds = [], lPageLoadCall =  ( $('#AvailableProjects').html().length === 0 ) ? true : false;			
			
			oRetValue.SearchByOption = $("input[name='SearchByOption']:checked").val();
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
				oRetValue.ProjectIds = ManageResultsView.SelectedProjectId;
				
			} else {				
				Modal.confirmDialog(0, 'Incomplete Selection',	'Please select at least one or more projects to search validations', ['Ok'] );
				oRetValue = {};
			}
			
			PageCtrl.debug.log('getSearchParameters', oRetValue);
			
			return oRetValue;
		},
}

$(document).ready(function() {
	var date_input=$('input[name="date"]'); //our date input has the name "date"
	var container=$('.bootstrap-iso form').length > 0 ? $('.bootstrap-iso form').parent() : "body";
	
	PageCtrl.debug.log('Page getting reloaded');
	
	date_input.datepicker({
        orientation: "right",
		format: 'yyyy-mm-dd',
		container: container,
		todayHighlight: true,
		autoclose: true,
		endDate: "today"
	})
	
	initializeJwfSpaInfra();	
	ManageResultsView.setup();
	ManageResultsView.getPaginatedResultsList();			
});
</script>

</head>