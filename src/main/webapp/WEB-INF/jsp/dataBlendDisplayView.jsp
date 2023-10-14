<%@page import="java.util.Map.Entry"%>
<%@page import="com.google.common.collect.LinkedHashMultimap"%>
<%@page import="com.google.common.collect.Multimap"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp" />

<jsp:include page="container.jsp" />
<!--BEGIN CONTENT -->
<div class="page-content-wrapper">
	<!-- BEGIN CONTENT BODY -->
	<div class="page-content">
		<!-- BEGIN PAGE TITLE-->
		<!-- END PAGE TITLE-->
		<!-- END PAGE HEADER-->



		<div class="row">
			<div class="col-md-6" style="width: 100%;">
				<div class="portlet light bordered init">
					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold ">Extended Template :
								${name}</span>
						</div>
					</div>
					<div class="portlet-body form">
						<form role="form" method="post">
							<input type="hidden" id="sourceid" value="" />

							<div class="form-body">
								<div class="row">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control catch-error"
												id="nameid" value="${name}" readonly> <label
												for="form_control">Extended Template Name</label>
										</div>
										<span class="required"></span>
									</div>
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control catch-error"
												id="descid" value="${lbdescription}" readonly> <label
												for="form_control">Description</label>
										</div>
										<span class="required"></span>
									</div>
								</div>
								</br>
								<div class="row">
									<div class="col-md-6">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="locationid"
												value="${lsdescription}" readonly> <label
												for="form_control">Data Template for Modification</label>
										</div>
									</div>
								</div>
								</br>
							</div>
						</form>
					</div>
					<!-- portlet-body form -->
				</div>
				<!-- init -->
			</div>
			<!-- col-6 -->
		</div>
		<!-- row   -->


		<div class="row">
			<div class="col-md-12">
				<!-- BEGIN EXAMPLE TABLE PORTLET-->
				<div class="portlet light bordered">
					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold">Column Details for Data
								Template for Modification : ${lsdescription}</span>
						</div>
					</div>
					<div class="portlet-body">
						<table class="table table-striped table-bordered table-hover"
							id="showDataTable">
							<thead>
								<tr>
									<th>Column Name</th>
									<th>Display Name</th>
									<th>Format</th>
									<th>Data Profile Check</th>
									<th>Group_By</th>
									<th>Duplicate_key</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="listdatadefinition"
									items="${listdatadefinition}">
									<tr class="source-item">
<%-- 										<span style="display: none" class="source-item-id">${listdatadefinition.idColumn}</span>
 --%>										<td class="source-display-name">${listdatadefinition.displayName}</td>

										<td class="source-display-name">${listdatadefinition.displayName}</td>
										<td class="source-format">${listdatadefinition.format}</td>


										<td class="source-kbe">${listdatadefinition.KBE}</td>
										<td class="source-dgroup">${listdatadefinition.dgroup}</td>
										<td class="source-dupkey">${listdatadefinition.dupkey}</td>

									</tr>
								</c:forEach>
							</tbody>

						</table>
						<!-- <a href="<?php //echo site_url('Datablend/create?id='.$source_id);?>" class="btn blue" >Add DataBlend</a> -->

					</div>
				</div>
				<!-- END EXAMPLE TABLE PORTLET-->
			</div>
		</div>
		<!-- closing table row -->

		<div class="row">
			<div class="col-md-12" style="width: 100%;">
				<div class="portlet light bordered init">
					<!-- BEGIN EXAMPLE TABLE PORTLET-->
					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold">Derived Columns </span>
						</div>
					</div>

					<div class="portlet-body">
						<table class="table table-striped table-bordered table-hover" id="showAltDataTable">
							<thead>
								<tr>
									<th>Derived Template Name</th>
									<th>Column Name</th>
									<th>Formula</th>
									<th>Column Value</th>
								</tr>
							</thead>
							<tbody>
							<c:forEach var="listDataBlend"
									items="${listDataBlend}">
									<tr class="source-item">
 										<td class="source-display-name">${listDataBlend.name}</td>
 										<td class="source-format">${listDataBlend.columnName}</td>
										<td class="source-display-name">${listDataBlend.expression}</td>
										<td class="source-kbe">${listDataBlend.columnValue}</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>

				</div>
				<!-- END EXAMPLE TABLE PORTLET-->
			</div>
		</div>

		<div class="row">
			<div class="col-md-12" style="width: 100%;">
				<div class="portlet light bordered init">
					<!-- BEGIN EXAMPLE TABLE PORTLET-->
					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold">Filters</span>
						</div>
					</div>
					<div class="portlet light bordered">
						<div class="portlet-body">
							<table class="table table-striped table-bordered table-hover"
								id="showAltDataTable">
								<thead>
									<tr>
										<th>Name</th>
										<th>Filter</th>
										<!-- <th> formula</th> -->
									</tr>
								</thead>
								<tbody>
									<c:forEach var="mapobjectFiltersobj"
										items="${mapobjectFilters}">
										<tr>
											<td>${mapobjectFiltersobj.key}</td>
											<td>${mapobjectFiltersobj.value}</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</div>
					</div>
				</div>
				<!-- END EXAMPLE TABLE PORTLET-->
			</div>
		</div>
		<!--  filter data end -->
		<!-- END EXAMPLE TABLE PORTLET-->
	</div>
</div>
<!--  filter data end -->

<!-- END CONTAINER-->
<jsp:include page="footer.jsp" />