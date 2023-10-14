<%@page import="com.databuck.service.RBACController"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<style>
	#btnAddNewDimension {
		margin-bottom:15px;;
	}
	#tblDimensionViewList {
		background: #f2f2f2;
		border-bottom: 2px solid #808080;
	}
	#form-body-Dimension-entry {
		padding: 15px;
	}
	#form-body-Dimension-entry label {
		height: 32px;
		font-size: 13.5px !important;
		margin-top: 10px !important;
		margin-bottom: -5px !important;
	}

	#form-body-Dimension-entry input[type="text"] {
		width: 60% !important;
		height: 32px;
		margin: auto;		
		font-size: 13.5px !important;
		font-family: inherit;		
	}
	
	#checkboxGlobalDimensionId {
		margin-top:20px;
	}
	#form-body-Dimension-entry textarea {
		width: 100%; 
		min-height: 125px;
		margin-top: 10px;
		padding: 5px;
		background: #f2f2f2;
		border: 2px solid #e6e6e6;
		font-size: 12.5px;
		pointer-events: none;
		font-family: "Lucida Console", Courier, monospace;
	}
	
	#form-body-Dimension-entry textarea[data-msgstate="hint"] {
		color: black;
	}

	#form-body-Dimension-entry textarea[data-msgstate="error"] {
		color: brown;
		border: 2px solid brown;
	}
	
	#checkboxGlobalDimensionId {
		width: 18px;	
		height: 18px; 
		margin-left:auto; 
		margin-right:auto;		
		margin-top: 16px; 
		margin-bottom: 5px;
	}
