   <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
   <jsp:include page="header.jsp"/>
   <jsp:include page="container.jsp"/>
   <jsp:include page="checkVulnerability.jsp" />
   <script>
	var request;
	var vals="";
	function sendInfo() {
		var v = document.getElementById("roleNameId").value;
		//var v=document.vinform.Database.value;  
		var url = "./duplicateRoleName?val=" + v;
		//alert("in ajax code");
		if (window.XMLHttpRequest) {
			request = new XMLHttpRequest();
		} else if (window.ActiveXObject) {
			request = new ActiveXObject("Microsoft.XMLHTTP");
		}

		try {
			request.onreadystatechange = getInfo;
			request.open("POST", url, true);
			request.setRequestHeader('token',$("#token").val());
			request.send();
		} catch (e) {
			alert("Unable to connect to server");
		}
	}

	function getInfo() {
		if (request.readyState == 4) {
			var val = request.responseText;
			//alert("getInfo");
			//alert(val);
			//var sal="Database already exists";
			//console.log(val);
			document.getElementById('amit').innerHTML = val;
		 	if (val!="") {
				vals=val;
				//alert("please enter another name");
				return false;
			}
			//alert("check123");
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
                                <span class="caption-subject bold "> Add New Role </span>
                            </div>
                        </div>
                        <div class="portlet-body form">
                            <input type="hidden" id="toUrl" value="saveNewRoleIntoDatabase" />
                                <div class="form-body">
                                    <div class="row">
                                        <div class="col-md-6 col-capture">
                                            <div class="form-group form-md-line-input">
                                                <input type="text" class="form-control catch-error" id="roleNameId" name="roleName" placeholder="Enter your Role name" onkeyup="sendInfo()">
                                                <label for="form_control_1">Role Name *</label>
                                            </div>
                                           <span id="amit" class="help-block required"></span>
                                        </div>
                                        <div class="col-md-6 col-capture">
                                            <div class="form-group form-md-line-input">
                                                <input type="text" class="form-control" id="descriptionId" name="description" placeholder="Enter your description  ">
                                                <label for="form_control_1">Description</label>
                                                <span class="help-block">Short description about Role</span>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="col-md-12 col-capture">
                                            <label for="form_control_1">Select the Access Control</label>
                                        </div>
                                    </div>
                                    <c:forEach var="modules" items="${Modules}">

													<%-- <option value="${modules.key}">${modules.value}</option>

												</c:forEach> --%>
                                    <div class="row">
                                        <div class="col-md-12">
                                            <div class="form-group form-md-checkboxes">
                                                <div class="md-checkbox-list">
                                                    <div class="md-checkbox">
                                                        <input type="checkbox" id="taskId${modules.key}" name="${modules.value}" class="md-check accessControl" value="${modules.key}">
                                                        <label for="taskId${modules.key}">
                                                            <span></span>
                                                            <span class="check"></span> 
                                                            <span class="box"></span> ${modules.value} </label>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div id="taskId${modules.key}Check" class="roleHide hidden" style="padding:0 0 0 5%;">
                                        <div class="row">
                                            <div class="col-md-6">
                                                <div class="form-group form-md-checkboxes">
                                                    <div class="md-checkbox-inline">
                                                        <div class="md-checkbox">
                                                            <input type="checkbox" id="${modules.key}C" name="${modules.key}C" class="md-check accessControlCheck" value="C">
                                                            <label for="${modules.key}C">
                                                                <span></span>
                                                                <span class="check"></span>
                                                                <span class="box"></span> C </label>
                                                        </div>
                                                        <div class="md-checkbox">
                                                            <input type="checkbox" id="${modules.key}R" name="${modules.key}R" class="md-check accessControlCheck" value="R">
                                                            <label for="${modules.key}R">
                                                                <span></span>
                                                                <span class="check"></span>
                                                                <span class="box"></span> R </label>
                                                        </div>
                                                        <div class="md-checkbox">
                                                           <input type="checkbox" id="${modules.key}U" name="${modules.key}U" class="md-check accessControlCheck" value="U">
                                                            <label for="${modules.key}U">
                                                                <span></span>
                                                                <span class="check"></span>
                                                                <span class="box"></span> U </label>
                                                        </div>
                                                        <div class="md-checkbox">
                                                            <input type="checkbox" id="${modules.key}D" name="${modules.key}D" class="md-check accessControlCheck" value="D">
                                                            <label for="${modules.key}D">
                                                                <span></span>
                                                                <span class="check"></span>
                                                                <span class="box"></span> D </label>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    </c:forEach>
                                    <br/>
                                    <div class="row">
                                        <div class="note note-danger hidden">
                                        </div>
                                    </div>
                                </div>
                                <div class="form-actions noborder align-center">
                                    <button type="submit" class="btn blue" id="submitRoleId">Submit</button>
                                </div>
                        </div>
                    </div>
                    <div class="note note-info hidden">
                        Group Created Successfully
                    </div>
                    <!-- END SAMPLE FORM PORTLET-->
                </div>
            </div>
        </div>
    </div>
    <!-- END QUICK SIDEBAR -->
<jsp:include page="footer.jsp"/>