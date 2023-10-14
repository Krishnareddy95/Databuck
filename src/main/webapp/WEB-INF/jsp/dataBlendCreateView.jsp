    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<jsp:include page="header.jsp"/>

<jsp:include page="container.jsp"/>
<jsp:include page="checkVulnerability.jsp" />
<script type="text/javascript">
function sendInfo() {
    var extendedTemplatename = document.getElementById("blendnameid").value;
    var expr = /^[a-zA-Z0-9_]*$/;
    if (!expr.test(extendedTemplatename)) {
    	document.getElementById("blendnameid").value ="";
        alert("Please Enter Extended Template Name without spaces and special characters");
    }
}

</script>

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
                                <span class="caption-subject bold "> Create Extended Template </span>
                            </div>
                        </div>
                        <div class="portlet-body form">
                            
                                <div class="form-body">
                                    <div class="row">
                                        
                                         <div class="col-md-6 col-capture">
                                            <div class="form-group form-md-line-input">
                                                <input type="text" class="form-control catch-error" id="blendnameid" maxlength="45" onkeyup="sendInfo()">
                                                <label for="blendnameid">Extended Template Name *</label >
                                               <!--  <span class="help-block">Data Set Name </span> -->
                                            </div><br/>
                                            <span class="required"></span>
                                        </div>

                                        <div class="col-md-6 col-capture">
                                            <div class="form-group form-md-line-input">
                                                <input type="text" class="form-control catch-error" id="blenddescid">
                                                <label for="blenddescid">Description</label>
                                               <!--  <span class="help-block">Data Set Name </span> -->
                                            </div><br/>
                                            <span class="required"></span>
                                        </div>
                                    </div>

                                    <div class="row">
                                        <div class="col-md-6 col-capture">
                                            <div class="form-group form-md-line-input">
                                                <select class="form-control" id="datasourceid" name="blendcolumn">
                                                <c:forEach var="getlistdatasourcesnameobj" items="${getlistdatasourcesname}">
                                                     
                                                     <option value="${getlistdatasourcesnameobj.idData}">${getlistdatasourcesnameobj.name}</option>

                                                </c:forEach>
                                            </select>
                                                <label for="columnid">Data Template for Modification</label><br>
                                            </div>
                                        </div>
                                    </div>

                                <div class="form-actions noborder align-center">
                                    <button type="submit" id="blendcreateid" class="btn blue">Submit</button>
                                </div>
                        </div>
                    </div>
                    <div class="note note-info hidden">
                        Blend created Successfully
                    </div>
                    <!-- END SAMPLE FORM PORTLET-->
                </div>
            </div>
        </div>
    </div>
    <!-- END QUICK SIDEBAR -->
</div>
<!-- END CONTAINER -->
<jsp:include page="footer.jsp"/>