<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />
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
							<span class="caption-subject bold "> Create Global Rule </span>
						</div>
					</div>
					<div id="loading-progress" class="ajax-loader hidden">
                        <h4>
                            <b>Filter List Loading in Progress..</b>
                        </h4>
                        <img
                            src="${pageContext.request.contextPath}/assets/global/img/loading-circle.gif"
                            class="img-responsive" />
                    </div>
					<div class="portlet-body form">

						<div class="form-body">
							<div class="row">

								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											id="rulenameid" maxlength="2500"> <label for="rulenameid">Global
											Rule Name *</label>
									</div>
									<br /> <span class="required"></span>
								</div>

								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											id="descid" maxlength="2500"> <label for="descid">Description</label>
									</div>
									<br /> <span class="required"></span>
								</div>
							</div>

							<div class="row">
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<select class="form-control " name="ruleCategory"
											id="ruleCategoryid">
											<option value="-1">Choose Rule Category</option>
											<option value="Referential">Referential</option>
											<option value="Orphan">Orphan</option>
											<option value="cross referential">Cross Referential</option>
											<option value="conditional orphan">Conditional Orphan</option>
											<option value="conditional referential">Conditional Referential</option>
											<option value="conditional sql internal rule">Conditional SQL Internal Rule</option>
											<option value="conditional duplicate check">Conditional Duplicate Check</option>
											<option value="conditional completeness check">Conditional Completeness Check</option>
										</select> <label for="ruleCategoryid">Rule Category </label>
									</div>
								</div>
							 
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">

											<select class="form-control" id="selectdomain_id" name="domainfunction">
										    <option value="-1">Select Domain</option>
											<c:forEach var="getDomainListobj" items="${listdomain}">
												 
												 <option value="${getDomainListobj.domainName}">${getDomainListobj.domainName}</option>
					
											</c:forEach>
										</select>
										<label for="blendfunid">Domain *</label><br>
											
										</div>
										<span class="required"></span>
									</div>
			                     
							</div>
							<div class = "row">
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											id="ruleThresholdId"> <label for="ruleThresholdId">
											Rule Threshold</label>
									</div>
								</div>
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<select class="form-control text-left" id="DimensionId"
											name="dimension" placeholder=""> 
											<option value="-1">Select Dimension</option>
											<c:forEach var="dimensionListObj"
												items="${dimensionList}">
												<option value="${dimensionListObj.idDimension}">${dimensionListObj.dimensionName}</option>
											</c:forEach>
										</select><label for="columnid">Dimension</label><br>
									</div>
								</div>
							</div>
							<!--  need to remove the hide later-->
							<div class="col-md-6 col-capture hidden" id="matchTypedividhide">
								<div class="form-group form-md-line-input">
									<select class="form-control " name="matchType" id="matchType">
										<!-- 	<option value="-1">Choose Match Type</option>
											<option value="Exact">Exact</option> -->
										<option value="Pattern">Lookup</option>
									</select> <label for="matchType">Match Type </label>
									<!--  <span class="help-block">Data Set Name </span> -->
								</div>
							</div>
							
						<!-- Global Filter for First template -->
						<div class = "row hidden" id="filterSelectId">
                            <div class="col-md-6 col-capture" >
                                <div class="form-group form-md-line-input">
                                    <select class="form-control" style="text-align: left;" id="listFilters">
                                    </select>
                                     <label for="form_control_1">Select Global Filter *</label>
                                </div>
                                <br /><span class="required"></span>
                            </div>
                        </div>


                        <div class="row hidden" id="filterConditionId">
                            <div class="col-md-12 col-capture">
                                <div class="form-group form-md-line-input">
                                    <input type="text" class="form-control catch-error"
                                        id="filterCondition" name="filterCondition" readonly="readonly"
                                        placeholder="dataset filter condition">
                                    <label for="form_control_1">Filter Condition *</label>
                                     <input type="hidden" id="filterId" value="" />
                                </div>
                                <br /> <span class="required"></span>
                            </div>
                        </div>

						<div class="row">
							<div class="col-md-6 col-capture hidden"
								id="rightdatasourceidcreateglobal">
								<div class="form-group form-md-line-input">
									<select class="form-control" id="rightsourceidcreateglobal"
										name="rightsource" placeholder="">
										<option value="-1">Select Second Source Template</option>
										<c:forEach var="getlistdatasourcesnameobj"
											items="${getlistdatasourcesname}">
											<option value="${getlistdatasourcesnameobj.idData}">${getlistdatasourcesnameobj.name}</option>
										</c:forEach>
									</select> <label for="form_control_1">Second Source Template *</label>
								</div>
								<br /> <span class="required"></span>
							</div>

							<div class="col-md-6 col-capture hidden"
								id="righttemplatecolumncreateglobal">
								<div class="form-group form-md-line-input">
									<select class="form-control "
										id="rightdatatemplatecolumncreateglobal" name="rightitem"
										placeholder="">
									</select> <label for="form_control_1">Select Data Column *</label>
								</div>
								<br /> <span class="required"></span>
							</div>
						</div>
						
						<!-- Global Filter for Secnond template -->
						<div class = "row hidden" id="rightTemplateFilterSelectId">
                            <div class="col-md-6 col-capture" >
                                <div class="form-group form-md-line-input">
                                    <select class="form-control" style="text-align: left;" id="rightTemplateFiltersList">
                                    </select>
                                     <label for="form_control_1">Select Global Filter for Second Template </label>
                                </div>
                                <br /><span class="required"></span>
                            </div>
                        </div>


                        <div class="row hidden" id="rightTemplateFilterConditionId">
                            <div class="col-md-12 col-capture">
                                <div class="form-group form-md-line-input">
                                    <input type="text" class="form-control catch-error"
                                        id="rightTemplateFilterCondition" name="rightTemplateFilterCondition" readonly="readonly"
                                        placeholder="second dataset filter condition">
                                    <label for="form_control_1">Second Template Filter Condition </label>
                                     <input type="hidden" id="rightTemplateFilterId" value="" />
                                </div>
                                <br /> <span class="required"></span>
                            </div>
                        </div>

						<div class="row hidden" id="matchingRulesId">
							<div class="col-md-12 col-capture">
								<div class="form-group form-md-line-input">
									<input type="text" class="form-control catch-error"
										id="matchingRules" name="matchexpr"
										placeholder="dataSource1.columnName=dataSource2.columnName">
									<label for="form_control_1">Matching Expression *</label>
								</div>
								<br /> <span class="required"></span>
							</div>
						</div>

						<div class="row">
							<div class="col-md-12 col-capture" id="expressionId">
								<div class="form-group form-md-line-input">
									<input type="text" class="form-control catch-error"
										id="expression" name="ruleexpr"> <label
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
											<input type="checkbox" class="md-check" id="aggregateResultsEnabled" name="aggregateResultsEnabled" value="N"> 
											<label for="aggregateResultsEnabled"><span></span> <span class="check"></span>
												<span class="box"></span>Enable Aggregate Results</label>
										</div>
									</div>
								</div>
							</div>
						</div>

						<div class="row" id="configuresynonymsid">
							<div class="col-md-6 col-capture">
								<div class="form-group form-md-checkboxes">
									<div class="md-checkbox-list">
										<div class="md-checkbox">
											<input type="checkbox" class="md-check" id="configurerulesid"
												name="configurerule" value="Y"> <label
												for="configurerulesid"><span></span> <span
												class="check"></span> <span class="box"></span>configure
												synonyms </label>
										</div>
									</div>
								</div>
							</div>

							<div class="hidden" id="incrementaltype">
								<table id="myTable" style="width: 100%">
									<tr>

										<!--    <th>Domain</th> -->
										<th>FieldName</th>
										<th>synonyms</th>

									</tr>
									<tr>


										<td><input class="form-control" type="text"
											id="fieldname"></td>
										<td><input class="form-control" type="text" id="synonyms"></td>
										<td><input class="form-control" type="button" id="addrow"
											class="button" value="Add Row" onclick="addField();"></td>

									</tr>

								</table>

							</div>

						</div>
						<div class="form-actions noborder align-center">
							<button type="submit" id="addnewruleid" class="btn blue">Submit</button>
						</div>

						<div class="col-md-6 col-capture"></div>
					</div>
					
				   </div>

				</div>
			</div>
			<div class="note note-info hidden">Blend created Successfully</div>
			<!-- END SAMPLE FORM PORTLET-->
		</div>
	</div>
