<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<link rel="stylesheet"  href="./assets/css/custom-style.css" type="text/css"  />
<style>
	#div_global_rules, #div_global_thresholds, #div_auto_rules {
		margin-top: 0px;
		margin-right: auto;
		margin-left: auto;
		padding: 10px;
		min-height: 200px;
		border-top: 2px solid var(--label-tabs-color);
	}
	
	.selectall_checkbox1 {
		width: 15px;
		height: 15px;
		box-shadow: 0 0 0 0px grey;
		cursor: pointer;	
	}

	#table_global_rules input[type="checkbox"], #table_global_thresholds input[type="checkbox"], #table_ref_rules input[type="checkbox"] {
		width: 15px;
		height: 15px;
		box-shadow: 0 0 0 0px grey;
		cursor: pointer;
	}

	#table_global_rules th, #table_global_thresholds th, #table_ref_rules th {
		text-align: left;
		margin: 0px;
	}
	 table.dataTable {
        overflow:scroll;
        }
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
		var v = document.getElementById("datasetid").value;
		var val = "Name must begin with a letter and cannot contain spaces,special characters"
		var expr = /^[a-zA-Z0-9_]*$/;
	    if (!expr.test(v)) {
	     	document.getElementById('amit').innerHTML = val;
	        return;
	    }
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
			//alert("Unable to connect to server");
		}
	}

	function getInfo() {
		if (request.readyState == 4) {
			var val = request.responseText;
			//alert("getInfo");
			//alert(val);
			//var sal="Database already exists";
			console.log(val);
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
						<%--  <% 
                        String message=(String)request.getAttribute("message");
                        System.out.print("message="+message);
                        if(message!=null){
                        %>
                   <div class="alert alert-success" style="width:50%; float:right">
   <button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
 <strong>${message}</strong>
</div>
<%} %> --%>
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold "> Create Data Template
							</span>
						</div>
						<div id="loading-progress" class="ajax-loader hidden">
							<h4>
								<b>Table List Loading in Progress..</b>
							</h4>
							<img
								src="${pageContext.request.contextPath}/assets/global/img/loading-circle.gif"
								class="img-responsive" />
						</div>
						<div id="loading-data-progress" class="ajax-loader hidden">
							<h4>
								<b>Table Data Loading in Progress..</b>
							</h4>
							<img
								src="${pageContext.request.contextPath}/assets/global/img/loading-circle.gif"
								class="img-responsive" />
						</div>

					</div>
					<div class="portlet-body form">
						<input type="hidden" id="tourl" value="createDataTemplate" /> <input
							type="hidden" id="currSelectedTable" value="currSelectedTable" /> <input
							type="hidden" id="queryInputTaken" value="queryInputTaken" />

						<form  action="createDataTemplate" enctype="multipart/form-data"
							method="POST" accept-charset="utf-8">
							<div class="form-body">
								<textarea name='SelectedRulesThresholds' id='SelectedRulesThresholds' style='display: none'></textarea>
								<div class="row" id="template_data">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control catch-error"
												id="datasetid" name="dataset"
												placeholder="Enter the Data Template Name"
												onkeyup="sendInfo()"> <label for="form_control_1">Data
												Template Name *</label>
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
											<select class="form-control" id="locationid" name="location"
												placeholder="Choose Location">
												<option value="-1">Select...</option>
												<option value="File System">File System</option>
												<option value="File Management">File Management</option>
												<option value="HDFS">HDFS</option>
												<option value="MSSQL">MS SQL</option>
												<option value="MYSQL">MYSQL</option>												
												<option value="MSSQLActiveDirectory">MS SQL(Active Directory)</option>
												<option value="Vertica">Vertica</option>
												<option value="Oracle">Oracle (Standalone)</option>
												<option value="Hive">Hive</option>
												<option value="Kafka">Kafka</option>
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
												<option value="Hive knox">Hive (Knox)</option>

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
									
								<div class="col-md-6 col-capture" style="display: none;">
									<div class="form-group form-md-line-input">
										<!-- <select class="form-control" name="domainfunction"
											id="selectdomain_id">
											<option value="sel">choose Domain</option>
											<option value="Banking">Banking</option>
											<option value="Telecom">Telecom</option>
											<option value="Finance">Finance</option>
											<option value="Medical">Medical</option>
											<option value="Advertisement">Advertisement</option>
											<option value="Others">Others</option>
										</select> <label for="blendfunid">Domain</label> -->
										
										<select class="form-control" id="selectdomain_id" name="domainfunction">
										    <option value="-1">Select Domain</option>
											<c:forEach var="getDomainListobj" items="${listdomain}">
												 
												 <option value="${getDomainListobj.domainId}">${getDomainListobj.domainName}</option>
					
											</c:forEach>
										</select>
										<label for="blendfunid">Domain </label><br>
									</div>
									
								</div>
								</div>
								
								<div class="row hidden" id="sourcedivid">	
									<div class="col-md-6 col-capture " >
										<div class="form-group form-md-line-input">
											<!-- <input type="text" class="form-control" id="sourceid" name="source" placeholder="Enter Data Template"> -->
											<select class="form-control" id="sourceid" name="source"
												placeholder="Choose Location">
												<option value="PSV">PSV(Pipe Delimited)</option>
												<option value="CSV">CSV</option>
												<option value="TSV">TSV</option>
												<option value="ORC">ORC</option>
												<option value="Parquet">Parquet</option>
												<option value="JSON">JSON</option>
												<option value="FLAT">Flat File</option>
												<!-- New options needs to be added here -->
												<!-- This would be a sample json code which would uploaded to the application for schema -->
												<!-- <option value="xls">XLS</option> -->
											</select> <label for="form_control_1">Data Format</label>
											<!-- <span class="help-block">Short text about Data Set</span> -->
										</div>
									</div>
									<div class="col-md-6 col-capture">
											<div class="form-group form-md-line-input">
												<input type="text" class="form-control catch-error"
													id="rowsId" name="rowsId"> <label
													for="form_control_1" id="rows-id">Number of rows
													to be ignored </label>
												<!--  <span class="help-block">Data Set Name </span> -->
											</div>
											<br /> <span class="required"></span>
										</div>
								</div>
								
								<div class="form-body hidden" id="checkid">
									<!-- <div class="form-body"  id="hdfs_form_id"> -->
									<div class="row">
										<div class="col-md-6 col-capture" id="host-id-container">
											<div class="form-group form-md-line-input">

												<input type="checkbox" checked class="md-check"
													id="headerId" name="headerId" value="Y"> <label
													for="form_control_1" id="host-id">File has Header?*</label>
												<!--  <span class="help-block">Data Set Name </span> -->
											</div>
											<br /> <span class="required"></span>
										</div>
										
										<div class="col-md-6 col-capture" id="rollingHeaderCheckId">
											<div class="form-group form-md-line-input">

												<input type="checkbox" class="md-check"
													id="rollingHeaderId" name="rollingHeaderPresent" value="Y"> <label
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
												id="rollingColumn_Id" name="rollingColumn"> <label
												for="form_control_1" id="rollingColumn-id">Rolling Column Name</label>
											<!--  <span class="help-block">Data Set Name </span> -->
										</div>
										<br /> <span class="required"></span>
									</div>
								</div>
								
								<div class="row hidden" id="maprdivid">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<select class="form-control" id="mapRDBSourceFormat"
												name="mapRDBSourceFormat">
												<option value="JSON">JSON</option>
												<!-- <option value="Binary">Binary</option> -->
											</select> <label for="mapRDBSourceFormat">MapR DB Format</label>
										</div>
									</div>
								</div>
								
								<div class="form-body" style="display: none;" id="s4_form_id">
									<div class="row">
										<div class="col-md-6 col-capture" id="host-id-container">
											<div class="form-group form-md-line-input">
												<select class="form-control" id="schemaId" name="schemaId1">
												</select> <label for="form_control_1">Select Data Connection
													*</label>
											</div>
											<br /> <span class="required"></span>
										</div>
										
										<%
											Object firstTblElementObj = session.getAttribute("FirstEleFromTablesList");
											//String firstTblElement = firstTblElementObj.toString();
														
											System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%IN JSP =>"+firstTblElementObj);
											
										%>
									</div>
									<div class="row">
									 <input type="hidden" id="defaultfirstTblElement" value="" />
										<div class="col-md-6 col-capture hidden" id="s4_tablename_id">
											<div class="form-group form-md-line-input">
												<select class="form-control multiselect text-left"
													style="text-align: left;" id="lstTable" multiple="multiple">
												</select> <input type="hidden" id="selectedTables"
													name="selectedTables" value="" /> <label
													for="form_control_1" id="host-id">Table Name*</label>
												<!--  <span class="help-block">Data Set Name </span> -->
											</div>
											<br /> <span class="required"></span>
										</div>
										<div class="col-md-6 col-capture hidden"
											id="s4_tablename_id_2">
											<div class="form-group form-md-line-input">
												<input type="text" class="form-control catch-error"
													id="tableNameid" name="tableNameid"> <label
													for="form_control_1" id="folder-id">Table Name*</label>
												<!--  <span class="help-block">Data Set Name </span> -->
											</div>
											<br /> <span class="required"></span>
										</div>
									</div>
									<div class="row">
										<div class="col-md-12 col-capture" id="conditionid">
											<div class="col-md-6 col-capture" id="whereconditionid" style="padding-left: 0;">
												<div class="form-group form-md-line-input">
													<input type="text" class="form-control catch-error"
														id="whereId" name="whereId"> <label for="whereId"
														id="whereId">Where Condition</label>
													<!--  <span class="help-block">Data Set Name </span> -->
												</div>
												<br /> <span class="required"></span>
											</div>
											<div class="col-md-6 col-capture" id="queryid1">
												<div class="form-group form-md-checkboxes">
													<div class="md-checkbox-list">
														<div class="md-checkbox">
															<input type="checkbox" class="md-check"
																id="querycheckboxid" name="querycheckboxid" value="Y">
															<label for="querycheckboxid"><span></span> <span
																class="check"></span> <span class="box"></span>Query?</label>
														</div>
													</div>
												</div>
											</div>
										</div>
										<div class="row">
											<div class="col-md-12 col-capture hidden"
												id="querytextboxiddiv">
												<div class="form-group form-md-line-input">
													<input type="text" class="form-control catch-error"
														id="querytextboxid" name="querytextboxid"> <label
														for="form_control_1" id="querytextboxid">Query</label>
													<!--  <span class="help-block">Data Set Name </span> -->
												</div>
												<br /> <span class="required"></span>
											</div>
										</div>
										<div class="row" >
											<div class="col-md-6 col-capture hidden" id="historicDateTableDiv">
												<div class="form-group form-md-line-input">
													<input type="text" class="form-control catch-error"
														id="historicDateTableId" name="historicDateTable"> <label
														for="form_control_1" id="historicDateTableId">Historic DateField Table Name</label>
												</div>
												<br /> <span class="required"></span>
											</div>
										</div>
									</div>
								</div>
								<div class="row" id="incremental_div">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-checkboxes">
											<div class="md-checkbox-list">
												<div class="md-checkbox">
													<input type="checkbox" class="md-check"
														id="incrementalsourceid" name="incrementalsourceid"
														value="Y"> <label for="incrementalsourceid"><span></span>
														<span class="check"></span> <span class="box"></span>Incremental
														Matching Source</label>
												</div>
											</div>
										</div>
									</div>
									<div class="hidden" id="incrementaltype">
										<div class="col-md-6 col-capture" id="host-id-container">
											<div class="form-group form-md-line-input">
												<input type="text" class="form-control catch-error"
													id="dateformatid" name="dateformatid"> <label
													for="dateformatid">Date Format</label>
												<!--  <span class="help-block">Data Set Name </span> -->
											</div>
											<br /> <span class="required"></span>
										</div>
										<div class="col-md-6 col-capture" id="host-id-container">
											<div class="form-group form-md-line-input">
												<input type="number" class="form-control catch-error"
													id="slicestartid" name="slicestartid" max="-1"> <label
													for="slicestartid">Slice Start</label>
												<!--  <span class="help-block">Data Set Name </span> -->
											</div>
											<br /> <span class="required"></span>
										</div>
										<div class="col-md-6 col-capture" id="host-id-container">
											<div class="form-group form-md-line-input">
												<input type="number" class="form-control catch-error"
													id="sliceendid" name="sliceendid" max="-1"> <label
													for="sliceendid" id="whereId">Slice End</label>
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
										id="hostName" name="hostName"> <label
										for="form_control_1" id="host-id1">Host URI*</label>
									<!--  <span class="help-block">Data Set Name </span> -->
								</div>
								<br /> <span class="required"></span>
							</div>
							<div class="col-md-6 col-capture">
								<div class="form-group form-md-line-input">
									<input type="text" class="form-control catch-error"
										id="schemaName" name="schemaName"> <label
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
										name="userName"> <label for="form_control"
										id="user-id">User login*</label>
									<!-- <span class="help-block">Short text about Data Set</span> -->
								</div>
							</div>
							<div class="col-md-6 col-capture">
								<div class="form-group form-md-line-input">
									<input type="password" class="form-control catch-error"
										id="pwd" name="pwd"> <label for="form_control_1"
										id="user-pwd">Password *</label>
									<!--  <span class="help-block">Data Set Name </span> -->
								</div>
								<br /> <span class="required"></span>
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
					<div class="form-body hidden" id="filesytem_query_id">
						 <div class="row">
						 	<div class="col-md-6 col-capture" id="fileQueryid1">
								<div class="form-group form-md-checkboxes">
									<div class="md-checkbox-list">
										<div class="md-checkbox">
											<input type="checkbox" class="md-check"
												id="filequerycheckboxid" name="filequerycheckboxid" value="Y">
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
										id="filequerytextboxid" name="filequerytextboxid"> <label
										for="form_control_1" id="filequerytextboxid">Query</label>
								</div>
								<br /> <span class="required"></span>
							</div>
						 </div>
					</div>
					<div class="form-body hidden" id="kafka_form_id">
						<!-- <div class="form-body"  id="hdfs_form_id"> -->
						<div class="row">
							<div class="col-md-6 col-capture" id="host-id-container">
								<div class="form-group form-md-line-input">
									<input type="text" class="form-control catch-error"
										id="src_brokerUri" name="src_brokerUri"> <label
										for="form_control_1" id="host-id1">Host URI*</label>
									<!--  <span class="help-block">Data Set Name </span> -->
								</div>
								<br /> <span class="required"></span>
							</div>
							<div class="col-md-6 col-capture">
								<div class="form-group form-md-line-input">
									<input type="text" class="form-control catch-error"
										id="src_topicName" name="src_topicName"> <label
										for="form_control_1" id="folder-id1">Folder *</label>
									<!--  <span class="help-block">Data Set Name </span> -->
								</div>
								<br /> <span class="required"></span>
							</div>
						</div>
						<div class="row">

							<div class="col-md-6 col-capture" id="host-id-container">

								<div class="form-group form-md-line-input">
									<input type="text" class="form-control catch-error"
										id="tar_brokerUri" name="tar_brokerUri"> <label
										for="form_control_1" id="host-id1">Broker URI*</label>
									<!--  <span class="help-block">Data Set Name </span> -->
								</div>
								<br /> <span class="required"></span>
							</div>
							<div class="col-md-6 col-capture">
								<div class="form-group form-md-line-input">
									<input type="text" class="form-control catch-error"
										id="tar_topicName" name="tar_topicName"> <label
										for="form_control_1" id="folder-id1">Topic Name *</label>
									<!--  <span class="help-block">Data Set Name </span> -->
								</div>
								<br /> <span class="required"></span>
							</div>
						</div>

						<div class="row" id="usercred_div">
							<div class="col-md-6 col-capture">
								<div class="form-group form-md-line-input">
									<input type="text" class="form-control" id="userName"
										name="userName"> <label for="form_control"
										id="user-id">User login*</label>
									<!-- <span class="help-block">Short text about Data Set</span> -->
								</div>
							</div>
							<div class="col-md-6 col-capture">
								<div class="form-group form-md-line-input">
									<input type="password" class="form-control catch-error"
										id="pwd" name="pwd"> <label for="form_control_1"
										id="user-pwd">Password *</label>
									<!--  <span class="help-block">Data Set Name </span> -->
								</div>
								<br /> <span class="required"></span>
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
						<div class="panel-heading">Source</div>
						<div class="row">
							<div class="col-md-6 col-capture">
								<div class="form-group form-md-line-input">
									<input type="text" class="form-control catch-error" id="hostid"
										name="host"> <label for="form_control_1">Host
										Name *</label>
									<!--  <span class="help-block">Data Set Name </span> -->
								</div>
								<br /> <span class="required"></span>
							</div>
							<div class="col-md-6 col-capture">
								<div class="form-group form-md-line-input">
									<input type="text" class="form-control" id="portid" name="port">
									<label for="form_control">Port *</label>
									<!-- <span class="help-block">Short text about Data Set</span> -->
								</div>
							</div>
						</div>

						<div class="panel-heading">Target</div>
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
									<input type="text" class="form-control" id="dbid" name="dbname">
									<label for="form_control">Database Name *</label>
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
						<input type="hidden" id="profilingId" name="profilingEnabled" />
						<input type="hidden" id="advancedRulesCheckId" name="advancedRulesEnabled" />
						<button type="submit" id="datasetupid1"
							onclick="return submitFormDataAndCreateTemplate('N','N')" class="btn blue">Create<br/></button>
						<button type="submit" id="datasetupid2"
							onclick="return submitFormDataAndCreateTemplate('Y','N')" class="btn blue">Create With Profiling <br/></button>
						<button type="submit" id="datasetupid3"
							onclick="return submitFormDataAndCreateTemplate('Y','Y')" class="btn blue">Create With Profiling & Rules</button>
					</div>
				</div>
			</div>
			<div class="cd-popup" id="popupOuterRule" role="alert" style="overflow-y:scroll;">
					<div id="popSampleRule" class="cd-popup-container" >
						<div id="popupRuleInner" style="overflow-y: scroll; max-height:85%;  margin-top: 50px; margin-bottom:50px;" ></div>
						<ul class="cd-buttons">
							<li><a onClick="validateHrefVulnerability(this);sendSelectedRules();"  href="javascript:void(0);"
								 id="popRuleSubmit">Submit</a></li>
							<!-- <li><a onClick="validateHrefVulnerability(this);sendSelectedRules();"  href="javascript:void(0);"  id="popRuleSubmit" ><strong>Submit</strong></a></li> -->
							<li><a onClick="validateHrefVulnerability(this);closeRulesPopup();"  href="javascript:void(0);"
								 id="ruleClose">Close</a></li>
						</ul>
					</div>
				</div>
			</div>
			<div class="cd-popup" id="popSample-rule" role="alert">
			<div class="cd-popup-container">
				<p>Should Global rules be configured for this table?</p>
				<ul class="cd-buttons">
					<li><a onClick="validateHrefVulnerability(this)"  id="popYesRule">Yes</a></li>
					<li><a onClick="validateHrefVulnerability(this)"  id="popNoRule">No</a></li>
				</ul>
			</div>
		</div>
			<div class="note note-info hidden">File uploaded Successfully</div>
			<!-- END SAMPLE FORM PORTLET-->
		</div>
	</div>
</div>

<!-- END QUICK SIDEBAR -->
<!-- END CONTAINER -->
<jsp:include page="footer.jsp" />
<head>
<script src="./assets/global/plugins/jquery-ui.min.js"></script>
<script
	src="./assets/global/plugins/bootstrap-multiselect.js"></script>
<link rel="stylesheet"
	href="./assets/global/plugins/bootstrap-multiselect.css">
	
<script type="text/javascript" src="./assets/global/plugins/jsapi.js"></script>
<!-- <script>
     $(document).ready(function() {
         $('#tokenForm1').ajaxForm({
        	headers: {'token':$("#token").val()}
        });
        });
</script> -->

<script type="text/javascript">
	$(document).ready(function() {
		//$('#lstTable').multiselect();
		$('#schemaId').multiselect({
			maxHeight : 200,
			buttonWidth : '100%',
			enableFiltering : true,
			nonSelectedText : 'Select Connection'
			 
		});
	});
	
	$('#lstTable').multiselect({
		maxHeight : 200,
		buttonWidth : '500px',
		includeSelectAllOption : true,
		enableFiltering : true,
		nonSelectedText : 'Select Table'
	});
</script>

<script type="text/javascript">

	var firstSelect = false;
	var datafirstSelect = false;
	var areTableDetailsDisplayed = false;
	$('#querycheckboxid').click(function() {
		
		if ($(this).prop("checked") == true) {
		
			$('#querytextboxiddiv').removeClass('hidden');
			$('#historicDateTableDiv').removeClass('hidden');
			
			if( $("#locationid").val() == "SnowFlake"){
				$('<span class="help-block">Table Name should be in this format DatabaseName.SchemaName.TableName</span> ').insertAfter($('#querytextboxid'));			 
			}
			
			$('#whereconditionid').addClass('hidden');
		} else if ($(this).prop("checked") == false) {
			$('#querytextboxiddiv').addClass('hidden');
			$('#historicDateTableDiv').addClass('hidden');
			$('#whereconditionid').removeClass('hidden');

		}
	});
	var selectedOption;
	var analysisData;
	$(document).on("click", "#sample_editable1 tr td", function() {
		$(".input-hide").addClass("hidden");
		$("label").removeClass("hidden");
		$(this).find(".input-hide").removeClass("hidden");
		$(this).find("label").addClass("hidden");
		var indexRow = $(this).closest("tr").data("attr");
		var indexCol = $(this).data("attr");
	});

	$('.input-hide').bind('focus', function(e) {
		$(this).data('done', false);
	});

	var isRuleCatalogDiscovery = ${isRuleCatalogDiscovery};

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
	
	$('#incrementalsourceid').click(function() {
		if ($(this).prop("checked") == true) {
			$('#incrementaltype').removeClass('hidden');
		} else if ($(this).prop("checked") == false) {
			$('#incrementaltype').addClass('hidden');
		}

	});
	
	function submitFormDataAndCreateTemplate(profilingEnabled,advancedRulesEnabled) {
		
		
		$('#datasetupid1').addClass('hidden');
		$('#datasetupid2').addClass('hidden');
		$('#datasetupid3').addClass('hidden');
		var no_error = 1;
		
		//by defaultselected first table name in case of query
		
		//selectedTables
		
		//$("#target option:first").attr('selected','selected');
	//	var defaultVal = $("#lstTable option:first").attr('selected','selected');
		
		//alert("default Val =>"+defaultVal);

		
	//	document.getElementById('defaultfirstTblElement').value = product(2, 3);
		
		var defaultSelectedTab = "<%=session.getAttribute("FirstEleFromTablesList") %>";
		//alert("=================selected table:" + $("#currSelectedTable").val());
		//alert("1st tablename =>"+defaultSelectedTab );
		
		if(!checkInputText()){ 
			no_error = 0;
			alert('Vulnerability in submitted data, not allowed to submit!');
		}
		
		$("#profilingId").val(profilingEnabled)
		$("#advancedRulesCheckId").val(advancedRulesEnabled)
		
		console.log("profilingEnabled:" + $("#profilingId").val());
		console.log("advancedRulesEnabled:" + $("#advancedRulesCheckId").val());
		
		var currentSelectedTable = $("#currSelectedTable").val();
		
		if (currentSelectedTable.length > 0) {
			//alert("In If.................");
			$("#queryInputTaken").val("yes");
			currentSelectedTable = defaultSelectedTab;
		}
		//alert("queryTextboxid =>"+$("#querytextboxid").val());
		//alert("queryInputTakn =>"+$("#queryInputTaken").val());
		
		if ($("#currSelectedTable").val().length == 0
				&& $("#querytextboxid").val().length > 0
				&& $("#queryInputTaken").val().length == 0) {
			event.preventDefault();
			var isQuery = "Y";
			var currentSelection = $("#querytextboxid").val();
		} else if ($("#queryInputTaken").val().length > 0) {
			if ($('#lstTable :selected').length > 0) {
				//build an array of selected values
				var selectednumbers = [];
				$('#lstTable :selected').each(function(i, selected) {
					selectednumbers[i] = $(selected).val();
				});

				$("#selectedTables").val(JSON.stringify(selectednumbers));
			}
			$('.input_error').remove();
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
			if ($("#datasetid").val().length == 0 && amit1 == "") {
				console.log('datasetid');
				$(
						'<span class="input_error" style="font-size:12px;color:red">Please Enter Data Template Name</span>')
						.insertAfter($('#datasetid'));
				no_error = 0;
			}
			
			
			if ($("#locationid").val() == -1) {
				console.log($("#locationid").val());
				$(
						'<span class="input_error" style="font-size:12px;color:red">Please Choose Data Location</span>')
						.insertAfter($('#locationid'));
				no_error = 0;
			}
			//What is locationId here and why this is being used
			if (($('#locationid').val() == 'MSSQLActiveDirectory')
					|| ($('#locationid').val() == 'Vertica')
					|| ($('#locationid').val() == 'Oracle')
					|| ($('#locationid').val() == 'Oracle RAC')
					|| ($('#locationid').val() == 'MYSQL')					
					|| ($('#locationid').val() == 'MSSQL')
					|| ($('#locationid').val() == 'Hive')
					|| ($('#locationid').val() == 'Hive Kerberos')
					|| ($('#locationid').val() == 'Hive knox')
					|| ($('#locationid').val() == 'MapR Hive')
					|| ($('#locationid').val() == 'Postgres')
					|| ($('#locationid').val() == 'Teradata')
					|| ($('#locationid').val() == 'MapR DB')
					|| ($('#locationid').val() == 'Amazon Redshift')
					|| ($('#locationid').val() == 'Cassandra')
					|| ($('#locationid').val() == 'SnowFlake')
					|| ($('#locationid').val() == 'FileSystem Batch')
					|| ($('#locationid').val() == 'S3 Batch')
					|| ($('#locationid').val() == 'S3 IAMRole Batch')
					|| ($('#locationid').val() == 'BigQuery')
					|| ($('#locationid').val() == 'AzureSynapseMSSQL')
					|| ($('#locationid').val() == 'AzureDataLakeStorageGen1')
					|| ($('#locationid').val() == 'AzureDataLakeStorageGen2Batch')
					|| ($('#locationid').val() == 0)) {
				console.log('locationid');

				if ($("#schemaId").val().length == 0 || $("#schemaId").val() == -1) {
					console.log('schemaId');
					$(
							'<span class="input_error" style="font-size:12px;color:red">Please Select One</span>')
							.insertAfter($('#schemaId'));
					no_error = 0;
				}
				//My Changes
				if ($("#selectedTables").val().length == 0 && $("#querycheckboxid").prop("checked") != true) {
				console.log('selectedTables');
				$(
						'<span class="input_error" style="font-size:12px;color:red"> Select The Table for Template</span>')
						.insertAfter($('#selectedTables'));
				no_error = 0;
			    }
			
				if($("#querycheckboxid").prop("checked") == true){
					
					if($('#querytextboxid').val().length == 0){
						console.log('querytextboxid');
						$(
								'<span class="input_error" style="font-size:12px;color:red"> Please Enter Query</span>')
								.insertAfter($('#querytextboxid'));
						no_error = 0;
					}
				}
				
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
			console.log(no_error);
			if (($('#locationid').val() == 'File System')
					|| ($('#locationid').val() == 'HDFS')
					|| ($('#locationid').val() == 'File Management')) {

				if($("#headerId").prop("checked") == false){
					if ($("#dataupload_id").val().length == 0) {
						$(
								'<span class="input_error" style="font-size:12px;color:red">Please Upload a File</span>')
								.insertAfter($('#dataupload_id'));
						no_error = 0;
					}
				}
				
				if($("#rollingHeaderId").prop("checked") == true){
					if ($("#dataupload_id").val().length == 0) {
						$(
								'<span class="input_error" style="font-size:12px;color:red">Please Upload rolling header File</span>')
								.insertAfter($('#dataupload_id'));
						no_error = 0;
					}
					
					/*
					if ($("#rollingColumn_Id").val().length == 0) {
						$(
								'<span class="input_error" style="font-size:12px;color:red">Please enter rolling column name</span>')
								.insertAfter($('#rollingColumn_Id'));
						no_error = 0;
					}*/
				}
				/*
				 if ($("#datasetid").val().length == 0) {
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
				if ($("#userName").val().length == 0 && $('#locationid').val() !== 'File System') {
					console.log('userName');
					$(
							'<span class="input_error" style="font-size:12px;color:red">Please enter Username</span>')
							.insertAfter($('#userName'));
					no_error = 0;
				}
				if ($("#pwd").val().length == 0 && $('#locationid').val() !== 'File System') {
					console.log('pwd');
					$(
							'<span class="input_error" style="font-size:12px;color:red">Please enter Password</span>')
							.insertAfter($('#pwd'));
					no_error = 0;
				}
			} else if (($('#locationid').val() == 'S3')) {
				if ($("#dataupload_id").val().length == 0 && $("#headerId").prop("checked") == false) {
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
							'<span class="input_error" style="font-size:12px;color:red">Please enter Bucket Name</span>')
							.insertAfter($('#hostName'));
					no_error = 0;
				}
				if ($("#schemaName").val().length == 0) {
					console.log('schemaName');
					$(
							'<span class="input_error" style="font-size:12px;color:red">Please enter Key</span>')
							.insertAfter($('#schemaName'));
					no_error = 0;
				}
				if ($("#userName").val().length == 0) {
					console.log('userName');
					$(
							'<span class="input_error" style="font-size:12px;color:red">Please enter Access Key</span>')
							.insertAfter($('#userName'));
					no_error = 0;
				}
				if ($("#pwd").val().length == 0) {
					console.log('pwd');
					$(
							'<span class="input_error" style="font-size:12px;color:red">Please enter Secret Key</span>')
							.insertAfter($('#pwd'));
					no_error = 0;
				}
				
			} else if (($('#locationid').val() == 'AzureDataLakeStorageGen2')) {
				
				if ($("#hostName").val().length == 0) {
					console.log('hostName');
					$(
							'<span class="input_error" style="font-size:12px;color:red">Please enter Container Name</span>')
							.insertAfter($('#hostName'));
					no_error = 0;
				}
				if ($("#schemaName").val().length == 0) {
					console.log('schemaName');
					$(
							'<span class="input_error" style="font-size:12px;color:red">Please enter File Path</span>')
							.insertAfter($('#schemaName'));
					no_error = 0;
				}
				if ($("#userName").val().length == 0) {
					console.log('userName');
					$(
							'<span class="input_error" style="font-size:12px;color:red">Please enter Account Name</span>')
							.insertAfter($('#userName'));
					no_error = 0;
				}
				if ($("#pwd").val().length == 0) {
					console.log('pwd');
					$(
							'<span class="input_error" style="font-size:12px;color:red">Please enter Account Key</span>')
							.insertAfter($('#pwd'));
					no_error = 0;
				}	
			}

			//Add the details for Kafka here this as Kafka doesn't have a structure 

			else if (($('#locationid').val() == 'Kafka')) {
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
							'<span class="input_error" style="font-size:12px;color:red">Please enter Bucket Name</span>')
							.insertAfter($('#hostName'));
					no_error = 0;
				}
				if ($("#schemaName").val().length == 0) {
					console.log('schemaName');
					$(
							'<span class="input_error" style="font-size:12px;color:red">Please enter Key</span>')
							.insertAfter($('#schemaName'));
					no_error = 0;
				}
				if ($("#userName").val().length == 0) {
					console.log('userName');
					$(
							'<span class="input_error" style="font-size:12px;color:red">Please enter Access Key</span>')
							.insertAfter($('#userName'));
					no_error = 0;
				}
				if ($("#pwd").val().length == 0) {
					console.log('pwd');
					$(
							'<span class="input_error" style="font-size:12px;color:red">Please enter Secret Key</span>')
							.insertAfter($('#pwd'));
					no_error = 0;
				}
			}
			
		}
		console.log(no_error);
		if (($('#locationid').val() == 'FileSystem Batch') || ($('#locationid').val() == 'S3 Batch')
				|| ($('#locationid').val() == 'S3 IAMRole Batch')) {
			
			if ($("#schemaId").val().length == 0) {
				console.log('schemaId');
				$(
						'<span class="input_error" style="font-size:12px;color:red">Please Select One</span>')
						.insertAfter($('#schemaId'));
				no_error = 0;
			}
			
			/* if ($("#selectedTables").val().length == 0) {
				$(
						'<span class="input_error" style="font-size:12px;color:red">Please Select Tables</span>')
						.insertAfter($('#selectedTables'));
				no_error = 0;
			} */
		
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
		
		console.log(no_error);
		
		if (no_error == 0) {
			console.log('error');
			$('.btn').removeClass('hidden');
			return false;
		} else {
			$('.btn').addClass('hidden');

		}
		
	}

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
	
	$('#filequerycheckboxid').click(function() {
		if ($(this).prop("checked") == true) {
			$('#filequerytextboxiddiv').removeClass('hidden');
		} else if ($(this).prop("checked") == false) {
			$('#filequerytextboxiddiv').addClass('hidden');
		}
	});
	
	$('#schemaId')
			.change(
					function() {
						var location = $('#locationid').val();
						if (location == 'Hive' || location == 'Hive Kerberos'
								|| location == 'Hive knox'
								|| location == 'MapR Hive'
								|| location == 'Oracle RAC'
								|| location == 'Oracle'
								|| location == 'MYSQL'								
								|| location == 'Postgres'
								|| location == 'Teradata'
								|| location == 'Vertica' || location == 'MSSQL'
								|| location == 'MSSQLActiveDirectory' || location =='MapR DB'
								|| location == 'Amazon Redshift'
								|| location == 'Cassandra'
								|| location == 'SnowFlake' 
								|| location == 'FileSystem Batch'
								|| location == 'S3 Batch'
								|| location == 'S3 IAMRole Batch'
								|| location == 'BigQuery'
								|| location == 'AzureSynapseMSSQL'
								|| location == 'AzureDataLakeStorageGen1'
								|| location == 'AzureDataLakeStorageGen2Batch') {

							var selectedVal = $this.val();
							$('#lstTable').empty();
							var form_data = {
								schemaId : $("#schemaId").val(),
								locationName : $("#locationid").val(),
							};
							$
									.ajax({
										url : './populateTables',
										type : 'GET',
										beforeSend : function() {
											$('#loading-progress').removeClass(
													'hidden').show();
										},
										datatype : 'json',
										data : form_data,
										success : function(obj) {
											$(obj)
													.each(
															function(i, item) {
																$('#lstTable')
																		.append(
																				$(
																						'<option>',
																						{
																							value : item,
																							text : item
																						}));
															});
											$('#lstTable').multiselect(
													'rebuild');
											$('#lstTable').multiselect({
												includeSelectAllOption : true
											});
											$('#s4_tablename_id').removeClass(
													'hidden');
										},
										error : function() {

										},
										complete : function() {
											$('#loading-progress').addClass(
													'hidden');
										}
									});
						} else {
							$('#s4_tablename_id_2').removeClass('hidden');
							$('#conditionid').removeClass('hidden');
						}
					});
	$('#locationid').change(
			function() {
				$this = $(this);
				var selectedVal = $this.val();
				$('#azureDataLakeStorageGen2').addClass('hidden');
				if (selectedVal == 'Hive' || selectedVal == 'Oracle'
						|| selectedVal == 'MSSQL'
						|| selectedVal == 'MSSQLActiveDirectory'
						|| selectedVal == 'MapR DB'
						|| selectedVal == 'MapR Hive'
						|| selectedVal == 'Vertica'
						|| selectedVal == 'MYSQL'						
						|| selectedVal == 'Postgres'
						|| selectedVal == 'Teradata'
						|| selectedVal == 'Cassandra'
						|| selectedVal == 'Amazon Redshift'
						|| selectedVal == 'Hive Kerberos'
						|| selectedVal == 'Hive knox'
						|| selectedVal == 'Oracle RAC'
						|| selectedVal == 'SnowFlake'
						|| selectedVal == 'FileSystem Batch'
						|| selectedVal == 'S3 Batch'
						|| selectedVal == 'S3 IAMRole Batch'
						|| selectedVal == 'BigQuery'
						|| selectedVal == 'AzureSynapseMSSQL'
						|| selectedVal == 'AzureDataLakeStorageGen1'
						|| selectedVal == 'AzureDataLakeStorageGen2Batch') {

					$('#maprdivid').addClass('hidden');
					$('#s4_form_id').removeClass('hidden').show();
					$('#uploaddivid').addClass('hidden');
					$('#rollingColumnId').addClass('hidden');
					$('#s3_form_id').addClass('hidden');
					$('#kafka_form_id').addClass('hidden');
					$('#checkid').addClass('hidden');
					$('#sourcedivid').addClass('hidden');
					$('#s4_tablename_id').addClass('hidden');
					$('#s4_tablename_id_2').addClass('hidden');
					$('#filesytem_query_id').addClass('hidden');
					$('#filequerycheckboxid').prop("checked", false);	
					$('#filequerytextboxid').val('');
				} else if (selectedVal == 'File System') {
					$('#maprdivid').addClass('hidden');
					$('#fileType').html('Data Source File*');
					$('#checkid').removeClass('hidden');
					$('#kafka_form_id').addClass('hidden');
					$('#sourcedivid').removeClass('hidden').show();
					$('#rollingHeaderCheckId').removeClass('hidden');
					$('#uploaddivid').addClass('hidden');
					$('#s4_form_id').addClass('hidden');
					$('#s3_form_id').removeClass('hidden');
					$('#host-id1').html('Host URI*');
					$('#folder-id1').html('Folder*');
					$('#user-id').html('User Login');
					$('#user-pwd').html('Password');
					$('#filesytem_query_id').removeClass('hidden');
					$('#filequerytextboxiddiv').addClass('hidden');
					$('#filequerycheckboxid').prop("checked", false);	
					$('#filequerytextboxid').val('');
				} else if (selectedVal == 'HDFS' || selectedVal == 'MapR FS') {
					$('#maprdivid').addClass('hidden');
					//$('#host-id').html('Host Name*');
					$('#fileType').html('Data Source File*');
					$('#checkid').removeClass('hidden');
					$('#kafka_form_id').addClass('hidden');
					$('#sourcedivid').removeClass('hidden').show();
					$('#rollingHeaderCheckId').removeClass('hidden');
					$('#uploaddivid').addClass('hidden');
					//$('#sourcedivid').addClass('hidden');
					$('#s4_form_id').addClass('hidden');
					//$('#uploaddivid').removeClass('hidden');
					$('#s3_form_id').removeClass('hidden');
					// $('#host-id-container').removeClass('hidden').show();
					$('#host-id1').html('Host URI*');
					$('#folder-id1').html('Folder*');
					$('#user-id').html('User Login*');
					$('#user-pwd').html('Password*');
					$('#filesytem_query_id').addClass('hidden');
					$('#filequerycheckboxid').prop("checked", false);	
					$('#filequerytextboxid').val('');
				} else if (selectedVal == 'File Management') {
					$('#maprdivid').addClass('hidden');
					//$('#host-id').html('Host URI or Host IP*');
					$('#fileType').html('File Management CSV*');
					$('#uploaddivid').removeClass('hidden');
					$('#rollingColumnId').addClass('hidden');
					$('#s3_form_id').removeClass('hidden');
					//hide remaining
					$('#s4_form_id').addClass('hidden');
					$('#kafka_form_id').addClass('hidden');
					$('#checkid').addClass('hidden');
					$('#sourcedivid').addClass('hidden');
					$('#filesytem_query_id').addClass('hidden');
					$('#filequerycheckboxid').prop("checked", false);	
					$('#filequerytextboxid').val('');
				} else if (selectedVal == 'S3') {
					$('#maprdivid').addClass('hidden');
					$('#host-id1').html('Bucket Name*');
					$('#folder-id1').html('Folder / File Name*');
					$('#user-id').html('Access Key*');
					$('#user-pwd').html('Secret Key*');
					$('#kafka_form_id').addClass('hidden');
					$('#fileType').html('Data Source File*');
					$('#checkid').removeClass('hidden');
					$('#sourcedivid').removeClass('hidden').show();
					//$('#sourcedivid').addClass('hidden');
					$('#s4_form_id').addClass('hidden');
					//$('#uploaddivid').removeClass('hidden');
					$('#s3_form_id').removeClass('hidden');
					$('#filesytem_query_id').addClass('hidden');
					$('#filequerycheckboxid').prop("checked", false);	
					$('#filequerytextboxid').val('');
				} else if (selectedVal == 'AzureDataLakeStorageGen2') {
					$('#maprdivid').addClass('hidden');
					$('#host-id1').html('Container Name*');
					$('#folder-id1').html('File Path*');
					$('#user-id').html('Account Name*');
					$('#user-pwd').html('Account Key*');
					$('#kafka_form_id').addClass('hidden');
					$('#fileType').html('Data Source File*');
					$('#checkid').removeClass('hidden');
					$('#sourcedivid').removeClass('hidden').show();
					$('#s4_form_id').addClass('hidden');
					$('#s3_form_id').removeClass('hidden');
					$('#filesytem_query_id').addClass('hidden');
					$('#filequerycheckboxid').prop("checked", false);	
					$('#filequerytextboxid').val('');
					$('#checkid').addClass('hidden');
					$('#uploaddivid').addClass('hidden');
					$('#incremental_div').addClass('hidden');
				} else if (selectedVal == 'Kafka') {
					$('#maprdivid').addClass('hidden');
					$('#host-id1').html('Broker url*');
					$('#folder-id1').html('Topic Name*');
					//$('#user-id').parentNode.style.display='none';
					//$('#user-pwd').parentNode.style.display='none';
					$('#s3_form_id').addClass('hidden');
					$('#incremental_div').addClass('hidden');
					$('#usercred_div').addClass('hidden');
					$('#rollingColumnId').addClass('hidden');
					$('#fileType').html('Data Source File*');
					$('#sourcedivid').removeClass('hidden').show();
					//$('#sourcedivid').addClass('hidden');
					$('#s4_form_id').addClass('hidden');
					$('#uploaddivid').removeClass('hidden');
					$('#kafka_form_id').removeClass('hidden');
					$('#filesytem_query_id').addClass('hidden');
					$('#filequerycheckboxid').prop("checked", false);	
					$('#filequerytextboxid').val('');
				}

				else if (selectedVal == 'MapR DB') {
					//$('#host-id').html('Host Name*');
					$('#maprdivid').removeClass('hidden');
					$('#fileType').html('Data Source File*');
					$('#checkid').removeClass('hidden');
					$('#sourcedivid').removeClass('hidden').show();
					//$('#sourcedivid').addClass('hidden');
					$('#s4_form_id').addClass('hidden');
					$('#uploaddivid').removeClass('hidden');
					$('#s3_form_id').removeClass('hidden');
					$('#rollingColumnId').addClass('hidden');
					$('#rollingHeaderCheckId').addClass('hidden');
					// $('#host-id-container').removeClass('hidden').show();
					$('#host-id1').html('Host URI*');
					$('#folder-id1').html('Folder*');
					$('#user-id').html('User Login*');
					$('#user-pwd').html('Password*');
					$('#filesytem_query_id').addClass('hidden');
					$('#filequerycheckboxid').prop("checked", false);	
					$('#filequerytextboxid').val('');
				}else if (selectedVal == 'AzureDataLakeStorageGen2Batch') {
                     $('#maprdivid').addClass('hidden');
                     $('#host-id1').html('Container Name*');
                     $('#folder-id1').html('Folder / File Name*');
                     $('#user-id').html('Account Name*');
                     $('#user-pwd').html('Account Key*');
                     $('#kafka_form_id').addClass('hidden');
                     $('#fileType').html('Data Source File*');
                     $('#checkid').removeClass('hidden');
                     $('#sourcedivid').removeClass('hidden').show();
                     //$('#sourcedivid').addClass('hidden');
                     $('#s4_form_id').addClass('hidden');
                     //$('#uploaddivid').removeClass('hidden');
                     $('#s3_form_id').removeClass('hidden');
                     $('#filesytem_query_id').addClass('hidden');
                     $('#filequerycheckboxid').prop("checked", false);
                     $('#filequerytextboxid').val('');
                }
				
			});
	
</script>
</head>