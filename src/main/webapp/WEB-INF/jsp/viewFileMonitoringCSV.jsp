<%@page import="com.databuck.bean.FileMonitorRules"%>
<%@page import="com.databuck.service.RBACController"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.ArrayList"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />

<style>
	table.dataTable tbody th,
	table.dataTable tbody td {
		white-space: nowrap;
	}

	.dataTables_scrollBody{
            overflow-y: hidden !important;
   	 	}
</style>

<!-- jQuery -->
<script src="js/jquery-1.10.2.min.js"></script>
<!-- <script
	src="./assets/global/plugins/jquery.min.js"></script>

<script type="text/javascript"
	src="./assets/js/bootstrap-material-datetimepicker.js"></script>

<link rel="stylesheet" href="./assets/css/timepicker.min.css">
 -->
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
							<span class="caption-subject bold ">File Monitoring View</span>
						</div>
					</div>
					<div class="portlet-body form">
						<!-- <form action="saveFileMonitoringRule" method="POST"
							accept-charset="utf-8"> -->

						<input type="hidden" id="fileDetails" value=fileDetails
							name="fileDetails">

						<div class="form-body">
							<div class="row">
								<div class="form-group form-md-line-input">

									<table class="table table-striped table-bordered table-hover dataTable no-footer" id="makeEditable">
										<thead>
											<tr>

												<!-- <th>Id</th> -->
												<th>Bucket_Name</th>
												<th>Day_Of_Check</th>
												<th>File_Count</th>
												<th>File_Pattern</th>
												<th>Folder_Path</th>
												<th>Frequency</th>
												<!-- <th>lastProcessedDate</th> -->
												<th>Time_Of_Check</th>
												<th>FileSize_Threshold</th>
												<th>Partitioned Folders</th>
												<th>MaxFolderDepth</th>
												<%
													boolean Delete = false;
													Delete = RBACController.rbac("Data Template", "D", session);
													//System.out.println("flag=" + flag);
													if (Delete) {
												%>
												<th>Delete</th>
												<%
													}
												%>

											</tr>
										</thead>
										<tbody>

											<%
												// Iterating through subjectList

												ArrayList<FileMonitorRules> arrCsv = (ArrayList) request.getAttribute("arrListFileMonitorRule");
												//	out.print(arrCsv);

												FileMonitorRules fileDetails;

												if (arrCsv != null) { // Null check for the object
													//	out.print("In IF.........");

													Iterator<FileMonitorRules> iterator = arrCsv.iterator(); // Iterator interface
													int i = 1;

													while (iterator.hasNext()) // iterate through all the data until the last record
													{

														fileDetails = iterator.next(); //assign individual employee record to the employee class object
														session.setAttribute("idAppVal", fileDetails.getIdApp());
											%>


											<tr>
												<%-- <td id="txtFileId"
													contenteditable="true"><%=i++%></td> --%>



												<td id="txtBucketName" name="bucketName"
													contenteditable="true"><%=fileDetails.getBucketName()%></td>



												<td><SELECT name="dayOfCheck"
													id=<%="selectDayOfChk" + i%> class="form-control"
													contenteditable="true">
														<option value="1"
															<%if ((fileDetails.getDayOfCheck()) == 1) {%> selected
															<%}%>>Sunday</option>

														<option value="2"
															<%if ((fileDetails.getDayOfCheck()) == 2) {%> selected
															<%}%>>Monday</option>

														<option value="3"
															<%if ((fileDetails.getDayOfCheck()) == 3) {%> selected
															<%}%>>Tuesday</option>

														<option value="4"
															<%if ((fileDetails.getDayOfCheck()) == 4) {%> selected
															<%}%>>Wednesday</option>

														<option value="5"
															<%if ((fileDetails.getDayOfCheck()) == 5) {%> selected
															<%}%>>Thursday</option>

														<option value="6"
															<%if ((fileDetails.getDayOfCheck()) == 6) {%> selected
															<%}%>>Friday</option>

														<option value="7"
															<%if ((fileDetails.getDayOfCheck()) == 7) {%> selected
															<%}%>>Saturday</option>
												</SELECT></td>


												<td id="txtFileCount" contenteditable="true"><%=fileDetails.getFileCount()%></td>

												<td id="txtFilePattern" contenteditable="true"><%=fileDetails.getFilePattern()%></td>


												<td id="txtFolderPath" contenteditable="true"><%=fileDetails.getFolderPath()%></td>


												<td><SELECT name="frequency" class="form-control"
													id=<%="selectFrequency" + i%> contenteditable="true">
														<option value="daily"
															<%if ((fileDetails.getFrequency()).equalsIgnoreCase("Daily")) {%>
															selected <%}%>>Daily</option>

														<option value="hourly"
															<%if ((fileDetails.getFrequency()).equalsIgnoreCase("Hourly")) {%>
															selected <%}%>>Hourly</option>

														<option value="weekly"
															<%if ((fileDetails.getFrequency()).equalsIgnoreCase("Weekly")) {%>
															selected <%}%>>Weekly</option>

												</SELECT></td>



												<td id="txtTimeOfCheck" contenteditable="true"><%=fileDetails.getTimeOfCheck()%></td>
												<td id="txtFileSizeThreshold" contenteditable="true"><%=fileDetails.getFileSizeThreshold()%></td>
												<td><SELECT name="partitionedFolders" class="form-control" id=<%="selectPartitionedFolders" + i%> contenteditable="true">
													<option value="N" selected>N</option>
													<option value="Y">Y</option>
												</SELECT></td>
												<td id="txtMaxFolderDepth" contenteditable="true"><%=fileDetails.getMaxFolderDepth()%></td>
												<!-- 	<div class="col-md-6 col-capture">
								<div class="form-group">
									<label class="control-label col-md-2">Time</label>
									<div class="col-md-4">
										<div class="input-group">
											<input type="text" id="getTimer" name="getTimer"
												class="form-control timepicker timepicker-24"> <span
												class="input-group-btn">
												<button class="btn default" type="button">
													<i class="fa fa-clock-o"></i>
												</button>
											</span>
										</div>
									</div>
								</div>
							</div>
												 -->




												<!-- <label class="control-label col-md-2">Time</label>
									<div class="col-md-4">
										<div class="input-group">
											<input type="text" id="getTimer" name="getTimer"
												class="form-control timepicker timepicker-24"> <span
												class="input-group-btn">
												<button class="btn default" type="button">
													<i class="fa fa-clock-o"></i>
												</button>
											</span>
										</div>
									</div>
								 -->




												<%
													if (Delete) {
												%>
												<%--  <td><a onClick="validateHrefVulnerability(this)" 
													href="deleteFileMonitorRuleByBuckName?bucketName=${listdatasource.idData}">
														<i style="margin-left: 20%; color: red"
														class="fa fa-trash"></i>
												</a></td> --%>

												<td><INPUT type="button" id="deleteRow" value="Delete"
													style="margin-left: 5%; color: red" class="fa fa-trash"
													onclick="deleteRow(this)"></td>


												<%
													}
												%>
											</tr>



											<%
												i = i + 1;
													}

												} else {

												}
											%>



										</tbody>
									</table>
								</div>



							<div style="margin-top: 10px;">
								<INPUT type="button" id="addrow" class="button"
									value="Add New Row" />
							</div>
								<!-- <INPUT type="button"
										value="Delete Row" onclick="deleteRow('dataTable')" /> -->

							</div>

							<div class="form-actions noborder align-center">
								<button type="submit" id="submitBtn" class="btn blue">Submit</button>
							</div>


						</div>
						<div id="displayDiv" style="display: none">
							<h3>JSON Data returned from Server after processing</h3>
							<div id="processedData"></div>
						</div>
						<!-- 	</form>
 -->
					</div>
					
					<div style="font-size: small;font-style: italic;">
						<b>Note:</b> Maximum allowed value for MaxFolderDepth is 2.
					</div>
				</div>


			</div>

		</div>
	</div>
