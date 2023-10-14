<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<head>
<link rel="stylesheet"
	href="./assets/global/plugins/bootstrap.min.css">
<script
	src="./assets/global/plugins/jquery.min.js"></script>
<script
	src="./assets/global/plugins/bootstrap.min.js"></script>
</head>
<script>
	var request;
	var vals = "";
	function sendInfo() {
		var v = document.getElementById("schemaNameid").value;
		//var v=document.vinform.Database.value;  
		var url = "./duplicateSchemaName?val=" + v;
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
			//alert("Unable to connect to server");
		}
	}

	function getInfo() {
		if (request.readyState == 4) {
			var val = request.responseText;
			//alert("getInfo");
			//alert(val);
			var sal = "Database already exists";
			//console.log(val);
			document.getElementById('amit').innerHTML = val;
			/* 	if (val!="") {
					vals=val;
					alert("please enter another name");
					return false;
				}
				alert("check123");
			 */
		}
	}
</script>

<!-- ClockPicker Stylesheet -->
<link rel="stylesheet" type="text/css"
	href="dist/bootstrap-clockpicker.min.css">
<!-- ClockPicker script -->
<script type="text/javascript" src="dist/bootstrap-clockpicker.min.js"></script>
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
							<span class="caption-subject bold "> File Monitoring
								Configuration </span>
						</div>
					</div>
					<font size="3">${result}</font>
					<%-- ${message} --%>
					<div class="portlet-body form">
						<div class="form-body">
							<div class="row">
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											id="bucketNameId" name="bucketName"
											placeholder=" Enter the Bucket Name" onkeyup="sendInfo()">
										<label for="form_control_1">Bucket_Name </label>
										<!--  <span class="help-block">Data Set Name </span> -->
									</div>
									<span class="help-block required"
										style="font-size: 12px; color: red" id="amit"></span>
									<!-- <span id="amit" class="help-block required"></span> -->
								</div>

								<div class="col-md-6 col-capture">

									<div class="form-group form-md-line-input">
										<select class="form-control" id="frequencyId"
											name="frequencyType">
											<option value="-1">Please Choose One</option>
											<option value="daily">Daily</option>

											<option value="hourly">Hourly</option>
											<option value="weekly">Weekly</option>


										</select> <label for="form_control_1">Frequency</label> <span
											class="help-block">Short text about Data Set</span>
									</div>



									<span class="help-block required"
										style="font-size: 12px; color: red" id="amit"></span>
									<!-- <span id="amit" class="help-block required"></span> -->
								</div>



							</div>
						</div>

						<div class="form-body">
							<div class="row">


								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											id="folderPathId" name="folderPath" placeholder="Folder Path">
										<label for="form_control_1">Folder_Path</label>
									</div>
									<br /> <span class="required"></span>
								</div>



								<span id="amit" class="help-block required"></span>

								<div class="col-md-6 col-capture">

									<div class="form-group form-md-line-input" id="divDayOfCheckId"
										style="display: none;">
										<select class="form-control" id="dayOfCheckId"
											name="dayOfCheck">
											<option value="-1">Please Choose One</option>
											<option value="mon">Monday</option>
											<option value="tue">Tuesday</option>
											<option value="wed">Wednesday</option>
											<option value="thur">Thursday</option>
											<option value="fri">Friday</option>
											<option value="sat">Saturday</option>
											<option value="sun">Sunday</option>


										</select> <label for="form_control_1">Day_Of_Check</label> <span
											class="help-block">Short text about Data Set</span>
									</div>

									<span class="help-block required"
										style="font-size: 12px; color: red" id="amit"></span>
									<!-- <span id="amit" class="help-block required"></span> -->
								</div>

							</div>

							<div class="form-body">
								<div class="row">

									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="filePatternId"
												name="filePattern" placeholder="File Pattern"
												onkeyup="sendInfo()"> <label for="form_control">File_Pattern</label>
											<!-- <span class="help-block">Short text about Data Set</span> -->
										</div>
									</div>

									<div class="col-md-6 col-capture">

										<div class="col-md-6 col-capture">
											<div class="form-group form-md-line-input" id="divTimeOfChk"
												style="display: none;">

												<input type="text" class="form-control" id="timeOfCheckId"
													name="timeOfCheck" placeholder="Time Of Check"> <label
													for="form_control">Time_Of_Check</label>
											</div>
										</div>
									</div>
								</div>
							</div>
							<div class="row">
								<div class="col-md-6 col-capture">

									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											id="fileCountId" name="fileCount" placeholder="File Count">
										<label for="form_control_1" id="labelURI">File_Count</label>
										<!--  <span class="help-block">Data Set Name </span> -->
									</div>
									<br /> <span class="required"></span>

									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											id="fileSizeThresholdId" name="fileSizeThreshold"
											placeholder="FileSize Threshold"> <label
											for="form_control_1" id="labelURI">File_Size_Threshold</label>
										<!--  <span class="help-block">Data Set Name </span> -->
									</div>
									<br /> <span class="required"></span>

								</div>

							</div>



							<div class="form-actions noborder align-center"></div>
							<p align="center">
								<button type="submit" id="submitId" class="btn blue">Submit</button>
							</p>

							<!-- </form> -->
							<!-- </form> -->
						</div>
					</div>
					<!-- END SAMPLE FORM PORTLET-->
				</div>
			</div>
		</div>
	</div>
	<!-- END QUICK SIDEBAR -->


	<jsp:include page="footer.jsp" />
	<head>
<!-- ClockPicker script -->
<script type="text/javascript" src="dist/bootstrap-clockpicker.min.js"></script>

<script type="text/javascript">
	$('.clockpicker').clockpicker();
</script>

<!--  
<option value="daily">Daily</option>
<option value="hourly">Hourly</option>
<option value="weekly">Weekly</option>

-->

<script type="text/javascript">
	$('#frequencyId').change(function() {

		var freqType = $('#frequencyId').val();

		if (freqType == 'daily' || freqType == 'hourly') {

			alert("freqType ---=>" + freqType);

			$('#divTimeOfChk').removeClass('hidden').show();

			/*    $('#divTimeOfChk').removeClass('hidden').show(); */

		}

		if (freqType == 'weekly') {

			alert("freqType ---=>" + freqType);

			$('#divTimeOfChk').removeClass('hidden').show();
			//divDayOfCheckId

			$('#divDayOfCheckId').removeClass('hidden').show();

		}
	});
</script>
	</head>