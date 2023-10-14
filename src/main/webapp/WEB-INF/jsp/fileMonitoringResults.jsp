<%@page import="java.util.List"%>
<%@page import="com.databuck.bean.FileTrackingSummary"%>
<%@page import="java.time.DayOfWeek"%>
<%@page import="com.databuck.bean.FileMonitorRules"%>
<%@page import="com.databuck.service.RBACController"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.databuck.service.RBACController"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<jsp:include page="header.jsp" />

<jsp:include page="container.jsp" />
<style>
	table.dataTable tbody th,
	table.dataTable tbody td {
		white-space: nowrap;
	}

	.dataTables_scrollBody{
            overflow-y: hidden !important;
   	 	}
</style>
<!--============= BEGIN CONTENT BODY============= -->


<!--*****************      BEGIN CONTENT **********************-->

<div class="page-content-wrapper">
	<!-- BEGIN CONTENT BODY -->
	<div class="page-content">
		<!-- BEGIN PAGE TITLE-->
		<!-- END PAGE TITLE-->
		<!-- END PAGE HEADER-->
		<div class="row">
			<div class="col-md-12">

				<!--****         BEGIN EXAMPLE TABLE PORTLET       **********-->

				<div class="portlet light bordered">
					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold ">Summary of
								File Monitoring Validation Check : ${appName} </span>
						</div>
					</div>
					<div class="portlet light bordered">
						<div class="portlet-title">
							<div class="caption font-red-sunglo">
								<span class="caption-subject bold ">File Tracking Summary</span>
							</div>
						</div>
						<div class="portlet-body">
							<table
								class="table table-striped table-bordered table-hover dataTable no-footer"
								style="width: 100%;">
								<thead>
									<tr>
										<th style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">Date</th>
										<th>Run</th>
										<th>Day</th>
										<th>Month</th>
										<th>Day_of_Month</th>
										<th>Day_of_Week</th>
										<th>Hour</th>
										<th>File_Monitor_Rules_Id</th>
										<th>File_Count</th>
										<th>File_Count_Status</th>
										<th>File_Size_Status</th>
										<th>Last_Updated_TimeStamp</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="fileTrackingSumm"
										items="${listFileTrackingSumm}">
										<tr>
											<td style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">${fileTrackingSumm.date}</td>
											<td>${fileTrackingSumm.run}</td>
											<td>${fileTrackingSumm.dayOfYear}</td>
											<td>${fileTrackingSumm.month}</td>
											<td>${fileTrackingSumm.dayOfMonth}</td>
											<td><c:choose>
													<c:when test="${fileTrackingSumm.dayOfWeek == 1}">${DayOfWeek.of(7)}</c:when>
													<c:otherwise>${DayOfWeek.of(fileTrackingSumm.dayOfWeek - 1)}</c:otherwise>
												</c:choose>
											</td>	
											<td>${fileTrackingSumm.hourOfDay}</td>
											<td>${fileTrackingSumm.fileMonitorRules.id}</td>
											<td>${fileTrackingSumm.fileCount}</td>
											<td><c:choose>
													<c:when test="${fileTrackingSumm.countStatus == 'passed'}">
														<span class='label label-success label-sm'>${fileTrackingSumm.countStatus}</span>
													</c:when>
													<c:when test="${fileTrackingSumm.countStatus == 'failed'}">
														<span class='label label-danger label-sm'>${fileTrackingSumm.countStatus}</span>
													</c:when>
													<c:otherwise>${fileTrackingSumm.countStatus}</c:otherwise>
												</c:choose></td>
											<td><c:choose>
													<c:when
														test="${fileTrackingSumm.fileSizeStatus == 'passed'}">
														<span class='label label-success label-sm'>${fileTrackingSumm.fileSizeStatus}</span>
													</c:when>
													<c:when
														test="${fileTrackingSumm.fileSizeStatus == 'failed'}">
														<span class='label label-danger label-sm'>${fileTrackingSumm.fileSizeStatus}</span>
													</c:when>
													<c:otherwise>${fileTrackingSumm.fileSizeStatus}</c:otherwise>
												</c:choose></td>
											<td>${fileTrackingSumm.lastUpdateTimeStamp}</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</div>
					</div>
					<div class="portlet light bordered">
						<div class="portlet-title">
							<div class="caption font-red-sunglo">
								<span class="caption-subject bold ">File Tracking Details</span>
							</div>
						</div>
						<div class="portlet-body">
							<table
								class="table table-striped table-bordered table-hover dataTable no-footer"
								style="width: 100%;">
								<thead>
									<tr>
										<th style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">Date</th>
										<th>Run</th>
										<th>Day</th>
										<th>Month</th>
										<th>Day_of_Month</th>
										<th>Day_of_Week</th>
										<th>Hour</th>
										<th>File_Monitor_Rules_Id</th>
										<th>Bucket_Name</th>
										<th>Folder_Path</th>
										<th>File_Name</th>
										<th>File_Arrival_Time</th>
										<th>File Format</th>
										<th>Status</th>
										<th>ZeroSizeFileCheck</th>
										<th>RecordLengthCheck</th>
										<th>RecordMaxLengthCheck</th>
										<th>ColumnCountCheck</th>
										<th>ColumnSequenceCheck</th>
										<th>FileExecution Status</th>
										<th>FileExecutionStatus_Message</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="listFileTrackingHistory"
										items="${listFileTrackingHistory}">
										<tr>
											<td style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">${listFileTrackingHistory.date}</td>
											<td>${listFileTrackingHistory.run}</td>
											<td>${listFileTrackingHistory.dayOfYear}</td>
											<td>${listFileTrackingHistory.month}</td>
											<td>${listFileTrackingHistory.dayOfMonth}</td>
											<td><c:choose>
													<c:when test="${listFileTrackingHistory.dayOfWeek == 1}">${DayOfWeek.of(7)}</c:when>
													<c:otherwise>${DayOfWeek.of(listFileTrackingHistory.dayOfWeek - 1)}</c:otherwise>
												</c:choose></td>
											<td>${listFileTrackingHistory.hourOfDay}</td>
											<td>${listFileTrackingHistory.fileMonitorRuleId}</td>
											<td>${listFileTrackingHistory.bucketName}</td>
											<td>${listFileTrackingHistory.folderPath}</td>
											<td>${listFileTrackingHistory.fileName}</td>
											<td>${listFileTrackingHistory.fileArrivalDate}</td>
											<td>${listFileTrackingHistory.fileFormat}</td>
											<td>	
												<c:if
													test="${listFileTrackingHistory.status eq 'passed'}">
													<span class="label label-success label-sm">${listFileTrackingHistory.status}</span>
												</c:if> 
												<c:if
													test="${listFileTrackingHistory.status eq 'failed'}">
													<span class="label label-danger label-sm">${listFileTrackingHistory.status}</span>
												</c:if>
											</td>
											<td>	
												<c:if
													test="${listFileTrackingHistory.zeroSizeFileCheck eq 'passed'}">
													<span class="label label-success label-sm">${listFileTrackingHistory.zeroSizeFileCheck}</span>
												</c:if> 
												<c:if
													test="${listFileTrackingHistory.zeroSizeFileCheck eq 'failed'}">
													<span class="label label-danger label-sm">${listFileTrackingHistory.zeroSizeFileCheck}</span>
												</c:if>
											</td>
											<td>	
												<c:if
													test="${listFileTrackingHistory.recordLengthCheck eq 'passed'}">
													<span class="label label-success label-sm">${listFileTrackingHistory.recordLengthCheck}</span>
												</c:if> 
												<c:if
													test="${listFileTrackingHistory.recordLengthCheck eq 'failed'}">
													<span class="label label-danger label-sm">${listFileTrackingHistory.recordLengthCheck}</span>
												</c:if>
											</td>
											<td>	
												<c:if
													test="${listFileTrackingHistory.recordMaxLengthCheck eq 'passed'}">
													<span class="label label-success label-sm">${listFileTrackingHistory.recordMaxLengthCheck}</span>
												</c:if> 
												<c:if
													test="${listFileTrackingHistory.recordMaxLengthCheck eq 'failed'}">
													<span class="label label-danger label-sm">${listFileTrackingHistory.recordMaxLengthCheck}</span>
												</c:if>
											</td>
											<td>	
												<c:if
													test="${listFileTrackingHistory.columnCountCheck eq 'passed'}">
													<span class="label label-success label-sm">${listFileTrackingHistory.columnCountCheck}</span>
												</c:if> 
												<c:if
													test="${listFileTrackingHistory.columnCountCheck eq 'failed'}">
													<span class="label label-danger label-sm">${listFileTrackingHistory.columnCountCheck}</span>
												</c:if>
											</td>
											<td>	
												<c:if
													test="${listFileTrackingHistory.columnSequenceCheck eq 'passed'}">
													<span class="label label-success label-sm">${listFileTrackingHistory.columnSequenceCheck}</span>
												</c:if> 
												<c:if
													test="${listFileTrackingHistory.columnSequenceCheck eq 'failed'}">
													<span class="label label-danger label-sm">${listFileTrackingHistory.columnSequenceCheck}</span>
												</c:if>
											</td>
											<td>${listFileTrackingHistory.fileExecutionStatus}</td>
											<td>${listFileTrackingHistory.fileExecutionStatusMsg}</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</div>
						<div class="portlet light bordered">
                                            <div class="portlet-title">
                                                <div class="caption font-red-sunglo">
                                                    <span class="caption-subject bold ">File Summary Details </span>
                                                </div>
                                            </div>
                                            <div class="portlet-body">
                                                <table
                                                    class="table table-striped table-bordered table-hover dataTable no-footer"
                                                    style="width: 100%;">
                                                    <thead>
                                                        <tr>
                                                            <th style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">Date</th>
                                                            <th>Connection_Name</th>
                                                            <th>Table_Name</th>
                                                            <th>File_Indicator</th>
                                                            <th>Day_Of_Week</th>
                                                            <th>Loaded_Hour</th>
                                                            <th>Expected_Minute</th>
                                                            <th>Actual_File_Count</th>
                                                            <th>Expected_File_Count</th>
                                                            <th>Status</th>

                                                        </tr>
                                                    </thead>
                                                    <tbody>
                                                        <c:forEach var="listFileFMSummaryDetails"
                                                           items="${listFileFMSummaryDetails}">
                                                           <tr>
                                                           <td style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">${listFileFMSummaryDetails.load_date}</td>
                                                               <td>${listFileFMSummaryDetails.connectionName}</td>
                                                               <td>${listFileFMSummaryDetails.table_or_subfolder_name}</td>
                                                               <td>${listFileFMSummaryDetails.file_indicator}</td>
                                                               <td>${listFileFMSummaryDetails.dayOfWeek}</td>
                                                               <td>${listFileFMSummaryDetails.loaded_hour}</td>
                                                               <td>${listFileFMSummaryDetails.expected_minute}</td>
                                                               <td>${listFileFMSummaryDetails.actual_file_count}</td>
                                                               <td>${listFileFMSummaryDetails.expected_file_count}</td>

                                                               <td>
                                                                   <c:if
                                                                       test="${listFileFMSummaryDetails.status eq 'passed' || listFileFMSummaryDetails.status eq 'ready'}">
                                                                       <span class="label label-success label-sm">${listFileFMSummaryDetails.status}</span>
                                                                   </c:if>
                                                                   <c:if
                                                                       test="${listFileFMSummaryDetails.status eq 'missing' || listFileFMSummaryDetails.status eq 'failed'}">
                                                                       <span class="label label-danger label-sm">${listFileFMSummaryDetails.status}</span>
                                                                   </c:if>
                                                                   <c:if
                                                                       test="${listFileFMSummaryDetails.status eq 'new file'}">
                                                                       <span class="label label-danger label-sm" style='background-color:blue;'>${listFileFMSummaryDetails.status}</span>
                                                                   </c:if>
                                                                    <c:if
                                                                      test="${listFileFMSummaryDetails.status eq 'additional'}">
                                                                      <span class="label label-danger label-sm" style='background-color:orange;'>${listFileFMSummaryDetails.status}</span>
                                                                  </c:if>
                                                               </td>
                                                             </tr>
                                                           </c:forEach>
                                                    </tbody>
                                                </table>
                                            </div>
                        </div>
							<div class="portlet light bordered">
                                <div class="portlet-title">
                                    <div class="caption font-red-sunglo">
                                        <span class="caption-subject bold ">File Arrival Details </span>
                                    </div>
                                </div>
                                <div class="portlet-body">
                                    <table
                                        class="table table-striped table-bordered table-hover dataTable no-footer"
                                        style="width: 100%;">
                                        <thead>
                                            <tr>
                                                <th style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">Date</th>
                                                <th>Connection_Name</th>
                                                <th>Table_Name</th>
                                                <th>File_Indicator</th>
                                                <th>Day_Of_Week</th>
                                                <th>Loaded_Time</th>
                                                <th>Expected_Time</th>
                                                <th>Volume</th>
                                                <th>Volume_Check</th>
                                                <th>Schema_Check</th>
                                                <th>Arrival_Status</th>
                                                <th>File_Status</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="listFileFMArrivalDetails"
                                               items="${listFileFMArrivalDetails}">
                                               <tr>
                                                 <td style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">${listFileFMArrivalDetails.load_date}</td>
                                                  <td>${listFileFMArrivalDetails.connectionName}</td>
                                                  <td>${listFileFMArrivalDetails.table_or_subfolder_name}</td>
                                                  <td>${listFileFMArrivalDetails.file_indicator}</td>
                                                  <td>${listFileFMArrivalDetails.dayOfWeek}</td>
                                                  <td>${listFileFMArrivalDetails.loaded_hour}:${listFileFMArrivalDetails.loaded_time}</td>
                                                  <td>${listFileFMArrivalDetails.expected_hour}:${listFileFMArrivalDetails.expected_time}</td>
                                                  <td>${listFileFMArrivalDetails.record_count}</td>
                                                   <td>
                                                      <c:if
                                                          test="${listFileFMArrivalDetails.record_count_check eq 'passed'}">
                                                          <span class="label label-success label-sm">${listFileFMArrivalDetails.record_count_check}</span>
                                                      </c:if>
                                                      <c:if
                                                          test="${listFileFMArrivalDetails.record_count_check eq 'failed'}">
                                                          <span class="label label-danger label-sm">${listFileFMArrivalDetails.record_count_check}</span>
                                                      </c:if>
                                                  </td>
                                                  <td>
                                                      <c:if
                                                          test="${listFileFMArrivalDetails.column_metadata_check eq 'passed'}">
                                                          <span class="label label-success label-sm">${listFileFMArrivalDetails.column_metadata_check}</span>
                                                      </c:if>
                                                      <c:if
                                                          test="${listFileFMArrivalDetails.column_metadata_check eq 'failed'}">
                                                          <span class="label label-danger label-sm">${listFileFMArrivalDetails.column_metadata_check}</span>
                                                      </c:if>
                                                  </td>
                                                 <td>
                                                      <c:if
                                                          test="${listFileFMArrivalDetails.file_arrival_status eq 'on time' || listFileFMArrivalDetails.file_arrival_status eq 'early'}">
                                                          <span class="label label-success label-sm">${listFileFMArrivalDetails.file_arrival_status}</span>
                                                      </c:if>
                                                      <c:if
                                                          test="${listFileFMArrivalDetails.file_arrival_status eq 'delayed'}">
                                                          <span class="label label-danger label-sm" style='background-color:orange;'>${listFileFMArrivalDetails.file_arrival_status}</span>
                                                      </c:if>
                                                      <c:if
                                                          test="${listFileFMArrivalDetails.file_arrival_status eq 'additional'}">
                                                          <span class="label label-danger label-sm" style='background-color:pink;'>${listFileFMArrivalDetails.file_arrival_status}</span>
                                                      </c:if>
                                                      <c:if
                                                          test="${listFileFMArrivalDetails.file_arrival_status eq 'new file'}">
                                                          <span class="label label-danger label-sm" style='background-color:blue;'>${listFileFMArrivalDetails.file_arrival_status}</span>
                                                      </c:if>
                                                  </td>
                                                  <td>
                                                   <c:if
                                                        test="${listFileFMArrivalDetails.file_validity_status eq 'passed'}">
                                                        <span class="label label-success label-sm">${listFileFMArrivalDetails.column_metadata_check}</span>
                                                    </c:if>
                                                    <c:if
                                                        test="${listFileFMArrivalDetails.file_validity_status eq 'failed'}">
                                                        <span class="label label-danger label-sm">${listFileFMArrivalDetails.column_metadata_check}</span>
                                                    </c:if>
                                                   </td>
                                               </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                           </div>

					</div>
				</div>
				<!--       *************END EXAMPLE TABLE PORTLET*************************-->

			</div>
		</div>
	</div>
</div>
<!--********     BEGIN CONTENT ********************-->
<script src="./assets/global/plugins/jquery.min.js"
		type="text/javascript">
</script>
<script src="./assets/global/plugins/jquery.dataTables.min.js"
		type="text/javascript">
</script>
<script>
$(document).ready(function() {
	var table;
	table = $('.table').dataTable({
		  	
		  	order: [[ 0, "desc" ]],
		  	"scrollX": true
	      });
});
</script>
<jsp:include page="footer.jsp" />
