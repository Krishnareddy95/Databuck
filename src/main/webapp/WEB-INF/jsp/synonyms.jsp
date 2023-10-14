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
							<span class="caption-subject bold "> Synonyms Library </span>
						</div>
					</div>
					<div class="portlet-body">
						<table
							class="table table-striped table-bordered  table-hover dataTable"
							style="width: 100%;">
							<thead>
								<tr>


									<th>User Fields</th>

									<th>Synonyms</th>

									<th>Domain Name</th>
									<!-- <th>MatchingRules</th> -->


									<% 
										boolean flagED = false;
									    flagED = RBACController.rbac("Extend Template & Rule", "D", session);
										if (flagED){
									%>
									<!-- <th>Edit</th> -->
									<% 	
									}
								   
									%>
									<%
											boolean flag = false;
												flag = RBACController.rbac("Extend Template & Rule", "D", session);
												if (flag) {
											%>

									<%} %>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="listColRulesDataObj" items="${rulefields}">
									<tr>


										<td>${listColRulesDataObj.usercolumns}</td>

										<td>${listColRulesDataObj.possiblenames}</td>

										<td>${listColRulesDataObj.domain_name}</td>

										<%-- <%
											if (flagED) {
										%>
										<td><a onClick="validateHrefVulnerability(this)"  
											href="editsynonym?rule_id=${listColRulesDataObj.rule_id}"><i
												style="margin-left: 20%;" class="fa fa-edit"></i></a></td>
										<%
											}
										%>  --%>

										<%-- 	<%	if (flag) {
										
											
											%>
										<td><span style="display: none">
												${listColRulesDataObj.idListColrules}</span> 
												<a onClick="validateHrefVulnerability(this)"  href="#"	data-toggle="confirmation" class="deleteglobalruleclass"
											data-singleton="true"><i
												style="margin-left: 20%; color: red" class="fa fa-trash "></i></a>
										</td>
										<%} %>
										 <td>
									
										 <button class="btn-link" onclick="copy('${listColRulesDataObj.ruleName}', ${listColRulesDataObj.idListColrules})">Copy</button> 
										</td> --%>
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
<jsp:include page="footer.jsp" />
<script>

function copy(ruleName, idListColrules) {
	 
	
	
	
	var person = prompt("Please Enter New Rule Name", ruleName);


   
    if (person != null) {
      
        var form_data = {
        		newRuleName : person,
        		idListColrules: idListColrules
		};
        
    	$.ajax({
    		type : 'GET',	        		
    		url : "./copyGlobalRules",
    		data : form_data,
    		success : function(message){
    			
    			 window.location.reload();
    		} 
    	});
    }
    
	}
</script>
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
