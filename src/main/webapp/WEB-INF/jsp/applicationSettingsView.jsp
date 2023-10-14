<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="org.springframework.jdbc.support.rowset.SqlRowSet"%>
<%@page import="com.databuck.service.RBACController"%>
<%@page
	import="org.springframework.jdbc.support.rowset.SqlRowSetMetaData"%>
<jsp:include page="checkVulnerability.jsp" />

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<style>
.asteriskInput::after {
	content: " *";
}

.modal {
	text-align: center;
	padding: 0 !important;
}

.modal:before {
	content: '';
	display: inline-block;
	height: 100%;
	vertical-align: middle;
	margin-right: -4px; /* Adjusts for spacing */
}

.modal-dialog {
	display: inline-block;
	text-align: left;
	vertical-align: middle;
	width: 100%;
}

input {
	display: inline;
}
</style>
<body>

	<jsp:include page="header.jsp" />
	<jsp:include page="container.jsp" />

	<!--============= BEGIN CONTENT BODY============= -->


	<!--*****************      BEGIN CONTENT **********************-->

	<div class="page-content-wrapper">
		<!-- BEGIN CONTENT BODY -->
		<div class="page-content">
			<!-- BEGIN PAGE TITLE-->
			<div class="row">
				<div class="col-md-6" style="width: 100%;">

					<!--****         BEGIN EXAMPLE TABLE PORTLET       **********-->

					<div class="portlet light bordered init">
						<div class="portlet-title">
							<div class="caption font-red-sunglo">
								<span class="caption-subject bold "> Properties File </span>

							</div>
						</div>
						<div class="portlet-body form">

							<div class="form-body">

								<div id="restart_msg_div" class="portlet light bordered hidden" style="background-color: mintcream;">
									<span>Some properties changes are waiting for restart. Please restart application for the changes to take effect.</span>
								</div>
								
								<div class="row">
									<div class="col-md-12">
										<div
											class="tabbable tabbable-custom boxless tabbable-reversed">
											<ul class="nav nav-tabs"
												style="border-bottom: 1px solid #ddd;">
												<%
													String currentSubLink = (String) request.getAttribute("currentSubLink");
													if (currentSubLink != null && currentSubLink.equals("appdb")) {
												%>
												<li class="active" style="width: 10%;">
													<%
														} else {
													%>
												
												<li style="width: 10%;">
													<%
														}
													%> <a onClick="validateHrefVulnerability(this)"  href="applicationSettingsView?vData=appdb"
													style="padding: 2px 2px;"><strong>AppDb</strong> </a>
												</li>
												
												<%
													if (currentSubLink != null && currentSubLink.equals("activedirectory")) {
												%>
												<li class="active" style="width: 13%;">
													<%
														} else {
													%>
												
												<li style="width: 13%;">
													<%
														}
													%> <a onClick="validateHrefVulnerability(this)"  href="applicationSettingsView?vData=activedirectory"
													style="padding: 2px 2px;"><strong>ActiveDirectory</strong>
												</a>
												</li>

												<%
													if (currentSubLink != null && currentSubLink.equals("cluster")) {
												%>
												<li class="active" style="width: 10%;">
													<%
														} else {
													%>
												
												<li style="width: 10%;">
													<%
														}
													%> <a onClick="validateHrefVulnerability(this)"  href="applicationSettingsView?vData=cluster"
													style="padding: 2px 2px;"><strong>Cluster</strong> </a>
												</li>

												<%
                                                    if (currentSubLink != null && currentSubLink.equals("mapr")) {
                                                %>
                                                <li class="active" style="width: 10%;">
                                                    <%
                                                        } else {
                                                    %>

                                                <li style="width: 10%;">
                                                    <%
                                                        }
                                                    %> <a onClick="validateHrefVulnerability(this)"  href="applicationSettingsView?vData=mapr"
                                                    style="padding: 2px 2px;"><strong>Mapr</strong> </a>
                                                </li>

                                                <%
                                                    if (currentSubLink != null && currentSubLink.equals("gcp")) {
                                                %>
                                                <li class="active" style="width: 10%;">
                                                    <%
                                                        } else {
                                                    %>

                                                <li style="width: 10%;">
                                                    <%
                                                        }
                                                    %> <a onClick="validateHrefVulnerability(this)"  href="applicationSettingsView?vData=gcp"
                                                    style="padding: 2px 2px;"><strong>GCP</strong> </a>
                                                </li>
                                                <%
                                                    if (currentSubLink != null && currentSubLink.equals("cdp")) {
                                                %>
                                                <li class="active" style="width: 10%;">
                                                    <%
                                                        } else {
                                                    %>

                                                <li style="width: 10%;">
                                                    <%
                                                        }
                                                    %> <a onClick="validateHrefVulnerability(this)"  href="applicationSettingsView?vData=cdp"
                                                    style="padding: 2px 2px;"><strong>CDP</strong> </a>
                                                </li>
                                                <%
                                                    if (currentSubLink != null && currentSubLink.equals("azure")) {
                                                %>
                                                <li class="active" style="width: 10%;">
                                                    <%
                                                        } else {
                                                    %>

                                                <li style="width: 10%;">
                                                    <%
                                                        }
                                                    %> <a onClick="validateHrefVulnerability(this)"  href="applicationSettingsView?vData=azure"
                                                    style="padding: 2px 2px;"><strong>Azure</strong> </a>
                                                </li>

												<%
													if (currentSubLink != null && currentSubLink.equals("mongodb")) {
												%>
												<li class="active" style="width: 10%;">
													<%
														} else {
													%>
												
												<li style="width: 10%;">
													<%
														}
													%> <a onClick="validateHrefVulnerability(this)"  href="applicationSettingsView?vData=mongodb"
													style="padding: 2px 2px;"><strong>MongoDb</strong> </a>
												</li>

												<%
													if (currentSubLink != null && currentSubLink.equals("license")) {
												%>
												<li class="active" style="width: 10%;">
													<%
														} else {
													%>
												
												<li style="width: 10%;">
													<%
														}
													%> <a onClick="validateHrefVulnerability(this)"  href="applicationSettingsView?vData=license"
													style="padding: 2px 2px;"><strong>License Key*</strong> </a>
												</li>
												
												<%
													if (currentSubLink != null && currentSubLink.equals("dbdependency")) {
												%>
												<li class="active" style="width: 13%;">
													<%
														} else {
													%>
												
												<li style="width: 13%;">
													<%
														}
													%> <a onClick="validateHrefVulnerability(this)"  href="applicationSettingsView?vData=dbdependency"
													style="padding: 2px 2px;"><strong>DbDependency</strong> </a>
												</li>
												
												<%
													if (currentSubLink != null && currentSubLink.equals("dynamicvariable")) {
												%>
												<li class="active" style="width: 13%;">
													<%
														} else {
													%>
												
												<li style="width: 13%;">
													<%
														}
													%> <a onClick="validateHrefVulnerability(this)"  href="applicationSettingsView?vData=dynamicvariable"
													style="padding: 2px 2px;"><strong>DynamicVariable</strong> </a>
												</li>
												
												<%
													if (currentSubLink != null && currentSubLink.equals("integration")) {
												%>
												<li class="active" style="width: 13%;">
													<%
														} else {
													%>
												
												<li style="width: 13%;">
													<%
														}
													%> <a onClick="validateHrefVulnerability(this)"  href="applicationSettingsView?vData=integration"
													style="padding: 2px 2px;"><strong>Integration</strong> </a>
												</li>

											</ul>
										</div>
									</div>
								</div>

								<span class="help-block"></span>

								<table id="PropertiesTable"
									class="table table-striped table-bordered  table-hover"
									style="width: 100%;">
									<thead>
										<tr>
											<th width="5%" align="center">Edit</th>
											<th width="30%">Property Name</th>
											<th width="20%">Default</th>
											<th width="45%">User Input</th>

										</tr>
									</thead>
									<c:forEach var="propertyData" items="${propertiesList}" varStatus="status">
										<c:set var="isMandatoryField" value="${propertyData.isMandatoryField()}" />
										<c:set var="isWarningField" value="${propertyData.isWarning()}" />
										<c:set var="isPasswordField" value="${propertyData.isPasswordField()}" />
										<c:set var="propertyName" value="${propertyData.getPropertyName()}" />

										<tr>
											<%
												boolean flag = false;
												boolean flagU = false;
												flagU = RBACController.rbac("Application Settings", "U", session);
												flag = RBACController.rbac("Application Settings", "R", session);
												//System.out.println("flag=" + flag);
												boolean isRuleCatalogDiscovery = pageContext.getAttribute("propertyName").equals("isRuleCatalogDiscovery");
												if (flagU && !isRuleCatalogDiscovery) {
											%>
											<td align="center"><input type="checkbox"
												name="EditCheck" class="md-check editControl"
												id="typeId${status.index}"
												value="${propertyData.propertyName}"></td>
											<%
												} else if (flag) {
											%>
											<td align="center"><input type="checkbox"
												name="EditCheck" class="md-check editControl"
												disabled="disabled"></td>
											<%
												}
											%>


											<td><input type=hidden name="EncryptData"
												id="EncryptData_typeId${status.index}"
												value="${propertyData.isValueEncrypted()}">
												${propertyData.propertyName} <%
 												boolean isMandatory = (boolean) pageContext.getAttribute("isMandatoryField");
												 if (isMandatory) {
												 %> <span class="asteriskInput"> </span> <%
												 	} else {
												 %> <%
												 	}
												 %>
												 <a onClick="validateHrefVulnerability(this)"  href="#" title="Description" data-toggle="popover" data-trigger="hover" data-content="${propertyData.description}">
												 	<span class="glyphicon glyphicon-info-sign"></span>
												 </a>
											</td>

											<td id="defaultKey_typeId${status.index}">${propertyData.propertyDefaultvalue}</td>

											<%
												boolean isWarning = (boolean) pageContext.getAttribute("isWarningField");
													if (isWarning) {
											%>

											<td style="border-color: #FF4E53; border-width: 2px">
												<%
													} else {
												%>
											
											<td>
												<%
													}
													boolean isPassword = (boolean) pageContext.getAttribute("isPasswordField");
													String textType = "text";
													if (isPassword) {
														textType = "password";
													}
												%> 
												
												<input type="<%= textType %>" class="form-control catch-error" name="UserInput"
													id="UserInput_typeId${status.index}" value="${propertyData.getPropertyValue()}" disabled="disabled"> 
													
												<input type="hidden" class="form-control" name="prop_acutaldata"
													id="prop_acutaldata_typeId${status.index}" value="${propertyData.getPropertyValue()}" disabled="disabled"> 
													
												<input type="hidden" class="form-control" name="prop_req_restart"
													id="prop_req_restart_typeId${status.index}" value="${propertyData.getPropRequiresRestart()}" disabled="disabled">
												
												<input type="hidden" class="form-control" name="prop_ismandatory"
													id="prop_ismandatory_typeId${status.index}" value="${propertyData.isMandatoryField()}" disabled="disabled"> 
				
												 <%
												 	if (isWarning) {
												 %> <span id="spanFileId" class="input_error" style="font-size: 12px; color: red"> 
													   <i>*Please enter the missing property value in the property file which is mandatory.</i></span> 
												 <%
												 	}
												 %>
											</td>

										</tr>
									</c:forEach>

								</table>


								<div id="propModal" class="modal fade" role="dialog">
									<div class="modal-dialog">

										<!-- Modal content-->
										<div class="modal-content">
											<div class="modal-header">

												<h4 class="modal-title"
													style="font-size: 18px; color: green">SUCCESS</h4>
											</div>
											<div class="modal-body">
												<p>
													Properties of Category - "${currentSubLink}" are updated successfully.<br /><br />
													Some properties needs application restart.<br /><br />
													Please restart the server for the changes to take effect.
												</p>
											</div>
											<div class="modal-footer">
											    <a onClick="validateHrefVulnerability(this)"  href="applicationReboot"
													style="padding: 2px 2px;"><strong class="blue btn">Reboot</strong> </a>
												<button type="button" id="modalButton" class="btn blue"
													data-dismiss="modal">Cancel</button>
													
											</div>
										</div>

									</div>
								</div>
								<div id="prevModal" class="modal fade" role="dialog">
									<div class="modal-dialog modal-lg" role="document">

										<!-- Modal content-->
										<div class="modal-content">
											<div class="modal-header">

												<h4 class="modal-title"
													style="font-size: 18px; color: green">Modified
													${currentSubLink} Properties - (Preview)</h4>
											</div>
											<div class="modal-body col-md-12 col-lg-12 previewModal">

											</div>										
											<div class="modal-footer">
												<button type="button" id="prevModalButton" class="btn blue"
													data-dismiss="modal">OK</button>
											</div>
										</div>

									</div>
								</div>


								<div class="form-actions noborder align-center">
									<button type="submit" id="submitPropId" name="submitPropId"
										class="btn blue Submit" disabled="disabled">Submit</button>
									&nbsp;&nbsp;&nbsp;
									
									<button type="reset" id="Cancel" name="Cancel"
										class="btn blue Reset" disabled="disabled"
										onClick="window.location.reload()">Cancel</button>

									&nbsp;&nbsp;&nbsp;
									<button type="button" id="Preview" name="Preview"
										class="btn blue" disabled="disabled" data-toggle="modal"
										data-target="#prevModal">Preview</button>
								</div>
								<br> <br>
							</div>
						</div>

					</div>

					<!--       *************END EXAMPLE TABLE PORTLET*************************-->

				</div>
			</div>
		</div>
	</div>

