
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<jsp:include page="header.jsp" />
<jsp:include page="checkVulnerability.jsp" />
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
							<span class="caption-subject bold ">Statistical Matching
								Checks Dashboard (<a onClick="validateHrefVulnerability(this)"  href="downloadCsv?tableName=sm_dashboard">Download
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
									<th>RC Status</th>
									<th>Measurement Sum Status</th>
									<th>Measurement Mean Status</th>
									<th>Measurement Std Dev Status</th>
								</tr>
							</thead>
							<tbody>
								<%
									System.out.print(request.getAttribute("dashboardTable"));
								%>
								<c:forEach var="dashboardTableObj" items="${dashboardTable}">
									<tr>
										<td>${dashboardTableObj.idApp}</td>
										<td style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">${dashboardTableObj.date}</td>
										<td>${dashboardTableObj.run}</td>
										<td><a onClick="validateHrefVulnerability(this)"  href="showStatisticalMatchingData?idApp=${dashboardTableObj.idApp}" class="nav-link">${dashboardTableObj.validationCheckName}</a></td>
										
										<td>${dashboardTableObj.source1}</td>
										<td>${dashboardTableObj.source2 }</td>
										<td><c:if
												test="${dashboardTableObj.rcStatus eq 'passed'}">
												<span class="label label-success label-sm">${dashboardTableObj.rcStatus}</span>
											</c:if> <c:if
												test="${dashboardTableObj.rcStatus eq 'failed'}">
												<span class="label label-danger label-sm">${dashboardTableObj.rcStatus}</span>
											</c:if>
											<c:if
												test="${dashboardTableObj.rcStatus eq 'NA'}">
												${dashboardTableObj.rcStatus}
											</c:if></td>
										<td><c:if
												test="${dashboardTableObj.sumStatus eq 'passed'}">
												<span class="label label-success label-sm">${dashboardTableObj.sumStatus}</span>
											</c:if> <c:if
												test="${dashboardTableObj.sumStatus eq 'failed'}">
												<span class="label label-danger label-sm">${dashboardTableObj.sumStatus}</span>
											</c:if>
											<c:if
												test="${dashboardTableObj.sumStatus eq 'NA'}">
												${dashboardTableObj.sumStatus}
											</c:if></td>
											<td><c:if
												test="${dashboardTableObj.meanStatus eq 'passed'}">
												<span class="label label-success label-sm">${dashboardTableObj.meanStatus}</span>
											</c:if> <c:if
												test="${dashboardTableObj.meanStatus eq 'failed'}">
												<span class="label label-danger label-sm">${dashboardTableObj.meanStatus}</span>
											</c:if>
											<c:if
												test="${dashboardTableObj.meanStatus eq 'NA'}">
												${dashboardTableObj.meanStatus}
											</c:if></td>
											<td><c:if
												test="${dashboardTableObj.stdDevStatus eq 'passed'}">
												<span class="label label-success label-sm">${dashboardTableObj.stdDevStatus}</span>
											</c:if> <c:if
												test="${dashboardTableObj.stdDevStatus eq 'failed'}">
												<span class="label label-danger label-sm">${dashboardTableObj.stdDevStatus}</span>
											</c:if>
											<c:if
												test="${dashboardTableObj.stdDevStatus eq 'NA'}">
												${dashboardTableObj.stdDevStatus}
											</c:if></td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				
					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold "> Select a validation check to view results: </span>
						</div>
					</div>
					<div class="portlet-body form">
						<form method="GET" action="showStatisticalMatchingData">
							<div class="form-body">
								<div class="row">

									<div class="col-md-6 col-capture non-schema-matching">
										<div class="form-group form-md-line-input">
											<select class="form-control" id="idApp" name="idApp">
											<option value="-1">Choose a Statistical Matching Application*</option>
												<c:forEach items="${resultmasterdata}" var="resultmasterdata">
													<option value=${resultmasterdata.key}>${resultmasterdata.value}</option>
												</c:forEach>
											</select> <label for="form_control_1">Statistical Matching Application*</label>
											<!-- <span class="help-block">Data Location  </span> -->
										</div>
										<br /> <span class="required"></span>
									</div>
								</div>

								<div class="form-actions noborder">
									<button type="submit" id="qualitybuttonid" class="btn blue">Submit</button>
								</div>
								<br> <br></div>
						</form>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
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
$('#qualitybuttonid').click(function() {
			var no_error = 1;
			$('.input_error').remove();
			 if ($("#idApp").val() == -1) {
					$('<span class="input_error" style="font-size:12px;color:red">Please Choose a Statistical Matching Application</span>')
							.insertAfter($('#idApp'));
					no_error = 0;
				} 
			 if (no_error==0) {
				 return false;
			 }
		});

</script>