
$('#ruleCategoryid').change(
		function() {
			if ($('#ruleCategoryid').val() == 'Referential') {
				$('#ruleexprdivid').removeClass('hidden').show();
				$('#rightdatasourceid').addClass('hidden').hide();
				$('#rightdatasourceid2').addClass('hidden').hide();
				$('#matchexprdivid').addClass('hidden').hide();
				$('#matchTypedivid').addClass('hidden').hide();

			} else if (($('#ruleCategoryid').val() == 'Cross Referential')) {
				$('#ruleexprdivid').removeClass('hidden').show();
				$('#matchexprdivid').removeClass('hidden').show();
				$('#rightdatasourceid2').removeClass('hidden').show();
				$('#rightdatasourceid').removeClass('hidden').show();
				$("#matchexprid").attr("placeholder",
						"dataSource1.columnName=dataSource2.columnName")
						.val("").focus().blur();
				$('#matchTypedivid').addClass('hidden').hide();
			} else if (($('#ruleCategoryid').val() == 'Orphan')) {
				$('#matchexprdivid').removeClass('hidden').show();
				$('#rightdatasourceid2').removeClass('hidden').show();
				$('#rightdatasourceid').removeClass('hidden').show();
				$('#ruleexprdivid').addClass('hidden').hide();
				$("#matchexprid").attr("placeholder", "column1=column2")
						.val("").focus().blur();
				$('#matchTypedivid').addClass('hidden').hide();
			} else if (($('#ruleCategoryid').val() == 'Regular Expression')) {
				$('#ruleexprdivid').removeClass('hidden').show();
				$('#rightdatasourceid').addClass('hidden').hide();
				$('#rightdatasourceid2').addClass('hidden').hide();
				$('#matchexprdivid').addClass('hidden').hide();
				$('#matchTypedivid').removeClass('hidden').show();
			}
		});

