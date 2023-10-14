<%@page import="com.databuck.service.RBACController"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<style>
	#BtnBulkAssignRoles {
		margin-bottom: 15px;
		margin-top: 15px;
	}
	
	#tblRoleList {
		margin-top: 15px;
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
								style="float: left !important;">Features Access Control</span> 
						</div>
					</div>
					<div class="portlet-body ">
						<button class="btn btn-primary" id="BtnBulkAssignRoles">Bulk Assign Roles</button>
						<table id="tblMobuleComponentList"  
							class="table table-striped table-bordered table-hover table-responsive">
							<thead>
								<tr>
									<th><input type='checkbox' id='all_components' onchange='alert("ok all components");'>Select All</th>
									<th>Module Name</th>
									<th>Feature</th>
									<th>Roles</th>
									<th>Assign Roles</th>
								</tr>
							</thead>
						</table>
					</div>
				</div>
			</div>
		</div>		
	</div>
	
	<div style='display: none;'>
		<div id='div_role_selection' style='width: 100%;'>
			<table id="tblRoleList" class="table table-striped table-bordered table-hover" width="100%">
				<thead>
					<tr>				
						<th><input type='checkbox' id='all_roles' onchange='alert("ok all roles");'>Select All</th>
						<th>Role Name</th>
					</tr>
				</thead>
			</table>
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
</head>

