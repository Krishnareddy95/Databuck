<%@page import="com.databuck.service.RBACController"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<style>
	
	#tblDomainViewList {
		background: #f2f2f2;
		border-bottom: 2px solid #808080;
	}
	
	#DomainMainParent textarea {
		width: 100%; 
		min-height: 125px;
		margin-top: 10px;
		padding: 5px;
		font-size: 12.5px;
		font-family: "Lucida Console", Courier, monospace;
	}
	
	.LongTextBox {
		width: 550px !important;
		height: 32px !important;
	}
	
	#AssginedProjects {
		border: 2px dotted grey;
		padding: 15px;
		max-height: 350px;
		overflow-x: hidden;
		overflow-y: auto;		
	}
	
	#AssginedProjects label, #AssginedProjects input {
		margin-bottom: 12px;
	}	

	input[type=checkbox] {
		width: 20px; 
		height: 20px;
		margin-right: 10px;
	}
	
	#AssginedProjects label {
		margin-top: -5px;
	}
	
	.RowInEditing {
		background-color: #3973ac !important; /* #0066cc */
		color: white;
		weight: bold;
	}
	
	#BtnNew {
		width: 220px !important; 	
		float: left; 
	}		

	#BtnSave, #BtnCancel {
		width: 150px !important; 	
		float: right; 
	}		
	
	#DataButtons {
		margin-top: 15px;
		margin-bottom: 15px !important;
	}
	
	#DataButtons button {
		margin-right: 15px !important; 
		text-align: center !important;
		font-size: 11.5px !important;
	}
	
</style>
<div class="page-content-wrapper">
	<div class="page-content">
		<div class="row">
			<div class="col-md-12">
				<div class="portlet light bordered">
					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span id='spanTitle' class="caption-subject bold ">Domain AccessKey and Projects</span>
						</div>
					</div>
					<div class="portlet-body">
						<div id='DomainMainParent' style="border: 2px solid #d9d9d9; padding: 15px;">
							<table id='tblDataFormContainer' width='100%'>
								<tr width='100%'>
									<td width='40%' valign='top'>
										<label for='DomainName'>Domain AccessKey</label></br>
										<input type='text' id='DomainName' class='LongTextBox'>
										</br></br>
										
										<label for='Description'>Description</label></br>
					        			<input type='text' id='DomainDescription' class='LongTextBox'>
					        			<br></br>
					        			
										<input type='checkbox' id='IsGlobalDomain' >					        			
										<label for='IsGlobalDomain'>Global Domain</label>
									</td>
									<td width='60%' valign='top'>
										<label>Projects belong to Domain AccessKey:</label></br>
										<div id='AssginedProjects'>											
										</div>
									</td>
								</tr>
								<tr>
							</table>
							
							<div id='DataButtons'>
								<button class='btn btn-primary DataBtns' id='BtnNew'>Add New Domain</button>
								<button class='btn btn-primary DataBtns' id='BtnCancel'>Cancel</button>
								<button class='btn btn-primary DataBtns' id='BtnSave'>Save</button>																														
							</div>					
							</br></br>
							
						</div>							
						</br>
						<table id='tblDomainViewList' class='table table-striped table-bordered table-hover' width='100%'>
							<thead>
								<tr>									
									 <th>Domain ID</th>
									 <th>Domain AccessKey</th>
									 <th>Is Global Domain</th>
									 <th>Project Ids</th>
									 <th>Edit</th>
									 <th>Delete</th>		 
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

<script src="./assets/global/plugins/jquery.min.js" type="text/javascript"> </script>
<script src="./assets/global/plugins/jquery.dataTables.min.js" type="text/javascript"></script>

<head>
	<link rel="stylesheet" href="./assets/jwf/content/JwfSpaInfra.css">
	<script src="./assets/jwf/scripts/JwfSpaInfra.js"></script>
</head>

