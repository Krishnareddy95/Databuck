
<head>
<!-- <link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.10.15/css/jquery.dataTables.min.css">
	<link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/fixedcolumns/3.2.2/css/fixedColumns.dataTables.min.css"> -->
</head>
<%@page import="com.databuck.service.RBACController"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<jsp:include page="header.jsp" />

<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<style>
	.modal-footer button {
		margin-left: 20px;
	}
	.modal-title {
		margin-left: 20px;
	}	

	td.no-edit, tr.no-edit {
		 background-color: #e2e2e2;
	}

	td[data-attr="domainName"] {
		background-color: #e2e2e2 !important;
	}

	.dataTables_scrollBody{
            overflow-y: hidden !important;
   	 	}
</style>
<div class="page-content-wrapper">
	<!-- BEGIN CONTENT BODY -->
	<div class="page-content">
		<!-- BEGIN PAGE TITLE-->
		<!-- END PAGE TITLE-->
		<!-- END PAGE HEADER-->
	
		<div class="row">
			
			<div class="col-md-12">
				<!-- BEGIN EXAMPLE TABLE PORTLET-->
			<div class="portlet light bordered">
					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold ">Global Thresholds </span>
						</div>
					</div>
					<div>
					<button class="btn btn-primary" id="insertRow" data-toggle="modal"
							data-target="#myModal">Add New Column</button>
					</div>
					<!-- <din>
					  <button class="btn" id="addRow">addRow</button>
					</din> -->
						<!-- The Modal -->
						<div class="modal" id="myModal">
							<div class="modal-dialog"
								style="display: inline-block; position: fixed; top: 0; bottom: 0; left: 0; right: 0; height: 100px; margin: auto;">
								<div class="modal-content">

									<!-- Modal Header -->
									<div class="modal-header">
										<button type="button" class="close" data-dismiss="modal">&times;</button>									
										<h4 class="modal-title">Insert New Global Threshold Column</h4>
									</div>

									<!-- Modal body -->
									<div class="modal-body" id="newValues">
									
										<div style="padding-left: 20px;padding-right: 221px;">
											<label>Domain</label><span>  </span>
											<select class= form-control id="newDomainId">											
												<c:forEach var="aListDomains" items="${aListDomains}">												
													<option value="${aListDomains.domainId}">${aListDomains.domainName}</option>
												</c:forEach>	
											</select>
											<br/>
											<label>Column Name</label><span></span><input type=text class= form-control id="newColumnName" />
										</div >
										
									</div>
									<!-- Modal footer -->
									<div style="text-align: left;" class="modal-footer">
										<button type="submit" class="btn btn-success" id="insertcall">Submit</button>
										<button type="button" class="btn btn-danger"	data-dismiss="modal">Cancel</button>
									</div>

								</div>
							</div>
						</div>
					<div class="portlet-body">
						<!--<table class="stripe row-border order-column" cellspacing="0"	width="100%" id="sample_editable">-->
						<table cellspacing="0"	width="100%" id="sample_editable">
							<thead>
								<tr class='no-edit'>
									<th>Domain</th>
									<th>Column Name</th>
									<th>NullCheck Threshold</th>
									<th>LengthCheck Threshold</th>
									<th>NumFingerprint Threshold</th>
									<!--<th>TextFingerprint Threshold</th> -->
									<th>DataDrift Threshold</th>
									<th>RecordAnomaly Threshold</th>		


									<!-- <th>hashValue<br> <a
										href="hashValueyes?idData=${idData}">select</a></th>
									<th>KBE</th>
									<th>outOfNormStat</th>
									<th>outOfNormStatThreshold</th> -->

								</tr>
							</thead>
							<tbody id="listGlobalThresholdstable">
								<c:forEach var="listglobalthresholds"
									items="${listGlobalThresholds}">
									<span style="display: none" class="source-item-id">${listglobalthresholds.globalColumnName}</span>
									<tr class="source-item" data-attr="${listglobalthresholds.globalColumnName}">

										<td class="source-domainName no-edit" data-attr="domainName">
											<label id="globalColumnName_${listglobalthresholds.domainName}_label">${listglobalthresholds.domainName} </label>
											<input id="globalColumnName_${listglobalthresholds.domainName}_text" type="text" class="hidden input-hide" value="${listglobalthresholds.domainId}" />
										</td>									
										
										<td class="source-globalColumnName no-edit" data-attr="globalColumnName">
											<label id="globalColumnName_${listglobalthresholds.globalColumnName}_label">${listglobalthresholds.globalColumnName} </label>
											<input id="globalColumnName_${listglobalthresholds.globalColumnName}_text" type="text" class="hidden input-hide" value="${listglobalthresholds.globalColumnName}" />
										</td>
											
										<!-- 
										
										<td class="source-primaryKey " data-attr="primaryKey"><label
											id="primaryKey_${listglobalthresholds.globalColumnName}_label">
												${listdatadefinition.primaryKey} </label> <input
											id="primaryKey_${listdatadefinition.idColumn}_text"
											type="text" class="hidden input-hide col-xs-9"
											value="${listdatadefinition.primaryKey}" /></td> 
										-->
										

										<td class="source-nullCountThreshold"
											data-attr="nullCountThreshold"><label
											id="nullCountThreshold_${listglobalthresholds.globalColumnName}_label">
												${listglobalthresholds.nullCountThreshold} </label> <input
											id="nullCountThreshold_${listglobalthresholds.globalColumnName}_text"
											type="text" class="hidden input-hide col-xs-9"
											value="${listglobalthresholds.nullCountThreshold}" /></td>
											
										<td class="source-lengthCheckThreshold"
											data-attr="lengthCheckThreshold"><label
											id="lengthCheckThreshold_${listglobalthresholds.globalColumnName}_label">
												${listglobalthresholds.lengthCheckThreshold} </label> <input
											id="lengthCheckThreshold_${listglobalthresholds.globalColumnName}_text"
											type="text" class="hidden input-hide col-xs-9"
											value="${listglobalthresholds.lengthCheckThreshold}" /></td>
											
										<td class="source-numericalThreshold"
											data-attr="numericalThreshold"><label
											id="numericalThreshold_${listglobalthresholds.globalColumnName}_label">
												${listglobalthresholds.numericalThreshold} </label> <input
											id="numericalThreshold_${listglobalthresholds.globalColumnName}_text"
											type="text" class="hidden input-hide col-xs-9"
											value="${listglobalthresholds.numericalThreshold}" /></td>
											
										<!--	
										<td class="source-stringStatThreshold"
											data-attr="stringStatThreshold"><label
											id="stringStatThreshold_${listglobalthresholds.globalColumnName}_label">
												${listglobalthresholds.stringstatThreshold} </label> <input
											id="stringStatThreshold_${listglobalthresholds.globalColumnName}_text"
											type="text" class="hidden input-hide col-xs-9"
											value="${listglobalthresholds.stringstatThreshold}" /></td>
										-->
										
										<td class="source-dataDriftThreshold"
											data-attr="dataDriftThreshold"><label
											id="dataDriftThreshold_${listglobalthresholds.globalColumnName}_label">
												${listglobalthresholds.dataDriftThreshold} </label> <input
											id="dataDriftThreshold_${listglobalthresholds.globalColumnName}_text"
											type="text" class="hidden input-hide col-xs-9"
											value="${listglobalthresholds.dataDriftThreshold}" /></td>
											
										<td class="source-recordAnomalyThreshold"
											data-attr="recordAnomalyThreshold"><label
											id="recordAnomalyThreshold_${listglobalthresholds.globalColumnName}_label">
												${listglobalthresholds.recordAnomalyThreshold} </label> <input
											id="recordAnomalyThreshold_${listglobalthresholds.globalColumnName}_text"
											type="text" class="hidden input-hide col-xs-9"
											value="${listglobalthresholds.recordAnomalyThreshold}" /></td>
										


										<%-- <td class="source-hashValue" data-attr="hashValue">
										<label id="hashValue_${listdatadefinition.idColumn}_label"> ${listdatadefinition.hashValue} </label>
										<input id="hashValue_${listdatadefinition.idColumn}_text" type="text" class="hidden input-hide" value="${listdatadefinition.hashValue}" />
										</td>
										<td class="source-kbe" data-attr="KBE">
										<label id="KBE_${listdatadefinition.idColumn}_label"> ${listdatadefinition.KBE} </label>
										<input id="KBE_${listdatadefinition.idColumn}_text" type="text" class="hidden input-hide" value="${listdatadefinition.KBE}" />
										</td>
										<td class="source-outOfNormStat" data-attr="outOfNormStat">
										<label id="outOfNormStat_${listdatadefinition.idColumn}_label"> ${listdatadefinition.outOfNormStat} </label>
										<input id="outOfNormStat_${listdatadefinition.idColumn}_text" type="text" class="hidden input-hide" value="${listdatadefinition.outOfNormStat}" />
										</td>
										<td class="source-outOfNormStatThreshold" data-attr="outOfNormStatThreshold">
										<label id="outOfNormStatThreshold_${listdatadefinition.idColumn}_label"> ${listdatadefinition.outOfNormStatThreshold} </label>
										<input id="outOfNormStatThreshold_${listdatadefinition.idColumn}_text" type="text" class="hidden input-hide" value="${listdatadefinition.outOfNormStatThreshold}" />
										</td> --%>

									</tr>
								</c:forEach>
							</tbody>
						</table>
						<!-- <a href="" class="btn blue" >Add DataBlend</a> --->						
					</div>
				</div>
				<!-- END EXAMPLE TABLE PORTLET-->
			</div>
		</div>
	</div>
