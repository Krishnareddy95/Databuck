<%@page import="com.databuck.service.RBACController"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<head>
	<link rel="stylesheet" href="./assets/jwf/content/JwfSpaInfra.css">
	<script src="./assets/jwf/scripts/JwfSpaInfra.js"></script>
</head>
<jsp:include page="container.jsp" />
<link rel="stylesheet" href="./assets/css/custom-style.css" type="text/css" />
<style>
	table#SearchTable {
	  width: 100%;
	  border: 1px solid #dddddd;
	}

	table#SearchTable tr td {
	  border: 0px solid #dddddd;
	  text-align: left;
	  padding: 8px !important;
	  width: 25%;
	  valign: top;
	}

	table#SearchTable td label {
		font-weight: bold;
		font-family: "Open Sans",sans-serif;
	}

	table#SearchTable td span {
		font-size: 13.5;
	}
	
	div#AvailableProjects {
		margin-top: 5px; 
		border: 2px solid #dddddd; 
		height: 130px; 
		padding: 7px; 
		overflow-x: hidden; 
		overflow-y: auto;
	}

	div#AvailableProjects label {
		font-family: "Open Sans",sans-serif;
		font-weight: normal !important;
	}
	
	.DataBtns {
		width: 100% !important;
		text-align: center !important;
		font-size: 12.5px !important;
		letter-spacing: 2px;
		margin-top: 17px;
	}
	

</style>


<!--============= BEGIN CONTENT BODY============= -->


<!--*****************      BEGIN CONTENT **********************-->

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
							<span class="caption-subject bold "> Extended Template</span>
						</div>
					</div>
                   <table id='SearchTable'>
						<tr>
							<td rowspan="2" valign="top">
								<label>Project Name*</label>
								<div style="float: right;">
									<img style="cursor: pointer;" id="select-all-projects" src="./assets/img/select-all.png" title="Select All" width="25" height="25">								
									<img style="cursor: pointer;" id="clear-all-projects" src="./assets/img/clear-all.png" title="Clear All" width="25" height="25">
								</div>								
								</br>
								<div id="AvailableProjects"></div>
							</td>
							<td>
								<label>From Date</label></br>
								<input class="form-control" id='FromDate' name='date' type='text' value="${toDate}">
							</td>
							<td>
								<label>To Date</label></br>
								<input class="form-control" id='ToDate' name='date' type='text' value="${fromDate}">
							</td>
							<td rowspan="2">
								<label>Search Rules By</label></br>
								<input type="radio" id="ByProjectAndDate" name="SearchByOption" value="1" checked>
								
								<span>Project and Date Range</span><br>
								<input type="radio" id="ByProjectAndDate" name="SearchByOption" value="2">
								
								<span>Project and Search Text</span><br>
								<input type="radio" id="ByAll" name="SearchByOption" value="3">
								
								<span>Project, Date Range and Search Text</span></br>
								
								<button id='SearchTemplate' class="btn btn-primary DataBtns">Search Template</button>
							</td>
						</tr>
						<tr>
							<td colspan="2">
								<label>Search Text in rule or template name or expression</label></br>
								<input class="form-control" id='SearchText' type='text'>
							</td>
						</tr>
					</table>
							
		
				<div class="portlet-body">
						<table class="table table-striped table-bordered  table-hover" id="extendedview" style="width: 100%;">
						<%-- 	<thead>
								<tr>
									<th>Extended Template Name</th>
									<th>Description</th>
									<th>Data Template</th>
									<th>Project Name</th>
									<th style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">Created_On</th>
									<th>Created By</th>
									<%
										boolean flag = false;
										flag = RBACController.rbac("Extend Template & Rule", "R", session);
										//System.out.println("flag=" + flag);
										if (flag) {
									%>
									<th></th>
									<%
										}
									%>
									<%
										boolean Add = false;
										Add = RBACController.rbac("Extend Template & Rule", "C", session);
										if (Add) {
									%>
									<th></th>
									<%
										}
									%>
									<%
										boolean Delete = false;
										Delete = RBACController.rbac("Extend Template & Rule", "D", session);
										if (Delete) {
									%>
									<th></th>
									<%
										}
									%>
								</tr>
							</thead> --%>
							<thead>
								<tr>
									<th>Extended Template Name</th>
									<th>Description</th>
									<th>Data Template</th>
									<th>Project Name</th>
									<th>Created_On</th>
									<th>Created By</th>
									<!-- <th>IdDataBlend</th>
									<th>Template Id</th> -->
								</tr>
							</thead>
							<%-- <tbody>
								<c:forEach var="templateview" items="${templateview}">
									<tr>
										<td>${templateview.name}</td>
										<td>${templateview.lbdescription}</td>
										<td>${templateview.lsdescription}</td>
										<td>${templateview.projectName}</td>
										<td style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">${templateview.createdAt}</td>
										<td>${templateview.createdByUser}</td>
										<%
											if (flag) {
										%>
										<td><a onClick="validateHrefVulnerability(this)" 
											href="index?idDataBlend=${templateview.idDataBlend}&idData=${templateview.idData}&name=${templateview.name}&lbdescription=${templateview.lbdescription}&lsdescription=${templateview.lsdescription}">View</a>
										</td>
										<%
											}
										%>
										<%
											if (Add) {
										%>
										<td><a onClick="validateHrefVulnerability(this)" 
											href="createcolumn?idDataBlend=${templateview.idDataBlend}&idData=${templateview.idData}&name=${templateview.name}&lbdescription=${templateview.lbdescription}&lsdescription=${templateview.lsdescription}"><i
												class="fa fa-plus"></i> Add Derived Column/Filter</a></td>
										<%
											}
										%>
										<%
											if (Delete) {
										%>
										<td><span style="display: none">
												deleteTemp?idDataBlend=${templateview.idDataBlend}</span> <a onClick="validateHrefVulnerability(this)" 
											href="deleteTemp?idDataBlend=${templateview.idDataBlend}"
											data-toggle="confirmation" data-singleton="true"><i
												style="margin-left: 20%; color: red" class="fa fa-trash "></i></a>
										</td>
										<%
											}
										%>
									</tr>
								</c:forEach>
							</tbody> --%>
						</table>
					</div>
				</div>
				<!--       *************END EXAMPLE TABLE PORTLET*************************-->

			</div>
		</div>
	</div>
