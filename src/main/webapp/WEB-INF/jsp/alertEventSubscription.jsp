<%@page import="com.databuck.service.RBACController"%>
<%@page import="java.util.List"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<jsp:include page="container.jsp" />

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
							<span class="caption-subject bold "> Event Subscription </span>
							<div style="position:absolute;top:10px;right:30px; z-index:99">
                              <button type="back" class="btn blue" onclick="history.back()">Back</button>
                            </div>
						</div>
					</div>
					<div class="portlet-body form">
						<input type="hidden" id="currSelectedEvent" value="${eventName}" />
						<input type="hidden" id="currSelectedEventId" value="${eventId}" />
						<input type="hidden" id="queryInputTaken" value="queryInputTaken" />
						<div class="form-body">
							<div class="row">
								<div class="col-md-5 col-capture">
									<div class="form-group form-md-line-input">

										<select class="form-control" id="selectedEvent"
											name="Subscrition type">
											<c:forEach items="${alertEventList}" var="alertEvent">
												<option value="${alertEvent.eventId}">${alertEvent.eventName}</option>
											</c:forEach>
										</select> <label for="form_control_1">Event Name</label>
									</div>
									<span id="amit" class="help-block required"></span>
								</div>
							</div>
							<br>
							<div class="row">
								<div class="col-md-5 col-capture">
									<div class="form-group form-md-line-input">
										<select class="form-control" id="subscription_type"
											name="Subscription_type">
											<option value="-1">Select...</option>
											<option value="global">Global</option>
											<option value="project">Project</option>
										</select> <label for="form_control_1">Subscription Type</label>
									</div>
									<br /> <span class="required"></span>
								</div>
							</div>
							<br>
							<div class="row hidden" id="projects_div">
								<div class="col-md-6 col-capture" id="host-id-container">
									<div class="form-group form-md-line-input">
										<select class="form-control text-left" id="projectId"
											multiple="multiple" name="projectId">
											<c:forEach var="projectObj" items="${projectList}">
												<option value="${projectObj.idProject}">${projectObj.projectName}</option>
											</c:forEach>
										</select> <label for="form_control_1">projectId</label>
									</div>
									<br /> <span class="required"></span>
								</div>
								<br>
							</div>
							<br>
							<div class="row hidden" id="comm_mode_div">
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<select class="form-control text-left" id="comm_mode"
											multiple="multiple" name="comm_mode">
										</select> <label for="form_control_1">Communication Mode</label>
									</div>
								</div>
							</div>
							<br>
							<br>

							<div class="row hidden" id="alert_email_div">
								<div class="col-md-5 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											id="id_email" name="alert_email" placeholder="Enter email">
										<label for="form_control_1">Email </label>
									</div>
									<span id="amit" class="help-block required"></span>
								</div>
							</div>

							<div class="row hidden" id="alert_jira_div">
								<div class="col-md-5 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											id="id_jira" name="alert_jira_project_key"
											placeholder="Enter Project Key"> <label
											for="form_control_1">Jira </label>
									</div>
									<span id="amit" class="help-block required"></span>
								</div>
							</div>

							<div class="row hidden" id="alert_slack_div">
								<div class="col-md-5 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											id="id_slack" name="alert_slack_channel"
											placeholder="Enter Slack Channel Id"> <label
											for="form_control_1">Slack </label>
									</div>
									<span id="amit" class="help-block required"></span>
								</div>
							</div>

							<div class="row hidden" id="alert_sns_div">
								<div class="col-md-5 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											id="id_sns" name="alert_sns_topic"
											placeholder="Enter Topic name"> <label
											for="form_control_1">SNS </label>
									</div>
									<span id="amit" class="help-block required"></span>
								</div>
							</div>

							<div class="row hidden" id="alert_sqs_div">
								<div class="col-md-5 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											id="id_sqs" name="alert_sqs_queue"
											placeholder="Enter Queue name"> <label
											for="form_control_1">SQS </label>
									</div>
									<span id="amit" class="help-block required"></span>
								</div>
							</div>

							<div class="row">
								<div class="form-actions noborder align-center">
									<button type="submit" id="subscriptionId" class="btn blue">Add
										Subscription</button>
								</div>
							</div>
						</div>
					</div>

					<div class="portlet-body">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold "> Available
								Subscriptions </span>
						</div>
						<table
							class="table table-striped table-bordered  table-hover dataTable"
							id="AlertSubscriptions" style="width: 100%;">
							<thead>
								<tr>
									<th>Alert Sub Id</th>
									<th>Project Name</th>
									<th>Communication Mode</th>
									<th>Is Global Subscription</th>
									<th>Communication Values</th>
								</tr>
							</thead>
							<tbody>

							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<jsp:include page="footer.jsp" />

