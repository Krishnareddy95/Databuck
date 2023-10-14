<%@page import="com.databuck.service.RBACController"%>
<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<style>
	#DomainMainParent textarea {
		width: 100%; 
		min-height: 125px;
		margin-top: 10px;
		padding: 5px;
		font-size: 12.5px;
		font-family: "Lucida Console", Courier, monospace;
	}
	#BtnNew {
		width: 220px !important; 	
		float: left; 
	}		

	#BtnSave, #BtnCancel {
		width: 150px !important; 	
		float: right; 
	}		
	
	#tblDataButtons tr td button {
		margin-right: 15px !important; 
		text-align: center !important;
		font-size: 11.5px !important;
		margin-top: 15px;
		margin-bottom: 15px !important;
	}
	.LongTextBox {
		width: 550px !important;
		height: 32px !important;
	}
	#DimensionId {
		width: 350px !important;
		height: 32px !important;
	}
	
	.RowInEditing {
		background-color: #3973ac !important; /* #0066cc */
		color: white;
		weight: bold;
	}
	
</style>
<div class="page-content-wrapper">
	<div class="page-content">
		<div class="row">
			<div class="col-md-12">
				<div class="portlet light bordered">
					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold">Defect Codes & Dimension</span>							
						</div>						
					</div>
					<div class="portlet-body">
						<div id='DomainMainParent' style="border: 2px solid #d9d9d9; padding: 15px;">
							<table id='tblDataFormContainer' width='100%'>
								<tr width='100%'>
									<td width='50%' valign='top'>
										<label for='DefectCode'>Defect Code</label></br>
										<input type='text' id='DefectCode' class='LongTextBox'>
										</br></br>
										
										<label for='DefectDescription'>Defect Code Description</label></br>
					        			<input type='text' id='DefectDescription' class='LongTextBox'>
					        			
									</td>
									<td width='50%' valign='top'>
										<label>Dimension</label></br>
										<select id = "DimensionId">
										</select>											
										</div>
									</td>
								</tr>
								<tr>
							</table>

							<table width=100% id='tblDataButtons'>
								<tr>
									<td width=25%><button class="btn btn-primary DataBtns"
											id="BtnNew">Add New Defect Code</button></td>
									<td width=75%>
										<button class="btn btn-primary DataBtns" id="BtnCancel">Cancel</button>
										<button class="btn btn-primary DataBtns" id="BtnSave">Save</button>
									</td>
								</tr>
							</table>
							
						</div>
						</br>
					
					<table id="tblDefectCodeViewList" class="table table-striped table-bordered table-hover" width="100%">
						<thead>
							<tr>
								 <th>Defect code</th>
								 <th>Description</th>
								 <th>Dimension</th>
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
var ManageDefectCodeDataEntry = {
	DefectCodeViewList: {},
	DimensionList: {},
	DatabaseDefectCodeRecord: {},
	EditingDefectCodeRecord: {},
	NewDefectCodeRecord: {},
	setup: function() {		
		$('#tblReportHeader tr td :input').change(ManageDefectCodeDataEntry.mainEventHandler);
		$('#tblDataButtons button').on('click', ManageDefectCodeDataEntry.mainEventHandler);			
	},
	loadDefectCodeViewList: function() {
		var sViewTable = '#tblDefectCodeViewList';

		JwfAjaxWrapper({
			WaitMsg: 'Loading Defect Code Records',
			Url: 'loadDefectCodeViewList',
			Headers:$("#token").val(),
			CallBackFunction: function(oResponse) {	
				ManageDefectCodeDataEntry.DefectCodeViewList = checkDefectDataVulnerability.filterVulnerableData(oResponse.DefectCodeViewList);
				ManageDefectCodeDataEntry.DimensionList = oResponse.DimensionList;
				PageCtrl.debug.log("ManageDefectCodeDataEntry.DefectCodeViewList", ManageDefectCodeDataEntry.DefectCodeViewList);

				UIManager.fillSelect($('#DimensionId')[0], ManageDefectCodeDataEntry.DimensionList, ManageDefectCodeDataEntry.DimensionList.row_id);
				$('#DimensionId').find('option:eq(0)').prop('selected', true);
				ManageDefectCodeDataEntry.DataTable = $(sViewTable).DataTable ({
					"data": ManageDefectCodeDataEntry.DefectCodeViewList,
					"columns" : [
						{ "data" : "DefectCode" },
						{ "data" : "DefectDescription" },
						{ "data" : "DimensionName" },
						{ "data" : "RowId-edit" },
						{ "data" : "RowId-delete" }							
					],
					order: [[ 0, "asc" ]],
					orderClasses: false,						
					destroy: true,
					drawCallback: function( oSettings ) {
						$(sViewTable + ' a').off('click', ManageDefectCodeDataEntry.mainEventHandler).on('click', ManageDefectCodeDataEntry.mainEventHandler);
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
						ManageDefectCodeDataEntry.mainEventHandler(oPageLoadEvent);
					}
				});					

			} catch (oError) { 
				PageCtrl.debug.log('Error occured in data table event', oError.message);
			}
		});
	},

	resetRowHighlighting: function() {
		$("#tblDefectCodeViewList > tbody > tr").each(function () {
			$(this).removeClass('RowInEditing');				
		});
	},

	/* All UI events (except keypress in filter text box) are centrally landed here and then  routed into client or server side flow logic */
	mainEventHandler: function(oEvent) {
		var oTarget = oEvent.target, sTagName = oTarget.tagName.toLowerCase(), sType = oTarget.type, oEffectiveTarget = (sTagName === 'i') ? oTarget.parentElement : oTarget;
		var sTargetId = oEffectiveTarget.id, sContextKey = '', sEventContext = ''; 
		var sFlowType = ( (sTargetId === 'BtnSave') || (sTargetId.indexOf('delete') > -1) ) ? 'server-flow' : 'client-flow'; 

		/* AppsShowOptionChanged, <AppsFilterChanged>, DataFormChanged, AppStateChanged, DataEdit, DataDelete, DataNew, DataSave, DataCancel */
		var oContextMatrix = { 
				"server-flow": { "BtnSave" : "DataSave", "delete" : "DataDelete" }, 
				"client-flow": { "BtnNew": "DataNew", "BtnCancel": "DataCancel", "edit": "DataEdit" }
			};			

		/* Get context key to retrieve event context */ 
		if (sTagName === 'i') {													// edit and delete icons
			sContextKey = sTargetId.split('-')[0];

		} else if ( 'text,select-one'.indexOf(sType) > -1)  {			// data form changed					
			sContextKey = 'DataFormChanged';

		} else if (sTagName === 'button') {									// data buttons save, new and cancel
			sContextKey = sTargetId;
		}				

		/* Get context key to determine event context */
		sEventContext = oContextMatrix[sFlowType][sContextKey]; 			
		//PageCtrl.debug.log('mainEventHandler 01 ' + sEventContext, 'sTagName = ' + sTagName + ', sType = ' + sType + ', sTargetId = ' + sTargetId + ', sFlowType = ' + sFlowType);			

		switch (sEventContext) {
			case 'AppStateChanged':
				ManageDefectCodeDataEntry.appCheckStateChanged(sTargetId.split('-')[1], $(oEffectiveTarget).prop('checked'), oEffectiveTarget);
				break;				

			case 'DataNew':
				ManageDefectCodeDataEntry.beginDataEditing(sEventContext, '-1', oEffectiveTarget);
				break;

			case 'DataEdit':
				ManageDefectCodeDataEntry.beginDataEditing(sEventContext, sTargetId.split('-')[1], oEffectiveTarget);
				break;

			case 'DataCancel':
				ManageDefectCodeDataEntry.beginDataEditing(sEventContext, ManageDefectCodeDataEntry.DatabaseDefectCodeRecord.RowId, null);
				break;

			case 'DataSave':
				ManageDefectCodeDataEntry.updateModelFromForm();
				ManageDefectCodeDataEntry.invokeMainDefectCodeHandler('Saving Defect Code Record', 'saveDefectCodeRecord', true);
				break;

			case 'DataDelete':
				ManageDefectCodeDataEntry.deleteDefectCodeRecord(sTargetId.split('-')[1]);
				break;

			default:
				PageCtrl.debug.log('mainEventHandler 02', 'sTagName = ' + sTagName + ', sType = ' + sType + ', sTargetId = ' + sTargetId + ', sFlowType = ' + sFlowType);			
		}		
	},

	appCheckStateChanged: function(sClickedAppId, lAppIdChecked, oCheckBox) {

	},

	beginDataEditing: function(sDataEditContext, sRowId, oEffectiveTarget) {
		var oFocusRecord = {}, oTableRow = null, lIsNewRecord = (sRowId === '-1' || typeof sRowId == "undefined" || sRowId == null) ? true : false;
		PageCtrl.debug.log("sRowId",sRowId );
		if(lIsNewRecord){
			var aFieldList = [ 'DefectCode', 'DefectDescription', 'DimensionId' ];

			aFieldList.forEach( function(sFieldName, nIndex) {
				ManageDefectCodeDataEntry.NewDefectCodeRecord[sFieldName] = $('#' + sFieldName).val();
				ManageDefectCodeDataEntry.NewDefectCodeRecord.RowId = '-1';
			});
		}

		if (sDataEditContext === 'DataNew') { 
			fillDefectData(oFocusRecord, lIsNewRecord);
			ManageDefectCodeDataEntry.DatabaseDefectCodeRecord = oFocusRecord;				
		}

		if (sDataEditContext === 'DataCancel') { 
			oFocusRecord = ManageDefectCodeDataEntry.DatabaseDefectCodeRecord;
			fillDefectData(oFocusRecord, lIsNewRecord);
		} else {
			ManageDefectCodeDataEntry.resetRowHighlighting();
		}

		if (sDataEditContext === 'DataEdit') {
			oFocusRecord = getDefectCodeRecordByRow(sRowId);
			fillDefectData(oFocusRecord, lIsNewRecord);
			oTableRow = $(oEffectiveTarget).closest('tr');
			$(oTableRow).addClass('RowInEditing');
		}			

		ManageDefectCodeDataEntry.DatabaseDefectCodeRecord = oFocusRecord;			

		// Applicable for all data actions new, edit and reset.  Make clone of JSON else all user editing will overwrite database copy. 
		ManageDefectCodeDataEntry.EditingDefectCodeRecord = JSON.parse( JSON.stringify(oFocusRecord) );

		$('#DefectCode').prop('disabled',!lIsNewRecord);
		function fillDefectData(oFocusRecord, lIsNewRecord) {
			$('#DefectCode').val(oFocusRecord.DefectCode);
			$('#DefectDescription').val(oFocusRecord.DefectDescription);
			if(lIsNewRecord){
				$('#DimensionId').find('option:eq(0)').prop('selected', true);
			} else {
			$('#DimensionId').val(oFocusRecord.DimensionId);
			}
		}

		function getDefectCodeRecordByRow(sRowId) {
			var nRecordIndex = -2, oRetValue = null; 

			if (sRowId === '-1') {
				oRetValue = JSON.parse( JSON.stringify(ManageDefectCodeDataEntry.NewDefectCodeRecord) );
			} else {
				nRecordIndex = UTIL.indexOfRecord(ManageDefectCodeDataEntry.DefectCodeViewList, 'RowId', sRowId);
				if (nRecordIndex > -1) { 
					oRetValue = JSON.parse( JSON.stringify(ManageDefectCodeDataEntry.DefectCodeViewList[nRecordIndex]) ); 
				}
			}
			return oRetValue;
		}

	},

	updateModelFromForm: function() {
		var aFieldList = [ 'DefectCode', 'DefectDescription', 'DimensionId' ];

		aFieldList.forEach( function(sFieldName, nIndex) {
			ManageDefectCodeDataEntry.EditingDefectCodeRecord[sFieldName] = $('#' + sFieldName).val();
		});	
		if(typeof ManageDefectCodeDataEntry.EditingDefectCodeRecord.RowId == "undefined" || ManageDefectCodeDataEntry.EditingDefectCodeRecord.RowId == null){
			ManageDefectCodeDataEntry.EditingDefectCodeRecord.RowId = '-1';
		}
		//PageCtrl.debug.log("ManageDefectCodeDataEntry.EditingDefectCodeRecord.RowId", ManageDefectCodeDataEntry.EditingDefectCodeRecord.RowId);
	},
	deleteDefectCodeRecord: function(sRowId) {
		var nRowIndex = UTIL.indexOfRecord( ManageDefectCodeDataEntry.DefectCodeViewList, 'RowId', sRowId);
		var sConfimDialog = "Are you sure to delete Defect Code '{0}'";

		if (nRowIndex > -1) {
			ManageDefectCodeDataEntry.EditingDefectCodeRecord = ManageDefectCodeDataEntry.DefectCodeViewList[nRowIndex];

			window.scrollTo(0,0);
			Modal.confirmDialog(
				0, 'Confirm Delete', sConfimDialog.replace('{0}', 
						ManageDefectCodeDataEntry.EditingDefectCodeRecord.DefectCode), 
				['Yes', 'No'], { onDialogClose: cbConfirmAction } 
			);	

		}

		function cbConfirmAction(oEvent) {
			var sEventId = oEvent.EventId, sBtnSelected = oEvent.ClickedButton;					
			if (sBtnSelected === 'Yes') { 
				ManageDefectCodeDataEntry.invokeMainDefectCodeHandler('Deleting Defect Code Record', 'deleteDefectCodeRecord', true);
				location.reload();
			}
		}			
	},

	invokeMainDefectCodeHandler: function(sWaitMsg, sContext, lReloadDataTable) {
		var oDefectCodeRecord = JSON.parse( JSON.stringify(ManageDefectCodeDataEntry.EditingDefectCodeRecord) ), oData = {}, sValidationMsg = '';			

		oDefectCodeRecord = UTIL.extractSubObjectByFields(oDefectCodeRecord, 'RowId,DefectCode,DefectDescription,DimensionId');
		sValidationMsg = isDataValidToSave(oDefectCodeRecord);
		PageCtrl.debug.log("oDefectCodeRecord", oDefectCodeRecord);
		if (sValidationMsg.length > 0) {
			Modal.confirmDialog(0, 'Invalid Data To Save',	sValidationMsg, ['Ok'] );

		} else {
			oData = { "Context": sContext, "Data": oDefectCodeRecord };			

			JwfAjaxWrapper({
				WaitMsg: sWaitMsg,
				Url: 'mainDefectCodeHandler',
				Headers:$("#token").val(),
				Data: { DefectCodeData: JSON.stringify(oData) },
				CallBackFunction: function(oResponse) {
					if (oResponse.Status) {
						toastr.info(oResponse.Msg);
						if (lReloadDataTable) { ManageDefectCodeDataEntry.loadDefectCodeViewList(); }
					} else {					
						toastr.info(oResponse.Msg);
					}
				}
			});
		}

		function isDataValidToSave(oDefectCodeRecord) {
			var sRetMsg = '';

			if (oDefectCodeRecord.DefectCode.trim().length < 1) {
				sRetMsg = 'Please enter Defect Code, it cannot be blank'
			} else {
				sRetMsg = checkDefectDataVulnerability.validateVulnerableData(oDefectCodeRecord);
			}

			return sRetMsg;
		}
	}		
}

