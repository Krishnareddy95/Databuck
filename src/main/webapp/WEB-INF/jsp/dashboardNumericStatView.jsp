


<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="org.springframework.jdbc.support.rowset.SqlRowSet"%>
<%@page import="org.springframework.jdbc.support.rowset.SqlRowSetMetaData"%>
<jsp:include page="checkVulnerability.jsp" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="./assets/global/plugins/bootstrap.min.css">
<link rel="stylesheet" href="./assets/pages/chosendropdown/css/chosen.css" />
<link rel="stylesheet" href="./assets/pages/chosendropdown/css/prism.css" />
<link rel="stylesheet" href="./assets/pages/chosendropdown/css/style.css" />

  <script src="./assets/global/plugins/bootstrap.min.js"></script>
  <script src="./assets/global/plugins/jquery.min.js"></script>


<style>
	#ColSummTable a {
		font-size: 26px;
		text-decoration: none;
		cursor: pointer !important;
	}
</style>
</head>
<style>
.loader {
  border: 16px solid #f3f3f3;
  border-radius: 50%;
  border-top: 16px solid #337ab7;
  border-bottom: 16px solid #337ab7;
  width: 80px;
  height: 80px;
  -webkit-animation: spin 2s linear infinite;
  animation: spin 2s linear infinite;
}

.force-scroll {
	overflow-y: scroll;
	height: 150px;
}

