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
							<span class="caption-subject bold "> Create Global Filter </span>
						</div>
					</div>
					<div class="portlet-body form">

						<div class="form-body">
							<div class="row">

								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											id="filternameId" maxlength="45"> <label for="filternameId">Filter Name *</label>

									</div>
									<br /> <span class="required"></span>
								</div>

								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											id="descid" maxlength="50"> <label for="descid">Description</label>
									</div>
									<br /> <span class="required"></span>
								</div>
							</div>

							<div class="row">

                                <div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<!-- <select class="form-control" name="domainfunction"
												id="selectdomain_id">
												<option value="-1">choose Domain</option>
												<option value="Banking">Banking</option>
												<option value="Telecom">Telecom</option>
												<option value="Finance">Finance</option>
												<option value="Medical">Medical</option>
												<option value="Advertisement">Advertisement</option>
												<option value="Others">Others</option>
											</select> <label for="blendfunid">Domain *</label><br> -->

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
							<div class="row">
							    <div class="col-md-6 col-capture">
                                    <div class="form-group form-md-line-input">
                                        <input type="text" class="form-control catch-error"
                                            id="filterConditionId"> <label for="filterConditionId">
                                            Filter Condition</label>
                                    </div>
                                    <span class="required"></span>
                                </div>
							</div>


						</div>
						<div class="form-actions noborder align-center">
							<button type="submit" id="addNewFilterId" class="btn blue">Submit</button>
						</div>

						<div class="col-md-6 col-capture"></div>
					</div>

				</div>
			</div>

			<!-- END SAMPLE FORM PORTLET-->
		</div>
	</div>
</div>
<!-- </div>
END QUICK SIDEBAR
</div> -->
<!-- END CONTAINER -->
<jsp:include page="footer.jsp" />

<%--  <jsp:include page="footer.jsp" />   --%>

<script>

	$('#addNewFilterId')
			.click(
					function() {
						var no_error = 1;
						$('.input_error').remove();

						if(!checkInputText()){
							no_error = 0;
							alert('Vulnerability in submitted data, not allowed to submit!');
						}

						if ($("#filternameId").val().length == 0) {
							$(
									'<span class="input_error" style="font-size:12px;color:red">Please Enter Filter Name</span>')
									.insertAfter($('#filternameId'));
							no_error = 0;
						}


						if ($("#selectdomain_id").val() == -1) {
							console.log('schemaTypeid');
							$(
									'<span class="input_error" style="font-size:12px;color:red">Please Select Domain </span>')
									.insertAfter($('#selectdomain_id'));
							no_error = 0;
						}
						if($("#filterConditionId").val().length == 0){
							   $('<span class="input_error" style="font-size:12px;color:red">Please enter Filter Condition</span>').insertAfter($('#filterConditionId'));
							   no_error = 0;
						}

						if (no_error) {

							//datasourceid
							var form_data = {
								filterName : $("#filternameId").val(),
								description : $("#descid").val(),
								domain : $("#selectdomain_id").val(),
								filterCondition :  $("#filterConditionId").val()
							};
							console.log(form_data);

							$
									.ajax({

										url : './createGlobalFilter',
										type : 'POST',
										headers: { 'token':$("#token").val()},
										datatype : 'json',
										data : form_data,

										success : function(message) {
											console.log(message);
											var j_obj = $.parseJSON(message);
											var status = j_obj.status;
                                            toastr.info(j_obj.message);
                                            if (status == 'success') {
                                                // alert(message);

                                                setTimeout(
                                                        function() {
                                                            window.location.href = 'viewGlobalFilters';
                                                            //window.location.reload();

                                                        }, 1000);
                                            } 
										},
										error : function(xhr, textStatus,
												errorThrown) {

											alert("In error");
											window.location.reload();
										}
									});
						}
						return false;
					});



</script>
