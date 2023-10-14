
<%@page import="com.databuck.service.RBACController"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<jsp:include page="header.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<jsp:include page="container.jsp" />
<style>
 	.blueClass{
      background-color:#87CEFA;
      }
	table.dataTable tbody th,
	table.dataTable tbody td {
		white-space: nowrap;
	}

	.dataTables_scrollBody{
            overflow-y: hidden !important;
   	 	}
</style>

 <link rel="stylesheet"  href="./assets/css/custom-style.css" type="text/css"  />

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
							<span class="caption-subject bold "> Data Connections </span><br />
							<span class="caption-subject bold "> ${message} </span> <br>
							<font size="4" color="red"></font>
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
						  <table id="connectionTableId" class="table table-striped table-bordered table-hover dataTable no-footer"
									style="width: 100%;">
							<thead>
								<tr>
									<th>Connection Id</th>
									<th>Connection Name</th>
									<th>Connection Type</th>
									<th>Host</th>
									<th>Schema</th>
									<th>Username</th>
									<th>Port</th>
									<th>Domain Name</th>
									<th>Project Name</th>
									<th>Created By</th>
									<%
										boolean flag = false;
										flag = RBACController.rbac("Data Connection", "U", session);
										//System.out.println("flag=" + flag);

										boolean deleteDC = false;
										deleteDC = RBACController.rbac("Data Connection", "D", session);
										//System.out.println("flag=" + flag);

									%>
									<% %>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="listdataschema" items="${listdataschema}">
									<tr>
										<td>${listdataschema.idDataSchema}</td>
										<td>
                                                <c:choose>
                                                    <c:when test="${listdataschema.action eq 'Yes' && listdataschema.alation_integration_enabled eq 'Y'}">
                                                    ${listdataschema.schemaName} &nbsp;&nbsp;<a onClick="validateHrefVulnerability(this);publish_schema_summary_to_Alation(${listdataschema.idDataSchema})"  style="margin-right: 7px;" title="Publish to Alation" id="schemaSummaryAllation" ><i class="fa fa-upload" style="color:green" aria-hidden="true"></i></a>
                                                    </c:when>
                                                    <c:otherwise>
                                                    ${listdataschema.schemaName}
                                                    </c:otherwise>
                                                </c:choose>
                                            <div>
                                                <c:choose>
                                                	<c:when test="${listdataschema.action eq 'Yes'}">
                                                		<span class="label label-success label-sm">Active</span>
                                                	</c:when>
                                                	<c:otherwise>
                                                		<span class="label label-danger label-sm">Inactive</span>
                                                	</c:otherwise>
                                                </c:choose>

                                             	<%
                                             	    if (flag) {
                                             	%>
                                             	    <c:choose>
                                                        <c:when test="${listdataschema.action eq 'Yes'}">
                                                            <a onClick="validateHrefVulnerability(this)"
                                                                href="editConnection?id=${listdataschema.idDataSchema}">
                                                                <i style="margin-left: 10px" class="fa fa-edit"></i>
                                                            </a>
                                                        </c:when>
                                                        <c:otherwise/>
                                                   </c:choose>

                                                <%
   											        }
   										        %>

										<%
											if (deleteDC) {
										%>
										        <c:choose>
                                                    <c:when test="${listdataschema.action eq 'Yes'}">
                                                        <a onClick="validateHrefVulnerability(this)"
                                                            href="deleteConnection?id=${listdataschema.idDataSchema}"
                                                            data-toggle="confirmation" data-singleton="true"><i
                                                            style="margin-left: 10px; color: red" class="fa fa-trash"></i>
                                                        </a>
                                                    </c:when>
                                            	<c:otherwise/>
                                            </c:choose>
										<%
											}
										%>
										 <a onclick="copy('${listdataschema.schemaName}', ${listdataschema.idDataSchema})" style="margin-left: 15px;" title="Copy" id="anker-copy-{0}"><i class="fa fa-files-o" aria-hidden="true"></i></a>
										<%
										/*	if (flag) {
										%>
											<c:choose>
												<c:when test="${listdataschema.schemaType == 'S3 IAMRole Batch' || listdataschema.schemaType == 'S3 Batch' || listdataschema.schemaType == 'FileSystem Batch' || listdataschema.schemaType == 'S3 IAMRole Batch Config' }">
													<c:choose>
													<c:when test="${listdataschema.enableFileMonitoring == 'Y'}">
														<button class="btn-link" onclick="deactivateFileMonitoring(${listdataschema.idDataSchema})">Deactivate</button>
													</c:when>
													<c:otherwise>
														<button class="btn-link" onclick="activateFileMonitoring(${listdataschema.idDataSchema})">Activate</button>
													</c:otherwise>
													</c:choose>
												</c:when>
											</c:choose>
										<%
											} */
										%>
										</div>
                                        </td>
										<td>${listdataschema.schemaType}</td>
										<td>${listdataschema.ipAddress}</td>
										<td>${listdataschema.databaseSchema}</td>
										<td>${listdataschema.username}</td>
										<td>${listdataschema.port}</td>
										<td>${listdataschema.domainName}</td>
										<td>${listdataschema.projectName}</td>
										<td>${listdataschema.createdByUser}</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
				<!--       *************END EXAMPLE TABLE PORTLET*************************-->

			</div>
			<div id="propModal" class="modal" role="dialog" style="top: 200px;">
                <div class="modal-dialog" style="width:400px">
                    <!-- Modal content-->
                    <div class="modal-content">
                        <div class="modal-body blueClass">
                            <img alt="" src="./assets/img/reload.gif" width='32px' height='30px'
                            style="padding-right:10px;" /><b>Please wait while publishing is in progress..</b>
                        </div>
                    </div>
                </div>
            </div>
		</div>
	</div>
