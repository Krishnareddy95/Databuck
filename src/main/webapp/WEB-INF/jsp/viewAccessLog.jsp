
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
							<span class="caption-subject bold "> User Activity  </span><br/>
							<br><font size="4" color="red"></font>
						</div>
					</div>
					
					<!-- Listing Scheduled Validations -->
					<div class="portlet-body">
						<div class="portlet-title">
							<div class="caption font-red-sunglo">
								<span class="caption-subject bold ">Access Log</span>
								<span
									style="font-size: small;"><a onClick="validateHrefVulnerability(this);clearAllAccesslog()"
									 href="#">(Clear Log)</a></span>
								<br/><br>
							</div>
						</div>
						<table class="table table-striped table-bordered table-hover"
							id="showDataTable">
							<thead>
								<tr>
								<th>User</th>
								<th>Activity</th>
								<th>Date Time</th>
								<th>Application URL</th>
								</tr>
							</thead>
							<tbody>
							<%
							if(request.getAttribute("logging_activity")!=null){
							 SqlRowSet loggingData =(SqlRowSet)request.getAttribute("logging_activity");
							 while(loggingData.next()){
								 out.println("<tr>");
								 	out.println("<td>"+loggingData.getString(1)+"</td>");
									out.println("<td>"+loggingData.getString(2)+"</td>");
									out.println("<td>"+loggingData.getString(3)+"</td>");
									out.println("<td>"+loggingData.getString(4)+"</td>");
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
	table = $('#showDataTable').dataTable({
		  	
		  	order: [[ 0, "desc" ]]
	      });
});

</script>
<script>
function clearAllAccesslog() {
		$.ajax({
			type : 'GET',
			url : "./clearAccesslog",
			success : function(message) {
				var j_obj1 = $.parseJSON(message);
                
                if (j_obj1.hasOwnProperty('success')) {
					window.location.reload();
                }
                
                if(j_obj1.hasOwnProperty('failure')){
                	var msg = j_obj1.message;
					alert(msg+"\nFailed to clear the Access Log !!");
                }
			}
		});
	}
</script>
<jsp:include page="footer.jsp" />