<script>
	var ManageDomainDataEntry = {
		PageData : {},
		NewRecord: { "DomainId": "-1", "DomainName": "", "DomainDescription": "", "IsGlobalDomain": "No", "ProjectIds": "" },
		DatabaseRecord: {},
		EditingRecord: {},
		ProjectHtml: "<input type='checkbox' id='ProjectId-{0}' {1}><label style='width: 220px; left-margin: 5px;' for='ProjectId-{0}'>&nbsp;&nbsp;{2}</label></br>",		
		setup: function() {
			$('#tblDataFormContainer tr td :input').on('change', ManageDomainDataEntry.mainEventHandler);
			
			$('#BtnNew').on('click', ManageDomainDataEntry.mainEventHandler);
			$('#BtnCancel').on('click', ManageDomainDataEntry.mainEventHandler);
			$('#BtnSave').on('click', ManageDomainDataEntry.mainEventHandler);			
		},			
		loadDomainList: function() {
			var sViewTable = '#tblDomainViewList';
			
			JwfAjaxWrapper({
				WaitMsg: 'Loading Domain Records',
				Url: 'loadDomainRecordList',
				Headers:$("#token").val(),
				Data: {},
				CallBackFunction: function(oResponse) {
					ManageDomainDataEntry.PageData = oResponse;					
				
					ManageDomainDataEntry.DataTable = $("#tblDomainViewList").DataTable ({
						"data" : ManageDomainDataEntry.PageData.DataSet,
						"columns" : [							
							{ "data" : "DomainId" },
							{ "data" : "DomainName" },
							{ "data" : "IsGlobalDomain" },
							{ "data" : "ProjectIds" },
							{ "data" : "DomainId-edit" },
							{ "data" : "DomainId-delete" }							
						],
						order: [[ 0, "asc" ]],
						orderClasses: false,						
						destroy: true,
						drawCallback: function( oSettings ) {
							$('#tblDomainViewList a').off('click', ManageDomainDataEntry.mainEventHandler).on('click', ManageDomainDataEntry.mainEventHandler);
						}					 
					});				
				}				
			});
			
			/* Programmatically generate click event i.e. as if user has clicked first row for editing */
			$(sViewTable).on('init.dt', function () {
				var oPageLoadEvent = {};
				
				try {				
					$(sViewTable + ' tr').each(function (nRow, oRow) {				
						if (nRow === 1) {				
							oPageLoadEvent.target = $(oRow).find('i')[0];
							ManageDomainDataEntry.mainEventHandler(oPageLoadEvent);
						}
					});
				
				} catch (oError) { 
					PageCtrl.debug.log('Error occured in data table event', oError.message);
				}
			});			
			
		},
		resetRowHighlighting: function() {
			$("#tblDomainViewList > tbody > tr").each(function () {
				$(this).removeClass('RowInEditing');				
			});
		},		
		mainEventHandler: function(oEvent) {		
			var oTarget = oEvent.target, sTagName = oTarget.tagName.toLowerCase(), sType = oTarget.type, oEffectiveTarget = (sTagName === 'i') ? oTarget.parentElement : oTarget;
			var sTargetId = oEffectiveTarget.id, sContextKey = '', sEventContext = ''; 
			var sFlowType = ( (sTargetId === 'BtnSave') || (sTargetId.indexOf('delete') > -1) ) ? 'server-flow' : 'client-flow'; 			

			var oContextMatrix = { 
					"server-flow": { "BtnSave" : "DataSave", "delete" : "DataDelete" }, 
					"client-flow": { "DataFormChanged" : "DataFormChanged", "ProjectId" : "ProjectStateChanged", "BtnNew" : "DataNew", "BtnCancel" : "DataCancel", "edit" : "DataEdit" }
				};			
			
			/* Get context key to retrieve event context */ 
			if (sTagName === 'i') {													// edit and delete icons
				sContextKey = sTargetId.split('-')[0];
			
			} else if ( (sTargetId === 'IsGlobalDomain') || ('text'.indexOf(sType) > -1) )  {				// data form changed					
				sContextKey = 'DataFormChanged';
				
			} else if ( (sType === 'checkbox') && (sTargetId.indexOf('ProjectId') > -1) )	{			
				sContextKey = 'ProjectId';											// project checked / unchecked				
				
			} else if (sTagName === 'button') {									// data buttons save, new and cancel
				sContextKey = sTargetId;
			}				
			
			/* Get context key to determine event context */
			sEventContext = oContextMatrix[sFlowType][sContextKey];			
			
			switch (sEventContext) {		
				case 'DataFormChanged':
					ManageDomainDataEntry.dataFormChanged(oEffectiveTarget, sTargetId);
					break;
			
				case 'ProjectStateChanged':
					ManageDomainDataEntry.projectStateChanged(oEffectiveTarget, sTargetId);
					break;
				
				case 'DataNew':
					ManageDomainDataEntry.beginDataEditing(sEventContext, '-1', oEffectiveTarget);
					break;
					
				case 'DataEdit':
					ManageDomainDataEntry.beginDataEditing(sEventContext, sTargetId.split('-')[1], oEffectiveTarget);
					break;

				case 'DataCancel':
					ManageDomainDataEntry.beginDataEditing(sEventContext, ManageDomainDataEntry.DatabaseRecord.DomainId, oEffectiveTarget);
					break;

				case 'DataSave': case 'DataDelete':
					ManageDomainDataEntry.invokeMainServerHandler(sEventContext, sTargetId.split('-')[1]);
					break;
				
				default:
			}			
			
			PageCtrl.debug.log('mainEventHandler 01 ' + sEventContext, 'sTagName = ' + sTagName + ', sType = ' + sType + ', sTargetId = ' + sTargetId + ', sFlowType = ' + sFlowType);	
		},
		beginDataEditing: function(sDataEditContext, sDomainId, oEffectiveTarget) {
			var oFocusRecord = {}, oTableRow = null, lIsNewRecord = (sDomainId === '-1') ? true : false;
			
			if (sDataEditContext === 'DataCancel') { 
				oFocusRecord = ManageDomainDataEntry.DatabaseRecord;						// reset edited data - get database copy saved at begin editing				
			} else {
				oFocusRecord = getDomainRecordByRow(sDomainId);								// begin editing - save either new or DB copy into variable
				ManageDomainDataEntry.resetRowHighlighting();
			}			
			
			ManageDomainDataEntry.DatabaseRecord = oFocusRecord;
			ManageDomainDataEntry.EditingRecord = JSON.parse( JSON.stringify(oFocusRecord) );
			
			loadDataForm(oFocusRecord, lIsNewRecord);
			
			if (sDataEditContext === 'DataEdit') {
				oTableRow = $(oEffectiveTarget).closest('tr');
				$(oTableRow).addClass('RowInEditing');
			}			
			
			function getDomainRecordByRow(sDomainId) {
				var nRecordIndex = -2, oRetValue = null; 

				if (sDomainId === '-1') {
					oRetValue = JSON.parse( JSON.stringify(ManageDomainDataEntry.NewRecord) );
				} else {
					nRecordIndex = UTIL.indexOfRecord(ManageDomainDataEntry.PageData.DataSet, 'DomainId', sDomainId);
					if (nRecordIndex > -1) { 
						oRetValue = JSON.parse( JSON.stringify(ManageDomainDataEntry.PageData.DataSet[nRecordIndex]) ); 
					}
				}
				return oRetValue;
			}
			
			function loadDataForm(oFocusRecord, lIsNewRecord) {
				var aFieldList = [ 'DomainName','DomainDescription','IsGlobalDomain' ], aProjectRowIds = Object.keys(ManageDomainDataEntry.PageData.ProjectData), 
					aAssignedProjectIds = (oFocusRecord.ProjectIds) ? oFocusRecord.ProjectIds.split(',') : [], 
					sFocusField = '#' + ( (lIsNewRecord) ? aFieldList[0] : aFieldList[1] ),
					lIsGlobalDomain = (oFocusRecord.IsGlobalDomain === 'Yes') ? true : false, 
					sProjectHtmlTmpl = '', lProjectAssigned = false, sProjectFullHtml = '',
					sTitle = 'Domain AccessKey and Projects ({0})'.replace('{0}', ((lIsNewRecord) ? 'New' : oFocusRecord.DomainId));

				$('#DomainName').prop('disabled',!lIsNewRecord);
				$(sFocusField).focus();
				$('#spanTitle').html(sTitle);
				
				aFieldList.forEach( function(sFieldName, nIndex) {
					if (sFieldName === 'IsGlobalDomain') {
						$('#IsGlobalDomain').prop('checked',lIsGlobalDomain);
					} else {
						$('#' + sFieldName).val( oFocusRecord[sFieldName] );
					}						
				});				
				
				aProjectRowIds.forEach( function(sProjectRowId, nIndex) {
					sProjectHtmlTmpl = ManageDomainDataEntry.ProjectHtml;			
					lProjectAssigned = (aAssignedProjectIds.indexOf(sProjectRowId) > -1) ? true : false;

					sProjectHtmlTmpl = UTIL.replaceAll(sProjectHtmlTmpl, '{0}', sProjectRowId);			
					sProjectHtmlTmpl = UTIL.replaceAll(sProjectHtmlTmpl, '{1}', (lProjectAssigned ? 'checked' : ''));
					sProjectHtmlTmpl = UTIL.replaceAll(sProjectHtmlTmpl, '{2}', ManageDomainDataEntry.PageData.ProjectData[sProjectRowId].ProjectName);

					sProjectFullHtml = sProjectFullHtml + sProjectHtmlTmpl;
				});
				$('#AssginedProjects').html(sProjectFullHtml);
				$('#AssginedProjects :input[type=checkbox]').off('click', ManageDomainDataEntry.mainEventHandler).on('click', ManageDomainDataEntry.mainEventHandler);			
			}			
		},
		dataFormChanged: function(oTarget, sTargetId) {
			var oFocusRecord = ManageDomainDataEntry.EditingRecord, lIsGlobalDomain = oTarget.checked;			
			
			if (sTargetId === 'IsGlobalDomain') {
				oFocusRecord[sTargetId] = (lIsGlobalDomain) ? 'Yes' : 'No';
			} else {
				oFocusRecord[sTargetId] = $('#' + sTargetId).val();
			}	
			PageCtrl.debug.log('dataFormChanged ' + sTargetId,  oFocusRecord);
		},
		projectStateChanged: function(oTarget, sTargetId) {
			var oFocusRecord = ManageDomainDataEntry.EditingRecord, 
				aAssignedProjectIds = (oFocusRecord.ProjectIds) ? oFocusRecord.ProjectIds.split(',') : [], 
				lProjectCheckValue = oTarget.checked, sProjectRowId = '', nFoundIndex = -2;
			
			//PageCtrl.debug.log('projectStateChanged 01',  sTargetId + ',' + oTarget.checked + ', At Exit row ids = **' + ManageDomainDataEntry.EditingRecord.ProjectIds + '**');				
			
			if ( sTargetId.split('-').length > 1 ) {
				sProjectRowId = sTargetId.split('-')[1];					

				if (lProjectCheckValue) {
					aAssignedProjectIds.push(sProjectRowId);
				} else {	
					nFoundIndex = aAssignedProjectIds.indexOf(sProjectRowId);						
					if (nFoundIndex > -1) { aAssignedProjectIds.splice(nFoundIndex,1); }
				}
				ManageDomainDataEntry.EditingRecord.ProjectIds = aAssignedProjectIds.join();
			}
			
			//PageCtrl.debug.log('projectStateChanged 02',  sTargetId + ',' + oTarget.checked + ', At Exit row ids = **' + ManageDomainDataEntry.EditingRecord.ProjectIds + '**');
		},
		invokeMainServerHandler: function(sEventContext, sDomainId) {
			if (sEventContext === 'DataDelete') {
				deleteDomainRecord(sEventContext, sDomainId);
			
			} else if (sEventContext === 'DataSave') {
				saveDomainRecord(sEventContext);	
			}				
			
			function saveDomainRecord(sDomainId) {
				var oDataRecord = UTIL.extractSubObjectByFields(ManageDomainDataEntry.EditingRecord, 'IsGlobalDomain,DomainId,DomainName,ProjectIds,ProjectNames,DomainDescription'), oData = { "Context": sEventContext, "Data": oDataRecord }, sValidationMsg = '',	sWaitMsg = 'Saving domain data';
				
				sValidationMsg = isDataValidToSave(oDataRecord);
				sValidationMsg = checkInputText() ? "" : "Vulnerability in submitted data, not allowed to submit!";
				if (sValidationMsg.length > 0) {
					Modal.confirmDialog(0, 'Invalid Data To Save',	sValidationMsg, ['Ok'] );
				} else {
					invokeService(oData, sWaitMsg);
				}							
			}			
			
			function isDataValidToSave(oDataRecord) {
				return ((oDataRecord.DomainId === '-1') && (oDataRecord.DomainName.trim().length < 1)) ? 'Please enter Domain Name, it cannot be blank' : '';
			}			
			
			function deleteDomainRecord(sEventContext, sDomainId) {
				var oDataRecord = {}, oData = { "Context": sEventContext, "Data": {} }, nRecordIndex = -2, sWaitMsg = 'Deleting selected domain';
				var sConfimDialog = "Domain '{0}' including project mapping linked to it will be deleted, Are you sure?";				
			
				nRecordIndex = UTIL.indexOfRecord(ManageDomainDataEntry.PageData.DataSet, 'DomainId', sDomainId);
				if (nRecordIndex < 0) {	return; }
				
				oDataRecord = JSON.parse( JSON.stringify(ManageDomainDataEntry.PageData.DataSet[nRecordIndex]) ); 
				oData.Data = UTIL.extractSubObjectByFields(oDataRecord, 'IsGlobalDomain,DomainId,DomainName,ProjectIds,ProjectNames,DomainDescription');
				
				window.scrollTo(0,0);
				Modal.confirmDialog(0, 'Confirm Delete', sConfimDialog.replace('{0}', oDataRecord.DomainName), ['Yes', 'No'], { onDialogClose: cbConfirmAction });
			
				function cbConfirmAction(oEvent) {
					var sEventId = oEvent.EventId, sBtnSelected = oEvent.ClickedButton;					
					if (sBtnSelected === 'Yes') { 
						invokeService(oData, sWaitMsg);
					}
				}
			}
			
			function invokeService(oData, sWaitMsg) {
				JwfAjaxWrapper({
					WaitMsg: sWaitMsg,
					Url: 'mainDomainHandler',
					Headers:$("#token").val(),
					Data: { DomainData: JSON.stringify(oData) },
					CallBackFunction: function(oResponse) {
						PageCtrl.debug.log('invokeService',  oResponse);
						if (oResponse.Status != false) {
							toastr.info(oResponse.Msg);
							ManageDomainDataEntry.loadDomainList();
						} else {
							//Modal.confirmDialog(1, 'Save Failed','Problem in one of the input parameters', ['Ok'] );
							Modal.confirmDialog(1, 'Save Failed', oResponse.Msg, ['Ok'] );
						}
					}
				});
			}
		}		
	}

	$(document).ready(function () {
		initializeJwfSpaInfra();
		ManageDomainDataEntry.setup();
		ManageDomainDataEntry.loadDomainList();

	});
</script>