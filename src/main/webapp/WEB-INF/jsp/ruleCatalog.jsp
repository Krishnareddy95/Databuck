<%@page import="com.databuck.service.RBACController"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<style>
 	.greenClass {background-color:#b4f3b4;}
  	.redClass {background-color:#f3c7c7;}
  	.blueClass {background-color:#87CEEB;}
  	
	#formNormalData {
		padding: 25px;
	}

	#formApprovalData {
		padding: 15px;
	}
	
	#formNewRuleData {
		padding: 25px;
	}
	
	#formNewRuleData select {
		width: 100% !important;
		height: 32px;
		margin: auto;
		font-size: 13.5px !important;
		font-family: inherit;
	}

	#formApprovalData label {
		height: 32px;
		font-size: 13.5px !important;
		margin-top: 10px !important;
		margin-bottom: -5px !important;
	}

	#formApprovalData select {
		width: 100% !important;
		height: 32px;
		margin: auto;
		font-size: 13.5px !important;
		font-family: inherit;
	}

	#formApprovalData textarea {
		width: 100% !important;
		min-height: 125px;
		margin: auto;
		font-size: 12.5px;
		font-family: "Lucida Console", Courier, monospace;
		border: 2px solid !important !important;
	}

	#tblRuleCatalogRecordList th {
		background: #f2f2f2;
		border-bottom: 2px solid #808080;
	}

	#BtnBackward {
		float: right;
	}


	tr#tblRuleCatalogRecordList
	{
	    background-color: #FB9090;
	}	
	
	.deactivated_feature {
		background-color: #bfbfbf !important;
		color: #4d4d4d;
	}
	
	button > span {
		text-decoration: none !important;
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
								style="float: left !important;">Rule Catalog for IdApp:
								${idApp}</span> 
						</div>
						<span class="caption-subject bold" style="float: right;">
							<button class="btn btn-primary" id="BtnBackward" data-fromMapping="${fromMapping}">Back</button>
						</span>
					</div>
					<div class="portlet-body "  >
						<button class="btn btn-primary" id="BtnApproveValidation">Approve Validation</button>
						<button class="btn btn-primary" id="BtnAddNewRule"> <i class="fa fa-plus-circle" style="color: white;" aria-hidden="true"></i> Add Rule</button>
						<span style="float: right; margin-bottom: 35px;">
							<a onClick="validateHrefVulnerability(this)"  href="downloadRuleCatalog?idApp=${idApp}">
							<i class="fa fa-download" aria-hidden="true" style="padding-right:5px"></i>Download Catalog</a>
						</span>
						
						<div id="val_approval_msg_div" class="portlet light bordered hidden" style="margin-top: 10px; background-color: mintcream;">
							<p><b>Current Validation Status: </b>"<span style="color:blue">Approved for Production</span>".</p>
							
							<p>All new changes will be stored in Staging. Staged changes must be approved to apply them in production.</p>
							   
							<p>Changes in Rules are highlighted with color coding.
							   <span style="padding-right:25px; padding-left:5px"><i class="fa fa-square" style="color:green;padding-right: 5px;"></i>New</span>
							   <span style="padding-right:25px"><i class="fa fa-square" style="color:red;padding-right: 5px;"></i>Missing/Deleted</span>
							   <span style="padding-right:25px"><i class="fa fa-square" style="color:blue;padding-right: 5px;"></i>Modified</span>
							</p>
						</div>
						
						<table id="tblRuleCatalogRecordList"  
							class="table table-striped table-bordered table-hover table-responsive">
							<thead>
								<tr>
									<th>Rule Staging Status</th>
									<th>Rule Reference</th>
									<th>Rule Category</th>
									<th>Rule Type</th>
									<th>Rule Name</th>
									<th>Column Name</th>
									<th>Custom/Global Rule Type</th>
									<th>Filter Condition</th>
									<th>Right Template Filter Condition</th>
									<th>Matching Rules</th>
									<th>Rule Expression</th>
									<th>Threshold</th>
									<th>Dimension</th>
									<th>Defect Code</th>
									<th>Rule Code</th>
									<th>Rule Description</th>
									<th>Tags</th>
									<th>Review Comments</th>
									<th>Reviewed By</th>
									<th>Reviewed Date</th>
								</tr>
							</thead>
						</table>
						<div id="divApprover" style='display: none; min-width: 550px;'>
							<fieldset id='formApprovalData' class="fieldset-row">	
								<label for="StatusCode">Status</label>
								<select id="StatusCode"></select>
								<br> <label for="Comments">Comments</label>
								<textarea rows="5" cols="7" id="Comments">${approvalComments}</textarea><br></br>								
							</fieldset>
						</div>
					</div>
				</div>	
						<div class="modal" id="myModal">
					<div class="modal-dialog"
						style="display: inline-block; position: fixed; top: 0; bottom: 0; left: 0; right: 0; height: 500px; margin: auto; overflow:auto;">
						<div class="modal-content">

							<!-- Modal Header -->
							<div class="modal-header">
								<h4 class="modal-title">Link tag to Rule</h4>
								<button type="button" class="close" data-dismiss="modal">&times;</button>
							</div>

							<!-- Modal body -->
							<div class="modal-body" id="newValues">
							<div><label>RuleId</label><span>  </span><input type=text disabled class= form-control id="ruleId" /></div>
							<br>
							<div><label>RuleName</label><span>  </span><input type=text disabled class= form-control id="ruleName" /></div>
							<br>
							<div class="form-group form-md-line-input">
									<select class="form-control text-left" style="text-align: left;" id="tagId" >
										<option value=-1>Select</option>
								     	<c:forEach var="tagObj" items="${tags}">
											<option value="${tagObj.tagId}">${tagObj.tagName}</option>
										</c:forEach>
									</select> 
									<label for="form_control_1" style="color: black;">Choose Tags for Rule *</label>
								</div>
							</div>
								
							<!-- Modal footer -->
							<div class="modal-footer">
								<button type="submit" class="btn btn-success" id="insertcall" onClick="addTagToRules()" text-align: center;>Submit</button>
								<button type="button" class="btn btn-danger" data-dismiss="modal">Cancel</button>
							</div>
							
						</div>
					</div>
				</div>		
			</div>
	</div>
	<div style='display: none;' class="collapsible">
		<fieldset id='formNormalData' class="">
             <div class='row' style="margin-bottom:10px" >
                <label class='col-md-3' style="text-align:right;" for="RuleCategory">Rule Category</label>
                <div class="col-md-9">
                    <input type="text" class=" form-control" style="width:100%;" id="RuleCategory" readonly="readonly"></input>
                </div>
            </div>
            <div class='row' style="margin-bottom:10px" >
                <label class='col-md-3' style="text-align:right;" for="RuleType">Rule Type</label>
                <div class="col-md-9">
                    <input type="text" class=" form-control" style="width:100%;" id="RuleType" readonly="readonly"></input>
                </div>
            </div>
			<div class='row' style="margin-bottom:10px" >
                <label class='col-md-3' style="text-align:right;" for="RuleName" >Rule Name</label>
                <div class="col-md-9">
                    <input type="text" class=" form-control" style="width:100%;" id="RuleName" readonly="readonly"></input>
                </div>
            </div>
            <div class='row hidden' style="margin-bottom:10px" id="custom_global_ruletype_Div">
                <label class='col-md-3' style="text-align:right;" for="CustomOrGlobalRuleType">Custom/Global Rule Type</label>
                <div class="col-md-9">
                    <input type="text" class=" form-control" style="width:100%;" id="CustomOrGlobalRuleType" readonly="readonly"></input>
                </div>
            </div>
            <div class='row' style="margin-bottom:10px" >
                <label class='col-md-3' style="text-align:right;" for="ColumnName">Column Name</label>
                <div class="col-md-9">
                    <input type="text" class=" form-control" style="width:100%;" id="ColumnName"></input>
                </div>
            </div>
            <div class='row hidden' style="margin-bottom:10px" id="filterConditionDiv">
                <label class='col-md-3' style="text-align:right;" for="FilterCondition">Filter Condition</label>
                <div class="col-md-9">
                    <input type="text" class=" form-control" style="width:100%;" id="filterCondition"></input>
                </div>
            </div>
            <div class='row hidden' style="margin-bottom:10px" id="rightTemplateFilterConditionDiv">
                <label class='col-md-3' style="text-align:right;" for="RightTemplateFilterCondition">Right Template Filter Condition</label>
                <div class="col-md-9">
                    <input type="text" class=" form-control" style="width:100%;" id="rightTemplateFilterCondition"></input>
                </div>
            </div>
             <div class='row hidden' style="margin-bottom:10px;" id="matchingRulesDiv">
                <label class='col-md-3' style="text-align:right;" for="MatchingRules">Matching Rules</label>
                <div class="col-md-9">
                    <input type="text" class=" form-control" style="width:100%;" id="MatchingRules" ></input>
                </div>
            </div>
             <div class='row hidden' style="margin-bottom:10px;" id="ruleExpressionDiv">
                <label class='col-md-3' style="text-align:right;" for="RuleExpression">Rule Expression</label>
                <div class="col-md-9">
                    <input type="text" class=" form-control" style="width:100%;" id="RuleExpression" ></input>
                </div>
            </div>
             <div class='row hidden' style="margin-bottom:10px;" id="patternDiv">
                <label class='col-md-3' style="text-align:right;" for="PatternExpression">Patterns List</label>
                <div class="col-md-9">
                       <table class="table" id="patternTable">
                         <thead>
                             <tr>
                              <th>Pattern</th>
                              <th>Percentage</th>
                              <th>&nbsp;</th>
                            </tr>
                        </thead>
                        <tbody>
                        </tbody>
                        </table>
                        <br/>
                        <button  class="btn-link" onclick="ManageRuleCatalogDataEntry.addRow('patternTable')" ><span class="label label-success label-sm" style="background-color: #81C182;;color: #ffffff">Add Row</span></button>
                </div>
            </div>

			<div class='row' style="margin-bottom:10px" id="ThresholdDiv">	
				<label class='col-md-3' style="text-align:right;" for="Threshold">Threshold</label>
				<div class="col-md-9"> 
					<input type="text" class=" form-control" style="width:100%;" id="Threshold"></input>
				</div>
			</div>	
			<div class='row' style="margin-bottom:10px" >	
	         <label class='col-md-3' style="text-align:right;" for="DimensionId">Assign Dimension</label>
	         <div class="col-md-9"> 
					<select class=" form-control" style="width:100%;" id="DimensionId"></select>
				</div>
			</div>			
			<div class='row' style="margin-bottom:10px" >	
	         <label class='col-md-3' style="text-align:right;" for="DefectCode">Assign Defect Code</label>
	         <div class="col-md-9"> 
					<select class=" form-control" style="width:100%;" id="DefectCode"></select>
				</div>
			</div>
			<div class='row mb-2' style="margin-bottom:10px">
				<label class='col-md-3' style="text-align:right;" for="RuleDescription">Rule Description</label>
				<div class="col-md-9"> 
        			<textarea class="col-md-9 form-control" style="width:100%;" rows="2" cols="10" id="RuleDescription"></textarea><br></br>
    			</div>
    		</div>
			<div class='row mb-2'>
				<label class='col-md-3' style="text-align:right;" for="ReviewComments">Review Comment</label>
				<div class="col-md-9"> 
        			<textarea class="col-md-9 form-control" style="width:100%;" rows="4" cols="10" id="ReviewComments"></textarea><br></br>
    			</div>
    		</div>
		</fieldset>
	</div>
	
	<div style='display: none;' class="collapsible">
		<fieldset id='formNewRuleData' class="">
			<div class='row' style="margin-bottom:10px" >	
	         	<label class='col-md-3' style="text-align:right;" for="newRuleType">Select RuleType</label>
	         	<div class="col-md-9"> 
					<select class=" form-control" style="width:100%;" id="newRuleType"></select>
				</div>
			</div>
			
			<div id="addRuleMsg" class='row hidden' style="margin-top: 20px;font-size: small;" ></div>	
		</fieldset>
	</div>
	
</div>

<script src="./assets/global/plugins/jquery.min.js"
	type="text/javascript"> </script>
<script
	src="./assets/global/plugins/jquery.dataTables.min.js"
	type="text/javascript"></script>

<head>
<link rel="stylesheet" href="./assets/jwf/content/JwfSpaInfra.css">
<script src="./assets/jwf/scripts/JwfSpaInfra.js"></script>
</head>
<jsp:include page="footer.jsp" />
<script>
function viewTags(ruleId,ruleName){
	$("#ruleId").val(ruleId);
	$("#ruleName").val(ruleName);
	$("#tagId").val(-1);
	$("#myModal").modal("show");   
}
</script>
<script type="text/javascript">
function addTagToRules(){
	
	 $('.input_error').remove();
	 var no_error = 1;
	   
  if ($("#tagId").val().length == 0 || $("#tagId").val() == -1) {
      $('<span class="input_error" style="font-size:12px;color:red">Please select Tag </span>')
          .insertAfter($('#tagId'));
      no_error = 0;
  }
	        
  if (no_error > 0) {

		$("#myModal").modal("hide");
		
		var globalRuleId = $("#ruleId").val();
		var tagId = $("#tagId").val();
		var idApp = ${idApp};
		var form_data = {
				ruleId : globalRuleId,
				tagId : tagId,
				idApp : idApp
		}
		$.ajax({
			url: 'linkDatabuckTagToRule',
			type: 'POST',
			headers: { 'token':$("#token").val()},
			datatype: 'json',
			data: form_data,
			success : function(response){
				var msg = response.message;
				var status = response.status;
				toastr.info(msg);
				// Reload the page
				setTimeout(function() {window.location.reload();}, 1000);
			} ,
			error: function (xhr, textStatus,errorThrown) {
				toastr.info("Unexpected error occurred");
				
				// Reload the page
				setTimeout(function() {window.location.reload();}, 1000);
			}
		});
	}
}
</script>

<script>
	var ManageRuleCatalogDataEntry = {
		DataSet: {},
		RecordInEditing: {},
		DefectDimensionList: {},		
		DialogBody: null,
		DialogBody2: null,
		NewRuleDialogBody: null,
		IdApp: ${idApp},
		isApprover: ${isApprover},
		isValidationProdApproved : ${isValidationProdApproved},
		isValidationActive : ${isValidationActive},
		
		isAlowReviewComments: function() {		
			return true;
		},
		setup: function() {	
			ManageRuleCatalogDataEntry.DialogBody = $('#formNormalData')[0];
			ManageRuleCatalogDataEntry.DialogBody2 = $('#formApprovalData')[0];
			ManageRuleCatalogDataEntry.NewRuleDialogBody = $('#formNewRuleData')[0];
			
			$('#BtnApproveValidation').on('click', function() { ManageRuleCatalogDataEntry.promoteApproveValidationDialog(); });
			
			$('#BtnAddNewRule').on('click', function() { ManageRuleCatalogDataEntry.promoteNewRuleDialog(); });
			
			$('#BtnApproveValidation').prop('disabled', (!(ManageRuleCatalogDataEntry.isApprover) || !(ManageRuleCatalogDataEntry.isValidationActive)));

			$('#DimensionId').on('change', function(oEvent) { 
				ManageRuleCatalogDataEntry.fillDefectsList(ManageRuleCatalogDataEntry.RecordInEditing);				
			});
			
			// If validation is approved for production, display the information div
			if(ManageRuleCatalogDataEntry.isValidationProdApproved){
				$("#val_approval_msg_div").removeClass("hidden");
			}
			
		},
		getDimensionDefectCodeList: function(){
			$.ajax({
				url : './getDimensionDefectCodeList',
				type : 'GET',
				datatype : 'json',
				success : function(response) {
					ManageRuleCatalogDataEntry.DefectDimensionList = JSON.parse(response).DefectDimensionList;
				},
				error : function() {

				}
			});			
		},
		loadValidationApprovalStatusList: function(){
			$.ajax({
				url : './getValidationApprovalStatus?idApp='+${idApp},
				type : 'GET',
				datatype : 'json',
				success : function(response) {
					var j_obj = $.parseJSON(response);
					
					var currentStatus = j_obj.validationStatus;
					var rcStatusList = j_obj.ruleCatalogStatusLists;
					
					// If validation is approved for production, load the approval status of staging
					if(ManageRuleCatalogDataEntry.isValidationProdApproved){
						currentStatus = j_obj.stagingCatalogStatus;
					}
					
					UIManager.fillSelect($('#StatusCode')[0], j_obj.ruleCatalogStatusLists, currentStatus.approvalStatusCode);  
				},
				error : function() {

				}
			});			
		},
		loadRuleCatalogRecordList: function(){
			JwfAjaxWrapper({
				WaitMsg: 'Loading Rule Catalog Records',
				Url: 'loadRuleCatalog',
				Headers:$("#token").val(),
				Data: {idApp: ${idApp}},
				CallBackFunction: function(oResponse) {
					var oApprovalRecord = {};
					var aDataColumns = [ 
							'rowId' ,'idApp' ,'laName' ,'ruleReference' ,'ruleCode' ,'defectCode' ,'ruleType' ,'customOrGlobalRuleType','ruleName' ,'columnName' ,'ruleCategory' ,'filterCondition','rightTemplateFilterCondition',
							'matchingRules' ,'ruleExpression' ,'customOrGlobalRuleId' ,'threshold' ,'dimensionId' ,'dimensionName' ,'agingCheckEnabled' ,
							'reviewComments' ,'reviewDate' ,'reviewBy' ,'activeFlag' ,'deltaType' ,'ruleDescription'                 
						];			
					ManageRuleCatalogDataEntry.DataSet = oResponse.DataSet;						
					ManageRuleCatalogDataEntry.DataColumns = aDataColumns;
					
					ManageRuleCatalogDataEntry.DataTable = $("#tblRuleCatalogRecordList").DataTable ({
						"data" : ManageRuleCatalogDataEntry.DataSet,
						"columns" : [
							{ "data" : "deltaType"	    },
							{ "data" : "ruleReference"	},
							{ "data" : "ruleCategory"	},
							{ "data" : "ruleType"		},
							{ "data" : "ruleName"		,
								"render": function(data, type, row, meta){
								  var ruleNo = row.rowId;
								  var deltaType = row.deltaType;
								  var agingEnabled = row.agingCheckEnabled;
								  
								  if(deltaType != "MISSING"){
									  data = data + '<br>';
						              data = data + '<button id="edit-'+ruleNo+'" title="edit" style="padding-left:0px;" class="btn-link" onclick="ManageRuleCatalogDataEntry.promoteEditDialog('+ruleNo+')"><i class="fa fa-edit"></i></button>';
						              data = data + '<button id="delete-'+ruleNo+'" title="delete" style="padding-left:0px;" class="btn-link" onclick="ManageRuleCatalogDataEntry.promoteRuleDeleteDialog('+ruleNo+')"><i style="color: red" class="fa fa-trash"></i></button>';
						         
						              if(agingEnabled === 'Y'){
						            	  data = data + '<button id="aging-"'+ruleNo+' class="btn-link" onclick="ManageRuleCatalogDataEntry.enableOrDisableAgingCheck('+ruleNo+')" ><span class="label label-success label-sm">Aging</span></button>';
							          } else {
							        	  data = data + '<button id="aging-"'+ruleNo+' class="btn-link" onclick="ManageRuleCatalogDataEntry.enableOrDisableAgingCheck('+ruleNo+')"><span class="label label-danger label-sm">Aging</span></button>'; 
							          }
							          
								  }
						    	  
						         return data;
					          }
							},
							{ "data" : "columnName"		},
							{ "data" : "customOrGlobalRuleType"	},
							{ "data" : "filterCondition"	},
							{ "data" : "rightTemplateFilterCondition"},
							{ "data" : "matchingRules"	},
							{ "data" : "ruleExpression" ,
								"render": function(data, type, row, meta){
									if(row.ruleType === "Default Pattern Check" && data != null){							
										 return data.replace("val:", "").replace(/per:/g," ").replace(/val:/g,"<br>");
									}else{
										 return data;
									}
								}
							},
							{ "data" : "threshold"   	},
							{ "data" : "dimensionName"	},
							{ "data" : "defectCode"		},
							{ "data" : "ruleCode"		},
							{ "data" : "ruleDescription"},
							{ "data" : "ruleTags",
						      "render": function (data, type, row, meta){  
							    if (type === 'display' && row.ruleTags != null) 
							    	
							      return "<span class='label label-success label-sm'>"+row.ruleTags.replaceAll(',','</span>&nbsp <span class="label label-success label-sm">')+"</span>&nbsp <a onClick=\"javascript:viewTags("+row.ruleReference+",\'"+row.ruleName+"\')\" class=\"fa fa-plus-circle\"></a>";
							    else 
						          return "<span class='label label-success label-sm'></span>&nbsp <a onClick=\"javascript:viewTags("+row.ruleReference+",\'"+row.ruleName+"\')\" class=\"fa fa-plus-circle\"></a>";
							   }
						    },
							{ "data" : "reviewComments" },
							{ "data" : "reviewBy"		},
							{ "data" : "reviewDate"		}
						],
						'createdRow': function(row, data, index){
						    if(data["deltaType"] === 'MISSING'){
						        $(row).find('td').addClass('redClass');
						    } 
						    else if (data["deltaType"] === 'NEW'){
						    	$(row).find('td').addClass('greenClass');
						    }
						    else if (data["deltaType"] === 'CHANGED'){
                                $(row).find('td').addClass('blueClass');
                            }
						 },
						order: [[ 1, "asc" ]],
						destroy: true,
						orderClasses: false,
						scrollX:true
					});				
				}
			});
		},
		enableOrDisableAgingCheck: function(sRowId) {
			JwfAjaxWrapper({
				WaitMsg: 'Updating Aging Check',
				NoWaitMsg: true,
				Url: 'enableOrDisableAgingCheck',
				Headers:$("#token").val(),
				Data: { 
						ruleCatalogRowId: sRowId ,
						idApp : ${idApp}
					},				
				CallBackFunction: function(oResponse) {				
					if (oResponse.Result) {
						toastr.info('Successfully updated AgingCheck');
						ManageRuleCatalogDataEntry.loadRuleCatalogRecordList();
					} else {					
						Modal.confirmDialog(3, 'AgingCheck update Failed', oResponse.Msg, ['Ok'] );
					}
				}				
			});
		},
		promoteRuleDeleteDialog: function(sRowId) {
			Modal.confirmDialog(1, 'Rule delete confirmation', 'Do you want to delete the rule?',
					['OK', 'Cancel'], { onDialogClose: cbPromoteApproveDeleteDialog, onDialogLoad: null });

			return;

			function cbPromoteApproveDeleteDialog(oEvent) {
				var sEventId = oEvent.EventId, sBtnSelected = oEvent.ClickedButton;

				switch(sEventId) {
					case jwfGlobal.EVT_MODAL_DIALOG_ONLOAD:
						PageCtrl.debug.log('on Dialog load','ok');
						break;
						
					case jwfGlobal.EVT_MODAL_DIALOG_ONCLOSE:
						if (sBtnSelected === 'OK') {  
							ManageRuleCatalogDataEntry.deleteRuleFromRuleCatalog(sRowId);
						}
						break;
				}
			}
		
		},
		deleteRow: function (childElem) {
		    var row = $(childElem).closest("tr"); // find <tr> parent
		    row.remove();
		},
		addRow: function (sTableId) {
			
			var table = document.getElementById(sTableId);
			
			var rowCount = table.rows.length;
			var row = table.insertRow(rowCount);
			row.id="rowData"+rowCount;
			
			var newcell	= row.insertCell(0);
		   	var elementText1 = document.createElement("input");
			elementText1.type = "text";
			elementText1.name = "txtbox"+rowCount+'0';
			newcell.appendChild(elementText1);
			newcell.classList.add("pattern");
			
			var newcell	= row.insertCell(1);
		  	var elementText2 = document.createElement("input");
			elementText2.type = "text";
			elementText2.name = "txtbox"+rowCount+'1';
			newcell.appendChild(elementText2);
			newcell.classList.add("percent");
			
			var newcell	= row.insertCell(2);
		    var elLink = document.createElement('a');
		    elLink.href = '#';
		    elLink.onclick = function() { ManageRuleCatalogDataEntry.deleteRow(this); };
		    elLink.title='Delete';
		    elLink.innerHTML = 'Delete';
			newcell.appendChild(elLink);
			
		},
		deleteRuleFromRuleCatalog: function(sRowId) {	
			
			var nRowIndex = UTIL.indexOfRecord(ManageRuleCatalogDataEntry.DataSet, 'rowId', sRowId);
			var oRuleCatalogRecord = ManageRuleCatalogDataEntry.DataSet[nRowIndex];
			
			JwfAjaxWrapper({
				WaitMsg: 'Deleting Rule from Rule Catalog',
				NoWaitMsg: false,
				Url: 'deleteRuleFromRuleCatalog',
				Headers:$("#token").val(),
				Data: { 
					ruleCatalogRecordToDelete: JSON.stringify(oRuleCatalogRecord),
				    idApp : ManageRuleCatalogDataEntry.IdApp 
				},				
				CallBackFunction: function(oResponse) {				
					if (oResponse.Result) {
						toastr.info('Rule deleted Successfully');
						window.location.reload();
					} else {					
						Modal.confirmDialog(3, 'Rule deletion Failed', oResponse.Msg, ['Ok'] , { onDialogClose: reloadRuleCatalogPage });
					}
					
					function reloadRuleCatalogPage(oEvent){
						window.location.reload(true);
					}
				}				
			});
		},
		SaveRuleCatalogStatus: function() {	
			
			if(!checkInputText()){
				alert('Vulnerability in submitted data, not allowed to submit!');
				return;
			}
			
			var oDataToSave = { StatusCode: $("#StatusCode").val(), Comments: $("#Comments").val()	};
			
			JwfAjaxWrapper({
				WaitMsg: 'Saving Approval Status',
				Url: 'SaveRuleCatalogStatus',
				Headers:$("#token").val(),
				Data: { idApp: ${idApp},  DataToSave: JSON.stringify(oDataToSave) },
				CallBackFunction: function(oResponse) {
					PageCtrl.debug.log("Save", oResponse);
					
					ManageRuleCatalogDataEntry.loadValidationApprovalStatusList;
					
					Modal.confirmDialog(0, 'Save Status', oResponse.Msg, ['Ok'], { onDialogClose: reloadRuleCatalogPage });
					
					function reloadRuleCatalogPage(oEvent){
						window.location.reload(true);
					}
				}
			});				
		},
		promoteApproveValidationDialog: function() {
			var ModeTitle =  'Approve Validation';
			var sDefaultMsg = 'Enter validation status';
			
			Modal.customDialog(1, ModeTitle,
					{ 'BodyDomObj': ManageRuleCatalogDataEntry.DialogBody2,'HeaderHtml': null },
					['Save', 'Cancel'], { onDialogClose: cbPromoteApproveValidationDialog, onDialogLoad: null });

			return;

			function cbPromoteApproveValidationDialog(oEvent) {
				var sEventId = oEvent.EventId, sBtnSelected = oEvent.ClickedButton;

				switch(sEventId) {
					case jwfGlobal.EVT_MODAL_DIALOG_ONLOAD:
						PageCtrl.debug.log('on Dialog load','ok');
						break;
						
					case jwfGlobal.EVT_MODAL_DIALOG_ONCLOSE:
						if (sBtnSelected === 'Save') {  
							ManageRuleCatalogDataEntry.SaveRuleCatalogStatus();
						}
						break;
				}
			}
		},
		promoteEditDialog: function(RowId) {			
			var ModeTitle =  'Edit Rule Features';
			
			var nRowIndex = UTIL.indexOfRecord(ManageRuleCatalogDataEntry.DataSet, 'rowId', RowId);
			var oRuleCatalogRecord = ManageRuleCatalogDataEntry.DataSet[nRowIndex];
			
			ManageRuleCatalogDataEntry.RecordInEditing = oRuleCatalogRecord;
			ManageRuleCatalogDataEntry.fillEditFormData(oRuleCatalogRecord);

			Modal.customDialog(1, ModeTitle,
					{ 'BodyDomObj': ManageRuleCatalogDataEntry.DialogBody,'HeaderHtml': null },
					['Save', 'Cancel'], { onDialogClose: cbPromoteEditDialog, onDialogLoad: null });

			return;

			function cbPromoteEditDialog(oEvent) {
				var sEventId = oEvent.EventId, sBtnSelected = oEvent.ClickedButton;

				switch(sEventId) {
					case jwfGlobal.EVT_MODAL_DIALOG_ONLOAD:
						PageCtrl.debug.log('on Dialog load','ok');
						break;
						
					case jwfGlobal.EVT_MODAL_DIALOG_ONCLOSE:
						$('.input_error').remove(); 
						
						if (sBtnSelected === 'Save') 
						{  
							// Validate request
							var status = ManageRuleCatalogDataEntry.validateEditDialogData(RowId);
							
							// Submit the data
							if(status){
								 ManageRuleCatalogDataEntry.submitEditDialogData(RowId);
							} else {
								return 'data_validation_failed';
							}
						}
						break;
				}
			}	
		},		
		fillEditFormData: function(oRuleCatalogRecord) {
            $('#Threshold').val(oRuleCatalogRecord.threshold);
            $('#Threshold').removeAttr('disabled');
			$('#ReviewComments').val(oRuleCatalogRecord.reviewComments);
			$('#RuleDescription').val(oRuleCatalogRecord.ruleDescription);
			$('#RuleName').val(oRuleCatalogRecord.ruleName);
			$('#ColumnName').val(oRuleCatalogRecord.columnName);
			$('#RuleType').val(oRuleCatalogRecord.ruleType);
			$('#CustomOrGlobalRuleType').val(oRuleCatalogRecord.customOrGlobalRuleType);
			$('#filterCondition').val(oRuleCatalogRecord.filterCondition);
			$('#rightTemplateFilterCondition').val(oRuleCatalogRecord.rightTemplateFilterCondition);
			$('#MatchingRules').val(oRuleCatalogRecord.matchingRules);
			$('#RuleExpression').val(oRuleCatalogRecord.ruleExpression);

			var ruleType = "" + oRuleCatalogRecord.ruleType;

			console.log("ruleType: " + ruleType);

			$('#ruleExpressionDiv').addClass('hidden');
			$('#matchingRulesDiv').addClass('hidden');
			$('#filterConditionDiv').addClass('hidden');
			$('#rightTemplateFilterConditionDiv').addClass('hidden');
			$('#custom_global_ruletype_Div').addClass('hidden');
			$('#ColumnName').attr('disabled', true);
			$('#patternDiv').addClass('hidden');

		    if(ruleType == "Date Rule Check" ){
		        $('#Threshold').attr('disabled','disabled');
		        $('<span class="input_error" style="font-size:12px;color:red">For Date Rule Check threshold is disabled</span>')
                  .insertAfter($('#Threshold'));
			}
		    else  if(ruleType == "Default Check" ){
		        $('#Threshold').attr('disabled','disabled');
		        $('<span class="input_error" style="font-size:12px;color:red">For Default Check threshold is disabled</span>')
                  .insertAfter($('#Threshold'));
			}
		    else  if(ruleType == "Timeliness Check" ){
		        $('#Threshold').attr('disabled','disabled');
		        $('<span class="input_error" style="font-size:12px;color:red">For Timeliness Check threshold is disabled</span>')
                  .insertAfter($('#Threshold'));
			}
		    // For custom rule
		    else if(ruleType == "Custom Rule"){

			  $('#ColumnName').attr('disabled', false);
			  $('#custom_global_ruletype_Div').removeClass('hidden');

			  var customOrGlobalRuleType = "" + oRuleCatalogRecord.customOrGlobalRuleType.toLowerCase();

			  // Edit of Rule Expression is allowed
			  if ((customOrGlobalRuleType == "referential") || (customOrGlobalRuleType == "regular expression")){
			     $('#ruleExpressionDiv').removeClass('hidden');
			     $('#matchingRulesDiv').addClass('hidden');
			     $('#filterConditionDiv').addClass('hidden');

              // Edit of Matching Rules is allowed
			  }else if (customOrGlobalRuleType == "orphan") {
				 $('#ruleExpressionDiv').addClass('hidden');
			     $('#matchingRulesDiv').removeClass('hidden');
			     $('#filterConditionDiv').addClass('hidden');

              // Edit of both Matching Rules and Rule Expression is allowed
              } else if (customOrGlobalRuleType == "cross referential") {
 				 $('#ruleExpressionDiv').removeClass('hidden');
			     $('#matchingRulesDiv').removeClass('hidden');
			     $('#filterConditionDiv').addClass('hidden');
              }
             
            }
		    // For Global rule
		    else if(ruleType == "Global Rule"){

				  $('#ColumnName').attr('disabled', true);
				  $('#custom_global_ruletype_Div').removeClass('hidden');

				  var customOrGlobalRuleType = "" + oRuleCatalogRecord.customOrGlobalRuleType.toLowerCase();

				  // Edit of Rule Expression is allowed
				  if ((customOrGlobalRuleType == "referential")){
				     $('#ruleExpressionDiv').removeClass('hidden');
				     $('#matchingRulesDiv').addClass('hidden');
				     $('#filterConditionDiv').addClass('hidden');
				     $('#RuleExpression').attr('disabled', true);

	              // Edit of Matching Rules is allowed
				  }else if (customOrGlobalRuleType == "orphan") {
					 $('#ruleExpressionDiv').addClass('hidden');
				     $('#matchingRulesDiv').removeClass('hidden');
				     $('#filterConditionDiv').addClass('hidden');
				     $('#MatchingRules').attr('disabled', true);
				     
				  // Edit of Matching Rules and Rule Expression is allowed
				  }else if (customOrGlobalRuleType == "cross referential") {
					 $('#ruleExpressionDiv').removeClass('hidden');
				     $('#matchingRulesDiv').removeClass('hidden');
				     $('#filterConditionDiv').addClass('hidden');
				     $('#MatchingRules').attr('disabled', true);
				     $('#RuleExpression').attr('disabled', true);

	              // Edit of both Filter Condition and Rule Expression is allowed
	              }else if (customOrGlobalRuleType == "conditional referential") {
	                 $('#ruleExpressionDiv').removeClass('hidden');
	                 $('#filterConditionDiv').removeClass('hidden');
	                 $('#matchingRulesDiv').addClass('hidden');
	                 $('#RuleExpression').attr('disabled', true);
	                 $('#filterCondition').attr('disabled', true);
	              }
	              // Edit of both Filter Condition and Matching Rules  is allowed
	              else if (customOrGlobalRuleType == "conditional orphan") {
	                $('#matchingRulesDiv').removeClass('hidden');
	                $('#filterConditionDiv').removeClass('hidden');
	                $('#rightTemplateFilterConditionDiv').removeClass('hidden');
	                $('#ruleExpressionDiv').addClass('hidden');
	                $('#MatchingRules').attr('disabled', true);
	                $('#filterCondition').attr('disabled', true);
	                $('#rightTemplateFilterCondition').attr('disabled', true);
	              }
				  // Edit of both Filter Condition and Rule Expression is allowed
	              else if (customOrGlobalRuleType == "conditional sql internal rule") {
	                $('#ruleExpressionDiv').removeClass('hidden');
	                $('#filterConditionDiv').removeClass('hidden');
	                $('#matchingRulesDiv').addClass('hidden');
	                $('#RuleExpression').attr('disabled', true);
	                $('#filterCondition').attr('disabled', true);
	              }
				  // Edit of both Filter Condition and Rule Expression is allowed
	              else if (customOrGlobalRuleType == "conditional duplicate check") {
	                $('#ruleExpressionDiv').removeClass('hidden');
	                $('#filterConditionDiv').removeClass('hidden');
	                $('#matchingRulesDiv').addClass('hidden');
	                $('#RuleExpression').attr('disabled', true);
	                $('#filterCondition').attr('disabled', true);
	              }
				// Edit of both Filter Condition and Rule Expression is allowed
	              else if (customOrGlobalRuleType == "conditional completeness check") {
	                $('#ruleExpressionDiv').removeClass('hidden');
	                $('#filterConditionDiv').removeClass('hidden');
	                $('#matchingRulesDiv').addClass('hidden');
	                $('#RuleExpression').attr('disabled', true);
	                $('#filterCondition').attr('disabled', true);
	              }
	        }
		    // Default pattern check
			else if(ruleType == "Default Pattern Check"){
				$('#patternDiv').removeClass('hidden');
				$('#Threshold').attr('disabled','disabled');
				if(oRuleCatalogRecord.ruleExpression != null && oRuleCatalogRecord.ruleExpression != ""){
				 var temp =  oRuleCatalogRecord.ruleExpression.replace("val:","").split(",val:");
			     var tbdy=document.createElement('TBODY');
			     for (var i=0;i<temp.length;i++){
			      var tempNew = temp[i].split("per:");
			      
			      var tr = tbdy.insertRow(i);
			      tr.id="mainData"+i;
			      var td1 = tr.insertCell(0);
			      var td2 = tr.insertCell(1);
			      var td3 = tr.insertCell(2);
			      
				  var elementText1 = document.createElement("input");
				  elementText1.type = "text";
				  elementText1.name = "txtbox"+i+"0";
				 
				  td1.appendChild(elementText1);
				  td1.innerHTML=tempNew[0];
				  td1.classList.add("pattern");
				  
				  var elementText2 = document.createElement("input");
				  elementText2.type = "text";
				  elementText2.name = "txtbox"+i+"1";
				  
				  td2.appendChild(elementText2);
				  td2.innerHTML=tempNew[1].replace("(","").replace(")","");
				  td2.classList.add("percent");
				  
			      var elLink = document.createElement('a');
			      var href='#';
			      elLink.href = href;
			      elLink.onclick = function() { ManageRuleCatalogDataEntry.deleteRow(this); };
			      elLink.title='Delete';
			      elLink.innerHTML = 'Delete';
			      td3.appendChild(elLink);
			    }
			     $("#patternTable tr>td").remove();
			     patternTable.appendChild(tbdy);
				}else{
				  $("#patternTable tr>td").remove();
				}
			}
            

            
			$('#RuleCategory').val(oRuleCatalogRecord.ruleCategory);

			/* Review comments cannot be added and edited once application ready or approved for test or export */
			$('#ReviewComments').prop('disabled', !ManageRuleCatalogDataEntry.isAlowReviewComments());
			
			ManageRuleCatalogDataEntry.fillDimensionsList(oRuleCatalogRecord);
			ManageRuleCatalogDataEntry.fillDefectsList(oRuleCatalogRecord);			
		},
		fillDimensionsList: function(oRuleCatalogRecord){
			var aDefectDimensionList = ManageRuleCatalogDataEntry.DefectDimensionList;
			var sDimensionId = oRuleCatalogRecord.dimensionId

			// Clear the dropdown
			$('#DimensionId').empty();
			
			// Default option
			$('#DimensionId').append($('<option>', {value : "-1",text : "Please select"}));
			
			aDefectDimensionList.forEach( function(data, nIndex) {
				var oOption = document.createElement('OPTION');				
				oOption.text = data.DimensionName;
				oOption.value = data.DimensionId;

				if (""+sDimensionId === ""+oOption.value) { oOption.selected = true; }
				$('#DimensionId')[0].add(oOption);
			});
		},
		fillDefectsList: function(oRuleCatalogRecord){
			var aDefectDimensionList = ManageRuleCatalogDataEntry.DefectDimensionList;
			var sDimensionId = $('#DimensionId').val();
			var sDefectCode = oRuleCatalogRecord.defectCode;
			var d_recordIndex = -1;
			
			// Get the index of selected dimension record
			aDefectDimensionList.forEach( function(oData, nIndex) {
				if(""+oData.DimensionId === ""+sDimensionId){
					d_recordIndex = nIndex;
				}
			});
			
			// Clear the dropdown
			$('#DefectCode').empty();
			
			// Default option
			$('#DefectCode').append($('<option>', {value : "-1",text : "Please select"}));
			
			if (d_recordIndex > -1) {
				var aDefectCodes = (aDefectDimensionList[d_recordIndex].DefectCodes.length > 0) ? aDefectDimensionList[d_recordIndex].DefectCodes.split(',') : [];
				
				aDefectCodes.forEach( function(data, nIndex) {
					var oOption = document.createElement('OPTION');				
					oOption.text = data;
					oOption.value = data;
					
					if (sDefectCode === oOption.value) { oOption.selected = true; }
					$('#DefectCode')[0].add(oOption);
				});
			}		
		},
		validateEditDialogData: function(RowId) {			

			var ruleType = ""+$('#RuleType').val();

			var isFormDataValid = true;
			
			// Validate Threshold
			var threshold = $('#Threshold').val();
			if(threshold.length == null || threshold.length == 0 || threshold < 0){
				isFormDataValid = false;
				$('<span class="input_error" style="font-size:12px;color:red">Please enter valid threshold value</span>')
				.insertAfter($('#Threshold'));
			}
			
			// Validate DimensionId
			var dimensionId = $('#DimensionId').val();
			if(dimensionId.length == null || dimensionId.length == 0 || dimensionId == -1){
				isFormDataValid = false;
				$('<span class="input_error" style="font-size:12px;color:red">Please select dimension</span>')
				.insertAfter($('#DimensionId'));
			}
			
			// Validate RuleExpression, Matching Rules and Column Name
			if(ruleType == "Custom Rule" || ruleType == "Global Rule"){
			 
			  var customOrGlobalRuleType = ""+$('#CustomOrGlobalRuleType').val().toLowerCase();

			  var ruleExpression = $('#RuleExpression').val();
			  var matchingRules = $('#MatchingRules').val();
			  var filterCondition = $('#filterCondition').val();
	          var columnName = $('#ColumnName').val();
	            
	          if (columnName.trim().length == 0) {
	        	  isFormDataValid = false;  
	        	  $('<span class="input_error" style="font-size:12px;color:red">Please enter comma separated anchor column names</span>')
					.insertAfter($('#ColumnName'));
	          } 
	          
			  if ((customOrGlobalRuleType == "referential") || (customOrGlobalRuleType == "regular expression")){
				  
				  if(ruleExpression.trim().length == 0){
					  isFormDataValid = false;  
					  $('<span class="input_error" style="font-size:12px;color:red">Please enter Rule Expression</span>')
						.insertAfter($('#RuleExpression'));
				  } 
              
			  }else if (customOrGlobalRuleType == "orphan") {
				  
				  if(matchingRules.trim().length == 0){
					  isFormDataValid = false;  
					  $('<span class="input_error" style="font-size:12px;color:red">Please enter Matching Rules</span>')
						.insertAfter($('#MatchingRules'));
				  } 
              
			  } else if (customOrGlobalRuleType == "cross referential") {
				  
				  if(ruleExpression.trim().length == 0){
					  isFormDataValid = false;  
					  $('<span class="input_error" style="font-size:12px;color:red">Please enter Rule Expression</span>')
						.insertAfter($('#RuleExpression'));
				  } 
				  
				  if(matchingRules.trim().length == 0){
					  isFormDataValid = false;  
					  $('<span class="input_error" style="font-size:12px;color:red">Please enter Matching Rules</span>')
						.insertAfter($('#MatchingRules'));
				  } 
              } else if (customOrGlobalRuleType == "conditional orphan") {
            	  
            	  if(filterCondition.trim().length == 0){
					  isFormDataValid = false;  
					  $('<span class="input_error" style="font-size:12px;color:red">Please enter Filter Condition</span>')
						.insertAfter($('#filterCondition'));
				  } 
				  
				  if(matchingRules.trim().length == 0){
					  isFormDataValid = false;  
					  $('<span class="input_error" style="font-size:12px;color:red">Please enter Matching Rules</span>')
						.insertAfter($('#MatchingRules'));
				  } 
              } else if ((customOrGlobalRuleType == "conditional referential") || (customOrGlobalRuleType == "conditional sql internal rule") || (customOrGlobalRuleType == "conditional duplicate check") || (customOrGlobalRuleType == "conditional completeness check")) {
            	  
            	  if(filterCondition.trim().length == 0){
					  isFormDataValid = false;  
					  $('<span class="input_error" style="font-size:12px;color:red">Please enter Filter Condition</span>')
						.insertAfter($('#filterCondition'));
				  } 
            	  
				  if(ruleExpression.trim().length == 0){
					  isFormDataValid = false;  
					  $('<span class="input_error" style="font-size:12px;color:red">Please enter Rule Expression</span>')
						.insertAfter($('#RuleExpression'));
				  } 
				  
              } 
            }
            
			if(isFormDataValid){
				return true;
			} else {
				return false;
			}

		},
		submitEditDialogData: function(RowId) {
			var oRecordInEditing = ManageRuleCatalogDataEntry.RecordInEditing;
			
			if(!checkInputText()){
				alert('Vulnerability in submitted data, not allowed to submit!');
				return;
			}
			
			oRecordInEditing.rowId = RowId;
			oRecordInEditing.threshold = $('#Threshold').val();
			oRecordInEditing.defectCode = $('#DefectCode').val();
			oRecordInEditing.dimensionId = $('#DimensionId').val();
			
			var isAlowReviewComments = 'No';
			if (ManageRuleCatalogDataEntry.isAlowReviewComments()) {
				isAlowReviewComments = 'Yes';
			} 
			
			oRecordInEditing.reviewComments = $('#ReviewComments').val();
			oRecordInEditing.isAlowReviewComments = isAlowReviewComments;
			
			oRecordInEditing.ruleDescription = $('#RuleDescription').val();

			var ruleType = ""+$('#RuleType').val();

			if(ruleType == "Custom Rule" || ruleType == "Global Rule"){
			 
				  var customOrGlobalRuleType = ""+$('#CustomOrGlobalRuleType').val().toLowerCase();
	
				  var ruleExpression = $('#RuleExpression').val();
				  var matchingRules = $('#MatchingRules').val();
		          var columnName = $('#ColumnName').val(); 
		            
		          oRecordInEditing.columnName = $('#ColumnName').val();        
		          
				  if ((customOrGlobalRuleType == "referential") || (customOrGlobalRuleType == "regular expression")){
						  oRecordInEditing.ruleExpression = ruleExpression; 
	              
				  }else if (customOrGlobalRuleType == "orphan") {	 
						  oRecordInEditing.matchingRules = matchingRules; 
	
				  } else if (customOrGlobalRuleType == "cross referential") {
					  oRecordInEditing.ruleExpression = ruleExpression; 
					  oRecordInEditing.matchingRules = matchingRules; 
					  
				  } else if (customOrGlobalRuleType == "conditional referential" || customOrGlobalRuleType == "conditional sql internal rule" || customOrGlobalRuleType == "conditional duplicate check" || customOrGlobalRuleType == "conditional completeness check") {
                          oRecordInEditing.filterCondition = $('#filterCondition').val();
                          oRecordInEditing.ruleExpression = ruleExpression;
                          
                  }else if (customOrGlobalRuleType == "conditional orphan"){
                          oRecordInEditing.filterCondition = $('#filterCondition').val();
                          oRecordInEditing.matchingRules = matchingRules;
                  }
              } 
			
			if(ruleType == "Default Pattern Check") {
				  var data = "";
				    var table = document.getElementById('patternTable');
				    var trid = table.getElementsByTagName("tr");
				        $(trid).each(function() {
				        	var pattern = $(this).find(".pattern").text();
				        	var percent = $(this).find(".percent").text();
				        	if ($(this).find(".pattern input").val() != null ){
				        		
				        		pattern = $(this).find(".pattern input").val();
					        	percent = $(this).find(".percent input").val();
				        		
				        	}
				        	
				        	
				        	   if (pattern.length > 0){
							        var percentage ='0%'
							        	if (percent.trim() != null & percent.trim() != ""){
							        		percentage =percent;
							        	}
							        var celldata= 'val:'+ pattern+'per: ('+ percentage.trim() +')';
							        if (data.length > 0)
							        	data = data + ','+celldata;
							        else
							        	data=celldata;
							       }
				        	});
				        oRecordInEditing.ruleExpression = data; 
				}
            
			PageCtrl.debug.log('submitEditDialogData', oRecordInEditing);

			JwfAjaxWrapper({
				WaitMsg: 'Saving Rule Catalog Review Comments',
				Url: 'SaveRuleCatalogFromEdit',
				Headers:$("#token").val(),
				Data: { RuleCatalogEditRecordToSave: JSON.stringify(oRecordInEditing),
					    idApp : ManageRuleCatalogDataEntry.IdApp 
					  },
				
				CallBackFunction: function(oResponse) {				
					if (!!oResponse.Result) {
						Modal.confirmDialog(0, 'Rule edit status','Rule edit is successful', 'OK', { onDialogClose: reloadRuleCatalogPage });
					} else {					
						Modal.confirmDialog(0, 'Save Failed', oResponse.Msg || 'Failed', ['Ok'] , { onDialogClose: reloadRuleCatalogPage });
					}
					
					function reloadRuleCatalogPage(oEvent){
						window.location.reload(true);
					}
				}				
			});

		},
		promoteNewRuleDialog: function() {
			// Load Rule Type
			ManageRuleCatalogDataEntry.loadRuleTypeList();
			
			var ModeTitle =  'Add new Rule';
			
			Modal.customDialog(1, ModeTitle,
					{ 'BodyDomObj': ManageRuleCatalogDataEntry.NewRuleDialogBody,'HeaderHtml': null },
					['OK', 'Cancel'], { onDialogClose: cbPromoteEditDialog, onDialogLoad: null });

			return;

			function cbPromoteEditDialog(oEvent) {
				var sEventId = oEvent.EventId, sBtnSelected = oEvent.ClickedButton;

				switch(sEventId) {
					case jwfGlobal.EVT_MODAL_DIALOG_ONLOAD:
						PageCtrl.debug.log('on Dialog load','ok');
						break;
						
					case jwfGlobal.EVT_MODAL_DIALOG_ONCLOSE:
						if (sBtnSelected === 'OK') 
						{  
							// Get the value
							var selectedAddRuleType = $('#newRuleType').val();
							
							// When nothing is selected 
							if(selectedAddRuleType === '-1'){
								// No action
							}
							// For Global Rule redirect to global rule discovery page
							else if(selectedAddRuleType === 'Global Rule'){
								window.open("loadGlobalRuleDiscoveryForValidation?idApp=${idApp}&validationName=${validationName}&pageSource=validation","_blank");
							}
							// For Custom Rule redirect to customRule creation page
							else if(selectedAddRuleType === 'Custom Rule'){
								window.open("addNewRule?selectedLeftTemplateId=${idData}","_blank");
							}
							// For Other Rule redirect to template view page
							else {
								window.open("listdataview?idData=${idData}&dataLocation=${templateDataLocation}&name=${templateName}&description=''","_blank");
							}
							
						}
						break;
				}
			}	
		},
		loadRuleTypeList: function(){

			$('#addRuleMsg').addClass('hidden');
			
			// Clear the dropdown
			$('#newRuleType').empty();
			
			// Default option
			$('#newRuleType').append($('<option>', {value : "-1",text : "Please select"}));
			
			$.ajax({
				url : './getRuleTypeList',
				type : 'GET',
				datatype : 'json',
				success : function(response) {
					var aRuleTypeList = JSON.parse(response).ruleTypeList;
					$(aRuleTypeList).each(
							function(nIndex, data) {
								var oOption = document.createElement('OPTION');				
								oOption.text = data;
								oOption.value = data;
								$('#newRuleType').append(oOption);
							}
					);
				},
				error : function() {

				}
			});	
			
			
					
		},
	};
	
	$(document).ready( function() {
		initializeJwfSpaInfra(); // Load JWF framework module on this page
		ManageRuleCatalogDataEntry.setup();
		ManageRuleCatalogDataEntry.loadRuleCatalogRecordList();
		ManageRuleCatalogDataEntry.loadValidationApprovalStatusList();
		ManageRuleCatalogDataEntry.getDimensionDefectCodeList();
		
		if($('#BtnBackward').attr('data-fromMapping') == 'validationView') {
			$('#BtnBackward').click(function() { window.location.href = 'validationCheck_View'});
		} 
		else if($('#BtnBackward').attr('data-fromMapping') == 'dashboardTableView') {
			$('#BtnBackward').click(function() { window.location.href = 'dashboard_table?idApp='+${idApp}});
		} 
		else {
			$('#BtnBackward').click(function() { window.location.href = 'validationCheck_View'});
		}	
		
		$('#newRuleType').change(function(){
			// Get the value
			var selectedAddRuleType = $('#newRuleType').val();
			
			var addRuleMessage = "";
			
			// When nothing is selected 
			if(selectedAddRuleType === '-1'){
				addRuleMessage = "<span style='color:blue;'>Please select rule type.</span>";
			}
			// For Global Rule 
			if(selectedAddRuleType === 'Global Rule'){
				addRuleMessage = "Click <b>'OK'</b> to open <span style='color:blue;'>Global Rule Discovery</span> page in new tab to configure global rule.<span style='color:Red;'> Reload Rule catalog after rule creation.</span>";
			}
			// For Custom Rule 
			else if(selectedAddRuleType === 'Custom Rule'){
				addRuleMessage = "Click <b>'OK'</b> to open <span style='color:blue;'>Add Custom Rule</span> page in new tab to create custom rule.<span style='color:Red;'> Reload Rule catalog after rule creation.</span>";
			}
			// For Other Rule 
			else if(selectedAddRuleType != '-1'){
				addRuleMessage = "Click <b>'OK'</b> to open <span style='color:blue;'>Template View</span> in new tab to configure <b>"+selectedAddRuleType+"</b>. Make sure <b>"+selectedAddRuleType+"</b> is enabled in validation.<p style='color:blue;'> Reload Rule catalog after rule creation.</p>";
			}
			
			$('#addRuleMsg').removeClass('hidden');
			$('#addRuleMsg').html(addRuleMessage);
			
		});
		
				
	});
</script>

