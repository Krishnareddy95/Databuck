<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<script>
	var request;
	var vals = "";
	function sendInfo() {
		var v = document.getElementById("datasetid").value;
		//var v=document.vinform.Database.value;  
		var url = "./duplicatedatatemplatename?val=" + v;
		//alert("in ajax code");
		if (window.XMLHttpRequest) {
			request = new XMLHttpRequest();
		} else if (window.ActiveXObject) {
			request = new ActiveXObject("Microsoft.XMLHTTP");
		}

		try {
			request.onreadystatechange = getInfo;
			request.open("POST", url, true);
			request.setRequestHeader('token',$("#token").val());
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
			//var sal="Database already exists";
			//console.log(val);
			document.getElementById('amit').innerHTML = val;
			if (val != "") {
				vals = val;
				//alert("please enter another name");
				return false;
			}
			//alert("check123");
		}
	}
</script>

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
						<%--  <% 
                        String message=(String)request.getAttribute("message");
                        System.out.print("message="+message);
                        if(message!=null){
                        %>
                   <div class="alert alert-success" style="width:50%; float:right">
   <button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
 <strong>${message}</strong>
</div>
<%} %> --%>
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold "> Add New Location
							</span>
						</div>
						<div id="loading-progress" class="ajax-loader hidden">
							<h4>
								<b>Table List Loading in Progress..</b>
							</h4>
							<img
								src="${pageContext.request.contextPath}/assets/global/img/loading-circle.gif"
								class="img-responsive" />
						</div>
						<div id="loading-data-progress" class="ajax-loader hidden">
							<h4>
								<b>Table Data Loading in Progress..</b>
							</h4>
							<img
								src="${pageContext.request.contextPath}/assets/global/img/loading-circle.gif"
								class="img-responsive" />
						</div>

					</div>
					<div class="portlet-body form">
						<input type="hidden" id="tourl" value="createDataTemplate" /> <input
							type="hidden" id="currSelectedTable" value="currSelectedTable" /> <input
							type="hidden" id="queryInputTaken" value="queryInputTaken" />

						
							<div class="form-body">
								<div class="row" id="template_data">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control catch-error" id="locationnameid" name="locationname" placeholder="Enter the Location Name"> <label for="form_control_1">Location Name *</label>
											<!--  <span class="help-block">Data Set Name </span> -->
										</div>
										<span id="amit" class="help-block required"></span>
									</div>
								</div>
					<div class="form-actions noborder align-center">
						<button onclick="insertLocationRecord()"
							 class="btn blue">Create<br/></button>
						
					</div>
				</div>
				<c:if test="${not empty message}">
    <h1>${message}</h1>
</c:if>
				<div id="show-msg"></div>
			</div>
			<div class="note note-info hidden">File uploaded Successfully</div>
			<!-- END SAMPLE FORM PORTLET-->
		</div>
	</div>
</div>

<!-- END QUICK SIDEBAR -->
<!-- END CONTAINER -->
<jsp:include page="footer.jsp" />
<head>
<script src="./assets/global/plugins/jquery-ui.min.js"></script>
<script
	src="./assets/global/plugins/bootstrap-multiselect.js"></script>
<link rel="stylesheet"
	href="./assets/global/plugins/bootstrap-multiselect.css">
<script type="text/javascript">
	$(document).ready(function() {
		
		/* message*/
	/*	     		var url_string      = window.location.href; 
		        	var url = new URL(url_string);
		        	var locationname = url.searchParams.get("locationname");
		        	var project = url.searchParams.get("project");
		        	$("#show-msg").html("<div class='alert alert-info'> <strong>Location Saved!</strong></div>"); */
		
		//$('#lstTable').multiselect();
		$('#lstTable').multiselect({
			maxHeight : 200,
			buttonWidth : '500px',
			includeSelectAllOption : true,
			enableFiltering : true,
			nonSelectedText : 'Select Table'
		});
	});
