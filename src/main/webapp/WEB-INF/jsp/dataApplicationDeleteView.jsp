      <jsp:include page="header.jsp" />
    <jsp:include page="container.jsp"/>
    <jsp:include page="checkVulnerability.jsp" />
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
                                <span class="caption-subject bold ">View Validation Check </span>
                            </div>
                        </div>
                        <div class="portlet-body form">
                                <div class="form-body">
                                    <div class="row">
                                        <div class="col-md-6 col-capture">
                                            <div class="form-group form-md-line-input">
                                                <input type="text" class="form-control catch-error" value =" ${listappName}" readonly>
                                                 <input type="hidden" id="appid" value ="${idApp}">
                                                <label for="form_control_1">Validation Check Name</label>

                                               <!--  <span class="help-block">Data Set Name </span> -->
                                            </div><br/>
                                            <span class="required"></span>
                                        </div>
                                        <div class="col-md-6 col-capture">
                                            <div class="form-group form-md-line-input">
                                                <input type="text" class="form-control" value =" ${listsourceName}" readonly>
                                                <label for="form_control">Description</label>
                                                <!-- <span class="help-block">Short text about Data Set</span> -->
                                            </div>
                                        </div>
                                    </div>

                                    <div class="form-actions noborder align-center">
                                        <a onClick="validateHrefVulnerability(this)"  href="#" class="btn red appdelid">Delete </a> 
                                    </div>
                                                    
                                </div>   <!-- <div class="form-body"> -->
                            </div> <!-- portlet-body form -->
                        </div> <!-- portlet light bordered init -->
                    </div> <!-- col-md-6 -->

                </div> <!-- <div class="row"> -->

                

    </div>   <!-- <div class="page-content"> -->
</div>    <!--  page-content-wrapper   -->   
	<jsp:include page="footer.jsp" />
     <script>
     
     
     
     </script>