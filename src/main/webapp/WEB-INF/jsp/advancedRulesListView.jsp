
<%@page import="com.databuck.service.RBACController"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<jsp:include page="header.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<jsp:include page="container.jsp" />

<style>
	table.dataTable tbody th, table.dataTable tbody td {
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
							<span class="caption-subject bold ">Auto Discovered Rules for
								Template with Id / Name: ${idData} / ${templateName} <a onClick="validateHrefVulnerability(this)" 
								href="downloadAdvancedRules?idData=${idData}&tableNickName=AdvancedRules_${idData}"
								class="CSVStatus" id=""> (Download CSV)</a>
							</span>
						</div>
					</div>
					<div class="portlet-body">
					<input type="hidden" id="createdByUserId" value="${createdByUser}">
						<table
							class="table table-striped table-bordered table-hover dataTable no-footer"
							style="width: 100%;">
							<thead>
								<tr>
									<th style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">Date</th>
									<th>Run</th>
									<th>Rule Id</th>
									<th>Rule Type</th>
									<th>Column Name</th>
									<th>Rule Expression</th>
									<th>Rule Sql</th>
									<th>Action</th>
								</tr>
							</thead>
							<tbody>

								<c:forEach var="advancedRule" items="${advancedRulesList}">
									<c:set var="sql" value="${advancedRule.ruleSql}" />
									<c:set var="search" value="'" />
									<c:set var="replace" value="\\'" />
									<c:set var="ruleSql"
										value="${fn:replace(sql, search, replace)}" />
									<tr>
										<td style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">${advancedRule.execDate}</td>
										<td>${advancedRule.run}</td>
										<td>${advancedRule.ruleId}</td>
										<td>${advancedRule.ruleType}</td>
										<td>${advancedRule.columnName}</td>
										<td>${advancedRule.ruleExpr}</td>
										<td>${advancedRule.ruleSql}</td>
										<td><c:choose>
												<c:when test="${advancedRule.isCustomRuleEligible == 'Y'}">
													<c:choose>
														<c:when test="${advancedRule.isRuleActive == 'Y'}">
															<button class="btn-link"
																onclick="deactivateAdvanceRule(${idData},${advancedRule.ruleId},${advancedRule.idListColrules})">Deactivate</button>
														</c:when>
														<c:otherwise>
															<button class="btn-link"
																onclick="activateAdvanceRule(${idData},${advancedRule.ruleId},'${advancedRule.ruleType}','${ruleSql}','${advancedRule.columnName}')">Activate</button>
														</c:otherwise>
													</c:choose>
												</c:when>
											</c:choose></td>
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

<script>
	$(document).ready(function() {
		var table;
		table = $('.table').dataTable({

			order : [ [ 0, "desc" ] ],
			"scrollX" : true
		});
	});
</script>

<script>
function activateAdvanceRule(idData, ruleId, ruleType, ruleSql, columnName) {
	var form_data = {
		idData : idData,
		ruleId : ruleId,
		ruleType : ruleType,
		ruleSql : ruleSql,
		columnName : columnName,
		createdByUser: document.getElementById("createdByUserId").value
	};
	
	$.ajax({
				url : './activateAdvancedRuleById',
				type : 'POST',
				headers: { 'token':$("#token").val()},
				datatype : 'json',
				data : form_data,
				headers: { 'token':$("#token").val()},
				success : function(message) {
					console.log(message);
					var j_obj = $.parseJSON(message);
					if (j_obj.hasOwnProperty('success')) {
						toastr.info(j_obj.success);
						 window.location.reload();
					} else if (j_obj.hasOwnProperty('fail')) {
						toastr.info(j_obj.fail);
					}
				}
	});
}
</script>
<script>
function deactivateAdvanceRule(idData, ruleId, idListColrules) {
	var form_data = {
			idData : idData,
			ruleId : ruleId,
			idListColrules : idListColrules
		};

		$.ajax({
					url : './deactivateAdvancedRuleById',
					type : 'POST',
					datatype : 'json',
					data : form_data,
					headers: { 'token':$("#token").val()},
					success : function(message) {
						console.log(message);
						var j_obj = $.parseJSON(message);
						if (j_obj.hasOwnProperty('success')) {
							toastr.info(j_obj.success);
							window.location.reload();
						} else if (j_obj.hasOwnProperty('fail')) {
							toastr.info(j_obj.fail);
						}
					}
		});
}

</script>
<jsp:include page="footer.jsp" />
<!--********     BEGIN CONTENT ********************-->

