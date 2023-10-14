
<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
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
							<span class="caption-subject bold ">Create Derived
								Column/Filter for Extended Template : ${name}</span>
						</div>
					</div>
					<div class="portlet-body form">

						<div class="form-body">
							<div class="row">

								<input type="hidden" id="blendid" value="${idDataBlend }" /> <input
									type="hidden" id="nameid" value="${name }" /> <input
									type="hidden" id="descriptionid" value="${lbdescription }" />
									
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											value="${name}" readonly> <label for="blendnameid">Extended
											Template Name *</label>
										<!--  <span class="help-block">Data Set Name </span> -->
									</div>
									<br /> <span class="required"></span>
								</div>

								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											value="${lbdescription}" readonly> <label
											for="blenddescid">Description</label>
										<!--  <span class="help-block">Data Set Name </span> -->
									</div>
									<br /> <span class="required"></span>
								</div>
								<?php } ?>
							</div>

							<div class="row">
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											value="${lsdescription}" readonly> <label
											for="blenddescid">Data Template</label>
										<!--  <span class="help-block">Data Set Name </span> -->
									</div>
									<br /> <span class="required"></span>
								</div>

								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<select class="form-control" id="blendtypeid" name="blendtype">
											<option value="Derived Column">Derived Column</option>
											<option value="Filter">Filter</option>
											<option value="Row Add">Row Add</option>
										</select> <label for="blenddescid">Choose Extension Type*</label>
										<!--  <span class="help-block">Data Set Name </span> -->
									</div>
									<br /> <span class="required"></span>
								</div>
							</div>
						</div>
						<!-- closing form body -->

						<div id="derived_form_id">

							<div class="row">
								<div class="col-md-6 col-capture" id="derived_input_id">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											id="formulanameid" name="formulaname"> <label
											for="formulanameid">Derived Column Name *</label>
										<!--  <span class="help-block">Data Set Name </span> -->
									</div>
									<br />
								</div>
								<div class="col-md-6 col-capture" id="columnCategory">
									<div class="form-group form-md-line-input">
										<select class="form-control " name="columnCategoryid"
											id="columnCategoryid">
											<option value="-1">Choose Column Category</option>
											<option value="Simple">Simple</option>
											<option value="Complex">Complex/Subgroup</option>
											<option value="ComplexEval">ComplexEval</option>
										</select> <label for="columnCategory">Derived Column Category </label>
										<!--  <span class="help-block">Data Set Name </span> -->
									</div>
									<br />
								</div>
								<div class="col-md-6 col-capture" id="colValueTypeDivId">
									<div class="form-group form-md-line-input">
										<select class="form-control" name="colValueTypeId"
											id="colValueTypeId">
											<option value="String">Choose Column Value Type</option>
											<option value="String">String</option>
											<option value="Numeric">Numeric</option>
										</select><label for="columnCategory">Column Value Type</label>
									</div>
								</div>
							</div>
							<br />
							<div class="col-md-6 col-capture" style="display: none"
								id="filter_input_id">
								<div class="form-group form-md-line-input">
									<input type="text" class="form-control catch-error"
										id="filternameid" name="filtername"> <label
										for="formulanameid">Filter Name *</label>
									<!--  <span class="help-block">Data Set Name </span> -->
								</div>
								<br /> <br />
							</div>
							<div class="row" id="sourcecolumnid">
								<div class="col-md-12">
									<div class="col-md-4 col-capture">
										<div class="form-group form-md-line-input">
											<select class="form-control idcolops" id="displayname"
												name="blendcolumn">

												<option value="-1">Choose Column</option>
												<c:forEach var="listdatadefinitionobj"
													items="${listdatadefinition}">

													<option value="${listdatadefinitionobj.idColumn}">${listdatadefinitionobj.displayName}</option>

												</c:forEach>
												<%-- <input type="hidden" id="selectedidColumnid"
												value="${listdatadefinitionobj.idColumn}" /> --%>
											</select> <label for="blendcolumnid">Source Column Name</label><br>
										</div>
									</div>
									<input type="hidden" id="sourceid" value="${idData}" />

									<div class="col-md-4 col-capture" id="filter_opr_id">
										<div class="form-group form-md-line-input">
											<select class="form-control idcolops" name="filterfunction">
												<option value="-1">Choose Filter Function</option>
												<option value="CONCAT(">CONCAT(</option>
												<option value="SUBSTR(">SUBSTR(</option>
												<option value="ROUND(">ROUND(</option>
												<option value="MIN(">MIN(</option>
												<option value="MAX(">MAX(</option>
												<option value="AVG(">AVG(</option>
											</select> <label for="blendfunid">Choose Filter Function</label><br>
										</div>
									</div>



									<div class="col-md-4 col-capture">
										<div class="form-group form-md-line-input">
											<select class="form-control idcolops" name="blendoperator">
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
											</select> <label for="blendoperid">Choose Operator</label><br>
										</div>
									</div>
								</div>
							</div>
							<hr />
							<div class="row" id="add_row_id">
								<div class="col-md-12 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											id="formulaid" name="formula"> <label
											for="form_control_1">Column Expression *</label>
									</div>
									<br /> <span class="required"></span>
								</div>
							</div>
						</div>

						<div class="row hidden" id="colvaluedivid">
							<div class="col-md-12 col-capture">
								<div class="form-group form-md-line-input">
									<input type="text" class="form-control catch-error"
										id="colvalueid" name="colvalueid"> <label
										for="form_control_1">Column Value </label>
								</div>
								<br /> <span class="required"></span>
							</div>
						</div>
					</div>

 									<div class="row hidden" id="rowaddid">
                                        <div class="col-md-12 col-capture">
                                            <div class="form-group form-md-line-input">
                                                <select class="selectpicker" name="bsource[]" id="bsourceid" multiple multiple title="Choose Single/Multiple Data Source">
                                                <c:forEach var="getlistdatasourcesnameobj" items="${getlistdatasourcesname}">
                                                     <option value="${getlistdatasourcesnameobj.idData}">${getlistdatasourcesnameobj.name}</option>
                                                </c:forEach>
                                            </select>
                                                <!-- <label for="columnid">Data Template for Modification</label><br> -->
                                            </div>
                                        </div>
                                    </div>

					<div class="form-actions noborder align-center hidden"
						id="addColId">
						<button type="submit" id="addAdditionalColumnsId" class="btn blue">Add
							Additional Columns</button>
					</div>
					<br />
					<br />
					<div class="form-actions noborder align-center">
						<button type="submit" id="blendcreatecolumnid" class="btn blue">Submit</button>
					</div>
				</div>
			</div>


			<div class="row">
				<div class="col-md-12">
					<!-- BEGIN EXAMPLE TABLE PORTLET-->
					<div class="portlet light bordered">
						<div class="portlet-body">
							<table class="table table-striped table-bordered table-hover"
								id="showDataTable">
								<thead>
									<tr>
										<th>Column Name</th>
										<th>Display Name</th>
										<th>Format</th>
										<th>Data Profile Check</th>
										<th>Group_By</th>
										<th>Duplicate_key</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="listdatadefinition"
										items="${listdatadefinition}">
										<tr class="source-item">
											<span style="display: none" class="source-item-id">${listdatadefinition.idColumn}</span>
											</td>
											<td class="source-display-name">${listdatadefinition.displayName}</td>

											<td class="source-display-name">${listdatadefinition.displayName}</td>
											<td class="source-format">${listdatadefinition.format}</td>


											<td class="source-kbe">${listdatadefinition.KBE}</td>
											<td class="source-dgroup">${listdatadefinition.dgroup}</td>
											<td class="source-dupkey">${listdatadefinition.dupkey}</td>

										</tr>
									</c:forEach>
								</tbody>
							</table>
						</div>
					</div>
					<!-- END EXAMPLE TABLE PORTLET-->
				</div>
			</div>
			<!-- closing table row -->

		</div>
		<div class="note note-info hidden">Csv uploaded Successfully</div>
		<!-- END SAMPLE FORM PORTLET-->
	</div>
</div>
</div>
<!-- END QUICK SIDEBAR -->
</div>
<!-- END CONTAINER -->
<jsp:include page="footer.jsp" />
<script>
	$('#addAdditionalColumnsId').click(
					function() {
						var no_error = 1;
						$('.input_error').remove();
						
						if(!checkInputText()){ 
							no_error = 0;
							alert('Vulnerability in submitted data, not allowed to submit!');
						}

						if ($("#blendtypeid").val() == 'Filter') {
							if ($("#filternameid").val().length == 0) {
								$(
										'<span class="input_error" style="font-size:12px;color:red">Please enter a name for the Filter</span>')
										.insertAfter($('#filternameid'));
								no_error = 0;
							}
						} else if ($("#blendtypeid").val() == "Derived Column") {
							if ($("#formulanameid").val().length == 0) {
								$(
										'<span class="input_error" style="font-size:12px;color:red">Please enter a name for the Blend Column</span>')
										.insertAfter($('#formulanameid'));
								no_error = 0;
							}
						} else if ($("#blendtypeid").val() == "Row Add") {
							console.log($('#bsourceid').selectpicker('val'));
							if (($('#bsourceid').selectpicker('val')) == null) {
								$(
										'<span class="input_error" style="font-size:12px;color:red">Please Select a Source</span>')
										.insertAfter($('#bsourceid'));
								no_error = 0;
							}
						}
						if (($("#formulaid").val().length == 0)
								&& ($("#blendtypeid").val() != "Row Add")) {
							$(
									'<span class="input_error" style="font-size:12px;color:red">Please enter a formula to be saved</span>')
									.insertAfter($('#formulaid'));
							no_error = 0;
						}
						
						var targeturl = $("#targeturl").val();
						if (no_error) {
							var form_data = {
								extensionType : $("#blendtypeid").val(),
								derivedColumnName : $("#formulanameid").val(),
								columnCategory : $("#columnCategoryid").val(),
								columnValueType : $("#colValueTypeId").val(),
								listDataDefinitionIdCol : $("#displayname").val(),
								expression : $("#formulaid").val(),
								columnValue : $("#colvalueid").val(),
								idDataBlend : $("#blendid").val(),
								idData : $("#sourceid").val(),
								filterName : $("#filternameid").val(),
								name : $("#nameid").val(),
								description : $("#descriptionid").val(),
								rowAdd : "abc",
							// times: new Date().getTime()
							};
							/*if( $("#blendtypeid").val() == 'Filter' )
							{
							    form_data['blendtype'] = 'Filter';
							    form_data['formulaname'] = $("#filternameid").val();
							}
							else if( $("#blendtypeid").val() == 'Derived Column' )
							{
							    form_data['blendtype'] = "Derived Column";
							    form_data['formulaname'] = $("#formulanameid").val();
							}
							else if( $("#blendtypeid").val() == "Row Add" )
							{
							    form_data['blendtype'] = "Row Add";
							    var dataToString = $('#bsourceid').selectpicker('val');
							    form_data['sourcedata'] = dataToString.toString();
							}*/
							console.log(form_data);
							$
									.ajax({
										url : './saveDataInListDataBlendTables',
										type : 'POST',
										datatype : 'json',
										headers: {'token':$("#token").val()},
										data : form_data,
										success : function(data) {
											if (data != "") {
												toastr.info('Modified Template Created Successfully');
												setTimeout(function() {
													//window.location.reload();
													$("#colvalueid").val('');
													$("#formulaid").val('');
												}, 1000);
											} else {
												toastr.info('There was a problem.');
											}
										},
										error : function(xhr, textStatus,
												errorThrown) {
											toastr.info('Item updated with error');
											console.log(xhr + " " + textStatus+ " " + errorThrown);
										}
									});
						}
						return false;
					});
</script>