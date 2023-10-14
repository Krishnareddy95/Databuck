
<%@page import="com.databuck.service.RBACController"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!-- <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script> -->
<script src="./assets/global/plugins/jquery.min.js"></script>
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
							<span class="caption-subject bold "> Export Data Templates
							</span>
						</div>

					</div>


					<!--  DC-148 amol  -->
					<!-- <form id="tokenForm1" action="exportCSVFileData" method="post" >-->
					<div class="portlet-body">
						<div class="form-actions noborder align-center">
							<p align="right">
								<!-- onclick="getValue() -->
								<button type="submit" id="exportBtn" class="btn blue"
									onclick="exportFile()">Export</button>

								<input type="hidden" id="idDataIds" value=idDataIds
									name="idDataIds"> <input type="hidden" id="fileName"
									value=fileName name="fileName"> <input type="hidden"
									id="type" value=DT name="type">
							</p>
						</div>

						<table id="dataTemplateViewTable"
							class="table table-striped table-bordered table-hover no-footer"
							style="width: 100%;">

							<thead>

								<tr>
									<th><input type="checkbox" id="selectAll" /> Select All</th>
									<th>Template Id</th>
									<th>Name</th>
									<!-- <th>Description</th> -->
									<th>Location</th>
									<th>TableName</th>
									<!-- 	<th>Source</th> -->
									<th
										style="max-width: 75px !important; width: 75px !important; min-width: 75px !important">Created
										At</th>
									<%
										boolean flag = false;
										flag = RBACController.rbac("Data Template", "R", session);
										//System.out.println("flag=" + flag);
										if (flag) {
									%>
									<th>View</th>
									<th>Edit</th>
									<%
										}
									%>
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
									<th>Copy</th>
								</tr>
							</thead>
							<tbody>

								<c:forEach var="listdatasource" items="${listdatasource}">


									<tr>
										<td><input type="checkbox"
											class="md-check accessControlCheck" name="chkIdDataSchema"
											value="${listdatasource.idData} "></td>

										<td>${listdatasource.idData}</td>
										<td>${listdatasource.name}</td>
										<%-- <td>${listdatasource.description}</td> --%>
										<td>${listdatasource.dataLocation}</td>
										<td>${listdatasource.tableName}</td>
										<%-- <td>${listdatasource.dataSource}</td> --%>
										<td
											style="max-width: 75px !important; width: 75px !important; min-width: 75px !important">${listdatasource.createdAt}</td>
										<%
											if (flag) {
										%>
										<td><a onClick="validateHrefVulnerability(this)"
											href="listdataview?idData=${listdatasource.idData}&dataLocation=${listdatasource.dataLocation}&name=${listdatasource.name}&description=${listdatasource.description}">View</a>
										</td>


										<%
											if (flag) {
										%>
										<td><a onClick="validateHrefVulnerability(this)"
											href="editDataTemplate?idData=${listdatasource.idData}"><i
												style="margin-left: 20%;" class="fa fa-edit"></i></a></td>
										<%
											}
										%>


										<%
											}
										%>
										<%
											if (Delete) {
										%>
										<td><a onClick="validateHrefVulnerability(this)"
											href="deletedatasource?idData=${listdatasource.idData}">
												<i style="margin-left: 20%; color: red" class="fa fa-trash"></i>
										</a></td>
										<%
											}
										%>
										<td>

											<button class="btn-link"
												onclick="copy('${listdatasource.name}', ${listdatasource.idData})">Copy</button>
										</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
					<!-- </form> DC-148 -->
				</div>
				<!--       *************END EXAMPLE TABLE PORTLET*************************-->

			</div>
		</div>
	</div>
</div>

<!-- <script>
        $(document).ready(function() {
        	$('#tokenForm1').ajaxForm({
        		headers: {'token':$("#token").val()}
        	});
        });
 </script> -->
<script>

$(document).ready(function() {
	var table;
	//var table1;
	
	table = $('#dataTemplateViewTable').dataTable({
		  	order: [[ 0, "desc" ]],
		  	 "scrollX": true
	      });
	

});

function copy(templateName, idData) {
	var templateName =templateName ;
	var copyTemplateName = prompt("Please Enter New Template Name", templateName);
	var isNameValidToSave = true; 
	var person = prompt("Please Enter New Template Name", templateName);
	do
  	{
		if(copyTemplateName.trim() == ''){
			alert("Template Name should not be blank..");
			var copyTemplateName = prompt("Please Enter New Template Name", templateName);
		}
		if(copyTemplateName == templateName){
			alert("Template Name should not be same...");
			var copyTemplateName = prompt("Please Enter New Template Name", templateName);
		}
		if(!checkPromptText(copyTemplateName)){
			isNameValidToSave = false;
			alert('Vulnerability in submitted data, not allowed to submit!');
		}
  	}while(copyTemplateName.trim() == '');


    if (copyTemplateName != null && isNameValidToSave) {
      
        var form_data = {
        		newTemplateName : copyTemplateName,
    			idData: idData
		};
        
    	$.ajax({
    		type : 'GET',	        		
    		url : "./copyTemplate",
    		data : form_data,
    		success : function(message){
    			
    			 window.location.reload();
    		} 
    	});
    }
    
}
var idDataArray = [];

$("input[name='chkIdDataSchema']").click( function(){ 
	if($(this).prop("checked") == true){
		idDataArray.push($(this).val());
    }
	else if($(this).prop("checked") == false){
		var indexIdData = idDataArray.indexOf($(this).val());
		idDataArray.splice(indexIdData, 1);
    }
});

function exportFile() {
	 
var fileName = prompt("Enter File Name", "Your FileName here");
	
	var idDataIds = [];
	
//	alert("fileName =>" +fileName);
	
	if(fileName===("Your FileName here")){
		alert("File name can not be left blank");
		var fileName = prompt("Enter File Name", "Your FileName here");
	}
	else if(fileName==""){
		alert("file name can not be left blank");
		var fileName = prompt("Please EnteFile Name", "Your FileName here");
	}
	var idDataIds = [];
	
	
	// $("input[name='chkIdDataSchema']:checked").each(function () {
	 // $.each($("input[name='chkIdDataSchema']:checked"), function(){    
		// $("input[type='checkbox']:checked").each(function() { 
			$.each($("input[name='chkIdDataSchema']:checked"), function(){ 
      	idDataIds.push($(this).val());
      	//alert(" checkbox ids: " + $(this).val());
      });
	  
	  
	  
	//alert("Selected checkbox ids: " + idDataIds.join(", "));
   
  // $('input[name="idDataIds"]').val( idDataIds.join(", "));
   $('input[name="idDataIds"]').val( idDataArray.join(", "));
    $('input[name="fileName"]').val(fileName);
    
    var form_data1 = {
    		newTemplateName : fileName,
			idData: idDataIds
	};
  //  alert("FileName =============>"+fileName);
    
    $.ajax({
        url: './exportCSVFileData',
        type: 'POST',
        headers: { 'token':$("#token").val()},
        datatype: 'json',
        data: form_data1
	});
   
 }

</script>

<script type="text/javascript">

 $("#selectAll").click(function(){
	
	//alert("select_all");
	
	 $('input:checkbox').prop('checked', this.checked);
	 
	 
}); 
 
/*  $.each($("input[name='chkIdDataSchema']:checked"), function(){            
   	idDataIds.push($(this).val());
   });
	   */

</script>
<!--********     BEGIN CONTENT ********************-->

<jsp:include page="footer.jsp" />