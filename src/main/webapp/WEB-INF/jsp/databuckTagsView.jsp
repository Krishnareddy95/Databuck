<%@page import="com.databuck.service.RBACController"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<style>
	
	#tblDomainViewList {
		background: #f2f2f2;
		border-bottom: 2px solid #808080;
	}
	
	#DomainMainParent textarea {
		width: 100%; 
		min-height: 125px;
		margin-top: 10px;
		padding: 5px;
		font-size: 12.5px;
		font-family: "Lucida Console", Courier, monospace;
	}
	
	.LongTextBox {
		width: 550px !important;
		height: 32px !important;
	}
	
	#AssginedProjects {
		border: 2px dotted grey;
		padding: 15px;
		max-height: 350px;
		overflow-x: hidden;
		overflow-y: auto;		
	}
	
	#AssginedProjects label, #AssginedProjects input {
		margin-bottom: 12px;
	}	

	input[type=checkbox] {
		width: 20px; 
		height: 20px;
		margin-right: 10px;
	}
	
	#AssginedProjects label {
		margin-top: -5px;
	}
	
	.RowInEditing {
		background-color: #3973ac !important; /* #0066cc */
		color: white;
		weight: bold;
	}
	
	#BtnNew {
		width: 220px !important; 	
		float: left; 
	}		

	#BtnSave, #BtnCancel {
		width: 150px !important; 	
		float: right; 
	}		
	
	#DataButtons {
		margin-top: 15px;
		margin-bottom: 15px !important;
	}
	
	#DataButtons button {
		margin-right: 15px !important; 
		text-align: center !important;
		font-size: 11.5px !important;
	}
	
</style>
<div class="page-content-wrapper">
	<div class="page-content">
		<div class="row">
			<div class="col-md-12">
				<div class="portlet light bordered">
					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span id='spanTitle' class="caption-subject bold ">Databuck Tags</span>
						</div>
					</div>
					<div class="portlet-body">
						<div id='DomainMainParent' style="border: 2px solid #d9d9d9; padding: 15px;">
							<table id='tblDataFormContainer' width='100%'>
								<tr width='100%'>
									<td width='40%' valign='top'>
										<label for='TagName'>Tag Name</label></br>
										<input type='text' id='TagName' class='LongTextBox'>
										<input type='hidden' id='TagId' />
										</br></br>
										
										<label for='Description'>Tag Description</label></br>
					        			<input type='text' id='TagDescription' class='LongTextBox'>
					        			<br></br>
									</td>
									
								</tr>
								<tr>
							</table>
							
							<div id='DataButtons'>
								<button class='btn btn-primary DataBtns' id='BtnNew' onclick="saveTagRecord()">Add New Tag</button>
								<div class='row hidden' id ='save'>
								<button class='btn btn-primary DataBtns' id='BtnCancel' onclick="javascript:cancel()">Cancel</button>
								<button class='btn btn-primary DataBtns' id='BtnSave' onclick="javascript:updateTagDetails()">Save</button>		
								</div>																												
							</div>					
							</br></br>
							
						</div>							
						</br>
						<table id='tblTagViewList' class='table table-striped table-bordered table-hover' width='100%'>
							<thead>
								<tr>									
									 <th>Tag ID</th>
									 <th>Tag Name</th>
									 <th>Tag Description</th>
									 <th>Edit</th>		 
								</tr>
							</thead>
						</table>					
					</div>
				</div>
			</div>
		</div>
	</div>

</div>

<jsp:include page="footer.jsp" />

<script src="./assets/global/plugins/jquery.min.js" type="text/javascript"> </script>
<script src="./assets/global/plugins/jquery.dataTables.min.js" type="text/javascript"></script>

<head>
	<link rel="stylesheet" href="./assets/jwf/content/JwfSpaInfra.css">
	<script src="./assets/jwf/scripts/JwfSpaInfra.js"></script>
</head>

