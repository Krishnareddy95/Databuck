<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<!-- BEGIN CONTENT -->
<script type="text/javascript">
function sendInfo() {
    var schedularname = document.getElementById("nameId").value;
    var expr = /^[a-zA-Z0-9_]*$/;
    if (!expr.test(schedularname)) {
    	 alert("Please Enter Scheduler Name without spaces and special characters");
    }
}

</script>
<div class="page-content-wrapper">
	<!-- BEGIN CONTENT BODY -->
	<div class="page-content">
		<!-- END PAGE HEADER-->
		<div class="row">
			<div class="col-md-6" style="width: 100%;">
				<!-- BEGIN SAMPLE FORM PORTLET-->
				<div class="portlet light bordered init">

					<input type="hidden" id="timerid" value=1 /> <input type="hidden"
						id="tourl" value="   " />
					<div class="form-body">
						<div class="row">
							<div class="col-md-12 col-capture">
								<div class="caption font-red-sunglo">
									<span class="caption-subject bold "> Edit Scheduler </span>
								</div>
							</div>
						</div>
						</br>

						<div class="row">
							<div class="col-md-6 col-capture">
								<div class="form-group form-md-line-input">
									<input type="text" class="form-control catch-error" id="nameId" maxlength="45" onkeyup="sendInfo()"
										name="name" value="${listScheduleData.name}" placeholder="Enter the Name"> <label for="form_control_1">Name*</label>
								</div>
								<input type="hidden" id="schedulerId" value="${listScheduleData.idSchedule}">
								<br /> <span class="required"></span>
							</div>

							<div class="col-md-6 col-capture">
								<div class="form-group form-md-line-input">
									<input type="text" class="form-control" id="descriptionId"
										name="description" maxlength="200"
										value="${listScheduleData.description}" placeholder=" Enter Description ">
									<label for="form_control">Description</label>
								</div>
							</div>
						</div>
						<div class="row">
							<div class="col-md-6 col-capture">
								<div class="form-group form-md-line-input">
									<select name="frequency" id="frequencyId" class="form-control">
										<option value="-1">Select...</option>
										<option value="weekly">Weekly</option>
										<option value="monthly">Monthly</option>
									</select> <br /> <label for="form_control_1">Choose Frequency</label>
									<input type="hidden" id="selectedFrequencyId" value="${listScheduleData.frequency}">
								</div>
								<br /> <span class="required"></span>
							</div>
							<div class="col-md-6 col-capture hidden" id="weeklyid">
								<div class="form-group form-md-line-input">
									Scheduled Day<br /> 
									<c:set var="selectedDays" value="${listScheduleData.scheduleDay}"/>
									<input type="checkbox" name="CheckBox[]" id="check_1" value="All" <c:if test="${fn:contains(selectedDays,'All')}">checked</c:if>>All<br> 
									<input type="checkbox" name="CheckBox[]" id="check_2" value="Monday" <c:if test="${fn:contains(selectedDays,'Monday')}">checked</c:if>>Monday<br> 
									<input type="checkbox" name="CheckBox[]" id="check_3" value="Tuesday" <c:if test="${fn:contains(selectedDays,'Tuesday')}">checked</c:if>>Tuesday<br>
									<input type="checkbox" name="CheckBox[]" id="check_4" value="Wednesday" <c:if test="${fn:contains(selectedDays,'Wednesday')}">checked</c:if>>Wednesday<br>
									<input type="checkbox" name="CheckBox[]" id="check_5" value="Thursday" <c:if test="${fn:contains(selectedDays,'Thursday')}">checked</c:if>>Thursday<br>
									<input type="checkbox" name="CheckBox[]" id="check_6" value="Friday" <c:if test="${fn:contains(selectedDays,'Friday')}">checked</c:if>>Friday<br>
									<input type="checkbox" name="CheckBox[]" id="check_7" value="Saturday" <c:if test="${fn:contains(selectedDays,'Saturday')}">checked</c:if>>Saturday<br>
									<input type="checkbox" name="CheckBox[]" id="check_8" value="Sunday" <c:if test="${fn:contains(selectedDays,'Sunday')}">checked</c:if>>Sunday<br>
								</div>
								<input type="hidden" id="selectedScheduleDaysId" value="${listScheduleData.scheduleDay}">
							</div>

							<div class="col-md-6 col-capture hidden" id="monthlyid">
								<div class="form-group form-md-line-input">
									<select name="day" id="dayId" class="form-control">
										<option value="">Select...</option>
										<option value="1">1</option>
										<option value="2">2</option>
										<option value="3">3</option>
										<option value="4">4</option>
										<option value="5">5</option>
										<option value="6">6</option>
										<option value="7">7</option>
										<option value="8">8</option>
										<option value="9">9</option>
										<option value="10">10</option>
										<option value="11">11</option>
										<option value="12">12</option>
										<option value="13">13</option>
										<option value="14">14</option>
										<option value="15">15</option>
										<option value="16">16</option>
										<option value="17">17</option>
										<option value="18">18</option>
										<option value="19">19</option>
										<option value="20">20</option>
										<option value="21">21</option>
										<option value="22">22</option>
										<option value="23">23</option>
										<option value="24">24</option>
										<option value="25">25</option>
										<option value="26">26</option>
										<option value="27">27</option>
										<option value="28">28</option>
										<option value="29">29</option>
										<option value="30">30</option>
										<option value="31">31</option>
									</select> <label for="form_control_1">Choose Date</label>
								</div>
							</div>
						</div>
					</div>

					<div class="row">
						<div class="col-md-6 col-capture">
							<div class="form-group">
								<label class="control-label col-md-2">Time</label>
								<div class="col-md-4">
									<div class="input-group">
										<input type="text" id="getTimer" name="getTimer"
											class="form-control timepicker timepicker-24"> <span
											class="input-group-btn">
											<button class="btn default" type="button">
												<i class="fa fa-clock-o"></i>
											</button>
										</span>
									</div>
								</div>
							</div>
							<input type="hidden" id="selectedTimerId" value="${listScheduleData.time}">
						</div>
					</div>
					<div class="form-actions noborder align-center">
						<button type="submit" class="btn blue" id="startTaskId">Submit</button>
					</div>
				</div>

			</div>
		</div>

		<!-- END SAMPLE FORM PORTLET-->
	</div>
