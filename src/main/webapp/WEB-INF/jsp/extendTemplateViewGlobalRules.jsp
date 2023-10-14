<%@page import="com.databuck.service.RBACController"%>
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
							<span class="caption-subject bold "> Extended Template
								View Rules</span>
						</div>
					</div>
					<div class="portlet-body">
						<table class="table table-striped table-bordered  table-hover datatable-master" style="width: 100%;">
							<thead>
								<tr>
									<th>Rule Name</th>
									<!-- <th>Description</th>
									<th>Template Name</th> -->
									<th style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">Created At</th>
									<!-- <th>RuleType</th> -->
									<th>Expression</th>
									<!-- <th>MatchingRules</th> -->
									
									
									<% 
										boolean flagED = false;
									    flagED = RBACController.rbac("Extend Template & Rule", "D", session);
										if (flagED){
									%>
									<th>Edit</th>
									<% 	
									}
								   
									%>
									<%
											boolean flag = false;
												flag = RBACController.rbac("Extend Template & Rule", "D", session);
												if (flag) {
											%>
									<th>Delete</th>
									<th>Copy</th>
									<%} %>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="listColRulesDataObj" items="${listColRulesData}">
									<tr>
										<td>${listColRulesDataObj.ruleName}</td>
										<%-- <td>${listColRulesDataObj.description}</td>
										<td>${listColRulesDataObj.templateName}</td> --%>
										<td style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">${listColRulesDataObj.createdAt}</td>
										<%-- <td>${listColRulesDataObj.ruleType}</td> --%>
										<td>${listColRulesDataObj.expression}</td>
									<%-- 	<td>${listColRulesDataObj.matchingRules}</td> --%>
										<%
											if (flagED) {
										%>
										<td><a onClick="validateHrefVulnerability(this)"  
											href="editExtendTemplateRule?idListColrules=${listColRulesDataObj.idListColrules}"><i
												style="margin-left: 20%;" class="fa fa-edit"></i></a></td>
										<%
											}
										%>
										
										<%	if (flag) {
										
											
											%>
										<td><span style="display: none">
												${listColRulesDataObj.idListColrules}</span> 
												<a onClick="validateHrefVulnerability(this)"  href="#"	data-toggle="confirmation" class="deletelistcolruleclass"
											data-singleton="true"><i
												style="margin-left: 20%; color: red" class="fa fa-trash "></i></a>
										</td>
										<%} %>
										 <td>
									
										 <button class="btn-link" onclick="copy('${listColRulesDataObj.ruleName}', ${listColRulesDataObj.idListColrules})">Copy</button> 
										</td>
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
    		url : "./copyRules",
    		data : form_data,
    		success : function(message){
    			
    			 window.location.reload();
    		} 
    	});
    }
    
	}
</script>