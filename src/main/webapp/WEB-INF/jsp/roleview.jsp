
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<jsp:include page="header.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<jsp:include page="container.jsp" />



<!--============= BEGIN CONTENT BODY============= -->


<!--*****************      BEGIN CONTENT **********************-->



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
							<span class="caption-subject bold "> Groups </span>
						</div>
					</div>
					<div class="portlet-body">
						<table class="table table-striped table-bordered dataTable"
							id="showDataTable">
							<thead>
								<tr>
									<th>Group Name</th>
									<th>Description</th>
									<th></th>


								</tr>
							</thead>
							<tbody>

								<c:forEach var="role" items="${role}">
									<tr>
										<td>${role.roleName}</td>
										<td>${role.description}</td>


										<td><a onClick="validateHrefVulnerability(this)"  href="updateRole?idRole=${role.idRole}"><i
												style="margin-left: 10%;" class="fa fa-edit edit-delete"></i></a>
											&nbsp;&nbsp;&nbsp; <a onClick="validateHrefVulnerability(this)" 
											href="deleteRole?idRole=${role.idRole}"> <i
												style="margin-left: 10%;" class="fa fa-trash edit-delete"></i></a>
										</td>


									</tr>
								</c:forEach>


							</tbody>
						</table>
					</div>
				</div>
				<!-- END EXAMPLE TABLE PORTLET-->
			</div>
		</div>
	</div>
</div>

<!--


<!--********     BEGIN CONTENT ********************-->

<jsp:include page="footer.jsp" />