<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<jsp:include page="header.jsp" />

<jsp:include page="container.jsp" />

<div class="page-content-wrapper">
	<!-- BEGIN CONTENT BODY -->
	<div class="page-content">
		<!-- BEGIN PAGE TITLE-->
		<!-- END PAGE TITLE-->
		<!-- END PAGE HEADER-->
		<div class="row">
			<div class="col-md-12">

				<!--****         BEGIN EXAMPLE TABLE PORTLET       **********-->

				<div class="portlet light bordered">
					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold ">License Information</span>
						</div>
					</div>
					<%java.text.DateFormat df = new java.text.SimpleDateFormat("dd.MMMM.yyyy"); %>
					<div class="form-body">
						<div class="row">
							<div class="col-md-6 col-capture">
								Version Number: <%= session.getAttribute("VersionNumber") %>
								<br /> <span class="required"></span>
							</div>
						</div>
						<br />
						<div class="row">
							<div class="col-md-6 col-capture">
								License Expiry Date: <%= df.format(session.getAttribute("licenseExpiryDate")) %>
								<br /> <span class="required"></span>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<jsp:include page="footer.jsp" />