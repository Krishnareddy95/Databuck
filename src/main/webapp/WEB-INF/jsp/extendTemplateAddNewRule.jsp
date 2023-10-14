
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<jsp:include page="header.jsp" />

<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<head>
<link rel="stylesheet" href="./assets/jwf/content/JwfSpaInfra.css">
<script src="./assets/jwf/scripts/JwfSpaInfra.js"></script>
</head>

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
	div.container {
        width: 80%;
    }
     div.vertical-line{
      width: 1px;
      background-color: silver;
      height: 100%;
      float: left;
      border: 1px ridge silver ;
      border-radius: 0px;
    }
    div.col-md-1 {
    width: 3.33333%;
}
div.col-md-8 {
    width: 71.66667%;
}

	.SqlRuleQuery:focus {
		background-color: #f2f2dc;	
		border: 2px solid #99d6ff;
	}
</style>

<script type="text/javascript">
function sendInfo() {
    var extendedTemplaterulename = document.getElementById("rulenameid").value;
    var expr = /^[a-zA-Z0-9_]*$/;
    if (!expr.test(extendedTemplaterulename)) {
    	document.getElementById("rulenameid").value ="";
        alert("Please Enter Extended Template Rule Name without spaces and special characters");
    }
}

</script>


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
							<span class="caption-subject bold "> Create Extended
								Template Rule </span>
						</div>
					</div>
					<div class="portlet-body form">

						<div class="form-body">
							<div class="row">

								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											id="rulenameid" onkeyup="sendInfo()"> <label for="rulenameid">Extended
											Template Rule Name *</label>
										<!--  <span class="help-block">Data Set Name </span> -->
									</div>
									<br /> <span class="required"></span>
								</div>

								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											id="descid"> <label for="descid">Description</label>
										<!--  <span class="help-block">Data Set Name </span> -->
									</div>
									<br /> <span class="required"></span>
								</div>
							</div>
                           <div class="row">
                           <div class="col-md-3 col-capture">
									<div class="form-group form-md-line-input">
							<div class="row">
								<div class="col-md-12 col-capture">
									<div class="form-group form-md-line-input">
										<select class="form-control " name="ruleCategory"
											id="ruleCategoryid">
											<option value="-1">Choose Rule Category</option>
											<option value="Referential">Referential</option>
											<option value="Cross Referential">Cross Referential</option>
											<option value="Orphan">Orphan</option>
											<option value="Regular Expression">Regular Expression</option>
											<!--  SQL RULE is deprecated -->
											<!--  <option value="SQL Rule">SQL Rule</option> -->
											<option value="SQL Internal Rule">SQL Internal Rule</option>
										</select> <label for="ruleCategoryid">Rule Category </label>
									</div>
								</div>
								<!--  need to remove the hide later-->
								<div class="col-md-12 col-capture hidden" id="matchTypedividhide">
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
							
							<div class ="row">
							  <div class="col-md-12 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control"
													id="cutomRuleThresholdId"
													name="cutomRuleThresholdId"> 
											<label for="cutomRuleThresholdId">Rule Threshold</label>
										</div>
									</div>
							</div><br/>
							<div class="row">
								<div class="col-md-12 col-capture">
									<div class="form-group form-md-line-input">
									<select class="form-control js-example-basic-single" style="width: 100%" id="datasourceid"
											name="datasource" onchange="ruleFilterFunction()" placeholder="">
											<option value="-1">Select Source Template</option>
											<c:forEach var="getlistdatasourcesnameobj"
												items="${getlistdatasourcesname}">
												
												<c:choose>
													<c:when test="${getlistdatasourcesnameobj.idData == selectedLeftTemplateId}">
														<option value="${getlistdatasourcesnameobj.idData}" selected>${getlistdatasourcesnameobj.idData}_${getlistdatasourcesnameobj.name}</option>
													</c:when>
													<c:otherwise>
														<option value="${getlistdatasourcesnameobj.idData}">${getlistdatasourcesnameobj.idData}_${getlistdatasourcesnameobj.name}</option>
													</c:otherwise>
												</c:choose>
											</c:forEach>
										</select><label for="form_control_1">Data Template for Modification</label>
										<br>
										
									</div>
										
								</div>
							</div>
							<div class="row">
								<div class="col-md-12 col-capture hidden" id="rightdatasourceid">
									<div class="form-group form-md-line-input">
										<select class="form-control js-example-basic-single value" style="width: 100%" id="rightsourceid"
											name="rightsource" placeholder="">
											<option value="-1">Select Second Source Template</option>
											<c:forEach var="getlistdatasourcesnameobj"
												items="${getlistdatasourcesname}">
												<option value="${getlistdatasourcesnameobj.idData}">${getlistdatasourcesnameobj.idData}_${getlistdatasourcesnameobj.name}</option>
											</c:forEach>
										</select> <label for="form_control_1">Second Source Template *</label>
									</div>
								</div>
							</div>
							<br /> <span class="required"></span>
							<br /> <span class="required"></span>
							
							<div class="row">
								<div class="col-md-12 col-capture">
									<div class="form-group form-md-line-input">
										<select class="form-control text-left value" id="domensionid"
											name="domension" onchange="ruleFilterFunction()" placeholder=""> 
											<option value="-1">Select Dimension</option>
											<c:forEach var="getlistdimensionnameobj"
												items="${getlistdimensionname}">
												<option value="${getlistdimensionnameobj.idDimension}">${getlistdimensionnameobj.dimensionName}</option>
											</c:forEach>
										</select><label for="columnid">Dimension</label><br>
									</div>
								</div>
							</div>
							<br /> <span class="required"></span>
							</div>
							</div>
							<div class="col-md-1 col-capture">
								<div class="form-group form-md-line-input">
								 <div class="vertical-line" style="height: 458px;"></div>
								</div>
							</div>
							<div class="col-md-8 col-capture" id="viewRulesId">
								<div class="form-group form-md-line-input">
								<div class="portlet-body">
								<div class="table-responsive-sm">
									<table class="table table-striped table-bordered  table-hover" id="getData" style="width: 100%;">
										<thead>
											<tr>
												<th>Created At</th>
												<th>Rule Name</th>
												<th>Description</th>
												<th>RuleType</th>
												<th>Expression</th>
												<th>Dimension</th>
												<th>Matching Rules</th>
												<th>Rule Threshold</th>
												<th>Created By</th>
												
											</tr>
										</thead>
										
									</table>
									</div>
								</div>
								</div>
								</div>
						</div>
						
						<br>
						<span class="required"></span>	
						
						<div class="row" id="anchorColumnsDiv">
							<div class="col-md-12 col-capture">
								<div class="form-group form-md-line-input">
									<input type="text" class="form-control catch-error" id="anchorColumnsId" name="anchorColumns" 
										placeholder="Enter comma separated list of column names associated with Rule expression"/>
									<label for="form_control_1">Anchor columns *</label>
								</div>
							</div>
						</div>	
						<br>
						<span class="required"></span>	
						
						<div id='NotNeededForSqlRule' style='display: block;'>
							<div class="row">
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
									<select class="form-control blendops secondops" id="leftitemid" name="leftitem"
											placeholder="">
										</select> <label for="form_control_1">Select Data Column *</label>
									</div>
								</div>
								<div class="col-md-6 col-capture hidden" id="rightdatasourceid2">
									<div class="form-group form-md-line-input">
										<select class="form-control secondops" id="rightitemid" name="rightitem"
											placeholder="">
										</select> <label for="form_control_1">Select Data Column (Second Template)  *</label>
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
										</select> <label for="blendfunid">Choose Filter Function</label>
										<br>
									</div>
								</div>
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
									<select class="form-control blendops" name="blendoperator" id="NewOrEditSelectOperator">
											<option value="-1">Choose Operator</option>
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
											<option value="||">||</option>
											<c:forEach items="${operatorsdata}"
													var="operatorsdata">
													<option value=${operatorsdata.key}>${operatorsdata.value}</option>
												</c:forEach>
										</select> <label for="blendoperid">Choose Operator</label><br>
									</div>
								</div>
							</div>
							<div class="row" >
								<div class="col-md-12 col-capture" id="ruleexprdivid">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error" id="formulaid" name="ruleexpr"> 																				
										<label for="formulaid">
											<input type="radio" id="RuleExprSelected" value="R" name="ExprSelected" checked>
											Rule Expression *
										</label>
									</div>
									<br /> <span class="required"></span>
								</div>
							</div>

							<div class="row hidden" id="matchexprdivid">
								<div class="col-md-12 col-capture">
									<div class="form-group form-md-line-input">
									<input type="text" class="form-control catch-error" id="matchexprid" name="matchexpr" placeholder="dataSource1.columnName=dataSource2.columnName"> 
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
	<textarea class="form-control" id="SqlRuleQuery" spellcheck="false"></textarea>
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
<jsp:include page="footer.jsp" />
<link href="./assets/global/plugins/select2/css/select2.min.css" rel="stylesheet" />
<script src="./assets/global/plugins/select2/js/select2.min.js"></script>
<script>
$(document).ready(function() {
    $('.js-example-basic-single').select2({
    	width: 'resolve',
    	theme: "classic"
    
    });
});