</div>
<jsp:include page="footer.jsp" />
<!-- END QUICK SIDEBAR -->
<script>

	$(document).ready(function() {
		// Set frequency
		var selectedFrequency = $("#selectedFrequencyId").val();
		console.log("selectedFrequency: "+selectedFrequency);
		$("#frequencyId").val(selectedFrequency);
		
		// Get selected Days
		var selectedScheduleDays = $("#selectedScheduleDaysId").val();
		console.log("selectedScheduleDays: "+selectedScheduleDays);

		if (selectedFrequency == 'weekly') {
			$('#weeklyid').removeClass('hidden').show();
			$('#monthlyid').addClass('hidden');
			
		} else if (selectedFrequency == 'monthly') {
			// Set Date
			$("#dayId").val(selectedScheduleDays);
			
			$('#monthlyid').removeClass('hidden').show();
			$('#weeklyid').addClass('hidden');
		}
		
		// Set Timer
		var selectedTimer = $("#selectedTimerId").val();
		console.log("selectedTimer: "+selectedTimer);
		$("#getTimer").val(selectedTimer);
		
	});

	$('#frequencyId').change(function() {
		$this = $(this);
		var selectedVal = $this.val();
		//alert(selectedVal);
		console.log(selectedVal);
		if (selectedVal == 'weekly') {
			$('#weeklyid').removeClass('hidden').show();
			$('#monthlyid').addClass('hidden');
		} else if (selectedVal == 'monthly') {
			$('#monthlyid').removeClass('hidden').show();
			$('#weeklyid').addClass('hidden');
		}
	});

	$('#startTaskId').click(function() {
		 var no_error = 1;
		    $('.input_error').remove();
		    
			    if(!checkInputText()){ 
					no_error = 0;
					alert('Vulnerability in submitted data, not allowed to submit!');
				}	
		    
		    	if($("#nameId").val().length == 0) {
		    		console.log('nameId');
		    		  $('<span class="input_error" style="font-size:12px;color:red">Please enter Name</span>').insertAfter($('#nameId'));
		    	        no_error = 0;
		    	}

 		    	if($("#frequencyId").val()== -1) {
		    		console.log('schemaTypeid');
		    		  $('<span class="input_error" style="font-size:12px;color:red">Please Choose a Frequency</span>').insertAfter($('#frequencyId'));
		    	        no_error = 0;
		    	  }
		    if(! no_error){
		              return false;
		          }
		var checkboxes = document.getElementsByName('CheckBox[]');
		var vals = "";
		for (var i = 0, n = checkboxes.length; i < n; i++) {
			if (checkboxes[i].checked) {
				vals += checkboxes[i].value + ", ";
				//console.log(checkboxes[i].value);
			}
		}
		var l=vals.length-2;
		var v=vals.substr(0,l);
		console.log(" result is ----  "+v);
		var form_data = {
			idSchedule:$("#schedulerId").val(),
			name : $("#nameId").val(),
			description : $("#descriptionId").val(),
			frequency : $("#frequencyId").val(),
			scheduledDay : v,
			day : $("#dayId").val(),
			ScheduleTimer : $("#getTimer").val(),
		};
		console.log(form_data); //return false;

		 
		
		$.ajax({
			url : './editScheduleTask',
			type : 'POST',
			headers: { 'token':$("#token").val()},
			datatype : 'json',
			data : form_data,
			success : function(data) {
				if (data != "") {
					toastr.info('Run task Schedule edited successfully');
					setTimeout(function() {
						location.reload();
						window.location.href= 'viewSchedules';

					}, 1000);
				} else {
					toastr.info('There was a problem.');
				}
			},
			error : function(xhr, textStatus, errorThrown) {
				$('#initial').hide();
				$('#fail').show();
			}
		});

		return false;
	});
</script>
<!-- END CONTAINER -->