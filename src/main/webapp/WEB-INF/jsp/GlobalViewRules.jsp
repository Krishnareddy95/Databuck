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
							<span class="caption-subject bold "> View Global Rules</span>
						</div>
					</div>
					<div class="portlet-body">
						<table
							class="table table-striped table-bordered  table-hover dataTable"
							style="width: 100%;">
							<thead>
								<tr>
									<th>Rule Id</th>
									<th>Rule Name</th>
									<!-- <th>Description</th> -->
									<th>Domain</th>
									<th>RuleType</th>
									<th>Filter Condition</th>
									<th>Right Template Filter Condition</th>
									<th>Matching Rules</th>
									<th>Expression</th>
									<th>Dimension</th>
									<th style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">Created_On</th>
									<th>Created By</th>


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
										<td>${listColRulesDataObj.idListColrules}</td>
										<td>${listColRulesDataObj.ruleName}</td>
										<%-- <td>${listColRulesDataObj.description}</td> --%>
										<td>${listColRulesDataObj.domain}</td>
										<%-- <td>${listColRulesDataObj.templateName}</td>  --%>
										<td>${listColRulesDataObj.ruleType}</td>
										<td>${listColRulesDataObj.filterCondition}</td>
										<td>${listColRulesDataObj.rightTemplateFilterCondition}</td>
										<td>${listColRulesDataObj.matchingRules}</td>
										<td>${listColRulesDataObj.expression}</td>
										<td>${listColRulesDataObj.dimensionName}</td>
										<td style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">${listColRulesDataObj.createdAt}</td>
										<td>${listColRulesDataObj.createdByUser}</td>

										<%
											if (flagED) {
										%>
										<td><a onClick="validateHrefVulnerability(this)" 
											href="editGlobalRule?idListColrules=${listColRulesDataObj.idListColrules}&domain=${listColRulesDataObj.domain}"><i
												style="margin-left: 20%;" class="fa fa-edit"></i></a></td>
										<%
											}
										%>

										<%	if (flag) {
										
											
											%>
										<td><span style="display: none">
												${listColRulesDataObj.idListColrules}</span>
												<a onClick="validateHrefVulnerability(this)"  href="#" data-toggle="confirmation" class="deleteglobalruleclass"
											data-singleton="true"><i
												style="margin-left: 20%; color: red" class="fa fa-trash "></i></a>
										</td>
										<%} %>
										<td>

											<button class="btn-link"
												onclick="copy('${listColRulesDataObj.ruleName}', ${listColRulesDataObj.idListColrules},'${listColRulesDataObj.domain}')">Copy</button>
										</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
				<!--       *************END EXAMPLE TABLE PORTLET*************************-->
               	<!-- The Modal -->

				<div class="modal" id="myModal">
					<div class="modal-dialog"
						style="display: inline-block; position: fixed; top: 0; bottom: 0; left: 0; right: 0; height: 500px; margin: auto; overflow:auto;">
						<div class="modal-content">

							<!-- Modal Header -->
							<div class="modal-header">
								<h4 class="modal-title">Link tag to Global Rule</h4>
								<button type="button" class="close" data-dismiss="modal">&times;</button>
							</div>

							<!-- Modal body -->
							<div class="modal-body" id="newValues">
								<div><label>GlobalRuleId</label><span>  </span><input type=text disabled class= form-control id="globalRuleId" /></div>
								<br>
								<div><label>GlobalRuleName</label><span>  </span><input type=text disabled class= form-control id="globalRuleName" /></div>
								<br>
								<div class="form-group form-md-line-input">
									<select class="form-control text-left" style="text-align: left;" id="tagId" >
										<option value=-1>Select</option>
										<c:forEach var="tagObj" items="${tags}">
											<option value="${tagObj.tagId}">${tagObj.tagName}</option>
										</c:forEach>
									</select> 
									<label for="form_control_1" style="color: black;">Choose Tags for Rule *</label>
								</div>
							</div>
								
							<!-- Modal footer -->
							<div class="modal-footer">
								<button type="submit" class="btn btn-success" id="insertcall" onClick="addTagToGlobalRule()" text-align: center; >Submit</button>
								<button type="button" class="btn btn-danger" data-dismiss="modal">Cancel</button>
							</div>
							
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<script src="./assets/global/plugins/jquery.min.js"
	type="text/javascript">
</script>
<script src="./assets/global/plugins/jquery-ui.min.js"></script>
<script src="./assets/global/plugins/bootstrap-multiselect.js"></script>
<link rel="stylesheet"
	href="./assets/global/plugins/bootstrap-multiselect.css">

<script
	src="./assets/global/plugins/jquery.dataTables.min.js"
	type="text/javascript">
</script>
<jsp:include page="footer.jsp" />
<script>

function copy(ruleName, idListColrules,domain) {
	 
	var isNameValidToSave = true;
	
	do
	{
		var newRule = prompt("Please Enter New Rule Name", ruleName);
		if(!checkPromptText(newRule)){
			isNameValidToSave = false;
			alert('Vulnerability in submitted data, not allowed to submit!');
		}
	}while(newRule == ruleName);
	


   
    if (newRule != null && isNameValidToSave) {
      
        var form_data = {
        		newRuleName : newRule,
        		idListColrules: idListColrules,
        		domain: domain
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


<script type="text/javascript">
$(document).ready(function() {
	var table;
	table = $('.table').dataTable({
		  	
		  	order: [[ 0, "desc" ]],
		  	"scrollX": true
	      });
	
});



</script>