</div>



<!--********     BEGIN CONTENT ********************-->
<head>
<script src="./assets/global/plugins/jquery.min.js"
		type="text/javascript">
</script>
<script src="./assets/global/plugins/jquery.dataTables.min.js"
		type="text/javascript">
</script>
<script src="./assets/global/plugins/bootstrap-multiselect.js"></script>
<link rel="stylesheet"
	href="./assets/global/plugins/bootstrap-multiselect.css">
<link rel="stylesheet" href="./assets/jwf/content/JwfSpaInfra.css">
<script src="./assets/jwf/scripts/JwfSpaInfra.js"></script>
<script type="text/javascript" src="./assets/global/plugins/bootstrap-datepicker.min.js"></script>
<link rel="stylesheet" href="./assets/global/plugins/bootstrap-datepicker3.css" />
</head>
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
 <script>
$(document).ready(function() {

	var table;
	 
	table = $('.table').dataTable({
		  	
		  	order: [[ 0, "desc" ]],
		  	"scrollX": true
	      });

	$('#connectionTableId').on('page.dt', function() {
		  setTimeout(
		    function() {
		      $("[data-toggle=confirmation]").confirmation({container:"body",btnOkClass:"btn btn-sm btn-success",btnCancelClass:"btn btn-sm btn-danger",onConfirm:function(event, element) { element.trigger('confirm'); }});
		    }, 500);
	});

});

</script>
 <script>

function copy(connetionName, idDataSchema) {
	var isNameValidToSave = true;
	var newConnetionName1 = prompt("Please Enter New Connection Name", connetionName);
	do
	{
		
		if(newConnetionName1.trim() == ''){
			alert("Connection Name should not be blank..");
			var newConnetionName1 = prompt("Please Enter New Connection Name", connetionName);
		}
		if(newConnetionName1 == connetionName){
			alert("Connection Name should not be same...");
			var newConnetionName1 = prompt("Please Enter New Connection Name", connetionName);
		}
		
		if(!checkPromptText(newConnetionName1)){ 
			isNameValidToSave = false;
			alert('Vulnerability in submitted data, not allowed to submit!');
		}
	}while(newConnetionName1.trim() == '' || newConnetionName1 == connetionName);


	if (newConnetionName1 != null && isNameValidToSave) {
      
        var form_data = {
        		newConnectionName : newConnetionName1,
        		idDataSchema: idDataSchema
		};
        
    	$.ajax({
    		type : 'GET',	        		
    		url : "./copyConnection",
    		data : form_data,
    		success : function(message){
    			 var j_obj1 = $.parseJSON(message);
                 
                 if (j_obj1.hasOwnProperty('success')) {
                	 window.location.reload();
                 }
                 
                 if (j_obj1.hasOwnProperty('failure')) {
                	 toastr.info(j_obj1.failure);
                 }
    			 
    		} 
    	});
    }
    
	}

function activateFileMonitoring(idDataSchema) {
        var form_data = {
        		idDataSchema: idDataSchema
		};
        
    	$.ajax({
    		type : 'GET',	        		
    		url : "./activateFileMonitoring",
    		data : form_data,
    		success : function(message){
    			 window.location.reload();
    		} 
    	});
}


function deactivateFileMonitoring(idDataSchema) {
    var form_data = {
    		idDataSchema: idDataSchema
	};
    
	$.ajax({
		type : 'GET',	        		
		url : "./deactivateFileMonitoring",
		data : form_data,
		success : function(message){
			 window.location.reload();
		} 
	});
}

function publish_schema_summary_to_Alation(idDataSchema){

           var form_data = {
                                 idDataSchema: idDataSchema
                           };
                 $.ajax({
                         url: './publishSchemaSummaryToAlation',
                         type: 'POST',
                         headers: { 'token':$("#token").val()},
                         datatype: 'json',
                         data: form_data,
                         beforeSend: function () {
                                $(".modal").show();
                            },
                         success: function (data) {
                              try {
                                   var j_obj = jQuery.parseJSON(data);
                                   var status= j_obj.status;
                                   var msg= j_obj.message;
                                   toastr.info(msg);
                              } catch(e) {
                                   toastr.info('Schema summary failed to publish.');
                              }
                         },
                         error: function (textStatus) {
                              toastr.info('Schema summary failed to publish.');
                         },
                         complete: function(){
                           $(".modal").hide();
                           setTimeout(function() {
                                    window.location.reload();
                                },1000);
                        }
                     });
       }

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
	       
	       //-------------------------------
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
<script>

</script>