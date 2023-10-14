<%@page import="com.databuck.service.RBACController"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<style>
	#form-body-add-edit-synonyms {
		padding: 25px;
	}	
	
	#form-body-add-edit-synonyms label {
		height: 32px;
		padding: 0px;
		maring: 0px;
		font-size: 13.5px;
	}

	#form-body-add-edit-synonyms select, #form-body-add-edit-synonyms input[type="text"] {
		padding: 0px;
		maring: 0px;
		width: 650px;
		height: 32px;
		font-size: 13.5px;
		/*font-family: "Helvetica Neue",Helvetica,Arial,sans-serif;*/
		font-family: inherit;
	}
	
	#form-body-add-edit-synonyms textarea {
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
	
	#form-body-add-edit-synonyms textarea[data-msgstate="hint"] {
		color: black;
	}

	#form-body-add-edit-synonyms textarea[data-msgstate="error"] {
		color: brown;
		border: 2px solid brown;
	}

	#tblSynonymsViewList th {
		/*background: #f2f2f2;*/
		border-bottom: 2px solid #808080;
	}

	.datatable-checkbox {
		width: 18px;	
		height: 18px; 
		margin-left:auto; 
		margin-right:auto;		
		margin-top: 8px; 
		margin-bottom: 5px;
	}
		
	.datatable-anker {
		margin-left:auto; 
		margin-right:auto;		
		margin-top: 8px; 
		margin-bottom: 5px;
	}
	
	#BtnAddNewSynonyms {
		margin-bottom: 15px;
	}
	
