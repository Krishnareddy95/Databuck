<%@page import="com.databuck.bean.DBKFileMonitoringRules"%>
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
                            <span class="caption-subject bold ">File Monitoring :
                                ${name} </span>
                        </div>
                    </div>
					<div class="portlet-body form">
						<!-- <form action="saveFileMonitoringRule" method="POST"
							accept-charset="utf-8"> -->

						<input type="hidden" id="fileDetails" value=fileDetails
							name="fileDetails">
						<input type="hidden" id="idApp" value="${idApp}"
                            name="idApp">
                        <input type="hidden" id="connectionId" value="${connectionId}"
                            name="connectionId">

						<div class="form-body">
							<div class="row">
								<div class="form-group form-md-line-input">

									<table class="table table-striped table-bordered table-hover dataTable no-footer" style="width: 100%;" id="makeEditable">
										<thead>
										   <th class="id_hide"> Id </th>
                                           <th> Connection Id </th>
                                           <th> Schema Name </th>
                                           <th> Table Name </th>
                                           <th> FileIndicator </th>
                                           <th> DayOfWeek </th>
                                           <th> HourOfDay </th>
                                           <th> Expected Time </th>
                                           <th> Expected File Count </th>
                                           <th> StartHour </th>
                                           <th> EndHour </th>
                                           <th> Frequency </th>
                                           <th></th>
										</thead>
										 <%
                                            boolean Delete = false;
                                            Delete = RBACController.rbac("Data Template", "D", session);

                                        %>
										<tbody id="dataRows">

											<%
										    	session.setAttribute("idAppVal",  request.getAttribute("idApp"));

												// Iterating through subjectList

												ArrayList<DBKFileMonitoringRules> arrCsv = (ArrayList) request.getAttribute("dbkFileMonitoringRules");
												//	out.print(arrCsv);

												DBKFileMonitoringRules fileDetails;

												if (arrCsv != null) { // Null check for the object
													//	out.print("In IF.........");

													Iterator<DBKFileMonitoringRules> iterator = arrCsv.iterator(); // Iterator interface
													int i = 1;

													while (iterator.hasNext()) // iterate through all the data until the last record
													{

														fileDetails = iterator.next(); //assign individual employee record to the employee class object
														session.setAttribute("idAppVal", fileDetails.getValidationId());
											%>


											<tr>
                            <td name="Id" class="id_hide" contenteditable="false"><%=fileDetails.getId()%></td>
                            <td name="connectionId"  contenteditable="false"><%=fileDetails.getConnectionId()%></td>
                            <td name="schemaName"  contenteditable="true"><%=fileDetails.getSchemaName()%></td>
                            <td name="tableName"  contenteditable="true"><%=fileDetails.getTableName()%></td>
                            <td ><SELECT name="fileIndicator" class="form-control" contenteditable="true">
                                 <option value="hourly"
                                 <%if ((fileDetails.getFileIndicator()).equalsIgnoreCase("Hourly")) {%>selected <%}%>>Hourly</option>

                                 <option value="frequency"
                                 <%if ((fileDetails.getFileIndicator()).equalsIgnoreCase("Frequency")) {%> selected <%}%>>Frequency</option>

                                 </SELECT></td>
                            <td ><SELECT name="dayOfWeek" class="form-control"  contenteditable="true">
                                    <option value="Monday"
                                      <%if ((fileDetails.getDayOfWeek()).equalsIgnoreCase("Monday")) {%> selected
                                      <%}%>>Monday</option>

                                    <option value="Tuesday"
                                      <%if ((fileDetails.getDayOfWeek()).equalsIgnoreCase("Tuesday")) {%> selected
                                      <%}%>>Tuesday</option>

                                    <option value="Wednesday"
                                      <%if ((fileDetails.getDayOfWeek()).equalsIgnoreCase("Wednesday")) {%> selected
                                      <%}%>>Wednesday</option>

                                    <option value="Thursday"
                                      <%if ((fileDetails.getDayOfWeek()).equalsIgnoreCase("Thursday")) {%> selected
                                      <%}%>>Thursday</option>

                                    <option value="Friday"
                                      <%if ((fileDetails.getDayOfWeek()).equalsIgnoreCase("Friday")) {%> selected
                                      <%}%>>Friday</option>

                                    <option value="Saturday"
                                      <%if ((fileDetails.getDayOfWeek()).equalsIgnoreCase("Saturday")) {%> selected
                                      <%}%>>Saturday</option>

                                    <option value="Sunday"
                                      <%if ((fileDetails.getDayOfWeek()).equalsIgnoreCase("Sunday")) {%> selected
                                      <%}%>>Sunday</option>
                                </SELECT></td>
                            <td name="hourOfDay"  contenteditable="true"><%=fileDetails.getHourOfDay()==null?"":fileDetails.getHourOfDay()%></td>
                            <td name="expectedTime"  contenteditable="true"><%=fileDetails.getExpectedTime()==null?"":fileDetails.getExpectedTime()%></td>
                            <td name="expectedFileCount"  contenteditable="true"><%=fileDetails.getExpectedFileCount()==null?"":fileDetails.getExpectedFileCount()%></td>
                            <td name="startHour"  contenteditable="true"><%=fileDetails.getStartHour()==null?"":fileDetails.getStartHour()%></td>
                            <td name="endHour"  contenteditable="true"><%=fileDetails.getEndHour()==null?"":fileDetails.getEndHour()%></td>
                            <td name="frequency"  contenteditable="true"><%=fileDetails.getFrequency()==null?"":fileDetails.getFrequency()%></td>


												<%
													if (Delete) {
												%>

												<td><button onclick="deleteRow(<%=fileDetails.getId()%>, <%=fileDetails.getValidationId()%>)"
                                                     style="background-color: Transparent; background-repeat: no-repeat; border: none; cursor: pointer; overflow: hidden; outline: none;">
                                                    <i style="margin-left: 20%; color: red"
                                                        class="fa fa-trash"></i>
                                                </button></td>


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
								<button type="submit" id="updateBtn" class="btn blue">Update</button>
							</div>


						</div>
						<div id="displayDiv" style="display: none">
							<h3>JSON Data returned from Server after processing</h3>
							<div id="processedData"></div>
						</div>
						<!-- 	</form>
 -->
					</div>

				</div>


			</div>

		</div>
	</div>
</div>

<jsp:include page="footer.jsp" />
<head>

<script
	src="./assets/global/plugins/jquery.dataTables.min.js"
	type="text/javascript">
</script>


<script
	src="./assets/global/plugins/bootstrap-multiselect.js"></script>
<link rel="stylesheet"
	href="./assets/global/plugins/bootstrap-multiselect.css">

<script type="text/javascript" src="./assets/global/plugins/jsapi.js"></script>
<script src="./assets/js/timepicker.min.js"></script>

<script type="text/javascript">

$(document).ready(function() {

    $("#id_hide").hide();
    $(".id_hide").hide();

	$('.table').DataTable({
    		  	"scrollX": true
    	});

});
</script>
<script>
	$('#makeEditable').SetEditable({
		$addButton : $('#but_add')
	});
</script>

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
						
						 $('#dataRows tr').each(function(index, item) {
						        var $item = $(item);
						        if($item.find("td:eq(0)").text() == 'No data available in table'){
						        	$(item).remove();
						        	row_len -1;
						        }
						 });

						var i = row_len;
						var connectionId = $("#connectionId").val();

						table
								.append('<tr>'
										+ '<td class="id_hide" contenteditable="true" >-1</td>'
										+ '<td contenteditable="false" >'+connectionId+'</td>'
										+ '<td contenteditable="true" ></td>'
										+ '<td contenteditable="true" ></td>'
										+ '<td><SELECT name="fileIndicator" class="form-control" contenteditable="true">'
                                            + '<option value="hourly">Hourly</option>'
                                            + '<option value="frequency">Frequency</option></SELECT></td>'
										+ '<td ><SELECT name="dayOfWeek" class="form-control"  contenteditable="true">'
										+ '<option value="Monday">Monday</option>'
										+ '<option value="Tuesday">Tuesday</option>'
										+ '<option value="Wednesday">Wednesday</option>'
										+ '<option value="Thursday">Thursday</option>'
										+ '<option value="Friday">Friday</option>'
										+ '<option value="Saturday">Saturday</option>'
										+ '<option value="Sunday">Sunday</option></SELECT></td>'
										+ '<td contenteditable="true" ></td>'
                                        + '<td contenteditable="true" ></td>'
                                        + '<td contenteditable="true" ></td>'
                                        + '<td contenteditable="true" ></td>'
                                        + '<td contenteditable="true" ></td>'
                                        + '<td contenteditable="true" ></td>'
										+'<td><button onclick="deleteNewRow(this)" style="background-color: Transparent; background-repeat: no-repeat; border: '
                                        +' none; cursor: pointer; overflow: hidden; outline: none;">  <i style="margin-left: 20%; color: red"'
                                        +'class="fa fa-trash"></i> </button></td>'
										+ '</tr>');
							$(".id_hide").hide();
					});


$('#updateBtn').click(function() {
    var myObjects = [];
    var idApp = $("#idApp").val()
    
    $('#dataRows tr').each(function(index, item) {
        var $item = $(item);
        if($item.find("td:eq(0)").text() != 'No data available in table'){
	        myObjects.push({
	            id: $item.find("td:eq(0)").text(),
	            connectionId: $item.find("td:eq(1)").text(),
	            validationId: idApp,
	            schemaName: $item.find("td:eq(2)").text(),
	            tableName: $item.find("td:eq(3)").text(),
	            fileIndicator: $item.find("td:eq(4)").find("select").val(),
	            dayOfWeek: $item.find("td:eq(5)").find("select").val(),
	            hourOfDay: $item.find("td:eq(6)").text(),
	            expectedTime: $item.find("td:eq(7)").text(),
	            expectedFileCount: $item.find("td:eq(8)").text(),
	            startHour: $item.find("td:eq(9)").text(),
	            endHour: $item.find("td:eq(10)").text(),
	            frequency: $item.find("td:eq(11)").text(),
	        });
        }
    });
          
    $.ajax({
        url: './editDBKFileMonitoringRule',
        headers: { 'token':$("#token").val()},
        method: 'POST',
        contentType : 'application/json; charset=utf-8',
        data: JSON.stringify(myObjects)
    })
    .done(function(myObjects) {
        // handle success
        toastr.info('Submitted Successfully');
                setTimeout(
                    function() {
                      window.location.href = "validationCheck_View";
                    }, 1000);

    })
    .fail(function() {
        // handle fail
        toastr.info('There was a problem.');
    });
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
	function deleteNewRow(btn) {
		var row = btn.parentNode.parentNode;
		row.parentNode.removeChild(row);

	//	$(this).parents("tr").remove();
	}
</script>

<script type="text/javascript">

	function deleteRow(id, idApp) {

		  var form_data = {
				  id : id,
				  idApp: idApp
			};

	    	$.ajax({
	    		type : 'GET',
	    		url : "./deleteDBKFileMonitorRule",
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

	}

</script>
<script type="text/javascript">

</head>