</script>
<script>	
	$(document).ready(function() {
		ManageExpressionTexts.setup(0);
		$('#SqlRuleQuery').addClass('SqlRuleQuery');
		initializeJwfSpaInfra();
		
		if(${selectedLeftTemplateId} > 0){
			$("#datasourceid").trigger("change");
		}
		
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
		
			$('#rightitemid').off('change');
			$('#leftitemid').off('change');			

			$('#leftitemid').change( function(oEvent) { ManageExpressionTexts.insertSelectedDropdownValue(oEvent); });
			$('#rightitemid').change( function(oEvent) { ManageExpressionTexts.insertSelectedDropdownValue(oEvent); });		
			$('#NewOrEditSelectFilter').change( function(oEvent) { ManageExpressionTexts.insertSelectedDropdownValue(oEvent); });
			$('#NewOrEditSelectOperator').change( function(oEvent) { ManageExpressionTexts.insertSelectedDropdownValue(oEvent); });

			ManageExpressionTexts.HideOrShowExprRadioButtons(sSelectedRuleCategory);		
		},
		HideOrShowExprRadioButtons: function(sRuleCategory) {
			var lShowRadioButtons = (sRuleCategory.toUpperCase() === 'CROSS REFERENTIAL') ? true : false;
			var lIsSqlRule = ((sRuleCategory.toUpperCase() === 'SQL RULE') || (sRuleCategory.toUpperCase() === 'SQL INTERNAL RULE')) ? true : false;
			var sSqlRuleCss = (lIsSqlRule) ? 'block' : 'none';
			var sNoSqlRuleCss = (lIsSqlRule) ? 'none' : 'block';
			
			if (lShowRadioButtons) {			
				$('#RuleExprSelected').closest('div').css('display', 'inline-block');	
				$('#MatchExprSelected').closest('div').css('display', 'inline-block');	
			} else {			
				$('#RuleExprSelected').closest('div').css('display', 'none');	
				$('#MatchExprSelected').closest('div').css('display', 'none');	
			}
			
			$('#NotNeededForSqlRule').css('display', sNoSqlRuleCss);
			$('#NeededForSqlRule').css('display', sSqlRuleCss);
			
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
			var sTargetId = oEvent.target.id, sTargetSpec = '#' + oEvent.target.id + ' option:selected';
			var sIsDefaultOption = ( $(sTargetSpec).val() === '-1' ) ? true : false; 				
			var sSelectedValue = '', oSelectedTextBox = null, sRuleType = $('#ruleCategoryid').val().toUpperCase();
			
			
			if (!sIsDefaultOption) {			
				sSelectedValue = $(sTargetSpec).text();
				oSelectedTextBox = ManageExpressionTexts.getWhichExpressionSelected();	

				if ( ('leftitemid,rightitemid'.indexOf(sTargetId) > -1) && (sRuleType === 'CROSS REFERENTIAL') && (oSelectedTextBox.id === 'formulaid') ) {				
					sSelectedValue = (oEvent.target.id === 'leftitemid') ? ('originalDf.' + sSelectedValue) : ('dfOther0.' + sSelectedValue);
				}				

				oSelectedTextBox.value = oSelectedTextBox.value + ' {v} '.replace('{v}',sSelectedValue);				
				PageCtrl.debug.log("insertSelectedDropdownValue", "Value '" + sSelectedValue + "' inserted into box '" + oSelectedTextBox.id + "'");
			}
		}
	}		
