<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<script>
	var request;
	var vals = "";
	function sendInfo() {
		var v = document.getElementById("datasetid").value;
		//var v=document.vinform.Database.value;  
		var url = "./duplicatedatatemplatename?val=" + v;
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
							<span class="caption-subject bold "> Edit Data Template </span>
						</div>
					</div>

					<div class="portlet-body form">
						<input type="hidden" id="tourl" value="createForm" />
						<!--  <form role="form" method="post" action="">  -->
						<form action="updateDataTemplate" method="GET"
							accept-charset="utf-8">
							<div class="form-body">
								<div class="row" id="template_data">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control catch-error"
												id="datasetid" name="dataset"
												value="${listDataSource.name }" onkeyup="sendInfo()" readonly="readonly">
											<label for="form_control_1">Data Template Name *</label>
											<!--  <span class="help-block">Data Set Name </span> -->
										</div>
										<span id="amit" class="help-block required"></span>
									</div>
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="descriptionid"
												name="description" value="${listDataSource.description }">
											<label for="form_control">Description</label>
											<!-- <span class="help-block">Short text about Data Set</span> -->
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<select disabled class="form-control" id="locationid"
												name="location" placeholder="Choose Location">
												<option value="-1">Select...</option>
												<option value="FILESYSTEM">File System</option>
												<option value="File Management">File Management</option>
												<option value="HDFS">HDFS</option>
												<option value="MSSQL">MS SQL</option>
												<option value="MYSQL">MYSQL</option>
												<option value="MSSQLActiveDirectory">MS SQL(Active
													Directory)</option>
												<option value="Vertica">Vertica</option>
												<option value="Oracle">Oracle (Standalone)</option>
												<option value="Hive">Hive</option>
												<option value="Cassandra">Cassandra</option>
												<option value="Postgres">Postgres</option>
												<option value="Teradata">Teradata</option>
												<option value="Amazon Redshift">Amazon Redshift</option>
												<option value="Hive Kerberos">Hive Kerberos</option>
												<option value="Oracle RAC">Oracle (RAC)</option>
													<option value="S3">S3</option>
												<option value="MapR FS">MapR FS</option>
												<option value="MapR DB">MapR DB</option>
												<option value="MapR Hive">MapR Hive</option>
												<option value="SnowFlake">SnowFlake</option>
												<option value="FileSystem Batch">FileSystem (Batch)</option>
												<option value="S3 Batch">S3 (Batch)</option>
												<option value="S3 IAMRole Batch">S3 IAMRole(Batch)</option>
												<option value="BigQuery">BigQuery</option>
												<option value="AzureSynapseMSSQL">AzureSynapseMSSQL</option>
												<option value="AzureDataLakeStorageGen1">Azure DataLake Storage Gen1</option>
												<option value="AzureDataLakeStorageGen2">Azure DataLake Storage Gen2</option>
												<option value="AzureDataLakeStorageGen2Batch">Azure DataLake Storage Gen2(Batch)</option>
											</select> <label for="form_control_1">Connection Type *</label>
											<!-- <span class="help-block">Data Location  </span> -->
										</div>
										<br /> <span class="required"></span>
									</div>
									
									<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input" id="domainDivId">
										<select disabled class="form-control" id="selectedDomainId" name="domainfunction">
										    <option value="-1">Select Domain</option>
											<c:forEach var="getDomainListobj" items="${listdomain}">
												 
												 <option value="${getDomainListobj.domainId}">${getDomainListobj.domainName}</option>
					
											</c:forEach>
										</select>
										<label for="blendfunid">Domain </label><br>
									</div>
									
								</div>
								<!-- <div id="hide-file" style="display:none;">
                                        <div class="row" id="csv_id" id="uploaddivid">
                                            <div class="col-md-6 col-capture">
                                                <div class="form-group form-md-line-input">
                                                     <input type="file" class="form-control" name="upload" id="upload_id" /> 
                                                    <label for="form_control">Metadata File</label>
                                                </div><br/>
                                            </div>
                                            <div class="col-md-6 col-capture" id="s3typeid">
                                                <div class="form-group form-md-line-input">
                                                    <input type="text" class="form-control" id="sourceid" name="source" placeholder="Enter Data Template">
                                                    <select class="form-control" id="s3typevalueid" name="s3type" placeholder="Choose Location">
                                                      <option value="DataBuck S3 Bucket">DataBuck S3 Bucket</option>
                                                      <option value="Your S3 Bucket">Your S3 Bucket</option>
                                                    </select>
                                                    <label for="form_control_1">Choose Bucket Type</label>
                                                    <span class="help-block">Short text about Data Set</span>
                                                </div>
                                            </div>
                                        </div> 
								<div class="row hidden" id="uploaddivid">
									<div class="col-md-6 col-capture">
										 <div class="form-group form-md-line-input">
											<input type="file" class="form-control" name="dataupload"
												id="dataupload_id" />  <label for="form_control"
												id="fileType">Data Source File</label>
										</div> 
										<br />
									</div>
								</div> -->
								
								</div>
								
								<div class="row">
								<div class="col-md-6 col-capture hidden" id="sourcedivid">
										<div class="form-group form-md-line-input">
											<!-- <input type="text" class="form-control" id="sourceid" name="source" placeholder="Enter Data Template"> -->
											<select disabled="disabled" class="form-control"
												id="sourceid" name="source" placeholder="Choose Location">
												<option value="PSV">PSV(Pipe Delimited)</option>
												<option value="CSV">CSV</option>
												<option value="TSV">TSV</option>
												<option value="ORC">ORC</option>
												<option value="Parquet">Parquet</option>
												<option value="JSON">JSON</option>
												<option value="FLAT">Flat File</option>
												<!-- <option value="xls">XLS</option> -->
											</select> <label for="form_control_1">Data Format</label>
											<!-- <span class="help-block">Short text about Data Set</span> -->
										</div>
									</div>
								
								<div class="col-md-6 col-capture" id = "ignoreRowsId">
											<div class="form-group form-md-line-input">
												<input type="text" class="form-control catch-error"
													id="rowsId" name="rowsId"
													value="${listDataSource.ignoreRowsCount}"> <label
													for="form_control_1" id="folder-id">Number of rows
													to be ignored </label>
												<!--  <span class="help-block">Data Set Name </span> -->
											</div>
											<br /> <span class="required"></span>
								</div>
								</div>

								<%-- <div class="form-body hidden" id="checkid">
									<!-- <div class="form-body"  id="hdfs_form_id"> -->
									<div class="row">
										<div class="col-md-6 col-capture" id="host-id-container">
											<div class="form-group form-md-line-input">

												<input type="checkbox" checked class="md-check"
													id="headerId" name="headerId"> <label
													for="form_control_1" id="host-id">File has Header?*</label>
												<!--  <span class="help-block">Data Set Name </span> -->
											</div>
											<br /> <span class="required"></span>
										</div>
										<div class="col-md-6 col-capture">
											<div class="form-group form-md-line-input">
												<input type="text" class="form-control catch-error"
													id="rowsId" name="rowsId"
													value="${listDataSource.ignoreRowsCount}"> <label
													for="form_control_1" id="folder-id">Number of rows
													to be ignored </label>
												<!--  <span class="help-block">Data Set Name </span> -->
											</div>
											<br /> <span class="required"></span>
										</div>
									</div>
								</div> --%>

								<%-- <div class="form-body" id="rollingHeadercheckid">
									<!-- <div class="form-body"  id="hdfs_form_id"> -->
									<div class="row">
										<div class="col-md-6 col-capture">
											<div class="form-group form-md-line-input">

												<input type="checkbox" class="md-check"
													id="rollingHeaderId" name="rollingHeader" value="${listDataAccess.rollingHeader}"> <label
													for="form_control_1" id="rollingHeader-id">Enable Rolling Header?</label>
												<!--  <span class="help-block">Data Set Name </span> -->
											</div>
											<br /> <span class="required"></span>
										</div>
										
										<div class="col-md-6 col-capture hidden" id="rollingColumnId">
											<div class="form-group form-md-line-input">
												<input type="text" class="form-control catch-error"
													id="rollingColumn_Id" name="rollingColumn" value="${listDataAccess.rollingColumn}"> <label
													for="form_control_1" id="rollingColumn-id">Rolling Column Name</label>
												<!--  <span class="help-block">Data Set Name </span> -->
											</div>
											<br /> <span class="required"></span>
										</div>
									</div>
								</div> --%>
								
								<div class="form-body hidden" id="checkid">
									<!-- <div class="form-body"  id="hdfs_form_id"> -->
									<div class="row">
										<div class="col-md-6 col-capture" id="host-id-container">
											<div class="form-group form-md-line-input">

												<input type="checkbox" checked class="md-check"
													id="headerId" name="headerId"> <label
													for="form_control_1" id="host-id">File has Header?*</label>
												<!--  <span class="help-block">Data Set Name </span> -->
											</div>
											<br /> <span class="required"></span>
										</div>
										
										<div class="col-md-6 col-capture" id="rollingHeaderCheckId">
											<div class="form-group form-md-line-input">

												<input type="checkbox" class="md-check"
													id="rollingHeaderId" name="rollingHeaderPresent" value="${listDataAccess.rollingHeader}"> <label
													for="form_control_1" id="rollingHeader-id">Enable Rolling Header?</label>
												<!--  <span class="help-block">Data Set Name </span> -->
											</div>
											<br /> <span class="required"></span>
										</div>
									</div>
								</div>
								
								<div class="row">
									<div class="col-md-6 col-capture hidden" id="uploaddivid">
										<div class="form-group form-md-line-input">
											<input type="file" class="form-control" name="dataupload"
												id="dataupload_id" /> <label for="form_control"
												id="fileType">Data Source File</label>
										</div>
										<br />
									</div>
									<div class="col-md-6 col-capture hidden" id="rollingColumnId">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control catch-error"
												id="rollingColumn_Id" name="rollingColumn" value="${listDataAccess.rollingColumn}"> <label
												for="form_control_1" id="rollingColumn-id">Rolling Column Name</label>
											<!--  <span class="help-block">Data Set Name </span> -->
										</div>
										<br /> <span class="required"></span>
									</div>
								</div>
								
								<div class="form-body" style="display: none;" id="s4_form_id">
									<div class="row">
										<div class="col-md-6 col-capture" id="host-id-container">
											<div class="form-group form-md-line-input">
												<select disabled class="form-control" id="schemaId"
													name="schemaId1">
													<option>${ listDataSchema.schemaName }</option>
												</select> <label for="form_control_1"> Select Data Connection
													* </label>
											</div>
											<br /> <span class="required"></span>
										</div>
										<%-- <div class="col-md-6 col-capture" id="host-id-container">
											<div class="form-group form-md-line-input">
												<select class="form-control" id="schemaId" name="schemaId1">
													<option value="">Select...</option>
													<c:forEach var="listdataschema" items="${listdataschema}">

														<option value="${listdataschema.idDataSchema}">${listdataschema.databaseSchema}</option>

													</c:forEach>
												</select> <label for="form_control_1" id="host-id">Data
													Connection*</label>
												<!--  <span class="help-block">Data Set Name </span> -->
											</div>
											<br /> <span class="required"></span>
										</div> --%>
										<div class="col-md-6 col-capture">
											<div class="form-group form-md-line-input">
												<input readonly="readonly" type="text"
													class="form-control catch-error" id="tableNameid"
													name="tableNameid" value="${listDataAccess.folderName }">
												<label for="form_control_1" id="folder-id">Table
													Name*</label>
												<!--  <span class="help-block">Data Set Name </span> -->
											</div>
											<br /> <span class="required"></span>
										</div>
									</div>
									<div class="row">

										<div class="col-md-6 col-capture" id="queryid1">
											<div class="form-group form-md-checkboxes">
												<div class="md-checkbox-list">
													<div class="md-checkbox">
														<input type="checkbox" disabled="disabled" class="md-check"
															id="querycheckboxid" name="querycheckboxid" value="Y"
															<c:if test = "${listDataAccess.query eq 'Y'}"> checked= "checked"    </c:if>>
														<label for="querycheckboxid"><span></span> <span
															class="check"></span> <span class="box"></span>Query?</label>
													</div>
												</div>
											</div>
										</div>
										<div class="col-md-6 col-capture" id="whereconditionid">
											<div class="form-group form-md-line-input">
												<input type="text" class="form-control catch-error"
													id="whereId" name="whereId"
													value="${listDataAccess.whereCondition }"> <label
													for="whereId" id="whereId">Where Condition</label>
												<!--  <span class="help-block">Data Set Name </span> -->
											</div>
											<br /> <span class="required"></span>
										</div>
									</div>
									<div class="row">
										<div class="col-md-12 col-capture hidden"
											id="querytextboxiddiv">
											<div class="form-group form-md-line-input">
												<!-- <input type="text"  readonly="readonly" class="form-control catch-error" -->
												<input type="text"  class="form-control catch-error"
													id="querytextboxid" name="querytextboxid"
													<c:if test = "${listDataAccess.query eq 'Y'}"> 	value="${listDataAccess.queryString }"  </c:if>>
												<label for="form_control_1" id="querytextboxid">Query</label>
												<!--  <span class="help-block">Data Set Name </span> -->
											</div>
											<br /> <span class="required"></span>
										</div>
									</div>
									<div class="row" >
										<div class="col-md-6 col-capture hidden" id="historicDateTableDiv">
											<div class="form-group form-md-line-input">
												<input type="text" class="form-control catch-error"
													id="historicDateTableId" name="historicDateTable" value="${listDataAccess.historicDateTable}"> 
												<label for="form_control_1" id="historicDateTableId">Historic DateField Table Name</label>
												<!--  <span class="help-block">Data Set Name </span> -->
											</div>
											<br /> <span class="required"></span>
										</div>
									</div>

									<div class="row">
										<div class="col-md-6 col-capture">
											<div class="form-group form-md-checkboxes">
												<div class="md-checkbox-list">
													<div class="md-checkbox">
														<input type="checkbox" class="md-check"
															id="incrementalsourceid" name="incrementalsourceid"
															value="Y"
															<c:if test = "${listDataAccess.incrementalType eq 'Y'}"> checked="checked"    </c:if>>
														<label for="incrementalsourceid"><span></span> <span
															class="check"></span> <span class="box"></span>Incremental
															Matching Source</label>
													</div>
												</div>
											</div>
										</div>
										<div class="hidden" id="incrementaltype">
											<div class="col-md-6 col-capture" id="host-id-container">
												<div class="form-group form-md-line-input">
													<input type="text" class="form-control catch-error"
														id="dateformatid" name="dateformatid"
														value="${listDataAccess.dateFormat }"> <label
														for="dateformatid">Date Format</label>
													<!--  <span class="help-block">Data Set Name </span> -->
												</div>
												<br /> <span class="required"></span>
											</div>


											<div class="col-md-6 col-capture" id="host-id-container">
												<div class="form-group form-md-line-input">
													<input type="number" class="form-control catch-error"
														id="slicestartid" name="slicestartid"
														value="${listDataAccess.sliceStart }" max="-1"> <label
														for="slicestartid">Slice Start</label>
													<!--  <span class="help-block">Data Set Name </span> -->
												</div>
												<br /> <span class="required"></span>
											</div>
											<div class="col-md-6 col-capture" id="host-id-container">
												<div class="form-group form-md-line-input">
													<input type="number" class="form-control catch-error"
														id="sliceendid" name="sliceendid"
														value="${listDataAccess.sliceEnd }" max="-1"> <label
														for="sliceendid">Slice End</label>
													<!--  <span class="help-block">Data Set Name </span> -->
												</div>
												<br /> <span class="required"></span>
											</div>
										</div>
									</div>
								</div>
							</div>


							<div class="form-body hidden" id="s3_form_id">
								<!-- <div class="form-body"  id="hdfs_form_id"> -->
								<div class="row">
									<div class="col-md-6 col-capture" id="host-id-container">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control catch-error"
												id="hostName" name="hostName"
												value="${listDataAccess.hostName}"> <label
												for="form_control_1" id="host-id1">Host URI*</label>
											<!--  <span class="help-block">Data Set Name </span> -->
										</div>
										<br /> <span class="required"></span>
									</div>
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control catch-error"
												id="schemaName" name="schemaName"
												value="${listDataAccess.folderName}"> <label
												for="form_control_1" id="folder-id1">Folder *</label>
											<!--  <span class="help-block">Data Set Name </span> -->
										</div>
										<br /> <span class="required"></span>
									</div>
								</div>
								<div class="row">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="userName"
												name="userName" value="${listDataAccess.userName}">
											<label for="form_control" id="user-id">User login*</label>
											<!-- <span class="help-block">Short text about Data Set</span> -->
										</div>
									</div>
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="password" class="form-control catch-error"
												id="pwd" name="pwd" value="${listDataAccess.pwd}"> <label
												for="form_control_1" id="user-pwd">Password *</label>
											<!--  <span class="help-block">Data Set Name </span> -->
										</div>
										<br /> <span class="required"></span>
									</div>
								</div>
								
								<div class="form-body hidden" id="filesytem_query_id">
									 <div class="row">
									 	<div class="col-md-6 col-capture" id="fileQueryid1">
											<div class="form-group form-md-checkboxes">
												<div class="md-checkbox-list">
													<div class="md-checkbox">
														<input type="checkbox" disabled="disabled" class="form-control catch-error md-check"
															id="filequerycheckboxid" name="filequerycheckboxid" value="Y"
														<c:if test = "${listDataAccess.query eq 'Y'}"> checked= "checked"</c:if>>
														<label for="filequerycheckboxid"><span></span> <span
															class="check"></span> <span class="box"></span>Query?</label>
													</div>
												</div>
											</div>
										</div>
									 </div>
									 <div class="row hidden" id="filequerytextboxiddiv">
										 <div class="col-md-6 col-capture ">
											<div class="form-group form-md-line-input">
												<input type="text" class="form-control catch-error"
													id="filequerytextboxid" name="filequerytextboxid" 													
												<c:if test = "${listDataAccess.query eq 'Y'}"> 	value="${listDataAccess.queryString }" </c:if>>
											 <label for="form_control_1" id="filequerytextboxid">Query</label>
											</div>
											<br /> <span class="required"></span>
										</div>
									 </div>
								</div>
					
								<div id="hide-table" style="display: none;">
									<div class="row">
										<div class="col-md-6 col-capture" id="vschema_table">
											<div class="form-group form-md-line-input">
												<input type="text" class="form-control" id="folderName"
													name="folderName"> <label for="form_control"
													id="table-name">Table Name *</label>
												<!-- <span class="help-block">Short text about Data Set</span> -->
											</div>
											<br />
										</div>
										<div id="hide-port" style="display: none">
											<div class="col-md-6 col-capture">
												<div class="form-group form-md-line-input">
													<input type="text" class="form-control" id="portName"
														name="portName"> <label for="form_control">Port
														*</label>
													<!-- <span class="help-block">Short text about Data Set</span> -->
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>


							<div class="form-body" style="display: none" id="mqsql_form_id">
								<div class="row">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control catch-error"
												id="hostid" name="host"> <label for="form_control_1">Host
												Name *</label>
											<!--  <span class="help-block">Data Set Name </span> -->
										</div>
										<br /> <span class="required"></span>
									</div>
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="portid"
												name="port"> <label for="form_control">Port
												*</label>
											<!-- <span class="help-block">Short text about Data Set</span> -->
										</div>
									</div>
								</div>

								<div class="row">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="userid"
												name="username"> <label for="form_control">Username
												*</label>
											<!-- <span class="help-block">Short text about Data Set</span> -->
										</div>
									</div>

									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="password" class="form-control" id="pwdid"
												name="password"> <label for="form_control">Password
												*</label>
											<!-- <span class="help-block">Short text about Data Set</span> -->
										</div>
									</div>
								</div>

								<div class="row">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="dbid"
												name="dbname"> <label for="form_control">Database
												Name *</label>
											<!-- <span class="help-block">Short text about Data Set</span> -->
										</div>
										<span class="required"></span>

										<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="tableid"
												name="tablename"> <label for="form_control">Table
												Name*</label>
											<!-- <span class="help-block">Short text about Data Set</span> -->
										</div>
									</div>

								</div>
								<br /> <br />
								<div class="row">
									<div class="note note-danger hidden"></div>
								</div>
							</div>
							 <div class="form-actions noborder align-center">
								<button type="submit" id="datasetupid" class="btn blue">Submit</button>
							</div> 
							<!-- </form> -->
							<input type="hidden" name="idData"
								value="${ listDataSource.idData}"> <input type="hidden"
								name="dataLocation" value="${ listDataSource.dataLocation}">
							<input type="hidden" name="idDataSchema"
								value="${ listDataSchema.idDataSchema}">
							<input type="hidden" name="querycheckbox"
								value="${ listDataAccess.query}">
							<input type="hidden" name="filequerycheckbox"
								value="${ listDataAccess.query}">
						   </form>  
					</div>
				</div>
				 <input type="hidden" value="${listDataSource.dataLocation}"
					id="location"> <input type="hidden"
					value="${listDataAccess.query}" id="queryidHidden"> <input
					type="hidden" value="${listDataAccess.incrementalType}"
					id="queryidIncrementalMatching"> <input type="hidden"
					value="${listDataSource.dataSource}" id="sourcetypeDataSource"> 
				<!-- END SAMPLE FORM PORTLET-->
			
			</div>
		</div>
	
	</div>
</div>
<!-- END QUICK SIDEBAR -->
<!-- END CONTAINER -->
<jsp:include page="footer.jsp" />
<head>
<script type="text/javascript">
	$(document)
			.ready(
					function() {
						console.log("ready!");
						var location = $("#location").val();
						$("#locationid").val(location);
						$("#selectedDomainId").val(${listDataSource.domain});
						var selectedVal = location;
						console.log("selectedVal:"+selectedVal);
						
						if (selectedVal == 'Hive' || selectedVal == 'Oracle'
								|| selectedVal == 'MSSQL'
								|| selectedVal == 'MSSQLActiveDirectory'
								|| selectedVal == 'Vertica'
								|| selectedVal == 'Cassandra'
								|| selectedVal == 'Amazon Redshift'
								|| selectedVal == 'Hive Kerberos'
								|| selectedVal == 'MapR Hive'
								|| selectedVal == 'Oracle RAC'
								|| selectedVal == 'Postgres'
								|| selectedVal == 'Teradata'
								|| selectedVal == 'SnowFlake'
								|| selectedVal == 'FileSystem Batch'
								|| selectedVal == 'S3 Batch'
								|| selectedVal == 'S3 IAMRole Batch'
								|| selectedVal == 'BigQuery'
								|| selectedVal == 'AzureSynapseMSSQL'
								|| selectedVal == 'AzureDataLakeStorageGen1'
								|| selectedVal == 'MYSQL'
								|| selectedVal == 'AzureDataLakeStorageGen2Batch'
						) {

							$('#maprdivid').addClass('hidden');
							$('#s4_form_id').removeClass('hidden').show();
							$('#uploaddivid').addClass('hidden');
							$('#s3_form_id').addClass('hidden');
							$('#checkid').addClass('hidden');
							$('#ignoreRowsId').addClass('hidden');
							$('#domainDivId').removeClass('hidden');
							$('#rollingHeaderCheckid').addClass('hidden');
							$('#sourcedivid').addClass('hidden');
							$('#filesytem_query_id').addClass('hidden');
						} else if (selectedVal == 'FILESYSTEM') {
							$('#maprdivid').addClass('hidden');
							//$('#host-id').html('Host Name*');
							$('#fileType').html('Data Source File*');
							$('#checkid').removeClass('hidden');
							$('#ignoreRowsId').removeClass('hidden');
							$('#domainDivId').removeClass('hidden');
							$('#rollingHeaderCheckid').removeClass('hidden').show();
							$('#sourcedivid').removeClass('hidden').show();
							//$('#sourcedivid').addClass('hidden');
							$('#s4_form_id').addClass('hidden');
							$('#uploaddivid').removeClass('hidden');
							$('#s3_form_id').removeClass('hidden');
							$('#filesytem_query_id').removeClass('hidden').show();
							// $('#host-id-container').removeClass('hidden').show();
						} else if (selectedVal == 'HDFS'|| selectedVal == 'MapR FS') {
							$('#maprdivid').addClass('hidden');
							//$('#host-id').html('Host Name*');
							$('#fileType').html('Data Source File*');
							$('#checkid').removeClass('hidden');
							$('#ignoreRowsId').removeClass('hidden');
							$('#domainDivId').removeClass('hidden');
							$('#rollingHeaderCheckid').removeClass('hidden').show();
							$('#sourcedivid').removeClass('hidden').show();
							//$('#sourcedivid').addClass('hidden');
							$('#s4_form_id').addClass('hidden');
							$('#uploaddivid').removeClass('hidden');
							$('#s3_form_id').removeClass('hidden');
							$('#filesytem_query_id').addClass('hidden');
							// $('#host-id-container').removeClass('hidden').show();
						} else if (selectedVal == 'MapR DB') {
							//$('#host-id').html('Host Name*');
							$('#maprdivid').removeClass('hidden');
							$('#fileType').html('Data Source File*');
							$('#checkid').removeClass('hidden');
							$('#ignoreRowsId').removeClass('hidden');
							$('#domainDivId').removeClass('hidden');
							$('#rollingHeaderCheckid').addClass('hidden');
							$('#sourcedivid').removeClass('hidden').show();
							//$('#sourcedivid').addClass('hidden');
							$('#s4_form_id').addClass('hidden');
							$('#uploaddivid').removeClass('hidden');
							$('#s3_form_id').removeClass('hidden');
							// $('#host-id-container').removeClass('hidden').show();
							$('#host-id1').html('Host URI*');
						     $('#folder-id1').html('Folder*');
						     $('#user-id').html('User Login*');
						     $('#user-pwd').html('Password*');
							$('#filesytem_query_id').addClass('hidden');
						}else if (selectedVal == 'File Management') {
							$('#maprdivid').addClass('hidden');
							//$('#host-id').html('Host URI or Host IP*');
							$('#fileType').html('File Management CSV*');
							$('#uploaddivid').removeClass('hidden');
							$('#s3_form_id').removeClass('hidden');
							//hide remaining
							$('#s4_form_id').addClass('hidden');
							$('#checkid').addClass('hidden');
							$('#ignoreRowsId').addClass('hidden');
							$('#domainDivId').removeClass('hidden');
							$('#rollingHeaderCheckid').addClass('hidden');
							$('#sourcedivid').addClass('hidden');
							$('#filesytem_query_id').addClass('hidden');
						}else if(selectedVal == 'S3'){
							$('#maprdivid').addClass('hidden');
							 $('#host-id1').html('Bucket Name*');
						     $('#folder-id1').html('Folder / File Name*');
						     $('#user-id').html('Access Key*');
						     $('#user-pwd').html('Secret Key*');
						     
						     $('#fileType').html('Data Source File*');
								$('#checkid').removeClass('hidden');
								$('#rollingHeaderCheckid').addClass('hidden');
								$('#sourcedivid').removeClass('hidden').show();
								//$('#sourcedivid').addClass('hidden');
								$('#s4_form_id').addClass('hidden');
								$('#uploaddivid').removeClass('hidden');
								$('#s3_form_id').removeClass('hidden');
								$('#filesytem_query_id').addClass('hidden');
								$('#ignoreRowsId').removeClass('hidden');
								$('#domainDivId').removeClass('hidden');
						}else if(selectedVal == 'AzureDataLakeStorageGen2'){
							$('#maprdivid').addClass('hidden');
							$('#host-id1').html('Container Name*');
						    $('#folder-id1').html('File Path*');
						    $('#user-id').html('Account Name*');
						    $('#user-pwd').html('Account Key*');
						    $('#fileType').html('Data Source File*');
							$('#checkid').removeClass('hidden');
							$('#rollingHeaderCheckid').addClass('hidden');
							$('#sourcedivid').removeClass('hidden').show();
							$('#s4_form_id').addClass('hidden');
							$('#uploaddivid').addClass('hidden');
							$('#s3_form_id').removeClass('hidden');
							$('#filesytem_query_id').addClass('hidden');
							$('#ignoreRowsId').removeClass('hidden');
							$('#domainDivId').removeClass('hidden');
							$('#checkid').addClass('hidden');
						}else if(selectedVal == 'AzureDataLakeStorageGen2Batch'){
                            $('#maprdivid').addClass('hidden');
                            $('#host-id1').html('Container Name*');
                            $('#folder-id1').html('File Path*');
                            $('#user-id').html('Account Name*');
                            $('#user-pwd').html('Account Key*');
                            $('#fileType').html('Data Source File*');
                            $('#checkid').removeClass('hidden');
                            $('#rollingHeaderCheckid').addClass('hidden');
                            $('#sourcedivid').removeClass('hidden').show();
                            $('#s4_form_id').addClass('hidden');
                            $('#uploaddivid').addClass('hidden');
                            $('#s3_form_id').removeClass('hidden');
                            $('#filesytem_query_id').addClass('hidden');
                            $('#ignoreRowsId').removeClass('hidden');
                            $('#domainDivId').removeClass('hidden');
                            $('#checkid').addClass('hidden');
                        }

						var rollingHeaderVal = $("#rollingHeaderId").val();
						console.log("rollingHeaderVal:"+rollingHeaderVal);
						var rhvalue = 0;
						
						if (rollingHeaderVal == 'Y') {
							$('#rollingColumnId').removeClass('hidden');
							rhvalue = 1;
						} else {
							$('#rollingColumnId').addClass('hidden');
						}
						$('#rollingHeaderId').prop('checked',(rhvalue == 1));
						
						var queryidHidden = $("#queryidHidden").val();
						if (queryidHidden == 'Y') {
							if(selectedVal == 'FILESYSTEM'){
								$('#filequerytextboxiddiv').removeClass('hidden').show();
								$('#whereconditionid').addClass('hidden');
								$('#querycheckboxid').val('');
								$('#querytextboxid').val('');
							} else {
								$('#filesytem_query_id').addClass('hidden');
								$('#querytextboxiddiv').removeClass('hidden');
								$('#historicDateTableDiv').removeClass('hidden');
								$('#whereconditionid').addClass('hidden');
							}
						} else {
							$('#querytextboxiddiv').addClass('hidden');
							$('#whereconditionid').removeClass('hidden');
							$('#filesytem_query_id').addClass('hidden');
						}

						var queryidIncrementalMatching = $(
								"#queryidIncrementalMatching").val();
						if (queryidIncrementalMatching == 'Y') {
							$('#incrementaltype').removeClass('hidden');
						} else {
							$('#incrementaltype').addClass('hidden');
						}

						var sourcetypeDataSource = $("#sourcetypeDataSource")
								.val();
						$("#sourceid").val(sourcetypeDataSource);

					});

	$('#querycheckboxid').click(function() {
		if ($(this).prop("checked") == true) {
			$('#querytextboxiddiv').removeClass('hidden');
			$('#whereconditionid').addClass('hidden');

			$('#whereId').val('');
		} else if ($(this).prop("checked") == false) {
			//$('#tableNameid').prop('readonly', false);
			//console.log("else");
			$('#tableNameid').val('');
			//$('#querytextboxid').val('');

			$('#querytextboxiddiv').addClass('hidden');
			$('#whereconditionid').removeClass('hidden');
		}
	});
	$('#incrementalsourceid').click(function() {
		if ($(this).prop("checked") == true) {
			$('#incrementaltype').removeClass('hidden');
		} else if ($(this).prop("checked") == false) {
			$('#incrementaltype').addClass('hidden');
		}

	});

	$('#datasetupid')
			.click(
					function() {
						var no_error = 1;
						$('.input_error').remove();
						
						if(!checkInputText()){ 
							no_error = 0;
							alert('Vulnerability in submitted data, not allowed to submit!');
						}
						
						if ($("#datasetid").val().length == 0) {
							console.log('datasetid');
							$(
									'<span class="input_error" style="font-size:12px;color:red">Please Enter Data Template Name</span>')
									.insertAfter($('#datasetid'));
							no_error = 0;
						}
						if (($('#locationid').val() == 'MSSQLActiveDirectory')
								|| ($('#locationid').val() == 'Vertica')
								|| ($('#locationid').val() == 'Oracle')
								|| ($('#locationid').val() == 'MSSQL')
								|| ($('#locationid').val() == 'Hive')
								|| ($('#locationid').val() == 0)) {
							console.log('locationid');

							if ($("#schemaId").val().length == 0) {
								console.log('schemaId');
								$(
										'<span class="input_error" style="font-size:12px;color:red">Please Select One</span>')
										.insertAfter($('#schemaId'));
								no_error = 0;
							}
							if ($("#tableNameid").val().length == 0) {
								console.log('tableNameid');
								$(
										'<span class="input_error" style="font-size:12px;color:red">Please Enter Table Name</span>')
										.insertAfter($('#tableNameid'));
								no_error = 0;
							}
							/* if ($("#querycheckboxid").prop("checked") == true) {
								alert($("#querytextboxid").val().length)
								if ($("#querytextboxid").val().length == 0) {
									$(
											'<span class="input_error" style="font-size:12px;color:red">Please Enter query</span>')
											.insertAfter($('#querytextboxid'));
									no_error = 0;
								}
							} */
							if ($("#incrementalsourceid").prop("checked") == true) {
								if ($("#dateformatid").val().length == 0) {
									$(
											'<span class="input_error" style="font-size:12px;color:red">Please Enter DateFormat</span>')
											.insertAfter($('#dateformatid'));
									no_error = 0;
								}
								if ($("#slicestartid").val().length == 0) {
									$(
											'<span class="input_error" style="font-size:12px;color:red">Please Enter Slice Start value</span>')
											.insertAfter($('#slicestartid'));
									no_error = 0;
								}
								if ($("#sliceendid").val().length == 0) {
									$(
											'<span class="input_error" style="font-size:12px;color:red">Please Enter Slice End value</span>')
											.insertAfter($('#sliceendid'));
									no_error = 0;
								}
								var slicestartid = $("#slicestartid").val();
								var sliceendid = $("#sliceendid").val();

								if (slicestartid > 0) {
									$(
											'<span class="input_error" style="font-size:12px;color:red">Please Enter Negative value</span>')
											.insertAfter($('#slicestartid'));
									no_error = 0;
								}
								if (sliceendid > 0) {
									$(
											'<span class="input_error" style="font-size:12px;color:red">Please Enter Negative value</span>')
											.insertAfter($('#sliceendid'));
									no_error = 0;
								}
								console.log(slicestartid + "" + sliceendid);
								if (parseInt(slicestartid) >= parseInt(sliceendid)) {
									toastr
											.info('The Slice Start cannot be Greater than Slice End value');
									no_error = 0;
								}
							}

						}

						if (($('#locationid').val() == 'File System')
								|| ($('#locationid').val() == 'HDFS')
								|| ($('#locationid').val() == 'File Management')) {

							if ($("#dataupload_id").val().length == 0) {
								$(
										'<span class="input_error" style="font-size:12px;color:red">Please Upload a File</span>')
										.insertAfter($('#dataupload_id'));
								no_error = 0;
							}
							/* if ($("#datasetid").val().length == 0) {
								$(
										'<span class="input_error" style="font-size:12px;color:red">Please enter Dataset Name</span>')
										.insertAfter($('#datasetid'));
								no_error = 0;
							} */
							if ($("#hostName").val().length == 0) {
								console.log('hostName');
								$(
										'<span class="input_error" style="font-size:12px;color:red">Please enter Host Name</span>')
										.insertAfter($('#hostName'));
								no_error = 0;
							}
							if ($("#schemaName").val().length == 0) {
								console.log('schemaName');
								$(
										'<span class="input_error" style="font-size:12px;color:red">Please enter schemaName</span>')
										.insertAfter($('#schemaName'));
								no_error = 0;
							}
							if ($("#userName").val().length == 0) {
								console.log('userName');
								$(
										'<span class="input_error" style="font-size:12px;color:red">Please enter Username</span>')
										.insertAfter($('#userName'));
								no_error = 0;
							}
							if ($("#pwd").val().length == 0) {
								console.log('pwd');
								$(
										'<span class="input_error" style="font-size:12px;color:red">Please enter Password</span>')
										.insertAfter($('#pwd'));
								no_error = 0;
							}
						}
						
						if (($('#locationid').val() == 'FileSystem Batch') || ($('#locationid').val() == 'S3 Batch') || ($('#locationid').val() == 'S3 IAMRole Batch')) {
							
							if ($("#schemaId").val().length == 0) {
								console.log('schemaId');
								$(
										'<span class="input_error" style="font-size:12px;color:red">Please Select One</span>')
										.insertAfter($('#schemaId'));
								no_error = 0;
							}
							
							if ($("#selectedTables").val().length == 0) {
								$(
										'<span class="input_error" style="font-size:12px;color:red">Please Select Tables</span>')
										.insertAfter($('#selectedTables'));
								no_error = 0;
							}
						
							console.log("query enabled:"+$("#querycheckboxid").prop("checked"))
							console.log("lstTable :selected"+$('#lstTable :selected').length);
							
							if($("#querycheckboxid").prop("checked") == true){
								if ($('#lstTable :selected').length > 2) {
									$('<span class="input_error" style="font-size:12px;color:red">Please Select Max two tables when query is enabled</span>')
											.insertAfter($('#selectedTables'));
									no_error = 0;
								}
								
							} 
						}
						var amit1 = $('#amit').html();
						// alert("outside"+amit1)
						console.log(amit1);
						if (amit1 == "") {

						} else {
							console.log("inside if");
							//toaster.info("Schema created successfully");
							//$('<span class="input_error" style="font-size:12px;color:red">already exist Database Name</span>').insertAfter($('#descriptionid'));
							no_error = 0;
						}
						if (no_error == 0) {
							console.log('error');
							return false;
						} else {
							$('.btn').addClass('hidden');

						}
					});
	$('#locationid').change(
			function() {
				$this = $(this);
				var selectedVal = $this.val();

				if (selectedVal == 'Hive' || selectedVal == 'Oracle'
					|| selectedVal == 'MSSQL'
						|| selectedVal == 'MSSQLActiveDirectory'
						|| selectedVal == 'Vertica'
						|| selectedVal == 'Cassandra'
						|| selectedVal == 'Amazon Redshift'
						|| selectedVal == 'Hive Kerberos'
						|| selectedVal == 'MapR Hive'
						|| selectedVal == 'Oracle RAC'
						|| selectedVal == 'Postgres'
						|| selectedVal == 'Teradata'
						|| selectedVal == 'SnowFlake'
						|| selectedVal == 'FileSystem Batch'
						|| selectedVal == 'S3 Batch'
						|| selectedVal == 'S3 IAMRole Batch'
						|| selectedVal == 'BigQuery'
						|| selectedVal == 'AzureSynapseMSSQL'
						|| selectedVal == 'AzureDataLakeStorageGen1'
						|| selectedVal == 'AzureDataLakeStorageGen2Batch'
						) {
					$('#s4_form_id').removeClass('hidden').show();
					$('#uploaddivid').addClass('hidden');
					$('#s3_form_id').addClass('hidden');
					$('#checkid').addClass('hidden');
					$('#sourcedivid').addClass('hidden');
				} else if (selectedVal == 'File System'
						|| selectedVal == 'HDFS') {
					//$('#host-id').html('Host Name*');
					$('#fileType').html('Data Source File *');
					$('#checkid').removeClass('hidden');
					$('#rollingHeadercheckid').removeClass('hidden');
					$('#sourcedivid').removeClass('hidden').show();
					//$('#sourcedivid').addClass('hidden');
					$('#s4_form_id').addClass('hidden');
					$('#uploaddivid').removeClass('hidden');
					$('#s3_form_id').removeClass('hidden');
					// $('#host-id-container').removeClass('hidden').show();
				} else if (selectedVal == 'File Management') {
					//$('#host-id').html('Host URI or Host IP*');
					$('#fileType').html('File Management CSV*');
					$('#uploaddivid').removeClass('hidden');
					$('#s3_form_id').removeClass('hidden');
					//hide remaining
					$('#s4_form_id').addClass('hidden');
					$('#checkid').addClass('hidden');
					$('#rollingHeadercheckid').addClass('hidden');
					$('#sourcedivid').addClass('hidden');
				}
			});
	
	$('#headerId').change(
			function() {
				console.log("headerId changed")
				console.log("isHeaderPresent:"+($('#headerId').is(':checked')))
				if(!$('#rollingHeaderId').is(':checked')){
					if($('#headerId').is(':checked')){
						$('#uploaddivid').addClass('hidden');
					} else {
						$('#uploaddivid').removeClass('hidden');
						$('#fileType').html('Data Source File*');
					}	
					
				}	
	});
	
	$('#rollingHeaderId').change(
			function() {
				console.log("rollingHeaderId changed")
				console.log("Is Rolling Header Present:"+($('#rollingHeaderId').is(':checked')))
				
				if($('#rollingHeaderId').is(':checked')){
					$('#uploaddivid').removeClass('hidden');
					$('#fileType').html('Rolling Header File*');
					$('#rollingColumnId').removeClass('hidden');
				} else {
					$('#uploaddivid').addClass('hidden');
					$('#rollingColumnId').addClass('hidden');
				}	
	});
	
	$('#sourceid').change(
			function() {
				console.log("sourceid changed")
				console.log("fileDataFormat:"+($('#sourceid').val()))
				if($('#sourceid').val() == 'FLAT'){
					$('#uploaddivid').removeClass('hidden');
					$('#fileType').html('Data Source File*');
					$('#headerId').prop("checked", false);
				} else {
					$('#uploaddivid').addClass('hidden');
					$('#headerId').prop("checked", true);
				}	
	});
</script>
</head>