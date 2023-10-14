
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<!--============= BEGIN CONTENT BODY============= -->
<style>

tbody{
    overflow:scroll;
    height:100px;
    overflow-wrap: anywhere;
    max-width: 100% !important;
    width: 100%;
}

</style>

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
							<span class="caption-subject bold "> Application Groups </span><br />
							<br> <font size="4" color="red"></font>
						</div>
					</div>
					<div class="portlet-body">
						<table class="table table-striped table-bordered table-hover dataTable no-footer"
							id="showDataTable">
							<thead>
								<tr>
								    <th style="width:30px;">Id</th>
									<th>Name</th>
									<th>Description</th>
									<th>Project Name</th>
									<th>Scheduling Enabled</th>
									<th>Frequency</th>
									<th>Schedule Day</th>
									<th>Time</th>
									<th>Customize</th>
									<th>Delete</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="listAppGroupDataObj" items="${listAppGroupData}">
									<tr>
										<td  style="width:30px;">${listAppGroupDataObj.idAppGroup}</td>
										<td>${listAppGroupDataObj.name}</td>
										<td>${listAppGroupDataObj.description}</td>
										<td>${listAppGroupDataObj.projectName}</td>
										<td>${listAppGroupDataObj.enableScheduling}</td>
										<td>${listAppGroupDataObj.frequency}</td>
										<td>${listAppGroupDataObj.scheduleDay}</td>
										<td>${listAppGroupDataObj.time}</td>
										<td><a onClick="validateHrefVulnerability(this)" 
											href="customizeAppGroup?idAppGroup=${listAppGroupDataObj.idAppGroup}"><i
												style="margin-left: 20%;" class="fa fa-edit"></i></a></td>
										
										<td><span style="display: none">${listAppGroupDataObj.idAppGroup}</span> <a onClick="validateHrefVulnerability(this)" 
											href="#" class="deleteAppGroupClass"
											data-toggle="confirmation" data-singleton="true"><i
											style="margin-left: 20%; color: red" class="fa fa-trash"></i></a></td>
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

			order : [ [ 0, "desc" ] ]
		});
	});

</script>
<jsp:include page="footer.jsp" />