$(document)
		.ready(
				function() {
					$("input[type='text']").keypress(function(e) {
						if (e.keyCode == '13') {
							e.preventDefault();
						}
					});
					var global_no_date_error = 1;
					var globalcolid = '';

					$('#columntable')
							.on(
									'click',
									'.bs_confirmation_demo_2',
									function(event) {
										var self = $(this);
										setTimeout(
												function(ev) {
													$(
															"[data-toggle=confirmation]")
															.confirmation(
																	{
																		container : "body",
																		btnOkClass : "btn btn-sm btn-success",
																		btnCancelClass : "btn btn-sm btn-danger",
																		onConfirm : function(
																				event) {
																			self
																					.trigger('Confirm');
																		}
																	});
												}, 500);
									});

					$('#columntable')
							.on(
									'confirmed.bs.confirmation',
									'.bs_confirmation_demo_2',
									function(e) {

										var selectedId = $(this).attr('class');
										var str = selectedId.split(" ");
										var matchid = str[1];
										var url;

										if ($(this).hasClass('subclassdel')) {
											url = $(this).closest(
													'.portlet-body').find(
													'.tourl3').val();
											matchid = str[2];
										} else {
											url = $(this).closest(
													'.portlet-body').find(
													'.tourl2').val();
										}

										var form_data = {
											id : matchid,
											appid : $("#appid").val(),
											times : new Date().getTime()
										};
										// console.log(form_data);
										// console.log(url);return false;
										$
												.ajax({
													url : url,
													type : 'POST',headers: { 'token':$("#token").val()},
													datatype : 'json',
													data : form_data,
													success : function(message) {
														var j_obj = $
																.parseJSON(message);
														// console.log(j_obj);return
														// false;
														if (j_obj
																.hasOwnProperty('success')) {
															toastr
																	.info('Deleted successfully');
															setTimeout(
																	function() {
																		// window.location.href=
																		// j_obj.success;
																		window.location
																				.reload();

																	}, 1000);
														} else {
															if (j_obj
																	.hasOwnProperty('fail')) {
																toastr
																		.info(j_obj.fail);
															}
														}
													},
													error : function(xhr,
															textStatus,
															errorThrown) {
														$('.validation-fail')
																.removeClass(
																		'hidden')
																.html(
																		"There seems to be a network problem. Please try again in some time.");
													}
												});

										return false;
									});

					$('#dataSourceid').change(function() {
						if ($('#dataSourceid').is(":checked")) {
							$('#dataSourceHideid').removeClass('hidden');
						} else {
							$('#dataSourceHideid').addClass('hidden');
						}
					});

					$('#dataBindid').change(function() {
						if ($('#dataBindid').is(":checked")) {
							$('#dataBindHideid').removeClass('hidden');
						} else {
							$('#dataBindHideid').hide();
						}
					});

					$('#scheduleid').change(function() {
						if ($('#scheduleid').is(":checked")) {
							$('#scheduleHideid').removeClass('hidden');
						} else {
							$('#scheduleHideid').addClass('hidden');
						}
					});

					$('#moduleid').change(function() {
						if ($('#moduleid').is(":checked")) {
							$('#moduleHideid').removeClass('hidden');
						} else {
							$('#moduleHideid').addClass('hidden');
						}
					});

					$('#userid').change(function() {
						if ($('#userid').is(":checked")) {
							$('#userHideid').removeClass('hidden');
						} else {
							$('#userHideid').addClass('hidden');
						}
					});

					$('#omethodid').change(function() {
						if ($('#omethodid').val() == 'moving average') {
							$('#avgrangecontainer').show();
						} else {
							$('#avgrangecontainer').hide();
						}
					});

					$('#submitgroupid')
							.click(
									function() {

										var no_error = 1;
										$('.required').html('');

										$(".catch-error")
												.each(
														function() {
															$this = $(this);
															if ($this.val().length == 0) {
																$this
																		.closest(
																				'.col-capture')
																		.find(
																				'.required')
																		.html(
																				'<i class="fa fa-warning"> </i>');
																$(
																		'.note-danger')
																		.removeClass(
																				'hidden')
																		.html(
																				'Required data missing');
																no_error = 0;
															}
														});

										if (no_error) {

											var dataSourceAll = '';
											var dataBindAll = '';
											var scheduleAll = '';
											var moduleAll = '';
											var userAll = '';

											// $(".accessControl").each(function()
											// {
											// var $this = $(this);
											// if ($this.is(":checked")) {
											// if (count) {
											// dataaccessControlAll += ('-' +
											// $this.val());
											// } else {
											// dataaccessControlAll =
											// $this.val();
											// }
											// count++;
											// }
											// });

											if ($('#dataSourceid').is(
													":checked")) {
												$('#dataSourceid').val();
											} else {
												$('#dataSourceid').val(0);
											}

											if ($('#dataBindid').is(":checked")) {
												$('#dataBindid').val();
											} else {
												$('#dataBindid').val(0);
											}

											if ($('#scheduleid').is(":checked")) {
												$('#scheduleid').val();
											} else {
												$('#scheduleid').val(0);
											}

											if ($('#moduleid').is(":checked")) {
												$('#moduleid').val();
											} else {
												$('#moduleid').val(0);
											}

											if ($('#userid').is(":checked")) {
												$('#userid').val();
											} else {
												$('#userid').val(0);
											}

											var count = 0;

											$(".dataSource")
													.each(
															function() {
																var $this = $(this);
																if ($this
																		.is(":checked")) {
																	if (count) {
																		dataSourceAll += ('-' + $this
																				.val());
																	} else {
																		dataSourceAll = $this
																				.val();
																	}
																	count++;
																}
															});

											var count = 0;

											$(".dataBind")
													.each(
															function() {
																var $this = $(this);
																if ($this
																		.is(":checked")) {
																	if (count) {
																		dataBindAll += ('-' + $this
																				.val());
																	} else {
																		dataBindAll = $this
																				.val();
																	}
																	count++;
																}
															});

											var count = 0;

											$(".schedule")
													.each(
															function() {
																var $this = $(this);
																if ($this
																		.is(":checked")) {
																	if (count) {
																		scheduleAll += ('-' + $this
																				.val());
																	} else {
																		scheduleAll = $this
																				.val();
																	}
																	count++;
																}
															});

											var count = 0;

											$(".module")
													.each(
															function() {
																var $this = $(this);
																if ($this
																		.is(":checked")) {
																	if (count) {
																		moduleAll += ('-' + $this
																				.val());
																	} else {
																		moduleAll = $this
																				.val();
																	}
																	count++;
																}
															});

											var count = 0;

											$(".user")
													.each(
															function() {
																var $this = $(this);
																if ($this
																		.is(":checked")) {
																	if (count) {
																		userAll += ('-' + $this
																				.val());
																	} else {
																		userAll = $this
																				.val();
																	}
																	count++;
																}
															});

											var form_data = {
												groupName : $("#groupNameid")
														.val(),
												description : $(
														"#descriptionid").val(),
												dataaccessControlAll : dataaccessControlAll,
												dataSourceAll : dataSourceAll,
												dataBindAll : dataBindAll,
												scheduleAll : scheduleAll,
												moduleAll : moduleAll,
												userAll : userAll,
												times : new Date().getTime()
											};
											// console.log(form_data); return
											// false;

											if ($('#editid').length > 0) {
												form_data.editMode = 'yes';
												form_data.idGroup = $(
														'#idGroup').val();
											}

											$
													.ajax({
														url : $("#tourl").val(),
														type : 'POST',headers: { 'token':$("#token").val()},
														datatype : 'json',
														data : form_data,
														success : function(
																message) {
															var j_obj = $
																	.parseJSON(message);
															// console.log(j_obj);return
															// false;
															if (j_obj
																	.hasOwnProperty('success')) {
																$('.init')
																		.hide();
																$('.note-info')
																		.removeClass(
																				'hidden');
															} else {
																if (j_obj
																		.hasOwnProperty('fail')) {
																	$(
																			'.note-danger')
																			.removeClass(
																					'hidden')
																			.html(
																					j_obj.fail);
																}
															}
														},
														error : function(xhr,
																textStatus,
																errorThrown) {
															$('.catch-form-msg')
																	.find(
																			'.help-block')
																	.html(
																			"There seems to be a network problem. Please try again in some time.");
														}
													});
										}
										return false;
									});

					// remove error messages on input text click
					$('.form-control').click(
							function() {
								$(this).closest('.col-capture').find(
										'.input_error').remove();
							});

					$('#createUserId')
							.click(
									function() {
										var no_error = 1;
										$('.required').html('');

										if ($('#firstNameId').val().length == 0) {
											$(
													'<div class="input_error" style="font-size:12px;color:red;">Please enter First Name</div>')
													.insertAfter(
															$('#firstNameId'));
											no_error = 0;
										}
										if ($('#lastNameId').val().length == 0) {
											$(
													'<div class="input_error" style="font-size:12px;color:red;">Please enter Last Name</div>')
													.insertAfter(
															$('#lastNameId'));
											no_error = 0;
										}
										if ($('#emailId').val().length == 0) {
											$(
													'<div class="input_error" style="font-size:12px;color:red;">Please enter Email</div>')
													.insertAfter($('#emailId'));
											no_error = 0;
										}
										if ($('#passwordId').val().length == 0) {
											$(
													'<div class="input_error" style="font-size:12px;color:red;">Please enter Password</div>')
													.insertAfter(
															$('#passwordId'));
											no_error = 0;
										}
										if ($('#confirmPasswordId').val().length == 0) {
											$(
													'<div class="input_error" style="font-size:12px;color:red;">Please confirm Password</div>')
													.insertAfter(
															$('#confirmPasswordId'));
											no_error = 0;
										}
										if ($('#companyId').val().length == 0) {
											$(
													'<div class="input_error" style="font-size:12px;color:red;">Please enter Company</div>')
													.insertAfter(
															$('#companyId'));
											no_error = 0;
										}

										if (no_error) {
											var form_data = {
												firstName : $("#firstNameId")
														.val(),
												lastName : $("#lastNameId")
														.val(),
												email : $("#emailId").val(),
												password : $("#passwordId")
														.val(),
												confirmPassword : $(
														"#confirmPasswordId")
														.val(),
												company : $("#companyId").val(),
												department : $("#departmentId")
														.val(),
												times : new Date().getTime()
											};

											var userRole = '';
											$(".accessControlCheckbox")
													.each(
															function() {
																if ($(
																		'#'
																				+ $(
																						this)
																						.attr(
																								'id'))
																		.is(
																				':checked')) {
																	userRole += ($(
																			'#'
																					+ $(
																							this)
																							.attr(
																									'id'))
																			.val() + '-');
																}
															});
											form_data.userRole = userRole;

											$
													.ajax({
														url : $("#toUrl").val(),
														type : 'POST',headers: { 'token':$("#token").val()},
														datatype : 'json',
														data : form_data,
														success : function(
																message) {
															var j_obj = $
																	.parseJSON(message);
															// console.log(j_obj);return
															// false;
															if (j_obj
																	.hasOwnProperty('success')) {
																$('.init')
																		.hide();
																$('.note-info')
																		.removeClass(
																				'hidden');
															} else {
																if (j_obj
																		.hasOwnProperty('fail')) {
																	$(
																			'.validation-fail')
																			.removeClass(
																					'hidden')
																			.html(
																					j_obj.fail);
																}
															}
														},
														error : function(xhr,
																textStatus,
																errorThrown) {
															$(
																	'.validation-fail')
																	.removeClass(
																			'hidden')
																	.html(
																			"There seems to be a network problem. Please try again in some time.");
														}
													});
										}
										return false;
									});

					$('#updateUserId')
							.click(
									function() {
										var no_error = 1;
										$('.required').html('');

										if ($('#firstNameId').val().length == 0) {
											$(
													'<div class="input_error" style="font-size:12px;color:red;">Please enter First Name</div>')
													.insertAfter(
															$('#firstNameId'));
											no_error = 0;
										}
										if ($('#lastNameId').val().length == 0) {
											$(
													'<div class="input_error" style="font-size:12px;color:red;">Please enter Last Name</div>')
													.insertAfter(
															$('#lastNameId'));
											no_error = 0;
										}
										if ($('#companyId').val().length == 0) {
											$(
													'<div class="input_error" style="font-size:12px;color:red;">Please enter Company</div>')
													.insertAfter(
															$('#companyId'));
											no_error = 0;
										}

										if (no_error) {
											var form_data = {
												editMode : 'yes',
												idUser : $("#idUserId").val(),
												firstName : $("#firstNameId")
														.val(),
												lastName : $("#lastNameId")
														.val(),
												company : $("#companyId").val(),
												department : $("#departmentId")
														.val(),
												times : new Date().getTime()
											};

											var userRole = '';
											$(".accessControlCheckbox")
													.each(
															function() {
																if ($(
																		'#'
																				+ $(
																						this)
																						.attr(
																								'id'))
																		.is(
																				':checked')) {
																	userRole += ($(
																			'#'
																					+ $(
																							this)
																							.attr(
																									'id'))
																			.val() + '-');
																}
															});
											form_data.userRole = userRole;

											// console.log(form_data); return
											// false;

											$
													.ajax({
														url : $("#toUrl").val(),
														type : 'POST',headers: { 'token':$("#token").val()},
														datatype : 'json',
														data : form_data,
														success : function(
																message) {
															var j_obj = $
																	.parseJSON(message);
															// console.log(j_obj);return
															// false;
															if (j_obj
																	.hasOwnProperty('success')) {
																$('.init')
																		.hide();
																$('.note-info')
																		.removeClass(
																				'hidden');
															} else {
																if (j_obj
																		.hasOwnProperty('fail')) {
																	$(
																			'.validation-fail')
																			.removeClass(
																					'hidden')
																			.html(
																					j_obj.fail);
																}
															}
														},
														error : function(xhr,
																textStatus,
																errorThrown) {
															$(
																	'.validation-fail')
																	.removeClass(
																			'hidden')
																	.html(
																			"There seems to be a network problem. Please try again in some time.");
														}
													});
										}
										return false;
									});

					$('#deleteUserId')
							.click(
									function() {
										var form_data = {
											idUser : $("#idUserId").val(),
											times : new Date().getTime()
										};
										// console.log(form_data); return false;
										$
												.ajax({
													url : $("#toUrl").val(),
													type : 'POST',headers: { 'token':$("#token").val()},
													datatype : 'json',
													data : form_data,
													success : function(message) {
														var j_obj = $
																.parseJSON(message);
														// console.log(j_obj);return
														// false;
														if (j_obj
																.hasOwnProperty('success')) {
															$('.init').hide();
															$('.note-info')
																	.removeClass(
																			'hidden');
														} else {
															if (j_obj
																	.hasOwnProperty('fail')) {
																$(
																		'.validation-fail')
																		.removeClass(
																				'hidden')
																		.html(
																				j_obj.fail);
															}
														}
													},
													error : function(xhr,
															textStatus,
															errorThrown) {
														$('.validation-fail')
																.removeClass(
																		'hidden')
																.html(
																		"There seems to be a network problem. Please try again in some time.");
													}
												});

										return false;
									});

					$('#deleteTaskId')
							.click(
									function() {
										var form_data = {
											idTask : $("#idTaskId").val(),
											times : new Date().getTime()
										};
										// console.log(form_data); return false;
										$
												.ajax({
													url : $("#toUrl").val(),
													type : 'POST',headers: { 'token':$("#token").val()},
													datatype : 'json',
													data : form_data,
													success : function(message) {
														var j_obj = $
																.parseJSON(message);
														// console.log(j_obj);return
														// false;
														if (j_obj
																.hasOwnProperty('success')) {
															$('.init').hide();
															$('.note-info')
																	.removeClass(
																			'hidden');
														} else {
															if (j_obj
																	.hasOwnProperty('fail')) {
																$(
																		'.validation-fail')
																		.removeClass(
																				'hidden')
																		.html(
																				j_obj.fail);
															}
														}
													},
													error : function(xhr,
															textStatus,
															errorThrown) {
														$('.validation-fail')
																.removeClass(
																		'hidden')
																.html(
																		"There seems to be a network problem. Please try again in some time.");
													}
												});

										return false;
									});

					$('#deleteRoleId')
							.click(
									function() {
										var form_data = {
											idRole : $("#idRoleId").val(),
											times : new Date().getTime()
										};
										// console.log(form_data); return false;
										$
												.ajax({
													url : $("#toUrl").val(),
													type : 'POST',headers: { 'token':$("#token").val()},
													datatype : 'json',
													data : form_data,
													success : function(message) {
														var j_obj = $
																.parseJSON(message);
														// console.log(j_obj);return
														// false;
														if (j_obj
																.hasOwnProperty('success')) {
															$('.init').hide();
															$('.note-info')
																	.removeClass(
																			'hidden');
														} else {
															if (j_obj
																	.hasOwnProperty('fail')) {
																$(
																		'.validation-fail')
																		.removeClass(
																				'hidden')
																		.html(
																				j_obj.fail);
															}
														}
													},
													error : function(xhr,
															textStatus,
															errorThrown) {
														$('.validation-fail')
																.removeClass(
																		'hidden')
																.html(
																		"There seems to be a network problem. Please try again in some time.");
													}
												});

										return false;
									});

					// $('#edititemid').click(function()
					// {

					// var no_error = 1;

					// if(no_error)
					// {

					// var form_data = {
					// itemid: $("#itemid").val(),
					// item1: $("#itemid1").val(),
					// item2: $("#itemid2").val(),
					// item3: $("#itemid3").val(),
					// item4: $("#itemid4").val(),
					// item5: $("#itemid5").val(),
					// times: new Date().getTime()
					// };
					// // console.log(form_data); return false;

					// $.ajax({
					// url: $("#tourl").val(),
					// type: 'POST',
					// datatype: 'json',
					// data: form_data,
					// success: function(message) {
					// var j_obj = $.parseJSON(message);
					// // console.log(j_obj);return false;
					// if (j_obj.hasOwnProperty('success')) {
					// $('.init').hide();
					// $('.note-info').removeClass('hidden');
					// } else {
					// if (j_obj.hasOwnProperty('fail')) {
					// $('.note-danger').removeClass('hidden').html(j_obj.fail);
					// }
					// }
					// },
					// error: function(xhr, textStatus, errorThrown)
					// {
					// $('.catch-form-msg').find('.help-block').html("There
					// seems to be a network problem. Please try again in some
					// time.");
					// }
					// });
					// }
					// return false;
					// });

					$('#deleteitemid')
							.click(
									function() {

										var targeturl = $("#targeturl").val();
										var form_data = {
											id : $("#itemid").val(),
											times : new Date().getTime()
										};
										// console.log(form_data); return false;
										$
												.ajax({
													url : $("#tourl").val(),
													type : 'POST',headers: { 'token':$("#token").val()},headers: { 'token':$("#token").val()},
													datatype : 'json',
													data : form_data,
													success : function(message) {
														var j_obj = $
																.parseJSON(message);
														// console.log(j_obj);return
														// false;
														if (j_obj
																.hasOwnProperty('success')) {
															// $('.init').hide();
															// $('.note-info').removeClass('hidden');
															toastr
																	.info('Item Deleted successfully');
															setTimeout(
																	function() {
																		window.location.href = targeturl;

																	}, 1000);
														} else {
															if (j_obj
																	.hasOwnProperty('fail')) {
																$(
																		'.note-danger')
																		.removeClass(
																				'hidden')
																		.html(
																				j_obj.fail);
															}
														}
													},
													error : function(xhr,
															textStatus,
															errorThrown) {
														$('.catch-form-msg')
																.find(
																		'.help-block')
																.html(
																		"There seems to be a network problem. Please try again in some time.");
													}
												});

										return false;
									});

					$('#deleteblendid')
							.click(
									function() {

										var targeturl = $("#targeturl").val();
										var form_data = {
											idDataBlend : $("#blendid").val(),
										// times: new Date().getTime()
										};
										console.log(form_data); // return false;
										$
												.ajax({
													url : './deleteTempId',
													type : 'POST',headers: { 'token':$("#token").val()},
													datatype : 'json',
													data : form_data,
													success : function(data) {
														if (data != "") {
															toastr
																	.info('Blend Deleted successfully');
															setTimeout(
																	function() {
																		window.location.href = 'extendTemplateView';
																	}, 1000);
														} else {
															toastr
																	.info('There was a problem.');
														}
													},
													error : function(xhr,
															textStatus,
															errorThrown) {
														$('.catch-form-msg')
																.find(
																		'.help-block')
																.html(
																		"There seems to be a network problem. Please try again in some time.");
													}
												});

										return false;
									});

					$('#submittaskid')
							.click(
									function() {
										var no_error = 1;
										$('.required').html('');

										if ($('#taskNameId').val().length == 0) {
											$(
													'<div class="input_error" style="font-size:12px;color:red;">Please enter Task Name</div>')
													.insertAfter($('.required'));
											no_error = 0;
										}

										if (no_error) {
											var form_data = {
												taskName : $("#taskNameId")
														.val(),
												times : new Date().getTime()
											};
											if ($('#editTaskFormId').length > 0) {
												form_data.editMode = 'yes';
												form_data.idTask = $(
														'#editTaskId').val();
											}
											$
													.ajax({
														url : $("#toUrl").val(),
														type : 'POST',headers: { 'token':$("#token").val()},
														datatype : 'json',
														data : form_data,
														success : function(
																message) {
															var j_obj = $
																	.parseJSON(message);
															// console.log(j_obj);return
															// false;
															if (j_obj
																	.hasOwnProperty('success')) {
																$('.init')
																		.hide();
																$('.note-info')
																		.removeClass(
																				'hidden');
															} else {
																if (j_obj
																		.hasOwnProperty('fail')) {
																	$(
																			'.note-danger')
																			.removeClass(
																					'hidden')
																			.html(
																					j_obj.fail);
																}
															}
														},
														error : function(xhr,
																textStatus,
																errorThrown) {
															$('.note-danger')
																	.removeClass(
																			'hidden')
																	.html(
																			"There seems to be a network problem. Please try again in some time.");
														}
													});
										}
										return false;
									});

					// to show/hide the CRUD values for each task
					$(".accessControl").click(function() {
						var selectedId = '#' + ($(this).attr('id'));
						if ($(selectedId).is(":checked")) {
							// alert(selectedId);
							// var id = selectedId.replace(/#roleId/, '');
							// console.log(selectedId);
							// return false;
							$(selectedId + "Check").removeClass('hidden');
						} else {
							$(selectedId + "Check").addClass('hidden');
						}
					});

					$('#submitRoleId')
							.click(
									function() {
										var amit1 = $('#amit').html();
										var no_error = 1;
										
										if(!checkInputText()){ 
											no_error = 0;
											alert('Vulnerability in submitted data, not allowed to submit!');
										}
										
										$('.required').html('');
										$(".catch-error")
												.each(
														function() {
															$this = $(this);
															if ($this.val().length == 0) {
																$this
																		.closest(
																				'.col-capture')
																		.find(
																				'.required')
																		.html(
																				'<i class="fa fa-warning"> </i>');
																$(
																		'.note-danger')
																		.removeClass(
																				'hidden')
																		.html(
																				'Required data missing');
																no_error = 0;
															}
														});

										console.log(amit1);
										if (amit1 == "") {

										} else {
											console.log("inside amit if");
											no_error = 0;
										}
										if (no_error == 0) {
											console.log('error');
											return false;
										}
										if (no_error) {
											var taskDataAll = '';
											var count = 0;
											$(".accessControl")
													.each(
															function() {
																var selectedId = '#'
																		+ ($(this)
																				.attr('id'));
																// alert(selectedId);
																// alert(taskDataAll);
																console
																		.log("accessControl"
																				+ selectedId);
																if ($(
																		selectedId)
																		.is(
																				":checked")) {
																	if (count) {
																		taskDataAll += ('-' + $(
																				selectedId)
																				.val());
																	} else {
																		taskDataAll = $(
																				selectedId)
																				.val();
																	}
																	count++;
																}
																// console.log(taskDataAll);
															});

											var accessControlData = '';
											var count = 0;
											$(".accessControlCheck")
													.each(
															function() {
																var selectedId = '#'
																		+ ($(this)
																				.attr('id'));
																// alert(selectedId);
																// alert(accessControlData);
																// console.log("accessControlCheck"+selectedId);
																if ($(
																		selectedId)
																		.is(
																				":checked")) {
																	// alert(selectedId);
																	// alert(accessControlData);
																	if (count) {
																		accessControlData += ('-' + $(
																				selectedId)
																				.val());
																	} else {
																		accessControlData = $(
																				selectedId)
																				.val();
																	}
																	// alert(accessControlData);
																	count++;

																}
																if ($(
																		selectedId)
																		.val() == 'D') {
																	accessControlData += '|';
																	// count =
																	// 0;
																}

															});
											var accessControlAll = '';
											splitData = accessControlData
													.split("|");
											accessControlAll = splitData
													.toString();
											// console.log(accessControlAll);
											// return false;
											var form_data = {
												roleName : $("#roleNameId")
														.val(),
												description : $(
														"#descriptionId").val(),
												taskDataAll : taskDataAll,
												accessControlAll : accessControlAll
											// times: new Date().getTime()
											};
											// alert("taskDataAll="+taskDataAll);
											// alert("accessControlAll="+accessControlAll);
											if ($('#editmodeid').length > 0) {
												form_data.editMode = 'yes';
												form_data.idRole = $(
														'#idRoleId').val();
											}

											// console.log(form_data); return
											// false;

											$
													.ajax({
														url : $("#toUrl").val(),
														type : 'POST',headers: { 'token':$("#token").val()},
														datatype : 'json',
														data : form_data,
														success : function(
																message) {
															var j_obj = $
																	.parseJSON(message);
															if (j_obj
																	.hasOwnProperty('success')) {
																toastr
																		.info("New Role Added Successfully");
																setTimeout(
																		function() {
																			window.location.href = "roleManagement";
																		}, 1000);
															} else {
																if (j_obj
																		.hasOwnProperty('fail')) {
																	toastr
																			.info("There was a problem");
																}
															}
														},
														error : function(xhr,
																textStatus,
																errorThrown) {
															$('.catch-form-msg')
																	.find(
																			'.help-block')
																	.html(
																			"There seems to be a network problem. Please try again in some time.");
														}
													});
										}
										return false;
									});

					// Users Table
					if ($('#showDataTable').length > 0) {
						$('#showDataTable').DataTable();
					}
					if ($('#showAltDataTable').length > 0) {
						$('#showAltDataTable').DataTable();
					}
					// Horizontal scroll dataTable for large data sets
					if ($('.showScrollXDataTable').length > 0) {
						$('.showScrollXDataTable').DataTable({
							"scrollX" : true
						});
					}

					if ($('table.display').length > 0) {
						$('table.display').DataTable({
							responsive : true
						// details: { confirmation:
						// $.fn.dataTable.Responsive.display.modal( { }) }
						});
					}

					/** Data Source functions start */
					$('#edititemid')
							.click(
									function() {

										var no_error = 1;
										var kbe = $("#KBEid").prop("checked") ? 'Y'
												: 'N';
										var dgrp = $("#dgroupid").prop(
												"checked") ? 'Y' : 'N';
										var dkey = $("#dupkeyid").prop(
												"checked") ? 'Y' : 'N';
										var targeturl = $("#targeturl").val();

										if (no_error) {

											var form_data = {
												itemid : $("#itemid").val(),
												columnName : $("#columnNameid")
														.val(),
												displayName : $(
														"#displayNameid").val(),
												format : $("#formatid").val(),
												// KBE: $("#KBEid").val(),
												KBE : kbe,
												dgroup : dgrp,
												dupkey : dkey,
												times : new Date().getTime()
											};

											$
													.ajax({
														url : $("#tourl").val(),
														type : 'POST',headers: { 'token':$("#token").val()},
														datatype : 'json',
														data : form_data,
														success : function(
																message) {
															var j_obj = $
																	.parseJSON(message);
															// console.log(j_obj);return
															// false;
															if (j_obj
																	.hasOwnProperty('success')) {
																// $('.init').hide();
																// $('.note-info').removeClass('hidden');
																toastr
																		.info('Item updated successfully');
																setTimeout(
																		function() {
																			window.location.href = targeturl;

																		}, 1000);

															} else {
																if (j_obj
																		.hasOwnProperty('fail')) {
																	$(
																			'.note-danger')
																			.removeClass(
																					'hidden')
																			.html(
																					j_obj.fail);
																}
															}
														},
														error : function(xhr,
																textStatus,
																errorThrown) {
															$('.catch-form-msg')
																	.find(
																			'.help-block')
																	.html(
																			"There seems to be a network problem. Please try again in some time.");
														}
													});
										}
										return false;
									});

					// $('#deleteitemid').click(function()
					// {

					// var form_data = {
					// id : $("#itemid").val(),
					// times: new Date().getTime()
					// };
					// // console.log(form_data); return false;
					// $.ajax({
					// url: $("#tourl").val(),
					// type: 'POST',
					// datatype: 'json',
					// data: form_data,
					// success: function(message) {
					// var j_obj = $.parseJSON(message);
					// // console.log(j_obj);return false;
					// if (j_obj.hasOwnProperty('success')) {
					// $('.init').hide();
					// $('.note-info').removeClass('hidden');
					// } else {
					// if (j_obj.hasOwnProperty('fail')) {
					// $('.note-danger').removeClass('hidden').html(j_obj.fail);
					// }
					// }
					// },
					// error: function(xhr, textStatus, errorThrown)
					// {
					// $('.catch-form-msg').find('.help-block').html("There
					// seems to be a network problem. Please try again in some
					// time.");
					// }
					// });

					// return false;
					// });

					$('#deletesourceid')
							.click(
									function() {

										var form_data = {
											id : $("#sourceid").val(),
											times : new Date().getTime()
										};
										// console.log(form_data); return false;
										$
												.ajax({
													url : $("#tourl").val(),
													type : 'POST',headers: { 'token':$("#token").val()},
													datatype : 'json',
													data : form_data,
													success : function(message) {
														var j_obj = $
																.parseJSON(message);
														// console.log(j_obj);return
														// false;
														if (j_obj
																.hasOwnProperty('success')) {
															$('.init').hide();
															$('.note-info')
																	.removeClass(
																			'hidden');
														} else {
															if (j_obj
																	.hasOwnProperty('fail')) {
																$(
																		'.note-danger')
																		.removeClass(
																				'hidden')
																		.html(
																				j_obj.fail);
															}
														}
													},
													error : function(xhr,
															textStatus,
															errorThrown) {
														$('.catch-form-msg')
																.find(
																		'.help-block')
																.html(
																		"There seems to be a network problem. Please try again in some time.");
													}
												});

										return false;
									});

					$("#sourceid").change(function() {

						if ($("#sourceid").val() === 'mysql') {
							// load new form for getting mysql data
							// document.location.href= $("#mysqlurl").val();
							$("#csv_id").hide();
							$("#mqsql_form_id").show();
						} else {
							$("#csv_id").show();
							$("#mqsql_form_id").hide();
						}

					});

					/** Data Source functions end */

					$('#usrpswdsubmitbtnid')
							.click(
									function() {
										var no_error = 1;
										$('.input_error').remove();

										if ($("#oldpasswordid").val().length == 0) {
											$(
													'<span class="input_error" style="font-size:12px;color:red">Please enter current password</span>')
													.insertAfter(
															$('#oldpasswordid'));
											no_error = 0;
										}
										if ($("#newpasswordid").val().length == 0) {
											$(
													'<span class="input_error" style="font-size:12px;color:red">Please enter new password</span>')
													.insertAfter(
															$('#newpasswordid'));
											no_error = 0;
										}
										if ($("#newpasswordid1").val().length == 0) {
											$(
													'<span class="input_error" style="font-size:12px;color:red">Please re-enter new password</span>')
													.insertAfter(
															$('#newpasswordid1'));
											no_error = 0;
										}
										if ($("#newpasswordid").val() != $(
												"#newpasswordid1").val()) {
											$(
													'<span class="input_error" style="font-size:12px;color:red">Password fields do not match. Please enter again.</span>')
													.insertAfter(
															$('#newpasswordid1'));
											no_error = 0;
										}

										if (no_error) {
											var changepassword = {
												oldpassword : $(
														"#oldpasswordid").val(),
												newpassword : $(
														"#newpasswordid").val(),

											// times: new Date().getTime()
											};

											console.log(changepassword);
											// return false;

											$
													.ajax({
														url : './sendnewpassword',
														type : 'POST',headers: { 'token':$("#token").val()},
														datatype : 'json',
														data : JSON
																.stringify(changepassword),
														contentType : "application/json",
														success : function(data) {
															// alert(data);
															if (data != "") {
																toastr
																		.info(data);
																// toastr.info('Item
																// updated
																// successfully');
																setTimeout(
																		function() {
																			window.location
																					.reload();
																		}, 1000);
															} else {
																toastr
																		.info('There was a problem.');
															}
														},
														error : function(xhr,
																textStatus,
																errorThrown) {
															toastr
																	.info("error while updating");
															$('#initial')
																	.hide();
															$('#fail').show();
														}
													});
										}
										return false;
									});

					// $('#locationid').change(function(){
					// if( $('#locationid').val() =='File System' )
					// {
					// $('#sourcedivid').show();
					// $('#uploaddivid').show();

					// $('#s3_form_id').hide();
					// $('#hdfs_form_id').hide();
					// $('#mqsql_form_id').hide();
					// $('#ag_form_id').hide();
					// $('#mos_form_id').hide();
					// }
					// else
					// {
					// $('#sourcedivid').hide();
					// $("#csv_id").hide();

					// if( $('#locationid').val() =='HDFS' )
					// {
					// $('#hdfs_form_id').show();
					// $('#s3_form_id').hide();
					// $('#mqsql_form_id').hide();
					// $('#ag_form_id').hide();
					// $('#mos_form_id').hide();
					// }
					// if( $('#locationid').val() =='S3' )
					// {
					// $('#s3_form_id').show();
					// $('#hdfs_form_id').hide();
					// $('#mqsql_form_id').hide();
					// $('#ag_form_id').hide();
					// $('#mos_form_id').hide();
					// }
					// if( ( $('#locationid').val() =='Google' ) || (
					// $('#locationid').val() =='Azure' ) )
					// {
					// $('#ag_form_id').show();
					// $('#s3_form_id').hide();
					// $('#hdfs_form_id').hide();
					// $('#mqsql_form_id').hide();
					// $('#mos_form_id').hide();

					// }
					// if( ( $('#locationid').val() =='Cassandra' ) || (
					// $('#locationid').val() =='MongoDB' ) )
					// {
					// $('#mos_form_id').show();
					// $('#ag_form_id').hide();
					// $('#s3_form_id').hide();
					// $('#hdfs_form_id').hide();
					// $('#mqsql_form_id').hide();

					// }
					// }
					// });

					// if( $("#sourceid").val() == 'mysql')
					// {
					// if($("#hostid").val().length == 0){
					// $('<span class="input_error"
					// style="font-size:12px;color:red">Please enter Host
					// Name</span>').insertAfter($('#hostid'));
					// no_error = 0;
					// }
					// if($("#portid").val().length == 0){
					// $('<span class="input_error"
					// style="font-size:12px;color:red">Please enter Port
					// Number</span>').insertAfter($('#portid'));
					// no_error = 0;
					// }
					// if($("#userid").val().length == 0){
					// $('<span class="input_error"
					// style="font-size:12px;color:red">Please enter User
					// Name</span>').insertAfter($('#userid'));
					// no_error = 0;
					// }
					// if($("#pwdid").val().length == 0){
					// $('<span class="input_error"
					// style="font-size:12px;color:red">Please enter
					// Password</span>').insertAfter($('#pwdid'));
					// no_error = 0;
					// }
					// if($("#dbid").val().length == 0){
					// $('<span class="input_error"
					// style="font-size:12px;color:red">Please enter Database
					// Name</span>').insertAfter($('#dbid'));
					// no_error = 0;
					// }
					// if($("#tableid").val().length == 0){
					// $('<span class="input_error"
					// style="font-size:12px;color:red">Please enter Table
					// Name</span>').insertAfter($('#tableid'));
					// no_error = 0;
					// }
					// }
					// if( $('#locationid').val() =='HDFS' )
					// {
					// if($("#hosturiid").val().length == 0){
					// $('<span class="input_error"
					// style="font-size:12px;color:red">Please enter Host
					// URI</span>').insertAfter($('#hosturiid'));
					// no_error = 0;
					// }
					// if($("#folderid").val().length == 0){
					// $('<span class="input_error"
					// style="font-size:12px;color:red">Please enter Folder
					// Name</span>').insertAfter($('#folderid'));
					// no_error = 0;
					// }
					// if($("#hdfsloginid").val().length == 0){
					// $('<span class="input_error"
					// style="font-size:12px;color:red">Please enter Login
					// Details</span>').insertAfter($('#hdfsloginid'));
					// no_error = 0;
					// }
					// if($("#hdfspwdid").val().length == 0){
					// $('<span class="input_error"
					// style="font-size:12px;color:red">Please enter
					// Password</span>').insertAfter($('#hdfspwdid'));
					// no_error = 0;
					// }
					// }
					// if( $('#locationid').val() =='S3' )
					// {
					// if($("#s3bucketid").val().length == 0){
					// $('<span class="input_error"
					// style="font-size:12px;color:red">Please enter Bucket
					// Name</span>').insertAfter($('#s3bucketid'));
					// no_error = 0;
					// }
					// if($("#s3accesskeyid").val().length == 0){
					// $('<span class="input_error"
					// style="font-size:12px;color:red">Please enter Access
					// Key</span>').insertAfter($('#s3accesskeyid'));
					// no_error = 0;
					// }
					// if($("#s3pwdid").val().length == 0){
					// $('<span class="input_error"
					// style="font-size:12px;color:red">Please enter Secret
					// Access Key</span>').insertAfter($('#s3pwdid'));
					// no_error = 0;
					// }
					// }
					// if( ( $('#locationid').val() =='Google' ) || (
					// $('#locationid').val() =='Azure' ) )
					// {
					// if($("#aguriid").val().length == 0){
					// $('<span class="input_error"
					// style="font-size:12px;color:red">Please enter
					// URI</span>').insertAfter($('#aguriid'));
					// no_error = 0;
					// }
					// if($("#agfolderid").val().length == 0){
					// $('<span class="input_error"
					// style="font-size:12px;color:red">Please enter Folder
					// Name</span>').insertAfter($('#agfolderid'));
					// no_error = 0;
					// }
					// if($("#agloginid").val().length == 0){
					// $('<span class="input_error"
					// style="font-size:12px;color:red">Please enter Access
					// Name</span>').insertAfter($('#agloginid'));
					// no_error = 0;
					// }
					// if($("#agspwdid").val().length == 0){
					// $('<span class="input_error"
					// style="font-size:12px;color:red">Please enter Account
					// Key</span>').insertAfter($('#agspwdid'));
					// no_error = 0;
					// }
					// }
					// if( ( $('#locationid').val() =='Cassandra' ) || (
					// $('#locationid').val() =='MongoDB' ) )
					// {
					// if($("#moshosturiid").val().length == 0){
					// $('<span class="input_error"
					// style="font-size:12px;color:red">Please enter
					// URI</span>').insertAfter($('#moshosturiid'));
					// no_error = 0;
					// }
					// if($("#mosloginid").val().length == 0){
					// $('<span class="input_error"
					// style="font-size:12px;color:red">Please enter User
					// Name</span>').insertAfter($('#mosloginid'));
					// no_error = 0;
					// }
					// if($("#mospwdid").val().length == 0){
					// $('<span class="input_error"
					// style="font-size:12px;color:red">Please enter
					// Password</span>').insertAfter($('#mospwdid'));
					// no_error = 0;
					// }
					// if($("#mosdbid").val().length == 0){
					// $('<span class="input_error"
					// style="font-size:12px;color:red">Please enter Database
					// Name</span>').insertAfter($('#mosdbid'));
					// no_error = 0;
					// }
					// if($("#moscolid").val().length == 0){
					// $('<span class="input_error"
					// style="font-size:12px;color:red">Please enter
					// Collection</span>').insertAfter($('#moscolid'));
					// no_error = 0;
					// }
					// }

					// $('#blendcolumnid').change(function()
					// {
					// var column_temp = $('#blendcolumnid').val()
					// var formula_val_temp = $("#formulaid").val();
					// var str_len = formula_val_temp.length;
					// var letters = /^[A-Za-z]+$/;
					// if( str_len!=0)
					// {
					// var temp = formula_val_temp.substr(str_len-1,str_len);
					// if( temp.match(letters) || temp==')')
					// {
					// alert('PLease select operator before proceeding');
					// }
					// else
					// {
					// formula_val_temp = formula_val_temp.concat(column_temp);
					// $("#formulaid").val(formula_val_temp);
					// }
					// }
					// else
					// {
					// formula_val_temp = formula_val_temp.concat(column_temp);
					// $("#formulaid").val(formula_val_temp);
					// }

					// // {
					// // formula_temp = $("#formulaid").val();
					// // $("#formulaid").val() =
					// formula_temp.concat($("#formulaid").val());
					// // }

					// });

					// $('#blendoperid').change(function()
					// {
					// var operator_temp = $('#blendoperid').val()
					// var formula_val_temp = $("#formulaid").val();
					// var str_len = formula_val_temp.length;
					// var operators = /([-,*+=<>!&\/])/;
					// if( str_len!=0)
					// {
					// var temp = formula_val_temp.substr(str_len-1,str_len);
					// if( temp.match(operators) || temp=='(')
					// {
					// alert('PLease select column before proceeding');
					// }
					// else
					// {
					// formula_val_temp =
					// formula_val_temp.concat(operator_temp);
					// $("#formulaid").val(formula_val_temp);
					// }
					// }
					// else
					// {
					// alert('PLease select column before selecting an
					// operator');
					// }

					// });

					$('#blendfunid')
							.change(
									function() {
										var column_temp = $('#blendfunid')
												.val()
										var formula_val_temp = $("#formulaid")
												.val();
										var str_len = formula_val_temp.length;
										var letters = /^[A-Za-z]+$/;
										if (str_len != 0) {
											var temp = formula_val_temp.substr(
													str_len - 1, str_len);
											if (temp.match(letters)
													|| temp == ')') {
												alert('PLease select operator before proceeding');
											} else {
												formula_val_temp = formula_val_temp
														.concat(column_temp);
												$("#formulaid").val(
														formula_val_temp);
											}
										} else {
											formula_val_temp = formula_val_temp
													.concat(column_temp);
											$("#formulaid").val(
													formula_val_temp);
										}

										// {
										// formula_temp = $("#formulaid").val();
										// $("#formulaid").val() =
										// formula_temp.concat($("#formulaid").val());
										// }

									});




					/*
					 * function getViewPage(){ $.ajax({ url: './tempview', type:
					 * 'GET', datatype:'json', success: function(data) { if
					 * (data!="") { alert("error in success");
					 * setTimeout(function(){ window.location.reload(); },1000); }
					 * else { toastr.info('There was a problem.'); } }, error:
					 * function(xhr, textStatus, errorThrown) {
					 * console.log(xhr+" "+textStatus+" "+errorThrown); } });
					 */

					$('#blendtypeid')
							.change(
									function() {
										if ($('#blendtypeid').val() == 'Filter') {
											$('#colvaluedivid').hide();
											$('#colValueTypeDivId').hide();
											$('#columnCategory').hide();
											$('#filter_input_id').show();
											$('#derived_input_id').hide();
											$('#filter_opr_id').show();
											$('#common_form_id').show();
											$('#add_row_id').show();
											$('#addColId').addClass('hidden')
													.hide();
											$('#sourcecolumnid').show();
											$('#rowaddid').hide();
										} else if ($('#blendtypeid').val() == 'Derived Column') {
											$('#colValueTypeDivId').show();
											$('#columnCategory').show();
											$('#filter_input_id').hide();
											$('#derived_input_id').show();
											$('#filter_opr_id').show();
											$('#common_form_id').show();
											$('#add_row_id').show();
											$('#sourcecolumnid').show();
											$('#rowaddid').hide();
										} else if ($('#blendtypeid').val() == 'Row Add') {
											$('#rowaddid')
													.removeClass('hidden')
													.show();
											$('#colValueTypeDivId').hide();
											$('#columnCategory').hide();
											$('#filter_input_id').hide();
											$('#derived_input_id').hide();
											$('#filter_opr_id').hide();
											$('#common_form_id').hide();
											$('#add_row_id').hide();
											$('#colvaluedivid').hide();
											$('#addColId').addClass('hidden')
													.hide();
											$('#sourcecolumnid').hide();
										}
									});
					$('#columnCategoryid')
							.change(
									function() {
										if ($('#columnCategoryid').val() == 'Simple') {
											$('#colvaluedivid').hide();
											$('#addColId').addClass('hidden')
													.hide();
										} else if (($('#columnCategoryid')
												.val() == 'Complex')
												|| ($('#columnCategoryid')
														.val() == 'ComplexEval')) {
											$('#colvaluedivid').removeClass(
													'hidden').show();
											$('#addColId')
													.removeClass('hidden')
													.show();
										} else {
											$('#colvaluedivid').hide();
										}
									});
					$('.idcolops').change(
							function() {
								if ($(this).val() != -1) {
									var operator_temp = $(this).find(
											"option:selected").text();
									var formula_val_temp = $("#formulaid")
											.val();

									formula_val_temp = formula_val_temp
											.concat(operator_temp);
									$("#formulaid").val(formula_val_temp);
								}
							});
					$('.blendops').change(
							function() {
    	        				var sIgnoreGlobalEvent = $(document.body).attr('data-ignore-global-event');

								//if ($(this).val() != -1) {
								if (( $(this).val() != -1) && (sIgnoreGlobalEvent !== 'Y')) {
									var operator_temp = $(this).val();
									var formula_val_temp = $("#formulaid")
											.val();

									formula_val_temp = formula_val_temp
											.concat(operator_temp);
									$("#formulaid").val(formula_val_temp);
								}
							});
					$('.secondops').change(
							function() {
								if ($(this).val() != -1) {
									var operator_temp = $(this).val();
									var formula_val_temp = $("#matchexprid")
											.val();

									formula_val_temp = formula_val_temp
											.concat(operator_temp);
									$("#matchexprid").val(formula_val_temp);
								}
							});
					$('#hmethodid').change(function() {
						if ($('#hmethodid').val() == 'Std Deviation') {
							$('#hdurationclassid').hide();
						} else {
							$('#hdurationclassid').show();
						}

					});
					$('#rmethodid').change(function() {
						if ($('#rmethodid').val() == 'Std Deviation') {
							$('#rdurationclassid').hide();
						} else {
							$('#rdurationclassid').show();
						}

					});

					$('#tsrcreateid')
							.click(
									function() {
										var no_error = 1;
										$('.input_error').remove();

										var count = $("#countid").prop(
												"checked") ? 'Y' : 'N';
										var sum = $("#sumid").prop("checked") ? 'Y'
												: 'N';
										var correlation = $("#correlationid")
												.prop("checked") ? 'Y' : 'N';
										var history = $("#hcid")
												.prop("checked") ? 'Y' : 'N';
										var stats = $("#spid").prop("checked") ? 'Y'
												: 'N';
										var duplicateFile = $("#dfid").prop(
												"checked") ? 'Y' : 'N';

										if (no_error) {
											var form_data = {
												appid : $("#appid").val(),
												count : count,
												sum : sum,
												correlation : correlation,
												statisticalparam : stats,
												duplicatefile : duplicateFile,
												times : new Date().getTime()
											};
											if ($("#hcid").prop("checked")) {
												form_data['hcomparison_type'] = "History";
												form_data['hmethod'] = $(
														"#hmethodid").val();
												form_data['hduration'] = $(
														"#hdurationid").val();
												form_data['hthreshold'] = $(
														"#hthresholdid").val();

											}
											if ($("#rtrid").prop("checked")) {
												form_data['rcomparison_type'] = "Run";
												form_data['rmethod'] = $(
														"#rmethodid").val();
												form_data['rduration'] = $(
														"#rdurationid").val();
												form_data['rthreshold'] = $(
														"#rthresholdid").val();
											}

											console.log(form_data);
											$
													.ajax({
														url : $("#tourl").val(),
														type : 'POST',headers: { 'token':$("#token").val()},
														datatype : 'json',
														data : form_data,
														success : function(
																message) {
															var j_obj = $
																	.parseJSON(message);
															if (j_obj
																	.hasOwnProperty('success')) {
																toastr
																		.info('Transaction set rule created successfully');
																setTimeout(
																		function() {
																			window.location.href = j_obj.success;

																		}, 1000);
															} else if (j_obj
																	.hasOwnProperty('fail')) {
																console
																		.log(j_obj.fail);
																toastr
																		.info(j_obj.fail);
															}
														},
														error : function(xhr,
																textStatus,
																errorThrown) {
															$('#initial')
																	.hide();
															$('#fail').show();
														}
													});
										}
										return false;
									});

					$('#trcreateid')
							.click(
									function() {
										var no_error = 1;
										$('.input_error').remove();

										var duprow = $("#duprowid").prop(
												"checked") ? 'Y' : 'N';
										var seqrow = $("#seqmid").prop(
												"checked") ? 'Y' : 'N';

										if (no_error) {
											var form_data = {
												appid : $("#appid").val(),
												duprow : duprow,
												seqrow : seqrow,
												colid : $("#colid").val(),
												times : new Date().getTime()
											};

											console.log(form_data);
											$
													.ajax({
														url : $("#tourl").val(),
														type : 'POST',headers: { 'token':$("#token").val()},
														datatype : 'json',
														data : form_data,
														success : function(
																message) {
															var j_obj = $
																	.parseJSON(message);
															if (j_obj
																	.hasOwnProperty('success')) {
																toastr
																		.info('Transaction rule created successfully');
																setTimeout(
																		function() {
																			window.location.href = j_obj.success;

																		}, 1000);
															} else if (j_obj
																	.hasOwnProperty('fail')) {
																console
																		.log(j_obj.fail);
																toastr
																		.info(j_obj.fail);
															}
														},
														error : function(xhr,
																textStatus,
																errorThrown) {
															$('#initial')
																	.hide();
															$('#fail').show();
														}
													});
										}
										return false;
									});

					// list rule popup window
					$("#opid").change(
							function() {
								var formula_val_temp = $("#listruleid").val();
								var str_len = formula_val_temp.length;
								if (str_len != 0) {
									formula_val_temp = formula_val_temp.concat(
											' ', $("#opid").val());
								} else {
									formula_val_temp = formula_val_temp
											.concat($("#opid").val());
								}

								$("#listruleid").val(formula_val_temp);
							});
					$(".leftitem").change(
							function() {
								var formula_val_temp = $("#listruleid").val();
								var str_len = formula_val_temp.length;
								if (str_len != 0) {
									formula_val_temp = formula_val_temp.concat(
											' ', $(".leftitem").val());
								} else {
									formula_val_temp = formula_val_temp
											.concat($(".leftitem").val());
								}

								$("#listruleid").val(formula_val_temp);

							});
					$("#refitemid").change(
							function() {
								var formula_val_temp = $("#refruleid").val();
								var str_len = formula_val_temp.length;
								if (str_len != 0) {
									formula_val_temp = formula_val_temp.concat(
											' ', $("#refitemid").val());
								} else {
									formula_val_temp = formula_val_temp
											.concat($("#refitemid").val());
								}

								$("#refruleid").val(formula_val_temp);

							});

					$(".functionslist")
							.change(
									function() {

										if ($(this).val() != -1) {
											var formula_val_temp = ' ';
											var formid = $(this)
													.closest('.row').closest(
															'.col-wrapper')
													.find('span').html();
											if (formid == 'listrule') {
												formula_val_temp = $(
														"#listruleid").val();
											} else if (formid == 'refrule') {
												formula_val_temp = $(
														"#refruleid").val();
											} else if (formid == 'leftsiderule') {
												formula_val_temp = $(
														"#matchkeyfid").val();
											} else if (formid == 'customA') {
												formula_val_temp = $(".rulebox")
														.val();
											}
											// if( formula_val_temp.length !=0 )
											// {
											// formula_val_temp =
											// formula_val_temp.concat(' ',
											// $(this).val(),'(');
											// }
											// else
											// {
											// formula_val_temp =
											// formula_val_temp.concat($(this).val(),'(');
											// }
											formula_val_temp = formula_val_temp
													.concat($(this).val(), '(');
											if (formid == 'listrule') {
												$("#listruleid").val(
														formula_val_temp);
											}
											if (formid == 'refrule') {
												$("#refruleid").val(
														formula_val_temp);
											}
											if (formid == 'leftsiderule') {
												$("#matchkeyfid").val(
														formula_val_temp);
											}
											if (formid == 'customA') {
												$(".rulebox").val(
														formula_val_temp);
											}
										}

									});

					$('#notesmodal').on('show.bs.modal', function(e) {
						var columnid = e.relatedTarget.dataset.columnid;
						var temp1 = e.relatedTarget.dataset.appidd;

						$("#columnid").val(columnid);
						$("#appid").val(temp1);

						if (globalcolid == columnid) {
							$("#statid").val('Y');
						} else {
							$("#statid").val('N');
						}
						$(".leftitem").empty();

					});

					$('#refmodal').on('show.bs.modal', function(e) {
						var columnid = e.relatedTarget.dataset.columnid;
						var temp1 = e.relatedTarget.dataset.appidd;

						$("#columnid").val(columnid);
						$("#appid").val(temp1);

						if (globalcolid == columnid) {
							$("#statid").val('Y');
						} else {
							$("#statid").val('N');
						}
						$(".leftitem1").empty();
					});

					$('#formatmodal').on('show.bs.modal', function(e) {
						var columnid = e.relatedTarget.dataset.columnid;
						var temp1 = e.relatedTarget.dataset.appidd;

						$("#columnid").val(columnid);
						$("#appid").val(temp1);

						if (globalcolid == columnid) {
							$("#statid").val('Y');
						} else {
							$("#statid").val('N');
						}
						// alert($("#statid").val());
					});

					$('#messagemodal').on('show.bs.modal', function(e) {
						var msgsourceid = e.relatedTarget.dataset.msgsourceid;
						var msgid = e.relatedTarget.dataset.msgid;
						var msg = e.relatedTarget.dataset.msg;

						$("#msgsourceid").val(msgsourceid);
						$("#msgid").val(msgid);
						$("#messageid").val(msg);

						if ($("#messageid").val().length != 0) {
							$("#messageid").prop("readonly", true);
						} else {
							$("#messageid").prop("readonly", false);
						}

					});

					$(".stats").click(function() {
						var selectedId = '#' + ($(this).attr('id'));
						if ($(selectedId).is(":checked")) {
							globalcolid = $(this).attr('id');
						} else {
							globalcolid = ' ';
						}
						// alert(globalcolid);
					});

					$("#listrulecreateid")
							.click(
									function() {
										var no_error = 1;
										$('.input_error').remove();

										if ($("#listruleid").val().length == 0) {
											$(
													'<span class="input_error" style="font-size:12px;color:red">Please enter Rule to proceed</span>')
													.insertAfter(
															$('#listruleid'));
											no_error = 0;
										}

										if (no_error) {

											var str = $("#listruleid").val();
											var strlength = $("#listruleid")
													.val().length;
											var pos = str.search($("#opid")
													.val());
											var operlength = $("#opid").val().length;
											var finallen = pos + operlength;
											var rightexp = str.substr(finallen,
													strlength);
											var leftexp = str
													.substr(0, pos - 1);

											var form_data = {
												appid : $("#appid").val(),
												colid : $("#columnid").val(),
												stat : $("#statid").val(),
												listrule : 'Y',
												operator : $("#opid").val(),
												leftexp : leftexp,
												rightexp : rightexp,
												times : new Date().getTime()
											};
											// console.log(form_data);
											$
													.ajax({
														url : $("#tourl").val(),
														type : 'POST',headers: { 'token':$("#token").val()},
														datatype : 'json',
														data : form_data,
														success : function(
																message) {
															var j_obj = $
																	.parseJSON(message);
															if (j_obj
																	.hasOwnProperty('success')) {
																toastr
																		.info('List rule created successfully');
																setTimeout(
																		function() {
																			window.location.href = j_obj.success;

																		}, 1000);
															} else if (j_obj
																	.hasOwnProperty('fail')) {
																console
																		.log(j_obj.fail);
																toastr
																		.info(j_obj.fail);
															}
														},
														error : function(xhr,
																textStatus,
																errorThrown) {
															$('#initial')
																	.hide();
															$('#fail').show();
														}
													});
										}
										return false;

									});
					$("#refopid").change(
							function() {
								var formula_val_temp = $("#refruleid").val();
								var str_len = formula_val_temp.length;
								if (str_len != 0) {
									formula_val_temp = formula_val_temp.concat(
											' ', $("#refopid").val());
								} else {
									formula_val_temp = formula_val_temp
											.concat($("#refopid").val());
								}
								$("#refruleid").val(formula_val_temp);
							});
					$("#refsid").change(
							function() {
								var formula_val_temp = $("#refruleid").val();
								var str_len = formula_val_temp.length;
								if (str_len != 0) {
									formula_val_temp = formula_val_temp.concat(
											' ', $("#refsid").val());
								} else {
									formula_val_temp = formula_val_temp
											.concat($("#refsid").val());
								}
								$("#refruleid").val(formula_val_temp);
							});
					//
					$("#refsaid").change(
							function() {
								var formula_val_temp = $("#refruleid").val();
								var str_len = formula_val_temp.length;
								if (str_len != 0) {
									formula_val_temp = formula_val_temp.concat(
											' ', $("#refsaid").val());
								} else {
									formula_val_temp = formula_val_temp
											.concat($("#refsaid").val());
								}
								$("#refruleid").val(formula_val_temp);
							});

					$("#refrulecreateid")
							.click(
									function() {
										var no_error = 1;
										$('.input_error').remove();

										if ($("#refruleid").val().length == 0) {
											$(
													'<span class="input_error" style="font-size:12px;color:red">Please enter Rule to proceed</span>')
													.insertAfter(
															$('#listruleid'));
											no_error = 0;
										}

										if (no_error) {

											var str = $("#refruleid").val();
											var strlength = $("#refruleid")
													.val().length;
											var pos = str.search($("#refopid")
													.val());
											var operlength = $("#refopid")
													.val().length;
											var finallen = pos + operlength;
											var rightexp = str.substr(finallen,
													strlength);
											var leftexp = str
													.substr(0, pos - 1);

											var form_data = {
												appid : $("#appid").val(),
												colid : $("#columnid").val(),
												stat : $("#statid").val(),
												refrule : 'Y',
												operator : $("#refopid").val(),
												leftexp : leftexp,
												rightexp : rightexp,
												times : new Date().getTime()
											};

											$
													.ajax({
														url : $("#tourl").val(),
														type : 'POST',headers: { 'token':$("#token").val()},
														datatype : 'json',
														data : form_data,
														success : function(
																message) {
															var j_obj = $
																	.parseJSON(message);
															if (j_obj
																	.hasOwnProperty('success')) {
																toastr
																		.info('Referential rule created successfully');
																setTimeout(
																		function() {
																			window.location.href = j_obj.success;

																		}, 1000);
															} else if (j_obj
																	.hasOwnProperty('fail')) {
																console
																		.log(j_obj.fail);
																toastr
																		.info(j_obj.fail);
															}
														},
														error : function(xhr,
																textStatus,
																errorThrown) {
															$('#initial')
																	.hide();
															$('#fail').show();
														}
													});
										}
										return false;
									});

					$("#formatrulecreateid")
							.click(
									function() {
										var no_error = 1;
										$('.input_error').remove();

										if ($("#formatruleid").val().length == 0) {
											$(
													'<span class="input_error" style="font-size:12px;color:red">Please enter Rule to proceed</span>')
													.insertAfter(
															$('#formatruleid'));
											no_error = 0;
										}

										if (no_error) {
											var form_data = {
												appid : $("#appid").val(),
												colid : $("#columnid").val(),
												stat : $("#statid").val(),
												formatrule : 'Y',
												leftexp : $("#formatruleid")
														.val(),
												times : new Date().getTime()
											};
											// console.log(form_data);

											$
													.ajax({
														url : $("#tourl").val(),
														type : 'POST',headers: { 'token':$("#token").val()},
														datatype : 'json',
														data : form_data,
														success : function(
																message) {
															var j_obj = $
																	.parseJSON(message);
															if (j_obj
																	.hasOwnProperty('success')) {
																toastr
																		.info('Format rule created successfully');
																setTimeout(
																		function() {
																			window.location.href = j_obj.success;

																		}, 1000);
															} else if (j_obj
																	.hasOwnProperty('fail')) {
																console
																		.log(j_obj.fail);
																toastr
																		.info(j_obj.fail);
															}
														},
														error : function(xhr,
																textStatus,
																errorThrown) {
															$('#initial')
																	.hide();
															$('#fail').show();
														}
													});
										}
										return false;
									});

					$(".delcolspec")
							.click(
									function() {
										var temp = $(this).closest('p').find(
												'span').html();
										var appid = $("#appid").val();
										var url = temp.concat('&ref=', appid);
										$
												.ajax({
													url : url,
													type : 'GET',
													datatype : 'json',
													success : function(message) {
														var j_obj = $
																.parseJSON(message);
														if (j_obj
																.hasOwnProperty('success')) {
															toastr
																	.info('Column Spec rule deleted successfully');
															setTimeout(
																	function() {
																		window.location.href = j_obj.success;

																	}, 1000);
														} else if (j_obj
																.hasOwnProperty('fail')) {
															console
																	.log(j_obj.fail);
															toastr
																	.info(j_obj.fail);
														}
													},
													error : function(xhr,
															textStatus,
															errorThrown) {
														$('#initial').hide();
														$('#fail').show();
													}
												});
										return false;
									});

					// $(".delcol").click(function(){
					// var temp = $(this).closest('td').find('span').html();
					// var url = $("#tourl").val();
					// url = url.concat('columnrules/delete');
					// var form_data = {
					// appid : $("#appid").val(),
					// colid : temp,
					// times: new Date().getTime()
					// };
					// $.ajax({
					// url: url,
					// type: 'POST',
					// datatype:'json',
					// data: form_data,
					// success: function(message){
					// var j_obj = $.parseJSON(message);
					// if(j_obj.hasOwnProperty('success'))
					// {
					// toastr.info('Column Spec rule deleted successfully');
					// setTimeout(function(){
					// window.location.href= j_obj.success;

					// },1000);
					// }else if(j_obj.hasOwnProperty('fail') )
					// {
					// console.log(j_obj.fail);
					// toastr.info(j_obj.fail);
					// }
					// },
					// error: function(xhr, textStatus, errorThrown){
					// $('#initial').hide();
					// $('#fail').show();
					// }
					// });
					// return false;
					// });

					// $(".deltransrule").click(function(){
					// var temp = $(this).closest('td').find('span').html();
					// var url = $("#tourl").val();
					// url = url.concat('rule/delete');
					// var form_data = {
					// appid : $("#appid").val(),
					// transruleid : temp,
					// times: new Date().getTime()
					// };
					// // console.log(form_data); console.log(url);return false;
					// $.ajax({
					// url: url,
					// type: 'POST',
					// datatype:'json',
					// data: form_data,
					// success: function(message){
					// var j_obj = $.parseJSON(message);
					// if(j_obj.hasOwnProperty('success'))
					// {
					// toastr.info('Transaction Rule deleted successfully');
					// setTimeout(function(){
					// window.location.href= j_obj.success;

					// },1000);
					// }else if(j_obj.hasOwnProperty('fail') )
					// {
					// console.log(j_obj.fail);
					// toastr.info(j_obj.fail);
					// }
					// },
					// error: function(xhr, textStatus, errorThrown){
					// $('#initial').hide();
					// $('#fail').show();
					// }
					// });
					// return false;
					// });

					$(".setcompruledel")
							.click(
									function() {
										var temp = $(this).closest('p').find(
												'span').html();
										var url = $("#tourl").val();
										url = url.concat('setrule/deletecomp');
										var form_data = {
											appid : $("#appid").val(),
											setcompruleid : temp,
											times : new Date().getTime()
										};
										// console.log(form_data);
										// console.log(url);return false;
										$
												.ajax({
													url : url,
													type : 'POST',headers: { 'token':$("#token").val()},
													datatype : 'json',
													data : form_data,
													success : function(message) {
														var j_obj = $
																.parseJSON(message);
														if (j_obj
																.hasOwnProperty('success')) {
															toastr
																	.info('Transaction Set Rule deleted successfully');
															setTimeout(
																	function() {
																		window.location.href = j_obj.success;

																	}, 1000);
														} else if (j_obj
																.hasOwnProperty('fail')) {
															console
																	.log(j_obj.fail);
															toastr
																	.info(j_obj.fail);
														}
													},
													error : function(xhr,
															textStatus,
															errorThrown) {
														$('#initial').hide();
														$('#fail').show();
													}
												});
										return false;
									});
					$(".setruledel")
							.click(
									function() {
										var temp = $(this).closest('td').find(
												'span').html();
										var url = $("#tourl").val();
										url = url.concat('setrule/delete');
										var form_data = {
											appid : $("#appid").val(),
											setruleid : temp,
											times : new Date().getTime()
										};
										// console.log(form_data);
										// console.log(url);return false;
										$
												.ajax({
													url : url,
													type : 'POST',headers: { 'token':$("#token").val()},
													datatype : 'json',
													data : form_data,
													success : function(message) {
														var j_obj = $
																.parseJSON(message);
														if (j_obj
																.hasOwnProperty('success')) {
															toastr
																	.info('Transaction Set Rule deleted successfully');
															setTimeout(
																	function() {
																		window.location.href = j_obj.success;

																	}, 1000);
														} else if (j_obj
																.hasOwnProperty('fail')) {
															console
																	.log(j_obj.fail);
															toastr
																	.info(j_obj.fail);
														}
													},
													error : function(xhr,
															textStatus,
															errorThrown) {
														$('#initial').hide();
														$('#fail').show();
													}
												});
										return false;
									});

					$(".appdelid")
							.click(
									function() {
										// var url = $("#tourl").val();
										// url =
										// url.concat('dataapp/deleteform');
										var form_data = {
											idApp : $("#appid").val(),
										// times: new Date().getTime()
										};
										// console.log(form_data);
										// console.log(url);return false;
										$
												.ajax({
													url : './deleteValidationCheckajax',
													type : 'POST',headers: { 'token':$("#token").val()},
													datatype : 'json',
													data : form_data,
													success : function(data) {
														if (data != "") {
															toastr.info(data);
															setTimeout(
																	function() {
																		window.location.href = 'validationCheck_View';
																	}, 1000);
														} else {
															toastr
																	.info('There was a problem.');
														}
													},
													error : function(xhr,
															textStatus,
															errorThrown) {
														$('#initial').hide();
														$('#fail').show();
													}
												});
										return false;
									});

					/*
					 * $("#apptypeid").change(function() { console.log('ok'); if
					 * ($("#apptypeid").val() == "Schema Matching") {
					 * $(".non-schema-matching").hide();
					 * $("#appnameid").val('Schema Matching App'); //set to a
					 * placeholder value $(".schema-matching").show(); } else {
					 * $(".non-schema-matching").show();
					 * $("#appnameid").val(''); //clear placeholder value
					 * $(".schema-matching").hide(); } });
					 */

					// create matching tyoe
					/*
					 * $("#matchtypecreateid").click(function(){ var no_error =
					 * 1; $('.input_error').remove();
					 *
					 * if($("#appid").val().length == 0){ $('<span
					 * class="input_error"
					 * style="font-size:12px;color:red">Please enter Validation
					 * Check ID to Continue</span>').insertAfter($('#matchtypecreateid'));
					 * no_error = 0; } if($("#mcardinalityid").val() == -1){ $('<span
					 * class="input_error"
					 * style="font-size:12px;color:red">Please Choose a Match
					 * Type</span>').insertAfter($('#mcardinalityid'));
					 * no_error = 0; } if($("#matchtypeid").val() == -1){ $('<span
					 * class="input_error"
					 * style="font-size:12px;color:red">Please Choose a Match
					 * Type</span>').insertAfter($('#matchtypeid')); no_error =
					 * 0; } if(no_error){ var form_data = { appid :
					 * $("#appid").val(), matchtype :
					 * $("#mcardinalityid").val(), matchtype2 :
					 * $("#matchtypeid").val(), times: new Date().getTime() }; //
					 * console.log(form_data);
					 *
					 * $.ajax({ url: $("#tourl").val(), type: 'POST',
					 * datatype:'json', data: form_data, success:
					 * function(message){ var j_obj = $.parseJSON(message);
					 * if(j_obj.hasOwnProperty('success')) { toastr.info('Match
					 * Type created successfully'); setTimeout(function(){
					 * window.location.href= j_obj.success;
					 *
					 * },1000); }else if(j_obj.hasOwnProperty('fail') ) {
					 * console.log(j_obj.fail); toastr.info(j_obj.fail); } },
					 * error: function(xhr, textStatus, errorThrown){
					 * $('#initial').hide(); $('#fail').show(); } }); } return
					 * false; });
					 */

					// To dynamcally load the columns of the source table
					// selected
					$("#leftsourceid")
							.change(
									function() {
										var no_error = 1;
										$('.input_error').remove();

										if ($("#leftsourceid").val() == '-1') {
											// $('<span class="input_error"
											// style="font-size:12px;color:red">Please
											// select a source to
											// Continue</span>').insertAfter($('#rightsourceid'));
											no_error = 0;
										}
										if (no_error) {
											var form_data = {
												id : $("#leftsourceid").val(),
												times : new Date().getTime()
											};
											// console.log(form_data);

											$
													.ajax({
														url : $("#tourl1")
																.val(),
														type : 'POST',headers: { 'token':$("#token").val()},
														datatype : 'json',
														data : form_data,
														success : function(
																message) {
															var j_obj = $
																	.parseJSON(message);
															if (j_obj
																	.hasOwnProperty('success')) {

																var temp = {};
																column_array = j_obj.success;
																// empty the
																// list from
																// previous
																// selection
																$("#leftitemid")
																		.empty();
																$
																		.each(
																				column_array,
																				function(
																						k1,
																						v1) {
																					var column_details = column_array[k1];
																					temp_name = column_details['displayName'];

																					$(
																							"<option />")
																							.attr(
																									"value",
																									column_details['displayName'])
																							.html(
																									temp_name)
																							.appendTo(
																									"#leftitemid");
																					temp[this.value] = this.subitems;
																				});
															} else if (j_obj
																	.hasOwnProperty('fail')) {
																console
																		.log(j_obj.fail);
																toastr
																		.info(j_obj.fail);
															}
														},
														error : function(xhr,
																textStatus,
																errorThrown) {
															$('#initial')
																	.hide();
															$('#fail').show();
														}
													});
										}
									});

					$(".leftsource")
							.change(
									function() {
										var no_error = 1;
										$('.input_error').remove();

										if ($(".leftsource").val() == '-1') {
											$(
													'<span class="input_error" style="font-size:12px;color:red">Please select a source to Continue</span>')
													.insertAfter(
															$('#rightsourceid'));
											no_error = 0;
										}
										if (no_error) {
											var form_data = {
												id : $(".leftsource").val(),
												times : new Date().getTime()
											};
											console.log(form_data);

											$
													.ajax({
														url : $("#tomsg").val(),
														type : 'POST',headers: { 'token':$("#token").val()},
														datatype : 'json',
														data : form_data,
														success : function(
																message) {
															var j_obj = $
																	.parseJSON(message);
															if (j_obj
																	.hasOwnProperty('success')) {

																var temp = {};
																column_array = j_obj.success;
																var value1;
																// empty the
																// list from
																// previous
																// selection
																$(".leftitem")
																		.empty();
																$
																		.each(
																				column_array,
																				function(
																						k1,
																						v1) {
																					var column_details = column_array[k1];
																					value1 = column_array[0]['sourcename'];
																					temp_name = column_details['displayName'];
																					value1 = value1
																							.concat(
																									'.',
																									column_details['displayName']);

																					$(
																							"<option />")
																							.attr(
																									"value",
																									value1)
																							.html(
																									temp_name)
																							.appendTo(
																									".leftitem");
																					temp[this.value] = this.subitems;
																				});
															} else if (j_obj
																	.hasOwnProperty('fail')) {
																console
																		.log(j_obj.fail);
																toastr
																		.info(j_obj.fail);
															}
														},
														error : function(xhr,
																textStatus,
																errorThrown) {
															$('#initial')
																	.hide();
															$('#fail').show();
														}
													});
										}
									});

					$(".leftsource1")
							.change(
									function() {
										var no_error = 1;
										$('.input_error').remove();

										if ($(".leftsource1").val() == '-1') {
											$(
													'<span class="input_error" style="font-size:12px;color:red">Please select a source to Continue</span>')
													.insertAfter(
															$('#rightsourceid'));
											no_error = 0;
										}
										if (no_error) {
											var form_data = {
												id : $(".leftsource1").val(),
												times : new Date().getTime()
											};

											$
													.ajax({
														url : $("#tomsg").val(),
														type : 'POST',headers: { 'token':$("#token").val()},
														datatype : 'json',
														data : form_data,
														success : function(
																message) {
															var j_obj = $
																	.parseJSON(message);
															if (j_obj
																	.hasOwnProperty('success')) {

																var temp = {};
																var value1 = '';
																column_array = j_obj.success;
																// empty the
																// list from
																// previous
																// selection
																$(".leftitem1")
																		.empty();
																$
																		.each(
																				column_array,
																				function(
																						k1,
																						v1) {
																					var column_details = column_array[k1];
																					value1 = column_array[0]['sourcename'];
																					temp_name = column_details['displayName'];
																					value1 = value1
																							.concat(
																									'.',
																									column_details['displayName']);
																					$(
																							"<option />")
																							.attr(
																									"value",
																									value1)
																							.html(
																									temp_name)
																							.appendTo(
																									".leftitem1");
																					temp[this.value] = this.subitems;
																				});
															} else if (j_obj
																	.hasOwnProperty('fail')) {
																console
																		.log(j_obj.fail);
																toastr
																		.info(j_obj.fail);
															}
														},
														error : function(xhr,
																textStatus,
																errorThrown) {
															$('#initial')
																	.hide();
															$('#fail').show();
														}
													});
										}
									});

					$("#rightsourceid")
							.change(
									function() {

										var no_error = 1;
										$('.input_error').remove();

										if ($("#rightsourceid").val() == '-1') {
											// $('<span class="input_error"
											// style="font-size:12px;color:red">Please
											// select a source to
											// Continue</span>').insertAfter($('#rightsourceid'));
											no_error = 0;
										}

										if (no_error) {
											var rightSourceId = $(
													"#rightsourceid :selected")
													.text();

											console.log(rightSourceId);
											var form_data = {
												idData : $("#rightsourceid")
														.val(),
											// times: new Date().getTime()
											};

											$
													.ajax({
														url : './changeDataColumnAjax',
														type : 'POST',headers: { 'token':$("#token").val()},
														datatype : 'json',
														data : form_data,
														success : function(
																message) {
															var j_obj = $
																	.parseJSON(message);
															if (j_obj
																	.hasOwnProperty('success')) {
																var temp = {};
																column_array = JSON
																		.parse(j_obj.success);
																// empty the
																// list from
																// previous
																// selection
																$(
																		"#rightitemid")
																		.empty();
																$("<option />")
																		.attr(
																				"value",
																				'-1')
																		.html(
																				'Select Column')
																		.appendTo(
																				"#rightitemid");
																temp[this.value] = this.subitems;

																$
																		.each(
																				column_array,
																				function(
																						k1,
																						v1) {
																					console
																							.log(v1);
																					console
																							.log(k1);
																					var column_details = column_array[k1];
																					console
																							.log(column_details);
																					var sourcename = rightSourceId;
																					temp_name = column_details;
																					// sourcename
																					// =
																					// sourcename.concat('.',
																					// temp_name);
																					sourcename = temp_name;
																					$(
																							"<option />")
																							.attr(
																									"value",
																									sourcename)
																							.html(
																									temp_name)
																							.appendTo(
																									"#rightitemid");
																					temp[this.value] = this.subitems;
																				});
															} else if (j_obj
																	.hasOwnProperty('fail')) {
																console
																		.log(j_obj.fail);
																toastr
																		.info(j_obj.fail);
															}
														},
														error : function(xhr,
																textStatus,
																errorThrown) {
															$('#initial')
																	.hide();
															$('#fail').show();
														}
													});
										}
									});
					/*$("#leftitemid").change(
							function() {
								if ($(this).val() != -1) {
									var formula_val_temp = $("#matchkeyfid").val();
									var str_len = 0;

									if (formula_val_temp) {
										str_len = formula_val_temp.length;
										formula_val_temp = formula_val_temp.concat($("#leftitemid").val());
										$("#matchkeyfid").val(formula_val_temp);
									}
								}
							});*/

					$("#leftitemid1").change(
							function() {
								var formula_val_temp = $("#refruleid").val();
								var str_len = formula_val_temp.length;
								formula_val_temp = formula_val_temp.concat($(
										"#leftitemid1").val());
								$("#refruleid").val(formula_val_temp);
							});

					/* $("#rightitemid").change(
							function() {
								console.log("First rightItemid change event: ");
								if ($(this).val() != -1) {
									var formula_val_temp = $("#matchkeyfid")
											.val();
									var str_len = formula_val_temp.length;
									formula_val_temp = formula_val_temp
											.concat($("#rightitemid").val());
									$("#matchkeyfid").val(formula_val_temp);
								}

							});
							
							*/
							
					// $("#rightfunid").change(function()
					// {
					// var formula_val_temp = $("#matchkeyfid").val();
					// var str_len = formula_val_temp.length;
					// if( str_len!=0)
					// {
					// formula_val_temp = formula_val_temp.concat(' and
					// ',$("#rightfunid").val(),'(');
					// }
					// else
					// {
					// alert('Please choose left expression first to proceed');
					// }

					// $("#matchkeyfid").val(formula_val_temp);
					// });
					$('.deleteglobalruleclass')
							.on(
									'confirmed.bs.confirmation',
									function() {

										var matchid = $(this).closest('td')
												.find('span').html();
										/*
										 * var url; if(
										 * $(this).hasClass('sub-class') ) { url =
										 * $(this).closest('.portlet-body').find('.tourl2').val(); }
										 * else { url =
										 * $(this).closest('.portlet-body').find('.tourl1').val(); }
										 */

										var form_data = {
											idListColrules : matchid,
										// appid : $("#appid").val(),
										// times: new Date().getTime()
										};
										console.log(form_data); /*
																 * console.log(url);//return
																 * false;
																 */
										$
												.ajax({
													url : "deleteGlobalRulesData",
													type : 'POST',headers: { 'token':$("#token").val()},
													datatype : 'json',
													data : form_data,
													success : function(data) {
														if (data != "") {
															toastr
																	.info('Global Rule Deleted Successfully');
															setTimeout(
																	function() {
																		window.location
																				.reload();
																	}, 1000);
														} else {
															toastr
																	.info('There was a problem.');
														}
													},
													error : function(xhr,
															textStatus,
															errorThrown) {
														$('.validation-fail')
																.removeClass(
																		'hidden')
																.html(
																		"There seems to be a network problem. Please try again in some time.");
													}
												});

										return false;
									});

					$('.deletelistcolruleclass')
							.on(
									'confirmed.bs.confirmation',
									function() {

										var matchid = $(this).closest('td')
												.find('span').html();
										/*
										 * var url; if(
										 * $(this).hasClass('sub-class') ) { url =
										 * $(this).closest('.portlet-body').find('.tourl2').val(); }
										 * else { url =
										 * $(this).closest('.portlet-body').find('.tourl1').val(); }
										 */

										var form_data = {
											idListColrules : matchid,
										// appid : $("#appid").val(),
										// times: new Date().getTime()
										};
										console.log(form_data); /*
																 * console.log(url);//return
																 * false;
																 */
										$
												.ajax({
													url : "deleteIdListColRulesData",
													type : 'POST',headers: { 'token':$("#token").val()},
													datatype : 'json',
													data : form_data,
													success : function(data) {
														if (data != "") {
															toastr
																	.info('Extend Template Rule Deleted Successfully');
															setTimeout(
																	function() {
																		window.location
																				.reload();
																	}, 1000);
														} else {
															toastr
																	.info('There was a problem.');
														}
													},
													error : function(xhr,
															textStatus,
															errorThrown) {
														$('.validation-fail')
																.removeClass(
																		'hidden')
																.html(
																		"There seems to be a network problem. Please try again in some time.");
													}
												});

										return false;
									});

					$('.deleteglobalruleclass')
							.on(
									'confirmed.bs.confirmation',
									function() {

										var matchid = $(this).closest('td')
												.find('span').html();
										var form_data = {
											globalRuleId : matchid,
										// appid : $("#appid").val(),
										// times: new Date().getTime()
										};
										console.log(form_data); /*
																 * console.log(url);//return
																 * false;
																 */
										$
												.ajax({
													url : "deleteGlobalRulesData",
													type : 'POST',headers: { 'token':$("#token").val()},
													datatype : 'json',
													data : form_data,
													success : function(data) {
														if (data != "") {
															toastr
																	.info('Global Rule Deleted Successfully');
															setTimeout(
																	function() {
																		window.location
																				.reload();
																	}, 1000);
														} else {
															toastr
																	.info('There was a problem.');
														}
													},
													error : function(xhr,
															textStatus,
															errorThrown) {
														$('.validation-fail')
																.removeClass(
																		'hidden')
																.html(
																		"There seems to be a network problem. Please try again in some time.");
													}
												});

										return false;
									});

					// $('.delmatchtypeId').click(function()
					$('.bs_confirmation_demo_1')
							.on(
									'confirmed.bs.confirmation',
									function() {

										var matchid = $(this).closest('td')
												.find('span').html();
										var url;
										if ($(this).hasClass('sub-class')) {
											url = $(this).closest(
													'.portlet-body').find(
													'.tourl2').val();
										} else {
											url = $(this).closest(
													'.portlet-body').find(
													'.tourl1').val();
										}

										var form_data = {
											idDataBlend : matchid,
										// appid : $("#appid").val(),
										// times: new Date().getTime()
										};
										console.log(form_data);
										console.log(url);// return false;
										$
												.ajax({
													url : url,
													type : 'POST',headers: { 'token':$("#token").val()},
													datatype : 'json',
													data : form_data,
													success : function(data) {
														if (data != "") {
															toastr
																	.info('Deleted Successfully');
															setTimeout(
																	function() {
																		window.location
																				.reload();
																	}, 1000);
														} else {
															toastr
																	.info('There was a problem.');
														}
													},
													error : function(xhr,
															textStatus,
															errorThrown) {
														$('.validation-fail')
																.removeClass(
																		'hidden')
																.html(
																		"There seems to be a network problem. Please try again in some time.");
													}
												});

										return false;
									});

					// DATA ANOMALY FUNCTIONS

					$("#sanomalycreateid")
							.click(
									function() {
										var no_error = 1;
										$('.input_error').remove();

										// if($("#matchkeyfid").val().length ==
										// 0){
										// $('<span class="input_error"
										// style="font-size:12px;color:red">Please
										// enter Matching key Expression to
										// Continue</span>').insertAfter($('#matchkeyfid'));
										// no_error = 0;
										// }
										var ben = $("#benfordid").prop(
												"checked") ? 'Y' : 'N';
										var out = $("#outlierid").prop(
												"checked") ? 'Y' : 'N';

										if (no_error) {
											var form_data = {
												appid : $("#appid").val(),
												benford : ben,
												benfordcol : $("#benfordcol")
														.val(),
												outlier : out,
												outliercol : $("#outliercolid")
														.val(),
												omethod : $("#omethodid").val(),
												othreshold : $("#othresholdid")
														.val(),
												avgrange : $("#avgrangeid")
														.val(),
												times : new Date().getTime()
											};
											// console.log(form_data); return
											// false;
											$
													.ajax({
														url : $("#tourl").val(),
														type : 'POST',headers: { 'token':$("#token").val()},
														datatype : 'json',
														data : form_data,
														success : function(
																message) {
															var j_obj = $
																	.parseJSON(message);
															if (j_obj
																	.hasOwnProperty('success')) {
																toastr
																		.info('Standard Anomaly created successfully');
																// setTimeout(function(){
																// window.location.href=
																// j_obj.success;

																// },1000);
															} else if (j_obj
																	.hasOwnProperty('fail')) {
																console
																		.log(j_obj.fail);
																toastr
																		.info(j_obj.fail);
															}
														},
														error : function(xhr,
																textStatus,
																errorThrown) {
															$('#initial')
																	.hide();
															$('#fail').show();
														}
													});
										}
										return false;
									});

					$("#canomalycreateid")
							.click(
									function() {
										var no_error = 1;
										$('.input_error').remove();

										if ($(".rulebox").val().length == 0) {
											$(
													'<span class="input_error" style="font-size:12px;color:red">Please enter Rule to proceed</span>')
													.insertAfter(
															$('#listruleid'));
											no_error = 0;
										}

										if (no_error) {

											var str = $(".rulebox").val();
											var strlength = $(".rulebox").val().length;
											var pos = str.search($(
													"#canomalyopid").val());
											var operlength = $("#canomalyopid")
													.val().length;
											var finallen = pos + operlength;
											var rightexp = str.substr(finallen,
													strlength);
											var leftexp = str
													.substr(0, pos - 1);

											var form_data = {
												appid : $("#appid").val(),
												custom : 'Y',
												operator : $("#canomalyopid")
														.val(),
												leftexp : leftexp,
												rightexp : rightexp,
												times : new Date().getTime()
											};
											// console.log(form_data); return
											// false;
											$
													.ajax({
														url : $("#tourl").val(),
														type : 'POST',headers: { 'token':$("#token").val()},
														datatype : 'json',
														data : form_data,
														success : function(
																message) {
															var j_obj = $
																	.parseJSON(message);
															if (j_obj
																	.hasOwnProperty('success')) {
																toastr
																		.info('Custom rule created successfully');
																// setTimeout(function(){
																// window.location.href=
																// j_obj.success;

																// },1000);
															} else if (j_obj
																	.hasOwnProperty('fail')) {
																console
																		.log(j_obj.fail);
																toastr
																		.info(j_obj.fail);
															}
														},
														error : function(xhr,
																textStatus,
																errorThrown) {
															$('#initial')
																	.hide();
															$('#fail').show();
														}
													});
										}
										return false;
									});

					// $('#dataSourceid').change(function() {
					// if ($('#dataSourceid').is(":checked")) {
					// $('#dataSourceHideid').removeClass('hidden');
					// }
					// else
					// {
					// $('#dataSourceHideid').addClass('hidden');
					// }
					// });
					$(".custom-control").change(
							function() {
								var formula_val_temp = $(".rulebox").val();
								var str_len = formula_val_temp.length;
								// if( str_len!=0)
								// {
								// formula_val_temp = formula_val_temp.concat('
								// ',$(this).val());
								// }
								// else
								// {
								// formula_val_temp =
								// formula_val_temp.concat($(this).val());
								// }
								formula_val_temp = formula_val_temp.concat($(
										this).val());
								$(".rulebox").val(formula_val_temp);
							});

					$(".tsrcreatemsgid")
							.click(
									function() {
										var no_error = 1;
										$('.input_error').remove();

										if ($("#messageid").val().length == 0) {
											$(
													'<span class="input_error" style="font-size:12px;color:red">Please enter message to be saved</span>')
													.insertAfter(
															$('#messageid'));
											no_error = 0;
										}

										if ($("#msgid").val()) {
											if ($("#msgparamid").val().length == 0) {
												$(
														'<span class="input_error" style="font-size:12px;color:red">Please enter Param Value</span>')
														.insertAfter(
																$('#msgparamid'));
												no_error = 0;
											}
											if ($("#msgvalueid").val().length == 0) {
												$(
														'<span class="input_error" style="font-size:12px;color:red">Please enter Param Value</span>')
														.insertAfter(
																$('#msgvalueid'));
												no_error = 0;
											}
										}

										if (no_error) {
											var form_data = {
												appid : $("#appid").val(),
												ruleid : $("#msgsourceid")
														.val(),
												rulesource : $("#msgsourcename")
														.val(),
												message : $("#messageid").val(),
												param : $("#msgparamid").val(),
												value : $("#msgvalueid").val(),
												times : new Date().getTime()
											};
											if ($("#msgid").length > 0) {
												form_data['msgid'] = $("#msgid")
														.val();
											}
											// console.log(form_data); return
											// false;
											$
													.ajax({
														url : $("#msgurl")
																.val(),
														type : 'POST',headers: { 'token':$("#token").val()},
														datatype : 'json',
														data : form_data,
														success : function(
																message) {
															var j_obj = $
																	.parseJSON(message);
															if (j_obj
																	.hasOwnProperty('success')) {
																toastr
																		.info('Param-Values saved successfully');
																setTimeout(
																		function() {
																			window.location
																					.reload();

																		}, 1000);
															} else if (j_obj
																	.hasOwnProperty('fail')) {
																console
																		.log(j_obj.fail);
																toastr
																		.info(j_obj.fail);
															}
														},
														error : function(xhr,
																textStatus,
																errorThrown) {
															$('#initial')
																	.hide();
															$('#fail').show();
														}
													});
										}
										return false;
									});
					$('.frequency')
							.click(
									function() {
										var check_value = $(
												'.frequency:checked').val();
										if ($('.frequency:checked').val() == 'daily') {
											$('.daywrapper').hide();
											$('#datewrapper').hide();
											$('.dayselector').show();
										} else {
											$('.dayselector').hide();
											if (($('.frequency:checked').val() == 'monthly')
													|| ($('.frequency:checked')
															.val() == 'yearly')) {
												$('#datewrapper').show();
												$('.daywrapper').hide();
											} else {
												$('.daywrapper').show();
												$('#datewrapper').hide();
											}
										}
									});

					$("#schedulecreateid")
							.click(
									function() {
										var no_error = 1;
										$('.input_error').remove();

										if ($('.frequency:checked').val().length == 0) {
											$(
													'<span class="input_error" style="font-size:12px;color:red">Please select recurrence time</span>')
													.insertAfter(
															$('.frequency'));
											no_error = 0;
										}

										if ($('.classday:checked').val().length == 0) {
											$(
													'<span class="input_error" style="font-size:12px;color:red">Please select day of the week</span>')
													.insertAfter($('.classday'));
											no_error = 0;
										}

										if ($("#schnameid").val().length == 0) {
											$(
													'<span class="input_error" style="font-size:12px;color:red">Please specifiy a name for Schedule</span>')
													.insertAfter(
															$('#schnameid'));
											no_error = 0;
										}
										if (($('.frequency:checked').val() == 'monthly')
												|| ($('.frequency:checked')
														.val() == 'yearly')) {
											if ($('#dateid').val().length == 0) {
												$(
														'<span class="input_error" style="font-size:12px;color:red">Please Choose a Date to Procees</span>')
														.insertAfter(
																$('#dateid'));
												no_error = 0;
											}
										}

										var expr = '';
										var i;
										var selectedId;

										for (i = 1; i < 8; i++) {

											selectedId = '#' + 'classday' + i;
											if ($(selectedId).is(":checked")) {
												expr = expr.concat(
														$(selectedId).val(),
														', ');
											}
										}
										if (no_error) {
											var form_data = {
												time : $("#timeid").val(),
												name : $("#schnameid").val(),
												description : $("#descid")
														.val(),
												frequency : $(
														'.frequency:checked')
														.val(),
												scheduleDay : $(
														'.classday:checked')
														.val(),
												times : new Date().getTime()
											};
											if ($('.frequency:checked').val() == 'daily') {
												form_data['scheduleDay'] = expr;
											}
											if (($('.frequency:checked').val() == 'monthly')
													|| ($('.frequency:checked')
															.val() == 'yearly')) {
												form_data['date'] = $('#dateid')
														.val();
											}
											// console.log(form_data); return
											// false;
											$
													.ajax({
														url : $("#tourl").val(),
														type : 'POST',headers: { 'token':$("#token").val()},
														datatype : 'json',
														data : form_data,
														success : function(
																message) {
															var j_obj = $
																	.parseJSON(message);
															if (j_obj
																	.hasOwnProperty('success')) {
																toastr
																		.info('Schedule saved successfully');
																setTimeout(
																		function() {
																			window.location.href = j_obj.success;

																		}, 1000);
															} else if (j_obj
																	.hasOwnProperty('fail')) {
																console
																		.log(j_obj.fail);
																toastr
																		.info(j_obj.fail);
															}
														},
														error : function(xhr,
																textStatus,
																errorThrown) {
															$('#initial')
																	.hide();
															$('#fail').show();
														}
													});
										}
										return false;
									});

					$("#assigncreateid")
							.click(
									function() {

										var no_error = 1;
										$('.input_error').remove();

										if ($('.calendarclass').is(':checked') == false) {
											// $('<span class="input_error"
											// style="font-size:12px;color:red">Please
											// choose a calendar to
											// proceed<br></span>').insertBefore($('#assigncreateid'));
											$('.note-danger')
													.removeClass('hidden')
													.html(
															'Please choose a calendar to proceed');
											no_error = 0;
											no_error = 0;
										}

										if (no_error) {
											var form_data = {
												scheduleid : $(
														'.calendarclass:checked')
														.val(),
												appid : $("#appid").val(),
												times : new Date().getTime()
											};

											// console.log(form_data); return
											// false;
											$
													.ajax({
														url : $("#tourl").val(),
														type : 'POST',headers: { 'token':$("#token").val()},
														datatype : 'json',
														data : form_data,
														success : function(
																message) {
															var j_obj = $
																	.parseJSON(message);
															if (j_obj
																	.hasOwnProperty('success')) {
																toastr
																		.info('Assign  saved successfully');
																setTimeout(
																		function() {
																			window.location
																					.reload();

																		}, 1000);
															} else if (j_obj
																	.hasOwnProperty('fail')) {
																console
																		.log(j_obj.fail);
																toastr
																		.info(j_obj.fail);
															}
														},
														error : function(xhr,
																textStatus,
																errorThrown) {
															$('#initial')
																	.hide();
															$('#fail').show();
														}
													});
										}
										return false;
									});

					$("#assigntrigcreateid")
							.click(
									function() {
										var no_error = 1;
										$('.input_error').remove();

										if ($('#sourceappid').val() == $(
												"#targetappid").val()) {
											// $('<span class="input_error"
											// style="font-size:12px;color:red">Source
											// and Target cannot be
											// same<br></span>').insertAfter($('#targetappid'));
											$('.note-danger')
													.removeClass('hidden')
													.html(
															'Source and Target cannot be same');
											no_error = 0;
										}

										if (no_error) {
											var form_data = {
												appid : $('#sourceappid').val(),
												appid2 : $("#targetappid")
														.val(),
												times : new Date().getTime()
											};
											// console.log(form_data); return
											// false;
											$
													.ajax({
														url : $("#tourl2")
																.val(),
														type : 'POST',headers: { 'token':$("#token").val()},
														datatype : 'json',
														data : form_data,
														success : function(
																message) {
															var j_obj = $
																	.parseJSON(message);
															if (j_obj
																	.hasOwnProperty('success')) {
																toastr
																		.info('Assign saved successfully');
																setTimeout(
																		function() {
																			window.location
																					.reload();

																		}, 1000);
															} else if (j_obj
																	.hasOwnProperty('fail')) {
																console
																		.log(j_obj.fail);
																toastr
																		.info(j_obj.fail);
															}
														},
														error : function(xhr,
																textStatus,
																errorThrown) {
															$('#initial')
																	.hide();
															$('#fail').show();
														}
													});
										}
										return false;
									});

					// RESULTS
					// if( $('#setruleresultsid').length > 1 ) {
					// console.log($('#setruleresultsid').length);
					// $('#setruleresultsid').dataTable({'bSort': false});
					// }
					var dataTableObject;

					$("#resultsftsid")
							.change(
									function() {

										if ($("#resultsftsid").val() != -1) {
											var form_data = {
												appid : $('#resultsftsid')
														.val(),
												times : new Date().getTime()
											};
											// var table =
											// $('#setruleresultsid').DataTable();
											// console.log(form_data); return
											// false;
											$
													.ajax({
														url : $("#tourl").val(),
														type : 'POST',headers: { 'token':$("#token").val()},
														datatype : 'json',
														data : form_data,
														success : function(
																message) {
															var j_obj = $
																	.parseJSON(message);
															if (j_obj
																	.hasOwnProperty('success')) {
																if (dataTableObject) { // Check
																						// if
																						// table
																						// object
																						// exists
																						// and
																						// needs
																						// to
																						// be
																						// flushed
																	dataTableObject
																			.destroy(); // For
																						// new
																						// version
																						// use
																						// table.destroy();
																	$(
																			'#setruleresultsid')
																			.empty(); // empty
																						// in
																						// case
																						// the
																						// columns
																						// change
																}

																dataTableObject = $(
																		'#setruleresultsid')
																		.DataTable(
																				{
																					"scrollX" : true,
																					data : j_obj.success['Data'],
																					columns : j_obj.success['columns']
																				});
																$("#typerowid")
																		.show();
																// $('#setruleresultsid').show();
															} else if (j_obj
																	.hasOwnProperty('fail')) {
																$("#typerowid")
																		.hide();
																console
																		.log(j_obj.fail);
																toastr
																		.info(j_obj.fail);
															}
														},
														error : function(xhr,
																textStatus,
																errorThrown) {
															$('#initial')
																	.hide();
															$('#fail').show();
														}
													});
										}
										return false;
									});

					var ruledataTableObject;
					var ruledataTableObject1;

					$("#resultsftid")
							.change(
									function() {

										if ($("#resultsftid").val() != -1) {
											var form_data = {
												appid : $('#resultsftid').val(),
												times : new Date().getTime()
											};
											// var table =
											// $('#setruleresultsid').DataTable();
											// console.log(form_data); return
											// false;
											$
													.ajax({
														url : $("#tourl").val(),
														type : 'POST',headers: { 'token':$("#token").val()},
														datatype : 'json',
														data : form_data,
														success : function(
																message) {
															var j_obj = $
																	.parseJSON(message);
															if (j_obj
																	.hasOwnProperty('success')) {
																if (ruledataTableObject) { // Check
																							// if
																							// table
																							// object
																							// exists
																							// and
																							// needs
																							// to
																							// be
																							// flushed
																	ruledataTableObject
																			.destroy(); // For
																						// new
																						// version
																						// use
																						// table.destroy();
																	$(
																			'#setruleresultsid')
																			.empty(); // empty
																						// in
																						// case
																						// the
																						// columns
																						// change
																}

																ruledataTableObject = $(
																		'#setruleresultsid')
																		.DataTable(
																				{
																					"scrollX" : true,
																					data : j_obj.success['summaryData']['Data'],
																					columns : j_obj.success['summaryData']['columns']
																				});

																if (ruledataTableObject1) { // Check
																							// if
																							// table
																							// object
																							// exists
																							// and
																							// needs
																							// to
																							// be
																							// flushed
																	ruledataTableObject1
																			.destroy(); // For
																						// new
																						// version
																						// use
																						// table.destroy();
																	$(
																			'#setruleresultsid1')
																			.empty(); // empty
																						// in
																						// case
																						// the
																						// columns
																						// change
																}

																ruledataTableObject1 = $(
																		'#setruleresultsid1')
																		.DataTable(
																				{
																					"scrollX" : true,
																					data : j_obj.success['detailData']['Data'],
																					columns : j_obj.success['detailData']['columns']
																				});
																$("#typerowid")
																		.show();
																// $('#setruleresultsid').show();
															} else if (j_obj
																	.hasOwnProperty('fail')) {
																$("#typerowid")
																		.hide();
																$('.table_div')
																		.hide();
																toastr
																		.info(j_obj.fail);
															}
														},
														error : function(xhr,
																textStatus,
																errorThrown) {
															$('#initial')
																	.hide();
															$('#fail').show();
														}
													});
										}
										return false;
									});

					$("#resultsfcrid")
							.change(
									function() {

										if ($("#resultsfcrid").val() != -1) {
											var form_data = {
												appid : $('#resultsfcrid')
														.val(),
												times : new Date().getTime()
											};
											// var table =
											// $('#setruleresultsid').DataTable();
											// console.log(form_data); return
											// false;
											$
													.ajax({
														url : $("#tourl").val(),
														type : 'POST',headers: { 'token':$("#token").val()},
														datatype : 'json',
														data : form_data,
														success : function(
																message) {
															var j_obj = $
																	.parseJSON(message);
															if (j_obj
																	.hasOwnProperty('success')) {
																if (ruledataTableObject) { // Check
																							// if
																							// table
																							// object
																							// exists
																							// and
																							// needs
																							// to
																							// be
																							// flushed
																	ruledataTableObject
																			.destroy(); // For
																						// new
																						// version
																						// use
																						// table.destroy();
																	$(
																			'#setruleresultsid')
																			.empty(); // empty
																						// in
																						// case
																						// the
																						// columns
																						// change
																}

																ruledataTableObject = $(
																		'#setruleresultsid')
																		.DataTable(
																				{
																					"scrollX" : true,
																					data : j_obj.success['summaryData']['Data'],
																					columns : j_obj.success['summaryData']['columns']
																				});
																$("#typerowid")
																		.show();

																// $('#setruleresultsid').show();
															} else if (j_obj
																	.hasOwnProperty('fail')) {

																toastr
																		.info(j_obj.fail);
															}
														},
														error : function(xhr,
																textStatus,
																errorThrown) {
															$('#initial')
																	.hide();
															$('#fail').show();
														}
													});
										}
										return false;
									});

					$("#columnruletypeid")
							.change(
									function() {

										if ($("#columnruletypeid").val() != -1) {
											var form_data;
											if ($("#columnruletypeid").val() == 'Summary') {
												form_data = {
													appid : $('#resultsfcrid')
															.val(),
													times : new Date()
															.getTime()
												};
											} else {
												form_data = {
													appid : $('#resultsfcrid')
															.val(),
													ruletype : $(
															'#columnruletypeid')
															.val(),
													times : new Date()
															.getTime()
												};

											}
											// var table =
											// $('#setruleresultsid').DataTable();
											// console.log(form_data); return
											// false;
											$
													.ajax({
														url : $("#tourl").val(),
														type : 'POST',headers: { 'token':$("#token").val()},
														datatype : 'json',
														data : form_data,
														success : function(
																message) {
															var j_obj = $
																	.parseJSON(message);
															if (j_obj
																	.hasOwnProperty('success')) {
																if (ruledataTableObject) { // Check
																							// if
																							// table
																							// object
																							// exists
																							// and
																							// needs
																							// to
																							// be
																							// flushed
																	ruledataTableObject
																			.destroy(); // For
																						// new
																						// version
																						// use
																						// table.destroy();
																	$(
																			'#setruleresultsid')
																			.empty(); // empty
																						// in
																						// case
																						// the
																						// columns
																						// change
																}

																ruledataTableObject = $(
																		'#setruleresultsid')
																		.DataTable(
																				{
																					"scrollX" : true,
																					data : j_obj.success['summaryData']['Data'],
																					columns : j_obj.success['summaryData']['columns']
																				});
																$("#typerowid")
																		.show();
																$('.table_div')
																		.show();

																// $('#setruleresultsid').show();
															} else if (j_obj
																	.hasOwnProperty('fail')) {

																$('.table_div')
																		.hide();
																console
																		.log(j_obj.fail);
																toastr
																		.info(j_obj.fail);
															}
														},
														error : function(xhr,
																textStatus,
																errorThrown) {
															$('#initial')
																	.hide();
															$('#fail').show();
														}
													});
										}
										return false;
									});

					var ruledataTableObject2;
					var ruledataTableObject3;

					$("#resultsdmid")
							.change(
									function() {

										if ($("#resultsdmid").val() != -1) {
											var form_data = {
												appid : $('#resultsdmid').val(),
												times : new Date().getTime()
											};
											// var table =
											// $('#setruleresultsid').DataTable();
											// console.log(form_data); return
											// false;
											$
													.ajax({
														url : $("#tourl").val(),
														type : 'POST',headers: { 'token':$("#token").val()},
														datatype : 'json',
														data : form_data,
														success : function(
																message) {
															var j_obj = $
																	.parseJSON(message);
															if (j_obj
																	.hasOwnProperty('success')) {
																$noDataCount = 0;
																if (!(j_obj.success.summaryData === undefined
																		|| j_obj.success.summaryData === null
																		|| j_obj.success['summaryData']['Data'] === undefined || j_obj.success['summaryData']['Data'] === null)) {
																	console
																			.log(j_obj.success['summaryData']['Data']);
																	if (ruledataTableObject) { // Check
																								// if
																								// table
																								// object
																								// exists
																								// and
																								// needs
																								// to
																								// be
																								// flushed
																		ruledataTableObject
																				.destroy(); // For
																							// new
																							// version
																							// use
																							// table.destroy();
																		$(
																				'#setruleresultsid')
																				.empty(); // empty
																							// in
																							// case
																							// the
																							// columns
																							// change
																	}
																	ruledataTableObject = $(
																			'#setruleresultsid')
																			.DataTable(
																					{
																						data : j_obj.success['summaryData']['Data'],
																						columns : j_obj.success['summaryData']['columns']
																					});
																} else {
																	// show
																	// message
																	// that no
																	// data is
																	// there
																	$noDataCount++;
																	$(
																			'.trans-summary')
																			.hide();
																}

																if (!(j_obj.success.unmatchedData === undefined || j_obj.success.unmatchedData === null)) {
																	console
																			.log(j_obj.success.unmatchedData.Data);
																	if (ruledataTableObject1) { // Check
																								// if
																								// table
																								// object
																								// exists
																								// and
																								// needs
																								// to
																								// be
																								// flushed
																		ruledataTableObject1
																				.destroy(); // For
																							// new
																							// version
																							// use
																							// table.destroy();
																		$(
																				'#setruleresultsid1')
																				.empty(); // empty
																							// in
																							// case
																							// the
																							// columns
																							// change
																	}
																	ruledataTableObject1 = $(
																			'#setruleresultsid1')
																			.DataTable(
																					{
																						data : j_obj.success['unmatchedData']['Data'],
																						columns : j_obj.success['unmatchedData']['columns']
																					});
																} else {
																	// show
																	// message
																	// that no
																	// data is
																	// there
																	$noDataCount++;
																	$(
																			'.trans-unmatched')
																			.hide();
																}

																if (!(j_obj.success.leftonlyData === undefined || j_obj.success.leftonlyData === null)) {
																	console
																			.log(j_obj.success['leftonlyData']['Data']);
																	if (ruledataTableObject2) { // Check
																								// if
																								// table
																								// object
																								// exists
																								// and
																								// needs
																								// to
																								// be
																								// flushed
																		ruledataTableObject2
																				.destroy(); // For
																							// new
																							// version
																							// use
																							// table.destroy();
																		$(
																				'#setruleresultsid2')
																				.empty(); // empty
																							// in
																							// case
																							// the
																							// columns
																							// change
																	}

																	ruledataTableObject2 = $(
																			'#setruleresultsid2')
																			.DataTable(
																					{
																						data : j_obj.success['leftonlyData']['Data'],
																						columns : j_obj.success['leftonlyData']['columns']
																					});
																} else {
																	// show
																	// message
																	// that no
																	// data is
																	// there
																	$noDataCount++;
																	$(
																			'.trans-left-only')
																			.hide();
																}

																if (!(j_obj.success.rightonlyData === undefined || j_obj.success.rightonlyData === null)) {
																	console
																			.log(j_obj.success['rightonlyData']['Data']);
																	if (ruledataTableObject3) { // Check
																								// if
																								// table
																								// object
																								// exists
																								// and
																								// needs
																								// to
																								// be
																								// flushed
																		ruledataTableObject3
																				.destroy(); // For
																							// new
																							// version
																							// use
																							// table.destroy();
																		$(
																				'#setruleresultsid3')
																				.empty(); // empty
																							// in
																							// case
																							// the
																							// columns
																							// change
																	}

																	ruledataTableObject3 = $(
																			'#setruleresultsid3')
																			.DataTable(
																					{
																						data : j_obj.success['rightonlyData']['Data'],
																						columns : j_obj.success['rightonlyData']['columns']
																					});
																} else {
																	// show
																	// message
																	// that no
																	// data is
																	// there
																	$noDataCount++;
																	$(
																			'.trans-right-only')
																			.hide();
																}

																$(".table_div")
																		.show();
																$("#typerowid")
																		.show();
																// $('#setruleresultsid').show();
															} else if (j_obj
																	.hasOwnProperty('fail')) {
																$('.table_div')
																		.hide();
																toastr
																		.info(j_obj.fail);
															}
														},
														error : function(xhr,
																textStatus,
																errorThrown) {
															$('#initial')
																	.hide();
															$('#fail').show();
														}
													});
										}
										return false;
									});

					$("#resultsasid")
							.change(
									function() {

										if ($("#resultsasid").val() != -1) {
											var form_data = {
												appid : $('#resultsasid').val(),
												times : new Date().getTime()
											};
											// var table =
											// $('#setruleresultsid').DataTable();
											// console.log(form_data); return
											// false;
											$
													.ajax({
														url : $("#tourl").val(),
														type : 'POST',headers: { 'token':$("#token").val()},
														datatype : 'json',
														data : form_data,
														success : function(
																message) {
															var j_obj = $
																	.parseJSON(message);
															if (j_obj
																	.hasOwnProperty('success')) {
																if (ruledataTableObject) { // Check
																							// if
																							// table
																							// object
																							// exists
																							// and
																							// needs
																							// to
																							// be
																							// flushed
																	ruledataTableObject
																			.destroy(); // For
																						// new
																						// version
																						// use
																						// table.destroy();
																	$(
																			'#setruleresultsid')
																			.empty(); // empty
																						// in
																						// case
																						// the
																						// columns
																						// change
																}

																ruledataTableObject = $(
																		'#setruleresultsid')
																		.DataTable(
																				{
																					data : j_obj.success[0]['matchingresults']['Data'],
																					columns : j_obj.success[0]['matchingresults']['columns']
																				});
																$("#typerowid")
																		.show();

																// $('#setruleresultsid').show();
															} else if (j_obj
																	.hasOwnProperty('fail')) {

																toastr
																		.info(j_obj.fail);
															}
														},
														error : function(xhr,
																textStatus,
																errorThrown) {
															$('#initial')
																	.hide();
															$('#fail').show();
														}
													});
										}
										return false;
									});

					$("#stdruletypeid")
							.change(
									function() {

										if ($("#stdruletypeid").val() != -1) {
											var form_data;
											if ($("#stdruletypeid").val() == 'Benford') {
												form_data = {
													appid : $('#resultsasid')
															.val(),
													times : new Date()
															.getTime()
												};
											} else {
												form_data = {
													appid : $('#resultsasid')
															.val(),
													ruletype : $(
															'#stdruletypeid')
															.val(),
													times : new Date()
															.getTime()
												};
											}
											// var table =
											// $('#setruleresultsid').DataTable();
											// console.log(form_data); return
											// false;
											$
													.ajax({
														url : $("#tourl").val(),
														type : 'POST',headers: { 'token':$("#token").val()},
														datatype : 'json',
														data : form_data,
														success : function(
																message) {
															var j_obj = $
																	.parseJSON(message);
															if (j_obj
																	.hasOwnProperty('success')) {
																if (ruledataTableObject) { // Check
																							// if
																							// table
																							// object
																							// exists
																							// and
																							// needs
																							// to
																							// be
																							// flushed
																	ruledataTableObject
																			.destroy(); // For
																						// new
																						// version
																						// use
																						// table.destroy();
																	$(
																			'#setruleresultsid')
																			.empty(); // empty
																						// in
																						// case
																						// the
																						// columns
																						// change
																}

																ruledataTableObject = $(
																		'#setruleresultsid')
																		.DataTable(
																				{
																					data : j_obj.success[0]['matchingresults']['Data'],
																					columns : j_obj.success[0]['matchingresults']['columns']
																				});
																$("#typerowid")
																		.show();
																$('.table_div')
																		.show();

																// $('#setruleresultsid').show();
															} else if (j_obj
																	.hasOwnProperty('fail')) {

																$('.table_div')
																		.hide();
																console
																		.log(j_obj.fail);
																toastr
																		.info(j_obj.fail);
															}
														},
														error : function(xhr,
																textStatus,
																errorThrown) {
															$('#initial')
																	.hide();
															$('#fail').show();
														}
													});
										}
										return false;
									});

					$("#resultsacid")
							.change(
									function() {

										if ($("#resultsacid").val() != -1) {
											var form_data = {
												appid : $('#resultsacid').val(),
												times : new Date().getTime()
											};
											// var table =
											// $('#setruleresultsid').DataTable();
											// console.log(form_data); return
											// false;
											$
													.ajax({
														url : $("#tourl").val(),
														type : 'POST',headers: { 'token':$("#token").val()},
														datatype : 'json',
														data : form_data,
														success : function(
																message) {
															var j_obj = $
																	.parseJSON(message);
															if (j_obj
																	.hasOwnProperty('success')) {
																if (ruledataTableObject) { // Check
																							// if
																							// table
																							// object
																							// exists
																							// and
																							// needs
																							// to
																							// be
																							// flushed
																	ruledataTableObject
																			.destroy(); // For
																						// new
																						// version
																						// use
																						// table.destroy();
																	$(
																			'#setruleresultsid')
																			.empty(); // empty
																						// in
																						// case
																						// the
																						// columns
																						// change
																}

																ruledataTableObject = $(
																		'#setruleresultsid')
																		.DataTable(
																				{
																					data : j_obj.success[0]['matchingresults']['Data'],
																					columns : j_obj.success[0]['matchingresults']['columns']
																				});
																$("#typerowid")
																		.show();

																// $('#setruleresultsid').show();
															} else if (j_obj
																	.hasOwnProperty('fail')) {

																toastr
																		.info(j_obj.fail);
															}
														},
														error : function(xhr,
																textStatus,
																errorThrown) {
															$('#initial')
																	.hide();
															$('#fail').show();
														}
													});
										}
										return false;
									});

					$("#cusruletypeid")
							.change(
									function() {

										if ($("#cusruletypeid").val() != -1) {
											var form_data;
											// if($("#cusruletypeid").val() ==
											// 'Rule 3')
											// { form_data = {
											// appid : $('#resultsacid').val(),
											// times: new Date().getTime()
											// };
											// }else
											// {
											form_data = {
												appid : $('#resultsacid').val(),
												ruletype : $('#cusruletypeid')
														.val(),
												times : new Date().getTime()
											// }
											};
											// var table =
											// $('#setruleresultsid').DataTable();
											// console.log(form_data); return
											// false;
											$
													.ajax({
														url : $("#tourl").val(),
														type : 'POST',headers: { 'token':$("#token").val()},
														datatype : 'json',
														data : form_data,
														success : function(
																message) {
															var j_obj = $
																	.parseJSON(message);
															if (j_obj
																	.hasOwnProperty('success')) {
																if (ruledataTableObject) { // Check
																							// if
																							// table
																							// object
																							// exists
																							// and
																							// needs
																							// to
																							// be
																							// flushed
																	ruledataTableObject
																			.destroy(); // For
																						// new
																						// version
																						// use
																						// table.destroy();
																	$(
																			'#setruleresultsid')
																			.empty(); // empty
																						// in
																						// case
																						// the
																						// columns
																						// change
																}

																console
																		.log(j_obj.success);

																ruledataTableObject = $(
																		'#setruleresultsid')
																		.DataTable(
																				{
																					data : j_obj.success[0]['matchingresults']['Data'],
																					columns : j_obj.success[0]['matchingresults']['columns']
																				});
																$("#typerowid")
																		.show();
																$('.table_div')
																		.show();

																// $('#setruleresultsid').show();
															} else if (j_obj
																	.hasOwnProperty('fail')) {

																$('.table_div')
																		.hide();
																console
																		.log(j_obj.fail);
																toastr
																		.info(j_obj.fail);
															}
														},
														error : function(xhr,
																textStatus,
																errorThrown) {
															$('#initial')
																	.hide();
															$('#fail').show();
														}
													});
										}
										return false;
									});

					// Date filter functions
					$("#customdateid")
							.click(
									function() {
										var form_data;
										if ($("#columnruletypeid").val() == 'Summary') {
											form_data = {
												appid : $('#resultsfcrid')
														.val(),
												times : new Date().getTime()
											};
										} else {
											form_data = {
												appid : $('#resultsfcrid')
														.val(),
												ruletype : $(
														'#columnruletypeid')
														.val(),
												times : new Date().getTime()
											};
										}
										if ($("#startdateid").val() != "") {
											form_data['startdate'] = $(
													"#startdateid").val();
										}
										if ($("#enddateid").val() != "") {
											form_data['enddate'] = $(
													"#enddateid").val();
										}
										// var table =
										// $('#setruleresultsid').DataTable();
										console.log(form_data); // return false;
										$
												.ajax({
													url : $("#tourl").val(),
													type : 'POST',headers: { 'token':$("#token").val()},
													datatype : 'json',
													data : form_data,
													success : function(message) {
														var j_obj = $
																.parseJSON(message);
														if (j_obj
																.hasOwnProperty('success')) {
															if (ruledataTableObject) { // Check
																						// if
																						// table
																						// object
																						// exists
																						// and
																						// needs
																						// to
																						// be
																						// flushed
																ruledataTableObject
																		.destroy(); // For
																					// new
																					// version
																					// use
																					// table.destroy();
																$(
																		'#setruleresultsid')
																		.empty(); // empty
																					// in
																					// case
																					// the
																					// columns
																					// change
															}

															console
																	.log(j_obj.success);

															ruledataTableObject = $(
																	'#setruleresultsid')
																	.DataTable(
																			{
																				data : j_obj.success[0]['matchingresults']['Data'],
																				columns : j_obj.success[0]['matchingresults']['columns']
																			});
															$("#typerowid")
																	.show();
															$('.table_div')
																	.show();

															// $('#setruleresultsid').show();
														} else if (j_obj
																.hasOwnProperty('fail')) {

															$('.table_div')
																	.hide();
															console
																	.log(j_obj.fail);
															toastr
																	.info(j_obj.fail);
														}
													},
													error : function(xhr,
															textStatus,
															errorThrown) {
														$('#initial').hide();
														$('#fail').show();
													}
												});

										return false;
									});

					$("#stddateid")
							.click(
									function() {
										var form_data;
										form_data = {
											appid : $('#resultsasid').val(),
											ruletype : $('#stdruletypeid')
													.val(),
											times : new Date().getTime()
										};

										if ($("#startdateid").val() != "") {
											form_data['startdate'] = $(
													"#startdateid").val();
										}
										if ($("#enddateid").val() != "") {
											form_data['enddate'] = $(
													"#enddateid").val();
										}
										// var table =
										// $('#setruleresultsid').DataTable();
										console.log(form_data); // return false;
										$
												.ajax({
													url : $("#tourl").val(),
													type : 'POST',headers: { 'token':$("#token").val()},
													datatype : 'json',
													data : form_data,
													success : function(message) {
														var j_obj = $
																.parseJSON(message);
														if (j_obj
																.hasOwnProperty('success')) {
															if (ruledataTableObject) { // Check
																						// if
																						// table
																						// object
																						// exists
																						// and
																						// needs
																						// to
																						// be
																						// flushed
																ruledataTableObject
																		.destroy(); // For
																					// new
																					// version
																					// use
																					// table.destroy();
																$(
																		'#setruleresultsid')
																		.empty(); // empty
																					// in
																					// case
																					// the
																					// columns
																					// change
															}

															ruledataTableObject = $(
																	'#setruleresultsid')
																	.DataTable(
																			{
																				data : j_obj.success[0]['matchingresults']['Data'],
																				columns : j_obj.success[0]['matchingresults']['columns']
																			});
															$("#typerowid")
																	.show();
															$('.table_div')
																	.show();

															// $('#setruleresultsid').show();
														} else if (j_obj
																.hasOwnProperty('fail')) {

															$('.table_div')
																	.hide();
															console
																	.log(j_obj.fail);
															toastr
																	.info(j_obj.fail);
														}
													},
													error : function(xhr,
															textStatus,
															errorThrown) {
														$('#initial').hide();
														$('#fail').show();
													}
												});

										return false;
									});

					$("#matchdateid")
							.click(
									function() {

										if ($("#resultsdmid").val() != -1) {
											var form_data = {
												appid : $('#resultsdmid').val(),
												times : new Date().getTime()
											};
											if ($("#startdateid").val() != "") {
												form_data['startdate'] = $(
														"#startdateid").val();
											}
											if ($("#enddateid").val() != "") {
												form_data['enddate'] = $(
														"#enddateid").val();
											}
											// var table =
											// $('#setruleresultsid').DataTable();
											console.log(form_data); // return
																	// false;
											$
													.ajax({
														url : $("#tourl").val(),
														type : 'POST',headers: { 'token':$("#token").val()},
														datatype : 'json',
														data : form_data,
														success : function(
																message) {
															var j_obj = $
																	.parseJSON(message);
															if (j_obj
																	.hasOwnProperty('success')) {
																if (ruledataTableObject) { // Check
																							// if
																							// table
																							// object
																							// exists
																							// and
																							// needs
																							// to
																							// be
																							// flushed
																	ruledataTableObject
																			.destroy(); // For
																						// new
																						// version
																						// use
																						// table.destroy();
																	$(
																			'#setruleresultsid')
																			.empty(); // empty
																						// in
																						// case
																						// the
																						// columns
																						// change
																}
																ruledataTableObject = $(
																		'#setruleresultsid')
																		.DataTable(
																				{
																					data : j_obj.success['summaryData']['Data'],
																					columns : j_obj.success['summaryData']['columns']
																				});

																if (ruledataTableObject1) { // Check
																							// if
																							// table
																							// object
																							// exists
																							// and
																							// needs
																							// to
																							// be
																							// flushed
																	ruledataTableObject1
																			.destroy(); // For
																						// new
																						// version
																						// use
																						// table.destroy();
																	$(
																			'#setruleresultsid1')
																			.empty(); // empty
																						// in
																						// case
																						// the
																						// columns
																						// change
																}

																ruledataTableObject1 = $(
																		'#setruleresultsid1')
																		.DataTable(
																				{
																					data : j_obj.success['unmatchedData']['Data'],
																					columns : j_obj.success['unmatchedData']['columns']
																				});

																if (ruledataTableObject2) { // Check
																							// if
																							// table
																							// object
																							// exists
																							// and
																							// needs
																							// to
																							// be
																							// flushed
																	ruledataTableObject2
																			.destroy(); // For
																						// new
																						// version
																						// use
																						// table.destroy();
																	$(
																			'#setruleresultsid2')
																			.empty(); // empty
																						// in
																						// case
																						// the
																						// columns
																						// change
																}

																ruledataTableObject2 = $(
																		'#setruleresultsid2')
																		.DataTable(
																				{
																					data : j_obj.success['leftonlyData']['Data'],
																					columns : j_obj.success['leftonlyData']['columns']
																				});

																if (ruledataTableObject3) { // Check
																							// if
																							// table
																							// object
																							// exists
																							// and
																							// needs
																							// to
																							// be
																							// flushed
																	ruledataTableObject3
																			.destroy(); // For
																						// new
																						// version
																						// use
																						// table.destroy();
																	$(
																			'#setruleresultsid3')
																			.empty(); // empty
																						// in
																						// case
																						// the
																						// columns
																						// change
																}

																ruledataTableObject3 = $(
																		'#setruleresultsid3')
																		.DataTable(
																				{
																					data : j_obj.success['rightonlyData']['Data'],
																					columns : j_obj.success['rightonlyData']['columns']
																				});
																$(".table_div")
																		.show();
																$("#typerowid")
																		.show();
																// $('#setruleresultsid').show();
															} else if (j_obj
																	.hasOwnProperty('fail')) {
																$('.table_div')
																		.hide();
																toastr
																		.info(j_obj.fail);
															}
														},
														error : function(xhr,
																textStatus,
																errorThrown) {
															$('#initial')
																	.hide();
															$('#fail').show();
														}
													});
										}
										return false;
									});

					$("#setruledateid")
							.click(
									function() {

										if ($("#resultsftsid").val() != -1) {
											var form_data = {
												appid : $('#resultsftsid')
														.val(),
												times : new Date().getTime()
											};
											if ($("#startdateid").val() != "") {
												form_data['startdate'] = $(
														"#startdateid").val();
											}
											if ($("#enddateid").val() != "") {
												form_data['enddate'] = $(
														"#enddateid").val();
											}
											// var table =
											// $('#setruleresultsid').DataTable();
											// console.log(form_data); //return
											// false;
											$
													.ajax({
														url : $("#tourl").val(),
														type : 'POST',headers: { 'token':$("#token").val()},
														datatype : 'json',
														data : form_data,
														success : function(
																message) {
															var j_obj = $
																	.parseJSON(message);
															if (j_obj
																	.hasOwnProperty('success')) {
																if (dataTableObject) { // Check
																						// if
																						// table
																						// object
																						// exists
																						// and
																						// needs
																						// to
																						// be
																						// flushed
																	dataTableObject
																			.destroy(); // For
																						// new
																						// version
																						// use
																						// table.destroy();
																	$(
																			'#setruleresultsid')
																			.empty(); // empty
																						// in
																						// case
																						// the
																						// columns
																						// change
																}

																dataTableObject = $(
																		'#setruleresultsid')
																		.DataTable(
																				{
																					// "bDestroy":
																					// true,
																					data : j_obj.success['Data'],
																					columns : j_obj.success['columns']
																				});
																$("#typerowid")
																		.show();
																// $('#setruleresultsid').show();
															} else if (j_obj
																	.hasOwnProperty('fail')) {
																$("#typerowid")
																		.hide();
																console
																		.log(j_obj.fail);
																toastr
																		.info(j_obj.fail);
															}
														},
														error : function(xhr,
																textStatus,
																errorThrown) {
															$('#initial')
																	.hide();
															$('#fail').show();
														}
													});
										}
										return false;
									});

					$("#ruledateid")
							.click(
									function() {

										if ($("#resultsftid").val() != -1) {
											var form_data = {
												appid : $('#resultsftid').val(),
												times : new Date().getTime()
											};
											if ($("#startdateid").val() != "") {
												form_data['startdate'] = $(
														"#startdateid").val();
											}
											if ($("#enddateid").val() != "") {
												form_data['enddate'] = $(
														"#enddateid").val();
											}
											// var table =
											// $('#setruleresultsid').DataTable();
											// console.log(form_data); return
											// false;
											$
													.ajax({
														url : $("#tourl").val(),
														type : 'POST',headers: { 'token':$("#token").val()},
														datatype : 'json',
														data : form_data,
														success : function(
																message) {
															var j_obj = $
																	.parseJSON(message);
															if (j_obj
																	.hasOwnProperty('success')) {
																if (ruledataTableObject) { // Check
																							// if
																							// table
																							// object
																							// exists
																							// and
																							// needs
																							// to
																							// be
																							// flushed
																	ruledataTableObject
																			.destroy(); // For
																						// new
																						// version
																						// use
																						// table.destroy();
																	$(
																			'#setruleresultsid')
																			.empty(); // empty
																						// in
																						// case
																						// the
																						// columns
																						// change
																}

																ruledataTableObject = $(
																		'#setruleresultsid')
																		.DataTable(
																				{
																					data : j_obj.success['summaryData']['Data'],
																					columns : j_obj.success['summaryData']['columns']
																				});

																if (ruledataTableObject1) { // Check
																							// if
																							// table
																							// object
																							// exists
																							// and
																							// needs
																							// to
																							// be
																							// flushed
																	ruledataTableObject1
																			.destroy(); // For
																						// new
																						// version
																						// use
																						// table.destroy();
																	$(
																			'#setruleresultsid1')
																			.empty(); // empty
																						// in
																						// case
																						// the
																						// columns
																						// change
																}

																ruledataTableObject1 = $(
																		'#setruleresultsid1')
																		.DataTable(
																				{
																					data : j_obj.success['detailData']['Data'],
																					columns : j_obj.success['detailData']['columns']
																				});
																$("#typerowid")
																		.show();
																// $('#setruleresultsid').show();
															} else if (j_obj
																	.hasOwnProperty('fail')) {
																$("#typerowid")
																		.hide();
																$('.table_div')
																		.hide();
																toastr
																		.info(j_obj.fail);
															}
														},
														error : function(xhr,
																textStatus,
																errorThrown) {
															$('#initial')
																	.hide();
															$('#fail').show();
														}
													});
										}
										return false;
									});

					$("#columndateid")
							.click(
									function() {

										if ($("#resultsfcrid").val() != -1) {
											var form_data = {
												appid : $('#resultsfcrid')
														.val(),
												times : new Date().getTime()
											};
											if ($("#startdateid").val() != "") {
												form_data['startdate'] = $(
														"#startdateid").val();
											}
											if ($("#enddateid").val() != "") {
												form_data['enddate'] = $(
														"#enddateid").val();
											}
											// var table =
											// $('#setruleresultsid').DataTable();
											console.log(form_data); // return
																	// false;
											$
													.ajax({
														url : $("#tourl").val(),
														type : 'POST',headers: { 'token':$("#token").val()},
														datatype : 'json',
														data : form_data,
														success : function(
																message) {
															var j_obj = $
																	.parseJSON(message);
															if (j_obj
																	.hasOwnProperty('success')) {
																if (ruledataTableObject) { // Check
																							// if
																							// table
																							// object
																							// exists
																							// and
																							// needs
																							// to
																							// be
																							// flushed
																	ruledataTableObject
																			.destroy(); // For
																						// new
																						// version
																						// use
																						// table.destroy();
																	$(
																			'#setruleresultsid')
																			.empty(); // empty
																						// in
																						// case
																						// the
																						// columns
																						// change
																}

																ruledataTableObject = $(
																		'#setruleresultsid')
																		.DataTable(
																				{
																					data : j_obj.success['summaryData']['Data'],
																					columns : j_obj.success['summaryData']['columns']
																				});
																$("#typerowid")
																		.show();

																// $('#setruleresultsid').show();
															} else if (j_obj
																	.hasOwnProperty('fail')) {
																// $("#typerowid").hide();
																toastr
																		.info(j_obj.fail);
															}
														},
														error : function(xhr,
																textStatus,
																errorThrown) {
															$('#initial')
																	.hide();
															$('#fail').show();
														}
													});
										}
										return false;
									});
					$('#s3typevalueid').change(function() {
						$this = $(this);
						var selectedVal = $this.val();
						if (selectedVal == 'Your S3 Bucket') {
							$('#s3_form_id').removeClass('hidden').show();
						} else if (selectedVal == 'DataBuck S3 Bucket') {
							$('#s3_form_id').addClass('hidden').hide();
						}
					});

					$(".appcloneid")
							.click(
									function() {
										var no_error = 1;
										$('.input_error').remove();
										if ($("#clonenameid").val().length == 0) {
											$(
													'<div class="input_error" style="font-size:12px;color:red;">Please enter Name for validation Check</div>')
													.insertAfter(
															$('#clonenameid'));
											no_error = 0;
										}
										if (no_error) {
											var form_data = {
												id : $("#appid").val(),
												name : $("#clonenameid").val(),
												times : new Date().getTime()
											};
											// console.log(form_data);
											// console.log(url);return false;
											$
													.ajax({
														url : $("#tourl").val(),
														type : 'POST',headers: { 'token':$("#token").val()},
														datatype : 'json',
														data : form_data,
														success : function(
																message) {
															var j_obj = $
																	.parseJSON(message);
															if (j_obj
																	.hasOwnProperty('success')) {
																toastr
																		.info('Validation Check Cloned successfully');
																setTimeout(
																		function() {
																			window.location.href = j_obj.success;

																		}, 1000);
															} else if (j_obj
																	.hasOwnProperty('fail')) {
																console
																		.log(j_obj.fail);
																toastr
																		.info(j_obj.fail);
															}
														},
														error : function(xhr,
																textStatus,
																errorThrown) {
															$('#initial')
																	.hide();
															$('#fail').show();
														}
													});
										}
										return false;
									});
					$(".dashboard-summary").click(function() {
						console.log('ok');
						var $this = $(this);
						var currId = $this.attr('id');
						console.log(currId);
						var currChartId = currId + '_div';
						$('.dashboard-summary-chart').hide();
						$('#' + currChartId).width('100%').show();
					});

					$('.datatable-matchingTable').DataTable({
						"scrollX" : true
					})

					if ($('.datatable-master').length > 0) {
						$('.datatable-master').DataTable({
							"scrollX" : true
						});
					}
				});