</div>
<jsp:include page="footer.jsp" />

// Load JWF framework module on this page
<head>
	<link rel="stylesheet" href="./assets/jwf/content/JwfSpaInfra.css">
	<script src="./assets/jwf/scripts/JwfSpaInfra.js"></script>
</head>

<script type="text/javascript">
	$("#listGlobalThresholdstable tr td").click(function(oEvent) {
		var sColName = $(this).data("attr"), sNotEditableColumns = 'globalColumnName,domainName';
		
		if (sNotEditableColumns.indexOf(sColName) > -1) {
			PageCtrl.debug.log('data table td click', "cannot edit column '{c}'".replace("{c}", sColName));			
		} else {			
			$(".input-hide").addClass("hidden");
			$("label").removeClass("hidden");
			
			$(this).find(".input-hide").removeClass("hidden");
			$(this).find("label").addClass("hidden");
			
			var indexRow = $(this).closest("tr").data("attr"), indexCol = $(this).data("attr");
			PageCtrl.debug.log('Entering in edit mode for', indexRow + ',' + indexCol);			
		}			
	})

	$('.input-hide').bind('focus', function(e) {
		$(this).data('done', false);
	});

	$(document)
			.ready(
					function() {					
						initializeJwfSpaInfra();       // Load JWF framework module on this page	
						console.log("documentready");
						$(".input-hide")
								.bind(
										'blur keypress',
										function(e) {
											console.log(e.type);
											if (e.type == 'blur'
													|| e.keyCode == '13') {
												if (!$(this).data('done')) {
													$(this).data('done', true);
													var indexRow = $(this)
															.closest("tr")
															.data("attr");
													var indexCol = $(this)
															.closest("td")
															.data("attr");
													var columnValue = $(
															"#" + indexCol
																	+ "_"
																	+ indexRow
																	+ "_text")
															.val();

													var form_data = {
														idColumn : indexRow,
														columnName : indexCol,
														columnValue : columnValue
													};
													console.log(form_data);
													$
															.ajax({

																url : "./updateGlobalThreshold",
																type : 'POST',
																headers: { 'token':$("#token").val()},
																datatype : 'json',
																data : form_data,
																success : function(
																		message) {
																	
																  
																	
																	
																	var j_obj = $
																			.parseJSON(message);
																	if (j_obj
																			.hasOwnProperty('success')) {
																		$(
																				"#"
																						+ indexCol
																						+ "_"
																						+ indexRow
																						+ "_label")
																				.html(
																						j_obj.columnValue);
																		toastr
																				.info(j_obj.success);
																		setTimeout(
																				function() {
																					location.reload();
																				},
																				1000);
																	} else {
																		if (j_obj
																				.hasOwnProperty('fail')) {
																			toastr
																					.info(j_obj.fail);
																			setTimeout(
																					function() {
																						location.reload();
																					},
																					1000);
																		}
																	}
																},
																error : function(
																		xhr,
																		textStatus,
																		errorThrown) {
																}
															});
													
													
													
													$(
															"#listGlobalThresholdstable tr td")
															.find(".input-hide")
															.addClass("hidden");
													$(
															"#listGlobalThresholdstable tr td")
															.find("label")
															.removeClass(
																	"hidden");
												}
											}
										});
					});
	$(document).ready(
			function() {
				var oTable = $('#sample_editable').DataTable(
						{
							//scrollY : "280px",
							scrollY : true,
							scrollX : true,
							scrollCollapse : true,
							paging : true,
							
							"aoColumnDefs" : [ {
								'bSortable' : true,
								'aTargets' : [ 0,1, 2, 3, 4]
							} ]
						});
				/*  $(".source-displayName").css({"padding-bottom": "20px"});  */
				$('#sample_editable')
				
			});
	$('[data-toggle="popover"]').popover({
		'trigger' : 'hover',
		'placement' : 'top',
		'container' : 'body'
	});
</script>
<script>

$("#insertcall").on("click", function() {
	
	//if(!checkInputText()){ 
		//alert('Vulnerability in submitted data, not allowed to submit!');
	//}else{
		
		var no_error = 1;
		if($('#newColumnName').val().length == 0){
			console.log('newColumnName');
			alert("Please Enter Column Name");
			return false;
		}
		var form_data = {
				columnName : $("#newColumnName").val(),
				domainId : $("#newDomainId").val()			
		}
		$("#myModal").modal("hide");
		$.ajax({
	
			url : "./insertGlobalThreshold",
			type : 'POST',
			headers: { 'token':$("#token").val()},
			datatype : 'json',
			data : form_data,
			success : function(
					message) {
				
			  
				
				
				var j_obj = $
						.parseJSON(message);
				if (j_obj
						.hasOwnProperty('success')) {
					toastr
					.info("New Column Inserted successfully");
					setTimeout(
							function() {
								location.reload();
							},
							1000);
				} else {
					if (j_obj
							.hasOwnProperty('fail')) {
						toastr
								.info(j_obj.fail);
						
					}
				}
			},
			error : function(
					xhr,
					textStatus,
					errorThrown) {
			}
		});
	//}
	
	
	
});

	

	 



</script>