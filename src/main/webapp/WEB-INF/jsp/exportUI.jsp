<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<script src="./assets/global/plugins/jquery-1.12.4.min.js"></script>
<script src="./assets/js/maindb98.js?db98" type="text/javascript"></script>
<script>
	var request;
	var vals = "";
	function sendInfo() {
		var v = document.getElementById("datasetid").value;
		//var v=document.vinform.Database.value;  
		var url = "./duplicatedatatemplatename?val=" + v;
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
			//alert("Unable to connect to server");
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
			if (val != "") {
				vals = val;
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
			<div class="col-md-6" style="width: 100%;">
				<!-- BEGIN SAMPLE FORM PORTLET-->
				<div class="portlet light bordered init">
					<div class="portlet-title">
				
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold "> Export </span>
						</div>
				
					</div>
					<div class="portlet-body form">
						<input type="hidden" id="tourl" value="createForm" /> <input
							type="hidden" id="currSelectedTable" value="" /> <input
							type="hidden" id="queryInputTaken" value="" />

					
							<div class="form-body">
							
								<div class="row">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input" id="secretTokenDivId">
											<select class="form-control" id="apptypeid" name="apptype">
												
											
												<option value="-1">select...</option>
														<option value="dataConnection">Data Connection</option>
														<option value="dataTemplate">Data Template</option>

														<option value="validationCheck">Validation Check</option>
											
											</select> <label for="form_control_1">Data Operations *</label>
											<!-- <span class="help-block">Data Location  </span> -->



										</div>
										<br /> <span class="required"></span>
									</div>
						
								</div>
								

</div>
</div>
</div>
</div>
</div>

	</div>
</div>
<!-- END QUICK SIDEBAR -->
<!-- END CONTAINER -->
<%-- <jsp:include page="footer.jsp" /> --%>

<script>


$('#apptypeid').change(function() {
	//alert("hopipo");
	
	var selectedModule = $(this).children("option:selected").val();
	//alert("You have selected  - " + selectedModule);
	
	var sAjaxUrlTmpl ="",sWebAppBaseUrl ="",sActualAjaxUrl ="";
	sWebAppBaseUrl = getWebAppBaseUrl();
	
	if (selectedModule == "dataConnection") {
		//alert("In Data Connection");
		sAjaxUrlTmpl = "{0}exportDataConnectionView";
		sActualAjaxUrl = sAjaxUrlTmpl.replace('{0}', sWebAppBaseUrl);
		window.location = sActualAjaxUrl;
		//window.location = "../databuck/exportDataConnectionView";
		
	} else if (selectedModule == "dataTemplate") {
		//alert("In Data Template");
		sAjaxUrlTmpl = "{0}exportDataTemplateView";
		sActualAjaxUrl = sAjaxUrlTmpl.replace('{0}', sWebAppBaseUrl);
		window.location = sActualAjaxUrl;
		
		//window.location = "../databuck/exportDataTemplateView";
	} else {
		//alert("In Validation Check");
		sAjaxUrlTmpl = "{0}exportValidationCheck";
		sActualAjaxUrl = sAjaxUrlTmpl.replace('{0}', sWebAppBaseUrl);
		window.location = sActualAjaxUrl;
		
		//window.location = "../databuck/exportValidationCheck";
	}
	
});
</script>
<script
	src="./assets/global/plugins/bootstrap-multiselect.js"></script>
