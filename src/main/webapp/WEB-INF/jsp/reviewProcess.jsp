<%@page import="com.databuck.service.RBACController"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<style>
	#form-body-action-entry {
		padding: 15px;
	}
	#form-body-action-entry label {
		height: 32px;
		font-size: 13.5px !important;
		margin-top: 10px !important;
		margin-bottom: -5px !important;
	}

	#form-body-action-entry select {
		width: 100% !important;
		height: 32px;
		margin: auto;		
		font-size: 13.5px !important;
		font-family: inherit;		
	}
	
	#form-body-action-entry textarea {
		width: 100% !important; 
		min-height: 125px;
		margin: auto;
		font-size: 12.5px;
		font-family: "Lucida Console", Courier, monospace;
		border: 2px solid !important !important;
	}
	
	#tblReviewRecordList td img {
		width: 45px !important;
		heigtht: 45px !important;
	}
	
	#tblReviewRecordList th {
		background: #f2f2f2;
		border-bottom: 2px solid #808080;
	}
	
	#BtnReviewForm {
		margin-bottom: 15px;
	}
	
	#BtnBackward {
		margin-bottom: 15px;
		float: right;
	}
</style>
<div class="page-content-wrapper">
	<div class="page-content">
		<div class="row">
			<div class="col-md-12">
				<div class="portlet light bordered">
					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold" style="float:left !important;">Review Process for IdApp: ${idApp}</span>
							<span id="IdAppProcessStatus" style="float:right !important;" class="caption-subject bold"></span>
						</div>
					</div>
					<div class="portlet-body">
						<button class="btn btn-primary" id="BtnReviewForm">Review the current run</button>	
						<button class="btn btn-primary" id="BtnBackward">Back to Dashboard</button>					
						<table id="tblReviewRecordList" class="table table-striped table-bordered table-hover" width="100%">
							<thead>
								<tr>
									<th>View</th>
									<th>Date</th>
									<th>Application</th>
									<th>Run</th>
									<th>Action</th>																		
									<th>Status</th>
									<th>Comments</th>
									<th>User</th>									
									<th>Action Date</th>
									<th>Approve Comments</th>																 
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
		<fieldset id='form-body-action-entry' class="fieldset-row">		
			<label for="ActionStateCode">Status</label>			
			<select id="ActionStateCode"></select>
			<br>
			<label for="ActionComments">Comments</label>
			<textarea rows="5" cols="7" id="ActionComments"></textarea>			
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
	var ManageReviewDataEntry = {
		DataSet: {},
		IdApp: ${idApp},
		PageOptionLists: {},
		ApproveProcessLogicalFlags: {}, 
		ActionRecord: {},
		DialogBody: null,
		setup: function() {
			ManageReviewDataEntry.DialogBody = $('#form-body-action-entry')[0];			
			$('#BtnReviewForm').on('click', function() { ManageReviewDataEntry.promoteReviewFormDialog(0,'-1'); });	
		},
		loadActionRecordList:  function(nLoadContext){
			JwfAjaxWrapper({
				WaitMsg: 'Loading Review Records',
				Url: 'loadActionRecordList',
				Headers:$("#token").val(),
				Data: { LoadContext: nLoadContext, idApp: ${idApp} },
				CallBackFunction: function(oResponse) {
					var lDisableReviewBtn = false, oApproveProcessLogicalFlags = {}, oApplicationStatus = {};
					
					ManageReviewDataEntry.DataSet = oResponse.DataSet;
					
					ManageReviewDataEntry.DataSet.forEach( function(oDataRow, nIndex) { oDataRow.TrimmedActionComments = oDataRow.ActionComments.split(' ').slice(0,5).join(' '); });
					
					ManageReviewDataEntry.PageOptionLists = (nLoadContext < 1) ? oResponse.PageOptionLists : ManageReviewDataEntry.PageOptionLists;
					ManageReviewDataEntry.ApproveProcessLogicalFlags = oResponse.ApproveProcessLogicalFlags;
					
					PageCtrl.debug.log('loadActionRecordList 01', ManageReviewDataEntry.ApproveProcessLogicalFlags);
					PageCtrl.debug.log('loadActionRecordList 02', JSON.parse(ManageReviewDataEntry.ApproveProcessLogicalFlags.ApplicationApprovalProcessStatus));					
					
					oApproveProcessLogicalFlags = ManageReviewDataEntry.ApproveProcessLogicalFlags;					
					lDisableReviewBtn = (oApproveProcessLogicalFlags.IsReviewCommentsAllowed.toLowerCase() == 'true') ? false : true;
					oApplicationStatus = JSON.parse(ManageReviewDataEntry.ApproveProcessLogicalFlags.ApplicationApprovalProcessStatus);					
					
					PageCtrl.debug.log('loadActionRecordList 03', 'Disable review button = ' + lDisableReviewBtn);
					
					$('#BtnReviewForm').prop("disabled", lDisableReviewBtn);
					$('#IdAppProcessStatus').html("&nbsp;[&nbsp;" + oApplicationStatus['element_text'] + "&nbsp;]");
					
					ManageReviewDataEntry.DataTable = $("#tblReviewRecordList").DataTable ({
						"data" : ManageReviewDataEntry.DataSet,
						"columns" : [
							{ "data" : "RowId-view" },
							{ "data" : "RunDate" },
							{ "data" : "IdApp" },
							{ "data" : "RunNo" },
							{ "data" : "ActionType" },														
							{ "data" : "ActionStateName" },
							{ "data" : "TrimmedActionComments" },
							{ "data" : "UserName" },							
							{ "data" : "ActionDate" },
							{ "data" : "IsDisplayApproveIcon" }							
						],
						order: [[ 0, "desc" ]],
						destroy: true,
						drawCallback: function( oSettings ) {
							$('#tblReviewRecordList a').off('click', ManageReviewDataEntry.AnkerIconClicked).on('click', ManageReviewDataEntry.AnkerIconClicked);
						}						 
					});	
				}
			});
		},
		AnkerIconClicked:  function(oEvent) {
			var lFoundId = false, sClickedAnkerId = oEvent.target.parentElement.id, aAnkerIdParts = sClickedAnkerId.split('-');			
			var sRowId = parseInt(aAnkerIdParts[1]), nContext = (aAnkerIdParts[0] === 'view') ? 1 : 2;			
			
			PageCtrl.debug.log('AnkerIconClicked 01', aAnkerIdParts);
			PageCtrl.debug.log('AnkerIconClicked 02', ManageReviewDataEntry.PageOptionLists["DQ_REVIEW_STATUS"][0]["row_id"] + ',' + ManageReviewDataEntry.PageOptionLists["DQ_APPROVE_STATUS"][0]["row_id"]);
			
									
			ManageReviewDataEntry.promoteReviewFormDialog(nContext, sRowId);			
		},
		promoteReviewFormDialog: function(nContext, sRowId) {
			var aDialogContext = [ "Add Review Comments", "View Comments", "Add Approval Comments" ];
			var aDialogButtons = (nContext === 1) ? ['Ok'] : ['Submit', 'Cancel'];

			ManageReviewDataEntry.fillActionData(nContext, sRowId);
			
			Modal.customDialog(1, aDialogContext[nContext],
				{ 'BodyDomObj': ManageReviewDataEntry.DialogBody,'HeaderHtml': null },
				aDialogButtons, { onDialogClose: cbPromoteAddEditDialog, onDialogLoad: cbPromoteAddEditDialog });

			return;

			function	cbPromoteAddEditDialog(oEvent) {
				var sEventId = oEvent.EventId, sBtnSelected = oEvent.ClickedButton;

				switch(sEventId) {
					case jwfGlobal.EVT_MODAL_DIALOG_ONLOAD:
						PageCtrl.debug.log('on Dialog load','ok');
						break;

					case jwfGlobal.EVT_MODAL_DIALOG_ONCLOSE:
						if (sBtnSelected === 'Submit') { ManageReviewDataEntry.submitActionData(); }
						break;
				}
			}	
		},
		fillActionData: function(nContext, sRowId) {
			var oActionRecord = {}, nIndex = -1;

			if (nContext === 1) {			
				nIndex = UTIL.indexOfRecord(ManageReviewDataEntry.DataSet, 'RowId', sRowId.toString());
				oActionRecord = ManageReviewDataEntry.DataSet[nIndex];
			} else {
				oActionRecord = (nContext === 0) ? ManageReviewDataEntry.getNewActionRecord("Review") : ManageReviewDataEntry.getNewActionRecord("Approve");
			}
			
			/* "DQ_REVIEW_STATUS, DQ_APPROVAL_PROCESS_STATUS, DQ_APPROVE_STATUS" */
			if (oActionRecord.ActionType === 'Review') {
				UIManager.fillSelect($('#ActionStateCode')[0],ManageReviewDataEntry.PageOptionLists['DQ_REVIEW_STATUS'], oActionRecord.ActionStateCode);
			} else {
				UIManager.fillSelect($('#ActionStateCode')[0],ManageReviewDataEntry.PageOptionLists['DQ_APPROVE_STATUS'], oActionRecord.ActionStateCode);
			}
			$('#ActionComments').val(oActionRecord.ActionComments);
			
			if (nContext === 1) {
				$('#ActionStateCode').prop("disabled", true);			
				$('#ActionComments').prop("disabled", true);
			} else {
				$('#ActionStateCode').prop("disabled", false);			
				$('#ActionComments').prop("disabled", false);
			}			
			ManageReviewDataEntry.ActionRecord = oActionRecord;          // save copy so except user entered field rest are as it is for saving			
		},
		getNewActionRecord: function(sWhich) {
			var sActionStateRowId = (sWhich === 'Review') ? ManageReviewDataEntry.PageOptionLists["DQ_REVIEW_STATUS"][0]["row_id"] : ManageReviewDataEntry.PageOptionLists["DQ_APPROVE_STATUS"][0]["row_id"];
			var oActionRecord = { "IdApp": ManageReviewDataEntry.IdApp.toString(), "RowId":"-1","ActionType": sWhich, "ActionStateCode": sActionStateRowId };		
			return oActionRecord;
		},
		submitActionData: function() {
			var oActionRecordToSave = ManageReviewDataEntry.ActionRecord;
			
			oActionRecordToSave.ActionStateCode = $('#ActionStateCode').val();
			oActionRecordToSave.ActionComments = $('#ActionComments').val();
		
			JwfAjaxWrapper({
				WaitMsg: 'Saving Review Records...',
				Url: 'SaveActionRecord',
				Headers:$("#token").val(),
				Data: { ActionRecordToSave: JSON.stringify(oActionRecordToSave) },
				CallBackFunction: function(oResponse) {				
					if (oResponse.Result) {
						ManageReviewDataEntry.loadActionRecordList(1);
					} else {					
						Modal.confirmDialog(3, 'Save Failed', oResponse.Msg, ['Ok'] );
					}
				}
			});
		}		
	}
	
	$('#BtnBackward').on('click', function() {
		window.location.href = 'dashboard_table?idApp='+${idApp};
		
	});	
		
	$(document).ready( function() {
		initializeJwfSpaInfra();       // Load JWF framework module on this page
		ManageReviewDataEntry.setup();
		ManageReviewDataEntry.loadActionRecordList(0);
		
	});
</script>