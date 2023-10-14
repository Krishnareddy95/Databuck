<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<!-- BEGIN CONTENT -->
<div class="page-content-wrapper">
	<!-- BEGIN CONTENT BODY -->
	<div class="page-content">
		<div class="row">
			<div class="col-md-6" style="width: 100%;">
				<!-- BEGIN SAMPLE FORM PORTLET-->
				<div class="portlet light bordered init">
					<div class="portlet-title">

						<div class="caption font-red-sunglo">
							<span class="caption-subject bold ">Import/Export Global Rules</span>
						</div>
					</div>

					<div class="portlet-body form">

						<div class="row">
							<div class="col-md-3">
								<div class="form-group form-md-checkboxes">
									<div class="md-checkbox-list">
										<div class="md-checkbox">
											<input type="checkbox" id="importChk_id" class="md-check"
												value="import" checked style="visibility: visible;">
											<label for="import">Import</label>
										</div>
									</div>
								</div>
							</div>

							<div class="col-md-3">
								<div class="form-group form-md-checkboxes">
									<div class="md-checkbox-list">
										<div class="md-checkbox">
											<input type="checkbox" id="exportChk_id" class="md-check"
												value="export" style="visibility: visible;"> 
											<label for="export">Export</label>
										</div>
									</div>
								</div>
							</div>
							
							<div class="col-md-3">
							</div>
							
							<div class="col-md-3">
								<a onClick="validateHrefVulnerability(this)"  href="getGlobalRulesImportSampleFile">
									<img alt="" src="./assets/img/downloadCsvIcon.png"  onClick="" width='45px' height='45px'/><b>Sample import file</b>
								</a>
							</div>
						</div>

						<!--  Import Global Rules -->
						<div id="importRuleDiv">
							<form id="myForm" action="importGlobalRulesFromCSV" enctype="multipart/form-data" method="POST"
								accept-charset="utf-8">

								<div class="form-body">
									<div class="row">
										<div class="col-md-6 col-capture">
											<div class="form-group form-md-line-input">
												<select class="form-control" id="importDomainId" name="domainId">
													<option value="-1">Select</option>
													<c:forEach var="domainObj" items="${domainList}">
														<option value="${domainObj.domainId}">${domainObj.domainName}</option>
													</c:forEach>
												</select> 
												<label for="form_control_1">Select Domain *</label>
											</div>
										</div>
									</div>

									<div class="row">
										<div class="col-md-12 col-capture">
											<div class="form-group form-md-line-input">
												<div class="row">
													<div class="col-md-6 col-capture" id="uploaddivid">
														<div class="form-group form-md-line-input">
															<input type="file" class="form-control" name="dataupload" id="dataupload_id" /> 
															<label for="form_control" id="fileType">Upload Global Rules File</label>
														</div>
														<br />
													</div>
												</div>
											</div>
										</div>
									</div>

									<div class="row">
										<div class="form-actions noborder" style="padding-left: 15px">
											<button type="submit" id="importBtnId" onclick="return validateImportRulesForm()" 
														class="btn blue">Import Rules To Domain</button>
										</div>
									</div>

								</div>
							</form>
						</div>
						<!--  Import Global Rules END -->
						
						<!--  Export Global Rules -->
						<form id="myForm" action="exportGlobalRulesToCSV" method="POST" accept-charset="utf-8">
							<div id="exportRuleDiv" class="hidden">
								<div class="form-body">
									<div class="row">
										<div class="col-md-6 col-capture">
											<div class="form-group form-md-line-input">
												<select class="form-control" id="exportDomainId" name="exportDomainId">
													<option value="-1">Select</option>
													<c:forEach var="domainObj" items="${domainList}">
														<option value="${domainObj.domainId}">${domainObj.domainName}</option>
													</c:forEach>
												</select> 
												<label for="form_control_1">Select Domain *</label>
											</div>
											<input type="hidden" id="exportDomainName_id" name="exportDomainName">
										</div>
									</div>
									<br />
									<div class="row">
										<div class="form-actions noborder" style="padding-left: 15px">
											<button type="submit" id="exportBtnId" onclick="return validateExportRulesForm()" 
													class="btn blue">Export Rules From Domain</button>
										</div>
									</div>
								</div>
							</div>
						</form>
						<!--  Export Global Rules END -->
					</div>

				</div>
			</div>
		</div>
	</div>
</div>

<jsp:include page="footer.jsp" />

<head>
<script type="text/javascript">
	$('#importChk_id').change(function() {
		console.log("entered");
		if ($(this).prop("checked") == true) {
			$('#exportChk_id').prop("checked", false);
			$("#exportRuleDiv").addClass("hidden");
			$("#importRuleDiv").removeClass("hidden").show();
		} else {
			$("#importRuleDiv").addClass("hidden");
			$('#exportChk_id').prop("checked", true);
			$("#exportRuleDiv").removeClass("hidden").show();
		}
	});

	$('#exportChk_id').click(function() {
		if ($(this).prop("checked") == true) {
			$('#importChk_id').prop("checked", false);
			$("#importRuleDiv").addClass("hidden");
			$("#exportRuleDiv").removeClass("hidden").show();
		} else {
			$("#exportRuleDiv").addClass("hidden");
			$('#importChk_id').prop("checked", true);
			$("#importRuleDiv").removeClass("hidden").show();
		}
	});
	
	$('#exportChk_id').change(function() {
		var domainName = $( "#exportDomainId option:selected" ).text()
		$('#exportDomainName_id').val(domainName)
	});
	
	function validateExportRulesForm(){
		var no_error = 1;
		$('.input_error').remove();
		
		if ($("#exportDomainId").val() <= 0) {
			$('<span class="input_error" style="font-size:12px;color:red">Please select domain for export</span>')
					.insertAfter($('#exportDomainId'));
			no_error = 0;
		}
		
		if (no_error == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	function validateImportRulesForm(){
		var no_error = 1;
		$('.input_error').remove();
		
		if ($("#importDomainId").val() <= 0) {
			$('<span class="input_error" style="font-size:12px;color:red">Please select domain for import</span>')
					.insertAfter($('#importDomainId'));
			no_error = 0;
		}
		
		if ($("#dataupload_id").val().length == 0) {
			$('<span class="input_error" style="font-size:12px;color:red">Please upload file to import</span>')
					.insertAfter($('#dataupload_id'));
			no_error = 0;
		}
		
		if (no_error == 0) {
			return false;
		} else {
			return true;
		}
	}
</script>
<!-- <script> 
        // wait for the DOM to be loaded 
        $(document).ready(function() { 
        	$('#myForm').ajaxForm({
        		headers: {'token':$("#token").val()}
        	}); 
        }); 
 </script> -->
</head> 
