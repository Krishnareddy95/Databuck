
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import ="java.util.*"%>
<%@ page import="com.databuck.bean.ruleFields" %>


<jsp:include page="header.jsp" />
<jsp:include page="checkVulnerability.jsp" />


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
							<span class="caption-subject bold "> Edit Global Rule </span>
						</div>
					</div>
					<div class="portlet-body form">
					<input type="hidden" id="domainFromDBid" value="${listDataSource.domain}" />
						<div class="form-body">
							<div class="row">

								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											id="rulenameid" value="${listDataSource.ruleName}"
											readonly="readonly"> <label for="rulenameid">Global
											Rule Name *</label>

									</div>
									<br /> <span class="required"></span>
								</div>

								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											id="descid" value="${listDataSource.description}"
											readonly="readonly"> <label for="descid">Description</label>

									</div>
									<br /> <span class="required"></span>
								</div>
							</div>

							<div class="row">
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										
										<input type="text" class="form-control catch-error"
											id="ruleCategoryid" value="${listDataSource.ruleType}"
											readonly="readonly">
										
										</select> <label for="ruleCategoryid">Rule Category </label>
									</div>
								</div>
								<div class = "row">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control catch-error"
												id="ruleThresholdId" value="${listDataSource.ruleThreshold}"> <label for="ruleThresholdId">
												Rule Threshold</label>
										</div>
									</div>
								</div>
								<!--  need to remove the hide later-->
								<div class="col-md-6 col-capture hidden" id="matchTypedividhide">
									<div class="form-group form-md-line-input">
										<select class="form-control " name="matchType" id="matchType">

											<option value="Pattern">Lookup</option>
										</select> <label for="matchType">Match Type </label>
									</div>
								</div>
							</div>
							<br />
							
							<div class="row">
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										
										<select class="form-control" id="selectdomain_id" name="domainfunction">

											<c:forEach var="getDomainListobj" items="${listdomain}">
												 
												 <option value="${getDomainListobj.domainName}">${getDomainListobj.domainName}</option>
					
											</c:forEach>
										</select>
										<label for="blendfunid">Domain</label><br>
										
									</div>
								</div>
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<select class="form-control text-left" id="DimensionId"
											name="dimension" placeholder=""> 
											<option value="-1">Please Select Dimension</option>
											<c:forEach var="dimensionListObj"
												items="${dimensionList}">
												<option value="${dimensionListObj.idDimension}">${dimensionListObj.dimensionName}</option>
											</c:forEach>
										</select><label for="columnid">Dimension</label><br>
									</div>
								</div>
							</div>

							<div class = "row hidden" id="filterSelectId">
                                <div class="col-md-6 col-capture">
                                    <div class="form-group form-md-line-input">
                                        <input type="hidden" id="filterName" value="${globalFilter.filterName}" />
                                       
                                        <select class="form-control text-left" id="listFilters">
                                             	<option value="-1">Select Filter</option>
	                                            <c:forEach var="globalFilterObj" items="${listFilterNames}">
	                                                 <option value="${globalFilterObj.filterName}">${globalFilterObj.filterId}_${globalFilterObj.filterName}</option>
	                                            </c:forEach>
                                        </select> <label for="form_control_1">Select Global Filter *</label>
                                    </div>
                                    <br /> <span class="required"></span>
                                </div>
                                <br>
                            </div>

                            <div class="row hidden" id="filterConditionId">
                                <div class="col-md-12 col-capture">
                                    <div class="form-group form-md-line-input">
                                        <input type="text" class="form-control catch-error"
                                            id="filterCondition" name="filterCondition" readonly="readonly"
                                            value="${listDataSource.filterCondition}"> <label
                                            for="form_control_1">Filter Condition *</label>
                                    </div>
                                    <br /> <span class="required"></span>
                                    <input type="hidden" id="filterId" value="${globalFilter.filterId}" />
                                </div>
                            </div>
                            
                            <div class="row">
								<div class="col-md-6 col-capture hidden" id="rightdatasourceideditglobal" >
									<div class="form-group form-md-line-input">
										<select class="form-control" id="rightsourceideditglobal"
											name="rightsourceeditglobal" placeholder="" disabled="disabled">
											<option value="${listDataSource.idRightData}">${listDataSource.externalDatasetName}</option>
											<c:forEach var="getlistdatasourcesnameobj"
												items="${getlistdatasourcesname}">
												<option value="${getlistdatasourcesnameobj.idData}">${getlistdatasourcesnameobj.name}</option>
											</c:forEach>
										</select> <label for="form_control_1">Second Source Template *</label>
									</div>
									<br /> <span class="required"></span>
								</div>

								<div class="col-md-6 col-capture hidden" id="righttemplatecolumneditglobal">
									<div class="form-group form-md-line-input">
										<select class="form-control secondops" id="rightdatatemplatecolumneditglobal"
											name="rightitem" placeholder="">
										</select> <label for="form_control_1">Select Data Column *</label>
									</div>
									<br /> <span class="required"></span>
								</div>
							</div>

							<!-- Global Filter for Second template -->
							<div class = "row hidden" id="rightTemplateFilterSelectId">
	                            <div class="col-md-6 col-capture" >
	                                <div class="form-group form-md-line-input">
	                                    <input type="hidden" id="rightTemplateFilterName" value="${rightTemplateGlobalFilter.filterName}" />
                                       
                                        <select class="form-control text-left" id="rightTemplateFiltersList">
                                             	<option value="-1">Select Filter</option>
	                                            <c:forEach var="globalFilterObj" items="${listFilterNames}">
	                                                 <option value="${globalFilterObj.filterName}">${globalFilterObj.filterId}_${globalFilterObj.filterName}</option>
	                                            </c:forEach>
                                        </select> <label for="form_control_1">Select Global Filter for Second Template </label>
	                                </div>
	                                <br /><span class="required"></span>
	                            </div>
	                        </div>
	
	
	                        <div class="row hidden" id="rightTemplateFilterConditionId">
	                            <div class="col-md-12 col-capture">
	                                <div class="form-group form-md-line-input">
	                                    <input type="text" class="form-control catch-error"
	                                        id="rightTemplateFilterCondition" name="rightTemplateFilterCondition" readonly="readonly"
	                                        placeholder="second dataset filter condition" value="${listDataSource.rightTemplateFilterCondition}">
	                                    <label for="form_control_1">Second Template Filter Condition </label>
	                                    
	                                    <input type="hidden" id="rightTemplateFilterId" value="${rightTemplateGlobalFilter.filterId}" />
	                                </div>
	                                <br /> <span class="required"></span>
	                            </div>
	                        </div>
                                                    
                            <div class="row hidden" id="matchexprdivid">
								<div class="col-md-12 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											id="matchexprid" name="matchexpr"
											value="${listDataSource.matchingRules}"> <label
											for="form_control_1">Matching Expression *</label>
									</div>
									<br /> <span class="required"></span>
								</div>
							</div>

							<div class="row hidden" id="ruleexprdivid">
								<div class="col-md-12 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											id="formulaid" name="ruleexpr"
											value="${listDataSource.expression}"> <label
											for="form_control_1" id="labelRuleExpr">Rule Expression *</label>
									</div>
									<div id="sqlinternal_msg_div" class="hidden" style="font-size:12px;color:blue">For SqlInternalRule, 'INTERNALQUERY' must be used instead of actual table name. Write sql to get failed data instead of passed data. </div>
									<br /> <span class="required"></span>
								</div>
							</div>
							
							<div class="row hidden" id="aggregate_results_div">
								<div class="col-md-12 col-capture">
									<div class="form-group form-md-checkboxes">
										<div class="md-checkbox-list">
											<div class="md-checkbox">
												<input type="checkbox" class="md-check" id="aggregateResultsEnabled" name="aggregateResultsEnabled" value="${listDataSource.aggregateResultsEnabled}"> 
												<label for="aggregateResultsEnabled"><span></span> <span class="check"></span>
													<span class="box"></span>Enable Aggregate Results</label>
											</div>
										</div>
									</div>
								</div>
							</div>
							
						<div class="row" id="editsynonymsid">
							<div class="col-md-6 col-capture">
							<div class="form-group form-md-checkboxes">
								<div class="md-checkbox-list">
									<div class="md-checkbox">
										<input type="checkbox" class="md-check" id="configurerulesid"
											name="configurerule" value="Y" > <label
											for="configurerulesid"><span></span> <span
											class="check"></span> <span class="box"></span>edit
											synonyms </label>
									</div>
								</div>
							</div>
						</div>
						</div>
	   
					</div>

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
<!-- END CONTAINER -->

