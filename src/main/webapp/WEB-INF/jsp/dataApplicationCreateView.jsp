
<%@page import="com.databuck.dao.impl.TemplateViewDAOImpl"%>
<%@page import="com.databuck.bean.ListDataSource"%>
<%@page import="java.util.Iterator"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<style>
	select ~ div > ul {
  		width: 450px;
	}
	select ~ div > button {
  		width: 450px !important;
	}
</style>
<script>
	var request;
	var vals = "";
	function sendInfo() {
		var v = document.getElementById("appnameid").value;
		var val = "Name must begin with a letter and cannot contain spaces,special characters"
			var expr = /^[a-zA-Z0-9_]*$/;
		    if (!expr.test(v)) {
		     	document.getElementById('amit').innerHTML = val;
		        return;
		    }
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
			//
			alert(val);
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
							<span class="caption-subject bold "> Create Validation
								Check </span>
						</div>
					</div>
					<div class="alert alert-warning" id="warningid">
						<h5 style="color: red">
							<i class="fa fa-warning"></i> &nbsp Please make sure that
							relevant columns have been selected in Data Template for your
							Validation Check.
						</h5>
					</div>
					<!-- <div class="portlet-body form"> -->
					<div class="portlet-body form">
					<form id="tokenForm1" action="createValidationCheckAjax" method="POST">
					
					<input type="hidden" id="dataLocation" value="${dataLocationName}"/>
					
						

							<div class="form-body">
								<div class="row non-schema-matching">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control catch-error"
												id="appnameid" name="dataset"
												placeholder="Enter Validation Check Name"
												onkeyup="sendInfo()"> <label for="form_control_1">Validation
												Check Name *</label>
											<!--  <span class="help-block">Data Set Name </span> -->
										</div>
										<span id="amit" class="help-block required"></span>
									</div>
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="descriptionid"
												name="description" placeholder="Enter your description">
											<label for="form_control">Description</label>
											<!-- <span class="help-block">Short text about Data Set</span> -->
										</div>
									</div>
								</div>

								<div class="row">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<select class="form-control" id="apptypeid" name="apptype">
												<option value="Data Forensics">Quality Profiling</option>
												<option value="Matching">Matching</option>
												<option value="Rolling DataMatching">DataMatching (Rolling)</option>
												<!-- <option value="Data Matching">Key Measurement Matching</option>
												<option value="Data Matching Group">Group By Matching</option>
													<option value="Statistical Matching">Fingerprint Matching</option> -->
												<option value="File Management">File Management</option>
												<option value="Model Governance">Model Governance</option>
												<option value="Model Governance Dashboard">Model
													Governance Dashboard</option>
												<option value="Schema Matching">Schema Matching</option>
												<option value="File Monitoring">File Monitoring</option>
											</select> <label for="form_control_1">Validation Check Type *</label>
											<!-- <span class="help-block">Data Location  </span> -->
										</div>
										<br /> <span class="required"></span>
									</div>
									<div class="col-md-6 col-capture hidden" id="fileMonitoringTypeDivId">
										<div class="form-group form-md-line-input">
											<select class="form-control" id="fileMonitoringType" name="fileMonitoringType">
												<option value="local">Local Filesytem</option>
												<option value="hdfs">HDFS</option>
												<!-- Will be enabled later after changing logic -->
												<!--  <option value="s3">S3</option> -->
												<option value="s3iamrole">S3 (IAMRole)</option>
												<option value="snowflake">SnowFlake</option>
												<option value="azuredatalakestoragegen2batch">Azure DataLake Storage Gen2(Batch)</option>
											    <option value="aws s3">AWS S3</option>
											</select> <label for="form_control_1">FileMonitoring Type *</label>
											<!-- <span class="help-block">Data Location  </span> -->
										</div>
										<br /> <span class="required"></span>
									</div>
									<div class="col-md-6 col-capture hidden" id="fm_connectionId_Div">
                                        <div class="form-group form-md-line-input">
                                            <select class="form-control" id="fm_connectionId" name="fm_connectionId" style="width: 790;">

                                            </select> <label for="form_control_1">Choose Connection *</label>
                                            <!-- <span class="help-block">Data Location  </span> -->
                                        </div>
                                        <br /> <span class="required"></span>
                                    </div>
									<div class="col-md-6 col-capture hidden" id="Matching">
										<div class="form-group form-md-line-input">
											<select class="form-control" id="matchapptypeid"
												name="matchapptype">
												<option value="Data Matching">Key Measurement Matching</option>
												<!-- 	<option value="Data Matching Group">Group By Matching</option> -->
												<option value="Statistical Matching">Fingerprint Matching</option>													
												<option value="Primary Key Matching">Primary Key Matching</option>
											</select> <label for="form_control_1">Choose Matching Type *</label>
											<!-- <span class="help-block">Data Location  </span> -->
										</div>
										<br /> <span class="required"></span>
									</div>

									<div
										class="col-md-6 col-capture non-schema-matching mgdashboard" id="firstSourceTempId">
										<div class="form-group form-md-line-input">
											<select class="form-control text-left"
													style="text-align: left;" id="lstTable" >
												<option value='-1'> Select Left Source Template </option>
												</select> <label for="form_control_1">First Source Template *</label> <input type="hidden" id="selectedTables"
													name="selectedTables" value="" />
									
											<!-- <span class="help-block">Data Location  </span> -->
										</div>
										<br /> <span class="required"></span>
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
								
								<!-- Added For Executive_Summary Enhancements -->
								<div class="row">
									<div class="col-md-6 col-capture" id="divData_domainId">
										<div class="form-group form-md-line-input">
												<!--<select class="form-control" id="data_domainId"
												name="data_domainId">
												<option value="1">Customer</option>
												<option value="2">Product</option>													
												<option value="3">Transaction</option>-->
												
												<select class="form-control" id="data_domainId" name="data_domainId">
												<c:forEach var="listDataDomainObj" items="${lstDataDomain}">

													<option value="${listDataDomainObj.row_id}">${listDataDomainObj.name}</option>


												</c:forEach>
											
											</select> <label for="form_control_1" id="data_domainId_label">Choose Data Domain *</label>
										</div>
										<br /> <span class="required"></span>
									</div>
									</div>
									</div>
								
								
									<%-- <div
										class="col-md-6 col-capture non-schema-matching hidden schemadashboard">
										<div class="form-group form-md-line-input">
											<select class="form-control" id="schemaid1" name="schemaid1"
												onchange="sendInfo1()">
												<c:forEach var="listDataDomainObj" items="${lstDataDomain}">

													<option value="${listDataDomainObj.row_id}">${listDataDomainObj.name}</option>


												</c:forEach>
											</select> <label for="form_control_1">First Schema *</label>
											<!-- <span class="help-block">Data Location  </span> -->
										</div>
										<span id="amit1" class="help-block required"></span>
									</div> --%>
								
								
								<!-- End of Executive_Summary  -->
								
								<div class="row">
								<div class="col-md-6 col-capture hidden" id="enableContinuousMonitorDivId">
								<div class="md-checkbox-list">
												<div class="md-checkbox">
													<input type="checkbox" class="md-check"
														id="enableContinuousMonitoring" name="enableContinuousMonitoring"
														value="Y"> <label for="enableContinuousMonitoring"><span></span>
														<span class="check"></span> <span class="box"></span>
														Enable Continuous Monitoring </label>
												</div>
												<span class="required"></span>
											</div>
											</div>
									<div
										class="col-md-6 col-capture non-schema-matching hidden schemadashboard">
										<div class="form-group form-md-line-input">
											<select class="form-control" id="schemaid1" name="schemaid1"
												onchange="sendInfo1()">
												<c:forEach var="listdataschemaObj" items="${listdataschema}">

													<option value="${listdataschemaObj.idDataSchema}">${listdataschemaObj.schemaName}</option>


												</c:forEach>
											</select> <label for="form_control_1">First Schema *</label>
											<!-- <span class="help-block">Data Location  </span> -->
										</div>
										<span id="amit1" class="help-block required"></span>
									</div>
									<div
										class="col-md-6 col-capture non-schema-matching hidden schemadashboard">
										<div class="form-group form-md-line-input">
											<select class="form-control" id="schemaid2" name="schemaid2"
												onchange="sendInfo2()">
												<c:forEach var="listdataschemaObj" items="${listdataschema}">
													<option value="${listdataschemaObj.idDataSchema}">${listdataschemaObj.schemaName}</option>
												</c:forEach>
											</select> <label for="form_control_1">Second Schema *</label>
										</div>
										<!-- <span class="help-block">Data Location  </span> -->
										<span id="amit2" class="help-block required"></span>
									</div>
									<div
										class="col-md-6 col-capture non-schema-matching hidden schemadashboard metadata">
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
										class="col-md-6 col-capture non-schema-matching hidden schemadashboard metadata">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="schema_rcid"
												name="schema_rc" placeholder="Enter Record count threshold" value = "0.0">
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
										class="col-md-6 col-capture non-schema-matching hidden schemadashboard">
										<div class="form-group form-md-line-input">
											<select class="form-control" id="schematypeid"
												name="schematypename" onchange="sendInfo3()">
												<option value="both">Schema Matching for Both
													MetaData and Record Count</option>
												<option value="metadata">Schema Matching for
													MetaData Only</option>
												<option value="rc">Schema Matching for Record Count
													Only</option>

											</select> <label for="form_control_1">Select schema matching
												type *</label>
										</div>
										<!-- <span class="help-block">Data Location  </span> -->
										<span id="amit2" class="help-block required"></span>
									</div>
										<div
										class="col-md-6 col-capture non-schema-matching hidden schemadashboard">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="prefix1"
												name="prefix1" placeholder="Enter prefix1">
											<label for="form_control">prefix1</label>
										</div>
										<!-- <span class="help-block">Data Location  </span> -->
										<span id="amit2" class="help-block required"></span>
									</div>
								</div>
								<div class="row">
									<div
										class="col-md-6 col-capture non-schema-matching hidden schemadashboard">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="prefix2"
												name="prefix2" placeholder="Enter prefix2">
											<label for="form_control">prefix2</label>
										</div>
										<!-- <span class="help-block">Data Location  </span> -->
										<span id="amit2" class="help-block required"></span>
									</div>
								</div>
							<!-- </div> -->
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
											<!-- <span class="help-block">Short text about Data Set</span> -->
										</div>
									</div>

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
								
								
								
									<div class="row">
									
										<div class="col-md-6 col-capture hidden" id="divWindowTime">
								<div class="form-group">
									<label class="control-label col-md-2">Window Time</label>
									<div class="col-md-4">
										<div class="input-group">
											<select class="form-control" id="windowTime" name="windowTime">
												<option value="120">2 min</option>
												<option value="300">5 min</option>
												<option value="420">7 min</option>
												<option value="600">10 min</option>
												
												<option value="900">15 min</option>
												<option value="1200">20 min</option>
												<option value="1500">25 min</option>
												<option value="1800">30 min</option>
											</select>
										
											</div>
									</div>
								</div>
							</div>
						
								
							<div class="col-md-6 col-capture hidden" id="divStartTime">
								<div class="form-group">
									<label class="control-label col-md-2">Start Time : </label>
									<div class="col-md-4">
										<div class="input-group">
											<input type="text" id="startTime" name="startTime"
												class="form-control timepicker timepicker-24"> <span
												class="input-group-btn">
												<button class="btn default" type="button">
													<i class="fa fa-clock-o"></i>
												</button>
											</span>
										</div>
									</div>
								</div>
							</div>
							
								<div class="col-md-6 col-capture hidden" id="divEndTime">
								<div class="form-group">
									<label class="control-label col-md-2">End Time : </label>
									<div class="col-md-4">
										<div class="input-group">
											<input type="text" id="endTime" name="endTime"
												class="form-control timepicker timepicker-24"> <span
												class="input-group-btn">
												<button class="btn default" type="button">
													<i class="fa fa-clock-o"></i>
												</button>
											</span>
										</div>
									</div>
								</div>
							</div>
							
						</div>
								
								
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





<jsp:include page="footer.jsp" />
<head>
<script src="./assets/global/plugins/jquery-ui.min.js"></script>
<script
	src="./assets/global/plugins/bootstrap-multiselect.js"></script>
<link rel="stylesheet"
	href="./assets/global/plugins/bootstrap-multiselect.css">

<script src="./assets/global/plugins/jquery.form.js"></script>


<script type="text/javascript">
	$(document).ready(function() {
		//$('#lstTable').multiselect();
		$('#lstTable').multiselect({
			maxHeight : 200,
			buttonWidth : '500px',
			includeSelectAllOption : true,
			enableFiltering : true,
			
			nonSelectedText : 'Select Template'
			 
		});
	});
</script>
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
						
						if(!checkInputText())
						{
							alert('Vulnerability in submitted data, not allowed to submit!');
							no_error = 0;
						}

						
						if ($("#appnameid").val().length == 0) {
							$(
									'<span class="input_error" style="font-size:12px;color:red">Please enter Validation Check name</span>')
									.insertAfter($('#appnameid'));
						
							no_error = 0;
						}
						
						if ($("#apptypeid").val().length == 0) {
							
						//	alert("In apptypeid");
							
							$(
									'<span class="input_error" style="font-size:12px;color:red">Please choose Validation Check Type</span>')
									.insertAfter($('#apptypeid'));
							no_error = 0;
						}

						if ($("#fm_connectionId").val() == -1) {

						//	alert("in fm_connectionId");
							$(
									'<span class="input_error" style="font-size:12px;color:red">Please choose Connection</span>')
									.insertAfter($('#fm_connectionId'));
							no_error = 0;
						}
						if(isTemplateMandatory()){
						
							
							
						if ($("#lstTable").val() == -1) {
							
						//	alert("in lstTbl");
							$(
									'<span class="input_error" style="font-size:12px;color:red">Please choose Template</span>')
									.insertAfter($('#lstTable'));
							no_error = 0;
						}
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
						
						/* $('#tokenForm1').ajaxForm({
			        		headers: {
			        			'token':$("#token").val()
			        			}
			        	}); */
			        	
					
					
						
					});
	
	
	function isTemplateMandatory(){
		 
		//alert("in istemp");
		var templateMandatory = false;

		var valueOfValidationCheckType = document.getElementById("apptypeid").value;
		
		//alert("valueOfValidationCheckType ->"+valueOfValidationCheckType);
		
		if(valueOfValidationCheckType == 'Schema Matching'){
			//alert("In Schema Matching IF");
			
			templateMandatory = false;
			
		}else if(valueOfValidationCheckType == 'File Monitoring'){
			//alert("In File Monitoring IF");
			
			templateMandatory = false;
			
		}else{
		//	alert("In valueOfValidationCheckType ELSE");
			
			templateMandatory = true;
		}
	//	alert("apptype val ->"+templateMandatory);
		
		return templateMandatory;
		//alert("apptype val ->"+templateMandatory);
		
	} 

	
	
	
	// search option for template
	var selectedOption;
	$('#lstTable').on('change', function() {
		
		var currentSelection;
  
		var currentValues = $(this).val();
		if (currentValues.length == 1) {
			currentSelection = currentValues;
		}
		selectedOption = $(this).val();
		
		$("#selectedTables").val(JSON.stringify(selectedOption));
		//alert("SelectedOption=>"+selectedOption);
		
		
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
			$('#divData_domainId').addClass('hidden');
			$('#fm_connectionId_Div').addClass('hidden');
		} else {
			$('#Matching').addClass('hidden');
			$('#thresholdId').addClass('hidden');
			$('#incId').addClass('hidden');
			$('#fm_connectionId_Div').addClass('hidden');
		}
		if (selectedVal == 'Model Governance Dashboard') {
			$('.mgdashboard').addClass('hidden');
			$('#divData_domainId').addClass('hidden');
	        $('#fm_connectionId_Div').addClass('hidden');
		} else {
			$('.mgdashboard').removeClass('hidden');
			$('#divData_domainId').removeClass('hidden');
			$('#fm_connectionId_Div').addClass('hidden');
		}
		if (selectedVal == 'Schema Matching') {
			$('.mgdashboard').addClass('hidden');
			$('.schemadashboard').removeClass('hidden');
			$('#warningid').addClass('hidden');
			$('#data_domainId').addClass('hidden');
			$('#data_domainId_label').addClass('hidden');
			$('#fm_connectionId_Div').addClass('hidden');
		} else {
			$('.mgdashboard').removeClass('hidden');
			$('.schemadashboard').addClass('hidden');
			$('#data_domainId').removeClass('hidden');
			$('#data_domainId_label').removeClass('hidden');
			$('#fm_connectionId_Div').addClass('hidden');
		}

		//changes for File Monitoring -------
		if(selectedVal == 'File Monitoring'){
			//alert("chng");
			 $('#firstSourceTempId').addClass('hidden');
			 $('#fileMonitoringTypeDivId').removeClass('hidden');
			 $('#enableContinuousMonitorDivId').removeClass('hidden');
			 $('#warningid').addClass('hidden');
			 $('#data_domainId').addClass('hidden');
			 $('#data_domainId_label').addClass('hidden');
			 $('#fm_connectionId_Div').addClass('hidden');
		}
		else { $('#fileMonitoringTypeDivId').addClass('hidden');
		 $('#enableContinuousMonitorDivId').addClass('hidden');
		 $('#fm_connectionId_Div').addClass('hidden');}
	});
	$('#matchapptypeid').change(function() {
		$this = $(this);
		var selectedVal = $this.val();
		console.log("selectedValllllllllllll=>"+selectedVal);
		if (selectedVal == 'Data Matching') {
			$('#warningid').addClass('hidden');
			$('#thresholdId').removeClass('hidden');
			/* $('#incId').removeClass('hidden'); */
		    $('#fm_connectionId_Div').addClass('hidden');
		}else if(selectedVal == 'Primary Key Matching'){
			$('#warningid').removeClass('hidden');
			/* $('#thresholdId').addClass('hidden'); */
		    $('#fm_connectionId_Div').addClass('hidden');
		}else {
			$('#warningid').removeClass('hidden');
			$('#thresholdId').addClass('hidden');
		    $('#fm_connectionId_Div').addClass('hidden');
	}});
	$('#fileMonitoringType').change(function() {
    		$this = $(this);
    		var selectedVal = $this.val();

    		if (selectedVal == 'snowflake' || selectedVal == 'azuredatalakestoragegen2batch' || selectedVal == 'aws s3') {
    		    $('#fm_connectionId_Div').removeClass('hidden');
    		    var selectedS3Con='';
                if ( $("#fileMonitoringType").val()=='aws s3')
                     selectedS3Con="S3 Batch";
      		    else
      		        selectedS3Con=selectedVal;
    		   	var form_data = {
                   			location : selectedS3Con,
                          // times: new Date().getTime()
                       };

                       $.ajax({
                         url: './changeLocationForDataTemplateAjax',
                         type: 'POST',
                         headers: {'token':$("#token").val()},
                         datatype:'json',
                         data: form_data,
                         success: function(message){
                             var j_obj = $.parseJSON(message);
                                 if(j_obj.hasOwnProperty('success'))
                                 {
                                   var temp = {};
                                   column_array = JSON.parse( j_obj.success);
                                   // empty the list from previous selection
                                   $("#fm_connectionId").empty();
                                   $("<option class='hidden'/>")
                                   .attr("value", "-1")
                                   .html("Select")
               					.appendTo("#fm_connectionId");
                                   $.each(column_array, function(k1, v1)
                                   {
                                   	console.log(v1);
                                   	console.log(k1);
                                       var column_details = column_array[k1];
                                       //console.log(column_details);
                                       var sourcename ;
                                       temp_name = k1+"_"+column_details;
                                       //sourcename = sourcename.concat('.', temp_name);
                                       sourcename = temp_name;
                                       $("<option />")
                                           .attr("value", k1)
                                           .html(temp_name)
                                           .appendTo("#fm_connectionId");
                                           temp[this.value] = this.subitems;
                                   });
                                   $('#fm_connectionId').multiselect('rebuild');

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
    	    }else{
    	       $('#fm_connectionId_Div').addClass('hidden');
    	    }
    });
	
	//changes for kafka
	
	$('#sourceid').on('change', function() {
		var value = $(this).val();
	//	alert(value);
		
		//alert("Value of DataLoc ->"+$('#dataLocation').val());
		var dataLoc = $('#dataLocation').val();
		
		if(dataLoc == "Kafka"){
			
			//alert("In kafka....");
			$('#divStartTime').removeClass('hidden').show();
			$('#divEndTime').removeClass('hidden').show();
			$('#divWindowTime').removeClass('hidden').show();
			
		}
		
	});
	//call for get template name
	$(document).ready(function(){
	
		        
		$.ajax({
			url : './templatename',
			type : 'GET',
			
			datatype : 'json',
			//data : form_data,
			success : function(obj) {
				//alert(obj);
				
				
				$(obj).each(function(i, item) {
						var idDataVal = item.substring(0, item.indexOf("-"));
						var Tname = item.substring((item.indexOf("-")) + 1, item.length);
									//test
									//alert("idData=>"+idDataVal+"TName=>"+TnameVal)
									$('#lstTable')
											.append(
													$(
															'<option>',
															{
															value : idDataVal,
															//text : Tname+"_"+idDataVal
															text : idDataVal+"_"+Tname
															}));
								});
				$('#lstTable').multiselect(
						'rebuild');
				$('#lstTable').multiselect({
					includeSelectAllOption : true
				});
				
			},
			error : function() {

			},
		
		});
		        
		});

</script>
<!-- <script>
        $(document).ready(function() {
        	$('#tokenForm1').ajaxForm({
        		headers: {'token':$("#token").val()}
        	});
        });
</script> -->
</head>