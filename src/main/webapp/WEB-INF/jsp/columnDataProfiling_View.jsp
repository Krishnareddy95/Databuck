<%@page import="com.databuck.bean.ColumnProfile_DP"%>
<%@page import="java.util.List"%>
<%@page import="com.databuck.service.RBACController"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Arrays"%>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<link rel="stylesheet" href="./assets/css/custom-style.css" type="text/css" />
<style>
#form-body-profile-entry {
	padding: 15px;
}

#form-body-profile-entry label {
	height: 32px;
	font-size: 13.5px !important;
	margin-top: 10px !important;
	margin-bottom: -5px !important;
}

#form-body-profile-entry select, #form-body-profile-entry input[type="email"]
	{
	width: 100% !important;
	height: 32px;
	margin: auto auto 15px auto;
	font-size: 13.5px !important;
	font-family: inherit;
	padding-bottom: 10px;
}

#form-body-profile-entry textarea {
	width: 100% !important;
	min-height: 125px;
	margin: auto;
	font-size: 12.5px;
	font-family: "Lucida Console", Courier, monospace;
	border: 2px solid !important !important;
}

#BtnPublish {
	margin-bottom: 15px;
	float: right;
}

.columnlabel {
    border: 1px solid red;
    border-radius: 5px 5px 5px;
    background-color: white;
    padding-left: 5px;
    font-size: smaller;
    padding-right: 5px;
    margin-left: 10px;
    padding-top: 1px;
    padding-bottom: 1px;
    color: blue;
}
.dataTables_scrollBody{
            overflow-y: hidden !important;
   	 	}
</style>


<!-- BEGIN CONTENT -->
<div class="page-content-wrapper">
	<!-- BEGIN CONTENT BODY -->
	<div class="page-content">
		<!-- BEGIN PAGE TITLE-->
		<!-- END PAGE TITLE-->
		<!-- END PAGE HEADER-->

		<div class="row">
			<div class="col-md-12">
				<div class="portlet light bordered">

					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold ">Column Profiling Summary 
							(<a onClick="validateHrefVulnerability(this)"  href="profilingDownloadCsvS3?tableName=Column_Profile&idData=${01}&tableNickName=ColumnProfile">Download csv</a>)
							</span>
						</div>

					</div>

					<!-- Project Date filter - START -->
					<div class="portlet-body">
						 <div class="row">
						 	<div class="col-md-3 col-capture form-group form-md-line-input">
						    	<select class="form-control multiselect text-left"
									style="text-align: left;" id="lstProject" name="listProject">
										<option value="${selectedProject.idProject}" selected>${selectedProject.projectName}</option>
									<c:forEach items="${projectList}" var="projectListdata">
										<option value="${projectListdata.idProject}">${projectListdata.projectName}</option>
									</c:forEach>
								</select> 
								<input type="hidden" id="selectedProject" name="selectedProject" value="" /> 
								<label for="form_control_1" id="host-id" style="padding-left: 20px;color: black;font-weight: bold;" >Project Name*</label>
							</div>
							
							<div class="col-md-2 col-capture form-group form-md-line-input">
								<input class="form-control toDate" id="date" name="date" 
											placeholder="From Date" type="text" value="${toDate}" />
								<label for="form_control_1" id="fromDate" style="padding-left: 20px;color: black;font-weight: bold;">From Date</label>
							</div>
							
							<div class="col-md-2 col-capture form-group form-md-line-input">
								<input class="form-control fromDate" id="date" name="date" 
										placeholder="To date" type="text" value="${fromDate}" />
								<label for="form_control_1" id="toDate" style="padding-left: 20px;color: black;font-weight: bold;">To Date</label>
							</div>
							
							<div class="col-md-2 col-capture">
								<div class="form-group form-md-line-input">
									<input type="button" id="filterDate" class="btn blue" value="Search" style="height: 38px; padding-top: 12px;">
								</div>
							</div>
					    </div>
					</div>
					<br/>
					<!-- Project Date filter - END -->
		
					<div class="portlet-body">
						<table
							class="table table-striped table-bordered dataTable no-footer"
							id="columnProfile_tableId">
							<thead>
								<tr>
									<th>Template Id</th>
									<th style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">Created Date</th>
									<th>Run</th>
									<th>Table/File Name</th>
									<th>Column_Name</th>
									<th>Project_Name</th>
									<th>Data_Type</th>
									<th>Total_Record_Count</th>
									<th>Missing_Value</th>
									<th>Percentage_Missing</th>
									<th>Unique_Count</th>
									<th>Min_Length</th>
									<th>Max_Length</th>
									<th>Mean</th>
									<th>Std_Dev</th>
									<th>Min</th>
									<th>Max</th>
									<th>99_percentile</th>
									<th>75_percentile</th>
									<th>25_percentile</th>
									<th>1_percentile</th>
									<th>Default_Patterns</th>
								</tr>
							</thead>

							<tbody>
								<c:forEach var="columnProfileObj"
									items="${columnProfileList}">
									<tr>
										<td>${columnProfileObj.idData}</td>
										<td style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">${columnProfileObj.execDate}</td>
										<td>${columnProfileObj.run}</td>
										<td>${columnProfileObj.table_or_fileName}</td>
										<td>${columnProfileObj.columnName}</td>
										<td>${columnProfileObj.projectName}</td>
										<td>${columnProfileObj.dataType}</td>
										<td>${columnProfileObj.totalRecordCount}</td>
										<td>${columnProfileObj.missingValue}</td>
										<td>${columnProfileObj.percentageMissing}</td>
										<td>${columnProfileObj.uniqueCount}</td>
										<td>${columnProfileObj.minLength}</td>
										<td>${columnProfileObj.maxLength}</td>
										<td>${columnProfileObj.mean}</td>
										<td>${columnProfileObj.stdDev}</td>
										<td>${columnProfileObj.min}</td>
										<td>${columnProfileObj.max}</td>
										<td>${columnProfileObj.percentile_99}</td>
										<td>${columnProfileObj.percentile_75}</td>
										<td>${columnProfileObj.percentile_25}</td>
										<td>${columnProfileObj.percentile_1}</td>
										<td>${columnProfileObj.defaultPatterns}</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<script src="./assets/global/plugins/jquery.min.js" type="text/javascript"></script>
