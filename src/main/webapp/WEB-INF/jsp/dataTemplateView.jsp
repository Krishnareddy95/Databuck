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

				<!--****         BEGIN EXAMPLE TABLE PORTLET       **********-->

				<div class="portlet light bordered">
					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold "> Data Templates </span>
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
								<label>Search Rules By</label></br>
								<input type="radio" id="ByProjectAndDate" name="SearchByOption" value="1" checked>

								<span>Project and Date Range</span><br>
								<input type="radio" id="ByProjectAndDate" name="SearchByOption" value="2">

								<span>Project and Search Text</span><br>
								<input type="radio" id="ByAll" name="SearchByOption" value="3">

								<span>Project, Date Range and Search Text</span></br>

								<button id='SearchDataTemplate' class="btn btn-primary DataBtns">Search Data Templates</button>
							</td>
						</tr>
						<tr>
							<td colspan="2">
								<label>Search Text in Data Template Name or Table Name</label></br>
								<input class="form-control" id='SearchText' type='text'>
							</td>
						</tr>
					</table>

					<br/>

					<div class="portlet-body">
						<table class="table table-striped table-bordered table-hover" id="dataTemplateViewListTBL" style="width: 100%;">
							<thead>
								<tr>
									<th>Template Id</th>
									<th>Template Name</th>
									<th>Connection Type</th>
									<th>Table</th>
									<th>Project</th>
									<th>Created_On</th>
									<th>Updated_On</th>
									<th>Created By</th>
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
var ManageDataTemplateView = {
		SelectedProjectId: ${SelectedProjectId},
		ViewPageData: {},
		IndexOfRecord: -1,
		setup: function() {
			$('#SearchDataTemplate').on('click', ManageDataTemplateView.getPaginatedDataTemplateList);
			$('div img').on('click', ManageDataTemplateView.handleActionClicks);
			$('div img').on('click', ManageDataTemplateView.handleActionClicks);
		},

		getPaginatedDataTemplateList :  function(oEvent) {
			var sViewTable = '#dataTemplateViewListTBL', oSearchParameters = ManageDataTemplateView.getSearchParameters();

			if(!checkInputText()){
				alert('Vulnerability in submitted data, not allowed to submit!');
				return;
			}

			if (Object.keys(oSearchParameters).length > 0) {
				JwfAjaxWrapper({
					WaitMsg: 'Loading data template list',
					Url: 'getPaginatedDataTemplateList',
					Headers:$("#token").val(),
					Data: oSearchParameters,
					CallBackFunction: function(oResponse) {
						ManageDataTemplateView.ViewPageData = oResponse;

						PageCtrl.debug.log('Server response got is', ManageDataTemplateView.ViewPageData);

						processDataActionIcons(ManageDataTemplateView.ViewPageData.ViewPageDataList);
						fillAvailableProjects(ManageDataTemplateView.ViewPageData.AllProjectList, ManageDataTemplateView.ViewPageData.SelectedProjectId);

						$(sViewTable).DataTable ({
							"data" : ManageDataTemplateView.ViewPageData.ViewPageDataList,
							"columns": [
								{ "data": "TemplateId" },
								{ "data": "TemplateName" },
								{ "data": "DataLocation" },
								{ "data": "Tablename" },
								{ "data": "ProjectName" },
								{ "data": "CreatedAt" },
								{ "data": "UpdatedAt" },
								{ "data": "CreatedByUser" }
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
								$(sViewTable + ' a').off('click', ManageDataTemplateView.handleActionClicks).on('click', ManageDataTemplateView.handleActionClicks);
								$(sViewTable + ' button').off('click', ManageDataTemplateView.handleActionClicks).on('click', ManageDataTemplateView.handleActionClicks);
							}
						})
					}
				});
			}

			function processDataActionIcons(aDataTemplateDataList) {
				var sAnkerCopyTmpl = '<a onClick="validateHrefVulnerability(this)"  style="margin-right: 7px;" title="Copy" id="anker-copy-{0}"><i class="fa fa-files-o" aria-hidden="true"></i></a>',
						sAnkerViewTmpl = '<a onClick="validateHrefVulnerability(this)"  style="margin-right: 7px;" title="View" id="anker-view-{0}" {1}><i class="fa fa-eye" aria-hidden="true"></i></a>',
						sAnkerEditTmpl = '<a onClick="validateHrefVulnerability(this)"  style="margin-right: 7px;" title="Edit" id="anker-edit-{0}" {1}><i class="fa fa-edit"></i></a>',
						sAnkerDeleteTmpl = '<a onClick="validateHrefVulnerability(this)"  style="margin-right: 7px;" title="Delete" id="anker-delete-{0}" {1}><i style="color: red" class="fa fa-trash"></i></a>',
						sAnkerReRunTemplateTmpl = '<a onClick="validateHrefVulnerability(this)"  style="margin-right: 7px;" title="ReRun Template" id="anker-reRunTemplate-{0}" {1}> <i class="fa fa-repeat"></i></a>',
						sAnkerReRunProfilingTmpl = '<a onClick="validateHrefVulnerability(this)"  style="margin-right: 7px;" title="ReRun Profiling" id="anker-reRunProfiling-{0}" {1}> <i class="fa fa-repeat"></i></a>',
						sAnkerGlobalRuleDiscoveryTmpl = '<a onClick="validateHrefVulnerability(this)" style="margin-right: 7px;" title="GlobalRule Discovery" id="anker-globalRuleDiscovery-{0}" {1}><img src="./assets/img/svg/global.svg" alt="GlobalRule Discovery" style="width: 16px;"></a>',
						sAnkerProfilingTmpl = '<a onClick="validateHrefVulnerability(this)"  style="margin-right: 7px;" title="Profiling" id="anker-profiling-{0}" {1}><img src="./assets/img/svg/profiling.svg" alt="Profiling" style="width: 16px;"></a>',
						sAdvancedRulesTmpl = '<a onClick="validateHrefVulnerability(this)"  style="margin-right: 7px;" title="Auto Discovered Rule" id="anker-advancedRules-{0}" {1}><img src="./assets/img/svg/auto-discovery.svg" alt="Auto Discovered Rule" style="width: 16px;"></a>',
						sTemplateCreateSuccessTmpl = '<img src="./assets/img/templateSuccess.png" title="Template Success" style="width: 16px;margin-right: 7px;">',
						sTemplateCreateFailureTmpl = '<img src="./assets/img/cancel.png" title="Template Failure" style="width: 16px;margin-right: 7px;">',
						sApprovedTmpl = '<span class="label label-success label-sm" title="Approved"> Approved</span>',
						sReviewPendingTmpl = '<span class="label label-danger label-sm" title="Review Pending"> ReviewPending</span>',
						sWaitingForApprovalTmpl = '<span class="label label-danger label-sm" title="Waiting For Approval"> Waiting For Approval</span>',
						sCopyActionIcon = '', sViewActionIcon = '', sEditActionIcon = '', sDeleteActionIcon = '',sReRunTemplateActionIcon = '', sReRunProfilingActionIcon = '', sGlobalRuleDiscoveryActionIcon = '', sProfilingActionIcon = '', sAdvancedRulesActionIcon = '',
						sTemplateCreateStatusIcon = '', sDeltaApprovalStatusIcon = '',
						oSecurityFlags =  ManageDataTemplateView.ViewPageData.SecurityFlags,
						lIsUserhaveAtLeastOneAccess = ( (oSecurityFlags.Create) || (oSecurityFlags.Update) || (oSecurityFlags.Delete) ) ? true : false;

						aDataTemplateDataList.forEach( function(oDataTemplateRecord, nIndex) {
							oDataTemplateRecord.OrgTemplateName = oDataTemplateRecord.TemplateName;

					if (oSecurityFlags.Create) {
						sCopyActionIcon = sAnkerCopyTmpl.replace('{0}',oDataTemplateRecord.TemplateId);
						sViewActionIcon = sAnkerViewTmpl.replace('{0}',oDataTemplateRecord.TemplateId).replace('{1}', 'href="listdataview?idData='+ oDataTemplateRecord.TemplateId +'&dataLocation='+ oDataTemplateRecord.DataLocation +'&name='+ oDataTemplateRecord.TemplateName + '&description=' + oDataTemplateRecord.Description + '"');
						var sEditActionUrl = (oDataTemplateRecord.DataLocation == 'Derived')? 'href="editDerivedDataTemplate?idData=' : 'href="editDataTemplate?idData=';
						sEditActionIcon = sAnkerEditTmpl.replace('{0}',oDataTemplateRecord.TemplateId).replace('{1}', sEditActionUrl + oDataTemplateRecord.TemplateId +'"');
						sDeleteActionIcon = sAnkerDeleteTmpl.replace('{0}',oDataTemplateRecord.TemplateId).replace('{1}', 'href="deletedatasource?idData='+ oDataTemplateRecord.TemplateId +'"');
						sReRunTemplateActionIcon = sAnkerReRunTemplateTmpl.replace('{0}',oDataTemplateRecord.TemplateId).replace('{1}', 'href="rerunTemplate?idData='+ oDataTemplateRecord.TemplateId +'"');
						sReRunProfilingActionIcon = sAnkerReRunProfilingTmpl.replace('{0}',oDataTemplateRecord.TemplateId).replace('{1}', 'href="rerunTemplateProfiling?idData='+ oDataTemplateRecord.TemplateId +'"');
						sGlobalRuleDiscoveryActionIcon = sAnkerGlobalRuleDiscoveryTmpl.replace('{0}',oDataTemplateRecord.TemplateId).replace('{1}', 'href="loadGlobalRuleDiscoveryForTemplate?idData='+ oDataTemplateRecord.TemplateId +'&templateName=' +oDataTemplateRecord.TemplateName+ '"');
						sProfilingActionIcon = sAnkerProfilingTmpl.replace('{0}',oDataTemplateRecord.TemplateId).replace('{1}', 'href="dataProfiling_View?idData='+ oDataTemplateRecord.TemplateId +'"');
						sAdvancedRulesActionIcon = sAdvancedRulesTmpl.replace('{0}',oDataTemplateRecord.TemplateId).replace('{1}', 'href="advancedRulesListView?idData='+ oDataTemplateRecord.TemplateId +'&templateName='+ oDataTemplateRecord.TemplateName +'&createdByUser='+ oDataTemplateRecord.CreatedByUser + '"');
						sTemplateCreateStatusIcon = (oDataTemplateRecord.Template_create_success == 'Y') ? sTemplateCreateSuccessTmpl : sTemplateCreateFailureTmpl;

						if(oDataTemplateRecord.DeltaApprovalStatus == 'approved'){
							sDeltaApprovalStatusIcon = sApprovedTmpl;
						}
						else if(oDataTemplateRecord.DeltaApprovalStatus == 'reviewpending'){
							sDeltaApprovalStatusIcon = sReviewPendingTmpl;
						}
						else if(oDataTemplateRecord.DeltaApprovalStatus == 'waitingforapproval'){
							sDeltaApprovalStatusIcon = sWaitingForApprovalTmpl;
						}
						else{
						    sDeltaApprovalStatusIcon ='';
						}

					}

					if (lIsUserhaveAtLeastOneAccess) {
						oDataTemplateRecord.TemplateName = oDataTemplateRecord.TemplateName + '</br>';

						oDataTemplateRecord.TemplateName = oDataTemplateRecord.TemplateName + sCopyActionIcon + sViewActionIcon + sEditActionIcon + sDeleteActionIcon;

						if((oDataTemplateRecord.Template_create_success == 'N' && oDataTemplateRecord.DeltaApprovalStatus != 'rejected') || (oDataTemplateRecord.Template_create_success == 'Y' && oDataTemplateRecord.DeltaApprovalStatus == 'approved')){
							oDataTemplateRecord.TemplateName = oDataTemplateRecord.TemplateName + sReRunTemplateActionIcon;
						}
						if (oDataTemplateRecord.Template_create_success == 'Y' && oDataTemplateRecord.ProfilingEnabled == 'Y' && oDataTemplateRecord.DeltaApprovalStatus != 'rejected'){
							oDataTemplateRecord.TemplateName = oDataTemplateRecord.TemplateName + sReRunProfilingActionIcon;
						}

						oDataTemplateRecord.TemplateName = oDataTemplateRecord.TemplateName + sGlobalRuleDiscoveryActionIcon;

						if(oDataTemplateRecord.ProfilingEnabled == 'Y'){
							oDataTemplateRecord.TemplateName = oDataTemplateRecord.TemplateName + sProfilingActionIcon;
						}
						if(oDataTemplateRecord.AdvancedRulesEnabled == 'Y'){
							oDataTemplateRecord.TemplateName = oDataTemplateRecord.TemplateName + sAdvancedRulesActionIcon;
						}

						oDataTemplateRecord.TemplateName = oDataTemplateRecord.TemplateName + sTemplateCreateStatusIcon + sDeltaApprovalStatusIcon;

					}
				});

				PageCtrl.debug.log('processDataActionIcons', lIsUserhaveAtLeastOneAccess);
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
					$('#AvailableProjects input[type="checkbox"]').off('click', ManageDataTemplateView.handleActionClicks).on('click', ManageDataTemplateView.handleActionClicks);
				}
			}
		},

		handleActionClicks : function(oEvent) {
			var oTarget = oEvent.target, oTargetParent = oTarget.parentElement, sTagName = oTarget.tagName, sInputType = oTarget.type,
				lBulkAction = false, nIndexOfRecord = -1, sTargetId = '', nActionCategory = (sTagName.toUpperCase() === 'I' || sTagName.toUpperCase() === 'SPAN') ? 0 : 1;

			sTargetId = (sTagName.toUpperCase() === 'I' || sTagName.toUpperCase() === 'SPAN') ? oTargetParent.id : oTarget.id;

			if (nActionCategory === 0) {

				nIndexOfRecord = UTIL.indexOfRecord(ManageDataTemplateView.ViewPageData.ViewPageDataList, 'TemplateId', sTargetId.split('-')[2]);
				ManageDataTemplateView.IndexOfRecord = nIndexOfRecord;

				if (sTargetId.indexOf('copy') > -1) {
					ManageDataTemplateView.copyAction( ManageDataTemplateView.ViewPageData.ViewPageDataList[nIndexOfRecord] );
				}

			} else {

				nIndexOfRecord = UTIL.indexOfRecord(ManageDataTemplateView.ViewPageData.ViewPageDataList, 'ValidationId', sTargetId.split('-')[2]);

				if (sTagName.toUpperCase() === 'IMG') {
					lBulkAction = (sTargetId === 'select-all-projects') ? true : false;
					$('#AvailableProjects').find('input:checkbox').prop('checked', lBulkAction);
				}
			}
		},

		copyAction : function copyValidation(oDataTemplateRecord) {
			var templateName = oDataTemplateRecord.TemplateName.split('</br>')[0] , idData = oDataTemplateRecord.TemplateId;
			var copyTemplateName = prompt("Please Enter New Template Name", templateName);
			var isNameValidToSave = true;
			do
		  	{
				if(copyTemplateName.trim() == ''){
					alert("Template Name should not be blank..");
					var copyTemplateName = prompt("Please Enter New Template Name", templateName);
				}
				if(copyTemplateName == templateName){
					alert("Template Name should not be same...");
					var copyTemplateName = prompt("Please Enter New Template Name", templateName);
				}
				if(!checkPromptText(copyTemplateName)){
					isNameValidToSave = false;
					alert('Vulnerability in submitted data, not allowed to submit!');
				}
		  	}while(copyTemplateName.trim() == '');



			if (copyTemplateName != null && isNameValidToSave) {
				var form_data = {
					newTemplateName: copyTemplateName,
					idData: idData
				};
				$.ajax({
					type: 'GET',
					url: "./copyTemplate",
					data: form_data,
					success: function (message) {

						window.location.reload();
					}
				});
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
				oRetValue.ProjectIds = ManageDataTemplateView.SelectedProjectId;

			} else {
				Modal.confirmDialog(0, 'Incomplete Selection',	'Please select at least one or more projects to search validations', ['Ok'] );
				oRetValue = {};
			}

			PageCtrl.debug.log('getSearchParameters', oRetValue);

			return oRetValue;
		}

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
	ManageDataTemplateView.setup();
	ManageDataTemplateView.getPaginatedDataTemplateList();

});
</script>