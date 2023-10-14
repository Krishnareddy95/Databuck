<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<%@page isELIgnored="false" %>

<!-- BEGIN CONTENT -->
<div class="page-content-wrapper">
	<!-- BEGIN CONTENT BODY -->
	<div class="page-content">
		<!-- END PAGE HEADER-->
		<div class="row">
			<div class="col-md-6" style="width: 100%;">
				<!-- BEGIN SAMPLE FORM PORTLET-->
				<div class="portlet light bordered init">
					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold ">Delete Data Template :
								${listdatasource.name}</span> 
								<span class="caption-subject bold ">
								${msg} </span>
						</div>
					</div>
				
				<div class="portlet-body form">

					<form role="form" method="get" action="delete_Template">
						<input type="hidden" id="toUrl" value="update" /> <input
							type="hidden" id="editTaskFormId" value="yes"> <input
							type="hidden" id="editTaskId" name="idData"
							value="${listdatasource.idData}">

						<div class="form-body">
							<div class="row">
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error" id="name"
											value="${listdatasource.name }" readonly> <label
											for="form_control">Name</label>
									</div>
									<span class="required"></span>
								</div>
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											id="description" value="${listdatasource.description}"
											readonly> <label for="form_control">Description</label>
									</div>
									<span class="required"></span>
								</div>
							</div>

							<div class="row">
								<div class="col-md-6">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control" id="dataLocation"
											value="${listdatasource.dataLocation}" readonly> <label
											for="form_control">Location</label>
									</div>
								</div>
								<div class="col-md-6">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control" id="dataSource"
											value="${listdatasource.dataSource}" readonly> <label
											for="form_control">Data Format</label>
									</div>
								</div>
							</div>



							<div class="form-actions noborder align-center">
								<button type="submit" class="btn red">Delete</button>

								<a onClick="validateHrefVulnerability(this)"  href="datatemplateview" class="btn default">Cancel</a>
							</div>

						</div>
					</form>
				</div>


			</div>
		</div>
		<div class="note note-info hidden">Data Template deleted
			Successfully</div>
		<div class="note note-danger hidden"></div>
		<!-- END SAMPLE FORM PORTLET-->
	</div>
</div>
</div>
<jsp:include page="footer.jsp" />