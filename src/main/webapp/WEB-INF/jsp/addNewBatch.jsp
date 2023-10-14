<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<head>
 <link rel="stylesheet" href="./assets/global/plugins/bootstrap.min.css">
  <script src="./assets/global/plugins/jquery.min.js"></script>
  <script src="./assets/global/plugins/bootstrap.min.js"></script>
 
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
							<span class="caption-subject bold "> Create Connection Batch</span>
						</div>
					</div>
					<font size="3">${result}</font>

					<%-- ${message} --%>
					<div class="portlet-body form">
					<form id="myForm" action="createSchemaBatch" enctype="multipart/form-data"
							method="POST" accept-charset="utf-8">
						<div class="form-body">
							<div class="row">
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											id="schemaNameid" name="schemaName"
											placeholder=" Enter the SchemaName" onkeyup="sendInfo()">
										<label for="form_control_1">Connection Batch Name </label>
										<!--  <span class="help-block">Data Set Name </span> -->
									</div>
									<span class="help-block required" style="font-size:12px;color:red" id="amit"></span>
									<!-- <span id="amit" class="help-block required"></span> -->
								</div>

								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<select class="form-control" id="schemaTypeid"
											name="schemaType">
											<option value="-1">Please Choose One</option>
											<option value="Hive Kerberos">Hive (Kerberos)</option>
											<option value="Oracle RAC">Oracle (RAC)</option>
										</select> <label for="form_control_1">Connection Type</label>
										<!-- <span class="help-block">Short text about Data Set</span> -->
									</div>
								</div>
							</div>
						</div>
						<div class="form-body">
							<div class="row">
								<div class="col-md-6 col-capture">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="file" class="form-control" name="dataupload"
												id="dataupload_id" /> <label for="form_control"
												id="fileType">Choose File Location</label>
										</div>
									</div>
									<br /> 
								</div>
							</div>
						</div>
						<div class="form-actions noborder align-center"></div>
						<p align="center">
							<button type="submit" id="datasetupid" class="btn blue">Submit</button>
						</p>
						
						 </form> 
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
   
 <script type="text/javascript">
	$('#datasetupid')
			.click(
					function() {
						var no_error = 1;
						$('.input_error').remove();
						
						if(!checkInputText()){ 
							no_error = 0;
							alert('Vulnerability in submitted data, not allowed to submit!');
						}
						
						if ($("#schemaNameid").val().length == 0) {
							console.log('schemaNameid');
							$(
									'<span class="input_error" style="font-size:12px;color:red">Please enter Schema Name</span>')
									.insertAfter($('#schemaNameid'));
							no_error = 0;
						}
						if ($("#schemaTypeid").val() == -1) {
							console.log('schemaTypeid');
							$(
									'<span class="input_error" style="font-size:12px;color:red">Please Choose a Schema Name</span>')
									.insertAfter($('#schemaTypeid'));
							no_error = 0;
						}
						if ($("#dataupload_id").val().length == 0) {
							$(
									'<span class="input_error" style="font-size:12px;color:red">Please Upload a File</span>')
									.insertAfter($('#dataupload_id'));
							no_error = 0;
						}
						var amit1 = $('#amit').html();
						console.log(amit1);
						if (amit1 == "") {

						} else {
							console.log("inside if");
							//toaster.info("Schema created successfully");
							//$('<span class="input_error" style="font-size:12px;color:red">already exist Database Name</span>').insertAfter($('#descriptionid'));
							no_error = 0;
						} 

						if (!no_error) {
							return false;
						}else {
							$('.btn').addClass('hidden');

						}
						
						
					});
	
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