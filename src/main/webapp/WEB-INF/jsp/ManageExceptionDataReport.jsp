<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />

<head>
	<link rel="stylesheet" href="./assets/jwf/content/JwfSpaInfra.css">
	<script src="./assets/jwf/scripts/JwfSpaInfra.js"></script>
</head>
<style>
	#collapseControl {
		float: right;
		color: grey;
		font-size: 25px !important;
		margin-right: 3px;
		text-decoration: none;
		cursor: pointer !important;
	}	

	.RowInEditing {
		background-color: #3973ac !important; /* #0066cc */
		color: white;
		weight: bold;
	}
	
	.RowInEditing a, .RowInEditing input[type=checkbox] {
		display: none;
	}

	#tblReportHeader {
		width: 100%;
		border-collapse: collapse;
		border: 2px dotted grey;
	}

	td {
		padding: 10px;
	}

	td input[type=radio] {
		width: 18px !important;
		height: 18px !important;
		margin-right: 10px !important;
		margin-left: 10px !important;
	}
	
	td input[type=radio]:focus {
		outline: none;
    	box-shadow: none;		
	}
	
	td input[type=text] {
		width: 70%;
		height: 32px;
		margin-right: 15px;
	}

	td select {
		width: 45%;
		height: 32px;
	}

	#displayStaticData {
		padding-left: 10px;
		padding-bottom: 10px;
		font-weight: bold;
		font-size: 15px;
		color: #E26A6A;
		display: block;		
	}
	
	#displayStaticData:[data-show-border=Y] {
		border-bottom: 2px dotted grey;
	}
	
	/* CSS for show options */

	#trShowOptions td:nth-child(1) {
		width: 26%;
	}

	#trShowOptions td:nth-child(2) {
		width: 37%;
	}

	#trShowOptions td:nth-child(3) {
		width: 37%;
	}

	/* CSS for colums of validation applications area (table) */

	#divReportDetails {
		width: 100%;
		padding: 5px;
		border-left: 2px dotted grey;
		border-right: 2px dotted grey;
		border-bottom: 2px dotted grey;
		max-height: 250px;
		overflow-x: hidden;
		overflow-y: auto;
		scroll-behavior: smooth;
	}

	#divReportDetails label {
	  float: left;
	  width: 47%;
	}

	#divReportDetails input[type=checkbox], #divReportDetails label {
		height: 20px !important;
	}

	#divReportDetails input[type=checkbox] {
	  width: 20px !important;
	  margin-right: 15px !important;
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
	}
		
</style>
<div class="page-content-wrapper">
	<!-- BEGIN CONTENT BODY -->
	<div class="page-content">
		<!-- BEGIN PAGE TITLE-->
		<!-- END PAGE TITLE-->
		<!-- END PAGE HEADER-->

		<div class="row">
			<div class="col-md-12">
				<!-- BEGIN EXAMPLE TABLE PORTLET-->
				<div class="portlet light bordered">
					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold ">Manage Exception Data Report</span>
						</div>
					</div>

					<p id='displayStaticData'></p>
					
					<table id='tblReportHeader'>
						<tr>
							<td>
								<label for='radio'>Show Validation Applications</label></br>
								<label for='ShowOption01'><input type='radio' name='ShowOption' id='ShowOption01' value='1' checked> All &nbsp; &nbsp; &nbsp;</label>
								<label for='ShowOption02'><input type='radio' name='ShowOption' id='ShowOption02' value='2'> Only Selected &nbsp; &nbsp; &nbsp;</label>
								<label for='ShowOption03'><input type='radio' name='ShowOption' id='ShowOption03' value='3'> Filtered &nbsp; &nbsp; &nbsp;</label>
							</td>
							<td>
								<label for='FilterText'>Enter Filter on Validation Applications</label></br>
								<input type='text' id='FilterText' value='Test'>
							</td>
							<td>
								<label for='LimitNoOfApps'>Limit No Of Applications Selection</label></br>
								<select id='LimitNoOfApps'>
									<option>5</option>
									<option selected>10</option>
								</select>								
							</td>
						</tr>
						<tr>
						<td>
							<label for='ReportName'>Exception Report Name</label></br>
							<input type='text' id='ReportName'>						
						</td>
						<td>
							<label for='ReportDescription'>Exception Report Description</label></br>
							<input type='text' id='ReportDescription'>						
						</td>
						<td>
							<label for='DataPeriodInt'>Data Frequency or Period</label></br>
							<select id='DataPeriodInt'>
								<option value='1'>Daily</option>
								<option value='2'>Weekly</option>
								<option value='3'>Monthly</option>
							</select>						
						</td>
						</tr>
					</table>					
					
					<div id='divReportDetails' style='display: block;'></div>
					
					<table width=100% id='tblDataButtons'><tr>
						<td width=25%><button class="btn btn-primary DataBtns" id="BtnNew">Add New Exception Report</button></td>
						<td width=75%>
							<button class="btn btn-primary DataBtns" id="BtnCancel">Cancel</button>
							<button class="btn btn-primary DataBtns" id="BtnSave">Save</button>							
						</td>
					</tr></table>	
					</br>
					
					<table id="tblExceptionReportViewList" class="table table-striped table-bordered table-hover" width="100%">
						<thead>
							<tr>
								 <th>Report Id</th>
								 <th>Report Name</th>									 
								 <th>Description</th>
								 <th>Project Id</th>
								 <th>Data Period</th>
								 <th>No of Applications</th>
								 <th>Applications</th>								 
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
<jsp:include page="footer.jsp" />