</script>
<script>
var table;
function ruleFilterFunction() { 
	
	var editor;
	/* $('#viewRulesId').removeClass('hidden').show() */
	 /* $('#viewRulesId').addClass('hidden');  */
    var no_error = 1;
    $('.input_error').remove();

    if($("#datasourceid").val() == '-1'){
       // $('<span class="input_error" style="font-size:12px;color:red">Please select a source to Continue</span>').insertAfter($('#rightsourceid'));
        no_error = 0;
        /* $('#viewRulesId').addClass('hidden'); */
    }

    if(no_error)
    {   var rightSourceId = $("#datasourceid :selected").text();
    
    console.log(rightSourceId);
    	var form_data = {
    		idData : $("#datasourceid").val(),
           // times: new Date().getTime()
        };

       // Mamta 19-May-2022 
        if ($.fn.DataTable.isDataTable('#getData')) {
			$('#getData').DataTable().destroy();
			$('#getData tbody').empty();
		}
    	//$('#getData').html('');
    	//get ActualWebBasedUrl 
        var sAjaxUrlTmpl = "{0}colRules?idData=";
        var sWebAppBaseUrl = getWebAppBaseUrl();
        var sActualAjaxUrl = sAjaxUrlTmpl.replace('{0}', sWebAppBaseUrl);
        //alert("sActualAjaxUrl =>"+sActualAjaxUrl);
        // url: "/databuck/colRules?idData="
        
    	  table = $('.table').dataTable(
			{
				"bPaginate" : true,
				"order" : [ 0, 'asc' ],
				//"bInfo" : true,
				//"iDisplayStart" : 0,
				"bProcessing" : true,
				"bServerSide" : true,
				'sScrollX' : true,
				"sAjaxSource" :sActualAjaxUrl + $("#datasourceid").val()+ "&dimensionId=" + $("#domensionid").val(),
				 "columnDefs": [
	                        { 
	                        	//"targets":[1],
	                        	"data": "createdAt" },
	                        { 
	                        	//"targets":[1],	
	                        	"data": "Rule Name" },
	                        { 
	                        	//"targets":[1],
	                        	"data": "Description"},
	                        { 
	                        	//"targets":[1],
	                        	 "data": "RuleType"},
	                        { 
	                           // "targets":[1],
	                        	"data": "Expression"},
	                        { 
		                        //"targets":[1],	
		                        "data": "Dimension"},
	                        { 
	                        	//"targets":[1],	
	                        	"data": "Matching Rules"},
	                        { 
		                        	//"targets":[1],
		                        "data": "ruleThreshold"},
		                        
	                        { 
	                        	//"targets":[1],
	                        	"data": "Created By"},
	                    ], 
	                    
				
			});
    	  
    	  
    
    	
    	
    	
       
    } 
}