</style>
<div class="page-content-wrapper">
	<div class="page-content">
		<div class="row">
			<div class="col-md-12">
				<div class="portlet light bordered">
					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold "> Synonyms Library </span>
						</div>
					</div>
					<div class="portlet-body">
						<button class="btn btn-primary" id="BtnAddNewSynonyms" data-toggle="modal" data-target="#myModal">Add New Synonyms</button>						
						<table id="tblSynonymsViewList" class="table table-striped table-bordered table-hover" width="100%">
							<thead>
								<tr>
									 <th>Domain Name</th>
									 <th>Synonym Name</th>
									 <th>User Fields</th>
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
	
	<div style='display: none; min-width: 550px;'>
		<fieldset id='form-body-add-edit-synonyms' class="fieldset-row">
			<label for="DomainId">Domain Name</label>
			<select id="DomainId"></select>
			</br>

			<label for="SynonymsName">Synonym Name</label>
			<input type="text" id="SynonymsName">
			</br>
			
			<label for="UserFields">User Fields</label>
			<input type="text" id="UserFields">
			</br>
			
			<textarea id='form-validation-msg' readonly disabled data-msgstate="hint"></textarea>
		</fieldset>
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
	var ManageSynonymsDataEntry = {
		DataSet: {},
		PageOptionLists: {},
		SynonymsRecordToSave: {},
		SynonymsRecordValidToSave: false,
		MsgTextArea: null,
		DialogBody: null,
		setup: function() {	
			ManageSynonymsDataEntry.MsgTextArea = $('#form-validation-msg')[0];
			ManageSynonymsDataEntry.DialogBody = $('#form-body-add-edit-synonyms')[0];			
			
			$('#BtnAddNewSynonyms').on('click', function() { ManageSynonymsDataEntry.promoteAddEditDialog('-1') });			
			$('#SynonymsName').on('change', ManageSynonymsDataEntry.onChangeData);
			$('#UserFields').on('change', ManageSynonymsDataEntry.onChangeData);
			
		},
		setValidationMsg: function(sMsg, lError) {
			var sMsgMode = (lError) ? "error" : "hint";
			
			ManageSynonymsDataEntry.MsgTextArea.value = sMsg;			
			ManageSynonymsDataEntry.MsgTextArea.setAttribute("data-msgstate", sMsgMode);
		},
		onChangeData: function(oEvent) {
			var sFieldName = oEvent.target.id, sFieldValue = $('#' + sFieldName).val().trim(), lValidData = true, sErrorMsg = '', sUserField = '';
			var sSynonymsName = $('#SynonymsName').val().trim(),  sUserFields = $('#UserFields').val().trim();
			var lIsMainDataBlank = ( (sSynonymsName.length < 1) || (sUserFields.length < 1) ) ? true : false;
			
			if (sFieldName === 'SynonymsName') {
				lValidData = /^[a-zA-Z0-9_]+$/.test(sFieldValue);
				sErrorMsg = (lValidData) ? "" : "{field}: Only alphanumeric and underscore characters allowed\nExamples: 'emp_id' Or 'employee_id' Or 'empid' Or 'empno'";	
				sUserField = 'Synonyms Name';
			} else {
				lValidData = /^[a-zA-Z0-9_ ,]+$/.test(sFieldValue);
				sErrorMsg = (lValidData) ? '' : 
						  "{field}: Only alphanumeric and underscore characters allowed in User Fields. MUST use comma (,) optionally along with space to seprate multiple user fields.  " 
						+ "If space entered between two words without comma may result into global rules and thresholds based DQ checks to work wrongly or even not getting applied.\n"
						+ "Example: 'emp_id1, emp_id2,   emp_no,emp_no1'"
				sUserField = 'User Fields';						
			}			

			if (sErrorMsg.length > 0) {				
				sErrorMsg = sErrorMsg.replace('{field}', sUserField);
				ManageSynonymsDataEntry.setValidationMsg(sErrorMsg,true);				
				ManageSynonymsDataEntry.SynonymsRecordValidToSave = false;
			} else {
				ManageSynonymsDataEntry.setValidationMsg( ('{field}: Entered value passed validation'.replace('{field}', sUserField)) ,false);
				ManageSynonymsDataEntry.SynonymsRecordValidToSave = true;
			}
			
			if (lIsMainDataBlank) { ManageSynonymsDataEntry.SynonymsRecordValidToSave = false; }
		},
		loadSynonymsViewList: function(nLoadContext) {
			JwfAjaxWrapper({
				WaitMsg: 'Reloading Synonyms',
				Url: 'loadSynonymsViewList',
				Headers:$("#token").val(),
				Data: { LoadContext: nLoadContext },
				CallBackFunction: function(oResponse) {
					ManageSynonymsDataEntry.DataSet = oResponse.DataSet;
					ManageSynonymsDataEntry.PageOptionLists = (nLoadContext < 1) ? oResponse.PageOptionLists : ManageSynonymsDataEntry.PageOptionLists;
					
					PageCtrl.debug.log('ManageSynonymsDataEntry loadSynonymsViewList', Array.isArray(ManageSynonymsDataEntry.DataSet) + ',' + Array.isArray(ManageSynonymsDataEntry.PageOptionLists['DOMAIN_LIST']) + ',' + nLoadContext);
					
					ManageSynonymsDataEntry.DataTable = $("#tblSynonymsViewList").DataTable ({
						"data" : ManageSynonymsDataEntry.DataSet,
						"columns" : [
							{ "data" : "DomainName" },
							{ "data" : "SynonymsName" },
							{ "data" : "UserFields" },
							{ "data" : "SynonymsId-edit" },
							{ "data" : "SynonymsId-delete" }								
						],
						order: [[ 0, "desc" ]],
						destroy: true,
						scrollX:true,
						drawCallback: function( oSettings ) {
							$('#tblSynonymsViewList a').off('click', ManageSynonymsDataEntry.AnkerIconClicked).on('click', ManageSynonymsDataEntry.AnkerIconClicked);
						}						 
					});					
				}		
			});
		},
		AnkerIconClicked:  function(oEvent) {
			var lFoundId = false, sClickedAnkerId = oEvent.target.parentElement.id, sRowId = '-1';
			var sDeleteDisabledMsg = 'Delete is disabled as Synonyms affect global rules and thresholds functionality directly.   '
					+ 'Hence accidental or incorrect delete of Synonyms may result into global rules and thresholds based DQ checks to work wrongly or even not getting applied.';			
			
			if (sClickedAnkerId.indexOf('delete') > -1) {
				Modal.confirmDialog(2, 'Delete Disabled',	sDeleteDisabledMsg, ['Ok'] );			
			} else {
				if ( sClickedAnkerId.split('-').length	> 1 ) { sRowId = sClickedAnkerId.split('-')[1]; }
				ManageSynonymsDataEntry.promoteAddEditDialog(sRowId);
			}				
		},
		fillSynonymsForm: function(lNewSynonyms, nRowIndex) {
			var oSynonymsRecordToSave = (lNewSynonyms) ? { "DomainId":"5", "UserFields":"","SynonymsName":"","SynonymsId":"-1"} : JSON.parse(JSON.stringify(ManageSynonymsDataEntry.DataSet[nRowIndex]));
			var sDefaultMsg = 'Enter or modify synonyms details';	
			
			
			ManageSynonymsDataEntry.SynonymsRecordToSave = oSynonymsRecordToSave;
			$('#SynonymsName').val(oSynonymsRecordToSave.SynonymsName);
			$('#UserFields').val(oSynonymsRecordToSave.UserFields);
			UIManager.fillSelect($('#DomainId')[0],ManageSynonymsDataEntry.PageOptionLists['DOMAIN_LIST'], oSynonymsRecordToSave.DomainId);
			
			$('#DomainId').prop("disabled", ( (lNewSynonyms) ? false : true ) );
			
			


			if (lNewSynonyms) { 
				$('#DomainId').focus(); 
				ManageSynonymsDataEntry.SynonymsRecordValidToSave = false;
				ManageSynonymsDataEntry.setValidationMsg(sDefaultMsg, true);
			} else { 
				$('#SynonymsName').focus();
				ManageSynonymsDataEntry.SynonymsRecordValidToSave = true;
				ManageSynonymsDataEntry.setValidationMsg(sDefaultMsg, false);
			}			
		},
		saveSynonymsData: function() {
		
			if ($("#SynonymsName").val().length == 0||$("#SynonymsName").val()== null) {
				
				alert("Please Enter Synonyms Name");
				return;						
			}
			if ($("#UserFields").val().length == 0||$("#UserFields").val()== null) {
				
				alert("Please Enter User Fields");
				return;						
			}
			var oSynonymsRecordToSave = UTIL.extractSubObjectByFields(ManageSynonymsDataEntry.SynonymsRecordToSave, 'DomainId,UserFields,DomainName,SynonymsName,SynonymsId');
			
			oSynonymsRecordToSave.SynonymsName = $('#SynonymsName').val();
			oSynonymsRecordToSave.UserFields = $('#UserFields').val();
			oSynonymsRecordToSave.DomainId = $('#DomainId').val();
			
			sValidationMsg = ManageSynonymsDataEntry.SynonymsRecordValidToSave;
			PageCtrl.debug.log('Is record valid to save', ManageSynonymsDataEntry.SynonymsRecordValidToSave);
			var sValidationMsg = checkInputText() ? true : false;
			
			if (sValidationMsg) {
				ManageSynonymsDataEntry.submitSynonymsData(oSynonymsRecordToSave);
			} else {
				alert('Vulnerability in submitted data, not allowed to submit!');
				/* Modal.confirmDialog(4, 'Invalid data', 'Kindly click save button after validation text box shows all data as valid. Your changes to Synonyms are NOT saved.', ['Ok'] ); */
			}
		},
		submitSynonymsData: function(oSynonymsRecordToSave) {
		
			JwfAjaxWrapper({
				WaitMsg: 'Saving Synonyms',
				Url: 'SaveSynonymsFromViewList',
				Headers:$("#token").val(),
				Data: { SynonymsRecordToSave: JSON.stringify(oSynonymsRecordToSave) },
				CallBackFunction: function(oResponse) {				
					if (oResponse.Result) {
	
						setTimeout(
								function() {
									window.location.reload();
								}, 500);
						
						//ManageSynonymsDataEntry.loadSynonymsViewList(1);
					} else {					
						Modal.confirmDialog(3, 'Save Failed', 'Problem in one of the input parameters', ['Ok'] );
						setTimeout(
								function() {
									window.location.reload();
								}, 1000);
					}
				}				
			});			
		},
		promoteAddEditDialog: function(sRowId) {
			var lNewSynonyms = (sRowId === '-1') ? true : false;			
			var sModeTitle = (lNewSynonyms) ? 'Add New Synonyms' : 'Edit Synonyms';
			
			var nRowIndex = (lNewSynonyms) ? -1 : UTIL.indexOfRecord(ManageSynonymsDataEntry.DataSet, 'SynonymsId', sRowId);						
			
			ManageSynonymsDataEntry.fillSynonymsForm(lNewSynonyms, nRowIndex);

			Modal.customDialog(1, sModeTitle,
					{ 'BodyDomObj': ManageSynonymsDataEntry.DialogBody,'HeaderHtml': null },
					['Save', 'Cancel'], { onDialogClose: cbPromoteAddEditDialog, onDialogLoad: cbPromoteAddEditDialog });

			return;

			function	cbPromoteAddEditDialog(oEvent) {
				var sEventId = oEvent.EventId, sBtnSelected = oEvent.ClickedButton;

				switch(sEventId) {
					case jwfGlobal.EVT_MODAL_DIALOG_ONLOAD:
						PageCtrl.debug.log('on Dialog load','ok');
						break;
						
					case jwfGlobal.EVT_MODAL_DIALOG_ONCLOSE:
						if (sBtnSelected === 'Save') { ManageSynonymsDataEntry.saveSynonymsData(); }
						break;
				}
			}	
		}
	}

	$(document).ready( function() {
		initializeJwfSpaInfra();       // Load JWF framework module on this page
		ManageSynonymsDataEntry.setup();
		ManageSynonymsDataEntry.loadSynonymsViewList(0);
	});

</script>
