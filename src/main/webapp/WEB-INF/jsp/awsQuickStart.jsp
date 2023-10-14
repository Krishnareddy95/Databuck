<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />

<head>
<link rel="stylesheet" href="./assets/global/plugins/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
<link rel="stylesheet" href="./assets/global/plugins/bootstrap-theme.min.css" integrity="sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp" crossorigin="anonymous">
<script src="./assets/global/plugins/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>
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
			<div class="col-md-12" style="width: 100%;">
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
											id="schemaNameid" name="schemaName"
											placeholder=" Enter the SchemaName" onkeyup="sendInfo()">
										<label for="form_control_1">Schema Name </label>
										<!--  <span class="help-block">Data Set Name </span> -->
									</div>
									<span class="help-block required" style="font-size:12px;color:red" id="amit"></span>
								</div>

								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<select class="form-control" id="schemaTypeid"
											name="schemaType" disabled>
											<option value="S3 Batch" selected>S3 (Batch)</option>
										</select> 
										<label for="form_control_1">Schema Type</label>
									</div>
								</div>
							</div>
						</div>

						<div class="form-body">
						  <div id="basicFormDiv">
							 <div class="row">
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="password" class="form-control" id="accessKey"
											name="accessKey" value="AKIASXFIW7QEXMO3RHJA"> <label for="form_control"
											id="access-key">AccessKey *</label>
										<!-- <span class="help-block">Short text about Data Set</span> -->
									</div>
								</div>
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="password" class="form-control catch-error"
											id="secretKey" name="secretKey" value="HKGU0QZeDfcSWGGvRRX21Gvsypm944vqfuQP3D8t"> <label for="form_control_1"
											id="secret-Key">SecretKey *</label>
										<!--  <span class="help-block">Data Set Name </span> -->
									</div>
									<br /> <span class="required"></span>
								</div>
								<div class="col-md-6 col-capture">
                                        <div class="form-group form-md-line-input">
                                            <input type="text" class="form-control catch-error"
                                                id="bucketNamePattern" name="bucketNamePattern">
                                            <label for="form_control_1" id="bucketNamePattern">Bucket Name Pattern</label>
                                            <!--  <span class="help-block">Data Set Name </span> -->
                                        </div>
                                        <br /> <span class="required"></span>
                                    </div>
									
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control catch-error"
												id="fileNamePattern" name="fileNamePattern">
											<label for="form_control_1" id="fileNamePattern">File Name Pattern</label>
											<!--  <span class="help-block">Data Set Name </span> -->
										</div>
										<br /> <span class="required"></span>
									</div>
									
							  </div>
							  <div class="row">
									<div class="col-md-6 col-capture" >
										<div class="form-group form-md-line-input">
											<input type="checkbox" checked class="md-check"
												id="headerId" name="headerPresent" value="Y"> 
											<label
												for="form_control_1" id="headerPresent">File has Header?*</label>
										</div>
										<br /> <span class="required"></span>
									</div>
									<div class="col-md-6 col-capture">
                                        <div class="form-group form-md-line-input">
                                            <select class="form-control" id="fileDataFormat" name="fileDataFormat"
                                                placeholder="Choose DataFormat">
                                                <option value="PSV">PSV(Pipe Delimited)</option>
                                                <option value="CSV" selected>CSV</option>
                                                <option value="TSV">TSV</option>
                                                <option value="ORC">ORC</option>
                                                <option value="Parquet">Parquet</option>
                                                <option value="JSON">JSON</option>
                                                <option value="FLAT">Flat File</option>
                                            </select> <label for="form_control_1">File Data Format</label>
                                        </div>
                                    </div>

								</div>
								
								<div class="hidden" id="headerInfoDivId">
									<div class="row">
									  <div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control catch-error"
												id="headerFilePath" name="headerFilePath">
											<label for="form_control_1" id="headerFilePath">Header File Path</label>
											<!--  <span class="help-block">Data Set Name </span> -->
										</div>
										<br /> <span class="required"></span>
									  </div>
									</div>
									<div class="row">
										<div class="col-md-6 col-capture">
											<div class="form-group form-md-line-input">
												<input type="text" class="form-control catch-error"
													id="headerFileNamePattern" name="headerFileNamePattern">
												<label for="form_control_1" id="headerFileNamePattern">Header FileName Pattern</label>
												<!--  <span class="help-block">Data Set Name </span> -->
											</div>
											<br /> <span class="required"></span>
										</div>
										
										<div class="col-md-6 col-capture">
											<div class="form-group form-md-line-input">
												<select class="form-control" id="headerFileDataFormat" name="headerFileDataFormat"
													placeholder="Choose DataFormat">
													<option value="PSV">PSV(Pipe Delimited)</option>
													<option value="CSV">CSV</option>
													<option value="TSV">TSV</option>
													<option value="ORC">ORC</option>
													<option value="Parquet">Parquet</option>
													<option value="JSON">JSON</option>
												</select> <label for="form_control_1">HeaderFile Data Format</label>
											</div>
										</div>
									</div>
								</div>
								
							</div>

							</div>
							</div>
							<div class="form-actions noborder align-center"></div>
                                <p align="center">
                                    <button type="submit" id="datasetupid" class="btn blue">Load Buckets</button>
                                    <button type="submit" id="resetBtnId" class="btn blue">Reset</button>
                               </p>
                            </div>
						
						</div>
						
				</div>
				<!--START SHOW ALL BUCKETS -->
				<div class="row">
					<div class="col-md-12 mt-0">
						<div class="portlet light bordered">
							<div class="portlet-title">
								<div class="caption font-red-sunglo">
									<span class="caption-subject bold ">Buckets </span>
								</div>
							</div>
							<div class="portlet-body" id="bucketdataTableId">
						 
							</div>
						</div>
					</div>
				</div><!-- END SHOW ALL BUCKETS -->
				<!-- END SAMPLE FORM PORTLET-->			
			</div>		
		</div>
	
