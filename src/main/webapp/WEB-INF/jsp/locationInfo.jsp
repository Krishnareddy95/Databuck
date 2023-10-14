<%@page import="java.util.Map"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="com.databuck.service.RBACController"%>
<link rel="stylesheet" href="./assets/global/plugins/bootstrap.min.css">
<jsp:include page="header.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<jsp:include page="container.jsp" />
<style>
	.alert {
		background-color: red;
	}

	.warning {
		background-color: yellow;
	}

	.safe {
		background-color: green;
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

					<!-- New Changes given by Dutta 23/12/2019 -->

					<!-- <div style="padding-left: 20px; padding-right: 20px; padding-top: 20px; ">
				<div class="align-center" style="background-color: #337ab7; height: 40px; padding-top: 10px; padding-bottom: 10px;">
					<button type="submit" id="quickStart" class="btn blue" style="padding-left: 80px; padding-right: 70px; padding-bottom: 10px;">Quick Start</button>
					<a onClick="validateHrefVulnerability(this)"  href="getQuickStartHome" style="color: white;"><span class="title" style="color: white;">QUICK START</span></a>
				</div>
			</div> -->
					<!-- 					<div class="cotainer">
						<ul class="nav nav-pills">
							<li class="nav-item">
								<div class="col-md-12 col-capture"
									style="background-color: #337ab7; height: 40px; padding-top: 10px; padding-bottom: 10px; padding-left: 10px;">
									<a onClick="validateHrefVulnerability(this)"  href="dashboard_View" style="color: white;"><span
										class="title" style="color: white;">Executive Summary</span></a>

								</div>
							</li>

							<li class="nav-item">
								<div class="col-md-12 col-capture"
									style="background-color: #337ab7; height: 40px; padding-top: 10px; padding-bottom: 10px; padding-left: 10px;">
									<a onClick="validateHrefVulnerability(this)"  href="dashboard_View" style="color: white;"><span
										class="title" style="color: white;">Detailed View</span></a>

								</div>
							</li>

							<li class="nav-item">
								<div class="col-md-12 col-capture"
									style="background-color: #337ab7; height: 40px; padding-top: 10px; padding-bottom: 10px;">
									<a onClick="validateHrefVulnerability(this)"  href="getQuickStartHome" style="color: white;"><span
										class="title" style="color: white;">DQ Universe</span></a>

								</div>
							</li>
						</ul>
					</div> -->


					<div class="cotainer">

						<div class="tabbable tabbable-custom boxless tabbable-reversed">
							<ul class="nav nav-tabs" style="border-bottom: 1px solid #ddd;">
								<li style="width: 8%">
									<a onClick="validateHrefVulnerability(this)"  href="executiveSummary" style="padding: 2px 2px;"><strong>Executive
											Summary</strong></a>
								</li>

								<li style="width: 8%">
									<a onClick="validateHrefVulnerability(this)"  href="dashboard_View" style="padding: 2px 2px;"><strong>Detailed
											View</strong></a>
								</li>

								<li style="width: 8.63%">
									<a onClick="validateHrefVulnerability(this)"  href="dqUniverse" style="padding: 2px 2px;"><strong>DQ Universe</strong></a>
								</li>

								<li class="active" style="width: 8%">
									<a onClick="validateHrefVulnerability(this)"  href="locationInfo" style="padding: 2px 2px;"><strong>DQ Lineage</strong></a>
								</li>
								<%
								boolean flag = false;
                                flag = RBACController.rbac("MyViews", "R", session);
                            if (flag) {
                            %>
								<li style="width: 8%"><a onClick="validateHrefVulnerability(this)"  href="myViews" style="padding: 2px 2px;"><strong>My
											Views</strong></a></li>
											<% } %>
							</ul>
						</div>
					</div>




					<div class="portlet-title">
						<table class="table-bordered">
							<c:forEach var="innerList" items="${tabledata}">
								<tr>
									<c:forEach var="obj" items="${innerList}">
										${obj}
									</c:forEach>
								<tr>
							</c:forEach>
						</table>
						<div id="myModal" class="modal fade" role="dialog">
							<div class="modal-dialog">

								<!-- Modal content-->
								<div class="modal-content">
									<div class="modal-header">
										<button type="button" class="close" data-dismiss="modal">&times;</button>
									</div>
									<div class="modal-body">
									</div>
									<div class="modal-footer">
										<button type="button" class="btn btn-danger" data-dismiss="modal">Close</button>
									</div>
								</div>

							</div>
						</div>
						<style>
							.modal {
								text-align: center;
								padding: 0 !important;
							}

							.modal:before {
								content: '';
								display: inline-block;
								height: 100%;
								vertical-align: middle;
								margin-right: -4px;
								/* Adjusts for spacing */
							}

							.modal-dialog {
								display: inline-block;
								text-align: left;
								vertical-align: middle;
								width: 100%;
							}
						</style>
						<!-- jQuery -->
						<script src="./assets/global/plugins/jquery.min.js"></script>
						<!-- BS JavaScript -->
						<script src="./assets/global/plugins/bootstrap.min.js"></script>
						<!-- Have fun using Bootstrap JS -->
						<script type="text/javascript">
							function callFuncation(item) {
								$('.modal-body').empty();
								$('#myModal').modal('show');
								var parentId = $(item).attr("id");
								var $options = $('#' + parentId + " #table-cell-data #table-cell-data2").clone();
								$('.modal-body').append($options);
								//$('#'+parentId+" #table-cell-data #table-cell-data2"+':not(:has(.info))').hide()

							}

							function callFuncation1(item) {
								window.location.href = './dashboard_table?idApp=' + item;

							}

							/* collect selected projec id and load it into globalProjectId */
							var globalProjectId = "";

							$("#projectid span").click(function () {
								globalProjectId = $(this).text();
								console.log($(this).text());
								var strErrorMsg = "";

								if (globalProjectId == "") {
									strErrorMsg = strErrorMsg + '<br> Please Select Project ID';
								}

								if (strErrorMsg != "") {
									toastr.info(strErrorMsg);
								} else {
									window.location.href = './locationInfo?project=' + globalProjectId;
								}
							});
						</script>
					</div>

				</div>
			</div>
		</div>
	</div>
</div>
<jsp:include page="footer.jsp" />