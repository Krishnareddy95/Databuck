<jsp:include page="checkVulnerability.jsp" />
<%@page import="java.math.*"%>
<head>
<!-- <link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.10.15/css/jquery.dataTables.min.css">
	<link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/fixedcolumns/3.2.2/css/fixedColumns.dataTables.min.css"> -->
<style>
  .templDeltaGreen {background-color:#b4f3b4;}
  .templDeltaRed {background-color:#f3c7c7;}
  .templDeltaNewLabel{color:#08a708 !important;}
  .templDeltaMissingLabel{color:red !important;}
  
.tooltip {
  position: relative;
  display: inline-block;
  border-bottom: 1px dotted black;
}

.tooltip .tooltiptext {
  visibility: hidden;
  width: 120px;
  background-color: #555;
  color: #fff;
  text-align: center;
  border-radius: 6px;
  padding: 5px 0;
  position: absolute;
  z-index: 1;
  bottom: 125%;
  left: 50%;
  margin-left: -60px;
  opacity: 0;
  transition: opacity 0.3s;
}

.tooltip .tooltiptext::after {
  content: "";
  position: absolute;
  top: 100%;
  left: 50%;
  margin-left: -5px;
  border-width: 5px;
  border-style: solid;
  border-color: #555 transparent transparent transparent;
}

.tooltip:hover .tooltiptext {
  visibility: visible;
  opacity: 1;
}

.btnApprove {
	margin-right: 5px;
}

.btnReject {
}

.columnlabel {
    border: 1px solid red;
    border-radius: 5px 5px 5px;
    background-color: white;
    padding-left: 5px;
    font-size: smaller;
    padding-right: 5px;
    margin-left: 10px;
    padding-top: 1px;
    padding-bottom: 1px;
    color: blue;
}   
 th, td { white-space: nowrap; }
   
</style>
</head>
<%@page import="com.databuck.service.RBACController"%>
<%@page import="com.databuck.bean.ListDataDefinitionDelta"%>
<%@page import="com.databuck.bean.ListDataDefinition"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<jsp:include page="header.jsp" />

<jsp:include page="container.jsp" />
<div class="page-content-wrapper">
	<!-- BEGIN CONTENT BODY -->
	<div class="page-content">
		<!-- BEGIN PAGE TITLE-->
		<!-- END PAGE TITLE-->
		<!-- END PAGE HEADER-->
	
		<div class="row">
			<span id="tourl" style="display: value="${listdatasource.name}"></span>
			<div class="col-md-12">
				<!-- BEGIN EXAMPLE TABLE PORTLET-->
			<div class="portlet light bordered">
					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold ">Details for Data
								Template : ${name} </span>
						</div>
					</div>
					
					<div id="template_approval_div" class="portlet light bordered" style="background-color: mintcream;">
						<input type="hidden" id="listDataDefinitionDelta_id" value="${listDataDefinitionData}">
						<input type="hidden" id="templateApprovalStatus_id" value="${templateDeltaResponse.deltaApprovalStatus}">
						<input type="hidden" id="microsegmentsChanged_id" value="${templateDeltaResponse.microsegmentsChanged}">
						<input type="hidden" id="colConfigChanged_id" value="${templateDeltaResponse.matchColumnsConfigChanged}">
						<input type="hidden" id="colDeleteCheck_id" value="${templateDeltaResponse.columnsDeleted}">
						<input type="hidden" id="colAddCheck_id" value="${templateDeltaResponse.columnsAdded}">
						<input type="hidden" id="templateId" value="${idData}">
						<input type="hidden" id="allowColDataChange_Id" name="allowColDataChange" value="N">
						
						<div id="approval_msg_div">
							<span id="approval_msg_id"></span>
						</div>
						
						<div id="dgroup_approval_div" class="row hidden">
							<br/>
							<div class="col-md-6">
								<div><b>Microsegments are changed:</b></div>
								<div>Click <b style="color:blue;">Approve</b> to create new template and validation</div>
								<div>Click <b style="color:red;">Reject</b> to replace with previous run configuration</div>
						    </div>
							<div class="col-md-6">
								<p align="right" style="padding-top:10px;">
									<button class="btn btn-primary btnApprove" id="btnDGroupApprove">Approve</button>
									<button class="btn btn-primary btnReject" id="btnDGroupReject">Reject</button>
								</p>
							</div>
						</div>
						
						<div id="colConfig_approval_div" class="row hidden">
							<br/>
							<div class="col-md-6">
								<div><b>Columns Configurations are changed:</b></div>
								<div>Click <b style="color:blue;">Approve</b> to accept changes and proceed</div>
								<div>Click <b style="color:red;">Reject</b> to replace with previous run configuration</div>
						    </div>
							<div class="col-md-6">
								<p align="right" style="padding-top:10px;">
									<button class="btn btn-primary btnApprove" id="btnColConfigApprove">Approve</button>
									<button class="btn btn-primary btnReject" id="btnColConfigReject">Reject</button>
								</p>
							</div>
						</div>
					</div>
					
					<div class="portlet-body">
						<table class="stripe row-border order-column" cellspacing="0"
							width="100%" id="sample_editable">
							<thead>
								<tr>
									<th>Column Name</th>
									<!-- <th>Display Name</th> -->
									<th>Format<a onClick="validateHrefVulnerability(this)"  href="#" title="Format" data-toggle="popover"
										data-trigger="hover"
										data-content="Data type of the column"><span
											class="glyphicon glyphicon-info-sign"></span></a></th>
									<th>Primary Key  
										<br> <a onClick="validateHrefVulnerability(this)"  href="primaryKeyyes?idData=${idData}&name=${name}">select</a>
										<a onClick="validateHrefVulnerability(this)"  href="#" title="Primary Key" data-toggle="popover" data-trigger="hover" data-content="A set of Primary Keys will uniquely identify every record. This can be one way to check for duplicate data."><span class="glyphicon glyphicon-info-sign"></span></a>
										</th>
										
									<th>Duplicate Key<br> <a onClick="validateHrefVulnerability(this)" 
										href="dupkeyyes?idData=${idData}&name=${name}">select</a>
										<a onClick="validateHrefVulnerability(this)"  href="#" title="Duplicate Key" data-toggle="popover" data-trigger="hover" data-content="Multiple columns can be selected here. Their values between the records will all be compared to determine the duplicate records in the table.">
									<span class="glyphicon glyphicon-info-sign"></span></a> 
										</th>	
										
										
									<th>Microsegment<br> <a onClick="validateHrefVulnerability(this)" 
										href="microSegmentyes?idData=${idData}&name=${name}">select</a>
										<a onClick="validateHrefVulnerability(this)"  href="#" title="Microsegment" data-toggle="popover" data-trigger="hover" data-content="Data will be grouped into cohorts based on the columns selected. For eg., in a data set containing school children grades, children enrolled in the Algebra class, taught by a specific teacher can be compared with each other and will belong to the same cohort.">
										<span class="glyphicon glyphicon-info-sign"></span></a>
										</th>
										
									<th>Last Read Time<br> <a onClick="validateHrefVulnerability(this)" 
										href="lastReadTimeyes?idData=${idData}&name=${name}">select</a>
										<a onClick="validateHrefVulnerability(this)"  href="#" title="Last Read	Time" data-toggle="popover" data-trigger="hover" data-content="Not a check. It should be enabled on single Date column which will be considered for Historic/ Incremental date.">
										<span class="glyphicon glyphicon-info-sign"></span></a>
										</th>
										
									<th>Do not Display<br> <a onClick="validateHrefVulnerability(this)" 
										href="doNotDisplayyes?idData=${idData}&name=${name}">select</a>
										<a onClick="validateHrefVulnerability(this)"  href="#" title="Do Not Display" data-toggle="popover"
										data-trigger="hover"
										data-content="Column enabled with one or all of following checks i.e., SubSegment, DataDrift, Distribution check, Record Anomaly is not eligible for 'Do Not Show' and vice versa."><span
											class="glyphicon glyphicon-info-sign"></span></a></th>
											
									<th>Partition Key<br> <a onClick="validateHrefVulnerability(this)" 
										href="partitionByyes?idData=${idData}&name=${name}">select</a>
										<a onClick="validateHrefVulnerability(this)"  href="#" title="Partition Key" data-toggle="popover" data-trigger="hover" data-content="Not a check. Data will be grouped in the column selected to increase execution speed on spark.">
									<span class="glyphicon glyphicon-info-sign"></span></a>
										</th>
										
									<th>Null Check<br> <a onClick="validateHrefVulnerability(this)" 
										href="nonNullyes?idData=${idData}&name=${name}">select</a>
										<a onClick="validateHrefVulnerability(this)"  href="#" title="Null Check" data-toggle="popover" data-trigger="hover" data-content="The frequency of null/blank values present in the selected column will be tracked for deviations from historical expectations.">
									<span class="glyphicon glyphicon-info-sign"></span></a>
										</th>
									
									<th>NullCheck Threshold
									<br><a onClick="validateHrefVulnerability(this)"  href="#" title="NullCheck Threshold" data-toggle="popover" data-trigger="hover" data-content="Maximum percentage of Nulls acceptable for that specific column. If data has more Nulls, the column is classified as having failed the Null test.">
									<span class="glyphicon glyphicon-info-sign"></span></a></th>
									
									<th>Distribution Check<br><a onClick="validateHrefVulnerability(this)" 
										href="numericalStatyes?idData=${idData}&name=${name}">select</a>
										 <a onClick="validateHrefVulnerability(this)"  href="#" title="Distribution Check" data-toggle="popover" data-trigger="hover" data-content="Pick the columns to track for unsual changes in the Mean, Std. Deviation and Sum of Values in the numerical data-column.">
									<span class="glyphicon glyphicon-info-sign"></span></a>
										</th>
										
										
									<%-- <th>Text Fingerprint<br> <a onClick="validateHrefVulnerability(this)" 
										href="stringStatyes?idData=${idData}&name=${name}">select</a></th> --%>
									<th>Data Drift<br> <a onClick="validateHrefVulnerability(this)" 
										href="dataDriftyes?idData=${idData}&name=${name}">select</a>
										<a onClick="validateHrefVulnerability(this)"  href="#" title="Data Drift" data-toggle="popover" data-trigger="hover" data-content="Track the unique values expected to be seen in a column. For eg., if a new product code appears (or an old one stops appearing) it will be flagged.">
									<span class="glyphicon glyphicon-info-sign"></span></a>
										</th>
									
									<th>DataDrift Threshold
									<br><a onClick="validateHrefVulnerability(this)"  href="#" title="DataDrift Threshold" data-toggle="popover" data-trigger="hover" data-content="Maximum acceptable percentage of Data Drift.">
									<span class="glyphicon glyphicon-info-sign"></span></a></th>
									
									<th>Start Date<br><a onClick="validateHrefVulnerability(this)" 
										href="startDateyes?idData=${idData}&name=${name}">select</a><a onClick="validateHrefVulnerability(this)"  href="#" title="Start Date" data-toggle="popover" data-trigger="hover" data-content="Applicable only for a date column, indicates that this column be used to identify the start of the sequence.">
									<span class="glyphicon glyphicon-info-sign"></span></a> </th>
										
									<th>End Date<br><a onClick="validateHrefVulnerability(this)" 
										href="endDateyes?idData=${idData}&name=${name}">select</a><a onClick="validateHrefVulnerability(this)"  href="#" title="End Date" data-toggle="popover" data-trigger="hover" data-content="Applicable only for a date column, indicates that this column be used to identify the end of the sequence.">
									<span class="glyphicon glyphicon-info-sign"></span></a></th>
									
									<th>Timeliness Key<br><a onClick="validateHrefVulnerability(this)" 
										href="timelinessKeyyes?idData=${idData}&name=${name}">select</a><a onClick="validateHrefVulnerability(this)"  href="#" title="Timeliness Key" data-toggle="popover" data-trigger="hover" data-content="Enable to check the sequence between two dates for a column selected. There should be only 1 Timeliness Key.">
									<span class="glyphicon glyphicon-info-sign"></span></a> </th>
									
									<th>Record Anomaly<br><a onClick="validateHrefVulnerability(this)" 
										href="recordAnomalyyes?idData=${idData}&name=${name}">select</a> <a onClick="validateHrefVulnerability(this)"  href="#" title="Record Anomaly" data-toggle="popover" data-trigger="hover" data-content="Enable to 'Y' to check for anomalous values in that column.">
									<span class="glyphicon glyphicon-info-sign"></span></a></th>
									
									<th>RecordAnomaly Threshold
									<br><a onClick="validateHrefVulnerability(this)"  href="#" title="RecordAnomaly Threshold" data-toggle="popover" data-trigger="hover" data-content="Maximum acceptable percentage deviation of values in this column compared to historical expectations.">
									<span class="glyphicon glyphicon-info-sign"></span></a></th>
									
									<th>Default Check<br> <a onClick="validateHrefVulnerability(this)" 
										href="defaultCheckyes?idData=${idData}&name=${name}">select</a><a onClick="validateHrefVulnerability(this)"  href="#" title="Default Check" data-toggle="popover" data-trigger="hover" data-content="Enable to 'Y' to check for default values in that column.">
									<span class="glyphicon glyphicon-info-sign"></span></a></th>
									
									<th>Default Values
									<br><a onClick="validateHrefVulnerability(this)"  href="#" title="Default Values" data-toggle="popover" data-trigger="hover" data-content="Enter the Default values that should be present in the column on which Default check is enabled.">
									<span class="glyphicon glyphicon-info-sign"></span></a></th>
									
									<th>Date Rules<br> <a onClick="validateHrefVulnerability(this)" 
										href="dateRuleyes?idData=${idData}&name=${name}">select</a>	<a onClick="validateHrefVulnerability(this)"  href="#" title="Date Rules" data-toggle="popover" data-trigger="hover" data-content="Applicable only for a Date column. Enable to Y to check for anaomalous values in the Date column.">
									<span class="glyphicon glyphicon-info-sign"></span></a></th>
									
									<th>Date Format
									<br><a onClick="validateHrefVulnerability(this)"  href="#" title="Date Format" data-toggle="popover" data-trigger="hover" data-content="Indicate the date format present in the table for a specific column. For eg., YYYYMMDD.">
									<span class="glyphicon glyphicon-info-sign"></span></a></th>
									
									<th>Regex Pattern Check<br> <a onClick="validateHrefVulnerability(this)" 
										href="patternCheckyes?idData=${idData}&name=${name}">select</a>
										<a onClick="validateHrefVulnerability(this)"  href="#" title="Regex Pattern Check" data-toggle="popover" data-trigger="hover" data-content="To check whether the column contains data in a particular pattern.">
									<span class="glyphicon glyphicon-info-sign"></span></a></th>
									
									<th>Regex Patterns
									<br><a onClick="validateHrefVulnerability(this)"  href="#" title="Regex Patterns" data-toggle="popover" data-trigger="hover" data-content="Regex should be given for the column on which Regex pattern check is enabled (for eg., AAA999 means first 3 letters are alphabets and the next 3 are numbers).">
									<span class="glyphicon glyphicon-info-sign"></span></a></th>
									
									
									<th>Regex PatternCheck Threshold
									<br><a onClick="validateHrefVulnerability(this)"  href="#" title=" Regex Pattern Check Threshold" data-toggle="popover" data-trigger="hover" data-content="Maximum acceptable percentage of records that fail the Regex Pattern check.">
									<span class="glyphicon glyphicon-info-sign"></span></a></th>
									
									<th>Default Pattern Check<br> <a onClick="validateHrefVulnerability(this)" 
									href="defaultPatternCheckyes?idData=${idData}&name=${name}">select</a>
									<a onClick="validateHrefVulnerability(this)"  href="#" title="Default Pattern Check" data-toggle="popover" data-trigger="hover" data-content="To check whether the column contains data in any of the default patterns identified.">
									<span class="glyphicon glyphicon-info-sign"></span></a></th>
									
									<th>Default Patterns
									<br><a onClick="validateHrefVulnerability(this)"  href="#" title="Default Patterns" data-toggle="popover" data-trigger="hover" data-content="List of top three patterns matching 90% of data">
									<span class="glyphicon glyphicon-info-sign"></span></a></th>
									
									<th>Bad Data Check <br><a onClick="validateHrefVulnerability(this)" 
										href="badDatayes?idData=${idData}&name=${name}">select</a>
										<a onClick="validateHrefVulnerability(this)"  href="#" title="Bad Data Check" data-toggle="popover" data-trigger="hover" data-content="Enable to identify the bad data in a particular column.">
									<span class="glyphicon glyphicon-info-sign"></span></a><br></th>
									
									<th>BadDataCheck Threshold
									<br><a onClick="validateHrefVulnerability(this)"  href="#" title="Bad Data Check Threshold" data-toggle="popover" data-trigger="hover" data-content="Maximum acceptable percentage of records that fail the Bad data check.">
									<span class="glyphicon glyphicon-info-sign"></span></a></th>
									
									<th>Length Check <br><a onClick="validateHrefVulnerability(this)" 
										href="lengthCheckyes?idData=${idData}&name=${name}">select</a><a onClick="validateHrefVulnerability(this)"  href="#" title="Length Check" data-toggle="popover" data-trigger="hover" data-content="Identify records which do not satisfy the expected length (for eg., a zip code can be 5 or 9 digits, but not 3 or 4).">
									<span class="glyphicon glyphicon-info-sign"></span></a><br> </th>
									
									 <th>Max Length Check<a onClick="validateHrefVulnerability(this)"  href="#" title="Max Length Check" data-toggle="popover" data-trigger="hover" data-content="-to be added">
									<span class="glyphicon glyphicon-info-sign"></span></a><br> <a onClick="validateHrefVulnerability(this)" 
										href="maxlengthCheckyes?idData=${idData}&name=${name}">select</a></th> 
									
									
									<th>Length Value <br><a onClick="validateHrefVulnerability(this)"  href="#" title="Length Value" data-toggle="popover" data-trigger="hover" data-content="length of the data that should be present in the column on which Length check is enabled (for eg., a zip code can be 5 or 9 digits, but not 3 or 4).">
									<span class="glyphicon glyphicon-info-sign"></span></a></th>
									
									
									<th>LengthCheck Threshold<br><a onClick="validateHrefVulnerability(this)"  href="#" title="Length Check Threshold" data-toggle="popover" data-trigger="hover" data-content="Maximum acceptable percentage of records that fail the Length check.">
									<span class="glyphicon glyphicon-info-sign"></span></a></th>
									
									<th>NumFingerprint Threshold<br><a onClick="validateHrefVulnerability(this)"  href="#" title="Num Fingerprint Threshold" data-toggle="popover" data-trigger="hover" data-content="Maximum acceptable percentage deviation in the mean or Std. Deviation or the Sum total of the values in that specific column. If deviation is greater the column is classified as having failed.">
									<span class="glyphicon glyphicon-info-sign"></span></a></th>
									<!-- <th>TextFingerprint Threshold</th> -->
									
									
									<th>Match Value <br><a onClick="validateHrefVulnerability(this)" 
										href="measurementyes?idData=${idData}&name=${name}">select</a><a onClick="validateHrefVulnerability(this)"  href="#" title="Match Value" data-toggle="popover" data-trigger="hover" data-content="Applicable only to match (or reconcile) numeric columns in different tables. Enable to 'Y' to match any column between two templates.">
									<span class="glyphicon glyphicon-info-sign"></span></a><br> </th>
								</tr>
							</thead>
							<tbody id="listdatadefinitiontable">
								<c:forEach var="listdatadefinitionDelta" items="${listDataDefinitionData}">
									<c:set var = "listdatadefinition" scope = "session" value = "${listdatadefinitionDelta.curListDataDefinition}"/> 
									<c:set var = "stgListdatadefinition" scope = "session" value = "${listdatadefinitionDelta.stgListDataDefinition}"/> 
									
									<span style="display: none" class="source-item-id">${stgListdatadefinition.idColumn}</span>
									
									<tr class="source-item" data-attr="${stgListdatadefinition.idColumn}">
										
									   	<td class="source-displayName" data-attr="displayName">
										   	<label id="displayName_${stgListdatadefinition.idColumn}_label" 
										   		class="<c:choose><c:when test="${(listdatadefinitionDelta.deltaType eq 'NEW')}">templDeltaNewLabel</c:when><c:when test="${(listdatadefinitionDelta.deltaType eq 'MISSING')}">templDeltaMissingLabel</c:when></c:choose>">
												${stgListdatadefinition.displayName}<c:choose><c:when test="${(listdatadefinitionDelta.deltaType eq 'NEW')}"><span class="columnlabel">New</span></c:when><c:when test="${(listdatadefinitionDelta.deltaType eq 'MISSING')}"><span class="columnlabel">Deleted</span></c:when></c:choose></label> 
											<input id="displayName_${stgListdatadefinition.idColumn}_text"
												type="text" class="hidden input-hide" value="${stgListdatadefinition.displayName}" disabled /></td>
											
										<td class="source-format <c:if test="${(listdatadefinitionDelta.deltaType eq 'CHANGED') and (stgListdatadefinition.format ne listdatadefinition.format)}">templDeltaRed</c:if>" data-attr="format" >
											<label id="format_${stgListdatadefinition.idColumn}_label" title="Current value : ${listdatadefinition.format}">
												${stgListdatadefinition.format}</label> 
											<input id="format_${stgListdatadefinition.idColumn}_text" type="text"
												class="hidden input-hide" value="${stgListdatadefinition.format}" /></td>
											
										<td class="source-primaryKey <c:if test="${(listdatadefinitionDelta.deltaType eq 'CHANGED') and (stgListdatadefinition.primaryKey ne listdatadefinition.primaryKey)}"><c:choose><c:when test="${(stgListdatadefinition.primaryKey eq 'Y')}">templDeltaGreen</c:when><c:otherwise>templDeltaRed</c:otherwise></c:choose></c:if>" 
											data-attr="primaryKey">
											<label id="primaryKey_${stgListdatadefinition.idColumn}_label">
												${stgListdatadefinition.primaryKey}</label> 
											<input id="primaryKey_${stgListdatadefinition.idColumn}_text"
												type="text" class="hidden input-hide col-xs-9" 
												value="${stgListdatadefinition.primaryKey}" /></td>
										
										<td class="source-dupkey <c:if test="${(listdatadefinitionDelta.deltaType eq 'CHANGED') and (listdatadefinition.dupkey ne stgListdatadefinition.dupkey)}"><c:choose><c:when test="${(stgListdatadefinition.dupkey eq 'Y')}">templDeltaGreen</c:when><c:otherwise>templDeltaRed</c:otherwise></c:choose></c:if>"
											data-attr="dupkey">
											<label id="dupkey_${stgListdatadefinition.idColumn}_label">
												${stgListdatadefinition.dupkey} </label> 
											<input id="dupkey_${stgListdatadefinition.idColumn}_text" type="text"
												class="hidden input-hide col-xs-9"
												value="${stgListdatadefinition.dupkey}" /></td>
										
										<td class="source-dgroup <c:if test="${(listdatadefinitionDelta.deltaType eq 'CHANGED') and (listdatadefinition.dgroup ne stgListdatadefinition.dgroup)}"><c:choose><c:when test="${(stgListdatadefinition.dgroup eq 'Y')}">templDeltaGreen</c:when><c:otherwise>templDeltaRed</c:otherwise></c:choose></c:if>" 
											data-attr="dgroup">
											<label id="dgroup_${stgListdatadefinition.idColumn}_label">
												${stgListdatadefinition.dgroup}</label> 
											<input id="dgroup_${stgListdatadefinition.idColumn}_text" type="text"
												class="hidden input-hide col-xs-9"
												value="${stgListdatadefinition.dgroup}" /></td>
											
										<td class="source-incrementalCol <c:if test="${(listdatadefinitionDelta.deltaType eq 'CHANGED') and (listdatadefinition.incrementalCol ne stgListdatadefinition.incrementalCol)}"><c:choose><c:when test="${(stgListdatadefinition.incrementalCol eq 'Y')}">templDeltaGreen</c:when><c:otherwise>templDeltaRed</c:otherwise></c:choose></c:if>" 
											data-attr="incrementalCol">
											<label id="incrementalCol_${stgListdatadefinition.idColumn}_label">
												${stgListdatadefinition.incrementalCol} </label> 
											<input id="incrementalCol_${stgListdatadefinition.idColumn}_text"
												type="text" class="hidden input-hide col-xs-9"
												value="${stgListdatadefinition.incrementalCol}" /></td>
										
										<td class="source-isMasked <c:if test="${(listdatadefinitionDelta.deltaType eq 'CHANGED') and (listdatadefinition.isMasked ne stgListdatadefinition.isMasked)}"><c:choose><c:when test="${(stgListdatadefinition.isMasked eq 'Y')}">templDeltaGreen</c:when><c:otherwise>templDeltaRed</c:otherwise></c:choose></c:if>" 
											data-attr="isMasked">
											<label id="isMasked_${stgListdatadefinition.idColumn}_label">
												${stgListdatadefinition.isMasked} </label> 
											<input id="isMasked_${stgListdatadefinition.idColumn}_text" type="text"
												class="hidden input-hide col-xs-9"
												value="${stgListdatadefinition.isMasked}" /></td>
											
										<td class="source-partitionBy <c:if test="${(listdatadefinitionDelta.deltaType eq 'CHANGED') and (listdatadefinition.partitionBy ne stgListdatadefinition.partitionBy)}"><c:choose><c:when test="${(stgListdatadefinition.partitionBy eq 'Y')}">templDeltaGreen</c:when><c:otherwise>templDeltaRed</c:otherwise></c:choose></c:if>" 
											data-attr="partitionBy">
											<label id="partitionBy_${stgListdatadefinition.idColumn}_label">
												${stgListdatadefinition.partitionBy} </label> 
											<input id="partitionBy_${stgListdatadefinition.idColumn}_text"
												type="text" class="hidden input-hide col-xs-9"
												value="${stgListdatadefinition.partitionBy}" /></td>
											
										<td class="source-nonNull <c:if test="${(listdatadefinitionDelta.deltaType eq 'CHANGED') and (listdatadefinition.nonNull ne stgListdatadefinition.nonNull)}"><c:choose><c:when test="${(stgListdatadefinition.nonNull eq 'Y')}">templDeltaGreen</c:when><c:otherwise>templDeltaRed</c:otherwise></c:choose></c:if>" 
											data-attr="nonNull">
											<label id="nonNull_${stgListdatadefinition.idColumn}_label"> 
												${stgListdatadefinition.nonNull} </label> 
											<input id="nonNull_${stgListdatadefinition.idColumn}_text" type="text"
												class="hidden input-hide col-xs-9"
												value="${stgListdatadefinition.nonNull}" /></td>
												
										
											<td class="source-nullCountThreshold <c:if test="${(listdatadefinitionDelta.deltaType eq 'CHANGED') and (listdatadefinition.nullCountThreshold ne stgListdatadefinition.nullCountThreshold)}">templDeltaRed</c:if>"
											data-attr="nullCountThreshold">
											<label id="nullCountThreshold_${stgListdatadefinition.idColumn}_label" title="${listdatadefinition.nullCountThreshold}">
												${stgListdatadefinition.nullCountThreshold} </label> 
											<input id="nullCountThreshold_${stgListdatadefinition.idColumn}_text"
												type="text" class="hidden input-hide col-xs-9"
												value="${stgListdatadefinition.nullCountThreshold}" /></td>
												
										<td class="source-numericalStat <c:if test="${(listdatadefinitionDelta.deltaType eq 'CHANGED') and (listdatadefinition.numericalStat ne stgListdatadefinition.numericalStat)}"><c:choose><c:when test="${(stgListdatadefinition.numericalStat eq 'Y')}">templDeltaGreen</c:when><c:otherwise>templDeltaRed</c:otherwise></c:choose></c:if>"
											data-attr="numericalStat">
											<label id="numericalStat_${stgListdatadefinition.idColumn}_label">
												${stgListdatadefinition.numericalStat} </label> 
											<input id="numericalStat_${stgListdatadefinition.idColumn}_text"
												type="text" class="hidden input-hide col-xs-9"
												value="${stgListdatadefinition.numericalStat}" /></td>
												
										<%-- <td class="source-stringStat <c:if test="${(listdatadefinitionDelta.deltaType eq 'CHANGED') and (listdatadefinition.stringStat ne stgListdatadefinition.stringStat)}"><c:choose><c:when test="${(stgListdatadefinition.stringStat eq 'Y')}">templDeltaGreen</c:when><c:otherwise>templDeltaRed</c:otherwise></c:choose></c:if>"
											data-attr="stringStat">
											<label id="stringStat_${stgListdatadefinition.idColumn}_label">
												${stgListdatadefinition.stringStat} </label> 
											<input id="stringStat_${stgListdatadefinition.idColumn}_text"
												type="text" class="hidden input-hide col-xs-9"
												value="${stgListdatadefinition.stringStat}" /></td> --%>
												
										<td class="source-dataDrift <c:if test="${(listdatadefinitionDelta.deltaType eq 'CHANGED') and (listdatadefinition.dataDrift ne stgListdatadefinition.dataDrift)}"><c:choose><c:when test="${(stgListdatadefinition.dataDrift eq 'Y')}">templDeltaGreen</c:when><c:otherwise>templDeltaRed</c:otherwise></c:choose></c:if>" 
											data-attr="dataDrift">
											<label id="dataDrift_${stgListdatadefinition.idColumn}_label">
												${stgListdatadefinition.dataDrift} </label> 
											<input id="dataDrift_${stgListdatadefinition.idColumn}_text"
												type="text" class="hidden input-hide col-xs-9"
												value="${stgListdatadefinition.dataDrift}" /></td>
												
										<td class="source-dataDriftThreshold <c:if test="${(listdatadefinitionDelta.deltaType eq 'CHANGED') and (listdatadefinition.dataDriftThreshold ne stgListdatadefinition.dataDriftThreshold)}">templDeltaRed</c:if>"
											data-attr="dataDriftThreshold">
											<label id="dataDriftThreshold_${stgListdatadefinition.idColumn}_label" title="${listdatadefinition.dataDriftThreshold}">
												${stgListdatadefinition.dataDriftThreshold} </label> 
											<input id="dataDriftThreshold_${stgListdatadefinition.idColumn}_text"
												type="text" class="hidden input-hide col-xs-9"
												value="${stgListdatadefinition.dataDriftThreshold}" /></td>

										<td class="source-startDate <c:if test="${(listdatadefinitionDelta.deltaType eq 'CHANGED') and (listdatadefinition.startDate ne stgListdatadefinition.startDate)}"><c:choose><c:when test="${(stgListdatadefinition.startDate eq 'Y')}">templDeltaGreen</c:when><c:otherwise>templDeltaRed</c:otherwise></c:choose></c:if>" 
											data-attr="startDate">
											<label id="startDate_${stgListdatadefinition.idColumn}_label">
												${stgListdatadefinition.startDate} </label> 
											<input id="startDate_${stgListdatadefinition.idColumn}_text"
												type="text" class="hidden input-hide col-xs-9"
												value="${stgListdatadefinition.startDate}" /></td>
											
										<td class="source-endDate <c:if test="${(listdatadefinitionDelta.deltaType eq 'CHANGED') and (listdatadefinition.endDate ne stgListdatadefinition.endDate)}"><c:choose><c:when test="${(stgListdatadefinition.endDate eq 'Y')}">templDeltaGreen</c:when><c:otherwise>templDeltaRed</c:otherwise></c:choose></c:if>"
											data-attr="endDate">
											<label id="endDate_${stgListdatadefinition.idColumn}_label">
												${stgListdatadefinition.endDate} </label> 
											<input id="endDate_${stgListdatadefinition.idColumn}_text" type="text"
												class="hidden input-hide col-xs-9"
												value="${stgListdatadefinition.endDate}" /></td>
											
										<td class="source-timelinessKey <c:if test="${(listdatadefinitionDelta.deltaType eq 'CHANGED') and (listdatadefinition.timelinessKey ne stgListdatadefinition.timelinessKey)}"><c:choose><c:when test="${(stgListdatadefinition.timelinessKey eq 'Y')}">templDeltaGreen</c:when><c:otherwise>templDeltaRed</c:otherwise></c:choose></c:if>"
											data-attr="timelinessKey">
											<label id="timelinessKey_${stgListdatadefinition.idColumn}_label">
												${stgListdatadefinition.timelinessKey} </label> 
											<input id="timelinessKey_${stgListdatadefinition.idColumn}_text"
												type="text" class="hidden input-hide col-xs-9"
												value="${stgListdatadefinition.timelinessKey}" /></td>

										<td class="source-recordAnomaly <c:if test="${(listdatadefinitionDelta.deltaType eq 'CHANGED') and (listdatadefinition.recordAnomaly ne stgListdatadefinition.recordAnomaly)}"><c:choose><c:when test="${(stgListdatadefinition.recordAnomaly eq 'Y')}">templDeltaGreen</c:when><c:otherwise>templDeltaRed</c:otherwise></c:choose></c:if>" 
											data-attr="recordAnomaly">
											<label id="recordAnomaly_${stgListdatadefinition.idColumn}_label">
												${stgListdatadefinition.recordAnomaly} </label> 
											<input id="recordAnomaly_${stgListdatadefinition.idColumn}_text"
												type="text" class="hidden input-hide col-xs-9"
												value="${stgListdatadefinition.recordAnomaly}" /></td>
												
												<td class="source-recordAnomalyThreshold <c:if test="${(listdatadefinitionDelta.deltaType eq 'CHANGED') and (listdatadefinition.recordAnomalyThreshold ne stgListdatadefinition.recordAnomalyThreshold)}">templDeltaRed</c:if>"
											data-attr="recordAnomalyThreshold">
											<label id="recordAnomalyThreshold_${stgListdatadefinition.idColumn}_label" title="${listdatadefinition.recordAnomalyThreshold}">
												${stgListdatadefinition.recordAnomalyThreshold} </label> 
											<input id="recordAnomalyThreshold_${stgListdatadefinition.idColumn}_text"
												type="text" class="hidden input-hide col-xs-9"
												value="${stgListdatadefinition.recordAnomalyThreshold}" /></td>

										<td class="source-defaultCheck <c:if test="${(listdatadefinitionDelta.deltaType eq 'CHANGED') and (listdatadefinition.defaultCheck ne stgListdatadefinition.defaultCheck)}"><c:choose><c:when test="${(stgListdatadefinition.defaultCheck eq 'Y')}">templDeltaGreen</c:when><c:otherwise>templDeltaRed</c:otherwise></c:choose></c:if>" 
											data-attr="defaultCheck">
											<label id="defaultCheck_${stgListdatadefinition.idColumn}_label">
												${stgListdatadefinition.defaultCheck} </label> 
											<input id="defaultCheck_${stgListdatadefinition.idColumn}_text"
													type="text" class="hidden input-hide col-xs-9"
											value="${stgListdatadefinition.defaultCheck}" />
										</td>

										<td class="source-defaultValues <c:if test="${(listdatadefinitionDelta.deltaType eq 'CHANGED') and (listdatadefinition.defaultValues ne stgListdatadefinition.defaultValues)}">templDeltaRed</c:if>" 
											data-attr="defaultValues">
											<label id="defaultValues_${stgListdatadefinition.idColumn}_label" title="${listdatadefinition.defaultValues}">
												${stgListdatadefinition.defaultValues} </label> 
											<input id="defaultValues_${stgListdatadefinition.idColumn}_text"
												type="text" class="hidden input-hide col-xs-9"
												value="${stgListdatadefinition.defaultValues}" /></td>
											
										<td class="source-dateRule <c:if test="${(listdatadefinitionDelta.deltaType eq 'CHANGED') and (listdatadefinition.dateRule ne stgListdatadefinition.dateRule)}"><c:choose><c:when test="${(stgListdatadefinition.dateRule eq 'Y')}">templDeltaGreen</c:when><c:otherwise>templDeltaRed</c:otherwise></c:choose></c:if>" 
											data-attr="dateRule">
											<label id="dateRule_${stgListdatadefinition.idColumn}_label">
												${stgListdatadefinition.dateRule} </label> 
											<input id="dateRule_${stgListdatadefinition.idColumn}_text"
												type="text" class="hidden input-hide col-xs-9"
												value="${stgListdatadefinition.dateRule}" /></td>

										<!-- dateFormat -->	
										<td class="source-dateFormat <c:if test="${(listdatadefinitionDelta.deltaType eq 'CHANGED') and (listdatadefinition.dateFormat ne stgListdatadefinition.dateFormat)}">templDeltaRed</c:if>" 
											data-attr="dateFormat">
											<label id="dateFormat_${stgListdatadefinition.idColumn}_label" title="${listdatadefinition.dateFormat}">
												${stgListdatadefinition.dateFormat} </label> 
											<input id="dateFormat_${stgListdatadefinition.idColumn}_text" type="text"
												class="hidden input-hide col-xs-9"
												value="${stgListdatadefinition.dateFormat}" /></td>

										<td class="source-patternCheck <c:if test="${(listdatadefinitionDelta.deltaType eq 'CHANGED') and (listdatadefinition.patternCheck ne stgListdatadefinition.patternCheck)}"><c:choose><c:when test="${(stgListdatadefinition.patternCheck eq 'Y')}">templDeltaGreen</c:when><c:otherwise>templDeltaRed</c:otherwise></c:choose></c:if>" 
											data-attr="patternCheck">
											<label id="patternCheck_${stgListdatadefinition.idColumn}_label">
												${stgListdatadefinition.patternCheck} </label> 
											<input id="patternCheck_${stgListdatadefinition.idColumn}_text"
												type="text" class="hidden input-hide col-xs-9"
												value="${stgListdatadefinition.patternCheck}" /></td>

										<td class="source-patterns <c:if test="${(listdatadefinitionDelta.deltaType eq 'CHANGED') and (listdatadefinition.patterns ne stgListdatadefinition.patterns)}">templDeltaRed</c:if>" 
											data-attr="patterns">
											<label id="patterns_${stgListdatadefinition.idColumn}_label" title="${listdatadefinition.patterns}">
												${stgListdatadefinition.patterns} </label> 
											<input id="patterns_${stgListdatadefinition.idColumn}_text" type="text"
												class="hidden input-hide col-xs-9"
												value="${stgListdatadefinition.patterns}" /></td>
												
										<td class="source-patternCheckThreshold <c:if test="${(listdatadefinitionDelta.deltaType eq 'CHANGED') and (listdatadefinition.patternCheckThreshold ne stgListdatadefinition.patternCheckThreshold)}">templDeltaRed</c:if>"
											data-attr="patternCheckThreshold">
											<label id="patternCheckThreshold_${stgListdatadefinition.idColumn}_label" title="${listdatadefinition.patternCheckThreshold}">
												${stgListdatadefinition.patternCheckThreshold} </label> 
											<input id="patternCheckThreshold_${stgListdatadefinition.idColumn}_text"
												type="text" class="hidden input-hide col-xs-9"
												value="${stgListdatadefinition.patternCheckThreshold}" /></td>	
												
										<td class="source-defaultPatternCheck <c:if test="${(listdatadefinitionDelta.deltaType eq 'CHANGED') and (listdatadefinition.defaultPatternCheck ne stgListdatadefinition.defaultPatternCheck)}"><c:choose><c:when test="${(stgListdatadefinition.defaultPatternCheck eq 'Y')}">templDeltaGreen</c:when><c:otherwise>templDeltaRed</c:otherwise></c:choose></c:if>" 
											data-attr="defaultPatternCheck">
											<label id="defaultPatternCheck_${stgListdatadefinition.idColumn}_label">
												${stgListdatadefinition.defaultPatternCheck} </label> 
											<input id="defaultPatternCheck_${stgListdatadefinition.idColumn}_text"
												type="text" class="hidden input-hide col-xs-9"
												value="${stgListdatadefinition.defaultPatternCheck}" /></td>

										<td class="source-defaultPatterns <c:if test="${(listdatadefinitionDelta.deltaType eq 'CHANGED') and (listdatadefinition.defaultPatterns ne stgListdatadefinition.defaultPatterns)}">templDeltaRed</c:if>" 
											data-attr="defaultPatterns">
											<label id="defaultPatterns_${stgListdatadefinition.idColumn}_label">
												${stgListdatadefinition.defaultPatterns} </label> 
											<input id="defaultPatterns_${stgListdatadefinition.idColumn}_text" type="text"
												class="hidden input-hide col-xs-9"
												value="${stgListdatadefinition.defaultPatterns}" disabled /></td>	
											
										<td class="source-badData <c:if test="${(listdatadefinitionDelta.deltaType eq 'CHANGED') and (listdatadefinition.badData ne stgListdatadefinition.badData)}"><c:choose><c:when test="${(stgListdatadefinition.badData eq 'Y')}">templDeltaGreen</c:when><c:otherwise>templDeltaRed</c:otherwise></c:choose></c:if>" 
											data-attr="badData">
											<label id="badData_${stgListdatadefinition.idColumn}_label">
												${stgListdatadefinition.badData} </label> 
											<input id="badData_${stgListdatadefinition.idColumn}_text" type="text"
												class="hidden input-hide col-xs-9"
												value="${stgListdatadefinition.badData}" /></td>
									
									    <td class="source-badDataCheckThreshold <c:if test="${(listdatadefinitionDelta.deltaType eq 'CHANGED') and (listdatadefinition.badDataThreshold ne stgListdatadefinition.badDataThreshold)}">templDeltaRed</c:if>"
											data-attr="badDataCheckThreshold">
											<label id="badDataCheckThreshold_${stgListdatadefinition.idColumn}_label" title="${listdatadefinition.badDataThreshold}">
												${stgListdatadefinition.badDataThreshold} </label> 
											<input id="badDataCheckThreshold_${stgListdatadefinition.idColumn}_text"
												type="text" class="hidden input-hide col-xs-9"
												value="${stgListdatadefinition.badDataThreshold}" /></td>
											
									
										<td class="source-lengthCheck <c:if test="${(listdatadefinitionDelta.deltaType eq 'CHANGED') and (listdatadefinition.lengthCheck ne stgListdatadefinition.lengthCheck)}"><c:choose><c:when test="${(stgListdatadefinition.lengthCheck eq 'Y')}">templDeltaGreen</c:when><c:otherwise>templDeltaRed</c:otherwise></c:choose></c:if>" 
										 	data-attr="lengthCheck">
											<label id="lengthCheck_${stgListdatadefinition.idColumn}_label">
												${stgListdatadefinition.lengthCheck} </label> 
											<input id="lengthCheck_${stgListdatadefinition.idColumn}_text"
												type="text" class="hidden input-hide col-xs-9"
												value="${stgListdatadefinition.lengthCheck}"  /></td>
												
										<td class="source-maxLengthCheck <c:if test="${(listdatadefinitionDelta.deltaType eq 'CHANGED') and (listdatadefinition.maxLengthCheck ne stgListdatadefinition.maxLengthCheck)}"><c:choose><c:when test="${(stgListdatadefinition.maxLengthCheck eq 'Y')}">templDeltaGreen</c:when><c:otherwise>templDeltaRed</c:otherwise></c:choose></c:if>" 
										 	data-attr="maxLengthCheck">
											<label id="maxLengthCheck_${stgListdatadefinition.idColumn}_label">
												${stgListdatadefinition.maxLengthCheck} </label> 
											<input id="maxLengthCheck_${stgListdatadefinition.idColumn}_text"
												type="text" class="hidden input-hide col-xs-9"
												value="${stgListdatadefinition.maxLengthCheck}"  /></td> 

										<td class="source-lengthValue <c:if test="${(listdatadefinitionDelta.deltaType eq 'CHANGED') and (listdatadefinition.lengthValue ne stgListdatadefinition.lengthValue)}">templDeltaRed</c:if>" 
											data-attr="lengthValue">
											<label id="lengthValue_${stgListdatadefinition.idColumn}_label" title="${listdatadefinition.lengthValue}">
												${stgListdatadefinition.lengthValue} </label> 
											<input id="lengthValue_${stgListdatadefinition.idColumn}_text"
												type="text" class="hidden input-hide col-xs-9"
												value="${stgListdatadefinition.lengthValue}"  /></td>

										<td class="source-lengthCheckThreshold <c:if test="${(listdatadefinitionDelta.deltaType eq 'CHANGED') and (listdatadefinition.lengthThreshold ne stgListdatadefinition.lengthThreshold)}">templDeltaRed</c:if>"
											data-attr="lengthCheckThreshold">
											<label id="lengthCheckThreshold_${stgListdatadefinition.idColumn}_label" title="${listdatadefinition.lengthThreshold}">
												${stgListdatadefinition.lengthThreshold} </label> 
											<input id="lengthCheckThreshold_${stgListdatadefinition.idColumn}_text"
												type="text" class="hidden input-hide col-xs-9"
												value="${stgListdatadefinition.lengthThreshold}" /></td>
										
										<td class="source-numericalThreshold <c:if test="${(listdatadefinitionDelta.deltaType eq 'CHANGED') and (listdatadefinition.numericalThreshold ne stgListdatadefinition.numericalThreshold)}">templDeltaRed</c:if>"
											data-attr="numericalThreshold">

											<label id="numericalThreshold_${stgListdatadefinition.idColumn}_label" title="${listdatadefinition.numericalThreshold}" ></label>


											<input id="numericalThreshold_${stgListdatadefinition.idColumn}_text"
												type="text" class="hidden input-hide col-xs-9"
												value="" /></td>

									<%-- 	<td class="source-stringStatThreshold <c:if test="${(listdatadefinitionDelta.deltaType eq 'CHANGED') and (listdatadefinition.stringStatThreshold ne stgListdatadefinition.stringStatThreshold)}">templDeltaRed</c:if>"
											data-attr="stringStatThreshold">
											<label id="stringStatThreshold_${stgListdatadefinition.idColumn}_label" title="${listdatadefinition.stringStatThreshold}">
												${stgListdatadefinition.stringStatThreshold} </label>
											<input id="stringStatThreshold_${stgListdatadefinition.idColumn}_text"
												type="text" class="hidden input-hide col-xs-9"
												value="${stgListdatadefinition.stringStatThreshold}" /></td> --%>
	
										<td class="source-measurement <c:if test="${(listdatadefinitionDelta.deltaType eq 'CHANGED') and (listdatadefinition.measurement ne stgListdatadefinition.measurement)}"><c:choose><c:when test="${(stgListdatadefinition.measurement eq 'Y')}">templDeltaGreen</c:when><c:otherwise>templDeltaRed</c:otherwise></c:choose></c:if>" 
											data-attr="measurement">
											<label id="measurement_${stgListdatadefinition.idColumn}_label">
												${stgListdatadefinition.measurement} </label> 
											<input id="measurement_${stgListdatadefinition.idColumn}_text"
												type="text" class="hidden input-hide col-xs-9"
												value="${stgListdatadefinition.measurement}" /></td>
									</tr>
									
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
				<!-- END EXAMPLE TABLE PORTLET-->
			</div>
		</div>
	</div>
</div>
<jsp:include page="footer.jsp" />

<link rel="stylesheet" href="./assets/jwf/content/JwfSpaInfra.css">
<script src="./assets/jwf/scripts/JwfSpaInfra.js"></script>

<script type="text/javascript">
	$("#listdatadefinitiontable tr td").click(function() {
		var allowColChanges = $('#allowColDataChange_Id').val();
		var templateApprovalStatus = $('#templateApprovalStatus_id').val();
		console.log("allowColChanges: "+allowColChanges);
		console.log("templateApprovalStatus: "+templateApprovalStatus);
		
		if(allowColChanges == 'Y'){
			$(".input-hide").addClass("hidden");
			$("label").removeClass("hidden");
			
			$(this).find(".input-hide").removeClass("hidden");
			$(this).find("label").addClass("hidden");
			var indexRow = $(this).closest("tr").data("attr");
			var indexCol = $(this).data("attr");
		} else {
			var dialogMsg = "";
			if(templateApprovalStatus == 'rejected'){
				dialogMsg = "Template Delta changes rejected, Data changes are not allowed.";
			} else {
				dialogMsg = "Template Delta changes are waiting for approval, Data changes are not allowed.";
			}
			
			Modal.confirmDialog(3, 'Warning', dialogMsg, ['Ok'] );
		}
	});
	
	$('.input-hide').bind('focus', function(e) {
		$(this).data('done', false);
	});

	$(document).ready(function() {
		console.log("documentready");

		<c:forEach var="listdatadefinitionDelta" items="${listDataDefinitionData}">
		<c:set var = "stgListdatadefinition" scope = "session" value = "${listdatadefinitionDelta.stgListDataDefinition}"/>
		    var data=${stgListdatadefinition.numericalThreshold};
		  $("#numericalThreshold_"+${stgListdatadefinition.idColumn}+"_text").val(data.toPrecision());
		  $("#numericalThreshold_"+${stgListdatadefinition.idColumn}+"_label").text(data.toPrecision());
		  $("#numericalThreshold_"+${stgListdatadefinition.idColumn}+"_label").attr('title', data.toPrecision() );
		</c:forEach>

		$(".input-hide").bind('blur keypress',
						function(e) {
							console.log(e.type);
							if (e.type == 'blur'
									|| e.keyCode == '13') {
								if (!$(this).data('done')) {
									$(this).data('done', true);
									var indexRow = $(this).closest("tr").data("attr");
									var indexCol = $(this).closest("td").data("attr");
									var columnValue = $("#" + indexCol+ "_"+ indexRow+ "_text").val();

									if(indexCol == 'dgroup'){
										$('#approval_msg_id').html("Microsegments are changed.<b> Please reload the page</b>.");
									}
									var form_data = {
										idColumn : indexRow,
										columnName : indexCol,
										columnValue : columnValue
									};
									console.log(form_data);
									$.ajax({
											url : "./saveDataTemplateDataInListDataDefinition",
											type : 'POST',
											headers: { 'token':$("#token").val()},
											datatype : 'json',
											data : form_data,
											success : function(
													message) {
												var j_obj = $.parseJSON(message);
												if (j_obj.hasOwnProperty('success')) {
													$("#"+ indexCol+ "_"+ indexRow+ "_label").html(j_obj.columnValue);
													
													toastr.info(j_obj.success);
													setTimeout(function() {},1000);
													
													$('#approval_msg_id').html("Column configuration change detected: <b> Please reload the page.</b>")
												} else {
													if (j_obj.hasOwnProperty('fail')) {
														toastr.info(j_obj.fail);
													}
												}
											},
											error : function(
													xhr,
													textStatus,
													errorThrown) {
											}
										});

									$("#listdatadefinitiontable tr td").find(".input-hide").addClass("hidden");
									$("#listdatadefinitiontable tr td").find("label").removeClass("hidden");
								}
							}
						});
		});
	
	$(document).ready(
			function() {
				var table = $('#sample_editable').DataTable(
						{
							scrollY : "280px",
							scrollX : true,
							scrollCollapse : true,
							paging : false,
							fixedColumns : {
								leftColumns : 1
							},
							"aoColumnDefs" : [ {
								'bSortable' : true,
								'aTargets' : [ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
										11, 12, 13, 14, 15, 16, 17, 18 ]
							} ]
						});
				/*  $(".source-displayName").css({"padding-bottom": "20px"});  */
			});
	$('[data-toggle="popover"]').popover({
		'trigger' : 'hover',
		'placement' : 'top',
		'container' : 'body'
	});
</script>
<script>
$(document).ready(function() {
	initializeJwfSpaInfra();
	
	var listDataDefinitionDelta_list = ${listDataDefinitionData};
	
	var templateApprovalStatus = $('#templateApprovalStatus_id').val();
	var isMicrosegmentsChanged = $('#microsegmentsChanged_id').val();
	var idData = $('#templateId').val();
	
	console.log("idData:"+idData);
	console.log("templateApprovalStatus:"+templateApprovalStatus);
	console.log("isMicrosegmentsChanged:"+isMicrosegmentsChanged);
	
	if(templateApprovalStatus == 'rejected'){
		$('#approval_msg_id').html("Template Delta Changes Status: <b>Rejected</b>.<span style=\"color:blue\"> Please check/create new template.</span>")
	
	} else if(templateApprovalStatus == 'waitingforapproval'){
		$('#approval_msg_id').html("Template Delta Changes Status: <b>Waiting For Approval</b>.<span style=\"color:blue\"> No Changes are allowed until approved.</span>")
	
	}
	else {
		if(templateApprovalStatus == 'reviewpending'){
			$('#approval_msg_id').html("Template Delta Changes Status: <b>Review Pending</b>")
		} else {
			$('#approval_msg_id').html("Template Delta Changes Status: <b>"+templateApprovalStatus+"</b>")
		}

		if(isMicrosegmentsChanged == 'Y'){
			$('#dgroup_approval_div').removeClass('hidden').show();
		} else {
			// Allow user to change column data
			$('#allowColDataChange_Id').val('Y');
			
			// Check if columns are added, deleted or config changed
			var isColsAdded = $('#colAddCheck_id').val();
			var isColsDeleted = $('#colDeleteCheck_id').val();
			var isColsConfigChanged = $('#colConfigChanged_id').val();
								
			console.log("isColsAdded:"+isColsAdded);
			console.log("isColsDeleted:"+isColsDeleted);
			console.log("isColsConfigChanged:"+isColsConfigChanged);
			
			if(isColsAdded=='Y' || isColsDeleted == 'Y' || isColsConfigChanged == 'Y'){
				$('#colConfig_approval_div').removeClass('hidden').show();
			} 
		}
	}
	
	// When MicroSegments are changed - [Approve]
	$('#btnDGroupApprove').click(function() {
		$('#dgroup_approval_div').addClass('hidden');
		$('#approval_msg_id').html("Microsegments Change is Approved. Creating new Template and Validation.<p><b>Please wait ....</b></p>")
		
		// Make API call to deactive the template and associated validations
		// And Copy and create a new template and validation with 
		$.ajax({
			url : './approveMicroSegments?idData='+idData,
			type : 'GET',
			success : function(data) {
				if (data!="") {
					console.log("data: "+data);
					var j_obj = $.parseJSON(data);
					console.log("json: "+j_obj);
					var templDeactivateStatus = j_obj.templateDeactivateStatus;
					var templCreationStatus = j_obj.templateCreationStatus;
					var valCreationStatus = j_obj.validationCreationStatus;
					// Get the new template Id and name
					var templateId = j_obj.templateId;
					var templateName = j_obj.templateName;
					// Get the new validation Id and name
					var validationId = j_obj.validationId;
					var validationName = j_obj.validationName;
					
					var msg = "";
					
					if(templCreationStatus == 'true'){
						if(valCreationStatus == 'true'){
							msg = "New Template and Validation are created Successfully !!";
							msg = msg+"<p style=\"margin-bottom: unset;\"><span style=\"color:blue\">Template</span> => Id: <b>"+templateId+"</b> Name: <b>"+templateName+"</b></p>";
							msg = msg+"<p style=\"margin-bottom: unset;\"><span style=\"color:blue\">Validation</span> => Id: <b>"+validationId+"</b> Name: <b>"+validationName+"</b></p>";
						
						} else {
							msg = "New Template is created Successfully !!";
							msg = msg+"<p style=\"margin-bottom: unset;\"><span style=\"color:blue\">Template</span> => Id: <b>"+templateId+"</b> Name: <b>"+templateName+"</b></p>";
							msg = msg+"<p style=\"margin-bottom: unset;\"><span style=\"color:blue\">Please create validation using new template details.</p>";
						}
						
						if(templDeactivateStatus == 'false'){
							msg = msg+"<p style=\"margin-bottom: unset;\"><span style=\"color:red\">Deactivation of current template is failed.</span> Please try manually.</p>";
						}
					} else {
						msg = "Approval of Microsegments failed: <b>reload the page.</b>";
					}
					
					window.setTimeout(function(){
						$('#approval_msg_id').html(msg);
					}, 1000);
					
				} else {
					$('#approval_msg_id').html("Approval of Microsegments failed: <b>reload the page.</b>")
				}
			},
			error : function(xhr, textStatus,
					errorThrown) {
				$('#approval_msg_id').html("Approval of Microsegments failed: <b>reload the page.</b>")
			}
		});
	});
	
	//When MicroSegments are changed - [Reject]
	$('#btnDGroupReject').click(function() {
		$('#dgroup_approval_div').addClass('hidden');
		$('#approval_msg_id').html("Updating Microsegments and verification of Column Delta changes is in progress. <p><b>Please wait ....</b></p>")
		
		// Make API call to reject and revert to old microsegments
		$.ajax({
			url : './rejectMicroSegments',
			type : 'POST',
			headers: { 'token':$("#token").val()},
			datatype: 'json',
			contentType: 'application/json',
			data : JSON.stringify(listDataDefinitionDelta_list),
			success : function(data) {
				if (data!="") {
					window.setTimeout(function(){
						window.location.reload();   
                    }, 1500);
				} else {
					$('#approval_msg_id').html("Rejection of Microsegments failed: <b>reload the page.</b>")
				}
			},
			error : function(xhr, textStatus,
					errorThrown) {
				$('#approval_msg_id').html("Rejection of Microsegments failed: <b>reload the page.</b>")
			}
		});
		
    });
	
	// When Column config changed - [Approve]
	$('#btnColConfigApprove').click(function() {
		$('#colConfig_approval_div').addClass('hidden');
		$('#approval_msg_id').html("Approval of Column Delta changes is in progress. <p><b>Please wait ....</b></p>")
		
		// Make API call to approve and apply to new column metadata
		$.ajax({
			url : './approveColumnAnalysisChanges',
			type : 'POST',
			datatype: 'json',
			headers: { 'token':$("#token").val()},
			contentType: 'application/json',
			data : JSON.stringify(listDataDefinitionDelta_list),
			success : function(data) {
				if (data!="") {
					window.setTimeout(function(){
						updateTemplateStatus(idData,'approved');   
                    }, 1500);
					
				} else {
					$('#approval_msg_id').html("Approval of Column Delta changes failed: <b>reload the page.</b>")
				}
			},
			error : function(xhr, textStatus,
					errorThrown) {
				$('#approval_msg_id').html("Approval of Column Delta changes failed: <b>reload the page.</b>")
			}
		});
	});
	
	//When Column config changed - [Reject]
	$('#btnColConfigReject').click(function() {
		$('#colConfig_approval_div').addClass('hidden');
		$('#approval_msg_id').html("Revert of Column Delta changes is in progress. <p><b>Please wait ....</b></p>")

		var idData = $('#templateId').val();
		
		// Make API call to reject and revert to old column data
		$.ajax({
			url : './rejectColumnAnalysisChanges?idData='+idData,
			type : 'POST',
			headers: { 'token':$("#token").val()},
			datatype: 'json',
			contentType: 'application/json',
			success : function(data) {
				if (data!="") {
					window.setTimeout(function(){
						updateTemplateStatus(idData,'approved');   
                    }, 1500);
					
				} else {
					$('#approval_msg_id').html("Rejection of Column Delta changes failed: <b>reload the page.</b>")
				}
			},
			error : function(xhr, textStatus,
					errorThrown) {
				$('#approval_msg_id').html("Rejection of Column Delta changes failed: <b>reload the page.</b>")
			}
		});
		
    });
});

function updateTemplateStatus(idData,approvalStatus){
	var form_data = {
			idData : idData,
			approvalStatus : approvalStatus
	};
	$.ajax({
		url : './updateTemplateDeltaApprovalStatus',
		type : 'POST',
		headers: { 'token':$("#token").val()},
		datatype : 'json',
		data : form_data,
		success : function(data) {
			console.log("update status:"+data)
			 
			if (data!="") {
				window.location.reload();
			} else {
				$('#approval_msg_id').html("Template Delta Changes Status: <b>Failure to update status, reload the page.</b>")
				toastr.info('There was a problem.');
			}
		},
		error : function(xhr, textStatus,
				errorThrown) {
			$('#approval_msg_id').html("Template Delta Changes Status: <b>Failure to update status, reload the page.</b>")
		}
	});
}

$('label[id^="defaultPatterns_"]').html(function(index, text) {
	  return text.replace("val:", "").replace(/per:/g," ").replace(/val:/g,"<br>");
	});

</script>