</script>
<script type="text/javascript" src="./assets/global/plugins/jsapi.js"></script>
<script type="text/javascript">
	google.load("visualization", "1", {
		packages : [ "corechart" ]
	});
	var firstSelect = false;
	var datafirstSelect = false;
	var areTableDetailsDisplayed = false;
	$('#querycheckboxid').click(function() {
		
		if ($(this).prop("checked") == true) {
			$('#querytextboxiddiv').removeClass('hidden');
			
			if( $("#locationid").val() == "SnowFlake"){
				$('<span class="help-block">Table Name should be in this format DatabaseName.SchemaName.TableName</span> ').insertAfter($('#querytextboxid'));			 
			}
			
			$('#whereconditionid').addClass('hidden');
		} else if ($(this).prop("checked") == false) {
			$('#querytextboxiddiv').addClass('hidden');
			$('#whereconditionid').removeClass('hidden');

		}
	});
	var selectedOption;
	var analysisData;
	$(document).on("click", "#sample_editable1 tr td", function() {
		$(".input-hide").addClass("hidden");
		$("label").removeClass("hidden");
		$(this).find(".input-hide").removeClass("hidden");
		$(this).find("label").addClass("hidden");
		var indexRow = $(this).closest("tr").data("attr");
		var indexCol = $(this).data("attr");
	});

	$('.input-hide').bind('focus', function(e) {
		$(this).data('done', false);
	});

	$('#lstTable').on('change', function() {
		//var tableName = $(this).val();
		var currentSelection;

		var currentValues = $(this).val();
		
		if(currentValues == null){
			
		}else{
			if (currentValues.length == 1) {
				currentSelection = currentValues;
			} else {
				currentSelection = currentValues.filter(function(el) {
					return selectedOption.indexOf(el) < 0;
				});
			}
			var tablelen = $(this).val().length;
			console.log(currentSelection);

			if (currentSelection.length > 0) {
				$("#currSelectedTable").val(currentSelection);
			}
			else {
				
				if (currentValues.length == 1) {
					currentSelection = currentValues;
				} else {
					currentSelection = currentValues.filter(function(el) {
						return selectedOption.indexOf(el) < 0;
					});
				}
				selectedOption = $(this).val();
				tablelen = $(this).val().length;
				console.log(currentSelection);
		
				if (currentSelection.length > 0) {
					$("#currSelectedTable").val(currentSelection);
				}
		    }
		}
		
		
		selectedOption = $(this).val();
	
		if (tablelen == 1) {
			$('#conditionid').removeClass('hidden');
		} else {
			$('#conditionid').addClass('hidden');
		}

	});

	$('#incrementalsourceid').click(function() {
		if ($(this).prop("checked") == true) {
			$('#incrementaltype').removeClass('hidden');
		} else if ($(this).prop("checked") == false) {
			$('#incrementaltype').addClass('hidden');
		}

	});

	function submitFormDataAndCreateTemplate(profilingEnabled,advancedRulesEnabled) {
		
		
		$('#datasetupid1').addClass('hidden');
		$('#datasetupid2').addClass('hidden');
		$('#datasetupid3').addClass('hidden');
		var no_error = 1;
		
		//by defaultselected first table name in case of query
		
		//selectedTables
		
		//$("#target option:first").attr('selected','selected');
	//	var defaultVal = $("#lstTable option:first").attr('selected','selected');
		
		//alert("default Val =>"+defaultVal);

		
	//	document.getElementById('defaultfirstTblElement').value = product(2, 3);
		
		var defaultSelectedTab = "<%=session.getAttribute("FirstEleFromTablesList") %>";
		//alert("=================selected table:" + $("#currSelectedTable").val());
		//alert("1st tablename =>"+defaultSelectedTab );
		
		if(!checkInputText()){ 
			no_error = 0;
			alert('Vulnerability in submitted data, not allowed to submit!');
		}
		
		$("#profilingId").val(profilingEnabled)
		$("#advancedRulesCheckId").val(advancedRulesEnabled)
		
		console.log("profilingEnabled:" + $("#profilingId").val());
		console.log("advancedRulesEnabled:" + $("#advancedRulesCheckId").val());
		
		var currentSelectedTable = $("#currSelectedTable").val();
		
		if (currentSelectedTable.length > 0) {
			//alert("In If.................");
			$("#queryInputTaken").val("yes");
			currentSelectedTable = defaultSelectedTab;
		}
		
		//alert("queryTextboxid =>"+$("#querytextboxid").val());
		//alert("queryInputTakn =>"+$("#queryInputTaken").val());
		
		if ($("#currSelectedTable").val().length == 0
				&& $("#querytextboxid").val().length > 0
				&& $("#queryInputTaken").val().length == 0) {
			event.preventDefault();
			var isQuery = "Y";
			var currentSelection = $("#querytextboxid").val();
		} else if ($("#queryInputTaken").val().length > 0) {
			if ($('#lstTable :selected').length > 0) {
				//build an array of selected values
				var selectednumbers = [];
				$('#lstTable :selected').each(function(i, selected) {
					selectednumbers[i] = $(selected).val();
				});

				$("#selectedTables").val(JSON.stringify(selectednumbers));
			}
			$('.input_error').remove();
			if ($("#datasetid").val().length == 0) {
				console.log('datasetid');
				$(
						'<span class="input_error" style="font-size:12px;color:red">Please Enter Data Template Name</span>')
						.insertAfter($('#datasetid'));
				no_error = 0;
			}
			if ($("#locationid").val() == -1) {
				console.log('locationid');
				$(
						'<span class="input_error" style="font-size:12px;color:red">Please Choose Data Location</span>')
						.insertAfter($('#locationid'));
				no_error = 0;
			}

			//What is locationId here and why this is being used
			if (($('#locationid').val() == 'MSSQLActiveDirectory')
					|| ($('#locationid').val() == 'Vertica')
					|| ($('#locationid').val() == 'Oracle')
					|| ($('#locationid').val() == 'MSSQL')
					|| ($('#locationid').val() == 'Hive')
					|| ($('#locationid').val() == 0)) {
				console.log('locationid');

				if ($("#schemaId").val().length == 0) {
					console.log('schemaId');
					$(
							'<span class="input_error" style="font-size:12px;color:red">Please Select One</span>')
							.insertAfter($('#schemaId'));
					no_error = 0;
				}
				if (($('#locationid').val() == 'Hive')
						|| ($('#locationid').val() == 'Oracle RAC')
						|| ($('#locationid').val() == 'Oracle')
						|| ($('#locationid').val() == 'Vertica')
						|| ($('#locationid').val() == 'MSSQL')
						|| ($('#locationid').val() == 'MSSQLActiveDirectory')
						|| ($('#locationid').val() == 'Amazon Redshift')($(
								'#locationid').val() == 'Cassandra')) {
					/* if ($("#selectedTables").val().length == 0) {
						console.log('selectedTables');
						$(
								'<span class="input_error" style="font-size:12px;color:red">Please Select Tables</span>')
								.insertAfter($('#selectedTables'));
						no_error = 0;
					} */
				} else {
					if ($("#tableNameid").val().length == 0) {
						console.log('tableNameid');
						$(
								'<span class="input_error" style="font-size:12px;color:red">Please enter Table Name</span>')
								.insertAfter($('#tableNameid'));
						no_error = 0;
					}
				}
				if ($("#incrementalsourceid").prop("checked") == true) {
					if ($("#dateformatid").val().length == 0) {
						$(
								'<span class="input_error" style="font-size:12px;color:red">Please Enter DateFormat</span>')
								.insertAfter($('#dateformatid'));
						no_error = 0;
					}
					if ($("#slicestartid").val().length == 0) {
						$(
								'<span class="input_error" style="font-size:12px;color:red">Please Enter Slice Start value</span>')
								.insertAfter($('#slicestartid'));
						no_error = 0;
					}
					if ($("#sliceendid").val().length == 0) {
						$(
								'<span class="input_error" style="font-size:12px;color:red">Please Enter Slice End value</span>')
								.insertAfter($('#sliceendid'));
						no_error = 0;
					}
					var slicestartid = $("#slicestartid").val();
					var sliceendid = $("#sliceendid").val();

					if (slicestartid > 0) {
						$(
								'<span class="input_error" style="font-size:12px;color:red">Please Enter Negative value</span>')
								.insertAfter($('#slicestartid'));
						no_error = 0;
					}
					if (sliceendid > 0) {
						$(
								'<span class="input_error" style="font-size:12px;color:red">Please Enter Negative value</span>')
								.insertAfter($('#sliceendid'));
						no_error = 0;
					}
					console.log(slicestartid + "" + sliceendid);
					if (parseInt(slicestartid) >= parseInt(sliceendid)) {
						toastr
								.info('The Slice Start cannot be Greater than Slice End value');
						no_error = 0;
					}
				}

			}

			if (($('#locationid').val() == 'File System')
					|| ($('#locationid').val() == 'HDFS')
					|| ($('#locationid').val() == 'File Management')) {

				if($("#headerId").prop("checked") == false){
					if ($("#dataupload_id").val().length == 0) {
						$(
								'<span class="input_error" style="font-size:12px;color:red">Please Upload a File</span>')
								.insertAfter($('#dataupload_id'));
						no_error = 0;
					}
				}
				/* if ($("#datasetid").val().length == 0) {
					$(
							'<span class="input_error" style="font-size:12px;color:red">Please enter Dataset Name</span>')
							.insertAfter($('#datasetid'));
					no_error = 0;
				} */
				if ($("#hostName").val().length == 0) {
					console.log('hostName');
					$(
							'<span class="input_error" style="font-size:12px;color:red">Please enter Host Name</span>')
							.insertAfter($('#hostName'));
					no_error = 0;
				}
				if ($("#schemaName").val().length == 0) {
					console.log('schemaName');
					$(
							'<span class="input_error" style="font-size:12px;color:red">Please enter schemaName</span>')
							.insertAfter($('#schemaName'));
					no_error = 0;
				}
				if ($("#userName").val().length == 0) {
					console.log('userName');
					$(
							'<span class="input_error" style="font-size:12px;color:red">Please enter Username</span>')
							.insertAfter($('#userName'));
					no_error = 0;
				}
				if ($("#pwd").val().length == 0) {
					console.log('pwd');
					$(
							'<span class="input_error" style="font-size:12px;color:red">Please enter Password</span>')
							.insertAfter($('#pwd'));
					no_error = 0;
				}
			} else if (($('#locationid').val() == 'S3')) {
				if ($("#dataupload_id").val().length == 0) {
					$(
							'<span class="input_error" style="font-size:12px;color:red">Please Upload a File</span>')
							.insertAfter($('#dataupload_id'));
					no_error = 0;
				}
				/* if ($("#datasetid").val().length == 0) {
					$(
							'<span class="input_error" style="font-size:12px;color:red">Please enter Dataset Name</span>')
							.insertAfter($('#datasetid'));
					no_error = 0;
				} */
				if ($("#hostName").val().length == 0) {
					console.log('hostName');
					$(
							'<span class="input_error" style="font-size:12px;color:red">Please enter Bucket Name</span>')
							.insertAfter($('#hostName'));
					no_error = 0;
				}
				if ($("#schemaName").val().length == 0) {
					console.log('schemaName');
					$(
							'<span class="input_error" style="font-size:12px;color:red">Please enter Key</span>')
							.insertAfter($('#schemaName'));
					no_error = 0;
				}
				if ($("#userName").val().length == 0) {
					console.log('userName');
					$(
							'<span class="input_error" style="font-size:12px;color:red">Please enter Access Key</span>')
							.insertAfter($('#userName'));
					no_error = 0;
				}
				if ($("#pwd").val().length == 0) {
					console.log('pwd');
					$(
							'<span class="input_error" style="font-size:12px;color:red">Please enter Secret Key</span>')
							.insertAfter($('#pwd'));
					no_error = 0;
				}
			}

			//Add the details for Kafka here this as Kafka doesn't have a structure 

			else if (($('#locationid').val() == 'Kafka')) {
				if ($("#dataupload_id").val().length == 0) {
					$(
							'<span class="input_error" style="font-size:12px;color:red">Please Upload a File</span>')
							.insertAfter($('#dataupload_id'));
					no_error = 0;
				}
				/* if ($("#datasetid").val().length == 0) {
					$(
							'<span class="input_error" style="font-size:12px;color:red">Please enter Dataset Name</span>')
							.insertAfter($('#datasetid'));
					no_error = 0;
				} */
				if ($("#hostName").val().length == 0) {
					console.log('hostName');
					$(
							'<span class="input_error" style="font-size:12px;color:red">Please enter Bucket Name</span>')
							.insertAfter($('#hostName'));
					no_error = 0;
				}
				if ($("#schemaName").val().length == 0) {
					console.log('schemaName');
					$(
							'<span class="input_error" style="font-size:12px;color:red">Please enter Key</span>')
							.insertAfter($('#schemaName'));
					no_error = 0;
				}
				if ($("#userName").val().length == 0) {
					console.log('userName');
					$(
							'<span class="input_error" style="font-size:12px;color:red">Please enter Access Key</span>')
							.insertAfter($('#userName'));
					no_error = 0;
				}
				if ($("#pwd").val().length == 0) {
					console.log('pwd');
					$(
							'<span class="input_error" style="font-size:12px;color:red">Please enter Secret Key</span>')
							.insertAfter($('#pwd'));
					no_error = 0;
				}
			}
			
		}
		
		if (($('#locationid').val() == 'FileSystem Batch') || ($('#locationid').val() == 'S3 Batch')
				|| || ($('#locationid').val() == 'S3 IAMRole Batch')) {
			
			if ($("#schemaId").val().length == 0) {
				console.log('schemaId');
				$(
						'<span class="input_error" style="font-size:12px;color:red">Please Select One</span>')
						.insertAfter($('#schemaId'));
				no_error = 0;
			}
			
			if ($("#selectedTables").val().length == 0) {
				$(
						'<span class="input_error" style="font-size:12px;color:red">Please Select Tables</span>')
						.insertAfter($('#selectedTables'));
				no_error = 0;
			}
		
			console.log("query enabled:"+$("#querycheckboxid").prop("checked"))
			console.log("lstTable :selected"+$('#lstTable :selected').length);
			
			if($("#querycheckboxid").prop("checked") == true){
				if ($('#lstTable :selected').length > 2) {
					$('<span class="input_error" style="font-size:12px;color:red">Please Select Max two tables when query is enabled</span>')
							.insertAfter($('#selectedTables'));
					no_error = 0;
				}
				
			} 
		}
		
		 
		var amit1 = $('#amit').html();
		// alert("outside"+amit1)
		console.log(amit1);
		if (amit1 == "") {

		} else {
			console.log("inside if");
			//toaster.info("Schema created successfully");
			//$('<span class="input_error" style="font-size:12px;color:red">already exist Database Name</span>').insertAfter($('#descriptionid'));
			no_error = 0;
		}
		if (no_error == 0) {
			console.log('error');
			$('.btn').removeClass('hidden');
			return false;
		} else {
			$('.btn').addClass('hidden');

		}
		
	}

	$('#headerId').change(
			function() {
				console.log("headerId changed")
				console.log("isHeaderPresent:"+($('#headerId').is(':checked')))
				if($('#headerId').is(':checked')){
					$('#uploaddivid').addClass('hidden');
				} else {
					$('#uploaddivid').removeClass('hidden');
				}	
	});
	
	$('#schemaId')
			.change(
					function() {
						var location = $('#locationid').val();
						if (location == 'Hive' || location == 'Hive Kerberos'
								|| location == 'Hive knox'
								|| location == 'Oracle RAC'
								|| location == 'Oracle'
								|| location == 'Postgres'
								|| location == 'Teradata'
								|| location == 'Vertica' || location == 'MSSQL'
								|| location == 'MSSQLActiveDirectory'
								|| location == 'Amazon Redshift'
								|| location == 'Cassandra'
								|| location == 'SnowFlake' 
								|| location == 'FileSystem Batch'
								|| location == 'S3 Batch') {
							var selectedVal = $this.val();
							$('#lstTable').empty();
							var form_data = {
								schemaId : $("#schemaId").val(),
								locationName : $("#locationid").val(),
							};
							$
									.ajax({
										url : './populateTables',
										type : 'GET',
										beforeSend : function() {
											$('#loading-progress').removeClass(
													'hidden').show();
										},
										datatype : 'json',
										data : form_data,
										success : function(obj) {
											$(obj)
													.each(
															function(i, item) {
																$('#lstTable')
																		.append(
																				$(
																						'<option>',
																						{
																							value : item,
																							text : item
																						}));
															});
											$('#lstTable').multiselect(
													'rebuild');
											$('#lstTable').multiselect({
												includeSelectAllOption : true
											});
											$('#s4_tablename_id').removeClass(
													'hidden');
										},
										error : function() {

										},
										complete : function() {
											$('#loading-progress').addClass(
													'hidden');
										}
									});
						} else {
							$('#s4_tablename_id_2').removeClass('hidden');
							$('#conditionid').removeClass('hidden');
						}
					});
	$('#locationid').change(
			function() {
				$this = $(this);
				var selectedVal = $this.val();
				if (selectedVal == 'Hive' || selectedVal == 'Oracle'
						|| selectedVal == 'MSSQL'
						|| selectedVal == 'MSSQLActiveDirectory'
						|| selectedVal == 'Vertica'
						|| selectedVal == 'Postgres'
						|| selectedVal == 'Teradata'
						|| selectedVal == 'Cassandra'
						|| selectedVal == 'Amazon Redshift'
						|| selectedVal == 'Hive Kerberos'
						|| selectedVal == 'Hive knox'
						|| selectedVal == 'Oracle RAC'
						|| selectedVal == 'SnowFlake'
						|| selectedVal == 'FileSystem Batch'
						|| selectedVal == 'S3 Batch') {
					$('#maprdivid').addClass('hidden');
					$('#s4_form_id').removeClass('hidden').show();
					$('#uploaddivid').addClass('hidden');
					$('#s3_form_id').addClass('hidden');
					$('#kafka_form_id').addClass('hidden');
					$('#checkid').addClass('hidden');
					$('#sourcedivid').addClass('hidden');
					$('#s4_tablename_id').addClass('hidden');
					$('#s4_tablename_id_2').addClass('hidden');
				} else if (selectedVal == 'File System'
						|| selectedVal == 'HDFS' || selectedVal == 'MapR FS') {
					$('#maprdivid').addClass('hidden');
					//$('#host-id').html('Host Name*');
					$('#fileType').html('Data Source File*');
					$('#checkid').removeClass('hidden');
					$('#kafka_form_id').addClass('hidden');
					$('#sourcedivid').removeClass('hidden').show();
					//$('#sourcedivid').addClass('hidden');
					$('#s4_form_id').addClass('hidden');
					//$('#uploaddivid').removeClass('hidden');
					$('#s3_form_id').removeClass('hidden');
					// $('#host-id-container').removeClass('hidden').show();
					$('#host-id1').html('Host URI*');
					$('#folder-id1').html('Folder*');
					$('#user-id').html('User Login*');
					$('#user-pwd').html('Password*');
				} else if (selectedVal == 'File Management') {
					$('#maprdivid').addClass('hidden');
					//$('#host-id').html('Host URI or Host IP*');
					$('#fileType').html('File Management CSV*');
					$('#uploaddivid').removeClass('hidden');
					$('#s3_form_id').removeClass('hidden');
					//hide remaining
					$('#s4_form_id').addClass('hidden');
					$('#kafka_form_id').addClass('hidden');
					$('#checkid').addClass('hidden');
					$('#sourcedivid').addClass('hidden');
				} else if (selectedVal == 'S3') {
					$('#maprdivid').addClass('hidden');
					$('#host-id1').html('Bucket Name*');
					$('#folder-id1').html('Folder / File Name*');
					$('#user-id').html('Access Key*');
					$('#user-pwd').html('Secret Key*');
					$('#kafka_form_id').addClass('hidden');
					$('#fileType').html('Data Source File*');
					$('#checkid').removeClass('hidden');
					$('#sourcedivid').removeClass('hidden').show();
					//$('#sourcedivid').addClass('hidden');
					$('#s4_form_id').addClass('hidden');
					//$('#uploaddivid').removeClass('hidden');
					$('#s3_form_id').removeClass('hidden');
					//This is added for Kafka
				} else if (selectedVal == 'Kafka') {
					$('#maprdivid').addClass('hidden');
					$('#host-id1').html('Broker url*');
					$('#folder-id1').html('Topic Name*');
					//$('#user-id').parentNode.style.display='none';
					//$('#user-pwd').parentNode.style.display='none';
					$('#s3_form_id').addClass('hidden');
					$('#incremental_div').addClass('hidden');
					$('#usercred_div').addClass('hidden');

					$('#fileType').html('Data Source File*');
					$('#sourcedivid').removeClass('hidden').show();
					//$('#sourcedivid').addClass('hidden');
					$('#s4_form_id').addClass('hidden');
					$('#uploaddivid').removeClass('hidden');
					$('#kafka_form_id').removeClass('hidden');
				}

				else if (selectedVal == 'MapR DB') {
					//$('#host-id').html('Host Name*');
					$('#maprdivid').removeClass('hidden');
					$('#fileType').html('Data Source File*');
					$('#checkid').removeClass('hidden');
					$('#sourcedivid').removeClass('hidden').show();
					//$('#sourcedivid').addClass('hidden');
					$('#s4_form_id').addClass('hidden');
					$('#uploaddivid').removeClass('hidden');
					$('#s3_form_id').removeClass('hidden');
					// $('#host-id-container').removeClass('hidden').show();
					$('#host-id1').html('Host URI*');
					$('#folder-id1').html('Folder*');
					$('#user-id').html('User Login*');
					$('#user-pwd').html('Password*');
				}
				
			});
	function viewChart(type, index) {
		var response = analysisData[type];
		var listOfValues;
		if (response != "") {
			var elements = jQuery.parseJSON(response);
			$.each(elements, function(i, item) {
				if (index == "") {
					index = item.key;
				}
				if (item.key == index) {
					listOfValues = item.dataMap;
					return false;
				}
			});
		}

		if (typeof listOfValues !== "undefined" && listOfValues != "") {
			var data1 = new google.visualization.DataTable();
			var colorArray = [ 'blue', 'yellow', 'pink' ];
			var index = 0;
			if (type == 'Numerical') {
				data1.addColumn('number', 'value');
				data1.addColumn('number', 'data');
				//data1.addColumn('string',{ role: "style" });
				$.each(listOfValues, function(key, value) {
					data1.addRow([ parseFloat(key), listOfValues[key] ]);
				});
			} else if (type == 'String & Character') {
				data1.addColumn('string', 'value');
				data1.addColumn('number', 'data');
				$.each(listOfValues, function(key, value) {
					data1.addRow([ key, listOfValues[key] ]);
				});
			} else if (type == 'Date') {
				data1.addColumn('date', 'value');
				data1.addColumn('number', 'data');
				$.each(listOfValues, function(key, value) {
					data1.addRow([ new Date(key), listOfValues[key] ]);
				});
			}

			var title = "Data Distribution:".concat(index);
			var options = {
				'title' : title,
				titleTextStyle : {
					color : '#cc0000'
				},
				hAxis : {
					title : 'Data',
					direction : -1,
					showTextEvery : 2,
					slantedText : true,
					textStyle : {
						color : '#009900',
						fontSize : 12,
						bold : true
					},
					titleTextStyle : {
						color : '#6600cc',
						bold : true
					}
				},
				vAxis : {
					minValue : 0,
					textStyle : {
						color : '#009900',
						fontSize : 12,
						bold : true
					},
					titleTextStyle : {
						color : '#6600cc',
						bold : true
					}
				},

				width : 1200,
				colors : [ 'ff6666' ],
				bar : {
					groupWidth : '70%'
				},
				isStacked : true
			};

			var chart = new google.visualization.ColumnChart(document
					.getElementById('chartDiv'));
			chart.draw(data1, options);
			$('#chartDiv').removeClass('hidden');
		} else {
			$('#chartDiv').addClass('hidden');
		}

	}

	/* collect selected projec id and load it into globalProjectId */
	var globalProjectId = "";
	$('#projectid span').on('click', function(){
		console.log($(this).text());
		globalProjectId = $(this).text();
	});
	
	/*
	Code Updated : 12thApril2020
	Code by : Anant Mahale
	Post call at UserSettingsController.java 
	insert location record
	*/
	function insertLocationRecord(){
		
		console.log("addNewLocation : insertLocationRecord :: "+globalProjectId);
		var strErrorMsg = "";
		if($("#locationnameid").val() == "" ){
			 strErrorMsg = strErrorMsg + 'Please Enter Location Name';
		}
		
		/*if(globalProjectId == ""){
			 strErrorMsg = strErrorMsg + '<br> Please Select Project ID';
		}*/
		
		
		
		if(strErrorMsg != ""){
			 toastr.info(strErrorMsg);
		}else{
			$.ajax({
		        type: 'POST',
		        url: "./insertNewLocation",
		        async: false,
		        headers: {'token':$("#token").val()},
		        data:{
		        	locationname : $("#locationnameid").val()
		        	//project : globalProjectId
		        },
		        success: function (message) {
		        	var j_obj = $.parseJSON(message);
                    if (j_obj.hasOwnProperty('success')) {
                   	 toastr.info('New Location Created Successfully');
           
                    } else {
                        if (j_obj.hasOwnProperty('fail')) {
                       	 toastr.info('There was a problem.');
                        }
                    }
		        },
		        error: function (textStatus, errorThrown) {
		            Success = false;
		        }
		    });	
		}
		
	}
</script>
</head>