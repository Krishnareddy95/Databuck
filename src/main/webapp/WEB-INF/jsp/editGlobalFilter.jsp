
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import ="java.util.*"%>
<%@ page import="com.databuck.bean.ruleFields" %>


<jsp:include page="header.jsp" />
<jsp:include page="checkVulnerability.jsp" />

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
							<span class="caption-subject bold "> Edit Filter Condition </span>
						</div>
					</div>
					<div class="portlet-body form">
					<input type="hidden" id="domainFromDBid" value="${globalFilters.domain}" />
						<div class="form-body">
							<div class="row">

								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											id="filterNameId" value="${globalFilters.filterName}"
											readonly="readonly"> <label for="filterNameId">Filter Name*</label>
									</div>
									<br /> <span class="required"></span>
								</div>

								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											id="descid" value="${globalFilters.description}" >
											<label for="descid">Description</label>

									</div>
									<br /> <span class="required"></span>
								</div>
							</div>

							<div class="row">
								<div class="col-md-6 col-capture">
                                    <div class="form-group form-md-line-input">

                                        <input type="text" class="form-control catch-error"
                                            id="domainId" value="${globalFilters.domain}"
                                            readonly="readonly">

                                        </select> <label for="domainId">Domain </label>
                                    </div>
                                </div>
                            </div>
                            <br><br>
								<div class = "row">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control catch-error"
												id="filterConditionId" value="${globalFilters.filterCondition}"> <label for="filterConditionId">
												Filter Condition</label>
										</div>
									</div>
								</div>


							<br />

	                    </div>

						</div>


						<div class="form-actions noborder align-center">
							<button type="submit" id="addnewfilterid" class="btn blue">Submit</button>
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

<input type="hidden" class="form-control catch-error"
	id="filterId" name="filterId"
	value="${globalFilters.filterId}">
<jsp:include page="footer.jsp" />
<script>

	$('#addnewfilterid')
			.click(
					function() {
						var no_error = 1;
						$('.input_error').remove();

						if(!checkInputText()){
							no_error = 0;
							alert('Vulnerability in submitted data, not allowed to submit!');
						}

						if (no_error) {
							var form_data = {
								filterName : $("#filterNameId").val(),
								filterId : $("#filterId").val(),
								description : $("#descid").val(),
								domain : $("#domainFromDBid").val(),
							    filterCondition : $("#filterConditionId").val()
							};

							console.log(form_data);
							//alert(form_data.domain);

							$
									.ajax({

										url : './updateGlobalFilter',
										type : 'POST',
										headers: { 'token':$("#token").val()},
										datatype : 'json',
										data : form_data,

										success : function(message) {
											var j_obj = $.parseJSON(message);
											var status = j_obj.status;
											var err_msg = j_obj.message;
											
											if (status == 'success') {
												// alert(message);
												toastr.info(err_msg);

												setTimeout(
														function() {
															window.location.href = 'viewGlobalFilters';
															//window.location.reload();

														}, 1000);
											} else if (status == 'failed') {
												
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
											}

										},
										error : function(xhr, textStatus,
												errorThrown) {
											window.location.reload();
										}
									});
						}
						return false;
					});


</script>