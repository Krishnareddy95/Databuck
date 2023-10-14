<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />
            <!-- BEGIN CONTENT -->
    <div class="page-content-wrapper">
        <!-- BEGIN CONTENT BODY -->
        <div class="page-content">
            <!-- BEGIN PAGE TITLE-->
            <!-- END PAGE TITLE-->
            <!-- END PAGE HEADER-->
                <div class="row">
                <div class="col-md-6" style="width:100%;">   
                    <div class="portlet light bordered init">
                    <div class="portlet-title">
                        <div class="caption font-red-sunglo">
                            <span class="caption-subject bold ">
                                Delete Extended Template : ${name}
                            </span>
                        </div>
                    </div>
                    <div class="portlet-body form">
                                <input type="hidden" id="blendid" value="${idDataBlend}" /> 
                                                    
                                <div class="form-body">
                                    <div class="row">
                                        <div class="col-md-6 col-capture">
                                            <div class="form-group form-md-line-input">
                                                <input type="text" class="form-control catch-error" id="nameid"  value="${name}" readonly>
                                                <label for="form_control">Name</label>
                                            </div>
                                            <span class="required"></span>
                                        </div>
                                        <div class="col-md-6 col-capture">
                                            <div class="form-group form-md-line-input">
                                                <input type="text" class="form-control catch-error" id="descid"  value="${lbdescription}" readonly>
                                                <label for="form_control">Description</label>
                                            </div>
                                            <span class="required"></span>
                                        </div>
                                    </div></br>
                                    <div class="row">    
                                        <div class="col-md-6">
                                            <div class="form-group form-md-line-input">
                                                <input type="text" class="form-control" id="locationid"  value="${lsdescription}" readonly>
                                                <label for="form_control">Data Template for Modification</label>
                                            </div>
                                        </div>
                                    </div></br>

                                     <div class="form-actions noborder align-center">
                                        <button type="submit" class="btn red" id="deleteblendid">Delete</button>
                                    </div>
                                    
                                </div>                                       
                        </div>
                    </div>
                 </div>
            </div> 
                

                    <div class="row">
                        <div class="col-md-12">
                            <!-- BEGIN EXAMPLE TABLE PORTLET-->
                            <div class="portlet light bordered">
                                <div class="portlet-title">
                                    <div class="caption font-red-sunglo">
                                        <span class="caption-subject bold">Derived Columns</span>
                                    </div>
                                </div>
                                <div class="portlet-body">
                                <input type="hidden" class="tourl1" value ="deletederivedcolumns">
                                <input type="hidden" id="appid" value =""> <!-- declared for js to work -->
                                    <table class="table table-striped table-bordered table-hover" id="showDataTable">
                                        <thead>
                                            <tr>
                                                <th> Name</th>
                                                <th> Formula</th>
                                                <!-- <th> </th> -->
                                                <!-- <th> formula</th> -->
                                            </tr>
                                        </thead>
                                               <tbody>
                                        <c:forEach var="mapobjectderievedColumnsobj" items="${mapobjectderievedColumns}">
                                        <tr>
                                        <td >${mapobjectderievedColumnsobj.key}</td>
                                        <td>${mapobjectderievedColumnsobj.value}</td>
                                      
                                                        <%-- <td>
                                                          <span style="display:none"> ${idDataBlend}</span>
                                                            <a onClick="validateHrefVulnerability(this)"  href="#" data-toggle="confirmation" class ="bs_confirmation_demo_1" data-singleton="true"><i style="margin-left: 20%;color:red" class="fa fa-trash "></i></a>
                                                        </td> --%>
                                                      </tr>
                                        </c:forEach>
                                                </tbody>
                                        
                                    </table>
                                </div>
                            </div>
                            <!-- END EXAMPLE TABLE PORTLET-->
                        </div>
                    </div>
                <?php } ?>

                    <div class="row">
                        <div class="col-md-12" style="width:100%;">
                        <div class="portlet light bordered init">
                            <!-- BEGIN EXAMPLE TABLE PORTLET-->
                            <div class="portlet-title">
                                <div class="caption font-red-sunglo">
                                    <span class="caption-subject bold">Filters</span>
                                </div>
                            </div>
                            <div class="portlet light bordered">
                                <div class="portlet-body">
                                <input type="hidden" class="tourl1" value ="deletefilter">
                                    <table class="table table-striped table-bordered table-hover" id="showAltDataTable">
                                        <thead>
                                            <tr>
                                                <th> Name</th>
                                                <th> Filter</th>
                                               <!--  <th> </th> -->
                                                <!-- <th> formula</th> -->
                                            </tr>
                                        </thead>
                                                <tbody>
                                                 <c:forEach var="mapobjectFiltersobj" items="${mapobjectFilters}">
                                        <tr>
                                        <td >${mapobjectFiltersobj.key}</td>
                                        <td>${mapobjectFiltersobj.value}</td>
                                                       <%--  <td>
                                                            <span style="display:none"> ${idDataBlend}</span>
                                                            <a onClick="validateHrefVulnerability(this)"  href="#" data-toggle="confirmation" class ="bs_confirmation_demo_1" data-singleton="true"><i style="margin-left: 20%;color:red" class="fa fa-trash "></i></a>
                                                        </td> --%>
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
                     <?php } ?>   

                     <div class="modal fade" id="notesmodal" tabindex="-1" role="basic" aria-hidden="true">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header">
                                <button type="button" class="close" data-dismiss="modal" aria-hidden="true"></button>
                                <h4 class="modal-title">Activity Notes for </h4>
                            </div>
                            <div class="modal-body">
                                 
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-gray" data-dismiss="modal">Close</button>
                            </div>
                        </div>
                        <!-- /.modal-content -->
                    </div>
                    <!-- /.modal-dialog -->
                </div>

                    
                    <!-- <div class="row">
                        <div class="col-md-12">
                            BEGIN EXAMPLE TABLE PORTLET
                            <div class="portlet light bordered">
                                <div class="portlet-title">
                                    <div class="caption font-red-sunglo">
                                        <span class="caption-subject bold "> No Extended Template Sources found.  </span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div> -->
                </div>
            </div>
    <!-- END CONTAINER -->

    
<jsp:include page="footer.jsp" />
