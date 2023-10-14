
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
			<div class="col-md-12" style="width: 100%;">
				<!-- BEGIN SAMPLE FORM PORTLET-->
				<div class="portlet light bordered init">
					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold "> Delete Task </span>
						</div>
					
					</div>
					
					<div class="portlet-body form">
					
						<form role="form" method="get"
							                            action="deleteTask">
							<input type="hidden" id="toUrl"
								value="update" /> <input
								type="hidden" id="editTaskFormId" value="yes"> <input
								type="hidden" id="editTaskId" name="idTask" value="${task.idTask}">
							<div class="form-body">
								<div class="row">
									<div class="col-md-12 col-capture">
										<div class="form-group form-md-line-input">
										<div class="form-actions noborder align-center">
								
								<button type="submit" class="btn red">Delete</button>
								<a onClick="validateHrefVulnerability(this)"  href="taskview" class="btn default">Cancel</a>
							</div>
										</div>
										<span class="required"></span>
									</div>
								</div>

								<br />
								<div class="row">
									<div class="note note-danger hidden"></div>
								</div>
							</div>
							
						</form>
					</div>
				</div>
				<div class="note note-info hidden">Module Edited Successfully
				</div>
				<!-- END SAMPLE FORM PORTLET-->
			</div>
		</div>
	</div>
</div>
	
	
	
	
	<jsp:include page="footer.jsp" />