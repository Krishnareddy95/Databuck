<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="com.databuck.service.RBACController"%>

<jsp:include page="header.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<jsp:include page="container.jsp" />

<style>
	#project_statistics {
		display: flex;
		align-items: stretch;
	}

	#project_statistics>div {
		margin: 10px;
		font-weight: bold;
		font-size: 17px;
		vertical-align: middle;
	}
	
	#stubVersionOutput {
		color: black;
		background-color: #FEF9E7;
		font-size: 15px;
		width: 100%;
		height: 150px;
	}		
	
	#divAggrAreaGraphChart {
		font-size: 17px;
		color: red;
		width: 100%;
		height: 250px;	
	}
	
	.AnkerButton, .AnkerButton:focus {
		color:White;
		background: #0088cc;
		font-family: Arial, Helvetica, sans-serif;
		text-decoration: none;
		border-radius: 5px;
		font-size: 13.5px;
		font-weight: bold;
		padding: 18px;
		margin: 25px;
		letter-spacing: 1px;
		display: inline-block;
	}

	.AnkerButton:hover {
		background: linear-gradient(#0099e6, #004466); 
		text-decoration: none;
		color: White;
		letter-spacing: 1px;
	}	

	.mainGraphContainer {
		width: 100%;
		padding: 5px;
		background-color: #d4e3f7;
		display: flex;
		flex-wrap: wrap;
	}

	.consolidatedChildDiv1 {
		min-height: 50px;
		flex: 1 1 auto;
		margin: 8px;
	}

	.consolidatedChildDiv2 {
		min-height: 100px;
		margin: 8px;
		width: 99%;
		margin: 0 auto;
		background-color: white;
	}
	
	.consolidatedChildDiv2 table, .consolidatedChildDiv1 table {
		margin: auto !important;
		min-width: 50px !important;
	}	
	
	div.main-graph-container {
		width: 100%;
		background-color: #e0ebf9;
		margin: 2px;
	}

	div.sub-graph-container {
		width: 100%;
		padding: 0px;
		background-color: #e0ebf9;
		display: flex;
		flex-wrap: wrap;
	}

	div.sub-graph-child {
		background-color: white;
		flex: 1 1 auto;
		margin: 8px;
	}

	table.graph-table-layout {
		width: 100%;
		min-height: 50px;
		border-collapse: collapse;
	}

	table.graph-table-layout th, td {
		border: 10px solid #e0ebf9;
		background: white;
		padding: 15px !important;
		text-align: center;
		margin: auto;
		width: 50%;
		min-height: 50px;
	}

	div.graph-group {
		border: 2px solid #66b3ff;
		width: 100%;
		margin: auto;
		box-sizing: border-box !important;
	}
	
	div.gauge-graph {
		display: inline-block !important; 
		margin: auto !important;
		min-height: 50px;
	}
	
	span.dq-index-date {
		color: #327ad5;
		font-weight: bold;
		font-size: 13;
	}
</style>

<div class="page-content-wrapper">
	<div class="page-content">
		<div class="row">
			<div class="col-md-6" style="width: 100%;">
				<div class="portlet light bordered init">
					<div class="cotainer">
						<div class="tabbable tabbable-custom boxless tabbable-reversed">
							<ul class="nav nav-tabs" style="border-bottom: 1px solid #ddd;">
								<li class="active" style="width: 8.63%"><a onClick="validateHrefVulnerability(this)"  href="executiveSummary" style="padding: 2px 2px;"><strong>Executive	Summary</strong></a></li>
								<li style="width: 8%"><a onClick="validateHrefVulnerability(this)"  href="dashboard_View" style="padding: 2px 2px;"><strong>Detailed View</strong></a></li>
								<li style="width: 8%"><a onClick="validateHrefVulnerability(this)"  href="dqUniverse" style="padding: 2px 2px;"><strong>DQ Universe</strong></a></li>
								<li style="width: 8%"><a onClick="validateHrefVulnerability(this)"  href="locationInfo" style="padding: 2px 2px;"><strong>DQ Lineage</strong></a></li>
									<%
										boolean flag = false;
										flag = RBACController.rbac("MyViews", "R", session);
										if (flag) {
									%>
										<li style="width: 8%"><a onClick="validateHrefVulnerability(this)"  href="myViews" style="padding: 2px 2px;"><strong>My Views</strong></a></li>
									<% 
										} 
									%>
							</ul>
						</div>
					</div>

					<div class="portlet-title">						

						<div class="form-body">
							<div class="row">							
								<div class="caption font-red-sunglo">
									<span class="caption-subject bold ">Executive Summary</span>
								</div>
								<br/> <span class="required"></span>

								<div class="col-md-4 col-capture " id="s4_tablename_id">
									<div class="form-group form-md-line-input">
										<select class="form-control multiselect  text-left" style="text-align: left;"
											id="lstProject" name="listProject" multiple="multiple">
											 <option value="${selectedProject.idProject}" selected>
												${selectedProject.projectName}</option> 
											<c:forEach items="${projectList}" var="projectListdata">
												<option value="${projectListdata.idProject}">
													${projectListdata.projectName}</option>
											</c:forEach>
										</select> <input type="hidden" id="selectedProject" name="selectedProject"	value="" /> <label for="form_control_1" id="host-id">Project Names*</label>
									</div>
									<br /> <span class="required"></span>
								</div>								
								
								<div class="col-md-2 col-capture " id="s4_tablename_id">
									<div class="form-group form-md-line-input">
										<select class="form-control" id="dateSelectionId" name="dateSelectionId">
										<option value="-1">Please Choose One</option>
											<option value="1">Daily</option>
											<option value="2">Weekly</option>
											<option value="3">Bi-monthly</option>
											<option value="4">Monthly</option>
											<option value="5">Quarterly</option>
											<option value="6">Yearly</option>
										</select> 
											
										<input type="hidden" id="dateSelectedIp" name="dateSelectedIp"	value="" /> 
										<label for="form_control_1" id="host-id">Date Selection*</label>
									</div>
									<br /> <span class="required"></span>
								</div>
								
								<div class="col-md-2 col-capture " id="s4_tablename_id">
									<div class="form-group form-md-line-input">
										<select class="form-control" style="text-align: left;"
											id="lstDownloadFormat" name="lstDownloadFormat">
											<option value="1-CSV" selected>CSV Format</option>											
											<option value="2-PDF">PDF Format</option>
										</select>
										<label for="lstDownloadFormat" id="host-id">Download Format*</label>										
									</div>
									<div style='display:none; margin-left: 8px; margin-top: -8px;'>
										<button id='BtnDownloadDqReport' class='AnkerButton'>Download Report</button>										
									</div>																			
								</div>	
								
															
							</div>	
						</div>

						<!--
						<div class='mainGraphContainer'>
							<div class='consolidatedChildDiv1'>
								<label for="lstProject">Project Names*</label></br>
								<select class="form-control multiselect  text-left" style="text-align: left;"
									id="lstProject" name="listProject" multiple="multiple">
									<option value="${selectedProject.idProject}" selected>
										${selectedProject.projectName}</option>
									<c:forEach items="${projectList}" var="projectListdata">
										<option value="${projectListdata.idProject}">
											${projectListdata.projectName}</option>
									</c:forEach>
								</select>

							</div>
							<div class='consolidatedChildDiv1'>
								<p>ok</p>
							</div>
							<div class='consolidatedChildDiv1'>
								<p>ok</p>
							</div>
							<div class='consolidatedChildDiv1'>
								<p>ok</p>
							</div>							
						</div>	
						-->

						<div class='main-graph-container'>

							<div class='graph-group'>
								<table class='graph-table-layout'>
									<tr>
										<th>
											<div style='height: 280px;' id='consolidatedPreviousDqGraph'>&nbsp;&nbsp;</div>
										</th>
										<th>
											<div style='height: 280px;' id='consolidatedCurrentDqGraph'>&nbsp;&nbsp;</div>
										</th>

									</tr>
									<tr>
										<td colspan='2'>
											<div id='consolidatedGaugeGraph' class='gauge-graph'>&nbsp;&nbsp;</div></br>
											<span id='consolidatedGaugeDqIndex' class='dq-index-date'>&nbsp;&nbsp;</span>
										</td>
									</tr>
								</table>
							</div>

							<div class='sub-graph-container'>

								<div class='sub-graph-child'>
									<div class='graph-group'>
										<table class='graph-table-layout'>
										<tr><th colspan="2" style='backgound-color: #e0ebf9;'>Customer</th></tr>
											<tr>
												<td style='height: 280px;' id='dataDomain1PreviousDqGraph'>&nbsp;&nbsp;</td>
												<td style='height: 280px;' id='dataDomain1CurrentDqGraph'>&nbsp;&nbsp;</td>

											</tr>
											<tr>
												<td colspan="2">
													<div id='dataDomain1GaugeGraph' class='gauge-graph'>&nbsp;&nbsp;</div></br>
													<span id='dataDomain1GaugeDqIndex' class='dq-index-date'>&nbsp;&nbsp;</span>
											</td>
											</tr>
										</table>
									</div>
								</div>
								
								<div class='sub-graph-child'>
									<div class='graph-group'>
										<table class='graph-table-layout'>
										<tr><th colspan="2" style='backgound-color: #e0ebf9;'>Product</th></tr>
											<tr>
												<th style='height: 280px;' id='dataDomain2PreviousDqGraph'>&nbsp;&nbsp;</th>
												<th style='height: 280px;' id='dataDomain2CurrentDqGraph'>&nbsp;&nbsp;</th>
											</tr>
											<tr>
												<td colspan="2">
													<div class='gauge-graph' id='dataDomain2GaugeGraph'>&nbsp;&nbsp;</div></br>
													<span id='dataDomain2GaugeDqIndex' class='dq-index-date'>&nbsp;&nbsp;</span>
												</td>
											</tr>
										</table>
									</div>
								</div>

								<div class='sub-graph-child'>
									<div class='graph-group'>
										<table class='graph-table-layout'>
										<tr><th colspan="2" style='backgound-color: #e0ebf9;'>Transaction</th></tr>
											<tr>
												<th style='height: 280px;' id='dataDomain3PreviousDqGraph'>&nbsp;&nbsp;</th>
												<th style='height: 280px;' id='dataDomain3CurrentDqGraph'>&nbsp;&nbsp;</th>
											</tr>
											<tr>
												<td colspan="2">
													<div id='dataDomain3GaugeGraph' class='gauge-graph'>&nbsp;&nbsp;</div></br>
													<span id='dataDomain3GaugeDqIndex' class='dq-index-date'>&nbsp;&nbsp;</span>													
												</td>
											</tr>
										</table>
									</div>
								</div>
								
								<canvas style='display: none; border: 2px solid blue;' id='workingCanvas'></canvas>
							<div>

						</div>
						
					</div>					
				</div>				
			</div>			
		</div>
	</div>
</div>

<div style='display: none' >
	<form id="downloadExecReportForm" method="post">	
		<input type="hidden" id="sProjectIds" name="sProjectIds"> 
		<input type="hidden" id="sSelectedDateRange" name="sSelectedDateRange"> 
		<input type="hidden" id="sSelectedFormat" name="sSelectedFormat"> 
		<input type="hidden" id="sPngRandomToken" name="sPngRandomToken"> 
	</form>
</div>

<jsp:include page="footer.jsp" />
<script type="text/javascript" src="./assets/global/plugins/loader.js"></script>
<script src="./assets/global/plugins/jquery.dataTables.min.js" type="text/javascript"></script>
<script src="./assets/global/plugins/bootstrap-multiselect.js"></script>
<link rel="stylesheet" href="./assets/global/plugins/bootstrap-multiselect.css">

<head>
	<link rel="stylesheet" href="./assets/jwf/content/JwfSpaInfra.css">
	<script src="./assets/jwf/scripts/JwfSpaInfra.js"></script>
</head>

<script type="text/javascript">
	var ManageDQSummary = {
		NoDataGraphic: ''
			+ '<svg height="30" width="200">'
			+ '<text x="0" y="15" fill="red" font-family="Arial" font-size="15" font-weight="bold">No Data Found</text>'
			+ '</svg>',		
		SelectedInputs: { SelectedProjects: "", SelectedDateRange: "-1", SelectedDownloadFormat: "-1", PngRandomToken: '' },		
		GraphSetCongiguration: [
			{ allGraphsData: [], currGraphElement: $('#consolidatedCurrentDqGraph')[0], prevGraphElement: $('#consolidatedPreviousDqGraph')[0], gaugeElement: $('#consolidatedGaugeGraph')[0] },
			{ allGraphsData: [], currGraphElement: $('#dataDomain1CurrentDqGraph')[0], prevGraphElement: $('#dataDomain1PreviousDqGraph')[0], gaugeElement: $('#dataDomain1GaugeGraph')[0] },
			{ allGraphsData: [], currGraphElement: $('#dataDomain2CurrentDqGraph')[0], prevGraphElement: $('#dataDomain2PreviousDqGraph')[0], gaugeElement: $('#dataDomain2GaugeGraph')[0] },
			{ allGraphsData: [], currGraphElement: $('#dataDomain3CurrentDqGraph')[0], prevGraphElement: $('#dataDomain3PreviousDqGraph')[0], gaugeElement: $('#dataDomain3GaugeGraph')[0] }					
		],
		GraphPngConfiguration: [
			{ SvgContainer: 'consolidatedCurrentDqGraph', PngFile: 'Consolidated_Current_Line_Graph.png', IsSentDone: false }, 
			{ SvgContainer: 'consolidatedPreviousDqGraph', PngFile: 'Consolidated_Previous_Line_Graph.png', IsSentDone: false },
			{ SvgContainer: 'consolidatedGaugeGraph', PngFile: 'Consolidated_Gauge_Graph.png', IsSentDone: false },
			{ SvgContainer: 'dataDomain1CurrentDqGraph', PngFile: 'Customer_Current_Line_Graph.png', IsSentDone: false },
			{ SvgContainer: 'dataDomain1PreviousDqGraph', PngFile: 'Customer_Previous_Line_Graph.png', IsSentDone: false },
			{ SvgContainer: 'dataDomain1GaugeGraph', PngFile: 'Customer_Gauge_Graph.png', IsSentDone: false },
			{ SvgContainer: 'dataDomain2CurrentDqGraph', PngFile: 'Product_Current_Line_Graph.png', IsSentDone: false },
			{ SvgContainer: 'dataDomain3PreviousDqGraph', PngFile: 'Product_Previous_Line_Graph.png', IsSentDone: false },
			{ SvgContainer: 'dataDomain2GaugeGraph', PngFile: 'Product_Gauge_Graph.png', IsSentDone: false },
			{ SvgContainer: 'dataDomain3CurrentDqGraph', PngFile: 'Transaction_Current_Line_Graph.png', IsSentDone: false },
			{ SvgContainer: 'dataDomain3PreviousDqGraph', PngFile: 'Transaction_Previous_Line_Graph.png', IsSentDone: false },
			{ SvgContainer: 'dataDomain3GaugeGraph', PngFile: 'Transaction_Gauge_Graph.png', IsSentDone: false }
		],
		DqResultData: {},
		setup: function() {
			$('#lstProject').on('change', ManageDQSummary.selectionChanged);
			$('#dateSelectionId').on('change', ManageDQSummary.selectionChanged);
			$('#lstDownloadFormat').on('change', ManageDQSummary.selectionChanged);
			
			$('#dateSelectionId').val('1');	
			$('#BtnDownloadDqReport').on('click',  ManageDQSummary.mainDownloadHandler);
			
		},
		mainDownloadHandler: function(oEvent) {
			var oRecursiveFunction = null, nSvgFile = 0, nPreventHangCtr = 40, oDownloadReportParms = ManageDQSummary.SelectedInputs;
			var lClinetSidePdf = (oDownloadReportParms.SelectedDownloadFormat === '2-PDF') ? true : false;
			
			
			ManageDQSummary.GraphPngConfiguration.forEach( function(oGraphPngCongiguration, nIndex) { 
				oGraphPngCongiguration.IsSentDone = false; 
			});			
			oDownloadReportParms.PngRandomToken = UTIL.getUniqueId();			

			if (lClinetSidePdf) {
				window.print();
				/*	Pradeep 24-06-2021 Server side PDF is disabled as it will take too long time */
				/* if (oDownloadReportParms.SelectedDownloadFormat === '2-PDF') {
						oRecursiveFunction = setInterval(sendSvgToPngFileToServer, 300);
						WaitForSeconds(6, 'Sending graphs to server to create PDF report. &nbsp; &nbsp; It may take few seconds');
				*/	
			} else {
				WaitForSeconds(3, 'Donwloading CSV report. &nbsp; &nbsp; It may take few seconds');
				submitDownloadRequest(oDownloadReportParms);
			}			

			function sendSvgToPngFileToServer() {
				var oDateTime = new Date(), oGraphPngCongiguration = null;
				
				if ((nPreventHangCtr < 1) || (nSvgFile > 11)) {
				
					clearInterval(oRecursiveFunction);
					submitDownloadRequest(oDownloadReportParms);					
				} else {
					--nPreventHangCtr;				
					
					oGraphPngCongiguration = ManageDQSummary.GraphPngConfiguration[nSvgFile];
					if (!oGraphPngCongiguration.IsSentDone) {
						sendGraphAsSvgToPngData(oGraphPngCongiguration);
					}
				}				
				PageCtrl.debug.log('sendSvgToPngFileToServer 01', oDateTime + ',' + nSvgFile + ',' + nPreventHangCtr);				
			}
			
			function sendGraphAsSvgToPngData(oGraphPngCongiguration) {				
				var oSvgContainer = $('#' + oGraphPngCongiguration.SvgContainer + ' svg')[0];
				var sSvgString = new XMLSerializer().serializeToString(oSvgContainer), oWorkingCanvas = $('#workingCanvas')[0];

				var oGraphCtx = oWorkingCanvas.getContext("2d"), oDOMURL = self.URL || self.webkitURL || self, oImg = new Image();
				var oSvg = new Blob([sSvgString], {type: "image/svg+xml;charset=utf-8"});
				var oUrl = oDOMURL.createObjectURL(oSvg), sPngContent = '';

				oWorkingCanvas.width = oSvgContainer.getBoundingClientRect().width;
				oWorkingCanvas.height = oSvgContainer.getBoundingClientRect().height;

				oImg.onload = function() {
					 oGraphCtx.drawImage(oImg, 0, 0);
					 sPngContent = oWorkingCanvas.toDataURL("image/png");
					 oDOMURL.revokeObjectURL(sPngContent);
					 saveSvgAsPngToServer(sPngContent);
				};
				oImg.src = oUrl;

				function saveSvgAsPngToServer(sPngContent) {
					JwfAjaxWrapper({
						WaitMsg: "",
						Url: 'saveSvgAsPngToServer',
						Headers:$("#token").val(),
						Data: { PngData:  JSON.stringify( { PngFile: oDownloadReportParms.PngRandomToken + '_' + oGraphPngCongiguration.PngFile, PngContent: sPngContent } ) },
						CallBackFunction: function(oResponse) {
							PageCtrl.debug.log('sendPngToServer 01', oResponse);
							oGraphPngCongiguration.IsSentDone = true;
							++nSvgFile;
						}
					});
				}
			}
			
			function submitDownloadRequest(oDownloadReportParms) {
				var oCustomForm = $("#downloadExecReportForm");				

				$('#sProjectIds').val(oDownloadReportParms.SelectedProjects);
				$('#sSelectedDateRange').val(oDownloadReportParms.SelectedDateRange);
				$('#sSelectedFormat').val(oDownloadReportParms.SelectedDownloadFormat);
				$('#sPngRandomToken').val(oDownloadReportParms.PngRandomToken);

				$(oCustomForm).attr('action', 'DownloadDqReportAsCsv');
				oCustomForm.submit();
			}			
			
		},
		selectionChanged: function(oEvent, nContext) {
			var aSelectedProjectIds = [], sSelectedProjectIds = "", nSelectedDateRange = $("#dateSelectionId").val(), sTargetId = oEvent.target.id, lProperInputs = false;
		
			$('#lstProject :selected').each(function (nIndex, oSelectedOption) {
				aSelectedProjectIds.push( $(oSelectedOption).val() );
			});
			sSelectedProjectIds = aSelectedProjectIds.join(',');
			
			ManageDQSummary.SelectedInputs.SelectedProjects = sSelectedProjectIds;
			ManageDQSummary.SelectedInputs.SelectedDateRange = nSelectedDateRange;
			ManageDQSummary.SelectedInputs.SelectedDownloadFormat = $('#lstDownloadFormat').val();
			
			PageCtrl.debug.log('selectionChanged 01', ManageDQSummary.SelectedInputs);
			
			lProperInputs = ( (ManageDQSummary.SelectedInputs.SelectedProjects.length < 1) || (ManageDQSummary.SelectedInputs.SelectedDateRange === '-1') ) ? false : true;			
		
			if ( (lProperInputs) && (sTargetId !== 'lstDownloadFormat') ) { 			
				ManageDQSummary.getDQResultsData(); 
			} else if (sTargetId === 'lstDownloadFormat') {
				ManageDQSummary.mainDownloadHandler(null);
			}
		},
		getDQResultsData: function(sContext, DQResultsData) {
			var sSelectedPorjects = ManageDQSummary.SelectedInputs.SelectedProjects;		
			
			PageCtrl.debug.log('getDQResultsData', ManageDQSummary.SelectedInputs);
			
			if (sSelectedPorjects.length < 1) {
				toastr.info('Please select at least select one project');
			} else {
				JwfAjaxWrapper({
					WaitMsg: "Wait Reloading Executive summary",
					Url: 'mainDqResultsHandler',
					Headers:$("#token").val(),
					Data: { sSelectedInputs: JSON.stringify(ManageDQSummary.SelectedInputs) },
					CallBackFunction: function(oResponse) {
						if (oResponse.Status) {
							toastr.info(oResponse.Msg);
							ManageDQSummary.DqResultData = oResponse.DqResultData;
							ManageDQSummary.loadDQResultsView();
							//PageCtrl.debug.log('getDQResultsData', oResponse);
						} else {					
							toastr.info(oResponse.Msg);
						}
					}
				});
			}				
		},		
		loadDQResultsView: function() {
			var aGraphSpecs = ManageDQSummary.GraphSetCongiguration;
	
				google.charts.load('current', {'packages':['gauge']});
				google.charts.load('current', {'packages':['corechart']});				
				
				aGraphSpecs.forEach( function(oGraphSpec, nIndex) {
					oGraphSpec.allGraphsData = ManageDQSummary.DqResultData.dataDomainGraphsData[nIndex];
					ManageDQSummary.loadGraphSet(oGraphSpec);
				});		
		},
		loadGraphSet: function(oGraphSpec) {
			var sGaugeData = oGraphSpec.allGraphsData.aggregateDqIndex, dDqIndexValue = parseFloat(sGaugeData.split(',')[0]), sDateValue = sGaugeData.split(',')[1];
			
			ManageDQSummary.drawGaugeChart(dDqIndexValue, sDateValue, oGraphSpec.gaugeElement);
			ManageDQSummary.drawLineChart(oGraphSpec.allGraphsData.currentPeriodGraphData, oGraphSpec.currGraphElement, 0);
			ManageDQSummary.drawLineChart(oGraphSpec.allGraphsData.previousPeriodGraphData, oGraphSpec.prevGraphElement, 1);
		},
		drawGaugeChart: function(dDqIndexValue, sDateValue, oContainerElement) {			
			var sFooterText = "Data Quality Index for '{0}'".replace('{0}',sDateValue), sSpanElementId = oContainerElement.id.replace('Graph', 'DqIndex');
			
			sFooterText = (sDateValue === '0000-00-00') ? 'No Data Found' : sFooterText;
			
			$('#' + sSpanElementId).html(sFooterText);
			google.charts.setOnLoadCallback(drawGaugeChart);

			function drawGaugeChart() {						
				var oDqIndexData = google.visualization.arrayToDataTable([
					['Label', 'Value'],
					['DQ Index', dDqIndexValue]
				]);

				var oDqIndexOptions = {
					width: 430, height: 130,
					redFrom: 0, redTo: 40,
					yellowFrom:40, yellowTo: 60,
					greenFrom: 60, greenTo: 100,
					minorTicks: 5,
					chartArea:{
							backgroundColor: '#fcfcfc'
					},					
					chart: {
						title: 'Company Performance',
						subtitle: 'Sales, Expenses, and Profit: May-August'
					} 					 
				};  
				
				var oGaugeChart = new google.visualization.Gauge(oContainerElement);			
				oGaugeChart.draw(oDqIndexData, oDqIndexOptions);							
			}				
		},
		drawLineChart: function(oLineGraphValues, oContainerElement, nColorCode) {
			if (Object.keys(oLineGraphValues).length < 1) {
				$(oContainerElement).html(ManageDQSummary.NoDataGraphic);
			} else {	
				google.charts.setOnLoadCallback(drawLineChart);
			}
			
			function drawLineChart() {
				var aDateValues = Object.keys(oLineGraphValues), aLineGraphData = [ ['Date', 'DQ Index'] ], 
					sLineColor = (nColorCode > 0) ? 'red' : 'blue', sTitle = (nColorCode > 0) ? 'Previous' : 'Current';
					
				sTitle = '     ' + sTitle + 'Aggregated DQI';				

				aDateValues.sort( function(sDate1, sDate2) {
					var nDate1 = parseInt(UTIL.replaceAll(sDate1, '-', '')), nDate2 = parseInt(UTIL.replaceAll(sDate2, '-', ''));					
					return nDate1 - nDate2;
				});				

				aDateValues.forEach( function(sDateValue, nIndex) {
					var aLineGraphRow = [];

					aLineGraphRow.push( sDateValue );
					aLineGraphRow.push( oLineGraphValues[sDateValue] );

					aLineGraphData.push(aLineGraphRow);					
				});				
				
				var oLineData = google.visualization.arrayToDataTable(aLineGraphData);				

				var oLineGraphOptions = {
					title: sTitle,
					curveType: 'function',
					legend: { position: 'right' },
					series: { 0: {color: sLineColor } },
					vAxis: {
						title: 'DQ Index',
						gridlines: false,
						viewWindowMode: 'explicit',
						viewWindow: {
							max: 105,
							min: 0
						}
					}
				};

				var oLineChart = new google.visualization.LineChart(oContainerElement);
				oLineChart.draw(oLineData, oLineGraphOptions);
			}			
		}		
	}	
	$(document).ready(function () {		
		initializeJwfSpaInfra();

		$('#lstProject').multiselect({
			maxHeight: 200,
			buttonWidth: '400px',
			includeSelectAllOption: true,
			enableFiltering: true
		});		
	
		$('#lstDownloadFormat').multiselect({
			maxHeight: 200,
			buttonWidth: '200px',
			includeSelectAllOption: true,
			enableFiltering: false
		});		
		ManageDQSummary.setup();		
	});
	
  function WaitForSeconds(nSeconds, sWaitMsg) {
		var nWaitCounter = (nSeconds * 3), oWaitFunction = setInterval(testWait, 300);

		Modal.waitDialog(sWaitMsg);

		function testWait() {
			var oDateTime = new Date();

			--nWaitCounter;
			PageCtrl.debug.log('WaitForSeconds 01', oDateTime + ',' + nWaitCounter);

			if (nWaitCounter < 1) {
				Modal.close();
				clearInterval(oWaitFunction);
			}
		}
	}
</script>