</div>
<jsp:include page="footer.jsp" />
<head>
<script src="./assets/global/plugins/jquery.min.js" type="text/javascript"></script>
<script src="./assets/global/plugins/jquery.dataTables.min.js"	type="text/javascript"></script>

<script src="./assets/global/plugins/jquery-ui.min.js"></script>
<script src="./assets/global/plugins/bootstrap-multiselect.js"></script>
<link rel="stylesheet" href="./assets/global/plugins/bootstrap-multiselect.css">

<link rel="stylesheet" href="./assets/jwf/content/JwfSpaInfra.css">
<script src="./assets/jwf/scripts/JwfSpaInfra.js"></script>
<script type="text/javascript" src="./assets/global/plugins/bootstrap-datepicker.min.js"></script>
<link rel="stylesheet" href="./assets/global/plugins/bootstrap-datepicker3.css" />

 <script>
$(document).ready(function() {
	var table;
	table = $('.table').dataTable({
		  	
		  	order: [[ 3, "desc" ]]
	      });
});

//from here...........
var ManageextendedView = {
		SelectedProjectId: ${SelectedProjectId},
		ViewPageData: {},
		IndexOfRecord: -1,
		setup: function() {
			$('#SearchTemplate').on('click', ManageextendedView.getPaginatedExtendedList);
			$('div img').on('click', ManageextendedView.handleActionClicks);
			$('div img').on('click', ManageextendedView.handleActionClicks);			
		},
	
	
	getPaginatedExtendedList:  function(tEvent) {
			var sViewTable = '#extendedview', oSearchParameters = ManageextendedView.getSearchParameters();			
			
			if(!checkInputText()){
				alert('Vulnerability in submitted data, not allowed to submit!');
				return;
			}
			
			if (Object.keys(oSearchParameters).length > 0) {
				JwfAjaxWrapper({
					WaitMsg: 'Loading extended list',
					Url: 'getPaginatedExtendedList',
					Headers:$("#token").val(),
					Data: oSearchParameters,
					CallBackFunction: function(tResponse) {
						ManageextendedView.ViewPageData = tResponse;
					
						PageCtrl.debug.log('Server response got is', ManageextendedView.ViewPageData);						
						
				  	    processDataActionIcons(ManageextendedView.ViewPageData.ViewPageDataList); 
						fillAvailableProject(ManageextendedView.ViewPageData.AllProjectList, ManageextendedView.ViewPageData.SelectedProjectId);			
				
						$(sViewTable).DataTable ({
							"data" : ManageextendedView.ViewPageData.ViewPageDataList,
							"columns": [
								{ "data": "ExtendedTemplateName" },
								{ "data": "Description" },
								{ "data": "DataTemplate" },
								{ "data": "ProjectName" },
								{ "data": "Created_On" },
								{ "data": "CreatedBy" },
								/* { "data": "IdDataBlend" },
								{ "data": "TemplateId" },
								 */
						    ],
							scrollX: true,
							scrollCollapse: true,
							scrollY: '400px',						
							autoWidth: false,	
							lengthMenu: [ 200, 100, 50 ],
							order: [[ 0, 'asc' ]],
							destroy: true,
							orderClasses: false,
							drawCallback: function( oSettings ) {
								 $(sViewTable + ' a').off('click', ManageextendedView.handleActionClicks).on('click', ManageextendedView.handleActionClicks);
								$(sViewTable + ' button').off('click', ManageextendedView.handleActionClicks).on('click', ManageextendedView.handleActionClicks);
							}						
						})
					}
				});
			}
			
			//changes from here 
					function processDataActionIcons(aDataextendDataList) {
				
					var	sAnkerViewTmpl = '<a onClick="validateHrefVulnerability(this)"  style="margin-right: 7px;" title="View" id="anker-view-{0}" {1}><i class="fa fa-eye" aria-hidden="true"></i></a>',
					    sAnkerDeleteTmpl = '<a onClick="validateHrefVulnerability(this)"  style="margin-right: 7px;" title="Delete" id="anker-delete-{0}" {1}><i style="color: red" class="fa fa-trash"></i></a>',
					    sAnkerAddTmpl=  '<a onClick="validateHrefVulnerability(this)" style="margin-right: 7px; title="Add" id="anker-add-{0}" {1}><i class="fa fa-plus"></i> Add Derived Column/Filter</a>',

						sViewActionIcon = '',sDeleteActionIcon = '', sAnkerAddIcon ='',
						oSecurityFlags =  ManageextendedView.ViewPageData.SecurityFlags,
						lIsUserhaveAtLeastOneAccess = ( (oSecurityFlags.Create) || (oSecurityFlags.Update) || (oSecurityFlags.Delete) ) ? true : false;
				
						aDataextendDataList.forEach( function(oDataextendRecord, nIndex) {				
							oDataextendRecord.OrgExtendedTemplateName = oDataextendRecord.ExtendedTemplateName;				

					if (oSecurityFlags.Create) {
						
						sViewActionIcon = sAnkerViewTmpl.replace('{0}',oDataextendRecord.IdDataBlend).replace('{1}', 'href="index?idDataBlend='+oDataextendRecord.IdDataBlend+'&idData='+oDataextendRecord.TemplateId+ '&name='+ oDataextendRecord.ExtendedTemplateName+  '&lbdescription=' + oDataextendRecord.Description +'&lsdescription=' + oDataextendRecord.DataTemplate +'"');
						sDeleteActionIcon = sAnkerDeleteTmpl.replace('{0}',oDataextendRecord.IdDataBlend).replace('{1}', 'href="deleteTemp?idDataBlend='+ oDataextendRecord.IdDataBlend +'"'); 
						sAnkerAddIcon = sAnkerAddTmpl.replace('{0}',oDataextendRecord.IdDataBlend).replace('{1}', 'href="createcolumn?idDataBlend='+ oDataextendRecord.IdDataBlend+'&idData='+oDataextendRecord.TemplateId+ '&name='+ oDataextendRecord.ExtendedTemplateName+  '&lbdescription=' + oDataextendRecord.Description +'&lsdescription=' + oDataextendRecord.DataTemplate +'"'); 
						
					}
					
				if (lIsUserhaveAtLeastOneAccess) {
						oDataextendRecord.ExtendedTemplateName = oDataextendRecord.ExtendedTemplateName + '</br>';

						oDataextendRecord.ExtendedTemplateName = oDataextendRecord.ExtendedTemplateName + sViewActionIcon + sDeleteActionIcon + sAnkerAddIcon
						
					} 						
				});
				
				PageCtrl.debug.log('processDataActionIcons', lIsUserhaveAtLeastOneAccess);				
			} 
			//to here......
			
			function fillAvailableProject(tProjectList, nSelectedProjectId) {
				var sCheckBoxesHtml = '', sCheckBoxTmpl = "", nNoOfCheckBoxes = $('#AvailableProjects').find('input:checkbox').length, nOfProjects = tProjectList.length;
				
				if (nNoOfCheckBoxes !== nOfProjects) {
					tProjectList.forEach( function(oDataRow, nIndex) {
						sCheckBoxTmpl = "<input type='checkbox' id='project-id-{1}' {3}><label for='project-id-{1}'>&nbsp;&nbsp;{2}</label></br>";

						sCheckBoxTmpl = UTIL.replaceAll(sCheckBoxTmpl, '{1}', oDataRow.idProject);
						sCheckBoxTmpl = sCheckBoxTmpl.replace('{2}', oDataRow.projectName);
						sCheckBoxTmpl = sCheckBoxTmpl.replace('{3}', ( (oDataRow.idProject === nSelectedProjectId) ? 'checked' : '') );				

						sCheckBoxesHtml = sCheckBoxesHtml + sCheckBoxTmpl;
					});
					$('#AvailableProjects').html(sCheckBoxesHtml);
					$('#AvailableProjects input[type="checkbox"]').off('click', ManageextendedView.handleActionClicks).on('click', ManageextendedView.handleActionClicks);
				}					
			}			
		},
		handleActionClicks : function(tEvent) {
			var oTarget = oEvent.target, oTargetParent = oTarget.parentElement, sTagName = oTarget.tagName, sInputType = oTarget.type, 
				lBulkAction = false, nIndexOfRecord = -1, sTargetId = '', nActionCategory = (sTagName.toUpperCase() === 'I' || sTagName.toUpperCase() === 'SPAN') ? 0 : 1;
			
			sTargetId = (sTagName.toUpperCase() === 'I' || sTagName.toUpperCase() === 'SPAN') ? oTargetParent.id : oTarget.id;
			
			if (nActionCategory === 0) {
			
				nIndexOfRecord = UTIL.indexOfRecord(ManageDataTemplateView.ViewPageData.ViewPageDataList, 'ExtendedTemplateName', sTargetId.split('-')[2]);
				ManageDataTemplateView.IndexOfRecord = nIndexOfRecord;

			/* 	if (sTargetId.indexOf('copy') > -1) {			
					ManageDataTemplateView.copyAction( ManageDataTemplateView.ViewPageData.ViewPageDataList[nIndexOfRecord] );
				} */
				
			} else {
			
				nIndexOfRecord = UTIL.indexOfRecord(ManageDataTemplateView.ViewPageData.ViewPageDataList, 'ValidationId', sTargetId.split('-')[2]);
				
				if (sTagName.toUpperCase() === 'IMG') {
					lBulkAction = (sTargetId === 'select-all-projects') ? true : false;
					$('#AvailableProjects').find('input:checkbox').prop('checked', lBulkAction);
				}
			}				
		},
		
	
		getSearchParameters: function() {
				var oRetValue = { "SearchByOption": "", "FromDate": "", "ToDate": "", "ProjectIds": "", "SearchText": "" };
				var aSelectedProjectIds = [], lPageLoadCall =  ( $('#AvailableProjects').html().length === 0 ) ? true : false;			
				
				oRetValue.SearchByOption = $("input[name='SearchByOption']:checked").val();
				oRetValue.FromDate = $("#FromDate").val();								
				oRetValue.ToDate = $("#ToDate").val();
				oRetValue.SearchText = $('#SearchText').val();
				
				$('#AvailableProjects').find('input:checkbox').each( function() {			
					if ( $(this).prop('checked') ) {
						aSelectedProjectIds.push( $(this).attr("id").split('-')[2] );
					}				
				});			
				
				if (aSelectedProjectIds.length > 0) {			
					oRetValue.ProjectIds = aSelectedProjectIds.join();
					
				} else if (lPageLoadCall) {				
					oRetValue.ProjectIds = ManageextendedView.SelectedProjectId;
					
				} else {				
					Modal.confirmDialog(0, 'Incomplete Selection',	'Please select at least one or more projects to search validations', ['Ok'] );
					oRetValue = {};
				}
				
				PageCtrl.debug.log('getSearchParameters', oRetValue);
				
				return oRetValue;
			},
		
}

$(document).ready(function(){
	var date_input=$('input[name="date"]'); //our date input has the name "date"
	var container=$('.bootstrap-iso form').length > 0 ? $('.bootstrap-iso form').parent() : "body";
	
	PageCtrl.debug.log('Page getting reloaded', $('#AvailableProjects').html().length);
	
	date_input.datepicker({
		format: 'yyyy-mm-dd',
		container: container,
		todayHighlight: true,
		autoclose: true,
		endDate: "today"
	})
	
	initializeJwfSpaInfra();	
	ManageextendedView.setup();
	ManageextendedView.getPaginatedExtendedList();		
});	
</script>
</head>