<script>
	var ManageDatabuckTagDataEntry = {
		PageData : {},
		DatabaseRecord: {},
		EditingRecord: {},
		setup: function() {
			$('#tblDataFormContainer tr td :input').on('change', ManageDatabuckTagDataEntry.mainEventHandler);
			
			$('#BtnNew').on('click', ManageDatabuckTagDataEntry.mainEventHandler);
			$('#BtnCancel').on('click', ManageDatabuckTagDataEntry.mainEventHandler);			
		},			
		loadTagList: function() {
			var sViewTable = '#tblTagViewList';
			JwfAjaxWrapper({
				WaitMsg: 'Loading Tag Records',
				Url: 'getDatabuckTags',
				Headers:$("#token").val(),
				Data: {},
				CallBackFunction: function(oResponse) {
					ManageDatabuckTagDataEntry.PageData = oResponse;					
				
					ManageDatabuckTagDataEntry.DataTable = $("#tblTagViewList").DataTable ({
						"data" : ManageDatabuckTagDataEntry.PageData.databuckTag,
						"columns" : [							
							{ "data" : "tagId" },
							{ "data" : "tagName" },
							{ "data" : "description" },
							{ "data" : "TagId-edit",
								  "render": function(data, type, row, meta){
				                    data = "<a onClick=\"javascript:editTagDetails("+row.tagId+",\'"+row.tagName+"\',\'"+row.description+"\')\" class=\"fa fa-edit\"></a>";
				                    return data;
							}
							}
						],
						order: [[ 0, "asc" ]],
						orderClasses: false,						
						destroy: true,
						drawCallback: function( oSettings ) {
							$('#tblTagViewList a').off('click', ManageDatabuckTagDataEntry.mainEventHandler).on('click', ManageDatabuckTagDataEntry.mainEventHandler);
						}					 
					});				
				}				
			});
								
			
		}		
	}
	function editTagDetails(tagId,tagName,tagDescription){
		 $('#save').removeClass('hidden').show();
		 $('#BtnNew').addClass('hidden').hide();
		    
		 $('#TagId').val(tagId); 
		 $('#TagName').val(tagName); 
		 $('#TagDescription').val(tagDescription); 
	}
	function cancel(){
		window.location.reload();
	}
	function updateTagDetails(){
		
	    $('.input_error').remove();
	    var no_error = 1;
	    if(!checkInputText()){
            no_error = 0;
            toastr.info('Vulnerability in submitted data, not allowed to submit!');
        }
	    if ($("#TagName").val().length == 0) {
	         console.log('tagName');
	         $('<span class="input_error" style="font-size:12px;color:red">   Please Enter Name </span>')
	             .insertAfter($('#TagName'));
	         no_error = 0;
	    }
	        
        if ($("#TagDescription").val().length == 0) {
            console.log('TagDescription');
            $('<span class="input_error" style="font-size:12px;color:red">   Please Enter Description </span>')
                .insertAfter($('#TagDescription'));
            no_error = 0;
        }
        
        if (no_error > 0) {
        	 var form_data = {
        			 tagId : $("#TagId").val(),
                     tagName : $("#TagName").val(),
                     description : $("#TagDescription").val()
               };
           $.ajax({
                    url : './updateDatabuckTagInfo',
                    type : 'POST',
                    headers: { 'token':$("#token").val()},
                    datatype : 'json',
                    data : form_data,
                    success : function(data) {
						var status = data.status;
						var message = data.message;
                        toastr.info(message);
                        setTimeout(function() {
                        	 window.location.reload();
                          }, 1000);
                    },
                    error : function(data){
                    	 toastr.info('Failed to update Tag');
                    	 setTimeout(function() {
                    		 window.location.reload();
                                 }, 1000);
                      }
                  });
        }
  
	}
	
	function saveTagRecord() {
		 $('.input_error').remove();
		    var no_error = 1;
		    if(!checkInputText()){
             no_error = 0;
             toastr.info('Vulnerability in submitted data, not allowed to submit!');
         }
        if ($("#TagName").val().length == 0) {
         console.log('tagName');
         $('<span class="input_error" style="font-size:12px;color:red"><br><br> Please Enter Name </span>')
             .insertAfter($('#TagName'));
         no_error = 0;
        						}
        if ($("#TagName").val().length > 20) {
            console.log('tagName');
            $('<span class="input_error" style="font-size:12px;color:red"><br><br> Data Too Long </span>')
                .insertAfter($('#TagName'));
            no_error = 0;
           						}
        
        if ($("#TagDescription").val().length == 0) {
            console.log('TagDescription');
            $('<span class="input_error" style="font-size:12px;color:red"><br><br> Please Enter Description </span>')
                .insertAfter($('#TagDescription'));
            no_error = 0;
           						}
         if (no_error > 0) {
          var form_data = {
                 tagName : $("#TagName").val(),
                 description : $("#TagDescription").val()
           };
            $.ajax({
                     url : './addDatabuckTags',
                     type : 'POST',
                     headers: { 'token':$("#token").val()},
                     datatype : 'json',
                     data : form_data,
                     success : function(data) {
                     var status = data.status;
                     var message = data.message;
                     toastr.info(message);
                     setTimeout(function() {
                    	 window.location.reload();;
                                 }, 1000);
                     },

                     error : function(data){
                    	 toastr.info(message);
                    	 setTimeout(function() {
                    		 window.location.reload();;
                                 }, 1000);
                      }
                   });
          }
	}

	$(document).ready(function () {
		initializeJwfSpaInfra();
		ManageDatabuckTagDataEntry.setup();
		ManageDatabuckTagDataEntry.loadTagList();

	});
	
</script>