$(document).ready(
		function() {
			if ($('#appCustomization').length > 0) {

				var incrementalMatching = $("#incrementalMatching").val();
				var buildHistoricFingerPrint = $("#buildHistoricFingerPrint")
						.val();
				console
						.log(incrementalMatching + ""
								+ buildHistoricFingerPrint);
				if ((incrementalMatching == "Y")
						&& (buildHistoricFingerPrint == "Y")) {
					$('#dateDivId').removeClass('hidden').show();
					console.log("Historic");
					$("#ApplicationType").val("Historic");
				} else if ((incrementalMatching == "Y")
						&& (buildHistoricFingerPrint == "N")) {
					$('#dateDivId').addClass("hidden");
					$("#ApplicationType").val("Ongoing");
				} else {
					$('#dateDivId').addClass("hidden");
					$("#ApplicationType").val("Normal");
				}

				/*if(incrementalMatching=="Y"){
					$("#incrementalTypeId").attr("checked",true);
				}else{
					$("#incrementalTypeId").attr("checked",false);
				}*/

				/*if(buildHistoricFingerPrint=="Y"){
					$("#buildHistoricId").attr("checked",true);
					$('#dateDivId').removeClass('hidden').show();
				}else{
					$("#buildHistoricId").attr("checked",false);
					$('#dateDivId').addClass("hidden");
				}*/

				var duplicateCheck = $("#duplicateCheck").val();
				//alert(stringStatStatus);
				if (duplicateCheck == "Y") {
					$("#dupCheck").attr("checked", true);
				} else {
					$("#dupCheck").attr("checked", false);
				}

				var stringStatStatus = $("#stringStatStatus").val();
				//alert(stringStatStatus);
				if (stringStatStatus == "Y") {
					$("#stringStat").attr("checked", true);
				} else {
					$("#stringStat").attr("checked", false);
				}
				var nullCountStatus = $("#nullCountStatus").val();
				//alert(stringStatStatus);
				if (nullCountStatus == "Y") {
					$("#nullCount").attr("checked", true);
				} else {
					$("#nullCount").attr("checked", false);
				}
				var numericalStatsStatus = $("#numericalStatsStatus").val();
				if (numericalStatsStatus == "Y") {
					$("#numericalStats").attr("checked", true);
				} else {
					$("#numericalStats").attr("checked", false);
				}

				var recordAnomalyStatus = $("#recordAnomalyStatus").val();
				//alert(stringStatStatus);
				if (recordAnomalyStatus == "Y") {
					$("#recordAnomalyid").attr("checked", true);
				} else {
					$("#recordAnomalyid").attr("checked", false);
				}

				var dataDriftStatus = $("#dataDriftStatus").val();
				//alert(stringStatStatus);
				if (dataDriftStatus == "Y") {
					$("#dataDriftCheck").attr("checked", true);
				} else {
					$("#dataDriftCheck").attr("checked", false);
				}

				var outofNormStatus = $("#outofNormStatus").val();
				//alert(stringStatStatus);
				if (outofNormStatus == "Y") {
					$("#outofNorm").attr("checked", true);
				} else {
					$("#outofNorm").attr("checked", false);
				}

				var identityDupRow = $("#identityDupRow").val();
				if (identityDupRow == "Y") {
					$("#duplicateCount").attr("checked", true);
				} else {
					$("#duplicateCount").attr("checked", false);
				}

				var defaultCheckStatus = $("#defaultCheckStatus").val();
				//alert(stringStatStatus);
				if (defaultCheckStatus == "Y") {
					$("#defaultCheck").attr("checked", true);
				} else {
					$("#defaultCheck").attr("checked", false);
				}

				var allDupRow = $("#allDupRow").val();
				//alert(stringStatStatus);
				if (allDupRow == "Y") {
					$("#duplicateCountAll").attr("checked", true);
				} else {
					$("#duplicateCountAll").attr("checked", false);
				}

				var recordCountAnomalyTypeStatus = $(
						"#recordCountAnomalyTypeStatus").val();
				//alert(stringStatStatus);
				if (recordCountAnomalyTypeStatus == "Y") {
					$(".recordCountAnomaly").val("RecordCountAnomaly");
				} else {
					$(".recordCountAnomaly").val("KeyBasedRecordCountAnomaly");
					$('#groupEqualitydiv').removeClass('hidden').show();
					var groupEqualityStatus = $("#groupEqualityStatus").val();
					//alert(incrementalMatching);
					if (groupEqualityStatus == "Y") {
						$("#groupEquality").attr("checked", true);
					} else {
						$("#groupEquality").attr("checked", false);
					}
				}

				var applyRulesStatus = $("#applyRulesStatus").val();
				if (applyRulesStatus == "Y") {
					$("#applyRules").attr("checked", true);
				} else {
					$("#applyRules").attr("checked", false);
				}

				var applyDerivedColumnsStatus = $("#applyDerivedColumnsStatus")
						.val();
				if (applyDerivedColumnsStatus == "Y") {
					$("#applyDerivedColumns").attr("checked", true);
				} else {
					$("#applyDerivedColumns").attr("checked", false);
				}
				var timelinessKeyStatus = $("#timelinessKeyStatus").val();
				if (timelinessKeyStatus == "Y") {
					$("#timelinessKey").attr("checked", true);
				} else {
					$("#timelinessKey").attr("checked", false);
				}
				var patternCheckStatus = $("#patternCheckStatus").val();
				if (patternCheckStatus == "Y") {
					$("#patternCheck").attr("checked", true);
				} else {
					$("#patternCheck").attr("checked", false);
				}
				var defaultPatternCheckStatus = $("#defaultPatternCheckStatus").val();
				if (defaultPatternCheckStatus == "Y") {
					$("#defaultPatternCheck").attr("checked", true);
				} else {
					$("#defaultPatternCheck").attr("checked", false);
				}
				//sumeet
				var badDataStatus = $("#badDataStatus").val();
				if (badDataStatus == "Y") {

					$("#badData").attr("checked", true);
				} else {
					$("#badData").attr("checked", false);
				}

				// length chaeck changes 28dec2018 priyanka

				var lengthCheckStatus = $("#lengthCheckStatus").val();

				if (lengthCheckStatus == "Y") {

					$("#lengthCheck").attr("checked", true);
				} else {
					$("#lengthCheck").attr("checked", false);
				}

				// dGroupNull check changes
				var dGroupNullCheckStatus = $("#dGroupNullCheckStatus").val();
				if (dGroupNullCheckStatus == "Y") {
					$("#dGroupNullCheck").attr("checked", true);
				} else {
					$("#dGroupNullCheck").attr("checked", false);
				}

				// dGroupDateRuleCheck changes
				var dGroupDateRuleCheckStatus = $("#dGroupDateRuleCheckStatus")
						.val();
				if (dGroupDateRuleCheckStatus == "Y") {
					$("#dGroupDateRuleCheck").attr("checked", true);
				} else {
					$("#dGroupDateRuleCheck").attr("checked", false);
				}

				// date rule changes 8jan2019 priyanka

				var dateRuleStatus = $("#dateRuleChkStatus").val();
				//alert("dateRuleStatus ->",dateRuleStatus);
				if (dateRuleStatus == "Y") {
					//	alert("hiiiiiiii");
					$("#dateRuleCheck").attr("checked", true);
				} else {
					$("#dateRuleCheck").attr("checked", false);
				}

				var fileNameValStatus = $("#fileNameValStatus").val();
				//alert(stringStatStatus);
				if (fileNameValStatus == "Y") {
					$("#fileNameVal").attr("checked", true);
				} else {
					$("#fileNameVal").attr("checked", false);
				}

				var columnOrderValStatus = $("#columnOrderValStatus").val();
				//alert(stringStatStatus);
				if (columnOrderValStatus == "Y") {
					$("#columnOrderVal").attr("checked", true);
				} else {
					$("#columnOrderVal").attr("checked", false);
				}
				var dataLocation = $("#dataLocation").val();
				if (dataLocation == "FILESYSTEM") {

					$(".fileSystemdiv").removeClass("hidden");
				}

				var noneStatus = $("#noneStatus").val();
				var month = $("#month").val();
				var dayOfWeek = $("#dayOfWeek").val();
				var hourOfDay = $("#hourOfDay").val();
				var dayOfMonth = $("#dayOfMonth").val();
				var frequencyDays = $("#frequencyDays").val();
				var updateFrequency = $("#updateFrequency").val();

				if (updateFrequency == "Daily") {
					$(".selectfrequency").val(updateFrequency);
				} else if (updateFrequency == "Never") {
					$(".selectfrequency").val(updateFrequency);

				} else {
					$(".selectfrequency").val(updateFrequency);
					$('#EveryDay').removeClass('hidden').show();
				}

				if (noneStatus == "None") {
					//alert(noneStatus);
					$("#none").attr("checked", true);
					$(".divmonth").addClass("hidden");
					$(".divHourOfDay").addClass("hidden");
					$(".divDayOfMonth").addClass("hidden");
					$(".divDayOfWeek").addClass("hidden");
					$('#noneid').addClass('hidden');
				} else {
				}

				if (month != "") {
					$("#chkmonth").attr("checked", true);
					$(".divmonth").removeClass("hidden");
					$(".month").val(month);
				} else {
					$("#chkmonth").attr("checked", false);
					$(".divmonth").addClass("hidden");
				}

				if (dayOfWeek != "") {
					$("#chkDayOfWeek").attr("checked", true);
					$(".divDayOfWeek").removeClass("hidden");
					$(".dayOfWeek").val(dayOfWeek);
				} else {
					$("#chkDayOfWeek").attr("checked", false);
					$(".divDayOfWeek").addClass("hidden");
				}

				if (hourOfDay != "") {
					console.log(hourOfDay);
					$("#chkHourOfDay").attr("checked", true);
					$(".divHourOfDay").removeClass("hidden");
					$(".hourofDay").val(hourOfDay);
				} else {
					$("#chkHourOfDay").attr("checked", false);
					$(".divHourOfDay").addClass("hidden");
				}

				if (dayOfMonth != "") {
					console.log(dayOfMonth);
					$("#chkDayOfMonth").attr("checked", true);
					$(".divDayOfMonth").removeClass("hidden");
					$(".dayOfMonth").val(dayOfMonth);
				} else {
					$("#chkDayOfMonth").attr("checked", false);
					$(".divDayOfMonth").addClass("hidden");
				}

				// Avishkar [03-Jun-2020] : changes for Microsegment Data Drift
				var dGroupDataDriftCheckStatus = $("#dGroupDataDriftCheckStatus").val();
				if (dGroupDataDriftCheckStatus == "Y") {
					$("#dGroupDataDriftCheck").attr("checked", true);
				} else {
					$("#dGroupDataDriftCheck").attr("checked", false);
				}
			}
		});

