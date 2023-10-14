
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<jsp:include page="header.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<jsp:include page="container.jsp" />



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
							<span class="caption-subject bold "> Schedulers </span><br/>
							<br><font size="4" color="red"></font>
						</div>
					</div>
					<div class="portlet-body">
						<table class="table table-striped table-bordered table-hover"
							id="showDataTable">
							<thead>
								<tr>
								<th>Name</th>
								<th>Description</th>
								<th>Project Name</th>
								<th>Time</th>
								<th>Frequency</th>
								<th>Schedule Day</th>
								<th>Edit</th>
								<th>Delete</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="ListScheduleDataObj" items="${listScheduleData}">
									<tr>
									<td>${ListScheduleDataObj.name}</td>
									<td>${ListScheduleDataObj.description}</td>
									<td>${ListScheduleDataObj.projectName}</td>
									<td>${ListScheduleDataObj.time}</td>
									<td>${ListScheduleDataObj.frequency}</td>
									<td>${ListScheduleDataObj.scheduleDay}</td>
									<td><a onClick="validateHrefVulnerability(this)"  href="editSchedule?idSchedule=${ListScheduleDataObj.idSchedule}"><i style="margin-left: 20%;" class="fa fa-edit"></i></a></td>
									<td><a onClick="validateHrefVulnerability(this)"  href='deleteSchedule?idSchedule=${ListScheduleDataObj.idSchedule}' data-toggle='confirmation' data-singleton='true'><i style='margin-left: 20%; color: red' class='fa fa-trash'></i></a> </td>
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
		  	
		  	order: [[ 0, "desc" ]],
			//By Mamta 24-3-2022 added to show delete popup on next page
		  	data-toggle="confirmation";
		   	// "scrollX": true
	      });
	});

</script>

<jsp:include page="footer.jsp" />