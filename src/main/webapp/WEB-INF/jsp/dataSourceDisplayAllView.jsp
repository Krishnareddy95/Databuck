<%@page import="com.databuck.service.RBACController"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<!-- BEGIN CONTENT -->
<style>
	table.dataTable tbody th,
	table.dataTable tbody td {
		white-space: nowrap;
	}
	.dataTables_scrollBody{
            overflow-y: hidden !important;
   	 	}
</style>
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
							<span class="caption-subject bold "> Data Templates </span>
						</div>
					</div>
					<div class="portlet-body">
						<table
							class="table table-striped table-bordered  table-hover dataTable"
							style="width: 100%;">
							<thead>
								<tr>
									<th>Name</th>
									<th>Description</th>
									<th>Location</th>
									<th>Source</th>
									<th>Created By</th>
									<th style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">Created At</th>

									<th>Actions</th>
									<%
											boolean delete = false;
                                                delete = RBACController.rbac("Validation Check", "D", session);
												if (delete) {
									%>
									<th>Delete</th>
									<%} %>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="listdatasource" items="${listdatasource}">
									<tr>
										<td>${listdatasource.name}</td>
										<td>${listdatasource.description}</td>
										<td>${listdatasource.dataLocation}</td>
										<td>${listdatasource.dataSource}</td>
										<td>${listdatasource.createdByUser}</td>
										<td style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">${listdatasource.createdAt}</td>
										<%
											boolean view = false;
                                    view = RBACController.rbac("Validation Check", "R", session);
												if (view) {
									%>
										<td><a onClick="validateHrefVulnerability(this)" 
											href="listdataview?idData=${listdatasource.idData}&dataLocation=${listdatasource.dataLocation}&name=${listdatasource.name}&description=${listdatasource.description}">View
										</td>
										<%} %>
										<%if(delete){ %>
										<td><a onClick="validateHrefVulnerability(this)" 
											href="deletedatasource?idData=${listdatasource.idData}">
												<i style="margin-left: 20%; color: red" class="fa fa-trash"></i>
										</a></td>
										<%} %>
									</tr>
								</c:forEach>
								<%--     <a onClick="validateHrefVulnerability(this)"  href="<?php echo site_url('dataitem/index?id='.$sourceData[$i]['idData']);?>">View</a>
                                                        <a onClick="validateHrefVulnerability(this)"  href="<?php echo site_url('datasource/delete?id='.$sourceData[$i]['idData']);?>"><i style="margin-left: 20%;color:red" class="fa fa-trash"></i></a> --%>
							</tbody>

						</table>
					</div>
				</div>
				<!-- END EXAMPLE TABLE PORTLET-->
			</div>
		</div>
		<!-- <div class="row">
                        <div class="col-md-12">
                            BEGIN EXAMPLE TABLE PORTLET
                            <div class="portlet light bordered">
                                <div class="portlet-title">
                                    <div class="caption font-red-sunglo">
                                        <span class="caption-subject bold "> No Data Templates found.  </span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div> -->
	</div>
</div>
<!-- END CONTAINER -->
<jsp:include page="footer.jsp" />

<script src="./assets/global/plugins/jquery.min.js"
	type="text/javascript">
</script>
<script
	src="./assets/global/plugins/jquery.dataTables.min.js"
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
