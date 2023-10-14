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
							<span class="caption-subject bold "> Extended Template View Rules </span>
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
								
								<button id='SearchCustomRules' class="btn btn-primary DataBtns">Search Rules</button>
							</td>
						</tr>
						<tr>
							<td colspan="2">
								<label>Search Text in rule or template name or expression</label></br>
								<input class="form-control" id='SearchText' type='text'>
							</td>
						</tr>
					</table>
					
					<br/>
					
					<div class="portlet-body">
						<table class="table table-striped table-bordered table-hover" id="customRulesViewList" style="width: 100%;">
							<thead>
								<tr>
									<th>Rule Id</th>
									<th>Rule Name</th>
									<th>Rule Type</th>									
									<th>Expression</th>								
									<th>Dimension</th>
									<th>Template Id	</th>
									<th>Template Name</th>									
									<th>Project Name</th>
									<th>Created_On</th>
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

<script>	
	var ManageCustomRulesView = {
		SelectedProjectId: ${SelectedProjectId},
		ViewPageData: {},
		IndexOfRecord: -1,	
		setup: function() {
			$('#SearchCustomRules').on('click', ManageCustomRulesView.getPaginatedCustomRulesList);
			$('div img').on('click', ManageCustomRulesView.handleActionClicks);
			$('div img').on('click', ManageCustomRulesView.handleActionClicks);			
		},		
		getPaginatedCustomRulesList:  function(oEvent) {
			var sViewTable = '#customRulesViewList', oSearchParameters = ManageCustomRulesView.getSearchParameters();			
			
			if(!checkInputText()){ 
				alert('Vulnerability in submitted data, not allowed to submit!');
				return;
			}
			
			if (Object.keys(oSearchParameters).length > 0) {
				JwfAjaxWrapper({
					WaitMsg: 'Loading extended rule list',
					Url: 'getPaginatedCustomRulesList',
					Headers:$("#token").val(),
					Data: oSearchParameters,
					CallBackFunction: function(oResponse) {
						ManageCustomRulesView.ViewPageData = oResponse;
						
						PageCtrl.debug.log('Server response got is', ManageCustomRulesView.ViewPageData);						
						
						processDataActionIcons(ManageCustomRulesView.ViewPageData.ViewPageDataList);
						fillAvailableProjects(ManageCustomRulesView.ViewPageData.AllProjectList, ManageCustomRulesView.ViewPageData.SelectedProjectId);			
						
						$(sViewTable).DataTable ({
							"data" : ManageCustomRulesView.ViewPageData.ViewPageDataList,
							"columns": [
								{ "data": "RuleId" },
								{ "data": "RuleName" },
								{ "data": "RuleType" },
								{ "data": "Expression" },
								{ "data": "Dimension" },
								{ "data": "TemplateId" },            
								{ "data": "TemplateName" },
								{ "data": "ProjectName" },
								{ "data": "CreatedAt" },
								{ "data": "CreatedBy" }				
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
								$(sViewTable + ' a').off('click', ManageCustomRulesView.handleActionClicks).on('click', ManageCustomRulesView.handleActionClicks);
							}						
						})
					}
				});
			}		
			function processDataActionIcons(aRuleDataList) {
				var sAnkerEditTmpl = '<a onClick="validateHrefVulnerability(this)"  style="margin-right: 7px;" title="Edit" id="anker-edit-{0}" {1}><i class="fa fa-edit" aria-hidden="true"></i></a>',
						sAnkerDeleteTmpl = '<a onClick="validateHrefVulnerability(this)"  style="margin-right: 7px;" title="Delete" id="anker-delete-{0}"><i class="fa fa-trash" aria-hidden="true"></i></a>',
						sAnkerCopyTmpl = '<a onClick="validateHrefVulnerability(this)"  style="margin-right: 7px;" title="Copy" id="anker-copy-{0}"><i class="fa fa-files-o" aria-hidden="true"></i></a>',
						sCopyActionIcon = '', sEditActionIcon = '', sDeleteActionIcon = '', oSecurityFlags =  ManageCustomRulesView.ViewPageData.SecurityFlags,
						lIsUserhaveAtLeastOneAccess = ( (oSecurityFlags.Create) || (oSecurityFlags.Update) || (oSecurityFlags.Delete) ) ? true : false;
				
				aRuleDataList.forEach( function(oRuleRecord, nIndex) {				
					oRuleRecord.OrgRuleName = oRuleRecord.RuleName;				

					if (oRuleRecord.Expression.length > 40) { 
						oRuleRecord.Expression = UTIL.chopLongText(oRuleRecord.Expression, 40, '</br>');						
					}					
					
					if (oSecurityFlags.Create) {
						sEditActionIcon = sAnkerEditTmpl.replace('{0}',oRuleRecord.RuleId).replace('{1}', 'href="editExtendTemplateRule?idListColrules=' + oRuleRecord.RuleId + '"');
						sCopyActionIcon = sAnkerCopyTmpl.replace('{0}',oRuleRecord.RuleId);
					}
					
					if (oSecurityFlags.Delete) {
						sDeleteActionIcon = sAnkerDeleteTmpl.replace('{0}',oRuleRecord.RuleId);
					}
					
					if (lIsUserhaveAtLeastOneAccess) {
						oRuleRecord.RuleName = oRuleRecord.RuleName + '</br>' + sEditActionIcon + sCopyActionIcon + sDeleteActionIcon;
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
					$('#AvailableProjects input[type="checkbox"]').off('click', ManageCustomRulesView.handleActionClicks).on('click', ManageCustomRulesView.handleActionClicks);
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
				oRetValue.ProjectIds = ManageCustomRulesView.SelectedProjectId;
				
			} else {				
				Modal.confirmDialog(0, 'Incomplete Selection',	'Please select at least one or more projects to search rules', ['Ok'] );
				oRetValue = {};
			}
			
			PageCtrl.debug.log('getSearchParameters', oRetValue);
			
			return oRetValue;
		},
		handleActionClicks: function(oEvent) {
			var oTarget = oEvent.target, oTargetParent = oTarget.parentElement, sTagName = oTarget.tagName, sInputType = oTarget.type, 
				lBulkAction = false, nIndexOfRecord = -1, sTargetId = '', nActionCategory = (sTagName.toUpperCase() === 'I') ? 0 : 1;
			
			sTargetId = (sTagName.toUpperCase() === 'I') ? oTargetParent.id : oTarget.id;
			
			if (nActionCategory === 0) {
			
				nIndexOfRecord = UTIL.indexOfRecord(ManageCustomRulesView.ViewPageData.ViewPageDataList, 'RuleId', sTargetId.split('-')[2]);
				ManageCustomRulesView.IndexOfRecord = nIndexOfRecord;

				if (sTargetId.indexOf('delete') > -1) {
					ManageCustomRulesView.deleteAction( ManageCustomRulesView.ViewPageData.ViewPageDataList[nIndexOfRecord] );

				} else if (sTargetId.indexOf('copy') > -1) {			
					ManageCustomRulesView.copyAction( ManageCustomRulesView.ViewPageData.ViewPageDataList[nIndexOfRecord] );
				}
			} else {
			
				if (sTagName.toUpperCase() === 'IMG') {
					lBulkAction = (sTargetId === 'select-all-projects') ? true : false;
					$('#AvailableProjects').find('input:checkbox').prop('checked', lBulkAction);
				}			
			}				
		},
		copyAction: function copyCustomRule(oRuleRecord) {
			var sOrgRuleName = oRuleRecord.OrgRuleName, sOrgRuleId = oRuleRecord.RuleId, sNewRuleName = sOrgRuleName, 
				lContinue = true, nAttempts = 2, oCopyData = {};
			var isNameValidToSave = true;	
			do
			{
				sNewRuleName = prompt("Enter new rule name.", sOrgRuleName);
				if(sNewRuleName.trim() == ''){
					alert("Rule Name should not be blank..");
				}
				if(sNewRuleName == sOrgRuleName){
					alert("Rule Name should not be same...");
				}
				if(!checkPromptText(sNewRuleName)){ 
					isNameValidToSave = false;
					alert('Vulnerability in submitted data, not allowed to submit!');
				}
				
			} while ( sNewRuleName.trim() == '' );

			if ((sNewRuleName.trim().toLowerCase() !== sOrgRuleName.trim().toLowerCase()) && isNameValidToSave) {
				oCopyData = { newRuleName : sNewRuleName,	idListColrules: parseInt(oRuleRecord.RuleId) };

				$.ajax({
					type : 'GET',	        		
					url : "./copyRules",
					headers: { 'token':$("#token").val()},
					data : oCopyData,
					success : function(message){
						window.location.reload();
					} 
				});
			}
		},
		deleteAction: function copyCustomRule(oRuleRecord) {
			var oDeleteData = { idListColrules: parseInt(oRuleRecord.RuleId) },
				sDeleteConfirmMsg = "Are you sure to Delete Rule '{0}'".replace('{0}', oRuleRecord.RuleId + ' - ' + oRuleRecord.OrgRuleName);

			window.scrollTo(0,0);
			Modal.confirmDialog(0, 'Confirm Delete', sDeleteConfirmMsg, ['Yes', 'No'], { onDialogClose: cbConfirmDeleteAction } );	

			return;

			function	cbConfirmDeleteAction(oEvent) {
				var sEventId = oEvent.EventId, sBtnSelected = oEvent.ClickedButton;

				switch(sEventId) {
					case jwfGlobal.EVT_MODAL_DIALOG_ONLOAD:
						break;
						
					case jwfGlobal.EVT_MODAL_DIALOG_ONCLOSE:
						if (sBtnSelected === 'Yes') { doDeleteAction(); }
						break;
				}
			}	

			function doDeleteAction() {
				$.ajax({
					type : 'POST',	        		
					url : "./deleteIdListColRulesData",
					headers: { 'token':$("#token").val()},
					data : oDeleteData,
					success : function(message) {
						window.location.reload();
					} 
				});
			}				
		}
	}
	
	$(document).ready(function(){
		var date_input=$('input[name="date"]'); //our date input has the name "date"
		var container=$('.bootstrap-iso form').length > 0 ? $('.bootstrap-iso form').parent() : "body";
		
		PageCtrl.debug.log('Page getting reloaded', $('#AvailableProjects').html().length);
		
		date_input.datepicker({
			format: 'yyyy-mm-dd',
			container: container,
			todayHighlight: true,
			autoclose: true,
			endDate: "today"
		})
		
		initializeJwfSpaInfra();	
		ManageCustomRulesView.setup();
		ManageCustomRulesView.getPaginatedCustomRulesList();		
	});	
</script>
</head>