<%@page import="com.databuck.dao.impl.TemplateViewDAOImpl"%>

<%@page import="java.util.Iterator"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<jsp:include page="header.jsp" />
<jsp:include page="container.jsp"/>
<jsp:include page="checkVulnerability.jsp" />

<script>
	var request;
	var vals = "";
	function sendInfo() {
		var v = document.getElementById("referencename").value;
		var val = "Name must begin with a letter and cannot contain spaces,special characters"
		var expr = /^[a-zA-Z0-9_]*$/;
	    if (!expr.test(v)) {
	     	document.getElementById('amit1').innerHTML = val;
	        return;
	    }
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
			console.log(val);
			document.getElementById('amit1').innerHTML = val;
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
					
					<form id="formAddBatchValidation" action="createReferencesTemplate" enctype="multipart/form-data"
							method="POST" accept-charset="utf-8">
					
						<div class="portlet-body form">

							<div class="form-body">
								<div class="row non-schema-matching">
									<!-- <div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="file" class="form-control catch-error"
												id="configPath1" name="configPath1"
												placeholder="Choose Config File"> 
												<label for="form_control_1">Choose Config File *</label>
											 <span class="help-block">Data Set Name </span>
										</div>
										<span id="amit" class="help-block required"></span>
									</div>  -->
										
									 <div class="col-md-6 col-capture">
									
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control catch-error"
												id="referencename" name="referencename"
												placeholder="Enter Internal References Name" onkeyup="sendInfo()"
											> <label for="form_control_1">References Name</label>
											
										</div>
										<span id="amit1" class="help-block required"></span>
									</div>
									<div class="col-md-6 col-capture" id="file">
										<div class="form-group form-md-line-input">
												<input type="file" class="form-control catch-error" id="configFile" name="configFile" placeholder="Choose Config File"> 
											<label for="form_control_1">Select References File *</label>
											<span class="help-block">Data Set Name </span>

										</div>
										<span id="amit" class="help-block required"></span>
									</div>
									
									
								</div>
                                <div class="row">
									
								</div>
								</div>
								<div class="form-actions noborder align-center">
									<button onClick="return submitReferenceFile()" type="submit" id="batch" class="btn blue">Submit</button>
								</div>
								<div id="tempview">
								</div>
								<br> <br>

							</div>
					</form>
				</div>
			</div>
		</div>
	</div>
</div>




<jsp:include page="footer.jsp" />
<head>


<script type="text/javascript">

$("#type").change(function(){
	   
	  var sBatchValType = $('#type option:selected').val();
	 console.log(sBatchValType);
	 if((sBatchValType == "External_r")){
		
		 
		 $( "#file" ).addClass( "hidden" );
	 }
	 else{		
		 
		 $( "#file" ).removeClass( "hidden" );
	 }
	});

</script>

<script type="text/javascript">

function submitReferenceFile(){
	var no_error = 1;
	$('.input_error').remove();
	var amit2 = $('#amit1').html();
	// alert("outside"+amit1)
	console.log(amit2);
	if (amit2 == "") {

	} else {
		console.log("inside if");
		no_error = 0;
	}
	if ($("#configFile").val().length == 0) {
		$('<span class="input_error" style="font-size:12px;color:red">Please Upload a File</span>')
				.insertAfter($('#configFile'));
		no_error = 0;
	}
		
	if ($("#referencename").val().length == 0 && amit2 == "") {
		$('<span class="input_error" style="font-size:12px;color:red">Please enter reference name</span>')
				.insertAfter($('#referencename'));
		no_error = 0;
	}
	
	if (no_error == 0) {
		$('.btn').removeClass('hidden');
		return false;
	} else {
		$('.btn').addClass('hidden');
		return true;
	}	
}

</script>


</head>