<div class="cd-popup" id="popSample" role="alert">
	<div class="cd-popup-container">
		<table class="table" id="makeEditable">
			<thead>
				<tr>
					<th>Field Name</th>
					<th>Synonym</th>
					

				</tr>
			</thead>
			<tbody>
			
			<% 
			/*	List<ruleFields> synforruledomain = (List<ruleFields>) request.getSession().getAttribute("synforruledomain");
									System.out.println("=========synforruledomain "+synforruledomain.toString());
			
	 	for (int i = 0; i < synforruledomain.size(); i++) {	
			System.out.println("=========synforruledomain "+synforruledomain.toString());
		} */
		%>
	<c:forEach var="oneSynonym" items="${synforruledomain}">
									<tr>
																	
				    <td contenteditable="true" id="fieldone" class=" catch-error">${oneSynonym.usercolumns}</td>
					<td contenteditable="true" id="Synonymone" class=" catch-error">${oneSynonym.possiblenames}</td> 

										
									</tr>
								
								</c:forEach>
		
			</tbody>


		</table>
		<div>
			<input class="btn btn-primary btn-md" type="button" id="save"
				class="button" value="Save" /> 
				<input class="btn btn-primary btn-md"
				type="button" id="closesynpop" class="button" value="Close" />
		</div>


	</div>

