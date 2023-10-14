
<%@page import="com.databuck.bean.ListApplications"%>
<%@page import="java.util.List"%>
<%@page import="com.databuck.bean.ListDataDefinition"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<style>
	/* #thrsholds_apply_option_container {
		float: right; 
		margin-top: -45px; 
		margin-right: 43px;	
	} */	
	#validation_tabs {
		width: 100%;
	}	
	
	#content_foundation_checks, #content_essential_checks, #content_advanced_checks {
		margin-top: 0px !important;
		margin-right: auto;
		padding-top: 50px;
		margin-left: auto;
		min-height: 200px;
		border-top: 2px solid var(--label-tabs-color);	
	}	
	
	.form .form-body, .portlet-form .form-body {
		 padding: 0px !important;
	}	
</style>
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
						<div class="caption font-red-sunglo" style="overflow-x:auto;">
							<span class="caption-subject bold "> Data Quality
								Fingerprint : ${listappName}</span>
						</div>
					</div>
					<!-- <div class="portlet-body form"> -->
					<!-- <form action="saveValidationCheckCustomizedData" method="POST"> -->
					<input type="hidden" id="idApp" name="idApp" value="${idApp}" /> <input
						type="hidden" id="stringStatStatus" value="${stringStatStatus}" />
					<input type="hidden" id="nullCountStatus"
						value="${nullCountStatus}" /> <input type="hidden"
						id="numericalStatsStatus" value="${numericalStatsStatus}" /> <input
						type="hidden" id="recordAnomalyStatus"
						value="${recordAnomalyStatus}" /> <input type="hidden"
						id="dataDriftStatus" value="${dataDriftStatus}" /> <input
						type="hidden" id="outofNormStatus" value="${outofNormStatus}" />
					<input type="hidden" id="recordCountAnomalyTypeStatus"
						value="${recordCountAnomalyTypeStatus}" /> <input type="hidden"
						id="recordCountAnomalyThresholdStatus"
						value="${recordCountAnomalyThresholdStatus}" /> <input
						type="hidden" id="applyRulesStatus" value="${applyRulesStatus}" />
					<input type="hidden" id="applyDerivedColumnsStatus"
						value="${applyDerivedColumnsStatus}" /> <input type="hidden"
						id="fileNameValStatus" value="${fileNameValStatus}" /> <input
						type="hidden" id="columnOrderValStatus"
						value="${columnOrderValStatus}" /> <input type="hidden"
						id="allDupRow" value="${allDupRow}" /> <input type="hidden"
						id="allThreshold" value="${allThreshold}" /> <input type="hidden"
						id="identityDupRow" value="${identityDupRow}" /> <input
						type="hidden" id="identityThreshold" value="${identityThreshold}" />
					<input type="hidden" id="dataLocation" value="${dataLocation}" />
					<input type="hidden" id="duplicateCheck" value="${duplicateCheck}" />
					<input type="hidden" id="reprofilingStatus" value="${reprofilingStatus}" />
					<input type="hidden" id="incrementalMatching"
						name="incrementalMatching" value="${incrementalMatching}" /> <input
						type="hidden" id="buildHistoricFingerPrint"
						value="${buildHistoricFingerPrint}" /> <input type="hidden"
						id="historicStartDate" name="historicStartDate"
						value="${historicStartDate}" /> <input type="hidden"
						id="historicEndDate" name="historicEndDate"
						value="${historicEndDate}" /> <input type="hidden"
						id="historicDateFormat" name="historicDateFormat"
						value="${historicDateFormat}" /> <input type="hidden" id="csvDir"
						name="csvDir" value="${csvDir}" /> <input type="hidden"
						id="groupEqualityStatus" name="groupEqualityStatus"
						value="${groupEquality}" /> <input type="hidden"
						id="groupEqualityThreshold" name="groupEqualityThreshold"
						value="${groupEqualityThreshold}" /><input
						type="hidden" id="timelinessKeyStatus" value="${timelinessKeyStatus}" />
						<input type="hidden" id="defaultCheckStatus" value="${defaultCheckStatus}" />
						<input type="hidden" id="patternCheckStatus" value="${patternCheckStatus}" />
						<input type="hidden" id="defaultPatternCheckStatus" value="${defaultPatternCheckStatus}" />
						<!-- sumeet -->
						<input type="hidden" id="badDataStatus" value="${badDataStatus}" />
						<input type="hidden" id="lengthCheckStatus" value="${lengthCheckStatus}" />
						
						<input type="hidden" id="maxLengthCheckStatus" value="${maxLengthCheckStatus}" /> 
						
						<!-- changes for daterulechk 8jan2019 priyanka -->
						<input type="hidden" id="dateRuleChkStatus" value="${dateRuleChkStatus}" />
						<input type="hidden" id="dGroupNullCheckStatus" value="${dGroupNullCheckStatus}" /> 
						<input type="hidden" id="dGroupDateRuleCheckStatus" value="${dGroupDateRuleCheckStatus}" /> 
						<input type="hidden" id="dGroupDataDriftCheckStatus" value="${dGroupDataDriftCheckStatus}" />
						
					<div id='validation_tabs' class='jwf-label-tabs-bar'>
						<label id='lbl_select_foundation' data-selected='true'>Foundation Checks</label>
						<label id='lbl_select_essential' data-selected='false'>Essential Checks</label>
						<label id='lbl_select_advanced' data-selected='false'>Advanced Checks</label>						
					</div>
					<div class="form-body">
						<%-- <c:forEach items="${getdatafromlistdftranrule}"
								var="getdatafromlistdftranrule">
								<div class="row non-schema-matching">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control catch-error"
												id="thresholdAll" name="thresholdAll"
												value="${getdatafromlistdftranrule.key}">
											<label for="form_control_1">Threshold for Duplicate
												Rows for all fields *</label>
											<!--  <span class="help-block">Data Set Name </span> -->
										</div>
										<br /> <span class="required"></span>
									</div>

									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control catch-error"
												id="thresholdIdentity" name="thresholdIdentity"
												value="${getdatafromlistdftranrule.value}">
											<label for="form_control_1">Threshold for Duplicate
												Rows for primary key fields *</label>
											<!--  <span class="help-block">Data Set Name </span> -->
										</div>
										<br /> <span class="required"></span>
									</div>
								</div>
							</c:forEach> --%>


						<div class="portlet-body form">

							<div class="form-body">
								<input type="hidden" id="idApp" name="idApp" value="${idApp}" />
								<input type="hidden" id="idData" name="idData" value="${idData}" />
								<%
										Object obj = request.getAttribute("listApplicationsData");
										ListApplications listapplications = (ListApplications) obj;
										//out.println(la);
									%>
								<c:forEach items="${listDataDefinitionData}"
									var="listDataDefinitionData">
									
									<div id = "content_foundation_checks" class="foundationChecks">

									<div class="row">
										<div class="col-md-12">
											<div class="col-md-1"></div>
											<div class="col-md-5 col-capture">
												<div class="form-group form-md-line-input">
													<select name="ApplicationType" id="ApplicationType"
														class="form-control">
														<option value="Normal">Bulk Load</option>
														<option value="Historic">Historic</option>
														<option value="Ongoing">Incremental</option>
													</select> <label for="ApplicationType">Choose Type of
														Application</label>
												</div>
											</div>

											<div class="col-md-5 col-capture"
												style="padding-bottom: 20px">
												<div class="form-group form-md-line-input">
													<input type="text" class="form-control" id="dateformatid"
														name="dateformatid" value="${historicDateFormat}"><label
														class="form_control_1">Date Format</label>
												</div>
											</div>
										</div>
									</div>
									<div class="row hidden" id="dateDivId" style="padding-bottom: 20px">
										<div class="col-md-12">
											<div class="col-md-1"></div>
											<div class="col-md-5 col-capture">
												<label class="form_control_1">Start Date</label>
												<div class="input-group input-medium">
													<input type="text" class="form-control" readonly
														id="startdateid" name="startdateid"
														value="${historicStartDate}"> <span
														class="input-group-btn">
														<label class="input-group-btn" for="startdateid">
															<span class="btn default">
																<i class="fa fa-calendar"></i>
															</span>
														</label>
													</span>
												</div>
											</div>
											<div class="col-md-6 col-capture">
												<label class="form_control_1">End Date</label>
												<div class="input-group input-medium">
													<input type="text" class="form-control" readonly
														id="enddateid" name="enddateid" value="${historicEndDate}">
													<label class="input-group-btn" for="enddateid">
														<span class="btn default">
															<i class="fa fa-calendar"></i>
														</span>
													</label>
												</div>
											</div>
										</div>
									</div>
									<%-- <div class="row">
											<div class="col-md-6" style="padding-left: 100px;">
												<div class="form-group form-md-line-input">
													<label class="form_control_1">Date Format</label> <input
														type="text" class="form-control" id="dateformatid"
														name="dateformatid" value="${historicDateFormat}">
												</div>
											</div>
										</div> --%>
									<br />
									<div class="row">
									<div class="col-md-12">
										<div class="col-md-1">
											<div class="form-group form-md-checkboxes">
												<div class="md-checkbox-list">
													<div class="md-checkbox">
														<!-- <input type="checkbox" id="recordCount" name="recordCount"
															class="md-check" value="Y"> <label
															for="recordCount"> <span></span> <span
															class="check"></span> <span class="box"></span></label> -->
													</div>
												</div>
											</div>
										</div>
										<div class="col-md-5 col-capture">
											<div class="form-group form-md-line-input">
												<select name="recordCountAnomalyType"
													id="recordCountAnomalyType"
													class="form-control recordCountAnomaly" disabled="true">
													<option value='RecordCountAnomaly'>Record Count
														Anomaly</option>
													<option value='KeyBasedRecordCountAnomaly'>Microsegment Based Record Anomaly</option>
												</select> <label for="recordCountAnomalyType">Choose Type of
													Record Count Anomaly</label>
											</div>
										</div>
										<div class="col-md-5 col-capture">
											<div class="form-group form-md-line-input">
												<input type="text" class="form-control"
													id="DFSetComparisonId" name="DFSetComparisonId"
													value="${recordCountAnomalyThresholdStatus}"> <label
													for="recordCountText">Record Count Anomaly</label>
											</div>
										</div>
									</div>
								    </div>
										
									<div class="row">
									<div class="col-md-12" style="padding-top: 50px;">	
										<div class="col-md-1"></div>
											<div class="col-md-5 col-capture">
												<div class="form-group form-md-line-input">
													<input type="text" class="form-control"
														id="validityThresholdId" name="validityThresholdId"
														value="1.0"> <label for="validityThresholdText">Validity
														Threshold</label>
												</div>
											</div>

											<div class="col-md-6 col-capture" style="">
												<div class="form-group form-md-line-input">
													<select name="frequencyid" id="frequencyid"
														class="form-control selectfrequency">
														<option value="Never">Never</option>
														<option value="Daily">Daily</option>
														<option value="EveryDay">Every 'x' Days</option>
													</select> <br /> <label for="form_control_1">Update
														Frequency</label>
												</div>
											 </div>
											
											 <div class="col-md-6 col-capture hidden" id="EveryDay" style="padding-left: 61px;">
													<div class="form-group form-md-checkboxes">
														<div class="form-group form-md-line-input">
															<input type="text" class="form-control"
																id="EveryDaytext" name="EveryDaytext"
																value="${frequencyDays}"> <label for="EveryDay">Every
																'x' Days</label>
														</div>
													</div>
											  </div>
									</div>
									</div>
								
							
								
								
									<div class="row" id="groupEqualitydiv">
										<div class="col-md-12">
											<div class="col-md-1" style="padding-top: 50px;">
												<div class="form-group form-md-checkboxes">
													<div class="md-checkbox-list">
														<div class="md-checkbox">
															<input type="checkbox" id="groupEquality"
																name="groupEquality" class="md-check" value="Y">
															<label for="groupEquality"> <span></span> <span
																class="check"></span> <span class="box"></span></label>
														</div>
													</div>
												</div>
											</div>
											<div class="col-md-5 col-capture" style="padding-top: 50px;">
												<div class="form-group form-md-line-input">
													<input type="text" class="form-control"
														id="groupEqualityText" name="groupEqualityText"
														value="${groupEqualityThreshold}"> <label
														for="groupEquality">Group Equality</label>
												</div>
											</div>
											
											<div id="thrsholds_apply_option_container" class="col-md-6 col-capture" style="padding-top: 50px;">
												<div class="form-group form-md-line-input">
													<select name="thresholds_apply_option" id="thresholds_apply_option"	class="form-control"></select> <br /> 
													<label for="thresholds_apply_option" style="font-weight:bold; color:black;">Apply Threshold Values As:</label>
												</div>
											</div>
										</div>
									</div>
									
									
									
								
								
									<div class="row">
									<div class="col-md-12">
										<div class="col-md-1"></div>
										<div class="col-md-5">
										<label style="padding-top: 20px;"> Data Cyclicality</label><br />
											<div class="form-group form-md-checkboxes">
												<div class="md-checkbox-list" style="padding-left: 5px;">
													<div class="md-checkbox">
														<input type="checkbox" id="none" name="check_id"
															class="md-check" value="None"> <label for="none">
															None<span></span> <span class="check"></span> <span
															class="box"></span>
														</label>
													</div>
												</div>
											</div>
										</div>
										
										<!-- Data Domain -->
										
										 <div class="col-md-5 col-capture" style="padding-top: 20px;">
											<div class="form-group form-md-line-input">
													<%-- <select class="form-control" id="data_domain"
													name="data_domain" disabled="true">
													<option value="${data_domain}">${data_domain}</option>
													</select> <label for="recordCountAnomalyType">Data Domain</label> --%>
													<select class="form-control" id="data_domainId" name="data_domainId">
													<c:forEach var="listDataDomainObj" items="${lstDataDomain}">
	
														<option value="${listDataDomainObj.row_id}">${listDataDomainObj.name}</option>
	
	
													</c:forEach>
													</select> <label for="data_domainId">Data Domain</label>
												</div>
										</div>
									</div>
									</div>
									<!-- END OF DATA DOMAIN -->	
									
									
							
							<div id="noneid" style="padding-left: 105px;">
								<div class="row">
									<div class="col-md-12">
										<div class="col-md-4">
											<div class="form-group form-md-checkboxes">
												<div class="md-checkbox-list">
													<div class="md-checkbox">
														<input type="checkbox" id="chkmonth" name="check_id"
															class="md-check" value="month"> <label
															for="chkmonth"> Month<span></span> <span
															class="check"></span> <span class="box"></span>
														</label>
													</div>
												</div>
											</div>
										</div>
										<!-- <div class="col-md-4 hidden divmonth">
											<div class="form-group form-md-line-input">
												<select name="monthdropdown" id="monthdropdown" class="form-control month">
													<option >Choose Month</option>
													<option  value='January'>January</option>
													<option value='February'>February</option>
													<option value='March'>March</option>
													<option value='April'>April</option>
													<option value='May'>May</option>
													<option value='June'>June</option>
													<option value='July'>July</option>
													<option value='August'>August</option>
													<option value='September'>September</option>
													<option value='October'>October</option>
													<option value='November'>November</option>
													<option value='December'>December</option>
												</select>
											</div></div> -->
									</div>
								</div>
								<div class="row">
									<div class="col-md-12">
										<div class="col-md-4">
											<div class="form-group form-md-checkboxes">
												<div class="md-checkbox-list">
													<div class="md-checkbox">
														<input type="checkbox" id="chkDayOfWeek" name="check_id"
															class="md-check" value="dayOfWeek"> <label
															for="chkDayOfWeek"> Day of Week<span></span> <span
															class="check"></span> <span class="box"></span>
														</label>
													</div>
												</div>
											</div>
										</div>

										<!--  <div class="col-md-4 hidden divDayOfWeek">
                                                <div class="col-md-6 col-capture">
                                                <div class="form-group form-md-line-input">
                                                <select name="dowdropdown" id="dowdropdown" class="form-control dayOfWeek">
                                                <option selected>Choose Day</option>
                                                    <option value="Monday">Monday</option>
                                                    <option value="Tuesday">Tuesday</option>
                                                    <option value="Wednesday">Wednesday</option>
                                                    <option value="Thursday">Thursday</option>
                                                    <option value="Friday">Friday</option>
                                                    <option value="Saturday">Saturday</option>
                                                    <option value="Sunday">Sunday</option>
                                                </select>
                                            </div></div> -->
									</div>
								</div>
								<div class="row">
									<div class="col-md-12">
										<div class="col-md-4">
											<div class="form-group form-md-checkboxes">
												<div class="md-checkbox-list">
													<div class="md-checkbox">
														<input type="checkbox" id="chkHourOfDay" name="check_id"
															class="md-check" value="hourOfDay"> <label
															for="chkHourOfDay"> Hour of Day<span></span> <span
															class="check"></span> <span class="box"></span>
														</label>
													</div>
												</div>
											</div>
										</div>
										<!-- <div class="col-md-4 hidden divHourOfDay">
												<div class="col-md-6 col-capture">
												<div class="form-group form-md-line-input">
													<select name="hoddropdown" id="hoddropdown" class="form-control hourofDay">
														<option selected>Choose Hour</option>
														<option value="1">1</option>
														<option value="2">2</option>
														<option value="3">3</option>
														<option value="4">4</option>
														<option value="5">5</option>
														<option value="6">6</option>
														<option value="7">7</option>
														<option value="8">8</option>
														<option value="9">9</option>
														<option value="10">10</option>
														<option value="11">11</option>
														<option value="12">12</option>
														<option value="13">13</option>
														<option value="14">14</option>
														<option value="15">15</option>
														<option value="16">16</option>
														<option value="17">17</option>
														<option value="18">18</option>
														<option value="19">19</option>
														<option value="20">20</option>
														<option value="21">21</option>
														<option value="22">22</option>
														<option value="23">23</option>
														<option value="24">24</option>
													</select> <br /> <label for="form_control_1">Choose Day</label>
												</div>
											</div> -->
									</div>
								</div>
								<div class="row">
									<div class="col-md-12">
										<div class="col-md-4">
											<div class="form-group form-md-checkboxes">
												<div class="md-checkbox-list">
													<div class="md-checkbox">
														<input type="checkbox" id="chkDayOfMonth" name="check_id"
															class="md-check" value="dayOfMonth"> <label
															for="chkDayOfMonth"> Day of Month<span></span> <span
															class="check"></span> <span class="box"></span>
														</label>
													</div>
												</div>
											</div>
										</div>
										<!-- <div class="col-md-4 hidden divDayOfMonth">
												<div class="col-md-6 col-capture">
												<div class="form-group form-md-line-input">
													<select name="domdropdown" id="domdropdown" class="form-control dayOfMonth">
														<option selected>Choose Date</option>
														<option value="1">1</option>
														<option value="2">2</option>
														<option value="3">3</option>
														<option value="4">4</option>
														<option value="5">5</option>
														<option value="6">6</option>
														<option value="7">7</option>
														<option value="8">8</option>
														<option value="9">9</option>
														<option value="10">10</option>
														<option value="11">11</option>
														<option value="12">12</option>
														<option value="13">13</option>
														<option value="14">14</option>
														<option value="15">15</option>
														<option value="16">16</option>
														<option value="17">17</option>
														<option value="18">18</option>
														<option value="19">19</option>
														<option value="20">20</option>
														<option value="21">21</option>
														<option value="22">22</option>
														<option value="23">23</option>
														<option value="24">24</option>
														<option value="25">25</option>
														<option value="26">26</option>
														<option value="27">27</option>
														<option value="28">28</option>
														<option value="29">29</option>
														<option value="30">30</option>
														<option value="31">31</option>
													</select> <br /> <label for="form_control_1">Choose Day</label>
												</div>
											</div> -->
									</div>
								</div>
							</div>
								<br>
                                  <div class="col-md-1" style="padding-top: 10px;">
                                     <div class="form-group form-md-checkboxes">
                                         <div class="md-checkbox-list">
                                             <div class="md-checkbox">
                                                 <input type="checkbox" id="reprofiling"
                                                     name="reprofiling" class="md-check" value="Y">
                                                 <label for="reprofiling"> <span></span> <span
                                                     class="check"></span> <span class="box"></span></label>
                                             </div>
                                         </div>
                                     </div>
                                 </div>
                                 <div class="col-md-5 col-capture">
                                         <div class="form-group form-md-line-input">
                                             <label for="reprofiling">Reprofiling</label>
                                         </div>
                                 </div>
                                 <br><br>
							</div>


								
							<div id = "content_essential_checks" class="essentialChecks">
									<div class="row">
										<div class="col-md-12">
											<div class="col-md-1 hidden">
												<div class="form-group form-md-checkboxes">
													<div class="md-checkbox-list">
														<div class="md-checkbox">
															<input type="checkbox" id="stringStat" name="stringStat"
																class="md-check" value="Y"> <label
																for="stringStat"> <span></span> <span
																class="check"></span> <span class="box"></span></label>
														</div>
													</div>
												</div>
											</div>
											<div class="col-md-5 col-capture hidden">
												<div class="form-group form-md-line-input">
													 <input type="text" class="form-control" id="stringStatText"
													name="stringStatText" value="${listDataDefinitionData.stringStatThreshold}">
													<label for="stringStatText">String Field Stat and
														Comparison</label>
												</div>
											</div>
											<div class="col-md-1">
												<div class="form-group form-md-checkboxes">
													<div class="md-checkbox-list">
														<div class="md-checkbox">
															<input type="checkbox" id="nullCount" name="nullCount"
																class="md-check" value="Y"> <label
																for="nullCount"> <span></span> <span
																class="check"></span> <span class="box"></span></label>
														</div>
													</div>
												</div>
											</div>
											<div class="col-md-5 col-capture">
												<div class="form-group form-md-line-input">
													<input type="text" class="form-control" id="nullCountText"
													name="nullCountText" value="${listDataDefinitionData.nullCountThreshold}"> 
													<label for="nullCountText">Null Count of Non-Null
														Fields</label>
												</div>
											</div>
											
											<div class="col-md-1" style="padding-top: 10px;">
													<div class="form-group form-md-checkboxes">
														<div class="md-checkbox-list">
															<div class="md-checkbox">
																<input type="checkbox" id="dGroupNullCheck"
																	name="dGroupNullCheck" class="md-check" value="Y">
																<label for="dGroupNullCheck"> <span></span> <span
																	class="check"></span> <span class="box"></span></label>
															</div>
														</div>
													</div>
												</div>
												<div class="col-md-5 col-capture">
													<div class="form-group form-md-line-input">
														<label for="dGroupNullCheck">Microsegment Null
															Check</label>
													</div>
											</div>
										</div>
									</div>
									<br/><br/>
									<div class="row" id="dataDriftDiv">
										<div class="col-md-12">
											<div class="col-md-1">
												<div class="form-group form-md-checkboxes">
													<div class="md-checkbox-list">
														<div class="md-checkbox">


															<input type="checkbox" id="dataDriftCheck"
																name="dataDriftCheck" class="md-check" value="Y">


															<!-- <input type="checkbox" id="dataDriftCheck"
														name="dataDriftCheck" class="md-check" value="Y"> -->
															<label for="dataDriftCheck"> <span></span> <span
																class="check"></span> <span class="box"></span></label>
														</div>
													</div>
												</div>
											</div>
											<div class="col-md-5 col-capture">
												<div class="form-group form-md-line-input">
													<input type="text" class="form-control"
															id="dataDriftCheckText" name="dataDriftCheckText"
															value="${listDataDefinitionData.dataDriftThreshold}"> 
													<label for="dataDriftCheck">Data Drift Check</label>
												</div>
											</div>

											<!-- <div class="col-md-1">
													<div class="form-group form-md-checkboxes">
														<div class="md-checkbox-list">
															<div class="md-checkbox">

																<input type="checkbox" id="outofNorm" name="outofNorm"
																	class="md-check" value="Y">

																<label for="outofNorm"> <span></span> <span
																	class="check"></span> <span class="box"></span></label>
															</div>
														</div>
													</div>
												</div>
												<div class="col-md-5 col-capture">
													<div class="form-group form-md-line-input">
														<input type="text" class="form-control"
														id="outofNormThreshold" name="outofNormThreshold">
														<label for="outofNormThreshold">Out of Norm</label>
													</div>
												</div> -->


											
											<%-- <div class="row">
										<div class="col-md-12">
										<div class="col-md-1">
											<div class="form-group form-md-checkboxes">
												<div class="md-checkbox-list">
													<div class="md-checkbox">
														<input type="checkbox" id="duplicateCountAll"
															name="duplicateCountAll" class="md-check" value="Y">
														<label for="duplicateCountAll"> <span></span> <span
															class="check"></span> <span class="box"></span></label>
													</div>
												</div>
											</div>
										</div>
										<div class="col-md-5 col-capture">
											<div class="form-group form-md-line-input">
												<input type="text" class="form-control"
													id="duplicateCountAllText" name="duplicateCountAllText" value="${allThreshold}">
												<label for="duplicateCountAllText">Duplicate rows
													based on all fields</label>
											</div>
										</div></div>
										</div> --%>
										
										
										<div class="col-md-1">
														<div class="form-group form-md-checkboxes">
													<div class="md-checkbox-list">
														<div class="md-checkbox">


															<input type="checkbox" id="defaultCheck"
																name="defaultCheck" class="md-check" value="Y">

															<label for="defaultCheck"> <span></span> <span
																class="check"></span> <span class="box"></span></label>
														</div>
													</div>
												</div>
											</div>
											<div class="col-md-5 col-capture">
												<div class="form-group form-md-line-input">
													<input type="text" class="form-control"
														id="defaultValues" name="defaultValues"
														value="${listDataDefinitionData.defaultValues}"> <label
														for="startDateText">Default Check (Enter Default Values Below)</label>
												</div>
											</div>
										</div>
									</div>
									<br/><br/>
									
									<div class="row" style="padding-top: 20px;">
										<div class="col-md-12">
											<div class="col-md-1" style="padding-top: 10px;">
												<div class="form-group form-md-checkboxes">
													<div class="md-checkbox-list">
														<div class="md-checkbox">

															<input type="checkbox" id="duplicateCount"
																name="duplicateCount" class="md-check" value="Y">

															<label for="duplicateCount"> <span></span> <span
																class="check"></span> <span class="box"></span></label>
														</div>
													</div>
												</div>
											</div>
											<div class="col-md-5 col-capture">
												<div class="form-group form-md-line-input">
													<input type="text" class="form-control"
														id="duplicateCountText" name="duplicateCountText"
														value="${identityThreshold}"> <label
														for="duplicateCountText">Duplicate Rows Based on
														Identity Fields</label>
												</div>
											</div>
											<div class="col-md-1" style="padding-top: 10px;">
												<div class="form-group form-md-checkboxes">
													<div class="md-checkbox-list">
														<div class="md-checkbox">

															<input type="checkbox" id="duplicateCountAll"
																name="duplicateCountAll" class="md-check" value="Y">


															<label for="duplicateCountAll"> <span></span> <span
																class="check"></span> <span class="box"></span></label>
														</div>
													</div>
												</div>
											</div>
											<div class="col-md-5 col-capture">
												<div class="form-group form-md-line-input">
													<input type="text" class="form-control"
														id="duplicateCountAllText" name="duplicateCountAllText"
														value="${allThreshold}"> <label
														for="duplicateCountText">Duplicate Rows Based on
														All Fields</label>
												</div>
											</div>
										</div>
									</div>
								
								<div class="row" style="padding-top: 20px;">
							    <div class="col-md-12">
									<div class="col-md-1" style="padding-top: 10px;">
										<div class="form-group form-md-checkboxes">
											<div class="md-checkbox-list">
												<div class="md-checkbox">
													<input type="checkbox" id="dupCheck" name="dupCheck"
														class="md-check" value="Y"> <label
														for="dupCheck"> <span></span> <span
														class="check"></span> <span class="box"></span></label>
												</div>
											</div>
										</div>
									</div>
									<div class="col-md-5 col-capture">
										<div class="form-group form-md-line-input">
										<input type="text" class="form-control"placeholder="Number of Rows" id="numberofRows" name="numberofRows">
											<label for="dupCheck">Record Count Duplicate Check</label>
										</div>
									</div>
									
									<div class="col-md-1" style="padding-top: 10px;">
													<div class="form-group form-md-checkboxes">
														<div class="md-checkbox-list">
															<div class="md-checkbox">
																<input type="checkbox" id="badData" name="badData"
																	class="md-check" value="Y"> <label
																	for="badData"> <span></span> <span
																	class="check"></span> <span class="box"></span></label>
															</div>
														</div>
													</div>
												</div>
								        <div class="col-md-5 col-capture">
													<div class="form-group form-md-line-input">
														<label for="applyRules">Bad Data Check</label>
													</div>
										</div>
							</div>
							</div>
							
							<div class="row" style="padding-top: 20px;">
											<div class="col-md-12">
												<div class="col-md-1" style="padding-top: 10px;">
													<div class="form-group form-md-checkboxes">
														<div class="md-checkbox-list">
															<div class="md-checkbox">
																<input type="checkbox" id="patternCheck"
																	name="patternCheck" class="md-check" value="Y">
																<label for="patternCheck"> <span></span> <span
																	class="check"></span> <span class="box"></span></label>
															</div>
														</div>
													</div>
												</div>
												<div class="col-md-5 col-capture">
													<div class="form-group form-md-line-input">
														<!--  <input type="text" class="form-control"placeholder="Number of Rows" id="numberofRows" name="numberofRows"> -->
														<label for="applyRules">Regex Pattern Check</label>
													</div>
												</div>
												<div class="col-md-1" style="padding-top: 10px;">
													<div class="form-group form-md-checkboxes">
														<div class="md-checkbox-list">
															<div class="md-checkbox">
																<input type="checkbox" id="defaultPatternCheck"
																	name="defaultPatternCheck" class="md-check" value="Y">
																<label for="defaultPatternCheck"> <span></span>
																	<span class="check"></span> <span class="box"></span></label>
															</div>
														</div>
													</div>
												</div>
												<div class="col-md-5 col-capture">
													<div class="form-group form-md-line-input">
														<!--  <input type="text" class="form-control"placeholder="Number of Rows" id="numberofRows" name="numberofRows"> -->
														<label for="defaultPatternCheck">Default Pattern
															Check</label>
													</div>
												</div>
											</div>
										</div>
									<!-- sumeet -->
									<!-- Changes Added Regarding Length Check 28Dec2018 priyanka -->
								<div class="row" style="padding-top: 20px;">
								<div class="col-md-12">
									<div class="col-md-1" style="padding-top: 10px;">
										<div class="form-group form-md-checkboxes">
											<div class="md-checkbox-list">
												<div class="md-checkbox">
													<input type="checkbox" id="lengthCheck"
														name="lengthCheck" class="md-check" value="Y">
													<label for="lengthCheck"> <span></span> <span
														class="check"></span> <span class="box"></span></label>
												</div>
											</div>
										</div>
									</div>
									<div class="col-md-5 col-capture">
											<div class="form-group form-md-line-input">
												<label for="lengthCheck">Length Check</label>
											</div>
									</div> 
									<!--Max Length Check  -->
									<div class="col-md-1" style="padding-top: 10px;">
										<div class="form-group form-md-checkboxes">
											<div class="md-checkbox-list">
												<div class="md-checkbox">
													<input type="checkbox" id="maxLengthCheck"
														name="maxLengthCheck" class="md-check" value="Y">
													<label for="maxLengthCheck"> <span></span> <span
														class="check"></span> <span class="box"></span></label>
												</div>
											</div>
										</div>
									</div>
									<div class="col-md-5 col-capture">
											<div class="form-group form-md-line-input">
												<label for="maxLengthCheck">Max Length Check</label>
											</div>
									</div> 
									<!-- End of Max Length Check -->
									</div>
									</div> 
									<div class="row" style="padding-top: 20px;">
											<div class="col-md-12">
												<div class="col-md-1" style="padding-top: 10px;">
													<div class="form-group form-md-checkboxes">
														<div class="md-checkbox-list">
															<div class="md-checkbox">
																<input type="checkbox" id="dateRuleCheck"
																	name="dateRuleCheck" class="md-check" value="Y">
																<label for="dateRuleCheck"> <span></span> <span
																	class="check"></span> <span class="box"></span></label>
															</div>
														</div>
													</div>
												</div>
												<div class="col-md-5 col-capture">
													<div class="form-group form-md-line-input">
														<!-- <input type="text" class="form-control"
												id="dateRuleText" name="dateRuleText"> -->
														<label for="applyRules">Date Rule Check</label>
													</div>
												</div>
												
												<div class="col-md-1" style="padding-top: 10px;">
													<div class="form-group form-md-checkboxes">
														<div class="md-checkbox-list">
															<div class="md-checkbox">
																<input type="checkbox" id="dGroupDateRuleCheck"
																	name="dGroupDateRuleCheck" class="md-check" value="Y">
																<label for="dGroupDateRuleCheck"> <span></span>
																	<span class="check"></span><span class="box"></span></label>
															</div>
														</div>
													</div>
												</div>
												<div class="col-md-5 col-capture">
													<div class="form-group form-md-line-input">
														<label for="dGroupDateRuleCheckLabel">Microsegment
															DateRule Check</label>
													</div>
												</div>
												
											</div>
										</div>
									
									
										<div class="row" style="padding-top: 20px;" >
											<div class="col-md-12">
												

												<div class="col-md-1" style="padding-top: 10px;">
													<div class="form-group form-md-checkboxes">
														<div class="md-checkbox-list">
															<div class="md-checkbox">
																<input type="checkbox" id="dGroupDataDriftCheck"
																	name="dGroupDataDriftCheck" class="md-check" value="Y">
																<label for="dGroupDataDriftCheck"> <span></span>
																	<span class="check"></span> <span class="box"></span>
																</label>
															</div>
														</div>
													</div>
												</div>
												<div class="col-md-5 col-capture">
													<div class="form-group form-md-line-input">
														<label for="dGroupDataDriftCheckLabel">MicroSegment
															Based DataDrift</label>
													</div>
												</div>
											</div>
										</div>
								
							<span id="appCustomization" value="yes"> <input
								type="hidden" value="${month}" id="month" /> <input
								type="hidden" value="${dayOfWeek}" id="dayOfWeek" /> <input
								type="hidden" value="${hourOfDay}" id="hourOfDay" /> <input
								type="hidden" value="${dayOfMonth}" id="dayOfMonth" /> <input
								type="hidden" value="${None}" id="noneStatus" /> <input
								type="hidden" value="${updateFrequency}" id="updateFrequency" />
								<input type="hidden" value="${frequencyDays}" id="frequencyDays" />
							</span> 

						</div>

	
					
									<div class="row hidden" hidden="true" >
										<div class="col-md-12" style="padding-left: 119px">
											<div class="col-md-6 col-capture" >
												<div class="form-group form-md-line-input">
													<input type="text" class="form-control"
														id="nameofEntityColumn" name="nameofEntityColumn"
														value="${entityColumn}"> <label
														for="nameofEntityColumn">Name of Entity Column</label>
												</div>
											</div>
											<div class="col-md-6 col-capture" style="padding-left: 66px">
												<div class="form-group form-md-line-input">
													<input type="text" class="form-control" id="csvDirectory"
														name="csvDirectory" value="${csvDir}"> <label
														for="csvDirectory">CSV Directory</label>
												</div>
											</div>
										</div>
									</div>
									
									
									<div class="hidden fileSystemdiv" hidden="true" >
										<div class="row hidden" hidden="true" >
											<div class="col-md-12">
												<div class="col-md-1">
													<div class="form-group form-md-checkboxes">
														<div class="md-checkbox-list">
															<div class="md-checkbox">
	
																<input type="checkbox" id="fileNameVal"
																	name="fileNameVal" class="md-check" value="Y"> <label
																	for="fileNameVal"> <span></span> <span
																	class="check"></span> <span class="box"></span></label>
															</div>
														</div>
													</div>
												</div>
												<div class="col-md-5 col-capture">
													<div class="form-group form-md-line-input">
														<!--  <input type="text" class="form-control"placeholder="Number of Rows" id="numberofRows" name="numberofRows"> -->
														<label for="numberofRows">File Name Validation</label>
													</div>
												</div>
												<div class="col-md-1">
													<div class="form-group form-md-checkboxes">
														<div class="md-checkbox-list">
															<div class="md-checkbox">
	
																<input type="checkbox" id="columnOrderVal"
																	name="columnOrderVal" class="md-check" value="Y">
																<label for="columnOrderVal"> <span></span> <span
																	class="check"></span> <span class="box"></span></label>
															</div>
														</div>
													</div>
												</div>
												<div class="col-md-5 col-capture">
													<div class="form-group form-md-line-input">
														<label for="columnOrderVal">Column Order Validation</label>
													</div>
												</div>
											</div>
										</div>										
									</div>									
							  </div>
				
							  <div id = "content_advanced_checks" class="advancedChecks">
							 
									<div class="row">
										<div class="col-md-12">
											<div class="col-md-1" style="padding-top: 10px;">
												<div class="form-group form-md-checkboxes">
													<div class="md-checkbox-list">
														<div class="md-checkbox">
															<input type="checkbox" id="numericalStats"
																name="numericalStats" class="md-check" value="Y">
															<label for="numericalStats"> <span></span> <span
																class="check"></span> <span class="box"></span></label>
														</div>
													</div>
												</div>
											</div>
											<div class="col-md-5 col-capture">
												<div class="form-group form-md-line-input">
													<!-- <input type="text" class="form-control"
													id="numericalStatsText" name="numericalStatsText" value="${listDataDefinitionData.numericalThreshold}">
													<label for="numericalStatsText">Numerical Field
														Stats and Comparison</label> -->
                                                  <label for="numericalStats">Distribution Check</label> 
												</div>
											</div>

											<div class="col-md-1" style="padding-top: 10px;">
												<div class="form-group form-md-checkboxes">
													<div class="md-checkbox">
														<input type="checkbox" id="recordAnomalyid"
															name="recordAnomalyid" class="md-check" value="Y">
														<label for="recordAnomalyid"> <span></span> <span
															class="check"></span> <span class="box"></span></label>
													</div>
												</div>
											</div>
											<div class="col-md-5 col-capture">
												<div class="form-group form-md-line-input">
													<!--  <input type="hidden" class="form-control"
													id="recordAnomalyThreshold"
													name="recordAnomalyThresholdId" value="${listDataDefinitionData.recordAnomalyThreshold}">  -->
													<label for="recordAnomalyThresholdId">Record
														Anomaly</label>
												</div>
											</div>
										</div>
									</div>

									<br />
									<div class="row">
									<div class="col-md-12">
										<div class="col-md-1" style="padding-top: 10px;">
											<div class="form-group form-md-checkboxes">
												<div class="md-checkbox-list">
													<div class="md-checkbox">
														<input type="checkbox" id="applyRules" name="applyRules"
															class="md-check" value="Y"> <label
															for="applyRules"> <span></span> <span
															class="check"></span> <span class="box"></span></label>
													</div>
												</div>
											</div>
										</div>
										<div class="col-md-5 col-capture">
											<div class="form-group form-md-line-input">
												<!--  <input type="text" class="form-control"placeholder="Number of Rows" id="numberofRows" name="numberofRows"> -->
												<label for="applyRules">Apply Rules</label>
											</div>
										</div>
										<div class="col-md-1" style="padding-top: 10px;">
											<div class="form-group form-md-checkboxes">
												<div class="md-checkbox-list">
													<div class="md-checkbox">

														<input type="checkbox" id="applyDerivedColumns"
															name="applyDerivedColumns" class="md-check" value="Y">

														<label for="applyDerivedColumns"> <span></span> <span
															class="check"></span> <span class="box"></span></label>
													</div>
												</div>
											</div>
										</div>
										<div class="col-md-5 col-capture">
											<div class="form-group form-md-line-input">
												<!--  <input type="text" class="form-control"placeholder="Number of Rows" id="numberofRows" name="numberofRows"> -->
												<label for="applyDerivedColumns">Apply Derived
													Columns</label>
											</div>
										</div>
									</div>
								</div>
									
								<br />
									
									<div class="row" >
										<div class="col-md-12">
											<div class="col-md-1" style="padding-top: 10px;">
												<div class="form-group form-md-checkboxes">
													<div class="md-checkbox-list">
														<div class="md-checkbox">

															<input type="checkbox" id="timelinessKey"
																name="timelinessKey" class="md-check" value="Y">

															<label for="timelinessKey"> <span></span> <span
																class="check"></span> <span class="box"></span></label>
														</div>
													</div>
												</div>
											</div>
											<div class="col-md-5 col-capture">
												<div class="form-group form-md-line-input">
													<!--  <input type="text" class="form-control"
														id="timelinessKeyText" name="timelinessKeyText"
														value="${timelinessKeyThreshold}">   -->
												   <label
														for="startDateText">Timeliness Check</label>
												</div>
											</div>
											
										</div>
									</div>	
									
							</div>
								</c:forEach>
							</div>
							
							
					<div id="configProblem" class=" hidden">
						<div class="alert alert-warning">
						<h5 style="color:red" id="jsonmsg"></h5>
							<h5 style="color:red">
								&nbsp&nbsp&nbsp&nbsp Please make sure that relevant columns have
								been selected in data template for your validation check. <a onClick="validateHrefVulnerability(this)"
									href="dataSourceDisplayAllView?idData=${idData}"
									target="_blank">Click here</a> to make the changes.
							</h5>
						</div>
						</div>
						<div id="rulesProblem" class=" hidden">
						<div class="alert alert-warning">
						<h5 style="color:red" id="jsonmsgforrules"></h5>
							<h5 style="color:red">
								&nbsp&nbsp&nbsp&nbsp Please make sure that either you have created a custom  rule
								for the selected data template or configured global rules while creating the data template <a onClick="validateHrefVulnerability(this)"
									href="addNewRule"
									target="_blank">Click here</a> to add a custom rule.
							</h5>
						</div>
						</div>
						<div id="derivedcolsProblem" class=" hidden">
						<div class="alert alert-warning">
						<h5 style="color:red" id="jsonmsgforderivedcols"></h5>
							<h5 style="color:red">
								&nbsp&nbsp&nbsp&nbsp Please make sure that you have created derived column
								for the selected data template. <a onClick="validateHrefVulnerability(this)"
									href="addNewExtendTemplate"
									target="_blank">Click here</a> to create derived column.
							</h5>
						</div>
						</div>
						<div id="incrementalProblem" class=" hidden">
						<div class="alert alert-warning">
						<h5 style="color:red" id="jsonmsgforincremental"></h5>
							<h5 style="color:red">
								&nbsp&nbsp&nbsp&nbsp Please change your Application type to Bulk Load.
							</h5>
						</div>
						</div>
						<br><br>
					<div class="form-actions noborder align-center" >
						<button type="submit" id="createValidationCheck" class="btn blue">Submit</button>
					</div>
					<!-- 	</form> -->
					<br> <br>
				</div>

			</div>
		</div>
	</div>
