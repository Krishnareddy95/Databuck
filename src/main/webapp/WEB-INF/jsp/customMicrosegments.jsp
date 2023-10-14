<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<!-- BEGIN CONTENT -->
<head>

<style type="text/css">
    .multiselect-container {
        width: 500px !important;
    }
</style>

<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="./assets/jwf/content/JwfSpaInfra.css">
    <script src="./assets/jwf/scripts/JwfSpaInfra.js"></script>
</head>

<script src="./assets/global/plugins/jquery.min.js"
	type="text/javascript"> </script>
<script
	src="./assets/global/plugins/jquery.dataTables.min.js"
	type="text/javascript"></script>


<div class="page-content-wrapper">
	<!-- BEGIN CONTENT BODY -->
	<div class="page-content">
		<!-- END PAGE HEADER-->
		<div class="row">
			<div class="col-md-6" style="width: 100%;">
				<!-- BEGIN SAMPLE FORM PORTLET-->
				<div class="portlet light bordered init">

					<div
						class="init-form portlet-body form <c:if test="${scheduledTask eq 'yes' }">  hidden </c:if> ">
						<form id="tokenForm1" role="form" action="#">
							<input type="hidden" id="timerid" value=1 />

							<input type="hidden" id="isRuleCatalogDiscovery" value="${isRuleCatalogDiscovery}" />
							<div class="form-body">
								<div class="row">
									<div class="col-md-12 col-capture">
										<div class="form-group form-md-line-input">
											<label for="form_control_1">Custom Microsegments</label>

										</div>
									</div>
								</div>
								<br />
								<div class="row">
                                    <div class="col-md-6 col-capture">
                                        <div class="form-group form-md-line-input">
                                            <select class="form-control" id="templateId"
                                                 name="Event Project">
                                                 <option value="selectTemplate"> Select Template...</option>
                                                <c:forEach items="${templateNames}" var="category" varStatus="loop">
                                                      <option value="${loop.index}">${category}</option>
                                                 </c:forEach>
                                            </select> <label for="form_control_1">Template Name</label>

                                        </div>
                                    </div>
                                </div>
                                <br><br>
                               <div class="row hidden" id="microColDropDown">
                                        <div class="col-md-6 col-capture">
                                            <div class="form-group form-md-line-input">
                                                <select  class="form-control text-left" id="microsegmentColumns"
                                                     name="microsegmentColumns" multiple="multiple"><br>
                                                </select>


                                            </div>
                                        </div>
                                </div>

                                 <br><br>
                                    <div class="row hidden" id="checkColDropDown">
                                        <div class="col-md-6 col-capture">
                                            <div class="form-group form-md-line-input">
                                                <select  class="form-control text-left" id="checkColumns"
                                                     name="checkColumns" multiple="multiple"><br>
                                                </select>

                                            </div>
                                        </div>
                                    </div>
                                    <br><br>
                                    <div class="form-actions noborder align-left">
                                        <button type="submit" class="btn blue hidden" id="addMicroSegments">Add Microsegments</button>

                                    </div>

                                <br><br>
                                    <div class="portlet-body hidden" id="custom_microsegment_table">
                                     <label >Available Microsegments</label>
                                      <table id="CustomMicrosegment" class="table table-striped table-bordered table-hover dataTable no-footer"
                                                style="width: 100%;">
                                            <thead>
                                                <tr>
                                                    <th>Check Name</th>
                                                    <th>Microsegments Columns</th>
                                                    <th>Check Columns</th>
                                                    <th>Delete</th>
                                                </tr>
                                            </thead>
                                           </table>
                                   </div>
							</div>


						</form>
					</div>
				<!-- END SAMPLE FORM PORTLET-->
			</div>
		</div>
	</div>
</div>
</div>
<jsp:include page="footer.jsp" />

<script src="./assets/global/plugins/jquery-ui.min.js"></script>
<script
	src="./assets/global/plugins/bootstrap-multiselect.js"></script>
<link rel="stylesheet"
	href="./assets/global/plugins/bootstrap-multiselect.css">