<script src="./assets/global/plugins/jquery-ui.min.js"></script>
<script src="./assets/global/plugins/bootstrap-multiselect.js"></script>
<link rel="stylesheet"
	href="./assets/global/plugins/bootstrap-multiselect.css">
<link rel="stylesheet" href="./assets/jwf/content/JwfSpaInfra.css">
<script src="./assets/jwf/scripts/JwfSpaInfra.js"></script>
<script type="text/javascript">
$(document).ready(function() {

	    $("#selectedEvent").val(${eventId});
	});


</script>


<script type="text/javascript">
$(document).ready(function() {
		  initializeJwfSpaInfra();
	      $('#subscriptionId').click(function(){

                var eventId = $('#selectedEvent').find(":selected").val();
                var subscriptionType = $('#subscription_type').val();
                var no_error = 1;
                $('.input_error').remove();
                if(!checkInputText()){
                    no_error = 0;
                    alert('Vulnerability in submitted data, not allowed to submit!');
                }

                var selected_mode ="";
                var selected_text= "";

                var form_data_list=[];
                var projectId;
                var commModeId;
                var comm_value="";

                var isGlobalSubscription = "N";
                if (subscriptionType != 'global') {
                    if($('#projectId').val() == 'undefined' || $('#projectId').val() == null ){
                         $('<br><span class="input_error" style="font-size:14px;color:red">Please select atleast one projectId column</span>').insertAfter($('#projectId'));
                         no_error = 0;
                    }
                }
                if($('#comm_mode').val() == 'undefined' || $('#comm_mode').val() == null ){
                     $('<br><span class="input_error" style="font-size:14px;color:red">Please select atleast one communication mode</span>').insertAfter($('#comm_mode'));
                     no_error = 0;
                }

                if(no_error > 0) {
                    $('#comm_mode :selected').each(function(i, selected) {

                        selected_text = $(selected).text();
                        if(selected_text == 'EMAIL'){
                            if($("#id_email").val().length == 0){
                               $('<span class="input_error" style="font-size:12px;color:red">Please enter email</span>').insertAfter($('#id_email'));
                               no_error = 0;
                            }
                        } else if(selected_text == 'JIRA'){
                            if($("#id_jira").val().length == 0){
                                 $('<span class="input_error" style="font-size:12px;color:red">Please enter jira details</span>').insertAfter($('#id_jira'));
                                 no_error = 0;
                            }
                        } else if(selected_text == 'SLACK'){
                            if($("#id_slack").val().length == 0){
                                $('<span class="input_error" style="font-size:12px;color:red">Please enter slack details</span>').insertAfter($('#id_slack'));
                                no_error = 0;
                            }
                        } else if(selected_text == 'SNS'){
                            if($("#id_sns").val().length == 0){
                                $('<span class="input_error" style="font-size:12px;color:red">Please enter sns details</span>').insertAfter($('#id_sns'));
                                no_error = 0;
                            }
                        } else if(selected_text == 'SQS'){
                            if($("#id_sqs").val().length == 0){
                                $('<span class="input_error" style="font-size:12px;color:red">Please enter sqs details</span>').insertAfter($('#id_sqs'));
                                no_error = 0;
                            }
                        }

                    });
                }

                if(no_error > 0) {
                    if(subscriptionType == 'global') {
                        isGlobalSubscription = "Y";
                        projectId = -1;
                        $('#comm_mode :selected').each(function(i, selected) {
                            commModeId = $(selected).val();
                            selected_text = $(selected).text();
                            if(selected_text == 'EMAIL'){
                                comm_value= $('#id_email').val();
                            } else if(selected_text == 'JIRA'){
                                comm_value= $('#id_jira').val();
                            } else if(selected_text == 'SLACK'){
                                comm_value= $('#id_slack').val();
                            } else if(selected_text == 'SNS'){
                                comm_value= $('#id_sns').val();
                            } else if(selected_text == 'SQS'){
                                comm_value= $('#id_sqs').val();
                            }

                            var form_data={
                                projectId:projectId,
                                eventId:eventId,
                                commModeId:commModeId,
                                isGlobalSubscription: isGlobalSubscription,
                                communicationValues:comm_value
                            };
                            form_data_list.push(form_data);
                        });
                    } else {
                        $('#projectId :selected').each(function(i, selected) {
                            projectId= $(selected).val();
                            $('#comm_mode :selected').each(function(i, selected) {
                                commModeId = $(selected).val();
                                selected_text = $(selected).text();
                                if(selected_text == 'EMAIL'){
                                    comm_value= $('#id_email').val();
                                } else if(selected_text == 'JIRA'){
                                    comm_value= $('#id_jira').val();
                                } else if(selected_text == 'SLACK'){
                                    comm_value= $('#id_slack').val();
                                } else if(selected_text == 'SNS'){
                                    comm_value= $('#id_sns').val();
                                } else if(selected_text == 'SQS'){
                                    comm_value= $('#id_sqs').val();
                                }

                                var form_data={
                                    projectId:projectId,
                                    eventId:eventId,
                                    commModeId:commModeId,
                                    isGlobalSubscription: isGlobalSubscription,
                                    communicationValues:comm_value
                                };
                                form_data_list.push(form_data);
                            });
                        });
                    }

                    if(form_data_list.length > 0){
                        $.ajax({
                            url : 'addEventSubscription',
                            type : 'POST',
                            headers: { 'token':$("#token").val()},
                            datatype : 'json',
                            data:JSON.stringify(form_data_list),
                            contentType : "application/json",
                            success : function(responseObj) {
                                console.log(responseObj);
                                //var j_obj = $.parseJSON(responseObj);
                                var status= responseObj.status;
                                toastr.info(responseObj.message);

                                if (status=='success') {
                                    setTimeout( function() {
                                        window.location.href = 'alertEventSubscription?selectedEvent='+eventId+'';
                                    }, 2000);
                                } else {
                                    window.location.reload;
                                }
                            },
                            error : function(xhr, textStatus, errorThrown) {
                                window.location.reload;
                            }
                        });
                    } else {
                        toastr.info('No subscription details found to add');
                    }
                }

                return false;
          });


		var eventName = $("#currSelectedEvent").val();
		var eventId = $("#currSelectedEventId").val();

		$('#comm_mode').multiselect({
	        maxHeight : 200,
	        buttonWidth : '560px',
	        includeSelectAllOption : true,
	        enableFiltering : true,
	        selectAll: true,
	        nonSelectedText : 'Select Communication Mode'

	    });

		$('#projectId').multiselect({
	        maxHeight : 200,
	        buttonWidth : '560px',
	        includeSelectAllOption : true,
	        enableFiltering : true,
	        selectAll: true,
	        nonSelectedText : 'Select Project'

	    });

	    $("#projectId").change(function() {
            console.log($('#projectId').val());
	        $('.input_error').remove();
	        if($('#projectId').val() == 'undefined' || $('#projectId').val() == null ){
                 $('<span class="input_error" style="font-size:14px;color:red">Please select atleast one projectId column</span>').insertAfter($('#projectId'));
                 no_error = 0;
            }
	    });

	    $("#comm_mode").change(function() {
	        console.log($('#comm_mode').val());
	        $('.input_error').remove();
            if($('#comm_mode').val() == 'undefined' || $('#comm_mode').val() == null ){
                 $('<span class="input_error" style="font-size:14px;color:red">Please select atleast one communication mode</span>').insertAfter($('#comm_mode'));
                 no_error = 0;
            }
        });

		$('#subscription_type').change(function() {
			var selected_subscription_type = $('#subscription_type').val();

			 $('#alert_email_div').addClass('hidden');
			 $('#alert_jira_div').addClass('hidden');
			 $('#alert_slack_div').addClass('hidden');
			 $('#alert_sns_div').addClass('hidden');
			 $('#alert_sqs_div').addClass('hidden');
			 $("#comm_mode").multiselect('clearSelection');

			if (selected_subscription_type == 'global'){
				$('#projects_div').addClass('hidden').show();
				$('#comm_mode_div').removeClass('hidden').show();

			} else if (selected_subscription_type == 'project'){
				$('#projects_div').removeClass('hidden').show();
				$('#comm_mode_div').removeClass('hidden').show();

			} else {
				$('#comm_mode_div').addClass('hidden').show();
				$('#projects_div').addClass('hidden').show();
			}

		});

		$('#comm_mode').change(function() {
			var selectedModes=[];

			 $('#alert_email_div').addClass('hidden');
			 $('#alert_jira_div').addClass('hidden');
			 $('#alert_slack_div').addClass('hidden');
			 $('#alert_sns_div').addClass('hidden');
			 $('#alert_sqs_div').addClass('hidden');

			$('#comm_mode :selected').each(function(i, selected) {
				 var selected_mode = $(selected).text();
				 selectedModes[i] =  selected_mode;

				 if(selected_mode == 'EMAIL'){
					 $('#alert_email_div').removeClass('hidden');
				 }

				 if(selected_mode == 'JIRA'){
					 $('#alert_jira_div').removeClass('hidden');
				 }

				 if(selected_mode == 'SLACK'){
					 $('#alert_slack_div').removeClass('hidden');
				 }

				 if(selected_mode == 'SNS'){
					 $('#alert_sns_div').removeClass('hidden');
				 }

				 if(selected_mode == 'SQS'){
					 $('#alert_sqs_div').removeClass('hidden');
				 }

			});

			var selectedModesStr = JSON.stringify(selectedModes);

		});

		$.ajax({
     			url : './getAllCommunicationModes',
     			type : 'GET',
     			datatype : 'json',
     			contentType : "application/json",
     			success : function(data) {
     			    $.each(data, function(index,obj) {
						$('#comm_mode').append($('<option>',
                                      {
                                          value : obj.commModeId,
                                          text : obj.commModeName
                                      }
                                   ));
                     });

     			   	$('#comm_mode').multiselect('rebuild');
                   	$('#comm_mode').multiselect({
                       includeSelectAllOption : true
                    });

     			},
     			error : function() {

     			},
     			complete : function() {

     			}

      });

	 $('#selectedEvent').on('change', function() {
			   var eventId = $("#selectedEvent").val();
			   window.location.href = 'alertEventSubscription?selectedEvent='+eventId+'';
	 });

      table2 = $('#AlertSubscriptions').dataTable({
                  "bPaginate" : true,
                  "order" : [ 0, 'asc' ],
                  "bInfo" : true,
                  "iDisplayStart" : 0,
                  "bProcessing" : false,
                  "bServerSide" : false,
                  "bFilter" : true,
                  'sScrollX' : true,
                    "sAjaxSource" : path+"/getAllSubscribedEventByEventId?eventId="+${eventId}+"",
                    "aoColumns": [
                              { "data": "alertSubId" },
                              { "data": "projectName" },
                              { "data": "commModeName" },
                              { "data": "isGlobalSubscription" },
                              { "data": "communicationValues",  
                            	  "render": function(data, type, row, meta){
                            		  var alertSubId = row.alertSubId;
                            		  var commValue = data;
                                      data = "<label id=\"commVal_"+alertSubId+"_label\">"+commValue+"</label>";
                                      data = data + "<input id=\"commVal_"+alertSubId+"_text\" type=\"text\" class=\"hidden input-hide col-xs-9\" style=\"width: 100%;\"value=\""+commValue+"\"/>"
                                  	return data;
                            	  }
                              }
                             

                      ],
                    'createdRow': function( row, data, dataIndex ) {
                    	 var alertSubId = data["alertSubId"];
                          $('td:eq(4)', row).attr('id', 'commVal'+alertSubId);
                          $(row).attr('id', alertSubId);
                     },
                    "dom" : 'C<"clear">lfrtip',
                      colVis : {
                          "align" : "right",
                          restore : "Restore",
                          showAll : "Show all",
                          showNone : "Show none",
                          order : 'alpha'
                      },
                      "language" : {
                          "infoFiltered" : ""
                      },
                      "dom" : 'Cf<"toolbar"">rtip',
         });
      	
      $("#AlertSubscriptions").on("click", "td", function() {
    	     var td_position = $(this).index()+1;
    	     
    	     $(".input-hide").addClass("hidden");
  			 $("label").removeClass("hidden");
  			
    	     if(td_position == 5){
     			$(this).find(".input-hide").removeClass("hidden");
     			$(this).find("label").addClass("hidden");
    	     } 
    	     
       });
      
      $("#AlertSubscriptions").on("blur keypress", "td", function(e) {

    	  if (e.type == 'focusout'
				|| e.keyCode == '13') {
		      if (!$(this).data('done')) {
		    	$(this).data('done', true);
				var indexRow = $(this).closest("tr").attr("id");
				var communicationValues = $("#commVal_"+ indexRow+ "_text").val();
	             if (communicationValues == ''){
                    $(".input-hide").addClass("hidden");
                    $("label").removeClass("hidden");
                    toastr.info('Communication values cannot be empty, not allowed to submit!');
                    window.setTimeout(function(){
                        window.location.reload();
                    }, 1000);
                } else{
	                var form_data = {
                        alertSubscriptionId : indexRow,
                        communicationValues : communicationValues
                    };
                    $.ajax({
                            url : "./updateAlertSubscriptionCommunicationValues",
                            type : 'POST',
                            headers: { 'token':$("#token").val()},
                            datatype : 'json',
                            data : form_data,
                            success : function(
                                    message) {
                                var j_obj = $.parseJSON(message);
                                var status = j_obj.status;
                                var message = j_obj.message;
                                toastr.info(message);
                                if (status == 'success') {
                                    $("#commVal_"+ indexRow+ "_label").html(communicationValues);
                                }

                                /*
                                window.setTimeout(function(){
                                    window.location.reload();
                                }, 1000);
                                */
                            },
                            error : function(
                                    xhr,
                                    textStatus,
                                    errorThrown) {
                            }
                        });

                        $(".input-hide").addClass("hidden");
                        $("label").removeClass("hidden");
                   }
	            }

    	  }
      });   
      
      $("#AlertSubscriptions").on("focus", "td", function() {
    	  var td_position = $(this).index()+1;
    	  if(td_position == 5){
  			$(this).data('done', false);
      	  }
  	  });
       
});
</script>