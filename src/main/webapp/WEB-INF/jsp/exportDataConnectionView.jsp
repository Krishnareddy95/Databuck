
<%@page import="com.databuck.service.RBACController"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
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
							<span class="caption-subject bold ">Export Data Connections </span><br />
							<span class="caption-subject bold "> ${message} </span> <br>
							<font size="4" color="red"></font>
						</div>
					</div>
					
					
					<form id="tokenForm1" action="exportCSVFileData" method="post">
					<div class="portlet-body">
				<div class="form-actions noborder align-center">
						<p align="right">
						<!-- onclick="getValue() -->
								<button type="submit" id="exportBtn" class="btn blue" onclick="exportFile()">Export</button>
							
							<input type="hidden" id="idDataIds" value=idDataIds name="idDataIds">
							<input type="hidden" id="fileName" value=fileName name="fileName">
							<input type="hidden" id="type" value=CN name="type">
						</p>
						</div>
						<!--  <input type="hidden" id="idDataIds" value="idDataIds"  name="idDataIds" /> -->
						  <table id="example" class="table table-striped table-bordered table-hover no-footer" 
									style="width: 100%;"> 
										
				
							<thead>
								<tr>
								 <th><input type="checkbox" id="selectAll"/> Select All</th>
									
									<th>Connection Id</th>
									<th>Connection Name</th>
									<th>Connection Type</th>
									<th>Host</th>
									<th>Schema</th>
									<th>Username</th>
									<th>Port</th>
									<%
										boolean flag = false;
										flag = RBACController.rbac("Data Connection", "U", session);
										//System.out.println("flag=" + flag);
										if (flag) {
									%>
									<th>Edit</th>
									<%
										}
									%>
									<%
										boolean deleteDC = false;
										deleteDC = RBACController.rbac("Data Connection", "D", session);
										//System.out.println("flag=" + flag);
										if (deleteDC) {
									%>
									<th>Delete</th>
									<%
										}
									%>
								</tr>
							</thead>
							<tbody>
							
								<c:forEach var="listdataschema" items="${listdataschema}">
									<tr>
									
									
													
								 <td>
    	  								<input type="checkbox" class="md-check accessControlCheck" name="chkIdDataSchema" value="${listdataschema.idDataSchema} ">    	  		
    	  							</td>
										
										<td>${listdataschema.idDataSchema}</td>
										<td>${listdataschema.schemaName}</td>
										<td>${listdataschema.schemaType}</td>
										<td>${listdataschema.ipAddress}</td>
										<td>${listdataschema.databaseSchema}</td>
										<td>${listdataschema.username}</td>
										<td>${listdataschema.port}</td>



										<%
											if (flag) {
										%>
										<td><a onClick="validateHrefVulnerability(this)" 
											href="editConnection?id=${listdataschema.idDataSchema}"><i
												style="margin-left: 20%;" class="fa fa-edit"></i></a></td>
										<%
											}
										%>

										<%
											if (deleteDC) {
										%>
										<td><a onClick="validateHrefVulnerability(this)" 
											href="deleteConnection?id=${listdataschema.idDataSchema}"
											data-toggle="confirmation" data-singleton="true"><i
												style="margin-left: 20%; color: red" class="fa fa-trash"></i></a></td>
										<%
											}
										%>
										
										
									
									</tr>
								
								</c:forEach>
							
							</tbody>
						
						</table>
					</div>
					</form>
				</div>
				<!--       *************END EXAMPLE TABLE PORTLET*************************-->

			</div>
		</div>
	</div>
</div>



<!--********     BEGIN CONTENT ********************-->

<jsp:include page="footer.jsp" />
<!--<script>
        $(document).ready(function() {
        	$('#tokenForm1').ajaxForm({
        		headers: {'token':$("#token").val()}
        	});
        });
</script>-->
<script type = "text/javascript">


$(document).ready(function() {
	var table;
	//var table1;
	
	table = $('#example').dataTable({
		  	order: [[ 0, "desc" ]],
	      //By Mamta 9-3-2022 added to make datatable scroll horizontally
 	        "scrollX": true
	      });
	

});


/* $(document).ready(function() {
    $("button").click(function(){
    	var idDataIds = [];
    
    	var form_data = {
    			idDataIds: idDataIds.join(", "),
              
            };
    	$.ajax({
            url: './exportDataConnectionView',
            type: 'POST',
            headers: { 'token':$("#token").val()},
            datatype: 'json',
            data: form_data
    	})
    	
        
        $.each($("input[name='chkIdDataSchema']:checked"), function(){            
        	idDataIds.push($(this).val());
        });*/
    //  alert("Selected checkbox ids: " + idDataIds.join(", "));
     /* $('input[name="idDataIds"]').val( idDataIds.join(", "));
       
      });
   
});
 */
 
 
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
	
	  $.each($("input[name='chkIdDataSchema']:checked"), function(){            
      	idDataIds.push($(this).val());
      });
	  
	 
	  
//	alert("Selected checkbox ids: " + idDataIds.join(", "));
   
   // $('input[name="idDataIds"]').val( idDataIds.join(", "));
    $('input[name="idDataIds"]').val( idDataArray.join(", "));
    $('input[name="fileName"]').val(fileName);
    
   // alert("FileName =============>"+fileName);
    
    $.ajax({
        url: './exportCSVFileData',
        type: 'POST',
        headers: { 'token':$("#token").val()},
        datatype: 'json',
        data: form_data,
        success : function(message){
			
			// alert("In .........");
		} 
	})
   
    
 }

	
 /* function getValue() {
    var retVal = prompt("Enter your name : ", "your name here");
    document.write("You have entered : " + retVal);
 } 
 */
</script>
<script type="text/javascript">

 $("#selectAll").click(function(){
	
//	alert("select_all");
	
	 $('input:checkbox').prop('checked', this.checked);
	 
	 
}); 
 
/*  $.each($("input[name='chkIdDataSchema']:checked"), function(){            
   	idDataIds.push($(this).val());
   });
	   */

</script>
