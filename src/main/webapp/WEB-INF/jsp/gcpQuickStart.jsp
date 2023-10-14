<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />

<head>
<script src="./assets/global/plugins/bootstrap.min.js"></script>
<script src="./assets/global/plugins/jquery-3.3.1.slim.min.js"></script>
<script src="./assets/global/plugins/popper.min.js"></script>
<script src="./assets/global/plugins/popper.min.js"></script>
<link rel="stylesheet" href="./assets/global/plugins/bootstrap.min.css">
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
			var sal = "Database already exists";
			document.getElementById('amit').innerHTML = val;
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
							<span class="caption-subject bold "> Create Data Schema </span>
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
											id="schemaNameid" name="schemaName" placeholder=" Enter the SchemaName" onkeyup="sendInfo()"> 
											<label for="form_control_1">Schema Name </label>
										<!--  <span class="help-block">Data Set Name </span> -->
									</div>
									<span class="help-block required"
										style="font-size: 12px; color: red" id="amit"></span>
									<!-- <span id="amit" class="help-block required"></span> -->
								</div>

								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<select class="form-control" id="schemaTypeid" name="schemaType">
											<option value="BigQuery" selected>BigQuery</option>
										</select> 
										<label for="form_control_1">Schema Type</label>
										<!-- <span class="help-block">Short text about Data Set</span> -->
									</div>
								</div>
							</div>
						</div>

						<div class="form-body">
							<div id="basicFormDiv">
								<div class="row">
									<div class="col-md-6 col-capture">

										<div class="form-group form-md-line-input">
											<textarea cols="30" rows="3" class="form-control"
												id="privateKey" name="privateKey" style="border: solid 1px #ddd"></textarea>
											<label for="form_contro" id="privatekey-name">Privatekey*</label>
											<!--  <span class="help-block">Data Set Name </span> -->
										</div>
										<br /> <span class="required"></span>
									</div>

									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="projectNameId"
												name="projectName" placeholder="Ex: XXXXX-1223"> 
											<label for="form_control">Project ID</label>
											<!-- <span class="help-block">Short text about Data Set</span> -->
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="password" class="form-control" id="privateKeyId" name="privateKeyId"> 
											<label for="form_control" id="privatekey-id">PrivatekeyId *</label>
											<!-- <span class="help-block">Short text about Data Set</span> -->
										</div>

									</div>
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="database"
												name="database" placeholder="Ex: Dataset Name"> 
											<label for="form_control" id="datasetnameid">Dataset Name</label>
											<!-- <span class="help-block">Short text about Data Set</span> -->
										</div>
										<br /> <span class="required"></span>
									</div>

								</div>
								<div class="row">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="clientId" name="clientId">
											<label for="form_control" id="client-id">ClientId </label>
											<!-- <span class="help-block">Short text about Data Set</span> -->
										</div>
									</div>
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control catch-error" id="clientEmail" name="clientEmail"> 
											<label for="form_control_1" id="clientEmail">ClientEmail</label>
											<!--  <span class="help-block">Data Set Name </span> -->
										</div>
										<br /> <span class="required"></span>
									</div>

								</div>

							</div>

						</div>
					</div>

				</div>

				<div class="form-actions noborder align-center"></div>
				<p align="center">
					<button type="submit" id="datasetupid" class="btn blue" data-toggle="modal" data-target="#templateModal">Submit</button>
				</p>
			</div>
		</div>

		<div class="row">
			<div class="col-md-12 mt-0">
				<div class="portlet light bordered">
					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold ">Tables </span>
						</div>
					</div>
					<div class="portlet-body" id="dataTableId"></div>
				</div>
			</div>
		</div>
		
		<!-- END SAMPLE FORM PORTLET-->
	</div>
</div>
<!-- END QUICK SIDEBAR -->

<jsp:include page="footer.jsp" />
<head>
<script type="text/javascript">
$('#datasetupid').click(function() {
		$("#datasetupid").addClass("hidden");
		
		var no_error = 1;
		$('.input_error').remove();
		
		if(!checkInputText()){ 
			no_error = 0;
			alert('Vulnerability in submitted data, not allowed to submit!');
		}
		
		if($("#schemaTypeid").val() == 'BigQuery'){
			
			if ($("#privateKey").val().length == 0) {
				console.log('privatekey');
				$(
						'<span class="input_error" style="font-size:12px;color:red">Please enter privatekey</span>')
						.insertAfter($('#privateKey'));
				no_error = 0;
			}
			if ($("#privateKeyId").val().length == 0) {
				console.log('privateKeyId');
				$(
						'<span class="input_error" style="font-size:12px;color:red">Please enter privatekeyid</span>')
						.insertAfter($('#privateKeyId'));
				no_error = 0;
			}
			if ($("#projectNameId").val().length == 0) {
				console.log('projectName');
				$(
						'<span class="input_error" style="font-size:12px;color:red">Please enter bigQueryProjectName</span>')
						.insertAfter($('#projectNameId'));
				no_error = 0;
			}
			if ($("#database").val().length == 0) {
				console.log('database');
				$(
						'<span class="input_error" style="font-size:12px;color:red">Please enter datasetName</span>')
						.insertAfter($('#database'));
				no_error = 0;
			}
			if ($("#clientId").val().length == 0) {
				console.log('clientId');
				$(
						'<span class="input_error" style="font-size:12px;color:red">Please enter clientId</span>')
						.insertAfter($('#clientId'));
				no_error = 0;
			}
			if ($("#clientEmail").val().length == 0) {
				console.log('clientEmail');
				$(
						'<span class="input_error" style="font-size:12px;color:red">Please enter clientEmail</span>')
						.insertAfter($('#clientEmail'));
				no_error = 0;
			}
			
		}
		
		var amit1 = $('#amit').html();
		
		console.log(amit1);
		if (amit1 == "") {
		
		} else {
			console.log("inside if");
			no_error = 0;
		}
		
		if (!no_error) {
			$("#datasetupid").removeClass("hidden");
			return false;
		}

		var form_data = {
			schemaName : $("#schemaNameid").val(),
			schemaType : $("#schemaTypeid").val(),
			privateKey : $("#privateKey").val(),
			privateKeyId : $("#privateKeyId").val(),
			projectName : $("#projectNameId").val(),
			database : $("#database").val(),
			clientId : $("#clientId").val(),
			clientEmail : $("#clientEmail").val()
		};

		console.log(form_data);
		$.ajax({
			url : './loadBigQueryTables',
			type : 'POST',
			headers: { 'token':$("#token").val()},
			datatype : 'json',
			data : form_data,
			success : function(response) {
				$('#dataTableId').html(response);
			},
			error : function(xhr, textStatus, errorThrown) {
				$('#initial').hide();
				$('#fail').show();
			}
		});
});
</script>
</head>


