<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<head>
<script type="text/javascript" src="./assets/global/plugins/jsapi.js"></script>
<script src="./assets/global/plugins/jquery-ui.min.js"></script>
<script
	src="./assets/global/plugins/jquery.min.js"></script>
<script
	src="./assets/global/plugins/bootstrap-multiselect.js"></script>
<link rel="stylesheet"
	href="./assets/global/plugins/bootstrap-multiselect.css">
<style>
.fbox {
	width: 180px;
	height: 200px;
}

.qbutton {
	background-color: #337ab7;
	height: 40px;
	padding-top: 10px;
	padding-bottom: 10px;
	width: 100px;
	padding-bottom: 10px;
}

.qhr {
	display: block;
	margin: unset;
	border-top: 2px solid #3598dc;
	margin-top: 20px;
}

.qHeaderDiv {
	padding-left: 170px;
	width: 450px;
	height: 50px;
	text-align: center;
}

.qClickDiv {
	font-size: x-small;
	text-align: right;
	padding-right: 2px;
}

.qNameDiv {
	color: red;
	text-align: center;
	padding-top: 10px;
	font-size: medium;
	padding-bottom: 10px;
}
</style>
</head>

<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<!-- BEGIN CONTENT -->
<div class="page-content-wrapper">
	<!-- BEGIN CONTENT BODY -->
	<div class="page-content">

		<!-- BEGIN SAMPLE FORM PORTLET-->
		<div class="portlet light bordered init">
			<div class="row">
				<div class="col-md-6" style="width: 40%;">
					<div>
						<img src="./assets/img/quickstartbg.jpg" alt="Image"
							style="width: 100%; height: max-content%;">
					</div>
					<div>
						<div style="padding-left: 160px; padding-top: 40px;">
							<img alt="" src="./assets/img/symbol.jpg" style="width: 100;">
						</div>
						<div class="qNameDiv">
							<b>DataBuck 3.0</b>
						</div>
					</div>
					<div style="padding-left: 50px; width: 370px; text-align: center;">
						<div class="portlet light bordered init"
							style="background: lightgrey; padding-top: 30px; padding-bottom: 30px;">
							<div style="color: red;">Your all-in-one, end-to-end</div>
							<div style="color: red;">data quality and matching
								platform.</div>
							<div style="color: red; padding-top: 20px;">DISCOVER RULES
								- MONITOR QUALITY</div>
						</div>
					</div>
					<div style="color: black; text-align: center; padding-top: 40px;">Learn
						More</div>
				</div>
				<div class="col-md-6" style="width: 60%;">
					<div class="portlet light bordered init"
						style="padding-bottom: unset;">
						<div class="portlet-body form">
							<div class="portlet-title">
								<div class="caption font-red-sunglo">
									<div class="align-center">
										<span class="caption-subject bold">GETTING STARTED WITH
											DATABUCK</span>
									</div>
								</div>
							</div>
							<div style="padding-top: 40px; padding-bottom: 20px;">
								<label for="form_control_1">Project Type *</label> <select
									id="featureId" name="feature">
									<option value="dataquality">DataQuality</option>
									<option value="datamatching">DataMatching</option>
								</select>
							</div>

							<div class="portlet light bordered init">
								<div id="dq_form_div_id" class="portlet-body form">
									<form id="myForm" action="processDQQuickStart"
										enctype="multipart/form-data" method="POST"
										accept-charset="utf-8">
										<div class="form-body">
											<div class="row">
												<div class="col-md-6 col-capture" id="uploaddivid">
													<div class="form-group form-md-line-input">
														<input type="file" class="form-control" name="dataupload"
															id="dataupload_id" /> <label for="form_control"
															id="fileType">Upload file to be analyzed</label>
													</div>
													<br />
												</div>
											</div>
										</div>

										<div class="row">
											<div class="noborder align-center">
												<button type="submit" id="submitBtn" class="btn blue">Submit</button>
											</div>
										</div>
									</form>
								</div>

								<div id="dm_form_div_id" class="portlet-body form hidden">
									<form  id="myForm" action="processDMQuickStart"
										enctype="multipart/form-data" method="POST"
										accept-charset="utf-8">
										<div class="form-body">
											<div class="row">
												<div class="col-md-6 col-capture" id="uploaddivid">
													<div class="form-group form-md-line-input">
														<input type="file" class="form-control"
															name="srcDataUpload" id="srcdataupload_id" /> <label
															for="form_control" id="srcfileType">Source Data
															File</label>
													</div>
													<br />
												</div>
												<div class="col-md-6 col-capture" id="uploaddivid">
													<div class="form-group form-md-line-input">
														<input type="file" class="form-control"
															name="targetDataUpload" id="trgtdataupload_id" /> <label
															for="form_control" id="trgtfileType">Target Data
															File</label>
													</div>
													<br />
												</div>
											</div>
										</div>

										<div class="row">
											<div class="noborder align-center">
												<button type="submit" id="submitBtn" class="btn blue">Submit</button>
											</div>
										</div>
									</form>
								</div>
							</div>

							<div id="dq_div_id">
								<hr class="qhr">
								<div class="qHeaderDiv">
									<div class="portlet light bordered init"
										style="background: #3598dc; padding-bottom: 5px; padding-top: 5px;">
										<div style="color: white;">DATA QUALITY CHALLENGE</div>
									</div>
								</div>
								<div class="portlet-body">
									<div class="row">
										<div class="col-md-6" style="width: unset;">
											<div class="portlet light bordered init fbox"
												style="padding: 3px;">
												<div>
													<a onClick="validateHrefVulnerability(this)"  href="processDQForSampleFile?filename=ms4.csv"><img
														alt="" src="./assets/img/financedata.jpg"
														style="width: 100%;"></a>
												</div>
												<div style="padding: 10px">Figure out interesting
													facts about finance data</div>
												<div class="qClickDiv">click on image to run</div>
											</div>
										</div>
										<div class="col-md-6" style="width: unset;">
											<div class="portlet light bordered init fbox"
												style="padding: 3px;">
												<div>
													<a onClick="validateHrefVulnerability(this)"
														href="processDQForSampleFile?filename=insurancedata1.csv"><img
														alt="" src="./assets/img/insurancedata.jpg"
														style="width: 100%;"></a>
												</div>
												<div style="padding: 10px">Explore the insurance
													claims dataset</div>
												<div class="qClickDiv">click on image to run</div>
											</div>
										</div>
										<div class="col-md-6" style="width: unset;">
											<div class="portlet light bordered init fbox"
												style="padding: 3px;">
												<div>
													<a onClick="validateHrefVulnerability(this)"  href="processDQForSampleFile?filename=SalesJan2009.csv"><img
														alt="" src="./assets/img/salesdata.jpg"
														style="width: 100%;"></a>
												</div>
												<div style="padding: 10px">Analyze sales data of
													January 2009</div>
												<div class="qClickDiv">click on image to run</div>
											</div>
										</div>
									</div>
								</div>
							</div>

							<div id="dm_div_id" class="hidden">
								<hr class="qhr">
								<div class="qHeaderDiv">
									<div class="portlet light bordered init"
										style="background: #3598dc; padding-bottom: 5px; padding-top: 5px;">
										<div style="color: white;">DATA MATCHING CHALLENGE</div>
									</div>
								</div>
								<div class="portlet-body">
									<div class="row">
										<div class="col-md-6" style="width: unset;">
											<div class="portlet light bordered init fbox"
												style="padding: 3px;">
												<div>
													<a onClick="validateHrefVulnerability(this)"
														href="processDMForSampleFile?srcFileName=ms4.csv&trgtFileName=ms5.csv"><img
														alt="" src="./assets/img/financedata.jpg"
														style="width: 100%;"></a>
												</div>
												<div style="padding: 10px">Figure out interesting
													facts about finance datasets</div>
												<div class="qClickDiv">click on image to run</div>
											</div>
										</div>
										<div class="col-md-6" style="width: unset;">
											<div class="portlet light bordered init fbox"
												style="padding: 3px;">
												<div>
													<a onClick="validateHrefVulnerability(this)"
														href="processDMForSampleFile?srcFileName=insurancedata1.csv&trgtFileName=insurancedata2.csv"><img
														alt="" src="./assets/img/insurancedata.jpg"
														style="width: 100%;"></a>
												</div>
												<div style="padding: 10px">Explore the insurance
													claims datasets</div>
												<div class="qClickDiv">click on image to run</div>
											</div>
										</div>
										<div class="col-md-6" style="width: unset;">
											<div class="portlet light bordered init fbox"
												style="padding: 3px;">
												<div>
													<a onClick="validateHrefVulnerability(this)"
														href="processDMForSampleFile?srcFileName=SalesJan2009.csv&trgtFileName=SalesJan2010.csv"><img
														alt="" src="./assets/img/salesdata.jpg"
														style="width: 100%;"></a>
												</div>
												<div style="padding: 10px">Match sales data of
													January 2009 and 2010</div>
												<div class="qClickDiv">click on image to run</div>
											</div>
										</div>
									</div>
								</div>
							</div>

						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>


<script type="text/javascript">
	$('#featureId').change(function() {
		if ($('#featureId').val() == 'dataquality') {
			$('#dm_div_id').addClass('hidden');
			$('#dm_form_div_id').addClass('hidden');
			$('#dq_div_id').removeClass('hidden');
			$('#dq_form_div_id').removeClass('hidden');
		} else {
			$('#dq_div_id').addClass('hidden');
			$('#dq_form_div_id').addClass('hidden');
			$('#dm_div_id').removeClass('hidden');
			$('#dm_form_div_id').removeClass('hidden');
		}

	});
</script>
<!-- <script> 
        // wait for the DOM to be loaded 
        $(document).ready(function() { 
        	$('#myForm').ajaxForm({
        		headers: {'token':$("#token").val()}
        	}); 
        }); 
 </script> -->
<jsp:include page="footer.jsp" />