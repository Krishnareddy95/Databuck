
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />
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
							<span class="caption-subject bold ">Data Quality
								Fingerprint</span>
						</div>
					</div>
					<div class="portlet-body form">
						<!-- <form action="createValidationCheckCustomize" method="POST"> -->
						<div class="form-body">
							<input type="hidden" id="idApp" name="idApp" value="${idApp}" />
							<input type="hidden" id="idData" name="idData" value="${idData}" />

							<div class="row">
								<div class="col-md-12">
									<div class="col-md-1"></div>
									<div class="col-md-5 col-capture">
										<div class="form-group form-md-line-input">
											<select name="ApplicationType" id="ApplicationType"
												class="form-control">
												<option value='Normal'>Bulk Load</option>
												<option value='Historic'>Historic</option>
												<option value='Ongoing'>Incremental</option>
												<option value='Streaming'>Streaming</option>
											</select> <label for="ApplicationType">Choose Type of
												Application</label>
										</div>
									</div>

									<div class="col-md-6 col-capture" style="padding-bottom: 20px">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="dateformatid"
												name="dateformatid"><label class="form_control_1">Date
												Format</label>
										</div>

									</div>
								</div>
							</div>
							<div class="row hidden " id="dateDivId"
								style="padding-left: 100px;">

								<div class="col-md-6">
									<label class="form_control_1">Start Date</label>
									<div class="input-group input-medium">
										<input type="text" class="form-control" readonly
											id="startdateid" name="startdateid"> <span
											class="input-group-btn">
											<label class="input-group-btn" for="startdateid">
												<span class="btn default">
													<i class="fa fa-calendar"></i>
												</span>
											</label>
										</span>
									</div>
								</div>
								<div class="col-md-6">
									<label class="form_control_1">End Date</label>
									<div class="input-group input-medium">
										<input type="text" class="form-control" readonly
											id="enddateid" name="enddateid"> <span
											class="input-group-btn">
											<label class="input-group-btn" for="enddateid">
												<span class="btn default">
												<i class="fa fa-calendar"></i>
												</span>
											</label>
										</span>
									</div>
								</div>

							</div>
							</br>
							<div class="row">
								<div class="col-md-12">
									<div class="col-md-1">
										<!-- <div class="form-group form-md-checkboxes">
											<div class="md-checkbox-list">
												<div class="md-checkbox">
													<input type="checkbox" id="recordCount" name="recordCount"
															class="md-check" value="Y"> <label
															for="recordCount"> <span></span> <span
															class="check"></span> <span class="box"></span></label>
												</div>
											</div>
										</div> -->
									</div>
									<div class="col-md-5 col-capture">
										<div class="form-group form-md-line-input">
											<select name="recordCountAnomalyType"
												id="recordCountAnomalyType" class="form-control">
												<option value='RecordCountAnomaly'>Record Count
													Anomaly</option>
												<option value='KeyBasedRecordCountAnomaly'> Microsegment Based Record Anomaly</option>
											</select> <label for="recordCountAnomalyType">Choose Type of
												Record Count Anomaly</label>
										</div>
									</div>
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control"
												id="DFSetComparisonId" name="DFSetComparisonId" value = "3.0"> <label
												for="recordCountText">Record Count anomaly</label>
										</div>
									</div>
								</div>
							</div>
							<br />

							<div class="row hidden" id="groupEqualitydiv">
								<div class="col-md-12">
									<div class="col-md-1">
										<div class="form-group form-md-checkboxes">
											<div class="md-checkbox-list">
												<div class="md-checkbox">
													<input type="checkbox" id="groupEquality"
														name="groupEquality" class="md-check" value="Y"> <label
														for="groupEquality"> <span></span> <span
														class="check"></span> <span class="box"></span></label>
												</div>
											</div>
										</div>
									</div>
									<div class="col-md-5 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control"
												id="groupEqualityText" name="groupEqualityText"> <label
												for="groupEquality">Group Equality</label>
										</div>
									</div>
								</div>
							</div>
							<br />

							<div class="row">
								<div class="col-md-12">
									<div class="col-md-1">
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
												id="duplicateCountText" name="duplicateCountText"> <label
												for="duplicateCountText">Duplicate rows based on
												identity fields</label>
										</div>
									</div>
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
												id="duplicateCountAllText" name="duplicateCountAllText">
											<label for="duplicateCountAllText">Duplicate rows
												based on all fields</label>
										</div>
									</div>
								</div>
							</div>
							<br />
							<div class="row">
								<div class="col-md-12">
								<div class="col-md-1">
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
									<div class="col-md-5 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="stringStatText"
												name="stringStatText"> <label for="stringStatText">String
												field stat and comparison</label>
										</div>
									</div>
									<div class="col-md-1">
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
												id="numericalStatsText" name="numericalStatsText"> <label
												for="numericalStatsText">Numerical field stats and
												comparison</label>-->
											<label for="numericalStats">Distribution Check</label>
										</div>
									</div>
								</div>
							</div>
							<br />
							<div class="row">
								<div class="col-md-12">
									<div id="dataDriftDiv">
									<div class="col-md-1">
										<div class="form-group form-md-checkboxes">
											<div class="md-checkbox-list">
												<div class="md-checkbox">
													<input type="checkbox" id="dataDriftCheck"
														name="dataDriftCheck" class="md-check" value="Y">
													<label for="dataDriftCheck"> <span></span> <span
														class="check"></span> <span class="box"></span></label>
												</div>
											</div>
										</div>
									</div>
									<div class="col-md-5 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control"
												id="dataDriftCheckText" name="dataDriftCheckText"> <label
												for="dataDriftCheck">Data Drift Check</label>
										</div>
									</div>
									</div>
									<div id="filesystemid">
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
												<label for="numericalStatsText">Column Order
													Validation</label>
											</div>
										</div>
									</div>
								</div>
							</div>

							<br />
							<div class="row" id="filesystemid1">
								<div class="col-md-12">
									<div class="col-md-1">
										<div class="form-group form-md-checkboxes">
											<div class="md-checkbox-list">
												<div class="md-checkbox">
													<input type="checkbox" id="fileNameVal" name="fileNameVal"
														class="md-check" value="Y"> <label
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
									<div class="col-md-5 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control"
												placeholder="Name of Entity Column" id="nameofEntityColumn"
												name="nameofEntityColumn"> <label
												for="nameofEntityColumn"></label>
										</div>
									</div>
								</div>
							</div>
							<br />
							<div class="row">
								<div class="col-md-12">
									<div class="col-md-1">
										<div class="form-group form-md-checkboxes">
											<div class="md-checkbox-list">
												<div class="md-checkbox">
													<input type="checkbox" id="recordAnomalyid"
														name="recordAnomalyid" class="md-check" value="Y">
													<label for="recordAnomalyid"> <span></span> <span
														class="check"></span> <span class="box"></span></label>
												</div>
											</div>
										</div>
									</div>
									<div class="col-md-5 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control"
													id="recordAnomalyThresholdId"
													name="recordAnomalyThresholdId" value="2"> 
											<label for="recordAnomalyThresholdId">Record Anomaly</label>
										</div>
									</div>
									<div class="col-md-1">
										<div class="form-group form-md-checkboxes">
											<div class="md-checkbox-list">
												<div class="md-checkbox">
													<input type="checkbox" id="nullCount" name="nullCount"
														class="md-check" value="Y"> <label for="nullCount">
														<span></span> <span class="check"></span> <span
														class="box"></span>
													</label>
												</div>
											</div>
										</div>
									</div>
									<div class="col-md-5 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="nullCountText"
												name="nullCountText"> <label for="nullCountText">Null
												count of Non-Null fields</label>
										</div>
									</div>
								</div>
							</div>

							<!-- <div class="row">
									<div class="col-md-12">
										<div class="col-md-1">
											<div class="form-group form-md-checkboxes">
												<div class="md-checkbox-list">
													<div class="md-checkbox">
														<input type="checkbox" id="dataDriftCheck"
															name="dataDriftCheck" class="md-check" value="Y">
														<label for="dataDriftCheck"> <span></span> <span
															class="check"></span> <span class="box"></span></label>
													</div>
												</div>
											</div>
										</div>
										<div class="col-md-5 col-capture">
											<div class="form-group form-md-line-input">
												<input type="text" class="form-control"
													id="dataDriftCheckText" name="dataDriftCheckText">
												<label for="dataDriftCheck">Data Drift Check</label>
											</div>
										</div>
									</div>
								</div> -->
							<br />
							<div class="row">
								<div class="col-md-12">
									<div class="col-md-1">
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
									<div class="col-md-1">
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
							<br />&nbsp&nbsp&nbsp
							<div class="row">
								<div class="col-md-12">
									<div class="col-md-1">
										<div class="form-group form-md-checkboxes">
											<div class="md-checkbox-list">
												<div class="md-checkbox">
													<input type="checkbox" id="dupCheck" name="dupCheck"
														class="md-check" value="Y"> <label for="dupCheck">
														<span></span> <span class="check"></span> <span
														class="box"></span>
													</label>
												</div>
											</div>
										</div>
									</div>
									<div class="col-md-5 col-capture">
										<div class="form-group form-md-line-input">
											<!--  <input type="text" class="form-control"placeholder="Number of Rows" id="numberofRows" name="numberofRows"> -->
											<label for="dupCheck">Record Count Duplicate Check</label>
										</div>
									</div>

									<div class="col-md-5 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="csvDirectory"
												name="csvDirectory"> <label for="csvDirectory">CSV
												Directory</label>
										</div>
									</div>
								</div>
							</div>
							
							<br />&nbsp&nbsp&nbsp
						
							<div class="row">
								<div class="col-md-12">
									<div class="col-md-1">
										<div class="form-group form-md-checkboxes">
											<div class="md-checkbox-list">
												<div class="md-checkbox">
													<input type="checkbox" id="timelinessKeyChk"
														name="timelinessKeyChk" class="md-check" value="Y">
													<label for="timelinessKeyChk"> <span></span> <span
														class="check"></span> <span class="box"></span></label>
												</div>
											</div>
										</div>
									</div>
									<div class="col-md-5 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control"
												id="timelinessKeyText" name="timelinessKeyText"> <label
												for="timelinessKeyText">Timeliness Check</label>
										</div>
									</div>
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
												id="defaultValues" name="defaultValues"> <label
												for="defaultValues">Default Check (Enter Default values below)</label>
										</div>
									</div>
								    	</div>
								</div>
									<br />&nbsp&nbsp&nbsp
						
							<div class="row">
								<div class="col-md-12">
									<div class="col-md-1">
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
												<label for="applyRules">Pattern Check</label>
											</div>
										</div>
										<div class="col-md-1">
										<div class="form-group form-md-checkboxes">
											<div class="md-checkbox-list">
												<div class="md-checkbox">
													<input type="checkbox" id="dateRuleChk"
														name="dateRuleChk" class="md-check" value="Y">
													<label for="dateRuleChk"> <span></span> <span
														class="check"></span> <span class="box"></span></label>
												</div>
											</div>
										</div>
									</div>
									<div class="col-md-5 col-capture">
										<div class="form-group form-md-line-input">
											<!-- <input type="text" class="form-control"
												id="dateRuleText" name="dateRuleText"> --> <label
												for="dateRuleText">Date Rule Check</label>
										</div>
									</div>                                    
								</div>
								</div>
								<!-- sumeet -->
								<div class="row">
								<div class="col-md-12">
									<div class="col-md-1">
										<div class="form-group form-md-checkboxes">
											<div class="md-checkbox-list">
												<div class="md-checkbox">
													<input type="checkbox" id="badData"
														name="badData" class="md-check" value="Y">
													<label for="badData"> <span></span> <span
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
									<div class="col-md-1">
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
												<label for="dGroupNullCheckThreashold">DGroup Null Check</label>
											</div>
									</div>                                      
								</div>
								</div>
									<!--endofcode-->
									
										<!-- 24_DEC_2018 (12.43pm) Priyanka -->	
									
							<div class="row">
								<div class="col-md-12">
									<div class="col-md-1">
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
												<label for="applyRules">Length Check</label>
											</div>
									</div> 
									<div class="col-md-1"> 
										<div class="form-group form-md-checkboxes">
											<div class="md-checkbox-list">
												<div class="md-checkbox">
													<input type="checkbox" id="dGroupDateRuleCheck"
														name="dGroupDateRuleCheck" class="md-check" value="Y">
													<label for="dGroupDateRuleCheck"> <span></span> <span
														class="check"></span><span class="box"></span></label>
												</div>
											</div>
										</div>
									</div>	
									<div class="col-md-5 col-capture">
										<div class="form-group form-md-line-input">
											<label for="dGroupDateRuleCheckLabel">DGroup Date Rule Check</label>
										</div>
									</div> 
								</div>
							</div>
								<!-- ------------------ -->	
									
							<br />&nbsp&nbsp&nbsp Data Cyclicality<br /> <br />
							<div class="row">
								<div class="col-md-12">
									<div class="col-md-6">
										<div class="form-group form-md-checkboxes">
											<div class="md-checkbox-list">
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
								</div>
							</div>
							<div id="noneid">
								<div class="row">
									<div class="col-md-12">
										<div class="col-md-4">
											<div class="form-group form-md-checkboxes">
												<div class="md-checkbox-list">
													<div class="md-checkbox">
														<input type="checkbox" id="month" name="check_id"
															class="md-check" value="month"> <label
															for="month"> Month<span></span> <span
															class="check"></span> <span class="box"></span>
														</label>
													</div>
												</div>
											</div>
										</div>
										<div class="col-md-4 hidden" id="monthchecked">
											<div class="form-group form-md-line-input">
												<select name="monthdropdown" id="monthdropdown"
													class="form-control">
													<option>Choose Month</option>
													<option value='January'>January</option>
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
											</div>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-md-12">
										<div class="col-md-4">
											<div class="form-group form-md-checkboxes">
												<div class="md-checkbox-list">
													<div class="md-checkbox">
														<input type="checkbox" id="dow" name="check_id"
															class="md-check" value="dayOfWeek"> <label
															for="dow"> Day of Week<span></span> <span
															class="check"></span> <span class="box"></span>
														</label>
													</div>
												</div>
											</div>
										</div>

										<div class="col-md-4 hidden" id="dowchecked">
											<!-- <div class="col-md-6 col-capture"> -->
											<div class="form-group form-md-line-input">
												<select name="dowdropdown" id="dowdropdown"
													class="form-control dayOfWeek">
													<option selected>Choose Day</option>
													<option value="Monday">Monday</option>
													<option value="Tuesday">Tuesday</option>
													<option value="Wednesday">Wednesday</option>
													<option value="Thursday">Thursday</option>
													<option value="Friday">Friday</option>
													<option value="Saturday">Saturday</option>
													<option value="Sunday">Sunday</option>
												</select>
											</div>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-md-12">
										<div class="col-md-4">
											<div class="form-group form-md-checkboxes">
												<div class="md-checkbox-list">
													<div class="md-checkbox">
														<input type="checkbox" id="hod" name="check_id"
															class="md-check" value="hourOfDay"> <label
															for="hod"> Hour of Day<span></span> <span
															class="check"></span> <span class="box"></span>
														</label>
													</div>
												</div>
											</div>
										</div>
										<div class="col-md-4 hidden" id="hodchecked">
											<!-- <div class="col-md-6 col-capture"> -->
											<div class="form-group form-md-line-input">
												<select name="hoddropdown" id="hoddropdown"
													class="form-control">
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
												</select>
												<!--  <br /> <label for="form_control_1">Choose Day</label> -->
											</div>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-md-12">
										<div class="col-md-4">
											<div class="form-group form-md-checkboxes">
												<div class="md-checkbox-list">
													<div class="md-checkbox">
														<input type="checkbox" id="dom" name="check_id"
															class="md-check" value="dayOfMonth"> <label
															for="dom"> Day of Month<span></span> <span
															class="check"></span> <span class="box"></span>
														</label>
													</div>
												</div>
											</div>
										</div>
										<div class="col-md-4 hidden" id="domchecked">
											<!-- <div class="col-md-6 col-capture"> -->
											<div class="form-group form-md-line-input">
												<select name="domdropdown" id="domdropdown"
													class="form-control">
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
												</select>
												<!-- <br /> <label for="form_control_1">Choose Day</label> -->
											</div>
										</div>
									</div>
								</div>
							</div>

						</div>

						<div class="row">
							<div class="col-md-12">
								<div class="col-md-6">
									<div class="form-group form-md-line-input">
										<select name="frequencyid" id="frequencyid"
											class="form-control">
											<option value="Never">Never</option>
											<option value="Daily">Daily</option>
											<option value="everyXdays">Every 'x' Days</option>
										</select> <br /> <label for="form_control_1">Update Frequency</label>
									</div>
								</div>
								<div class="col-md-6 hidden" id="EveryDay">
									<div class="form-group form-md-checkboxes">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="EveryDaytext"
												name="EveryDaytext"> <label for="EveryDay">Every
												'x' Days</label>
										</div>
									</div>
								</div>
							</div>
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
								&nbsp&nbsp&nbsp&nbsp Please make sure that you have created rule
								for the selected data template. <a onClick="validateHrefVulnerability(this)"
									href="addNewRule"
									target="_blank">Click here</a> to add a new rule.
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
						<div class="row">
							<div class="form-actions noborder">
								<button type="submit" id="createValidationCheck"
									class="btn blue btn-primary center-block">Submit</button>
							</div>
						</div>
						<input type="hidden" id="filesystem" value="${filesystem}">
						<!-- </form> -->
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<jsp:include page="footer.jsp" />
<script type="text/javascript"
       src="./assets/global/plugins/bootstrap-datepicker.min.js"></script>
