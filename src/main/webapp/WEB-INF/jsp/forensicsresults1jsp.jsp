<%@page
	import="org.springframework.jdbc.support.rowset.SqlRowSetMetaData"%>
<%@page import="com.mysql.jdbc.ResultSetMetaData"%>
<%@page import="org.springframework.jdbc.support.rowset.SqlRowSet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="container.jsp" />
<jsp:include page="header.jsp" />


<!-- BEGIN CONTENT -->


<!-- BEGIN CONTENT -->

<!-- BEGIN CONTENT -->
<div class="page-content-wrapper">
	<!-- BEGIN CONTENT BODY -->
	<div class="page-content">
		<!-- BEGIN PAGE TITLE-->
		<!-- END PAGE TITLE-->
		<!-- END PAGE HEADER-->
		<div class="row">
			<div class="col-md-6" style="width: 100%;">
				<!-- BEGIN SAMPLE FORM PORTLET-->
				<div class="portlet light bordered init">
					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold ">
								DATA_QUALITY_${appId}_Transactionset_sum_A1</span>
						</div>
					</div>

					<table class="table table-striped table-bordered table-hover"
						id="showDataTable">
						<thead>
							<tr>
								<th>Id</th>
								<th style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">Date</th>
								<th>Run</th>
								<th>RecordCount</th>
								<th>
									<%
										out.println(request.getAttribute("dynamiccolumnName"));
										System.out.print("dynamiccolumnName:" + request.getAttribute("dynamiccolumnName"));
									%>
								</th>
								<%-- <th>${dynamiccolumnName}</th> --%>
								<th>DuplicateDataSet</th>
								<th>RC_Std_Dev</th>
								<th>RC_Mean</th>
								<th>RC_Deviation</th>
								<th>RC_Std_Dev_Status</th>
								<th>RC_Mean_Moving_Avg</th>
								<th>RC_Mean_Moving_Avg_Status</th>
								<th>M_Std_Dev</th>
								<th>M_Mean</th>
								<th>M_Deviation</th>
								<th>M_Std_Dev_Status</th>
								<th>M_Mean_Moving_Avg</th>
								<th>M_Mean_Moving_Avg_Status</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="readTransactionset_sum_A1Tablepojo"
								items="${readTransactionset_sum_A1Table}">

								<tr>
									<td>${readTransactionset_sum_A1Tablepojo.id}</td>
									<td style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">${readTransactionset_sum_A1Tablepojo.date}</td>
									<td>${readTransactionset_sum_A1Tablepojo.run}</td>
									<td>${readTransactionset_sum_A1Tablepojo.recordCount}</td>
									<td>${readTransactionset_sum_A1Tablepojo.recordCount}</td>
									<td>${readTransactionset_sum_A1Tablepojo.duplicateDataSet}</td>
									<td>${readTransactionset_sum_A1Tablepojo.rc_Std_Dev}</td>
									<td>${readTransactionset_sum_A1Tablepojo.rc_Mean}</td>
									<td>${readTransactionset_sum_A1Tablepojo.rc_Deviation}</td>
									<td>${readTransactionset_sum_A1Tablepojo.rc_Std_Dev_Status}</td>
									<td>${readTransactionset_sum_A1Tablepojo.rc_Mean_Moving_Avg}</td>
									<td>${readTransactionset_sum_A1Tablepojo.rc_Mean_Moving_Avg_Status}</td>
									<td>${readTransactionset_sum_A1Tablepojo.m_Std_Dev}</td>
									<td>${readTransactionset_sum_A1Tablepojo.m_Mean}</td>
									<td>${readTransactionset_sum_A1Tablepojo.m_Deviation}</td>
									<td>${readTransactionset_sum_A1Tablepojo.m_Std_Dev_Status}</td>
									<td>${readTransactionset_sum_A1Tablepojo.m_Mean_Moving_Avg}</td>
									<td>${readTransactionset_sum_A1Tablepojo.m_Mean_Moving_Avg_Status}</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold ">
								DATA_QUALITY_${appId}_Transaction_Summary</span>
						</div>
					</div>
					<table class="table table-striped table-bordered table-hover"
						id="showDataTable">
						<thead>
							<tr>
								<th style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">Date</th>
								<th>Run</th>
								<th>Duplicate</th>
								<th>Type</th>
								<th>TotalCount</th>
								<th>Percentage</th>
								<th>Threshold</th>
								<th>Status</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="readTransaction_SummaryTable"
								items="${readTransaction_SummaryTable}">
								<tr>
									<td style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">${readTransaction_SummaryTable.date}</td>
									<td>${readTransaction_SummaryTable.run}</td>
									<td>${readTransaction_SummaryTable.duplicate}</td>
									<td>${readTransaction_SummaryTable.type}</td>
									<td>${readTransaction_SummaryTable.totalCount}</td>
									<td>${readTransaction_SummaryTable.percentage}</td>
									<td>${readTransaction_SummaryTable.threshold}</td>
									<td>${readTransaction_SummaryTable.status}</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold ">
							<% %>	DATA_QUALITY_${appId}_Transaction_Detail_All</span>
						</div>
					</div>
					<table class="table table-striped table-bordered table-hover"
						id="showDataTable">
						<thead>
							<tr>
								<%-- <c:forEach var="column" items="${columnNames}">
									<th>${column}</th>
								</c:forEach> --%>
								<%
									SqlRowSet sqlRowSet1 = (SqlRowSet) request.getAttribute("readTransaction_DetailTable");
									SqlRowSetMetaData metaData = sqlRowSet1.getMetaData();
									for (int i = 1; i <= metaData.getColumnCount(); i++) {
										out.println("<th>" + metaData.getColumnName(i) + "</th>");
									}
								%>
							</tr>
						</thead>
						<tbody>
							<%
								SqlRowSet sqlRowSet = (SqlRowSet) request.getAttribute("readTransaction_DetailTable");
								while (sqlRowSet.next()) {
									out.println("<tr>");
									for (int k = 1; k <= sqlRowSet.getMetaData().getColumnCount(); k++) {
										out.println("<td>" + sqlRowSet.getString(k) + "</td>");
									}
									out.println("</tr>");
								}
							%>
						</tbody>
					</table>
					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold ">
								DATA_QUALITY_${appId}_Transaction_Detail_Identity</span>
						</div>
					</div>
					<table class="table table-striped table-bordered table-hover"
						id="showDataTable">
						<thead>
							<tr>
								<%
									SqlRowSet sqlrowsetidentity = (SqlRowSet) request.getAttribute("readTransaction_Detail_IdentityTable");
									SqlRowSetMetaData metadataidentity = sqlrowsetidentity.getMetaData();
									for (int i = 1; i <= metadataidentity.getColumnCount(); i++) {
										out.println("<th>" + metadataidentity.getColumnName(i) + "</th>");
									}
								%>
							</tr>
						</thead>
						<tbody>
							<%
								SqlRowSet sqlRowSetidentity = (SqlRowSet) request.getAttribute("readTransaction_Detail_IdentityTable");
								while (sqlRowSetidentity.next()) {
									out.println("<tr>");
									for (int k = 1; k <= sqlRowSetidentity.getMetaData().getColumnCount(); k++) {
										out.println("<td>" + sqlRowSetidentity.getString(k) + "</td>");
									}
									out.println("</tr>");
								}
							%>
						</tbody>
					</table>
					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold ">
								DATA_QUALITY_${appId}_Column_Summary</span>
						</div>
					</div>
					<table class="table table-striped table-bordered table-hover"
						id="showDataTable">
						<thead>
							<tr>
								<%
									SqlRowSet sqlrowsetsummary = (SqlRowSet) request.getAttribute("readColumn_SummaryTable");
									SqlRowSetMetaData metadatasummary = sqlrowsetsummary.getMetaData();
									for (int i = 1; i <= metadatasummary.getColumnCount(); i++) {
										out.println("<th>" + metadatasummary.getColumnName(i) + "</th>");
									}
								%>
							</tr>
						</thead>
						<tbody>
							<%
								SqlRowSet sqlRowSetsummary = (SqlRowSet) request.getAttribute("readColumn_SummaryTable");
								while (sqlRowSetsummary.next()) {
									out.println("<tr>");
									for (int k = 1; k <= sqlRowSetsummary.getMetaData().getColumnCount(); k++) {
										out.println("<td>" + sqlRowSetsummary.getString(k) + "</td>");
									}
									out.println("</tr>");
								}
							%>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
</div>
<jsp:include page="footer.jsp"/>