</div>

<jsp:include page="footer.jsp" />
<head>

<!-- <script
	src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-multiselect/0.9.13/js/bootstrap-multiselect.js"></script>
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-multiselect/0.9.13/css/bootstrap-multiselect.css">

<script type="text/javascript" src="./assets/global/plugins/jsapi.js"></script>
<script src="./assets/js/timepicker.min.js"></script> -->


<script type="text/javascript">
$(document).ready(function() {
	$('.table').dataTable({
		  	"scrollX": true
	});
});
</script>
<script>
	$('#makeEditable').SetEditable({
		$addButton : $('#but_add')
	});
</script>
<script
	src="./assets/global/plugins/bootstrap-multiselect.js"></script>
<link rel="stylesheet"
	href="./assets/global/plugins/bootstrap-multiselect.css">

<script type="text/javascript" src="./assets/global/plugins/jsapi.js"></script>
<script src="./assets/js/timepicker.min.js"></script>

<script type="text/javascript">
	$("#addrow")
			.click(
					function() {

						//alert("addField");
						//Try to get tbody first with jquery children. works faster!
						var tbody = $('#makeEditable');

						var myTab = document.getElementById('makeEditable');

						var table = tbody.length ? tbody : $('#makeEditable');

						var row_len = (myTab.rows.length) - 1;

						//	alert("Table row length ->"+row_len);

						var i = row_len;

						table
								.append('<tr>'
										+ '<td contenteditable="true" id="txtBucketName"></td>'
										+ '<td><SELECT name="dayOfCheck" class="form-control" id="selectDayOfChk'+i+'" contenteditable="true">'
										+ '<option value=1>Monday</option>'
										+ '<option value=2>Tuesday</option>'
										+ '<option value=3>Wednesday</option>'
										+ '<option value=4>Thursday</option>'
										+ '<option value=5>Friday</option>'
										+ '<option value=6>Saturday</option>'
										+ '<option value=7>Sunday</option></SELECT></td>'
										+ '<td id="txtFileCount" contenteditable="true"></td>'
										+ '<td id="txtFilePattern" contenteditable="true"></td>'
										+ '<td id="txtFolderPath" contenteditable="true"></td>'
										+ '<td><SELECT name="frequency" class="form-control" id="selectFrequency'+i+'" contenteditable="true">'
										+ '<option value="daily">Daily</option>'
										+ '<option value="hourly">Hourly</option>'
										+ '<option value="weekly">Weekly</option></SELECT></td>'
										+ '<td value="13:14" id="txtTimeOfCheck" contenteditable="true"></td>'
										+ '<td id="txtFileSizeThreshold" contenteditable="true"></td>'
										+ '<td><SELECT name="partitionedFolders" class="form-control" id="selectPartitionedFolders'+i+'" contenteditable="true">'
										+ '<option value="N" selected>N</option>'
										+ '<option value="Y">Y</option>'
									    + '</SELECT></td>'
									    + '<td id="txtMaxFolderDepth" contenteditable="true">2</td>'
										+ '<td><INPUT type="button" id="deleteRow" value="Delete" style="margin-left: 5%; color: red" class="fa fa-trash" onclick="deleteRow(this)"></td>'
										+ '</tr>')
					});
	$("#submitBtn")
			.click(
					function() {

						//document.getElementById('info').innerHTML = "";
						var myTab = document.getElementById('makeEditable');

						var isError;
						//alert("subBtn");

						// LOOP THROUGH EACH ROW OF THE TABLE AFTER HEADER.

						//	alert("table rows len =>" + myTab.rows.length)

						for (i = 1; i < myTab.rows.length; i++) {
							var str = "";
							// GET THE CELLS COLLECTION OF THE CURRENT ROW.
							var objCells = myTab.rows.item(i).cells;

							//str = str + '||' + objCells.item(j).innerHTML;
							for (var j = 0; j < objCells.length; j++) {
								//info.innerHTML = info.innerHTML + '||' + objCells.item(j).innerHTML;
								
								if (objCells.item(j).innerHTML
										.indexOf("select") != -1) {
									if(j === 8){
										if ($('#selectPartitionedFolders' + i + ' :selected')) {
											
											var pf_selectElement = $(
													'#selectPartitionedFolders' + i
															+ ' :selected').text();

											str = str + '||' + pf_selectElement;
										}
										
									} else {
										var selectElement;
	
										//alert("check ->"+$('#selectDayOfChk'+i+' :selected').text());
	
										if ($('#selectDayOfChk' + i + ' :selected')) {
	
											//alert("In DayOfChk");
	
											selectElement = $(
													'#selectDayOfChk' + i
															+ ' :selected').text()
													+ '#';
	
										}
	
										if ($('#selectFrequency' + i + ' :selected')) {
	
											//	alert("In Fre");
											selectElement += $(
													'#selectFrequency' + i
															+ ' :selected').text();
	
										}
	
										//		alert("selectElement"+selectElement);
										str = str + '||' + selectElement;
	
								   }	
								} else {
										str = str + '||'
											+ objCells.item(j).innerHTML;
								}
								//alert(objCells.item(j).innerHTML);

							}

							var json_str = JSON.stringify(str);

							//alert("jsonStr--->" + json_str);

							var data_1 = {
								singleRowData : json_str
							};
							$.ajax({

								type : "POST",
								headers: { 'token':$("#token").val()},
								url : "saveFileMonitoringRule",
								data : data_1,
								datatype : 'json',

								success : function(message) {

								},

								error : function(xhr, textStatus, errorThrown) {
									
									isError = 1;
								}

							});

							//
						}

						if (isError == 1) {
							
							//alert("In IF......");
							toastr.info('There was a problem.');
						} else {
							
							//alert("In Else....");

								toastr.info('Submitted Successfully');
								setTimeout(
										function() {
											window.location.href = "validationCheck_View";
										}, 1000);
						
						}
					});
</script>

<script>
function Remove(button) {
    //Determine the reference of the Row using the Button.
    var row = $(button).closest("TR");
    var name = $("TD", row).eq(0).html();
    if (confirm("Do you want to delete: " + name)) {

        //Get the reference of the Table.
        var table = $("#tblCustomers")[0];

        //Delete the Table row using it's Index.
        table.deleteRow(row[0].rowIndex);
    }
};
</script>


<script type="text/javascript">
	function deleteRow(btn) {
		var row = btn.parentNode.parentNode;
		row.parentNode.removeChild(row);
		
	//	$(this).parents("tr").remove();
	}
</script>




</head>