
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<script>
	var request;
	var vals = "";
	function sendInfo() {
		var v = document.getElementById("appnameid").value;
		//var v=document.vinform.Database.value;  
		var url = "./duplicateValidationCheckName?val=" + v;
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
<script>
	var request;
	var vals = "";
	function sendInfo1() {
		console.log('sendinfo1');
		var e = document.getElementById("schemaid1");
		var idDataSchema = e.options[e.selectedIndex].value;
		//alert("in ajax code"+v);
		//var v=document.vinform.Database.value;  
		var url = "./checkSchemaType?idDataSchema=" + idDataSchema;
		//alert("in ajax code");
		if (window.XMLHttpRequest) {
			request = new XMLHttpRequest();
		} else if (window.ActiveXObject) {
			request = new ActiveXObject("Microsoft.XMLHTTP");
		}
		try {
			request.onreadystatechange = getInfo1;
			request.open("POST", url, true);
			request.setRequestHeader('token',$("#token").val());
			request.send();
		} catch (e) {
			alert("Unable to connect to server");
		}
	}

	function getInfo1() {
		if (request.readyState == 4) {
			var val = request.responseText;
			//alert("getInfo");
			//alert(val);
			//var sal="Database already exists";
			//console.log(val);
			document.getElementById('amit1').innerHTML = val;
			if (val != "") {
				vals = val;
				//alert("please enter another name");
				return false;
			}
			//alert("check123");
		}
	}
</script>
<script>
	var request;
	var vals = "";
	function sendInfo2() {
		//console.log('sendinfo2');
		var e = document.getElementById("schemaid2");
		var idDataSchema = e.options[e.selectedIndex].value;
		//alert("in ajax code"+v);
		//var v=document.vinform.Database.value;  
		var url = "./checkSchemaType?idDataSchema=" + idDataSchema;
		//alert("in ajax code");
		if (window.XMLHttpRequest) {
			request = new XMLHttpRequest();
		} else if (window.ActiveXObject) {
			request = new ActiveXObject("Microsoft.XMLHTTP");
		}
		try {
			request.onreadystatechange = getInfo2;
			request.open("POST", url, true);
			request.setRequestHeader('token',$("#token").val());
			request.send();
		} catch (e) {
			alert("Unable to connect to server");
		}
	}

	function getInfo2() {
		if (request.readyState == 4) {
			var val = request.responseText;
			//alert("getInfo");
			//alert(val);
			//var sal="Database already exists";
			//console.log(val);
			document.getElementById('amit2').innerHTML = val;
			if (val != "") {
				vals = val;
				//alert("please enter another name");
				return false;
			}
			//alert("check123");
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
							<span class="caption-subject bold "> Customize Schema Matching Validation
								Check </span>
						</div>
					</div>
				<!-- for get value schematching by pravin  -->
					 <input
						type="hidden" id="Schemacount" value="${entityColumn}">
						<!-- for threshold type value  -->
						<input type="hidden"
						id="thresholdtype" value="${recordCountAnomaly}">
				<!-- 	<div class="alert alert-warning" id="warningid">
						<h5 style="color: red">
							<i class="fa fa-warning"></i> &nbsp Please make sure that
							relevant columns have been selected in Data Template for your
							Validation Check.
						</h5>
					</div> -->
					<form  id="myForm" action="updateSchemaMatching" method="POST"   ">
						<div class="portlet-body form">

							<div class="form-body">
								<div class="row non-schema-matching">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control catch-error"
												id="appnameid" name="dataset"
												placeholder="Enter Validation Check Name" readonly="readonly" value="${name}"
												onkeyup="sendInfo()"> <label for="form_control_1">Validation
												Check Name *</label>
											<!--  <span class="help-block">Data Set Name </span> -->
										</div>
										<span id="amit" class="help-block required"></span>
									</div>
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="descriptionid"
												name="description" placeholder="Enter your description"   value="${description}"    >
											<label for="form_control">Description</label>
											<!-- <span class="help-block">Short text about Data Set</span> -->
										</div>
									</div>
								</div>

								<div class="row">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<select class="form-control" id="apptypeid" name="apptype" readonly>
											<option value="Schema Matching">Schema Matching</option>
											
												
											</select> <label for="form_control_1">Validation Check Type *</label>
											<!-- <span class="help-block">Data Location  </span> -->
										</div>
										<br /> <span class="required"></span>
									</div>
									<div class="col-md-6 col-capture hidden" id="Matching">
										<div class="form-group form-md-line-input">
											<select class="form-control" id="matchapptypeid"
												name="matchapptype">
												<option value="Data Matching">Key Measurement
													Matching</option>
												<!-- 	<option value="Data Matching Group">Group By Matching</option> -->
												<option value="Statistical Matching">Fingerprint
													Matching</option>
											</select> <label for="form_control_1">Choose Matching Type *</label>
											<!-- <span class="help-block">Data Location  </span> -->
										</div>
										<br /> <span class="required"></span>
									</div>

									<div
										class="col-md-6 col-capture non-schema-matching mgdashboard">
									<%-- 	<div class="form-group form-md-line-input">
											<select class="form-control" id="sourceid" name="sourceid">
												<c:forEach var="getlistdatasourcesnameobj"
													items="${getlistdatasourcesname}">
													<option value="${getlistdatasourcesnameobj.idData}">${getlistdatasourcesnameobj.name}</option>
												</c:forEach>
											</select> <label for="form_control_1">First Source Template *</label>
											<!-- <span class="help-block">Data Location  </span> -->
										</div>
										<br /> <span class="required"></span> --%>
									</div>


									<div class="col-md-6 col-capture hidden" id="thresholdId">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control catch-error"
												id="threshold_id" name="threshold_id"> <label
												for="form_control_1">Threshold</label>
										</div>
										<br /> <span class="required"></span>
									</div>

								</div>
								<div class="row">
									<div
										class="col-md-6 col-capture non-schema-matching schemadashboard">
										<div class="form-group form-md-line-input">
											<select class="form-control" id="schemaid1" name="schemaid1"
												onchange="sendInfo1()">
												<option value="-1">${idDataSchemaNmeLeft}
													</option>
												<c:forEach var="listdataschemaObj" items="${listdataschema1}">

													<option value="${listdataschemaObj.idDataSchema}">${listdataschemaObj.schemaName}</option>


												</c:forEach>
											</select> <label for="form_control_1">First Schema *</label>
											<!-- <span class="help-block">Data Location  </span> -->
										</div>
										<span id="amit1" class="help-block required"></span>
									</div>
									<div
										class="col-md-6 col-capture non-schema-matching schemadashboard">
										<div class="form-group form-md-line-input">
											<select class="form-control" id="schemaid2" name="schemaid2"
												onchange="sendInfo2()">
												<option value="-1">${idDataSchemaNmeRight}
													</option>
												<c:forEach var="listdataschemaObj" items="${listdataschema1}">
												
												
													<option value="${listdataschemaObj.idDataSchema}">${listdataschemaObj.schemaName}</option>
												</c:forEach>
											</select> <label for="form_control_1">Second Schema *</label>
										</div>
										<!-- <span class="help-block">Data Location  </span> -->
										<span id="amit2" class="help-block required"></span>
									</div>
									<div
										class="col-md-6 col-capture non-schema-matching schemadashboard metadata">
										<div class="form-group form-md-line-input">
											<select class="form-control" id="schematypethresholdid"
												name="schema_thresholdtype">
												
												<option value="absolute">Absolute threshold</option>
												<option value="percentage">Percentage threshold</option>
											</select> <label for="form_control_1">Threshold type *</label>
										</div>
										<!-- <span class="help-block">Data Location  </span> -->
										<span id="amit2" class="help-block required"></span>
									</div>
									<div
										class="col-md-6 col-capture non-schema-matching schemadashboard metadata">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="schema_rcid"
												name="schema_rc" placeholder="Enter Record count threshold" value = "${RecordCountThreshold }">
											<label for="form_control">Record count threshold *</label>
											<!-- <span class="help-block">Short text about Data Set</span> -->
										</div>
										<span id="count" class="help-block required"></span>
										<!-- <span class="help-block">Data Location  </span> -->
									</div>
									<span id="count" class="help-block required"></span>
								</div>
								<div class="row">
									<div
										class="col-md-6 col-capture non-schema-matching schemadashboard">
										<div class="form-group form-md-line-input">
											<select class="form-control" id="schematypeid"
												name="schematypename"  onchange="sendInfo3()">
												<option value="both">Schema Matching for  Record Count And
													MetaData Only</option>
												<option value="MetaData">Schema Matching for
													MetaData Only</option>
												<option value="Record Count">Schema Matching for Record Count
													Only</option>
 
											</select> <label for="form_control_1">Select schema matching
												type *</label>
										</div>
										<!-- <span class="help-block">Data Location  </span> -->
										<span id="amit2" class="help-block required"></span>
									</div>
										<div
										class="col-md-6 col-capture non-schema-matching schemadashboard">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="prefix1"
												name="prefix1" placeholder="Enter prefix1" value="${prefix1}">
											<label for="form_control">prefix1</label>
										</div>
										<!-- <span class="help-block">Data Location  </span> -->
										<span id="amit2" class="help-block required"></span>
									</div>
								</div>
								<div class="row">
									<div
										class="col-md-6 col-capture non-schema-matching schemadashboard">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="prefix2"
												name="prefix2" placeholder="Enter prefix2" value ="${prefix2}">
											<label for="form_control">prefix2</label>
										</div>
										<!-- <span class="help-block">Data Location  </span> -->
										<span id="amit2" class="help-block required"></span>
									</div>
								</div>
							</div>
							<div class="row schema-matching" style="display: none;">
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<select class="form-control" id="schemaoneid" name="schemaone"
											placeholder="">

										</select> <label for="schemaoneid">First Schema</label>
									</div>
									<br /> <span class="required"></span>
								</div>

								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<select class="form-control" id="schematwoid" name="schematwo">

										</select> <label for="schematwoid">Second Schema</label>
										<!-- <span class="help-block">Data Location  </span> -->
									</div>
									<br /> <span class="required"></span>
								</div>
							</div>

							<div class="form-body">
								<div class="row">


<!-- 
									<div class="col-md-6 col-capture hidden" id="incId">
										<div class="md-checkbox-list">
											<input type="checkbox" class="form-control catch-error"
												id="incremental_Matching_Id" name="incremental_Matching_Id"
												value="Y"> <label for="form_control_1">Incremental
												Matching</label>
										</div>
										<span class="required"></span>
									</div>

									<div class="col-md-6 col-capture hidden" id="s4_form_id">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="dateformatid"
												name="dateformatid" placeholder="Enter Date Format">
											<label for="form_control">Date Format</label>
											<span class="help-block">Short text about Data Set</span>
										</div>
									</div> -->

									<div class="form-body hidden">
										<!-- <div class="row"> -->


										<!-- <div class="col-md-6 col-capture">
											<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="leftsliceend"
												name="leftsliceend" placeholder="Enter Slice End" value="-1">
											<label for="form_control">Slice End</label>
											<span class="help-block">Short text about Data Set</span>
										</div>
										</div>
									</div> -->
									</div>
									<!-- <div class="row">
										<div class="col-md-12">
											<div class="col-md-4">
												<div class="form-group form-md-checkboxes">
													<div class="md-checkbox-list">
														<div class="md-checkbox">
															<input type="checkbox" class="md-check"
																id="statistical_Matching_Id"
																name="statistical_Matching_Id" value="Y"> <label
																for="statistical_Matching_Id">Statistical
																Matching<span></span><span class="check"></span><span
																class="box"></span>
															</label>
														</div>
													</div>
												</div>
											</div>
										</div>
									</div> -->


									<!-- <div class="form-body hidden" id="statisticalDivId">
									<div class="row">
										<div class="col-md-12">
											<div class="col-md-3" style="padding-top: 10px;">
												<div class="form-group form-md-checkboxes">
													<div class="md-checkbox-list">
														<div class="md-checkbox">
															<input type="checkbox" id="recordCount"
																name="recordCount" class="md-check" value="Y"> <label
																for="recordCount"> Record Count<span></span> <span
																class="check"></span> <span class="box"></span>
															</label>
														</div>
													</div>
												</div>
											</div>
											<div class="col-md-4">
												<div class="form-group form-md-line-input">
													<select name="recordCountType" id="recordCountType"
														class="form-control recordCountType">
														<option value="Percentage">Percentage</option>
														<option value="Absolute Difference">Absolute
															Difference</option>
													</select>
												</div>
											</div>
											<div class="col-md-5">
												<div class="form-group form-md-line-input">
													<input type="text" class="form-control"
														id="recordCountThreshold" name="recordCountThreshold"
														placeholder="Threshold"> <label
														for="recordCountThreshold"></label>
												</div>
											</div>
										</div>
									</div>

									<div class="row">
										<div class="col-md-12">
											<div class="col-md-3" style="padding-top: 10px;">
												<div class="form-group form-md-checkboxes">
													<div class="md-checkbox-list">
														<div class="md-checkbox">
															<input type="checkbox" id="measurementSum"
																name="measurementSum" class="md-check" value="Y">
															<label for="measurementSum"> Measurement Sum<span></span>
																<span class="check"></span> <span class="box"></span>
															</label>
														</div>
													</div>
												</div>
											</div>
											<div class="col-md-4">
												<div class="form-group form-md-line-input">
													<select name="measurementSumType" id="measurementSumType"
														class="form-control">
														<option value="Percentage">Percentage</option>
														<option value="Absolute Difference">Absolute
															Difference</option>
													</select>
												</div>
											</div>
											<div class="col-md-5">
												<div class="form-group form-md-line-input">
													<input type="text" class="form-control"
														id="measurementSumThreshold"
														name="measurementSumThreshold" placeholder="Threshold">
													<label for="measurementSumThreshold"></label>
												</div>
											</div>
										</div>
									</div>

									<div class="row">
										<div class="col-md-12">
											<div class="col-md-3" style="padding-top: 10px;">
												<div class="form-group form-md-checkboxes">
													<div class="md-checkbox-list">
														<div class="md-checkbox">
															<input type="checkbox" id="measurementMean"
																name="measurementMean" class="md-check" value="Y">
															<label for="measurementMean"> Measurement Mean<span></span>
																<span class="check"></span> <span class="box"></span>
															</label>
														</div>
													</div>
												</div>
											</div>
											<div class="col-md-4">
												<div class="form-group form-md-line-input">
													<select name="measurementMeanType" id="measurementMeanType"
														class="form-control">
														<option value="Percentage">Percentage</option>
														<option value="Absolute Difference">Absolute
															Difference</option>
													</select>
												</div>
											</div>
											<div class="col-md-5">
												<div class="form-group form-md-line-input">
													<input type="text" class="form-control"
														id="measurementMeanThreshold"
														name="measurementMeanThreshold" placeholder="Threshold">
													<label for="measurementMeanThreshold"></label>
												</div>
											</div>
										</div>
									</div>
									<div class="row">
										<div class="col-md-12">
											<div class="col-md-3" style="padding-top: 10px;">
												<div class="form-group form-md-checkboxes">
													<div class="md-checkbox-list">
														<div class="md-checkbox">
															<input type="checkbox" id="measurementStdDev"
																name="measurementStdDev" class="md-check" value="Y">
															<label for="measurementStdDev"> Measurement Std
																Dev<span></span> <span class="check"></span> <span
																class="box"></span>
															</label>
														</div>
													</div>
												</div>
											</div>
											<div class="col-md-4">
												<div class="form-group form-md-line-input">
													<select name="measurementStdDevType"
														id="measurementStdDevType" class="form-control">
														<option value="Percentage">Percentage</option>
														<option value="Absolute Difference">Absolute
															Difference</option>
													</select>
												</div>
											</div>
											<div class="col-md-5">
												<div class="form-group form-md-line-input">
													<input type="text" class="form-control"
														id="measurementStdDevThreshold"
														name="measurementStdDevThreshold" placeholder="Threshold">
													<label for="measurementStdDevThreshold"></label>
												</div>
											</div>
										</div>
									</div>
									<div class="row">
										<div class="col-md-12">
											<div class="col-md-3" style="padding-top: 10px;">
												<div class="form-group form-md-checkboxes">
													<div class="md-checkbox-list">
														<div class="md-checkbox">
															<input type="checkbox" id="groupBy" name="groupBy"
																class="md-check" value="Y"> <label for="groupBy">
																Group By<span></span> <span class="check"></span> <span
																class="box"></span>
															</label>
														</div>
													</div>
												</div>
											</div>
											<div class="col-md-4">
												<div class="form-group form-md-line-input">
													<select name="groupByType" id="groupByType"
														class="form-control">
														<option value="Percentage">Percentage</option>
														<option value="Absolute Difference">Absolute
															Difference</option>
													</select>
												</div>
											</div>
											<div class="col-md-5">
												<div class="form-group form-md-line-input">
													<input type="text" class="form-control"
														id="groupByThreshold" name="groupByThreshold"
														placeholder="Threshold"> <label
														for="groupByThreshold"></label>
												</div>
											</div>
										</div>
									</div>

								</div> -->
								</div>
								<div class="form-actions noborder align-center">
									<button type="submit" id="appcreateid" class="btn blue">Submit</button>
								</div>
								<br> <br>

							</div>
					</form>
				</div>
			</div>
		</div>
	</div>
</div>
</div>

<jsp:include page="footer.jsp" />
<script type="text/javascript">
	$('#schematypeid').change(function() {
		$this = $(this);
		var selectedVal = $this.val();
		console.log(selectedVal);
		if (selectedVal == 'metadata') {
			$('.metadata').addClass('hidden');

		} else {
			$('.metadata').removeClass('hidden');
		}
	});

	$('#incremental_Matching_Id').click(function() {
		var check = "";
		if ($(this).is(':checked')) {
			// alert("Are you sure?");
			check = "Y";
			$('#s4_form_id').removeClass('hidden').show();
		} else {
			check = "N";
			$('#s4_form_id').addClass('hidden');

		}

	});

	$('#statistical_Matching_Id').click(function() {
		var check = "";
		if ($(this).is(':checked')) {
			// alert("Are you sure?");
			check = "Y";
			$('#statisticalDivId').removeClass('hidden').show();
		} else {
			check = "N";
			$('#statisticalDivId').addClass('hidden');

		}

	});
	$('#appcreateid')
			.click(
					function() {
						var no_error = 1;
						$('.input_error').remove();

						if ($("#appnameid").val().length == 0) {
							$(
									'<span class="input_error" style="font-size:12px;color:red">Please enter Validation Check name</span>')
									.insertAfter($('#appnameid'));
							no_error = 0;
						}
						if ($("#apptypeid").val().length == 0) {
							$(
									'<span class="input_error" style="font-size:12px;color:red">Please choose Validation Check Type</span>')
									.insertAfter($('#apptypeid'));
							no_error = 0;
						}
						if ($("#sourceid").val().length == 0) {
							$(
									'<span class="input_error" style="font-size:12px;color:red">Please choose a Source Type</span>')
									.insertAfter($('#sourceid'));
							no_error = 0;
						}

						if ($("#schema_rcid").val().length == 0) {
							$(
									'<span class="input_error" style="font-size:12px;color:red">Please enter Record count threshold</span>')
									.insertAfter($('#schema_rcid'));
							no_error = 0;
						}
						var amit = $('#amit').html();
						// alert("outside"+amit1)
						console.log(amit);
						if (amit == "") {

						} else {
							console.log("inside if");
							//toaster.info("Schema created successfully");
							//$('<span class="input_error" style="font-size:12px;color:red">already exist Database Name</span>').insertAfter($('#descriptionid'));
							no_error = 0;
						}
						var amit1 = $('#amit1').html();
						// alert("outside"+amit1)
						console.log(amit1);
						if (amit1 == "") {

						} else {
							console.log("inside if");
							//toaster.info("Schema created successfully");
							//$('<span class="input_error" style="font-size:12px;color:red">already exist Database Name</span>').insertAfter($('#descriptionid'));
							no_error = 0;
						}
						var amit2 = $('#amit2').html();
						// alert("outside"+amit1)
						console.log(amit2);
						if (amit2 == "") {

						} else {
							console.log("inside if");
							//toaster.info("Schema created successfully");
							//$('<span class="input_error" style="font-size:12px;color:red">already exist Database Name</span>').insertAfter($('#descriptionid'));
							no_error = 0;
						}
						var count = $('#count').html();
						// alert("outside"+amit1)
						console.log(amit);
						if (count == "") {

						} else {
							console.log("inside if");
							//toaster.info("Schema created successfully");
							//$('<span class="input_error" style="font-size:12px;color:red">already exist Database Name</span>').insertAfter($('#descriptionid'));
							no_error = 0;
						}
						
						if (!no_error) {
							// console.log('ok');

							return false;
						} else {
							$('.btn').addClass('hidden');

						}
					});

	$('#apptypeid').change(function() {
		$this = $(this);
		var selectedVal = $this.val();
		console.log(selectedVal);
		/* if (selectedVal == 'Data Matching') {
			$('#warningid').addClass('hidden');
			$('#thresholdId').removeClass('hidden');
		} else {
			$('#warningid').removeClass('hidden');
			$('#thresholdId').addClass('hidden');
		} */
		if (selectedVal == 'Matching') {
			$('#Matching').removeClass('hidden');
			$('#thresholdId').removeClass('hidden');
			$('#incId').removeClass('hidden');
		} else {
			$('#Matching').addClass('hidden');
			$('#thresholdId').addClass('hidden');
			$('#incId').addClass('hidden');
		}
		if (selectedVal == 'Model Governance Dashboard') {
			$('.mgdashboard').addClass('hidden');
		} else {
			$('.mgdashboard').removeClass('hidden');
		}
		if (selectedVal == 'Schema Matching') {
			$('.mgdashboard').addClass('hidden');
			$('.schemadashboard').removeClass('hidden');
		} else {
			$('.mgdashboard').removeClass('hidden');
			$('.schemadashboard').addClass('hidden');
		}
  
	});
	$('#matchapptypeid').change(function() {
		$this = $(this);
		//$this= "${name}"
		
		var selectedVal = $this.val();
		console.log(selectedVal);
		if (selectedVal == 'Data Matching') {
			$('#warningid').addClass('hidden');
			$('#thresholdId').removeClass('hidden');
			/* $('#incId').removeClass('hidden'); */

		} else {
			$('#warningid').removeClass('hidden');
			$('#thresholdId').addClass('hidden');
			$('#incId').addClass('hidden');
		}
	});
	
	/* $('#schematypeid').ready(function() {
		//var schemaType = $("#schematypeid").val();
		var schemaType = $("${entityColumn}").val();
		$("#schematypeid").val(schemaType);
		if (schemaType == 'Recount Count') {
			$('#schematypeid').removeClass('hidden').show();
			$('#OracleXE').addClass('hidden');
			$('#krb5confdiv').addClass('hidden');
			$('#labelURI').text("Uri");
			$('#labelServiceName').text("Service Name");
			$('#labelDomain').text("Domain");
			}
		if (schemaType == 'metadata') {
			$('#schematypeid').removeClass('hidden').show();
			$('#OracleXE').addClass('hidden');
			$('#krb5confdiv').addClass('hidden');
			$('#labelURI').text("Uri");
			$('#labelServiceName').text("Service Name");
			$('#labelDomain').text("Domain");
			}
		}); */
	
</script>
<!-- <script> 
        // wait for the DOM to be loaded 
        $(document).ready(function() { 
        	$('#myForm').ajaxForm({
        		headers: {'token':$("#token").val()}
        	}); 
        }); 
 </script> --> 
<script>
//by pravin
$(document).ready( function () {
	myfunction();
});


function myfunction(){
	
	
	
	//let databaseVal = document.getElementsByName('Recount');
	
	//For Select Schema Matching Type
	var databaseVal =document.getElementById("Schemacount").value;
	
		
	
	var sel = document.getElementById("schematypeid");
	//window.alert("sel=>"+sel.options.length);
	for(var i= 0;i<sel.options.length;i++){
		
		
		var optVal = sel.options[i].value;
		//alert(" sel.options[2].value :::::"+ sel.options[i].value)
		/* alert(" sel.options[0].value :::::"+ sel.options[0].value)
		alert(" sel.options[1].value :::::"+ sel.options[1].value)
		alert(" sel.options[2].value :::::"+ sel.options[2].value) */
		if(sel.options[i].value == databaseVal){
			//alert("In IF...........");
			  // document.getElementById("schematypeid").selectedIndex = i;
		sel.options[i].selected=true;
	}
}
	
	//For Threshold Type
	var threshold = document.getElementById("thresholdtype").value;
	var Thresholdtype =document.getElementById("schematypethresholdid");
	
	
	for(var i =0;i<Thresholdtype.options.length-1;i++){
		
		//alert(" value come for db : ");
		if(Thresholdtype.options[i].value == threshold){
			//alert("in if  "+Thresholdtype.options.length);
			//alert("in if  "+Thresholdtype.options[i].value)
			Thresholdtype.options[i].selected =true;
		}
			
	}
	
	}



</script>