$('#ruleCategoryid').change(function() {
	var sIgnoreGlobalEvent = $(document.body).attr('data-ignore-global-event');

	if ( $('#ruleCategoryid').val() == 'Referential')
	{
		$('#ruleexprdivid').removeClass('hidden').show();
		$('#rightdatasourceid').addClass('hidden').hide();
		$('#rightdatasourceid2').addClass('hidden').hide();
		$('#matchexprdivid').addClass('hidden').hide();
		$('#matchTypedivid').addClass('hidden').hide();
	}
	else if(($('#ruleCategoryid').val() =='Cross Referential'))
	{
		$('#ruleexprdivid').removeClass('hidden').show();
		$('#matchexprdivid').removeClass('hidden').show();
		$('#rightdatasourceid2').removeClass('hidden').show();
		$('#rightdatasourceid').removeClass('hidden').show();
		//$("#matchexprid").attr("placeholder", "dataSource1.columnName=dataSource2.columnName").val("").focus().blur();
		$("#matchexprid").attr("placeholder", "dataSource1.columnName=dataSource2.columnName");
		$('#matchTypedivid').addClass('hidden').hide();
	}
	else if(($('#ruleCategoryid').val() =='Orphan'))
	{
		$('#matchexprdivid').removeClass('hidden').show();
		$('#rightdatasourceid2').removeClass('hidden').show();
		$('#rightdatasourceid').removeClass('hidden').show();
		$('#ruleexprdivid').addClass('hidden').hide();
		//$("#matchexprid").attr("placeholder", "column1=column2").val("").focus().blur();
		$("#matchexprid").attr("placeholder", "column1=column2");
		$('#matchTypedivid').addClass('hidden').hide();
	}
	else  if(($('#ruleCategoryid').val() =='Regular Expression')){
		$('#ruleexprdivid').removeClass('hidden').show();
		$('#rightdatasourceid').addClass('hidden').hide();
		$('#rightdatasourceid2').addClass('hidden').hide();
		$('#matchexprdivid').addClass('hidden').hide();
		$('#matchTypedivid').removeClass('hidden').show();
	}

	if ( (sIgnoreGlobalEvent !== undefined) && (ManageExpressionTexts !== undefined) ) {
		ManageExpressionTexts.HideOrShowExprRadioButtons($('#ruleCategoryid').val());
	}

});

