<%@page import="com.databuck.service.RBACController"%>
<%@page
	import="org.springframework.jdbc.support.rowset.SqlRowSetMetaData"%>
<%@page import="org.springframework.jdbc.support.rowset.SqlRowSet"%>
<%@page import="com.databuck.bean.ListApplications"%>
<%@page import="java.util.List"%>
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
			<div class="col-md-12">
				<!-- BEGIN EXAMPLE TABLE PORTLET-->
				<div class="portlet light bordered">
					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold "> View Roles </span>
						</div>
					</div>
					<div class="portlet-body">
					<a onClick="validateHrefVulnerability(this)"  href="addNewRole" class="nav-link "><button class="btn btn-primary" id="BtnAddNewRole" data-toggle="modal" data-target="#myModal">Add New Role</button>	</a>
					<br><br>
						<table
							class="table table-striped table-bordered  table-hover dataTable"
							style="width: 100%;">
							<thead>
								<tr>
									<th>Role Name</th>
									<th>Description</th>
									<%
											boolean flag = false;
									flag = RBACController.rbac("User Settings", "U", session);
                                   			 //System.out.println("customize="+customize);
												if (flag) {
									%>
									<th>Action</th>
									<%} %>
									<th>Delete</th>
								</tr>
							</thead>
							<tbody>
								<%
										try{
								SqlRowSet sqlRowSet = (SqlRowSet) request.getAttribute("roleManagementData");
								while (sqlRowSet.next()) {
									out.println("<tr>");
									for (int k = 2; k <= sqlRowSet.getMetaData().getColumnCount(); k++) {
										out.println("<td class='role-name'>" + sqlRowSet.getString(k) + "</td>");
										 }  if (flag) {
											 out.println("<td class='idRole'><a onClick='validateHrefVulnerability(this)'  href=editRoleModule?idRole="+sqlRowSet.getString(1)+"><i style='margin-left: 20%;'></i>Edit</a></td>");
											 out.println("<td> <a onClick='validateHrefVulnerability(this)'  href=deleteRoleModule?idRole="+sqlRowSet.getString(1)+" data-toggle='confirmation' data-singleton='true'><i style='margin-left: 20%; color: red' class='fa fa-trash'></i></a> </td>");
										 }
									out.println("</tr>");
									
								}}catch(NullPointerException e)
								{
									%><p>No data available in table</p>
								<%
								}
										%>
								
							</tbody>
						</table>
					</div>
				</div>
				<!-- END EXAMPLE TABLE PORTLET-->
			</div>
		</div>
		<!--   <div class="note note-info" style="width:90%;">
                <h3>No Validation Checks found!</h3> -->
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

<script>
$(document).ready(function() {
	var table;
	table = $('.table').dataTable({
		  	
		  	order: [[ 0, "desc" ]],
		  	"scrollX": true
	      });
});

</script>

<!-- END CONTAINER -->
<jsp:include page="footer.jsp" />
