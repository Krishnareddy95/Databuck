
<%@page import="com.databuck.service.RBACController"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<jsp:include page="header.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<jsp:include page="container.jsp" />

<style>
table.dataTable tbody th, table.dataTable tbody td {
	white-space: nowrap;
}

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
							<span class="caption-subject bold "> Data Templates with
								Auto Discovered Rules </span>
						</div>
					</div>
					<div class="portlet-body">
						<table
							class="table table-striped table-bordered table-hover dataTable no-footer"
							style="width: 100%;">
							<thead>
								<tr>
									<th>Template Id</th>
									<th
										style="max-width: 75px !important; width: 75px !important; min-width: 75px !important">Date</th>
									<th>Data Template Name</th>
									<th>Location</th>
									<th>Table Name</th>
									<th>Project Name</th>
									<th>Created By</th>
									<%
									boolean flag = false;
									flag = RBACController.rbac("Data Template", "R", session);
									%>
									<th>Template Status</th>
								</tr>
							</thead>
							<tbody>

								<c:forEach var="listdatasource" items="${listdatasource}">
									<tr>
										<td>${listdatasource.idData}<input type="hidden"
											id="tIdData" value="${listdatasource.idData}">
										</td>
										<td
											style="max-width: 75px !important; width: 75px !important; min-width: 75px !important">${listdatasource.createdAt}</td>
										<td><a onClick="validateHrefVulnerability(this)" 
											href="advancedRulesListView?idData=${listdatasource.idData}&templateName=${listdatasource.name}&createdByUser=${listdatasource.createdByUser}"
											class="nav-link"> <span class="title">${listdatasource.name}</span></a></td>
										<td>${listdatasource.dataLocation}</td>
										<td>${listdatasource.tableName}</td>
										<td>${listdatasource.projectName}</td>
										<td>${listdatasource.createdByUser}</td>
										<td><c:choose>
												<c:when
													test="${listdatasource.templateCreateSuccess == 'Y'}">
													<span class='label label-success label-sm'>${listdatasource.templateCreateSuccess}</span>
												</c:when>
												<c:when
													test="${listdatasource.templateCreateSuccess == 'N'}">
													<span class='label label-danger label-sm'>${listdatasource.templateCreateSuccess}</span>
												</c:when>
												<c:otherwise>
													${listdatasource.templateCreateSuccess}
												</c:otherwise>
											</c:choose></td>
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

			order : [ [ 0, "desc" ] ],
			"scrollX" : true
		});
	});
</script>
<jsp:include page="footer.jsp" />