<script type="text/javascript">

	$(document).ready(function() {
	initializeJwfSpaInfra(); // Load JWF framework module on this page
	
		$('#templateId').multiselect({
			maxHeight : 200,
			buttonWidth : '500px',
			enableFiltering : true,
			nonSelectedText : 'Select Connection'
			 
		});
	
		$('#microsegmentColumns').multiselect({
            maxHeight : 200,
            buttonWidth : '500px',
            includeSelectAllOption : true,
            enableFiltering : true,
            selectAll: true,
            nonSelectedText : 'Select Microsegments'

        });
        $('#checkColumns').multiselect({
             maxHeight : 200,
             buttonWidth : '500px',
             includeSelectAllOption : true,
             enableFiltering : true,
             selectAll: true,
             nonSelectedText : 'Select Check Columns'

        });
        
		$('#templateId').change(
        			function() {
                    $('#microsegmentColumns').empty();
                    $('#checkColumns').empty();
        			$('#custom_microsegment_table').removeClass('hidden').show();
        			$('#microColDropDown').removeClass('hidden').show();
        			$('#checkColDropDown').removeClass('hidden').show();
        			$('#addMicroSegments').removeClass('hidden').show();

        			var templateStr= $("#templateId option:selected").text();
        			const tempNameArr = templateStr.split("-");
        			var idData= tempNameArr[0];
        			var checkName='Numerical Statistics Check';
        			$("#CustomMicrosegment").dataTable().fnDestroy();
        			$('#CustomMicrosegment tbody').empty();

                    showColumns(idData);
                    showCheckColumn(idData,checkName)

        			var table = $('#CustomMicrosegment').dataTable({
                           		"bPaginate": true,
                    		  	"order": [ 0, 'asc' ],
                    		  	"bInfo": true,
                    		  	"iDisplayStart":0,
                    		  	"bProcessing" : true,
                    		 	"bServerSide" : false,
                    		 	"bFilter": true,
                    		 	'sScrollX' : true,
                    		 	"sAjaxSource" : path+"/getCustomMicroSegmentsByTemplateId?idData="+idData,
                    		 	"aoColumns": [
                                        { "data": "checkName" },
                                        { "data": "microsegmentColumns" },
                                        { "data": "checkEnabledColumns" },
                                        { "data" : "id",
                                                "render": function(data, type, row, meta){
                                                     var id = row.id;
                                                     data = "<a onClick=\"javascript:deleteMicroSegments('"+id+"')\" ><i style=\"margin-left: 20%; color: red\" class=\"fa fa-trash \"></i></a>";
                                             return data;
                                             }
                                          },

                                ],
                    		 	"dom": 'C<"clear">lfrtip',
                    			colVis: {
                    				"align": "right",
                    	            restore: "Restore",
                    	            showAll: "Show all",
                    	            showNone: "Show none",
                    				order: 'alpha'
                    	        },
                    		    "language": {
                    	            "infoFiltered": ""
                    	        },
                    	        "dom": 'Cf<"toolbar"">rtip',
                    	      });
        			});

	});
</script>
<script>
    function showColumns(idData){
        	var form_data = {
        			idData : idData,
        	};

           $.ajax({
             url: './changeDataColumnAjax',
             type: 'POST',
             headers: { 'token':$("#token").val()},
             datatype:'json',
             data: form_data,
             success: function(message){
                 var j_obj = $.parseJSON(message);
                  if(j_obj.hasOwnProperty('success'))
                  {
                    column_array = JSON.parse( j_obj.success);

                    $(column_array).each(function(i, item) {
                           $('#microsegmentColumns').append($('<option>',
                                              {
                                                  value : item,
                                                  text : item
                                              }));
                     });
                  }else if(j_obj.hasOwnProperty('fail') ) {
                      toastr.info(j_obj.fail);
                  }
                     
                   $('#microsegmentColumns').multiselect('rebuild');
                      $('#microsegmentColumns').multiselect({
                          includeSelectAllOption : true
                    });


             },
             error: function(xhr, textStatus, errorThrown){

             }
          });
    }
</script>


<script>
    function showCheckColumn(idData,checkName){
        	var form_data = {
        			idData : idData,
        			checkName:checkName,
        	};

           $.ajax({
             url: './getCustomMicroSegmentColumnNamesForCheck',
             type: 'POST',
             headers: { 'token':$("#token").val()},
             datatype:'json',
             data:form_data,
             success: function(message){
                 var j_obj = $.parseJSON(message);
                  if(j_obj.hasOwnProperty('success'))
                  {
                    column_array = JSON.parse( j_obj.success);

                    $(column_array).each(function(i, item) {
                          $('#checkColumns').append($('<option>',
                                        {
                                            value : item,
                                            text : item,
                                        }));
                     });
                  }else if(j_obj.hasOwnProperty('fail') ) {
                      toastr.info(j_obj.fail);
                  }
                  $('#checkColumns').multiselect('rebuild');
                   $('#checkColumns').multiselect({
                          includeSelectAllOption : true
                   });
             },
             error: function(xhr, textStatus, errorThrown){

             }
          });
    }
