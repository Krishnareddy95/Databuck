<%@page import="com.databuck.service.RBACController"%>
<%@page import="com.databuck.bean.ListApplications"%>
<%@page import="java.util.List"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp"/>
<jsp:include page="checkVulnerability.jsp" />
<jsp:include page="container.jsp"/>

<style>
	table.dataTable tbody th,
	table.dataTable tbody td {
		white-space: nowrap;
	}

    .dataTables_scrollBody{
            overflow-y: hidden !important;
   	 	}
</style>
            <!-- BEGIN CONTENT -->
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
                            <span class="caption-subject bold "> File Monitoring Validations </span>
                        </div>
                    </div>
                    <div class="portlet-body">
                        <table class="table table-striped table-bordered table-hover dataTable no-footer"
									style="width: 100%;">
                            <thead>
                                <tr>
                                	<th>IdApp</th>
									<th style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">Date</th>
									<th>Run</th>
									<th>Validation_Check_Name</th>
									<th>File_Count_Status</th>
									<th>File_Size_Status</th> 
                                </tr>
                            </thead>
                            <tbody>
                               <c:forEach var="listappslistdsobj" items="${listappslistds}">
                                    <tr>
                                      <td>${listappslistdsobj.idApp}</td>
										<td style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">${listappslistdsobj.date}</td>
										<td>${listappslistdsobj.run}</td>
										<td><a onClick="validateHrefVulnerability(this)"  href="fileMonitorResults?idApp=${listappslistdsobj.idApp}&appName=${listappslistdsobj.validationCheckName}" class="nav-link"> <span
											class="title">${listappslistdsobj.validationCheckName}</span></a></td>
											
												<td><c:if
														test="${listappslistdsobj.fileCountStatus eq 'passed'}">
														<span class="label label-success label-sm">${listappslistdsobj.fileCountStatus}</span>
													</c:if> <c:if
														test="${listappslistdsobj.fileCountStatus eq 'failed'}">
														<span class="label label-danger label-sm">${listappslistdsobj.fileCountStatus}</span>
													</c:if></td>
											
											<td><c:if
														test="${listappslistdsobj.fileSizeStatus eq 'passed'}">
														<span class="label label-success label-sm">${listappslistdsobj.fileSizeStatus}</span>
													</c:if> <c:if
														test="${listappslistdsobj.fileSizeStatus eq 'failed'}">
														<span class="label label-danger label-sm">${listappslistdsobj.fileSizeStatus}</span>
													</c:if></td>
											
											<%-- <td>${fileSizeStatus}</td> --%>
                                    </tr>

                                </c:forEach>
                            </tbody>
                            
                        </table>
                    </div>
                </div>
                <!-- END EXAMPLE TABLE PORTLET-->
            </div>
        </div>
          <!--   <div class="note note-info" style="width:90%;">
                <h3>No Validation Checks found!</h3> -->
      </div>
    </div>
<script>

</script>
<script src="./assets/global/plugins/jquery.min.js"
		type="text/javascript">
</script>
<script src="./assets/global/plugins/jquery.dataTables.min.js"
		type="text/javascript">
</script>

 <script>
$(document).ready(function() {
	var table;
	table = $('.table').dataTable({
		  	
		  	order: [[ 0, "desc" ]],
		  	"scrollX": true
	      });
});

</script>
    <!-- END CONTAINER -->
<jsp:include page="footer.jsp"/>