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
							<span class="caption-subject bold "> Create Validation
								Check </span>
						</div>
					</div>
					<div class="portlet-body form">

						<form id="myForm" method="POST" action="matchingKeys">
							<input type="hidden" id="idApp" name="idApp" value="${idApp}" />
							<input type="hidden" id="idData" name="idData" value="${idData}" />
							<input type="hidden" id="apptype" name="apptype"
								value="${apptype}" /> <input type="hidden" id="applicationName"
								name="applicationName" value="${applicationName}" /> <input
								type="hidden" id="description" name="description"
								value="${description}" /> <input type="hidden" id="name"
								name="name" value="${name}" />
							<div class="form-body">
								<div class="row">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control catch-error"
												id="appnameid" name="dataset" value="${name }" readonly />
											<label for="form_control_1">Validation Check Name *</label>
											<!--  <span class="help-block">Data Set Name </span> -->
										</div>
										<br /> <span class="required"></span>
									</div>
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="descriptionid"
												name="description" placeholder="Enter your description"
												value="${description }" readonly /> <label
												for="form_control">Description</label>
											<!-- <span class="help-block">Short text about Data Set</span> -->
										</div>
									</div>
								</div>

								<div class="row">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="descriptionid"
												name="description" value="${apptype }" readonly /> <label
												for="form_control">Validation Check Type</label>
											<!-- <span class="help-block">Short text about Data Set</span> -->
										</div>
									</div>

									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="descriptionid"
												name="description" value="${applicationName}" readonly>
											<label for="form_control">Data Template</label>
											<!-- <span class="help-block">Short text about Data Set</span> -->
										</div>
									</div>
								</div>

								<!--  <div class="form-actions noborder align-center">
                                        <button type="submit" id="appcreateid" class="btn blue">Submit</button>
                                    </div> -->
								<br> <br>
								<div class="row">
									<div class="col-md-12">
										<div
											class="tabbable tabbable-custom boxless tabbable-reversed">
											<ul class="nav nav-tabs"
												style="border-bottom: 1px solid #ddd;">
												<li class="active" style="width: 48%" disabled="disabled">
													<a onClick="validateHrefVulnerability(this)"  href="#" style="padding: 10px 2px;">
														<button type="button"
															class="btn btn-top-menu btn-circle red">1</button>
														<strong> Matching Type </strong>
												</a>
												</li>

												<li style="width: 48%"><a onClick="validateHrefVulnerability(this)"  href="#"
													style="padding: 10px 2px;">
														<button type="button"
															class="btn btn-top-menu btn-circle red">2</button>
														<strong> Matching Keys</strong>
												</a></li>
											</ul>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<select class="form-control" id="mcardinalityid"
												name="mcardinality">
												<option value="-1">Choose Match Category</option>
												<option value="One to One">One to One</option>
												<!-- <option value="One to Many">One to Many</option> -->
												<!-- <option value="Many to Many">Many to Many</option> -->
											</select> <label for="form_control_1">Match Category *</label>
										</div>
										<br /> <span class="required"></span>
									</div>
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<select class="form-control" id="matchtypeid"
												name="matchtype">
												<option value="-1">Choose Match Type</option>
												<option value="Key Fields Match">Key Fields Match</option>
												<option value="Measurements Match">Measurements
													Match</option>
											</select> <label for="form_control_1">Match Type *</label>
										</div>
										<br /> <span class="required"></span>
									</div>
								</div>
								<div class="form-actions noborder align-center tab-one-save">
									<button type="submit" id="matchtypecreateid" class="btn blue">Save</button>
								</div>
								<c:if test="${matchingRulesTrue eq 'true'}">


									<div class="portlet-title">
										<div class="caption font-red-sunglo">
											<span class="caption-subject bold ">Matching Rules<!-- <a onClick="validateHrefVulnerability(this)"  href="javascript:;">(Download CSV)</a> -->
											</span>
										</div>
									</div>
									<hr />
									<div class="portlet-body">
										<table
											class="table table-striped table-bordered  table-hover datatable-master"
											style="width: 100%;">
											<thead>
												<tr>
													<th>LeftSideExpression</th>
													<th>RightSideExpression</th>
												</tr>
											</thead>
											<tbody>
												<c:forEach var="matchingRulesObj" items="${matchingRules}">
													<tr>
														<td>${matchingRulesObj.leftSideExp}</td>
														<td>${matchingRulesObj.rightSideExp}</td>
													</tr>
												</c:forEach>
											</tbody>
										</table>
									</div>
								</c:if>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<jsp:include page="footer.jsp" />
<head>
<script type="text/javascript">
$('#matchtypecreateid').click(function() {
    var no_error = 1;
    $('.input_error').remove();
    if($("#mcardinalityid").val() == -1){
        $('<span class="input_error" style="font-size:12px;color:red">Please Choose a Match Type</span>').insertAfter($('#mcardinalityid'));
        no_error = 0;
    }
     if($("#matchtypeid").val() == -1){
        $('<span class="input_error" style="font-size:12px;color:red">Please Choose a Match Type</span>').insertAfter($('#matchtypeid'));
        no_error = 0;
    }
    
    if( no_error==0){
    	console.log('error');
        return false;
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
</head>
