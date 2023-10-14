
<%@page import="java.util.Map"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
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
							<span class="caption-subject bold "> Edit Role </span>
						</div>
					</div>
					<div class="portlet-body form">
						<input type="hidden" id="toUrl" value="saveEditedRoleIntoDatabase" />
						<input type="hidden" id="idRole" value="${idRole}" /> <input
							type="hidden" id="idRoleId"
							value="<?php echo $roleData[0]->idRole?>" />
						<div class="form-body">
							<div class="row">
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											id="roleNameId" name="roleName" value="${Name}" readonly>
										<label for="form_control_1">Role Name</label>
										<!--    <span class="help-block">Enter group name   </span> -->
									</div>
									<span id="amit" class="help-block required"></span>
								</div>
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control" id="descriptionId"
											name="description" value="${Description}" readonly> <label
											for="form_control_1">Description</label>
										<!--  <span class="help-block">Short description about group</span> -->
									</div>
								</div>
							</div>
							<div class="row">
								<div class="col-md-12 col-capture">
									<label for="form_control_1">Select the Access Control</label>
								</div>
							</div>
							<c:forEach var="module" items="${Module}">

								<div class="row">
									<div class="col-md-12">
										<div class="form-group form-md-checkboxes">
											<div class="md-checkbox-list">
												<div class="md-checkbox">
													<input type="checkbox" id="taskId${module.key}"
														name="${module.key}" class="md-check accessControl"
														value="${module.key}"
														<c:forEach var="roleModule" items="${RoleModule}"> 
			                                     				<c:if test="${roleModule.key eq module.key}">
			                                     					checked
			                                     				</c:if>
			                                     			</c:forEach> />
													<label for="taskId${module.key}"> <span></span> <span
														class="check"></span> <span class="box"></span>
														${module.value}
													</label>
												</div>
											</div>
										</div>
									</div>
								</div>
								<c:set var="c" value=""></c:set>
								<c:set var="r" value=""></c:set>
								<c:set var="u" value=""></c:set>
								<c:set var="d" value=""></c:set>
								<c:set var="flag" value="false"></c:set>


								<c:forEach var="roleModule" items="${RoleModule}">
									<c:if test="${roleModule.key eq module.key}">
										<c:set var="flag" value="true"></c:set>
											<c:set var="listOfModes"
											value="${fn:replace(roleModule.value,'-',',')} ">
										</c:set>

									</c:if>
								</c:forEach>
								<c:if test="${flag}">
									<%-- ${listOfModes} --%>
									<span> <c:forEach var="mode" items="${listOfModes}">
											<c:if test="${fn:contains(mode, 'C')}">
												<c:set var="c" value="${mode}"></c:set>
											</c:if>
											<c:if test="${fn:contains(mode, 'R')}">
												<c:set var="r" value="${mode}"></c:set>
											</c:if>
											<c:if test="${fn:contains(mode, 'U')}">
												<c:set var="u" value="${mode}"></c:set>
											</c:if>
											<%-- <c:if test="${ 'D' eq mode}">
													<c:out value="${mode}"></c:out>
													<c:set var="d" value="${mode}"></c:set>
												</c:if>
												${mode}  --%>
											<c:if test="${fn:contains(mode, 'D')}">
												<c:set var="d" value="${mode}"></c:set>
											</c:if>
										</c:forEach>
									</span>
								</c:if>
								<%-- ${d} --%>
								<span>
									<%-- ${c} --> ${r} --> ${u} --> ${d} --%> <c:set var="c"
										value="${fn:replace(c,' ', '')}" /> <c:set var="r"
										value="${fn:replace(r,' ', '')}" /> <c:set var="u"
										value="${fn:replace(u,' ', '')}" /> <c:set var="d"
										value="${fn:replace(d,' ', '')}" />
								</span>
								<div id="taskId${module.key}Check" class="roleHide "
									style="padding: 0 0 0 5%;">
									<div class="row">
										<div class="col-md-6">
											<div class="form-group form-md-checkboxes">
												<div class="md-checkbox-inline">
													<div class="md-checkbox">
														<input type="checkbox" id="${module.key}C"
															name="${module.key}C" class="md-check accessControlCheck"
															value="C"
															<c:if test="${ c eq 'C'}">
				                         		  			checked
			                         		  			</c:if>>
														<label for="${module.key}C"> <span></span> <span
															class="check"></span> <span class="box"></span> C
														</label>
													</div>
													<div class="md-checkbox">
														<input type="checkbox" id="${module.key}R"
															name="${module.key}R" class="md-check accessControlCheck"
															value="R"
															<c:if test="${ r eq 'R'}">
				                         		  			checked
			                         		  			</c:if>>
														<label for="${module.key}R"> <span></span> <span
															class="check"></span> <span class="box"></span> R
														</label>
													</div>
													<div class="md-checkbox">
														<input type="checkbox" id="${module.key}U"
															name="${module.key}U" class="md-check accessControlCheck"
															value="U"
															<c:if test="${ u eq 'U'}">
				                         		  			checked
			                         		  			</c:if>>
														<label for="${module.key}U"> <span></span> <span
															class="check"></span> <span class="box"></span> U
														</label>
													</div>
													<div class="md-checkbox">
														<input type="checkbox" id="${module.key}D"
															name="${module.key}D" class="md-check accessControlCheck"
															value="D"
															<c:if test="${ d eq 'D'}">
				                         		  			checked
			                         		  			</c:if>>
														<label for="${module.key}D"> <span></span> <span
															class="check"></span> <span class="box"></span> D
														</label>
													</div>
												</div>
											</div>
										</div>
									</div>
								</div>
							</c:forEach>
							<br />
							<div class="row">
								<div class="note note-danger hidden"></div>
							</div>
						</div>
						<div class="form-actions noborder align-center">
							<button type="submit" class="btn blue" id="submitEditRoleId">Submit</button>
						</div>
					</div>
				</div>
				<div class="note note-info hidden">Group Updated Successfully
				</div>
				<!-- END SAMPLE FORM PORTLET-->
			</div>
		</div>
	</div>