</div>
<!-- </div>
END QUICK SIDEBAR
</div> -->
<!-- END CONTAINER -->
<jsp:include page="footer.jsp" /> 

<div class="cd-popup" id="popSample" role="alert">
	<div class="cd-popup-container">
		<table class="table" id="makeEditable">
			<thead>
				<tr>
					<th>Synonyms Name</th>
					<th>User Fields</th>

				</tr>
			</thead>
			<tbody>
				<tr onkeypress="myFunction(this)">
					<td contenteditable="true" id="fieldone"></td>
					<td contenteditable="true" id="Synonymone"></td>

				</tr>
				<tr onkeypress="myFunction(this)">
					<td contenteditable="true" id="fieldrow2"></td>
					<td contenteditable="true" id="synonymFiled2"></td>

				</tr>
				<tr>

				</tr>


			</tbody>


		</table>
		<div>
			<input class="btn btn-primary btn-md" type="button" id="addrow"
				class="btn btn-primary btn-md" value="Add New Row"
				onclick="addField();" /> <input class="btn btn-primary btn-md"
				type="button" id="save" class="button" value="Save" /> <input
				class="btn btn-primary btn-md" type="button" id="closesynpop"
				class="button" value="Close" />
		</div>
	</div>

</div>
<%--  <jsp:include page="footer.jsp" />   --%>
<script>
	//alert(""+domain);
	function myFunction(x) {
		var domain = $("#selectdomain_id").val();
		
		//alert("domain =>"+domain);
		var request = new XMLHttpRequest();
		x.addEventListener("keyup", function(event) {

			var objCell = x.cells;
			
			//alert("objCell =>"+objCell);

			if (event.keyCode === 9 || event.keyCode === 1) {
				event.preventDefault();

				str = objCell[0].innerHTML;

				this.focus();
				var enteredVar = str;

				var url = "./getsynonymslist?val=" + enteredVar + "&domain="
						+ domain;

				try {
					request.onreadystatechange = function() {
						if (request.readyState == 4) {
							var val = request.responseText;
							//alert("value from   "+val);
							if (val != undefined && val != "" && val != 0
									&& val != false && val != null) {

								objCell[1].innerHTML = val;
							}

						}
					}//end of function  
					request.open("POST", url, true);
					request.setRequestHeader('token',$("#token").val());
					request.send();
				} catch (e) {
					alert("Unable to connect to server");
				}

			}

		});
		//for click events
		
		x.addEventListener("mousedown", function(event) {
			
			//alert("In mouse click ................");

			var objCell = x.cells;

		/* 	if (event.keyCode === 9 || event.keyCode === 1) {
				event.preventDefault();
 */
				str = objCell[0].innerHTML;
 
				var enteredVar = str;

				var url = "./getsynonymslist?val=" + enteredVar + "&domain="
						+ domain;

				try {
					request.onreadystatechange = function() {
						if (request.readyState == 4) {
							var val = request.responseText;
							//alert("value from   "+val);
							if (val != undefined && val != "" && val != 0
									&& val != false && val != null) {

								objCell[1].innerHTML = val;
							}

						}
					}//end of function  
					request.open("POST", url, true);
					request.setRequestHeader('token',$("#token").val());
					request.send();
				} catch (e) {
					alert("Unable to connect to server");
				}

			//}

		});

	}