@-webkit-keyframes spin {
  0% { -webkit-transform: rotate(0deg); }
  100% { -webkit-transform: rotate(360deg); }
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.px200 {
  width: 200px;
  max-width: 200px;
  word-wrap: break-word;
}

.dataTables_scrollBody{
    overflow-y: hidden !important;
}
</style>
<body>
<jsp:include page="dashboardTableCommon.jsp" />
							<div class="row">
								<div class="col-md-12">
									<div class="tabcontainer">
										<ul class="nav nav-tabs responsive" role="tablist"
											style="border-top: 1px solid #ddd; border-bottom: 1px solid #ddd;">

											<li ><a onClick="validateHrefVulnerability(this)" 
												href="dashboard_table?idApp=${idApp}"
												style="padding: 2px 2px;"> <strong>Count Reasonability</strong>
											</a></li>
											<li ><a onClick="validateHrefVulnerability(this)" 
												href="validity?idApp=${idApp}"
												style="padding: 2px 2px;"> <strong>Microsegment Validity</strong>
											</a></li>
											<li ><a onClick="validateHrefVulnerability(this)"  href="dupstats?idApp=${idApp}"
												style="padding: 2px 2px;"> <strong>Uniqueness</strong>
											</a></li>
											<li ><a onClick="validateHrefVulnerability(this)" 
												href="nullstats?idApp=${idApp}" style="padding: 2px 2px;">
													<strong>Completeness</strong>
											</a></li>
											<li ><a onClick="validateHrefVulnerability(this)" 
												href="badData?idApp=${idApp}"
												style="padding: 2px 2px;"><strong>Conformity</strong>
											</a></li>
											
											<li ><a onClick="validateHrefVulnerability(this)" 
												href="sqlRules?idApp=${idApp}"
												style="padding: 2px 2px;"><strong>Custom Rules</strong>
											</a></li>											
											
											<li ><a onClick="validateHrefVulnerability(this)" 
												href="stringstats?idApp=${idApp}" style="padding: 2px 2px;">
													<strong>Drift & Orphan</strong>
											</a></li>
											<li class="active" ><a onClick="validateHrefVulnerability(this)" 
												href="numericstats?idApp=${idApp}" style="padding: 2px 2px;">
													<strong>Distribution Check</strong>
											</a></li>
											<li ><a onClick="validateHrefVulnerability(this)" 
												href="recordAnomaly?idApp=${idApp}"
												style="padding: 2px 2px;"> <strong>Record Anomaly</strong> 
												</a></li>
											<li ><a onClick="validateHrefVulnerability(this)" 
												href="timelinessCheck?idApp=${idApp}"
												style="padding: 2px 2px;"><strong>Sequence</strong>
											</a></li>
											<li ><a onClick="validateHrefVulnerability(this)" 
				                         		href="exceptions?idApp=${idApp}" 
				                         		style="padding: 2px 2px;"><strong>Exceptions</strong>
					                         </a></li>
											<li ><a onClick="validateHrefVulnerability(this)" 
												href="rootCauseAnalysis?idApp=${idApp}"
												style="padding: 2px 2px;"><strong>RootCause Analysis</strong>
											</a></li>
										</ul>
									</div>
								</div>
							</div>
							<div class="portlet-title">
								<div class="caption font-red-sunglo">
									<span class="caption-subject bold ">Column Summary<a onClick="validateHrefVulnerability(this);javascript:downloadCsvReports('${idApp}','${ColSumm_TableName}','NumStatData')"
										 href="#"
										class="CSVStatus" id="">(Download CSV)</a></span>
								</div>
							</div>
							<hr />
							
		<!-- filter button -->
	<div>
		<button id="addAdvanceFilter" class="btn w3-black w3-button w3-circle">+</button>
		<label id="addAdvanceFilter_v" class="text-info"> Add Advance
			Filter</label>
	</div>
	<div>
		<button id="rmAdvanceFilter"
			class="btn w3-black w3-button w3-circle hidden">-</button>
		<label id="rmAdvanceFilter_v" class="text-info hidden"> Remove
			Advance Filter</label>
	</div>
	<hr />
	
	<div class="row">
		<div>
			<div class="col-md-0"></div>
			<div class="col-md-12">
				<div class="row hidden" id="nav_button-bar"></div>
			</div>
		</div>
	</div>

	<ul class="nav nav-tabs">
      <li class="active"><a onClick="validateHrefVulnerability(this)"  data-toggle="tab" href="#home_table">Table</a></li>
      <li><a onClick="validateHrefVulnerability(this);trendChartDropDown()"  data-toggle="tab" href="#menu1" >Avg Trend</a></li>
      <li><a onClick="validateHrefVulnerability(this);stdDevTrendChartDropDown()"  data-toggle="tab" href="#menu2" >Shape Trend</a></li> <!-- Distribution Trend -->
       <li><a onClick="validateHrefVulnerability(this);sumOfNumStats()"  data-toggle="tab" href="#menu5" >Total Vol/ Amt Trend</a></li> <!-- Sum Trend -->
      <li><a onClick="validateHrefVulnerability(this);getListOfColName()"  data-toggle="tab" href="#menu3" >Comparative Analytics</a></li> <!-- Comparison Chart -->
      <li><a onClick="validateHrefVulnerability(this);loadRollUpAnalysis()"  data-toggle="tab" href="#menu4" >Roll-Up Analysis</a></li>
    </ul>
  
    <div class="tab-content">
      <div id="home_table" class="tab-pane fade in active">
        		
							<div class="portlet-body">
								<input type="hidden" id="tableName" value="${ColSumm_TableName}">
								<table id="ColSummTable"
									class="table table-striped table-bordered  table-hover"
									style="width: 100%;">
									<thead>
										<tr>
											<th style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">Date</th>
											<th>Run</th>
											<th style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">Status</th>
											<th>Learn</th>
											<th>DQI</th>
											<th>Col_Name</th>
											<th>Count</th>
											<th>Min</th>
											<th>Max</th>
											<th>Std_Dev</th>
											<th>Mean</th>
											<th>Num_Mean_Avg</th>
											<th>Num_Mean_Std_Dev</th>
											<th>Num_Mean_Deviation</th>
											<th>Num_Mean_Threshold</th>
											<th>Num_SD_Avg</th>
											<th>Num_SD_Std_Dev</th>
											<th>Num_SD_Deviation</th>
											<th>Num_SD_Threshold</th>
											<th>Microsegment_Val</th>
											<th>Microsegment</th>
											<th>Total</th> <!-- Sum_Of_Num_Stat -->
											<th>Hist Avg(Total)</th> <!-- Sum_Avg -->
											<th>Hist Std_dev (Total)</th> <!-- Sum_Std_Dev -->
											<th>Sum_Threshold</th>
											<th>NumMeanStatus</th>
											<th>NumSDStatus</th>
											<th>NumSumStatus</th>
										</tr>
									</thead>
								</table>
							</div>
						
      </div>
      <div id="menu1" class="tab-pane fade">
      	<div class="row" style="margin-top: 5%">
      		<div class="col-sm-4">
      			<div class="dropdown">
				  <button class="btn btn-primary dropdown-toggle" type="button" data-toggle="dropdown">Select MicroSegment Val
				  <span id="trendChart-dropdown-menu-Span"></span><span class="caret"></span></button>
				  <ul class="dropdown-menu force-scroll" id="trendChart-dropdown-menu">
				  </ul>
				</div>
			</div>
      		<div class="col-sm-4">
      			<div class="dropdown">
				  <button class="btn btn-primary dropdown-toggle" type="button" data-toggle="dropdown">Select Column Name
				  <span id="trendChart-colname-dropdown-menu-Span"></span><span class="caret"></span></button>
				  <ul class="dropdown-menu force-scroll" id="trendChart-colname-dropdown-menu">
				  </ul>
				</div>
      		
      		</div>
      		<div class="col-sm-4"><button type="button" class="btn btn-primary" onclick="trenchartcall() ">Show Chart</button></div>
      	</div>
      	<div class="row" style="margin-top: 3%" >
      	<div class="col-sm-2"></div>
      	<div class="col-sm-8" id="avg-trend-chart-headerid"></div>
      	<div class="col-sm-2"></div>
      	</div>
        	<div id="avTrendChart_div"></div>
      </div>
      
      
      <div id="menu2" class="tab-pane fade">
      	<div class="row" style="margin-top: 5%">
      		<div class="col-sm-4">
      				<div class="dropdown">
				  <button class="btn btn-primary dropdown-toggle" type="button" data-toggle="dropdown">Select MicroSegment Val
				  <span id="stddevtrendChart-dropdown-menu-Span"></span><span class="caret"></span></button>
				  <ul class="dropdown-menu force-scroll" id="stddevtrendChart-dropdown-menu">
				  </ul>
				</div>
      		</div>
      		<div class="col-sm-4">
      			<div class="dropdown">
				  <button class="btn btn-primary dropdown-toggle" type="button" data-toggle="dropdown">Select Column Name
				  <span id="stddevtrendChart-colname-dropdown-menu-Span"></span><span class="caret"></span></button>
				  <ul class="dropdown-menu force-scroll" id="stddevtrendChart-colname-dropdown-menu">
				  </ul>
				</div>
      		
      		</div>
      		<div class="col-sm-4"><button type="button" class="btn btn-primary" onclick="stddevtrenchartcall() ">Show Chart</button></div>
      	</div>
      		<div class="row" style="margin-top: 3%">
      	<div class="col-sm-2"></div>
      	<div class="col-sm-8" id="std-dev-avg-trend-chart-headerid"></div>
      	<div class="col-sm-2"></div>
      	</div>
        	<div id="avStdDevTrendChart_div"></div>
      </div>
      
      
      <div id="menu3" class="tab-pane fade">
      	<div class="row" style="margin-top: 5%">
      		<div class="col-sm-3"> 
      			<div class="dropdown">
				  <button class="btn btn-primary dropdown-toggle" type="button" data-toggle="dropdown">Select Column
				  <span id="comparisonTrendChart-dropdown-menu-span"></span><span class="caret"></span></button>
				  <ul class="dropdown-menu force-scroll" id="comparisonTrendChart-dropdown-menu">
				  </ul>
				</div>     		
      		</div>
      		<div class="col-sm-6" id="comparisonTrendChartDGroupValParent">
      		</div>
      		<div class="col-sm-3">
      			<button type="button" class="btn btn-primary" onclick="comparisonChartCall()">Load Chart</button>
      		</div>
      		
      	</div>
      	<div class="row" style="margin-top: 3%" >
	      	<div class="col-sm-2"></div>
	      	<div class="col-sm-8" id="comparison-chart-headerid"></div>
	      	<div class="col-sm-2"></div>
      	</div>
        <div id="trendChart_div"></div>
      </div>
      
      <div id="menu4" class="tab-pane fade">
       		<div class="row" style="margin-top: 3%">
       			<div class="col-sm-4">
				  <div class="dropdown">
				  <button class="btn btn-primary dropdown-toggle" type="button" data-toggle="dropdown">Select Roll-Up Variable
				  <span id="distributioncheckRollUpVariableSelectSpanid"></span>	<span class="caret"></span></button>
				  <ul class="dropdown-menu force-scroll" id="distributioncheckRollUpVariableSelectid">
				  </ul>
				</div>
				  
       			</div>
       			<div class="col-sm-4">
   
				  <div class="dropdown">
				  <button class="btn btn-primary dropdown-toggle" type="button" data-toggle="dropdown">Select Column Name
				  <span id="distributioncheckRollUpVariableSelectColumnNameSpanid"></span><span class="caret"></span></button>
				  <ul class="dropdown-menu force-scroll" id="distributioncheckRollUpVariableSelectColumnNameid">
				  </ul>
				</div>
       			</div>
       			<div class="col-sm-4">
       				<button type="button" class="btn btn-primary" onclick="loadrollupTabDetails()">Load Details</button>
       			</div>
       		</div>
       		<div class="row" style="margin-top: 3%;">
       		<div class="col-sm-2">
       		</div>
       		<div class="col-sm-8">
       			<div id="rollupdropdownErrMsg"> </div>
       		</div>
       		<div class="col-sm-2">
       		</div>
       			
       		</div>
       		
       		<!-- tabs within rollup tabs -->
       		<div class="" id="rolluptabs">
  					<div class="" style="margin-top: 3%;">
       			<ul class="nav nav-tabs">
				  <li class="nav-item">
				    <a onClick="validateHrefVulnerability(this)"  class="nav-link active" data-toggle="tab" href="#rollUphome">Table</a>
				  </li>
				  <li class="nav-item">
				    <a onClick="validateHrefVulnerability(this)"  class="nav-link" data-toggle="tab" href="#rollUpmenu1">Trend Chart</a> <!-- TrendBreack Chart  -->
				  </li>
				  <li class="nav-item">
				    <a onClick="validateHrefVulnerability(this);callRollUpComparisonChart()"  class="nav-link" data-toggle="tab"  href="#rollUpmenu2">Comparison Chart</a>
				  </li>
				</ul>
				
				<!-- Tab panes -->
				<div class="tab-content">
				  <div class="tab-pane active" id="rollUphome">
				 
				 	<div class="portlet-body">
				 			  
				  <div class="row">
				
				  <div class="col-sm-12">
				  		<div class="portlet-body">
				  		<table id="rollupTable"
									class="table table-striped table-bordered  table-hover"
									style="width: 100%;">
									<thead>
										<tr>
											<th>Date</th>
											<th>Run</th>
									<!--  		<th>dayOfYear</th>
											<th>month</th>
											<th>Day_Of_Month</th>
											<th>Day_Of_Week</th>
											<th>Hour_Of_Day</th> -->
											<th>Col_Name</th>
											<th style="width:100px">Status</th>
											<th>Record_Count</th>
											<th>Min</th>
											<th>Max</th>
											<th>Threshold</th>
											<th>Total</th>
											<th>Hist Avg</th>
											<th>Hist Std_dev</th>
											<th>RollUp Column</th>
											<th>Mean</th>
											
											<th>UPPER LIMIT</th>
											<th>LOWER LIMIT</th>
										</tr>
									</thead>
								</table>
				  	</div>
				  </div>
				  </div>
				 	</div>
				  </div>
				  <div class="tab-pane container fade" id="rollUpmenu1">
				  
				<div class="row" style="margin-top: 3%;">
					<div class="col-sm-4">
								<div class="dropdown">
									<button class="btn btn-primary dropdown-toggle" type="button"
										data-toggle="dropdown">
										Select Roll-Up Variable <span id="distributioncheckRollUpTrendBreakSelectSpanid"></span> <span class="caret"></span>
									</button>
									<ul class="dropdown-menu force-scroll"
										id="distributioncheckRollUpTrendBreakSelectid">
									</ul>
								</div>
					</div>
					<div class="col-sm-4"></div>
					<div class="col-sm-4">
						<button type="button" class="btn btn-primary" onclick="distributionRollUpTrendBreakElement()">Load Chart</button>
					</div>
				</div>
				
				<div class="row" style="margin-top: 3%">
			      	<div class="col-sm-2"></div>
			      	<div class="col-sm-8" id="distributionRollUpTrendBreakElement-chart-headerid"></div>
			      	<div class="col-sm-2"></div>
		      	</div>
				  	<div class="row">
				  		<div id="rolluptrendBreakChart_div"></div>
				  	</div>
				  </div>
				  <div class="tab-pane container fade" id="rollUpmenu2">
					  <div class="row" style="margin-top: 5%">
			      		<div class="col-sm-6" id="rollupAnanlysisComparisonChartRollupVariableDropdownParent">
									      				<select  class="chosen-select" multiple tabindex="4">
									<option value=""></option>
									</select>
						</div>
			      		<div class="col-sm-3">
			      		<button type="button" class="btn btn-primary" onclick="showRollUpAnanlysisComparisonChart()">Show Chart</button>
			      		</div>
			      		<div class="col-sm-3"></div>
	      			</div>
					<div class="row" style="margin-top: 3%">
				      	<div class="col-sm-2"></div>
				      	<div class="col-sm-8" id="rollupComparisonChartDivId-chart-headerid"></div>
				      	<div class="col-sm-2"></div>
				     </div>
				  	<div class="row">
				  		<div id="rollupComparisonChartDivId"></div>
				  	</div>
				</div>
				</div>
       		</div>
  		</div>
      </div>
      
      <div id="menu5" class="tab-pane fade">
		 <!-- sum of num stats tab-content code starts -->
		 <div class="row" style="margin-top: 5%">
      		<div class="col-sm-4">
      			<div class="dropdown">
				  <button class="btn btn-primary dropdown-toggle" type="button" data-toggle="dropdown">Select MicroSegment Val
				  <span id="sumOfNumStatsChart-Span"></span><span class="caret"></span></button>
				  <ul class="dropdown-menu force-scroll" id="sumOfNumStatsChart-dropdown-menu">
				  </ul>
				</div>
			</div>
      		<div class="col-sm-4">
      			<div class="dropdown">
				  <button class="btn btn-primary dropdown-toggle" type="button" data-toggle="dropdown">Select Column Name
				  <span id="sumOfNumStatsChart-colname-Span"></span><span class="caret"></span></button>
				  <ul class="dropdown-menu force-scroll" id="sumOfNumStatsChart-colname-dropdown-menu">
				  </ul>
				</div>
      		
      		</div>
      		<div class="col-sm-4"><button type="button" class="btn btn-primary" onclick="sumOfNumStatsChartDropdownMenuValueSection() ">Show Chart</button></div>
      	</div>
      	<div class="row" style="margin-top: 3%" >
      	<div class="col-sm-2"></div>
      	<div class="col-sm-8" id="sum-chart-headerid"></div>
      	<div class="col-sm-2"></div>
      	</div>
        	<div id="avTrendChart_div"></div>
		 	<div class="row">
		 		<div id="sumOfNumStatsChart_div"></div>
		 	</div>
		  <!-- sum of num stats tab-content code ends -->
	 </div>
    </div>

	 <br><br><br><br> 
      <hr />  

	  <div class="portlet-title">
    	<div class="caption font-red-sunglo">
            <span class="caption-subject bold ">Custom Microsegments Column Summary</span>
        </div> 
        <hr />  
      </div>  
            <!-- Adding table and charts for custom microsegments -->
            	<ul class="nav nav-tabs">
                      <li class="active"><a onClick="validateHrefVulnerability(this)"  data-toggle="tab" href="#home_table2">Table</a></li>
                      <li><a onClick="validateHrefVulnerability(this);custMcsgTrendChartColumnsDropDown()"  data-toggle="tab" href="#custom_menu1" >Avg Trend</a></li>
                      <li><a onClick="validateHrefVulnerability(this);custMcsgStdDevTrendChartColumnsDropDown()"  data-toggle="tab" href="#custom_menu2" >Shape Trend</a></li> <!-- Distribution Trend -->
                       <li><a onClick="validateHrefVulnerability(this);custMcsgSumOfNumStatsColumnsDropdown()"  data-toggle="tab" href="#custom_menu3" >Total Vol/ Amt Trend</a></li> <!-- Sum Trend -->
                      <li><a onClick="validateHrefVulnerability(this);custMcsgGetListOfColName()"  data-toggle="tab" href="#custom_menu4" >Comparative Analytics</a></li> <!-- Comparison Chart -->
                    </ul>

                   <div class="tab-content">
                      <div id="home_table2" class="tab-pane fade in active">
                         <div class="portlet-body">
                            <input type="hidden" id="tableName2" value="${Custom_microseg_ColSumm_TableName}">
                            <table id="ColSummTable2"
                               class="table table-striped table-bordered  table-hover"
                               style="width: 100%;">
                               <thead>
                                  <tr>
                                     <th style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">Date</th>
                                     <th>Run</th>
                                     <th style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">Status</th>
                                     <th>DQI</th>
                                     <th>Col_Name</th>
                                     <th>Count</th>
                                     <th>Min</th>
                                     <th>Max</th>
                                     <th>Std_Dev</th>
                                     <th>Mean</th>
                                     <th>Num_Mean_Avg</th>
                                     <th>Num_Mean_Std_Dev</th>
                                     <th>Num_Mean_Deviation</th>
                                     <th>Num_Mean_Threshold</th>
                                     <th>Num_SD_Avg</th>
                                     <th>Num_SD_Std_Dev</th>
                                     <th>Num_SD_Deviation</th>
                                     <th>Num_SD_Threshold</th>
                                     <th>Microsegment_Val</th>
                                     <th>Microsegment</th>
                                     <th>Total</th>
                                     <!-- Sum_Of_Num_Stat -->
                                     <th>Hist Avg(Total)</th>
                                     <!-- Sum_Avg -->
                                     <th>Hist Std_dev (Total)</th>
                                     <!-- Sum_Std_Dev -->
                                     <th>Sum_Threshold</th>
                                     <th>NumMeanStatus</th>
                                     <th>NumSDStatus</th>
                                     <th>NumSumStatus</th>
                                  </tr>
                               </thead>
                            </table>
                         </div>
                      </div>

                      <div id="custom_menu1" class="tab-pane fade">
                            <div class="row" style="margin-top: 5%">
                            <div class="col-sm-4">
                                  <div class="dropdown">
                                     <button class="btn btn-primary dropdown-toggle" type="button" data-toggle="dropdown">Select Column Name
                                     <span id="custmcsg-trendChart-colname-dropdown-menu-Span"></span><span class="caret"></span></button>
                                     <ul class="dropdown-menu force-scroll" id="custmcsg-trendChart-colname-dropdown-menu">
                                     </ul>
                                  </div>
                               </div>
                               <div class="col-sm-4">
                                  <div class="dropdown">
                                     <button class="btn btn-primary dropdown-toggle" type="button" data-toggle="dropdown">Select MicroSegment Val
                                     <span id="custmcsg-trendChart-dropdown-menu-Span"></span><span class="caret"></span></button>
                                     <ul class="dropdown-menu force-scroll" id="custmcsg-trendChart-dropdown-menu">
                                     </ul>
                                  </div>
                               </div>
                               <div class="col-sm-4"><button type="button" class="btn btn-primary" onclick="custMicrosegTrendChartcall()">Show Chart</button></div>
                            </div>
                            <div class="row" style="margin-top: 3%" >
                               <div class="col-sm-2"></div>
                               <div class="col-sm-8" id="custmcsg-avg-trend-chart-headerid"></div>
                               <div class="col-sm-2"></div>
                            </div>
                            <div id="custmcsg_avTrendChart_div"></div>
                         </div>
                         
                      		<div id="custom_menu2" class="tab-pane fade">
   							<div class="row" style="margin-top: 5%">
	   							<div class="col-sm-4">
					      			<div class="dropdown">
									  <button class="btn btn-primary dropdown-toggle" type="button" data-toggle="dropdown">Select Column Name
									  <span id="custmcsg-stddevtrendChart-colname-dropdown-menu-Span"></span><span class="caret"></span></button>
									  <ul class="dropdown-menu force-scroll" id="custmcsg-stddevtrendChart-colname-dropdown-menu">
									  </ul>
									</div>
					      		</div>
					      		<div class="col-sm-4">
				      				<div class="dropdown">
								  <button class="btn btn-primary dropdown-toggle" type="button" data-toggle="dropdown">Select MicroSegment Val
								  <span id="custmcsg-stddevtrendChart-dropdown-menu-Span"></span><span class="caret"></span></button>
								  <ul class="dropdown-menu force-scroll" id="custmcsg-stddevtrendChart-dropdown-menu">
								  </ul>
								</div>
				      		</div>
				      		<div class="col-sm-4"><button type="button" class="btn btn-primary" onclick="custmcsgStddevtrenchartcall() ">Show Chart</button></div>
				      	</div>
				      		<div class="row" style="margin-top: 3%">
				      	<div class="col-sm-2"></div>
				      	<div class="col-sm-8" id="custmcsg-std-dev-avg-trend-chart-headerid"></div>
				      	<div class="col-sm-2"></div>
				      	</div>
				        <div id="custmcsg_avStdDevTrendChart_div"></div>
				      </div>
				      
				      <div id="custom_menu3" class="tab-pane fade">
						 <!-- sum of num stats tab-content code starts -->
						 <div class="row" style="margin-top: 5%">
				      		<div class="col-sm-4">
				      			<div class="dropdown">
								  <button class="btn btn-primary dropdown-toggle" type="button" data-toggle="dropdown">Select Column Name
								  <span id="custmcsg-sumOfNumStatsChart-colname-Span"></span><span class="caret"></span></button>
								  <ul class="dropdown-menu force-scroll" id="custmcsg-sumOfNumStatsChart-colname-dropdown-menu">
								  </ul>
								</div>
				      		</div>
				      		<div class="col-sm-4">
				      			<div class="dropdown">
								  <button class="btn btn-primary dropdown-toggle" type="button" data-toggle="dropdown">Select MicroSegment Val
								  <span id="custmcsg-sumOfNumStatsChart-Span"></span><span class="caret"></span></button>
								  <ul class="dropdown-menu force-scroll" id="custmcsg-sumOfNumStatsChart-dropdown-menu">
								  </ul>
								</div>
							</div>
				      		<div class="col-sm-4"><button type="button" class="btn btn-primary" onclick="custmcsgSumOfNumStatsChartDropdownMenuValueSection() ">Show Chart</button></div>
				      	</div>
				      	<div class="row" style="margin-top: 3%" >
				      	<div class="col-sm-2"></div>
				      	<div class="col-sm-8" id="custmcsg-sum-chart-headerid"></div>
				      	<div class="col-sm-2"></div>
				      	</div>
					 	<div class="row">
					 		<div id="custmcsg_sumOfNumStatsChart_div"></div>
					 	</div>
						  <!-- sum of num stats tab-content code ends -->
					</div>
					
					 <div id="custom_menu4" class="tab-pane fade">
				      	<div class="row" style="margin-top: 5%">
				      		<div class="col-sm-3"> 
				      			<div class="dropdown">
								  <button class="btn btn-primary dropdown-toggle" type="button" data-toggle="dropdown">Select Column
								  <span id="custmcsg-comparisonTrendChart-dropdown-menu-span"></span><span class="caret"></span></button>
								  <ul class="dropdown-menu force-scroll" id="custmcsg-comparisonTrendChart-dropdown-menu">
								  </ul>
								</div>     		
				      		</div>
				      		<div class="col-sm-6" id="custmcsg-comparisonTrendChartDGroupValParent">
				      		</div>
				      		<div class="col-sm-3">
				      			<button type="button" class="btn btn-primary" onclick="custmcsgComparisonChartCall()">Load Chart</button>
				      		</div>
				      		
				      	</div>
				      	<div class="row" style="margin-top: 3%" >
					      	<div class="col-sm-2"></div>
					      	<div class="col-sm-8" id="custmcsg-comparison-chart-headerid"></div>
					      	<div class="col-sm-2"></div>
				      	</div>
				        <div id="custmcsg_trendChart_div"></div>
				  </div>

                </div>
		 
	<div class="cd-popup2" id="popSample">
		<div class="cd-popup-container1" id="outerContainer1">
			<div id="loading-data-progress" class="ajax-loader hidden">
				<h4>
					<b>Summary DQI Data Loading in Progress..</b> 
				</h4>
				<img
						src="${pageContext.request.contextPath}/assets/global/img/loading-circle.gif"
						class="img-responsive" />
			</div>
			<br/><br/><br/><br/>
			<div id="chartDiv"
				style="height: 500px; width: 100%; margin: 0 auto;"
				class="dashboard-summary-chart"></div>
			<button type="submit" id="dqiDataCloseBtn" class="btn blue">Close</button>
		</div>
	</div>
	<jsp:include page="downloadCsvReports.jsp"/>
	<jsp:include page="footer.jsp" />
</body>
<script src="./assets/pages/chosendropdown/js/chosen.jquery.js" type="text/javascript"></script>
<script src="./assets/pages/chosendropdown/js/init.js" type="text/javascript"></script>
<script src="./assets/pages/chosendropdown/js/prism.js" type="text/javascript"></script>
<script>

jQuery(document).ready(function() {
	
	//$('#comparisonTrendChart-dgroupVal-dropdown-menu').selectpicker();
		
	//on page load hide rollup tabs
	$('#rolluptabs').hide();	
	
	$('#filter-chart-content').hide();
	var table;
	console.log("hello");
	var tableName=$( "#tableName" ).val();
	var tableName2=$( "#tableName2" ).val();
	var idApp=$( "#idApp" ).val();
	formdatatableName = tableName;
	microDataValueData = {
	tableName : formdatatableName,
	idApp : idApp
	};
	
	advancefilter($("#idApp").val(), formdatatableName, 'ColSummTableForNumericTab', 'ColSummTable'); //function code is written in search.js
	loadCustomMicrosegmentSummaryData('ColSummTable2', 'getCustomMicrosegmentSummaryDetails',tableName2, idApp,"");
});
</script>
<script>

  $(document).ready(function() {
    	
        $('.hrefCall').click(function() {
        	 $('#popSample').addClass('is-visible');
            var validationCheck=$(this).attr('id');
           // alert($(this).attr('id'));
            var form_data = {
                validationCheck: validationCheck,
                idApp: $("#idApp").val(),
            };
            $('#loading-data-progress').removeClass('hidden').show();	
            console.log(form_data);
            $.ajax({
                url: './getDQIScoresMap',
                type: 'POST',
                headers: { 'token':$("#token").val()},
                datatype: 'json',
                data: form_data,
                success: function(listOfValues) {
                    console.log(listOfValues);
                    if (typeof listOfValues !== "undefined" && listOfValues != "") {
                    	
                    	google.load("visualization", "1", {packages:["corechart"]});
                        google.setOnLoadCallback(drawChart);
                        var data1 = new google.visualization.DataTable();
                        data1.addColumn('string', 'Date (Run)');
              		    data1.addColumn('number', 'DQI');
              		 
              		  //data1.addRow([new Date("2016-02-04"),90]);
                        $.each(listOfValues, function(key, value) {	
                        	$.each( value, function( i, l ){
                        		 // alert( "Index #" + i + ": " + l );
        					data1.addRow([key,l]);
                        	});
        				});
                        data1.sort({column: 0, asc: true});
                        var title="";
                        if(validationCheck=="Aggregate DQI Summary"){
                        	title=validationCheck;
                        }else{
                        	title=validationCheck + " DQI";
                        }
                        var len = $.map(listOfValues, function(n, i) { return i; }).length;
                        console.log(len);
                        var showTextEvery=2;
                      	if(len<=10){
                      		showTextEvery=1;
                      	}else if(len>10 && len<=20){
                      		showTextEvery=2;
                      	}else if(len>20){
                      		showTextEvery=(len/10);
                      	}
                      	showTextEvery=Math.round(showTextEvery);
                      	
                        //alert(len);
                        var options = {
                            title: title,
                            hAxis: {title: 'Date (Run)',  titleTextStyle: {color: '#333'},direction:-1, slantedText:true, slantedTextAngle:45,showTextEvery:showTextEvery},
                            vAxis: {minValue: 0},
                            chartArea: {bottom: 100},
			    			height: 400,
                            width: 1000
                        };
                        $('#loading-data-progress').addClass('hidden');
                        var chart = new google.visualization.AreaChart(document.getElementById('chartDiv'));
                        chart.draw(data1, options);
                    	
                    	
                        $('#chartDiv').removeClass('hidden');
                    } else {
                        $('#chartDiv').addClass('hidden');
                    }

                },
                error: function(xhr, textStatus,
                    errorThrown) {
                    $('#initial').hide();
                    $('#fail').show();
                }
            });
        });
        $( "#dqiDataCloseBtn" ).click(function() {
        	  $('#popSample').removeClass('is-visible');
        	});
    });
  
  
  /*trend chart */
  function trendChart(tablename) {
		var tablename = $("#tableName").val();
		var idApp = $("#idApp").val();
		$(function() {
			var form_data = {
				tableName : tablename
			};
			var request = $.ajax({
				url : './avgTrendChart',
				type : 'POST',
				headers: { 'token':$("#token").val()},
				data : form_data,
				dataType : 'json'
			});
			request.done(function(msg) {
				runTrendChart(msg);
			});
			request.fail(function(jqXHR, textStatus) {
				console.log("trendChart Request failed: " + textStatus);
			});
		});
	}
	
	function runTrendChart(obj){
		var children = obj.children;		
		var data = google.visualization.arrayToDataTable(children);
	     data.sort({
	         column: 0,
	         desc: true
	       }); 
		
		var options = {
		          title : '',
		          vAxis: {title: 'Mean'},
		          hAxis: {title: 'Date & Run',direction:-1, slantedText:true, slantedTextAngle:45},
		          seriesType: 'line',
		          series: {1: {color: 'black',
		                lineWidth: 0,
		                pointSize: 5}},
		                chartArea:{left:80,top:10,width:'70%'},
		    			width : 1000,
		    			height : 400,
		    			isStacked: true
		    };
		
		var chart = new google.visualization.ComboChart(document.getElementById('avTrendChart_div'));
        chart.draw(data, options);
	}
	
	function custmcsgRunTrendChart(obj){
		var children = obj.children;		
		var data = google.visualization.arrayToDataTable(children);
	     data.sort({
	         column: 0,
	         desc: true
	       }); 
		
		var options = {
		          title : '',
		          vAxis: {title: 'Mean'},
		          hAxis: {title: 'Date & Run',direction:-1, slantedText:true, slantedTextAngle:45},
		          seriesType: 'line',
		          series: {1: {color: 'black',
		                lineWidth: 0,
		                pointSize: 5}},
		                chartArea:{left:80,top:10,width:'70%'},
		    			width : 1000,
		    			height : 400,
		    			isStacked: true
		    };
		
		var chart = new google.visualization.ComboChart(document.getElementById('custmcsg_avTrendChart_div'));
        chart.draw(data, options);
	}
	
	function stdDevTrendChart(){
		console.log('stdDevTrendChart started');
		var tablename = $("#tableName").val();
		var idApp = $("#idApp").val();
		console.log(tablename);
		$(function() {
			var form_data = {	
				tableName : tablename,
				idApp : idApp
			};
			var request = $.ajax({
				url : './stdDevavgTrendChart',
				type : 'POST',
				headers: { 'token':$("#token").val()},
				data : form_data,
				dataType : 'json'
			});
			request.done(function(msg) {
				runStdDevTrendChart(msg);
			});
			request.fail(function(jqXHR, textStatus) {
				console.log("stdDevTrendChart Request failed: " + textStatus);
			});
		});
	}
	
	function custmcsgStdDevTrendChart(){
		console.log('stdDevTrendChart started');
		var tablename = $("#tableName2").val();
		var idApp = $("#idApp").val();
		console.log(tablename);
		$(function() {
			var form_data = {	
				tableName : tablename,
				idApp : idApp
			};
			var request = $.ajax({
				url : './stdDevavgTrendChart',
				type : 'POST',
				headers: { 'token':$("#token").val()},
				data : form_data,
				dataType : 'json'
			});
			request.done(function(msg) {
				custmcsgRunStdDevTrendChart(msg);
			});
			request.fail(function(jqXHR, textStatus) {
				console.log("stdDevTrendChart Request failed: " + textStatus);
			});
		});
	}
	
	function runStdDevTrendChart(obj){children
		var children = obj.children;
	
		var data = new google.visualization.DataTable();
		data.addColumn('string', 'Date & Run');
		data.addColumn('number', 'Upper Limit');
		data.addColumn('number', 'Standard Deviation');
		data.addColumn('number', 'Lower Limit');
		data.addRows(children);

		 data.sort({
	         column: 0,
	         desc: true
	       });
		 
		var options = {
			title : '',
			vAxis : {
				title : 'Std Dev'
			},
			hAxis : {
				title : 'Date & Run',direction:-1, slantedText:true, slantedTextAngle:45
			},
			seriesType : 'line',
			series : {
				1 : {
					color : 'black',
					lineWidth : 0,
					pointSize : 5
				}
			},
			 chartArea:{left:80,top:10,width:'70%'},
				width : 1000,
				height : 400,
				isStacked: true
		};

		var chart = new google.visualization.ComboChart(document.getElementById('avStdDevTrendChart_div'));
		chart.draw(data, options);
	}
	
	function custmcsgRunStdDevTrendChart(obj){
		var children = obj.children;
	
		var data = new google.visualization.DataTable();
		data.addColumn('string', 'Date & Run');
		data.addColumn('number', 'Upper Limit');
		data.addColumn('number', 'Standard Deviation');
		data.addColumn('number', 'Lower Limit');
		data.addRows(children);

		 data.sort({
	         column: 0,
	         desc: true
	       });
		 
		var options = {
			title : '',
			vAxis : {
				title : 'Std Dev'
			},
			hAxis : {
				title : 'Date & Run',direction:-1, slantedText:true, slantedTextAngle:45
			},
			seriesType : 'line',
			series : {
				1 : {
					color : 'black',
					lineWidth : 0,
					pointSize : 5
				}
			},
			 chartArea:{left:80,top:10,width:'70%'},
				width : 1000,
				height : 400,
				isStacked: true
		};

		var chart = new google.visualization.ComboChart(document.getElementById('custmcsg_avStdDevTrendChart_div'));
		chart.draw(data, options);
	}

	function trendChartDropDown() {
		var tablename = $("#tableName").val();
		var idApp =  $("#idApp").val();
		$(function() {
			var form_data = {
				tableName : tablename,
				idApp : idApp
			};
			var request = $.ajax({
				url : './listOfDGroupValByTableName',
				type : 'POST',
				headers: { 'token':$("#token").val()},
				data : form_data,
				dataType : 'json'
			});
			request.done(function(msg) {
				bindTrendChartDropDown(msg);
			});
			request.fail(function(jqXHR, textStatus) {
						console.log("trendChartDropDown Request failed: "+ textStatus);
					});
		});
	}
	
	function bindTrendChartDropDown(obj) {
		var dGroupValObj = obj.dGroupVal;
		var colNameObj = obj.ColName;

		/*bind dgroupval*/
		var arrayLength = dGroupValObj.length;
		var $dropdown = $("#trendChart-dropdown-menu");
		$("#trendChart-dropdown-menu").empty();
		for (var i = 0; i < arrayLength; i++) {
			$("#trendChart-dropdown-menu").append("<li><a onClick='validateHrefVulnerability(this);distributionAvgTrendChartDGroupValOnClick(this)' >"+ dGroupValObj[i] + "</a></li>");
		}

		/*bind colname*/
		var arrayLength = colNameObj.length;
		var $dropdown = $("#trendChart-dropdown-menu");
		$("#trendChart-colname-dropdown-menu").empty();
		for (var i = 0; i < arrayLength; i++) {
			$("#trendChart-colname-dropdown-menu").append("<li><a onClick='validateHrefVulnerability(this);distributionAvgTrendChartColNameOnClick(this)'  >"+ colNameObj[i] + "</a></li>");
		}
	}
	
	function custMcsgTrendChartColumnsDropDown() {
		var tablename = $("#tableName2").val();
		var idApp =  $("#idApp").val();
		$(function() {
			var form_data = {
				tableName : tablename,
				idApp : idApp
			};
			var request = $.ajax({
				url : './listOfDistributionCheckColumnsByTableName',
				type : 'POST',
				headers: { 'token':$("#token").val()},
				data : form_data,
				dataType : 'json'
			});
			request.done(function(msg) {
				custMcsgBindTrendChartColumnsDropDown(msg);
			});
			request.fail(function(jqXHR, textStatus) {
						console.log("trendChartDropDown2 Request failed: "+ textStatus);
					});
		});
	}
	
	function custMcsgBindTrendChartColumnsDropDown(obj) {
        var colNameObj = obj;
        /*bind colname*/
        var arrayLength = colNameObj.length;
        var $dropdown = $("#custmcsg-trendChart-dropdown-menu");
        $("#custmcsg-trendChart-colname-dropdown-menu").empty();
        for (var i = 0; i < arrayLength; i++) {
          $("#custmcsg-trendChart-colname-dropdown-menu").append("<li><a onClick='validateHrefVulnerability(this);custmcsgDistributionAvgTrendChartColNameOnClick(\""+colNameObj[i]+"\");'>"+ colNameObj[i] + "</a></li>");
        }
      }
	
	function custMcsgTrendChartDGroupValDropDown(param) {
		console.log("custMcsgTrendChartDGroupValDropDown: " + param);
		var tablename = $("#tableName2").val();
		var columnName = param;
		var idApp =  $("#idApp").val();
		$(function() {
			var form_data = {
				tableName : tablename,
				columnName : columnName,
				idApp : idApp
			};
			
			console.log("custMcsgTrendChartDGroupValDropDown:form_data: " + form_data);
			var request = $.ajax({
				url : './listOfDGroupValForColumnByTableName',
				type : 'POST',
				headers: { 'token':$("#token").val()},
				data : form_data,
				dataType : 'json'
			});
			request.done(function(msg) {
				custMcsgBindTrendChartDGroupValDropDown(msg);
			});
			request.fail(function(jqXHR, textStatus) {
						console.log("trendChartDropDown2 Request failed: "+ textStatus);
					});
		});
	}

	function custMcsgBindTrendChartDGroupValDropDown(obj) {
        var dGroupValObj = obj;
        
        /*bind dgroupval*/
        var arrayLength = dGroupValObj.length;
        var $dropdown = $("#custmcsg-trendChart-dropdown-menu");
        $("#custmcsg-trendChart-dropdown-menu").empty();
        for (var i = 0; i < arrayLength; i++) {
          $("#custmcsg-trendChart-dropdown-menu").append("<li><a onClick='validateHrefVulnerability(this);custmcsgDistributionAvgTrendChartDGroupValOnClick(this)' >"+ dGroupValObj[i] + "</a></li>");
        }

      }

	var avgtrendChartDgroupVal;
	var avgtrendChartColName;

	function distributionAvgTrendChartDGroupValOnClick(param) {
		document.getElementById("trendChart-dropdown-menu-Span").innerHTML = "";
		avgtrendChartDgroupVal = $(param).text();
		document.getElementById("trendChart-dropdown-menu-Span").innerHTML = "["+ avgtrendChartDgroupVal + "]";
	}

	function distributionAvgTrendChartColNameOnClick(param) {
		document.getElementById("trendChart-colname-dropdown-menu-Span").innerHTML = "";
		avgtrendChartColName = $(param).text();
		document.getElementById("trendChart-colname-dropdown-menu-Span").innerHTML = "["+ avgtrendChartColName + "]";
	}
	
	function custmcsgDistributionAvgTrendChartDGroupValOnClick(param) {
		document.getElementById("custmcsg-trendChart-dropdown-menu-Span").innerHTML = "";
		avgtrendChartDgroupVal = $(param).text();
		document.getElementById("custmcsg-trendChart-dropdown-menu-Span").innerHTML = "["+ avgtrendChartDgroupVal + "]";
	}

	function custmcsgDistributionAvgTrendChartColNameOnClick(param) {
		document.getElementById("custmcsg-trendChart-colname-dropdown-menu-Span").innerHTML = "";
		avgtrendChartColName = param;
		document.getElementById("custmcsg-trendChart-colname-dropdown-menu-Span").innerHTML = "["+ avgtrendChartColName + "]";
		custMcsgTrendChartDGroupValDropDown(avgtrendChartColName)
	}

	/* Trend Chart Call 	
	 * Updated Date : 5thMay,2020
	 Code By : Anant S. Mahale
	 Ajax call to get Trend Chart Json Object. There are two ways 1) from on click of Trend Chart tab -> trendChartDropDown()
	 get dropdown data (DGroupVal and Column Name) and bind that to avgtrendChartDgroupVal avgtrendChartColName variables OR 
	 2) From main data table on click of graph icon, get its respective DGroupVal and Column Name and generate chart. 
	 */
	function trenchartcall(param) {
		document.getElementById("avg-trend-chart-headerid").innerHTML = "";
		document.getElementById("avg-trend-chart-headerid").innerHTML = "<div class='loader' style='margin-left: 50%;'></div>";
		var tablename = $("#tableName").val();
		var idApp = $("#idApp").val();
		if (param) { // if param is not null
			var paramObj = param.data;
			var arrayLength = paramObj.length;

			for (var i = 0; i < arrayLength; i++) {
				if (i == 0) {
					avgtrendChartDgroupVal = paramObj[i].toString();
				}
				if (i == 1) {
					avgtrendChartColName = paramObj[i].toString();
				}
			}
			$('.nav-tabs a[href="#menu1"]').tab('show'); // enable tab
			document.getElementById("trendChart-dropdown-menu-Span").innerHTML = "";
			document.getElementById("trendChart-colname-dropdown-menu-Span").innerHTML = "";
			document.getElementById("trendChart-dropdown-menu-Span").innerHTML = "["+ avgtrendChartDgroupVal + "]";
			document.getElementById("trendChart-colname-dropdown-menu-Span").innerHTML = "["+ avgtrendChartColName + "]";
			trendChartDropDown(); // bind data to dropdowns
		}
		console.log('trendChart Call : avgtrendChartDgroupVal :: '+ avgtrendChartDgroupVal + ' | avgtrendChartColName :: '+ avgtrendChartColName);

		if (tablename && avgtrendChartDgroupVal && avgtrendChartColName) {
			$(function() {
				var form_data = {
					tableName : tablename,
					DGroupVal : avgtrendChartDgroupVal,
					colName : avgtrendChartColName,
					idApp : idApp
				};
				var request = $.ajax({
					url : './avgTrendChart',
					type : 'POST',
					headers: { 'token':$("#token").val()},
					data : form_data,
					dataType : 'json'
				});
				request.done(function(msg) {
							runTrendChart(msg);
							document.getElementById('avg-trend-chart-headerid').innerHTML = '<h5><b> Average Trend Chart with DGroupVal : '+ avgtrendChartDgroupVal+ ' & Column Name : '+ avgtrendChartColName + ' </b></h5>';
						});
				request.fail(function(jqXHR, textStatus) {
					console.log("trendChart Request failed: " + textStatus);

				});

			});
		} else {
			console.log('table name, avgtrendChartDgroupVal or avgtrendChartColName might be empty or null');
			document.getElementById("avg-trend-chart-headerid").innerHTML = "<div class='alert alert-danger'> <strong>Error!</strong> Please select values from dropdowns </div>";
		}

	}
	 
	 function custMicrosegTrendChartcall(param) {
			document.getElementById("custmcsg-avg-trend-chart-headerid").innerHTML = "";
			document.getElementById("custmcsg-avg-trend-chart-headerid").innerHTML = "<div class='loader' style='margin-left: 50%;'></div>";
			var tablename2 = $("#tableName2").val();
			var idApp = $("#idApp").val();
			if (param) { // if param is not null
				var paramObj = param.data;
				var arrayLength = paramObj.length;

				for (var i = 0; i < arrayLength; i++) {
					if (i == 0) {
						avgtrendChartDgroupVal = paramObj[i].toString();
					}
					if (i == 1) {
						avgtrendChartColName = paramObj[i].toString();
					}
				}
				// bind data to dropdowns
				custMcsgTrendChartColumnsDropDown(); 
				custMcsgTrendChartDGroupValDropDown(avgtrendChartColName);
				$('.nav-tabs a[href="#custom_menu1"]').tab('show'); // enable tab
				document.getElementById("custmcsg-trendChart-dropdown-menu-Span").innerHTML = "";
				document.getElementById("custmcsg-trendChart-colname-dropdown-menu-Span").innerHTML = "";
				document.getElementById("custmcsg-trendChart-dropdown-menu-Span").innerHTML = "["+ avgtrendChartDgroupVal + "]";
				document.getElementById("custmcsg-trendChart-colname-dropdown-menu-Span").innerHTML = "["+ avgtrendChartColName + "]";
			}
			console.log('custMicrosegTrendChartcall Call : avgtrendChartDgroupVal :: '+ avgtrendChartDgroupVal + ' | avgtrendChartColName :: '+ avgtrendChartColName);

			if (tablename2 && avgtrendChartDgroupVal && avgtrendChartColName) {
				$(function() {
					var form_data = {
						tableName : tablename2,
						DGroupVal : avgtrendChartDgroupVal,
						colName : avgtrendChartColName,
						idApp : idApp
					};
					var request = $.ajax({
						url : './avgTrendChart',
						type : 'POST',
						headers: { 'token':$("#token").val()},
						data : form_data,
						dataType : 'json'
					});
					request.done(function(msg) {
							custmcsgRunTrendChart(msg);
								document.getElementById('custmcsg-avg-trend-chart-headerid').innerHTML = '<h5><b> Average Trend Chart with DGroupVal : '+ avgtrendChartDgroupVal+ ' & Column Name : '+ avgtrendChartColName + ' </b></h5>';
							});
					request.fail(function(jqXHR, textStatus) {
						console.log("trendChart Request failed: " + textStatus);

					});

				});
			} else {
				console.log('table name, avgtrendChartDgroupVal or avgtrendChartColName might be empty or null');
				document.getElementById("custmcsg-avg-trend-chart-headerid").innerHTML = "<div class='alert alert-danger'> <strong>Error!</strong> Please select values from dropdowns </div>";
			}

		}

	/* distribution trend - to get data to bind dgroup val and column name dropdowns*/
	function stdDevTrendChartDropDown() {
		var tablename = $("#tableName").val();
		var idApp = $("#idApp").val();
		$(function() {
			var form_data = {
				tableName : tablename,
				idApp : idApp
			};
			var request = $.ajax({
				url : './listOfDGroupValByTableNameForDistribution',
				type : 'POST',
				headers: { 'token':$("#token").val()},
				data : form_data,
				dataType : 'json'
			});
			request.done(function(msg) {
				bindStdDevTrendChartDropDown(msg);
			});
			request.fail(function(jqXHR, textStatus) {
						console.log("trendChartDropDown Request failed: "+ textStatus);
					});
		});
	}
	
	/* distribution trend - bind values to dGroup val dropdown and column name dropdown*/
	function bindStdDevTrendChartDropDown(obj) {
		var dGroupValObj = obj.dGroupVal;
		var colNameObj = obj.ColName;

		/*bind dgroupval*/
		var arrayLength = dGroupValObj.length;
		var $dropdown = $("#trendChart-dropdown-menu");
		$("#stddevtrendChart-dropdown-menu").empty();
		for (var i = 0; i < arrayLength; i++) {
			$("#stddevtrendChart-dropdown-menu").append("<li><a onClick='validateHrefVulnerability(this);distributionStdDevTrendChartDGroupValOnClick(this)'  >"+ dGroupValObj[i] + "</a></li>");
		}

		/*bind colname*/
		var arrayLength = colNameObj.length;
		var $dropdown = $("#trendChart-dropdown-menu");
		$("#stddevtrendChart-colname-dropdown-menu").empty();
		for (var i = 0; i < arrayLength; i++) {
			$("#stddevtrendChart-colname-dropdown-menu").append("<li><a onClick='validateHrefVulnerability(this);distributionStdDevTrendChartColNameOnClick(this)' >"+ colNameObj[i] + "</a></li>");
		}
	}
	
	function custMcsgStdDevTrendChartColumnsDropDown() {
		var tablename = $("#tableName2").val();
		var idApp = $("#idApp").val();
		$(function() {
			var form_data = {
				tableName : tablename,
				idApp : idApp
			};
			var request = $.ajax({
				url : './listOfDistributionCheckColumnsByTableName',
				type : 'POST',
				headers: { 'token':$("#token").val()},
				data : form_data,
				dataType : 'json'
			});
			request.done(function(msg) {
				custmcsgBindStdDevTrendChartColumnsDropDown(msg);
			});
			request.fail(function(jqXHR, textStatus) {
						console.log("trendChartDropDown Request failed: "+ textStatus);
					});
		});
	}
	
	/* distribution trend - bind values to dGroup val dropdown and column name dropdown*/
	function custmcsgBindStdDevTrendChartColumnsDropDown(obj) {
		var colNameObj = obj;

		/*bind colname*/
		var arrayLength = colNameObj.length;
		var $dropdown = $("#custmcsg-trendChart-dropdown-menu");
		$("#custmcsg-stddevtrendChart-colname-dropdown-menu").empty();
		for (var i = 0; i < arrayLength; i++) {
			$("#custmcsg-stddevtrendChart-colname-dropdown-menu").append("<li><a onClick='validateHrefVulnerability(this);custmcsgDistributionStdDevTrendChartColNameOnClick(\""+colNameObj[i]+"\");' >"+ colNameObj[i] + "</a></li>");
		}
	}
	
	function custMcsgStdDevTrendChartDGroupValDropDown(param) {
		var tablename = $("#tableName2").val();
		var columnName = param;
		var idApp = $("#idApp").val();
		$(function() {
			var form_data = {
				tableName : tablename,
				columnName : columnName,
				idApp : idApp
			};
			var request = $.ajax({
				url : './listOfDGroupValForColumnByTableName',
				type : 'POST',
				headers: { 'token':$("#token").val()},
				data : form_data,
				dataType : 'json'
			});
			request.done(function(msg) {
				custmcsgBindStdDevTrendChartDGroupValDropDown(msg);
			});
			request.fail(function(jqXHR, textStatus) {
						console.log("trendChartDropDown Request failed: "+ textStatus);
					});
		});
	}
	
	function custmcsgBindStdDevTrendChartDGroupValDropDown(obj) {
		var dGroupValObj = obj;

		/*bind dgroupval*/
		var arrayLength = dGroupValObj.length;
		var $dropdown = $("#custmcsg-trendChart-dropdown-menu");
		$("#custmcsg-stddevtrendChart-dropdown-menu").empty();
		for (var i = 0; i < arrayLength; i++) {
			$("#custmcsg-stddevtrendChart-dropdown-menu").append("<li><a onClick='validateHrefVulnerability(this);custmcsgDistributionStdDevTrendChartDGroupValOnClick(this)'  >"+ dGroupValObj[i] + "</a></li>");
		}
	}

	var stddevtrendChartDgroupVal;
	var stddevtrendChartColName;

	/* distribution tab - load dgroup dropdown selected value into variable*/
	function distributionStdDevTrendChartDGroupValOnClick(param) {
		document.getElementById("stddevtrendChart-dropdown-menu-Span").innerHTML = "";
		stddevtrendChartDgroupVal = $(param).text();
		document.getElementById("stddevtrendChart-dropdown-menu-Span").innerHTML = "["+ stddevtrendChartDgroupVal + "]";
	}

	/* distribution tab - load column name dropdown selected value into variable*/
	function distributionStdDevTrendChartColNameOnClick(param) {
		document.getElementById("stddevtrendChart-colname-dropdown-menu-Span").innerHTML = "";
		stddevtrendChartColName = $(param).text();
		document.getElementById("stddevtrendChart-colname-dropdown-menu-Span").innerHTML = "["+ stddevtrendChartColName + "]";
	}
	
	/* distribution tab - load dgroup dropdown selected value into variable*/
	function custmcsgDistributionStdDevTrendChartDGroupValOnClick(param) {
		document.getElementById("custmcsg-stddevtrendChart-dropdown-menu-Span").innerHTML = "";
		stddevtrendChartDgroupVal = $(param).text();
		document.getElementById("custmcsg-stddevtrendChart-dropdown-menu-Span").innerHTML = "["+ stddevtrendChartDgroupVal + "]";
	}

	/* distribution tab - load column name dropdown selected value into variable*/
	function custmcsgDistributionStdDevTrendChartColNameOnClick(param) {
		document.getElementById("custmcsg-stddevtrendChart-colname-dropdown-menu-Span").innerHTML = "";
		stddevtrendChartColName = param;
		console.log("custmcsgDistributionStdDevTrendChartColNameOnClick: stddevtrendChartColName: "+stddevtrendChartColName);
		document.getElementById("custmcsg-stddevtrendChart-colname-dropdown-menu-Span").innerHTML = "["+ stddevtrendChartColName + "]";
		custMcsgStdDevTrendChartDGroupValDropDown(stddevtrendChartColName);
	}

	/* call for distribution chart (standard deviation)
	Updated Date : 5thMay,2020
	Code By : Anant S. Mahale
	Ajax call to get Distribution Chart (Standard Deviation Chart / Std Dev Chart) Json Object. There are two ways 1) from on click of Distribution Chart tab -> stdDevTrendChartDropDown()
	get dropdown data (DGroupVal and Column Name) and bind that to stddevtrendChartDgroupVal & stddevtrendChartColName variables OR 
	2) From main data table on click of graph icon, get its respective DGroupVal and Column Name and generate chart. 
	 */

	function stddevtrenchartcall(param) {
		console.log(" #trendChart-dropdown-li click started");
		console.log('trendChart started');
		document.getElementById("std-dev-avg-trend-chart-headerid").innerHTML = "";
		document.getElementById("std-dev-avg-trend-chart-headerid").innerHTML = "<div class='loader' style='margin-left: 50%;'></div>";
		var tablename = $("#tableName").val();
		var idApp = $("#idApp").val();
		
		if (param) { // check param is null or not null
			console.log(param);
			var paramObj = param.data;
			var arrayLength = paramObj.length;

			for (var i = 0; i < arrayLength; i++) {
				if (i == 0) {
					stddevtrendChartDgroupVal = paramObj[i].toString();
				}
				if (i == 1) {
					stddevtrendChartColName = paramObj[i].toString();
				}
			}
			$('.nav-tabs a[href="#menu2"]').tab('show'); // Enable distribution chart tab
			document.getElementById("stddevtrendChart-dropdown-menu-Span").innerHTML = "";
			document.getElementById("stddevtrendChart-colname-dropdown-menu-Span").innerHTML = "";
			document.getElementById("stddevtrendChart-dropdown-menu-Span").innerHTML = "["+ stddevtrendChartDgroupVal + "]";
			document.getElementById("stddevtrendChart-colname-dropdown-menu-Span").innerHTML = "["+ stddevtrendChartColName + "]";
			stdDevTrendChartDropDown(); // load data into dropdowns
		}

		if (tablename && stddevtrendChartDgroupVal && stddevtrendChartColName) {
			$(function() {
				var form_data = {
					tableName : tablename,
					DGroupVal : stddevtrendChartDgroupVal,
					colName : stddevtrendChartColName,
					idApp : idApp
				};
				var request = $.ajax({
					url : './stdDevavgTrendChart',
					type : 'POST',
					headers: { 'token':$("#token").val()},
					data : form_data,
					dataType : 'json'
				});
				request.done(function(msg) {
							runStdDevTrendChart(msg);
							document.getElementById('std-dev-avg-trend-chart-headerid').innerHTML = '<h5><b> Distribution Trend Chart with DGroupVal : '+ stddevtrendChartDgroupVal+ ' & Column Name : '+ stddevtrendChartColName + ' </b></h5>';
						});
				request.fail(function(jqXHR, textStatus) {
					console.log("trendChart Request failed: " + textStatus);


				});

			});
		} else {
			console.log('values are not selected');
			document.getElementById("std-dev-avg-trend-chart-headerid").innerHTML = "<div class='alert alert-danger'> <strong>Error!</strong> Please select values from dropdowns </div>";
		}

	}
	
	function custmcsgStddevtrenchartcall(param) {
		console.log(" #custmcsg-trendChart-dropdown-li click started");
		console.log('trendChart started');
		document.getElementById("custmcsg-std-dev-avg-trend-chart-headerid").innerHTML = "";
		document.getElementById("custmcsg-std-dev-avg-trend-chart-headerid").innerHTML = "<div class='loader' style='margin-left: 50%;'></div>";
		var tablename = $("#tableName2").val();
		var idApp = $("#idApp").val();
		
		if (param) { // check param is null or not null
			console.log(param);
			var paramObj = param.data;
			var arrayLength = paramObj.length;

			for (var i = 0; i < arrayLength; i++) {
				if (i == 0) {
					stddevtrendChartDgroupVal = paramObj[i].toString();
				}
				if (i == 1) {
					stddevtrendChartColName = paramObj[i].toString();
				}
			}
			// load data into dropdowns
			custMcsgStdDevTrendChartColumnsDropDown(); 
			custMcsgStdDevTrendChartDGroupValDropDown(stddevtrendChartColName);
			$('.nav-tabs a[href="#custom_menu2"]').tab('show'); // Enable distribution chart tab
			document.getElementById("custmcsg-stddevtrendChart-dropdown-menu-Span").innerHTML = "";
			document.getElementById("custmcsg-stddevtrendChart-colname-dropdown-menu-Span").innerHTML = "";
			document.getElementById("custmcsg-stddevtrendChart-dropdown-menu-Span").innerHTML = "["+ stddevtrendChartDgroupVal + "]";
			document.getElementById("custmcsg-stddevtrendChart-colname-dropdown-menu-Span").innerHTML = "["+ stddevtrendChartColName + "]";
		}

		if (tablename && stddevtrendChartDgroupVal && stddevtrendChartColName) {
			$(function() {
				var form_data = {
					tableName : tablename,
					DGroupVal : stddevtrendChartDgroupVal,
					colName : stddevtrendChartColName,
					idApp : idApp
				};
				var request = $.ajax({
					url : './stdDevavgTrendChart',
					type : 'POST',
					headers: { 'token':$("#token").val()},
					data : form_data,
					dataType : 'json'
				});
				request.done(function(msg) {
						custmcsgRunStdDevTrendChart(msg);
							document.getElementById('custmcsg-std-dev-avg-trend-chart-headerid').innerHTML = '<h5><b> Distribution Trend Chart with DGroupVal : '+ stddevtrendChartDgroupVal+ ' & Column Name : '+ stddevtrendChartColName + ' </b></h5>';
						});
				request.fail(function(jqXHR, textStatus) {
					console.log("trendChart Request failed: " + textStatus);


				});

			});
		} else {
			console.log('values are not selected');
			document.getElementById("custmcsg-std-dev-avg-trend-chart-headerid").innerHTML = "<div class='alert alert-danger'> <strong>Error!</strong> Please select values from dropdowns </div>";
		}

	}

	function getListOfColName() {
		
		var tablename = $("#tableName").val();
		var idApp = $("#idApp").val();
		$(function() {
			var form_data = {
				tableName : tablename,
				idApp : idApp
			};
			var request = $.ajax({
				url : './listOfDGroupValByTableName',
				type : 'POST',
				headers: { 'token':$("#token").val()},
				data : form_data,
				dataType : 'json'
			});
			request.done(function(msg) {
				bindListOfColumnNameToDropDown(msg);
			});
			request.fail(function(jqXHR, textStatus) {console.log("trendChartDropDown Request failed: "+ textStatus);
				
			});
		});
	}
	
	function bindListOfColumnNameToDropDown(obj) {
		console.log('bindListOfColumnNameToDropDown');
		console.log(obj);
		var dGroupValObj = obj.dGroupVal;
		var colNameObj = obj.ColName;
	
		/*bind dgroupval*/
		var arrayLength = colNameObj.length;
		$("#comparisonTrendChart-dropdown-menu").empty();
		for (var i = 0; i < arrayLength; i++) {
			$("#comparisonTrendChart-dropdown-menu").append("<li><a onClick='validateHrefVulnerability(this);comparisionChartBindValueToVariable(this)'  >"+ colNameObj[i] + "</a></li>");
		}
		
		removejscssfile("./assets/pages/chosendropdown/js/chosen.jquery.js", "js");
		removejscssfile("./assets/pages/chosendropdown/js/init.js", "js");
		removejscssfile("./assets/pages/chosendropdown/js/prism.js", "js");		

		document.getElementById("comparisonTrendChartDGroupValParent").innerHTML = "";
		document.getElementById("comparisonTrendChartDGroupValParent").innerHTML = "<select  class='chosen-select' multiple tabindex='4'><option value=''></option></select>";

		/*bind colname*/
		var arrayLength = dGroupValObj.length; 
		$("#comparisonTrendChartDGroupValParent .chosen-select").empty();
		for (var i = 0; i < arrayLength; i++) {
		//	$("#comparisonTrendChart-dgroupVal-dropdown-menu").append("<li><a onClick='validateHrefVulnerability(this);comparisionChartBindValueToDGroupValVariable(this)'  >"+ colNameObj[i] + "</a></li>");
			$("#comparisonTrendChartDGroupValParent .chosen-select").append($("<option></option>")
                    .attr("value",dGroupValObj[i])
                    .text(dGroupValObj[i])); 
		}
		$("#comparisonTrendChartDGroupValParent .chosen-container").css('width','100%');
		$.getScript("./assets/pages/chosendropdown/js/chosen.jquery.js");
		$.getScript("./assets/pages/chosendropdown/js/init.js");
		$.getScript("./assets/pages/chosendropdown/js/prism.js");
	}
	
	function custMcsgGetListOfColName() {
		
		var tablename = $("#tableName2").val();
		var idApp = $("#idApp").val();
		$(function() {
			var form_data = {
				tableName : tablename,
				idApp : idApp
			};
			var request = $.ajax({
				url : './listOfDistributionCheckColumnsByTableName',
				type : 'POST',
				headers: { 'token':$("#token").val()},
				data : form_data,
				dataType : 'json'
			});
			request.done(function(msg) {
				custmcsgBindListOfColumnNameToDropDown(msg);
			});
			request.fail(function(jqXHR, textStatus) {console.log("trendChartDropDown Request failed: "+ textStatus);
				
			});
		});

	}
	
	function custmcsgBindListOfColumnNameToDropDown(obj) {
		var colNameObj = obj;
	
		/*bind columnname*/
		var arrayLength = colNameObj.length;
		$("#custmcsg-comparisonTrendChart-dropdown-menu").empty();
		for (var i = 0; i < arrayLength; i++) {
			$("#custmcsg-comparisonTrendChart-dropdown-menu").append("<li><a onClick='validateHrefVulnerability(this);custmcsgComparisionChartBindValueToVariable(\""+colNameObj[i]+"\");'  >"+ colNameObj[i] + "</a></li>");
		}
		
		removejscssfile("./assets/pages/chosendropdown/js/chosen.jquery.js", "js");
		removejscssfile("./assets/pages/chosendropdown/js/init.js", "js");
		removejscssfile("./assets/pages/chosendropdown/js/prism.js", "js");		
	}
	
	function custMcsgGetListOfDGroupValForColName(param) {
		
		var tablename = $("#tableName2").val();
		var columnName = param;
		var idApp = $("#idApp").val();
		$(function() {
			var form_data = {
				tableName : tablename,
				columnName : columnName,
				idApp : idApp
			};
			var request = $.ajax({
				url : './listOfDGroupValForColumnByTableName',
				type : 'POST',
				headers: { 'token':$("#token").val()},
				data : form_data,
				dataType : 'json'
			});
			request.done(function(msg) {
				custmcsgBindListOfDGroupValToDropDown(msg);
			});
			request.fail(function(jqXHR, textStatus) {console.log("trendChartDropDown Request failed: "+ textStatus);
				
			});
		});

	}
	
	function custmcsgBindListOfDGroupValToDropDown(obj) {
		var dGroupValObj = obj;
		
		removejscssfile("./assets/pages/chosendropdown/js/chosen.jquery.js", "js");
		removejscssfile("./assets/pages/chosendropdown/js/init.js", "js");
		removejscssfile("./assets/pages/chosendropdown/js/prism.js", "js");		
		
		document.getElementById("custmcsg-comparisonTrendChartDGroupValParent").innerHTML = "";
		document.getElementById("custmcsg-comparisonTrendChartDGroupValParent").innerHTML = "<select  class='chosen-select' multiple tabindex='4'><option value=''></option></select>";
	
		/*bind dgroupval*/
		var arrayLength = dGroupValObj.length; 
		$("#custmcsg-comparisonTrendChartDGroupValParent .chosen-select").empty();
		for (var i = 0; i < arrayLength; i++) {
			$("#custmcsg-comparisonTrendChartDGroupValParent .chosen-select").append($("<option></option>")
                    .attr("value",dGroupValObj[i])
                    .text(dGroupValObj[i])); 
		}
		$("#custmcsg-comparisonTrendChartDGroupValParent .chosen-container").css('width','100%');
		$.getScript("./assets/pages/chosendropdown/js/chosen.jquery.js");
		$.getScript("./assets/pages/chosendropdown/js/init.js");
		$.getScript("./assets/pages/chosendropdown/js/prism.js");
	}

	var comparisionChartSelectedColumnValue;
	var comparisionChartSelectedDGroupValue;

	function comparisionChartBindValueToVariable(param) {
		document.getElementById("comparisonTrendChart-dropdown-menu-span").innerHTML = "";
		comparisionChartSelectedColumnValue = $(param).text();
		document.getElementById("comparisonTrendChart-dropdown-menu-span").innerHTML = "["+ comparisionChartSelectedColumnValue + "]";
	}
	
	function comparisionChartBindValueToDGroupValVariable(param) {
		document.getElementById("comparisonTrendChart-dgroupVal-dropdown-menu-span").innerHTML = "";
		comparisionChartSelectedDGroupValue = $(param).text();
		document.getElementById("comparisonTrendChart-dgroupVal-dropdown-menu-span").innerHTML = "["+ comparisionChartSelectedColumnValue + "]";
	}
	
	function custmcsgComparisionChartBindValueToVariable(param) {
		document.getElementById("custmcsg-comparisonTrendChart-dropdown-menu-span").innerHTML = "";
		comparisionChartSelectedColumnValue = param;
		document.getElementById("custmcsg-comparisonTrendChart-dropdown-menu-span").innerHTML = "["+ comparisionChartSelectedColumnValue + "]";
		custMcsgGetListOfDGroupValForColName(comparisionChartSelectedColumnValue);
	}
	
	function custmcsgComparisionChartBindValueToDGroupValVariable(param) {
		document.getElementById("custmcsg-comparisonTrendChart-dgroupVal-dropdown-menu-span").innerHTML = "";
		comparisionChartSelectedDGroupValue = $(param).text();
		document.getElementById("custmcsg-comparisonTrendChart-dgroupVal-dropdown-menu-span").innerHTML = "["+ comparisionChartSelectedColumnValue + "]";
	}


	function comparisonChartCall() {
		console.log('comparisonChartCall');
		var dGroupValList="";
		var temp;
		
		$('#comparisonTrendChartDGroupValParent .chosen-choices .search-choice span').each(function() {
		    var x = $(this).text();
		    dGroupValList = dGroupValList+""+x+","; 
		});
		
		console.log("Selected DGroups: "+dGroupValList);
		
		if(dGroupValList.length==0){
			dGroupValList = "";
			$('#comparisonTrendChartDGroupValParent option').each(function() {
			    var x = $(this).text();
			    dGroupValList = dGroupValList+""+x+","; 
			});
			console.log("All DGroups: "+dGroupValList);
		}
		
		if(dGroupValList.length > 0){
			dGroupValList = dGroupValList.substring(0,dGroupValList.length-1);
		}
		
		temp = dGroupValList;
		console.log("Final DGroups: "+dGroupValList);
		
		document.getElementById("comparison-chart-headerid").innerHTML = "";
		document.getElementById("comparison-chart-headerid").innerHTML = "<div class='loader' style='margin-left: 50%;'></div>";
		var tablename = $("#tableName").val();
		var idApp = $("#idApp").val();
		console.log('tablename :: '+tablename);
		if (tablename && comparisionChartSelectedColumnValue && temp) {
			$(function() {
				var form_data = {
					tableName : tablename,
					colName : comparisionChartSelectedColumnValue,
					dGroupValList : temp,
					idApp : idApp
				};
				var request = $.ajax({
					url : './distrubutionCheckTrendChart',
					type : 'POST',
					headers: { 'token':$("#token").val()},
					data : form_data,
					dataType : 'json'
				});
				request.done(function(msg) {
							document.getElementById("comparison-chart-headerid").innerHTML = "";
							document.getElementById("comparison-chart-headerid").innerHTML = "<h5><b> Comparision Chart with Column Name : "+ comparisionChartSelectedColumnValue+ "</b></h5>";
							drawDistributionCheckTrendChart(msg, 'Date', 'Mean');
						});
				request.fail(function(jqXHR, textStatus) {
					console.log("trendChart Request failed: " + textStatus);
				});

			});
		} else {
			document.getElementById("comparison-chart-headerid").innerHTML = "<div class='alert alert-danger'> <strong>Error!</strong> Column Name is not Selected </div>";
			console.log('Comparison chart : table name or column name is empty or null');
		}

	}
	
	
	function custmcsgComparisonChartCall() {
		console.log('custmcsgComparisonChartCall');
		var dGroupValList="";
		var temp;
		
		$('#custmcsg-comparisonTrendChartDGroupValParent .chosen-choices .search-choice span').each(function() {
		    var x = $(this).text();
		    dGroupValList = dGroupValList+""+x+","; 
		});
		
		console.log("Selected DGroups: "+dGroupValList);
		
		if(dGroupValList.length==0){
			dGroupValList = "";
			$('#custmcsg-comparisonTrendChartDGroupValParent option').each(function() {
			    var x = $(this).text();
			    dGroupValList = dGroupValList+""+x+","; 
			});
			console.log("All DGroups: "+dGroupValList);
		}
		
		if(dGroupValList.length > 0){
			dGroupValList = dGroupValList.substring(0,dGroupValList.length-1);
		}
		
		temp = dGroupValList;
		console.log("Final DGroups: "+dGroupValList);
		
		document.getElementById("custmcsg-comparison-chart-headerid").innerHTML = "";
		document.getElementById("custmcsg-comparison-chart-headerid").innerHTML = "<div class='loader' style='margin-left: 50%;'></div>";
		var tablename = $("#tableName2").val();
		var idApp = $("#idApp").val();
		console.log('tablename :: '+tablename);
		if (tablename && comparisionChartSelectedColumnValue && temp) {
			$(function() {
				var form_data = {
					tableName : tablename,
					colName : comparisionChartSelectedColumnValue,
					dGroupValList : temp,
					idApp : idApp
				};
				var request = $.ajax({
					url : './distrubutionCheckTrendChart',
					type : 'POST',
					headers: { 'token':$("#token").val()},
					data : form_data,
					dataType : 'json'
				});
				request.done(function(msg) {
							document.getElementById("custmcsg-comparison-chart-headerid").innerHTML = "";
							document.getElementById("custmcsg-comparison-chart-headerid").innerHTML = "<h5><b> Comparision Chart with Column Name : "+ comparisionChartSelectedColumnValue+ "</b></h5>";
							custmcsgDrawDistributionCheckTrendChart(msg, 'Date', 'Mean');
						});
				request.fail(function(jqXHR, textStatus) {
					console.log("trendChart Request failed: " + textStatus);
				});

			});
		} else {
			document.getElementById("custmcsg-comparison-chart-headerid").innerHTML = "<div class='alert alert-danger'> <strong>Error!</strong> Column Name is not Selected </div>";
			console.log('Comparison chart : table name or column name is empty or null');
		}

	}
	

	// function called from loadChart() and providing dGroupVal (for header) and
	// char array for chart
	function drawDistributionCheckTrendChart(array, xaxis, yaxis) {

		booleanHideGraphLoadStatus = true;
		// 			console.log("drawChart :: array :: " + array);
		// var jsonObj = JSON.parse(array);
		var header = array.header;
		var chartdata = array.chart;
		// 			console.log("header :: " + header);
		// 			console.log("chart :: " + chartdata);
		// Define the chart to be drawn.
		var data = new google.visualization.DataTable();
		// changecat(dGroup);
		data.addColumn('string', 'Date');
		for (i = 0; i < header.length; i++) {
			data.addColumn('number', header[i]);
		}

		// data.addColumn('string', 'dGroupCol');
		data.addRows(chartdata);
		
		 data.sort({
	         column: 0,
	         desc: true
	       });

		// Set chart options
		var options = {
			'title' : '',
			//		legend: { position: 'bottom' },
			hAxis : {
				title : xaxis, direction:-1, slantedText:true, slantedTextAngle:45
			},
			vAxis : {
				title : yaxis,
				vAxis : {
					viewWindowMode : "explicit",
					viewWindow : {
						min : 0
					}
				}
			},
			'width' : 900,
			'height' : 750,
			legend : {
				maxLines : 1,
				textStyle : {
					fontSize : 15
				}
			},
			 chartArea:{left:80,top:10,width:'70%'},
			 isStacked: true
		};
		// 			console.log("before clicking on legend");
		// Instantiate and draw the chart.
		var chart = new google.visualization.LineChart(document.getElementById('trendChart_div')); // on this element google chart
		// will load
		chart.draw(data, options);

		var columns = [];
		var series = {};
		for (var i = 0; i < data.getNumberOfColumns(); i++) {
			columns.push(i);
			if (i > 0) {
				series[i - 1] = {};
			}
		}

		var options = {
			'title' : '',
			//		legend: { position: 'bottom' },
			hAxis : {
				title : xaxis, direction:-1, slantedText:true, slantedTextAngle:45
			},
			vAxis : {
				title : yaxis,
				vAxis : {
					viewWindowMode : "explicit",
					viewWindow : {
						min : 0
					}
				}
			},
			'width' : 900,
			'height' : 750,
			legend : {
				maxLines : 1,
				textStyle : {
					fontSize : 15
				}
			},
			 chartArea:{left:80,top:10,width:'70%'},
			 isStacked: true,
			series : series
		};

		google.visualization.events.addListener(chart, 'select', function() {
			console.log("trying to hide line on click of legend");
			var sel = chart.getSelection();
			// if selection length is 0, we deselected an element
			// 		        console.log("sel");
			// 		        console.log(sel);
			if (sel.length > 0) {
				// if row is undefined, we clicked on the legend
				if (sel[0].row === null) {
					var col = sel[0].column;
					// 		                console.log("column : ");
					// 		                console.log(col);
					if (columns[col] == col) {
						// hide the data series
						// 		                	 console.log("column condition true ");
						// 		                	 console.log(data.getColumnLabel(col));
						// 		                	 console.log(data.getColumnType(col));
						columns[col] = {
							label : data.getColumnLabel(col),
							type : data.getColumnType(col),
							calc : function() {
								return 0;
							}
						};

						// grey out the legend entry
						series[col - 1].color = '#CCCCCC';
					} else {
						// show the data series
						columns[col] = col;
						series[col - 1].color = null;
					}
					//		                console.log("column dfn ");
					//		                console.log(columns);
					var view = new google.visualization.DataView(data);
					view.setColumns(columns);
					chart.draw(view, options);
				}
			}
		});

		if (booleanHideGraphLoadStatus) {
			$("#nochartdata").hide();
		}
		//	$('#segment-dropdown').show();

	}
	
	
	function custmcsgDrawDistributionCheckTrendChart(array, xaxis, yaxis) {

		booleanHideGraphLoadStatus = true;
		var header = array.header;
		var chartdata = array.chart;
		// Define the chart to be drawn.
		var data = new google.visualization.DataTable();
		data.addColumn('string', 'Date');
		for (i = 0; i < header.length; i++) {
			data.addColumn('number', header[i]);
		}

		// data.addColumn('string', 'dGroupCol');
		data.addRows(chartdata);
		
		 data.sort({
	         column: 0,
	         desc: true
	       });

		// Set chart options
		var options = {
			'title' : '',
			//		legend: { position: 'bottom' },
			hAxis : {
				title : xaxis, direction:-1, slantedText:true, slantedTextAngle:45
			},
			vAxis : {
				title : yaxis,
				vAxis : {
					viewWindowMode : "explicit",
					viewWindow : {
						min : 0
					}
				}
			},
			'width' : 900,
			'height' : 750,
			legend : {
				maxLines : 1,
				textStyle : {
					fontSize : 15
				}
			},
			 chartArea:{left:80,top:10,width:'70%'},
			 isStacked: true
		};
		// Instantiate and draw the chart.
		var chart = new google.visualization.LineChart(document.getElementById('custmcsg_trendChart_div')); // on this element google chart
		// will load
		chart.draw(data, options);

		var columns = [];
		var series = {};
		for (var i = 0; i < data.getNumberOfColumns(); i++) {
			columns.push(i);
			if (i > 0) {
				series[i - 1] = {};
			}
		}

		var options = {
			'title' : '',
			hAxis : {
				title : xaxis, direction:-1, slantedText:true, slantedTextAngle:45
			},
			vAxis : {
				title : yaxis,
				vAxis : {
					viewWindowMode : "explicit",
					viewWindow : {
						min : 0
					}
				}
			},
			'width' : 900,
			'height' : 750,
			legend : {
				maxLines : 1,
				textStyle : {
					fontSize : 15
				}
			},
			 chartArea:{left:80,top:10,width:'70%'},
			 isStacked: true,
			series : series
		};

		google.visualization.events.addListener(chart, 'select', function() {
			console.log("trying to hide line on click of legend");
			var sel = chart.getSelection();
			// if selection length is 0, we deselected an element
			// 		        console.log("sel");
			// 		        console.log(sel);
			if (sel.length > 0) {
				// if row is undefined, we clicked on the legend
				if (sel[0].row === null) {
					var col = sel[0].column;
					// 		                console.log("column : ");
					// 		                console.log(col);
					if (columns[col] == col) {
						// hide the data series
						// 		                	 console.log("column condition true ");
						// 		                	 console.log(data.getColumnLabel(col));
						// 		                	 console.log(data.getColumnType(col));
						columns[col] = {
							label : data.getColumnLabel(col),
							type : data.getColumnType(col),
							calc : function() {
								return 0;
							}
						};

						// grey out the legend entry
						series[col - 1].color = '#CCCCCC';
					} else {
						// show the data series
						columns[col] = col;
						series[col - 1].color = null;
					}
					//		                console.log("column dfn ");
					//		                console.log(columns);
					var view = new google.visualization.DataView(data);
					view.setColumns(columns);
					chart.draw(view, options);
				}
			}
		});

		if (booleanHideGraphLoadStatus) {
			$("#nochartdata").hide();
		}
		//	$('#segment-dropdown').show();

	}

	/* advance filter */

	$("#addAdvanceFilter").click(function() {

		$("#nav_button-bar").removeClass('hidden').show();
		$("#rmAdvanceFilter").removeClass('hidden').show();
		$("#addAdvanceFilter").addClass('hidden');
		$("#addAdvanceFilter_v").addClass('hidden');
		$("#rmAdvanceFilter_v").removeClass('hidden').show();
		//$("#tableresult").addClass('hidden');
		$('#portletbodyfilter').show();
		$('#portletbody').hide();
	//	$('#load_refresh').hide();

	});
	$("#rmAdvanceFilter").click(function() {

		$("#nav_button-bar").addClass('hidden');
		$("#rmAdvanceFilter").addClass('hidden');
		$("#addAdvanceFilter").removeClass('hidden').show();
		$("#addAdvanceFilter_v").removeClass('hidden').show();
		$("#rmAdvanceFilter_v").addClass('hidden');
		$('#dgroupbodyId').show();
		//$("#tableresult").removeClass('hidden').show();
	});

	/**
	 *  this code for advance filter at first it hit
	 *  /microDataColumn  to get column name and create select option label
	 *  then /microDataValue select option data
	 */
	var strSearchValue = "";
	var column_name = [];
	var _this = this;
	var intSeperators = 0;
	var uniqueArray = [];
	var tableName;
	var api;
	var idApp;
	var dataTableId;
	var strSearchValue;
	var globalTableName;
	function advancefilter(idApp, tableName, api, dataTableId) {
		window.tableName = tableName;
		console.log('advancefilter :: ' + tableName);
		globalTableName = tableName;
		window.idApp = idApp;
		window.api = api;
		window.dataTableId = dataTableId;
		//	console.log(window.tableName+' '+window.api+' '+window.tableName+' '+window.idApp+' '+window.dataTableId);
		var no_error = 1;
		$('.input_error').remove();
		var microDataValueData = {
			tableName : tableName,
			idApp : idApp
		};
		var form_data = {
			idApp : idApp
		};

		$.ajax({
					url : './microDataColumn',
					type : 'POST',
					datatype : 'json',
					headers: { 'token':$("#token").val()},
					data : form_data,
					success : function(message) {
						var j_obj = $.parseJSON(message);
						if (j_obj.hasOwnProperty('success')) {
							var temp = {};
							column_array = JSON.parse(j_obj.success);
							// empty the list from previous selection
							//$('#Microsegval').empty();
							intSeperators = column_array.length - 1;
							//			console.log(" seperators :: "+ intSeperators);
							var navButtons = document.getElementById("nav_button-bar");

							$.each(column_array,function(i, obj) {
												var column_details = column_array[i];
												var div = document.createElement("div");
												div.setAttribute("id",column_details + "_"+ i);
												div.setAttribute("class","col-md-2 form-group-sm");
												div.setAttribute("style","float:left;")

												_this.column_name.push(column_details);
												var button = document.createElement("button");
												button.setAttribute("id",column_details);
												button.setAttribute("class","btn btn-group-justified btn-primary margin-bottom-10");

												button.innerHTML = column_details;

												var input = document.createElement("select");
												input.setAttribute("id","col_value" + i);
												if (i > 0) {
													input.setAttribute("class","col-md-8 form-group-sm");
												} else {
													input.setAttribute("class","col-md-8 form-group-sm");
												}

												//input.setAttribute("class","form-control");

												div.appendChild(button);
												div.appendChild(input);

												navButtons.appendChild(div);
												/*  $("#col_value"+i).fastselect({
														toggleButtonClass: 'form-control fstToggleBtn',
														queryInputClass: 'form-control fstQueryInput',
														placeholder: 'value',
														itemClass:'form fstResultItem',
														searchPlaceholder: 'search..',
													}); */

												$("#col_value" + i).append('<option value= >All</option>');
												//alert(obj.value+":"+obj.text);
												//  var div_data="<option value="+column_details+">"+column_details+"</option>";
												// alert(div_data);
												//   $(div_data).appendTo('#Microsegval'); 
											});
							var divvar = document.createElement("div");
							divvar.setAttribute("id", "failcheckdivbar");
							divvar.setAttribute("class","col-md-2 form-group-sm");

							var checkbox = document.createElement('input');
							checkbox.type = "checkbox";
							checkbox.name = "failrecordcheck";
							checkbox.checked = 'true';
						//	checkbox.value = "Failed Records";
							checkbox.id = "failedCheckInputId";
							
							var label = document.createElement('label')
							label.htmlFor = "failedCheckInputId";
							label.appendChild(document.createTextNode('Failed Records'));
							
							divvar.appendChild(checkbox);
							divvar.appendChild(label);
							navButtons.appendChild(divvar);
							
							var divbar = document.createElement("div");
							divbar.setAttribute("id", "divbar");
							divbar.setAttribute("class","col-md-2 form-group-sm");

							var refresh = document.createElement("button");
							refresh.setAttribute("id", "r_efresh");
							refresh.setAttribute("class", "btn");
							refresh.setAttribute("onClick","getUpdateTableValues()");
							refresh.innerHTML = "Search";

							divbar.appendChild(refresh)
							navButtons.appendChild(divbar);

							/* add refresh button to load actual data*/

							var loaddivbar = document.createElement("div");
							loaddivbar.setAttribute("id", "loaddivbar");
							loaddivbar.setAttribute("class","col-md-2 form-group-sm");

							var loadrefresh = document.createElement("button");
							loadrefresh.setAttribute("id", "load_refresh");
							loadrefresh.setAttribute("class", "btn");
							loadrefresh.setAttribute("onClick","destroyandloaddata()");
					//		loadrefresh.setAttribute("hidden", true);
							loadrefresh.innerHTML = "Refresh";

							loaddivbar.appendChild(loadrefresh);
							navButtons.appendChild(loaddivbar);

							/* refresh data button code done*/

							//					console.log("start...........");
							//					console.log("before ./microDataValue call formdatatableName :: "+ formdatatableName);
							$.ajax({
										url : './microDataValue',
										type : 'POST',
										datatype : 'json',
										headers: { 'token':$("#token").val()},
										data : microDataValueData,
										success : function(message) {
											loadfreshdata('ColSummTable', 'ColSummTableForNumericTab',tableName, idApp,""); // 1: tableid/class, 2: api call 3:tableName & 4: idApp

											//			console.log("Mid.......");
											var j_objv = $.parseJSON(message);
											if (j_objv.hasOwnProperty('success')) {
												console.log("after ./microDataValue call formdatatableName :: "+ formdatatableName);
												value_array = JSON.parse(j_objv.success);
												uniqueArray = value_array.filter(onlyUnique);
												//						console.log("value details");
												//					console.log(uniqueArray);
												var value_m = [];
												$.each(uniqueArray,function(i, obj) {
																	var value_details = uniqueArray[i];
																	var arrRawData = value_details.split("-",intSeperators + 1);
																	//						console.log("value details : "+ arrRawData);
																	value_m = arrRawData;
																	//	value_m = value_details.split(/-(.+)/)[intSeperators];
																	//							console.log("print item and index");
																	value_m.forEach(function(item,index) {
																				//								console.log(item,index);

																				if (!$("#col_value"+ index+ " option[value='"+ item+ "']").length) {
																					//								        console.log(item + ' is a new value!');
																					//   $('<option>').item.appendTo($('#col_value'+index));

																					var select = $("#col_value"+ index);
																					select.append('<option value="'+item+'">'+ item+ '</option>');
																				}

																				//	var select = $("#col_value"+ index);
																				//	select.append('<option value="'+item+'">'+ item+ '</option>');
																			});
																});
											} else if (j_objv.hasOwnProperty('fail')) {
												//								console.log(j_objv.fail);
												toastr.info(j_objv.fail);
											}
										}
									});
						} else if (j_obj.hasOwnProperty('fail')) {
							//						console.log(j_obj.fail);
							toastr.info(j_obj.fail);
						}
					},
					error : function(xhr, textStatus, errorThrown) {
						$('#initial').hide();
						$('#fail').show();
					}
				});
	}

	function getUpdateTableValues() {
		showFilterChart = true;

		$('#load_refresh').show();
		var arrDropDownCollection = [];
		var strSearchValue = "";
		for (var i = 0; i < intSeperators + 1; i++) {
			var e = document.getElementById("col_value" + i);
			var strData = e.options[e.selectedIndex].value;
			if (strData != null && strData !== '') {
				if (i > 0) {
					strSearchValue = strSearchValue + "-" + strData;
				} else {
					strSearchValue = strSearchValue + strData;
				}
			} else {
				strData = '_'; // to replace it with % in java code
				if (i > 0) { // from second word onwords
					strSearchValue = strSearchValue + "-" + strData;
				} else { // for first word
					strSearchValue = strSearchValue + strData;
				}
			}

		}

		destroyCreateDatatable(window.dataTableId);
		loadfreshdata(window.dataTableId, window.api, window.tableName,window.idApp, strSearchValue);
		filterChartCall(window.tableName, strSearchValue);

		$('#loaddivbar').show();
	}

	function destroyandloaddata() {
		console.log('destroyandloaddata');
		//to reset dropdowns
		for (var i = 0; i < intSeperators + 1; i++) {
			$("#" + "col_value" + i).prop('selectedIndex', 0);
		}
		document.getElementById("failedCheckInputId").checked = false;
		showFilterChart = false;
		destroyCreateDatatable(window.dataTableId);
		loadfreshdata(window.dataTableId, window.api, window.tableName,
				window.idApp, window.strSearchValue);
		$('#loaddivbar').hide();
	}

	function destroyCreateDatatable(tableid) {
		var localTableid = '#' + tableid;
		if ($.fn.DataTable.isDataTable(localTableid)) {
			$(localTableid).DataTable().destroy();
		}
		$(localTableid + ' tbody').empty();
	}

	function loadfreshdata(tableid, api, tableName, idApp, strSearchValue) {
		var faildRecordsHiddenFieldValue = 'false';
		
		if (faildRecordsHiddenFieldValue === null|| faildRecordsHiddenFieldValue === undefined) {
			faildRecordsHiddenFieldValue = 'false';
		}
	
		if (document.getElementById('failedCheckInputId').checked) {
			faildRecordsHiddenFieldValue = 'true';
			$('#load_refresh').show();
        } else {
        	faildRecordsHiddenFieldValue = 'false';
        }

		$('#' + tableid).dataTable(

				{
					"bPaginate" : true,
					"order" : [ 0, 'asc' ],
					"bInfo" : true,
					"iDisplayStart" : 0,
					"bProcessing" : true,
					"bServerSide" : true,
					"dataSrc" : "",
					'sScrollX' : true,
					"sAjaxSource" : path + "/" + api + "?tableName="
							+ tableName + "&idApp=" + idApp + "&searchValue="
							+ strSearchValue+"&checkFailedRecords="+faildRecordsHiddenFieldValue,
                  "aoColumns": [
                          {"data" : "Date"},
                          {"data" : "Run1"},
                          {"data" : "NumMeanStatus",
                            "render": function(data, type, row, meta){
                                    var strDGroupVal = row.DGroupVal;
                                    var strColName = row.ColName;
                                    var strRun1 = row.Run1;
                                    var numMeanStatus = row.NumMeanStatus;
                                    var numSDStatus = row.NumSDStatus;
                                    var numsumstatus1 = row.numsumstatus1;

                                    var trendChartParams = [ strDGroupVal, strColName, strRun1];
                                    var trendChartJson = {
                                         "data" : trendChartParams
                                    };

                                    var trendChartJsonStr = JSON.stringify(trendChartJson);
                                    console.log("trendChartJsonStr:"+trendChartJsonStr);
                                    
									if (numMeanStatus ==="" && numSDStatus === "" && numsumstatus1 === ""){
										data = ""
									}
									else if (numMeanStatus ==="passed" && numSDStatus === "passed" && numsumstatus1 === "passed"){
                                       data = "<span class='label label-success label-sm'>" + "passed" + "</span>";
                                    } else {
                                       data = "<span class='label label-danger label-sm'>" + "failed" + "</span>";
                                    }

                                    var numMeanStatus;
                                     if (numMeanStatus === "failed") {
                                        strAvgColor = "red;";
                                    }else {
                                        strAvgColor = "violet;";
                                    }
                                    var numSDStatus;
                                    if (numSDStatus === "failed") {
                                        strStdColor = "red;";
                                    }else {
                                        strStdColor = "green;";
                                    }
                                    var numsumstatus1;
                                     if (numsumstatus1 === "failed") {
                                        strSumColor = "red;";
                                    }else {
                                        strSumColor = "blue;";
                                    }

                                   data = data+ "<a onclick='trenchartcall("+trendChartJsonStr+")' title='Avg Trend | Micro-Segment : "+strDGroupVal+" & Column Name : "+strColName+"' style='color: "+strAvgColor+"; margin-left: 2%;'> <span class='fa fa-bar-chart'></span></a>";
                                   data = data + "<a onclick='stddevtrenchartcall("+trendChartJsonStr+")' title='Distribution Trend | Micro-Segment : "+strDGroupVal+" & Column Name : "+strColName+"' style='color: "+strStdColor+"; margin-left: 2%;'> <span class='fa fa-area-chart'></span></a>";
                                   data = data  + "<a onclick='sumOfNumStatsChartDropdownMenuValueSection("+trendChartJsonStr+")' title='Sum Trend | Micro-Segment : "+strDGroupVal+" & Column Name : "+strColName+"' style='color: "+strSumColor+"; margin-left: 2%;'> <span class='fa fa-line-chart'></span></a>";
                                   return data;
                               }
                          },
                          {"data" : "NumMeanDeviation1",
                              "render": function(data, type, row, meta){
                                   var numMeanDeviation1 = row.NumMeanDeviation1.replace(/,/g, "");
                                   var numSDDeviation1 =row.NumSDDeviation1.replace(/,/g, "");
                                   var numSumDeviation = row.NumSumStdDev1.replace(/,/g, "");
                                   var numStatColName = row.ColName;
                                   var passingNumberthreshold = Math.max(numMeanDeviation1,numSDDeviation1,numSumDeviation);
                                   data = "<a onClick=\"javascript:updateColumnCheckThreshold("+idApp+",'Numerical Statistics Check','"+numStatColName+"','"+passingNumberthreshold+"')\" class=\"fa fa-thumbs-down\" </a>";
                               return data;
                               }
                          },
                          {"data" : "NumDqi"},
                          {"data" : "ColName"},
                          {"data" : "Count1"},
                          {"data" : "Min1"},
                          {"data" : "Max1"},
                          {"data" : "Std_Dev1"},
                          {"data" : "Mean1"},
                          {"data" : "NumMeanAvg1"},
                          {"data" : "NumMeanStdDev1"},
                          {"data" : "NumMeanDeviation1"},
                          {"data" : "NumMeanThreshold"},
                          {"data" : "NumSDAvg1"},
                          {"data" : "NumSDStdDev1"},
                          {"data" : "NumSDDeviation1"},
                          {"data" : "NumSDThreshold"},
                          {"data" : "DGroupVal"},
                          {"data" : "DGroupCol"},
                          {"data" : "SumOfNumStat1"},
                          {"data" : "NumSumAvg1"},
                          {"data" : "NumSumStdDev1"},
                          {"data" : "NumSumThreshold"},
                          {"data" : "NumMeanStatus",
                            "render": function(data, type, row, meta){
                                if (data === "passed") {
                                    data = "<span class='label label-success label-sm'>"
                                            + data + "</span>";

                                } else if (data === "failed") {
                                    data = "<span class='label label-danger label-sm'>"
                                            + data + "</span>";
                                }
                                return data;
                                }
                            },
                          {"data" : "NumSDStatus",
                          "render": function(data, type, row, meta){
                              if (data === "passed") {
                                  data = "<span class='label label-success label-sm'>"
                                          + data + "</span>";

                              } else if (data === "failed") {
                                  data = "<span class='label label-danger label-sm'>"
                                          + data + "</span>";
                              }
                              return data;
                              }
                          },
                          {"data" : "numsumstatus1",
                          "render": function(data, type, row, meta){
                              if (data === "passed") {
                                  data = "<span class='label label-success label-sm'>"
                                          + data + "</span>";

                              } else if (data === "failed") {
                                  data = "<span class='label label-danger label-sm'>"
                                          + data + "</span>";
                              }
                              return data;
                              }
                         }
                    ],
					"dom" : 'C<"clear">lfrtip',
					colVis : {
						"align" : "right",
						restore : "Restore",
						showAll : "Show all",
						showNone : "Show none",
						order : 'alpha'
					},
					"language" : {
						"infoFiltered" : ""
					},
					"dom" : 'Cf<"toolbar"">rtip',
				});
	//	$('#loaddivbar').hide();
	}
	
	function loadCustomMicrosegmentSummaryData(tableid, api, tableName, idApp, strSearchValue) {
		var faildRecordsHiddenFieldValue = 'false';
		$('#' + tableid).dataTable(
				{
					"bPaginate" : true,
					"order" : [ 0, 'asc' ],
					"bInfo" : true,
					"iDisplayStart" : 0,
					"bProcessing" : true,
					"bServerSide" : true,
					"dataSrc" : "",
					'sScrollX' : true,
					"sAjaxSource" : path + "/" + api + "?tableName="
							+ tableName + "&idApp=" + idApp + "&searchValue="
							+ strSearchValue+"&checkFailedRecords="+faildRecordsHiddenFieldValue,
                    "aoColumns": [
                          {"data" : "Date"},
                          {"data" : "Run1"},
                          {"data" : "NumMeanStatus",
                            "render": function(data, type, row, meta){
                                    var strDGroupVal = row.DGroupVal;
                                    var strColName = row.ColName;
                                    var strRun1 = row.Run1;
                                    var numMeanStatus = row.NumMeanStatus;
                                    var numSDStatus = row.NumSDStatus;
                                    var numsumstatus1 = row.numsumstatus1;
                                    var minValue = row.Min1;
                                    var maxValue = row.Max1;
                                    
                                    var trendChartParams = [ strDGroupVal, strColName, strRun1];
                                    var trendChartJson = {
                                         "data" : trendChartParams
                                    };
                                    var trendChartJsonStr = JSON.stringify(trendChartJson);
                                    
                                    // For a String type column no graph
                                    if(minValue === "" && maxValue === ""){
                                    	 if (numMeanStatus ===""){
    										data = ""
    									 } else if (numMeanStatus ==="passed"){
                                            data = "<span class='label label-success label-sm'>" + "passed" + "</span>";
                                         } else {
                                            data = "<span class='label label-danger label-sm'>" + "failed" + "</span>";
                                         }
                                    	 
                                    	 var numMeanStatus;
                                         if (numMeanStatus === "failed") {
                                            strAvgColor = "red;";
                                         }else {
                                            strAvgColor = "violet;";
                                         }
                                    	
                                        data = data+ "<a onclick='custMicrosegTrendChartcall("+trendChartJsonStr+")' title='Avg Trend | Micro-Segment : "+strDGroupVal+" & Column Name : "+strColName+"' style='color: "+strAvgColor+"; margin-left: 2%;'> <span class='fa fa-bar-chart'></span></a>";
                                    } else {
                                    	
    									if (numMeanStatus ==="" && numSDStatus === "" && numsumstatus1 === ""){
    										data = ""
    									}
    									else if (numMeanStatus ==="passed" && numSDStatus === "passed" && numsumstatus1 === "passed"){
                                           data = "<span class='label label-success label-sm'>" + "passed" + "</span>";
                                        } else {
                                           data = "<span class='label label-danger label-sm'>" + "failed" + "</span>";
                                        }

                                        var numMeanStatus;
                                         if (numMeanStatus === "failed") {
                                            strAvgColor = "red;";
                                        }else {
                                            strAvgColor = "violet;";
                                        }
                                        var numSDStatus;
                                        if (numSDStatus === "failed") {
                                            strStdColor = "red;";
                                        }else {
                                            strStdColor = "green;";
                                        }
                                        var numsumstatus1;
                                         if (numsumstatus1 === "failed") {
                                            strSumColor = "red;";
                                        }else {
                                            strSumColor = "blue;";
                                        }

                                       data = data+ "<a onclick='custMicrosegTrendChartcall("+trendChartJsonStr+")' title='Avg Trend | Micro-Segment : "+strDGroupVal+" & Column Name : "+strColName+"' style='color: "+strAvgColor+"; margin-left: 2%;'> <span class='fa fa-bar-chart'></span></a>";
                                       data = data + "<a onclick='custmcsgStddevtrenchartcall("+trendChartJsonStr+")' title='Distribution Trend | Micro-Segment : "+strDGroupVal+" & Column Name : "+strColName+"' style='color: "+strStdColor+"; margin-left: 2%;'> <span class='fa fa-area-chart'></span></a>";
                                       data = data  + "<a onclick='custmcsgSumOfNumStatsChartDropdownMenuValueSection("+trendChartJsonStr+")' title='Sum Trend | Micro-Segment : "+strDGroupVal+" & Column Name : "+strColName+"' style='color: "+strSumColor+"; margin-left: 2%;'> <span class='fa fa-line-chart'></span></a>";
                                    }
                                    return data;    
                               }
                          },
                          {"data" : "NumDqi"},
                          {"data" : "ColName"},
                          {"data" : "Count1"},
                          {"data" : "Min1"},
                          {"data" : "Max1"},
                          {"data" : "Std_Dev1"},
                          {"data" : "Mean1"},
                          {"data" : "NumMeanAvg1"},
                          {"data" : "NumMeanStdDev1"},
                          {"data" : "NumMeanDeviation1"},
                          {"data" : "NumMeanThreshold"},
                          {"data" : "NumSDAvg1"},
                          {"data" : "NumSDStdDev1"},
                          {"data" : "NumSDDeviation1"},
                          {"data" : "NumSDThreshold"},
                          {"data" : "DGroupVal"},
                          {"data" : "DGroupCol"},
                          {"data" : "SumOfNumStat1"},
                          {"data" : "NumSumAvg1"},
                          {"data" : "NumSumStdDev1"},
                          {"data" : "NumSumThreshold"},
                          {"data" : "NumMeanStatus",
                            "render": function(data, type, row, meta){
                                if (data === "passed") {
                                    data = "<span class='label label-success label-sm'>"
                                            + data + "</span>";

                                } else if (data === "failed") {
                                    data = "<span class='label label-danger label-sm'>"
                                            + data + "</span>";
                                }
                                return data;
                                }
                            },
                          {"data" : "NumSDStatus",
                          "render": function(data, type, row, meta){
                              if (data === "passed") {
                                  data = "<span class='label label-success label-sm'>"
                                          + data + "</span>";

                              } else if (data === "failed") {
                                  data = "<span class='label label-danger label-sm'>"
                                          + data + "</span>";
                              }
                              return data;
                              }
                          },
                          {"data" : "numsumstatus1",
                          "render": function(data, type, row, meta){
                              if (data === "passed") {
                                  data = "<span class='label label-success label-sm'>"
                                          + data + "</span>";

                              } else if (data === "failed") {
                                  data = "<span class='label label-danger label-sm'>"
                                          + data + "</span>";
                              }
                              return data;
                              }
                         }
                    ],
					"dom" : 'C<"clear">lfrtip',
					colVis : {
						"align" : "right",
						restore : "Restore",
						showAll : "Show all",
						showNone : "Show none",
						order : 'alpha'
					},
					"language" : {
						"infoFiltered" : ""
					},
					"dom" : 'Cf<"toolbar"">rtip',
				});
	//	$('#loaddivbar').hide();
	}

	function filterChartCall(globalTableName, strSearchValue) {
		$(function() {
			var form_data = {
				tableName : globalTableName,
				dGroupVal : strSearchValue,
				idApp :  $("#idApp").val()
			};
			var request = $.ajax({
				url : './distrubutionCheckTrendDGroupValChart',
				type : 'POST',
				headers: { 'token':$("#token").val()},
				data : form_data,
				dataType : 'json'
			});
			request.done(function(msg) {
				mainJsonObject = msg;
				filterChart(msg, 'Date', 'RecordCount');

			});
			request.fail(function(jqXHR, textStatus) {
				console.log("Request failed: " + textStatus);
			});
		});
	}

	function filterChart(array, xaxis, yaxis) {

		booleanHideGraphLoadStatus = true;
		//	console.log("drawChart :: array :: " + array);
		// var jsonObj = JSON.parse(array);
		var header = array.header;
		var chartdata = array.chart;
		//	console.log("header :: " + header);
		//	console.log("chart :: " + chartdata);
		// Define the chart to be drawn.
		var data = new google.visualization.DataTable();
		// changecat(dGroup);
		data.addColumn('string', 'Date');
		for (i = 0; i < header.length; i++) {
			data.addColumn('number', header[i]);
		}

		// data.addColumn('string', 'dGroupCol');
		data.addRows(chartdata);

		 data.sort({
	         column: 0,
	         desc: true
	       });
		
		// Set chart options
		var options = {
			'title' : '',
			//		legend: { position: 'bottom' },
			hAxis : {
				title : xaxis, direction:-1, slantedText:true, slantedTextAngle:45
			},
			vAxis : {
				title : yaxis,
				vAxis : {
					viewWindowMode : "explicit",
					viewWindow : {
						min : 0
					}
				}
			},
			'width' : 900,
			'height' : 750,
			legend : {
				maxLines : 1,
				textStyle : {
					fontSize : 15
				}
			},
			chartArea : {
				left : 80,
				top : 10,
				width : '50%',
				height : '60%'
			},
			isStacked: true
		};
		//	console.log("before clicking on legend");
		// Instantiate and draw the chart.
		var chart = new google.visualization.LineChart(document.getElementById('filter-modal-body')); // on this element google chart
		// will load
		chart.draw(data, options);

		var columns = [];
		var series = {};
		for (var i = 0; i < data.getNumberOfColumns(); i++) {
			columns.push(i);
			if (i > 0) {
				series[i - 1] = {};
			}
		}

		var options = {
			'title' : '',
			//		legend: { position: 'bottom' },
			hAxis : {
				title : xaxis, direction:-1, slantedText:true, slantedTextAngle:45
			},
			vAxis : {
				title : yaxis,
				vAxis : {
					viewWindowMode : "explicit",
					viewWindow : {
						min : 0
					}
				}
			},
			'width' : 900,
			'height' : 750,
			legend : {
				maxLines : 1,
				textStyle : {
					fontSize : 15
				}
			},
			chartArea : {
				left : 80,
				top : 10,
				width : '50%',
				height : '60%'
			},
			isStacked: true,
			series : series
		};

		google.visualization.events.addListener(chart, 'select', function() {
			console.log("trying to hide line on click of legend");
			var sel = chart.getSelection();
			// if selection length is 0, we deselected an element
			console.log("sel");
			console.log(sel);
			if (sel.length > 0) {
				// if row is undefined, we clicked on the legend
				if (sel[0].row === null) {
					var col = sel[0].column;
					console.log("column : ");
					console.log(col);
					if (columns[col] == col) {
						// hide the data series
						//            	 console.log("column condition true ");
						//          	 console.log(data.getColumnLabel(col));
						//        	 console.log(data.getColumnType(col));
						columns[col] = {
							label : data.getColumnLabel(col),
							type : data.getColumnType(col),
							calc : function() {
								return 0;
							}
						};

						// grey out the legend entry
						series[col - 1].color = '#CCCCCC';
					} else {
						// show the data series
						columns[col] = col;
						series[col - 1].color = null;
					}
					//                console.log("column dfn ");
					//              console.log(columns);
					var view = new google.visualization.DataView(data);
					view.setColumns(columns);
					chart.draw(view, options);
				}
			}
		});

		if (booleanHideGraphLoadStatus) {
			$("#filter-nochartdata").hide();
		}

		if (booleanHideGraphLoadStatus) {
			$("#nochartdata").hide();
		}
		//	$('#segment-dropdown').show();

	}
	function onlyUnique(value, index, self) {
		return self.indexOf(value) === index;
	}

	/* roll up analysis*/
	/* Last Update : 28thApril,2020
	 * Code By: Anant S. Mahale
	 *On Click of Roll-Up Analysis tab function being call 
	 */
	function loadRollUpAnalysis() {
		var tablename = $("#tableName").val();
		var idApp = $("#idApp").val();
		
		if (tablename) {
			$(function() {
				var form_data = {
					tableName : tablename,
					idApp : idApp
				};
				var request = $.ajax({
					url : './rollupAnalysisDropdownDataLoad', // DataQualityResultsController.java
					type : 'POST',
					headers: { 'token':$("#token").val()},
					data : form_data,
					dataType : 'json'
				});
				request.done(function(msg) {
					rollUpAnanlysisBindDataToDropdowns(msg); // to bind data to dropdowns
				});
				request.fail(function(jqXHR, textStatus) {
					console.log("loadRollUpAnalysis function Request failed: "+ textStatus);
				});

			});
		} else {
			console.log('loadRollUpAnalysis function : table name is empty or null');
		}

	}

	/*
	Updated Date: 28thApril,2020
	Code By:Anant S. Mahale
	To bind data to Select Roll Up Variable & Select Column Name dropdowns in Rollup Analysis tab
	 */
	function rollUpAnanlysisBindDataToDropdowns(obj) {
		var dGroupCalSplitObj = obj.dGroupCalSplit;
		var colNameObj = obj.colName;

		/*bind dgroupval*/
		var arrayLength = dGroupCalSplitObj.length;
		$("#distributioncheckRollUpVariableSelectid").empty();
		for (var i = 0; i < arrayLength; i++) {
			$("#distributioncheckRollUpVariableSelectid").append("<li><a onClick='validateHrefVulnerability(this);distributionRollUpVariable(this)'  >"+ dGroupCalSplitObj[i] + "</a></li>");
		}

		/*bind colname*/
		var arrayLength = colNameObj.length;
		$("#distributioncheckRollUpVariableSelectColumnNameid").empty();
		for (var i = 0; i < arrayLength; i++) {
			$("#distributioncheckRollUpVariableSelectColumnNameid").append("<li><a onClick='validateHrefVulnerability(this);distributionRollUpColumnName(this)'  >"+ colNameObj[i] + "</a></li>");
		}
	}

	var rollupVariable;
	var rollupVariableColName;

	/* distribution - rollup ananlysis tab - load rollup selected value into variable*/
	function distributionRollUpVariable(param) {
		document.getElementById("distributioncheckRollUpVariableSelectSpanid").innerHTML = "";
		rollupVariable = $(param).text();
		document.getElementById("distributioncheckRollUpVariableSelectSpanid").innerHTML = "["+ rollupVariable + "]";
	}

	/* distribution - rollup ananlysis tab - load column name dropdown selected value into variable*/
	function distributionRollUpColumnName(param) {
		document.getElementById("distributioncheckRollUpVariableSelectColumnNameSpanid").innerHTML = "";
		rollupVariableColName = $(param).text();
		document.getElementById("distributioncheckRollUpVariableSelectColumnNameSpanid").innerHTML = "["+ rollupVariableColName + "]";
	}

	/*
	On Click of Load Details button in Roll-Up Analysis tab
	1) load JSON Object in Table Tab datatable
	2) bind data to dropdown in TrendBreak Chart
	3) load data into comparison chart
	 */
	function loadrollupTabDetails() {
		document.getElementById("rollupdropdownErrMsg").innerHTML = "<div class='loader' style='margin-left: 50%;'></div>";
		
	//	$('.nav-tabs a[href="#rollUphome"]').tab('show');
		$('#rollupdropdownErrMsg').html(""); // erase previous error msg

		if (rollupVariable && rollupVariableColName) {
			$('#rolluptabs').show();
			loadrollupDataTable();
			rollupAnalysisTrendBreakChartDropdownDataLoad();
	//		callRollUpComparisonChart();
		} else {
			$('#rollupdropdownErrMsg').html("<div class='alert alert-danger'> <strong>Error!</strong> Roll-Up Variable Or Column Name is not selected  </div>");
		}
		document.getElementById("rollupdropdownErrMsg").innerHTML = "<h5><b> Roll-Up Analysis :  Roll-Up Variable : "+rollupVariable+" & Column Name : "+rollupVariableColName+" </b></h5>";
		
	}

	/*
		Update Date : 28thApril,2020
		Code By: Anant S. Mahale
		To call for comparison chart, in Roll-up Analysis
	 */
	function callRollUpComparisonChart() {
		var tablename = $("#tableName").val();
		var idApp = $("#idApp").val();
		$(function() {
			var form_data = {
				tableName : tablename,
				rollupVariable : rollupVariable,
				colName : rollupVariableColName,
				idApp : idApp
			};
			var request = $.ajax({
				url : './listOfRollUpVariableByTableName',
				type : 'POST',
				headers: { 'token':$("#token").val()},
				data : form_data,
				dataType : 'json'
			});
			request.done(function(msg) {
				bindListOfDGroupValToRollUpComparisonDropdown(msg);
			});
			request.fail(function(jqXHR, textStatus) {console.log("trendChartDropDown Request failed: "+ textStatus);
				
			});
		});
	}
	
	function bindListOfDGroupValToRollUpComparisonDropdown(obj) {
		console.log('bindListOfColumnNameToDropDown');
		console.log(obj);
		var dGroupValObj = obj.rollupVarlist;
		
		removejscssfile("./assets/pages/chosendropdown/js/chosen.jquery.js", "js");
		removejscssfile("./assets/pages/chosendropdown/js/init.js", "js");
		removejscssfile("./assets/pages/chosendropdown/js/prism.js", "js");
		
		document.getElementById("rollupAnanlysisComparisonChartRollupVariableDropdownParent").innerHTML = "";
		document.getElementById("rollupAnanlysisComparisonChartRollupVariableDropdownParent").innerHTML = "<select  class='chosen-select' multiple tabindex='4'><option value=''></option></select>";

		/*bind colname*/
		var arrayLength = dGroupValObj.length; 
		$("#rollupAnanlysisComparisonChartRollupVariableDropdownParent .chosen-select").empty();
		for (var i = 0; i < arrayLength; i++) {
		//	$("#comparisonTrendChart-dgroupVal-dropdown-menu").append("<li><a onClick='validateHrefVulnerability(this);comparisionChartBindValueToDGroupValVariable(this)' >"+ colNameObj[i] + "</a></li>");
			$("#rollupAnanlysisComparisonChartRollupVariableDropdownParent .chosen-select").append($("<option></option>")
                    .attr("value",dGroupValObj[i])
                    .text(dGroupValObj[i])); 
		}
		$("#rollupAnanlysisComparisonChartRollupVariableDropdownParent .chosen-container").css('width','100%');
		$.getScript("./assets/pages/chosendropdown/js/chosen.jquery.js");
		$.getScript("./assets/pages/chosendropdown/js/init.js");
		$.getScript("./assets/pages/chosendropdown/js/prism.js");
		
	}
	
	function showRollUpAnanlysisComparisonChart() {
		document.getElementById("rollupComparisonChartDivId-chart-headerid").innerHTML = "<div class='loader' style='margin-left: 50%;'></div>";
		var dGroupValList="";
		var temp;
		$('#rollupAnanlysisComparisonChartRollupVariableDropdownParent .chosen-choices .search-choice span').each(function() {
		    var x = $(this).text();
		    dGroupValList = dGroupValList+""+x+","; 
		});
		console.log("Selected DGroups: "+dGroupValList);
		
		if(dGroupValList.length == 0){
			dGroupValList="";
			$('#rollupAnanlysisComparisonChartRollupVariableDropdownParent option').each(function() {
			    var x = $(this).text();
			    dGroupValList = dGroupValList+""+x+","; 
			});
			
			console.log("All DGroups: "+dGroupValList);
	    }
		if(dGroupValList.length > 0){
			dGroupValList = dGroupValList.substring(0,dGroupValList.length-1);
		}
		console.log("Final DGroups: "+dGroupValList);
		temp = dGroupValList;
		
		var tablename = $("#tableName").val();
		var idApp = $("#idApp").val();
		
		if (tablename && rollupVariable && rollupVariableColName && temp) {
			$(function() {
				var form_data = {
					tableName : tablename,
					rollupVariable : rollupVariable,
					colName : rollupVariableColName,
					rolluplist : temp,
					idApp : idApp
				};
				var request = $.ajax({
					url : './callRollUpComparisonChart',
					type : 'POST',
					headers: { 'token':$("#token").val()},
					data : form_data,
					dataType : 'json'
				});
				request.done(function(msg) {
					rollupAnalysisComparisonChartBind(msg, 'Date & Run','Sum Of Num Stat'); //to bind data
					document.getElementById("rollupComparisonChartDivId-chart-headerid").innerHTML = "";
				});
				request.fail(function(jqXHR, textStatus) {
							console.log("callRollUpComparisonChart function Request failed: "+ textStatus);
						});
			});
		} else {
			console.log('callRollUpComparisonChart function :  tablename && rollupVariable && rollupVariableColName name is empty or null');
		}
	}
	/*
		Update Date : 28thApril,2020
		Code By: Anant S. Mahale
		Bind object to Google Combined Chart, 
	 */
	function rollupAnalysisComparisonChartBind(array, xaxis, yaxis) {

		booleanHideGraphLoadStatus = true;
		var header = array.header;
		var chartdata = array.chart;
		var data = new google.visualization.DataTable();
		data.addColumn('string', 'Date & Run');
		for (i = 0; i < header.length; i++) {
			data.addColumn('number', header[i]);
		}

		data.addRows(chartdata);

		 data.sort({
	         column: 0,
	         desc: true
	       });
		
		var options = {
			'title' : '',
			hAxis : {
				title : xaxis, direction:-1, slantedText:true, slantedTextAngle:45
			},
			vAxis : {
				title : yaxis,
				vAxis : {
					viewWindowMode : "explicit",
					viewWindow : {
						min : 0
					}
				}
			},
			'width' : 900,
			'height' : 750,
			legend : {
				maxLines : 1,
				textStyle : {
					fontSize : 15
				}
			},
			chartArea:{left:120,top:50,width:'70%'},
			isStacked: true
		};
		console.log("before clicking on legend");
		var chart = new google.visualization.LineChart(document
				.getElementById('rollupComparisonChartDivId')); // on this element google chart
		// will load
		chart.draw(data, options);
		var columns = [];
		var series = {};
		for (var i = 0; i < data.getNumberOfColumns(); i++) {
			columns.push(i);
			if (i > 0) {
				series[i - 1] = {};
			}
		}

		var options = {
			'title' : '',
			hAxis : {
				title : xaxis, direction:-1, slantedText:true, slantedTextAngle:45
			},
			vAxis : {
				title : yaxis,
				vAxis : {
					viewWindowMode : "explicit",
					viewWindow : {
						min : 0
					}
				}
			},
			'width' : 900,
			'height' : 750,
			legend : {
				maxLines : 1,
				textStyle : {
					fontSize : 15
				}
			},
			chartArea:{left:120,top:50,width:'70%'},	
			isStacked: true,
			series : series
		};

		google.visualization.events.addListener(chart, 'select', function() {
			//			console.log("trying to hide line on click of legend");
			var sel = chart.getSelection();
			// if selection length is 0, we deselected an element
			console.log("sel");
			console.log(sel);
			if (sel.length > 0) {
				// if row is undefined, we clicked on the legend
				if (sel[0].row === null) {
					var col = sel[0].column;
					console.log("column : ");
					console.log(col);
					if (columns[col] == col) {
						// hide the data series
						//                	 console.log("column condition true ");
						//              	 console.log(data.getColumnLabel(col));
						//            	 console.log(data.getColumnType(col));
						columns[col] = {
							label : data.getColumnLabel(col),
							type : data.getColumnType(col),
							calc : function() {
								return 0;
							}
						};
						// grey out the legend entry
						series[col - 1].color = '#CCCCCC';
					} else {
						// show the data series
						columns[col] = col;
						series[col - 1].color = null;
					}
					//          console.log("column dfn ");
					//         console.log(columns);
					var view = new google.visualization.DataView(data);
					view.setColumns(columns);
					chart.draw(view, options);
				}
			}
		});
	}

	/*
		Update Date : 28thApril,2020
		Code By: Anant S. Mahale
		Ajax call for to get JSON object for Trend break chart dropdown
	 */
	function rollupAnalysisTrendBreakChartDropdownDataLoad() {
		var tablename = $("#tableName").val();
		var idApp = $("#idApp").val();
		if (tablename && rollupVariable && rollupVariableColName) {
			$(function() {
				var form_data = {
					tableName : tablename,
					rollupVariable : rollupVariable,
					colName : rollupVariableColName,
					idApp : idApp
				};
				var request = $.ajax({
					url : './rollupAnalysisTrendBreakChartDropdownDataLoad',
					type : 'POST',
					headers: { 'token':$("#token").val()},
					data : form_data,
					dataType : 'json'
				});
				request.done(function(msg) {
					rollupAnalysisTrendBreakChartDropdownDataBind(msg);
				});
				request.fail(function(jqXHR, textStatus) {
							console.log("rollupAnalysisTrendBreakChartDropdownDataLoad function Request failed: "+ textStatus);
						});

			});
		} else {
			console.log('rollupAnalysisTrendBreakChartDropdownDataLoad function : table name is empty or null');
		}
	}

	/*
		Update Date : 28thApril,2020
		Code By: Anant S. Mahale
		Bind JSON Object to dropdown
	 */
	function rollupAnalysisTrendBreakChartDropdownDataBind(obj) {

		/*bind dgroupval*/
		var arrayLength = obj.length;
		$("#distributioncheckRollUpTrendBreakSelectid").empty();
		for (var i = 0; i < arrayLength; i++) {
			console.log(obj[i]);
			$("#distributioncheckRollUpTrendBreakSelectid").append("<li><a onClick='validateHrefVulnerability(this);distributionRollUpTrendBreakDropdown(this)'  >"+ obj[i] + "</a></li>");
		}
	}

	var rollUpTrendBreakElement;
	
	function distributionRollUpTrendBreakDropdown(param){
		document.getElementById("distributioncheckRollUpTrendBreakSelectSpanid").innerHTML = "";
		rollUpTrendBreakElement = $(param).text();
		document.getElementById("distributioncheckRollUpTrendBreakSelectSpanid").innerHTML = "["+ rollUpTrendBreakElement + "]";
	}
	/*
		Update Date : 28thApril,2020
		Code By: Anant S. Mahale
		AJax call for TrendBreak Chart
	 */
	function distributionRollUpTrendBreakElement(param) {
			document.getElementById("distributionRollUpTrendBreakElement-chart-headerid").innerHTML = "<div class='loader' style='margin-left: 50%;'></div>";
			console.log('distributionRollUpTrendBreakElement');
			if (param) {
				var paramObj = param.element;
				
				var arrayLength = paramObj.length;

				for (var i = 0; i < arrayLength; i++) {
					if (i == 0) {
						rollupVariable = paramObj[i].toString();
					}
					if (i == 1) {
						rollupVariableColName = paramObj[i].toString();
					}
					if (i == 2) {
						rollUpTrendBreakElement = paramObj[i].toString();
					}
				}
				$('.nav-tabs a[href="#rollUpmenu1"]').tab('show');
				document.getElementById("distributioncheckRollUpTrendBreakSelectSpanid").innerHTML = "";
				document.getElementById("distributioncheckRollUpTrendBreakSelectSpanid").innerHTML = "["+ rollUpTrendBreakElement + "]";
			}
		var tablename = $("#tableName").val();
		var idApp = $("#idApp").val();
		console.log(tablename, rollupVariable, rollupVariableColName, rollUpTrendBreakElement);
		if(rollUpTrendBreakElement){
			$(function() {
				var form_data = {
					tableName : tablename,
					rollupVariable : rollupVariable,
					colName : rollupVariableColName,
					element : rollUpTrendBreakElement,
					idApp : idApp
				};
				var request = $.ajax({
					url : './rollupAnalysisTrendBreakChartData',
					type : 'POST',
					data : form_data,
					headers: { 'token':$("#token").val()},
					dataType : 'json'
				});
				request.done(function(msg) {
					distributionRollUpTrendBreakCharLoad(msg); //, 'Date', 'Sum Of Num Stat'
					document.getElementById("distributionRollUpTrendBreakElement-chart-headerid").innerHTML = "<h5><b> Roll-Up Analysis : Trendbreak Chart Column Name : "+rollupVariableColName+", Roll-Up Variable : "+rollupVariable+" and Element from Roll-Up Variable : "+rollUpTrendBreakElement+" </b></h5>";
					
				});
				request.fail(function(jqXHR, textStatus) {
					console.log("Request failed: " + textStatus);
				});
			});
		}else{
			document.getElementById("distributionRollUpTrendBreakElement-chart-headerid").innerHTML = "<div class='alert alert-danger'> <strong>Error!</strong> Select values from dropdowns </div>";
		}
		
	}

	/*
		Update Date : 28thApril,2020
		Code By: Anant S. Mahale
		Bind json object to trend break chart
	 */
	function distributionRollUpTrendBreakCharLoad(obj) {
		var children = obj.children;
		var data = new google.visualization.DataTable();
		 data.addColumn('string', 'Date & Run');
	     data.addColumn('number', 'Upper Limit');
	     data.addColumn('number', 'Sum Of Num Stats');
	     data.addColumn('number', 'Lower Limit');
	     data.addRows(children);
	     
	     data.sort({
	         column: 0,
	         desc: true
	       });
	     
		var options = {
			title : '',
			vAxis : {
				title : 'Sum Of Num Stat'
			},
			hAxis : {
				title : 'Date & Run',direction:-1, slantedText:true, slantedTextAngle:45
			},
			seriesType : 'line',
			series : {
				1 : {
					color : 'black',
					lineWidth : 0,
					pointSize : 5
				}
			},
			 chartArea:{left:80,top:10,width:'70%'},
				width : 1000,
				height : 400,
				isStacked: true
		};

		var chart = new google.visualization.ComboChart(document.getElementById('rolluptrendBreakChart_div'));
		chart.draw(data, options);
	}

	/*
		Update Date : 28thApril,2020
		Code By: Anant S. Mahale
		Roll-Up Table - Datatable Ajax call & bind data to data-table
	 */
	function loadrollupDataTable() {
		var tablename = $("#tableName").val();
		var idApp = $("#idApp").val();
		
		if ($.fn.DataTable.isDataTable('#rollupTable')) {
			$('#rollupTable').DataTable().destroy();
			$('#rollupTable tbody').empty();
		}
		console.log('loadrollupDataTable');
		console.log(tableName, rollupVariable, rollupVariableColName);
		$('#rollupTable').dataTable(
				{
					"pagingType" : "full_numbers",
					// 'autoWidth': false,
					    'scrollX': true,
					    'scrollCollapse': true,
					"sAjaxSource" : path + "/rollupdatatable?tableName="+ tableName + "&rollupVariable=" + rollupVariable+ "&colName=" + rollupVariableColName+"&idApp="+idApp,
					"aoColumns": [
                        {"data" : "date"},
                        {"data" : "run"},
                        {"data" : "colName"},
                        {"data" : "status",
                            "render": function(data, type, row, meta){
                                var rollupVariable1 = rollupVariable;
                                var colName = row.colName;
                                var roll_up_column = row.rollUpColumn;

                                console.log("@@@@@"+rollupVariable1+colName+roll_up_column);
                                var listParamVars = [ rollupVariable1, colName, roll_up_column];
                                var trendChartJson = {
                                     "element" : listParamVars
                                };

                                var JSONObjectRollupTrendBreakChart = JSON.stringify(trendChartJson);
                                if (data == "Passed") {
                                    data = "<div class='row' style='width:100px; margin-left: 4%;'><span class='label label-success label-sm'>Passed</span><a onclick='distributionRollUpTrendBreakElement("+JSONObjectRollupTrendBreakChart+")'><span class='fa fa-bar-chart' style='margin-left: 4%;'></span></a></div>";


                                } else if (data =="Failed") {

                                    data = "<div class='row' style='width:100px; margin-left: 4%;'><span class='label label-danger label-sm'>Failed</span><a onclick='distributionRollUpTrendBreakElement("+JSONObjectRollupTrendBreakChart+")'><span class='fa fa-bar-chart' style='margin-left: 4%;'></span></a></div>";
                                }

                                return data;
                                }
                        },
                        {"data" : "recordCount"},
                        {"data" : "min"},
                        {"data" : "max"},
                        {"data" : "numMeanThreshold"},
                        {"data" : "sumOfNumStat"},
                        {"data" : "histAvg"},
                        {"data" : "histStdDev"},
                        {"data" : "rollUpColumn"},
                        {"data" : "mean"},
                        {"data" : "upperLimit"},
                        {"data" : "lowerLimit"}

                    ],

					"dom" : 'C<"clear">lfrtip',
					colVis : {
						"align" : "right",
						restore : "Restore",
						showAll : "Show all",
						showNone : "Show none",
						order : 'alpha'

					},
					"language" : {
						"infoFiltered" : ""
					},
					"dom" : 'Cf<"toolbar"">rtip',

				});
	}

	/* sum of num stats*/
	/*
	Update Date : 29thApril,2020
	Code By: Anant S. Mahale
	Sum Of Num Stats Tab -  Ajax call to get object of dgroup values
	 */
	function sumOfNumStats() {
		var tablename = $("#tableName").val();
		var idApp = $("#idApp").val();
		$(function() {
			var form_data = {
				tableName : tablename,
				idApp : idApp
			};
			var request = $.ajax({
				url : './listOfDGroupValByTableName',
				type : 'POST',
				headers: { 'token':$("#token").val()},
				data : form_data,
				dataType : 'json'
			});
			request.done(function(msg) {
				console.log(" trendChartDropDown success");
				sumOfNumStatsDgroupValDropdown(msg);
			});
			request.fail(function(jqXHR, textStatus) {console.log("trendChartDropDown Request failed: "+ textStatus);
					});
		});
	}
	
	/*
	Update Date : 29thApril,2020
	Code By: Anant S. Mahale
	Bind JSON Object to Data dgroup value dropdown
	 */
	function sumOfNumStatsDgroupValDropdown(obj) {
		var dGroupValObj = obj.dGroupVal;
		var colNameObj = obj.ColName;

		/*bind dgroupval*/
		var arrayLength = dGroupValObj.length;
		var $dropdown = $("#sumOfNumStatsChart-dropdown-menu");
		$("#sumOfNumStatsChart-dropdown-menu").empty();
		for (var i = 0; i < arrayLength; i++) {
			console.log(obj[i]);
			$("#sumOfNumStatsChart-dropdown-menu").append("<li><a onClick='validateHrefVulnerability(this);sumOfNumStatsChartDGroupValOnClick(this)'  >"+ dGroupValObj[i] + "</a></li>");
		}

		/*bind colname*/
		var arrayLength = colNameObj.length;
		var $dropdown = $("#trendChart-dropdown-menu");
		$("#sumOfNumStatsChart-colname-dropdown-menu").empty();
		for (var i = 0; i < arrayLength; i++) {
			console.log(obj[i]);
			$("#sumOfNumStatsChart-colname-dropdown-menu").append("<li><a onClick='validateHrefVulnerability(this);sumOfNumStatsChartColNameOnClick(this)'  >"+ colNameObj[i] + "</a></li>");
		}
	}
	
	function custMcsgSumOfNumStatsColumnsDropdown() {
		var tablename = $("#tableName2").val();
		var idApp = $("#idApp").val();
		$(function() {
			var form_data = {
				tableName : tablename,
				idApp : idApp
			};
			var request = $.ajax({
				url : './listOfDistributionCheckColumnsByTableName',
				type : 'POST',
				headers: { 'token':$("#token").val()},
				data : form_data,
				dataType : 'json'
			});
			request.done(function(msg) {
				console.log(" trendChartDropDown success");
				custmcsgBindSumOfNumStatsColumnsDropdown(msg);
			});
			request.fail(function(jqXHR, textStatus) {console.log("trendChartDropDown Request failed: "+ textStatus);
					});
		});
	}
	
	function custmcsgBindSumOfNumStatsColumnsDropdown(obj) {
		var colNameObj = obj;

		/*bind colname*/
		var arrayLength = colNameObj.length;
		var $dropdown = $("#custmcsg-trendChart-dropdown-menu");
		$("#custmcsg-sumOfNumStatsChart-colname-dropdown-menu").empty();
		for (var i = 0; i < arrayLength; i++) {
			console.log(obj[i]);
			$("#custmcsg-sumOfNumStatsChart-colname-dropdown-menu").append("<li><a onClick='validateHrefVulnerability(this);custmcsgSumOfNumStatsChartColNameOnClick(\""+colNameObj[i]+"\");'  >"+ colNameObj[i] + "</a></li>");
		}
	}
	
	function custMcsgSumOfNumStatsDGroupValDropdown(param) {
		var tablename = $("#tableName2").val();
		var columnName = param;
		var idApp = $("#idApp").val();
		$(function() {
			var form_data = {
				tableName : tablename,
				columnName : columnName,
				idApp : idApp
			};
			var request = $.ajax({
				url : './listOfDGroupValForColumnByTableName',
				type : 'POST',
				headers: { 'token':$("#token").val()},
				data : form_data,
				dataType : 'json'
			});
			request.done(function(msg) {
				console.log(" trendChartDropDown success");
				custmcsgBindSumOfNumStatsDgroupValDropdown(msg);
			});
			request.fail(function(jqXHR, textStatus) {console.log("trendChartDropDown Request failed: "+ textStatus);
					});
		});
	}
	
	function custmcsgBindSumOfNumStatsDgroupValDropdown(obj) {
		var dGroupValObj = obj;

		/*bind dgroupval*/
		var arrayLength = dGroupValObj.length;
		var $dropdown = $("#custmcsg-sumOfNumStatsChart-dropdown-menu");
		$("#custmcsg-sumOfNumStatsChart-dropdown-menu").empty();
		for (var i = 0; i < arrayLength; i++) {
			console.log(obj[i]);
			$("#custmcsg-sumOfNumStatsChart-dropdown-menu").append("<li><a onClick='validateHrefVulnerability(this);custmcsgSumOfNumStatsChartDGroupValOnClick(this)'  >"+ dGroupValObj[i] + "</a></li>");
		}
	}

	// variable for selected dgroup value 
	var sumOfNumStatsDGroupSelectedValue;
	var sumOfNumStatsColumnNameSelectedValue;
	var sumOfNumStatsRunValue;

	function sumOfNumStatsChartDGroupValOnClick(param) {
		document.getElementById("sumOfNumStatsChart-Span").innerHTML = "";
		sumOfNumStatsDGroupSelectedValue = $(param).text();
		document.getElementById("sumOfNumStatsChart-Span").innerHTML = "["+ sumOfNumStatsDGroupSelectedValue + "]";
	}

	function sumOfNumStatsChartColNameOnClick(param) {
		document.getElementById("sumOfNumStatsChart-colname-Span").innerHTML = "";
		sumOfNumStatsColumnNameSelectedValue = $(param).text();
		document.getElementById("sumOfNumStatsChart-colname-Span").innerHTML = "["+ sumOfNumStatsColumnNameSelectedValue + "]";
	}
	
	function custmcsgSumOfNumStatsChartDGroupValOnClick(param) {
		document.getElementById("custmcsg-sumOfNumStatsChart-Span").innerHTML = "";
		sumOfNumStatsDGroupSelectedValue = $(param).text();
		document.getElementById("custmcsg-sumOfNumStatsChart-Span").innerHTML = "["+ sumOfNumStatsDGroupSelectedValue + "]";
	}

	function custmcsgSumOfNumStatsChartColNameOnClick(param) {
		document.getElementById("custmcsg-sumOfNumStatsChart-colname-Span").innerHTML = "";
		sumOfNumStatsColumnNameSelectedValue = param;
		document.getElementById("custmcsg-sumOfNumStatsChart-colname-Span").innerHTML = "["+ sumOfNumStatsColumnNameSelectedValue + "]";
		custMcsgSumOfNumStatsDGroupValDropdown(sumOfNumStatsColumnNameSelectedValue);
	}

	/*
		Updated Date : 5thMay,2020
	 	Code By : Anant S. Mahale
		with selected dgroup value get chart object
	
	 	Ajax call to get Sum Chart (Sum Of Num Stats Chart) Json Object. There are two ways 1) from on click of Sum Chart tab -> sumOfNumStats()
	 	get dropdown data (DGroupVal and Column Name) and bind that to sumOfNumStatsDGroupSelectedValue & sumOfNumStatsColumnNameSelectedValue variables OR 
	 	2) From main data table on click of graph icon, get its respective DGroupVal and Column Name and generate chart.
	 */
	function sumOfNumStatsChartDropdownMenuValueSection(param) {
		console.log('sumOfNumStatsChartDropdownMenuValueSection started');
		document.getElementById("sum-chart-headerid").innerHTML = "";
		document.getElementById("sum-chart-headerid").innerHTML = "<div class='loader' style='margin-left: 50%;'></div>";
		if (param) {
			var paramObj = param.data;
			var arrayLength = paramObj.length;

			for (var i = 0; i < arrayLength; i++) {
				if (i == 0) {
					sumOfNumStatsDGroupSelectedValue = paramObj[i].toString();
				}
				if (i == 1) {
					sumOfNumStatsColumnNameSelectedValue = paramObj[i]
							.toString();
				}
				if (i == 2) {
					sumOfNumStatsRunValue = paramObj[i].toString();
				}
			}
			$('.nav-tabs a[href="#menu5"]').tab('show');
			document.getElementById("sumOfNumStatsChart-Span").innerHTML = "";
			document.getElementById("sumOfNumStatsChart-colname-Span").innerHTML = "";
			document.getElementById("sumOfNumStatsChart-Span").innerHTML = "["+ sumOfNumStatsDGroupSelectedValue + "]";
			document.getElementById("sumOfNumStatsChart-colname-Span").innerHTML = "["+ sumOfNumStatsColumnNameSelectedValue + "]";
			sumOfNumStats();
		}
		console.log("before condition");
		console.log(sumOfNumStatsRunValue);
		console.log("after condition");
		if (sumOfNumStatsRunValue === null|| sumOfNumStatsRunValue === undefined) {
			sumOfNumStatsRunValue = '0';
			console.log(sumOfNumStatsRunValue);
		}

		var tablename = $("#tableName").val();
		var idApp = $("#idApp").val();
		
		if (tablename && sumOfNumStatsDGroupSelectedValue
				&& sumOfNumStatsColumnNameSelectedValue
				&& sumOfNumStatsRunValue) {
			console.log("ajax call");
			console.log(sumOfNumStatsRunValue);
			sumOfNumStatsRunValue = '0';
			$(function() {
				var form_data = {
					tableName : tablename,
					dGroupVal : sumOfNumStatsDGroupSelectedValue,
					colName : sumOfNumStatsColumnNameSelectedValue,
					run : sumOfNumStatsRunValue,
					idApp : idApp
				};
				var request = $.ajax({
					url : './sumOfNumStatsChart',
					type : 'POST',
					headers: { 'token':$("#token").val()},
					data : form_data,
					dataType : 'json'
				});
				request.done(function(msg) {
							sumOfNumStatsRunValue = '0';
							sumOfNumStatsChart(msg); //, 'Date', 'Sum Of Num Stat'
							document.getElementById('sum-chart-headerid').innerHTML = '<h5><b> Distribution Trend Chart with DGroupVal : '+ sumOfNumStatsDGroupSelectedValue+ ' & Column Name : '+ sumOfNumStatsColumnNameSelectedValue+ ' </b></h5>';
						});
				request.fail(function(jqXHR, textStatus) {
					sumOfNumStatsRunValue = '0';
					console.log("Request failed: " + textStatus);
				});
			});
		} else {
			document.getElementById("sum-chart-headerid").innerHTML = "<div class='alert alert-danger'> <strong>Error!</strong> Select values from dropdowns </div>";
		}
	}
	 	
 	function custmcsgSumOfNumStatsChartDropdownMenuValueSection(param) {
		console.log('sumOfNumStatsChartDropdownMenuValueSection started');
		document.getElementById("custmcsg-sum-chart-headerid").innerHTML = "";
		document.getElementById("custmcsg-sum-chart-headerid").innerHTML = "<div class='loader' style='margin-left: 50%;'></div>";
		if (param) {
			var paramObj = param.data;
			var arrayLength = paramObj.length;

			for (var i = 0; i < arrayLength; i++) {
				if (i == 0) {
					sumOfNumStatsDGroupSelectedValue = paramObj[i].toString();
				}
				if (i == 1) {
					sumOfNumStatsColumnNameSelectedValue = paramObj[i]
							.toString();
				}
				if (i == 2) {
					sumOfNumStatsRunValue = paramObj[i].toString();
				}
			}
			custMcsgSumOfNumStatsColumnsDropdown();
			custMcsgSumOfNumStatsDGroupValDropdown(sumOfNumStatsColumnNameSelectedValue);
			$('.nav-tabs a[href="#custom_menu3"]').tab('show');
			document.getElementById("custmcsg-sumOfNumStatsChart-Span").innerHTML = "";
			document.getElementById("custmcsg-sumOfNumStatsChart-colname-Span").innerHTML = "";
			document.getElementById("custmcsg-sumOfNumStatsChart-Span").innerHTML = "["+ sumOfNumStatsDGroupSelectedValue + "]";
			document.getElementById("custmcsg-sumOfNumStatsChart-colname-Span").innerHTML = "["+ sumOfNumStatsColumnNameSelectedValue + "]";
		}
		console.log("before condition");
		console.log(sumOfNumStatsRunValue);
		console.log("after condition");
		if (sumOfNumStatsRunValue === null|| sumOfNumStatsRunValue === undefined) {
			sumOfNumStatsRunValue = '0';
			console.log(sumOfNumStatsRunValue);
		}

		var tablename = $("#tableName2").val();
		var idApp = $("#idApp").val();
		
		if (tablename && sumOfNumStatsDGroupSelectedValue
				&& sumOfNumStatsColumnNameSelectedValue
				&& sumOfNumStatsRunValue) {
			console.log("ajax call");
			console.log(sumOfNumStatsRunValue);
			sumOfNumStatsRunValue = '0';
			$(function() {
				var form_data = {
					tableName : tablename,
					dGroupVal : sumOfNumStatsDGroupSelectedValue,
					colName : sumOfNumStatsColumnNameSelectedValue,
					run : sumOfNumStatsRunValue,
					idApp : idApp
				};
				var request = $.ajax({
					url : './sumOfNumStatsChart',
					type : 'POST',
					headers: { 'token':$("#token").val()},
					data : form_data,
					dataType : 'json'
				});
				request.done(function(msg) {
							sumOfNumStatsRunValue = '0';
							custmcsgSumOfNumStatsChart(msg); //, 'Date', 'Sum Of Num Stat'
							document.getElementById('custmcsg-sum-chart-headerid').innerHTML = '<h5><b> Distribution Trend Chart with DGroupVal : '+ sumOfNumStatsDGroupSelectedValue+ ' & Column Name : '+ sumOfNumStatsColumnNameSelectedValue+ ' </b></h5>';
						});
				request.fail(function(jqXHR, textStatus) {
					sumOfNumStatsRunValue = '0';
					console.log("Request failed: " + textStatus);
				});
			});
		} else {
			document.getElementById("custmcsg-sum-chart-headerid").innerHTML = "<div class='alert alert-danger'> <strong>Error!</strong> Select values from dropdowns </div>";
		}
	}

	/*
		Update Date : 06thMay,2020
		Code By: Anant S. Mahale
		bind oject to data drift object
	 */
	function sumOfNumStatsChart(obj) {
		var sumOfNumStatsChart = obj.sumOfNumStatsChart;
		var data = new google.visualization.DataTable();
		data.addColumn('string', 'Date & Run');
		data.addColumn('number', 'Upper Limit');
		data.addColumn('number', 'Sum Of Num Stats');
		data.addColumn('number', 'Lower Limit');
		data.addRows(sumOfNumStatsChart);
		
		 data.sort({
	         column: 0,
	         desc: true
	       });
		
		var options = {
			title : '',
			vAxis : {
				title : 'Sum Of Num Stat'
			},
			hAxis : {
				title : 'Date & Run',direction:-1, slantedText:true, slantedTextAngle:45
			},
			seriesType : 'line',
			series : {
				1 : {
					color : 'black',
					lineWidth : 0,
					pointSize : 5
				}
			},
			 chartArea:{left:80,top:10,width:'70%'},
			width : 1000,
			height : 400,
			isStacked: true
		};

		var chart = new google.visualization.ComboChart(document.getElementById('sumOfNumStatsChart_div'));
		chart.draw(data, options);
	}
	
	function custmcsgSumOfNumStatsChart(obj) {
		var sumOfNumStatsChart = obj.sumOfNumStatsChart;
		var data = new google.visualization.DataTable();
		data.addColumn('string', 'Date & Run');
		data.addColumn('number', 'Upper Limit');
		data.addColumn('number', 'Sum Of Num Stats');
		data.addColumn('number', 'Lower Limit');
		data.addRows(sumOfNumStatsChart);
		
		 data.sort({
	         column: 0,
	         desc: true
	       });
		
		var options = {
			title : '',
			vAxis : {
				title : 'Sum Of Num Stat'
			},
			hAxis : {
				title : 'Date & Run',direction:-1, slantedText:true, slantedTextAngle:45
			},
			seriesType : 'line',
			series : {
				1 : {
					color : 'black',
					lineWidth : 0,
					pointSize : 5
				}
			},
			 chartArea:{left:80,top:10,width:'70%'},
			width : 1000,
			height : 400,
			isStacked: true
		};

		var chart = new google.visualization.ComboChart(document.getElementById('custmcsg_sumOfNumStatsChart_div'));
		chart.draw(data, options);
	}
	
	function returnListOfDGroupValWithTableName() {
		console.log('returnListOfDGroupValWithTableName');
		var tablename = $("#tableName").val();
		var idApp = $("#idApp").val();
		$(function() {
			var form_data = {
				tableName : tablename,
				idApp : idApp
			};
			var request = $.ajax({
				url : './listOfDGroupValByTableName',
				type : 'POST',
				headers: { 'token':$("#token").val()},
				data : form_data,
				dataType : 'json'
			});
			request.done(function(msg) {
				console.log(msg);
				return msg;
			});
			request.fail(function(jqXHR, textStatus) {console.log("trendChartDropDown Request failed: "+ textStatus);
				return null;		
			});
		});
	}
	
	function removejscssfile(filename, filetype){
	    var targetelement=(filetype=="js")? "script" : (filetype=="css")? "link" : "none" //determine element type to create nodelist from
	    var targetattr=(filetype=="js")? "src" : (filetype=="css")? "href" : "none" //determine corresponding attribute to test for
	    var allsuspects=document.getElementsByTagName(targetelement)
	    for (var i=allsuspects.length; i>=0; i--){ //search backwards within nodelist for matching elements to remove
	    if (allsuspects[i] && allsuspects[i].getAttribute(targetattr)!=null && allsuspects[i].getAttribute(targetattr).indexOf(filename)!=-1)
	        allsuspects[i].parentNode.removeChild(allsuspects[i]) //remove element by calling parentNode.removeChild()
	    }
	}
</script>
</html>