
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import ="java.util.*"%>
<%@ page import="com.databuck.bean.ruleFields" %>


<jsp:include page="header.jsp" />
<jsp:include page="checkVulnerability.jsp" />

<jsp:include page="container.jsp" />
<style>

#MsgSubject {
	width: 100% !important;
	min-height: 50px;
	margin: auto;
	font-size: 12.5px;
	font-family: "Lucida Console", Courier, monospace;
	border: 2px solid !important !important;
}

.LongTextBox {
	width: 520px;
}


#MessageBody {
	min-height: 125px;
	margin: auto;
	font-size: 13.5px;
	font-family: "Lucida Console", Courier, monospace;
	border: 2px solid !important !important;
}

</style>
<div class="page-content-wrapper">

	<div class="page-content">

		<div class="row">
			<div class="col-md-6" style="width: 100%;">
				<!-- BEGIN SAMPLE FORM PORTLET-->
				<div class="portlet light bordered init">
					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold "> Customize Alert Event Notification </span>
							<div style="position:absolute;top:10px;right:30px; z-index:99">
                              <button type="back" class="btn blue" onclick="history.back()">Back</button>
                            </div>
						</div>
					</div>
					<div class="portlet-body form">
					 <input type="hidden" id="currSelectedEvent" value="${alertEventMaster.eventId}" />
						<div class="form-body">
							<div class="row">
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
									 <select class="form-control" id="selectedEvent" name="Event_Topic">
                                         <c:forEach items="${eventNameList}" var="category">
                                         <option value="${category.eventId}">${category.eventName}</option>
                                         </c:forEach>
                                         </select>

                                         <label for="form_control_1">Event Topic Name</label>
									</div>
									<br /> <span class="required"></span>
								</div>
								</div>
								<div class="row">
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
                                        <label for="MessageSubject">Event Message Subject</label><br>
										<input type="text" class='LongTextBox' id="MessageSubject"
										 value="${alertEventMaster.eventMessageSubject}"
											readonly="readonly">
									</div>
									<br /> <span class="required"></span>
								</div>
								</div>
								
                                <div class="row">
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
                                        <label for="eventMessageCode">Event Message Code</label><br>
										<input type="text" class='LongTextBox'
										id="eventMessageCode" value="${alertEventMaster.eventMessageCode}"
                                            readonly="readonly">

									</div>
									<br /> <span class="required"></span>
								</div>
							</div>
							<div class="row">
							<div class="col-md-6 col-capture">
                                    <div class="form-group form-md-line-input">
                                        <label for="eventMessageBody">Event Message Body</label><br>
                                        <textarea  class="LongTextBox" id='eventMessageBody' value=''
                                        rows="8">${alertEventMaster.eventMessageBody}</textarea>
                                    </div>
                                    <br /> <span class="required"></span>
                                </div>
							</div>

	                    </div>

						</div>
						<div class="form-actions noborder align-center">
							<button type="submit" id="updateEvent" class="btn blue">Update</button>
						</div>
					</div>
				</div>
				<!-- END SAMPLE FORM PORTLET-->
			</div>
		</div>
	</div>
</div>
<!-- END CONTAINER -->

<jsp:include page="footer.jsp" />
<script>
    'use strict'
   $(document).ready(function() {
   console.log( "ready!" );
    var eventId = $("#currSelectedEvent").val();
    	$("#selectedEvent").val(eventId);
   	});
    
   $('#selectedEvent').on('change', function() {
	   var eventId = $("#selectedEvent").val();
	   window.location.href = 'editAlertEventNotification?eventId='+eventId+'';   
   });

   
    $('#updateEvent').click(function() {
    					    $('.input_error').remove();
    					    var no_error = 1;
    					    if(!checkInputText()){
                                no_error = 0;
                                toastr.info('Vulnerability in submitted data, not allowed to submit!');
                            }
                           if ($("#eventMessageBody").val().length == 0) {
                            console.log('eventMessageBody');
                            $(
                                '<span class="input_error" style="font-size:12px;color:red">Please Enter Message </span>')
                                .insertAfter($('#eventMessageBody'));
                            no_error = 0;
                           						}
                            if (no_error > 0) {
                             var form_data = {
                                    eventId : ${alertEventMaster.eventId},
                                    name : "${alertEventMaster.eventName}",
                                    msgSubject : $("#MessageSubject").val(),
                                    msgBody : $("#eventMessageBody").val(),
                              };
                               $.ajax({

                                        url : './updateEventView',
                                        type : 'POST',
                                        headers: { 'token':$("#token").val()},
                                        datatype : 'json',
                                        data : form_data,
                                        success : function(data) {
                                        setTimeout(
                                                    function() {
                                                        window.location.href = 'alertNotificationView';
                                                    }, 1000);
                                        },

                                        error : function(data){
                                            window.location.reload();
                                          }
                                      });
                             }

    					});
</script>