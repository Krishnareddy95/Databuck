<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />

<style>
	#tblPrimaryKeyMatching tr td input[type=checkbox] {
		width: 18px;
		height: 18px;
	}
	
	#tblPrimaryKeyMatching tr td select {
		width: 180px;
		height: 25px;
	}
	
	#tblPrimaryKeyMatching tr td input[type=text]:focus !important {
		width: 180px;
		height: 25px;	
	}	

	#divConfigVisual, #divConfigCode {
		margin: auto;
		padding: 10px;
		min-height: 200px;
		border-top: 2px solid var(--label-tabs-color);
		border-bottom: 2px solid var(--label-tabs-color);
	}

	.divConfigTabBar > label {
		/*font-family: "Helvetica Neue", "Arial", sans-serif !important; "Open Sans",sans-serif;*/
		font-family: "Open Sans",sans-serif !important;
		font-size: 12.5px !important;
		font-weight: bold;
		letter-spacing: 1px !important;
	}
	
	/*		
		font-family: Tahoma, sans-serif;
		font-size: 14px !important;
		font-weight: bold;
		letter-spacing: 1px !important;	
	*/
	
	#BtnSave {
		font-size: 12px !important;
	}

	#lblConfigVisual {
		margin-left: 5px;
	}	
	
	#divConfigCode textarea {
		font-family: monospace !important;
		font-size: 13.5px !important;
		padding: 5px !important;
		border-radius: 5px !important;
		text-align: left !important;
		min-height: 175px !important;
		max-height: 175px !important;
		overflow-x: hidden;
		overflow-y: audo;
	}
</style>
<script>
	console.log('script tag to console');
</script>