$(document).ready(function() {
    $("input[type='text']").keypress(function(e) {
        if (e.keyCode == '13') {
            e.preventDefault();
        }
    });
    var global_no_date_error = 1;
    var globalcolid='';

    $('#columntable').on('click', '.bs_confirmation_demo_2', function(event) {
      var self = $(this);
      setTimeout(
        function(ev) {
          $("[data-toggle=confirmation]").confirmation({container:"body",btnOkClass:"btn btn-sm btn-success",btnCancelClass:"btn btn-sm btn-danger",onConfirm:function(event) { self.trigger('Confirm') ;}});
        }, 500);
    });

    $('#columntable').on('confirmed.bs.confirmation', '.bs_confirmation_demo_2', function(e) {

        var selectedId = $(this).attr('class');
        var str = selectedId.split(" ");
        var matchid =  str[1];
        var url;

        if ( $(this).hasClass('subclassdel') )
        {
         url = $(this).closest('.portlet-body').find('.tourl3').val();
         matchid =  str[2];
        }
        else
        {
            url = $(this).closest('.portlet-body').find('.tourl2').val();
        }


        var form_data = {
                id : matchid,
                appid : $("#appid").val(),
                times: new Date().getTime()
            };
             // console.log(form_data); console.log(url);return false;
            $.ajax({
                url: url,
                type: 'POST',
                headers: { 'token':$("#token").val()},
                datatype: 'json',
                data: form_data,
                success: function(message) {
                    var j_obj = $.parseJSON(message);
                    // console.log(j_obj);return false;
                    if (j_obj.hasOwnProperty('success')) {
                        toastr.info('Deleted successfully');
                            setTimeout(function(){
                           // window.location.href= j_obj.success;
                            window.location.reload();

                        },1000);
                    } else {
                        if (j_obj.hasOwnProperty('fail')) {
                            toastr.info(j_obj.fail);
                        }
                    }
                },
                error: function(xhr, textStatus, errorThrown)
                {
                    $('.validation-fail').removeClass('hidden').html("There seems to be a network problem. Please try again in some time.");
                }
            });

        return false;
    });


    $('#dataSourceid').change(function() {
        if ($('#dataSourceid').is(":checked")) {
            $('#dataSourceHideid').removeClass('hidden');
        }
        else
        {
            $('#dataSourceHideid').addClass('hidden');
        }
    });

    $('#dataBindid').change(function() {
        if ($('#dataBindid').is(":checked")) {
            $('#dataBindHideid').removeClass('hidden');
        }
        else
        {
            $('#dataBindHideid').hide();
        }
    });

    $('#scheduleid').change(function() {
        if ($('#scheduleid').is(":checked")) {
            $('#scheduleHideid').removeClass('hidden');
        }
        else
        {
            $('#scheduleHideid').addClass('hidden');
        }
    });

    $('#moduleid').change(function() {
        if ($('#moduleid').is(":checked")) {
            $('#moduleHideid').removeClass('hidden');
        }
        else
        {
            $('#moduleHideid').addClass('hidden');
        }
    });

    $('#userid').change(function() {
        if ($('#userid').is(":checked")) {
            $('#userHideid').removeClass('hidden');
        }
        else
        {
            $('#userHideid').addClass('hidden');
        }
    });

    $('#omethodid').change(function() {
        if ($('#omethodid').val() == 'moving average') {
            $('#avgrangecontainer').show();
        }
        else
        {
            $('#avgrangecontainer').hide();
        }
    });

    $('#submitgroupid').click(function()
    {

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


        if (no_error) {

            var dataSourceAll = '';
            var dataBindAll = '';
            var scheduleAll = '';
            var moduleAll = '';
            var userAll = '';

            // $(".accessControl").each(function() {
            //     var $this = $(this);
            //     if ($this.is(":checked")) {
            //         if (count) {
            //             dataaccessControlAll += ('-' + $this.val());
            //         } else {
            //             dataaccessControlAll = $this.val();
            //         }
            //         count++;
            //     }
            // });

            if ($('#dataSourceid').is(":checked")) {
                $('#dataSourceid').val();
            } else {
                $('#dataSourceid').val(0);
            }

            if ($('#dataBindid').is(":checked")) {
                $('#dataBindid').val();
            } else {
                $('#dataBindid').val(0);
            }

            if ($('#scheduleid').is(":checked")) {
                $('#scheduleid').val();
            } else {
                $('#scheduleid').val(0);
            }

            if ($('#moduleid').is(":checked")) {
                $('#moduleid').val();
            } else {
                $('#moduleid').val(0);
            }

            if ($('#userid').is(":checked")) {
                $('#userid').val();
            } else {
                $('#userid').val(0);
            }

            var count = 0;

            $(".dataSource").each(function() {
                var $this = $(this);
                if ($this.is(":checked")) {
                    if (count) {
                        dataSourceAll += ('-' + $this.val());
                    } else {
                        dataSourceAll = $this.val();
                    }
                    count++;
                }
            });

            var count = 0;

            $(".dataBind").each(function() {
                var $this = $(this);
                if ($this.is(":checked")) {
                    if (count) {
                        dataBindAll += ('-' + $this.val());
                    } else {
                        dataBindAll = $this.val();
                    }
                    count++;
                }
            });

            var count = 0;

            $(".schedule").each(function() {
                var $this = $(this);
                if ($this.is(":checked")) {
                    if (count) {
                        scheduleAll += ('-' + $this.val());
                    } else {
                        scheduleAll = $this.val();
                    }
                    count++;
                }
            });

            var count = 0;

            $(".module").each(function() {
                var $this = $(this);
                if ($this.is(":checked")) {
                    if (count) {
                        moduleAll += ('-' + $this.val());
                    } else {
                        moduleAll = $this.val();
                    }
                    count++;
                }
            });

            var count = 0;

            $(".user").each(function() {
                var $this = $(this);
                if ($this.is(":checked")) {
                    if (count) {
                        userAll += ('-' + $this.val());
                    } else {
                        userAll = $this.val();
                    }
                    count++;
                }
            });

            var form_data = {
                groupName: $("#groupNameid").val(),
                description: $("#descriptionid").val(),
                dataaccessControlAll: dataaccessControlAll,
                dataSourceAll: dataSourceAll,
                dataBindAll: dataBindAll,
                scheduleAll: scheduleAll,
                moduleAll: moduleAll,
                userAll: userAll,
                times: new Date().getTime()
            };
            // console.log(form_data); return false;

            if ($('#editid').length > 0) {
                form_data.editMode = 'yes';
                form_data.idGroup = $('#idGroup').val();
            }

            $.ajax({
                url: $("#tourl").val(),
                type: 'POST',
                headers: { 'token':$("#token").val()},
                datatype: 'json',
                data: form_data,
                success: function(message) {
                    var j_obj = $.parseJSON(message);
                    // console.log(j_obj);return false;
                    if (j_obj.hasOwnProperty('success')) {
                        $('.init').hide();
                        $('.note-info').removeClass('hidden');
                    } else {
                        if (j_obj.hasOwnProperty('fail')) {
                            $('.note-danger').removeClass('hidden').html(j_obj.fail);
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

    // remove error messages on input text click
    $('.form-control').click(function() {
        $(this).closest('.col-capture').find('.input_error').remove();
    });


    $('#createUserId').click(function() {
        var no_error = 1;
        $('.required').html('');

        if ($('#firstNameId').val().length == 0) {
            $('<div class="input_error" style="font-size:12px;color:red;">Please enter First Name</div>').insertAfter($('#firstNameId'));
            no_error = 0;
        }
        if ($('#lastNameId').val().length == 0) {
            $('<div class="input_error" style="font-size:12px;color:red;">Please enter Last Name</div>').insertAfter($('#lastNameId'));
            no_error = 0;
        }
        if ($('#emailId').val().length == 0) {
            $('<div class="input_error" style="font-size:12px;color:red;">Please enter Email</div>').insertAfter($('#emailId'));
            no_error = 0;
        }
        if ($('#passwordId').val().length == 0) {
            $('<div class="input_error" style="font-size:12px;color:red;">Please enter Password</div>').insertAfter($('#passwordId'));
            no_error = 0;
        }
        if ($('#confirmPasswordId').val().length == 0) {
            $('<div class="input_error" style="font-size:12px;color:red;">Please confirm Password</div>').insertAfter($('#confirmPasswordId'));
            no_error = 0;
        }
        if ($('#companyId').val().length == 0) {
            $('<div class="input_error" style="font-size:12px;color:red;">Please enter Company</div>').insertAfter($('#companyId'));
            no_error = 0;
        }

        if (no_error) {
            var form_data = {
                firstName: $("#firstNameId").val(),
                lastName: $("#lastNameId").val(),
                email: $("#emailId").val(),
                password: $("#passwordId").val(),
                confirmPassword: $("#confirmPasswordId").val(),
                company: $("#companyId").val(),
                department: $("#departmentId").val(),
                times: new Date().getTime()
            };

            var userRole = '';
            $(".accessControlCheckbox").each(function() {
                if ($('#' + $(this).attr('id')).is(':checked')) {
                    userRole += ($('#' + $(this).attr('id')).val() + '-') ;
                }
            });
            form_data.userRole = userRole;

            $.ajax({
                url: $("#toUrl").val(),
                type: 'POST',
                headers: { 'token':$("#token").val()},
                datatype: 'json',
                data: form_data,
                success: function(message) {
                    var j_obj = $.parseJSON(message);
                    // console.log(j_obj);return false;
                    if (j_obj.hasOwnProperty('success')) {
                        $('.init').hide();
                        $('.note-info').removeClass('hidden');
                    } else {
                        if (j_obj.hasOwnProperty('fail')) {
                            $('.validation-fail').removeClass('hidden').html(j_obj.fail);
                        }
                    }
                },
                error: function(xhr, textStatus, errorThrown)
                {
                    $('.validation-fail').removeClass('hidden').html("There seems to be a network problem. Please try again in some time.");
                }
            });
        }
        return false;
    });

    $('#updateUserId').click(function() {
        var no_error = 1;
        $('.required').html('');

        if ($('#firstNameId').val().length == 0) {
            $('<div class="input_error" style="font-size:12px;color:red;">Please enter First Name</div>').insertAfter($('#firstNameId'));
            no_error = 0;
        }
        if ($('#lastNameId').val().length == 0) {
            $('<div class="input_error" style="font-size:12px;color:red;">Please enter Last Name</div>').insertAfter($('#lastNameId'));
            no_error = 0;
        }
        if ($('#companyId').val().length == 0) {
            $('<div class="input_error" style="font-size:12px;color:red;">Please enter Company</div>').insertAfter($('#companyId'));
            no_error = 0;
        }

        if (no_error) {
            var form_data = {
                editMode: 'yes',
                idUser: $("#idUserId").val(),
                firstName: $("#firstNameId").val(),
                lastName: $("#lastNameId").val(),
                company: $("#companyId").val(),
                department: $("#departmentId").val(),
                times: new Date().getTime()
            };

            var userRole = '';
            $(".accessControlCheckbox").each(function() {
                if ($('#' + $(this).attr('id')).is(':checked')) {
                    userRole += ($('#' + $(this).attr('id')).val() + '-') ;
                }
            });
            form_data.userRole = userRole;

            // console.log(form_data); return false;

            $.ajax({
                url: $("#toUrl").val(),
                type: 'POST',
                headers: { 'token':$("#token").val()},
                datatype: 'json',
                data: form_data,
                success: function(message) {
                    var j_obj = $.parseJSON(message);
                    // console.log(j_obj);return false;
                    if (j_obj.hasOwnProperty('success')) {
                        $('.init').hide();
                        $('.note-info').removeClass('hidden');
                    } else {
                        if (j_obj.hasOwnProperty('fail')) {
                            $('.validation-fail').removeClass('hidden').html(j_obj.fail);
                        }
                    }
                },
                error: function(xhr, textStatus, errorThrown)
                {
                    $('.validation-fail').removeClass('hidden').html("There seems to be a network problem. Please try again in some time.");
                }
            });
        }
        return false;
    });

    $('#deleteUserId').click(function()
    {
        var form_data = {
                idUser : $("#idUserId").val(),
                times: new Date().getTime()
            };
            // console.log(form_data); return false;
            $.ajax({
                url: $("#toUrl").val(),
                type: 'POST',
                headers: { 'token':$("#token").val()},
                datatype: 'json',
                data: form_data,
                success: function(message) {
                    var j_obj = $.parseJSON(message);
                    // console.log(j_obj);return false;
                    if (j_obj.hasOwnProperty('success')) {
                        $('.init').hide();
                        $('.note-info').removeClass('hidden');
                    } else {
                        if (j_obj.hasOwnProperty('fail')) {
                            $('.validation-fail').removeClass('hidden').html(j_obj.fail);
                        }
                    }
                },
                error: function(xhr, textStatus, errorThrown)
                {
                    $('.validation-fail').removeClass('hidden').html("There seems to be a network problem. Please try again in some time.");
                }
            });

        return false;
    });

    $('#deleteTaskId').click(function()
    {
        var form_data = {
                idTask : $("#idTaskId").val(),
                times: new Date().getTime()
            };
            // console.log(form_data); return false;
            $.ajax({
                url: $("#toUrl").val(),
                type: 'POST',
                headers: { 'token':$("#token").val()},
                datatype: 'json',
                data: form_data,
                success: function(message) {
                    var j_obj = $.parseJSON(message);
                    // console.log(j_obj);return false;
                    if (j_obj.hasOwnProperty('success')) {
                        $('.init').hide();
                        $('.note-info').removeClass('hidden');
                    } else {
                        if (j_obj.hasOwnProperty('fail')) {
                            $('.validation-fail').removeClass('hidden').html(j_obj.fail);
                        }
                    }
                },
                error: function(xhr, textStatus, errorThrown)
                {
                    $('.validation-fail').removeClass('hidden').html("There seems to be a network problem. Please try again in some time.");
                }
            });

        return false;
    });



    $('#deleteRoleId').click(function()
    {
        var form_data = {
                idRole : $("#idRoleId").val(),
                times: new Date().getTime()
            };
            // console.log(form_data); return false;
            $.ajax({
                url: $("#toUrl").val(),
                type: 'POST',
                headers: { 'token':$("#token").val()},
                datatype: 'json',
                data: form_data,
                success: function(message) {
                    var j_obj = $.parseJSON(message);
                    // console.log(j_obj);return false;
                    if (j_obj.hasOwnProperty('success')) {
                        $('.init').hide();
                        $('.note-info').removeClass('hidden');
                    } else {
                        if (j_obj.hasOwnProperty('fail')) {
                            $('.validation-fail').removeClass('hidden').html(j_obj.fail);
                        }
                    }
                },
                error: function(xhr, textStatus, errorThrown)
                {
                    $('.validation-fail').removeClass('hidden').html("There seems to be a network problem. Please try again in some time.");
                }
            });

        return false;
    });

    // $('#edititemid').click(function()
    // {

    //     var no_error = 1;

    //     if(no_error)
    //     {

    //         var form_data = {
    //             itemid: $("#itemid").val(),
    //             item1: $("#itemid1").val(),
    //             item2: $("#itemid2").val(),
    //             item3: $("#itemid3").val(),
    //             item4: $("#itemid4").val(),
    //             item5: $("#itemid5").val(),
    //             times: new Date().getTime()
    //         };
    //         // console.log(form_data); return false;


    //         $.ajax({
    //             url: $("#tourl").val(),
    //             type: 'POST',
    //             datatype: 'json',
    //             data: form_data,
    //             success: function(message) {
    //                 var j_obj = $.parseJSON(message);
    //                 // console.log(j_obj);return false;
    //                 if (j_obj.hasOwnProperty('success')) {
    //                     $('.init').hide();
    //                     $('.note-info').removeClass('hidden');
    //                 } else {
    //                     if (j_obj.hasOwnProperty('fail')) {
    //                         $('.note-danger').removeClass('hidden').html(j_obj.fail);
    //                     }
    //                 }
    //             },
    //             error: function(xhr, textStatus, errorThrown)
    //             {
    //                 $('.catch-form-msg').find('.help-block').html("There seems to be a network problem. Please try again in some time.");
    //             }
    //         });
    //     }
    //     return false;
    // });

    $('#deleteitemid').click(function()
    {

        var targeturl = $("#targeturl").val();
        var form_data = {
                id : $("#itemid").val(),
                times: new Date().getTime()
            };
            // console.log(form_data); return false;
            $.ajax({
                url: $("#tourl").val(),
                type: 'POST',
                headers: { 'token':$("#token").val()},
                datatype: 'json',
                data: form_data,
                success: function(message) {
                    var j_obj = $.parseJSON(message);
                    // console.log(j_obj);return false;
                    if (j_obj.hasOwnProperty('success')) {
                        // $('.init').hide();
                        // $('.note-info').removeClass('hidden');
                        toastr.info('Item Deleted successfully');
                        setTimeout(function(){
                            window.location.href= targeturl;

                        },1000);
                    } else {
                        if (j_obj.hasOwnProperty('fail')) {
                            $('.note-danger').removeClass('hidden').html(j_obj.fail);
                        }
                    }
                },
                error: function(xhr, textStatus, errorThrown)
                {
                    $('.catch-form-msg').find('.help-block').html("There seems to be a network problem. Please try again in some time.");
                }
            });

        return false;
    });

    $('#deleteblendid').click(function()
    {

        var targeturl = $("#targeturl").val();
        var form_data = {
        		idDataBlend : $("#blendid").val(),
               // times: new Date().getTime()
            };
             console.log(form_data); //return false;
            $.ajax({
                url: './deleteTempId',
                type: 'POST',
                headers: { 'token':$("#token").val()},
                datatype: 'json',
                data: form_data,
                success: function(data) {
                    if (data!="") {
                        toastr.info('Blend Deleted successfully');
                        setTimeout(function(){
                        	 window.location.href= 'extendTemplateView';
                        },1000);
                    } else {
                        toastr.info('There was a problem.');
                    }
                },
                error: function(xhr, textStatus, errorThrown)
                {
                    $('.catch-form-msg').find('.help-block').html("There seems to be a network problem. Please try again in some time.");
                }
            });

        return false;
    });

    $('#submittaskid').click(function()
    {
        var no_error = 1;
        $('.required').html('');

        if($('#taskNameId').val().length == 0)
        {
            $('<div class="input_error" style="font-size:12px;color:red;">Please enter Task Name</div>').insertAfter($('.required'));
            no_error = 0;
        }

        if(no_error)
        {
            var form_data = {
                taskName: $("#taskNameId").val(),
                times: new Date().getTime()
            };
            if ($('#editTaskFormId').length > 0) {
                form_data.editMode = 'yes';
                form_data.idTask = $('#editTaskId').val();
            }
            $.ajax({
                url: $("#toUrl").val(),
                type: 'POST',
                headers: { 'token':$("#token").val()},
                datatype: 'json',
                data: form_data,
                success: function(message) {
                    var j_obj = $.parseJSON(message);
                    // console.log(j_obj);return false;
                    if (j_obj.hasOwnProperty('success')) {
                        $('.init').hide();
                        $('.note-info').removeClass('hidden');
                    } else {
                        if (j_obj.hasOwnProperty('fail')) {
                            $('.note-danger').removeClass('hidden').html(j_obj.fail);
                        }
                    }
                },
                error: function(xhr, textStatus, errorThrown)
                {
                    $('.note-danger').removeClass('hidden').html("There seems to be a network problem. Please try again in some time.");
                }
            });
        }
        return false;
    });

    // to show/hide the CRUD values for each task
    $(".accessControl").click(function(){
        var selectedId = '#' + ($(this).attr('id'));
        if( $(selectedId).is(":checked") ) {
        	//alert(selectedId);
            // var id = selectedId.replace(/#roleId/, '');
            // console.log(selectedId);
            // return false;
            $(selectedId + "Check").removeClass('hidden');
        }
        else
        {
            $(selectedId + "Check").addClass('hidden');
        }
    });

    $('#submitRoleId').click(function()
    {
    	 var amit1 = $('#amit').html();
        var no_error = 1;
        
        if(!checkInputText()){ 
			no_error = 0;
			alert('Vulnerability in submitted data, not allowed to submit!');
		}
        
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
            // console.log(accessControlAll); return false;
            var form_data = {
                roleName: $("#roleNameId").val(),
                description: $("#descriptionId").val(),
                taskDataAll: taskDataAll,
                accessControlAll: accessControlAll
                //times: new Date().getTime()
            };
            //alert("taskDataAll="+taskDataAll);
           // alert("accessControlAll="+accessControlAll);
            if ($('#editmodeid').length > 0) {
                form_data.editMode = 'yes';
                form_data.idRole = $('#idRoleId').val();
            }

             //console.log(form_data); return false;

            $.ajax({
                url: $("#toUrl").val(),
                type: 'POST',
                headers: { 'token':$("#token").val()},
                datatype: 'json',
                data: form_data,
                success: function(message) {
                    var j_obj = $.parseJSON(message);
                    if (j_obj.hasOwnProperty('success')) {
                    	toastr.info("New Role Added Successfully");
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

    // Users Table
    if( $('#showDataTable').length > 0 ) {
      $('#showDataTable').DataTable();
    }
    if( $('#showAltDataTable').length > 0 ) {
      $('#showAltDataTable').DataTable();
    }
     //Horizontal scroll dataTable for large data sets
    if ($('.showScrollXDataTable').length > 0) {
        $('.showScrollXDataTable').DataTable({
          "scrollX": true
        });
    }

    if ($('table.display').length > 0) {
        $('table.display').DataTable({responsive: true
   // details:  { confirmation: $.fn.dataTable.Responsive.display.modal( { }) }
        });
    }

    /** Data Source functions start */
    $('#edititemid').click(function()
    {

        var no_error = 1;
        var kbe = $("#KBEid").prop("checked") ? 'Y': 'N';
        var dgrp = $("#dgroupid").prop("checked") ? 'Y': 'N';
        var dkey = $("#dupkeyid").prop("checked") ? 'Y': 'N';
        var targeturl = $("#targeturl").val();

        if(no_error)
        {

            var form_data = {
                itemid: $("#itemid").val(),
                columnName: $("#columnNameid").val(),
                displayName: $("#displayNameid").val(),
                format: $("#formatid").val(),
                //KBE: $("#KBEid").val(),
                KBE: kbe,
                dgroup: dgrp,
                dupkey: dkey ,
                times: new Date().getTime()
            };

            $.ajax({
                url: $("#tourl").val(),
                type: 'POST',
                headers: { 'token':$("#token").val()},
                datatype: 'json',
                data: form_data,
                success: function(message) {
                    var j_obj = $.parseJSON(message);
                    // console.log(j_obj);return false;
                    if (j_obj.hasOwnProperty('success')) {
                        //$('.init').hide();
                       // $('.note-info').removeClass('hidden');
                        toastr.info('Item updated successfully');
                        setTimeout(function(){
                            window.location.href= targeturl;

                        },1000);


                    } else {
                        if (j_obj.hasOwnProperty('fail')) {
                            $('.note-danger').removeClass('hidden').html(j_obj.fail);
                        }
                    }
                },
                error: function(xhr, textStatus, errorThrown)
                {
                    $('.catch-form-msg').find('.help-block').html("There seems to be a network problem. Please try again in some time.");
                }
            });
        }
        return false;
    });

    // $('#deleteitemid').click(function()
    // {

    //     var form_data = {
    //             id : $("#itemid").val(),
    //             times: new Date().getTime()
    //         };
    //         // console.log(form_data); return false;
    //         $.ajax({
    //             url: $("#tourl").val(),
    //             type: 'POST',
    //             datatype: 'json',
    //             data: form_data,
    //             success: function(message) {
    //                 var j_obj = $.parseJSON(message);
    //                 // console.log(j_obj);return false;
    //                 if (j_obj.hasOwnProperty('success')) {
    //                     $('.init').hide();
    //                     $('.note-info').removeClass('hidden');
    //                 } else {
    //                     if (j_obj.hasOwnProperty('fail')) {
    //                         $('.note-danger').removeClass('hidden').html(j_obj.fail);
    //                     }
    //                 }
    //             },
    //             error: function(xhr, textStatus, errorThrown)
    //             {
    //                 $('.catch-form-msg').find('.help-block').html("There seems to be a network problem. Please try again in some time.");
    //             }
    //         });

    //     return false;
    // });

    $('#deletesourceid').click(function()
    {

        var form_data = {
                id : $("#sourceid").val(),
                times: new Date().getTime()
            };
            // console.log(form_data); return false;
            $.ajax({
                url: $("#tourl").val(),
                type: 'POST',
                headers: { 'token':$("#token").val()},
                datatype: 'json',
                data: form_data,
                success: function(message) {
                    var j_obj = $.parseJSON(message);
                    // console.log(j_obj);return false;
                    if (j_obj.hasOwnProperty('success')) {
                        $('.init').hide();
                        $('.note-info').removeClass('hidden');
                    } else {
                        if (j_obj.hasOwnProperty('fail')) {
                            $('.note-danger').removeClass('hidden').html(j_obj.fail);
                        }
                    }
                },
                error: function(xhr, textStatus, errorThrown)
                {
                    $('.catch-form-msg').find('.help-block').html("There seems to be a network problem. Please try again in some time.");
                }
            });

        return false;
    });

    $("#sourceid").change(function()
    {

        if($("#sourceid").val() === 'mysql')
        {
            //load new form for getting mysql data
            //document.location.href= $("#mysqlurl").val();
            $("#csv_id").hide();
            $("#mqsql_form_id").show();
        }
        else
        {
            $("#csv_id").show();
            $("#mqsql_form_id").hide();
        }

    });

    /** Data Source functions end */

    $('#usrpswdsubmitbtnid').click(function(){
        var no_error = 1;
        $('.input_error').remove();
        
        if(!checkInputText()){ 
        	no_error = 0;
        	alert('Vulnerability in submitted data, not allowed to submit!');
		}

        if($("#oldpasswordid").val().length == 0){
            $('<span class="input_error" style="font-size:12px;color:red">Please enter current password</span>').insertAfter($('#oldpasswordid'));
            no_error = 0;
        }
        if($("#newpasswordid").val().length == 0){
            $('<span class="input_error" style="font-size:12px;color:red">Please enter new password</span>').insertAfter($('#newpasswordid'));
            no_error = 0;
        }
        if($("#newpasswordid1").val().length == 0){
            $('<span class="input_error" style="font-size:12px;color:red">Please re-enter new password</span>').insertAfter($('#newpasswordid1'));
            no_error = 0;
        }
        if($("#newpasswordid").val()!= $("#newpasswordid1").val())
        {
            $('<span class="input_error" style="font-size:12px;color:red">Password fields do not match. Please enter again.</span>').insertAfter($('#newpasswordid1'));
            no_error = 0;
        }

        if(no_error){
        	var changepassword = {
                    oldpassword : $("#oldpasswordid").val(),
                    newpassword : $("#newpasswordid").val(),

                    //times: new Date().getTime()
                };

                 console.log(changepassword);
                 //return false;

                $.ajax({
                  url: './sendnewpassword',
                  type: 'POST',
                  headers: { 'token':$("#token").val()},
                  datatype:'json',
                  data: JSON.stringify(changepassword),
                  contentType:"application/json",
                  success: function(data) {
                  	//alert(data);
                      if (data!="") {
                    	 toastr.info(data);
                         // toastr.info('Item updated successfully');
                          setTimeout(function(){
                              window.location.reload();
                          },1000);
                      } else {
                          toastr.info('There was a problem.');
                      }
                  },
                  error: function(xhr, textStatus, errorThrown){
                	 toastr.info("error while updating");
                        $('#initial').hide();
                        $('#fail').show();
                  }
               });
            }
            return false;
        });


    // $('#locationid').change(function(){
    //     if( $('#locationid').val() =='File System' )
    //     {
    //        $('#sourcedivid').show();
    //        $('#uploaddivid').show();

    //         $('#s3_form_id').hide();
    //         $('#hdfs_form_id').hide();
    //         $('#mqsql_form_id').hide();
    //         $('#ag_form_id').hide();
    //         $('#mos_form_id').hide();
    //     }
    //     else
    //     {
    //         $('#sourcedivid').hide();
    //         $("#csv_id").hide();

    //         if( $('#locationid').val() =='HDFS' )
    //         {
    //              $('#hdfs_form_id').show();
    //              $('#s3_form_id').hide();
    //              $('#mqsql_form_id').hide();
    //              $('#ag_form_id').hide();
    //              $('#mos_form_id').hide();
    //         }
    //         if( $('#locationid').val() =='S3' )
    //         {
    //              $('#s3_form_id').show();
    //              $('#hdfs_form_id').hide();
    //              $('#mqsql_form_id').hide();
    //              $('#ag_form_id').hide();
    //              $('#mos_form_id').hide();
    //         }
    //         if( ( $('#locationid').val() =='Google' ) || ( $('#locationid').val() =='Azure' )  )
    //         {
    //              $('#ag_form_id').show();
    //              $('#s3_form_id').hide();
    //              $('#hdfs_form_id').hide();
    //              $('#mqsql_form_id').hide();
    //              $('#mos_form_id').hide();

    //         }
    //         if( ( $('#locationid').val() =='Cassandra' ) || ( $('#locationid').val() =='MongoDB' )  )
    //         {
    //             $('#mos_form_id').show();
    //             $('#ag_form_id').hide();
    //             $('#s3_form_id').hide();
    //             $('#hdfs_form_id').hide();
    //             $('#mqsql_form_id').hide();

    //         }
    //     }
    // });

        // if( $("#sourceid").val() == 'mysql')
        // {
        //     if($("#hostid").val().length == 0){
        //     $('<span class="input_error" style="font-size:12px;color:red">Please enter Host Name</span>').insertAfter($('#hostid'));
        //     no_error = 0;
        //     }
        //     if($("#portid").val().length == 0){
        //     $('<span class="input_error" style="font-size:12px;color:red">Please enter Port Number</span>').insertAfter($('#portid'));
        //     no_error = 0;
        //     }
        //     if($("#userid").val().length == 0){
        //     $('<span class="input_error" style="font-size:12px;color:red">Please enter User Name</span>').insertAfter($('#userid'));
        //     no_error = 0;
        //     }
        //     if($("#pwdid").val().length == 0){
        //     $('<span class="input_error" style="font-size:12px;color:red">Please enter Password</span>').insertAfter($('#pwdid'));
        //     no_error = 0;
        //     }
        //     if($("#dbid").val().length == 0){
        //     $('<span class="input_error" style="font-size:12px;color:red">Please enter Database Name</span>').insertAfter($('#dbid'));
        //     no_error = 0;
        //     }
        //     if($("#tableid").val().length == 0){
        //     $('<span class="input_error" style="font-size:12px;color:red">Please enter Table Name</span>').insertAfter($('#tableid'));
        //     no_error = 0;
        //     }
        // }
        // if( $('#locationid').val() =='HDFS' )
        // {
        //     if($("#hosturiid").val().length == 0){
        //     $('<span class="input_error" style="font-size:12px;color:red">Please enter Host URI</span>').insertAfter($('#hosturiid'));
        //     no_error = 0;
        //     }
        //     if($("#folderid").val().length == 0){
        //     $('<span class="input_error" style="font-size:12px;color:red">Please enter Folder Name</span>').insertAfter($('#folderid'));
        //     no_error = 0;
        //     }
        //     if($("#hdfsloginid").val().length == 0){
        //     $('<span class="input_error" style="font-size:12px;color:red">Please enter Login Details</span>').insertAfter($('#hdfsloginid'));
        //     no_error = 0;
        //     }
        //     if($("#hdfspwdid").val().length == 0){
        //     $('<span class="input_error" style="font-size:12px;color:red">Please enter Password</span>').insertAfter($('#hdfspwdid'));
        //     no_error = 0;
        //     }
        // }
        // if( $('#locationid').val() =='S3' )
        // {
        //     if($("#s3bucketid").val().length == 0){
        //     $('<span class="input_error" style="font-size:12px;color:red">Please enter Bucket Name</span>').insertAfter($('#s3bucketid'));
        //     no_error = 0;
        //     }
        //     if($("#s3accesskeyid").val().length == 0){
        //     $('<span class="input_error" style="font-size:12px;color:red">Please enter Access Key</span>').insertAfter($('#s3accesskeyid'));
        //     no_error = 0;
        //     }
        //     if($("#s3pwdid").val().length == 0){
        //     $('<span class="input_error" style="font-size:12px;color:red">Please enter Secret Access Key</span>').insertAfter($('#s3pwdid'));
        //     no_error = 0;
        //     }
        // }
        // if( ( $('#locationid').val() =='Google' )  || ( $('#locationid').val() =='Azure' ) )
        // {
        //     if($("#aguriid").val().length == 0){
        //     $('<span class="input_error" style="font-size:12px;color:red">Please enter URI</span>').insertAfter($('#aguriid'));
        //     no_error = 0;
        //     }
        //     if($("#agfolderid").val().length == 0){
        //     $('<span class="input_error" style="font-size:12px;color:red">Please enter Folder Name</span>').insertAfter($('#agfolderid'));
        //     no_error = 0;
        //     }
        //     if($("#agloginid").val().length == 0){
        //     $('<span class="input_error" style="font-size:12px;color:red">Please enter Access Name</span>').insertAfter($('#agloginid'));
        //     no_error = 0;
        //     }
        //     if($("#agspwdid").val().length == 0){
        //     $('<span class="input_error" style="font-size:12px;color:red">Please enter Account Key</span>').insertAfter($('#agspwdid'));
        //     no_error = 0;
        //     }
        // }
        // if( ( $('#locationid').val() =='Cassandra' )  || ( $('#locationid').val() =='MongoDB' ) )
        // {
        //     if($("#moshosturiid").val().length == 0){
        //     $('<span class="input_error" style="font-size:12px;color:red">Please enter URI</span>').insertAfter($('#moshosturiid'));
        //     no_error = 0;
        //     }
        //     if($("#mosloginid").val().length == 0){
        //     $('<span class="input_error" style="font-size:12px;color:red">Please enter User Name</span>').insertAfter($('#mosloginid'));
        //     no_error = 0;
        //     }
        //     if($("#mospwdid").val().length == 0){
        //     $('<span class="input_error" style="font-size:12px;color:red">Please enter Password</span>').insertAfter($('#mospwdid'));
        //     no_error = 0;
        //     }
        //     if($("#mosdbid").val().length == 0){
        //     $('<span class="input_error" style="font-size:12px;color:red">Please enter Database Name</span>').insertAfter($('#mosdbid'));
        //     no_error = 0;
        //     }
        //     if($("#moscolid").val().length == 0){
        //     $('<span class="input_error" style="font-size:12px;color:red">Please enter Collection</span>').insertAfter($('#moscolid'));
        //     no_error = 0;
        //     }
        // }


    // $('#blendcolumnid').change(function()
    // {
    //     var column_temp = $('#blendcolumnid').val()
    //     var formula_val_temp = $("#formulaid").val();
    //     var str_len = formula_val_temp.length;
    //     var letters = /^[A-Za-z]+$/;
    //     if( str_len!=0)
    //     {
    //         var temp = formula_val_temp.substr(str_len-1,str_len);
    //         if( temp.match(letters) || temp==')')
    //         {
    //             alert('PLease select operator before proceeding');
    //         }
    //         else
    //         {
    //             formula_val_temp = formula_val_temp.concat(column_temp);
    //             $("#formulaid").val(formula_val_temp);
    //         }
    //     }
    //     else
    //     {
    //         formula_val_temp = formula_val_temp.concat(column_temp);
    //             $("#formulaid").val(formula_val_temp);
    //     }

    //     // {
    //     //     formula_temp = $("#formulaid").val();
    //     //     $("#formulaid").val() = formula_temp.concat($("#formulaid").val());
    //     // }

    // });

    // $('#blendoperid').change(function()
    // {
    //     var operator_temp = $('#blendoperid').val()
    //     var formula_val_temp = $("#formulaid").val();
    //     var str_len = formula_val_temp.length;
    //     var operators = /([-,*+=<>!&\/])/;
    //     if( str_len!=0)
    //     {
    //         var temp = formula_val_temp.substr(str_len-1,str_len);
    //         if( temp.match(operators) || temp=='(')
    //         {
    //             alert('PLease select column before proceeding');
    //         }
    //         else
    //         {
    //             formula_val_temp = formula_val_temp.concat(operator_temp);
    //             $("#formulaid").val(formula_val_temp);
    //         }
    //     }
    //     else
    //     {
    //          alert('PLease select column before selecting an operator');
    //     }

    // });

    $('#blendfunid').change(function()
    {
        var column_temp = $('#blendfunid').val()
        var formula_val_temp = $("#formulaid").val();
        var str_len = formula_val_temp.length;
        var letters = /^[A-Za-z]+$/;
        if( str_len!=0)
        {
            var temp = formula_val_temp.substr(str_len-1,str_len);
            if( temp.match(letters) || temp==')' )
            {
                alert('PLease select operator before proceeding');
            }
            else
            {
                formula_val_temp = formula_val_temp.concat(column_temp);
                $("#formulaid").val(formula_val_temp);
            }
        }
        else
        {
            formula_val_temp = formula_val_temp.concat(column_temp);
                $("#formulaid").val(formula_val_temp);
        }

        // {
        //     formula_temp = $("#formulaid").val();
        //     $("#formulaid").val() = formula_temp.concat($("#formulaid").val());
        // }

    });

    $('#blendcreateid').click(function(){
        var no_error = 1;
        $('.input_error').remove();
        
        if(!checkInputText()){ 
			no_error = 0;
			alert('Vulnerability in submitted data, not allowed to submit!');
		}

        if($("#blendnameid").val().length == 0){
            $('<span class="input_error" style="font-size:12px;color:red">Please enter Extended Template Name</span>').insertAfter($('#blendnameid'));
            no_error = 0;
        }
        // if($("#formulaid").val().length == 0){
        //     $('<span class="input_error" style="font-size:12px;color:red">Please enter a formula to be saved</span>').insertAfter($('#formulaid'));
        //     no_error = 0;
        // }
        var targeturl = $("#targeturl").val();
        if(no_error){
            var form_data = {
                name : $("#blendnameid").val(),
                description : $("#blenddescid").val(),
                // formula : $("#formulaid").val(),
                sourceid : $("#datasourceid").val(),
                //times: new Date().getTime()
            };
            if($("#blendid").val()!=null)
            {
                form_data['blendid'] = $("#blendid").val();
            }

            console.log(form_data);
            console.log($("#tourl").val());

            $.ajax({
              url: './createExtendTemplate',
              type: 'POST',
              headers: { 'token':$("#token").val()},
              datatype:'json',
              data: form_data,
              success: function(data) {
                  if (data!="") {
                          toastr.info('Modified template created successfully');
                        setTimeout(function(){
                            window.location.href= 'extendTemplateView';

                        },1000);
                      }else {
                          toastr.info('There was a problem.');
                      }
                  },
              error: function(xhr, textStatus, errorThrown){
                    $('#initial').hide();
                    $('#fail').show();
              }
           });
        }
        return false;
    });
    $('#blendcreatecolumnid').click(function(){
        var no_error = 1;
        $('.input_error').remove();
        
        if(!checkInputText()){ 
			no_error = 0;
			alert('Vulnerability in submitted data, not allowed to submit!');
		}

        if( $("#blendtypeid").val() == 'Filter' )
        {
            if($("#filternameid").val().length == 0){
            $('<span class="input_error" style="font-size:12px;color:red">Please enter a name for the Filter</span>').insertAfter($('#filternameid'));
            no_error = 0;
            }
        }
        else if( $("#blendtypeid").val() == "Derived Column" )
        {
            if($("#formulanameid").val().length == 0){
            $('<span class="input_error" style="font-size:12px;color:red">Please enter a name for the Blend Column</span>').insertAfter($('#formulanameid'));
            no_error = 0;
            }
        }
       /* var dataToString = $('#bsourceid').selectpicker('val');
		  console.log("dataToString="+dataToString);
		  return false;*/
       /* else if( $("#blendtypeid").val() == "Row Add" )
        {
            //console.log($('#bsourceid').selectpicker('val'));
            if( ($('#bsourceid').selectpicker('val') ) == null ){
            $('<span class="input_error" style="font-size:12px;color:red">Please Select a Source</span>').insertAfter($('#bsourceid'));
            no_error = 0;
            }
        }*/
        if ( ($("#formulaid").val().length == 0) && ( $("#blendtypeid").val() != "Row Add" ) ){
            $('<span class="input_error" style="font-size:12px;color:red">Please enter a formula to be saved</span>').insertAfter($('#formulaid'));
            no_error = 0;
        }
        var abc="";
        if( $("#blendtypeid").val() == "Row Add" ){
        var targeturl = $("#bsourceid").val();
        var arrayLength = targeturl.length;

        for (var i = 0; i < arrayLength; i++) {
            //alert(targeturl[i]);
        	 abc=abc+targeturl[i]+",";
            //Do something
        }
        }
        if(no_error){
            var form_data = {
            		extensionType : $("#blendtypeid").val(),
            		derivedColumnName : $("#formulanameid").val(),
            		 columnCategory : $("#columnCategoryid").val(),
            		 columnValueType :$("#colValueTypeId").val(),
            		 listDataDefinitionIdCol:$( "#displayname" ).val(),
            		 expression : $("#formulaid").val(),
            		 columnValue : $("#colvalueid").val(),
            		 idDataBlend :  $("#blendid").val(),
            		 idData : $("#sourceid").val(),
            		 filterName : $("#filternameid").val(),
            		 name : $("#nameid").val(),
            		 description : $("#descriptionid").val(),
            		rowAdd : abc,
               // times: new Date().getTime()
            };
            /*if( $("#blendtypeid").val() == 'Filter' )
            {
                form_data['blendtype'] = 'Filter';
                form_data['formulaname'] = $("#filternameid").val();
            }
            else if( $("#blendtypeid").val() == 'Derived Column' )
            {
                form_data['blendtype'] = "Derived Column";
                form_data['formulaname'] = $("#formulanameid").val();
            }
            else if( $("#blendtypeid").val() == "Row Add" )
            {
                form_data['blendtype'] = "Row Add";
                var dataToString = $('#bsourceid').selectpicker('val');
                form_data['sourcedata'] = dataToString.toString();
            }*/
            console.log(form_data);
            $.ajax({
              url: './saveDataInListDataBlendTables',
              type: 'POST',
              headers: { 'token':$("#token").val()},
              datatype:'json',
              data: form_data,
              success: function(data) {
                  if (data!="") {
                      toastr.info('Modified Template Created Successfully');
                      setTimeout(function(){
                    	    window.location.href= 'extendTemplateView';
                      },1000);
                  } else {
                      toastr.info('There was a problem.');
                  }
              },
              error: function(xhr, textStatus, errorThrown)
              {
              	toastr.info('Item updated with error');
              	console.log(xhr+" "+textStatus+" "+errorThrown);
              }
           });
        }
        return false;
    });

  /*  function getViewPage(){
    	$.ajax({
            url: './tempview',
            type: 'GET',
            datatype:'json',
            success: function(data) {
                if (data!="") {
                	alert("error in success");
                    setTimeout(function(){
                        window.location.reload();
                    },1000);
                } else {
                    toastr.info('There was a problem.');
                }
            },
            error: function(xhr, textStatus, errorThrown)
            {
            	console.log(xhr+" "+textStatus+" "+errorThrown);
            }
         });*/

    $('#blendtypeid').change(function()
    {
        if ( $('#blendtypeid').val() == 'Filter')
        {
        	 $('#colvaluedivid').hide();
        	 $('#colValueTypeDivId').hide();
        	 $('#columnCategory').hide();
            $('#filter_input_id').show();
            $('#derived_input_id').hide();
            $('#filter_opr_id').show();
            $('#common_form_id').show();
            $('#add_row_id').show();
            $('#addColId').addClass('hidden').hide();
            $('#sourcecolumnid').show();
            $('#rowaddid').hide();
        }
        else if ( $('#blendtypeid').val() == 'Derived Column')
        {
        	 $('#colValueTypeDivId').show();
        	 $('#columnCategory').show();
            $('#filter_input_id').hide();
            $('#derived_input_id').show();
            $('#filter_opr_id').show();
            $('#common_form_id').show();
            $('#add_row_id').show();
            $('#sourcecolumnid').show();
            $('#rowaddid').hide();
        }
        else if ( $('#blendtypeid').val() == 'Row Add')
        {
        	  $('#rowaddid').removeClass('hidden').show();
        	 $('#colValueTypeDivId').hide();
        	 $('#columnCategory').hide();
            $('#filter_input_id').hide();
            $('#derived_input_id').hide();
            $('#filter_opr_id').hide();
            $('#common_form_id').hide();
            $('#add_row_id').hide();
            $('#colvaluedivid').hide();
            $('#addColId').addClass('hidden').hide();
            $('#sourcecolumnid').hide();
        }
    });
    $('#columnCategoryid').change(function()
    	    {
    	        if ( $('#columnCategoryid').val() == 'Simple')
    	        {
    	        	 $('#colvaluedivid').hide();
    	        	 $('#addColId').addClass('hidden').hide();
    	        }
    	        else if(($('#columnCategoryid').val() =='Complex') || ($('#columnCategoryid').val() =='ComplexEval'))
    	        {
    	        	 $('#colvaluedivid').removeClass('hidden').show();
    	        	 $('#addColId').removeClass('hidden').show();
    	        }
    	        else
    	        {
    	        	 $('#colvaluedivid').hide();
    	        }
    	    });

    $('.blendops').change(function()
    	    {
    	        var sIgnoreGlobalEvent = $(document.body).attr('data-ignore-global-event');

    	        if ( ($(this).val() != -1) && (sIgnoreGlobalEvent !== 'Y') )
    	        {
    	            var operator_temp = $(this).val();
    	            var formula_val_temp = $("#formulaid").val();

    	            formula_val_temp = formula_val_temp.concat(operator_temp);
    	            $("#formulaid").val(formula_val_temp);
    	        }
    	    });
    $('.secondops').change(function()
    	    {
    	        var sIgnoreGlobalEvent = $(document.body).attr('data-ignore-global-event');

    	        if ( ($(this).val() != -1) && (sIgnoreGlobalEvent !== 'Y') )
    	        {
    	            var operator_temp = $(this).val();
    	            var formula_val_temp = $("#matchexprid").val();

    	            formula_val_temp = formula_val_temp.concat(operator_temp);
    	            $("#matchexprid").val(formula_val_temp);
    	        }
    	    });
    $('#hmethodid').change(function(){
        if( $('#hmethodid').val() == 'Std Deviation')
        {
             $('#hdurationclassid').hide();
        }
        else
        {
            $('#hdurationclassid').show();
        }

    });
    $('#rmethodid').change(function(){
        if( $('#rmethodid').val() == 'Std Deviation')
        {
             $('#rdurationclassid').hide();
        }
        else
        {
            $('#rdurationclassid').show();
        }

    });

    $('#tsrcreateid').click(function(){
        var no_error = 1;
        $('.input_error').remove();

        var count = $("#countid").prop("checked") ? 'Y': 'N';
        var sum = $("#sumid").prop("checked") ? 'Y': 'N';
        var correlation = $("#correlationid").prop("checked") ? 'Y': 'N';
        var history = $("#hcid").prop("checked") ? 'Y': 'N';
        var stats = $("#spid").prop("checked") ? 'Y': 'N';
        var duplicateFile = $("#dfid").prop("checked") ? 'Y': 'N';

        if(no_error){
            var form_data = {
                appid : $("#appid").val(),
                count : count,
                sum :  sum,
                correlation : correlation,
                statisticalparam : stats,
                duplicatefile : duplicateFile,
                times: new Date().getTime()
            };
           if( $("#hcid").prop("checked") )
           {
            form_data['hcomparison_type'] = "History";
            form_data['hmethod'] = $("#hmethodid").val();
            form_data['hduration'] = $("#hdurationid").val();
            form_data['hthreshold'] = $("#hthresholdid").val();

           }
            if( $("#rtrid").prop("checked") )
           {
            form_data['rcomparison_type'] = "Run";
            form_data['rmethod'] = $("#rmethodid").val();
            form_data['rduration'] = $("#rdurationid").val();
            form_data['rthreshold'] = $("#rthresholdid").val();
           }

           console.log(form_data);
            $.ajax({
              url: $("#tourl").val(),
              type: 'POST',
              headers: { 'token':$("#token").val()},
              datatype:'json',
              data: form_data,
              success: function(message){
                  var j_obj = $.parseJSON(message);
                      if(j_obj.hasOwnProperty('success'))
                      {
                            toastr.info('Transaction set rule created successfully');
                            setTimeout(function(){
                            window.location.href= j_obj.success;

                        },1000);
                      }else if(j_obj.hasOwnProperty('fail') )
                      {
                          console.log(j_obj.fail);
                          toastr.info(j_obj.fail);
                      }
              },
              error: function(xhr, textStatus, errorThrown){
                    $('#initial').hide();
                    $('#fail').show();
              }
           });
        }
        return false;
    });

    $('#trcreateid').click(function(){
        var no_error = 1;
        $('.input_error').remove();

        var duprow = $("#duprowid").prop("checked") ? 'Y': 'N';
        var seqrow = $("#seqmid").prop("checked") ? 'Y': 'N';

        if(no_error){
            var form_data = {
                appid : $("#appid").val(),
                duprow : duprow,
                seqrow: seqrow,
                colid: $("#colid").val(),
                times: new Date().getTime()
            };

           console.log(form_data);
            $.ajax({
              url: $("#tourl").val(),
              type: 'POST',
              headers: { 'token':$("#token").val()},
              datatype:'json',
              data: form_data,
              success: function(message){
                  var j_obj = $.parseJSON(message);
                      if(j_obj.hasOwnProperty('success'))
                      {
                            toastr.info('Transaction rule created successfully');
                            setTimeout(function(){
                            window.location.href= j_obj.success;

                        },1000);
                      }else if(j_obj.hasOwnProperty('fail') )
                      {
                          console.log(j_obj.fail);
                          toastr.info(j_obj.fail);
                      }
              },
              error: function(xhr, textStatus, errorThrown){
                    $('#initial').hide();
                    $('#fail').show();
              }
           });
        }
        return false;
    });

    // list rule popup window
    $("#opid").change(function(){
        var formula_val_temp = $("#listruleid").val();
        var str_len = formula_val_temp.length;
        if( str_len!=0)
        {
            formula_val_temp =    formula_val_temp.concat(' ',$("#opid").val());
        }
        else
        {
            formula_val_temp =    formula_val_temp.concat($("#opid").val());
        }

        $("#listruleid").val(formula_val_temp);
    });
    $(".leftitem").change(function(){
        var formula_val_temp = $("#listruleid").val();
        var str_len = formula_val_temp.length;
        if( str_len!=0)
        {
            formula_val_temp =    formula_val_temp.concat(' ',$(".leftitem").val());
        }
        else
        {
            formula_val_temp =    formula_val_temp.concat($(".leftitem").val());
        }

        $("#listruleid").val(formula_val_temp);

    });
    $("#refitemid").change(function(){
        var formula_val_temp = $("#refruleid").val();
        var str_len = formula_val_temp.length;
        if( str_len!=0)
        {
            formula_val_temp =    formula_val_temp.concat(' ',$("#refitemid").val());
        }
        else
        {
            formula_val_temp =    formula_val_temp.concat($("#refitemid").val());
        }

        $("#refruleid").val(formula_val_temp);

    });

    $(".functionslist").change(function(){

        if( $(this).val() != -1 )
        {
            var formula_val_temp=' ';
            var formid = $(this).closest('.row').closest('.col-wrapper').find('span').html();
            if( formid == 'listrule' )
            {
                formula_val_temp = $("#listruleid").val();
            }
            else if( formid == 'refrule')
            {
                formula_val_temp = $("#refruleid").val();
            }
            else if( formid == 'leftsiderule')
            {
                formula_val_temp = $("#matchkeyfid").val();
            }
            else if( formid == 'customA')
            {
                formula_val_temp = $(".rulebox").val();
            }
            // if( formula_val_temp.length !=0 )
            // {
            //     formula_val_temp  = formula_val_temp.concat(' ', $(this).val(),'(');
            // }
            // else
            // {
            //     formula_val_temp  = formula_val_temp.concat($(this).val(),'(');
            // }
            formula_val_temp  = formula_val_temp.concat($(this).val(),'(');
            if( formid == 'listrule' )
            {
                $("#listruleid").val(formula_val_temp);
            }
            if( formid == 'refrule' )
            {
                $("#refruleid").val(formula_val_temp);
            }
            if( formid == 'leftsiderule' )
            {
                $("#matchkeyfid").val(formula_val_temp);
            }
            if( formid == 'customA' )
            {
                $(".rulebox").val(formula_val_temp);
            }
        }

    });

    $('#notesmodal').on('show.bs.modal', function(e) {
      var columnid = e.relatedTarget.dataset.columnid;
      var temp1 = e.relatedTarget.dataset.appidd;

      $("#columnid").val(columnid);
      $("#appid").val(temp1);

      if (globalcolid == columnid)
      {
        $("#statid").val('Y');
      }
      else
      {
        $("#statid").val('N');
      }
      $(".leftitem").empty();

    });

    $('#refmodal').on('show.bs.modal', function(e) {
      var columnid = e.relatedTarget.dataset.columnid;
      var temp1 = e.relatedTarget.dataset.appidd;

      $("#columnid").val(columnid);
      $("#appid").val(temp1);

      if (globalcolid == columnid)
      {
        $("#statid").val('Y');
      }
      else
      {
        $("#statid").val('N');
      }
      $(".leftitem1").empty();
    });

    $('#formatmodal').on('show.bs.modal', function(e) {
      var columnid = e.relatedTarget.dataset.columnid;
      var temp1 = e.relatedTarget.dataset.appidd;

      $("#columnid").val(columnid);
      $("#appid").val(temp1);

      if (globalcolid == columnid)
      {
        $("#statid").val('Y');
      }
      else
      {
        $("#statid").val('N');
      }
      // alert($("#statid").val());
    });

    $('#messagemodal').on('show.bs.modal', function(e) {
      var msgsourceid = e.relatedTarget.dataset.msgsourceid;
      var msgid = e.relatedTarget.dataset.msgid;
      var msg = e.relatedTarget.dataset.msg;

      $("#msgsourceid").val(msgsourceid);
      $("#msgid").val(msgid);
        $("#messageid").val(msg);

        if( $("#messageid").val().length !=0)
        {
         $("#messageid").prop("readonly", true);
        }
        else
        {
            $("#messageid").prop("readonly", false);
        }

    });

    $(".stats").click(function()
    {
      var selectedId = '#' + ($(this).attr('id'));
      if ($(selectedId).is(":checked")) {
          globalcolid = $(this).attr('id');
      }
      else {
          globalcolid = ' ';
      }
      // alert(globalcolid);
    });

    $("#listrulecreateid").click(function(){
         var no_error = 1;
        $('.input_error').remove();

        if($("#listruleid").val().length == 0){
            $('<span class="input_error" style="font-size:12px;color:red">Please enter Rule to proceed</span>').insertAfter($('#listruleid'));
            no_error = 0;
        }

        if(no_error){

            var str = $("#listruleid").val();
            var strlength = $("#listruleid").val().length;
            var pos = str.search($("#opid").val());
            var operlength = $("#opid").val().length;
            var finallen = pos+operlength;
            var rightexp = str.substr(finallen,strlength);
            var leftexp = str.substr(0, pos-1);

            var form_data = {
                appid : $("#appid").val(),
                colid : $("#columnid").val(),
                stat :  $("#statid").val(),
                listrule:'Y',
                operator : $("#opid").val(),
                leftexp: leftexp,
                rightexp: rightexp,
                times: new Date().getTime()
            };
            // console.log(form_data);
            $.ajax({
              url: $("#tourl").val(),
              type: 'POST',
              datatype:'json',
              headers: { 'token':$("#token").val()},
              data: form_data,
              success: function(message){
                  var j_obj = $.parseJSON(message);
                      if(j_obj.hasOwnProperty('success'))
                      {
                            toastr.info('List rule created successfully');
                            setTimeout(function(){
                            window.location.href= j_obj.success;

                        },1000);
                      }else if(j_obj.hasOwnProperty('fail') )
                      {
                          console.log(j_obj.fail);
                          toastr.info(j_obj.fail);
                      }
              },
              error: function(xhr, textStatus, errorThrown){
                    $('#initial').hide();
                    $('#fail').show();
              }
           });
        }
        return false;

    });
    $("#refopid").change(function()
    {
        var formula_val_temp = $("#refruleid").val();
        var str_len = formula_val_temp.length;
        if( str_len!=0)
        {
            formula_val_temp =    formula_val_temp.concat(' ',$("#refopid").val());
        }
        else
        {
            formula_val_temp =    formula_val_temp.concat($("#refopid").val());
        }
        $("#refruleid").val(formula_val_temp);
    });
    $("#refsid").change(function()
    {
        var formula_val_temp = $("#refruleid").val();
        var str_len = formula_val_temp.length;
        if( str_len!=0)
        {
            formula_val_temp =    formula_val_temp.concat(' ',$("#refsid").val());
        }
        else
        {
            formula_val_temp =    formula_val_temp.concat($("#refsid").val());
        }
        $("#refruleid").val(formula_val_temp);
    });
    //
    $("#refsaid").change(function()
    {
        var formula_val_temp = $("#refruleid").val();
        var str_len = formula_val_temp.length;
        if( str_len!=0)
        {
            formula_val_temp =    formula_val_temp.concat(' ',$("#refsaid").val());
        }
        else
        {
            formula_val_temp =    formula_val_temp.concat($("#refsaid").val());
        }
        $("#refruleid").val(formula_val_temp);
    });

    $("#refrulecreateid").click(function(){
         var no_error = 1;
        $('.input_error').remove();

        if($("#refruleid").val().length == 0){
            $('<span class="input_error" style="font-size:12px;color:red">Please enter Rule to proceed</span>').insertAfter($('#listruleid'));
            no_error = 0;
        }

        if(no_error){

            var str = $("#refruleid").val();
            var strlength = $("#refruleid").val().length;
            var pos = str.search($("#refopid").val());
            var operlength = $("#refopid").val().length;
            var finallen = pos+operlength;
            var rightexp = str.substr(finallen,strlength);
            var leftexp = str.substr(0, pos-1);

            var form_data = {
                appid : $("#appid").val(),
                colid : $("#columnid").val(),
                stat :  $("#statid").val(),
                refrule:'Y',
                operator : $("#refopid").val(),
                leftexp: leftexp,
                rightexp: rightexp,
                times: new Date().getTime()
            };

            $.ajax({
              url: $("#tourl").val(),
              type: 'POST',
              headers: { 'token':$("#token").val()},
              datatype:'json',
              data: form_data,
              success: function(message){
                  var j_obj = $.parseJSON(message);
                      if(j_obj.hasOwnProperty('success'))
                      {
                            toastr.info('Referential rule created successfully');
                            setTimeout(function(){
                            window.location.href= j_obj.success;

                        },1000);
                      }else if(j_obj.hasOwnProperty('fail') )
                      {
                          console.log(j_obj.fail);
                          toastr.info(j_obj.fail);
                      }
              },
              error: function(xhr, textStatus, errorThrown){
                    $('#initial').hide();
                    $('#fail').show();
              }
           });
        }
        return false;
    });

     $("#formatrulecreateid").click(function(){
         var no_error = 1;
        $('.input_error').remove();

        if($("#formatruleid").val().length == 0){
            $('<span class="input_error" style="font-size:12px;color:red">Please enter Rule to proceed</span>').insertAfter($('#formatruleid'));
            no_error = 0;
        }

        if(no_error){
            var form_data = {
                appid : $("#appid").val(),
                colid : $("#columnid").val(),
                stat :  $("#statid").val(),
                formatrule:'Y',
                leftexp: $("#formatruleid").val(),
                times: new Date().getTime()
            };
            // console.log(form_data);

            $.ajax({
              url: $("#tourl").val(),
              type: 'POST',
              headers: { 'token':$("#token").val()},
              datatype:'json',
              data: form_data,
              success: function(message){
                  var j_obj = $.parseJSON(message);
                      if(j_obj.hasOwnProperty('success'))
                      {
                            toastr.info('Format rule created successfully');
                            setTimeout(function(){
                            window.location.href= j_obj.success;

                        },1000);
                      }else if(j_obj.hasOwnProperty('fail') )
                      {
                          console.log(j_obj.fail);
                          toastr.info(j_obj.fail);
                      }
              },
              error: function(xhr, textStatus, errorThrown){
                    $('#initial').hide();
                    $('#fail').show();
              }
           });
        }
        return false;
    });

    $(".delcolspec").click(function(){
        var temp = $(this).closest('p').find('span').html();
        var appid = $("#appid").val();
        var url = temp.concat('&ref=',appid);
            $.ajax({
              url: url,
              type: 'GET',
              datatype:'json',
              success: function(message){
                  var j_obj = $.parseJSON(message);
                      if(j_obj.hasOwnProperty('success'))
                      {
                            toastr.info('Column Spec rule deleted successfully');
                            setTimeout(function(){
                            window.location.href= j_obj.success;

                        },1000);
                      }else if(j_obj.hasOwnProperty('fail') )
                      {
                          console.log(j_obj.fail);
                          toastr.info(j_obj.fail);
                      }
              },
              error: function(xhr, textStatus, errorThrown){
                    $('#initial').hide();
                    $('#fail').show();
              }
           });
        return false;
    });

    // $(".delcol").click(function(){
    //     var temp = $(this).closest('td').find('span').html();
    //     var url = $("#tourl").val();
    //     url = url.concat('columnrules/delete');
    //     var form_data = {
    //             appid : $("#appid").val(),
    //             colid : temp,
    //             times: new Date().getTime()
    //         };
    //         $.ajax({
    //           url: url,
    //           type: 'POST',
    //           datatype:'json',
    //           data: form_data,
    //           success: function(message){
    //               var j_obj = $.parseJSON(message);
    //                   if(j_obj.hasOwnProperty('success'))
    //                   {
    //                         toastr.info('Column Spec rule deleted successfully');
    //                         setTimeout(function(){
    //                         window.location.href= j_obj.success;

    //                     },1000);
    //                   }else if(j_obj.hasOwnProperty('fail') )
    //                   {
    //                       console.log(j_obj.fail);
    //                       toastr.info(j_obj.fail);
    //                   }
    //           },
    //           error: function(xhr, textStatus, errorThrown){
    //                 $('#initial').hide();
    //                 $('#fail').show();
    //           }
    //        });
    //     return false;
    // });

    // $(".deltransrule").click(function(){
    //     var temp = $(this).closest('td').find('span').html();
    //     var url = $("#tourl").val();
    //     url = url.concat('rule/delete');
    //     var form_data = {
    //             appid : $("#appid").val(),
    //             transruleid : temp,
    //             times: new Date().getTime()
    //         };
    //         // console.log(form_data); console.log(url);return false;
    //         $.ajax({
    //           url: url,
    //           type: 'POST',
    //           datatype:'json',
    //           data: form_data,
    //           success: function(message){
    //               var j_obj = $.parseJSON(message);
    //                   if(j_obj.hasOwnProperty('success'))
    //                   {
    //                         toastr.info('Transaction Rule deleted successfully');
    //                         setTimeout(function(){
    //                         window.location.href= j_obj.success;

    //                     },1000);
    //                   }else if(j_obj.hasOwnProperty('fail') )
    //                   {
    //                       console.log(j_obj.fail);
    //                       toastr.info(j_obj.fail);
    //                   }
    //           },
    //           error: function(xhr, textStatus, errorThrown){
    //                 $('#initial').hide();
    //                 $('#fail').show();
    //           }
    //        });
    //     return false;
    // });

    $(".setcompruledel").click(function(){
        var temp = $(this).closest('p').find('span').html();
        var url = $("#tourl").val();
        url = url.concat('setrule/deletecomp');
        var form_data = {
                appid : $("#appid").val(),
                setcompruleid : temp,
                times: new Date().getTime()
            };
             // console.log(form_data); console.log(url);return false;
            $.ajax({
              url: url,
              type: 'POST',
              headers: { 'token':$("#token").val()},
              datatype:'json',
              data: form_data,
              success: function(message){
                  var j_obj = $.parseJSON(message);
                      if(j_obj.hasOwnProperty('success'))
                      {
                            toastr.info('Transaction Set Rule deleted successfully');
                            setTimeout(function(){
                            window.location.href= j_obj.success;

                        },1000);
                      }else if(j_obj.hasOwnProperty('fail') )
                      {
                          console.log(j_obj.fail);
                          toastr.info(j_obj.fail);
                      }
              },
              error: function(xhr, textStatus, errorThrown){
                    $('#initial').hide();
                    $('#fail').show();
              }
           });
        return false;
    });
    $(".setruledel").click(function(){
        var temp = $(this).closest('td').find('span').html();
        var url = $("#tourl").val();
        url = url.concat('setrule/delete');
        var form_data = {
                appid : $("#appid").val(),
                setruleid : temp,
                times: new Date().getTime()
            };
             // console.log(form_data); console.log(url);return false;
            $.ajax({
              url: url,
              type: 'POST',
              headers: { 'token':$("#token").val()},
              datatype:'json',
              data: form_data,
              success: function(message){
                  var j_obj = $.parseJSON(message);
                      if(j_obj.hasOwnProperty('success'))
                      {
                            toastr.info('Transaction Set Rule deleted successfully');
                            setTimeout(function(){
                            window.location.href= j_obj.success;

                        },1000);
                      }else if(j_obj.hasOwnProperty('fail') )
                      {
                          console.log(j_obj.fail);
                          toastr.info(j_obj.fail);
                      }
              },
              error: function(xhr, textStatus, errorThrown){
                    $('#initial').hide();
                    $('#fail').show();
              }
           });
        return false;
    });

    $(".appdelid").click(function(){
       // var url = $("#tourl").val();
      //  url = url.concat('dataapp/deleteform');
        var form_data = {
        		idApp : $("#appid").val(),
              //  times: new Date().getTime()
            };
             // console.log(form_data); console.log(url);return false;
            $.ajax({
              url: './deleteValidationCheckajax',
              type: 'POST',
              headers: { 'token':$("#token").val()},
              datatype:'json',
              data: form_data,
              success: function(data) {
                  if (data!="") {
                  	  toastr.info(data);
                      setTimeout(function(){
                    	   window.location.href= 'validationCheck_View';
                      },1000);
                  } else {
                      toastr.info('There was a problem.');
                  }
              },
              error: function(xhr, textStatus, errorThrown){
                  $('#initial').hide();
                  $('#fail').show();
              }
          });
      return false;
  });

    /*$("#apptypeid").change(function() {
        console.log('ok');
        if ($("#apptypeid").val() == "Schema Matching") {
            $(".non-schema-matching").hide();
            $("#appnameid").val('Schema Matching App'); //set to a placeholder value
            $(".schema-matching").show();
        } else {
            $(".non-schema-matching").show();
            $("#appnameid").val(''); //clear placeholder value
            $(".schema-matching").hide();
        }
    });*/

    //create matching tyoe

   /* $("#matchtypecreateid").click(function(){
         var no_error = 1;
        $('.input_error').remove();

        if($("#appid").val().length == 0){
            $('<span class="input_error" style="font-size:12px;color:red">Please enter Validation Check ID to Continue</span>').insertAfter($('#matchtypecreateid'));
            no_error = 0;
        }
        if($("#mcardinalityid").val() == -1){
            $('<span class="input_error" style="font-size:12px;color:red">Please Choose a Match Type</span>').insertAfter($('#mcardinalityid'));
            no_error = 0;
        }
         if($("#matchtypeid").val() == -1){
            $('<span class="input_error" style="font-size:12px;color:red">Please Choose a Match Type</span>').insertAfter($('#matchtypeid'));
            no_error = 0;
        }
        if(no_error){
            var form_data = {
                appid : $("#appid").val(),
                matchtype : $("#mcardinalityid").val(),
                matchtype2 : $("#matchtypeid").val(),
                times: new Date().getTime()
            };
            // console.log(form_data);

            $.ajax({
              url: $("#tourl").val(),
              type: 'POST',
              datatype:'json',
              data: form_data,
              success: function(message){
                  var j_obj = $.parseJSON(message);
                      if(j_obj.hasOwnProperty('success'))
                      {
                            toastr.info('Match Type created successfully');
                            setTimeout(function(){
                            window.location.href= j_obj.success;

                        },1000);
                      }else if(j_obj.hasOwnProperty('fail') )
                      {
                          console.log(j_obj.fail);
                          toastr.info(j_obj.fail);
                      }
              },
              error: function(xhr, textStatus, errorThrown){
                    $('#initial').hide();
                    $('#fail').show();
              }
           });
        }
        return false;
    });*/

    // To dynamcally load the columns of the source table selected
    $("#leftsourceid").change(function(){
         var no_error = 1;
        $('.input_error').remove();

        if($("#leftsourceid").val() == '-1'){
           // $('<span class="input_error" style="font-size:12px;color:red">Please select a source to Continue</span>').insertAfter($('#rightsourceid'));
            no_error = 0;
        }
        if(no_error)
        {
            var form_data = {
                id : $("#leftsourceid").val(),
                times: new Date().getTime()
            };
            // console.log(form_data);

            $.ajax({
              url: $("#tourl1").val(),
              type: 'POST',
              headers: { 'token':$("#token").val()},
              datatype:'json',
              data: form_data,
              success: function(message){
                  var j_obj = $.parseJSON(message);
                      if(j_obj.hasOwnProperty('success'))
                      {

                        var temp = {};
                        column_array = j_obj.success;
                        // empty the list from previous selection
                        $("#leftitemid").empty();
                        $.each(column_array, function(k1, v1)
                        {
                            var column_details = column_array[k1];
                            temp_name = column_details['displayName'];

                            $("<option />")
                                .attr("value", column_details['displayName'])
                                .html(temp_name)
                                .appendTo("#leftitemid");
                                temp[this.value] = this.subitems;
                        });
                      }else if(j_obj.hasOwnProperty('fail') )
                      {
                          console.log(j_obj.fail);
                          toastr.info(j_obj.fail);
                      }
              },
              error: function(xhr, textStatus, errorThrown){
                    $('#initial').hide();
                    $('#fail').show();
              }
           });
        }
    });

    $(".leftsource").change(function(){
         var no_error = 1;
        $('.input_error').remove();

        if($(".leftsource").val() == '-1'){
            $('<span class="input_error" style="font-size:12px;color:red">Please select a source to Continue</span>').insertAfter($('#rightsourceid'));
            no_error = 0;
        }
        if(no_error)
        {
            var form_data = {
                id : $(".leftsource").val(),
                times: new Date().getTime()
            };
            console.log(form_data);

            $.ajax({
              url: $("#tomsg").val(),
              type: 'POST',
              headers: { 'token':$("#token").val()},
              datatype:'json',
              data: form_data,
              success: function(message){
                  var j_obj = $.parseJSON(message);
                      if(j_obj.hasOwnProperty('success'))
                      {

                        var temp = {};
                        column_array = j_obj.success;var value1;
                        // empty the list from previous selection
                        $(".leftitem").empty();
                        $.each(column_array, function(k1, v1)
                        {
                            var column_details = column_array[k1];
                            value1 = column_array[0]['sourcename'];
                            temp_name = column_details['displayName'];
                            value1 = value1.concat('.', column_details['displayName']);

                            $("<option />")
                                .attr("value", value1)
                                .html(temp_name)
                                .appendTo(".leftitem");
                                temp[this.value] = this.subitems;
                        });
                      }else if(j_obj.hasOwnProperty('fail') )
                      {
                          console.log(j_obj.fail);
                          toastr.info(j_obj.fail);
                      }
              },
              error: function(xhr, textStatus, errorThrown){
                    $('#initial').hide();
                    $('#fail').show();
              }
           });
        }
    });

    $(".leftsource1").change(function(){
         var no_error = 1;
        $('.input_error').remove();

        if($(".leftsource1").val() == '-1'){
            $('<span class="input_error" style="font-size:12px;color:red">Please select a source to Continue</span>').insertAfter($('#rightsourceid'));
            no_error = 0;
        }
        if(no_error)
        {
            var form_data = {
                id : $(".leftsource1").val(),
                times: new Date().getTime()
            };

            $.ajax({
              url: $("#tomsg").val(),
              type: 'POST',
              headers: { 'token':$("#token").val()},
              datatype:'json',
              data: form_data,
              success: function(message){
                  var j_obj = $.parseJSON(message);
                      if(j_obj.hasOwnProperty('success'))
                      {

                        var temp = {}; var value1='';
                        column_array = j_obj.success;
                        // empty the list from previous selection
                        $(".leftitem1").empty();
                        $.each(column_array, function(k1, v1)
                        {
                            var column_details = column_array[k1];
                            value1 = column_array[0]['sourcename'];
                            temp_name = column_details['displayName'];
                            value1 = value1.concat('.',column_details['displayName'] );
                            $("<option />")
                                .attr("value", value1)
                                .html(temp_name)
                                .appendTo(".leftitem1");
                                temp[this.value] = this.subitems;
                        });
                      }else if(j_obj.hasOwnProperty('fail') )
                      {
                          console.log(j_obj.fail);
                          toastr.info(j_obj.fail);
                      }
              },
              error: function(xhr, textStatus, errorThrown){
                    $('#initial').hide();
                    $('#fail').show();
              }
           });
        }
    });

    $("#rightsourceid").change(function(){

        var no_error = 1;
        $('.input_error').remove();

        if($("#rightsourceid").val() == '-1'){
           // $('<span class="input_error" style="font-size:12px;color:red">Please select a source to Continue</span>').insertAfter($('#rightsourceid'));
            no_error = 0;
        }

        if(no_error)
        {   var rightSourceId = $("#rightsourceid :selected").text();

        console.log(rightSourceId);
        	var form_data = {
        		idData : $("#rightsourceid").val(),
               // times: new Date().getTime()
            };

            $.ajax({
              url: './changeDataColumnAjax',
              type: 'POST',
              headers: { 'token':$("#token").val()},
              datatype:'json',
              data: form_data,
              success: function(message){
                  var j_obj = $.parseJSON(message);
                      if(j_obj.hasOwnProperty('success'))
                      {
                        var temp = {};
                        column_array = JSON.parse( j_obj.success);
                        // empty the list from previous selection
                        $("#rightitemid").empty();
                      $("<option />")
                               .attr("value", '-1')
                             .html('Select Column')
                             .appendTo("#rightitemid");
                               temp[this.value] = this.subitems;

                        $.each(column_array, function(k1, v1)
                        {
                        	console.log(v1);
                        	console.log(k1);
                            var column_details = column_array[k1];
                            console.log(column_details);
                            var sourcename = rightSourceId;
                            temp_name = column_details;
                            //sourcename = sourcename.concat('.', temp_name);
                            sourcename = temp_name;
                            $("<option />")
                                .attr("value", sourcename)
                                .html(temp_name)
                                .appendTo("#rightitemid");
                                temp[this.value] = this.subitems;
                        });
                      }else if(j_obj.hasOwnProperty('fail') )
                      {
                          console.log(j_obj.fail);
                          toastr.info(j_obj.fail);
                      }
              },
              error: function(xhr, textStatus, errorThrown){
                    $('#initial').hide();
                    $('#fail').show();
              }
           });
        }
    });
    $("#leftitemid").change(function()
    {
        var sIgnoreGlobalEvent = $(document.body).attr('data-ignore-global-event');

        if ( ($(this).val() != -1) && (sIgnoreGlobalEvent !== 'Y') )
        {
            var formula_val_temp = $("#matchkeyfid").val();
            var str_len = formula_val_temp.length;
            formula_val_temp = formula_val_temp.concat($("#leftitemid").val());
            $("#matchkeyfid").val(formula_val_temp);
        }
    });

     $("#leftitemid1").change(function()
    {
        var formula_val_temp = $("#refruleid").val();
        var str_len = formula_val_temp.length;
        formula_val_temp = formula_val_temp.concat($("#leftitemid1").val());
        $("#refruleid").val(formula_val_temp);
    });

    $("#rightitemid").change(function()
    {
        var sIgnoreGlobalEvent = $(document.body).attr('data-ignore-global-event');

        if ( ($(this).val() != -1) && (sIgnoreGlobalEvent !== 'Y') )
        {
            var formula_val_temp = $("#matchkeyfid").val();
            var str_len = formula_val_temp.length;
            formula_val_temp =    formula_val_temp.concat($("#rightitemid").val());
            $("#matchkeyfid").val(formula_val_temp);
        }

    });
    // $("#rightfunid").change(function()
    // {
    //     var formula_val_temp = $("#matchkeyfid").val();
    //     var str_len = formula_val_temp.length;
    //     if( str_len!=0)
    //     {
    //         formula_val_temp =    formula_val_temp.concat('  and ',$("#rightfunid").val(),'(');
    //     }
    //     else
    //     {
    //         alert('Please choose left expression first to proceed');
    //     }

    //     $("#matchkeyfid").val(formula_val_temp);
    // });
    $('.deletelistcolruleclass').on('confirmed.bs.confirmation', function () {

        var matchid = $(this).closest('td').find('span').html();
       /* var url;
        if( $(this).hasClass('sub-class') )
        {
          url = $(this).closest('.portlet-body').find('.tourl2').val();
        }
        else
        {
           url = $(this).closest('.portlet-body').find('.tourl1').val();
        }*/

        var form_data = {
        		idListColrules : matchid,
               // appid : $("#appid").val(),
               // times: new Date().getTime()
            };
             console.log(form_data); /*console.log(url);//return false;
*/            $.ajax({
                url: "deleteIdListColRulesData",
                type: 'POST',
                headers: { 'token':$("#token").val()},
                datatype: 'json',
                data: form_data,
                success: function(data) {
                    if (data!="") {
                        toastr.info('Extend Template Rule Deleted Successfully');
                        setTimeout(function(){
                            window.location.reload();
                        },1000);
                    } else {
                        toastr.info('There was a problem.');
                    }
                },
                error: function(xhr, textStatus, errorThrown)
                {
                    $('.validation-fail').removeClass('hidden').html("There seems to be a network problem. Please try again in some time.");
                }
            });

        return false;
    });

    $('.deleteAppGroupClass').on('confirmed.bs.confirmation', function () {
        var idAppGroup = $(this).closest('td').find('span').html();
		var form_data = {
			idAppGroup : idAppGroup
		};
		console.log(form_data);
		$.ajax({
			type : 'POST',headers: { 'token':$("#token").val()},
			url : "deleteAppGroup",
			data : form_data,
			success : function(message) {
				var j_obj1 = $.parseJSON(message);

                if (j_obj1.hasOwnProperty('success')) {
                	toastr.info('AppGroup Deleted Successfully');
					window.location.reload();
                }else {
                	toastr.info('There was a problem.');
                }
			}
		});
		return false;
	});

    // $('.delmatchtypeId').click(function()
    $('.bs_confirmation_demo_1').on('confirmed.bs.confirmation', function () {

        var matchid = $(this).closest('td').find('span').html();
        var url;
        if( $(this).hasClass('sub-class') )
        {
          url = $(this).closest('.portlet-body').find('.tourl2').val();
        }
        else
        {
           url = $(this).closest('.portlet-body').find('.tourl1').val();
        }

        var form_data = {
        		idDataBlend : matchid,
               // appid : $("#appid").val(),
               // times: new Date().getTime()
            };
             console.log(form_data); console.log(url);//return false;
            $.ajax({
                url: url,
                type: 'POST',
                headers: { 'token':$("#token").val()},
                datatype: 'json',
                data: form_data,
                success: function(data) {
                    if (data!="") {
                        toastr.info('Deleted Successfully');
                        setTimeout(function(){
                            window.location.reload();
                        },1000);
                    } else {
                        toastr.info('There was a problem.');
                    }
                },
                error: function(xhr, textStatus, errorThrown)
                {
                    $('.validation-fail').removeClass('hidden').html("There seems to be a network problem. Please try again in some time.");
                }
            });

        return false;
    });

    // DATA ANOMALY FUNCTIONS

    $("#sanomalycreateid").click(function(){
        var no_error = 1;
        $('.input_error').remove();

        // if($("#matchkeyfid").val().length == 0){
        //     $('<span class="input_error" style="font-size:12px;color:red">Please enter Matching key Expression to Continue</span>').insertAfter($('#matchkeyfid'));
        //     no_error = 0;
        // }
        var ben = $("#benfordid").prop("checked") ? 'Y': 'N';
        var out = $("#outlierid").prop("checked") ? 'Y': 'N';

        if(no_error){
            var form_data = {
                appid : $("#appid").val(),
                benford : ben,
                benfordcol : $("#benfordcol").val(),
                outlier : out,
                outliercol : $("#outliercolid").val(),
                omethod: $("#omethodid").val(),
                othreshold: $("#othresholdid").val(),
                avgrange: $("#avgrangeid").val(),
                times: new Date().getTime()
            };
             // console.log(form_data); return false;
            $.ajax({
              url: $("#tourl").val(),
              type: 'POST',
              headers: { 'token':$("#token").val()},
              datatype:'json',
              data: form_data,
              success: function(message){
                  var j_obj = $.parseJSON(message);
                      if(j_obj.hasOwnProperty('success'))
                      {
                            toastr.info('Standard Anomaly created successfully');
                        //     setTimeout(function(){
                        //     window.location.href= j_obj.success;

                        // },1000);
                      }else if(j_obj.hasOwnProperty('fail') )
                      {
                          console.log(j_obj.fail);
                          toastr.info(j_obj.fail);
                      }
              },
              error: function(xhr, textStatus, errorThrown){
                    $('#initial').hide();
                    $('#fail').show();
              }
           });
        }
        return false;
    });

    $("#canomalycreateid").click(function(){
         var no_error = 1;
        $('.input_error').remove();

        if($(".rulebox").val().length == 0){
            $('<span class="input_error" style="font-size:12px;color:red">Please enter Rule to proceed</span>').insertAfter($('#listruleid'));
            no_error = 0;
        }

        if(no_error){

            var str = $(".rulebox").val();
            var strlength = $(".rulebox").val().length;
            var pos = str.search($("#canomalyopid").val());
            var operlength = $("#canomalyopid").val().length;
            var finallen = pos+operlength;
            var rightexp = str.substr(finallen,strlength);
            var leftexp = str.substr(0, pos-1);

            var form_data = {
                appid : $("#appid").val(),
                custom: 'Y',
                operator : $("#canomalyopid").val(),
                leftexp: leftexp,
                rightexp: rightexp,
                times: new Date().getTime()
            };
            // console.log(form_data); return false;
            $.ajax({
              url: $("#tourl").val(),
              type: 'POST',
              headers: { 'token':$("#token").val()},
              datatype:'json',
              data: form_data,
              success: function(message){
                  var j_obj = $.parseJSON(message);
                      if(j_obj.hasOwnProperty('success'))
                      {
                            toastr.info('Custom rule created successfully');
                        //     setTimeout(function(){
                        //     window.location.href= j_obj.success;

                        // },1000);
                      }else if(j_obj.hasOwnProperty('fail') )
                      {
                          console.log(j_obj.fail);
                          toastr.info(j_obj.fail);
                      }
              },
              error: function(xhr, textStatus, errorThrown){
                    $('#initial').hide();
                    $('#fail').show();
              }
           });
        }
        return false;
    });

    // $('#dataSourceid').change(function() {
    //     if ($('#dataSourceid').is(":checked")) {
    //         $('#dataSourceHideid').removeClass('hidden');
    //     }
    //     else
    //     {
    //         $('#dataSourceHideid').addClass('hidden');
    //     }
    // });
    $(".custom-control").change(function()
    {
        var formula_val_temp = $(".rulebox").val();
        var str_len = formula_val_temp.length;
        // if( str_len!=0)
        // {
        //     formula_val_temp =    formula_val_temp.concat(' ',$(this).val());
        // }
        // else
        // {
        //     formula_val_temp = formula_val_temp.concat($(this).val());
        // }
        formula_val_temp = formula_val_temp.concat($(this).val());
        $(".rulebox").val(formula_val_temp);
    });

     $(".tsrcreatemsgid").click(function(){
         var no_error = 1;
        $('.input_error').remove();

        if($("#messageid").val().length == 0){
            $('<span class="input_error" style="font-size:12px;color:red">Please enter message to be saved</span>').insertAfter($('#messageid'));
            no_error = 0;
        }

        if( $("#msgid").val() )
        {
            if($("#msgparamid").val().length == 0){
            $('<span class="input_error" style="font-size:12px;color:red">Please enter Param Value</span>').insertAfter($('#msgparamid'));
            no_error = 0;
            }
            if($("#msgvalueid").val().length == 0){
            $('<span class="input_error" style="font-size:12px;color:red">Please enter Param Value</span>').insertAfter($('#msgvalueid'));
            no_error = 0;
            }
        }

        if(no_error){
            var form_data = {
                appid : $("#appid").val(),
                ruleid : $("#msgsourceid").val(),
                rulesource : $("#msgsourcename").val(),
                message : $("#messageid").val(),
                param : $("#msgparamid").val(),
                value : $("#msgvalueid").val(),
                times: new Date().getTime()
            };
            if( $("#msgid").length >0 )
            {
                form_data['msgid'] =  $("#msgid").val();
            }
            // console.log(form_data); return false;
            $.ajax({
              url: $("#msgurl").val(),
              type: 'POST',
              headers: { 'token':$("#token").val()},
              datatype:'json',
              data: form_data,
              success: function(message){
                  var j_obj = $.parseJSON(message);
                      if(j_obj.hasOwnProperty('success'))
                      {
                            toastr.info('Param-Values saved successfully');
                            setTimeout(function(){
                            window.location.reload();

                        },1000);
                      }else if(j_obj.hasOwnProperty('fail') )
                      {
                          console.log(j_obj.fail);
                          toastr.info(j_obj.fail);
                      }
              },
              error: function(xhr, textStatus, errorThrown){
                    $('#initial').hide();
                    $('#fail').show();
              }
           });
        }
        return false;
    });
     $('.frequency').click(function(){
        var check_value = $('.frequency:checked').val();
        if( $('.frequency:checked').val() == 'daily') {
            $('.daywrapper').hide();
            $('#datewrapper').hide();
            $('.dayselector').show();
        } else {
            $('.dayselector').hide();
           if (($('.frequency:checked').val() == 'monthly') || ($('.frequency:checked').val() == 'yearly')) {
             $('#datewrapper').show();
             $('.daywrapper').hide();
           } else {
            $('.daywrapper').show();
            $('#datewrapper').hide();
           }
        }
     });

     $("#schedulecreateid").click(function(){
         var no_error = 1;
        $('.input_error').remove();

        if($('.frequency:checked').val().length == 0){
            $('<span class="input_error" style="font-size:12px;color:red">Please select recurrence time</span>').insertAfter($('.frequency'));
            no_error = 0;
        }

        if($('.classday:checked').val().length == 0){
            $('<span class="input_error" style="font-size:12px;color:red">Please select day of the week</span>').insertAfter($('.classday'));
            no_error = 0;
        }

        if($("#schnameid").val().length == 0){
            $('<span class="input_error" style="font-size:12px;color:red">Please specifiy a name for Schedule</span>').insertAfter($('#schnameid'));
            no_error = 0;
        }
         if(  ( $('.frequency:checked').val() == 'monthly') || ( $('.frequency:checked').val() == 'yearly') )
            {
                if($('#dateid').val().length == 0){
                    $('<span class="input_error" style="font-size:12px;color:red">Please Choose a Date to Procees</span>').insertAfter($('#dateid'));
                    no_error = 0;
                }
            }

        var expr='';var i;var selectedId;

        for(i=1;i<8;i++)
        {

            selectedId = '#' + 'classday'+i;
            if ($(selectedId).is(":checked")) {
                expr=expr.concat($(selectedId).val(),', ');
            }
        }
        if(no_error){
            var form_data = {
                time : $("#timeid").val(),
                name : $("#schnameid").val(),
                description : $("#descid").val(),
                frequency : $('.frequency:checked').val(),
                scheduleDay: $('.classday:checked').val(),
                times: new Date().getTime()
            };
            if( $('.frequency:checked').val() =='daily')
            {
                form_data['scheduleDay'] = expr;
            }
            if(  ( $('.frequency:checked').val() == 'monthly') || ( $('.frequency:checked').val() == 'yearly') )
            {
                form_data['date'] =  $('#dateid').val();
            }
             // console.log(form_data); return false;
            $.ajax({
              url: $("#tourl").val(),
              type: 'POST',
              headers: { 'token':$("#token").val()},
              datatype:'json',
              data: form_data,
              success: function(message){
                  var j_obj = $.parseJSON(message);
                      if(j_obj.hasOwnProperty('success'))
                      {
                            toastr.info('Schedule saved successfully');
                            setTimeout(function(){
                            window.location.href=j_obj.success;

                        },1000);
                      }else if(j_obj.hasOwnProperty('fail') )
                      {
                          console.log(j_obj.fail);
                          toastr.info(j_obj.fail);
                      }
              },
              error: function(xhr, textStatus, errorThrown){
                    $('#initial').hide();
                    $('#fail').show();
              }
           });
        }
        return false;
    });


     $("#assigncreateid").click(function(){

        var no_error = 1;
        $('.input_error').remove();

        if ( $('.calendarclass').is(':checked') ==false)
        {
            //$('<span class="input_error" style="font-size:12px;color:red">Please choose a calendar to proceed<br></span>').insertBefore($('#assigncreateid'));
            $('.note-danger').removeClass('hidden').html('Please choose a calendar to proceed');
            no_error = 0;
            no_error = 0;
        }

        if(no_error){
            var form_data = {
                scheduleid : $('.calendarclass:checked').val(),
                appid : $("#appid").val(),
                times: new Date().getTime()
            };

            // console.log(form_data); return false;
            $.ajax({
              url: $("#tourl").val(),
              type: 'POST',
              headers: { 'token':$("#token").val()},
              datatype:'json',
              data: form_data,
              success: function(message){
                  var j_obj = $.parseJSON(message);
                      if(j_obj.hasOwnProperty('success'))
                      {
                            toastr.info('Assign  saved successfully');
                            setTimeout(function(){
                            window.location.reload();

                        },1000);
                      }else if(j_obj.hasOwnProperty('fail') )
                      {
                          console.log(j_obj.fail);
                          toastr.info(j_obj.fail);
                      }
              },
              error: function(xhr, textStatus, errorThrown){
                    $('#initial').hide();
                    $('#fail').show();
              }
           });
        }
        return false;
    });

    $("#assigntrigcreateid").click(function(){
        var no_error = 1;
        $('.input_error').remove();

        if( $('#sourceappid').val() == $("#targetappid").val() )
        {
             //$('<span class="input_error" style="font-size:12px;color:red">Source and Target cannot be same<br></span>').insertAfter($('#targetappid'));
            $('.note-danger').removeClass('hidden').html('Source and Target cannot be same');
            no_error = 0;
        }

        if(no_error){
            var form_data = {
                appid : $('#sourceappid').val(),
                appid2 : $("#targetappid").val(),
                times: new Date().getTime()
            };
             // console.log(form_data); return false;
            $.ajax({
              url: $("#tourl2").val(),
              type: 'POST',
              headers: { 'token':$("#token").val()},
              datatype:'json',
              data: form_data,
              success: function(message){
                  var j_obj = $.parseJSON(message);
                      if(j_obj.hasOwnProperty('success'))
                      {
                            toastr.info('Assign saved successfully');
                            setTimeout(function(){
                            window.location.reload();

                        },1000);
                      }else if(j_obj.hasOwnProperty('fail') )
                      {
                          console.log(j_obj.fail);
                          toastr.info(j_obj.fail);
                      }
              },
              error: function(xhr, textStatus, errorThrown){
                    $('#initial').hide();
                    $('#fail').show();
              }
           });
        }
        return false;
    });

  //RESULTS
 // if( $('#setruleresultsid').length > 1 ) {
 //    console.log($('#setruleresultsid').length);
 //    $('#setruleresultsid').dataTable({'bSort': false});
 //    }
var dataTableObject;

  $("#resultsftsid").change(function(){

    if( $("#resultsftsid").val() != -1 )
    {
        var form_data = {
                appid : $('#resultsftsid').val(),
                times: new Date().getTime()
            };
        //var table = $('#setruleresultsid').DataTable();
             // console.log(form_data); return false;
            $.ajax({
              url: $("#tourl").val(),
              type: 'POST',
              headers: { 'token':$("#token").val()},
              datatype:'json',
              data: form_data,
              success: function(message){
                  var j_obj = $.parseJSON(message);
                      if(j_obj.hasOwnProperty('success'))
                      {
                           if(dataTableObject) { // Check if table object exists and needs to be flushed
                                dataTableObject.destroy(); // For new version use table.destroy();
                                $('#setruleresultsid').empty(); // empty in case the columns change
                            }

                            dataTableObject = $('#setruleresultsid').DataTable( {
                                "scrollX": true,
                                data:  j_obj.success['Data'],
                                columns:j_obj.success['columns']
                            } );
                            $("#typerowid").show();
                            //$('#setruleresultsid').show();
                      }else if(j_obj.hasOwnProperty('fail') )
                      {
                          $("#typerowid").hide();
                          console.log(j_obj.fail);
                          toastr.info(j_obj.fail);
                      }
              },
              error: function(xhr, textStatus, errorThrown){
                    $('#initial').hide();
                    $('#fail').show();
              }
           });
        }
        return false;
  });

var ruledataTableObject;
var ruledataTableObject1;

   $("#resultsftid").change(function(){

    if( $("#resultsftid").val() != -1 )
    {
        var form_data = {
                appid : $('#resultsftid').val(),
                times: new Date().getTime()
            };
        //var table = $('#setruleresultsid').DataTable();
             // console.log(form_data); return false;
            $.ajax({
              url: $("#tourl").val(),
              type: 'POST',
              headers: { 'token':$("#token").val()},
              datatype:'json',
              data: form_data,
              success: function(message){
                  var j_obj = $.parseJSON(message);
                      if(j_obj.hasOwnProperty('success'))
                      {
                           if(ruledataTableObject) { // Check if table object exists and needs to be flushed
                                ruledataTableObject.destroy(); // For new version use table.destroy();
                                $('#setruleresultsid').empty(); // empty in case the columns change
                            }

                            ruledataTableObject = $('#setruleresultsid').DataTable( {
                                "scrollX": true,
                                data:  j_obj.success['summaryData']['Data'],
                                columns:j_obj.success['summaryData']['columns']
                            } );

                            if(ruledataTableObject1) { // Check if table object exists and needs to be flushed
                                ruledataTableObject1.destroy(); // For new version use table.destroy();
                                $('#setruleresultsid1').empty(); // empty in case the columns change
                            }

                            ruledataTableObject1 = $('#setruleresultsid1').DataTable( {
                                "scrollX": true,
                                data:  j_obj.success['detailData']['Data'],
                                columns:j_obj.success['detailData']['columns']
                            } );
                            $("#typerowid").show();
                            //$('#setruleresultsid').show();
                      }else if(j_obj.hasOwnProperty('fail') )
                      {
                          $("#typerowid").hide();
                          $('.table_div').hide();
                          toastr.info(j_obj.fail);
                      }
              },
              error: function(xhr, textStatus, errorThrown){
                    $('#initial').hide();
                    $('#fail').show();
              }
           });
        }
        return false;
  });

  $("#resultsfcrid").change(function(){

    if( $("#resultsfcrid").val() != -1 )
    {
        var form_data = {
                appid : $('#resultsfcrid').val(),
                times: new Date().getTime()
            };
        //var table = $('#setruleresultsid').DataTable();
             // console.log(form_data); return false;
            $.ajax({
              url: $("#tourl").val(),
              type: 'POST',
              headers: { 'token':$("#token").val()},
              datatype:'json',
              data: form_data,
              success: function(message){
                  var j_obj = $.parseJSON(message);
                      if(j_obj.hasOwnProperty('success'))
                      {
                           if(ruledataTableObject) { // Check if table object exists and needs to be flushed
                                ruledataTableObject.destroy(); // For new version use table.destroy();
                                $('#setruleresultsid').empty(); // empty in case the columns change
                            }

                            ruledataTableObject = $('#setruleresultsid').DataTable( {
                                "scrollX": true,
                                data:  j_obj.success['summaryData']['Data'],
                                columns:j_obj.success['summaryData']['columns']
                            } );
                            $("#typerowid").show();

                            //$('#setruleresultsid').show();
                      }else if(j_obj.hasOwnProperty('fail') )
                      {

                          toastr.info(j_obj.fail);
                      }
              },
              error: function(xhr, textStatus, errorThrown){
                    $('#initial').hide();
                    $('#fail').show();
              }
           });
        }
        return false;
  });

  $("#columnruletypeid").change(function(){

    if( $("#columnruletypeid").val() != -1 )
    {
        var form_data;
        if($("#columnruletypeid").val() == 'Summary')
            {   form_data = {
                appid : $('#resultsfcrid').val(),
                times: new Date().getTime()
                 };
            }else
            {
                 form_data = {
                appid : $('#resultsfcrid').val(),
                ruletype: $('#columnruletypeid').val(),
                times: new Date().getTime()
            };


            }
        //var table = $('#setruleresultsid').DataTable();
             // console.log(form_data); return false;
            $.ajax({
              url: $("#tourl").val(),
              type: 'POST',
              headers: { 'token':$("#token").val()},
              datatype:'json',
              data: form_data,
              success: function(message){
                  var j_obj = $.parseJSON(message);
                      if(j_obj.hasOwnProperty('success'))
                      {
                           if(ruledataTableObject) { // Check if table object exists and needs to be flushed
                                ruledataTableObject.destroy(); // For new version use table.destroy();
                                $('#setruleresultsid').empty(); // empty in case the columns change
                            }

                            ruledataTableObject = $('#setruleresultsid').DataTable( {
                                "scrollX": true,
                                data:  j_obj.success['summaryData']['Data'],
                                columns:j_obj.success['summaryData']['columns']
                            } );
                            $("#typerowid").show();
                            $('.table_div').show();

                            //$('#setruleresultsid').show();
                      }else if(j_obj.hasOwnProperty('fail') )
                      {

                         $('.table_div').hide();
                          console.log(j_obj.fail);
                          toastr.info(j_obj.fail);
                      }
              },
              error: function(xhr, textStatus, errorThrown){
                    $('#initial').hide();
                    $('#fail').show();
              }
           });
        }
        return false;
  });

  var ruledataTableObject2;
var ruledataTableObject3;

  $("#resultsdmid").change(function(){

    if( $("#resultsdmid").val() != -1 )
    {
        var form_data = {
                appid : $('#resultsdmid').val(),
                times: new Date().getTime()
            };
        //var table = $('#setruleresultsid').DataTable();
             // console.log(form_data); return false;
            $.ajax({
              url: $("#tourl").val(),
              type: 'POST',
              headers: { 'token':$("#token").val()},
              datatype:'json',
              data: form_data,
              success: function(message){
                  var j_obj = $.parseJSON(message);
                      if(j_obj.hasOwnProperty('success'))
                      {
                            $noDataCount = 0;
                            if (!(j_obj.success.summaryData === undefined || j_obj.success.summaryData === null || j_obj.success['summaryData']['Data'] === undefined || j_obj.success['summaryData']['Data'] === null)) {
                                console.log(j_obj.success['summaryData']['Data']);
                                if(ruledataTableObject) { // Check if table object exists and needs to be flushed
                                    ruledataTableObject.destroy(); // For new version use table.destroy();
                                    $('#setruleresultsid').empty(); // empty in case the columns change
                                }
                                ruledataTableObject = $('#setruleresultsid').DataTable( {
                                    data:  j_obj.success['summaryData']['Data'],
                                    columns:j_obj.success['summaryData']['columns']
                                } );
                            } else {
                                //show message that no data is there
                                $noDataCount++;
                                $('.trans-summary').hide();
                            }

                            if (!(j_obj.success.unmatchedData === undefined || j_obj.success.unmatchedData === null)) {
                                console.log(j_obj.success.unmatchedData.Data);
                                if(ruledataTableObject1) { // Check if table object exists and needs to be flushed
                                    ruledataTableObject1.destroy(); // For new version use table.destroy();
                                    $('#setruleresultsid1').empty(); // empty in case the columns change
                                }
                                ruledataTableObject1 = $('#setruleresultsid1').DataTable( {
                                    data:  j_obj.success['unmatchedData']['Data'],
                                    columns:j_obj.success['unmatchedData']['columns']
                                } );
                            } else {
                                //show message that no data is there
                                $noDataCount++;
                                $('.trans-unmatched').hide();
                            }

                            if (!(j_obj.success.leftonlyData === undefined || j_obj.success.leftonlyData === null)) {
                                console.log(j_obj.success['leftonlyData']['Data']);
                                if(ruledataTableObject2) { // Check if table object exists and needs to be flushed
                                    ruledataTableObject2.destroy(); // For new version use table.destroy();
                                    $('#setruleresultsid2').empty(); // empty in case the columns change
                                }

                                ruledataTableObject2 = $('#setruleresultsid2').DataTable( {
                                    data:  j_obj.success['leftonlyData']['Data'],
                                    columns:j_obj.success['leftonlyData']['columns']
                                } );
                            } else {
                                //show message that no data is there
                                $noDataCount++;
                                $('.trans-left-only').hide();
                            }

                            if (!(j_obj.success.rightonlyData === undefined || j_obj.success.rightonlyData === null)) {
                                console.log(j_obj.success['rightonlyData']['Data']);
                                if(ruledataTableObject3) { // Check if table object exists and needs to be flushed
                                    ruledataTableObject3.destroy(); // For new version use table.destroy();
                                    $('#setruleresultsid3').empty(); // empty in case the columns change
                                }

                                ruledataTableObject3 = $('#setruleresultsid3').DataTable( {
                                    data:  j_obj.success['rightonlyData']['Data'],
                                    columns:j_obj.success['rightonlyData']['columns']
                                } );
                            } else {
                                //show message that no data is there
                                $noDataCount++;
                                $('.trans-right-only').hide();
                            }

                            $(".table_div").show();
                            $("#typerowid").show();
                            //$('#setruleresultsid').show();
                      }else if(j_obj.hasOwnProperty('fail') )
                      {
                          $('.table_div').hide();
                          toastr.info(j_obj.fail);
                      }
              },
              error: function(xhr, textStatus, errorThrown){
                    $('#initial').hide();
                    $('#fail').show();
              }
           });
        }
        return false;
  });

  $("#resultsasid").change(function(){

    if( $("#resultsasid").val() != -1 )
    {
        var form_data = {
                appid : $('#resultsasid').val(),
                times: new Date().getTime()
            };
        //var table = $('#setruleresultsid').DataTable();
             // console.log(form_data); return false;
            $.ajax({
              url: $("#tourl").val(),
              type: 'POST',
              headers: { 'token':$("#token").val()},
              datatype:'json',
              data: form_data,
              success: function(message){
                  var j_obj = $.parseJSON(message);
                      if(j_obj.hasOwnProperty('success'))
                      {
                           if(ruledataTableObject) { // Check if table object exists and needs to be flushed
                                ruledataTableObject.destroy(); // For new version use table.destroy();
                                $('#setruleresultsid').empty(); // empty in case the columns change
                            }

                            ruledataTableObject = $('#setruleresultsid').DataTable( {
                                data:  j_obj.success[0]['matchingresults']['Data'],
                                columns:j_obj.success[0]['matchingresults']['columns']
                            } );
                            $("#typerowid").show();

                            //$('#setruleresultsid').show();
                      }else if(j_obj.hasOwnProperty('fail') )
                      {

                          toastr.info(j_obj.fail);
                      }
              },
              error: function(xhr, textStatus, errorThrown){
                    $('#initial').hide();
                    $('#fail').show();
              }
           });
        }
        return false;
  });

   $("#stdruletypeid").change(function(){

    if( $("#stdruletypeid").val() != -1 )
    {
        var form_data;
        if($("#stdruletypeid").val() == 'Benford')
            {   form_data = {
                appid : $('#resultsasid').val(),
                times: new Date().getTime()
                 };
            }else
            {
                 form_data = {
                appid : $('#resultsasid').val(),
                ruletype: $('#stdruletypeid').val(),
                times: new Date().getTime()
            };
            }
        //var table = $('#setruleresultsid').DataTable();
             // console.log(form_data); return false;
            $.ajax({
              url: $("#tourl").val(),
              type: 'POST',
              headers: { 'token':$("#token").val()},
              datatype:'json',
              data: form_data,
              success: function(message){
                  var j_obj = $.parseJSON(message);
                      if(j_obj.hasOwnProperty('success'))
                      {
                           if(ruledataTableObject) { // Check if table object exists and needs to be flushed
                                ruledataTableObject.destroy(); // For new version use table.destroy();
                                $('#setruleresultsid').empty(); // empty in case the columns change
                            }

                            ruledataTableObject = $('#setruleresultsid').DataTable( {
                                data:  j_obj.success[0]['matchingresults']['Data'],
                                columns:j_obj.success[0]['matchingresults']['columns']
                            } );
                            $("#typerowid").show();
                            $('.table_div').show();

                            //$('#setruleresultsid').show();
                      }else if(j_obj.hasOwnProperty('fail') )
                      {

                         $('.table_div').hide();
                          console.log(j_obj.fail);
                          toastr.info(j_obj.fail);
                      }
              },
              error: function(xhr, textStatus, errorThrown){
                    $('#initial').hide();
                    $('#fail').show();
              }
           });
        }
        return false;
  });

  $("#resultsacid").change(function(){

    if( $("#resultsacid").val() != -1 )
    {
        var form_data = {
                appid : $('#resultsacid').val(),
                times: new Date().getTime()
            };
        //var table = $('#setruleresultsid').DataTable();
             // console.log(form_data); return false;
            $.ajax({
              url: $("#tourl").val(),
              type: 'POST',
              headers: { 'token':$("#token").val()},
              datatype:'json',
              data: form_data,
              success: function(message){
                  var j_obj = $.parseJSON(message);
                      if(j_obj.hasOwnProperty('success'))
                      {
                           if(ruledataTableObject) { // Check if table object exists and needs to be flushed
                                ruledataTableObject.destroy(); // For new version use table.destroy();
                                $('#setruleresultsid').empty(); // empty in case the columns change
                            }

                            ruledataTableObject = $('#setruleresultsid').DataTable( {
                                data:  j_obj.success[0]['matchingresults']['Data'],
                                columns:j_obj.success[0]['matchingresults']['columns']
                            } );
                            $("#typerowid").show();

                            //$('#setruleresultsid').show();
                      }else if(j_obj.hasOwnProperty('fail') )
                      {

                          toastr.info(j_obj.fail);
                      }
              },
              error: function(xhr, textStatus, errorThrown){
                    $('#initial').hide();
                    $('#fail').show();
              }
           });
        }
        return false;
  });

  $("#cusruletypeid").change(function(){

    if( $("#cusruletypeid").val() != -1 )
    {
        var form_data;
        // if($("#cusruletypeid").val() == 'Rule 3')
        //     {   form_data = {
        //         appid : $('#resultsacid').val(),
        //         times: new Date().getTime()
        //          };
        //     }else
        //     {
                form_data = {
                appid : $('#resultsacid').val(),
                ruletype: $('#cusruletypeid').val(),
                times: new Date().getTime()
            // }
            };
        //var table = $('#setruleresultsid').DataTable();
             // console.log(form_data); return false;
            $.ajax({
              url: $("#tourl").val(),
              type: 'POST',
              headers: { 'token':$("#token").val()},
              datatype:'json',
              data: form_data,
              success: function(message){
                  var j_obj = $.parseJSON(message);
                      if(j_obj.hasOwnProperty('success'))
                      {
                           if(ruledataTableObject) { // Check if table object exists and needs to be flushed
                                ruledataTableObject.destroy(); // For new version use table.destroy();
                                $('#setruleresultsid').empty(); // empty in case the columns change
                            }

                            console.log(j_obj.success);

                            ruledataTableObject = $('#setruleresultsid').DataTable( {
                                data:  j_obj.success[0]['matchingresults']['Data'],
                                columns:j_obj.success[0]['matchingresults']['columns']
                            } );
                            $("#typerowid").show();
                            $('.table_div').show();

                            //$('#setruleresultsid').show();
                      }else if(j_obj.hasOwnProperty('fail') )
                      {

                         $('.table_div').hide();
                          console.log(j_obj.fail);
                          toastr.info(j_obj.fail);
                      }
              },
              error: function(xhr, textStatus, errorThrown){
                    $('#initial').hide();
                    $('#fail').show();
              }
           });
        }
        return false;
  });

  //Date filter functions
   $("#customdateid").click(function(){
        var form_data;
           if($("#columnruletypeid").val() == 'Summary')
            {   form_data = {
                appid : $('#resultsfcrid').val(),
                times: new Date().getTime()
                 };
            }else
            {
                form_data = {
                appid : $('#resultsfcrid').val(),
                ruletype: $('#columnruletypeid').val(),
                times: new Date().getTime()
                };
            }
            if( $("#startdateid").val() != "" )
            {
                form_data['startdate'] = $("#startdateid").val();
            }
            if( $("#enddateid").val() != "" )
            {
                form_data['enddate'] = $("#enddateid").val();
            }
        //var table = $('#setruleresultsid').DataTable();
               console.log(form_data); //return false;
            $.ajax({
              url: $("#tourl").val(),
              type: 'POST',
              headers: { 'token':$("#token").val()},
              datatype:'json',
              data: form_data,
              success: function(message){
                  var j_obj = $.parseJSON(message);
                      if(j_obj.hasOwnProperty('success'))
                      {
                           if(ruledataTableObject) { // Check if table object exists and needs to be flushed
                                ruledataTableObject.destroy(); // For new version use table.destroy();
                                $('#setruleresultsid').empty(); // empty in case the columns change
                            }

                            console.log(j_obj.success);

                            ruledataTableObject = $('#setruleresultsid').DataTable( {
                                data:  j_obj.success[0]['matchingresults']['Data'],
                                columns:j_obj.success[0]['matchingresults']['columns']
                            } );
                            $("#typerowid").show();
                            $('.table_div').show();

                            //$('#setruleresultsid').show();
                      }else if(j_obj.hasOwnProperty('fail') )
                      {

                         $('.table_div').hide();
                          console.log(j_obj.fail);
                          toastr.info(j_obj.fail);
                      }
              },
              error: function(xhr, textStatus, errorThrown){
                    $('#initial').hide();
                    $('#fail').show();
              }
           });

        return false;
  });

  $("#stddateid").click(function(){
     var form_data;
         form_data = {
        appid : $('#resultsasid').val(),
        ruletype: $('#stdruletypeid').val(),
        times: new Date().getTime()
            };

            if( $("#startdateid").val() != "" )
            {
                form_data['startdate'] = $("#startdateid").val();
            }
            if( $("#enddateid").val() != "" )
            {
                form_data['enddate'] = $("#enddateid").val();
            }
        //var table = $('#setruleresultsid').DataTable();
            console.log(form_data); //return false;
            $.ajax({
              url: $("#tourl").val(),
              type: 'POST',
              headers: { 'token':$("#token").val()},
              datatype:'json',
              data: form_data,
              success: function(message){
                  var j_obj = $.parseJSON(message);
                      if(j_obj.hasOwnProperty('success'))
                      {
                           if(ruledataTableObject) { // Check if table object exists and needs to be flushed
                                ruledataTableObject.destroy(); // For new version use table.destroy();
                                $('#setruleresultsid').empty(); // empty in case the columns change
                            }

                            ruledataTableObject = $('#setruleresultsid').DataTable( {
                                data:  j_obj.success[0]['matchingresults']['Data'],
                                columns:j_obj.success[0]['matchingresults']['columns']
                            } );
                            $("#typerowid").show();
                            $('.table_div').show();

                            //$('#setruleresultsid').show();
                      }else if(j_obj.hasOwnProperty('fail') )
                      {

                         $('.table_div').hide();
                          console.log(j_obj.fail);
                          toastr.info(j_obj.fail);
                      }
              },
              error: function(xhr, textStatus, errorThrown){
                    $('#initial').hide();
                    $('#fail').show();
              }
           });

        return false;
  });

   $("#matchdateid").click(function(){

    if( $("#resultsdmid").val() != -1 )
    {
        var form_data = {
                appid : $('#resultsdmid').val(),
                times: new Date().getTime()
            };
        if( $("#startdateid").val() != "" )
        {
            form_data['startdate'] = $("#startdateid").val();
        }
        if( $("#enddateid").val() != "" )
        {
            form_data['enddate'] = $("#enddateid").val();
        }
        //var table = $('#setruleresultsid').DataTable();
         console.log(form_data); //return false;
            $.ajax({
              url: $("#tourl").val(),
              type: 'POST',
              headers: { 'token':$("#token").val()},
              datatype:'json',
              data: form_data,
              success: function(message){
                  var j_obj = $.parseJSON(message);
                      if(j_obj.hasOwnProperty('success'))
                      {
                           if(ruledataTableObject) { // Check if table object exists and needs to be flushed
                                ruledataTableObject.destroy(); // For new version use table.destroy();
                                $('#setruleresultsid').empty(); // empty in case the columns change
                            }
                            ruledataTableObject = $('#setruleresultsid').DataTable( {
                                data:  j_obj.success['summaryData']['Data'],
                                columns:j_obj.success['summaryData']['columns']
                            } );

                            if(ruledataTableObject1) { // Check if table object exists and needs to be flushed
                                ruledataTableObject1.destroy(); // For new version use table.destroy();
                                $('#setruleresultsid1').empty(); // empty in case the columns change
                            }

                            ruledataTableObject1 = $('#setruleresultsid1').DataTable( {
                                data:  j_obj.success['unmatchedData']['Data'],
                                columns:j_obj.success['unmatchedData']['columns']
                            } );

                             if(ruledataTableObject2) { // Check if table object exists and needs to be flushed
                                ruledataTableObject2.destroy(); // For new version use table.destroy();
                                $('#setruleresultsid2').empty(); // empty in case the columns change
                            }

                            ruledataTableObject2 = $('#setruleresultsid2').DataTable( {
                                data:  j_obj.success['leftonlyData']['Data'],
                                columns:j_obj.success['leftonlyData']['columns']
                            } );

                             if(ruledataTableObject3) { // Check if table object exists and needs to be flushed
                                ruledataTableObject3.destroy(); // For new version use table.destroy();
                                $('#setruleresultsid3').empty(); // empty in case the columns change
                            }

                            ruledataTableObject3 = $('#setruleresultsid3').DataTable( {
                                data:  j_obj.success['rightonlyData']['Data'],
                                columns:j_obj.success['rightonlyData']['columns']
                            } );
                            $(".table_div").show();
                            $("#typerowid").show();
                            //$('#setruleresultsid').show();
                      }else if(j_obj.hasOwnProperty('fail') )
                      {
                          $('.table_div').hide();
                          toastr.info(j_obj.fail);
                      }
              },
              error: function(xhr, textStatus, errorThrown){
                    $('#initial').hide();
                    $('#fail').show();
              }
           });
        }
        return false;
    });

    $("#setruledateid").click(function(){

    if( $("#resultsftsid").val() != -1 )
    {
        var form_data = {
                appid : $('#resultsftsid').val(),
                times: new Date().getTime()
            };
        if( $("#startdateid").val() != "" )
        {
            form_data['startdate'] = $("#startdateid").val();
        }
        if( $("#enddateid").val() != "" )
        {
            form_data['enddate'] = $("#enddateid").val();
        }
        //var table = $('#setruleresultsid').DataTable();
              //console.log(form_data); //return false;
            $.ajax({
              url: $("#tourl").val(),
              type: 'POST',
              headers: { 'token':$("#token").val()},
              datatype:'json',
              data: form_data,
              success: function(message){
                  var j_obj = $.parseJSON(message);
                      if(j_obj.hasOwnProperty('success'))
                      {
                           if(dataTableObject) { // Check if table object exists and needs to be flushed
                                dataTableObject.destroy(); // For new version use table.destroy();
                                $('#setruleresultsid').empty(); // empty in case the columns change
                            }

                            dataTableObject = $('#setruleresultsid').DataTable( {
                                 //"bDestroy": true,
                                data:  j_obj.success['Data'],
                                columns:j_obj.success['columns']
                            } );
                            $("#typerowid").show();
                            //$('#setruleresultsid').show();
                      }else if(j_obj.hasOwnProperty('fail') )
                      {
                          $("#typerowid").hide();
                          console.log(j_obj.fail);
                          toastr.info(j_obj.fail);
                      }
              },
              error: function(xhr, textStatus, errorThrown){
                    $('#initial').hide();
                    $('#fail').show();
              }
           });
        }
        return false;
    });

   $("#ruledateid").click(function(){

    if( $("#resultsftid").val() != -1 )
    {
        var form_data = {
                appid : $('#resultsftid').val(),
                times: new Date().getTime()
            };
        if( $("#startdateid").val() != "" )
        {
            form_data['startdate'] = $("#startdateid").val();
        }
        if( $("#enddateid").val() != "" )
        {
            form_data['enddate'] = $("#enddateid").val();
        }
        //var table = $('#setruleresultsid').DataTable();
             // console.log(form_data); return false;
            $.ajax({
              url: $("#tourl").val(),
              type: 'POST',
              headers: { 'token':$("#token").val()},
              datatype:'json',
              data: form_data,
              success: function(message){
                  var j_obj = $.parseJSON(message);
                      if(j_obj.hasOwnProperty('success'))
                      {
                           if(ruledataTableObject) { // Check if table object exists and needs to be flushed
                                ruledataTableObject.destroy(); // For new version use table.destroy();
                                $('#setruleresultsid').empty(); // empty in case the columns change
                            }

                            ruledataTableObject = $('#setruleresultsid').DataTable( {
                                data:  j_obj.success['summaryData']['Data'],
                                columns:j_obj.success['summaryData']['columns']
                            } );

                            if(ruledataTableObject1) { // Check if table object exists and needs to be flushed
                                ruledataTableObject1.destroy(); // For new version use table.destroy();
                                $('#setruleresultsid1').empty(); // empty in case the columns change
                            }

                            ruledataTableObject1 = $('#setruleresultsid1').DataTable( {
                                data:  j_obj.success['detailData']['Data'],
                                columns:j_obj.success['detailData']['columns']
                            } );
                            $("#typerowid").show();
                            //$('#setruleresultsid').show();
                      }else if(j_obj.hasOwnProperty('fail') )
                      {
                          $("#typerowid").hide();
                          $('.table_div').hide();
                          toastr.info(j_obj.fail);
                      }
              },
              error: function(xhr, textStatus, errorThrown){
                    $('#initial').hide();
                    $('#fail').show();
              }
           });
        }
        return false;
    });

    $("#columndateid").click(function(){

    if( $("#resultsfcrid").val() != -1 )
    {
        var form_data = {
                appid : $('#resultsfcrid').val(),
                times: new Date().getTime()
            };
        if( $("#startdateid").val() != "" )
        {
            form_data['startdate'] = $("#startdateid").val();
        }
        if( $("#enddateid").val() != "" )
        {
            form_data['enddate'] = $("#enddateid").val();
        }
        //var table = $('#setruleresultsid').DataTable();
             console.log(form_data); //return false;
            $.ajax({
              url: $("#tourl").val(),
              type: 'POST',
              headers: { 'token':$("#token").val()},
              datatype:'json',
              data: form_data,
              success: function(message){
                  var j_obj = $.parseJSON(message);
                      if(j_obj.hasOwnProperty('success'))
                      {
                           if(ruledataTableObject) { // Check if table object exists and needs to be flushed
                                ruledataTableObject.destroy(); // For new version use table.destroy();
                                $('#setruleresultsid').empty(); // empty in case the columns change
                            }

                            ruledataTableObject = $('#setruleresultsid').DataTable( {
                                data:  j_obj.success['summaryData']['Data'],
                                columns:j_obj.success['summaryData']['columns']
                            } );
                            $("#typerowid").show();

                            //$('#setruleresultsid').show();
                      }else if(j_obj.hasOwnProperty('fail') )
                      {
                          //$("#typerowid").hide();
                          toastr.info(j_obj.fail);
                      }
              },
              error: function(xhr, textStatus, errorThrown){
                    $('#initial').hide();
                    $('#fail').show();
              }
           });
        }
        return false;
    });
    $('#s3typevalueid').change(function() {
       $this = $(this);
        var selectedVal = $this.val();
        if (selectedVal == 'Your S3 Bucket') {
            $('#s3_form_id').removeClass('hidden').show();
        }
        else if (selectedVal == 'DataBuck S3 Bucket') {
            $('#s3_form_id').addClass('hidden').hide();
        }
    });


    $(".appcloneid").click(function(){
        var no_error = 1;
        $('.input_error').remove();
        if( $("#clonenameid").val().length == 0 )
        {
            $('<div class="input_error" style="font-size:12px;color:red;">Please enter Name for validation Check</div>').insertAfter($('#clonenameid'));
            no_error = 0;
        }
        if(no_error){
            var form_data = {
                    id : $("#appid").val(),
                    name : $("#clonenameid").val(),
                    times: new Date().getTime()
                };
                // console.log(form_data); console.log(url);return false;
                $.ajax({
                  url: $("#tourl").val(),
                  type: 'POST',
                  headers: { 'token':$("#token").val()},
                  datatype:'json',
                  data: form_data,
                  success: function(message){
                      var j_obj = $.parseJSON(message);
                          if(j_obj.hasOwnProperty('success'))
                          {
                                toastr.info('Validation Check Cloned successfully');
                                setTimeout(function(){
                                window.location.href= j_obj.success;

                            },1000);
                          }else if(j_obj.hasOwnProperty('fail') )
                          {
                              console.log(j_obj.fail);
                              toastr.info(j_obj.fail);
                          }
                  },
                  error: function(xhr, textStatus, errorThrown){
                        $('#initial').hide();
                        $('#fail').show();
                  }
               });
        }
        return false;
    });
    $(".dashboard-summary").click(function(){
        console.log('ok');
        var $this = $(this);
        var currId = $this.attr('id');
        console.log(currId);
        var currChartId = currId + '_div';
        $('.dashboard-summary-chart').hide();
        $('#'+currChartId).width('100%').show();
    });

    $('.datatable-matchingTable').DataTable({
        "scrollX": true
    })

    if ($('.datatable-master').length > 0) {
        $('.datatable-master').DataTable( {
            "scrollX": true
        });
    }
});

$(document).ready(function(){
	if ($('#appCustomization').length > 0) {

		var incrementalMatching=$("#incrementalMatching").val();
		var buildHistoricFingerPrint=$("#buildHistoricFingerPrint").val();
		console.log(incrementalMatching+""+buildHistoricFingerPrint);
		if((incrementalMatching=="Y") && (buildHistoricFingerPrint=="Y")){
			$('#dateDivId').removeClass('hidden').show();
			console.log("Historic");
			$("#ApplicationType").val("Historic");
		}else if((incrementalMatching=="Y") && (buildHistoricFingerPrint=="N")){
			$('#dateDivId').addClass("hidden");
			$("#ApplicationType").val("Ongoing");
		}else{
			$('#dateDivId').addClass("hidden");
			$("#ApplicationType").val("Normal");
		}

		/*if(incrementalMatching=="Y"){
			$("#incrementalTypeId").attr("checked",true);
		}else{
			$("#incrementalTypeId").attr("checked",false);
		}*/

		/*if(buildHistoricFingerPrint=="Y"){
			$("#buildHistoricId").attr("checked",true);
			$('#dateDivId').removeClass('hidden').show();
		}else{
			$("#buildHistoricId").attr("checked",false);
			$('#dateDivId').addClass("hidden");
		}*/

		var duplicateCheck = $("#duplicateCheck").val();
		//alert(stringStatStatus);
		if(duplicateCheck=="Y"){
			$("#dupCheck").attr("checked",true);
		}else{
			$("#dupCheck").attr("checked",false);
		}

		var stringStatStatus = $("#stringStatStatus").val();
		//alert(stringStatStatus);
		if(stringStatStatus=="Y"){
			$("#stringStat").attr("checked",true);
		}else{
			$("#stringStat").attr("checked",false);
		}
		var nullCountStatus = $("#nullCountStatus").val();
		//alert(stringStatStatus);
		if(nullCountStatus=="Y"){
			$("#nullCount").attr("checked",true);
		}else{
			$("#nullCount").attr("checked",false);
		}
		var numericalStatsStatus = $("#numericalStatsStatus").val();
		if(numericalStatsStatus=="Y"){
			$("#numericalStats").attr("checked",true);
		}else{
			$("#numericalStats").attr("checked",false);
		}

		var recordAnomalyStatus = $("#recordAnomalyStatus").val();
		//alert(stringStatStatus);
		if(recordAnomalyStatus=="Y"){
			$("#recordAnomalyid").attr("checked",true);
		}else{
			$("#recordAnomalyid").attr("checked",false);
		}

		var dataDriftStatus = $("#dataDriftStatus").val();
		//alert(stringStatStatus);
		if(dataDriftStatus=="Y"){
			$("#dataDriftCheck").attr("checked",true);
		}else{
			$("#dataDriftCheck").attr("checked",false);
		}

		var outofNormStatus = $("#outofNormStatus").val();
		//alert(stringStatStatus);
		if(outofNormStatus=="Y"){
			$("#outofNorm").attr("checked",true);
		}else{
			$("#outofNorm").attr("checked",false);
		}

		var identityDupRow = $("#identityDupRow").val();
		if(identityDupRow=="Y"){
			$("#duplicateCount").attr("checked",true);
		}else{
			$("#duplicateCount").attr("checked",false);
		}

		var defaultCheckStatus = $("#defaultCheckStatus").val();
		//alert(stringStatStatus);
		if(defaultCheckStatus=="Y"){
			$("#defaultCheck").attr("checked",true);
		}else{
			$("#defaultCheck").attr("checked",false);
		}

		var allDupRow = $("#allDupRow").val();
		//alert(stringStatStatus);
		if(allDupRow=="Y"){
			$("#duplicateCountAll").attr("checked",true);
		}else{
			$("#duplicateCountAll").attr("checked",false);
		}

		var recordCountAnomalyTypeStatus = $("#recordCountAnomalyTypeStatus").val();
		//alert(stringStatStatus);
		if(recordCountAnomalyTypeStatus=="Y"){
			$(".recordCountAnomaly").val("RecordCountAnomaly");
		}else{
			$(".recordCountAnomaly").val("KeyBasedRecordCountAnomaly");
			$('#groupEqualitydiv').removeClass('hidden').show();
			var groupEqualityStatus=$("#groupEqualityStatus").val();
			//alert(incrementalMatching);
			if(groupEqualityStatus=="Y"){
				$("#groupEquality").attr("checked",true);
			}else{
				$("#groupEquality").attr("checked",false);
			}
		}

		var applyRulesStatus = $("#applyRulesStatus").val();
		if(applyRulesStatus=="Y"){
			$("#applyRules").attr("checked",true);
		}else{
			$("#applyRules").attr("checked",false);
		}

		var applyDerivedColumnsStatus = $("#applyDerivedColumnsStatus").val();
		if(applyDerivedColumnsStatus=="Y"){
			$("#applyDerivedColumns").attr("checked",true);
		}else{
			$("#applyDerivedColumns").attr("checked",false);
		}
		var timelinessKeyStatus = $("#timelinessKeyStatus").val();
		if(timelinessKeyStatus=="Y"){
			$("#timelinessKey").attr("checked",true);
		}else{
			$("#timelinessKey").attr("checked",false);
		}
		var patternCheckStatus = $("#patternCheckStatus").val();
		if(patternCheckStatus=="Y"){
			$("#patternCheck").attr("checked",true);
		}else{
			$("#patternCheck").attr("checked",false);
		}
		var defaultPatternCheckStatus = $("#defaultPatternCheckStatus").val();
		if (defaultPatternCheckStatus == "Y") {
			$("#defaultPatternCheck").attr("checked", true);
		} else {
			$("#defaultPatternCheck").attr("checked", false);
		}
		//sumeet
		var badDataStatus = $("#badDataStatus").val();
		if(badDataStatus=="Y"){

			$("#badData").attr("checked",true);
		}else{
			$("#badData").attr("checked",false);
		}

		// length chaeck changes 28dec2018 priyanka

		var lengthCheckStatus = $("#lengthCheckStatus").val();

		if(lengthCheckStatus=="Y"){

			$("#lengthCheck").attr("checked",true);
		}else{
			$("#lengthCheck").attr("checked",false);
		}


		// dGroupNull check changes
		var dGroupNullCheckStatus = $("#dGroupNullCheckStatus").val();
		if(dGroupNullCheckStatus=="Y"){
			$("#dGroupNullCheck").attr("checked",true);
		}else{
			$("#dGroupNullCheck").attr("checked",false);
		}

		// dGroupDateRuleCheck changes
		var dGroupDateRuleCheckStatus = $("#dGroupDateRuleCheckStatus").val();
		if(dGroupDateRuleCheckStatus=="Y"){
			$("#dGroupDateRuleCheck").attr("checked",true);
		}else{
			$("#dGroupDateRuleCheck").attr("checked",false);
		}

		// date rule changes 8jan2019 priyanka

		var dateRuleStatus = $("#dateRuleChkStatus").val();
		//alert("dateRuleStatus ->",dateRuleStatus);
		if(dateRuleStatus=="Y"){
		//	alert("hiiiiiiii");
			$("#dateRuleCheck").attr("checked",true);
		}else{
			$("#dateRuleCheck").attr("checked",false);
		}

		var fileNameValStatus = $("#fileNameValStatus").val();
		//alert(stringStatStatus);
		if(fileNameValStatus=="Y"){
			$("#fileNameVal").attr("checked",true);
		}else{
			$("#fileNameVal").attr("checked",false);
		}

		var columnOrderValStatus = $("#columnOrderValStatus").val();
		//alert(stringStatStatus);
		if(columnOrderValStatus=="Y"){
			$("#columnOrderVal").attr("checked",true);
		}else{
			$("#columnOrderVal").attr("checked",false);
		}
		var dataLocation = $("#dataLocation").val();
		if(dataLocation=="FILESYSTEM"){

			$(".fileSystemdiv").removeClass("hidden");
		}

		var noneStatus = $("#noneStatus").val();
		var month = $("#month").val();
		var dayOfWeek = $("#dayOfWeek").val();
		var hourOfDay = $("#hourOfDay").val();
		var dayOfMonth = $("#dayOfMonth").val();
		var frequencyDays = $("#frequencyDays").val();
		var updateFrequency = $("#updateFrequency").val();

		if(updateFrequency=="Daily"){
			$(".selectfrequency").val(updateFrequency);
		}else if(updateFrequency=="Never"){
			$(".selectfrequency").val(updateFrequency);

		}else{
			$(".selectfrequency").val(updateFrequency);
			$('#EveryDay').removeClass('hidden').show();
		}

		if(noneStatus== "None"){
			//alert(noneStatus);
			$("#none").attr("checked",true);
			$(".divmonth").addClass("hidden");
			$(".divHourOfDay").addClass("hidden");
			$(".divDayOfMonth").addClass("hidden");
			$(".divDayOfWeek").addClass("hidden");
			$('#noneid').addClass('hidden');
		}else{
		}

		if(month!= ""){
			$("#chkmonth").attr("checked",true);
			$(".divmonth").removeClass("hidden");
			$(".month").val(month);
		}else{
			$("#chkmonth").attr("checked",false);
			$(".divmonth").addClass("hidden");
		}

		if(dayOfWeek!=""){
			$("#chkDayOfWeek").attr("checked",true);
			$(".divDayOfWeek").removeClass("hidden");
			$(".dayOfWeek").val(dayOfWeek);
		}else{
			$("#chkDayOfWeek").attr("checked",false);
			$(".divDayOfWeek").addClass("hidden");
		}

		if(hourOfDay!=""){
			console.log(hourOfDay);
			$("#chkHourOfDay").attr("checked",true);
			$(".divHourOfDay").removeClass("hidden");
			$(".hourofDay").val(hourOfDay);
		}else{
			$("#chkHourOfDay").attr("checked",false);
			$(".divHourOfDay").addClass("hidden");
		}

		if(dayOfMonth!=""){
			console.log(dayOfMonth);
			$("#chkDayOfMonth").attr("checked",true);
			$(".divDayOfMonth").removeClass("hidden");
			$(".dayOfMonth").val(dayOfMonth);
		}else{
			$("#chkDayOfMonth").attr("checked",false);
			$(".divDayOfMonth").addClass("hidden");
		}
	}
});

$("#locationid").change(function(){

    var no_error = 1;
    $('.input_error').remove();

    if($("#locationid").val() == '-1'){
       // $('<span class="input_error" style="font-size:12px;color:red">Please select a source to Continue</span>').insertAfter($('#rightsourceid'));
        no_error = 0;
    }

    if(no_error)
    {   var locationid = $("#locationid").val();

    console.log(locationid);
    	var form_data = {
    			location : $("#locationid").val(),
           // times: new Date().getTime()
        };

        $.ajax({
          url: './changeLocationForDataTemplateAjax',
          type: 'POST',
          headers: {'token':$("#token").val()},
          datatype:'json',
          data: form_data,
          success: function(message){
              var j_obj = $.parseJSON(message);
                  if(j_obj.hasOwnProperty('success'))
                  {
                    var temp = {};
                    column_array = JSON.parse( j_obj.success);
                    // empty the list from previous selection
                    $("#schemaId").empty();
                    $("<option class='hidden'/>")
                    .attr("value", "-1")
                    .html("Select")
					.appendTo("#schemaId");
                    $.each(column_array, function(k1, v1)
                    {
                    	console.log(v1);
                    	console.log(k1);
                        var column_details = column_array[k1];
                        //console.log(column_details);
                        var sourcename ;
                        temp_name = k1+"_"+column_details;
                        //sourcename = sourcename.concat('.', temp_name);
                        sourcename = temp_name;
                        $("<option />")
                            .attr("value", k1)
                            .html(temp_name)
                            .appendTo("#schemaId");
                            temp[this.value] = this.subitems;
                    });
                    $('#schemaId').multiselect('rebuild');

                  }else if(j_obj.hasOwnProperty('fail') )
                  {
                      console.log(j_obj.fail);
                      toastr.info(j_obj.fail);
                  }
          },
          error: function(xhr, textStatus, errorThrown){
                $('#initial').hide();
                $('#fail').show();
          }
       });
    }
});

/* Pradeep - this function to be called instead of hard coding 'databuck' in urls in Java script code */
function getWebAppBaseUrl() {
	 return window.location.href.match(/^.*\//);
}