<!-- END QUICK SIDEBAR -->


<jsp:include page="footer.jsp" />
<head>
<script type="text/javascript">
$("#autoGenerateId").change(function() {
	if ($("#schemaTypeid").val()== 'Hive Kerberos'){
    if(this.checked) {
    	$('#ignoreDiv').removeClass('hidden').show();
    }else{
    	$('#ignoreDiv').addClass('hidden');
    }
	}
});

$('#headerId').change(
		function() {
			console.log("headerId changed")
			console.log("isHeaderPresent:"+($('#headerId').is(':checked')))
			if($('#headerId').is(':checked')){
				$('#headerInfoDivId').addClass('hidden');
			} else {
				$('#headerInfoDivId').removeClass('hidden');
			}	
});

$('#resetBtnId').click(function() {
	window.location.reload();
});

$('#datasetupid')
			.click(function() {
						$('#datasetupid').addClass('hidden');
						var no_error = 1;
						$('.input_error').remove();
						
						if(!checkInputText()){ 
							no_error = 0;
							alert('Vulnerability in submitted data, not allowed to submit!');
						}

						if ($("#schemaNameid").val().length == 0) {
							console.log('schemaNameid');
							$(
								'<span class="input_error" style="font-size:12px;color:red">Please enter Connection Name</span>')
								.insertAfter($('#schemaNameid'));
							no_error = 0;
						}
						
						if ($("#accessKey").val().length == 0) {
							console.log('accessKey');
							$(
									'<span class="input_error" style="font-size:12px;color:red">Please enter AccessKey</span>')
									.insertAfter($('#accessKey'));
							no_error = 0;
						}
						
						if ($("#secretKey").val().length == 0) {
							console.log('secretKey');
							$(
									'<span class="input_error" style="font-size:12px;color:red">Please enter SecretKey</span>')
									.insertAfter($('#secretKey'));
							no_error = 0;
						}
						
						if ($("#bucketNamePattern").val().length == 0) {
							console.log('bucketNamePattern');
							$(
									'<span class="input_error" style="font-size:12px;color:red">Please enter BucketName Pattern</span>')
									.insertAfter($('#bucketNamePattern'));
							no_error = 0;
						}
						
						if ($("#fileNamePattern").val().length == 0) {
							console.log('fileNamePattern');
							$(
									'<span class="input_error" style="font-size:12px;color:red">Please enter File Name Pattern</span>')
									.insertAfter($('#fileNamePattern'));
							no_error = 0;
						}
						
						if (!($('#headerId').is(':checked'))) {
							
							if ($("#headerFilePath").val().length == 0) {
								console.log('headerFilePath');
								$(
										'<span class="input_error" style="font-size:12px;color:red">Please enter HeaderFilePath</span>')
										.insertAfter($('#headerFilePath'));
								no_error = 0;
							}
							if ($("#headerFileNamePattern").val().length == 0) {
								console.log('headerFileNamePattern');
								$(
										'<span class="input_error" style="font-size:12px;color:red">Please enter HeaderFile Name Pattern</span>')
										.insertAfter($('#headerFileNamePattern'));
								no_error = 0;
							}
						}
					  
					
						if (!no_error) {
							$('#datasetupid').removeClass('hidden');
							return false;
						}
						if ($("#autoGenerateId").prop("checked") == true) {
							var autoGenerateId = "Y";
						} else {
							var autoGenerateId = "N";
						}
						
						if ($("#headerId").prop("checked") == true) {
							var headerPresent = "Y";
						} else {
							var headerPresent = "N";
						}
						
						var form_data = {
								schemaName : $("#schemaNameid").val(),
								schemaType : $("#schemaTypeid").val(),
								fileDataFormat:$("#fileDataFormat").val(),
								bucketNamePattern:$("#bucketNamePattern").val(),
								fileNamePattern:$("#fileNamePattern").val(),
								headerPresent:headerPresent,
								headerFilePath:$("#headerFilePath").val(),
								headerFileNamePattern:$("#headerFileNamePattern").val(),
								headerFileDataFormat:$("#headerFileDataFormat").val(),
								accessKey:$("#accessKey").val(),
								secretKey:$("#secretKey").val()
							};
						console.log(form_data);
						$.ajax({
									url : './loadS3Buckets',
									type : 'POST',
									headers: { 'token':$("#token").val()},
									datatype : 'json',
									data : form_data,
									success : function(response) {
										$('#bucketdataTableId').html(response);
									},
									error : function(xhr, textStatus,
											errorThrown) {
										$('#initial').hide();
										$('#fail').show();
									}
								});
			});
	
	
</script>

</head>