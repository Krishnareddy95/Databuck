
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />

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
							<span class="caption-subject bold ">${msg}</span>
							<br/><br>
							<span
							style="font-size: small;">
							<a onClick="validateHrefVulnerability(this)"  href="logout">(Kindly click here to logout and login again for domain and project changes to take effect)</a></span>
						    <br/><br>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>



<jsp:include page="footer.jsp" />
