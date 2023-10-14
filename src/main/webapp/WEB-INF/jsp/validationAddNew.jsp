<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<script
	src="./assets/global/plugins/jquery.min.js"></script>
<script
	src="./assets/global/plugins/bootstrap.min.js"></script>
<script
	src="./assets/global/plugins/bootstrap.min.js"></script>
	<link rel="stylesheet" href="./assets/global/plugins/bootstrap-select.min.css">

<!-- Latest compiled and minified JavaScript -->
<script src="./assets/global/plugins/bootstrap-select.min.js"></script>

<!-- (Optional) Latest compiled and minified JavaScript translation files -->
<!-- <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.11.2/js/i18n/defaults-*.min.js"></script> -->
	
<!-- BEGIN CONTENT -->


<!-- BEGIN CONTENT -->

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
							<span class="caption-subject bold "> Validation Check - Add New </span>
						</div>
					</div>
				<%-- 	action="customizeSchemainVC?idDataSchema=${listdataschema.idDataSchema} --%>
					<div class="portlet-body form">
						<form method="GET"
							action="validationCheck_AddNew_Customize">
							<div class="container">
								<label for="sel1">Select Schema (select one):</label><br /> 
								
								<select	name="idDataSchema" id="locationid" class="form-control" width="300" style="width: 300px">
									<option id="select" value="">select</option>
									<c:forEach items="${listDataSchema}" var="listDataSchemaobj">
										<option value=${listDataSchemaobj.idDataSchema}>${listDataSchemaobj.databaseSchema}</option>
									</c:forEach>
								</select> <br/>
								<span class="required"></span> <br /> <br />
								  <button type="submit" formaction="validationvalidationCheck_AddNew_Create?idDataSchema" class="btn blue dropdowns">create</button>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp
								  <button type="submit" id="schemaSubmit" value="customize" class="btn blue dropdowns">customize</button>
<!-- 								<input type="submit" id="schemaSubmit" value="customize" class="btn blue dropdowns">
 -->								</table>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<script>
$('.dropdowns').click(function(){
	$('.input_error').remove();
	 if ($('#locationid').val() == "") {
		  $('<span class="input_error" style="font-size:12px;color:red">Please select anyone</span>').insertAfter($('#locationid'));
         //alert("Please select anyone");
         return false;
     }
});
</script>
<jsp:include page="footer.jsp"/>