<script src="./assets/global/plugins/jquery.dataTables.min.js" type="text/javascript"></script>
<script>
	$(document).ready(function() {
		var table;
		table = $('#columnProfile_tableId').dataTable({
			order : [ [ 0, "desc" ] ],
			"scrollX" : true,
			'columnDefs': [
		        {
		           'targets': 21,
		           'render': function(data, type, full, meta){
		              return data.replace("val:",'').replace(/val:/g,"<br>");
		           }
		        }
		     ],
			"aoColumns": [
	            { "sWidth": "100" },
	            { "sWidth": "100" },
	            { "sWidth": "100" },
	            { "sWidth": "100" },
	            { "sWidth": "100" },
	            { "sWidth": "100" },
	            { "sWidth": "100" },
	            { "sWidth": "100" },
	            { "sWidth": "100" },
	            { "sWidth": "100" },
	            { "sWidth": "100" },
	            { "sWidth": "100" },
	            { "sWidth": "100" },
	            { "sWidth": "100" },
	            { "sWidth": "100" },
	            { "sWidth": "100" },
	            { "sWidth": "100" },
	            { "sWidth": "100" },
	            { "sWidth": "100" },
	            { "sWidth": "100" },
	            { "sWidth": "100" },
	            { "sWidth": "200" }
	        ]
		});
	});
</script>
<script>
$(document).ready(function(){
	var date_input=$('input[name="date"]'); //our date input has the name "date"
	var container=$('.bootstrap-iso form').length>0 ? $('.bootstrap-iso form').parent() : "body";
	date_input.datepicker({
		format: 'yyyy-mm-dd',
		container: container,
		todayHighlight: true,
		autoclose: true,
		endDate: "today"
	})
});
</script>
<script type="text/javascript">
	var code = {};
	$("select[name='listProject'] > option").each(function () {
		if (code[this.text]) {
			$(this).remove();
		} else {
			code[this.text] = this.value;
		}
	});
</script>
<script>	
	   $("#filterDate").click(function () {	       		
	       	var selectednumbers = [];
			$('#lstProject :selected').each(function (i, selected) {
				selectednumbers[i] = $(selected).val();
			});
			var projectid = selectednumbers.join();
	       	var form_data = {
	    		   toDate : $(".toDate").val(),
                   fromDate: $(".fromDate").val(),
                   projectid: projectid,
           	};
           	$.ajax({
               url : './dateAndProjectFilter',
               type : 'GET',
               
               datatype : 'json',
               data : form_data,
               success : function(message) {
            	
            	   window.location= window.location.href;
               }
           }); 
	   });
</script>
<jsp:include page="footer.jsp" />