</script>

<script>
/* Added for hiding edit synonym */
document.getElementById("configuresynonymsid").style.display = "none";
/* ----------------------- */


	$('#ruleCategoryid').change(
			function() {
				$('#sqlinternal_msg_div').addClass('hidden').hide();
				$('#aggregate_results_div').addClass('hidden').hide();
				
				if ($('#ruleCategoryid').val() == 'Referential') {
					$('#expressionId').removeClass('hidden').show();
					$('#rightdatasourceidcreateglobal').addClass('hidden')
							.hide();
					$('#righttemplatecolumncreateglobal').addClass('hidden')
							.hide();
					$('#matchingRulesId').addClass('hidden').hide();
					$('#filterConditionId').addClass('hidden').hide();
					$('#rightTemlateFilterConditionId').addClass('hidden').hide();
					$("#expression").attr("placeholder", "Eg: @synonym_name_column1@>5000")
                    .val("").focus().blur();
					$('#labelRuleExpr').text("Rule Expression *");

				} else if (($('#ruleCategoryid').val() == 'Orphan')) {
					$('#matchingRulesId').removeClass('hidden').show();
					$('#rightdatasourceidcreateglobal').removeClass('hidden')
							.show();
					$('#righttemplatecolumncreateglobal').removeClass('hidden')
							.show();
					$('#expressionId').addClass('hidden').hide();
					$('#filterConditionId').addClass('hidden').hide();
					$('#rightTemlateFilterConditionId').addClass('hidden').hide();

					$("#matchingRules").attr("placeholder", "@synonym_name_column1@=dfOther0.column2")
							.val("").focus().blur();
					$('#labelRuleExpr').text("Rule Expression *");
				
				} else if (($('#ruleCategoryid').val() == 'cross referential')) {
					$('#matchingRulesId').removeClass('hidden').show();
					$('#rightdatasourceidcreateglobal').removeClass('hidden')
							.show();
					$('#righttemplatecolumncreateglobal').removeClass('hidden')
							.show();
					$('#expressionId').removeClass('hidden').show();
					$('#filterConditionId').addClass('hidden').hide();
					$('#rightTemlateFilterConditionId').addClass('hidden').hide();

					$("#matchingRules").attr("placeholder", "@synonym_name_column1@=dfOther0.column2")
							.val("").focus().blur();
					$("#expression").attr("placeholder", "Eg: @synonym_name_column1@>5000")
                     .val("").focus().blur();
					$('#labelRuleExpr').text("Rule Expression *");

				}else if (($('#ruleCategoryid').val() == 'conditional orphan')) {
                    $('#matchingRulesId').removeClass('hidden').show();
                    $('#filterConditionId').removeClass('hidden').show();
                    $('#rightTemplateFilterConditionId').removeClass('hidden').show();
                    $('#expressionId').addClass('hidden').hide();

                    $('#rightdatasourceidcreateglobal').removeClass('hidden')
                            .show();
                    $('#righttemplatecolumncreateglobal').removeClass('hidden')
                            .show();
                    $("#matchingRules").attr("placeholder", "@synonym_name_column1@=dfOther0.column2")
                            .val("").focus().blur();
                    $("#filterCondition").attr("placeholder", "Filter Condition")
                                                .val("").focus().blur();
                    $("#rightTemplateFilterCondition").attr("placeholder", "Filter Condition")
                    							.val("").focus().blur();
                    $('#labelRuleExpr').text("Rule Expression *");
                    
                }else if (($('#ruleCategoryid').val() == 'conditional referential')) {
                     $('#expressionId').removeClass('hidden').show();
                     $('#matchingRulesId').addClass('hidden').hide();
                     $('#filterConditionId').removeClass('hidden').show();
 					$('#rightTemlateFilterConditionId').addClass('hidden').hide();
                     $('#rightdatasourceidcreateglobal').addClass('hidden').hide();
                     $('#righttemplatecolumncreateglobal').addClass('hidden').hide();

                     $("#matchingRules").attr("placeholder", "column1=column2")
                             .val("").focus().blur();
                     $("#filterCondition").attr("placeholder", "Filter Condition")
                                                 .val("").focus().blur();
                     $("#expression").attr("placeholder", "Eg: @synonym_name_column1@>5000")
                                                 .val("").focus().blur();
                     $('#labelRuleExpr').text("Rule Expression *");
                     $('#aggregate_results_div').removeClass('hidden').show();
                     
                 }else if (($('#ruleCategoryid').val() == 'conditional sql internal rule')) {
                     $('#expressionId').removeClass('hidden').show();
                     $('#matchingRulesId').addClass('hidden').hide();
                     $('#filterConditionId').removeClass('hidden').show();
 					$('#rightTemlateFilterConditionId').addClass('hidden').hide();
                     $('#rightdatasourceidcreateglobal').addClass('hidden').hide();
                     $('#righttemplatecolumncreateglobal').addClass('hidden').hide();

                     $("#matchingRules").attr("placeholder", "column1=column2")
                             .val("").focus().blur();
                     $("#filterCondition").attr("placeholder", "Filter Condition")
                                                 .val("").focus().blur();
                     $("#expression").attr("placeholder", "Eg: Select * from INTERNALQUERY where @synonym_name_column1@>6000")
                                                 .val("").focus().blur();
                     $('#sqlinternal_msg_div').removeClass('hidden').show();
                     $('#labelRuleExpr').text("Rule Expression *");
                     
                 }else if (($('#ruleCategoryid').val() == 'conditional duplicate check')) {
                     $('#expressionId').removeClass('hidden').show();
                     $('#matchingRulesId').addClass('hidden').hide();
                     $('#filterConditionId').removeClass('hidden').show();
 					$('#rightTemlateFilterConditionId').addClass('hidden').hide();
                     $('#rightdatasourceidcreateglobal').addClass('hidden').hide();
                     $('#righttemplatecolumncreateglobal').addClass('hidden').hide();

                     $("#matchingRules").attr("placeholder", "column1=column2")
                             .val("").focus().blur();
                     $("#filterCondition").attr("placeholder", "Filter Condition")
                                                 .val("").focus().blur();
                     $("#expression").attr("placeholder", "Eg: @synonym_name_column1@,@synonym_name_column2@")
                                                 .val("").focus().blur();
                     $('#sqlinternal_msg_div').addClass('hidden').hide();
                     $('#labelRuleExpr').text("Duplicate Check Columns *");
                     
                 }else if (($('#ruleCategoryid').val() == 'conditional completeness check')) {
                     $('#expressionId').removeClass('hidden').show();
                     $('#matchingRulesId').addClass('hidden').hide();
                     $('#filterConditionId').removeClass('hidden').show();
 					$('#rightTemlateFilterConditionId').addClass('hidden').hide();
                     $('#rightdatasourceidcreateglobal').addClass('hidden').hide();
                     $('#righttemplatecolumncreateglobal').addClass('hidden').hide();

                     $("#matchingRules").attr("placeholder", "column1=column2")
                             .val("").focus().blur();
                     $("#filterCondition").attr("placeholder", "Filter Condition")
                                                 .val("").focus().blur();
                     $("#expression").attr("placeholder", "Eg: @synonym_name_column@")
                                                 .val("").focus().blur();
                     $('#sqlinternal_msg_div').addClass('hidden').hide();
                     $('#labelRuleExpr').text("Completeness Check Column *");
                 }
			});
