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

				<!--****         BEGIN EXAMPLE TABLE PORTLET       **********-->

				<div class="portlet light bordered">
					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold "> Validation Checks </span>
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
								
								<button id='SearchValidation' class="btn btn-primary DataBtns">Search Validation</button>
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
						<table class="table table-striped table-bordered table-hover" id="validationViewListTBL" style="width: 100%;">
							<thead>
								<tr>
									<th>Validation Id</th>
									<th>Validation Check Name</th>
									<th>Data Template Name</th>									
									<th>App Type</th>
									<th>App Mode</th>
									<th>Project Name</th>								
									<th>Created_On</th>
									<th>Created By</th>
									<%if((Boolean)request.getAttribute("isRuleCatalogDiscovery")) {%>
									<th>Approval Status</th>
									<th>Staging Status</th>
									<th>Approved_on</th>
									<th>Approved_by</th>
									<%}%>
									<th>Status</th>								
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
var ManageValidationView = {
		SelectedProjectId: ${SelectedProjectId},
		ViewPageData: {},
		IndexOfRecord: -1,
		setup: function() {
			$('#SearchValidation').on('click', ManageValidationView.getPaginatedValidationsList);
			$('div img').on('click', ManageValidationView.handleActionClicks);
			$('div img').on('click', ManageValidationView.handleActionClicks);			
		},
		
		getPaginatedValidationsList:  function(oEvent) {
			var sViewTable = '#validationViewListTBL', oSearchParameters = ManageValidationView.getSearchParameters();			
			
			if(!checkInputText()){
				alert('Vulnerability in submitted data, not allowed to submit!');
				return;
			}
			
			if (Object.keys(oSearchParameters).length > 0) {
				JwfAjaxWrapper({
					WaitMsg: 'Loading validation list',
					Url: 'getPaginatedValidationsList',
					Headers:$("#token").val(),
					Data: oSearchParameters,
					CallBackFunction: function(oResponse) {
						ManageValidationView.ViewPageData = oResponse;
						
						PageCtrl.debug.log('Server response got is', ManageValidationView.ViewPageData);						
						
						processDataActionIcons(ManageValidationView.ViewPageData.ViewPageDataList);
						fillAvailableProjects(ManageValidationView.ViewPageData.AllProjectList, ManageValidationView.ViewPageData.SelectedProjectId);			
						
						var aCoulmns = null;
						if(${isRuleCatalogDiscovery}){
							aCoulmns = [
								{ "data": "ValidationId" },
								{ "data": "ValidationCheckName" },
								{ "data": "DataTemplateName" },
								{ "data": "AppType" },
								{ "data": "AppMode" },
								{ "data": "ProjectName" },
								{ "data": "CreatedOn" },            
								{ "data": "CreatedBy" },
								{ "data": "ApprovalStatus" },
								{ "data": "StagingStatus" },
								{ "data": "ApprovedOn" },
								{ "data": "ApprovedBy"},
								{ "data": "Status"}
							];
						}else {
							aCoulmns = [
								{ "data": "ValidationId" },
								{ "data": "ValidationCheckName" },
								{ "data": "DataTemplateName" },
								{ "data": "AppType" },
								{ "data": "AppMode" },
								{ "data": "ProjectName" },
								{ "data": "CreatedOn" },            
								{ "data": "CreatedBy" },
								{ "data": "Status"}
								];
						}
						
						
						$(sViewTable).DataTable ({
							"data" : ManageValidationView.ViewPageData.ViewPageDataList,
							"columns": aCoulmns,
							scrollX: true,
							scrollCollapse: true,
							scrollY: '400px',						
							autoWidth: false,	
							lengthMenu: [ 200, 100, 50 ],
							order: [[ 0, 'desc' ]],
							destroy: true,
							orderClasses: false,
							drawCallback: function( oSettings ) {
								$(sViewTable + ' a').off('click', ManageValidationView.handleActionClicks).on('click', ManageValidationView.handleActionClicks);
								$(sViewTable + ' button').off('click', ManageValidationView.handleActionClicks).on('click', ManageValidationView.handleActionClicks);
							}						
						})
					}
				});
			}		
			
			function processDataActionIcons(aValidationsDataList) {
				var sAnkerCustomizeTmpl = '<a onClick="validateHrefVulnerability(this)"  style="margin-right: 7px;" title="Customize" id="anker-customize-{0}" {1}><i class="fa fa-cogs" aria-hidden="true"></i></a>',
						sAnkerSourceTmpl = '<a onClick="validateHrefVulnerability(this)"  style="margin-right: 7px;" title="Source" id="anker-Source-{0}" {1}><i class="fa fa-file-code-o" aria-hidden="true"></i></a>',
						sAnkerCopyTmpl = '<a onClick="validateHrefVulnerability(this)"  style="margin-right: 7px;" title="Copy" id="anker-copy-{0}"><i class="fa fa-files-o" aria-hidden="true"></i></a>',
						sRuleCatalogTmpl = '<a onClick="validateHrefVulnerability(this)"  style="margin-right: 7px;" title="Rule Catalog" id="anker-ruleCatalog-{0}" {1}><img src="./assets/img/catalog.png" width="16" height="16" /></a>',
						sProfilingTmpl = '<a onClick="validateHrefVulnerability(this)"  style="margin-right: 7px;" title="Profiling" id="anker-profiling-{0}" {1}><img src="./assets/img/profiling.png" width="16" height="16" /></a>',
                        sButtonGlobalRuleDiscoveryTmpl = '<a onClick="validateHrefVulnerability(this)" style="margin-right: 7px;" title="GlobalRule Discovery" class="btn-link" {1}><img src="./assets/img/svg/global.svg" alt="GlobalRule Discovery" style="width: 16px;" /></a>',
						sAdvancedRulesTmpl = '<a onClick="validateHrefVulnerability(this)"  style="margin-right: 7px;" title="Auto Discovered Rules" id="anker-advancedRules-{0}" {1}><img src="./assets/img/auto-discovery.png" width="16" height="16" /></a>',
						sStatusTmpl = '<button class="btn-link" id="button-Status-{0}" ><span class="label label-{1} label-sm">{2}</span></button>',
						sUnitTestRunImpl = '<a onClick="validateHrefVulnerability(this)"  style="margin-right: 7px;" title="UnitTest Run" id="anker-unitTestRun-{0}" {1}><i class="fa fa-play-circle" style="color:blue" aria-hidden="true"></i></a>',
						sFullLoadRunImpl = '<a onClick="validateHrefVulnerability(this)"  style="margin-right: 7px;" title="FullLoad Run" id="anker-fullLoadRun-{0}" {1}><i class="fa fa-play-circle" style="color:green" aria-hidden="true"></i></a>',
						sStatusButton = '', sCopyActionIcon = '', sCustomizeActionIcon = '', sSourceActionIcon = '', sRuleCatalogIcon = '', sProfilingIcon = '', sAdvancedRulesIcon = '', oSecurityFlags =  ManageValidationView.ViewPageData.SecurityFlags,fromMapping=ManageValidationView.ViewPageData.FromMapping,
						lIsUserhaveAtLeastOneAccess = ( (oSecurityFlags.Create) || (oSecurityFlags.Update) || (oSecurityFlags.Delete) ) ? true : false;
				
						aValidationsDataList.forEach( function(oValidationRecord, nIndex) {				
							oValidationRecord.OrgValidationCheckName = oValidationRecord.ValidationCheckName;				

					if (oSecurityFlags.Create) {
						sCustomizeActionIcon = sAnkerCustomizeTmpl.replace('{0}',oValidationRecord.ValidationId).replace('{1}', 'href="customizeValidation?idApp=' + oValidationRecord.ValidationId + '&laName=' + oValidationRecord.ValidationCheckName + '&idData=' + oValidationRecord.DataTemplateId + '&lsName= ' + oValidationRecord.DataTemplateName + '"');
						sCopyActionIcon = sAnkerCopyTmpl.replace('{0}',oValidationRecord.ValidationId);
						sRuleCatalogIcon = sRuleCatalogTmpl.replace('{0}',oValidationRecord.ValidationId).replace('{1}', 'href="getRuleCatalog?idApp=' + oValidationRecord.ValidationId + '&fromMapping=' + fromMapping + '"');
						sSourceActionIcon = sAnkerSourceTmpl.replace('{0}',oValidationRecord.ValidationId).replace('{1}', 'href="listdataview?idData=' + oValidationRecord.DataTemplateId + '&dataLocation=&name='+oValidationRecord.DataTemplateName+'&description="');
						sProfilingIcon = sProfilingTmpl.replace('{0}',oValidationRecord.ValidationId).replace('{1}', 'href="dataProfiling_View?idData=' + oValidationRecord.DataTemplateId + '"');
						sGlobalRuleDiscoveryActionIcon = sButtonGlobalRuleDiscoveryTmpl.replace('{0}',oValidationRecord.ValidationId).replace('{1}', 'href="loadGlobalRuleDiscoveryForValidation?idApp=' + oValidationRecord.ValidationId + '&validationName=' + oValidationRecord.ValidationCheckName + '&pageSource=validation"');
						sAdvancedRulesIcon = sAdvancedRulesTmpl.replace('{0}',oValidationRecord.ValidationId).replace('{1}', 'href="advancedRulesListView?idData=' + oValidationRecord.DataTemplateId + '&templateName=' + oValidationRecord.DataTemplateName + '&createdByUser=' + oValidationRecord.CreatedBy + '"');
						sStatusButton = sStatusTmpl.replace('{0}',oValidationRecord.ValidationId).replace('{1}', (oValidationRecord.Status == 'yes'? 'success' : 'danger')).replace('{2}', (oValidationRecord.Status == 'yes'? 'Active' : 'Inactive'));
						sUnitTestRunIcon = sUnitTestRunImpl.replace('{0}',oValidationRecord.ValidationId).replace('{1}', 'href="runValidationByRunType?idApp=' + oValidationRecord.ValidationId + '&validationName=' + oValidationRecord.ValidationCheckName + '&validationRunType=unit_testing"' );
						sFullLoadRunIcon = sFullLoadRunImpl.replace('{0}',oValidationRecord.ValidationId).replace('{1}', 'href="runValidationByRunType?idApp=' + oValidationRecord.ValidationId + '&validationName=' + oValidationRecord.ValidationCheckName + '&validationRunType=full_load"' );
					}
					
					if (lIsUserhaveAtLeastOneAccess) {
						oValidationRecord.ValidationCheckName = oValidationRecord.ValidationCheckName + '</br>';

						if(oValidationRecord.AppType != 'Data Forensics' || !(${isRuleCatalogDiscovery})) {
							oValidationRecord.ValidationCheckName = oValidationRecord.ValidationCheckName + sCustomizeActionIcon + sSourceActionIcon + sCopyActionIcon;
						}
						else {
							oValidationRecord.ValidationCheckName = oValidationRecord.ValidationCheckName + sRuleCatalogIcon + sCustomizeActionIcon + sSourceActionIcon + sCopyActionIcon+ sGlobalRuleDiscoveryActionIcon;
						}
						
						if(oValidationRecord.ProfilingEnabled == 'Y'){
							oValidationRecord.ValidationCheckName = oValidationRecord.ValidationCheckName + sProfilingIcon;
						}
						if(oValidationRecord.AdvancedRulesEnabled == 'Y'){
							oValidationRecord.ValidationCheckName = oValidationRecord.ValidationCheckName + sAdvancedRulesIcon;
						}

						if(oValidationRecord.Status == 'yes'){

						    if(oValidationRecord.AppType == 'Data Forensics'){
                                if(${isRuleCatalogDiscovery}){
                                    if(oValidationRecord.ApprovalStatus == 'UNIT TEST READY' || oValidationRecord.ApprovalStatus == 'APPROVED FOR PRODUCTION'){
                                        oValidationRecord.ValidationCheckName =  oValidationRecord.ValidationCheckName + sUnitTestRunIcon + sFullLoadRunIcon;
                                    }
                                } else {
                                    oValidationRecord.ValidationCheckName =  oValidationRecord.ValidationCheckName + sFullLoadRunIcon;
                                }
						    } else {
                              oValidationRecord.ValidationCheckName =  oValidationRecord.ValidationCheckName + sFullLoadRunIcon;
                            }

						}
						oValidationRecord.Status = sStatusButton;
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
					$('#AvailableProjects input[type="checkbox"]').off('click', ManageValidationView.handleActionClicks).on('click', ManageValidationView.handleActionClicks);
				}					
			}			
		},




		
		handleActionClicks: function(oEvent) {
			var oTarget = oEvent.target, oTargetParent = oTarget.parentElement, sTagName = oTarget.tagName, sInputType = oTarget.type, 
				lBulkAction = false, nIndexOfRecord = -1, sTargetId = '', nActionCategory = (sTagName.toUpperCase() === 'I' || sTagName.toUpperCase() === 'SPAN') ? 0 : 1;
			
			sTargetId = (sTagName.toUpperCase() === 'I' || sTagName.toUpperCase() === 'SPAN') ? oTargetParent.id : oTarget.id;
			
			if (nActionCategory === 0) {
			
				nIndexOfRecord = UTIL.indexOfRecord(ManageValidationView.ViewPageData.ViewPageDataList, 'ValidationId', sTargetId.split('-')[2]);
				ManageValidationView.IndexOfRecord = nIndexOfRecord;

				if (sTargetId.indexOf('copy') > -1) {			
					ManageValidationView.copyAction( ManageValidationView.ViewPageData.ViewPageDataList[nIndexOfRecord] );
				}
				else if(sTargetId.indexOf('Status') > -1){
					ManageValidationView.changeStatusAction( ManageValidationView.ViewPageData.ViewPageDataList[nIndexOfRecord] );
				}
			} else {
			
				nIndexOfRecord = UTIL.indexOfRecord(ManageValidationView.ViewPageData.ViewPageDataList, 'ValidationId', sTargetId.split('-')[2]);
				
				if (sTagName.toUpperCase() === 'IMG') {
					lBulkAction = (sTargetId === 'select-all-projects') ? true : false;
					$('#AvailableProjects').find('input:checkbox').prop('checked', lBulkAction);
				}
			}				
		},
		
		changeStatusAction: function changeStatusAction(oValidationRecord) {
			var oStatusData = { idApp: parseInt(oValidationRecord.ValidationId) },
				sStatusConfirmMsg = "Are you sure to change Status of validation '{0}'".replace('{0}', oValidationRecord.ValidationId + ' - ' + oValidationRecord.OrgValidationCheckName);

			window.scrollTo(0,0);
			Modal.confirmDialog(0, 'Confirm Status Change', sStatusConfirmMsg, ['Yes', 'No'], { onDialogClose: cbConfirmStatusAction } );	

			return;

			function	cbConfirmStatusAction(oEvent) {
				var sEventId = oEvent.EventId, sBtnSelected = oEvent.ClickedButton;

				switch(sEventId) {
					case jwfGlobal.EVT_MODAL_DIALOG_ONLOAD:
						break;
						
					case jwfGlobal.EVT_MODAL_DIALOG_ONCLOSE:
						if (sBtnSelected === 'Yes') { doStatusAction(); }
						break;
				}
			}	

			function doStatusAction() {
				$.ajax({
					type : 'GET',	        		
					url : "./inactivateValidation",
					data : oStatusData,
					success : function(message) {
						window.location.reload();
					} 
				});
			}				
		},
		
		copyAction: function copyValidation(oValidationRecord) {
			var sOrgValidationCheckName = oValidationRecord.OrgValidationCheckName, sOrgValidationId = oValidationRecord.ValidationId, sNewValidationCheckName = sOrgValidationCheckName, 
				lContinue = true, nAttempts = 2, oCopyData = {};
			var isNameValidToSave = true;

			sNewValidationCheckName = prompt("Enter new Validation Name.", sOrgValidationCheckName);
			do
		  	{
				if(sNewValidationCheckName.trim() == ''){
					alert("Validation Name should not be blank..");
					var sNewValidationCheckName = prompt("Please Enter New Validation Name", sOrgValidationCheckName);
				}
				if(sNewValidationCheckName == sOrgValidationCheckName){
					alert("Validation Name should not be same...");
					var sNewValidationCheckName = prompt("Please Enter New Validation Name", sOrgValidationCheckName);
				}
				if(!checkPromptText(sNewValidationCheckName)){ 
					isNameValidToSave = false;
					alert('Vulnerability in submitted data, not allowed to submit!');
				}
		  	}while(sNewValidationCheckName.trim() == '');
				
		

			if ((sNewValidationCheckName.trim().toLowerCase() !== sOrgValidationCheckName.trim().toLowerCase()) && isNameValidToSave) {
				oCopyData = { newValidationName : sNewValidationCheckName,	idApp: parseInt(oValidationRecord.ValidationId) };

				$.ajax({
					type : 'GET',	        		
					url : "./copyValidation",
					data : oCopyData,
					success : function(message){
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
				oRetValue.ProjectIds = ManageValidationView.SelectedProjectId;
				
			} else {				
				Modal.confirmDialog(0, 'Incomplete Selection',	'Please select at least one or more projects to search validations', ['Ok'] );
				oRetValue = {};
			}
			
			PageCtrl.debug.log('getSearchParameters', oRetValue);
			
			return oRetValue;
		},
}


$(document).ready(function(){
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
	ManageValidationView.setup();
	ManageValidationView.getPaginatedValidationsList();		
});	
</script>

</head>
<!-- END CONTAINER -->

