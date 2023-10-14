
<%@page import="com.databuck.service.RBACController"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />


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
							<span class="caption-subject bold "> List of Projects </span><br />
							<span class="caption-subject bold "> ${message} </span> <br>

							<font size="4" color="red"></font>
						</div>
					</div>
					<div class="portlet-body">
						<a onClick="validateHrefVulnerability(this)"  href="addNewProject" class="nav-link "><button class="btn btn-primary" id="BtnAddNewProject" data-toggle="modal" data-target="#myModal">Add New Project</button>	</a>
                   <br><br>
						<table id="data-quality-table"
							class="table table-striped table-bordered  table-hover"
							style="width: 100%;">
							<thead>
								<tr>
									<th>Project Id</th>
									<th>Project Name</th>
									<th>Description</th>
									<th>Edit</th>
									<th>Delete</th>

								</tr>
							</thead>

						</table>
					</div>
				</div>
				<!--       *************END EXAMPLE TABLE PORTLET*************************-->

			</div>
		</div>
	</div>
</div>

<jsp:include page="footer.jsp" />


<script>
	jQuery(document).ready(function() {
		var table;
		var path = '${pageContext.request.contextPath}';
		table = $('#data-quality-table').dataTable({
			"processing" : true,
			"serverSide" : true,
			"bPaginate" : true,
			"order" : [ 0, 'asc' ],
			"bInfo" : true,
			"iDisplayStart" : 0,
			"iDisplayLength" : 10,
			"dataSrc" : "",
			'sScrollX' : true,
			"sAjaxSource" : path + "/projectResultView",
			"dom" : 'C<"clear">lfrtip',
			colVis : {
				"align" : "right",
				restore : "Restore",
				showAll : "Show all",
				showNone : "Show none",
				order : 'alpha'
			//"buttonText": "columns <img src=\"/datatableServersideExample/images/caaret.png\"/>"
			},
			"language" : {
				"infoFiltered" : ""
			},
			"dom" : 'Cf<"toolbar"">rtip',

		});
		$('#data-quality-table_filter input').unbind();
		$('#data-quality-table_filter input').bind('keyup', function(e) {
			if (e.keyCode == 13) {
				table.fnFilter(this.value);
			}
		});

	});
</script>







<!--********     BEGIN CONTENT ********************-->