</body>
<script>
	var isReload = false;
	$(".editControl").on("click", function() {

		var checked = $(this).is(':checked');
		var selectedIdInput = '#UserInput_' + ($(this).attr('id'));
		var actualPropId = '#prop_acutaldata_' + ($(this).attr('id'));
		var actualPropValue = $(actualPropId).val();
			
		$(selectedIdInput).prop("disabled", !checked);

		var value = checked ? $(selectedIdInput).val() : '';
		$(selectedIdInput).val(value);

		var countBox = $(".editControl").filter(":checked").length;
		if (countBox == 0) {
			$('.btn').prop('disabled', true);
			isReload = false;
		} else {
			$('.btn').prop('disabled', false);
			isReload = true;
		}

		if (!checked) {
			$(this).closest("tr").find("span[id^='spanid']").remove();
			$(selectedIdInput).val(actualPropValue);
		}

	});

	window.addEventListener("beforeunload",function(e) {
		if (isReload) {

			event.preventDefault();
			var confirmationMessage = "Please save your changes, otherwise your changes will be lost.";

			(e || window.event).returnValue = confirmationMessage; //Gecko + IE
			event.returnValue = confirmationMessage;
			return confirmationMessage; //Gecko + Webkit, Safari, Chrome etc.
		} else {
			return undefined;
		}
	});

	$('#Preview').click(function() {
		var previewtable = '<table class="table table-bordered" style="  width: 100%;">';
		var previewthead = '<thead style="background-color: #D7FBF8;"><tr><th width=20%>Property Name</th><th>Default</th><th>User Input</th></tr></thead>';
		var previewData = '';
		
		$(".editControl").each(function() {
			var selectedId = '#'+ ($(this).attr('id'));

			if ($(selectedId).is(":checked")) {
				var selectedIdInput = '#UserInput_'+ ($(this).attr('id'));
				var selectedIdDefault = '#defaultKey_'+ ($(this).attr('id'));
				var selectedIdInputVal = '#val_'+ ($(this).attr('id'));
				var finalVal = ''
				var attrType = $(selectedIdInput).attr('type');
				if (attrType == "password" && $(selectedIdInput).val().length > 0) {
					finalVal = "*********";
				} else {
					finalVal = $(selectedIdInput).val();
				}

				previewData += '<tr><td>'
						+ $(selectedId).val()
						+ '</td><td>'
						+ $(selectedIdDefault)
								.text()
						+ '</td><td>'
						+ finalVal
						+ '</td></tr>';
			}

		});

		var previewTableData = previewtable + previewthead
				+ previewData + "</table>";

		$(".previewModal").html(previewTableData);

	});

	$('#submitPropId').click(function() {

			$('span[id^="spanid"]').remove();

			var no_error = 1;
			
			if(!checkInputText()){ 
				no_error = 0;
				alert('Vulnerability in submitted data, not allowed to submit!');
			}

			$(".catch-error").each(function() {
					$this = $(this);
					var selectedId = '#'+ ($(this).attr('id')).replace("UserInput_","");
					var selectedIdDefault = '#defaultKey_'+ ($(this).attr('id')).replace("UserInput_","");
					var selectedIdMandatory = '#prop_ismandatory_'+($(this).attr('id')).replace("UserInput_","");
					
					if ($this.val().trim().length == 0
							&& $(selectedId).is(":checked")
							&& $(selectedIdMandatory).val() == 'true') {

						$('<span id="spanid" class="input_error" style="font-size:12px;color:red"><i>* Please enter non null value</i></span>')
								.insertAfter($this);
						no_error = 0;
					}

			});

			$('#modalButton').on('click', function() {
				window.location.reload(true);
			});

			if (no_error == 0) {
				console.log('error');
				toastr.info("Update failed, mandatory properties values missing.");
				return false;
			}

			if (no_error) {
				var PropName = "${currentSubLink}";
				isReload = false;
				var EditDataAll = '';

				var paramJsonValues = [];

				$(".editControl").each(
						function() {
							var selectedId = '#'+ ($(this).attr('id'));
							var selectedIdInput = '#UserInput_'+ ($(this).attr('id'));
							var selectedEncrypt = '#EncryptData_'+ ($(this).attr('id'));
							var selectedPropReqRestart = '#prop_req_restart_'+ ($(this).attr('id'));
							
							if ($(selectedId).is(":checked")) {
								paramJsonValues.push({
									"propKeys" : $(selectedId).val(),
									"propValues" : $(selectedIdInput).val(),
									"propEncrypt" : $(selectedEncrypt).val(),
									"propReqRestart" : $(selectedPropReqRestart).val(),
									"propName" : PropName
								});
							}
				});

				var postParamValues = JSON.stringify(paramJsonValues);

				$.ajax({
					url : path + '/saveUpdatedProperties',
					type : 'POST',
					headers: { 'token':$("#token").val()},
					datatype : 'json',
					contentType : 'application/json',
					data : postParamValues,

					success : function(message) {
						var j_obj = $.parseJSON(message);
						var status = j_obj.status;
						var message = j_obj.message;
						if (status == 'success') {
							toastr.info(message);
							
							var isRestartRequired = j_obj.isRestartRequired;
							
							if(isRestartRequired == 'Y') {
								$('#restart_msg_div').removeClass("hidden");
								$('#propModal').modal('show');
							} else {
								setTimeout(function() {
									window.location.reload(true);
								}, 1000);
							}
							
						} else {
							if (status == 'failed') {
								toastr.info(message);
								
								setTimeout(function() {
									window.location.reload(true);
								}, 1000);
							}
						}
					},

					error : function(xhr, textStatus,errorThrown) {
						$('.catch-form-msg')
								.find('.help-block')
								.html("There seems to be a network problem. Please try again in some time.");
					}

				});

			}
		return false;

	});
</script>
<script>
$(document).ready(function(){
  $('[data-toggle="popover"]').popover();
  
  var isPropChangesWaitingRestart = ${isPropChangesWaitingRestart};
  if(isPropChangesWaitingRestart){
	  $('#restart_msg_div').removeClass("hidden");
  } else {
	  $('#restart_msg_div').addClass("hidden");
  }
  
});
</script>
<jsp:include page="footer.jsp" />