</div>
<jsp:include page="footer.jsp" />

<!-- Load JWF framework module on this page -->	
<head>
	<link rel="stylesheet" href="./assets/jwf/content/JwfSpaInfra.css">
	<script src="./assets/jwf/scripts/JwfSpaInfra.js"></script>
	<script type="text/javascript"
        src="./assets/global/plugins/bootstrap-datepicker.min.js"></script>
	<link rel="stylesheet"
        href="./assets/global/plugins/bootstrap-datepicker3.css" />
</head>

<script>

$(document).ready(function() {

	if($('#maxLengthCheckStatus').val()=='Y'){
	    $('#maxLengthCheck')[0].checked = true;
	}
	// added for Distribution Check - Mamta 31-Jan-22 
	if($('#numericalStatsStatus').val()=='Y'){
	    $('#numericalStats')[0].checked = true;
	}

	if($('#reprofilingStatus').val()=='Y'){
        $('#reprofiling')[0].checked = true;
    }
	
	var date_input = $('input[name="startdateid"]');
	var container = $('.bootstrap-iso form').length > 0 ? $('.bootstrap-iso form').parent() : "body";
	date_input.datepicker({
		orientation: "right",
		format: 'yyyy-mm-dd',
		container: container,
		todayHighlight: true,
		autoclose: true,
		endDate: "today"
	});
	var date_input = $('input[name="enddateid"]');
	var container = $('.bootstrap-iso form').length > 0 ? $('.bootstrap-iso form').parent() : "body";
	date_input.datepicker({
		orientation: "right",
		format: 'yyyy-mm-dd',
		container: container,
		todayHighlight: true,
		autoclose: true,
		endDate: "today"
	});
});	
</script>

