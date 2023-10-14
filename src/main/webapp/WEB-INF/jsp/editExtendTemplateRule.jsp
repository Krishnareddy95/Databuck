
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="checkVulnerability.jsp" />
<style>
	.SqlRuleQuery { 
	  width: 100%;
	  height: 150px !important;
	  border: 1px solid #cccccc;
	  padding: 5px;
	  overflow: auto;
	  font-family: Courier New;
	  font-size: 14px;
	  resize: vertical;
	}

	.SqlRuleQuery:focus {
		background-color: #f2f2dc;	
		border: 2px solid #99d6ff;
	}
</style>

<jsp:include page="header.jsp" />
<script>
	var request;
	var vals = "";
	function sendInfo() {
		var v = document.getElementById("rulenameid").value;
		//var v=document.vinform.Database.value;  
		var url = "./duplicatedatatemplatename?val=" + v;
		//alert("in ajax code");
		if (window.XMLHttpRequest) {
			request = new XMLHttpRequest();
		} else if (window.ActiveXObject) {
			request = new ActiveXObject("Microsoft.XMLHTTP");
		}

		try {
			request.onreadystatechange = getInfo;
			request.open("POST", url, true);
			request.setRequestHeader('token',$("#token").val());
			request.send();
		} catch (e) {
			alert("Unable to connect to server");
		}
	}

	function getInfo() {
		if (request.readyState == 4) {
			var val = request.responseText;
			//alert("getInfo");
			//alert(val);
			//var sal="Database already exists";
			//console.log(val);
			document.getElementById('amit').innerHTML = val;
			if (val != "") {
				vals = val;
				//alert("please enter another name");
				return false;
			}
			//alert("check123");
		}
	}
</script>
<jsp:include page="container.jsp" />

<head>
<link rel="stylesheet" href="./assets/jwf/content/JwfSpaInfra.css">
<script src="./assets/jwf/scripts/JwfSpaInfra.js"></script>
</head>

