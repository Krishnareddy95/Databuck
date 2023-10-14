<%@ page import="java.util.*"%><!DOCTYPE html>
<html>
<head>
<script type="text/javascript">
	var path = '${pageContext.request.contextPath}';
</script>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet"
	href="./assets/global/plugins/bootstrap.min.css">
<link href="./assets/global/plugins/jquery.dataTables.css"
	rel="stylesheet" type="text/css">
<!-- <link
	href="http://datatables.net/release-datatables/extensions/ColVis/css/dataTables.colVis.css"
	rel="stylesheet" type="text/css"> -->
<script src="./assets/global/plugins/jquery-1.11.1.min.js"></script>
<script
	src="./assets/global/plugins/jquery.dataTables.min.js"></script>
<!-- <script
	src="http://datatables.net/release-datatables/extensions/ColVis/js/dataTables.colVis.js"></script> -->
<!-- <script
	src="http://jquery-datatables-column-filter.googlecode.com/svn/trunk/media/js/jquery.dataTables.columnFilter.js"></script> -->
<script type="text/javascript"
	src="./assets/pagination/custom-datatable.js"></script>
<script type="text/javascript"
	src="./assets/pagination/fnStandingRedraw.js"></script>
<script type="text/javascript"
	src="./assets/pagination/fnSetFilteringDelay.js"></script>



</head>
<!-- <body>
	<form>
		<div class="form">
			<input type="hidden" id="tableName"
				value="DATA_QUALITY_127_Transactionset_sum_A1">
			<table id="personTable" class="display" cellspacing="0" width="100%"
				style="overflow-x: auto">
				<table width="100%" border="0" margin="0" padding="0" 
			class="row-border tableHeader" id="personTable">
				<thead>
					<tr>
						<th>Id</th>
						<th>Date</th>
						<th>Run</th>
						<th>DayOfYear</th>
						<th>Month</th>
						<th>DayOfMonth</th>
						<th>DayOfWeek</th>
						<th>HourOfDay</th>
						<th>FileNameValidationStatus</th>
						<th>ColumnOrderValidationStatus</th>
						<th>RC_Std_Dev_Status</th>
						<th>RC_Mean_Moving_Avg_Status</th>
						<th>RecordCount</th>
						<th>DuplicateDataSet</th>
						<th>RC_Std_Dev</th>
						<th>RC_Mean</th>
						<th>RC_Deviation</th>
						<th>RC_Mean_Moving_Avg</th>
						<th>DGroupVal</th>
						<th>DGroupCol</th>
					</tr>
				</thead>
			</table>
		</div>
	</form>
	<style>
tfoot input {
        width: 100%;
        padding: 3px;
        box-sizing: border-box;
    }
.tableHeader{
text-align:left;
}
tfoot {
    display: table-header-group;
}
.dataTables_length
{
position: absolute;
    top: 10px;
    left: 220px;
}
.dataTables_info {
    position: absolute;
    top: 0px;
    left: 5px;
}
.ColVis{
 padding-right:10px;
 padding-top:5px;
 
}
.dataTables_filter {
   position: absolute;
   top: 10px;
   left: 200px;
   font-size:15px;
}
.dataTables_filter input{
height:22px;
width:150px
}
input
{
-moz-border-radius: 15px;
 border-radius: 3px;
 border:solid 1px #c7c7c7;
 padding:5px;
}
table.dataTable tbody td {
    padding: 5px;
    padding-left: 20px;
}
</style>
</body> -->
</html>