</div>
<!-- END CONTAINER -->
<input type="hidden" class="form-control catch-error"
	id="idListColrules" name="idListColrules"
	value="${listDataSource.idListColrules}">
<jsp:include page="footer.jsp" />
<script>
	$(document).ready(function() {
		
		var domainName = $("#domainFromDBid").val();
		$("#selectdomain_id").val(domainName);

		var filterName = $("#filterName").val();
        $("#listFilters").val(filterName);
        
        var rightTemplateFilterName = $("#rightTemplateFilterName").val();
        $("#rightTemplateFiltersList").val(rightTemplateFilterName);

        if(!$("#filterName").val()){
            $("#filterId").val(0);
        }
        
        if(!$("#rightTemplateFilterName").val()){
            $("#rightTemplateFilterId").val(0);
        }
        
        console.log($("#filterName").val());
        
        var aggregateResultsEnabled = $("#aggregateResultsEnabled").val();
        if(aggregateResultsEnabled == 'Y'){
        	$('#aggregateResultsEnabled').prop('checked', true);
        } else{
        	$('#aggregateResultsEnabled').prop('checked', false);	
        }

		//alert("rule type->" + $('#ruleCategoryid').val());
		if ($('#ruleCategoryid').val() == 'orphan') {
			//alert("In orphan->");
			$('#rightdatasourceideditglobal').removeClass('hidden').show();
			$('#righttemplatecolumneditglobal').removeClass('hidden').show();
			//alert("matching exp" + $('#matchexprdivid').val())
			$('#matchexprdivid').removeClass('hidden').show();
			$('#labelRuleExpr').text("Rule Expression *");
		}
		
		if ($('#ruleCategoryid').val() == 'cross referential') {
			$('#rightdatasourceideditglobal').removeClass('hidden').show();
			$('#righttemplatecolumneditglobal').removeClass('hidden').show();
			$('#matchexprdivid').removeClass('hidden').show();
			$('#ruleexprdivid').removeClass('hidden').show();
			$('#labelRuleExpr').text("Rule Expression *");
		}

		if ($('#ruleCategoryid').val() == 'referential') {
			//alert("In ref->");
			$('#ruleexprdivid').removeClass('hidden').show();
			$('#labelRuleExpr').text("Rule Expression *");
		}

		if ($('#ruleCategoryid').val() == 'conditional orphan') {

            $('#rightdatasourceideditglobal').removeClass('hidden').show();
            $('#righttemplatecolumneditglobal').removeClass('hidden').show();
            $('#matchexprdivid').removeClass('hidden').show();
            $('#filterConditionId').removeClass('hidden').show();
            $('#filterSelectId').removeClass('hidden').show();
            $('#rightTemplateFilterConditionId').removeClass('hidden').show();
            $('#rightTemplateFilterSelectId').removeClass('hidden').show();
            $('#labelRuleExpr').text("Rule Expression *");
        }

        if ($('#ruleCategoryid').val() == 'conditional referential') {

            $('#ruleexprdivid').removeClass('hidden').show();
            $('#filterConditionId').removeClass('hidden').show();
            $('#filterSelectId').removeClass('hidden').show();
            $('#aggregate_results_div').removeClass('hidden').show();
            $('#labelRuleExpr').text("Rule Expression *");
        }
        
        if ($('#ruleCategoryid').val() == 'conditional sql internal rule') {

            $('#ruleexprdivid').removeClass('hidden').show();
            $('#filterConditionId').removeClass('hidden').show();
            $('#filterSelectId').removeClass('hidden').show();
            $('#sqlinternal_msg_div').removeClass('hidden').show();
            $('#labelRuleExpr').text("Rule Expression *");
        }
        
        if ($('#ruleCategoryid').val() == 'conditional duplicate check') {

            $('#ruleexprdivid').removeClass('hidden').show();
            $('#filterConditionId').removeClass('hidden').show();
            $('#filterSelectId').removeClass('hidden').show();
            $('#sqlinternal_msg_div').addClass('hidden').hide();
            $('#labelRuleExpr').text("Duplicate Check Columns *");
        }
        
        if ($('#ruleCategoryid').val() == 'conditional completeness check') {

            $('#ruleexprdivid').removeClass('hidden').show();
            $('#filterConditionId').removeClass('hidden').show();
            $('#filterSelectId').removeClass('hidden').show();
            $('#sqlinternal_msg_div').addClass('hidden').hide();
            $('#labelRuleExpr').text("Completeness Check Column *");
        }
		
		$('#DimensionId').val(${listDataSource.dimensionId});
	});
	
