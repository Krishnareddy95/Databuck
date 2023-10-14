<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="com.databuck.service.RBACController"%>

<jsp:include page="header.jsp" />
<jsp:include page="checkVulnerability.jsp" />
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

								<li class="active" style="width: 8.63%">
									<a onClick="validateHrefVulnerability(this)"  href="dqUniverse" style="padding: 2px 2px;"><strong>DQ Universe</strong></a>
								</li>
								<li style="width: 8%">
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

						<div class="caption font-red-sunglo">
							<span class="caption-subject bold ">Force-Directed Graph
							</span>
						</div>
					</div>

				</div>
			</div>
		</div>
	</div>
</div>
<jsp:include page="footer.jsp" />