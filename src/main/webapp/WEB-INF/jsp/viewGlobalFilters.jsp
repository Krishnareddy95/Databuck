<%@page import="com.databuck.service.RBACController"%>
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
							<span class="caption-subject bold "> View Global Filters</span>
						</div>
					</div>
					<br>
					<form action="addNewGlobalFilter">
					<input type="hidden" name="domain" value="${globalFiltersDataObj.domain}" />
					<button class="btn btn-primary" id="BtnAddNewFilter" data-toggle="modal" data-target="#myModal">Add New Filter</button>
                     </form>                       <br><br>
					<div class="portlet-body">

						<table
							class="table table-striped table-bordered  table-hover dataTable"
							style="width: 100%;">

							<thead>
								<tr>
									<th>Id</th>
									<th>Filter Name</th>
									<th>Domain</th>
									<th>Filter Condition</th>
									<th>Description</th>
									<th>Created By</th>

									<%
										boolean flagED = false;
									    flagED = RBACController.rbac("Global Rule", "D", session);
										if (flagED){
									%>
									<th>Edit</th>
									<%
									}
									%>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="globalFiltersDataObj" items="${globalFiltersData}">
									<tr>
										<td>${globalFiltersDataObj.filterId}</td>
										<td>${globalFiltersDataObj.filterName}</td>
										<td>${globalFiltersDataObj.domain}</td>
										<td>${globalFiltersDataObj.filterCondition}</td>
										<td>${globalFiltersDataObj.description}</td>
										<td style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">${globalFiltersDataObj.createdAt}</td>

										<%
											if (flagED) {
										%>
										<td><a onClick="validateHrefVulnerability(this)"
											href="editGlobalFilter?filterId=${globalFiltersDataObj.filterId}&domain=${globalFiltersDataObj.domain}"><i
												style="margin-left: 20%;" class="fa fa-edit"></i></a></td>
										<%
											}
										%>

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
<script src="./assets/global/plugins/jquery.min.js"
	type="text/javascript">
</script>
<script
	src="./assets/global/plugins/jquery.dataTables.min.js"
	type="text/javascript">
</script>
<jsp:include page="footer.jsp" />


<script>
$(document).ready(function() {
	var table;
	table = $('.table').dataTable({

		  	order: [[ 0, "desc" ]],
		  	"scrollX": true
	      });
});


</script>