/* Added for hiding edit synonym */
	document.getElementById("editsynonymsid").style.display = "none";
/* ----------------------- */
	 $("#rightsourceideditglobal").ready(function(){

		 var no_error = 1;
		 $('.input_error').remove();
	
		 if($("#rightsourceideditglobal").val() == '-1'){
		 // $('<span class="input_error" style="font-size:12px;color:red">Please select a source to Continue</span>').insertAfter($('#rightsourceideditglobal'));
		 	no_error = 1;
		 } 
	
		 if(no_error)
		 {   
			 
			 var rightsourceideditglobal = $("#rightsourceideditglobal :selected").val();
			
			//alert("rightsourceideditglobal =>"+rightsourceideditglobal); 
			//alert("idData =>"+ $("#rightsourceideditglobal").val());
			// console.log(rightsourceideditglobal);
			 var form_data = {
			 	idData : $("#rightsourceideditglobal").val()
			 	
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
				 $("#rightdatatemplatecolumneditglobal").empty();
				 $("<option />")
				 .attr("value", '-1')
				 .html('Select Column')
				 .appendTo("#rightdatatemplatecolumneditglobal");
				 temp1[this.value] = this.subitems;
				 //
				
				 $.each(column_array1, function(k1, v1) 
				 {
					 console.log(v1);
					 console.log(k1);
					 var column_details1 = column_array1[k1];
					 console.log(column_details1);
					 var sourcename = rightsourceideditglobal;
					 temp_name = column_details1;
					 //sourcename = sourcename.concat('.', temp_name);
					 sourcename = temp_name;
					 $("<option />")
					 .attr("value", sourcename)
					 .html(temp_name)
					 .appendTo("#rightdatatemplatecolumneditglobal");
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

$('#configurerulesid').click(function() {
	
	//alert("In Edit..........");
	var no_error = 1;
	$('.input_error').remove();

	if ($("#formulaid").val().length == 0) {
		
		$(
				'<span class="input_error" style="font-size:12px;color:red">Please enter Rule Expression</span>')
				.insertAfter($('#formulaid'));
		//$('input:checkbox').removeAttr('checked');
		
		no_error = 0;
		
	}
	else
		{
		 $('#popSample').addClass('is-visible');
		}
	if(configurerulesid.checked == false){
		 $('#popSample').removeClass('is-visible');
	}
	//$('input:checkbox').removeAttr('checked');
	
	if ($("#matchexprid").val().length == 0) {
		$(
		'<span class="input_error" style="font-size:12px;color:red">Please enter Match Expression</span>')
		.insertAfter($('#matchexprid'));
		//$('input:checkbox').removeAttr('checked');

		no_error = 0;
		} else {
		$('#popSample').addClass('is-visible');
		//$('input:checkbox').removeAttr('checked');
		}


});

$('#popSample').on('click', function(event){
	if( $(event.target).is('#save')) {
			
	
		event.preventDefault();
		$(this).removeClass('is-visible');
	}
});
$("#closesynpop").click(
		function() {
			 $('#popSample').removeClass('is-visible');
			
		}
)

	$('#addnewruleid')
			.click(
					function() {
						var no_error = 1;
						$('.input_error').remove();
						
						if(!checkInputText()){ 
							no_error = 0;
							alert('Vulnerability in submitted data, not allowed to submit!');
						}

						if($("#ruleThresholdId").val().length == 0){
							   $('<span class="input_error" style="font-size:12px;color:red">Please enter Rule Threshold</span>').insertAfter($('#ruleThresholdId'));
							   no_error = 0;
						}else {
							   if(isNaN(parseFloat($("#ruleThresholdId").val()))){
							     $('<span class="input_error" style="font-size:12px;color:red">Please enter Numeric Value for Threshold</span>').insertAfter($('#ruleThresholdId'));
							     no_error = 0;
							   }
					    }
						
						if($("#DimensionId").val() == -1){
							   $('<span class="input_error" style="font-size:12px;color:red">Please Select Dimension</span>').insertAfter($('#DimensionId'));
							   no_error = 0;
						}
						
						if ($("#selectdomain_id").val() == -1) {
							console.log('selectdomain_id');
							$(
									'<span class="input_error" style="font-size:12px;color:red">Please Select Domain </span>')
									.insertAfter($('#selectdomain_id'));
							no_error = 0;
						}
						
						var ruleCategory = $('#ruleCategoryid').val();
						
						if(ruleCategory == 'orphan' || ruleCategory == 'conditional orphan' || ruleCategory == 'cross referential'){
							
							if ($("#matchexprid").val().length == 0) {
								$('<span class="input_error" style="font-size:12px;color:red">Please enter Match Expression</span>').insertAfter($('#matchexprid'));
								no_error = 0;
							}
							
						} 
						
						if(ruleCategory == 'referential' || ruleCategory == 'conditional referential' || ruleCategory == 'conditional sql internal rule' || ruleCategory == 'cross referential'){
							
							if($("#formulaid").val().length == 0){
								   $('<span class="input_error" style="font-size:12px;color:red">Please enter Rule Expression</span>').insertAfter($('#formulaid'));
								   no_error = 0;
								 }
						}
						
						if(ruleCategory == 'conditional duplicate check'){
							if($("#formulaid").val().length == 0){
								   $('<span class="input_error" style="font-size:12px;color:red">Please enter comma separated list of columns for duplicate check</span>').insertAfter($('#formulaid'));
								   no_error = 0;
							}
						}
						
						if(ruleCategory == 'conditional completeness check'){
							if($("#formulaid").val().length == 0){
								   $('<span class="input_error" style="font-size:12px;color:red">Please enter column name for completeness check</span>').insertAfter($('#formulaid'));
								   no_error = 0;
							}
						}
						
						if($("#filterId").val().length == 0 && (ruleCategory == 'conditional referential' || ruleCategory == 'conditional sql internal rule' || ruleCategory == 'conditional duplicate check' || ruleCategory == 'conditional completeness check')){
							$('<span class="input_error" style="font-size:12px;color:red">Please select global filter</span>').insertAfter($('#listFilters'));
							no_error = 0;
						} 
						
						if($("#filterId").val().length == 0 && $("#rightTemplateFilterId").val().length == 0 && ruleCategory == 'conditional orphan' ){
							 $('<span class="input_error" style="font-size:12px;color:red">Please select global filter</span>').insertAfter($('#listFilters'));
						     no_error = 0;
								
						}
						
						var aggregateResultsEnabled = "N";
						if($("#aggregateResultsEnabled").prop("checked") == true){
							aggregateResultsEnabled = "Y";
						}
						
						var targeturl = $("#targeturl").val();
						if (no_error) {
							var form_data = {
								name : $("#rulenameid").val(),
								idListColrules : $("#idListColrules").val(),
								description : $("#descid").val(),
								domain : $("#selectdomain_id").val(),
								ruleCategory : $("#ruleCategoryid").val(),
								dataSource2 : $("#rightsourceideditglobal").val(), 
								ruleExpression : $("#formulaid").val(),
							 	matchingExpression : $("#matchexprid").val(),
								externalDatasetName:$("#rightsourceideditglobal").find( "option:selected" ).text(),
								ruleThreshold: $("#ruleThresholdId").val(),
								dimension : $("#DimensionId").val(),
								filterId : $("#filterId").val(),
								rightTemplateFilterId : $("#rightTemplateFilterId").val(),
								aggregateResultsEnabled : aggregateResultsEnabled
							};
							console.log(form_data);
							//alert(form_data.domain);


							$.ajax({

										url : './updateGlobalRule',
										type : 'POST',
										headers: { 'token':$("#token").val()},
										datatype : 'json',
										data : form_data,

										success : function(message) {
											console.log(message);
											var j_obj = $.parseJSON(message);
											var err_msg = j_obj.message;
											var status = j_obj.status;
											
											if (status == 'success') {
												// alert(message);
												toastr
														.info("Global Rule updated successfully");
												setTimeout(
														function() {
															window.location.href = 'viewGlobalRules';
															//window.location.reload();

														}, 1000);
											} else if (j_obj.status == 'failed') {
												
												// Displaying mapping errors
												if(j_obj.hasOwnProperty("mappingErrors")){
													
													var mappingErrors = j_obj.mappingErrors;
													
													if(mappingErrors.length <= 6){
														$.each(mappingErrors,function(i){
															toastr.info(mappingErrors[i]);
														});
													}
												}
												
												toastr.info(err_msg);
												
											} else if(j_obj.hasOwnProperty('fail') )
							                  {
							                	  toastr.info(j_obj['fail']);
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
	$("#save").click(
			function() {
			  
				
				
				/* var field = document.getElementById("fieldone").innerHTML; 
				var Synonymone = document.getElementById("Synonymone").innerHTML;
				var fieldsecond= document.getElementById("fieldsecond").innerHTML;
				var Synonymsecond = document.getElementById("Synonymsecond").innerHTML;
				//alert("fieldsecond :: "+fieldsecond);
				//var length= tab_logic
				 var no_error = 1;
					$('.input_error').remove();
				 if (field != "" && Synonymone == "") {
						//alert("welcome..");
						$(
								'<span class="input_error" style="font-size:12px;color:red">Please enter Synonym</span>')
								.insertAfter($('#Synonymone'));
						no_error = 0;
					} 
					if (fieldsecond != "" && Synonymsecond == "") {
						alert("fieldsecond.."+fieldsecond);
						$(
								'<span class="input_error" style="font-size:12px;color:red">Please enter Synonym</span>')
								.insertAfter($('#Synonymsecond'));
						no_error = 0;
					}
					
					if (!no_error) {
						return false;
					}
				 */
				
			
			       
			        var myTab = document.getElementById('makeEditable');
			        var str="";
			        // LOOP THROUGH EACH ROW OF THE TABLE AFTER HEADER.
			        for (i = 1; i < myTab.rows.length; i++) {

			            // GET THE CELLS COLLECTION OF THE CURRENT ROW.
			            var objCells = myTab.rows.item(i).cells;

			            // LOOP THROUGH EACH CELL OF THE CURENT ROW TO READ CELL VALUES.
			            for (var j = 0; j < objCells.length; j++) {
			              
			                 str = str + '||' + objCells.item(j).innerHTML;
			                
			                
			                
			            }
			          
			        }
			       //alert(str);
			        var json_str=JSON.stringify(str);
			        var data_1 = {
		        			singleRowData : json_str,
		        			domain : $("#selectdomain_id").val(),
							name : $("#rulenameid").val()
					};
			    
			        $.ajax({
			        	type: "POST",
			        	url: "editRuleSynonym",
			        	headers: { 'token':$("#token").val()},
			        	data: data_1,
			        	datatype : 'json',
			        	success: function (message) {			        					        							
							
			        	/* alert(message); */

			        	},
			        	error: function (message) {

			        	/* alert("error"+message);
 */
			        	}
			});
			});

$('#selectdomain_id').change(function() {

    if (($('#ruleCategoryid').val() == 'conditional orphan') || ($('#ruleCategoryid').val() == 'conditional referential') || ($('#ruleCategoryid').val() == 'conditional sql internal rule') || ($('#ruleCategoryid').val() == 'conditional duplicate check') || ($('#ruleCategoryid').val() == 'conditional completeness check')){
        populateFilters();
    }else{
        $('#filterSelectId').addClass('hidden').hide();
    }
    
 	// Display and hide of second template filter
	if (($('#ruleCategoryid').val() == 'conditional orphan')){
		$('#rightTemplateFilterSelectId').removeClass('hidden').show();
        populateSecondTemplateFilters();
    }else{
        $('#rightTemplateFilterSelectId').addClass('hidden').hide();
    }

});

	
$('#listFilters').change(addFilterCondition);
$('#rightTemplateFiltersList').change(addSecondTemplateFilterCondition);

function addFilterCondition(){
    var form_data = {
                     filterName: $("#listFilters").val(),
                      domain : $("#selectdomain_id").val()

                  };
     $.ajax({
             url : './getFilterConditionByName',
             type : 'GET',
             datatype : 'json',
             data : form_data,
             success : function(obj) {
                  var filterObj = $.parseJSON(obj);
                       var filterId = filterObj.filterId;
                       var filterCondition = filterObj.filterCondition;
                 $('#filterCondition').val(filterCondition);
                 $('#filterId').val(filterId);

             },
             error : function() {
                 alert("error");
             },
             complete : function() {

             }
         });

 }

function addSecondTemplateFilterCondition(){
    var form_data = {
                     filterName: $("#rightTemplateFiltersList").val(),
                      domain : $("#selectdomain_id").val()
                  };
     $.ajax({
                 url : './getFilterConditionByName',
                 type : 'GET',
                 datatype : 'json',
                 data : form_data,
                 success : function(obj) {
                      var filterObj = $.parseJSON(obj);
                           var filterId = filterObj.filterId;
                           var filterCondition = filterObj.filterCondition;
                     $('#rightTemplateFilterCondition').val(filterCondition);
                     $('#rightTemplateFilterId').val(filterId);

                 },
                 error : function() {
                     alert("error");
                 },
                 complete : function() {

                 }
             });

 }
 
function populateFilters(){
        $('#listFilters').empty();
        var form_data = {
                            domain : $("#selectdomain_id").val()
                        };
       $.ajax({
            url : './populateFilters',
            type : 'GET',
            beforeSend : function() {
                $('#loading-progress').removeClass(
                        'hidden').show();
                $("#listFilters").empty();
            },
            datatype : 'json',
            data : form_data,
            success : function(obj) {
                $(obj).each(function(i, item) {
                                    $('#listFilters').append($('<option>',
	                                     {
	                                         value : item.filterName,
	                                         text : item.filterId+"_"+item.filterName
	                                     }));
                                });

                addFilterCondition();
                $('#filterSelectId').removeClass('hidden').show();
            },
            error : function() {

            },
            complete : function() {
                $('#loading-progress').addClass('hidden');
            }
        });
 }

function populateSecondTemplateFilters(){
    var form_data = {
        domain : $("#selectdomain_id").val()
    };
   	$.ajax({
             url : './populateFilters',
             type : 'GET',
             beforeSend : function() {
                 $('#loading-progress').removeClass('hidden').show();
                 $("#rightTemplateFiltersList").empty();
                 // Add default value
                 $("<option />").attr("value", '-1').html('Select Filter').appendTo("#rightTemplateFiltersList");
             },
             datatype : 'json',
             data : form_data,
             success : function(obj) {
                 $(obj).each(function(i, globalFilterObj) {
                                     $('#rightTemplateFiltersList').append($('<option>',
		                                      {
		                                          value : globalFilterObj.filterName,
		                                          text : globalFilterObj.filterId+"_"+globalFilterObj.filterName
		                                      }));
                                 });

                 addFilterCondition();
                 $('#rightTemplateFilterSelectId').removeClass('hidden').show();
             },
             error : function() {

             },
             complete : function() {
                 $('#loading-progress').addClass('hidden');
             }
         });
}

</script>