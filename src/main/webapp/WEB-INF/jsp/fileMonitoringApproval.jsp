<%@page import="com.databuck.service.RBACController"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<style>
 	.greenClass {background-color:#b4f3b4;}
  	.redClass {background-color:#f3c7c7;}
  	.blueClass {background-color:#87CEEB;}

	#tblRuleCatalogRecordList th {
		background: #f2f2f2;
		border-bottom: 2px solid #808080;
	}

	button > span {
		text-decoration: none !important;
	}
</style>
<div class="page-content-wrapper">
	<div class="page-content">
		<div class="row">
			<div class="col-md-12">
				<div class="portlet light bordered">
					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold"
								style="float: left !important;">File Monitoring :
								 ${name}</span>
								 <input type="hidden" name="idApp" id="idApp" value="${idApp}"/>
								 <input type="hidden" name="checkFlag" id="checkFlag" value="0"/>
								 <input type="hidden" name="idData" id="idData" value="${idData}"/>
								 <input type="hidden" name="applicationName" id="applicationName" value="${lsName}"/>
								 <input type="hidden" name="name" id="name" value="${laName}"/>
						</div>
					</div>
					<div class="portlet-body "  >
						<button class="btn btn-primary" id="BtnApproveValidation">Approve</button>
						<button class="btn btn-primary" id="BtnRejectValidation"> <i class="fa fa-ban" style="color: white;" aria-hidden="true"></i> Reject</button>

						<div id="val_approval_msg_div" class="portlet light bordered" style="margin-top: 10px; background-color: mintcream;">
							<p><b>Current Validation Status: </b>"<span style="color:blue">Rule changes are pending for approval</span>".</p>

							<p>All new changes will be stored in Staging. Staged changes must be approved to apply them in production.</p>

							<p>Changes in Rules are highlighted with color coding.
							   <span style="padding-right:25px; padding-left:5px"><i class="fa fa-square" style="color:green;padding-right: 5px;"></i>New</span>
							   <span style="padding-right:25px"><i class="fa fa-square" style="color:red;padding-right: 5px;"></i>Missing/Deleted</span>
							   <span style="padding-right:25px"><i class="fa fa-square" style="color:blue;padding-right: 5px;"></i>Modified</span>
							</p>
						</div>

						<table id="tblRuleCatalogRecordList"
							class="table table-bordered table-hover table-responsive">
							<thead>
								<tr>
									<th>Rule Delta Type</th>
									<th>Schema Name</th>
									<th>Table Name</th>
									<th>File Indicator</th>
									<th>Day Of Week</th>
									<th>Hour Of Day</th>
									<th>Expected Time</th>
									<th>Expected File Count</th>
									<th>Start Hour</th>
									<th>End Hour</th>
									<th>Frequency</th>
								</tr>
							</thead>
							<tbody>
                                <c:forEach var="fmRule"
                                   items="${dbkFileMonitoringRules}">
                                   <tr class="<c:choose><c:when test="${(fmRule.ruleDeltaType eq 'MISSING')}">redClass</c:when><c:when test="${(fmRule.ruleDeltaType eq 'NEW')}">greenClass</c:when><c:when test="${(fmRule.ruleDeltaType eq 'CHANGED')}">blueClass</c:when></c:choose>">
                                      <td>${fmRule.ruleDeltaType}</td>
                                      <td>${fmRule.schemaName}</td>
                                      <td>${fmRule.tableName}</td>
                                      <td>${fmRule.fileIndicator}</td>
                                      <td>${fmRule.dayOfWeek}</td>
                                      <td>${fmRule.hourOfDay}</td>
                                      <td>${fmRule.expectedTime}</td>
                                      <td>${fmRule.expectedFileCount}</td>
                                      <td>${fmRule.startHour}</td>
                                      <td>${fmRule.endHour}</td>
                                      <td>${fmRule.frequency}</td>
                                   </tr>
                                </c:forEach>
                            </tbody>
						</table>

					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<jsp:include page="footer.jsp" />
<script src="./assets/global/plugins/jquery.min.js" type="text/javascript"> </script>
<script src="./assets/global/plugins/jquery.dataTables.min.js" type="text/javascript"></script>

<head>
<link rel="stylesheet" href="./assets/jwf/content/JwfSpaInfra.css">
<script src="./assets/jwf/scripts/JwfSpaInfra.js"></script>
</head>

<script>
    $('#BtnApproveValidation').click(function() {
      	Modal.confirmDialog(0, 'Rules approval confirmation','Do you want to approval the rules changes?', ['OK', 'Cancel'], { onDialogClose: approveRuleChanges, onDialogLoad: null });
    });

    $('#BtnRejectValidation').click(function() {
      	Modal.confirmDialog(0, 'Rules reject confirmation','Do you want to reject the rules changes?',['OK', 'Cancel'], { onDialogClose: rejectRuleChanges, onDialogLoad: null });
    });

    function approveRuleChanges() {
    	var lsName =$('#applicationName').val();
        var idData =$('#idData').val();
        var laName =$('#name').val();
        var idApp=$('#idApp').val();
        
        $.ajax({
			url : './fmApproveChanges?idApp='+idApp,
			type : 'GET',
			datatype : 'json',
			success : function(response) {
				var j_obj = $.parseJSON(response);
				
				if(j_obj.status == 'success'){
					toastr.info('Rules changes approval is successfull');
					window.location.href = "customizeValidation?idApp="+idApp+"&lsName="+lsName+"&idData="+idData+"&laName="+laName;
		            
				} else{
					Modal.confirmDialog(3, 'Rules changes approval failed', oResponse.Msg, ['Ok'] );
				}
				
			},
			error : function() {

			}
		});		
    }
    
    function rejectRuleChanges() {
    	var lsName =$('#applicationName').val();
        var idData =$('#idData').val();
        var laName =$('#name').val();
        var idApp=$('#idApp').val();
        
        $.ajax({
			url : './fmRejectChanges?idApp='+idApp,
			type : 'GET',
			datatype : 'json',
			success : function(response) {
				var j_obj = $.parseJSON(response);
				
				if(j_obj.status == 'success'){
					toastr.info('Rules changes rejection is successfull');
					window.location.href = "customizeValidation?idApp="+idApp+"&lsName="+lsName+"&idData="+idData+"&laName="+laName;
		            
				} else{
					Modal.confirmDialog(3, 'Rules changes rejection failed', oResponse.Msg, ['Ok'] );
				}
				
			},
			error : function() {

			}
		});		
    }
    
   	$(document).ready( function() {
		initializeJwfSpaInfra(); // Load JWF framework module on this page
	});



</script>

