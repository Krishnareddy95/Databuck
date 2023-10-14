<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="org.springframework.jdbc.support.rowset.SqlRowSet"%>
<%@page
	import="org.springframework.jdbc.support.rowset.SqlRowSetMetaData"%>
	<jsp:include page="checkVulnerability.jsp" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<style>

/* body{border:20px} */
.dataTables_scroll{position:relative}
.dataTables_scrollHead{margin-bottom:40px;}
.dataTables_scrollFoot{position:absolute; top:38px}
</style>
<body>
	<jsp:include page="dashboardTableCommon.jsp" />


	<div class="row">
		<div class="col-md-12">
			<div class="tabcontainer">
				<ul class="nav nav-tabs responsive" role="tablist"
					style="border-top: 1px solid #ddd; border-bottom: 1px solid #ddd;">

					<li ><a onClick="validateHrefVulnerability(this)" 
						href="dashboard_table?idApp=${idApp}" style="padding: 2px 2px;">
							<strong>Count Reasonability</strong>
					</a></li>
					<li ><a onClick="validateHrefVulnerability(this)"  href="validity?idApp=${idApp}"
						style="padding: 2px 2px;"> <strong>Microsegment Validity</strong>
					</a></li>
					<li ><a onClick="validateHrefVulnerability(this)"  href="dupstats?idApp=${idApp}"
						style="padding: 2px 2px;"> <strong>Uniqueness</strong>
					</a></li>
					<li ><a onClick="validateHrefVulnerability(this)"  href="nullstats?idApp=${idApp}"
						style="padding: 2px 2px;"> <strong>Completeness</strong>
					</a></li>
					<li ><a onClick="validateHrefVulnerability(this)"  href="badData?idApp=${idApp}"
						style="padding: 2px 2px;"><strong>Conformity</strong> </a></li>
					<li ><a onClick="validateHrefVulnerability(this)"  href="sqlRules?idApp=${idApp}"
						style="padding: 2px 2px;"><strong>Custom Rules</strong> </a></li>
					<li ><a onClick="validateHrefVulnerability(this)"  href="stringstats?idApp=${idApp}"
						style="padding: 2px 2px;"> <strong>Drift & Orphan</strong>
					</a></li>
					<li ><a onClick="validateHrefVulnerability(this)"  href="numericstats?idApp=${idApp}"
						style="padding: 2px 2px;"> <strong>Distribution Check</strong>
					</a></li>
					<li ><a onClick="validateHrefVulnerability(this)"  href="recordAnomaly?idApp=${idApp}"
						style="padding: 2px 2px;"> <strong>Record Anomaly</strong></a></li>
					<li ><a onClick="validateHrefVulnerability(this)"  href="timelinessCheck?idApp=${idApp}"
						style="padding: 2px 2px;"><strong>Sequence</strong> </a></li>
					<li class="active" ><a onClick="validateHrefVulnerability(this)" 
						href="exceptions?idApp=${idApp}" style="padding: 2px 2px;"><strong>Exceptions</strong> </a></li>
					<li ><a onClick="validateHrefVulnerability(this)"  href="rootCauseAnalysis?idApp=${idApp}"
						style="padding: 2px 2px;"><strong>RootCause Analysis</strong> </a></li>

				</ul>
			</div>
		</div>
	</div>


	<div class="portlet-title">
		<div class="caption font-red-sunglo">
			<span class="caption-subject bold ">Row Summary <a onClick="validateHrefVulnerability(this);javascript:downloadCsvReports('${idApp}','${row_summary_TableName}','RowSummary')"

				href="#" class="CSVStatus" id="">(Download CSV)</a>
			</span>
		</div>
	</div>
	<hr />
	<input type="hidden" id="tableName" value="${row_summary_TableName}">
	<input type="hidden" id="tableNameCsv"value="${row_summary_TableNameCsv}">
	
	<div class="portlet-body">
		<table id="RowSummaryTable"
			class="display nowrap table-bordered"
			style="width: 100%;">
			
			<thead>
				<tr></tr>
			</thead>
			<tfoot>
			
			</tfoot>
			

		</table>
	</div>
	<div>
		<b>Note: </b><i>Displaying records from latest file</i>
	</div>
	<br />

	<span style="display: inline-block; height: 50px;"></span>

	<jsp:include page="downloadCsvReports.jsp" />
	<jsp:include page="footer.jsp" />

</body>
<script src="./assets/global/plugins/jquery-3.3.1.js"></script>
<script src="./assets/global/plugins/jquery.dataTables.min.js"></script>

<link rel="stylesheet" href="./assets/global/plugins/jquery.dataTables.min.css">
<script>

    var rows = "";
	var vdata = "All";
	var tableNameCsv = $("#tableNameCsv").val();

	$.ajax({
		url : path + '/dynamicDataColumnCsv?tableNameCsv=' + tableNameCsv,
		dataType : 'json',
		success : function(data) {
			//column_array = JSON.parse(data.success);
			var aryColTableChecked = data.success;
			var aryJSONColTable = [];

			for (var i = 0; i < aryColTableChecked.length; i++) {
				aryJSONColTable.push({
					"sTitle" : aryColTableChecked[i],
					"aTargets" : [ i ]
				});
				rows = rows + "<th>" + aryColTableChecked[i] + "</th>";
			}
			;
			
			rows = "<tr>" + rows + "</tr>";
			$('#RowSummaryTable tfoot').append(rows);
			$(document).ready(
					function() {
						
						  $('#RowSummaryTable tfoot th').each( function () {
						        var title = $(this).text();
						        
						        $(this).html( '<input type="text" placeholder="Search '+title+'" />' );
						        
						    } );
						   
						
						/* var table = $('#RowSummaryTable').DataTable(
								{
									"paging" : true,
									"bInfo" : true,
									"bProcessing" : false,
									"bServerSide" : false,
									"bFilter" : true,
									"iDisplayStart" : 0,
									'sScrollX' : true,

									"sAjaxSource" : path
											+ "/dynamicDataSetCsv?vdata="
											+ vdata + "&tableNameCsv="
											+ tableNameCsv,

									"aoColumnDefs" : aryJSONColTable,
									"dom" : 'C<"clear">lfrtip',

									"targets" : 'no-sort',
									"bSort" : false,
									"order" : [],
									"dom" : 'Cf<"toolbar"">rtip'
									

								}); */
						var table = $('#RowSummaryTable').DataTable(
								{
									"paging" : true,
									"bInfo" : true,
									"iDisplayStart" : 0,
									'sScrollX' : true,
									"sAjaxSource" : path
											+ "/dynamicDataSetCsv?vdata="
											+ vdata + "&tableNameCsv="
											+ tableNameCsv,

									"aoColumnDefs" : aryJSONColTable,
									"dom" : 'C<"clear">lfrtip',
									"targets" : 'no-sort',
									"order" : [],
									"dom" : 'Cf<"toolbar"">rtip'
									
									

								});
						table.columns().eq( 0 ).each( function ( colIdx ) {
							 
					      $( 'input', table.column( colIdx ).footer() ).on( 'keyup change', function () {
					            table
					                .columns(colIdx)
					                .search(this.value)
					                .draw(); 
					        } );
					    } );
					});
		}
	});
</script>
