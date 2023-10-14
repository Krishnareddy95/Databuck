
<%@page import="org.springframework.jdbc.support.rowset.SqlRowSet"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="com.google.common.collect.LinkedHashMultimap"%>
<%@page import="com.google.common.collect.Multimap"%>
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
							<span class="caption-subject bold "> Triggers </span><br/>
							<br><font size="4" color="red"></font>
						</div>
					</div>
					
					<!-- Listing Scheduled Validations -->
					<div class="portlet-body">
						<div class="portlet-title">
							<div class="caption font-red-sunglo">
								<span class="caption-subject bold ">Scheduled Validations</span>
								<br/><br>
							</div>
						</div>
						<table class="table table-striped table-bordered table-hover"
							id="showDataTable">
							<thead>
								<tr>
								<th>Trigger Id</th>
								<th>Validation Check</th>
								<th>Schedule</th>
								<th>Project Name</th>
								<th>Delete</th>
								</tr>
							</thead>
							<tbody>
							<%
							if(request.getAttribute("validationTriggerData")!=null){
							 SqlRowSet triggerData =(SqlRowSet)request.getAttribute("validationTriggerData");
							 while(triggerData.next()){
								 out.println("<tr>");
								 	out.println("<td>"+triggerData.getLong(1)+"</td>");
									out.println("<td>"+triggerData.getString(2)+"</td>");
									out.println("<td>"+triggerData.getString(3)+"</td>");
									out.println("<td>"+triggerData.getString(4)+"</td>");
									out.println("<td><a onClick='validateHrefVulnerability(this)'  href='deleteTrigger?id="+triggerData.getLong(1)+"' data-toggle='confirmation' data-singleton='true'><i style='margin-left: 20%; color: red' class='fa fa-trash'>"+"</i></a></td>");
									out.println("</tr>");
							 }
							}
							%>
							</tbody>
						</table>
					</div>
					
					<br>
					
					<!-- Listing Scheduled Schemas -->
					<div class="portlet-body">
						<div class="portlet-title">
							<div class="caption font-red-sunglo">
								<span class="caption-subject bold ">Scheduled Schemas</span>
								<br/><br>
							</div>
						</div>
						<table class="table table-striped table-bordered table-hover"
							id="showDataTable1">
							<thead>
								<tr>
								<th>Trigger Id</th>
								<th>Schema Name</th>
								<th>Schedule</th>
								<th>Project Name</th>
								<th>Delete</th>
								</tr>
							</thead>
							<tbody>
							<%
							if(request.getAttribute("schemaTriggerData")!=null){
							 SqlRowSet triggerData =(SqlRowSet)request.getAttribute("schemaTriggerData");
							 while(triggerData.next()){
								 out.println("<tr>");
								 	out.println("<td>"+triggerData.getLong(1)+"</td>");
									out.println("<td>"+triggerData.getString(2)+"</td>");
									out.println("<td>"+triggerData.getString(3)+"</td>");
									out.println("<td>"+triggerData.getString(4)+"</td>");
									out.println("<td><a onClick='validateHrefVulnerability(this)'  href='deleteTrigger?id="+triggerData.getLong(1)+"' data-toggle='confirmation' data-singleton='true'><i style='margin-left: 20%; color: red' class='fa fa-trash'>"+"</i></a></td>");
									out.println("</tr>");
							 }
							}
							%>
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
	var table1;

	table = $('#showDataTable').dataTable({
		    //By Mamta 24/02/2022 
		    //data-toggle='confirmation',
		  	order: [[ 0, "desc" ]]
	      });
	 table1 = $('#showDataTable1').dataTable({
		    order: [[ 0, "desc" ]]
	  	     });
	
	
});

</script>
<jsp:include page="footer.jsp" />