//  01-08-19 

	$("#rightdatatemplatecolumncreateglobal").change(
							function() {								
								if ($(this).val() != -1) {
									
									var operator_temp = $(this).val();
									var formula_val_temp = $("#matchingRules")
											.val();

									formula_val_temp = formula_val_temp
											.concat(operator_temp);
									$("#matchingRules").val(formula_val_temp);
								}
							});
//

	$("#rightsourceidcreateglobal")
			.change(
					function() {

						var no_error = 1;
						$('.input_error').remove();

						if ($("#rightsourceidcreateglobal").val() == '-1') {
							// $('<span class="input_error" style="font-size:12px;color:red">Please select a source to Continue</span>').insertAfter($('#rightsourceidcreateglobal'));
							no_error = 0;
						}

						if (no_error) {
							var rightsourceidcreateglobal = $(
									"#rightsourceidcreateglobal :selected")
									.text();

							console.log(rightsourceidcreateglobal);
							var form_data = {
								idData : $("#rightsourceidcreateglobal").val(),

							};

							$
									.ajax({
										url : './changeDataColumnAjax',
										type : 'POST',
										headers: { 'token':$("#token").val()},
										datatype : 'json',
										data : form_data,
										success : function(message) {
											var j_obj = $.parseJSON(message);
											if (j_obj.hasOwnProperty('success')) {
												var temp = {};
												column_array = JSON
														.parse(j_obj.success);
												// empty the list from previous selection
												$("#rightdatatemplatecolumncreateglobal").empty();
												$("<option />").attr("value", '-1').html('Select Column').appendTo(
																"#rightdatatemplatecolumncreateglobal");
												temp[this.value] = this.subitems; 

												$.each(
																column_array,
																function(k1, v1) {
																	console.log("start from..");
																	console.log(v1);
																	console.log(k1);
																	var column_details = column_array[k1];
																	//console.log(column_details);
																	var sourcename = rightsourceidcreateglobal;
																	temp_name = column_details;
																	console.log("column_details ::  "+column_details);
																	
																	
																	//sourcename = sourcename.concat('.', temp_name);
																	sourcename = temp_name;
																$("<option />").attr("value",sourcename).html(temp_name).appendTo(
																					"#rightdatatemplatecolumncreateglobal");
																	temp[this.value] = this.subitems;
																});
											} else if (j_obj.hasOwnProperty('fail')) {
												//console.log(j_obj.fail);
												toastr.info(j_obj.fail);
											}
										},
										error : function(xhr, textStatus,
												errorThrown) {
											$('#initial').hide();
											$('#fail').show();
										}
									});
						}
					});

	$('#addnewruleid').click(
					function() {
						var no_error = 1;
						$('.input_error').remove();
						
						if(!checkInputText()){ 
							no_error = 0;
							alert('Vulnerability in submitted data, not allowed to submit!');
						}

						if ($("#rulenameid").val().length == 0) {
							$(
									'<span class="input_error" style="font-size:12px;color:red">Please Enter Global Rule Name</span>')
									.insertAfter($('#rulenameid'));
							no_error = 0;
						}
					
						
						if ($("#selectdomain_id").val() == -1) {
							console.log('selectdomain_id');
							$(
									'<span class="input_error" style="font-size:12px;color:red">Please Select Domain </span>')
									.insertAfter($('#selectdomain_id'));
							no_error = 0;
						}

						var targeturl = $("#targeturl").val();
						if($("#DimensionId").val() == -1){
							   $('<span class="input_error" style="font-size:12px;color:red">Please Select Dimension</span>').insertAfter($('#DimensionId'));
							   no_error = 0;
							 }
						
						if($("#ruleCategoryid").val() == -1){
							   $('<span class="input_error" style="font-size:12px;color:red">Please Select Rule Category</span>').insertAfter($('#ruleCategoryid'));
							   no_error = 0;
							 }
						
						var ruleCategory = $('#ruleCategoryid').val();
						
						if(ruleCategory == 'Orphan' || ruleCategory == 'conditional orphan'){
							
							if ($("#matchingRules").val().length == 0) {
								$('<span class="input_error" style="font-size:12px;color:red">Please enter Match Expression</span>').insertAfter($('#matchingRules'));
								no_error = 0;
							}
							
							if ($("#rightsourceidcreateglobal").val() == -1) {
								$('<span class="input_error" style="font-size:12px;color:red">Please enter second template</span>').insertAfter($('#rightsourceidcreateglobal'));
								no_error = 0;
							}
							
						} 
						else if(ruleCategory == 'Referential' || ruleCategory == 'conditional referential' || ruleCategory == 'conditional sql internal rule'){
							
							if($("#expression").val().length == 0){
								   $('<span class="input_error" style="font-size:12px;color:red">Please enter Rule Expression</span>').insertAfter($('#expression'));
								   no_error = 0;
								 }
						}
						else if(ruleCategory == 'conditional duplicate check'){
							
							if($("#expression").val().length == 0){
								   $('<span class="input_error" style="font-size:12px;color:red">Please enter comma separated list of columns for duplicate check</span>').insertAfter($('#expression'));
								   no_error = 0;
								 }
						}
						else if(ruleCategory == 'conditional completeness check'){
							
							if($("#expression").val().length == 0){
								   $('<span class="input_error" style="font-size:12px;color:red">Please enter column name for completeness check</span>').insertAfter($('#expression'));
								   no_error = 0;
								 }
						}
						else if(ruleCategory == 'cross referential'){
							
							if($("#expression").val().length == 0){
								   $('<span class="input_error" style="font-size:12px;color:red">Please enter Rule Expression</span>').insertAfter($('#expression'));
								   no_error = 0;
							}
							
							if ($("#matchingRules").val().length == 0) {
								$('<span class="input_error" style="font-size:12px;color:red">Please enter Match Expression</span>').insertAfter($('#matchingRules'));
								no_error = 0;
							}
							
							if ($("#rightsourceidcreateglobal").val() == -1) {
								$('<span class="input_error" style="font-size:12px;color:red">Please enter second template</span>').insertAfter($('#rightsourceidcreateglobal'));
								no_error = 0;
							}
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
						
						if (no_error) {
						    if(!$("#filterId").val()){
                                $("#filterId").val(0);
                            }
						    
						    if(!$("#rightTemplateFilterId").val()){
                                $("#rightTemplateFilterId").val(0);
                            }

							//datasourceid 
							var form_data = {
								domain : $("#selectdomain_id").val(),
								name : $("#rulenameid").val(),
								description : $("#descid").val(),
								ruleCategory : $("#ruleCategoryid").val(),
								dataSource2 : $("#rightsourceidcreateglobal").val(),
								expression : $("#expression").val(),
								matchingRules : $("#matchingRules").val(),
								externalDatasetName : $("#rightsourceidcreateglobal").find("option:selected").text(),
								ruleThreshold :  $("#ruleThresholdId").val(),
								dimension : $("#DimensionId").val(),
								filterId : $('#filterId').val(),
								rightTemplateFilterId : $('#rightTemplateFilterId').val(),
								aggregateResultsEnabled : aggregateResultsEnabled
							};
							
							console.log(form_data);

							$.ajax({
										url : './createGlobalRule',
										type : 'POST',
										headers: { 'token':$("#token").val()},
										datatype : 'json',
										data : form_data,

										success : function(message) {
											console.log(message);
											var j_obj = $.parseJSON(message);
											if (j_obj.hasOwnProperty('success')) {
												//alert(message);
												toastr
														.info("Global Rule created successfully");
												setTimeout(
														function() {
															window.location.href = 'viewGlobalRules';
															//window.location.reload();

														}, 1000);
											} else if (j_obj
													.hasOwnProperty('fail')) {
												//alert(message);
												//toastr
														//.info("This Rule Name or Expression already Exists . Please Change ");
												console.log(j_obj);
												toastr
												.info(j_obj['fail']);
												//console.log(j_obj.fail);
												//satoastr.info(j_obj.fail);
											}
										},
										error : function(xhr, textStatus,
												errorThrown) {

											alert("In error");

											$('#initial').hide();
											$('#fail').show();
										}
									});
						}
						return false;
					});

	$('#configurerulesid')
			.click(
					function() {

						var no_error = 1;
						$('.input_error').remove();

						if ($("#expression").val().length == 0) {

							$(
									'<span class="input_error" style="font-size:12px;color:red">Please enter Rule Expression</span>')
									.insertAfter($('#expression'));
							$('input:checkbox').removeAttr('checked');

							no_error = 0;

						} else {
							$('#popSample').addClass('is-visible');
						}

						if ($("#matchingRules").val().length == 0) {
							$(
									'<span class="input_error" style="font-size:12px;color:red">Please enter Match Expression</span>')
									.insertAfter($('#matchingRules'));
							//$('input:checkbox').removeAttr('checked');

							no_error = 0;
						} else {
							$('#popSample').addClass('is-visible');
							//$('input:checkbox').removeAttr('checked');
						}

					});

	$('#popSample').on('click', function(event) {
		if ($(event.target).is('#save')) {

			event.preventDefault();
			$(this).removeClass('is-visible');
		}
	}); 

	function addField(argument) {

		//Try to get tbody first with jquery children. works faster!
		var tbody = $('#makeEditable').children('tbody');

		//Then if no tbody just select your table 
		var table = tbody.length ? tbody : $('#makeEditable');

		table
				.append('<tr onkeypress="myFunction(this)"><td id="a" contenteditable="true"></td><td id="b" contenteditable="true"></td></tr>');

	}

	$("#closesynpop").click(function() {
		$('#popSample').removeClass('is-visible');

	})
	var str1 = "";
	$("#save")
			.click(
					function() {

						var field = document.getElementById("fieldone").innerHTML;
						var Synonymone = document.getElementById("Synonymone").innerHTML;
						/* 	var fieldsecond = document
									.getElementById("fieldsecond").innerHTML;
							var Synonymsecond = document
									.getElementById("Synonymsecond").innerHTML; */
						//alert("fieldsecond :: "+fieldsecond);
						//var length= tab_logic
						var no_error = 1;
						$('.input_error').remove();
						/* if (field != "" && Synonymone == "") {
							//alert("welcome..");
							$(
									'<span class="input_error" style="font-size:12px;color:red">Please enter Synonym</span>')
									.insertAfter($('#Synonymone'));
							no_error = 0;
						} */

						if (!no_error) {
							return false;
						}

						var myTab = document.getElementById('makeEditable');
						var str = "";
						var check="";

						// LOOP THROUGH EACH ROW OF THE TABLE AFTER HEADER.
						for (i = 1; i < myTab.rows.length; i++) {

							// GET THE CELLS COLLECTION OF THE CURRENT ROW.
							var objCells = myTab.rows.item(i).cells;

							// LOOP THROUGH EACH CELL OF THE CURENT ROW TO READ CELL VALUES.
							for (var j = 0; j < objCells.length; j++) {
								
								check=objCells.item(j).innerHTML;
								//alert(" cell =>"+objCells.item(j).innerHTML);
								if(check == ""){
									//alert("empty");
									check = null;
								}
								str = str + '||' + check;
								str1 = check;
								
							//	alert("sr1 =>"+objCells.item(j+1).innerHTML);
															}

						}
					//	alert(str);
						var json_str = JSON.stringify(str);
						var data_1 = {
							singleRowData : json_str,
							domain : $("#selectdomain_id").val(),
							name : $("#rulenameid").val()
						};

						$.ajax({

							type : "POST",
							url : "userrule",
							headers: { 'token':$("#token").val()},
							data : data_1,
							datatype : 'json',

							success : function(message) {

								/* alert(message); */

							},
							error : function(message) {

								/* alert("error"+message);
								 */
							}
						});
					});


$('#selectdomain_id').change(function() {

    		if (($('#ruleCategoryid').val() == 'conditional orphan') || ($('#ruleCategoryid').val() == 'conditional referential') || ($('#ruleCategoryid').val() == 'conditional sql internal rule') || ($('#ruleCategoryid').val() == 'conditional duplicate check') || ($('#ruleCategoryid').val() == 'conditional completeness check')){
    			$('#filterSelectId').removeClass('hidden').show();
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

$('#ruleCategoryid').change(function() {

		  if (($('#ruleCategoryid').val() == 'conditional orphan') || ($('#ruleCategoryid').val() == 'conditional referential') || ($('#ruleCategoryid').val() == 'conditional sql internal rule') || ($('#ruleCategoryid').val() == 'conditional duplicate check') || ($('#ruleCategoryid').val() == 'conditional completeness check')){
		      	$('#filterSelectId').removeClass('hidden').show();
	      }else{
	          $('#filterSelectId').addClass('hidden').hide();
	      }
		  
		  // Display and hide of second template filter
  		  if (($('#ruleCategoryid').val() == 'conditional orphan')){
  			  
  			  $('#rightTemplateFilterSelectId').removeClass('hidden').show();
  			  
  			  if($('#selectdomain_id').val() !== "-1"){
  				populateSecondTemplateFilters();
  			  }
         
  		  }else{
              $('#rightTemplateFilterSelectId').addClass('hidden').hide();
          }
	
          if (($('#selectdomain_id').val() !== "-1") && (($('#ruleCategoryid').val() == 'conditional orphan') || ($('#ruleCategoryid').val() == 'conditional referential') || ($('#ruleCategoryid').val() == 'conditional sql internal rule') || ($('#ruleCategoryid').val() == 'conditional duplicate check') || ($('#ruleCategoryid').val() == 'conditional completeness check'))){
            populateFilters();
          }

  });

 function populateFilters(){
        var form_data = {
            domain : $("#selectdomain_id").val()
        };
       	$.ajax({
                 url : './populateFilters',
                 type : 'GET',
                 beforeSend : function() {
                     $('#loading-progress').removeClass('hidden').show();
                     $("#listFilters").empty();
                     
                     // Add default value
                     $("<option />").attr("value", '-1').html('Select Filter').appendTo("#listFilters");
                 },
                 datatype : 'json',
                 data : form_data,
                 success : function(obj) {
                     $(obj).each(function(i, globalFilterObj) {
                                         $('#listFilters').append($('<option>',
		                                      {
		                                          value : globalFilterObj.filterName,
		                                          text : globalFilterObj.filterId+"_"+globalFilterObj.filterName
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

</script>
