<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<!-- <script>
	var request;
	var vals="";
	function sendInfo() {
		var v = document.getElementById("descriptionid").value;
		//var v=document.vinform.Database.value;  
		var url = "./duplicateatabasename?val=" + v;
		//alert("in ajax code");
		if (window.XMLHttpRequest) {
			request = new XMLHttpRequest();
		} else if (window.ActiveXObject) {
			request = new ActiveXObject("Microsoft.XMLHTTP");
		}

		try {
			request.onreadystatechange = getInfo;
			request.open("POST", url, true);
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
			var sal="Database already exists";
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

</script> -->
<head>
<style>
 	.greenClass{
      background-color:#b4f3b4;
      border-color: #f9e491;
      color: #000000; }
  	.redClass{
       background-color:#f3c7c7;
       border-color: #f9e491;
       color: #000000; }
  	.blueClass{
     background-color:#87CEEB;
     border-color: #f9e491;
     color: #000000; }
</style>
</head>

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
							<span class="caption-subject bold "> Customize Data Connection
							</span>
						</div>
					</div>
					<font size="3">${result}</font> <input type="hidden" id="idDataSchema" value="${idDataSchema}">
					<input type="hidden" id="SchemaType" value="${schemaType}">
					<input type="hidden" id="h_fileDataFormat" value="${fileDataFormat}">
					<input type="hidden" id="h_headerFileDataFormat" value="${headerFileDataFormat}">
					<input type="hidden" id="h_headerPresent" value="${headerPresent}">
					<%-- ${message} --%>
					<div class="portlet-body form">

						<div class="form-body">
							<div class="row">
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error" id="schemaNameid"
											name="schemaName" placeholder=" Enter the SchemaName" value="${schemaName}"
											readonly> <label for="form_control_1">Connection
											Name </label>
										<!--  <span class="help-block">Data Set Name </span> -->
									</div>
									<span class="required"></span>
								</div>

								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<select class="form-control" id="schemaTypeid" name="schemaType">
											<option value="-1">Please Choose One</option>
											<option value="MSSQL">MS SQL</option>
											<option value="MSSQLActiveDirectory">MS SQL(Active
												Directory)</option>
											<option value="Vertica">Vertica</option>
											<option value="Oracle">Oracle (Standalone)</option>
											<option value="Hive">Hive</option>
											<option value="Cassandra">Cassandra</option>
											<option value="Postgres">Postgres</option>
											<option value="Teradata">Teradata</option>
											<option value="Amazon Redshift">Amazon Redshift</option>
											<option value="Hive Kerberos">Hive (Kerberos)</option>
											<option value="Oracle RAC">Oracle (RAC)</option>

											<!-- changes for import 29jan2019  -->
											<option value="MYSQL">MySQL</option>
											<!-- ----- -->


											<!-- changes for mapr DB 28may2019 -->
											<option value="MapR DB">MapR DB</option>

											<!-- Added Hive (Knox) 4Jun2019 -->
											<option value="Hive knox">Hive (Knox)</option>

											<option value="MapR Hive">MapR Hive</option>

											<option value="SnowFlake">SnowFlake</option>

											<!-- changes for FileSystem(batch) 03Oct2019 -->
											<option value="FileSystem Batch">FileSystem (Batch)</option>
											<option value="S3 Batch">S3 (Batch)</option>
											<option value="S3 IAMRole Batch">S3 IAMRole(Batch)</option>
											<option value="S3 IAMRole Batch Config">S3 IAMRole Config(Batch)</option>
											<option value="BigQuery">BigQuery</option>
											<option value="AzureSynapseMSSQL">AzureSynapseMSSQL</option>
											<option value="AzureDataLakeStorageGen1">Azure DataLake Storage Gen1</option>
										    <option value="AzureDataLakeStorageGen2Batch">Azure DataLake Storage Gen2(Batch) </option>
										</select> <label for="form_control_1">Connection Type</label>
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
											<input type="text" class="form-control catch-error" id="datasetid"
												name="Uri" placeholder=" Enter the Uri" value="${ipAddress}"> <label
												for="form_control_1" id="labelURI">Uri</label>
											<!--  <span class="help-block">Data Set Name </span> -->
										</div>
										<br /> <span class="required"></span>
									</div>

									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="descriptionid" name="Database"
												placeholder=" Enter Database " value="${databaseSchema}"> <label 
												id="labelDatabaseName" for="form_control">Database</label>
											<!-- <span class="help-block">Short text about Data Set</span> -->
										</div>
									</div>
									<span id="amit" class="help-block required"></span>
								</div>

								<div class="form-body">
									<div class="row">
										<div class="col-md-6 col-capture">
											<div class="form-group form-md-line-input">
												<input type="text" class="form-control catch-error" id="Username"
													name="Username" placeholder=" Enter Username" value="${username}">
												<label for="form_control_1">Username</label>
											</div>
											<br /> <span class="required"></span>
										</div>
										<div class="col-md-6 col-capture">
											<div class="form-group form-md-line-input">
												<input type="text" class="form-control" id="Port" name="Port"
													placeholder=" Enter Port " value="${port}"> <label
													for="form_control">Port</label>
												<!-- <span class="help-block">Short text about Data Set</span> -->
											</div>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="password" class="form-control" id="Pwd" name="Password"
												placeholder="Leave password blank to keep it unchanged">
											<label for="form_control">Password</label>
											<!-- <span class="help-block">Short text about Data Set</span> -->
										</div>
									</div>
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="password" class="form-control" id="confirmPassword"
												name="confirmPassword"
												placeholder="Leave Confirm password blank to keep it unchanged">
											<label for="form_control">Confirm Password</label>
											<!-- <span class="help-block">Short text about Data Set</span> -->
										</div>
									</div>

								</div>
							</div>

							<!--  Start of  BigQuery fields -->
							<div class="hidden form-body" id="bigConnDiv">
								<div class="row">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="hidden" id="privatekeytemp" value=${privatekey}>

											<textarea cols="30" rows="3" class="form-control" id="privatekey"
												name="privatekey" style="border: solid 1px #ddd"
												placeholder=" Enter privatekey " value="${privatekey}"></textarea>
											<label for="form_control" id="private-Key">PrivateKey *</label>
											<!-- <span class="help-block">Short text about Data Set</span> -->
										</div>
									</div>
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="password" class="form-control catch-error" id="privatekeyid"
												name="privatekeyid" placeholder=" Enter Port " value="${privatekeyId}">
											<label for="form_control_1" id="privatekey-Id">PrivatekeyId *</label>
											<!--  <span class="help-block">Data Set Name </span> -->
										</div>
										<br /> <span class="required"></span>
									</div>
								</div>

								<div class="row">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="bigQueryProjectName"
												name=bigQueryProjectName placeholder=" Enter BigQueryProjectName "
												value="${bigQueryProjectName}"> <label for="form_control"
												id="project-Id">Project ID *</label>
											<!-- <span class="help-block">Short text about Data Set</span> -->
										</div>
									</div>
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control catch-error" id="datasetName"
												name="datasetName" placeholder=" Enter datasetName "
												value="${datasetName}"> <label for="form_control_1"
												id="datasetNameId">Dataset Name *</label>
											<!--  <span class="help-block">Data Set Name </span> -->
										</div>
										<br /> <span class="required"></span>
									</div>
								</div>


								<div class="row">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="clientId" name="clientId"
												placeholder=" Enter clientId " value="${clientId}"> <label
												for="form_control" id="client-Id">ClientId *</label>
											<!-- <span class="help-block">Short text about Data Set</span> -->
										</div>
									</div>
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control catch-error" id="clientEmail"
												name="clientEmail" placeholder=" Enter clientEmail "
												value="${clientEmail}"> <label for="form_control_1"
												id="clientEmailId">ClientEmail *</label>
											<!--  <span class="help-block">Data Set Name </span> -->
										</div>
										<br /> <span class="required"></span>
									</div>
								</div>


							</div>
							<!--  End of BigQuery  fields -->

							<!--  Start of S3 Batch fields -->
							<div class="hidden form-body" id="s3ConnDiv">
								<div class="row" id="s3Access_div">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="password" class="form-control" id="accessKey" name="accessKey"
												value="${accessKey}"> <label for="form_control"
												id="access-key">AccessKey *</label>
											<!-- <span class="help-block">Short text about Data Set</span> -->
										</div>
									</div>
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="password" class="form-control catch-error" id="secretKey"
												name="secretKey" placeholder="Leave SecretKey blank to keep it unchanged"> <label for="form_control_1"
												id="secret-Key">SecretKey *</label>
											<!--  <span class="help-block">Data Set Name </span> -->
										</div>
										<br /> <span class="required"></span>
									</div>
								</div>

								<div class="row">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control catch-error" id="bucketName"
												name="bucketName" value="${bucketName}">
											<label for="form_control_1" id="bucketNameId">BucketName</label>
											<!--  <span class="help-block">Data Set Name </span> -->
										</div>
										<br /> <span class="required"></span>
									</div>
								</div>
							</div>
							<!--  End of S3 Batch fields -->
                             <!--  Start of AzureDataLakeStorageGen2 Batch fields -->
                                  <div class="hidden form-body" id="azureDataLakeStorageGen2">
                                        <div class="row" id="azureDataLakeStorageGen2_div">
                                           <div class="row">
                                                <div class="col-md-6 col-capture">
                                                    <div class="form-group form-md-line-input">
                                                        <input type="text" class="form-control catch-error" id="azureFolderPath"
                                                            name="azureFolderPath" value="${azureFolderPath}" style="margin-left: 10px;">
                                                        <label for="form_control_1" id="azureFolderPath_lbl" style="margin-left: 10px;">Azure Folder Path</label>
                                                    </div>
                                                    <br /> <span class="required"></span>
                                                </div>
                                                 <div class="col-md-6 col-capture">
                                                    <div class="form-group form-md-line-input">
                                                            <select class="form-control" id="azureFileDataFormat" name="azureFileDataFormat" value="${azureFileDataFormat}"
                                                              placeholder="Choose DataFormat">
                                                              <option value="PSV">PSV(Pipe Delimited)</option>
                                                              <option value="CSV">CSV</option>
                                                              <option value="TSV">TSV</option>
                                                              <option value="ORC">ORC</option>
                                                              <option value="Parquet">Parquet</option>
                                                              <option value="JSON">JSON</option>
                                                              <option value="FLAT">Flat File</option>
                                                           </select>
                                                        <label for="form_control_1" id="azureFileFormat_lbl">Azure File Format</label>
                                                    </div>
                                                    <br /> <span class="required"></span>
                                                </div>
                                            </div>
                                        </div>
                                         <div class="row">
											<div class="col-md-6 col-capture">
												<div class="form-group form-md-line-input">
													<input type="checkbox" class="md-check" id="azurePartitionedFoldersId"
														name="azurePartitionedFolders" value="${azurePartitionedFolders}">
													<label for="form_control_1" id="azurePartitionedFolders">Partitioned Folders?
													</label>
												</div>
												<br /> <span class="required"></span>
											</div>
										 </div>	
                                  </div>
                             <!--  End of AzureDataLakeStorageGen2 Batch fields -->

							<!--  Start of Azure Data Lake fields -->
							<div class="hidden form-body" id="azureDataLakeDiv">
								<div class="row">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="password" class="form-control" id="azureClientId"
												name="azureClientId" value="${azureClientId}">
											<label for="form_control" id="azureClientId_lbl">ClientID *</label>
										</div>
									</div>
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="password" class="form-control catch-error"
												id="azureClientSecret" name="azureClientSecret"
												value="${azureClientSecret}">
											<label for="form_control_1" id="azureClientSecret_lbl">ClientSecret
												*</label>
										</div>
										<br /> <span class="required"></span>
									</div>
								</div>

								<div class="row">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control catch-error" id="azureTenantId"
												name="azureTenantId" value="${azureTenantId}">
											<label for="form_control_1" id="azureTenantId_lbl">TenantID *</label>
										</div>
										<br /> <span class="required"></span>
									</div>
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control catch-error" id="azureServiceURI"
												name="azureServiceURI" value="${azureServiceURI}">
											<label for="form_control_1" id="azureServiceURI_lbl">Service URI *</label>
										</div>
										<br /> <span class="required"></span>
									</div>
								</div>
								<div class="row">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control catch-error" id="azureFilePath"
												name="azureFilePath" value="${azureFilePath}">
											<label for="form_control_1" id="azureFilePath_lbl">Folder Path</label>
										</div>
										<br /> <span class="required"></span>
									</div>
								</div>
							</div>
							<!--  End of Azure Data Lake fields -->

							<!--  Start of FileSystem Batch fields -->
							<div class="hidden" id="fileSystemBatchDiv">
								<div class="row">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control catch-error" id="folderPath"
												name="folderPath" value="${folderPath}">
											<label for="form_control_1" id="folderPath">Folder Path</label>
											<!--  <span class="help-block">Data Set Name </span> -->
										</div>
										<br /> <span class="required"></span>
									</div>

									<div class="col-md-3 col-capture">
										<div class="form-group form-md-line-input">
											<input type="checkbox" class="md-check" id="fileEncryptedId"
												name="fileEncrypted" value="${fileEncrypted}">
											<label for="form_control_1" id="fileEncrypted">File Encrypted? </label>
										</div>
										<br /> <span class="required"></span>
									</div>
								</div>

								<div class="row">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control catch-error" id="fileNamePattern"
												name="fileNamePattern" value="${fileNamePattern}">
											<label for="form_control_1" id="fileNamePattern">FileName Pattern</label>
											<!--  <span class="help-block">Data Set Name </span> -->
										</div>
										<br /> <span class="required"></span>
									</div>

									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<select class="form-control" id="fileDataFormat" name="fileDataFormat"
												placeholder="Choose DataFormat">
												<option value="PSV">PSV(Pipe Delimited)</option>
												<option value="CSV">CSV</option>
												<option value="TSV">TSV</option>
												<option value="ORC">ORC</option>
												<option value="Parquet">Parquet</option>
												<option value="JSON">JSON</option>
												<option value="FLAT">Flat File</option>
											</select> <label for="form_control_1">File Data Format</label>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="checkbox" class="md-check" id="multiFolderEnabled" name="multiFolderEnabled"
												value="${multiFolderEnabled}">
											<label for="form_control_1" id="multiFolderEnabled">Multi Folder Data? </label>
										</div>
                                        <br /> <span class="required"></span>
									</div>
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="checkbox" class="md-check" id="singleFileId" name="singleFile"
												value="${singleFile}">
											<label for="form_control_1" id="singleFile">Single File? </label>
										</div>
										<br /> <span class="required"></span>
									</div>
								</div>
								<div class="row">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="checkbox" class="md-check" id="partitionedFoldersId"
												name="partitionedFolders" value="${partitionedFolders}">
											<label for="form_control_1" id="partitionedFolders">Partitioned Folders?
											</label>
										</div>
										<br /> <span class="required"></span>
									</div>

									<div class="col-md-6 col-capture hidden" id="multiPatternDivId">
										<div class="form-group form-md-line-input">
											<input type="checkbox" class="md-check" id="multiPatternId"
												name="multiPattern" value="${multiPattern}">
											<label for="form_control_1" id="multiPattern">Multiple Pattern Exist?
											</label>
										</div>
										<br /> <span class="required"></span>
									</div>
								</div>

								<div class="hidden" id="maxFolderDepthDivId">
									<div class="row">
										<div class="col-md-6 col-capture">
											<div class="form-group form-md-line-input">
												<input type="text" class="form-control catch-error"
													id="maxFolderDepthId" name="maxFolderDepth"
													value="${maxFolderDepth}">
												<label for="form_control_1" id="maxFolderDepth">Maximum Folder
													Depth</label>
											</div>
											<br /> <span class="required"></span>
										</div>
									</div>
								</div>

								<div class="hidden" id="multiPatternConfigDivId">
									<div class="row">
										<div class="col-md-6 col-capture">
											<div class="form-group form-md-line-input">
												<input type="text" class="form-control catch-error"
													id="startingUniqueCharCountId" name="startingUniqueCharCount"
													value="${startingUniqueCharCount}">
												<label for="form_control_1" id="startingUniqueCharCount">Starting Unique
													Characters Count</label>
											</div>
											<br /> <span class="required"></span>
										</div>

										<div class="col-md-6 col-capture">
											<div class="form-group form-md-line-input">
												<input type="text" class="form-control catch-error"
													id="endingUniqueCharCountId" name="endingUniqueCharCount"
													value="${endingUniqueCharCount}">
												<label for="form_control_1" id="endingUniqueCharCount">Ending Unique
													Characters Count</label>
											</div>
											<br /> <span class="required"></span>
										</div>
									</div>
								</div>

								<div class="row">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="checkbox" checked class="md-check" id="headerId"
												name="headerPresent" value="${headerPresent}">
											<label for="form_control_1" id="headerPresent">File has Header?*</label>
										</div>
										<br /> <span class="required"></span>
									</div>
								</div>

								<div class="hidden" id="headerInfoDivId">
									<div class="row">
										<div class="col-md-6 col-capture">
											<div class="form-group form-md-line-input">
												<input type="text" class="form-control catch-error" id="headerFilePath"
													name="headerFilePath" value="${headerFilePath}">
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
													id="headerFileNamePattern" name="headerFileNamePattern"
													value="${headerFileNamePattern}">
												<label for="form_control_1" id="headerFileNamePattern">Header FileName
													Pattern</label>
												<!--  <span class="help-block">Data Set Name </span> -->
											</div>
											<br /> <span class="required"></span>
										</div>

										<div class="col-md-6 col-capture">
											<div class="form-group form-md-line-input">
												<select class="form-control" id="headerFileDataFormat"
													name="headerFileDataFormat" placeholder="Choose DataFormat">
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
								<!-- External File Start -->
								<div class="row">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="checkbox" class="md-check" id="externalFileNamePatternId"
												name="externalFileNamePattern" value="${externalFileNamePattern}">
											<label for="form_control_1" id="externalFileNamePattern">External Name File
												Pattern? </label>
										</div>
										<br /> <span class="required"></span>
									</div>
									<div class="hidden" id="externalFileData">
										<div class="col-md-6 col-capture">
											<div class="form-group form-md-line-input">
												<input type="text" class="form-control catch-error"
													id="externalFileNameId" name="externalFileName"
													placeholder="Enter External File Name" value="${externalFileName}">
												<label for="form_control_1" id="externalFileName">External File
													Name</label>
												<!--  <span class="help-block">Data Set Name </span> -->
											</div>
											<br /> <span class="required"></span>
										</div>
										<div class="col-md-6 col-capture">
											<div class="form-group form-md-line-input">
												<input type="text" class="form-control catch-error" id="patternColumnId"
													name="patternColumn" placeholder=" Enter Pattern Column"
													value="${patternColumn}">
												<label for="form_control_1" id="patternColumn">Pattern Column</label>
												<!--  <span class="help-block">Data Set Name </span> -->
											</div>
											<br /> <span class="required"></span>
										</div>
										<div class="col-md-6 col-capture">
											<div class="form-group form-md-line-input">
												<input type="text" class="form-control catch-error" id="headerColumnId"
													name="headerColumn" placeholder="Enter Header Column"
													value="${headerColumn}">
												<label for="form_control_1" id="headerColumn">Header Column</label>
												<!--  <span class="help-block">Data Set Name </span> -->
											</div>
											<br /> <span class="required"></span>
										</div>
										<div class="col-md-6 col-capture">
											<div class="form-group form-md-line-input">
												<input type="text" class="form-control catch-error" id="localDirectoryColumnIndexId"
													name="localDirectoryColumnIndex" placeholder="Enter Local Directory Column Index" value="${localDirectoryColumnIndex}">
												<label for="form_control_1" id="localDirectoryColumnIndex">Local Directory Column Index</label>
												<!--  <span class="help-block">Data Set Name </span> -->
											</div>
											<br /> <span class="required"></span>
										</div>
											<div class="col-md-6 col-capture">
											<div class="form-group form-md-line-input">
												<input type="text" class="form-control catch-error" id="xsltFolderPathId"
													name="xsltFolderPathName" placeholder="Enter XSLT Folder Path" value="${xsltFolderName}">
												<label for="form_control_1" id="xsltFolderPathName">XSLT File Path</label>
												<!--  <span class="help-block">Data Set Name </span> -->
											</div>
										</div>
									</div>
								</div>
								<!-- External File End-->
							</div>
							<!--  End of FileSystem Batch fields -->

							<!--  //Start of Hive knox fields -->
							<br />
							<div class="row hidden" id="hiveKnoxDiv">
								<div class="col-md-6 col-capture"  id="gatewaypathId">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error" id="gatewayPathId"
											name="gatewayPath" placeholder="Ex: gateway/default/hive"
											value="${gatewayPath}">
										<label for="form_control_1" id="labelURI">Gateway Path</label>
										<!--  <span class="help-block">Data Set Name </span> -->
									</div>
									<br /> <span class="required"></span>
								</div>

								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control" id="jksPathId" name="jksPath"
											placeholder="Ex:  /Users/nagesh/Downloads/hiveJDBC-master/jdbc.JKS"
											value="${jksPath}" onkeyup="sendInfo()"> <label for="form_control">JKS
											Path</label>
										<!-- <span class="help-block">Short text about Data Set</span> -->
									</div>
								</div>

								<span id="amit" class="help-block required"></span>
							</div>

							<div class="row hidden" id="hiveKnoxUrlDiv">

								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control" id="zookeeperUrlId" name="zookeeperUrl"
											placeholder="" value="${zookeeperUrl}" onkeyup="sendInfo()"> <label
											for="form_control">Zookeeper Url</label>
									</div>
								</div>
							</div>

							<!--  //end of Hive knox fields -->
							<div class="form-body">
								<div class="row">
									<div class="col-md-6 col-capture hidden" id="OracleXE">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="servicename" name="servicename"
												placeholder=" Enter Service Name" value="${servicename}"> <label
												for="form_control" id="labelServiceName">Service
												Name</label>
										</div>
									</div>
									<div class="col-md-6 col-capture hidden" id="MSSQLAD">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="Domain" name="Domain"
												placeholder=" Enter Domain " value="${domain}">
											<label for="form_control" id="labelDomain">Domain</label>
										</div>
									</div>
								</div>
								<br>
								<div class="row">
									<div class="col-md-6 col-capture hidden" id="krb5confdiv">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="krb5conf" name="krb5conf"
												placeholder="" value="${krb5conf}"> <label for="form_control"
												id="labelDomain">krb5conf</label>
										</div>
									</div>
								</div>

							</div>

							<div class="row hidden" id="enableFileMonitoring_Div">
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="checkbox" class="md-check" id="enableFileMonitoring_Id"
											name="enableFileMonitoring" value="${enableFileMonitoring}">
										<label for="form_control_1" id="enableFileMonitoring">Enable Ingestion
											Monitoring? </label>
									</div>
									<br />
								</div>
							</div>
							<!--  Start of  Mapr Hive fields -->
                            <div class="hidden form-body" id="maprHive">
                             <div class="row">
                            	<div class="col-md-6 col-capture">
                            		<div class="form-group form-md-checkboxes">
										<div class="md-checkbox-list">
											<div class="md-checkbox">
												<input type="checkbox" class="md-check" id="readLatestPartition"
													name="readLatestPartition" value="${readLatestPartition}"> <label
													for="readLatestPartition"><span></span> <span class="check"></span>
													<span class="box"></span>Read Latest Partition</label>
											</div>
										</div>
									</div>
                            	</div>
                             </div>
                            </div>
                            <!--  End of Mapr Hive  fields -->

                            <div class="row " id="sslEnabled_div">
                                <div class="col-md-12 col-capture">
                                    <div class="form-group form-md-checkboxes">
                                        <div class="md-checkbox-list">
                                            <div class="md-checkbox">
                                                <input type="checkbox" class="md-check" id="sslEnabled"
                                                    name="sslEnabled" value="${sslEnabled}"> <label
                                                    for="sslEnabled"><span></span> <span class="check"></span>
                                                    <span class="box"></span>Enable SSL</label>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
							<br>
							<div class="row " id="notKMSEnabled_div">
								<div class="col-md-12 col-capture">
									<div class="form-group form-md-checkboxes">
										<div class="md-checkbox-list">
											<div class="md-checkbox">
												<input type="checkbox" class="md-check" id="kmsAuthDisabled"
													name="kmsAuthDisabled" value="${kmsAuthDisabled}"> <label
													for="kmsAuthDisabled"><span></span> <span class="check"></span>
													<span class="box"></span>KMS Authentication Disabled</label>
											</div>
										</div>
									</div>
								</div>
							</div>
                             <div class="row hidden" id="incrementalDataReadEnabled_div">
                                 <div class="col-md-12 col-capture">
                                     <div class="form-group form-md-checkboxes">
                                         <div class="md-checkbox-list">
                                             <div class="md-checkbox">
                                                <input type="checkbox" class="md-check"
                                                     id="incrementalsourceid" name="incrementalsourceid"
                                                     value="${incrementalDataReadEnabled}"> <label for="incrementalsourceid"><span></span>
                                                     <span class="check"></span> <span class="box"></span>Incremental
                                                     DataRead Enabled</label>
                                             </div>
                                         </div>
                                     </div>
                                 </div>
                             </div>
							<!-- Adding new field 'Alation'  -->
							<br>
							<div class="row " id="alationEnabled">
                                <div class="col-md-12 col-capture">
                                    <div class="form-group form-md-checkboxes">
                                        <div class="md-checkbox-list">
                                            <div class="md-checkbox">
                                                <input type="checkbox" class="md-check" id="alation_integration_enabled"
                                                    name="alation_integration_enabled" value="${alation_integration_enabled}"> <label
                                                    for="alation_integration_enabled"><span></span> <span class="check"></span>
                                                    <span class="box"></span>Alation Integration Enabled</label>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="row " id="notPushDownQueryEnabled_div">
                                <div class="col-md-12 col-capture">
                                    <div class="form-group form-md-checkboxes">
                                        <div class="md-checkbox-list">
                                            <div class="md-checkbox">
                                                <input type="checkbox" class="md-check" id="pushDownQueryEnabled"
                                                    name="pushDownQueryEnabled" value="${pushDownQueryEnabled}"> <label
                                                    for="pushDownQueryEnabled"><span></span> <span class="check"></span>
                                                    <span class="box"></span>Push Down Query Enabled ?</label>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <br /> <span class="required"></span>
							<div id="userInfo" class=" hidden">
                                <div class="alert alert-warning">
                                    <h5 style="color:red" id="infoMsg">Info Msg
                                    </h5>
                                </div>
                            </div>
                             <div id="alationDiv" class="hidden">
                                <h5 id="alationMsg">
                                </h5>
                            </div>

						</div>
						<p align="center">
							<button type="submit" id="datasetupid" class="btn blue">Submit</button>
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							<button type="submit" id="testconnectionid" class="btn blue">Test
								Connection</button>
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							<button type="button" id="dataCancelId" class="btn red"
								onclick="window.location.href ='dataConnectionView'">Cancel</button>
						</p>
						<div class="note note-info hidden" id="testConNoteDiv">
							<img alt="" src="./assets/img/reload.gif" width='32px' height='30px'
								style="padding-right:10px;" /><b>Testing connection ..</b>
						</div>
						<!-- </form> -->
						<!-- </form> -->
					</div>
				</div>
				<div class="note note-info hidden">File uploaded Successfully</div>
				<!-- END SAMPLE FORM PORTLET-->
			</div>
		</div>
	</div>
</div>
<!-- END QUICK SIDEBAR -->


<jsp:include page="footer.jsp" />

<head>
	<script type="text/javascript">
	var alation_color="";

	     $('#readLatestPartition').change(function () {
            if ($('#readLatestPartition').is(':checked')) {
                var msg = "Make sure Business Date or Partition Date column name is mentioned as value to property 'unique.partitionkey.fieldname'.";
                $('#infoMsg').html("<i class='fa fa-warning'></i> "+ msg);
                $('#userInfo').removeClass('hidden').show();
            } else {
                $('#userInfo').addClass('hidden');
            }
        });

        $("#descriptionid").on("change keyup", function() {

        if ($('#alation_integration_enabled').is(':checked')) {
            check_alation_integration_enabled();
        }

        });

        $('#alation_integration_enabled').change(function () {
            check_alation_integration_enabled();
        });

        function check_alation_integration_enabled(){

          $('#alationDiv').removeClass(alation_color);
          
          var schemaType = $("#schemaTypeid").val();
          var schemaName = $("#descriptionid").val();

          if(schemaType != -1 && schemaName.length > 0){
              if ($('#alation_integration_enabled').is(':checked')) {
                     var msg = "Checking for Alation Integration for this database schema";
                     $('#alationDiv').removeClass('hidden').show();

                     alation_color = 'alert blueClass';
                     $('#alationDiv').addClass(alation_color);
                     $('#alationMsg').html(""+ msg);

                 var form_data = {
                                 schemaType: $("#schemaTypeid").val(),
                                 schemaName: $("#descriptionid").val()
                                 };
                 $.ajax({
                         url: './isAlationIntegrationEnabled',
                         type: 'POST',
                         headers: { 'token':$("#token").val()},
                         datatype: 'json',
                         data: form_data,
                         success: function (data) {
                             $('#alationDiv').removeClass(alation_color);

                              var j_obj = jQuery.parseJSON(data);
                              var status= j_obj.status;
                              var msg= j_obj.message;

                               if(status==='success')
                               {
                                 alation_color = 'alert greenClass';
                                 if(msg==''){
                                    msg="Alation Integration is enabled";
                                 }

                               }else{
                                 msg = "<i class='fa fa-warning'></i> " + msg;
                                 $('#alation_integration_enabled')[0].checked = false;
                                 alation_color = 'alert redClass';
                               }

                               $('#alationDiv').addClass(alation_color);
                               $('#alationMsg').html(""+ msg);
                         },
                         error: function (xhr, textStatus,
                             errorThrown) {
                             $('#initial').hide();
                             $('#fail').show();
                              toastr.info('Allation enabled could not be verified.');
                              window.location.reload();
                         }
                     });
                 }else{
                        $('#alationDiv').addClass('hidden');
                    }

          }else{
             $('#alation_integration_enabled')[0].checked = false;
              var msg = "Schema Type and Database schema need to be entered";

              $('#alationDiv').removeClass('hidden').show();
               alation_color = 'alert redClass';
               $('#alationDiv').addClass(alation_color);
              $('#alationMsg').html("<i class='fa fa-warning'></i> "+ msg);
          }
       }

		$('#headerId').change(
			function () {
				console.log("headerId changed")
				console.log("isHeaderPresent:" + ($('#headerId').is(':checked')))
				if ($('#headerId').is(':checked')) {
					$('#headerInfoDivId').addClass('hidden');
				} else {
					$('#headerInfoDivId').removeClass('hidden');
				}
			});

		$('#partitionedFoldersId').change(
			function () {
				if ($('#partitionedFoldersId').is(':checked')) {
					$('#multiPatternDivId').removeClass('hidden');
					$('#maxFolderDepthDivId').removeClass('hidden');
				} else {
					$('#multiPatternDivId').addClass('hidden');
					$('#maxFolderDepthDivId').addClass('hidden');
					$('#multiPatternConfigDivId').addClass('hidden');
					$('#multiPatternId').prop('checked', false);
				}
			});

		$('#multiPatternId').change(
			function () {
				if ($('#multiPatternId').is(':checked')) {
					$('#multiPatternConfigDivId').removeClass('hidden');
				} else {
					$('#multiPatternConfigDivId').addClass('hidden');
				}
			});
		$('#externalFileNamePatternId').click(function () {
			if ($("#schemaTypeid").val() == 'FileSystem Batch'
				|| $("#schemaTypeid").val() == 'S3 Batch'
				|| $("#schemaTypeid").val() == 'S3 IAMRole Batch'
				|| $("#schemaTypeid").val() == 'S3 IAMRole Batch Config') {

				if ($(this).prop("checked") == true) {
					$('#externalFileData').removeClass('hidden');
				} else if ($(this).prop("checked") == false) {
					$('#externalFileData').addClass('hidden');
				}
			}
		});

		$('#testconnectionid').click(function () {
			if ($("#headerId").prop("checked") == true) {
				var headerPresent = "Y";
			} else {
				var headerPresent = "N";
			}

			var maxFolderDepth = 0;
			var partitionedFolders = "N";
			if ($("#partitionedFoldersId").prop("checked") == true) {
				partitionedFolders = "Y";
				maxFolderDepth = $("#maxFolderDepthId").val();
			}

			var fileEncrypted = "N";
			if ($("#fileEncryptedId").prop("checked") == true) {
				fileEncrypted = "Y";
			}
			var singleFile = "N";
			if ($("#singleFileId").prop("checked") == true) {
				singleFile = "Y";
			}
			
			var azurePartitionedFolders = "N";
			if ($("#azurePartitionedFoldersId").prop("checked") == true) {
				azurePartitionedFolders = "Y";
			}
			
			var folderPath= $("#folderPath").val();
            var fileDataFormat= $("#fileDataFormat").val();
            if($("#schemaTypeid").val() == "AzureDataLakeStorageGen2Batch"){
                folderPath = $("#azureFolderPath").val();
                fileDataFormat= $("#azureFileDataFormat").val();
                partitionedFolders= azurePartitionedFolders;
            }

			var enableFileMonitoring = "N";
            if ($("#enableFileMonitoring_Id").prop("checked") == true) {
                enableFileMonitoring = "Y";
            }
			var multiPattern = "N";
			var startingUniqueCharCount = $("#startingUniqueCharCountId").val();
			var endingUniqueCharCount = $("#endingUniqueCharCountId").val();
			if ($("#multiPatternId").prop("checked") == true) {
				multiPattern = "Y";
			} else {
				startingUniqueCharCount = 0;
				endingUniqueCharCount = 0;
			}
			
			var sslEnabled = "N";
			if ($("#sslEnabled").prop("checked") == true) {
				sslEnabled = "Y";
			}
			var kmsAuthDisabled = "N";
			if ($("#kmsAuthDisabled").prop("checked") == true) {
				kmsAuthDisabled = "Y";
			}
			console.log("kmsAuthDisabled:"+kmsAuthDisabled);

			var pushDownQueryEnabled = "N";
            if ($("#pushDownQueryEnabled").prop("checked") == true) {
                pushDownQueryEnabled = "Y";
            }
			console.log("sslEnabled:"+sslEnabled);

			var alation_integration_enabled = "N";
            if ($("#alation_integration_enabled").prop("checked") == true) {
                alation_integration_enabled = "Y";
            }
            console.log("alation_integration_enabled:"+alation_integration_enabled);

			var readLatestPartition = "N";
            if ($("#readLatestPartition").prop("checked") == true) {
                readLatestPartition = "Y";
            }
            var incrementalDataReadEnabled="N";
            if ($("#incrementalsourceid").prop("checked") == true) {
                            incrementalDataReadEnabled = "Y";
            }
			var form_data = {
				schemaName: $("#schemaNameid").val(),
				schemaType: $("#schemaTypeid").val(),
				uri: $("#datasetid").val(),
				database: $("#descriptionid").val(),
				username: $("#Username").val(),
				password: $("#Pwd").val(),
				port: $("#Port").val(),
				domain: $("#Domain").val(),
				serviceName: $("#servicename").val(),
				krb5conf: $("#krb5conf").val(),
				idDataSchema: $("#idDataSchema").val(),
				gatewayPath: $("#gatewayPathId").val(),
				jksPath: $("#jksPathId").val(),
				folderPath: folderPath,
				fileNamePattern: $("#fileNamePattern").val(),
				incrementalDataReadEnabled: incrementalDataReadEnabled,
				fileDataFormat: fileDataFormat,
				headerPresent: headerPresent,
				partitionedFolders: partitionedFolders,
				headerFilePath: $("#headerFilePath").val(),
				headerFileNamePattern: $("#headerFileNamePattern").val(),
				headerFileDataFormat: $("#headerFileDataFormat").val(),
				accessKey: $("#accessKey").val(),
				secretKey: $("#secretKey").val(),
				bucketName: $("#bucketName").val(),
				privatekey: $("#privatekey").val(),
				privatekeyId: $("#privatekeyid").val(),
				bigQueryProjectName: $("#bigQueryProjectName").val(),
				datasetName: $("#datasetName").val(),
				clientId: $("#clientId").val(),
				clientEmail: $("#clientEmail").val(),
				azureClientId: $("#azureClientId").val(),
				azureClientSecret: $("#azureClientSecret").val(),
				azureTenantId: $("#azureTenantId").val(),
				azureServiceURI: $("#azureServiceURI").val(),
				azureFilePath: $("#azureFilePath").val(),
				multiPattern: multiPattern,
				startingUniqueCharCount: startingUniqueCharCount,
				endingUniqueCharCount: endingUniqueCharCount,
				maxFolderDepth: maxFolderDepth,
				fileEncrypted: fileEncrypted,
				kmsAuthDisabled: kmsAuthDisabled,
				pushDownQueryEnabled: pushDownQueryEnabled,
				readLatestPartition: readLatestPartition,
				alation_integration_enabled:alation_integration_enabled,
				sslEnb:sslEnabled
			};
			console.log(form_data);
			$('.btn').addClass('hidden');

			$.ajax({
				url: './testDataConnection',
				type: 'POST',
				headers: { 'token':$("#token").val()},
				datatype: 'json',
				data: form_data,
				beforeSend: function () {
					$("#testConNoteDiv").removeClass("hidden").show();
				},
				success: function (data) {
					//alert(data);
					if (data != "") {
						toastr.info(data);
					} else {
						toastr.info('There was a problem.Please try again later');
					}
				},
				error: function (xhr, textStatus, errorThrown) {
					$('#initial').hide();
					$('#fail').show();
				},
				complete: function (data) {
					$("#testConNoteDiv").addClass("hidden");
					$('.btn').removeClass('hidden');
				}
			});
		});

		$(document).ready(function () {
			     var ident ="${azureFileDataFormat}";
			     $("#privatekey").val("${privatekey}");
            	 var schemaType = $("#SchemaType").val();
                 var incrementalsource = $("#incrementalsourceid").val();
                 if (incrementalsource == 'Y') {
                   $('#incrementalsourceid')[0].checked = true;
                 }
                 else{
                   $('#incrementalsourceid')[0].checked = false;
                 }
                 var enableFileMonitoringcheck = $("#enableFileMonitoring_Id").val();
                 console.log("enableFileMonitoringcheck: " + enableFileMonitoringcheck)
                 if (enableFileMonitoringcheck == 'Y') {
                     $('#enableFileMonitoring_Id')[0].checked = true;
                 } else {
                     $('#enableFileMonitoring_Id')[0].checked = false;
                 }

                 var sslEnabled = $("#sslEnabled").val();
                  console.log("sslEnabled: " + sslEnabled)
                  if (sslEnabled == 'Y') {
                      $('#sslEnabled')[0].checked = true;
                  } else {
                      $('#sslEnabled')[0].checked = false;
                  }

            	 console.log("schemaType:" + schemaType+" incrementalsource:"+incrementalsource);
               	 $("#schemaTypeid").val(schemaType);
            	 
            	 $('#labelDatabaseName').text("Database");
				 $('#maprHive').addClass('hidden');
			
                 $('#azureFileDataFormat option[value="'+ident+'"]').prop("selected", true);
            	 $('#labelDatabaseName').text("Database");
                 $('#maprHive').addClass('hidden');
                 $('#fileSystemBatchDiv').addClass('hidden');
                 $('#azureDataLakeStorageGen2').addClass('hidden');
                 $('#incrementalDataReadEnabled_div').addClass('hidden');
                 $('#sslEnabled_div').addClass('hidden');


			if (schemaType == 'MSSQLActiveDirectory') {
				$('#basicFormDiv').removeClass('hidden');
				$('#fileSystemBatchDiv').addClass('hidden');
				$('#enableFileMonitoring_Div').addClass('hidden');
				$('#MSSQLAD').removeClass('hidden').show();
				$('#OracleXE').addClass('hidden');
				$('#krb5confdiv').addClass('hidden');
				$('#labelURI').text("Uri");
				$('#labelServiceName').text("Service Name");
				$('#labelDomain').text("Domain");
				$('#s3ConnDiv').addClass('hidden');
				$('#bigConnDiv').addClass('hidden');
				$('#azureDataLakeDiv').addClass('hidden');
			} else if (schemaType == 'Oracle') {
				$('#basicFormDiv').removeClass('hidden');
				$('#fileSystemBatchDiv').addClass('hidden');
				$('#enableFileMonitoring_Div').addClass('hidden');
				$('#OracleXE').removeClass('hidden').show();
				$('#MSSQLAD').addClass('hidden');
				$('#krb5confdiv').addClass('hidden');
				$('#labelURI').text("Uri");
				$('#labelServiceName').text("Service Name");
				$('#labelDatabaseName').text("Schema");
				$('#labelDomain').text("Domain");
				$('#s3ConnDiv').addClass('hidden');
				$('#bigConnDiv').addClass('hidden');
				$('#azureDataLakeDiv').addClass('hidden');
			} else if (schemaType == 'Hive Kerberos') {
				$('#basicFormDiv').removeClass('hidden');
				$('#fileSystemBatchDiv').addClass('hidden');
				$('#enableFileMonitoring_Div').addClass('hidden');
				$('#labelURI').text("Internal IP");
				$("#datasetid").attr("placeholder", "Ex: ip-10-1-1-109.ec2.internal");
				$('#MSSQLAD').removeClass('hidden').show();
				$('#OracleXE').removeClass('hidden').show();
				$('#krb5confdiv').removeClass('hidden').show();
				$('#labelServiceName').text("gss_jaas");
				$('#labelDomain').text("principal");
				$("#servicename").attr("placeholder", "");
				$("#Domain").attr("placeholder", "Ex: hive/ip-10-1-1-109.ec2.internal@HADOOPSECURITY.COM");
				$('#s3ConnDiv').addClass('hidden');
				$('#bigConnDiv').addClass('hidden');
				$('#azureDataLakeDiv').addClass('hidden');
				$('#hiveKnoxDiv').removeClass('hidden');
				$('#gatewaypathId').addClass('hidden');
				$('#hiveKnoxUrlDiv').addClass('hidden');
			} else if (schemaType == 'Oracle RAC') {
				$('#basicFormDiv').removeClass('hidden');
				$('#fileSystemBatchDiv').addClass('hidden');
				$('#enableFileMonitoring_Div').addClass('hidden');
				$('#OracleXE').removeClass('hidden').show();
				$('#MSSQLAD').addClass('hidden');
				$('#krb5confdiv').addClass('hidden');
				$('#labelURI').text("Uri");
				$('#labelServiceName').text("Service Name");
				$('#labelDomain').text("Domain");
				$('#s3ConnDiv').addClass('hidden');
				$('#bigConnDiv').addClass('hidden');
				$('#azureDataLakeDiv').addClass('hidden');
			} else if (schemaType == 'MapR DB') {
				$('#basicFormDiv').removeClass('hidden');
				$('#fileSystemBatchDiv').addClass('hidden');
				$('#enableFileMonitoring_Div').addClass('hidden');
				$('#hiveKnoxDiv').addClass('hidden');
				$('#hiveKnoxUrlDiv').addClass('hidden');
				$('#s3ConnDiv').addClass('hidden');
				$('#bigConnDiv').addClass('hidden');
				$('#azureDataLakeDiv').addClass('hidden');
				$('#Username').hide();
				$('#Username').parent().fadeOut();
				$('#Port').hide();
				$('#Port').parent().fadeOut();
				$('#Pwd').hide();
				$('#Pwd').parent().fadeOut();
				$('#datasetid').hide();
				$('#datasetid').parent().fadeOut();
				$('#confirmPassword').hide();
				$('#confirmPassword').parent().fadeOut();
				$('#s3_form_id').addClass('hidden');
				$("#s3_form_id").hide();
				$('#descriptionid').attr("placeholder",
					"Enter folder Name Ex : /apps/");
			} else if (schemaType == 'MapR Hive') {
				$('#basicFormDiv').removeClass('hidden');
				$('#fileSystemBatchDiv').addClass('hidden');
				$('#enableFileMonitoring_Div').addClass('hidden');
				$('#hiveKnoxDiv').addClass('hidden');
				$('#hiveKnoxUrlDiv').addClass('hidden');
				$('#s3ConnDiv').addClass('hidden');
				$('#bigConnDiv').addClass('hidden');
				$('#azureDataLakeDiv').addClass('hidden');
				$('#Username').hide();
				$('#Username').parent().fadeOut();
				$('#Pwd').hide();
				$('#Pwd').parent().fadeOut();
				$('#confirmPassword').hide();
				$('#confirmPassword').parent().fadeOut();
				$('#maprHive').removeClass('hidden');
			} else if (schemaType == 'Hive knox') {
				$('#basicFormDiv').removeClass('hidden');
				$('#fileSystemBatchDiv').addClass('hidden');
				$('#enableFileMonitoring_Div').addClass('hidden');
				$('#hiveKnoxDiv').removeClass('hidden').show();
				$('#gatewaypathId').removeClass('hidden');
				$('#hiveKnoxUrlDiv').removeClass('hidden').show();
				$('#s3ConnDiv').addClass('hidden');
				$('#bigConnDiv').addClass('hidden');
				$('#azureDataLakeDiv').addClass('hidden');
			} else if (schemaType == 'FileSystem Batch' || schemaType == 'S3 Batch'
				|| schemaType == 'S3 IAMRole Batch' || schemaType == 'S3 IAMRole Batch Config') {
				$('#hiveKnoxDiv').addClass('hidden').show();
				$('#hiveKnoxUrlDiv').addClass('hidden').show();
				$('#MSSQLAD').addClass('hidden');
				$('#OracleXE').addClass('hidden');
				$('#krb5confdiv').addClass('hidden');
				$('#basicFormDiv').addClass('hidden');
				$('#fileSystemBatchDiv').removeClass('hidden');
                $('#enableFileMonitoring_Div').removeClass('hidden');
                $('#azureDataLakeStorageGen2').addClass('hidden');
                $('#incrementalDataReadEnabled_div').removeClass('hidden');
                
				var h_fileDataFormat = $("#h_fileDataFormat").val();
				console.log("fileDataFormat:" + h_fileDataFormat)
				$("#fileDataFormat").val(h_fileDataFormat);

				var h_headerFileDataFormat = $("#h_headerFileDataFormat").val();
				console.log("headerFileDataFormat:" + h_headerFileDataFormat)
				$("#headerFileDataFormat").val(h_headerFileDataFormat);

				var h_headerPresent = $("#h_headerPresent").val();
				console.log("h_headerPresent:" + h_headerPresent)
			

				if (h_headerPresent == 'Y') {
					$('#headerId')[0].checked = true;
					$('#headerInfoDivId').addClass('hidden');
				} else {
					$('#headerId')[0].checked = false;
					$('#headerInfoDivId').removeClass('hidden');
				}

				var fileEncrypted = $("#fileEncryptedId").val();
				console.log("fileEncrypted: " + fileEncrypted)

				if (fileEncrypted == 'Y') {
					$('#fileEncryptedId')[0].checked = true;
				} else {
					$('#fileEncryptedId')[0].checked = false;
				}

				var singleFilecheck = $("#singleFileId").val();
				console.log("singleFilecheck: " + singleFilecheck)

				if (singleFilecheck == 'Y') {
					$('#singleFileId')[0].checked = true;
				} else {
					$('#singleFileId')[0].checked = false;
				}
				
				var multiFolderEnabled = $("#multiFolderEnabled").val();
				console.log("multiFolderEnabled: " + multiFolderEnabled)

				if (multiFolderEnabled == 'Y') {
					$('#multiFolderEnabled')[0].checked = true;
				} else {
					$('#multiFolderEnabled')[0].checked = false;
				}

                var pushDownQueryEnabled = $("#pushDownQueryEnabled").val();

                if (pushDownQueryEnabled == 'Y') {
                    $('#pushDownQueryEnabled')[0].checked = true;
                } else {
                    $('#pushDownQueryEnabled')[0].checked = false;
                }

              	var partitionedFolders = $("#partitionedFoldersId").val();
				console.log("partitionedFolders: " + partitionedFolders)

				if (partitionedFolders == 'Y') {
					$('#partitionedFoldersId')[0].checked = true;
					$('#multiPatternDivId').removeClass('hidden');
					$('#maxFolderDepthDivId').removeClass('hidden');
				} else {
					$('#partitionedFoldersId')[0].checked = false;
				}

				var multiPattern = $("#multiPatternId").val();
				console.log("multiPattern: " + multiPattern)
				 
				console.log("localDirectoryColumnIndex:" + '${localDirectoryColumnIndex}')
				if (multiPattern == 'Y') {
					$('#multiPatternId')[0].checked = true;
					$('#multiPatternConfigDivId').removeClass('hidden');
				} else {
					$('#multiPatternId')[0].checked = false;
				}

				if (schemaType == 'S3 Batch') {
					$('#s3ConnDiv').removeClass('hidden');
					$('#s3Access_div').removeClass('hidden');
				} else if (schemaType == 'S3 IAMRole Batch') {
					$('#s3ConnDiv').removeClass('hidden');
					$('#s3Access_div').addClass('hidden');
				}else if (schemaType == 'S3 IAMRole Batch Config') {
					$('#s3ConnDiv').removeClass('hidden');
					$('#s3Access_div').addClass('hidden');
				}else {
					$('#s3ConnDiv').addClass('hidden');
				}

				$('#bigConnDiv').addClass('hidden');
				$('#azureDataLakeDiv').addClass('hidden');

				var externalFileNamePattern = $("#externalFileNamePatternId").val();
				if (externalFileNamePattern == 'Y') {
					$('#externalFileNamePatternId')[0].checked = true;
					$('#externalFileData').removeClass('hidden');
				} else {
					$('#externalFileData').addClass('hidden');
				}

			} else if (schemaType == 'BigQuery') {
				$('#hiveKnoxDiv').addClass('hidden').show();
				$('#hiveKnoxUrlDiv').addClass('hidden').show();
				$('#MSSQLAD').addClass('hidden');
				$('#OracleXE').addClass('hidden');
				$('#krb5confdiv').addClass('hidden');
				$('#basicFormDiv').addClass('hidden');
				$('#fileSystemBatchDiv').addClass('hidden');
				$('#enableFileMonitoring_Div').addClass('hidden');
				$('#s3ConnDiv').addClass('hidden');
				$('#bigConnDiv').removeClass('hidden');
				$('#folderPath').addClass('hidden');
				$('#fileNamePatternandFileDataFormat').addClass('hidden');
				$('#azureDataLakeDiv').addClass('hidden');
			} else if (schemaType == 'AzureDataLakeStorageGen1') {
				$('#basicFormDiv').addClass('hidden');
				$('#fileSystemBatchDiv').addClass('hidden');
				$('#enableFileMonitoring_Div').addClass('hidden');
				$('#s3ConnDiv').addClass('hidden');
				$('#azureDataLakeDiv').removeClass('hidden');
				$('#bigConnDiv').addClass('hidden');
				$('#hiveKnoxDiv').addClass('hidden').show();
				$('#hiveKnoxUrlDiv').addClass('hidden').show();
				$('#krb5confdiv').addClass('hidden');
				$('#MSSQLAD').addClass('hidden');
				$('#autoGenerateDiv').addClass('hidden');
				$('#OracleXE').addClass('hidden');
				$('#bigConnDiv').addClass('hidden');
			} else if (schemaType == 'SnowFlake') {
                //$('#SnowFlake').removeClass('hidden').show();
                $('#fileSystemBatchDiv').addClass('hidden');
                $('#MSSQLAD').removeClass('hidden');
                $('#OracleXE').addClass('hidden');
                $('#krb5confdiv').addClass('hidden');
                $('#labelURI').text("Uri");
                $('#labelServiceName').text("Service Name");
                $('#labelDomain').addClass('hidden');
                $("#Domain").addClass('hidden');
                $('#autoGenerateDiv').addClass('hidden');
                $('#basicFormDiv').removeClass('hidden');
                $('#s3ConnDiv').addClass('hidden');
                $('#azureDataLakeDiv').addClass('hidden');
                $('#bigConnDiv').addClass('hidden');
                $('#ignoreDiv').addClass('hidden');
                $('#hiveKnoxDiv').addClass('hidden');
                $('#hiveKnoxUrlDiv').addClass('hidden');
                $('#enableFileMonitoring_Div').removeClass('hidden');

            }else if (schemaType == 'AzureDataLakeStorageGen2Batch') {
                  $('#hiveKnoxDiv').addClass('hidden').show();
                  $('#hiveKnoxUrlDiv').addClass('hidden').show();
                  $('#MSSQLAD').addClass('hidden');
                  $('#OracleXE').addClass('hidden');
                  $('#krb5confdiv').addClass('hidden');
                  $('#basicFormDiv').addClass('hidden');
                  $('#azureDataLakeStorageGen2').removeClass('hidden');
                  $('#enableFileMonitoring_Div').removeClass('hidden');
                  $('#bigConnDiv').addClass('hidden');
                  $('#s3ConnDiv').removeClass('hidden');
                  $('#s3Access_div').removeClass('hidden');
                  $('#azureDataLakeDiv').addClass('hidden');
                  $('#enableFileMonitoring_Div').addClass('hidden');
                  $('#fileSystemBatchDiv').addClass('hidden');
                  $('#access-key').text("AccountName*");
                  $('#secret-Key').text("AccountKey*");
                  $("#secretKey").attr("placeholder", "Leave AccountKey blank to keep it unchanged");
                  $('#bucketNameId').text("ContainerName");
                  $('#incrementalDataReadEnabled_div').removeClass('hidden');
                  $('#enableFileMonitoring_Div').removeClass('hidden');
	              var azurePartitionedFolders = $("#azurePartitionedFoldersId").val();
				  console.log("azurePartitionedFolders: " + azurePartitionedFolders);
	
				  if (azurePartitionedFolders == 'Y') {
					$('#azurePartitionedFoldersId')[0].checked = true;
				  } else {
					$('#azurePartitionedFoldersId')[0].checked = false;
				  }

            }else {
                if (schemaType == 'MYSQL') {
                    $('#sslEnabled_div').removeClass('hidden');
                }
				$('#basicFormDiv').removeClass('hidden');
				$('#fileSystemBatchDiv').addClass('hidden');
				$('#enableFileMonitoring_Div').removeClass('hidden');
				$('#MSSQLAD').addClass('hidden');
				$('#OracleXE').addClass('hidden');
				$('#krb5confdiv').addClass('hidden');
				$('#labelURI').text("Uri");
				$('#labelServiceName').text("Service Name");
				if(schemaType == 'Teradata' || schemaType == 'MSSQL'){
					$('#labelDatabaseName').text("Schema");
				} else{
					$('#labelDatabaseName').text("Database");
				}
				$('#labelDomain').text("Domain");
				$("#datasetid").attr("placeholder", "Ex: 55.33.130.223");
				$("#servicename").attr("placeholder", "Ex: xe");
				$("#Domain").attr("placeholder", "Ex: ec2-34-168-70-22.us-west-2.compute.amazonaws.com");
				$('#s3ConnDiv').addClass('hidden');
				$('#bigConnDiv').addClass('hidden');
				$('#azureDataLakeDiv').addClass('hidden');
			}
			
			var kmsAuthDisabled = $("#kmsAuthDisabled").val();
			if (kmsAuthDisabled == 'Y') {
				$('#kmsAuthDisabled')[0].checked = true;
			}

			var pushDownQueryEnabled = $("#pushDownQueryEnabled").val();
            			if (pushDownQueryEnabled == 'Y') {
            				$('#pushDownQueryEnabled')[0].checked = true;
            			}

			var sslEnabled = $("#sslEnabled").val();
			if (sslEnabled == 'Y') {
				$('#sslEnabled')[0].checked = true;
			}else
			    $('#sslEnabled')[0].checked = false;

			var readLatestPartition = $("#readLatestPartition").val();
            if (readLatestPartition == 'Y') {
                $('#readLatestPartition')[0].checked = true;
            }
            var alation_integration_enabled = $("#alation_integration_enabled").val();
            if (alation_integration_enabled == 'Y') {
                $('#alation_integration_enabled')[0].checked = true;
            }

		});

		$('#schemaTypeid').change(function () {
			$this = $(this);
			var selectedVal = $this.val();

			// Reset and Hide the FileSytem - MultiPattern config details
			// Start
			$('#multiPatternDivId').addClass('hidden');
			$('#maxFolderDepthDivId').addClass('hidden');
			$('#multiPatternConfigDivId').addClass('hidden');
			$('#multiPatternId').prop('checked', false);
			$('#partitionedFoldersId').prop('checked', false);
			$('#multiFolderEnabled').prop('checked', false);
			$('#pushDownQueryEnabled').prop('checked', false);
			$('#fileEncryptedId').prop('checked', false);
			// End
            $('#fileSystemBatchDiv').removeClass('hidden');
			$('#labelDatabaseName').text("Database");
			$('#maprHive').addClass('hidden');
		    $('#azureDataLakeStorageGen2').addClass('hidden');
		    $('#folderPath').removeClass('hidden');
            $('#incrementalDataReadEnabled_div').addClass('hidden');
            $('#sslEnabled_div').addClass('hidden');

			if (selectedVal == 'MSSQLActiveDirectory') {
				$('#basicFormDiv').removeClass('hidden');
				$('#fileSystemBatchDiv').addClass('hidden');
				$('#enableFileMonitoring_Div').addClass('hidden');
				$('#MSSQLAD').removeClass('hidden').show();
				$('#OracleXE').addClass('hidden');
				$('#krb5confdiv').addClass('hidden');
				$('#labelURI').text("Uri");
				$('#labelServiceName').text("Service Name");
				$('#labelDomain').text("Domain");
				$('#s3ConnDiv').addClass('hidden');
				$('#bigConnDiv').addClass('hidden');
				$('#azureDataLakeDiv').addClass('hidden');
			} else if (selectedVal == 'Oracle') {
				$('#basicFormDiv').removeClass('hidden');
				$('#fileSystemBatchDiv').addClass('hidden');
				$('#enableFileMonitoring_Div').addClass('hidden');
				$('#OracleXE').removeClass('hidden').show();
				$('#MSSQLAD').addClass('hidden');
				$('#krb5confdiv').addClass('hidden');
				$('#labelURI').text("Uri");
				$('#labelServiceName').text("Service Name");
				$('#labelDatabaseName').text("Schema");
				$('#labelDomain').text("Domain");
				$('#s3ConnDiv').addClass('hidden');
				$('#bigConnDiv').addClass('hidden');
				$('#azureDataLakeDiv').addClass('hidden');
			} else if (selectedVal == 'Hive Kerberos') {
				$('#basicFormDiv').removeClass('hidden');
				$('#fileSystemBatchDiv').addClass('hidden');
				$('#enableFileMonitoring_Div').addClass('hidden');
				$('#labelURI').text("Internal IP");
				$("#datasetid").attr("placeholder", "Ex: ip-10-1-1-109.ec2.internal");
				$('#MSSQLAD').removeClass('hidden').show();
				$('#OracleXE').removeClass('hidden').show();
				$('#krb5confdiv').removeClass('hidden').show();
				$('#labelServiceName').text("gss_jaas");
				$('#labelDomain').text("principal");
				$("#servicename").attr("placeholder", "");
				$("#Domain").attr("placeholder", "Ex: hive/ip-10-1-1-109.ec2.internal@HADOOPSECURITY.COM");
				$('#s3ConnDiv').addClass('hidden');
				$('#bigConnDiv').addClass('hidden');
				$('#azureDataLakeDiv').addClass('hidden');
				$('#hiveKnoxDiv').removeClass('hidden');
				$('#gatewaypathId').addClass('hidden');
				$('#hiveKnoxUrlDiv').addClass('hidden');
			} else if (selectedVal == 'Hive knox') {
				$('#basicFormDiv').removeClass('hidden');
				$('#fileSystemBatchDiv').addClass('hidden');
				$('#enableFileMonitoring_Div').addClass('hidden');
				$('#hiveKnoxDiv').removeClass('hidden').show();
				$('#gatewaypathId').removeClass('hidden');
				$('#hiveKnoxUrlDiv').removeClass('hidden').show();
				$('#s3ConnDiv').addClass('hidden');
				$('#bigConnDiv').addClass('hidden');
				$('#azureDataLakeDiv').addClass('hidden');
			} else if (selectedVal == 'Oracle RAC') {
				$('#basicFormDiv').removeClass('hidden');
				$('#fileSystemBatchDiv').addClass('hidden');
				$('#enableFileMonitoring_Div').addClass('hidden');
				$('#OracleXE').removeClass('hidden').show();
				$('#MSSQLAD').addClass('hidden');
				$('#krb5confdiv').addClass('hidden');
				$('#labelURI').text("Uri");
				$('#labelServiceName').text("Service Name");
				$('#labelDomain').text("Domain");
				$('#s3ConnDiv').addClass('hidden');
				$('#bigConnDiv').addClass('hidden');
				$('#azureDataLakeDiv').addClass('hidden');
			} else if (selectedVal == 'FileSystem Batch') {
				$('#hiveKnoxDiv').addClass('hidden').show();
				$('#hiveKnoxUrlDiv').addClass('hidden').show();
				$('#MSSQLAD').addClass('hidden');
				$('#OracleXE').addClass('hidden');
				$('#krb5confdiv').addClass('hidden');
				$('#basicFormDiv').addClass('hidden');
				$('#fileSystemBatchDiv').removeClass('hidden');
				$('#enableFileMonitoring_Div').removeClass('hidden');
				$('#s3ConnDiv').addClass('hidden');
				$('#bigConnDiv').addClass('hidden');
				$('#azureDataLakeDiv').addClass('hidden');
			} else if (selectedVal == 'S3 Batch') {
				$('#hiveKnoxDiv').addClass('hidden').show();
				$('#hiveKnoxUrlDiv').addClass('hidden').show();
				$('#MSSQLAD').addClass('hidden');
				$('#OracleXE').addClass('hidden');
				$('#krb5confdiv').addClass('hidden');
				$('#basicFormDiv').addClass('hidden');
				$('#fileSystemBatchDiv').removeClass('hidden');
				$('#enableFileMonitoring_Div').removeClass('hidden');
				$('#s3ConnDiv').removeClass('hidden');
				$('#s3Access_div').removeClass('hidden');
				$('#bigConnDiv').addClass('hidden');
				$('#azureDataLakeDiv').addClass('hidden');
				$('#access-key').text("AccessKey*");
                $('#secret-Key').text("SecretKey*");
                $("#secretKey").attr("placeholder", "Leave SecretKey blank to keep it unchanged");
                $('#bucketNameId').text("BucketName");
                $('#incrementalDataReadEnabled_div').removeClass('hidden');
			} else if (selectedVal == 'S3 IAMRole Batch') {
				$('#hiveKnoxDiv').addClass('hidden').show();
				$('#hiveKnoxUrlDiv').addClass('hidden').show();
				$('#MSSQLAD').addClass('hidden');
				$('#OracleXE').addClass('hidden');
				$('#krb5confdiv').addClass('hidden');
				$('#basicFormDiv').addClass('hidden');
				$('#fileSystemBatchDiv').removeClass('hidden');
				$('#enableFileMonitoring_Div').removeClass('hidden');
				$('#s3ConnDiv').removeClass('hidden');
				$('#s3Access_div').addClass('hidden');
				$('#bigConnDiv').addClass('hidden');
				$('#azureDataLakeDiv').addClass('hidden');
				$('#incrementalDataReadEnabled_div').removeClass('hidden');
			} else if (selectedVal == 'S3 IAMRole Batch Config') {
				$('#hiveKnoxDiv').addClass('hidden').show();
				$('#hiveKnoxUrlDiv').addClass('hidden').show();
				$('#MSSQLAD').addClass('hidden');
				$('#OracleXE').addClass('hidden');
				$('#krb5confdiv').addClass('hidden');
				$('#basicFormDiv').addClass('hidden');
				$('#fileSystemBatchDiv').removeClass('hidden');
				$('#enableFileMonitoring_Div').removeClass('hidden');
				$('#s3ConnDiv').removeClass('hidden');
				$('#s3Access_div').addClass('hidden');
				$('#bigConnDiv').addClass('hidden');
				$('#azureDataLakeDiv').addClass('hidden');
			}else if (selectedVal == 'BigQuery') {
				$('#hiveKnoxDiv').addClass('hidden').show();
				$('#hiveKnoxUrlDiv').addClass('hidden').show();
				$('#MSSQLAD').addClass('hidden');
				$('#OracleXE').addClass('hidden');
				$('#krb5confdiv').addClass('hidden');
				$('#basicFormDiv').addClass('hidden');
				$('#fileSystemBatchDiv').addClass('hidden');
				$('#enableFileMonitoring_Div').addClass('hidden');
				$('#s3ConnDiv').addClass('hidden');
				$('#bigConnDiv').removeClass('hidden');
				$('#folderPath').addClass('hidden');
				$('#fileNamePatternandFileDataFormat').addClass('hidden');
				$('#azureDataLakeDiv').addClass('hidden');
			} else if (selectedVal == 'AzureDataLakeStorageGen1') {
				$('#basicFormDiv').addClass('hidden');
				$('#fileSystemBatchDiv').addClass('hidden');
				$('#enableFileMonitoring_Div').addClass('hidden');
				$('#s3ConnDiv').addClass('hidden');
				$('#azureDataLakeDiv').removeClass('hidden');
				$('#bigConnDiv').addClass('hidden');
				$('#hiveKnoxDiv').addClass('hidden').show();
				$('#hiveKnoxUrlDiv').addClass('hidden').show();
				$('#krb5confdiv').addClass('hidden');
				$('#MSSQLAD').addClass('hidden');
				$('#autoGenerateDiv').addClass('hidden');
				$('#OracleXE').addClass('hidden');
				$('#bigConnDiv').addClass('hidden');
			} else if (selectedVal == 'AzureDataLakeStorageGen2Batch') {
                  $('#hiveKnoxDiv').addClass('hidden').show();
                  $('#hiveKnoxUrlDiv').addClass('hidden').show();
                  $('#fileSystemBatchDiv').addClass('hidden');
                  $('#MSSQLAD').addClass('hidden');
                  $('#OracleXE').addClass('hidden');
                  $('#krb5confdiv').addClass('hidden');
                  $('#basicFormDiv').addClass('hidden');
                  $('#azureDataLakeStorageGen2').removeClass('hidden');
                  $('#enableFileMonitoring_Div').removeClass('hidden');
                  $('#azureDataLakeDiv').addClass('hidden');
                  $('#s3ConnDiv').removeClass('hidden');
                  $('#s3Access_div').removeClass('hidden');
                  $('#bigConnDiv').addClass('hidden');
                  $('#azureDataLakeDiv').addClass('hidden');
                  $('#enableFileMonitoring_Div').addClass('hidden');
                  $('#access-key').text("AccountName*");
                  $('#secret-Key').text("AccountKey*");
                  $("#secretKey").attr("placeholder", "Leave AccountKey blank to keep it unchanged");
                  $('#bucketNameId').text("ContainerName");
                  $('#partitionedFolders_div').removeClass('hidden');
                  $('#enableFileMonitoring_Div').removeClass('hidden');
                  $('#incrementalDataReadEnabled_div').removeClass('hidden');
            }else if (selectedVal == 'MapR DB') {
				$('#basicFormDiv').removeClass('hidden');
				$('#fileSystemBatchDiv').addClass('hidden');
				$('#enableFileMonitoring_Div').addClass('hidden');
				$('#s3ConnDiv').addClass('hidden');
				$('#azureDataLakeDiv').addClass('hidden');
				$('#bigConnDiv').addClass('hidden');
				$('#hiveKnoxDiv').addClass('hidden');
				$('#hiveKnoxUrlDiv').addClass('hidden');
				$('#Username').hide();
				$('#Username').parent().fadeOut();
				$('#Port').hide();
				$('#Port').parent().fadeOut();
				$('#Pwd').hide();
				$('#Pwd').parent().fadeOut();
				$('#datasetid').hide();
				$('#datasetid').parent().fadeOut();
				$('#confirmPassword').hide();
				$('#confirmPassword').parent().fadeOut();
				$('#s3_form_id').addClass('hidden');
				$("#s3_form_id").hide();
				$('#descriptionid').attr("placeholder",
					"Enter folder Name Ex : /apps/");
			} else if (selectedVal == 'MapR Hive') {
				$('#basicFormDiv').removeClass('hidden');
				$('#fileSystemBatchDiv').addClass('hidden');
				$('#enableFileMonitoring_Div').addClass('hidden');
				$('#azureDataLakeDiv').addClass('hidden');
				$('#bigConnDiv').addClass('hidden');
				$('#hiveKnoxDiv').addClass('hidden');
				$('#hiveKnoxUrlDiv').addClass('hidden');
				$('#s3ConnDiv').addClass('hidden');
				$('#Username').hide();
				$('#Username').parent().fadeOut();
				$('#Pwd').hide();
				$('#Pwd').parent().fadeOut();
				$('#confirmPassword').hide();
				$('#confirmPassword').parent().fadeOut();
				$('#maprHive').removeClass('hidden');
			} else if (selectedVal == 'SnowFlake') {
                 //$('#SnowFlake').removeClass('hidden').show();
                 $('#fileSystemBatchDiv').addClass('hidden');
                 $('#MSSQLAD').removeClass('hidden');
                 $('#OracleXE').addClass('hidden');
                 $('#krb5confdiv').addClass('hidden');
                 $('#labelURI').text("Uri");
                 $('#labelServiceName').text("Service Name");
                 $('#labelDomain').addClass('hidden');
                 $("#Domain").addClass('hidden');
                 $('#autoGenerateDiv').addClass('hidden');
                 $('#basicFormDiv').removeClass('hidden');
                 $('#s3ConnDiv').addClass('hidden');
                 $('#azureDataLakeDiv').addClass('hidden');
                 $('#bigConnDiv').addClass('hidden');
                 $('#ignoreDiv').addClass('hidden');
                 $('#hiveKnoxDiv').addClass('hidden');
                 $('#hiveKnoxUrlDiv').addClass('hidden');
                 $('#enableFileMonitoring_Div').removeClass('hidden');
            }  else {
                   if (selectedVal == 'MYSQL') {
                           $('#sslEnabled_div').removeClass('hidden');
                       }
				$('#basicFormDiv').removeClass('hidden');
				$('#fileSystemBatchDiv').addClass('hidden');
				$('#enableFileMonitoring_Div').addClass('hidden');
				$('#MSSQLAD').addClass('hidden');
				$('#OracleXE').addClass('hidden');
				$('#krb5confdiv').addClass('hidden');
				$('#labelURI').text("Uri");
				$('#labelServiceName').text("Service Name");
				if(selectedVal == 'Teradata' || selectedVal == 'MSSQL'){
					$('#labelDatabaseName').text("Schema");
				} else{
					$('#labelDatabaseName').text("Database");
				}
				$('#labelDomain').text("Domain");
				$("#datasetid").attr("placeholder", "Ex: 55.33.130.223");
				$("#servicename").attr("placeholder", "Ex: xe");
				$("#Domain").attr("placeholder", "Ex: ec2-34-168-70-22.us-west-2.compute.amazonaws.com");
				$('#s3ConnDiv').addClass('hidden');
				$('#bigConnDiv').addClass('hidden');
				$('#azureDataLakeDiv').addClass('hidden');
			}
		});
		$('#datasetupid')
			.click(
				function () {
					var no_error = 1;
					$('.input_error').remove();
					
					if(!checkInputText()){ 
						no_error = 0;
						alert('Vulnerability in submitted data, not allowed to submit!');
					}
					
					var kmsAuthDisabled = "N";
					if ($("#kmsAuthDisabled").prop("checked") == true) {
						kmsAuthDisabled = "Y";
					}
					var pushDownQueryEnabled = "N";
                    if ($("#pushDownQueryEnabled").prop("checked") == true) {
                        pushDownQueryEnabled = "Y";
                    }
					var sslEnabled = "N";
					if ($("#sslEnabled").prop("checked") == true) {
						sslEnabled = "Y";
					}
					console.log("kmsAuthDisabled:"+kmsAuthDisabled);
					console.log("sslEnabled:"+sslEnabled);

					var alation_integration_enabled = "N";
                    if ($("#alation_integration_enabled").prop("checked") == true) {
                        alation_integration_enabled = "Y";
                    }
                    console.log("alation_integration_enabled:"+alation_integration_enabled);

					var readLatestPartition = "N";
					
					if ($("#schemaNameid").val().length == 0) {
						console.log('schemaNameid');
						$(
							'<span class="input_error" style="font-size:12px;color:red">Please enter Schema Name</span>')
							.insertAfter($('#schemaNameid'));
						no_error = 0;
					}

					if ($("#schemaTypeid").val() == 'FileSystem Batch' || $("#schemaTypeid").val() == 'S3 Batch'
						|| $("#schemaTypeid").val() == 'S3 IAMRole Batch' || $("#schemaTypeid").val() == 'S3 IAMRole Batch Config') {

						if ($("#schemaTypeid").val() == 'S3 Batch') {

							if ($("#accessKey").val().length == 0) {
								console.log('accessKey');
								$(
									'<span class="input_error" style="font-size:12px;color:red">Please enter AccessKey</span>')
									.insertAfter($('#accessKey'));
								no_error = 0;
							}

							if ($("#bucketName").val().length == 0) {
								console.log('bucketName');
								$(
									'<span class="input_error" style="font-size:12px;color:red">Please enter BucketName</span>')
									.insertAfter($('#bucketName'));
								no_error = 0;
							}
						}

						if ($("#schemaTypeid").val() == 'S3 IAMRole Batch') {

							if ($("#bucketName").val().length == 0) {
								console.log('bucketName');
								$(
									'<span class="input_error" style="font-size:12px;color:red">Please enter BucketName</span>')
									.insertAfter($('#bucketName'));
								no_error = 0;
							}
						}
						if ($("#schemaTypeid").val() == 'S3 IAMRole Batch Config') {

							if ($("#bucketName").val().length == 0) {
								console.log('bucketName');
								$(
									'<span class="input_error" style="font-size:12px;color:red">Please enter BucketName</span>')
									.insertAfter($('#bucketName'));
								no_error = 0;
							}
						}

						if ($("#schemaTypeid").val() == 'FileSystem Batch') {
							if ($("#folderPath").val().length == 0) {
								console.log('folderPath');
								$(
									'<span class="input_error" style="font-size:12px;color:red">Please enter FolderPath</span>')
									.insertAfter($('#folderPath'));
								no_error = 0;
							}
						}
						if ($("#fileNamePattern").val().length == 0) {
							console.log('fileNamePattern');
							$(
								'<span class="input_error" style="font-size:12px;color:red">Please enter FileNamePattern</span>')
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

						if ($('#multiPatternId').is(':checked')) {
							if ($("#startingUniqueCharCountId").val().length == 0 && $("#endingUniqueCharCountId").val().length == 0) {
								$('<span class="input_error" style="font-size:12px;color:red">Please enter either starting count or ending count</span>')
									.insertAfter($('#startingUniqueCharCountId'));

								$('<span class="input_error" style="font-size:12px;color:red">Please enter either starting count or ending count</span>')
									.insertAfter($('#endingUniqueCharCountId'));
								no_error = 0;
							}
						}

						if ($('#partitionedFoldersId').is(':checked')) {
							if (($("#maxFolderDepthId").val().length == 0 || $("#maxFolderDepthId").val() == 0 || $("#maxFolderDepthId").val() > 2)) {
								$('<span class="input_error" style="font-size:12px;color:red">Please enter valid depth 1 or 2 </span>')
									.insertAfter($('#maxFolderDepthId'));

								no_error = 0;
							}
						}
					} else if ($("#schemaTypeid").val() == 'AzureDataLakeStorageGen2Batch') {

                            if ($("#accessKey").val().length == 0) {
                                console.log('accessKey');
                                $(
                                    '<span class="input_error" style="font-size:12px;color:red">Please enter Account Name</span>')
                                    .insertAfter($('#accessKey'));
                                no_error = 0;
                            }

                            if ($("#bucketName").val().length == 0) {
                                console.log('bucketName');
                                $(
                                    '<span class="input_error" style="font-size:12px;color:red">Please enter Container Name</span>')
                                    .insertAfter($('#bucketName'));
                                no_error = 0;
                            }
                            if ($("#azureFolderPath").val().length == 0) {
                                console.log('azureFolderPath');
                                $(
                                    '<span class="input_error" style="font-size:12px;color:red">Please enter AzureFolderPath</span>')
                                    .insertAfter($('#azureFolderPath'));
                                no_error = 0;
                            }
                              if ($("#azureFileDataFormat").val().length == 0) {
                                console.log('azureFileDataFormat');
                                $(
                                    '<span class="input_error" style="font-size:12px;color:red">Please enter AzureFileFormat</span>')
                                    .insertAfter($('#azureFileDataFormat'));
                                no_error = 0;
                            }
					}
					else if ($("#schemaTypeid").val() == 'BigQuery') {

						if ($("#privatekey").val().length == 0) {
							console.log('privatekey');
							$(
								'<span class="input_error" style="font-size:12px;color:red">Please enter privatekey</span>')
								.insertAfter($('#privatekey'));
							no_error = 0;
						}
						if ($("#privatekeyid").val().length == 0) {
							console.log('privatekeyid');
							$(
								'<span class="input_error" style="font-size:12px;color:red">Please enter privatekeyid</span>')
								.insertAfter($('#privatekeyid'));
							no_error = 0;
						}
						if ($("#bigQueryProjectName").val().length == 0) {
							console.log('bigQueryProjectName');
							$(
								'<span class="input_error" style="font-size:12px;color:red">Please enter bigQueryProjectName</span>')
								.insertAfter($('#bigQueryProjectName'));
							no_error = 0;
						}
						if ($("#datasetName").val().length == 0) {
							console.log('datasetName');
							$(
								'<span class="input_error" style="font-size:12px;color:red">Please enter datasetName</span>')
								.insertAfter($('#datasetName'));
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

					} else if ($("#schemaTypeid").val() == 'AzureDataLakeStorageGen1') {

						if ($("#azureClientId").val().length == 0) {
							$(
								'<span class="input_error" style="font-size:12px;color:red">Please enter clientID</span>')
								.insertAfter($('#azureClientId'));
							no_error = 0;

						}
						if ($("#azureClientSecret").val().length == 0) {
							$(
								'<span class="input_error" style="font-size:12px;color:red">Please enter clientSecret</span>')
								.insertAfter($('#azureClientSecret'));
							no_error = 0;

						}
						if ($("#azureTenantId").val().length == 0) {
							$(
								'<span class="input_error" style="font-size:12px;color:red">Please enter tenantId</span>')
								.insertAfter($('#azureTenantId'));
							no_error = 0;

						}
						if ($("#azureServiceURI").val().length == 0) {
							$(
								'<span class="input_error" style="font-size:12px;color:red">Please enter azserviceURI</span>')
								.insertAfter($('#azureServiceURI'));
							no_error = 0;

						}
					}
					else {
						if ($("#descriptionid").val().length == 0) {
							console.log('descriptionid');
							if($("#schemaTypeid").val() == 'Oracle'){
								$('<span class="input_error" style="font-size:12px;color:red">Please enter DatabaseSchemaName</span>')
								.insertAfter($('#descriptionid')); 
							} else {
								$('<span class="input_error" style="font-size:12px;color:red">Please enter DatabaseName</span>')
								.insertAfter($('#descriptionid'));
							}
							no_error = 0;
						}
						
						if($("#schemaTypeid").val() == 'Oracle'){
							if($("#servicename").val().length == 0){
								$('<span class="input_error" style="font-size:12px;color:red">Please enter Service Name</span>')
								.insertAfter($('#servicename')); 
							}
						}
						
						if(kmsAuthDisabled == 'Y'){
							if ($("#datasetid").val().length == 0) {
								console.log('datasetid');
								$(
									'<span class="input_error" style="font-size:12px;color:red">Please enter URI</span>')
									.insertAfter($('#datasetid'));
								no_error = 0;
							}
							
							if ($("#Username").val().length == 0) {
								console.log('Username');
								$(
									'<span class="input_error" style="font-size:12px;color:red">Please enter Username</span>')
									.insertAfter($('#Username'));
								no_error = 0;
							}
							
							if ($("#Port").val().length == 0) {
								console.log('Username');
								$(
									'<span class="input_error" style="font-size:12px;color:red">Please enter Port Number</span>')
									.insertAfter($('#Port'));
								no_error = 0;
							}
							
							if ($("#schemaTypeid").val() == -1) {
								console.log('schemaTypeid');
								$(
									'<span class="input_error" style="font-size:12px;color:red">Please Choose a Schema Name</span>')
									.insertAfter($('#schemaTypeid'));
								no_error = 0;
							}
							var password = $("#Pwd").val();
							var confirmPassword = $("#confirmPassword").val();
							//alert(password);
							//alert(confirmPassword);
							if (password == confirmPassword) {
								//alert("same");
							} else {
								$(
									'<span class="input_error" style="font-size:12px;color:red">Please Enter Confirm Password Same as Password</span>')
									.insertAfter($('#confirmPassword'));
								no_error = 0;
							}
						}
						if(pushDownQueryEnabled == 'Y'){
                            if ($("#datasetid").val().length == 0) {
                                $(
                                    '<span class="input_error" style="font-size:12px;color:red">Please enter URI</span>')
                                    .insertAfter($('#datasetid'));
                                no_error = 0;
                            }
                        }
						/* var amit1 = $('#amit').html();
						// alert("outside"+amit1)
						 console.log(amit1);
						 	if(amit1=="")
						 	{
						 		
						 	}
						 	else{
						 		console.log("inside if");
						 		//toaster.info("Schema created successfully");
						         //$('<span class="input_error" style="font-size:12px;color:red">already exist Database Name</span>').insertAfter($('#descriptionid'));
						         no_error = 0;
						 	} */

						if ($("#schemaTypeid").val() == 'MapR DB') {
							if ($("#descriptionid").val().length != 0) {
								no_error = 1;
							} else {
								no_error = 0;
							}
						}

						if ($("#schemaTypeid").val() == 'MapR Hive') {
							if ($("#descriptionid").val().length != 0) {
								no_error = 1;
							} else if ($("#datasetid").val().length != 0) {
								no_error = 1;
							} else if ($("#Port").val().length != 0) {
								no_error = 1;
							} else {
								no_error = 0;
							}
							 if ($("#readLatestPartition").prop("checked") == true) {
                                    readLatestPartition = "Y";
                            }
						}
					}


					// External File Name
					if ($("#externalFileNamePatternId").prop("checked") == true) {
						if ($("#externalFileNameId").val().length == 0) {
							$(
								'<span class="input_error" style="font-size:12px;color:red">Please Enter External File Name</span>')
								.insertAfter($('#externalFileName'));
							no_error = 0;
						}
						if ($("#patternColumnId").val().length == 0) {
							$(
								'<span class="input_error" style="font-size:12px;color:red">Please Enter Pattern Column</span>')
								.insertAfter($('#patternColumn'));
							no_error = 0;
						}
						if ($("#headerColumnId").val().length == 0) {
							$(
								'<span class="input_error" style="font-size:12px;color:red">Please Enter header Column</span>')
								.insertAfter($('#headerColumn'));
							no_error = 0;
						}
						if ($("#localDirectoryColumnIndexId").val().length == 0) {
							$(
								'<span class="input_error" style="font-size:12px;color:red">Please Enter Local Directory Column Index</span>')
								.insertAfter($('#localDirectoryColumnIndex'));
							no_error = 0;
						}
						if ($("#xsltFolderPathId").val().length == 0) {
							$(
								'<span class="input_error" style="font-size:12px;color:red">Please XSLT Folder Path</span>')
								.insertAfter($('#xsltFolderPathName'));
							no_error = 0;
						}
						
					}


					if (!no_error) {
						return false;
					} else {
						$('.btn').addClass('hidden');

					}

					if ($("#headerId").prop("checked") == true) {
						var headerPresent = "Y";
					} else {
						var headerPresent = "N";
					}

					var fileEncrypted = "N";
					if ($("#fileEncryptedId").prop("checked") == true) {
						fileEncrypted = "Y";
					}
					var singleFile = "N";
					if ($("#singleFileId").prop("checked") == true) {
						singleFile = "Y";
					}
					
					var multiFolderEnabled = "N";
					if ($("#multiFolderEnabled").prop("checked") == true) {
						multiFolderEnabled = "Y";
					}

					var pushDownQueryEnabled = "N";
                    if ($("#pushDownQueryEnabled").prop("checked") == true) {
                        pushDownQueryEnabled = "Y";
                    }

                    var enableFileMonitoring = "N";
                    if ($("#enableFileMonitoring_Id").prop("checked") == true) {
                        enableFileMonitoring = "Y";
                    }

					var partitionedFolders = "N";
					var maxFolderDepth = 0;
					if ($("#partitionedFoldersId").prop("checked") == true) {
						partitionedFolders = "Y";
						maxFolderDepth = $("#maxFolderDepthId").val();
					}

					var multiPattern = "N";
					var startingUniqueCharCount = $("#startingUniqueCharCountId").val();
					var endingUniqueCharCount = $("#endingUniqueCharCountId").val();
					if ($("#multiPatternId").prop("checked") == true) {
						multiPattern = "Y";
					} else {
						startingUniqueCharCount = 0;
						endingUniqueCharCount = 0;
					}
					if ($("#externalFileNamePatternId").prop("checked") == true) {
						var externalFileNamePatternVal = "Y";
					} else {
						var externalFileNamePatternVal = "N";
					}
					
					var azurePartitionedFolders = "N";
					if ($("#azurePartitionedFoldersId").prop("checked") == true) {
						azurePartitionedFolders = "Y";
					}
					
                    var folderPath= $("#folderPath").val();
                    var fileDataFormat= $("#fileDataFormat").val();
                    if($("#schemaTypeid").val() == "AzureDataLakeStorageGen2Batch"){
                        folderPath = $("#azureFolderPath").val();
                        fileDataFormat= $("#azureFileDataFormat").val();
                        partitionedFolders=azurePartitionedFolders;
                    }
                    var incrementalDataReadEnabled="N";
                     if ($("#incrementalsourceid").prop("checked") == true) {
                                    incrementalDataReadEnabled = "Y";
                    }

					var form_data = {
						schemaName: $("#schemaNameid").val(),
						schemaType: $("#schemaTypeid").val(),
						uri: $("#datasetid").val(),
						database: $("#descriptionid").val(),
						username: $("#Username").val(),
						password: $("#Pwd").val(),
						port: $("#Port").val(),
						domain: $("#Domain").val(),
						serviceName: $("#servicename").val(),
						idDataSchema: $("#idDataSchema").val(),
						krb5conf: $("#krb5conf").val(),
						hivejdbchost: "",
						hivejdbcport: "",
						sslTrustStorePath: "",
						trustPassword: "",
						gatewayPath: $("#gatewayPathId").val(),
						incrementalDataReadEnabled: incrementalDataReadEnabled,
						jksPath: $("#jksPathId").val(),
						zookeeperUrl: $("#zookeeperUrlId").val(),
						folderPath: folderPath,
						fileNamePattern: $("#fileNamePattern").val(),
						fileDataFormat: fileDataFormat,
						headerPresent: headerPresent,
						partitionedFolders: partitionedFolders,
						headerFilePath: $("#headerFilePath").val(),
						headerFileNamePattern: $("#headerFileNamePattern").val(),
						headerFileDataFormat: $("#headerFileDataFormat").val(),
						accessKey: $("#accessKey").val(),
						secretKey: $("#secretKey").val(),
						bucketName: $("#bucketName").val(),
						privatekey: $("#privatekey").val(),
						privatekeyId: $("#privatekeyid").val(),
						bigQueryProjectName: $("#bigQueryProjectName").val(),
						datasetName: $("#datasetName").val(),
						clientId: $("#clientId").val(),
						clientEmail: $("#clientEmail").val(),
						azureClientId: $("#azureClientId").val(),
						azureClientSecret: $("#azureClientSecret").val(),
						azureTenantId: $("#azureTenantId").val(),
						azureServiceURI: $("#azureServiceURI").val(),
						azureFilePath: $("#azureFilePath").val(),
						multiPattern: multiPattern,
						startingUniqueCharCount: startingUniqueCharCount,
						endingUniqueCharCount: endingUniqueCharCount,
						maxFolderDepth: maxFolderDepth,
						fileEncrypted: fileEncrypted,
						singleFile: singleFile,
						enableFileMonitoring: enableFileMonitoring,
						externalFileNamePatternId: externalFileNamePatternVal,
						externalFileName: $("#externalFileNameId").val(),
						patternColumn: $("#patternColumnId").val(),
						headerColumn: $("#headerColumnId").val(),
						localDirectoryColumnIndex: $("#localDirectoryColumnIndexId").val(),
						xsltFolderPath: $("#xsltFolderPathId").val(),
						kmsAuthDisabled: kmsAuthDisabled,
						pushDownQueryEnabled:pushDownQueryEnabled,
						readLatestPartition:readLatestPartition,
						alation_integration_enabled:alation_integration_enabled,
						multiFolderEnabled:multiFolderEnabled,
						sslEnb:sslEnabled
					};
					console.log(form_data);
					$
						.ajax({
							url: './updateSchema',
							type: 'POST',
							headers: { 'token':$("#token").val()},
							datatype: 'json',
							data: form_data,
							success: function (data) {
								//alert(data);
								if (data != "") {
									toastr.info(data);
									setTimeout(
										function () {
											location.reload();
											window.location.href = 'dataConnectionView';
										}, 1000);
								} else {
									toastr.info('Data Connection Failed, Please Check Configuration.');
								}
							},
							error: function (xhr, textStatus,
								errorThrown) {
								$('#initial').hide();
								$('#fail').show();
							}
						});
					/* if( no_error==0){
						console.log('error');
						return false;
					} */
				});
	</script>
</head>
