
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<jsp:include page="header.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<jsp:include page="container.jsp" />



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
							<span class="caption-subject bold "> Modules </span>
						</div>
					</div>
					<div class="portlet-body">
						<table class="table table-striped table-bordered table-hover"
							id="showDataTable">
							<thead>
								<tr>
									<th>Module Name</th>
									<th>&nbsp;&nbsp;&nbsp;Update
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Delete</th>

								</tr>
							</thead>
							<tbody>

								<c:forEach var="task" items="${task}">
									<tr>
										<td>${task.taskName}</td>


										<td><a onClick="validateHrefVulnerability(this)"  href="update?idTask=${task.idTask}"><i
												style="margin-left: 10%;" class="fa fa-edit edit-delete"></i></a>
											&nbsp;&nbsp;&nbsp;&nbsp; <a onClick="validateHrefVulnerability(this)" 
											href="delete?idTask=${task.idTask}"> <i
												style="margin-left: 10%;" class="fa fa-trash edit-delete"></i></a>
										</td>


									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
				<!--       *************END EXAMPLE TABLE PORTLET*************************-->

			</div>
		</div>
	</div>
</div>



<!--********     BEGIN CONTENT ********************-->

<jsp:include page="footer.jsp" />