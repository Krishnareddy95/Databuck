<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />

<script>
	var request;
	var vals = "";
	function sendInfo() {
		var v = document.getElementById("nameId").value;
		//var v=document.vinform.Database.value;  
		var url = "./duplicateschedulername?val=" + v;
		//alert("in ajax code");
		if (window.XMLHttpRequest) {
			request = new XMLHttpRequest();
		} else if (window.ActiveXObject) {
			request = new ActiveXObject("Microsoft.XMLHTTP");
		}

		try {
			request.onreadystatechange = getInfo;
			request.open("POST", url, true);
			request.setRequestHeader('token', $("#token").val());
			request.send();
		} catch (e) {
			//alert("Unable to connect to server");
		}
	}

	function getInfo() {
		if (request.readyState == 4) {
			var val = request.responseText;
			//alert("getInfo");
			//alert(val);
			var sal = "Database already exists";
			//console.log(val);
			document.getElementById('amit').innerHTML = val;
			if (val != "") {
				vals = val;
				//alert("please enter another name");
				return false;
			}
		}
	}
</script>
<!-- BEGIN CONTENT -->

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
									<span class="caption-subject bold "> Create Scheduler </span>
								</div>
							</div>
						</div>
						</br>

						<div class="row">
							<div class="col-md-6 col-capture">
								<div class="form-group form-md-line-input">
									<input type="text" class="form-control catch-error" id="nameId"
										maxlength="45" name="name" placeholder=" Enter the Name"
										onkeyup="sendInfo()"> <label for="form_control_1">Name*</label>
								</div>
								<span class="help-block required"
									style="font-size: 12px; color: red" id="amit"></span>
							</div>

							<div class="col-md-6 col-capture">
								<div class="form-group form-md-line-input">
									<input type="text" class="form-control" id="descriptionId"
										maxlength="200" name="description"
										placeholder=" Enter Description "> <label
										for="form_control">Description</label>
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
								</div>
								<br /> <span class="required"></span>
							</div>
							<div class="col-md-6 col-capture hidden" id="weeklyid"
								width="400px">
								<div class="form-group form-md-line-input" id="checkAll">
									Scheduled Day<br /> <br /> <span class="required"></span> <input
										type="checkbox" name="CheckBox[]" value="All">All<br>
									<input type="checkbox" name="CheckBox[]" value="Monday">Monday<br>
									<input type="checkbox" name="CheckBox[]" value="Tuesday">Tuesday<br>
									<input type="checkbox" name="CheckBox[]" value="Wednesday">Wednesday<br>
									<input type="checkbox" name="CheckBox[]" value="Thursday">Thursday<br>
									<input type="checkbox" name="CheckBox[]" value="Friday">Friday<br>
									<input type="checkbox" name="CheckBox[]" value="Saturday">Saturday<br>
									<input type="checkbox" name="CheckBox[]" value="Sunday">Sunday<br>
								</div>
								<br>

							</div>

							<div class="col-md-6 col-capture hidden" id="monthlyid">
								<div class="form-group form-md-line-input">
									<select name="day" id="dayId" class="form-control">
										<option value="-1">Select...</option>
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
								<br /> <span class="required"></span>
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
						</div>
					</div>
					<div class="form-actions noborder align-center">
						<button type="submit" class="btn blue" id="startTaskId">Submit</button>
					</div>
				</div>


				</form>
			</div>
		</div>


		<!-- END SAMPLE FORM PORTLET-->
	</div>
</div>
</div>
<jsp:include page="footer.jsp" />
<!-- END QUICK SIDEBAR -->
<script>
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

	$('#startTaskId')
			.click(
					function() {
						var no_error = 1;
						$('.input_error').remove();

						if (!checkInputText()) {
							no_error = 0;
							alert('Vulnerability in submitted data, not allowed to submit!');
						}
						if ($("#nameId").val().length == 0) {
							console.log('nameId');
							$(
									'<span class="input_error" style="font-size:12px;color:red">Please enter Name</span>')
									.insertAfter($('#nameId'));
							no_error = 0;
						}
						/* 		    	if($("#descriptionId").val().length == 0) {
						 console.log('descriptionId');
						 $('<span class="input_error" style="font-size:12px;color:red">Please enter Description</span>').insertAfter($('#descriptionId'));
						 no_error = 0;
						 }
						 */
						if ($("#frequencyId").val() == -1) {
							console.log('frequencyId');
							$(
									'<span class="input_error" style="font-size:12px;color:red">Please Choose a Frequency</span>')
									.insertAfter($('#frequencyId'));
							no_error = 0;
						}
						/*if (!($('#checkAll').is(':checked'))) {

							if ($("#checkAll").val().length == 0) {
								console.log('headerFilePath');
								$(
									'<span class="input_error" style="font-size:12px;color:red">Please enter week</span>')
									.insertAfter($('#checkAll'));
								no_error = 0;
							}*/
							// By Mamta 28-Feb-2022
						if ($("#dayId").val() == -1
								&& $("#frequencyId").val() == "monthly") {
							$(
									'<span class="input_error" style="font-size:12px;color:red">Please Choose Date</span>')
									.insertAfter($('#dayId'));
							no_error = 0;
						}
						var amit1 = $('#amit').html();
						console.log(amit1);
						if (amit1 == "") {

						} else {
							console.log("inside if");
							no_error = 0;
						}

						var checkboxes = document
								.getElementsByName('CheckBox[]');
						var vals = "";
						// By Mamta 28-Feb-2022
						var no_error1 = 1;
						$(document).on("click", "input[name='CheckBox[]']",
								function() {

									no_error1 = 0;
									$('.input_error').remove();

								});
						for (var i = 0, n = checkboxes.length; i < n; i++) {

							if (checkboxes[i].checked) {

								vals += checkboxes[i].value + ", ";
								//console.log(checkboxes[i].value);
								no_error1 = 0;
							}

						}
						if (no_error1 && $("#frequencyId").val() == "weekly") {
							console.log('checkAll');

							$(
									'<span class="input_error" style="font-size:12px;color:red">Please Select Scheduled Day</span>')
									.insertAfter($('#checkAll'));
							no_error = 0;
						}

						if (!no_error) {
							return false;
						}
						var l = vals.length - 2;
						var v = vals.substr(0, l);
						console.log(" result is ----  " + v);
						var form_data = {
							//	idApp: $("#locationid").val(),
							name : $("#nameId").val(),
							description : $("#descriptionId").val(),
							frequency : $("#frequencyId").val(),
							scheduledDay : v,
							day : $("#dayId").val(),
							ScheduleTimer : $("#getTimer").val(),
						//task_name : $("#locationid option:selected").text(),
						// value : $('#locationid:selected').html(),
						// times: new Date().getTime(),

						};
						console.log(form_data); //return false;
						$('button').prop('disabled', true);

						$
								.ajax({
									url : './scheduleTask',
									type : 'POST',
									headers : {
										'token' : $("#token").val()
									},
									datatype : 'json',
									data : form_data,
									success : function(data) {
										if (data != "") {
											toastr
													.info('Run task Scheduled successfully');
											setTimeout(
													function() {
														//location.reload();
														window.location.href = 'viewSchedules';

													}, 1000);
										} else {
											toastr.info('There was a problem.');
											$('button').prop('disabled', false);
										}
									},
									error : function(xhr, textStatus,
											errorThrown) {
										$('#initial').hide();
										$('#fail').show();
									}
								});

						return false;
					});
</script>
<!-- END CONTAINER -->