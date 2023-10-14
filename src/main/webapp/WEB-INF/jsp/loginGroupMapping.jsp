<%@page import="com.databuck.service.RBACController"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<style>
	#tblLoginGroupMappingList {
		margin-top: 15px;
	}
	
	#data-form {
		border: 0px solid #a6a6a6;
		padding: 10px;
	}	

	td input[type="text"] {
		float: left;
		height: 32px !important;
		width: 250px;
		margin-left: 0px;
	}
	
	td input[type="checkbox"] {
		height: 18px; 
		width: 18px;	
	}
	
	tbody {
	    overflow: scroll;
	    overflow-wrap: anywhere;
	    max-width: 100% !important;
	    width: 100%;
	}

	td label {
		height: 28px !important;
		margin-bottom: 3px;
	}
	
	.DataBtns {
		text-align: center !important;
		min-width: 150px;
	}	
	
	.RowInEditing {
		background-color: #3973ac !important; /* #0066cc */
		color: white;
		weight: bold;
	}
	
	.RowInEditing a, .RowInEditing input[type=checkbox] {
		display: none;
	}
	
</style>
<div class="page-content-wrapper">
	<div class="page-content">
		<div class="row">
			<div class="col-md-12">
				<div class="portlet light bordered">
					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold"
								style="float: left !important;">Assign Login Groups to Roles and Projects</span> 
						</div>						
					</div>	
					
					<div class="portlet-body ">
						<div style="border: 2px solid #d9d9d9; padding: 15px;">
							<table id="data-form-parent" width="100%">
								<tr width="100%">
									<td width="33%" valign="top">
										<label>Login Group Name</label></br>
										<input type="text" id="LoginGroupName">
										</br></br></br>
										<label>Belong to Approver Group</label></br>
										<input type="checkbox" id="IsApproverInt">										
									</td>
									<td width="33%" valign="top">
										<label>Assigned Roles :</label></br>
										<div id="AssginedRoles" style="max-height: 350px; overflow-x: hidden; overflow-y: auto;">
										</div>
									</td>
									<td width="33%" valign="top">
										<label>Assigned Projects :</label></br>
										<div id="AssginedProjects" style="margin-left: 25px; max-height: 350px; overflow-x: hidden; overflow-y: auto;">
										</div>
									</td>
								</tr>
								<tr>
							</table>
							<hr>
							<div id="DataButtons">
								<button class="btn btn-primary DataBtns" id="BtnAddNew" style="margin-right: 350px;">Add New Group</button>
								<button class="btn btn-primary DataBtns" id="BtnCancel" style="float: right; margin-left: 15px; margin-right: 35px;">Cancel</button>
								<button class="btn btn-primary DataBtns" id="BtnSave" style="float: right; margin-left: 15px;">Save</button>								
							</div>
						</div>							
						</br>
						
						<table id="tblLoginGroupMappingList"  
							class="table table-striped table-bordered table-hover table-responsive">
							<thead>
								<tr>
									<th>Login Group Name</th>
									<th>In Approver Group</th>									
									<th>Assigned Roles</th>
									<th>Assigned Projects</th>
									<th>Modify</th>
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
<script src="./assets/global/plugins/jquery.min.js"
	type="text/javascript"> </script>
<script
	src="./assets/global/plugins/jquery.dataTables.min.js"
	type="text/javascript"></script>

<head>
<link rel="stylesheet" href="./assets/jwf/content/JwfSpaInfra.css">
<script src="./assets/jwf/scripts/JwfSpaInfra.js"></script>
<script src="./assets/jwf/scripts/JwfSpaInfra.js"></script>
<script src="./assets/global/scripts/multiselect.js"></script>
</head>