$("#leftitemid").change(function(){
	
	table.fnFilter($("#leftitemid :selected").text());
});

</script>
<script>


$("#datasourceid").change(function(){

    var no_error = 1;
    $('.input_error').remove();

    if($("#datasourceid").val() == '-1'){
       // $('<span class="input_error" style="font-size:12px;color:red">Please select a source to Continue</span>').insertAfter($('#rightsourceid'));
        no_error = 0;
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
          datatype:'json',
          headers: { 'token':$("#token").val()},
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



	$('#addnewruleid').click(function() {
		               	var no_error = 1;
						$('.input_error').remove();
						
						if(!checkInputText()){ 
							no_error = 0;
							alert('Vulnerability in submitted data, not allowed to submit!');
						}

						 if ($("#rulenameid").val().length == 0) {
							$('<span class="input_error" style="font-size:12px;color:red">Please enter Extended Template Rule Name</span>')
									.insertAfter($('#rulenameid'));
							no_error = 0;
						} 
						 if ($("#ruleCategoryid").val() == -1) {
								$('<span class="input_error" style="font-size:12px;color:red">Please Choose Rule Category</span>')
										.insertAfter($('#ruleCategoryid'));
								no_error = 0;
							} 
						 if( ( $('#ruleCategoryid').val() =='Referential' ) || ( $('#ruleCategoryid').val() =='Cross Referential' ) || ( $('#ruleCategoryid').val() =='Regular Expression' ) ){
							 if ($("#formulaid").val().length == 0) {
									$('<span class="input_error" style="font-size:12px;color:red">Please enter Rule Expression</span>')
											.insertAfter($('#formulaid'));
									no_error = 0;
								} 
						 }
						 if( ( $('#ruleCategoryid').val() =='Orphan' ) || ( $('#ruleCategoryid').val() =='Cross Referential' )  ){
							 if ($("#matchexprid").val().length == 0) {
									$('<span class="input_error" style="font-size:12px;color:red">Please enter Matching Expression</span>')
											.insertAfter($('#matchexprid'));
									no_error = 0;
								} 
						 }
						// if($("#formulaid").val().length == 0){
						//     $('<span class="input_error" style="font-size:12px;color:red">Please enter a formula to be saved</span>').insertAfter($('#formulaid'));
						//     no_error = 0;
						// }
						 if(($('#cutomRuleThresholdId').val()).trim() == ''){
							   $('<span class="input_error" style="font-size:12px;color:red">Please enter Rule Threshold Value</span>').insertAfter($('#cutomRuleThresholdId'));
							   no_error = 0;
							 }
						 
						/* var dimensionVal = $("#domensionid").val();
						if(dimensionVal == -1){
							dimensionVal = 1;
						} */
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
								domension : $("#domensionid").val(),
								anchorColumns : $("#anchorColumnsId").val()
							//times: new Date().getTime()
							};
							console.log(form_data);
							// $('#addnewruleid').addClass('hidden');
							$('button').prop('disabled', true);
							//$("#addnewruleid").html('<i class="fa fa-spinner fa-spin" aria-hidden="true"></i> In Progress');
							
							$.ajax({
										url : './createExtendTemplateRule',
										type : 'POST',
										datatype : 'json',
										headers: { 'token':$("#token").val()},
										data : form_data,
										 success: function(message){
								        	    console.log(message);
								             var j_obj = $.parseJSON(message);
								                  if(j_obj.hasOwnProperty('success'))
								                  {
								                	  //alert(message);
								                     toastr.info(j_obj['success']);
								                        setTimeout(function(){
								                         //$('#addnewruleid').removeClass('hidden');
								                         $('button').prop('disabled', false);
								                         //$("#addnewruleid").html('Completed');
								                         window.location.href='viewRules'; 
								                        //window.location.reload();
								                        $('#getData').DataTable().ajax.reload();

								                    },1000); 
								                  }else if(j_obj.hasOwnProperty('fail') )
								                  {
								                	 toastr.info(j_obj['fail']);
								                	 $('button').prop('disabled', false);
								                	// $("#addnewruleid").html('Submit');
								                	// $('#addnewruleid').removeClass('hidden');
								                     // console.log(j_obj.fail);
								                      //satoastr.info(j_obj.fail);
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