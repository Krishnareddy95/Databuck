


<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="org.springframework.jdbc.support.rowset.SqlRowSet"%>
<%@page
	import="org.springframework.jdbc.support.rowset.SqlRowSetMetaData"%>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<jsp:include page="header.jsp" />

<jsp:include page="container.jsp" />

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


					<div>
						<button class="btn btn-primary" id="update">update</button>
						<button class="btn btn-primary" id="insertRow" data-toggle="modal"
							data-target="#myModal">New Row</button>
					</div>

					<div class="portlet-body" id="tableresult">
						<input type="hidden" id="tableName" value="${name}">
						<table id="ColSummTable"
							class="table table-striped table-bordered  table-hover"
							style="width: 100%;">
							<thead>
								<tr id="filters">

								</tr>

							</thead>

						</table>
					</div>
					<div>
						<!-- Button to Open the Modal -->
						<!--  <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#myModal">
								    Open modal
								  </button> -->

						<!-- The Modal -->
						<div class="modal" id="myModal">
							<div class="modal-dialog"
								style="display: inline-block; position: fixed; top: 0; bottom: 0; left: 0; right: 0; height: 500px; margin: auto; overflow:auto;">
								<div class="modal-content">

									<!-- Modal Header -->
									<div class="modal-header">
										<h4 class="modal-title">Insert New Values to Reference
											Table</h4>
										<button type="button" class="close" data-dismiss="modal">&times;</button>
									</div>

									<!-- Modal body -->
									<div class="modal-body" id="newValues"></div>

									<div id="popup_form_validate_msg_div" class="hidden">
										<span style="padding-left:20px;color:red;">Please fill the values</span>
									</div>
										
									<!-- Modal footer -->
									<div class="modal-footer">
										<button type="submit" class="btn btn-success" id="insertcall"text-align: center; >Submit</button>
										<button type="button" class="btn btn-danger"
											data-dismiss="modal">Cancel</button>
									</div>
									
								</div>
							</div>
						</div>
					</div>





					<div class="cd-popup2" id="popSample">
						<div class="cd-popup-container1" id="outerContainer1">
							<div id="loading-data-progress" class="ajax-loader hidden">
								<h4>
									<b>Summary DQI Data Loading in Progress..</b>
								</h4>
								<img
									src="${pageContext.request.contextPath}/assets/global/img/loading-circle.gif"
									class="img-responsive" />
							</div>
							<br /> <br /> <br /> <br />
							<div id="chartDiv"
								style="height: 500px; width: 100%; margin: 0 auto;"
								class="dashboard-summary-chart"></div>
							<button type="submit" id="dqiDataCloseBtn" class="btn blue">Close</button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>


<script src="./assets/global/plugins/jquery.min.js"
	type="text/javascript">
</script>
<script src="../../extensions/Editor/js/dataTables.editor.min.js"></script>
<script
	src="./assets/global/plugins/jquery.dataTables.min.js"
	type="text/javascript">
</script>



<script>

/*jQuery(document).ready(function() {
	var table;
	console.log("hello");
	//listDataDefinitionData
	var tableName=$( "#tableName" ).val();
	var idData=$( "#idData" ).val();
	//alert(tableName);
	/*table = $('#ColSummTable').dataTable({
			
		  	"bPaginate": true,
		  	"order": [ 0, 'asc' ],
		  	"targets": 'no-sort',
			"bSort": false,
		  	//"order": [],
		  	"bInfo": true,
		  	"iDisplayStart":0,
		  	//"bFilter": false,
		  	"bProcessing" : true,
		 	"bServerSide" : true,
		 	"dataSrc": "",
		 	'sScrollX' : true,
		 	"sAjaxSource" :"./referenceTable?tableName="+tableName+"&idData="+idData,
		 	"dom": 'C<"clear">lfrtip',
		 	
			colVis: {
				"align": "right",
	            restore: "Restore",
	            showAll: "Show all",
	            showNone: "Show none",
				order: 'alpha'
				//"buttonText": "columns <img src=\"/datatableServersideExample/images/caaret.png\"/>"
	        },
		    "language": {
	            "infoFiltered": ""
	        },
	        "dom": 'Cf<"toolbar"">rtip',
	    
	        
	      });*/
			
	        
	    /* $(document).on('click','#r_efresh', function()
			{
			    var element = document.getElementById('nav_button-bar');
				var i;
				var values = [];
				
			    for(i=0 ; i < $('#nav_button-bar').find('select').length ; i++){
			    	values.push($('#nav_button-bar').find(":selected")[i].value);
			    }
			    values = values.join("-");
			    
			    table.fnFilter(values) ;
			  
			    
			    console.log(values);
			    
			    
			});*/
	      

