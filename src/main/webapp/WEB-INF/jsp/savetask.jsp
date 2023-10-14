








<%@page isELIgnored="false" %>




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
			<div class="col-md-12" style="width: 100%;">
				<!-- BEGIN SAMPLE FORM PORTLET-->
				<div class="portlet light bordered init">
					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold "> Create Module <h3>${msg}</h3></span>
						</div>
					</div>
					<div class="portlet-body form">
						<form id="tokenForm1" role="form" method="post" action="save">
							<input type="hidden" id="toUrl" value="save" />
							<div class="form-body">
								<div class="row">
									<div class="col-md-12 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control catch-error"
												id="taskNameId" name="task"
												placeholder="Enter your Task name"> <label
												for="taskNameId">Task name * </label> <span
												class="help-block"></span>
										</div>
										<span class="required"></span>
									</div>
								</div>

								<br />
								<div class="row">
									<div class="note note-danger hidden"></div>
								</div>
							</div>
							<div class="form-actions noborder align-center">
								<button type="submit" class="btn blue">Submit</button>
							</div>
						</form>
					</div>
				</div>
				<div class="note note-info hidden">Module Created Successfully
				</div>
				<!-- END SAMPLE FORM PORTLET-->
			</div>
		</div>
	</div>
</div>


<jsp:include page="footer.jsp" />

<script>
        $(document).ready(function() {
        	$('#tokenForm1').ajaxForm({
        		headers: {'token':$("#token").val()}
        	});
        });
 </script>

<!-- 
	<form action="save" method="post">

		<input type="text" class="form-control catch-error" id="taskNameId"
			name="task" placeholder="Enter your Task name"> <label
			for="taskNameId"></label> <span class="help-block"></span>


		<div class="form-actions noborder align-center">
			<button type="submit" class="btn blue" id="submittaskid">Submit</button>
		</div>
	</form>
	
	
	 -->