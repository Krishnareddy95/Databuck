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
							<span class="caption-subject bold "> Generate API Token</span>
						</div>
					</div>
				<div class="portlet-body form">
					<div class="form-body">
						<div class="row">
						<div class="col-md-6 col-capture">
								<div class="form-group form-md-line-input">
								<p align="center">
								<button type="submit" id="generateButton" class="btn blue">Generate Token</button>
								</p>
								</div>
								<span class="help-block required"
									style="font-size: 12px; color: red" id="amit"></span>
								<!-- <span id="amit" class="help-block required"></span> -->
							</div>
							<div class="col-md-6 col-capture">
								<div class="form-group form-md-line-input">
								<p align="center">
								<button type="submit" id="secretButton" class="btn blue">Show Secret Token</button>
								</p>
								</div>
								<span class="help-block required"
									style="font-size: 12px; color: red" id="amit"></span>
								<!-- <span id="amit" class="help-block required"></span> -->
							</div>
							</div>
							<div class="row">
							<div class="col-md-6 col-capture">
								<div class="form-group form-md-line-input hidden" id="accessTokenDivId">
									<input type="text" class="form-control catch-error"	id="tokenId" name="tokenId">
									<label for="tokenId">Access Token Id</label>
								</div>
							</div>
							<div class="col-md-6 col-capture">
								<div class="form-group form-md-line-input hidden" id="secretTokenDivId">
									<input type="text" class="form-control catch-error"	id="secretTokenId" name="tokenId">
									<label for="tokenId">Secret Access Token</label>
								</div>
							</div>
						</div>
					</div>
				</div>
				</div>
			</div>
		</div>
	</div>
</div>
<jsp:include page="footer.jsp" />
<script>
$("#generateButton").button().click(function(){
	$.ajax({
		url : './generateToken',
		type : 'POST',
		headers: { 'token':$("#token").val()},
		datatype : 'json',
		data : "",
		success : function(message) {
			var j_obj = $.parseJSON(message);
			//toastr.info(j_obj['accessToken']);
			$('#accessTokenDivId').removeClass('hidden').show();
			//alert(message);
			$("#tokenId").val(j_obj['accessToken']);
			$("#secretTokenId").val(j_obj['secretAccessToken']);
		},
		error : function(xhr, textStatus,
				errorThrown) {
			$('#initial').hide();
			$('#fail').show();
		}
	});
}); 
$("#secretButton").button().click(function(){
	$('#secretTokenDivId').removeClass('hidden').show();
	//$("#secretTokenId").val("XRWQJBEMC18YGP5YQ29V");
});
</script>