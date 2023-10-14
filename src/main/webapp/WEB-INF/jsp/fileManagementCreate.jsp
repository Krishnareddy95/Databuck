<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp"/>
<jsp:include page="container.jsp"/>
 <!-- BEGIN CONTENT -->
    <div class="page-content-wrapper">
        <!-- BEGIN CONTENT BODY -->
        <div class="page-content">
            <!-- BEGIN PAGE TITLE-->
            <!-- END PAGE TITLE-->
            <!-- END PAGE HEADER-->
            <div class="row">
                <div class="col-md-6" style="width:100%;">
                    <!-- BEGIN SAMPLE FORM PORTLET-->
                    <div class="portlet light bordered init">
                        <div class="portlet-title">
                            <div class="caption font-red-sunglo">
                                <span class="caption-subject bold "> Create Validation Check </span>
                            </div>
                        </div>
                        <div class="portlet-body form">
                                <input type="hidden" id="idApp" name="idApp" value="${idApp}" />
                                     <input type="hidden" id="idData" name="idData" value="${idData}" />
                                          <input type="hidden" id="apptype" name="apptype" value="${apptype}" />
                                               <input type="hidden" id="applicationName" name="applicationName" value="${applicationName}" />
                                                    <input type="hidden" id="description" name="description" value="${description}" />
                                                       <input type="hidden" id="name" name="name" value="${name}" />
                                <div class="form-body">
                                    <div class="row">
                                        <div class="col-md-6 col-capture">
                                            <div class="form-group form-md-line-input">
                                                <input type="text" class="form-control catch-error" id="appnameid" name="dataset" value="${name }" readonly />
                                                <label for="form_control_1">Validation Check Name *</label>
                                               <!--  <span class="help-block">Data Set Name </span> -->
                                            </div><br/>
                                            <span class="required"></span>
                                        </div>
                                        <div class="col-md-6 col-capture">
                                            <div class="form-group form-md-line-input">
                                                <input type="text" class="form-control" id="descriptionid" name="description" placeholder="Enter your description" value="${description }" readonly/>
                                                <label for="form_control">Description</label>
                                                <!-- <span class="help-block">Short text about Data Set</span> -->
                                            </div>
                                        </div>
                                    </div>

                                    <div class="row">
                                        <div class="col-md-6 col-capture">
                                            <div class="form-group form-md-line-input">
                                                <input type="text" class="form-control" id="descriptionid" name="description"  value="${apptype }" readonly />
                                                <label for="form_control">Validation Check Type</label>
                                                <!-- <span class="help-block">Short text about Data Set</span> -->
                                            </div>
                                        </div>

                                         <div class="col-md-6 col-capture">
                                            <div class="form-group form-md-line-input">
                                                <input type="text" class="form-control" id="descriptionid" name="description"  value="${applicationName}" readonly>
                                                <label for="form_control">Data Template</label>
                                                <!-- <span class="help-block">Short text about Data Set</span> -->
                                            </div>
                                        </div>
                                    </div>

                                    <br> <br>
                                         <div class="row">
                                            <div class="col-md-12">
                                                <div class="form-group form-md-checkboxes">
                                                    <div class="md-checkbox-list">
                                                        <div class="md-checkbox">
                                                            <input type="checkbox" id="dupCheckid" name="dupCheck" class="md-check accessControl" value="Y" >
                                                            <label for="dupCheckid">
                                                                <span></span>
                                                                <span class="check"></span> 
                                                                <span class="box"></span>Duplicate Check?
                                                            </label>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div> 
                                            <div class="form-actions noborder align-center tab-one-save">
                                                <button type="submit" id="matchtypecreateid" class="btn blue">Save</button>
                                            </div> 
                                    </div> 
                            </div>
                        </div>
                    </div>
                </div>
            </div> 
        </div>           
<jsp:include page="footer.jsp"/>            
<head>
<script type="text/javascript">
$('#matchtypecreateid').click(function() {
	var no_error = 1;
	 if(no_error){
	if ($('#dupCheckid').is(":checked"))
	{
		var form_data = {
	    		dupCheck  : "Y",
	    		idApp:$("#idApp").val(),
	    };
	}else{
		var form_data = {
	    		dupCheck  : "N",
	    		idApp:$("#idApp").val(),
	    };
	}
   
     console.log(form_data); //return false;
    $.ajax({
      url: './fileManagementSave',
      type: 'POST',
      headers: { 'token':$("#token").val()},
      datatype:'json',
      data: form_data,
      success: function(message){
    	    //console.log(message);
         var j_obj = $.parseJSON(message);
              if(j_obj.hasOwnProperty('success') )
              {
            	  //alert(message);
                    toastr.info("File Management Rule created successfully");
                    setTimeout(function(){
                    window.location.href='validationCheck_View';
                   // window.location.reload();

                },1000); 
              }else if(j_obj.hasOwnProperty('fail') )
              {
            	  toastr.info("Sorry, There was a Problem");
                 // console.log(j_obj.fail);
                  //satoastr.info(j_obj.fail);
              }
      },
      error: function(xhr, textStatus, errorThrown){
            $('#initial').hide();
            $('#fail').show();
      }
   });
	 }
	    return false;
});
</script></head>  