checkDefectDataVulnerability = {
	filterVulnerableData: function(aDefectDataList) {
		var sRetMsg = '', nVulnerabilityCount = 0,  aSanitizedDataList = [], aFilteredRowIds = [];
		
		aDefectDataList.forEach( function(oDefectRecord, nIndex) {
			nVulnerabilityCount = checkVulnerability.getVulnerabilityTagCounts(oDefectRecord.DefectDescription + ' ' + oDefectRecord.DefectCode);
			
			if (nVulnerabilityCount > 0) {
				aFilteredRowIds.push(oDefectRecord.RowId);
			} else {
				aSanitizedDataList.push(oDefectRecord);
			}					
		});	
	
		if (aFilteredRowIds.length > 0) { 
			Modal.confirmDialog(2, 'Vulnerable Data Skipped',	
			"Following defect records '" + aFilteredRowIds.join() + "' contains Vulnerable Contents not displayed on application page for Cyber Safety. <br>To clean data from database contact FirstEigen Support.", 
			['Ok'] );
		}
	
		return aSanitizedDataList;
	},
	validateVulnerableData: function(oDefectRecordToSave) {
		var nVulnerabilityCount = checkVulnerability.getVulnerabilityTagCounts(oDefectRecordToSave.DefectDescription + ' ' + oDefectRecordToSave.DefectCode);	
		
		return (nVulnerabilityCount > 0) ? 'Entered defect data contains Vulnerable Contents, cannot save record' : '';
	}
}

$(document).ready(function () {
	initializeJwfSpaInfra();	
	ManageDefectCodeDataEntry.setup();		
	ManageDefectCodeDataEntry.loadDefectCodeViewList();
});
</script>