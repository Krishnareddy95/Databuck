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
<script
	src="./assets/global/plugins/jquery.min.js"></script>

<script type="text/javascript"
	src="./assets/js/bootstrap-material-datetimepicker.js"></script>

<link rel="stylesheet" href="./assets/css/timepicker.min.css">

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
							<span class="caption-subject bold ">File Monitoring :
								${name} </span>
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

												<th>Id</th>
												<th>Bucket_Name</th>
												<th>Day_Of_Check</th>
												<th>File_Count</th>
												<th>File_Pattern</th>
												<th>Folder_Path</th>
												<th>Frequency</th>
												<!-- <th>lastProcessedDate</th> -->
												<th>Time_Of_Check</th>
												<th>File_Size_Threshold</th>
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
												Object objIdApp = request.getAttribute("idApp");

												String strIdApp = objIdApp.toString();
												int idApp = Integer.parseInt(strIdApp);

												session.setAttribute("idApp", idApp);

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
											%>


											<tr>
												<%-- <td id="txtFileId"
													contenteditable="true"><%=i++%></td> --%>
												<td id="txtId" name="id" contenteditable="false"><%=fileDetails.getId()%></td>

												<td id="txtBucketName" name="bucketName"
													contenteditable="true"><%=fileDetails.getBucketName()%></td>


												<%-- <td id="selectDayOfChk" contenteditable="true" ><%=fileDetails.getDayOfCheck()%> </td>  --%>

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
												<!-- 		
														<ul id="selectDayOfCheck" class="dropdown-menu" role="menu">
										<li><a onClick="validateHrefVulnerability(this)"  tabindex="-1" href="#" class="payLink">Pay</a></li>
										<li><a onClick="validateHrefVulnerability(this)"  tabindex="-1" href="#" class="delLink">Delete</a></li>
									</ul> -->



												<%-- <td class="dropdown"><a onClick="validateHrefVulnerability(this)" 
													class="btn btn-default actionButton" data-toggle="dropdown"
													href="#"></a><%=fileDetails.getDayOfCheck()%> </td> --%>

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

												<%-- 	<td class="dropdown" contenteditable="true"><%=fileDetails.getFrequency()%> </td> --%>

												<%-- 									<td class="dropdown"><a onClick="validateHrefVulnerability(this)" 
													class="btn btn-default actionButton" data-toggle="dropdown"
													href="#"></a><%=fileDetails.getFrequency()%> </td> --%>

												<%-- 
												<td class="dropdown"><a onClick="validateHrefVulnerability(this)" 
													class="btn btn-default actionButton" data-toggle="dropdown"
													href="#"></a><%=fileDetails.getFrequency()%> </td>
	 --%>

												<td id="txtTimeOfCheck" contenteditable="true"><%=fileDetails.getTimeOfCheck()%></td>
												<td id=txtFileSizeThreshold contenteditable="true"><%=fileDetails.getFileSizeThreshold()%></td>
												<td>
												  <SELECT name="partitionedFolders" class="form-control" id=<%="selectPartitionedFolders" + i%> contenteditable="true">
													<option value="N" <%if ((fileDetails.getPartitionedFolders()).equalsIgnoreCase("N")) {%>
															selected <%}%>>N</option>
													<option value="Y"<%if ((fileDetails.getPartitionedFolders()).equalsIgnoreCase("Y")) {%>
															selected <%}%>>Y</option>
												</SELECT></td>
												<td id="txtMaxFolderDepth" contenteditable="true"><%=fileDetails.getMaxFolderDepth()%></td>
												<%
													if (Delete) {
												%>
												<%--  <td><a onClick="validateHrefVulnerability(this)" 
													href="deleteFileMonitorRuleByBuckName?bucketName=${listdatasource.idData}">
														<i style="margin-left: 20%; color: red"
														class="fa fa-trash"></i>
												</a></td>
												
												data-toggle="confirmation" data-singleton="true"
													id="deleteLink" 
												 --%>
												<td>
													<%-- <a onClick="validateHrefVulnerability(this);deleteRow(this)"
													href="deleteFileMonitorRuleById?id=<%=fileDetails.getId()%>&idApp=<%=fileDetails.getIdApp()%>"
													> <i
														style="margin-left: 20%; color: red" class="fa fa-trash"></i>
												</a> --%>
													<button
														onclick="deleteRow(<%=fileDetails.getId()%>, <%=fileDetails.getIdApp()%>)"
														 style="background-color: Transparent; background-repeat: no-repeat; border: none; cursor: pointer; overflow: hidden; outline: none;">
														<i style="margin-left: 20%; color: red"
															class="fa fa-trash"></i>
													</button>
												</td>
												<!-- <td><INPUT type="button" id="deleteRow" value="Delete"
													style="margin-left: 5%; color: red" class="fa fa-trash"
													onclick="deleteRow(this)"></td> -->
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

											<!-- <tr>
												<td>101</td>
												<td>bucket_S2</td>
												<td><SELECT name="dayOfCheck"><option value="mon">Monday</option>

											<option value="tue">Tuesday</option>
											<option value="wed">Wednesday</option>
											<option value="thur">Thursday</option>
											<option value="fri">Friday</option>
											<option value="sat">Saturday</option>
											<option value="sun">Sunday</option></SELECT></td>
												<td>filePatt</td>
												<td>C:/test</td>
												<td><SELECT name="frequency">
														<option value="daily">Daily</option>

														<option value="hourly">Hourly</option>
														<option value="weekly">Weekly</option>
												</SELECT></td>

												<td>null</td>
												<td>12:30</td>

 -->
											<%-- <td>${listDataSourceobj.description}</td>
												<td>${listDataSourceobj.dataLocation}</td>
												<td>${listDataSourceobj.createdAt}</td> 
												<td><a onClick="validateHrefVulnerability(this)" 
													href="viewMetaData?idData=${listDataSourceobj.idData}">view</a></td> </tr>--%>



										</tbody>
									</table>

								</div>



								<!-- 	<INPUT type="button" id="addrow" class="button"
									value="Add New Row" />
 -->
								<!-- <INPUT type="button"
										value="Delete Row" onclick="deleteRow('dataTable')" /> -->

							</div>

							<div class="form-actions noborder align-center">
								<button type="submit" id="submitBtn" class="btn blue">UPDATE</button>
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

<script
	src="./assets/global/plugins/bootstrap-multiselect.js"></script>
<link rel="stylesheet"
	href="./assets/global/plugins/bootstrap-multiselect.css">

<script type="text/javascript" src="./assets/global/plugins/jsapi.js"></script>
<script src="./assets/js/timepicker.min.js"></script>


<script type="text/javascript">
$(document).ready(function() {
	$('.table').dataTable({
		  	order: [[ 0, "desc" ]],
		  	"scrollX": true
	});
});

	document.addEventListener("DOMContentLoaded", function(event) {
		timepicker.load({
			interval : 15
		});
	});
	/* jQuery(document).ready(function($) {
	
	 alert("txtTimeOfCheck");
	
	 $('#txtTimeOfCheck').bootstrapMaterialDatePicker({ date: false });	
	 });
	 */

	$('#makeEditable').SetEditable({
		$addButton : $('#but_add')
	});

	$(window).load(function() {
		//save the selector so you don't have to do the lookup everytime
		$dropdown = $("#selectDayOfCheck");
		$(".actionButton").click(function() {
			//get row ID
			var id = $(this).closest("tr").children().first().html();
			//move dropdown menu
			$(this).after($dropdown);
			//update links
			$dropdown.find(".payLink");
			$dropdown.find(".delLink");
			//show dropdown
			$(this).dropdown();
		});
	});
</script>


<script type="text/javascript">
	/* $("#addrow")
			.click(
					function() {

						//	alert("addField");
						//Try to get tbody first with jquery children. works faster!
						var tbody = $('#makeEditable');

						var table = tbody.length ? tbody : $('#makeEditable');

						/* 
						
						 table.append('<tr>'
						 +'<td contenteditable="true" id="txtFileId"><em></em></td>'
						 +'<td contenteditable="true" id="txtBucketName"><em></em></td>'
						 +'<td><em><SELECT name="dayOfCheck" class="form-control" id="selectDayOfCheck">'
						 +'<option value="mon" contenteditable="true">Monday</option>'
						 +'<option value="tue">Tuesday</option>'
						 +'<option value="wed">Wednesday</option>'
						 +'<option value="thur">Thursday</option>'
						 +'<option value="fri">Friday</option>'
						 +'<option value="sat">Saturday</option>'
						 +'<option value="sun">Sunday</option></SELECT></em></td>'
						 +'<td><em><input type="text" class="form-control" id="txtFileCount" contenteditable="true"></em></td>'
						 +'<td><em><input type="text" class="form-control" id="txtFilePattern" contenteditable="true"></em></td>'
						 +'<td><em><input type="text" class="form-control" id="txtFolderPath" contenteditable="true"></em></td>'
						 +'<td><em><SELECT name="frequency" class="form-control" id="selectFrequency" contenteditable="true">'
						 +'<option value="daily">Daily</option>'
						 +'<option value="hourly">Hourly</option>'
						 +'<option value="weekly">Weekly</option></SELECT></em></td>'
						 +'<td><em><div class="input-group clockpicker" data-placement="left" data-align="top" data-autoclose="true">'
						 +'<input type="text" class="form-control" value="13:14" id="txtTimeOfCheck" contenteditable="true">'
						 +'<span class="input-group-addon"> <span class="glyphicon glyphicon-time"></span>'
						 +'</span></div><em></td>'
						 +'<td><a onClick="validateHrefVulnerability(this)"  href="deletedatasource?idData=${listdatasource.idData}">'
						 +'<i style="margin-left: 20%; color: red" class="fa fa-trash"></i></a></td>'
						 +'</tr>'); 

						table
								.append('<tr>'
										+ '<td contenteditable="true" id="txtBucketName"></td>'
										+ '<td><SELECT name="dayOfCheck" class="form-control" id="selectDayOfCheck">'
										+ '<option value="mon" contenteditable="true">Monday</option>'
										+ '<option value="tue">Tuesday</option>'
										+ '<option value="wed">Wednesday</option>'
										+ '<option value="thur">Thursday</option>'
										+ '<option value="fri">Friday</option>'
										+ '<option value="sat">Saturday</option>'
										+ '<option value="sun">Sunday</option></SELECT></td>'
										+ '<td id="txtFileCount" contenteditable="true"></td>'
										+ '<td id="txtFilePattern" contenteditable="true"></td>'
										+ '<td id="txtFolderPath" contenteditable="true"></td>'
										+ '<td><SELECT name="frequency" class="form-control" id="selectFrequency" contenteditable="true">'
										+ '<option value="daily">Daily</option>'
										+ '<option value="hourly">Hourly</option>'
										+ '<option value="weekly">Weekly</option></SELECT></td>'
										+ '<td value="13:14" id="txtTimeOfCheck" contenteditable="true"></td>'
										+ '<td><INPUT type="button" id="deleteRow" value="Delete" style="margin-left: 5%; color: red" class="fa fa-trash" onclick="deleteRow(this)"></td>'
										+ '</tr>');

					}); */

	$("#submitBtn")
			.click(
					function() {

						//document.getElementById('info').innerHTML = "";
						//document.getElementById('info').innerHTML = "";
						var myTab = document.getElementById('makeEditable');

						// LOOP THROUGH EACH ROW OF THE TABLE AFTER HEADER.
						var isError = 0;
						
						for (i = 1; i < myTab.rows.length; i++) {
							var str = "";
							// GET THE CELLS COLLECTION OF THE CURRENT ROW.
							var objCells = myTab.rows.item(i).cells;

							//str = str + '||' + objCells.item(j).innerHTML;
							for (var j = 0; j < objCells.length; j++) {
								//info.innerHTML = info.innerHTML + '||' + objCells.item(j).innerHTML;

								if (objCells.item(j).innerHTML
										.indexOf("select") != -1) {

									if(j === 9){
										if ($('#selectPartitionedFolders' + i + ' :selected')) {
											
											var pf_selectElement = $(
													'#selectPartitionedFolders' + i
															+ ' :selected').text();

											str = str + '||' + pf_selectElement;
										}
										
									} else {
										var selectElement;
	
										//	alert("check ->"+$('#selectDayOfChk'+i+' :selected').text());
	
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

						//	alert("jsonStr--->" + json_str);

							var data_1 = {
								singleRowData : json_str
							};
							$.ajax({

								type : "POST",
								url : "updateFileMonitoringById",
								data : data_1,
								headers: { 'token':$("#token").val()},
								datatype : 'json',

									success : function(message) {
										
									
									},
										
									error :function(xhr, textStatus, errorThrown) 
			                		{
			                			isError = 1;	
			                 		}
								
								
							});

						}

				
						if(isError == 1){
							
							toastr.info('There was a problem.');
							
							 
						}else{
							
							toastr.info('Updated Successfully');
			        	      setTimeout(function(){
			        	    	  window.location.href= "validationCheck_View";
			              },1000); 
							
							 
						}
							
						//	alert("table rows len =>" + myTab.rows.length)

					});
</script>


<!--  var j_obj = $.parseJSON(message);
									
									//	alert("j_obj ->"+j_obj);
									   
									  // 	alert("In json......");
									   
								         if (j_obj.hasOwnProperty('success')) {
								        	 
								        	// alert("In json......");
								        	 
								        	 toastr.info('Updated Successfully');
								        	      setTimeout(function(){
								        	    	  window.location.href= "validationCheck_View";
								              },1000); 
								         } else {
								             if (j_obj.hasOwnProperty('fail')) {
								            	 toastr.info('There was a problem.');
								             }
								         } -->
<script type="text/javascript">


	function deleteRow(id, idApp) {
		
		  var form_data = {
				  id : id,
				  idApp: idApp
			};
	        
	    	$.ajax({
	    		type : 'GET',	        		
	    		url : "./deleteFileMonitorRuleById",
	    		headers: { 'token':$("#token").val()},
	    		data : form_data,
	            success : function(message) {
					
				
					 toastr.info('Deleted Successfully');
					 
					 setTimeout(function(){ location.reload(); }, 1000);
					 
				},
				
		 		 error: function (xhr, textStatus, errorThrown) {
		 			 
		 			toastr.info('There was a problem.');
		 			
         		 }
				
				
				
		 });
	
	
			
			/*  setTimeout(function(){
				 window.location.reload();
         },1000);  */
			
	}

</script>
<script type="text/javascript">
/* 
 	 $('a').click(function(){
 	
		 $.ajax({

			type : "POST",
			url : "deleteFileMonitorRuleById",
			headers: { 'token':$("#token").val()},
			datatype : 'json',

			success : function(message) {
				
				 var j_obj = $.parseJSON(message);
			
			//	alert("j_obj ->"+j_obj);
			   
			  // 	alert("In json......");
			   
		         if (j_obj.hasOwnProperty('success')) {
		        	 
		        	// alert("In json......");
		        	 
		        	 toastr.info('Deleted Successfully');
		        	      setTimeout(function(){
		        	    	  window.location.href= "validationCheck_View";
		              },1000); 
		         } else {
		             if (j_obj.hasOwnProperty('fail')) {
		            	 toastr.info('There was a problem.');
		             }
		         }
			},
				
				error :function(xhr, textStatus, errorThrown) 
    			{
    				 toastr.info('There was a problem.');
     			}
		
		});
	}); 
	 */
	
 
/*  $("#deleteLink")
	.click(
			function() {
 
				
				alert("In del link");
 $.ajax({
		
		
		
		type : "POST",
		url : "deleteFileMonitorRuleById",
		datatype : 'json',
		headers: { 'token':$("#token").val()},
		data : form_data,
		
		success : function(message) {
		
		 var j_obj = $.parseJSON(message);
	
		alert("j_obj ->"+j_obj);
	   
	   	alert("In json......");
	   
         if (j_obj.hasOwnProperty('success')) {
        	 
        	 alert("In json......");
        	 
        	 toastr.info('Deleted Successfully');
        	      setTimeout(function(){
        	    	  window.location.href= "editFileMonitoring";
              },1000); 
         } else {
             if (j_obj.hasOwnProperty('fail')) {
            	 toastr.info('There was a problem.');
             }
         }
		}
		
	});
			}); */
 </script>



<!-- <script type="text/javascript">
	function deleteRow(btn) {
		var row = btn.parentNode.parentNode;
		row.parentNode.removeChild(row);
	}
	
	
	   var j_obj = $.parseJSON(message);
                     if (j_obj.hasOwnProperty('success')) {
                    	 toastr.info('New User Created Successfully');
                    	      setTimeout(function(){
                    	    	  window.location.href= "viewUsers";
                          },1000); 
                     } else {
                         if (j_obj.hasOwnProperty('fail')) {
                        	 toastr.info('There was a problem.');
                         }
                     }
	
	
</script> -->

</head>