<div class="page-content-wrapper">
	<div class="page-content">
		<div class="row">
			<div class="col-md-6" style="width: 100%;">
				<div class="portlet light bordered init">
					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold "> Create Validation Check (Primary Key Matching) </span>
						</div>
					</div>
					<div class="portlet-body form">
						<input type="hidden" id="idApp" name="idApp" value="${idApp}" />
						<input type="hidden" id="idData" name="idData" value="${idData}" />
						<input type="hidden" id="apptype" name="apptype"
							value="${apptype}" /> <input type="hidden" id="applicationName"
							name="applicationName" value="${applicationName}" /> <input
							type="hidden" id="description" name="description"
							value="${description}" />
						<div class="form-body">
							<div class="row">
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											id="appnameid" name="dataset" value="${name }" readonly /> <label
											for="form_control_1">Validation Check Name *</label>
									</div>
									<br /> <span class="required"></span>
								</div>
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control" id="descriptionid"
											name="description" placeholder="Enter your description"
											value="${description }" readonly /> <label
											for="form_control">Description</label>
									</div>
								</div>
							</div>

							<div class="col-md-6 col-capture">
								<div class="form-group form-md-line-input">
									<input type="text" class="form-control" id="SelectLeftDataTmpl"
										name="description" value="${idData}_${applicationName}" readonly /> <label
										for="form_control">Left Source Template </label>
								</div>
							</div>

							<div class="col-md-6 col-capture">
								<div class="form-group form-md-line-input">
									<select class="form-control" id="SelectRightDataTmpl"
										name="rightsource" placeholder="">
										<option value="-1">Select Right Source Template</option>
										<c:forEach var="getlistdatasourcesnameobj"
											items="${getlistdatasourcesname}">
											<option value="${getlistdatasourcesnameobj.idData}">${getlistdatasourcesnameobj.idData}_${getlistdatasourcesnameobj.name}</option>
										</c:forEach>
									</select> <label for="form_control_1">Right Source Template *</label>
								</div>
								<br /> <span class="required"></span>
							</div>
						</div>

											
					<div id="firstSource" class="hidden ">
						<div class="alert alert-warning">
						<h5 style="color:red" id="jsonmsgforderivedcols"></h5>
							<h5 style="color:red">
								&nbsp&nbsp&nbsp&nbsp Please make sure that you have selected atleast one dgroup from the selected data template. 
								<a onClick="validateHrefVulnerability(this)" 	href="dataSourceDisplayAllView?idData=${idData}"
									target="_blank">Click here</a> to make the changes.
							</h5>
						</div>
						</div>
						<div id="secondSource" class="hidden ">
						<div class="alert alert-warning">
						<h5 style="color:red" id="jsonmsgforderivedcols"></h5>
							<h5 style="color:red">
								&nbsp&nbsp&nbsp&nbsp Please make sure that you have selected atleast one dgroup from the selected data template. 
								<a onClick="validateHrefVulnerability(this)" 	href="dataSourceDisplayAllView?idData=${idData}"
									target="_blank" id="myLink">Click here</a> to make the changes.
							</h5>
						</div>
						</div>

						<br><br><br>
						
						<div id='divPrimaryKeyMatching' style='display: none'>
							<div id='divConfigBody'>
								<div id='divConfigTabBar' class='jwf-label-tabs-bar divConfigTabBar'>
									<label id='lblConfigVisual' data-selected='true'>Configure Matching Criteria</label>
									<label id='lblConfigCode' data-selected='false'>Preview Generated Code</label>
								</div>
								<div id='divConfigVisual' style='display: block;'>
								    <table id="tblPrimaryKeyMatching" class="table table-striped table-bordered table-hover" width="100%">
										<thead>
										  	<tr>
												 <th><input type="checkbox" style="width: 17px; height: 17px;padding-left: -10px;" id="selectPrimaryCheckbox"> Select Primary Key</th>
												 <th><input type="checkbox" style="width: 17px; height: 17px;padding-left: -10px;" id="selectMatchCheckbox"> Select Match Field</th>
												 <th>Left Column</th>
												 <th>Left Type</th>
												 <th>Left Customize</th>									 
												 <th>Left Expression</th>
												 <th>Right Column</th>
												 <th>Right Customize</th>									 
												 <th>Right Expression</th>
											</tr>
										</thead>
									</table>
								</div>
								<div id='divConfigCode' style='display: none;'>
									<div class="caption font-red-sunglo">
										<span class="caption-subject bold ">Please Check below Generated Matching Criteria Before Saving</span>
									</div>							
									<br>
									<label>Primary Key Columns &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <i>(Hint: Fields from 'Select Primary Key' will be used to join left and right side datasets)</i></label>
									<textarea id='textareaPrimaryKeyCriteria' style='width: 100%' disabled></textarea>
									<br><br>
									<label>Match Value Columns  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <i>(Hint: Fields from 'Select Match Field' are used to compare values from left and right datasets)</i></label>
									<textarea id='textareaMatchValueCriteria' style='width: 100%' disabled></textarea>									
								</div>
							</div>
							
							<div class="form-actions noborder align-center tab-one-save">
								<button id="BtnSave" class="btn btn-primary DataBtns">Save Match Criteria</button>
							</div>							
						</div>					
						
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<jsp:include page="footer.jsp" />

<head>
	<script src="./assets/global/plugins/jquery.min.js" type="text/javascript"> </script>
	<script src="./assets/global/plugins/jquery.dataTables.min.js" type="text/javascript"></script>

	<link rel="stylesheet" href="./assets/jwf/content/JwfSpaInfra.css">
	<script src="./assets/jwf/scripts/JwfSpaInfra.js"></script>
</head>

