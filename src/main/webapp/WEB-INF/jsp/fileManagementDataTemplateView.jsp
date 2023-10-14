


<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />

<!-- BEGIN CONTENT -->
<div class="page-content-wrapper">
    <!-- BEGIN CONTENT BODY -->
    <div class="page-content">     
        <!-- END PAGE HEADER-->
        <div class="row">
            <div class="col-md-6" style="width:100%;">
                <!-- BEGIN SAMPLE FORM PORTLET-->
                <div class="portlet light bordered init">
                    <div class="portlet-title">
                        <div class="caption font-red-sunglo">
                            <span class="caption-subject bold ">View Data Template :${name} </span>
                        </div>
                    </div>
                    <div class="portlet-body form">
                               		            	
			            		<div class="form-body">
				                    <div class="row">
				                        <div class="col-md-6 col-capture">
				                            <div class="form-group form-md-line-input">
				                                <input type="text" class="form-control catch-error" id="nameid"  value="${name}" readonly>
				                                <label for="form_control">Name</label>
				                            </div>
				                            <span class="required"></span>
				                        </div>
				                        <div class="col-md-6 col-capture">
				                            <div class="form-group form-md-line-input">
				                                <input type="text" class="form-control catch-error" id="descid"  value="${description}" readonly>
				                                <label for="form_control">Description</label>
				                            </div>
				                            <span class="required"></span>
				                        </div>
				                    </div></br>
				                    <div class="row">    
				                        <div class="col-md-6">
				                            <div class="form-group form-md-line-input">
				                                <input type="text" class="form-control" id="locationid"  value="File Management" readonly>
				                                <label for="form_control">Source Type</label>
				                            </div>
				                        </div>

				                        <div class="col-md-6">
				                            <div class="form-group form-md-line-input">
				                                <input type="text" class="form-control" id="createdid" value="Administrator" readonly>
				                                <label for="form_control">User Name</label>
				                            </div>
				                        </div>

				                    </div></br>
									<c:forEach var="listDataAccessData" items="${listDataAccessData}">
				                    <div class="row">
				                        <div class="col-md-6">
				                            <div class="form-group form-md-line-input">
				                                <input type="text" class="form-control" id="createdid" value="${listDataAccessData.hostName}" readonly>
				                                <label for="form_control">Host Name/URI</label>
				                            </div>
				                        </div>

				                        <div class="col-md-6">
				                            <div class="form-group form-md-line-input">
				                                <input type="text" class="form-control" id="createdid" value="${listDataAccessData.folderName}" readonly>
				                                <label for="form_control">Folder Name</label>
				                            </div>
				                        </div>
				                       
				                    </div></br>
</c:forEach>
				                    <div class="row">
				                        <div class="col-md-12">
				                            <div class="form-group form-md-line-input">
				                                <input type="text" class="form-control" value="${referenceFiles}" readonly>
				                                <label for="form_control">Reference Files</label>
				                            </div>
				                        </div>
				                       
				                    </div></br>
				               

			            		</div>	                                     
			            	</form>                                                           
                    	</div>
                	</div>
                	<div class="note note-info hidden">
                        Data Template deleted Successfully
                    </div>
                    <div class="note note-danger hidden">
                    </div>
                <!-- END SAMPLE FORM PORTLET-->
            </div>
        </div>
    </div>
</div>
<!-- END QUICK SIDEBAR -->

<jsp:include page="footer.jsp" />

