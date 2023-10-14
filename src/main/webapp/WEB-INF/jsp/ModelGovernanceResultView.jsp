
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<jsp:include page="header.jsp" />

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
							<span class="caption-subject bold "> Model Governance </span>
						</div>
					</div>
					<div class="portlet-body form">
						<form method="GET" action="showModelGovernanceData">
							<div class="form-body">
								<div class="row">

									<div class="col-md-6 col-capture non-schema-matching">
										<div class="form-group form-md-line-input">
											<select class="form-control" id="idApp" name="idApp">
											<option value="-1">Choose a Model Governance Application*</option>
												<c:forEach items="${resultmasterdata}" var="resultmasterdata">
													<option value=${resultmasterdata.key}>${resultmasterdata.value}</option>
												</c:forEach>
											</select> <label for="form_control_1">Model Governance Application*</label>
											<!-- <span class="help-block">Data Location  </span> -->
										</div>
										<br /> <span class="required"></span>
									</div>
								</div>

								<div class="form-actions noborder">
									<button type="submit" id="qualitybuttonid" class="btn blue">Submit</button>
								</div>
								<br> <br></div>
						</form>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<jsp:include page="footer.jsp" />
<script>
$('#qualitybuttonid').click(function() {
			var no_error = 1;
			$('.input_error').remove();
			 if ($("#idApp").val() == -1) {
					$('<span class="input_error" style="font-size:12px;color:red">Please Choose a Model Governance Application</span>')
							.insertAfter($('#idApp'));
					no_error = 0;
				} 
			 if (no_error==0) {
				 return false;
			 }
		});

</script>