<script>
	var ManageLoginGroupMapping = {
		DataSet: {},
		DataToSave: { GroupRowId: "-1", Roles: [], Projects: [], RoleRowIds: "", ProjectRowIds: "" },
		ViewTableRow: {},
		DomTmpl: { 
			RoleCheckBox: "<input type='checkbox' id='role-{1}' {3}><label for='role-{1}'>&nbsp;&nbsp;{2}</label></br>",
			ProjectCheckBox: "<input type='checkbox' id='project-{1}' {3}><label for='project-{1}'>&nbsp;&nbsp;{2}</label></br>"
		},
		setup: function() {		
			$('#BtnAddNew').on('click', function(oEvent) { ManageLoginGroupMapping.saveLoginGroupData('New'); });
			$('#BtnSave').on('click', function(oEvent) { ManageLoginGroupMapping.saveLoginGroupData('Save'); });
			$('#BtnCancel').on('click', function(oEvent) { ManageLoginGroupMapping.saveLoginGroupData('Cancel'); });
		},
		loadLoginGroupMappingList:  function() {
			var sViewTable = '#tblLoginGroupMappingList';
		
			JwfAjaxWrapper({
				WaitMsg: 'Loading login group mapping data',
				Url: 'loadLoginGroupMappingList',
				Headers:$("#token").val(),
				Data: {},
				CallBackFunction: function(oResponse) {
					
					ManageLoginGroupMapping.DataSet = oResponse.DataSet;					
					ManageLoginGroupMapping.resetRolesProjectsSelection();
					
					PageCtrl.debug.log('loadLoginGroupMappingList ajax callback', ManageLoginGroupMapping.DataSet);										

					if (ManageLoginGroupMapping.DataSet.SecurityMatrix.length > 0) {
						ManageLoginGroupMapping.fillGroupDataForm(ManageLoginGroupMapping.DataSet.SecurityMatrix[0].GroupRowId);
					}						
				
					ManageLoginGroupMapping.AccessesDataTable = $(sViewTable).on('init.dt', dataTableIniateDone).DataTable ({
						"data" : ManageLoginGroupMapping.DataSet.SecurityMatrix,
						"columns" : [
							{ "data" : "GroupName" },
							{ "data" : "IsApproverStr" },
							{ "data" : "RoleNames" },
							{ "data" : "ProjectNames" },							
							{ "data" : "GroupRowId-edit" },
							{ "data" : "GroupRowId-delete" }
						],
						order: [[ 0, "asc" ]],
						destroy: true,
						orderClasses: false,
						drawCallback: function( oSettings ) {
							$(sViewTable + ' a').off('click', ManageLoginGroupMapping.ankerRolesProjectsClicked).on('click', ManageLoginGroupMapping.ankerRolesProjectsClicked);
						}
					});

					/* Programmatically generate click event i.e. as if user has clicked first row for editing */
					function dataTableIniateDone() {
						var oPageLoadEvent = {};

						try {				

							$(sViewTable + ' tr').each(function (nRow, oRow) {				
								if (nRow === 1) {				
									oPageLoadEvent.target = $(oRow).find('i')[0];
									ManageLoginGroupMapping.ankerRolesProjectsClicked(oPageLoadEvent);
									PageCtrl.debug.log('programatic click', oPageLoadEvent);
								}
							});					

						} catch (oError) { 
							PageCtrl.debug.log('Error occured in data table event', oError.message);
						}					
					}					
				}
			});
		},
		resetRolesProjectsSelection: function() {					
			ManageLoginGroupMapping.DataSet.SecurityMatrix.forEach( function(oDataRow, nIndex) { 
				if (oDataRow.RoleRowIds === null) { oDataRow.RoleRowIds = ''; }
				if (oDataRow.ProjectRowIds === null) { oDataRow.ProjectRowIds = ''; }
			});				

			ManageLoginGroupMapping.DataSet.AllRoles.forEach( function(oDataRow, nIndex) { oDataRow.Selected = false; });					
			ManageLoginGroupMapping.DataSet.AllProjects.forEach( function(oDataRow, nIndex) { oDataRow.Selected = false; });		
		},
		saveLoginGroupData: function(sDataAction) {
			var oDataRecord = {}, sGroupName = $('#LoginGroupName').val().trim(), sInvalidDataMsg = '', sDeleteConfirm;
			
			ManageLoginGroupMapping.DataToSave.GroupName = sGroupName;			
			oDataRecord = ManageLoginGroupMapping.DataToSave;
			
			sDeleteConfirm = "Confirm Delete Group', 'Are sure to delete group '{0}'?".replace('{0}', sGroupName);
			sInvalidDataMsg = isDataValid(oDataRecord);	
			
			if(!checkInputText()){ 
				sInvalidDataMsg = 'Vulnerability in submitted data, not allowed to submit!';
			}
			
			switch(sDataAction) {
				case 'New':
					ManageLoginGroupMapping.fillGroupDataForm('-1');
					break;

				case 'Save':
					if (sInvalidDataMsg.length < 1) {
						ManageLoginGroupMapping.submitLoginGroupData();
					} else {
						Modal.confirmDialog(0, 'Invalid Data',	sInvalidDataMsg, ['Ok'] );	
					}						
					break;

				case 'Delete':
					PageCtrl.debug.log('saveLoginGroupData Delete Action', ManageLoginGroupMapping.DataToSave);
					Modal.confirmDialog(2, 'Confirm Delete', sDeleteConfirm,	['Yes', 'No'], { onDialogClose: cbDeleteGroup } );					
					break;					

				case 'Cancel':
					//ManageLoginGroupMapping.fillGroupDataForm(ManageLoginGroupMapping.DataSet.SecurityMatrix[0].GroupRowId);								
					ManageLoginGroupMapping.loadLoginGroupMappingList();
					break;
			}
			
			function isDataValid(oDataToSave) {
				var sGroupName = '', aSelectedRoles = oDataToSave.Roles, aSelectedProjects = oDataToSave.Projects, sRetValue = '', sMsgPrefix = 'Cannot submit data to save. ';							
			
				if ( (aSelectedRoles.length < 1) || (aSelectedProjects.length < 1) ) {
					sRetValue = sMsgPrefix + ' At least assign one Role & Project before saving.'
					
				} else if (parseInt(oDataToSave.GroupRowId) < 0) {				
					sGroupName = oDataToSave.GroupName.trim();
					
					if (sGroupName.length < 1) {
						sRetValue =  sMsgPrefix + ' New group name to be added cannot be empty.';						
						
					/* Do not allow user entry of our special value -2 as it is used for delete action in controller save logic flow */									
					} else if (sGroupName === '-2') {
						sRetValue =  sMsgPrefix + ' New group name to be added have invalid value.';						
					}					
				}
				
				return sRetValue;
			}
			
			function cbDeleteGroup(oEvent) {
				var sEventId = oEvent.EventId, sBtnSelected = oEvent.ClickedButton;
				if (sBtnSelected === 'Yes') { 
					ManageLoginGroupMapping.DataToSave.GroupName = "-2";
					ManageLoginGroupMapping.submitLoginGroupData(); 
				} else {
					$(ManageLoginGroupMapping.ViewTableRow).addClass('RowInEditing'); 
				}	
			}
			
		},
		submitLoginGroupData: function() {
			var oDataToSave = JSON.parse(JSON.stringify(ManageLoginGroupMapping.DataToSave)), oSubmitData = { sDataToSave: '' };
			
			oDataToSave.Roles = oDataToSave.Roles.join();
			oDataToSave.Projects = oDataToSave.Projects.join();
			
			oSubmitData.sDataToSave = JSON.stringify(oDataToSave);

			JwfAjaxWrapper({
				WaitMsg: 'Saving login group mapping data',
				Url: 'saveLoginGroupData',
				Headers:$("#token").val(),
				Data: oSubmitData,
				CallBackFunction: function(oResponse) {
					
					if (oResponse.Status) {
						ManageLoginGroupMapping.loadLoginGroupMappingList();
						toastr.info(oResponse.Msg);
					} else {
						Modal.confirmDialog(1, 'Save Failed',	oResponse.Msg, ['Ok'] );
					}
				}
			});			
			
		},
		resetRowHighlighting: function() {
			var sViewTable = '#tblLoginGroupMappingList';			
			$(sViewTable + " > tbody > tr").each(function () {
				$(this).removeClass('RowInEditing');				
			});
		},		
		ankerRolesProjectsClicked: function(oEvent) {
			var aRowIdParts = oEvent.target.parentElement.id.split('-'), sRowId = ''; 
			
			ManageLoginGroupMapping.ViewTableRow = $(oEvent.target).closest('tr');			
			ManageLoginGroupMapping.resetRowHighlighting();				
			
			if (aRowIdParts.length > 1) { 
				sRowId = aRowIdParts[1];
				ManageLoginGroupMapping.fillGroupDataForm(sRowId);
				
				if (aRowIdParts[0] === 'delete') { ManageLoginGroupMapping.saveLoginGroupData('Delete'); }
				if (aRowIdParts[0] === 'edit') { $(ManageLoginGroupMapping.ViewTableRow).addClass('RowInEditing'); }				
			}		
			
		},
		fillGroupDataForm: function(sRowId) {
			var oCheckBoxDiv = { RoleCheckBoxes: $('#AssginedRoles')[0], ProjectCheckBoxes: $('#AssginedProjects')[0] }, sCheckBoxesHtml = '', sCheckBoxTmpl = '';
			var nRecordIndex = UTIL.indexOfRecord(ManageLoginGroupMapping.DataSet.SecurityMatrix, 'GroupRowId', sRowId.toString()), oDataRecord = {};
			var lContinue = true, lNewGroup = false, aSelectedRoles = [], aSelectedProjects = [];			
			
			ManageLoginGroupMapping.resetRolesProjectsSelection();    // reset is unconditional, every first clear data and then set it
			
			/* Existing data record */
			if (nRecordIndex > -1) {
				oDataRecord = ManageLoginGroupMapping.DataSet.SecurityMatrix[nRecordIndex];
				$('#LoginGroupName').val(oDataRecord.GroupName);
				$('#LoginGroupName').prop('disabled',true);				
				$('#IsApproverInt').prop('checked', ((oDataRecord.IsApproverInt === '1') ? true : false));				
							
				aSelectedRoles = (oDataRecord.RoleRowIds.length < 1) ? [] : oDataRecord.RoleRowIds.replace(' ','').split(',');
				aSelectedProjects = (oDataRecord.ProjectRowIds.length < 1) ? [] : oDataRecord.ProjectRowIds.replace(' ','').split(',');				
				
				ManageLoginGroupMapping.DataSet.AllRoles.forEach( function(oDataRow, nIndex) { 
					oDataRow.Selected = (aSelectedRoles.indexOf(oDataRow.RoleRowId) > -1) ? true : false;
				});
				
				ManageLoginGroupMapping.DataSet.AllProjects.forEach( function(oDataRow, nIndex) { 
					oDataRow.Selected = (aSelectedProjects.indexOf(oDataRow.ProjectRowId) > -1) ? true : false;
				});
				
				ManageLoginGroupMapping.DataToSave = { GroupRowId: oDataRecord.GroupRowId, Roles: aSelectedRoles, Projects: aSelectedProjects, RoleRowIds: "", ProjectRowIds: "", IsApproverInt:oDataRecord.IsApproverInt };
			
			/* Add new data record */				
			} else if (sRowId === '-1') {
				$('#LoginGroupName').val('');
				$('#LoginGroupName').prop('disabled',false);
				$('#IsApproverInt').prop('checked', false);
				lNewGroup = true;
				ManageLoginGroupMapping.DataToSave = { GroupRowId: "-1", Roles: [], Projects: [], RoleRowIds: "", ProjectRowIds: "", IsApproverInt:'0'};
			
			/* Exception scenario - should not occur very rare case */	
			} else {				
			
				Modal.confirmDialog(1, 'Error',	'No data found for clicked row', ['Ok'] );
				lContinue = false;
			}
			
			PageCtrl.debug.log('fillGroupDataForm 01', 'sRowId = ' + sRowId + ' [' + aSelectedRoles + '],[' + aSelectedProjects + ']');
			
			if (lContinue) {
				ManageLoginGroupMapping.DataSet.AllRoles.forEach( function(oDataRow, nIndex) {
					sCheckBoxTmpl = ManageLoginGroupMapping.DomTmpl.RoleCheckBox;

					sCheckBoxTmpl = UTIL.replaceAll(sCheckBoxTmpl, '{1}', oDataRow.RoleRowId);
					sCheckBoxTmpl = sCheckBoxTmpl.replace('{2}', oDataRow.RoleName);
					sCheckBoxTmpl = sCheckBoxTmpl.replace('{3}', ( (oDataRow.Selected) ? 'checked' : '') );				

					sCheckBoxesHtml = sCheckBoxesHtml + sCheckBoxTmpl;
				});								
				oCheckBoxDiv.RoleCheckBoxes.innerHTML = sCheckBoxesHtml;

				sCheckBoxesHtml = '';
				ManageLoginGroupMapping.DataSet.AllProjects.forEach( function(oDataRow, nIndex) {
					sCheckBoxTmpl = ManageLoginGroupMapping.DomTmpl.ProjectCheckBox;				

					sCheckBoxTmpl = UTIL.replaceAll(sCheckBoxTmpl, '{1}', oDataRow.ProjectRowId);
					sCheckBoxTmpl = sCheckBoxTmpl.replace('{2}', oDataRow.ProjectName);
					sCheckBoxTmpl = sCheckBoxTmpl.replace('{3}', ( (oDataRow.Selected) ? 'checked' : '') );				

					sCheckBoxesHtml = sCheckBoxesHtml + sCheckBoxTmpl;
				});			
				oCheckBoxDiv.ProjectCheckBoxes.innerHTML = sCheckBoxesHtml;

				$('#data-form-parent input[type="checkbox"]').on('click', function(oEvent) {
					ManageLoginGroupMapping.updateDataToSave(oEvent.target);
				});			
			}
		},
		updateDataToSave: function(oClickedCheckBox) {
			var aRowIdParts = oClickedCheckBox.id.split('-'), sRowId = '', lCheckBoxChecked = oClickedCheckBox.checked, 
				nIndexPos = -2, lIsApproverIntClicked = (oClickedCheckBox.id === 'IsApproverInt') ? true : false;
			
			if (lIsApproverIntClicked) {
				ManageLoginGroupMapping.DataToSave.IsApproverInt = (lCheckBoxChecked) ? '1' : '0';
				
			} else if (aRowIdParts.length < 2) { 
				Modal.confirmDialog(1, 'Error',	'No data found for clicked row', ['Ok'] );
				
			} else if (aRowIdParts[0] === 'role') {
				sRowId = aRowIdParts[1];
				nIndexPos = ManageLoginGroupMapping.DataToSave.Roles.indexOf(sRowId);
				
				/* Roles => if checked and row id already do not exists add to array */
				if ( (lCheckBoxChecked) && (nIndexPos < 0) ) { 
					ManageLoginGroupMapping.DataToSave.Roles.push(sRowId); 
					
				/* Roles => if not checked and row id exists then delete from array */					
				} else if ( (!lCheckBoxChecked) && (nIndexPos > -1) ) {
					ManageLoginGroupMapping.DataToSave.Roles.splice(nIndexPos,1); 
				}
				
				PageCtrl.debug.log('updateDataToSave Roles 01', ManageLoginGroupMapping.DataToSave.Roles + ',' + oClickedCheckBox.checked);
				
			} else if (aRowIdParts[0] === 'project') {
				sRowId = aRowIdParts[1];
				nIndexPos = ManageLoginGroupMapping.DataToSave.Projects.indexOf(sRowId);
				
				/* Roles => if checked and row id already do not exists add to array */
				if ( (lCheckBoxChecked) && (nIndexPos < 0) ) { 
					ManageLoginGroupMapping.DataToSave.Projects.push(sRowId); 
					
				/* Roles => if not checked and row id exists then delete from array */					
				} else if ( (!lCheckBoxChecked) && (nIndexPos > -1) ) {
					ManageLoginGroupMapping.DataToSave.Projects.splice(nIndexPos,1); 
				}
				
				PageCtrl.debug.log('updateDataToSave Projects 02', ManageLoginGroupMapping.DataToSave.Projects + ',' + oClickedCheckBox.checked);
			}
			
			PageCtrl.debug.log('updateDataToSave Roles 03', ManageLoginGroupMapping.DataToSave);
		}
	};	
		
	$(document).ready( function() {
		initializeJwfSpaInfra(); // Load JWF framework module on this page
		ManageLoginGroupMapping.setup();
		ManageLoginGroupMapping.loadLoginGroupMappingList();	
	});
</script>