<!-- BEGIN CONTENT -->
<div class="page-content-wrapper">
	<!-- BEGIN CONTENT BODY -->
	<div class="page-content">
		<!-- BEGIN PAGE TITLE-->
		<!-- END PAGE TITLE-->
		<!-- END PAGE HEADER-->
		<div class="row">
			<div class="col-md-6" style="width: 100%;">
				<!-- BEGIN SAMPLE FORM PORTLET-->
				<div class="portlet light bordered init">
					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold "> Edit Extended
								Template Rule </span>
						</div>
					</div>
					<div class="portlet-body form">

						<div class="form-body">
							<div class="row">

								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											id="rulenameid" 
											value="${listDataSource.ruleName}"  readonly="readonly" > 
											<label for="rulenameid">Extended Template Rule Name *</label>
										<!--  <span class="help-block">Data Set Name </span> -->
									</div>
									<br /> <span class="required"></span>
								</div>

								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											id="descid"
											value="${listDataSource.description}" >
											 <label for="descid">Description</label>
										<!--  <span class="help-block">Data Set Name </span> -->
									</div>
									<br /> <span class="required"></span>
								</div>
							</div>

							<div class="row">
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<%-- <select class="form-control " name="ruleCategory"
											id="ruleCategoryid" value="${listDataSource.ruleType}"  readonly="readonly"> --%>
											<input type="text" class="form-control catch-error"
											id="ruleCategoryid"
											value="${listDataSource.ruleType}"  readonly="readonly" >
											<input type="hidden" id="ruleExternal" value="${listDataSource.external}" />
										    <!-- <option value="-1">Choose Rule Category</option>
											<option value="Referential">Referential</option>
											<option value="Cross Referential">Cross Referential</option>
											<option value="Orphan">Orphan</option>
											<option value="Regular Expression">Regular Expression</option>  -->
										 <label for="ruleCategoryid">Rule Category </label>
									</div>
								</div>
								<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="cutomRuleThresholdId" name="cutomRuleThresholdId" value="${listDataSource.ruleThreshold}"> 
											<label for="cutomRuleThresholdId">Rule Threshold</label>
										</div>
								</div>
								<!--  need to remove the hide later-->
								<div class="col-md-6 col-capture hidden" id="matchTypedividhide">
									<div class="form-group form-md-line-input">
										<select class="form-control " name="matchType"
											id="matchType">
										<!-- 	<option value="-1">Choose Match Type</option>
											<option value="Exact">Exact</option> -->
											<option value="Pattern">Lookup</option>
										</select> <label for="matchType">Match Type </label>
										<!--  <span class="help-block">Data Set Name </span> -->
									</div>
								</div>
							</div><br/>
							<div class="row">
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
									 <select class="form-control" id="datasourceid"
											name="datasource" placeholder="" disabled="disabled">
											<option value="${listDataSource.idData}">${listDataSource.templateName}</option>
											 <c:forEach var="getlistdatasourcesnameobj"
												items="${getlistdatasourcesname}">
												<option value="${getlistdatasourcesnameobj.idData}">${getlistdatasourcesnameobj.name}</option>
											</c:forEach> 
										</select> <label for="columnid">Data Template for Modification</label><br>
									</div>
								</div>
								<div class="col-md-6 col-capture hidden" id="rightdatasourceid" >
									<div class="form-group form-md-line-input">
										<select class="form-control" id="rightsourceid"
											name="rightsource" placeholder="" disabled="disabled">
											<option value="${listDataSource.idRightData}">${listDataSource.externalDatasetName}</option>
											<c:forEach var="getlistdatasourcesnameobj"
												items="${getlistdatasourcesname}">
												<option value="${getlistdatasourcesnameobj.idData}">${getlistdatasourcesnameobj.name}</option>
											</c:forEach>
										</select> <label for="form_control_1">Second Source Template *</label>
									</div>
									<br /> <span class="required"></span>
								</div>
							</div>
							<br /> <span class="required"></span>
							<div class="row">
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<select class="form-control text-left" id="domensionid"
											name="domension" placeholder="" > 
											<option value="-1">Please Select Dimension</option>
											<c:forEach var="getlistdimensionnameobj"
												items="${getlistdimensionname}">
												<option value="${getlistdimensionnameobj.idDimension}">${getlistdimensionnameobj.dimensionName}</option>
											</c:forEach>
										</select><label for="columnid">Dimension</label><br>
									</div>
								</div>
							</div>
							<br /> <span class="required"></span>
							
							<div class="row" id="anchorColumnsDiv">
								<div class="col-md-12 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error" id="anchorColumnsId" name="anchorColumns" value="${listDataSource.expression}"
											placeholder="Enter comma separated list of column names associated with Rule expression"/>
										<label for="form_control_1">Anchor columns *</label>
									</div>
								</div>
							</div>	
							<br>
							<span class="required"></span>
							
							<div id="NotNeededForSqlRule" style='display: block;'>							
								<div class="row">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
										<select class="form-control blendops secondops" id="leftitemid" name="leftitem">
											</select> <label for="form_control_1">Select Data Column *</label>
										</div>
									</div>
									<div class="col-md-6 col-capture hidden" id="rightdatasourceid2">
										<div class="form-group form-md-line-input">
											<select class="form-control secondops" id="rightitemid" name="rightitem"
												placeholder="">
											</select> <label for="form_control_1">Select Data Column *</label>
										</div>
										<br /> <span class="required"></span>
									</div>
								</div>
								<div class="row">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<select class="form-control blendops" name="filterfunction" id="NewOrEditSelectFilter">
												<!-- <option value="-1">Choose Filter Function</option>
												<option value="CONCAT(">CONCAT</option>
												<option value="SUBSTR(">SUBSTR</option>
												<option value="ROUND(">ROUND</option>
												<option value="MIN(">MIN</option>
												<option value="MAX(">MAX</option>
												<option value="AVG(">AVG</option> -->
												<c:forEach items="${functionsdata}"
														var="functionsdata">
														<option value=${functionsdata.key}>${functionsdata.value}</option>
												</c:forEach>
											</select> <label for="blendfunid">Choose Filter Function</label><br>
										</div>
									</div>
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<select class="form-control blendops" name="blendoperator" id="NewOrEditSelectOperator">
												<%-- <option value="-1">Choose Operator</option>
												<option value="+">+</option>
												<option value="-">-</option>
												<option value="*">*</option>
												<option value="/">/</option>
												<option value=">">></option>
												<option value="<"><</option>
												<option value=">=">>=</option>
												<option value="<="><=</option>
												<option value="!=">!=</option>
												<option value="==">==</option>
												<option value="&&">&&</option>
												<option value="||">||</option> --%>
												<c:forEach items="${operatorsdata}"
														var="operatorsdata">
														<option value=${operatorsdata.key}>${operatorsdata.value}</option>
												</c:forEach>
											</select> <label for="blendoperid">Choose Operator</label><br>
										</div>
									</div>
								</div>
								<div class="row hidden" id="ruleexprdivid">
									<div class="col-md-12 col-capture" >
										<div class="form-group form-md-line-input">
											<div class="hidden" id="prevRuleExpValueId">${listDataSource.expression}</div>
											<input type="text" class="form-control catch-error"	id="formulaid" name="ruleexpr" value=""> 
											<label for="formulaid" ><input type="radio" id="RuleExprSelected" value="R" name="ExprSelected" checked>
												Rule Expression *
											</label>
										</div>
										<br /> <span class="required"></span>
									</div>
								</div>
	
								<div class="row hidden" id="matchexprdivid">
									<div class="col-md-12 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control catch-error"
												id="matchexprid" name="matchexpr" 
												value="${listDataSource.matchingRules}" > 
												<label for="matchexprid">
													<input type="radio" id="MatchExprSelected" value="M" name="ExprSelected">
													Matching Expression *
												</label>
										</div>
										<br /> <span class="required"></span>
									</div>
								</div>
							</div>
							<div id="NeededForSqlRule" class="form-group" style="display: none;">
								<label for="SqlRuleQuery">Sql Query for Rule *</label>
								<textarea class="form-control" id="SqlRuleQuery" spellcheck="false" >${listDataSource.expression}</textarea>
							</div>							
							<div class="form-actions noborder align-center">
								<button type="submit" id="addnewruleid" class="btn blue">Submit</button>
							</div>
						</div>
					</div>
					<div class="note note-info hidden">Blend created Successfully
					</div>
					<!-- END SAMPLE FORM PORTLET-->
				</div>
			</div>
		</div>
	</div>
	<!-- END QUICK SIDEBAR -->