<script>
	function loadThresholdOptions(sThresholdOptions, sThresholdsApplyOption) {
		var aThresholdOptions = JSON.parse(sThresholdOptions), oSelectDomObj = $('#thresholds_apply_option')[0], nThresholdsApplyOption = parseInt(sThresholdsApplyOption);
		
		UIManager.fillSelect(oSelectDomObj,aThresholdOptions, nThresholdsApplyOption);
		document.documentElement.style.setProperty('--label-tabs-color', '#337ab7');
		
		UIManager.LabelTabs.create({
			TabsId: 'validation_tabs',
			TabsPairInfo:
					[
						{ "TabLabel": "lbl_select_foundation", "ContentDivId": "content_foundation_checks" },
						{ "TabLabel": "lbl_select_essential", "ContentDivId": "content_essential_checks" },
						{ "TabLabel": "lbl_select_advanced", "ContentDivId": "content_advanced_checks" }						
					],
			TabsMinWidth: 150
		});
		
		$('#content_essential_checks').css('display','none');		
		$('#content_advanced_checks').css('display','none');
	}
	
	$(document).ready(function() {
	
		var sThresholdOptions = '${ThresholdOptions}', sThresholdsApplyOption = '${ThresholdsApplyOption}';
		initializeJwfSpaInfra();       // Load JWF framework module on this page
		loadThresholdOptions(sThresholdOptions, sThresholdsApplyOption);

	
		/* 
		Removed Avishkar 24-Mar-2020
					$('.foundationChecks').removeClass('hidden').show();
					$('.essentialChecks').addClass("hidden");
					$('.advancedChecks').addClass("hidden");
		*/
		$("#data_domainId").val(${selectedDataDomain});

	});
