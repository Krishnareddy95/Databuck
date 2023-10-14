<%@page import="com.databuck.service.RBACController"%>
<%@page import="com.databuck.bean.ListApplications"%>
<%@page import="java.util.List"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<jsp:include page="header.jsp"/>
<jsp:include page="checkVulnerability.jsp" />
<jsp:include page="container.jsp"/>
<script src="./assets/global/plugins/jquery.min.js"></script>

            <!-- BEGIN CONTENT -->
<div class="page-content-wrapper">
                <!-- BEGIN CONTENT BODY -->
    <div class="page-content">
    <!-- BEGIN PAGE TITLE-->
    <!-- END PAGE TITLE-->
    <!-- END PAGE HEADER-->
   
        <div class="row">
            <div class="col-md-12">
                <!-- BEGIN EXAMPLE TABLE PORTLET-->
                <div class="portlet light bordered">
                    <div class="portlet-title">
                        <div class="caption font-red-sunglo">
                            <span class="caption-subject bold ">Export Validation Checks </span>
                        </div>
                    </div>
                    <form id="frm-example" action="exportCSVFileData" method="post">		
                    <div class="portlet-body">
                    
                    <div class="form-actions noborder align-center">
						<p align="right">
						<!-- onclick="getValue() -->
								<button type="submit" id="exportBtn" class="btn blue" onclick="exportFile()">Export</button>
								
							
							<input type="hidden" id="idDataIds" value=idDataIds name="idDataIds">
							<input type="hidden" id="fileName" value=fileName name="fileName">
							<input type="hidden" id="type" value=VC name="type">
						</p>
						</div>
                        <!--<table id="example" class="table table-striped table-bordered table-hover dataTable no-footer datatable-master" style="width: 100%;">-->
                        <table id="example" class="table table-striped table-bordered table-hover no-footer" style="width: 100%;">
                            <thead>
                                <tr>
                                <!-- <th><input type="checkbox" id="selectAll"/> Select All</th> -->
                                <th><input type="checkbox" id="selectAll"/> Select All</th>
                                <th>IdApp</th>
                                    <th>Validation Check Name</th>
                                    <th>Data Template Name </th>
                                    <th>App Type </th>
                                    <th style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">Created At</th>
                                    <%
											boolean customize = false;
                                   			 customize = RBACController.rbac("Validation Check", "U", session);
                                   			 //System.out.println("customize="+customize);
												if (customize) {
									%>
                                    <th> </th>
                                    <%} %>
                                    <%
											boolean view = false;
                                    view = RBACController.rbac("Validation Check", "R", session);
												if (view) {
									%>
                                    <th> </th>
                                     <%} %>
                                    <%
											boolean delete = false;
                                    delete = RBACController.rbac("Validation Check", "D", session);
												if (delete) {
									%>
                                    <th> </th>
                                     <%} %>
                                     <th>Copy</th>
                                </tr>
                            </thead>
                            <tbody>
                               <c:forEach var="listappslistdsobj" items="${listappslistds}">
                                    <tr>
                                    <td>
    	  								<input type="checkbox" class="md-check accessControlCheck" name="chkIdDataSchema" value="${listappslistdsobj.idApp} ">    	  		
    	  							</td>
                                        <td>
                                         ${listappslistdsobj.idApp}
                                        </td>
                                        <td>
                                            ${listappslistdsobj.laName}
                                        </td>

                                        <td>
                                             ${listappslistdsobj.lsName}
                                        </td>
                                         <td>
                                             ${listappslistdsobj.appType}
                                        </td>
                                        <td style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">
                                            ${listappslistdsobj.createdAt}
                                        </td>   
										<%if (customize) { %>
                                        <td>
                                           <a onClick="validateHrefVulnerability(this)"  href="customizeValidation?idApp=${listappslistdsobj.idApp}&laName=${listappslistdsobj.laName}&idData=${listappslistdsobj.idData}&lsName= ${listappslistdsobj.lsName}">Customize</a> 
                                        </td> 
                                        <%} %>
                                        <%if (view) { %>
                                        <td>
                                           <a onClick="validateHrefVulnerability(this)"  href="dataSourceDisplayAllView?idData=${listappslistdsobj.idData}">Source</a> 
                                        </td> 
                                          <%} %>
                                          <%if (delete) { %>
                                        <td>
                                           <a onClick="validateHrefVulnerability(this)"  href="dataApplicationDeleteView?idApp=${listappslistdsobj.idApp}&laName=${listappslistdsobj.laName}&lsName=${listappslistdsobj.lsName}"><i style="margin-left: 20%;color:red" class="fa fa-trash"></i></a> 
                                        </td> 
  <%} %>

                                         <td>
									
										 <button class="btn-link" onclick="copy('${listappslistdsobj.laName}', ${listappslistdsobj.idApp})">Copy</button> 
										</td>
                                       
                                    </tr>

                                </c:forEach>
                            </tbody>
                            
                        </table>
                    </div>
                   
                    </form>
                </div>
                <!-- END EXAMPLE TABLE PORTLET-->
            </div>
            
        </div>
          <!--   <div class="note note-info" style="width:90%;">
                <h3>No Validation Checks found!</h3> -->
    
            </div>
    </div>

    <!-- <script>
            $(document).ready(function() {
            	$('#frm-example').ajaxForm({
            		headers: {'token':$("#token").val()}
            	});
            });
    </script> -->

     <script>

    
     
     
     