</div>
<!-- END CONTAINER -->
<input type="hidden" class="form-control catch-error"
											id="idListColrules" name="idListColrules" 
											value="${listDataSource.idListColrules}" >
<jsp:include page="footer.jsp" />
<script>

$(document).ready(function() {
	console.log('Page Load:', $('#ruleCategoryid').val());
	$('#domensionid').val(${listDataSource.idDimension});
	
	initializeJwfSpaInfra();
	
	/* 2091/10/16 (Pradeep) - Original code Commented
	if ( $('#ruleCategoryid').val() == 'Orphan' || ($('#ruleCategoryid').val() == 'Referential' && $('#ruleExternal').val() == 'Y'))	
	{
		 $('#rightdatasourceid').removeClass('hidden').show();
		 $('#rightdatasourceid2').removeClass('hidden').show();	
		 $('#matchexprdivid').removeClass('hidden').show();	
	}	
	
	if ( $('#ruleCategoryid').val() == 'Referential' || $('#ruleCategoryid').val() =='Regular Expression')
	{ 
		$('#ruleexprdivid').removeClass('hidden').show();	
	}
	*/	

	window.onload = function() {
		console.log('edit page sql rule addition executed ' + $('#ruleCategoryid').val().toUpperCase());	
		$('#SqlRuleQuery').addClass('SqlRuleQuery');
		
		$(document.body).attr('data-ignore-global-event','Y');			
		
		$('#rightitemid').off('change');
		$('#leftitemid').off('change');		
		
		if ($('#ruleCategoryid').val() == 'Referential') {
			$('#ruleexprdivid').removeClass('hidden').show();
			$('#rightdatasourceid').addClass('hidden').hide();
			$('#rightdatasourceid2').addClass('hidden').hide();
			$('#matchexprdivid').addClass('hidden').hide();
			$('#matchTypedivid').addClass('hidden').hide();

		} else if (($('#ruleCategoryid').val() == 'Cross Referential')) {
			
			$('#ruleexprdivid').removeClass('hidden').show();
			$('#matchexprdivid').removeClass('hidden').show();
			$('#rightdatasourceid2').removeClass('hidden').show();
			$('#rightdatasourceid').removeClass('hidden').show();
			//$("#matchexprid").attr("placeholder","dataSource1.columnName=dataSource2.columnName").val("").focus().blur();
			$('#matchTypedivid').addClass('hidden').hide();
		} else if (($('#ruleCategoryid').val() == 'Orphan')) {
			$('#matchexprdivid').removeClass('hidden').show();
			$('#rightdatasourceid2').removeClass('hidden').show();
			$('#rightdatasourceid').removeClass('hidden').show();
			$('#ruleexprdivid').addClass('hidden').hide();
			//$("#matchexprid").attr("placeholder", "column1=column2").val("").focus().blur();
			$("#matchexprid").attr("placeholder", "column1=column2");
			$('#matchTypedivid').addClass('hidden').hide();
		} else if (($('#ruleCategoryid').val() == 'Regular Expression')) {
			$('#ruleexprdivid').removeClass('hidden').show();
			$('#rightdatasourceid').addClass('hidden').hide();
			$('#rightdatasourceid2').addClass('hidden').hide();
			$('#matchexprdivid').addClass('hidden').hide();
			$('#matchTypedivid').removeClass('hidden').show();
		} else if ( $('#ruleCategoryid').val().toUpperCase() === 'SQL RULE' || $('#ruleCategoryid').val().toUpperCase() === 'SQL INTERNAL RULE' ){
			$('#ruleexprdivid').addClass('hidden').hide();
			$('#rightdatasourceid').addClass('hidden').hide();
			$('#rightdatasourceid2').addClass('hidden').hide();
			$('#matchexprdivid').addClass('hidden').hide();
			$('#matchTypedivid').removeClass('hidden').show();
			$('#NeededForSqlRule').removeClass('hidden').show();
			$('#NotNeededForSqlRule').addClass('hidden').hide();
		}			
		ManageExpressionTexts.setup(1);
	}	
	
	var elementDivText = document.getElementById("prevRuleExpValueId").textContent;
	elementDivText= elementDivText.replace(/"/g, '\"');
	document.getElementById("formulaid").value = elementDivText;
	
	
});

/* 2091/10/16 (Pradeep) - Added code for including radio buttons, which will decide to which expr text box DDL selected value to be stuffed */
var ManageExpressionTexts = {
	setup: function(nNewOrEditMode) {
		var sSelectedRuleCategory = (nNewOrEditMode === 0) ? '' : $('#ruleCategoryid').val();

		if (sSelectedRuleCategory !== undefined) { 
			sSelectedRuleCategory = sSelectedRuleCategory.toUpperCase(); 
		} else {
			sSelectedRuleCategory = '';
		}				

		$('#MatchExprSelected').css("pointer-events", "auto");	
		$('#RuleExprSelected').css("pointer-events", "auto");

		$(document.body).attr('data-ignore-global-event','Y');

		$('#leftitemid').change( function(oEvent) { ManageExpressionTexts.insertSelectedDropdownValue(oEvent); });
		$('#rightitemid').change( function(oEvent) { ManageExpressionTexts.insertSelectedDropdownValue(oEvent); });		
		$('#NewOrEditSelectFilter').change( function(oEvent) { ManageExpressionTexts.insertSelectedDropdownValue(oEvent); });
		$('#NewOrEditSelectOperator').change( function(oEvent) { ManageExpressionTexts.insertSelectedDropdownValue(oEvent); });

		ManageExpressionTexts.HideOrShowExprRadioButtons(sSelectedRuleCategory);		
	},
	HideOrShowExprRadioButtons: function(sRuleCategory) {
		var lShowRadioButtons = (sRuleCategory.toUpperCase() === 'CROSS REFERENTIAL') ? true : false;
		
		console.log('HideOrShowExprRadioButtons:', lShowRadioButtons + "," + sRuleCategory); 

		if (lShowRadioButtons) {			
			$('#RuleExprSelected').closest('div').css('display', 'inline-block');	
			$('#MatchExprSelected').closest('div').css('display', 'inline-block');	
		} else {			
			$('#RuleExprSelected').closest('div').css('display', 'none');	
			$('#MatchExprSelected').closest('div').css('display', 'none');	
		}	
	},
	getWhichExpressionSelected: function() {
		var sSelectedExpr = $("input[name='ExprSelected']:checked").val().toUpperCase(), 
			sSelectedRuleCategory = $('#ruleCategoryid').val().toUpperCase(), 
			oWhichTextBox = document.getElementById('formulaid');

		switch (sSelectedRuleCategory) {
			case 'CROSS REFERENTIAL':
				oWhichTextBox = (sSelectedExpr === 'M') ? document.getElementById('matchexprid') : document.getElementById('formulaid');
				break;

			case 'REFERENTIAL': case 'REGULAR EXPRESSION':
				oWhichTextBox = document.getElementById('formulaid');
				break;

			case 'ORPHAN':
				oWhichTextBox = document.getElementById('matchexprid');
				break;
		}

		return oWhichTextBox;
	},


	 insertSelectedDropdownValue: function(oEvent) {		
		var sIsDefaultOption = ($( "{target} option:selected".replace("{target}", "#" + oEvent.target.id) ).val())!= '-1';
		
		if(sIsDefaultOption){
		
		var sSelectedValue = $( "{target} option:selected".replace("{target}", "#" + oEvent.target.id) ).text();
		var oSelectedTextBox = ManageExpressionTexts.getWhichExpressionSelected();	
		var sRuleType = $('#ruleCategoryid').val().toUpperCase();
		
		console.log('Point 01 ' + oSelectedTextBox.id + ',' + oSelectedTextBox.value);

		if ( ('leftitemid,rightitemid'.indexOf(oEvent.target.id) > -1) && (sRuleType === 'CROSS REFERENTIAL') && (oSelectedTextBox.id === 'formulaid') ) {		
			sSelectedValue = (oEvent.target.id === 'leftitemid') ? ('originalDf.' + sSelectedValue) : ('dfOther0.' + sSelectedValue);
		}
		
		oSelectedTextBox.value = oSelectedTextBox.value + ' {v} '.replace('{v}',sSelectedValue);
		
		console.log('Point 02 ' + oSelectedTextBox.id + ',' + oSelectedTextBox.value);
		}
	}
	
	
}		

$("#datasourceid").ready(function(){

    var no_error = 1;
    $('.input_error').remove();

    if($("#datasourceid").val() == '-1'){
       // $('<span class="input_error" style="font-size:12px;color:red">Please select a source to Continue</span>').insertAfter($('#rightsourceid'));
        no_error = 1;
    } 

    if(no_error)
    {   var rightSourceId = $("#datasourceid :selected").text();
    
    console.log(rightSourceId);
    	var form_data = {
    		idData : $("#datasourceid").val(),
           // times: new Date().getTime()
        };
        
        $.ajax({
          url: './changeDataColumnAjax',
          type: 'POST',
          headers: { 'token':$("#token").val()},
          datatype:'json',
          data: form_data,
          success: function(message){
              var j_obj = $.parseJSON(message);
                  if(j_obj.hasOwnProperty('success'))
                  {
                    var temp = {};
                    column_array = JSON.parse( j_obj.success);
                    // empty the list from previous selection
                    $("#leftitemid").empty();
                    $("<option />")
                           .attr("value", '-1')
                         .html('Select Column')
                         .appendTo("#leftitemid");
                           temp[this.value] = this.subitems;
                           
                           
                     
                    
                    $.each(column_array, function(k1, v1) 
                    {
                    	console.log(v1);
                    	console.log(k1);
                        var column_details = column_array[k1];
                        console.log(column_details);
                        var sourcename = rightSourceId;
                        temp_name = column_details;
                        //sourcename = sourcename.concat('.', temp_name);
                        sourcename = temp_name;
                        $("<option />")
                            .attr("value", sourcename)
                            .html(temp_name)
                            .appendTo("#leftitemid");
                            temp[this.value] = this.subitems;
                    });
					
					
                  }else if(j_obj.hasOwnProperty('fail') )
                  {
                      console.log(j_obj.fail);
                      toastr.info(j_obj.fail);
                  }
          },
          error: function(xhr, textStatus, errorThrown){
                $('#initial').hide();
                $('#fail').show();
          }
       });
    }    
});

$("#rightsourceid").ready(function(){

    var no_error = 1;
    $('.input_error').remove();

    if($("#rightsourceid").val() == '-1'){
       // $('<span class="input_error" style="font-size:12px;color:red">Please select a source to Continue</span>').insertAfter($('#rightsourceid'));
        no_error = 1;
    } 

    if(no_error)
    {   var rightSourceId = $("#rightsourceid :selected").text();
    
    console.log(rightSourceId);
    	var form_data = {
    		idData : $("#rightsourceid").val(),
           // times: new Date().getTime()
        };
        
        $.ajax({
          url: './changeDataColumnAjax',
          type: 'POST',
          headers: { 'token':$("#token").val()},
          datatype:'json',
          data: form_data,
          success: function(message){
              var j_obj = $.parseJSON(message);
                  if(j_obj.hasOwnProperty('success'))
                  {
                                              
                           
                     //Sumeet_30_08_2018
                     var temp1 = {};
                    column_array1 = JSON.parse( j_obj.success);
                    // empty the list from previous selection
                    $("#rightitemid").empty();
                    $("<option />")
                           .attr("value", '-1')
                         .html('Select Column')
                         .appendTo("#rightitemid");
                           temp1[this.value] = this.subitems;
					//
                    					
                    $.each(column_array1, function(k1, v1) 
                            {
                            	console.log(v1);
                            	console.log(k1);
                                var column_details1 = column_array1[k1];
                                console.log(column_details1);
                                var sourcename = rightSourceId;
                                temp_name = column_details1;
                                //sourcename = sourcename.concat('.', temp_name);
                                sourcename = temp_name;
                                $("<option />")
                                    .attr("value", sourcename)
                                    .html(temp_name)
                                    .appendTo("#rightitemid");
                                    temp1[this.value] = this.subitems;
                            });
                  }else if(j_obj.hasOwnProperty('fail') )
                  {
                      console.log(j_obj.fail);
                      toastr.info(j_obj.fail);
                  }
          },
          error: function(xhr, textStatus, errorThrown){
                $('#initial').hide();
                $('#fail').show();
          }
       });
    }    
});



	$('#addnewruleid').click(function() {
						var no_error = 1;
						$('.input_error').remove();
						
						if(!checkInputText()){ 
							no_error = 0;
							alert('Vulnerability in submitted data, not allowed to submit!');
						}

						 /* if ($("#rulenameid").val().length == 0) {
							$('<span class="input_error" style="font-size:12px;color:red">Please enter Extended Template Rule Name</span>')
									.insertAfter($('#rulenameid'));
							no_error = 0;
						} 
						 if ($("#ruleCategoryid").val() == -1) {
								$('<span class="input_error" style="font-size:12px;color:red">Please Choose Rule Category</span>')
										.insertAfter($('#ruleCategoryid'));
								no_error = 0;
							} 
						 if( ( $('#ruleCategoryid').val() =='Referential' ) || ( $('#ruleCategoryid').val() =='Cross Referential' )||( $('#ruleCategoryid').val() =='referential' )  ){
							 if ($("#formulaid").val().length == 0) {
									$('<span class="input_error" style="font-size:12px;color:red">Please enter Rule Expression</span>')
											.insertAfter($('#formulaid'));
									no_error = 0;
								} 
						 }
						 if( ( $('#ruleCategoryid').val() =='Orphan' ) || ( $('#ruleCategoryid').val() =='Cross Referential' ) || ( $('#ruleCategoryid').val() =='orphanreferential' )  ){
							 if ($("#matchexprid").val().length == 0) {
									$('<span class="input_error" style="font-size:12px;color:red">Please enter Matching Expression</span>')
											.insertAfter($('#matchexprid'));
									no_error = 0;
								} 
						 } */
						// if($("#formulaid").val().length == 0){
						//     $('<span class="input_error" style="font-size:12px;color:red">Please enter a formula to be saved</span>').insertAfter($('#formulaid'));
						//     no_error = 0;
						// }
						 
						 if(($('#cutomRuleThresholdId').val()).trim() == ''){
							   $('<span class="input_error" style="font-size:12px;color:red">Please enter Rule Threshold Value</span>').insertAfter($('#cutomRuleThresholdId'));
							   no_error = 0;
							 }
						 
						 if($("#domensionid").val() == -1){
							   $('<span class="input_error" style="font-size:12px;color:red">Please Select Dimension</span>').insertAfter($('#domensionid'));
							   no_error = 0;
							 }
						 
						 if($("#anchorColumnsId").val().length == 0){
							   $('<span class="input_error" style="font-size:12px;color:red">Please mention Anchor columns</span>').insertAfter($('#anchorColumnsId'));
							   no_error = 0;
						 }
						 
						var targeturl = $("#targeturl").val();
						if (no_error) {datasourceid
							var form_data = {
								name : $("#rulenameid").val(),
								idListColrules :$('#idListColrules').val(),
								description : $("#descid").val(),
								ruleCategory : $("#ruleCategoryid").val(),
								dataSource1 : $("#datasourceid").val(),
								dataSource2 : $("#rightsourceid").val(),
								ruleExpression : $("#formulaid").val(),
								sSqlRuleQuery: $("#SqlRuleQuery").val(),								
								matchingExpression : $("#matchexprid").val(),
								externalDatasetName:$("#rightsourceid").find( "option:selected" ).text(), 
								matchType : $("#matchType").val(),
								regularExprColumnName:$("#leftitemid").find( "option:selected" ).text(),
								ruleThreshold :$("#cutomRuleThresholdId").val(),
								dimension : $("#domensionid").val(),
								anchorColumns : $("#anchorColumnsId").val()
							//times: new Date().getTime()
							};
							console.log(form_data);

							$.ajax({
										url : './updateExtendTemplateRule',
										type : 'POST',
										headers: { 'token':$("#token").val()},
										datatype : 'json',
										data : form_data,
										 success: function(message){
								        	    console.log(message);
								             var j_obj = $.parseJSON(message);
								                  if(j_obj.hasOwnProperty('success'))
								                  {
								                	  //alert(message);
								                     toastr.info("Extended Template Rule updated successfully");
								                        setTimeout(function(){
								                        window.location.href='viewRules';
								                        //window.location.reload();

								                    },1000); 
								                  }else if(j_obj.hasOwnProperty('fail') )
								                  {
								                	  toastr.info("This Rule Name exists for the Data Source. Please Change Rule Name");
								                     // console.log(j_obj.fail);
								                      //satoastr.info(j_obj.fail);
								                  }else if(j_obj.hasOwnProperty('failed') )
								                  {
								                	  toastr.info(j_obj['failed']);
								                     // console.log(j_obj.fail);
								                      //satoastr.info(j_obj.fail);
								                  }
								                  else{
														toastr.info("Unexpected error");
													}
										},
										error : function(xhr, textStatus,
												errorThrown) {
											$('#initial').hide();
											$('#fail').show();
										}
									});
						}
						return false;
					});
</script>