$("#ApplicationType").change(function() {
	var ApplicationType = $("#ApplicationType").val();
	//alert(ApplicationType);
	if (ApplicationType == "Historic") {
		$('#dateDivId').removeClass('hidden').show();
		$('#dataDriftDiv').removeClass('hidden').show();
	} else if (ApplicationType == "Ongoing"){
		$('#dataDriftDiv').removeClass('hidden').show();
		$('#dateDivId').addClass("hidden");
	}else {
		$('#dateDivId').addClass("hidden");
		$('#dataDriftDiv').removeClass('hidden').show();
	}
});

/* Removed Avishkar 24-Mar-2020
			$("#fChecks").click(function() {
				$('.foundationChecks').removeClass('hidden').show();
				$('.essentialChecks').addClass("hidden");
				$('.advancedChecks').addClass("hidden");
				
			});
			$("#eChecks").click(function() {
				
				$('.foundationChecks').addClass("hidden");
				$('.essentialChecks').removeClass("hidden").show();
				$('.advancedChecks').addClass("hidden");
				
			});
			$("#adChecks").click(function() {
				
				$('.foundationChecks').addClass("hidden");
				$('.essentialChecks').addClass("hidden");
				$('.advancedChecks').removeClass('hidden').show();
				
			});
			
			$("#fCheckNext").click(function() {
					
					$('.foundationChecks').addClass("hidden");
					$('.essentialChecks').removeClass("hidden").show();
					$('.advancedChecks').addClass("hidden");
					
				});
			
			$("#eCheckNext").click(function() {
					
					$('.foundationChecks').addClass("hidden");
					$('.essentialChecks').addClass("hidden");
					$('.advancedChecks').removeClass('hidden').show();
					
				});
*/