<script>
    $('#selectPrimaryCheckbox').change(function(e){
         if($('#selectPrimaryCheckbox').is(':checked'))
        {
          $('#tblPrimaryKeyMatching tr td:first-child').find(':checkbox').prop('checked', true);
          $('#tblPrimaryKeyMatching tr td:first-child').find(':checkbox').trigger('change');
          $('#selectMatchCheckbox').prop('checked', false);
          $('#tblPrimaryKeyMatching tr td:nth-child(2)').find(':checkbox').prop('disabled',true);
          $('#tblPrimaryKeyMatching tr td:first-child').find(':checkbox').prop('disabled',false);
        }
         else
         {
           $('#tblPrimaryKeyMatching tr td:first-child').find(':checkbox').prop('checked', false);
           $('#tblPrimaryKeyMatching tr td:first-child').find(':checkbox').prop('disabled',false);
           $('#tblPrimaryKeyMatching tr td:nth-child(2)').find(':checkbox').prop('disabled',false);
         }
    });
    $('#selectMatchCheckbox').change(function(e){
         if($('#selectMatchCheckbox').is(':checked'))
         {
           $('#tblPrimaryKeyMatching tr td:nth-child(2)').find(':checkbox').prop('checked', true);
           $('#tblPrimaryKeyMatching tr td:nth-child(2)').find(':checkbox').trigger('change');
           $('#selectPrimaryCheckbox').prop('checked', false);
           $('#tblPrimaryKeyMatching tr td:first-child').find(':checkbox').prop('disabled',true);
           $('#tblPrimaryKeyMatching tr td:nth-child(2)').find(':checkbox').prop('disabled',false);
         }
         else
         {
          $('#tblPrimaryKeyMatching tr td:nth-child(2)').find(':checkbox').prop('checked', false);
          $('#tblPrimaryKeyMatching tr td:nth-child(2)').find(':checkbox').prop('disabled',false);
          $('#tblPrimaryKeyMatching tr td:first-child').find(':checkbox').prop('disabled',false);
         }

   });

	var ManagePrimaryMatchingValidation = {
		PageData: { 
			RightDataReloaded: true,
			IdApp: "${idApp}",
			LeftDataTmplId : "${idData}",
			RightDataTmplId : "",
			LeftDataSet: [],
			RightDataSet: [],
			ActiveColumnRow: {}
		},
		setup: function() {
			$('#SelectRightDataTmpl').on('change',ManagePrimaryMatchingValidation.mainEventHandler);
			$('#BtnSave').on('click',ManagePrimaryMatchingValidation.mainEventHandler);
			
			document.documentElement.style.setProperty('--label-tabs-color', '#808080');
			
			UIManager.LabelTabs.create({
				TabsId: 'divConfigTabBar',
				TabsPairInfo:
						[
							{ "TabLabel": "lblConfigVisual", "ContentDivId": "divConfigVisual" },
							{ "TabLabel": "lblConfigCode", "ContentDivId": "divConfigCode" }
						],
				TabsMinWidth: 150
			});			
		},
		fillRightFieldDropDowns: function() {
			var aRightOptionData = ManagePrimaryMatchingValidation.PageData.RightDataSet.map( function(oRightColumn) { 
				return { text: oRightColumn.RightColumnName + '  (' + oRightColumn.RightColumnType + ')', value: oRightColumn.RightColumnId + '-' + oRightColumn.RightColumnName }
			});
			
			ManagePrimaryMatchingValidation.PageData.LeftDataSet.forEach( function(oLeftColumnData, nIndex) {
				var oSelectObject = document.getElementById('RightColumnNameAndType-' + oLeftColumnData.LeftPrimaryColumnId), sRightColumnIdAndName = '';
				
				if (oSelectObject) { 
					if (oLeftColumnData.RightColumnIdAndName.length < 1) {
						sRightColumnIdAndName = getMatchingRightColumn(aRightOptionData, oLeftColumnData.LeftColumnName);					
						oLeftColumnData.RightColumnIdAndName = sRightColumnIdAndName;
					} else {
						sRightColumnIdAndName = oLeftColumnData.RightColumnIdAndName;
					}						
					UIManager.fillSelectRaw(oSelectObject, aRightOptionData, sRightColumnIdAndName); 
				}		
				
			});
			
			function getMatchingRightColumn(aRightOptionData, sLeftColumnName) {
				var lMatchFound = false, sRightColumnName = '', sRetValue = '';
				aRightOptionData.some( function(oRightOptionData, nIndex) {

					sRightColumnName = oRightOptionData.value.split('-')[1];
			         sRightColumnName=sRightColumnName.toLowerCase();
			         sLeftColumnName=sLeftColumnName.toLowerCase();
			         if (sRightColumnName === sLeftColumnName) {
                        sRetValue = oRightOptionData.value;
                        lMatchFound = true;
                     }
                     else{
                          if(sRightColumnName.indexOf("_") >= 0){
                           sRightColumnName=sRightColumnName.split("_").pop();
                          }
                          if(sLeftColumnName.indexOf("_") >= 0){
                           sLeftColumnName=sLeftColumnName.split("_").pop();
                          }
                          if (sRightColumnName === sLeftColumnName) {
                            sRetValue = oRightOptionData.value;
                            lMatchFound = true;
                          }
                     }

					return lMatchFound;
				});

				return sRetValue;				
			}
		},
		mainEventHandler: function(oEvent) {
			var sTargetId = oEvent.target.id, sTargetTagName = oEvent.target.tagName, sTargetType = oEvent.target.type;
			var sEventCategory = '', sContextKey = '', sEventContext = '', sDataField = 'LeftPrimaryColumnId', sDataValue = '', oRecordToUpdate = {}; 
			var oContextMatrix = { 
					"server-flow": { "BtnSave" : "SaveMatchConfiguration", "SelectRightDataTmpl" : "LoadMatchConfiguration" }, 					
					"client-flow": { 
							/* All checkbox events */
							"LeftPrimaryColumnId": "selectPrimaryCheckBoxClicked",  
							"LeftValueColumnId": "selectLeftValueCheckBoxClicked", 
							"LeftCustomize": "customizeLeftCheckBoxClicked", 
							"RightCustomize": "customizeRightCheckBoxClicked",
							
							/* Select right side column */
							"RightColumnNameAndType": "selectRightColumnChanged",
							
							/* Customize text boxes */
							"LeftExprTextBox": "changeLeftExprTextBox", 
							"RightExprTextBox": "changeRightExprTextBox"							
					}
				};


			if ( (sTargetId === 'BtnSave') || (sTargetId === 'SelectRightDataTmpl') ) {
				sEventCategory = 'server-flow';
				sContextKey = sTargetId;
				
			} else {
				sEventCategory = 'client-flow';
				
				if (sTargetType === 'checkbox') {				
					sContextKey = sTargetId.split('-')[0];
					sDataValue = sTargetId.split('-')[2];
					
				} else if (sTargetType === 'select-one') {
					sContextKey = sTargetId.split('-')[0];
					sDataValue = sTargetId.split('-')[1];
					
				} else if (sTargetType === 'text') {
					sContextKey = sTargetId.split('-')[0];
					sDataValue = sTargetId.split('-')[1];
						
				}			
			}
			sEventContext = oContextMatrix[sEventCategory][sContextKey];
			oRecordToUpdate = ManagePrimaryMatchingValidation.getRecordToUpdate(sDataField, sDataValue); 
			
			PageCtrl.debug.log('mainEventHandler 01', sTargetId + ',' + sTargetTagName + ',' + sTargetType + ',' + sEventCategory + ',' + sEventContext);
		
			switch (sEventContext) {
				case 'selectPrimaryCheckBoxClicked':
					ManagePrimaryMatchingValidation.selectPrimaryCheckBoxClicked(oRecordToUpdate, oEvent.target);
					break;

				case 'selectLeftValueCheckBoxClicked':
					ManagePrimaryMatchingValidation.selectLeftValueCheckBoxClicked(oRecordToUpdate, oEvent.target);
					break;				

				case 'customizeLeftCheckBoxClicked':
					ManagePrimaryMatchingValidation.customizeLeftCheckBoxClicked(oRecordToUpdate, oEvent.target);
					break;				

				case 'customizeRightCheckBoxClicked':
					ManagePrimaryMatchingValidation.customizeRightCheckBoxClicked(oRecordToUpdate, oEvent.target);
					break;

				case 'selectRightColumnChanged':
					ManagePrimaryMatchingValidation.selectRightColumnChanged(oRecordToUpdate, oEvent.target);
					break;					

				case 'selectRightColumnChanged':
					ManagePrimaryMatchingValidation.selectRightColumnChanged(oRecordToUpdate, oEvent.target);
					break;					

				case 'changeLeftExprTextBox':
					ManagePrimaryMatchingValidation.changeLeftExprTextBox(oRecordToUpdate, oEvent.target);
					break;					
					
				case 'changeRightExprTextBox':
					ManagePrimaryMatchingValidation.changeRightExprTextBox(oRecordToUpdate, oEvent.target);
					break;
					
				case 'SaveMatchConfiguration': case 'LoadMatchConfiguration':
					ManagePrimaryMatchingValidation.PageData.RightDataTmplId = $(oEvent.target).val();
					ManagePrimaryMatchingValidation.mainServerHandler(sEventContext);
					break;
				default:					
			}
			
			if (sEventCategory === 'client-flow') { ManagePrimaryMatchingValidation.refreshPreviewTab(); }
			//PageCtrl.debug.log('mainEventHandler 02', oRecordToUpdate);
			
		},
		getRecordToUpdate: function(sDataField, sDataValue) {
			var nRecordIndex = UTIL.indexOfRecord(ManagePrimaryMatchingValidation.PageData.LeftDataSet, sDataField, sDataValue), oRecordToUpdate = {};			
			
			if (nRecordIndex > -1) { oRecordToUpdate = ManagePrimaryMatchingValidation.PageData.LeftDataSet[nRecordIndex]; }
			return oRecordToUpdate;
		},
		selectPrimaryCheckBoxClicked: function(oRecordToUpdate, oCheckBox) {
			var lCheckBoxState = $(oCheckBox).prop('checked') ? true : false;
			
			oRecordToUpdate.IsLeftColumnPrimaryField = lCheckBoxState;
			
			oRecordToUpdate.IsLeftColumnValueField = false;
			$('#LeftValueColumnId-check-' + oRecordToUpdate.LeftPrimaryColumnId).prop('checked', false);			
			$('#LeftValueColumnId-check-' + oRecordToUpdate.LeftPrimaryColumnId).prop('disabled', lCheckBoxState);			
		},
		selectLeftValueCheckBoxClicked: function(oRecordToUpdate, oCheckBox) {
			var lCheckBoxState = $(oCheckBox).prop('checked') ? true : false;
			
			oRecordToUpdate.IsLeftColumnValueField = lCheckBoxState;
			
			oRecordToUpdate.IsLeftColumnPrimaryField = false;
			$('#LeftPrimaryColumnId-check-' + oRecordToUpdate.LeftPrimaryColumnId).prop('checked', false);
			$('#LeftPrimaryColumnId-check-' + oRecordToUpdate.LeftPrimaryColumnId).prop('disabled', lCheckBoxState);
		},
		customizeLeftCheckBoxClicked: function(oRecordToUpdate, oCheckBox) {
			var lCheckBoxState = $(oCheckBox).prop('checked') ? true : false;
			oRecordToUpdate.IsLeftColumnCustomized = lCheckBoxState;
			
			$('#LeftExprTextBox-' + oRecordToUpdate.LeftPrimaryColumnId).prop('disabled', !lCheckBoxState);		
			if (!lCheckBoxState) { 
				oRecordToUpdate.LeftColumnCustomizedExpr = ''; 
				$('#LeftExprTextBox-' + oRecordToUpdate.LeftPrimaryColumnId).val(oRecordToUpdate.LeftColumnCustomizedExpr);
			}
		},
		customizeRightCheckBoxClicked: function(oRecordToUpdate, oCheckBox) {
			var lCheckBoxState = $(oCheckBox).prop('checked') ? true : false;
			oRecordToUpdate.IsRightColumnCustomized = lCheckBoxState;
			
			$('#RightExprTextBox-' + oRecordToUpdate.LeftPrimaryColumnId).prop('disabled', !lCheckBoxState);
			if (!lCheckBoxState) { 
				oRecordToUpdate.RightColumnCustomizedExpr = ''; 
				$('#RightExprTextBox-' + oRecordToUpdate.LeftPrimaryColumnId).val(oRecordToUpdate.RightColumnCustomizedExpr);							
			}
		},
		selectRightColumnChanged: function(oRecordToUpdate, oSelectControl) {
			var sSelectedValue = $(oSelectControl).val(), lIsNoValueSelected = (sSelectedValue === '-1') ? true : false;
			oRecordToUpdate.RightColumnIdAndName = sSelectedValue;
			
			/* Reset linked JSON values */
			oRecordToUpdate.IsLeftColumnPrimaryField = false;
			oRecordToUpdate.IsLeftColumnValueField = false;
			oRecordToUpdate.IsLeftColumnCustomized = false;
			oRecordToUpdate.IsRightColumnCustomized = false;
			
			oRecordToUpdate.LeftColumnCustomizedExpr = '';
			oRecordToUpdate.RightColumnCustomizedExpr = '';
			
			/* Reset control values linked controls state */
			$('#LeftPrimaryColumnId-check-' + oRecordToUpdate.LeftPrimaryColumnId).prop('checked', oRecordToUpdate.IsLeftColumnPrimaryField);
			$('#LeftValueColumnId-check-' + oRecordToUpdate.LeftPrimaryColumnId).prop('checked', oRecordToUpdate.IsLeftColumnValueField);
			$('#LeftCustomize-check-' + oRecordToUpdate.LeftPrimaryColumnId).prop('checked', oRecordToUpdate.IsLeftColumnCustomized);
			$('#RightCustomize-check-' + oRecordToUpdate.LeftPrimaryColumnId).prop('checked', oRecordToUpdate.IsRightColumnCustomized);
			
			$('#LeftExprTextBox-' + oRecordToUpdate.LeftPrimaryColumnId).val(oRecordToUpdate.LeftColumnCustomizedExpr);
			$('#RightExprTextBox-' + oRecordToUpdate.LeftPrimaryColumnId).val(oRecordToUpdate.RightColumnCustomizedExpr);			
		
			/* Reset Enable/Disable linked controls state */
			$('#LeftPrimaryColumnId-check-' + oRecordToUpdate.LeftPrimaryColumnId).prop('disabled', lIsNoValueSelected);
			$('#LeftValueColumnId-check-' + oRecordToUpdate.LeftPrimaryColumnId).prop('disabled', lIsNoValueSelected);
			$('#LeftCustomize-check-' + oRecordToUpdate.LeftPrimaryColumnId).prop('disabled', lIsNoValueSelected);
			$('#RightCustomize-check-' + oRecordToUpdate.LeftPrimaryColumnId).prop('disabled', lIsNoValueSelected);
			
			$('#LeftExprTextBox-' + oRecordToUpdate.LeftPrimaryColumnId).prop('disabled', true);			
			$('#RightExprTextBox-' + oRecordToUpdate.LeftPrimaryColumnId).prop('disabled', true);			
			
			PageCtrl.debug.log('selectRightColumnChanged', sSelectedValue + ',' + lIsNoValueSelected);
			
		},
		changeLeftExprTextBox: function(oRecordToUpdate, oTextkBox) {
			var sSelectedValue = $(oTextkBox).val();
			oRecordToUpdate.LeftColumnCustomizedExpr = sSelectedValue;			
		},
		changeRightExprTextBox: function(oRecordToUpdate, oTextkBox) {
			var sSelectedValue = $(oTextkBox).val();
			oRecordToUpdate.RightColumnCustomizedExpr = sSelectedValue;
		},
		setInitialUiState: function() {			
			ManagePrimaryMatchingValidation.PageData.LeftDataSet.forEach( function(oLeftColumnData, nIndex) {
				var oSelectObject = document.getElementById('RightColumnNameAndType-' + oLeftColumnData.LeftPrimaryColumnId), sSelectedRightColumn = $(oSelectObject).val();
				
				if ( (oSelectObject) && (sSelectedRightColumn === '-1') ) { 
					$('#LeftPrimaryColumnId-check-' + oLeftColumnData.LeftPrimaryColumnId).prop('disabled', true);
					$('#LeftValueColumnId-check-' + oLeftColumnData.LeftPrimaryColumnId).prop('disabled', true);
					$('#LeftCustomize-check-' + oLeftColumnData.LeftPrimaryColumnId).prop('disabled', true);
					$('#RightCustomize-check-' + oLeftColumnData.LeftPrimaryColumnId).prop('disabled', true);
					$('#LeftExprTextBox-' + oLeftColumnData.LeftPrimaryColumnId).prop('disabled', true);
					$('#RightExprTextBox-' + oLeftColumnData.LeftPrimaryColumnId).prop('disabled', true);
				}
			});			
			
		},
		refreshPreviewTab: function() {
			const DEFAULT_PKC = 'No primary key criteria', DEFAULT_PKM = 'No match value criteria';
			var sPrimaryKeyCriteria = '', sMatchValueCriteria = '';				

			ManagePrimaryMatchingValidation.PageData.LeftDataSet.forEach( function(oCriteriaData, nIndex) {
				if (oCriteriaData.IsLeftColumnPrimaryField) {
					sPrimaryKeyCriteria = sPrimaryKeyCriteria + getSingleCriteria(oCriteriaData);

				} else if (oCriteriaData.IsLeftColumnValueField) {
					sMatchValueCriteria = sMatchValueCriteria + getSingleCriteria(oCriteriaData);
				}				
			});

			sPrimaryKeyCriteria = (sPrimaryKeyCriteria.length === 0) ? DEFAULT_PKC : sPrimaryKeyCriteria; 
			sMatchValueCriteria = (sMatchValueCriteria.length === 0) ? DEFAULT_PKM : sMatchValueCriteria; 

			PageCtrl.debug.log('refreshPreviewTab', DEFAULT_PKC + ',' + DEFAULT_PKM);

			$('#textareaPrimaryKeyCriteria').val(sPrimaryKeyCriteria);
			$('#textareaMatchValueCriteria').val(sMatchValueCriteria);

			function getSingleCriteria(oCriteriaData) {
				var sRetValue = '', sLeftPart = '', sRightPart = '';
				
				sLeftPart = (oCriteriaData.IsLeftColumnCustomized) ? oCriteriaData.LeftColumnCustomizedExpr : oCriteriaData.LeftColumnName;
				sRightPart = (oCriteriaData.IsRightColumnCustomized) ? oCriteriaData.RightColumnCustomizedExpr : oCriteriaData.RightColumnIdAndName.split('-')[1];
				
				sLeftPart = (sLeftPart.trim().length === 0) ? 'Not Specified' : sLeftPart.trim();
				sRightPart = (sRightPart.trim().length === 0) ? 'Not Specified': sRightPart.trim();
				
				sRetValue = sLeftPart + ' = ' + sRightPart + '\n';				
			
				return sRetValue;
			}
		},
		mainServerHandler: function(sEventContext) {
			var oWrapperData = {}, sWaitMsg = '', lCallServer = true,
				sIncompleteData = 'Select at least one primary key and one match value criteria';
			
			
			if (sEventContext === 'LoadMatchConfiguration') {			
				sWaitMsg = 'Loading Template Columns Data';
				oWrapperData = { 
						"Context": sEventContext, 
						"Data": { "LeftDataTmplId" : ManagePrimaryMatchingValidation.PageData.LeftDataTmplId, "RightDataTmplId": ManagePrimaryMatchingValidation.PageData.RightDataTmplId } 
					};
			} else if (!IsDataReadyToSave()) {
				lCallServer = false;
				Modal.confirmDialog(0, 'Incomplete Criteria',sIncompleteData, ['Ok'] );
				
			} else {
				
				if(!checkInputText()){ 
					lCallServer = false;
					alert('Vulnerability in submitted data, not allowed to submit!');
				}
				else{
					sWaitMsg = 'Saving Primary Key Matching Validation';
					oWrapperData = { 
							"Context": sEventContext, 
							"IdApp" : ManagePrimaryMatchingValidation.PageData.IdApp, 
							"RightDataTmplId": $('#SelectRightDataTmpl').val(),
							"Data": ManagePrimaryMatchingValidation.PageData.LeftDataSet					
						};	
				}
			}
			
			PageCtrl.debug.log('mainServerHandler 01', oWrapperData.Context);
			
			if (lCallServer) {
				JwfAjaxWrapper({
					WaitMsg: sWaitMsg,
					Url: 'mainPrimaryMatchingHandler',
					Headers:$("#token").val(),
					Data: { sWrapperData: JSON.stringify(oWrapperData) },
					CallBackFunction: function(oResponse) {
						if (sEventContext === 'LoadMatchConfiguration') {
							handleLoadMatchConfiguration(oResponse);
						} else {
							handleSaveMatchConfiguration(oResponse);
						}
					}
				});
			}
			
			function IsDataReadyToSave() {
				var nPrimaryKeyCriteria = 0, nMatchValueCriteria = 0, lRetValue = false;
				
				ManagePrimaryMatchingValidation.PageData.LeftDataSet.forEach( function(oCriteriaData, nIndex) {				
					nPrimaryKeyCriteria = nPrimaryKeyCriteria + ( (oCriteriaData.IsLeftColumnPrimaryField) ? 1 : 0 );
					nMatchValueCriteria = nMatchValueCriteria + ( (oCriteriaData.IsLeftColumnValueField) ? 1 : 0 );
				});
				
				lRetValue = ( (nPrimaryKeyCriteria > 0) && (nMatchValueCriteria > 0) ) ? true : false;
				
				return lRetValue;
			}			
			
			function handleLoadMatchConfiguration(oResponse) {
				var sViewTable = '#tblPrimaryKeyMatching';
				
				ManagePrimaryMatchingValidation.PageData.LeftDataSet = oResponse.LeftDataSet;
				ManagePrimaryMatchingValidation.PageData.RightDataSet = oResponse.RightDataSet;

				$('#divPrimaryKeyMatching').css('display', 'block');
				ManagePrimaryMatchingValidation.RightDataReloaded = true;

				/* Convert string booleans to java script boolean values */				
				ManagePrimaryMatchingValidation.PageData.LeftDataSet.forEach( function(oLeftColumnData, nIndex) {
					oLeftColumnData.IsLeftColumnPrimaryField = false;
					oLeftColumnData.IsLeftColumnValueField = false;
					oLeftColumnData.IsLeftColumnCustomized = false;
					oLeftColumnData.IsRightColumnCustomized = false;				
				});				

				ManagePrimaryMatchingValidation.DataTable = $(sViewTable).DataTable ({
					"data": ManagePrimaryMatchingValidation.PageData.LeftDataSet,
					"columns" : [
						{ "data" : "LeftPrimaryColumnId-check" },
						{ "data" : "LeftValueColumnId-check" },
						{ "data" : "LeftColumnName" },
						{ "data" : "LeftColumnType" },
						{ "data" : "LeftCustomize-check" },							
						{ "data" : "LeftColumnExpr" },
						{ "data" : "RightColumnNameAndType" },
						{ "data" : "RightCustomize-check" },
						{ "data" : "RightColumnExpr" }
					],
					order: [[ 0, "asc" ]],
					orderClasses: false,
					sScrollX: '100%',
					destroy: true,
					drawCallback: function( oSettings ) {						
						ManagePrimaryMatchingValidation.fillRightFieldDropDowns();												
						ManagePrimaryMatchingValidation.setInitialUiState();						
						ManagePrimaryMatchingValidation.refreshPreviewTab();
						
						$(sViewTable + ' tr td :input').off('change', ManagePrimaryMatchingValidation.mainEventHandler).on('change', ManagePrimaryMatchingValidation.mainEventHandler);
						
						ManagePrimaryMatchingValidation.RightDataReloaded = false;
						var list1 =$.parseJSON(JSON.stringify(${jsonprimaryId}));
                            $.each(list1, function( index, value ) {
                               $('#LeftPrimaryColumnId-check-'+value).trigger('click');
                            });

				   }
				});				
			}
			
			function handleSaveMatchConfiguration(oResponse) {
				if (oResponse.Status) {
					//Modal.confirmDialog(0, "Match Criteria Successfully Saved",getTargetUrl(), ['Ok'] );
					alert("Match Criteria Successfully Saved");
					window.location = getTargetUrl() + 'validationCheck_View';
				} else {					
					Modal.confirmDialog(0, "Match Criteria Save Failed",oResponse.Msg, ['Ok'] );
				}
				
			function getTargetUrl() {
				var sRegExp = new RegExp(/^.*\//);
				return sRegExp.exec(window.location.href);
			}

			}
			
		}

	}

	$(document).ready(function () {

		initializeJwfSpaInfra();
		PageCtrl.debug.log("left source id", ManagePrimaryMatchingValidation.PageData.LeftDataTmplId);
		
		ManagePrimaryMatchingValidation.setup();


	});


</script>