<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />


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
							<span class="caption-subject bold ">File Monitoring</span>
						</div>
					</div>
					<%
								//int idApp = Integer.parseInt(request.getAttribute("idApp"));
								Object objIdApp = request.getAttribute("idApp");
								String idApp = objIdApp.toString();
								
								session.setAttribute("idAppVal", idApp);
								
								System.out.println("idApp JSP!!!!!!!!!=>"+idApp);
							%>

					<div class="portlet-body form">
						<form id="tokenForm1" action="submitFileMonitoringCSV"
							enctype="multipart/form-data" method="POST"
							accept-charset="utf-8">
							<div class="form-body">

								<div class="row">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<div class="row">
												<div class="col-md-6 col-capture" id="uploaddivid">
													<div class="form-group form-md-line-input">
														<input type="file" class="form-control" name="dataupload"
															id="dataupload_id" /> <label for="form_control"
															id="fileType">File Monitoring Data File</label>
															<input type="hidden" name="fileMonitoringType" value="${fileMonitoringType}" />
															<input type="hidden" name="connectionId" value="${connectionId}" />
													</div>
													<br/>
												</div>

											</div>
										</div>
										<div class="row">
											<div class="form-actions noborder align-center">
												<button type="submit" id="submitBtn" class="btn blue">Submit</button>
											</div>
										</div>

									</div>
									 <div class="col-md-6 col-capture">
                                        <a onClick="validateHrefVulnerability(this)"  href="getFileMonitoringImportSampleFile">
                                            <img alt="" src="./assets/img/downloadCsvIcon.png"  onClick="" width='45px' height='45px'/><b>Snowflake FM Rules template</b>
                                        </a>
                                    </div>
								</div>

							</div>

						</form>

					</div>
				</div>


			</div>

		</div>
	</div>
</div>
<!-- <script>
        $(document).ready(function() {
        	$('#tokenForm1').ajaxForm({
        		headers: {'token':$("#token").val()}
        	});
        });
</script> -->

<jsp:include page="footer.jsp" />
<head>
<script src="./assets/global/plugins/jquery-ui.min.js"></script>
<script
	src="./assets/global/plugins/bootstrap-multiselect.js"></script>
<link rel="stylesheet"
	href="./assets/global/plugins/bootstrap-multiselect.css">

<script type="text/javascript" src="./assets/global/plugins/jsapi.js"></script>
<script type="text/javascript"></script>
</head>