<script>	
	var ManageExceptionDataReport = {
		TableFormHtml: '',
		PageData: {},
		DataTable: {},
		HtmlTemplates: { 
			"SingleCheckBoxRow": "{1}{2}</br></br>",
			"SingleCheckBox": "<label for='AppId-{0}'><input type='checkbox' id='AppId-{0}' {2}>{1}</label>",
			"displayStaticData":	"Report Id: {1}{0} Project: {2}{0} Applications for Project: {3}{0} Shown: {4}{0}<a onClick='validateHrefVulnerability(this)'  id='collapseControl' data-collapsed='false'>&#9650;</a>",
			"paddingSpace": "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;",
			"RadioLabels": {
				"ShowOption01" : "<input type='radio' name='ShowOption' id='ShowOption01' value='1' checked> All &nbsp; &nbsp; &nbsp;",
				"ShowOption02" : "<input type='radio' name='ShowOption' id='ShowOption02' value='2' checked> Only Selected &nbsp; &nbsp; &nbsp;",
				"ShowOption03" : "<input type='radio' name='ShowOption' id='ShowOption02' value='3' checked> Filtered &nbsp; &nbsp; &nbsp;",
			}
		},
		FilterType: '1',
		ProjectValidationList: [],
		DatabaseExceptionRecord: {},
		EditingExceptionRecord: {},
		setup: function() {		
			$('#tblReportHeader tr td :input').change(ManageExceptionDataReport.mainEventHandler);
			$('#tblDataButtons button').on('click', ManageExceptionDataReport.mainEventHandler);			
			$('#FilterText').keyup( function() { ManageExceptionDataReport.applyShowFilter(false, true); });			
		},		
		loadExceptionReportViewList: function() {
			var sViewTable = '#tblExceptionReportViewList';
			
			JwfAjaxWrapper({
				WaitMsg: 'Loading Exception Data Report Records',
				Url: 'loadExceptionReportViewList',
				Headers:$("#token").val(),
				CallBackFunction: function(oResponse) {
					ManageExceptionDataReport.PageData = oResponse;
	
					ManageExceptionDataReport.DataTable = $(sViewTable).DataTable ({
						"data": ManageExceptionDataReport.PageData.ExceptionReportViewList,
						"columns" : [
							{ "data" : "ReportId" },
							{ "data" : "ReportName" },
							{ "data" : "ReportDescription" },
							{ "data" : "ProjectId" },
							{ "data" : "DataPeriodWord" },
							{ "data" : "AppCount" },							
							{ "data" : "AppIds" },
							{ "data" : "ReportRowId-edit" },
							{ "data" : "ReportRowId-delete" }							
						],
						order: [[ 0, "asc" ]],
						orderClasses: false,						
						destroy: true,
						drawCallback: function( oSettings ) {
							$(sViewTable + ' a').off('click', ManageExceptionDataReport.mainEventHandler).on('click', ManageExceptionDataReport.mainEventHandler);
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
							ManageExceptionDataReport.mainEventHandler(oPageLoadEvent);
						}
					});					
				
				} catch (oError) { 
					PageCtrl.debug.log('Error occured in data table event', oError.message);
				}
			});			
		},
		resetRowHighlighting: function() {
			$("#tblExceptionReportViewList > tbody > tr").each(function () {
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
					"client-flow": { "ShowOption": "AppsShowOptionChanged",  "DataFormChanged": "DataFormChanged", "AppId": "AppStateChanged", "BtnNew": "DataNew", "BtnCancel": "DataCancel", "edit": "DataEdit" }
				};			
			
			/* Get context key to retrieve event context */ 
			if (sTagName === 'i') {													// edit and delete icons
				sContextKey = sTargetId.split('-')[0];
			
			} else if (sType === 'radio') {										// show applications radio button
				sContextKey = 'ShowOption';
			
			} else if ( 'text,select-one'.indexOf(sType) > -1)  {			// data form changed					
				sContextKey = 'DataFormChanged';
				
			} else if (sType === 'checkbox')	{			
				sContextKey = 'AppId';												// Application checked / unchecked				
				
			} else if (sTagName === 'button') {									// data buttons save, new and cancel
				sContextKey = sTargetId;
			}				
			
			/* Get context key to determine event context */
			sEventContext = oContextMatrix[sFlowType][sContextKey]; 			
			//PageCtrl.debug.log('mainEventHandler 01 ' + sEventContext, 'sTagName = ' + sTagName + ', sType = ' + sType + ', sTargetId = ' + sTargetId + ', sFlowType = ' + sFlowType);			
			
			switch (sEventContext) {
				case 'AppsShowOptionChanged':
					ManageExceptionDataReport.applyShowFilter(false, false);
					break;
				
				case 'AppStateChanged':
					ManageExceptionDataReport.appCheckStateChanged(sTargetId.split('-')[1], $(oEffectiveTarget).prop('checked'), oEffectiveTarget);
					break;				
				
				case 'DataNew':
					ManageExceptionDataReport.beginDataEditing(sEventContext, '-1', oEffectiveTarget);
					break;
					
				case 'DataEdit':
					ManageExceptionDataReport.beginDataEditing(sEventContext, sTargetId.split('-')[1], oEffectiveTarget);
					break;

				case 'DataCancel':
					ManageExceptionDataReport.beginDataEditing(sEventContext, ManageExceptionDataReport.DatabaseExceptionRecord.ReportRowId, null);
					break;
			
				case 'DataSave':
					ManageExceptionDataReport.updateModelFromForm();
					if(!checkInputText()){
						alert('Vulnerability in submitted data, not allowed to submit!');
						return;
					}
					ManageExceptionDataReport.invokeMainExceptionReportHandler('Saving Exception Report', 'saveExceptionReport', true);
					break;

				case 'DataDelete':
					ManageExceptionDataReport.deleteExceptionReport(sTargetId.split('-')[1]);
					break;
					
				default:
					PageCtrl.debug.log('mainEventHandler 02', 'sTagName = ' + sTagName + ', sType = ' + sType + ', sTargetId = ' + sTargetId + ', sFlowType = ' + sFlowType);			
			}		
		},
		beginDataEditing: function(sDataEditContext, sReportRowId, oEffectiveTarget) {
			var oFocusRecord = {}, oTableRow = null, aFieldList = [ 'ReportName','ReportDescription','DataPeriodInt' ], aAppIdList = [],
				lIsNewRecord = (sReportRowId === '-1') ? true : false,
				sFocusField = '#' + ( (lIsNewRecord) ? aFieldList[0] : aFieldList[1] );		
		
			if (sDataEditContext === 'DataCancel') { 
				oFocusRecord = ManageExceptionDataReport.DatabaseExceptionRecord;							// reset edited data - get database copy saved at begin editing
				
			} else {
				oFocusRecord = getExceptionRecordByRow(sReportRowId);											// begin editing - save either new or DB copy into variable
				ManageExceptionDataReport.resetRowHighlighting();
			}			
			
			oFocusRecord.AppIds = (oFocusRecord.AppIds) ? oFocusRecord.AppIds : '';
			aAppIdList = (oFocusRecord.AppIds.length > 0) ? oFocusRecord.AppIds.split(',') : [];
			ManageExceptionDataReport.DatabaseExceptionRecord = oFocusRecord;								
			ManageExceptionDataReport.ProjectValidationList = getProjectValidationList(oFocusRecord.ProjectId, aAppIdList);				
			
			// Applicable for all data actions new, edit and reset.  Make clone of JSON else all user editing will overwrite database copy. 
			ManageExceptionDataReport.EditingExceptionRecord = JSON.parse( JSON.stringify(oFocusRecord) );
			ManageExceptionDataReport.EditingExceptionRecord.ShownAppList = [];
			
			$('#ReportName').prop('disabled',!lIsNewRecord);
			$(sFocusField).focus();
			aFieldList.forEach( function(sFieldName, nIndex) {
				$('#' + sFieldName).val( oFocusRecord[sFieldName] );
			});
			
			if (sDataEditContext === 'DataEdit') {
				oTableRow = $(oEffectiveTarget).closest('tr');
				$(oTableRow).addClass('RowInEditing');
			}			
			ManageExceptionDataReport.applyShowFilter(true, false);
			ManageExceptionDataReport.collapseDetailsClick(null, true);
			
			/* Make below two functions as closure as part of begin edit logic only, later can be moved to public scope if needed */
			function getProjectValidationList(sProjectId, aAppIdList) {
				var aProjectValidationList = [];

				ManageExceptionDataReport.PageData.ValidationMasterList.forEach( function(oValidationApp, nIndex) {
					oValidationApp.Show = true;
					if (oValidationApp.ProjectId === sProjectId) {
						oValidationApp.checked = (aAppIdList.indexOf(oValidationApp.AppId) > -1) ? true : false;
						aProjectValidationList.push(oValidationApp); 
					}
				});
				return aProjectValidationList;		
			}

			function getExceptionRecordByRow(sReportRowId) {
				var nRecordIndex = -2, oRetValue = null; 

				if (sReportRowId === '-1') {
					oRetValue = JSON.parse( JSON.stringify(ManageExceptionDataReport.PageData.NewExceptionReport) );
				} else {
					nRecordIndex = UTIL.indexOfRecord(ManageExceptionDataReport.PageData.ExceptionReportViewList, 'ReportRowId', sReportRowId);
					if (nRecordIndex > -1) { 
						oRetValue = JSON.parse( JSON.stringify(ManageExceptionDataReport.PageData.ExceptionReportViewList[nRecordIndex]) ); 
					}
				}
				return oRetValue;
			}
			
		},
		applyShowFilter(lBeginEditingCall, lKeyPressCall) {		
			var lIsShowApplication = false, aShownAppList = [], sFilterText = $('#FilterText').val().toUpperCase(), sFilterType = '', lDisableFilterText = true, sFilterText = '';
			
			if (lBeginEditingCall) {
				["ShowOption01", "ShowOption02", "ShowOption03"].forEach( function(sRadioButtonId, nIndex) {				
					$('label[for="' + sRadioButtonId + '"]').html(ManageExceptionDataReport.HtmlTemplates.RadioLabels[sRadioButtonId]);
				});
				
				$('input[name="ShowOption"]:radio').off('change', ManageExceptionDataReport.mainEventHandler).on('change', ManageExceptionDataReport.mainEventHandler);				
				$('[name=ShowOption]').prop('checked',false);
				$('[name=ShowOption][value="1"]').prop('checked',true);				
			}
			
			sFilterType = $('input[name="ShowOption"]:radio:checked').val();
			lDisableFilterText = (sFilterType !== '3') ? true : false;
			
			$('#FilterText').prop('disabled', lDisableFilterText);			
			sFilterText = (sFilterType === '3') ? $('#FilterText').val() : '';
			$('#FilterText').val(sFilterText);
			
			if (sFilterType === '1') {																		// Show all applications
				ManageExceptionDataReport.EditingExceptionRecord.ShownAppList = ManageExceptionDataReport.ProjectValidationList;    
				
			} else {																								// Show only selected or filted by user entered text
				ManageExceptionDataReport.EditingExceptionRecord.ShownAppList = ManageExceptionDataReport.ProjectValidationList.filter( function(oValidationApp) {
					if (sFilterType === '2') {
						lIsShowApplication = oValidationApp.checked;
					} else if (sFilterType === '3') {	
						lIsShowApplication = (oValidationApp.AppName.toUpperCase().indexOf(sFilterText) > -1) ? true : false;
					} else {
						lIsShowApplication = true;
					}
					return lIsShowApplication;
				});
			}
			ManageExceptionDataReport.loadDetailsData();
			
		},
		loadDetailsData: function() {
			var sSingleCheckBoxRowHtmlTmpl = ManageExceptionDataReport.HtmlTemplates.SingleCheckBoxRow,
				sDivValidationAppsHtml = '',  sSingleCheckBoxRowHtml = '', sCheckBox1 = '', sCheckBox2 = '', aAppIdList = [],	
				nShownApplications = ManageExceptionDataReport.EditingExceptionRecord.ShownAppList.length, nProjectApplications = ManageExceptionDataReport.ProjectValidationList.length,
				sStaticData = ManageExceptionDataReport.HtmlTemplates.displayStaticData, 
				sReportId = ManageExceptionDataReport.EditingExceptionRecord.ReportId === '-1' ? 'New' : ManageExceptionDataReport.EditingExceptionRecord.ReportId;

			aAppIdList = (ManageExceptionDataReport.EditingExceptionRecord.AppIds.length < 1) ? [] : ManageExceptionDataReport.EditingExceptionRecord.AppIds.split(',');
			nNoOfSelectedApplications = aAppIdList.length;

			ManageExceptionDataReport.EditingExceptionRecord.ShownAppList.forEach( function(oValidationApp, nIndex) {
				//oValidationApp.Selected = ( aAppIdList.indexOf(oValidationApp.AppId) > -1 ? true : false );

				if ( (nIndex % 2) === 0 ) {
					sCheckBox1 = getSingleCheckBoxHtml(oValidationApp, nIndex);
				} else {
					sCheckBox2 = getSingleCheckBoxHtml(oValidationApp, nIndex);

					sSingleCheckBoxRowHtml = sSingleCheckBoxRowHtmlTmpl.replace('{1}',sCheckBox1).replace('{2}',sCheckBox2);
					sDivValidationAppsHtml = sDivValidationAppsHtml + sSingleCheckBoxRowHtml;
				}

				/* if total no of applications are odd and it is last index */
				if ( ((nShownApplications % 2) !== 0) && (nIndex === (nShownApplications - 1)) ) {
					sCheckBox2 = '';
					sSingleCheckBoxRowHtml = sSingleCheckBoxRowHtmlTmpl.replace('{1}',sCheckBox1).replace('{2}',sCheckBox2);
					sDivValidationAppsHtml = sDivValidationAppsHtml + sSingleCheckBoxRowHtml;
				}				
			});
			
			sStaticData = sStaticData.replace('{1}', sReportId).replace('{2}', getProjectName(ManageExceptionDataReport.EditingExceptionRecord.ProjectId));
			sStaticData = UTIL.replaceAll(sStaticData.replace('{3}', nProjectApplications), '{4}', nShownApplications);
			sStaticData = UTIL.replaceAll(sStaticData, '{0}', ManageExceptionDataReport.HtmlTemplates.paddingSpace);
			
			$('#displayStaticData').html(sStaticData);
			$('#divReportDetails').html(sDivValidationAppsHtml);
			$('#displayStaticData a').on('click', ManageExceptionDataReport.collapseDetailsClick);
			
			$('#divReportDetails :input[type=checkbox]').off('click', ManageExceptionDataReport.mainEventHandler).on('click', ManageExceptionDataReport.mainEventHandler);			

			function getSingleCheckBoxHtml(oValidationRecord) {
				var sRetValue = ManageExceptionDataReport.HtmlTemplates.SingleCheckBox;

				sRetValue = UTIL.replaceAll(sRetValue, '{0}', oValidationRecord.AppId).replace('{1}',  oValidationRecord.AppName);
				sRetValue = sRetValue.replace('{2}', ( (oValidationRecord.checked) ? 'checked' : '') );

				return sRetValue;
			}
			
			function getProjectName(sProjectId) {
				var nFoundIndex = UTIL.indexOfRecord(ManageExceptionDataReport.PageData.ProjectMasterList, 'ProjectId', sProjectId), sRetValue = '';

				if (nFoundIndex > -1) { sRetValue = ManageExceptionDataReport.PageData.ProjectMasterList[nFoundIndex].ProjectName; }			
				return sRetValue;
			}			
		},
		appCheckStateChanged: function(sClickedAppId, lAppIdChecked, oCheckBox) {		
			var sAppIds = '', aAppIds = [], nFoundIndex = -2, nLimitNoOfApps = parseInt($('#LimitNoOfApps').val());
			
			PageCtrl.debug.log('appCheckStateChanged 01 ' + (new Date()).getTime(), nLimitNoOfApps +  ' ,**' + ManageExceptionDataReport.EditingExceptionRecord.AppIds + '**');
			
			nFoundIndex = UTIL.indexOfRecord(ManageExceptionDataReport.ProjectValidationList,'AppId', sClickedAppId);
			if (nFoundIndex > -1) { 
				ManageExceptionDataReport.ProjectValidationList[nFoundIndex].checked = lAppIdChecked;
				
				ManageExceptionDataReport.ProjectValidationList.filter( function(oValidationApp) { 
					if (oValidationApp.checked) { aAppIds.push(oValidationApp.AppId); }
					return false; 
				});
				
				sAppIds = (aAppIds.length > 0) ? aAppIds.join() : '';
				ManageExceptionDataReport.EditingExceptionRecord.AppIds = sAppIds;
			}
			
			if (aAppIds.length > nLimitNoOfApps) {
				Modal.confirmDialog(0, "Warning - Applications Limit Exceeded",	"Selected more than '{0}' validation applications, kindly deselect one or more else save will not be allowed".replace('{0}', nLimitNoOfApps), ['Ok'] );					
			}
			
			PageCtrl.debug.log('appCheckStateChanged 02 ' + (new Date()).getTime(), '**' + ManageExceptionDataReport.EditingExceptionRecord.AppIds + '**');			
		},
		/* 04-Jan-2020 Pradeep - Below method not used, not removed just to retain code in GIT */
		appCheckStateChanged_Not_Used: function(oEvent) {		
			var aAppIds = (ManageExceptionDataReport.EditingExceptionRecord.AppIds.length < 1) ? [] : ManageExceptionDataReport.EditingExceptionRecord.AppIds.split(','), sClickedAppId = oEvent.target.id.split('_')[1]; 
			var nFoundIndex = -2, lAppIdChecked = $(oEvent.target).prop('checked');
			
			PageCtrl.debug.log('appCheckStateChanged 01', sClickedAppId + ',' + $(oEvent.target).prop('checked') + Array.isArray(aAppIds) + aAppIds.length + ' , **' + ManageExceptionDataReport.EditingExceptionRecord.AppIds + '**');			
			
			if (Array.isArray(aAppIds)) { 			
				if (lAppIdChecked) {
					aAppIds.push(sClickedAppId);
				} else {	
					nFoundIndex = aAppIds.indexOf(sClickedAppId);						
					if (nFoundIndex > -1) { aAppIds.splice(nFoundIndex,1); }
				}
			}
			if ( (ManageExceptionDataReport.EditingExceptionRecord.AppIds.length < 1) && (aAppIds.length > 1) ) {
				ManageExceptionDataReport.EditingExceptionRecord.AppIds = aAppIds[1];
			} else {			
				ManageExceptionDataReport.EditingExceptionRecord.AppIds = aAppIds.join();
			}				
			
			nFoundIndex = UTIL.indexOf(ManageExceptionDataReport.ProjectValidationList,'AppId', sClickedAppId);
			if (nFoundIndex > -1) { ManageExceptionDataReport.ProjectValidationList[nFoundIndex].checked = lAppIdChecked; }
			
			PageCtrl.debug.log('appCheckStateChanged 02', sClickedAppId + ',' + $(oEvent.target).prop('checked') + aAppIds.length +  ',**' + ManageExceptionDataReport.EditingExceptionRecord.AppIds + '**, ' + nFoundIndex);			
		},
		deleteExceptionReport(sReportRowId) {
			var nRowIndex = UTIL.indexOfRecord( ManageExceptionDataReport.PageData.ExceptionReportViewList, 'ReportRowId', sReportRowId);
			var sConfimDialog = "Are you sure to delete Exception Data Report '{0}'";
			
			if (nRowIndex > -1) {
				ManageExceptionDataReport.EditingExceptionRecord = ManageExceptionDataReport.PageData.ExceptionReportViewList[nRowIndex];
				
				window.scrollTo(0,0);
				Modal.confirmDialog(
					0, 'Confirm Delete', sConfimDialog.replace('{0}', 
					ManageExceptionDataReport.EditingExceptionRecord.ReportName), 
					['Yes', 'No'], { onDialogClose: cbConfirmAction } 
				);				
			}
			
			function cbConfirmAction(oEvent) {
				var sEventId = oEvent.EventId, sBtnSelected = oEvent.ClickedButton;					
				if (sBtnSelected === 'Yes') { 
					ManageExceptionDataReport.invokeMainExceptionReportHandler('Deleting Exception Report', 'deleteExceptionReport', true);
				}
			}			
		},		
		updateModelFromForm: function() {
			var aFieldList = [ 'ReportName', 'ReportDescription','DataPeriodInt' ];
			
			aFieldList.forEach( function(sFieldName, nIndex) {
				ManageExceptionDataReport.EditingExceptionRecord[sFieldName] = $('#' + sFieldName).val();
			});			
		},
		invokeMainExceptionReportHandler: function(sWaitMsg, sContext, lReloadDataTable) {
			var oExceptionRecord = JSON.parse( JSON.stringify(ManageExceptionDataReport.EditingExceptionRecord) ), oData = {}, sValidationMsg = '';			
			
			oExceptionRecord = UTIL.extractSubObjectByFields(oExceptionRecord, 'ReportRowId,ReportName,ReportDescription,ReportId,ProjectId,DataPeriodInt,AppIds');
			sValidationMsg = isDataValidToSave(oExceptionRecord);
			
			if (sValidationMsg.length > 0) {
				Modal.confirmDialog(0, 'Invalid Data To Save',	sValidationMsg, ['Ok'] );
				
			} else {
				oData = { "Context": sContext, "Data": oExceptionRecord };			
				
				JwfAjaxWrapper({
					WaitMsg: sWaitMsg,
					Url: 'mainExceptionReportHandler',
					Headers:$("#token").val(),
					Data: { ExceptionReportData: JSON.stringify(oData) },
					CallBackFunction: function(oResponse) {
						if (oResponse.Status) {
							toastr.info(oResponse.Msg);
							if (lReloadDataTable) { ManageExceptionDataReport.loadExceptionReportViewList(); }
						} else {					
							toastr.info(oResponse.Msg);
						}
					}
				});
			}
			
			function isDataValidToSave(oExceptionRecord) {
				var sRetMsg = '', nLimitNoOfApps = parseInt($('#LimitNoOfApps').val()), aAppIds = (oExceptionRecord.AppIds.length < 1) ? [] : oExceptionRecord.AppIds.split(',');
				
				if ((oExceptionRecord.ReportRowId === '-1') && (oExceptionRecord.ReportName.trim().length < 1)) {
					sRetMsg = 'Please enter Report Name, it cannot be blank'
					
				} else if (oExceptionRecord.AppIds.length < 1) {					
					sRetMsg = 'Please select at least one validation application, cannot save exception report without any application'
					
				} else if (nLimitNoOfApps < aAppIds.length) {
					sRetMsg = "Cannot select more than '{0}' validation applications in single exception report".replace('{0}', nLimitNoOfApps);
				}
				
				return sRetMsg;
			}
			
		},
		collapseDetailsClick: function(oEvent, lBeginEditingCall) {	
			var oTarget = null, sCollapseState = '';
			
			if (lBeginEditingCall) {
				sCollapseState = 'false';
				oTarget = $('#displayStaticData a')[0];
			} else {					
				oTarget = oEvent.target, 
				sCollapseState = $(oTarget).attr('data-collapsed');			
				sCollapseState = (sCollapseState === 'true') ? 'false' : 'true';
			}
			
			$(oTarget).attr('data-collapsed', sCollapseState);
			$(oTarget).html( (sCollapseState === 'true') ? '&#9660;' : '&#9650;' );				 

			if (sCollapseState === 'true') {
				$('#tblReportHeader').css('display','none');
				$('#divReportDetails').css('display','none');
				$('#tblDataButtons').css('display','none');
				$('#displayStaticData').css('border-bottom', '2px dotted grey');				
			} else {					
				$('#tblReportHeader').css('display','table');
				$('#divReportDetails').css('display','block');
				$('#tblDataButtons').css('display','table');
				$('#displayStaticData').css('border-bottom', '0px dotted grey');
			}
		}		
	}
	
	$(document).ready(function () {
		initializeJwfSpaInfra();	
		ManageExceptionDataReport.setup();		
		ManageExceptionDataReport.loadExceptionReportViewList();
	});
</script>