</script>

<script>

$('#addMicroSegments').click(function() {
                    var microsegmentColumns= $("#microsegmentColumns").val();
                    var checkColumns= $("#checkColumns").val();

                    var no_error = 1;
                    if(!checkInputText()){
                        no_error = 0;
                        toastr.info('Vulnerability in submitted data, not allowed to submit!');
                    }
                    if( $('#microsegmentColumns').val() == '0' || $('#microsegmentColumns').val() == '' || $('#microsegmentColumns').val() == 'undefined' || $('#microsegmentColumns').val() == null ){
                        $('<br><span class="input_error" style="font-size:14px;color:red">Please select Atleast one Microsegment column</span>')
                                .insertAfter($('#microsegmentColumns'));
                        no_error = 0;
                    }
                    if( $('#checkColumns').val() == '0' || $('#checkColumns').val() == '' || $('#checkColumns').val() == 'undefined' || $('#checkColumns').val() == null ){
                        $('<br><span class="input_error" style="font-size:14px;color:red">Please select Atleast one Check column</span>')
                                .insertAfter($('#checkColumns'));
                        no_error = 0;
                    }

                    if (no_error) {
                            var templateStr= $("#templateId option:selected").text();
                            const tempNameArr = templateStr.split("-");
                            var idData= tempNameArr[0];

                            var data = {
                               templateId : idData,
                               microsegmentColumns : microsegmentColumns.toString(),
                               checkEnabledColumns : checkColumns.toString(),
                               checkName :  "Numerical Statistics Check"
                            };

                     		$.ajax({

                                 url : './addCustomMicrosegments',
                                 type : 'POST',
                                 contentType: "application/json",
                                 headers: { 'token':$("#token").val()},
                                 datatype : 'json',
                                 data: JSON.stringify(data),
                                 success : function(message) {
                                     var j_obj = $.parseJSON(message);
                                     var status = j_obj.status;
                                     toastr.info(j_obj.message);
                                     if (status == 'success') {
                                         setTimeout(function(){
                                            window.location.href = 'customMicrosegments';
                                         },1000);
                                     }
                                 },
                                 error : function(xhr, textStatus,
                                         errorThrown) {

                                     setTimeout(function(){
                                        toastr.info("Unexpected error occurred");
                                        window.location.reload;
                                     },1000);

                                     return false;

                                 }
                             });
                        }

                        return false;
	});
</script>
<script>

function deleteMicroSegments(id){
    Modal.confirmDialog(1, 'Microsegment delete confirmation', 'Do you want to delete the Microsegment?',
  					['OK', 'Cancel'], { onDialogClose: cbPromoteApproveDeleteDialog, onDialogLoad: null });

  			return;

	function cbPromoteApproveDeleteDialog(oEvent) {
					var sEventId = oEvent.EventId, sBtnSelected = oEvent.ClickedButton;
	
					switch(sEventId) {
						case jwfGlobal.EVT_MODAL_DIALOG_ONLOAD:
							PageCtrl.debug.log('on Dialog load','ok');
							break;
	
						case jwfGlobal.EVT_MODAL_DIALOG_ONCLOSE:
							if (sBtnSelected === 'OK') {
								deleteCustomMicrosegments(id);
							}
							break;
					}
	}
}

function deleteCustomMicrosegments(id){

    var no_error = 1;
        if(!checkInputText()){
            no_error = 0;
            toastr.info('Vulnerability in submitted data, not allowed to submit!');
        }

        if( id == '0' || id == '' || id == 'undefined' || id == null ){
            $('<br><span class="input_error" style="font-size:14px;color:red">Incorrect Row Id</span>')
                    .insertAfter($('#CustomMicrosegment'));
            no_error = 0;
        }
    
     if(no_error){
        var form_data = 
        			{
                        id : id
                    };
        $.ajax({
                url : './deleteCustomMicrosegments',
                type : 'POST',
                headers: { 'token':$("#token").val()},
                datatype : 'json',
                data : form_data,

                success : function(message) {
                    console.log(message);
                    var j_obj = $.parseJSON(message);
                    var status = j_obj.status;
                    toastr.info(j_obj.message);
                    if (status == 'success') {
                        setTimeout(
                                function() {
                                	setTimeout(function(){window.location.href = 'customMicrosegments';},1000);
                                    //window.location.reload();
                                }, 1000);
                    }
                },
                error : function(xhr, textStatus,
                        errorThrown) {
                    return false;
                   // window.location.reload();
                }
            });
        }
        return false;
}

</script>

