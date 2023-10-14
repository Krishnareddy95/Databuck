<%@page import="com.databuck.service.RBACController"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<style>
	
	#DataButtons {
		text-align: center;
	}
	.DataBtns {
		text-align: center !important;
		min-width: 150px;
	}
	
	.LongTextBox {
		width: 550px; 
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
							<span class="caption-subject bold">External API Alert</span>
						</div>						
					</div>
					<div class="portlet-body">
						<div id='ExternalApiAlertMainParent' style="border: 2px solid #d9d9d9; padding: 15px;">
							<table id="data-form-parent" width="100%">
								<tr width="100%">
									<td width="50%" valign="top">
										<label for='AlertMsg'>Alert Message</label></br>
										<input type="text" id="AlertMsg" class='LongTextBox'></br></br>
									</td>
									<td width="50%" valign="top">
										<label for='AlertMsgCode'>Alert Message Code</label></br>
										<input type="text" id="AlertMsgCode" class='LongTextBox'></br></br>
									</td>
								</tr>
								<tr>
							</table>
							</br></br>
							<div id="DataButtons">
								<button class="btn btn-primary DataBtns" id="BtnSave" style="margin-left: 15px;">Save</button>															
								<button class="btn btn-primary DataBtns" id="BtnCancel" style="margin-left: 15px;">Reset</button>							
							</div>							
						</div>							
						</br>
						<table id="tblExternalApiAlertViewList" class="table table-striped table-bordered table-hover" width="100%">
							<thead>
								<tr>
									 <th>Parent Topic</th>
									 <th>Sub Topic</th>									 
									 <th>Focus Object</th>
									 <th>Alert Message</th>
									 <th>Alert Message Code</th>
									 <th>Alert Label</th>								 
									 <th>Edit</th>		 
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
var ManageExternalApiAlertDataEntry = {
		Dataset: {},
		ExternalApiAlertDataToSave: {},
		ExternalApiAlertDataToReset: {},
		
		setup: function() {
			
			$('#BtnCancel').on("click", ManageExternalApiAlertDataEntry.mainExternalApiAlertHandler);
			$('#BtnSave').on("click", ManageExternalApiAlertDataEntry.mainExternalApiAlertHandler);
		},
		loadExternalApiAlertDataTable: function() {
			var sViewTable = '#tblExternalApiAlertViewList';
			JwfAjaxWrapper({
				WaitMsg: 'Loading External Api Alert Records',
				Url: 'loadExternalApiAlertDataTable',
				Headers:$("#token").val(),
				Data: {},
				CallBackFunction: function(oResponse) {
					ManageExternalApiAlertDataEntry.DataSet = oResponse.DataSet;
					
					ManageExternalApiAlertDataEntry.DataTable = $(sViewTable).DataTable ({
						"data": ManageExternalApiAlertDataEntry.DataSet,
						"columns" : [
							{ "data" : "ParentTopicTitle" },
							{ "data" : "SubTopicTitle" },
							{ "data" : "FocusType" },							
							{ "data" : "AlertMsg" },							
							{ "data" : "AlertMsgCode" },
							{ "data" : "AlertLabel" },													
							{ "data" : "SubTopicRowId-edit" }							
						],
						order: [[ 0, "asc" ]],
						orderClasses: false,						
						destroy: true,
						drawCallback: function( oSettings ) {
							$('#tblExternalApiAlertViewList a').off('click', ManageExternalApiAlertDataEntry.mainExternalApiAlertHandler).on('click', ManageExternalApiAlertDataEntry.mainExternalApiAlertHandler);
							$("#tblExternalApiAlertViewList input[type='checkbox']").on("click",ManageExternalApiAlertDataEntry.mainExternalApiAlertHandler);
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
							
							ManageExternalApiAlertDataEntry.mainExternalApiAlertHandler(oPageLoadEvent);
						}
					});					
				
				} catch (oError) { 
					PageCtrl.debug.log('Error occured in data table event', oError.message);
				}
			});	
		},
		
		resetRowHighlighting: function() {
			$("#tblExternalApiAlertViewList > tbody > tr").each(function () {
				$(this).removeClass('RowInEditing');				
			});
		},
		
		/* All UI actions are centrally landed here and depending on UI action context, routed into client side flow or server side flow (or call) */
		mainExternalApiAlertHandler: function(oEvent) {
			var oTarget = oEvent.target, sTagName = oTarget.tagName.toLowerCase(), oEffectiveTarget = ((sTagName === 'button') || (sTagName === 'input')) ? oTarget : oTarget.parentElement;
			var sTargetId = oEffectiveTarget.id, aTargetIdParts = sTargetId.split('-'), sTargetIdLeft = aTargetIdParts[0], sTargetIdRight = (aTargetIdParts.length > 0) ? aTargetIdParts[1] : '';
			
			var oContextTypes = { "BtnSave": "server-flow", "edit": "client-flow", "BtnCancel": "client-flow" }; 
			var sContextType = oContextTypes[sTargetIdLeft], nClickedDataIndex = 0, oExternalApiAlertData = {};
			
			/* If click is on control within table row then get data linked to this table row. In this case sTargetIdRight = VersionRowId */
			if (sTagName !== 'button') {
				nClickedDataIndex = UTIL.indexOfRecord(ManageExternalApiAlertDataEntry.DataSet, 'SubTopicRowId',sTargetIdRight); 
				oExternalApiAlertData = (nClickedDataIndex > -1) ? ManageExternalApiAlertDataEntry.DataSet[nClickedDataIndex] : {};
			}
			
			if (sContextType === 'client-flow') { 
				ManageExternalApiAlertDataEntry.clientExternalApiAlertFlow(oEffectiveTarget, sTargetId, sTargetIdLeft, oExternalApiAlertData); 
			} else {
				ManageExternalApiAlertDataEntry.serverExternalApiAlertFlow(oEffectiveTarget, sTargetId, sTargetIdLeft, oExternalApiAlertData); 
			}			
		},
		
		clientExternalApiAlertFlow(oEffectiveTarget, sTargetId, sTargetIdLeft, oExternalApiAlertData){
			var oTableRow = $(oEffectiveTarget).closest('tr');
			
			if (sTargetIdLeft === 'edit') {
				ManageExternalApiAlertDataEntry.resetRowHighlighting();
				$(oTableRow).addClass('RowInEditing');			
				ManageExternalApiAlertDataEntry.fillNotificationFullFormData(oExternalApiAlertData);
				
			} else if (sTargetIdLeft === 'BtnCancel') {
				ManageExternalApiAlertDataEntry.fillNotificationFullFormData(ManageExternalApiAlertDataEntry.ExternalApiAlertDataToReset);
			}		
		},
		
		fillNotificationFullFormData(oExternalApiAlertData) {				
			
			ManageExternalApiAlertDataEntry.ExternalApiAlertDataToSave = oExternalApiAlertData;
			ManageExternalApiAlertDataEntry.ExternalApiAlertDataToReset = JSON.parse(JSON.stringify(oExternalApiAlertData));		
		
			$('#AlertMsg').val(oExternalApiAlertData.AlertMsg);
			$('#AlertMsgCode').val(oExternalApiAlertData.AlertMsgCode);
			
			$('#ExternalApiAlertMainParent :input').off('change', ManageExternalApiAlertDataEntry.onDataChange).on('change', ManageExternalApiAlertDataEntry.onDataChange);
		},
		
		onDataChange(oEvent) {
			var oTarget = oEvent.target, sTargetId = oTarget.id, sInputType = oTarget.type.toLowerCase();
			var lIsClickedValidInput = ( typeof(sInputType) === 'string') ? true : false;

			sInputType = (lIsClickedValidInput) ? sInputType : 'dummy_do_no_exist';
			PageCtrl.debug.log('onDataChange sInputType val: ' + sInputType);
			
			switch (sInputType) {
				case 'dummy_do_no_exist':
					PageCtrl.debug.log('onDataChange', "Invalid 'dummy_do_no_exist' input detected");
					break;
				
				case 'text': case 'textarea': case 'password':
					ManageExternalApiAlertDataEntry.ExternalApiAlertDataToSave[sTargetId] = $('#' + sTargetId).val();
					break;
			}		
		},
		
		getExternalApiAlertFullFormData() {
			return ManageExternalApiAlertDataEntry.ExternalApiAlertDataToSave;
		},
		
		serverExternalApiAlertFlow(oEffectiveTarget, sTargetId, sTargetIdLeft, oExternalApiAlertData) {
			var oWaitMsg = { "Save": "Saving External Api Alert data changes" };
			var sWaitMsg = '', lReloadDataTable = true, sConfimDialog = null;		
			
			
			if (sTargetId === 'BtnSave') {
				if(!checkInputText()){ 
					Modal.confirmDialog(0, 'Invalid Data To Save',	'Vulnerability in submitted data, not allowed to submit!', ['Ok'] );
					return;
				}
				
				sWaitMsg = oWaitMsg.Save;			
				oExternalApiAlertData = { Context: 'saveFullExternalApiAlertData', Data: UTIL.extractSubObjectByFields(ManageExternalApiAlertDataEntry.getExternalApiAlertFullFormData(), 'SubTopicRowId,ParentTopicRowId,AlertMsg,FocusType,AlertMsgCode,AlertLabel,SubTopicTitle,ParentTopicTitle') };
			}
			
			if (sConfimDialog) {
				window.scrollTo(0,0);
				Modal.confirmDialog(0, 'Confirm Action', sConfimDialog, ['Yes', 'No'], { onDialogClose: cbConfirmAction } );
				
			} else {
				ManageExternalApiAlertDataEntry.invokeMainExternalApiAlertHandler(sWaitMsg, oExternalApiAlertData, lReloadDataTable);	
			}

			function cbConfirmAction(oEvent) {
				var sEventId = oEvent.EventId, sBtnSelected = oEvent.ClickedButton;					
				if (sBtnSelected === 'Yes') { 
					ManageExternalApiAlertDataEntry.invokeMainExternalApiAlertHandler(sWaitMsg, oExternalApiAlertData, lReloadDataTable); 
				}
			}		
		},
		
		invokeMainExternalApiAlertHandler(sWaitMsg, oExternalApiAlertData, lReloadDataTable) {
			JwfAjaxWrapper({
				WaitMsg: sWaitMsg,
				Url: 'mainExternalApiAlertHandler',
				Headers:$("#token").val(),
				Data: { ExternalApiAlertData: JSON.stringify(oExternalApiAlertData) },
				CallBackFunction: function(oResponse) {
					if (oResponse.Status) {
						toastr.info(oResponse.Msg);
						if (lReloadDataTable) { ManageExternalApiAlertDataEntry.loadExternalApiAlertDataTable(); }
					} else {					
						toastr.info(oResponse.Msg);
					}
				}
			});		
		}
}

$(document).ready(function () {
	initializeJwfSpaInfra();
	ManageExternalApiAlertDataEntry.setup();
	ManageExternalApiAlertDataEntry.loadExternalApiAlertDataTable();		
});
</script>