$("#createValidationCheck")
.click(
		function() {
			
			if(!checkInputText()){ 
				alert('Vulnerability in submitted data, not allowed to submit!');
				return;
			}
			//now//alert( "Handler for .click() called." );
			var idApp = $('#idApp').val();
			var idData = $('#idData').val();

			var dataDomainName = $("#data_domainId").val();
			
			//alert("dataDomainName =>"+dataDomainName);
			var ApplicationType=$("#ApplicationType").val();
			var incrementalTypeId = "N";
			var buildHistoricId = "N";
			if (ApplicationType=="Historic") {
				 incrementalTypeId = "Y";
				  buildHistoricId = "Y";
			} else if (ApplicationType=="Ongoing"){
				 incrementalTypeId = "Y";
			}else{
				
				}
			/* var buildHistoricId = "N";
			if ($('#buildHistoricId').is(":checked")) {
				buildHistoricId = "Y";
			}
			var incrementalTypeId = "N";
			if ($('#incrementalTypeId').is(":checked")) {
				incrementalTypeId = "Y";
			} */
			var startdateid = $('#startdateid').val();
			var enddateid = $('#enddateid').val();
			var dateformatid = $('#dateformatid').val();
			var recordCountAnomalyType = $(
					'#recordCountAnomalyType').val();
			var KeyBasedRecordCountAnomaly = "N";
			if (recordCountAnomalyType == "RecordCountAnomaly") {
				recordCountAnomalyType = "Y";
			} else {
				recordCountAnomalyType = "N";
				KeyBasedRecordCountAnomaly = "Y";
			}

			var DFSetComparisonId = $('#DFSetComparisonId').val();
			var validityThresholdId = $('#validityThresholdId').val();
			var groupEquality = "N";
			if ($('#groupEquality').is(":checked")) {
				groupEquality = "Y";
			}
			var groupEqualityText = $('#groupEqualityText').val();
			var duplicateCount = "N";
			if ($('#duplicateCount').is(":checked")) {
				duplicateCount = "Y";
			}
			var duplicateCountText = $('#duplicateCountText').val();
			var duplicateCountAll = "N";
			if ($('#duplicateCountAll').is(":checked")) {
				duplicateCountAll = "Y";
			}
			var duplicateCountAllText = $('#duplicateCountAllText').val();
			var dataDriftCheck = "N";
			if ($('#dataDriftCheck').is(":checked")) {
				dataDriftCheck = "Y";
			}
			var dataDriftCheckText = $('#dataDriftCheckText').val();
			var numericalStats = "N";
			if ($('#numericalStats').is(":checked")) {
				numericalStats = "Y";
			}
			var numericalStatsText ='0.0' ;
			var stringStat = "N";
			if ($('#stringStat').is(":checked")) {
				stringStat = "Y";
			}

			var stringStatText = $('#stringStatText').val();
			var columnOrderVal = "N";
			if ($('#columnOrderVal').is(":checked")) {
				columnOrderVal = "Y";
			}
			var fileNameVal = "N";
			if ($('#fileNameVal').is(":checked")) {
				fileNameVal = "Y";
			}
			var nameofEntityColumn = $('#nameofEntityColumn').val();
			
			
			var recordAnomalyThreshold = 3;
			var recordAnomalyid = "N";
			if ($('#recordAnomalyid').is(":checked")) {
				recordAnomalyid = "Y";
			}
			var nullCount = "N";
			if ($('#nullCount').is(":checked")) {
				nullCount = "Y";
			}
			var nullCountText = $('#nullCountText').val();
			var applyRules = "N";
			if ($('#applyRules').is(":checked")) {
				applyRules = "Y";
			}
			var applyDerivedColumns = "N";
			if ($('#applyDerivedColumns').is(":checked")) {
				applyDerivedColumns = "Y";
			}
			var csvDirectory = $('#csvDirectory').val();
			var frequencyid = $('#frequencyid').val();
			var EveryDay = $('#EveryDaytext').val();
			var dataCyclicality = "";
			if ($('#none').is(":checked")) {
				dataCyclicality = dataCyclicality + "None,";
			}
			if ($('#chkmonth').is(":checked")) {
				dataCyclicality = dataCyclicality + "month,";
			}
			if ($('#chkDayOfWeek').is(":checked")) {
				dataCyclicality = dataCyclicality + "dayOfWeek,";
			}
			if ($('#chkHourOfDay').is(":checked")) {
				dataCyclicality = dataCyclicality + "hourOfDay,";
			}
			if ($('#chkDayOfMonth').is(":checked")) {
				dataCyclicality = dataCyclicality + "dayOfMonth,";
			}
			dataCyclicality = dataCyclicality.substring(0,
					dataCyclicality.length - 1);
			//alert(dataCyclicality);
			if (dataCyclicality == "") {
				dataCyclicality = "None"
			}
			if (DFSetComparisonId == "") {
				DFSetComparisonId = 0.0;
			}
			if (validityThresholdId == ""){
				validityThresholdId = 1.0;
			}
			if (startdateid == "") {
				startdateid = null;
			}
			if (enddateid == "") {
				enddateid = null;
			}
			if (dataDriftCheckText == "") {
				dataDriftCheckText = 0.0;
			}
			
			var dupCheck = "N";
			if ($('#dupCheck').is(":checked")) {
				dupCheck = "Y";
			}
			
			var timelinessKey = "N";
			if ($('#timelinessKey').is(":checked")) {
				timelinessKey = "Y";
			}
			
			var defaultCheck = "N";
			if ($('#defaultCheck').is(":checked")) {
				defaultCheck = "Y";
			}
			var defaultValues = $('#defaultValues').val();
			
			var patternCheck = "N";
			if ($('#patternCheck').is(":checked")) {
				patternCheck = "Y";
			}
			
			var dateRuleCheck = $('#dateRuleCheck').val();
			var dateRuleCheck = "N";
			if ($('#dateRuleCheck').is(":checked")) {
				
				dateRuleCheck = "Y";
			}
			var badData = $('#badData').val();
			var badData = "N";
			if($('#badData').is(":checked")){
				badData = "Y";
			}

			//changes lengthCheck
			var lengthCheck = "N";
			
			if($('#lengthCheck').is(":checked")){
				lengthCheck = "Y";
			}

			//changes reprofiling
			var reprofiling="N";
            if($('#reprofiling').is(":checked")){
                reprofiling = "Y";
            }
			
			var defaultPatternCheck = "N";
			if ($('#defaultPatternCheck').is(":checked")) {
				defaultPatternCheck = "Y";
			}
			
			//Max Length Check
			
			var maxLengthCheck = "N";
			
			if($('#maxLengthCheck').is(":checked")){
				maxLengthCheck = "Y";
			}
			//Changes dGroupNullCheck
			var dGroupNullCheck = "N";
			if($('#dGroupNullCheck').is(":checked")){
				dGroupNullCheck = "Y";
			}

			//Changes dGroupDateRuleCheck
			var dGroupDateRuleCheck = "N";
			if($('#dGroupDateRuleCheck').is(":checked")){
				dGroupDateRuleCheck = "Y";
			}
			
			var dGroupDataDriftCheck = "N";
			console.log("dGroupDataDriftCheck :"+($('#dGroupDataDriftCheck').is(":checked")))
			if($('#dGroupDataDriftCheck').is(":checked")){
				dGroupDataDriftCheck = "Y";
			}
			//alert("dataCyclicality="+dataCyclicality);
			
			var nThresholdApplyOption = $('#thresholds_apply_option').val();						
			
			var listApplications = {

				idApp : idApp,
				idData : idData,
				buildHistoricFingerPrint : buildHistoricId,
				historicStartDate : startdateid,
				historicEndDate : enddateid,
				historicDateFormat : dateformatid,
				incrementalMatching : incrementalTypeId,
				recordCountAnomaly : recordCountAnomalyType,
				keyBasedRecordCountAnomaly : KeyBasedRecordCountAnomaly,
				recordCountAnomalyThreshold : DFSetComparisonId,
				validityThreshold : validityThresholdId,
				dupRowAll : duplicateCountAll,
				dupRowIdentity : duplicateCount,
				dupRowAllThreshold : duplicateCountAllText,
				dupRowIdentityThreshold : duplicateCountText,
				groupEquality : groupEquality,
				groupEqualityThreshold : groupEqualityText,
				dataDriftCheck : dataDriftCheck,
				dataDriftThreshold : dataDriftCheckText,
				numericalStatCheck : numericalStats,
				numericalStatThreshold : numericalStatsText,
				stringStatCheck : stringStat,
				stringStatThreshold : stringStatText,
				fileNameValidation : fileNameVal,
				colOrderValidation : columnOrderVal,
				entityColumn : nameofEntityColumn,
				recordAnomalyCheck : recordAnomalyid,
				recordAnomalyThreshold : recordAnomalyThreshold,
				nonNullCheck : nullCount,
				nonNullThreshold : nullCountText,
				applyRules : applyRules,
				applyDerivedColumns : applyDerivedColumns,
				csvDir : csvDirectory,
				updateFrequency : frequencyid,
				frequencyDays : EveryDay,
				timeSeries : dataCyclicality,
				duplicateCheck : dupCheck,				
				timelinessKeyChk : timelinessKey,
				defaultCheck : defaultCheck,
				defaultValues : defaultValues,
				patternCheck : patternCheck,
				dateRuleChk : dateRuleCheck,
				badData : badData,
				lengthCheck : lengthCheck,
				reprofiling : reprofiling,
				maxLengthCheck : maxLengthCheck,
				dGroupNullCheck : dGroupNullCheck,
				dGroupDateRuleCheck : dGroupDateRuleCheck,
				dGroupDataDriftCheck : dGroupDataDriftCheck,
				thresholdsApplyOption: nThresholdApplyOption,
				data_domain : dataDomainName,
				defaultPatternCheck : defaultPatternCheck
				
			};
			$('#jsonmsg').html('');
			$('#configProblem').addClass(
			'hidden');
	$('#rulesProblem').addClass(
	'hidden');
	$('#derivedcolsProblem').addClass(
	'hidden');
	$('#incrementalProblem').addClass('hidden');
			console.log(listApplications);
			
			
			$('button').prop('disabled', true);
			$
					.ajax({
						url : './customizeUpdateValidationCheckAjax',
						type : 'POST',
						headers: { 'token':$("#token").val()},
						datatype : 'json',
						data : JSON.stringify(listApplications),
						contentType : "application/json",
						success : function(message) {
							var j_obj = $.parseJSON(message);
							if (j_obj.hasOwnProperty('success')) {

								$('#configProblem').addClass(
										'hidden');
								toastr
										.info('Validation Check Created Successfully');
								setTimeout(
										function() {
											window.location.href = "validationCheck_View";

										}, 1000);
							} else if (j_obj.hasOwnProperty('fail')) {
								$('#configProblem').removeClass(
										'hidden').show();
								var msg = j_obj['fail']
								console.log(msg);
								$('#jsonmsg').html(
										"<i class='fa fa-warning'></i> "
												+ msg);
								console.log(j_obj.fail);
								toastr.info(j_obj.fail);
								$('#rulesProblem').addClass(
								'hidden');
								$('#derivedcolsProblem').addClass(
								'hidden');
								$('#incrementalProblem').addClass('hidden');
								$('button').prop('disabled', false);
							}
							 else if (j_obj.hasOwnProperty('rules')) {
									$('#rulesProblem').removeClass(
											'hidden').show();
									var msg = j_obj['rules']
									console.log(msg);
									$('#jsonmsgforrules').html(
											"<i class='fa fa-warning'></i> "
													+ msg);
									console.log(j_obj.rules);
									toastr.info(j_obj.rules);
									$('#configProblem').addClass(
									'hidden');
							$('#derivedcolsProblem').addClass(
							'hidden');
							$('#incrementalProblem').addClass('hidden');
							$('button').prop('disabled', false);
								}
							 else if (j_obj.hasOwnProperty('derivedcols')) {
									$('#derivedcolsProblem').removeClass(
											'hidden').show();
									var msg = j_obj['derivedcols']
									console.log(msg);
									$('#jsonmsgforderivedcols').html(
											"<i class='fa fa-warning'></i> "
													+ msg);
									console.log(j_obj.derivedcols);
									toastr.info(j_obj.derivedcols);
									$('#configProblem').addClass('hidden');
									$('#rulesProblem').addClass('hidden');
									$('#incrementalProblem').addClass('hidden');
									$('button').prop('disabled', false);
								}
							 else if (j_obj.hasOwnProperty('incremental')) {
									$('#incrementalProblem').removeClass(
									'hidden').show();
							var msg = j_obj['incremental']
							console.log(msg);
							$('#jsonmsgforincremental').html(
									"<i class='fa fa-warning'></i> "
											+ msg);
							console.log(j_obj.incremental);
							toastr.info(j_obj.incremental);
					$('#configProblem').addClass('hidden');
					$('#rulesProblem').addClass('hidden');
					$('#derivedcolsProblem').addClass('hidden');
					$('button').prop('disabled', false);
						}
						},
						error : function(xhr, textStatus,
								errorThrown) {
							$('#initial').hide();
							$('#fail').show();
						}
					});
		});