function copy(validationName, idApp) {
	 
	
	
	 var name = validationName.substr(validationName.indexOf("_") + 1);
	var person = prompt("Please Enter New Validation Name", name);


   
    if (person != null) {
      
        var form_data = {
        		newValidationName : person,
    			idApp: idApp
		};
        alert(idApp);
        alert(person);
    	$.ajax({
    		type : 'GET',	        		
    		url : "./copyValidation",
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
	
	
	  
	
//	alert("fileName =>" +fileName);
	
/* 	 $("#selectAll").click(function(){
	
	alert("select_all");
	
	 $('input:checkbox').prop('checked', this.checked);
	 
	 idDataIds.push($(this).val());
	 
	 alert("In select all........."+idDataIds);
}); */
	
	  $.each($("input[name='chkIdDataSchema']:checked"), function(){            
      	idDataIds.push($(this).val());
      });
	  
	 
	  
//	alert("Selected checkbox ids: " + idDataIds.join(", "));
   
   // $('input[name="idDataIds"]').val( idDataIds.join(", "));
    $('input[name="idDataIds"]').val( idDataArray.join(", "));
    $('input[name="fileName"]').val(fileName);
    
  //  alert("FileName =============>"+fileName);
    
    $.ajax({
        url: './exportCSVFileData',
        type: 'POST',
        headers: { 'token':$("#token").val()},
        datatype: 'json',
        data: form_data
    
	})
   
    
 }

 // select all checkboxes
 
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

<!-- New Script for checkbox selected IDs-- -->

<script type="text/javascript">

$(document).ready(function() {
   var table = $('#example').DataTable({     
      'columnDefs': [
         {
            'targets': 0,
            'checkboxes': {
               'selectRow': true
            }
         }
      ],
      'select': {
         'style': 'multi'
      },
      "scrollX": true,
      destroy: true, 
      'order': [[1, 'asc']]
   });
});

$('#frm-example').on('submit', function(e){
    var form = this;
    
    alert("#frm-example");
    
    var rows_selected = table.
    // Iterate over all selected checkboxes
    $.each(rows_selected, function(index, rowId){
       // Create a hidden element 
       $(form).append(
           $('<input>')
              .attr('type', 'hidden')
              .attr('name', 'id[]')
              .val(rowId)
       );
    });
    // Output form data to a console     
    $('#example-console-rows').text(rows_selected.join(","));
    
    // Output form data to a console     
    $('#example-console-form').text($(form).serialize());
});
 
</script>

<!-- End of Script for checkbox selected IDs-- -->

    <!-- END CONTAINER -->
<jsp:include page="footer.jsp"/>