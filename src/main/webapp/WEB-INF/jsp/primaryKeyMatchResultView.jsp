<%@page import="com.databuck.bean.KeyMeasurementMatchingDashboard"%>
<%@page import="com.databuck.bean.DataQualityMasterDashboard"%>
<%@page import="java.util.List"%>
<%@page import="java.text.DecimalFormat"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<style>
	table.dataTable tbody th,
	table.dataTable tbody td {
		white-space: nowrap;
	}

	.dataTables_scrollBody{
            overflow-y: hidden !important;
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
			<div class="col-md-12">
				<!-- BEGIN EXAMPLE TABLE PORTLET-->
				<div class="portlet light bordered">
					<div class="portlet-title">

						<div class="caption font-red-sunglo">
							<span class="caption-subject bold ">Primary Key Matching Validation Check Dashboard (<a onClick="validateHrefVulnerability(this)"  href="downloadCsv?tableName=pkm_dashboard">Download
									csv</a>)
							</span>
						</div>
					</div>
					<div class="portlet-body">
						<table
							class="table table-striped table-bordered table-hover dataTable no-footer"
							style="width: 100%;">
							<thead>
								<tr>
									<th>IdApp</th>
									<th style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">Date</th>
									<th>Run</th>
									<th>Validation Check Name</th>
									<th>Source1 Name</th>
									<th>Source2 Name</th>
									<th style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">Source1 Count</th>
									<th style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">Source1 Only Records</th>
									<th style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">Source2 Count</th>
									<th style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">Source2 Only Records</th>
									<th style="max-width: 85px !important;width: 85px !important;min-width: 85px !important">UnMatched Records</th>
									<th style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">Source1 Status</th>
									<th style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">Source2 Status</th>
									<th style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">UnMatched Status</th>
									<!-- <th>UnMatched Status</th> -->

								</tr>
							</thead>
							<tbody>
								<%
									System.out.print(request.getAttribute("dashboardTable"));
									DecimalFormat df = new DecimalFormat(",###");
									
									 //String source1Count=null; 
									/* String onlySrcCnt1=null;
									String source2Count=null;
									String onlySrcCnt2=null;
									String unmatchRec = null;  */
									
									
								%>
								<c:forEach var="dashboardTableObj" items="${dashboardTable}">
									<tr>
										<td>${dashboardTableObj.idApp}</td>
										<td style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">${dashboardTableObj.date}</td>
										<td>${dashboardTableObj.run}</td>
										<td><a onClick="validateHrefVulnerability(this)"  href="getPrimaryKeyMatchTablesData?appId=${dashboardTableObj.idApp}&source1=${dashboardTableObj.source1}&source2=${dashboardTableObj.source2}" class="nav-link">${dashboardTableObj.validationCheckName}</a></td>
										<td>${dashboardTableObj.source1}</td>
										<td>${dashboardTableObj.source2}</td>
										
										<%--  <%
												
									 List<KeyMeasurementMatchingDashboard> dataQualityMaster = (List)request.getAttribute("dashboardTable");
											for(KeyMeasurementMatchingDashboard d : dataQualityMaster){
											//source1Count = d.getSource1Count().toString();
											onlySrcCnt1 = d.getSource1Records().toString();
											source2Count = d.getSource2Count().toString();
											onlySrcCnt2 = d.getSource2Records().toString();
											unmatchRec = d.getUnmatchedRecords().toString(); 
										}
										%> --%>  
										<td style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">${dashboardTableObj.source1Count}</td>
										<td style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">${dashboardTableObj.source1Records}</td>
										<td style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">${dashboardTableObj.source2Count}</td>
										<td style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">${dashboardTableObj.source2Records}</td>
										<td style="max-width: 85px !important;width: 85px !important;min-width: 85px !important">${dashboardTableObj.unmatchedRecords}</td>
									
										<%--  <td>${dashboardTableObj.source1Count}</td> --%>
										<%-- <td>${dashboardTableObj.source1Records }</td> --%>
										
									
										
									 	<%-- <td><%=df.format(Double.parseDouble(source1Count)) %></td>   --%>
										<%-- <td><%=df.format(Double.parseDouble(onlySrcCnt1)) %></td> --%>
										<td style="max-width: 75px !important;width: 75px !important;min-width: 75px !important"><c:if
												test="${dashboardTableObj.source1OnlyStatus eq 'passed'}">
												<span class="label label-success label-sm">${dashboardTableObj.source1OnlyStatus}</span>
											</c:if> <c:if
												test="${dashboardTableObj.source1OnlyStatus eq 'failed'}">
												<span class="label label-danger label-sm">${dashboardTableObj.source1OnlyStatus}</span>
											</c:if></td>
									<%-- 	<td>${dashboardTableObj.source2Count }</td>
										<td>${dashboardTableObj.source2Records }</td> --%>
										
										
										<%-- <td><%=df.format(Double.parseDouble(source2Count)) %></td>
										<td><%=df.format(Double.parseDouble(onlySrcCnt2)) %></td> --%>
										
										<td style="max-width: 75px !important;width: 75px !important;min-width: 75px !important"><c:if
												test="${dashboardTableObj.source2OnlyStatus eq 'passed'}">
												<span class="label label-success label-sm">${dashboardTableObj.source2OnlyStatus}</span>
											</c:if> <c:if
												test="${dashboardTableObj.source2OnlyStatus eq 'failed'}">
												<span class="label label-danger label-sm">${dashboardTableObj.source2OnlyStatus}</span>
											</c:if></td>


<%-- 										<td><c:if
												test="${dashboardTableObj.unmatchedStatus eq 'passed'}">
												<span class="label label-success label-sm">${dashboardTableObj.unmatchedStatus}</span>
											</c:if> <c:if
												test="${dashboardTableObj.unmatchedStatus eq 'failed'}">
												<span class="label label-danger label-sm">${dashboardTableObj.unmatchedStatus}</span>
											</c:if> <c:if test="${dashboardTableObj.unmatchedStatus eq 'NA'}">
											<span class="label label-warning label-sm">NA</span>
														${dashboardTableObj.unmatchedStatus}
													</c:if></td>
													
 --%>						
                                <td style="max-width: 75px !important;width: 75px !important;min-width: 75px !important"><c:if
												test="${dashboardTableObj.source2OnlyStatus eq 'passed'}">
												<span class="label label-success label-sm">${dashboardTableObj.unmatchedStatus}</span>
											</c:if> <c:if
												test="${dashboardTableObj.source2OnlyStatus eq 'failed'}">
												<span class="label label-danger label-sm">${dashboardTableObj.unmatchedStatus}</span>
											</c:if></td>
                                		
                             	</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>

					<div class="portlet-title" hidden="true">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold ">Select a validation
								check to view results:</span>
						</div>
					</div>
					<form action="getPrimaryKeyMatchTablesData" method="GET" hidden="true">
						<div class="portlet-body">
							<input type="hidden" id="tourl" value="getMatchingTablesData">
							<div class="row">
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<select class="form-control" name="appId" id="myselect">
											<option value="-1">Choose a Matching Application*</option>
											<c:forEach var="entry" items="${matchingResultTableData}">
												<option value="${entry.key}">${entry.value }</option>
											</c:forEach>
										</select> <label for="form_control_1">Matching Application*</label>
									</div>
									<br /> <span class="required"></span>
								</div>

							</div>
							<br /> <br />
							<div class="form-actions noborder">
								&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp
								<button type="submit" class="btn blue" id="validationId">Submit</button>
								<!-- <button type="submit" class="btn blue">Submit</button> -->
							</div>
						</div>
					</form>
				</div>
			</div>

			<!-- END EXAMPLE TABLE PORTLET-->
		</div>
	</div>
</div>
<!-- close if -->
<!-- close if -->
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
<script>
	$('#validationId')
			.click(
					function() {
						var no_error = 1;
						$('.input_error').remove();
						if ($("#myselect").val() == -1) {
							$(
									'<span class="input_error" style="font-size:12px;color:red">Please Choose a Matching Application</span>')
									.insertAfter($('#myselect'));
							no_error = 0;
						}
						if (no_error == 0) {
							return false;
						}
					});
</script>