</style>
<div class="page-content-wrapper">
	<div class="page-content">
		<div class="row">
			<div class="col-md-12">
				<div class="portlet light bordered">
					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold "> Dimension List </span>
						</div>
					</div>
					<div class="portlet-body">
						<button class="btn btn-primary" id="btnAddNewDimension">Add New Dimension</button>
						<table id="tblDimensionViewList" class="table table-striped table-bordered table-hover" width="100%">
							<thead>
								<tr>
									
									 <th>Dimension ID</th>
									 <th>Dimension Name</th>
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
	
	<div>
	<div id='form-body-parent' style='display: none; min-width: 550px;'>
		<fieldset id='form-body-Dimension-entry' class="fieldset-row">		
			
			
			<label for="nameOfNewDimension">Dimension Name</label>			
			<input type="text" id="nameOfNewDimension" >
			
		</fieldset>
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
var ManageDimensionDataEntry = {
		Dataset : {},
		DialogBody: null,
		DimensionRecordToSave: {},
		DimensionRecordValidToSave: false,
		
		setup: function() {
			
			ManageDimensionDataEntry.DialogBody = $('#form-body-Dimension-entry')[0];
			$('#btnAddNewDimension').on("click",function() { ManageDimensionDataEntry.promoteAddNewDimensionDialog('-1'); });
			$('#nameOfNewDimension').on('change', ManageDimensionDataEntry.onChangeData);
			},
		
		onChangeData: function(oEvent) {
			var sFieldName = oEvent.target.id, sFieldValue = $('#' + sFieldName).val().trim(), lValidData = true, sErrorMsg = '', sUserField = '';
			var sDimensionName = $('#nameOfNewDimension').val().trim()
			var lIsMainDataBlank = (sDimensionName < 1) ? true : false;
			
			
				lValidData = /^[a-zA-Z0-9_]+$/.test(sFieldValue);
				sErrorMsg = (lValidData) ? '' : "{field}: Only alphanumeric and underscore characters allowed\nExamples: 'emp_id' Or 'employee_id' Or 'empid' Or 'empno'";
				sUserField = 'Dimension Name';
			
			
			if (sErrorMsg.length > 0) {				
				sErrorMsg = sErrorMsg.replace('{field}', sUserField);
				ManageDimensionDataEntry.DimensionRecordValidToSave = false;
			} else {
				ManageDimensionDataEntry.DimensionRecordValidToSave = true;
			}
			
			if (lIsMainDataBlank) { ManageDimensionDataEntry.DimensionRecordValidToSave = false; }
		},
			
		loadDimensionList: function(){
			JwfAjaxWrapper({
				WaitMsg: 'Loading Dimension Records',
				Url: 'loadDimensionRecordList',
				Headers:$("#token").val(),
				Data: {},
				CallBackFunction: function(oResponse) {
					ManageDimensionDataEntry.DataSet = oResponse.DataSet;
					
					ManageDimensionDataEntry.DataTable = $("#tblDimensionViewList").DataTable ({
						"data" : ManageDimensionDataEntry.DataSet,
						"columns" : [
							
							{ "data" : "DimensionId" },
							{ "data" : "DimensionName" },
							{ "data" : "DimensionId-edit" },
							{ "data" : "DimensionId-delete" }
							
						],
						order: [[ 0, "asc" ]],
						destroy: true,
						drawCallback: function( oSettings ) {
							$('#tblDimensionViewList a').off('click', ManageDimensionDataEntry.AnkerIconClicked).on('click', ManageDimensionDataEntry.AnkerIconClicked);
						}					 
					});	
				}
				
			});
		},
		
		AnkerIconClicked:  function(oEvent) {
			var lFoundId = false, sClickedAnkerId = oEvent.target.parentElement.id, sRowId = '-1';			
			
			if (sClickedAnkerId.indexOf('delete') > -1) {
				sRowId = sClickedAnkerId.split('-')[1];
				ManageDimensionDataEntry.promoteDeleteDimensionDialog(sRowId);
				
			} else {
				if ( sClickedAnkerId.split('-').length	> 1 ) { sRowId = sClickedAnkerId.split('-')[1]; }
				ManageDimensionDataEntry.promoteAddNewDimensionDialog(sRowId);
			} 		
		},
		
		deleteDimension: function(sRowId) {
			PageCtrl.debug.log('Delete', 'Test'+sRowId);
			JwfAjaxWrapper({
				WaitMsg: 'Deleting Dimension Record...',
				Url: 'deleteDimensionRecord',
				Headers:$("#token").val(),
				Data: { DimensionIdToDelete: sRowId },
				CallBackFunction: function(oResponse) {				
					if (oResponse.Result) {
						ManageDimensionDataEntry.loadDimensionList();
					} else {					
						Modal.confirmDialog(3, 'Delete Failed', oResponse.Msg, ['Ok'] );
					}
				}
			});
		},
		
		promoteDeleteDimensionDialog: function(sRowId){
			var sDeleteMsg = 'Are you sure?';
			Modal.confirmDialog(2, 'Delete Dimension',	sDeleteMsg, ['Yes', 'No'], { onDialogClose: cbPromoteDeleteDimensionDialog, onDialogLoad: cbPromoteDeleteDimensionDialog });
			return;
			
			function	cbPromoteDeleteDimensionDialog(oEvent) {
				var sEventId = oEvent.EventId, sBtnSelected = oEvent.ClickedButton;

				switch(sEventId) {
					case jwfGlobal.EVT_MODAL_DIALOG_ONLOAD:
						PageCtrl.debug.log('on Dialog load','ok');
						break;

					case jwfGlobal.EVT_MODAL_DIALOG_ONCLOSE:
						if (sBtnSelected === 'Yes') { ManageDimensionDataEntry.deleteDimension(sRowId); }
						break;
				}
			}
		},
		
		fillDimensionForm: function(lNewDimension, nRowIndex) {
			var oDimensionRecordToSave = (lNewDimension)? {"DimensionName":""} : JSON.parse(JSON.stringify(ManageDimensionDataEntry.DataSet[nRowIndex]));
			var sDefaultMsg = 'Enter or modify Dimension details';
			
			ManageDimensionDataEntry.DimensionRecord = oDimensionRecordToSave
			$('#nameOfNewDimension').val(oDimensionRecordToSave.DimensionName);
			
			$('#nameOfNewDimension').prop("enabled", ( (lNewDimension) ? false : true ) );
			
			if(lNewDimension) {
				ManageDimensionDataEntry.DimensionRecordValidToSave = false;
			}
			else {
				ManageDimensionDataEntry.DimensionRecordValidToSave = true;
			}
		},
		
		saveDimensionData: function(sRowId) {
			var oDimensionRecordToSave = ManageDimensionDataEntry.DimensionRecordToSave;
			
			oDimensionRecordToSave.DimensionId = sRowId;
			oDimensionRecordToSave.DimensionName = $('#nameOfNewDimension').val();
			PageCtrl.debug.log('Is record valid to save', ManageDimensionDataEntry.DimensionRecordToSave);
			var sValidationMsg = checkInputText() ? true : false;
			
			if(sValidationMsg){
				ManageDimensionDataEntry.submitDimensionData(oDimensionRecordToSave);
			} else {
				alert('Vulnerability in submitted data, not allowed to submit!');
				/* Modal.confirmDialog(4, 'Invalid data', 'Kindly click save button after validation text box shows all data as valid. Your changes to Dimension are NOT saved.', ['Ok'] ); */
			}
			
		},
		
		submitDimensionData: function(oDimensionRecordToSave) {
			JwfAjaxWrapper({
				WaitMsg: 'Saving Dimension Records...',
				Url: 'saveDimensionRecord',
				Headers:$("#token").val(),
				Data: { DimensionRecordToSave: JSON.stringify(oDimensionRecordToSave) },
				CallBackFunction: function(oResponse) {				
					if (oResponse.Result) {
						//ManageDimensionDataEntry.loadDimensionList();
						//Mamta 18-May-2022 DC-433
						setTimeout(
								function() {
									window.location.reload();
								}, 500);
					} else {	
						Modal.confirmDialog(3, 'Save Failed', 'Please Enter Valid Dimension Name', ['Ok']);
						setTimeout(
								function() {
									window.location.reload();
								}, 1000);
						
						
					}
				}
			});
		},
		
		promoteAddNewDimensionDialog: function(sRowId) {
			var lNewDimension = (sRowId === '-1') ? true : false;
			var aDialogContext = (lNewDimension) ? 'Add New Dimension' : 'Edit Dimension';
			var aDialogButtons = ['Submit', 'Cancel'];
			$('#DimensionId').val();
			
			var nRowIndex = (lNewDimension) ? -1 : UTIL.indexOfRecord(ManageDimensionDataEntry.DataSet, 'DimensionId', sRowId);
			ManageDimensionDataEntry.fillDimensionForm(lNewDimension, nRowIndex);
			
			Modal.customDialog(1, aDialogContext,
					{ 'BodyDomObj': ManageDimensionDataEntry.DialogBody,'HeaderHtml': null },
					aDialogButtons, { onDialogClose: cbPromoteAddDimensionDialog, onDialogLoad: cbPromoteAddDimensionDialog });
			return;
			
			function	cbPromoteAddDimensionDialog(oEvent) {
				var sEventId = oEvent.EventId, sBtnSelected = oEvent.ClickedButton;

				switch(sEventId) {
					case jwfGlobal.EVT_MODAL_DIALOG_ONLOAD:
						PageCtrl.debug.log('on Dialog load','ok');
						break;

					case jwfGlobal.EVT_MODAL_DIALOG_ONCLOSE:
						if (sBtnSelected === 'Submit') { ManageDimensionDataEntry.saveDimensionData(sRowId); }
						break;
				}
			}	
		}
		
}
$(document).ready(function () {
	initializeJwfSpaInfra();
	ManageDimensionDataEntry.setup();
	ManageDimensionDataEntry.loadDimensionList();
});
</script>