</div>
<!-- END QUICK SIDEBAR -->

<jsp:include page="footer.jsp" />
<script>
$('#submitEditRoleId').click(function() 
	    {
	    	 var amit1 = $('#amit').html();
	        var no_error = 1;
	        $('.required').html('');
	        $(".catch-error").each(function() {
	            $this = $(this);
	            if ($this.val().length == 0) {
	                $this.closest('.col-capture').find('.required').html('<i class="fa fa-warning"> </i>');
	                $('.note-danger').removeClass('hidden').html('Required data missing');
	                no_error = 0;
	            }
	        });
	       
	        console.log(amit1);
	        	if(amit1=="")
	        	{
	        		
	        	}
	        	else{
	        		console.log("inside amit if");
	                no_error = 0;
	        	} 
	       if( no_error==0){
	       	console.log('error');
	           return false;
	       }
	        if (no_error) {
	            var taskDataAll = '';
	            var count = 0;
	            $(".accessControl").each(function(){
	                var selectedId = '#' + ($(this).attr('id'));
	                //alert(selectedId);
	                //alert(taskDataAll);
	                console.log("accessControl"+selectedId);
	                if( $(selectedId).is(":checked") ) {
	                     if (count) {
	                        taskDataAll += ('-' + $(selectedId).val());
	                    } else {
	                        taskDataAll = $(selectedId).val();
	                    }
	                    count++;
	                }
	                // console.log(taskDataAll);
	            });

	            var accessControlData = '';
	            var count = 0;
	            $(".accessControlCheck").each(function(){
	                var selectedId = '#' + ($(this).attr('id'));
	                //alert(selectedId);
	                //alert(accessControlData);
	                //console.log("accessControlCheck"+selectedId);
	                if( $(selectedId).is(":checked") ) {
	                	//alert(selectedId);
	                	//alert(accessControlData);
	                     if (count) {
	                        accessControlData += ('-' + $(selectedId).val());
	                    } else {
	                        accessControlData = $(selectedId).val();
	                    }
	                     //alert(accessControlData);
	                    count++;
	                    
	                }
	                if($(selectedId).val() == 'D') {
	                    accessControlData += '|';
	                    //count = 0;
	                }
	                
	            });
	            var accessControlAll = '';
	            splitData = accessControlData.split("|");
	            accessControlAll = splitData.toString();
	            var form_data = {
	                roleName: $("#roleNameId").val(),
	                description: $("#descriptionId").val(),
	                idRole: $("#idRole").val(),
	                taskDataAll: taskDataAll,
	                accessControlAll: accessControlAll
	            };
	            if ($('#editmodeid').length > 0) {
	                form_data.editMode = 'yes';
	                form_data.idRole = $('#idRoleId').val();
	            }

	            $.ajax({
	                url: $("#toUrl").val(),
	                type: 'POST',
	                headers: { 'token':$("#token").val()},
	                datatype: 'json',
	                data: form_data,
	                success: function(message) {
	                    var j_obj = $.parseJSON(message);
	                    if (j_obj.hasOwnProperty('success')) {
	                    	toastr.info("Role Updated Successfully");
	                        setTimeout(function(){
	                            window.location.href= "roleManagement";
	                        },1000); 
	                    } else {
	                        if (j_obj.hasOwnProperty('fail')) {
	                        	toastr.info("There was a problem");
	                        }
	                    }
	                },
	                error: function(xhr, textStatus, errorThrown) {
	                    $('.catch-form-msg').find('.help-block').html("There seems to be a network problem. Please try again in some time.");
	                }
	            });
	        }
	        return false;
	    });

</script>