//});



</script>
<script>




$(document).ready(function() {
    var no_error = 1;
    $('.input_error').remove();
        // alert(${idData});
        var idDataValue = ${idData};
        
    	var form_data = {
    		idData :idDataValue ,
    		tablename: $( "#tableName" ).val()
           // times: new Date().getTime()
        };
        
        $.ajax({
          url: './refDataColumn',
          type: 'POST',
          headers: { 'token':$("#token").val()},
          datatype:'json',
          data: form_data,
          success: function(message){
              var j_obj = $.parseJSON(message);
                  if(j_obj.hasOwnProperty('success'))
                  {
                    var temp = {};
                    column_array = JSON.parse( j_obj.success);
                    // empty the list from previous selection
                    //$('#Microsegval').empty();
                    var navButtons = document.getElementById("filters");
                    
                    var column_name =[];
                    $.each(column_array,function(i,obj)
                    {        var column_details = column_array[i];
                             
                             column_name.push(column_details);
                             $("#filters").append("<th id=col_value"+i+">"+column_details+"</th>");
                            
                             
                             //alert(obj.value+":"+obj.text);
                           //  var div_data="<option value="+column_details+">"+column_details+"</option>";
                           // alert(div_data);
                         //   $(div_data).appendTo('#Microsegval'); 
                     });
                  
                    var column_N = column_name.join("-");
                	var tableName=$( "#tableName" ).val();
                	var idData=$( "#idData" ).val();  
         	        var row_id ;
                    jQuery(document).ready(function() {
                    	var table;
                    	console.log("hello");
                    	
                    	
                    	//alert(tableName);
                    	table = $('#ColSummTable').DataTable({
                    			
                    		  	"bPaginate": true,
                    		  	"order": [ 0, 'asc' ],
                    		  	"targets": 'no-sort',
                    			"bSort": true,
                    		  	//"order": [],
                    		  	"bInfo": true,
                    		  	"iDisplayStart":0,
                    		  	//"bFilter": false,
                    		  	"bProcessing" : true,
                    		 	"bServerSide" : true,
                    		 	"dataSrc": "",
                    		 	'sScrollX' : true,
                    		 	"sAjaxSource" :"./referenceTable?tableName="+tableName+"&idData="+idData+"&columnName="+column_N,
                    		 	"dom": 'C<"clear">lfrtip',
                    		 	
                    			colVis: {
                    				"align": "right",
                    	            restore: "Restore",
                    	            showAll: "Show all",
                    	            showNone: "Show none",
                    				order: 'alpha'
                    				//"buttonText": "columns <img src=\"/datatableServersideExample/images/caaret.png\"/>"
                    	        },
                    		    "language": {
                    	            "infoFiltered": ""
                    	        },
                    	        "dom": 'Cf<"toolbar"">rtip',
                    	        /* "aoColumnDefs" : [ {
        							'bSortable' : true,
        							'aTargets' : [ 1, 2 ]
        						} ], */
        						
                    	       
                    	    
                    	        
                    	      });
                    	$("#ColSummTable").on("click", "tr", function(e) {            	        	
            	        	var currentRow=$(this).closest("tr"); 
            	        	row_id=currentRow.find("td:eq(0)").text();
            	            
            	        });
                    	var table_1 = document.getElementById("ColSummTable");
                    	table_1.addEventListener('click', function(e) {
                    		
                    	    var target = e.target;
                    	    //test if clicked element is TD.
                    	    if (target && target.tagName && target.tagName.toLowerCase() == "td")
                    	    {
                    	    	var col_id = target.cellIndex;
                    	    	
                    	    	// Dont allow to change the primary key column dbk_row_id in postion 0.
                    	    	if(col_id==0){
                         	        target.setAttribute('contenteditable', 'false');
                    	    	} else {
                    	        	//make cell editable
                    	        	target.setAttribute('contenteditable', 'true');
                    	        }
                    	    	
                    	        //on blur close the editable field and return to normal cell.
                    	        target.onblur = function(){
                    	        	this.removeAttribute('contenteditable');
	                    	        
	
	                    	       //alert(col_id);
	                    	       //alert($("#filters").val());
	                    	       // var th = $('#ColSummTable th').eq($(this).index());
	                    	        //alert(target.innerHTML);

	                    	       var column_Name_update = column_name[col_id];
	                    	       var updated_col_val = target.innerHTML.replace(/\<br\>/g," ");
                   	    	       var updated_row_id = row_id;
                   	    	    
	                    	       $("#update").on("click", function() {
	                    	        	var form_data = {
	                    	            		row_id :updated_row_id ,
	                    	            		col_name: column_Name_update,
	                    	                    update_val:updated_col_val,
	                    	                    table_name:$( "#tableName" ).val()
	                    	                };
	                    	                
	                    	                $.ajax({
	                    	                  url: './updateRefData',
	                    	                  type: 'POST',
	                    	                  headers: { 'token':$("#token").val()},
	                    	                  datatype:'json',
	                    	                  data: form_data,
	                    	                  success:  function(message){
	                    	                      var j_obj = $.parseJSON(message);
	                    	                      
	                    	                      if(j_obj.hasOwnProperty('success'))
	                    	                      {
	                    	          				
	                    	          				toastr.info('Updated successfully');
	                    	          				setTimeout(
															function() {
																location.reload();
															},
															1000);
	                    	          				
	                    	          			  }else{
	                    	          				
	                    	          				toastr.info('Failed to Update');
	                    	          				
	                    	          				setTimeout(
															function() {
																location.reload();
															},
															1000);
	                    	          				
	                    	          			}
	                    	                  }
	                    	                  
	                    	                });
	                    	            
	                    	        });
                    	       
                    	         }
                    	                            	        
                    	    }
                    	    

                    	});
                    	$.each(column_name,function(i,obj)
                        {
                    		//var j =i+1;
                    		if(i > 0){
                    		
                    	    	$('#myModal .modal-body').append("<div><label>"+column_name[i]+"</label><span>  </span><input type=text class= form-control id="+column_name[i]+" /></div>");
                    	   
                    		}
                        });
                    	$("#insertRow").on("click", function() {
                    		 $("#popup_form_validate_msg_div").addClass("hidden").hide();
                    		 $.each(column_name,function(i,obj)
                                 {
                             		//var j =i+1;
                             		if(i > 0){
                             	    	$('#'+column_name[i]).val('');
                             		}
                                 });
                    	});
                    	$("#insertcall").on("click", function() {
                    		
                            $("#popup_form_validate_msg_div").addClass("hidden").hide();

                    		 var colname = [];
                    		 var colval = [];
                    		 var empty_values_count = 0;
                    		 var total_values_count = 0;
                             $.each(column_name,function(i,obj)
                             {
                            	if(i > 0){
                            	 colname.push(column_name[i]);
                            	 colval.push($("#"+column_name[i]).val());
                            	 total_values_count = total_values_count + 1;
                            	 
                            	 if($("#"+column_name[i]).val().length == 0){
                            		 empty_values_count = empty_values_count+ 1;
                            	 }
                            	}
                             });
                             
                             if(empty_values_count == total_values_count){
                            	 $("#popup_form_validate_msg_div").removeClass("hidden").show();
                            	 
                             } else {
                             
                             	$("#myModal").modal("hide");
	                    		var cName = colname.join("$$");
	                    		var cVal = colval.join("$$");
	                    		console.log(cName);
	                    		console.log(cVal);
                    		 
                 	        	var form_data_insert = {
                 	            		columnName :cName,
                 	            		columnValue :cVal,
                 	                    table_name:$( "#tableName" ).val()
                 	                };
                 	                
                 	                $.ajax({
                 	                  url: './insertNewValueToRefTable',
                 	                  type: 'POST',
                 	                 headers: { 'token':$("#token").val()},
                 	                  datatype:'json',
                 	                  data: form_data_insert,
                 	                  success:  function(message){
                 	                      var j_obj = $.parseJSON(message);
                 	                      
                 	                      if(j_obj.hasOwnProperty('success'))
                 	                      {
                 	          				toastr.info('New Row Inserted successfully');	
                 	          				setTimeout(
														function() {
															location.reload();
														},
														1000);
                 	          				
                 	          			  }else{
                 	          				toastr.info('Failed to Insert New Row');
                 	          				
                 	          			}
                 	                    
                 	                     $("#popup_form_validate_msg_div").addClass("hidden").hide();
                 	                  }
                 	                  
                 	                });
                             }
                    	});
                    
                    });
                 
                    
                  }else if(j_obj.hasOwnProperty('fail') )
                  {
                      console.log(j_obj.fail);
                      toastr.info(j_obj.fail);
                  }
          },
          error: function(xhr, textStatus, errorThrown){
                $('#initial').hide();
                $('#fail').show();
          }
       });
        
});
/* $(document).ready(
		function() {
			var table = $('#ColSummTable').DataTable(
					{
						scrollY : "280px",
						scrollX : true,
						scrollCollapse : true,
						paging : false,
						"aoColumnDefs" : [ {
							'bSortable' : true,
							'aTargets' : [ 1, 2 ]
						} ]
					});
			/*  $(".source-displayName").css({"padding-bottom": "20px"});  */
//}); */


</script>
<script>
// Function to open the popup

   

</script>






<jsp:include page="footer.jsp" />