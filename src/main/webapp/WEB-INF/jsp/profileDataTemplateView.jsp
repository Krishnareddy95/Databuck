<%@page import="com.databuck.service.RBACController" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<link rel="stylesheet" href="./assets/css/custom-style.css" type="text/css" />

<style>
	.dataTables_scrollBody {
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
									<span class="caption-subject bold "> Profiling </span>
								</div>
							</div>
							
							<!-- Project Date filter - START -->
							<div class="portlet-body">
								 <div class="row">
								 	<div class="col-md-3 col-capture form-group form-md-line-input">
								    	<select class="form-control multiselect text-left"
											style="text-align: left;" id="lstProject" name="listProject">
												<option value="${selectedProject.idProject}" selected>${selectedProject.projectName}</option>
											<c:forEach items="${projectList}" var="projectListdata">
												<option value="${projectListdata.idProject}">${projectListdata.projectName}</option>
											</c:forEach>
										</select> 
										<input type="hidden" id="selectedProject" name="selectedProject" value="" /> 
										<label for="form_control_1" id="host-id" style="padding-left: 20px;color: black;font-weight: bold;" >Project Name*</label>
									</div>
									
									<div class="col-md-2 col-capture form-group form-md-line-input">
										<input class="form-control toDate" id="date" name="date" 
													placeholder="From Date" type="text" value="${toDate}" />
										<label for="form_control_1" id="fromDate" style="padding-left: 20px;color: black;font-weight: bold;">From Date</label>
									</div>
									
									<div class="col-md-2 col-capture form-group form-md-line-input">
										<input class="form-control fromDate" id="date" name="date" 
												placeholder="To date" type="text" value="${fromDate}" />
										<label for="form_control_1" id="toDate" style="padding-left: 20px;color: black;font-weight: bold;">To Date</label>
									</div>
									
									<div class="col-md-2 col-capture">
										<div class="form-group form-md-line-input">
											<input type="button" id="filterDate" class="btn blue" value="Search" style="height: 38px; padding-top: 12px;">
										</div>
									</div>
							    </div>
							</div>
							<br/>
							<!-- Project Date filter - END -->
					
							<div class="portlet-body">
								<table id="profilingResultTable"
									class="table table-striped table-bordered table-hover dataTable no-footer"
									style="width: 100%;">
									<thead>
										<tr>
											<th>Template Id</th>
											<th
												style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">
												Created Date</th>
											<th>Name</th>
											<th>Data Connection Name</th>
											<th>Location</th>
											<th>Project Name</th>
											<th>Table Name</th>
											<th>Template Status</th>
										</tr>
									</thead>
									<tbody>

										<c:forEach var="listdatasource" items="${listdatasource}">
											<tr>
												<td>${listdatasource.idData}<input type="hidden" id="tIdData"
														value="${listdatasource.idData}">

												</td>
												<td
													style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">
													${listdatasource.createdAt}</td>
												<td><a onClick="validateHrefVulnerability(this)"  href="dataProfiling_View?idData=${listdatasource.idData}"
														class="nav-link"> <span
															class="title">${listdatasource.name}</span></a></td>

												<td>${listdatasource.schemaName}</td>
												<td>${listdatasource.dataLocation}</td>
												<td>${listdatasource.projectName}</td>
												<td>${listdatasource.tableName}</td>
												<td>

													<c:choose>
														<c:when test="${listdatasource.templateCreateSuccess == 'Y'}">
															<span
																class='label label-success label-sm'>${listdatasource.templateCreateSuccess}</span>
														</c:when>
														<c:when test="${listdatasource.templateCreateSuccess == 'N'}">
															<span
																class='label label-danger label-sm'>${listdatasource.templateCreateSuccess}</span>
														</c:when>
														<c:otherwise>
															${listdatasource.templateCreateSuccess}
														</c:otherwise>
													</c:choose>
												</td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
							</div>
						</div>
						<!--       *************END EXAMPLE TABLE PORTLET*************************-->

					</div>
				</div>
			</div>
		</div>
		<script src="./assets/global/plugins/jquery.min.js" type="text/javascript">

		</script>
		<script src="./assets/global/plugins/jquery.dataTables.min.js" type="text/javascript">

		</script>

		<script>
			$(document).ready(function () {
				var table;
				table = $('#profilingResultTable').dataTable({

					order: [[0, "desc"]],
					"scrollX": true,
					"aoColumns": [
						{ "sWidth": "200" },
						{ "sWidth": "200" },
						{ "sWidth": "100" },
						{ "sWidth": "400" },
						{ "sWidth": "100" },
						{ "sWidth": "100" },
						{ "sWidth": "100" },
						{ "sWidth": "100" }
					]
				});
			});
		</script>
		<script>
			$(document).ready(function(){
				var date_input=$('input[name="date"]'); //our date input has the name "date"
				var container=$('.bootstrap-iso form').length>0 ? $('.bootstrap-iso form').parent() : "body";
				date_input.datepicker({
					format: 'yyyy-mm-dd',
					container: container,
					todayHighlight: true,
					autoclose: true,
					endDate: "today"
				})
			});
		</script>
		<script type="text/javascript">
			var code = {};
			$("select[name='listProject'] > option").each(function () {
				if (code[this.text]) {
					$(this).remove();
				} else {
					code[this.text] = this.value;
				}
			});
		</script>
		<script>	
			   $("#filterDate").click(function () {	       		
			       	var selectednumbers = [];
					$('#lstProject :selected').each(function (i, selected) {
						selectednumbers[i] = $(selected).val();
					});
					var projectid = selectednumbers.join();
			       	var form_data = {
			    		   toDate : $(".toDate").val(),
		                   fromDate: $(".fromDate").val(),
		                   projectid: projectid,
		           	};
		           	$.ajax({
		               url : './dateAndProjectFilter',
		               type : 'GET',
		               
		               datatype : 'json',
		               data : form_data,
		               success : function(message) {
		            	
		            	   window.location= window.location.href;
		               }
		           }); 
			   });
		
		</script>
		<!--********     BEGIN CONTENT ********************-->

		<jsp:include page="footer.jsp" />