<link rel="stylesheet"
       href="./assets/global/plugins/bootstrap-datepicker3.css" />
<script>
	$(document).ready(function() {
		
		var date_input = $('input[name="startdateid"]');
		var container = $('.bootstrap-iso form').length > 0 ? $('.bootstrap-iso form').parent() : "body";
		date_input.datepicker({
			orientation: "right",
			format: 'yyyy-mm-dd',
			container: container,
			todayHighlight: true,
			autoclose: true,
			endDate: "today"
		})
		var date_input = $('input[name="enddateid"]');
		var container = $('.bootstrap-iso form').length > 0 ? $('.bootstrap-iso form').parent() : "body";
		date_input.datepicker({
			orientation: "right",
			format: 'yyyy-mm-dd',
			container: container,
			todayHighlight: true,
			autoclose: true,
			endDate: "today"
		})
		
		var filesystem = $("#filesystem").val();
		console.log(filesystem);
		if (filesystem == "true") {
			$('#filesystemid').removeClass('hidden').show();
			$('#filesystemid1').removeClass('hidden').show();
		} else {
			$('#filesystemid').addClass("hidden");
			$('#filesystemid1').addClass("hidden");
		}
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

						var ApplicationType = $("#ApplicationType").val();
						var incrementalTypeId = "N";
						var buildHistoricId = "N";
						if (ApplicationType == "Historic") {
							incrementalTypeId = "Y";
							buildHistoricId = "Y";
						} else if (ApplicationType == "Ongoing") {
							incrementalTypeId = "Y";
						} else {

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
						if(duplicateCountText==""){
							duplicateCountText=0;
						}
						var duplicateCountAll = "N";
						if ($('#duplicateCountAll').is(":checked")) {
							duplicateCountAll = "Y";
						}
						var duplicateCountAllText = $('#duplicateCountAllText')
								.val();
						if(duplicateCountAllText==""){
							duplicateCountAllText=0;
						}
						var dataDriftCheck = "N";
						if ($('#dataDriftCheck').is(":checked")) {
							dataDriftCheck = "Y";
						}
						var dataDriftCheckText = $('#dataDriftCheckText').val();
						var numericalStats = "N";
						if ($('#numericalStats').is(":checked")) {
							numericalStats = "Y";
						}
						var numericalStatsText = '0.0';
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
						
						
						
						var recordAnomalyThreshold = $('#recordAnomalyThresholdId').val();
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
						if ($('#month').is(":checked")) {
							dataCyclicality = dataCyclicality + "month,";
						}
						if ($('#dow').is(":checked")) {
							dataCyclicality = dataCyclicality + "dayOfWeek,";
						}
						if ($('#hod').is(":checked")) {
							dataCyclicality = dataCyclicality + "hourOfDay,";
						}
						if ($('#dom').is(":checked")) {
							dataCyclicality = dataCyclicality + "dayOfMonth,";
						}
						dataCyclicality = dataCyclicality.substring(0,
								dataCyclicality.length - 1);
						if (dataCyclicality == "") {
							dataCyclicality = "None"
						}
						if (DFSetComparisonId == "") {
							DFSetComparisonId = 0.0;
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
						
						var timelinessKeyChk = "N";
						if ($('#timelinessKeyChk').is(":checked")) {
							timelinessKeyChk = "Y";
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
						var dateRuleChk = "N";
						if ($('#dateRuleChk').is(":checked")) {
							dateRuleChk = "Y";
						}
						var badData = "N";
						if($('#badData').is(":checked")){
							badData = "Y";
						}
						
						//priyanka 25-12-2018 
						
						var lengthCheck = "N";
						if($('#lengthCheck').is(":checked")){
							lengthCheck = "Y";
						}
						
						var dGroupNullCheck = "N";
						console.log("dGroupNullcheck :"+($('#dGroupNullCheck').is(":checked")))
						if($('#dGroupNullCheck').is(":checked")){
							dGroupNullCheck = "Y";
						}
						
						var dGroupDateRuleCheck = "N";
						console.log("dGroupDateRuleCheck :"+($('#dGroupDateRuleCheck').is(":checked")))
						if($('#dGroupDateRuleCheck').is(":checked")){
							dGroupDateRuleCheck = "Y";
						}
						
						//alert("dataCyclicality="+dataCyclicality);
						//alert(KeyBasedRecordCountAnomaly);
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
							timelinessKeyChk : timelinessKeyChk,
							defaultCheck : defaultCheck,
							defaultValues : defaultValues,
							patternCheck : patternCheck,
							dateRuleChk : dateRuleChk,
							badData : badData,
							//priyanka 25-12-2018 
							
							lengthCheck : lengthCheck,
							dGroupNullCheck : dGroupNullCheck,
							dGroupDateRuleCheck : dGroupDateRuleCheck
						};
						$('#jsonmsg').html('');
				$('#configProblem').addClass('hidden');
				$('#rulesProblem').addClass('hidden');
				$('#derivedcolsProblem').addClass('hidden');
				$('#incrementalProblem').addClass('hidden');
						console.log(listApplications);
						$('button').prop('disabled', true);
						$.ajax({
									url : './updateValidationCheckAjax',
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
											$('#rulesProblem').addClass(
											'hidden');
											$('#derivedcolsProblem').addClass(
											'hidden');
											toastr
													.info('Validation Check Created Successfully');
											setTimeout(function() {
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

	$('#frequencyid').change(function() {
		$this = $(this);
		var selectedVal = $this.val();
		if (selectedVal == 'everyXdays') {
			$('#EveryDay').removeClass('hidden').show();
		} else {
			$('#EveryDay').addClass('hidden');
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
			$('#month').prop("checked", false);
			$('#dow').prop("checked", false);
			$('#hod').prop("checked", false);
			$('#dom').prop("checked", false);
			$('#noneid').addClass('hidden');
		} else {
			$('#noneid').removeClass('hidden').show();
		}
	});
	$('#recordCountAnomalyType').change(function() {
		$this = $(this);
		var selectedVal = $this.val();

		if (selectedVal == 'KeyBasedRecordCountAnomaly') {
			$('#groupEqualitydiv').removeClass('hidden').show();
		} else {
			$('#groupEqualitydiv').addClass('hidden');
		}
	});
	/* $("#month").click(function() { 
		if ($('#month').is(':checked'))	{
		$('#monthchecked').removeClass('hidden').show();
	} else {
		$('#monthchecked').addClass('hidden');
	}
	});
	$("#dow").click(function() { 
		if ($('#dow').is(':checked'))	{
		$('#dowchecked').removeClass('hidden').show();
	} else {
		$('#dowchecked').addClass('hidden');
	}
	});
	
	$("#hod").click(function() { 
		if ($('#hod').is(':checked'))	{
		$('#hodchecked').removeClass('hidden').show();
	} else {
		$('#hodchecked').addClass('hidden');
	}
	});
	
	$("#dom").click(function() { 
		if ($('#dom').is(':checked'))	{
		$('#domchecked').removeClass('hidden').show();
	} else {
		$('#domchecked').addClass('hidden');
	}
	}); */
</script>