/* $( "#createValidationCheck" ).click(function() {
	  //now//alert( "Handler for .click() called." );
		var idApp = $('#idApp').val();
	    var idData = $('#idData').val();
	    var buildHistoricId="N";
	    if ($('#buildHistoricId').is(":checked"))
	    {
	    	buildHistoricId="Y";
	    }
	    var incrementalTypeId="N";
	    if ($('#incrementalTypeId').is(":checked"))
	    {
	    	incrementalTypeId="Y";
	    }
	    var startdateid = $('#startdateid').val();
	    var enddateid = $('#enddateid').val();
	    var dateformatid = $('#dateformatid').val();
	    var recordCountAnomalyType = $('#recordCountAnomalyType').val();
	    var KeyBasedRecordCountAnomaly="N";
	    if(recordCountAnomalyType=="RecordCountAnomaly"){
	    	recordCountAnomalyType="Y";
	    }else {
	    	recordCountAnomalyType="N";
	    	KeyBasedRecordCountAnomaly="Y";
	    }
	   
	    var DFSetComparisonId = $('#DFSetComparisonId').val(); 
	     var groupEquality="N";
	    if ($('#groupEquality').is(":checked"))
	    {
	    	groupEquality="Y";
	    }
	    var groupEqualityText = $('#groupEqualityText').val();
	    var duplicateCount="N";
	    if ($('#duplicateCount').is(":checked"))
	    {
	    	duplicateCount="Y";
	    }
	    var duplicateCountText = $('#duplicateCountText').val();
	    var duplicateCountAll="N";
	    if ($('#duplicateCountAll').is(":checked"))
	    {
	    	duplicateCountAll="Y";
	    }
	    var duplicateCountAllText = $('#duplicateCountAllText').val();
	    var dataDriftCheck="N";
	    if ($('#dataDriftCheck').is(":checked"))
	    {
	    	dataDriftCheck="Y";
	    }
	    var dataDriftCheckText = $('#dataDriftCheckText').val();
	    var numericalStats="N";
	    if ($('#numericalStats').is(":checked"))
	    {
	    	numericalStats="Y";
	    }
	    var numericalStatsText = '0.0';
	    var stringStat="N";
	    if ($('#stringStat').is(":checked"))
	    {
	    	stringStat="Y";
	    }
	   
	    var stringStatText = $('#stringStatText').val();
	    var columnOrderVal="N";
	    if ($('#columnOrderVal').is(":checked"))
	    {
	    	columnOrderVal="Y";
	    }
	    var fileNameVal="N";
	    if ($('#fileNameVal').is(":checked"))
	    {
	    	fileNameVal="Y";
	    }
	    var nameofEntityColumn = $('#nameofEntityColumn').val();
	    var recordAnomalyid="N";
	    if ($('#recordAnomalyid').is(":checked"))
	    {
	    	recordAnomalyid="Y";
	    }
	    var nullCount="N";
	    if ($('#nullCount').is(":checked"))
	    {
	    	nullCount="Y";
	    }
	    var nullCountText = $('#nullCountText').val();
	    var applyRules="N";
	    if ($('#applyRules').is(":checked"))
	    {
	    	applyRules="Y";
	    }
	    var applyDerivedColumns="N";
	    if ($('#applyDerivedColumns').is(":checked"))
	    {
	    	applyDerivedColumns="Y";
	    }
	    var csvDirectory = $('#csvDirectory').val();
	    var frequencyid = $('#frequencyid').val();
	    var EveryDay = $('#EveryDaytext').val();
	    var dataCyclicality="";
	    if ($('#none').is(":checked"))
	    {
	    	dataCyclicality=dataCyclicality+"None,";
	    }
	    if ($('#month').is(":checked"))
	    {
	    	dataCyclicality=dataCyclicality+"month,";
	    }
	    if ($('#dow').is(":checked"))
	    {
	    	dataCyclicality=dataCyclicality+"dayOfWeek,";
	    }
	    if ($('#hod').is(":checked"))
	    {
	    	dataCyclicality=dataCyclicality+"hourOfDay,";
	    }
	    if ($('#dom').is(":checked"))
	    {
	    	dataCyclicality=dataCyclicality+"dayOfMonth,";
	    }
	    dataCyclicality = dataCyclicality.substring(0, dataCyclicality.length - 1);
	    if(dataCyclicality==""){
	    	dataCyclicality="None"
	    }
	    if(DFSetComparisonId==""){
	    	DFSetComparisonId=0.0;
	    }
	    if(startdateid==""){
	    	startdateid=null;
	    }
	    if(enddateid==""){
	    	enddateid=null;
	    }
	    if(dataDriftCheckText==""){
	    	dataDriftCheckText=0.0;
	    }
	    //alert("dataCyclicality="+dataCyclicality);
	    
	    var listApplications={
	    		
	    		idApp: idApp,
	    		idData: idData,
	    		buildHistoricFingerPrint: buildHistoricId,
				historicStartDate: startdateid,
				historicEndDate: enddateid,
				historicDateFormat: dateformatid,
				incrementalMatching: incrementalTypeId,
				recordCountAnomaly: recordCountAnomalyType,
				keyBasedRecordCountAnomaly: KeyBasedRecordCountAnomaly,
				recordCountAnomalyThreshold: DFSetComparisonId,
				dupRowAll: duplicateCountAll,
				dupRowIdentity: duplicateCount,
				dupRowAllThreshold: duplicateCountAllText,
				dupRowIdentityThreshold: duplicateCountText,
				groupEquality: groupEquality,
				groupEqualityThreshold: groupEqualityText,
				dataDriftCheck: dataDriftCheck,
				dataDriftThreshold: dataDriftCheckText,
				numericalStatCheck: numericalStats,
				numericalStatThreshold: numericalStatsText,
				stringStatCheck: stringStat,
				stringStatThreshold: stringStatText,
				fileNameValidation: fileNameVal,
				colOrderValidation: columnOrderVal,
				entityColumn: "",
				recordAnomalyCheck: recordAnomalyid,
				nonNullCheck: nullCount,
				nonNullThreshold: nullCountText,
				applyRules: applyRules,
	    		applyDerivedColumns: applyDerivedColumns,
	    		csvDir: csvDirectory,
	    		updateFrequency: frequencyid,
	    		frequencyDays: EveryDay,
	    		timeSeries: dataCyclicality
	    };
	    $('#jsonmsg').html('');
	    
	    console.log(listApplications);
    
	    
	    $.ajax({
        url:'./customizeUpdateValidationCheckAjax',
        type: 'POST',
        datatype: 'json',
        data:JSON.stringify(listApplications),
        contentType:"application/json",
        success: function(message) {
        	 var j_obj = $.parseJSON(message);
             if(j_obj.hasOwnProperty('success'))
             {
            	
            	 $('#configProblem').addClass('hidden');
                   toastr.info('Validation Check Customized Successfully');
                    setTimeout(function(){
                    window.location.href= "validationCheck_View";

                },1000); 
             }else if(j_obj.hasOwnProperty('fail') )
             {
            	 $('#configProblem').removeClass('hidden').show();
            	 var msg=j_obj['fail']
            	 console.log(msg);
            	 $('#jsonmsg').html("<i class='fa fa-warning'></i> "+msg);
                 console.log(j_obj.fail);
                 toastr.info(j_obj.fail);
             }
     },
     error: function(xhr, textStatus, errorThrown){
           $('#initial').hide();
           $('#fail').show();
     }
    });
	}); */

	$('#recordCountAnomalyType').change(function() {
		$this = $(this);
		var selectedVal = $this.val();

		if (selectedVal == 'KeyBasedRecordCountAnomaly') {
			$('#groupEqualitydiv').removeClass('hidden').show();
		} else {
			$('#groupEqualitydiv').addClass('hidden');
		}
	});

	$(document).on('change', '#chkDayOfWeek', function() {
		if ($(this).prop("checked") == true) {
			$(".divDayOfWeek").removeClass("hidden");
		} else {
			$(".divDayOfWeek").addClass("hidden");
		}
	});
	$(document).on('change', '#chkDayOfMonth', function() {
		if ($(this).prop("checked") == true) {
			$(".divDayOfMonth").removeClass("hidden");
		} else {
			$(".divDayOfMonth").addClass("hidden");
		}
	});
	$(document).on('change', '#chkHourOfDay', function() {
		if ($(this).prop("checked") == true) {
			$(".divHourOfDay").removeClass("hidden");
		} else {
			$(".divHourOfDay").addClass("hidden");
		}
	});
	$(document).on('change', '#chkmonth', function() {
		if ($(this).prop("checked") == true) {
			$(".divmonth").removeClass("hidden");
		} else {
			$(".divmonth").addClass("hidden");
		}
	});

	/* $("#buildHistoricId").click(function() {
		if ($('#buildHistoricId').is(':checked')) {
			$('#dateDivId').removeClass('hidden').show();
		} else {
			$('#dateDivId').addClass("hidden");
		}
	}); */
	$("#none").click(function() {
		if ($('#none').is(':checked')) {
			$('#chkmonth').prop("checked", false);
			$('#chkHourOfDay').prop("checked", false);
			$('#chkDayOfWeek').prop("checked", false);
			$('#chkDayOfMonth').prop("checked", false);
			$('#noneid').addClass('hidden');
			$(".divmonth").addClass("hidden");
			$(".divHourOfDay").addClass("hidden");
			$(".divDayOfMonth").addClass("hidden");
			$(".divDayOfWeek").addClass("hidden");
		} else {
			$('#noneid').removeClass('hidden').show();
		}
	});
	$('#frequencyid').change(function() {
		$this = $(this);
		var selectedVal = $this.val();
		if (selectedVal == 'EveryDay') {
			$('#EveryDay').removeClass('hidden').show();
		} else {
			$('#EveryDay').addClass('hidden');
		}
	});
</script>
