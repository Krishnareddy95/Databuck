
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<jsp:include page="header.jsp" />

<jsp:include page="container.jsp" />
<style>

.amar { 
  width: 100%;
  height: 410px;
  border: 1px groove #b3b3b3;
  background-color: #d9d9d9;
  padding: 5px;
  overflow: auto;
  font-family: Courier New;
  font-size: 12.5px;     
}

</style>
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
							<span class="caption-subject bold "> Download Log Files </span>
						</div>
					</div>
					<div class="portlet-body form">
						<form>
							<div class="form-body">
								<div class="row">
									<div class="col-md-12">
										<div class="col-md-3">
											<label class="form_control_1">Download Showio.txt</label>
											<div class="form-actions noborder">
												<button type="submit" onClick="BtnShowLog('batchExecution'); return false;" class="btn blue">DOWNLOAD</button>
											</div>
										</div>
										<div class="col-md-3">
											<label class="form_control_1">Download Showio1.txt</label>
											<div class="form-actions noborder">
												<button type="submit" onClick="BtnShowLog('timeExecution'); return false;" class="btn blue">DOWNLOAD</button>
											</div>
										</div>
										<div class="col-md-3">
											<label class="form_control_1">Download Catalina.out</label>
											<div class="form-actions noborder">
												<button type="submit" onClick="BtnShowLog('uiLogs'); return false;" class="btn blue">DOWNLOAD</button>
											</div>
										</div>
										<div class="col-md-3">
											<label class="form_control_1">Fetch List of Audit Logs</label>
											<div class="form-actions noborder">
												<button type="submit" onClick="BtnGetLogFileList(); return false;" class="btn blue">Fetch File List</button>
											</div>
										</div>
									</div>
										<div class="col-md-3">
											<label for="LogFileList">Audit Log Files</label>										
											<select class="form-control"  id="LogFileList" onchange="BtnShowLog('auditLogs', this)">
											</select>
											<br><br>
										</div>											
									<div class="col-md-12">

										<!-- <input type='text' id="ValidationExecLog" rows="30" cols="150" class="amar"
											style="width: 100%; height: 100px"; font-family: Courier New; background-color: #d9d9d9;"
											readonly> -->
									<div id="ValidationExecLog" class="amar">
									</div>
							
								</div>								
								<br> <br>
							</div>

						</form>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<jsp:include page="footer.jsp" />

<script>

function BtnGetLogFileList() {	
	var oData = {};	
	
	$.ajax({
		type: "POST",
		headers: { 'token':$("#token").val()},
      datatype: 'json',
		url: "getValidationLogFileList",
		timeout: 1000,		
		data: oData,		
		success: function (sResponse) {
			var oResponse = JSON.parse(sResponse);
			toastr.info(oResponse.msg);
			populateLogFiles(oResponse);
		},
		error: function (oError) {
			toastr.info('There was problem');
		}
	});
	return false;	
	
	function populateLogFiles(oResponse) {
		var oLogFiles = document.getElementById('LogFileList'), aLogFiles = JSON.parse(oResponse.logfiles);			
		
		while (oLogFiles.options.length > 0) { oLogFiles.options.remove(0); }
		
		oOption = document.createElement("OPTION");
		oOption.text = 'Please select log file';
		oOption.value = '-1';

		oLogFiles.add(oOption);		

		aLogFiles.forEach(function(sFileName) {
				oOption = document.createElement("OPTION");
				oOption.text = sFileName;
				oOption.value = sFileName;

				oLogFiles.add(oOption);
		});		
	}
}

function BtnShowLog(sWhichLog, oSelectList) {	
	var sFileName = '', oData = {}, lCallController = true;
	
	if (oSelectList !== undefined) {
		sFileName = $('#LogFileList').find(":selected").val();
		sFileName = (sFileName !== '-1') ? sFileName.substring(0, sFileName.indexOf(' ')) : '';	
		lCallController = (sFileName.length > 0) ? true : false;
	}	
	
	oData = { "sWhichLog": sWhichLog, sSelectedLogFile: sFileName };
	
	if (lCallController) {
		$.ajax({
			type: "POST",
			headers: { 'token':$("#token").val()},
			datatype: 'json',
			url: "showValidationLog",
			timeout: 1000,		
			data: oData,		
			success: function (sResponse) {
				var oResponse = JSON.parse(sResponse);
				toastr.info(oResponse.msg);
				$("#ValidationExecLog").empty();
				$("#ValidationExecLog").append('<h3>' + sFileName + '</h3>' + oResponse.loglines);
			},
			error: function (oError) {
				toastr.info('There was problem');
			}
		});
	} else {
		toastr.info('No log file selected');
	}				
	return false;
}

</script>

<!-- ========================END THEME GLOBAL SCRIPTS =========================-->

<script type="text/javascript"
	src="./assets/global/plugins/jquery.dataTables.min.js"></script>
<script type="text/javascript"
	src="./assets/global/plugins/dataTables.bootstrap.min.js"></script>
<script type="text/javascript"
	src="./assets/global/plugins/dataTables.responsive.min.js"></script>
<!-- <script type="text/javascript"  src="https://cdn.datatables.net/responsive/2.0.2/js/responsive.bootstrap.min.js"></script> -->

<!-- =========================BEGIN PAGE LEVEL SCRIPTS============================== -->