<%@page import="com.databuck.service.RBACController"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!-- <script
	src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script> -->
<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<style>

tbody{
	border-collapse: collapse;
        max-width: 100% !important;
        width: 100%;
        overflow: scroll;
        overflow-wrap: anywhere;
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
			<div class="col-md-6" style="width: 100%;">
				<!-- BEGIN SAMPLE FORM PORTLET-->
				<div class="portlet light bordered init">
					<div class="portlet-title">

						<div class="caption font-red-sunglo">
							<span class="caption-subject bold "> Import </span>
						</div>
					</div>
					<div class="portlet-body form">
						<form id="myForm" action="submitImportUiForm" enctype="multipart/form-data"
							method="POST" accept-charset="utf-8">
							<div class="form-body">

								<div class="row">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<select class="form-control" id="locationid" name="location"
												placeholder="Choose Location">
												<option value="-1">Select...</option>


												<!-- <option value="MSSQL">MS SQL</option> -->
												<option value="MySQL">MySQL</option>

											</select> <label for="form_control_1">Data Location *</label>
											<!-- <span class="help-block">Data Location  </span> -->

											<div class="form-body" style="display: none;" id="s4_form_id">
												<!--<div class="row">
												 <div class="col-md-6 col-capture" id="host-id-container">
													<div class="form-group form-md-line-input">
														<select class="form-control" id="schemaId"
															name="schemaId1">
														</select> <label for="form_control_1">Connection *</label>
													</div>
													<br /> <span class="required"></span>
												</div> -->

												<div class="row">
													<div class="col-md-6 col-capture" id="uploaddivid">
														<div class="form-group form-md-line-input">
															<input type="file" class="form-control" name="dataupload"
																id="dataupload_id" /> <label for="form_control"
																id="fileType">Data Source File</label>
														</div>
														<br />

													</div>

												</div>
												<div class="form-group form-md-checkboxes">
													<div class="md-checkbox-list">
														<div class="md-checkbox">
															<input type="checkbox" class="md-check"
																id="connectionsourceid" name="connectionsourceid"
																value="Y"> <label for="connectionsourceid"><span></span>
																<span class="check"></span> <span class="box"></span>Import
																Into Existing Data Connection</label>
														</div>
													</div>
												</div>


											</div>
										</div>

									</div>
									<br /> <span class="required"></span>
								</div>
							</div>
							
							<div class="portlet-body" id="connectionList">
								
								<table class="table table-striped table-bordered table-hover dataTable no-footer" style="width: 100%;">

									<thead>
										<tr style>
											<th >Select</th>
											<th >Connection Id</th>
											<th >Connection Name</th>
											<th >Connection Type</th>
											<th >Host</th>
											<th >Schema</th>
											<th >Username</th>
											<th >Port</th>
										</tr>
									</thead>
									<tbody>

										<c:forEach var="listdataschema" items="${listdataschema}">
											<tr>
												<td><input type="checkbox"
													class="md-check accessControlCheck" id="chkIdDataSchema"
													name="chkIdDataSchema1"
													value="${listdataschema.idDataSchema} "></td>

												<td>${listdataschema.idDataSchema}</td>
												<td>${listdataschema.schemaName}</td>
												<td>${listdataschema.schemaType}</td>
												<td>${listdataschema.ipAddress}</td>
												<td>${listdataschema.databaseSchema}</td>
												<td>${listdataschema.username}</td>
												<td>${listdataschema.port}</td>
											</tr>

										</c:forEach>

									</tbody>

								</table>
							</div>
						
							<div class="form-actions noborder align-center">
								<button type="submit" id="submitBtn" class="btn blue">Submit</button>
							</div>

						</form>
					</div>
				</div>


				</div>

			</div>
		</div>
</div>

<jsp:include page="footer.jsp" />
<head>
<script src="./assets/global/plugins/jquery-ui.min.js"></script>
<!-- <script
	src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-multiselect/0.9.13/js/bootstrap-multiselect.js"></script>
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-multiselect/0.9.13/css/bootstrap-multiselect.css"> -->

<script type="text/javascript" src="./assets/global/plugins/jsapi.js"></script>
<script src="./assets/global/plugins/jquery.dataTables.min.js"
		type="text/javascript">
</script>

<script>
 	$(document).ready(function() {
		table = $('.table').dataTable({
		  	"order": [[ 1, "asc" ]]
	     });
    });
</script>

<script type="text/javascript">
$('#connectionList').addClass('hidden');
var table;
	 /* $('#chkIdDataSchema').change(      
		        function() {
		        	var location = $('#locationid').val();
		        	var schemaId = $("#chkIdDataSchema").val();
		        	
		        	 
		            /* if (location =='MSSQL'||location =='MySQL'){
		                var selectedVal = $this.val();
		                $('#lstTable').empty();
		                var form_data = {
		                        
		                        locationName: $("#locationid").val(),
		                        
		                };
		               
		               
		            }else{
		                $('#conditionid').removeClass('hidden');
		            }               
		    }); */
	 
	 /* $("input[name='chkIdDataSchema']").click( function(){ 
		 $('input[name="idDataIds"]').val($("#chkIdDataSchema").val());
	 }); */
	
	
	 
	$('#locationid').change(
			function() {
				$this = $(this);
				var selectedVal = $this.val();
				if ( selectedVal == 'MSSQL'
						|| selectedVal == 'MySQL'
						) {
				
					//alert("fileType");
				//	$('#fileType').html('Data Source File*').show();
					$('#s4_form_id').removeClass('hidden').show();
					
				} 
				
			});
	$('#connectionsourceid').click(function() {
		if ($(this).prop("checked") == true) {
			
			//table.ajax.reload();
			$('#connectionList').removeClass('hidden');
			table.ajax.reload();
		} else if ($(this).prop("checked") == false) {
			$('#connectionList').addClass('hidden');
		}

	});
	
	

       	

	
</script>

<!-- <script> 
        // wait for the DOM to be loaded 
        $(document).ready(function() { 
        	$('#myForm').ajaxForm({
        		headers: {'token':$("#token").val()}
        	}); 
        }); 
 </script> -->
</head>