<script>
	var ManageComponentAccessDataEntry = {
		AccessListData: {},
		RoleListData: {},
		Selected: { Components: [], SaveComponents: [], Roles: [] },
		DialogBody: null,
		RoleList: null,
		setup: function() {
			$('#BtnBulkAssignRoles').on('click', ManageComponentAccessDataEntry.assignRoleClicked);
		},
		loadComponentAccessControlViewList:  function() {
			JwfAjaxWrapper({
				WaitMsg: 'Loading Access control data',
				Url: 'loadComponentAccessControlViewList',
				Headers:$("#token").val(),
				Data: {},
				CallBackFunction: function(oResponse) {
					oResponse.AccessListData.forEach( function(oDataRow, nIndex) {	oDataRow.Selected = false; });
					oResponse.RoleListData.forEach( function(oDataRow, nIndex) { oDataRow.Selected = false; });
					
					ManageComponentAccessDataEntry.AccessListData = oResponse.AccessListData;
					ManageComponentAccessDataEntry.RoleListData = oResponse.RoleListData;				
					ManageComponentAccessDataEntry.Selected = { Components: [], Roles: [] };
					
					PageCtrl.debug.log('loadComponentAccessControlViewList ajax callback', oResponse.RoleListData);
				
					ManageComponentAccessDataEntry.AccessesDataTable = $("#tblMobuleComponentList").DataTable ({
						"data" : ManageComponentAccessDataEntry.AccessListData,
						"columns" : [
							{ "data" : "ComponentRowId-check" },
							{ "data" : "ModuleName" },
							{ "data" : "ComponentTitle" },
							{ "data" : "RoleNames" },
							{ "data" : "ComponentRowId-edit" }
						],
						order: [[ 1, "asc" ]],
						destroy: true,
						orderClasses: false,
						drawCallback: function( oSettings ) {
							$('#tblMobuleComponentList a').off('click', ManageComponentAccessDataEntry.assignRoleClicked).on('click', ManageComponentAccessDataEntry.assignRoleClicked);
							$('#tblMobuleComponentList input[type="checkbox"]').off('click', ManageComponentAccessDataEntry.selectComponentClicked).on('click', ManageComponentAccessDataEntry.selectComponentClicked);
						}
					});
				}
			});
		},
		selectComponentClicked: function(oEvent) {
			var lFoundId = false, nIndexOfClickedRow = -1, sRowId = oEvent.target.id.split('-')[1];
			var nIndexOfClickedRow = -1, aSelectedRowIds = [], aDataSet = ManageComponentAccessDataEntry.AccessListData;

			lFoundId = aDataSet.some( function(oDataRow, nIndex) {
				if (oDataRow.ComponentRowId.toString() === sRowId) {
					nIndexOfClickedRow = nIndex;
					return true;
				}
			});

			if (nIndexOfClickedRow > -1) {
				aDataSet[nIndexOfClickedRow].Selected = oEvent.target.checked;
				aDataSet.forEach( function(oDataRow, nIndex) { if (oDataRow.Selected) { aSelectedRowIds.push(oDataRow.ComponentRowId); }	});
				ManageComponentAccessDataEntry.Selected.Components = aSelectedRowIds;
			}			
			PageCtrl.debug.log('selectComponentClicked', ' Selected row ids are ' + ManageComponentAccessDataEntry.Selected.Components.join(','));
		},		
		assignRoleClicked: function(oEvent) {
			var sAssignMode = (oEvent.target.tagName.toUpperCase() === 'BUTTON') ? 'bulk' : 'single';
			var sClickedComponentRowId = '', aClickedComponentRowId = [];
			
			if (sAssignMode === 'single') {
				sClickedComponentRowId = oEvent.target.parentElement.id.split('-')[1];
				aClickedComponentRowId.push(sClickedComponentRowId);
				ManageComponentAccessDataEntry.Selected.SaveComponents = aClickedComponentRowId;
			} else {
				ManageComponentAccessDataEntry.Selected.SaveComponents = ManageComponentAccessDataEntry.Selected.Components;
			}		
		
			if ( ManageComponentAccessDataEntry.prepareRoleDialog(sAssignMode, oEvent.target) ) { ;
				ManageComponentAccessDataEntry.promoteRoleSelectionDialog();
			}				
		},
		/* Boolean function to sync up in memory data & UI elements, before dialog opens
				(a) roles as assigned to selected components => (b) ManageComponentAccessDataEntry.Selected.Roles => (c) preselect role check boxes controls
			Returns True = means all set to open role dialog.  false = user already shown message that dialog not ready to open and why?				
		*/
		prepareRoleDialog(sAssignMode, oClickedUIControl) {
			var sClickedComponentRowId = '', aComponentRowIdList = [], aComponentRecordList = ManageComponentAccessDataEntry.AccessListData, nIndexOfRecord = 0; 
			var aRoleRowIdList = [], aRoleRecordList = ManageComponentAccessDataEntry.RoleListData, oRoleIdData = {}, aEffectiveRoleRowIdList = [];
			var lComponentsSelected = (sAssignMode === 'bulk') ? ((ManageComponentAccessDataEntry.Selected.Components.length > 0) ? true : false) : true; 
			
			if (lComponentsSelected) {         																	 // Either one or more components selected or row anker icon clicked 
			
				/* Get effective selected components in aComponentRowIdList, so these are components to which roles to be assigned/saved, by assign role button click */
				if (sAssignMode === 'single') {
					sClickedComponentRowId = oClickedUIControl.parentElement.id.split('-')[1];
					aComponentRowIdList.push(sClickedComponentRowId);
					ManageComponentAccessDataEntry.Selected.SaveComponents = aComponentRowIdList;
				} else {
					ManageComponentAccessDataEntry.Selected.SaveComponents = ManageComponentAccessDataEntry.Selected.Components;
					aComponentRowIdList = ManageComponentAccessDataEntry.Selected.Components;
				}			

				/* Get Effective roles, which need to be pre checked in role dialog */
				aComponentRowIdList.forEach( function(sRowId, nIndex) {
					nIndexOfRecord = UTIL.indexOfRecord(aComponentRecordList,'ComponentRowId', sRowId);

					if (nIndexOfRecord > -1) {
						aRoleRowIdList = aComponentRecordList[nIndexOfRecord]['RoleRowIds'].split(',');
						if (aRoleRowIdList.length > 0) {
							aRoleRowIdList.forEach( function(sRoleRowId, nIndex) { oRoleIdData[sRoleRowId] = sRoleRowId; });
						}
					}
				});
				
				/* Mark selected as true or false so check box will be preselected for true roles */
				aEffectiveRoleRowIdList = Object.keys(oRoleIdData);
				aRoleRecordList.forEach( function(oRoleRecord, nIndex) { oRoleRecord.Selected = false; });
				
				aEffectiveRoleRowIdList.forEach( function(nRowId, nIndex) {
					nIndexOfRecord = UTIL.indexOfRecord(aRoleRecordList,'RoleRowId', nRowId);
					PageCtrl.debug.log('prepareRoleDialog 01', nRowId + ',' + nIndexOfRecord);
					
					if (nIndexOfRecord > -1) { aRoleRecordList[nIndexOfRecord].Selected = true; }					
				});
				
				ManageComponentAccessDataEntry.RoleListData = aRoleRecordList;
			} else {				
				Modal.confirmDialog(1, 'Bulk Assign',	'No component is selected, cannot do bulk assign unless one or more component(s) are selected', ['Ok'] );							
			}
			PageCtrl.debug.log('prepareRoleDialog 02', 'Role ids to be selected are ' + aEffectiveRoleRowIdList);
			return lComponentsSelected;			
		},		
		promoteRoleSelectionDialog: function(sAssignMode, oClickedUIControl) {		 	
		 	
		 	$("#tblRoleList").DataTable().clear();                                               // Reset all data table i.e. role chcek boxes states     		 	

			ManageComponentAccessDataEntry.RolesDataTable = $("#tblRoleList").DataTable ({
				"data" : ManageComponentAccessDataEntry.RoleListData,
				"columns" : [
					{ "data" : "RoleRowId-check" },
					{ "data" : "RoleName" }
				],
				order: [[ 1, "asc" ]],
				destroy: true,
				orderClasses: false,
				drawCallback: function( oSettings ) {
					$('#tblRoleList input[type="checkbox"]').off('click', ManageComponentAccessDataEntry.selectRoleClicked).on('click', ManageComponentAccessDataEntry.selectRoleClicked);					
				},
				createdRow: function ( oRow, aData, nIndex ) {
					var oCheckBox = $(oRow).find('input[type="checkbox"]')[0];					
					oCheckBox.checked = (ManageComponentAccessDataEntry.RoleListData[nIndex].Selected) ? true : false;
				}
			});
			ManageComponentAccessDataEntry.displayRoleSelectionDialog();
		},
		displayRoleSelectionDialog: function() {
			var oDialogBody = document.getElementById('div_role_selection');

			Modal.customDialog(0, 'Select Roles to Assign',
					{ 'BodyDomObj': oDialogBody,'HeaderHtml': null },
					['Assign Roles', 'Cancel'], { onDialogClose: cbRoleSelectionDialog, onDialogLoad: cbRoleSelectionDialog });

			return;

			function	cbRoleSelectionDialog(oEvent) {
				var sEventId = oEvent.EventId, sBtnSelected = oEvent.ClickedButton;

				switch(sEventId) {
					case jwfGlobal.EVT_MODAL_DIALOG_ONLOAD:
						break;
					case jwfGlobal.EVT_MODAL_DIALOG_ONCLOSE:
						if (sBtnSelected === 'Assign Roles') {
							ManageComponentAccessDataEntry.SaveSelectedAccessControl();
						}							
						break;
				}
			}
		},
		selectRoleClicked: function(oEvent) {
			var lFoundId = false, nIndexOfClickedRow = -1, sRowId = oEvent.target.id.split('-')[1];
			var nIndexOfClickedRow = -1, aSelectedRowIds = [], aDataSet = ManageComponentAccessDataEntry.RoleListData;

			lFoundId = aDataSet.some( function(oDataRow, nIndex) {
				if (oDataRow.RoleRowId.toString() === sRowId) {
					nIndexOfClickedRow = nIndex;
					return true;
				}
			});

			if (nIndexOfClickedRow > -1) {
				aDataSet[nIndexOfClickedRow].Selected = oEvent.target.checked;
				aDataSet.forEach( function(oDataRow, nIndex) { if (oDataRow.Selected) { aSelectedRowIds.push(oDataRow.RoleRowId); }	});
				ManageComponentAccessDataEntry.Selected.Roles = aSelectedRowIds;
			}			
			PageCtrl.debug.log('RoleSelectClicked', ' Selected row ids are ' + ManageComponentAccessDataEntry.Selected.Roles.join(','));
		},
		SaveSelectedAccessControl: function() {		
			var lContinue = ( (ManageComponentAccessDataEntry.Selected.SaveComponents.length > 0) && (ManageComponentAccessDataEntry.Selected.Roles.length > 0) ) ? true : false;
			var oData = { SelectedComponents: ManageComponentAccessDataEntry.Selected.SaveComponents.join(','), SelectedRoles: ManageComponentAccessDataEntry.Selected.Roles.join(',') };
			
			PageCtrl.debug.log('SaveSelectedAccessControl', lContinue + ' , ' + JSON.stringify(oData));
			
			if (lContinue) {
				JwfAjaxWrapper({
					WaitMsg: 'Saving Access control data',
					Url: 'saveSelectedAccessControl',
					Headers:$("#token").val(),
					Data: oData,
					CallBackFunction: function(oResponse) {
						ManageComponentAccessDataEntry.loadComponentAccessControlViewList();
						location.reload();
					}
				});
			} else {
				Modal.confirmDialog(1, 'Save Assign Roles',	'No Role(s) are selected, select one or more roles', ['Ok'] );
				 location.reload();
			}
		}
	};	
		
	$(document).ready( function() {
		initializeJwfSpaInfra(); // Load JWF framework module on this page
		ManageComponentAccessDataEntry.setup();
		ManageComponentAccessDataEntry.loadComponentAccessControlViewList();		
	});
</script>