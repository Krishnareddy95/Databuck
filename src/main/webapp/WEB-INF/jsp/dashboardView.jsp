<%@page import="com.databuck.service.RBACController"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<jsp:include page="container.jsp" />

<style>
.pagination {
	display: -webkit-box;
	display: -ms-flexbox;
	display: flex;
	padding-left: 0;
	list-style: none;
	border-radius: .25rem;
}

.justify-content-end {
	-webkit-box-pack: end !important;
	-ms-flex-pack: end !important;
	justify-content: flex-end !important;
}

.dataTables_filter {
	float: right;
	text-align: right;
}

.dataTables_filter {
	color: #333;
}

label {
	display: inline-block;
	font-family: Montserrat;
	font-style: normal;
	font-weight: normal;
	font-size: 14px;
	line-height: 17px;
	color: #000000;
}
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
							<span class="caption-subject bold ">Dashboard Views</span>
						</div>
					</div>
					<div class="portlet-body">
						<!-- <div class="row">
							<div class="col-md-6">
								<div class="dataTables_length" id="showRecordCount">
									<label>Show <select name="showRecordCount"
										class="form-control input-sm">
											<option value="10">10</option>
											<option value="25">25</option>
											<option value="50">50</option>
											<option value="100">100</option>
									</select> entries
									</label>
								</div>
							</div>
							<div class="col-md-6">
								<div id="searchRecord" class="dataTables_filter">
									<label>Search:<input type="search"
										class="form-control input-sm" placeholder="">
									</label>
								</div>
							</div>
						</div> -->
						<!-- datatable -->
						<table
							class="table table-striped table-bordered table-hover"
							style="width: 100%;">
							<thead>
								<tr>
									<th>View Name</th>
									<th>View Description</th>
									<th>View</th>
									<%
									boolean flagED = false;
									flagED = RBACController.rbac("Dashboard", "D", session);
									if (flagED) {
									%>
									<th>Edit</th>
									<%
									}
									%>
									<%
									boolean flag = false;
									flag = RBACController.rbac("Dashboard", "D", session);
									if (flag) {
									%>
									<th>Delete</th>
									<%
									}
									%>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="dashboardViewObj" items="${listDashboardViews}">
									<tr>
										<td>${dashboardViewObj.viewName}</td>
										<td>${dashboardViewObj.description}</td>
										<td><a onClick="validateHrefVulnerability(this)" 
											href="viewRuleDetails?idruleMap=${dashboardViewObj.idruleMap}">View</a>
										</td>
										<td><a onClick="validateHrefVulnerability(this)" 
											href="editView?idruleMap=${dashboardViewObj.idruleMap}"><i
												style="margin-left: 20%;" class="fa fa-edit"></i></a></td>
										<td><a onClick="validateHrefVulnerability(this)"  data-attr="${dashboardViewObj.idruleMap}"
											data-toggle="confirmation" class="deleteRuleView"
											data-singleton="true"><i
												style="margin-left: 20%; color: red" class="fa fa-trash "></i></a></td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
						<!-- <nav aria-label="Page navigation example">
							<ul class="pagination justify-content-end">
								<li class="page-item disabled"><a onClick="validateHrefVulnerability(this)"  class="page-link"
									href="#" tabindex="-1">Previous</a></li>
								<li class="page-item"><a onClick="validateHrefVulnerability(this)"  class="page-link" href="#">1</a></li>
								<li class="page-item"><a onClick="validateHrefVulnerability(this)"  class="page-link" href="#">2</a></li>
								<li class="page-item"><a onClick="validateHrefVulnerability(this)"  class="page-link" href="#">3</a></li>
								<li class="page-item"><a onClick="validateHrefVulnerability(this)"  class="page-link" href="#">Next</a>
								</li>
							</ul>
						</nav> -->
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
<script src="./assets/global/plugins/jquery.dataTables.min.js"
	type="text/javascript">
	
</script>
<jsp:include page="footer.jsp" />
<script>
	$(document).ready(function () {
	    var table;
	    table = $('.table').dataTable({

	        order: [[0, "desc"]],
	        "scrollX": true
	    });
	});
	$("body").delegate(".deleteRuleView", "click", function() {
		var id = $(this).attr('data-attr');

		$.ajax({
			url : './deleteView',
			type : 'GET',
			data : {
				idruleMap : id
			},
			success : function(message) {
				var j_obj = $.parseJSON(message);
				if (j_obj.hasOwnProperty('success')) {
					toastr.info(j_obj.success);
					location.reload();
				}
			}
		});

	});
</script>