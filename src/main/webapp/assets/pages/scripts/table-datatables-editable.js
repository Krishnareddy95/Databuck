var TableDatatablesEditable = function () {

    var handleTable = function () {

        function restoreRow(oTable, nRow) {
            var aData = oTable.fnGetData(nRow);
            var jqTds = $('>td', nRow);

            for (var i = 0, iLen = jqTds.length; i < iLen; i++) {
                oTable.fnUpdate(aData[i], nRow, i, false);
            }

            oTable.fnDraw();
        }

        function editRow(oTable, nRow) {
            var aData = oTable.fnGetData(nRow);
            var jqTds = $('>td', nRow);
            console.log(aData);
            jqTds[0].innerHTML = '<input type="text" class="form-control input-small" value="' + aData[0] + '">';
            jqTds[1].innerHTML = '<input type="text" class="form-control input-small" value="' + aData[1] + '">';
            jqTds[2].innerHTML = '<input type="text" class="form-control input-small" value="' + aData[2] + '">';
            if (aData[3] == 'Y') {
            	jqTds[3].innerHTML = '<input type="checkbox" id="checkboxdata3" checked class="form-control " value="' + aData[3] + '">';
            } else {
            	jqTds[3].innerHTML = '<input type="checkbox" id="checkboxdata3" class="form-control " value="' + aData[3] + '">';
            }if (aData[4] == 'Y') {
            	jqTds[4].innerHTML = '<input type="checkbox" id="checkboxdata4" checked class="form-control " value="' + aData[4] + '">';
            } else {
            	jqTds[4].innerHTML = '<input type="checkbox" id="checkboxdata4" class="form-control " value="' + aData[4] + '">';
            }
            if (aData[5] == 'Y') {
            	jqTds[5].innerHTML = '<input type="checkbox" id="checkboxdata5" checked class="form-control " value="' + aData[5] + '">';
            } else {
            	jqTds[5].innerHTML = '<input type="checkbox" id="checkboxdata5" class="form-control" value="' + aData[5] + '">';
            }
            if (aData[6] == 'Y') {
            	jqTds[6].innerHTML = '<input type="checkbox" id="checkboxdata6" checked class="form-control " value="' + aData[6] + '">';
            } else {
            	jqTds[6].innerHTML = '<input type="checkbox" id="checkboxdata6" class="form-control" value="' + aData[6] + '">';
            }if (aData[7] == 'Y') {
            	jqTds[7].innerHTML = '<input type="checkbox" id="checkboxdata7" checked class="form-control " value="' + aData[7] + '">';
            } else {
            	jqTds[7].innerHTML = '<input type="checkbox" id="checkboxdata7" class="form-control" value="' + aData[7] + '">';
            }
            	/*jqTds[4].innerHTML = '<input type="text" class="form-control input-small" value="' + aData[4] + '">';
            jqTds[5].innerHTML = '<input type="text" class="form-control input-small" value="' + aData[5] + '">';
            jqTds[6].innerHTML = '<input type="text" class="form-control input-small" value="' + aData[6] + '">';*/
           // jqTds[7].innerHTML = '<input type="text" class="form-control input-small" value="' + aData[7] + '">';
           
            if (aData[8] == 'Y') {
            	jqTds[8].innerHTML = '<input type="checkbox" id="checkboxdata11" checked class="form-control " value="' + aData[8] + '">';
            } else {
            	jqTds[8].innerHTML = '<input type="checkbox" id="checkboxdata11" class="form-control" value="' + aData[8] + '">';
            }
            if (aData[9] == 'Y') {
            	jqTds[9].innerHTML = '<input type="checkbox" id="checkboxdata12" checked class="form-control " value="' + aData[9] + '">';
            } else {
            	jqTds[9].innerHTML = '<input type="checkbox" id="checkboxdata12" class="form-control " value="' + aData[9] + '">';
            }
            if (aData[10] == 'Y') {
            	jqTds[10].innerHTML = '<input type="checkbox" id="checkboxdata13" checked class="form-control " value="' + aData[10] + '">';
            } else {
            	jqTds[10].innerHTML = '<input type="checkbox" id="checkboxdata13" class="form-control" value="' + aData[10] + '">';
            }
            if (aData[11] == 'Y') {
            	jqTds[11].innerHTML = '<input type="checkbox" id="checkboxdata14" checked class="form-control " value="' + aData[11] + '">';
            } else {
            	jqTds[11].innerHTML = '<input type="checkbox" id="checkboxdata14" class="form-control" value="' + aData[11] + '">';
            }if (aData[12] == 'Y') {
            	jqTds[12].innerHTML = '<input type="checkbox" id="checkboxdata15" checked class="form-control " value="' + aData[12] + '">';
            } else {
            	jqTds[12].innerHTML = '<input type="checkbox" id="checkboxdata15" class="form-control" value="' + aData[12] + '">';
            }
            if (aData[13] == 'Y') {
            	jqTds[13].innerHTML = '<input type="checkbox" id="checkboxdata16" checked class="form-control " value="' + aData[13] + '">';
            } else {
            	jqTds[13].innerHTML = '<input type="checkbox" id="checkboxdata16" class="form-control" value="' + aData[13] + '">';
            }
            if (aData[14] == 'Y') {
            	jqTds[14].innerHTML = '<input type="checkbox" id="checkboxdata18" checked class="form-control " value="' + aData[14] + '">';
            } else {
            	jqTds[14].innerHTML = '<input type="checkbox" id="checkboxdata18" class="form-control" value="' + aData[14] + '">';
            }
            if (aData[15] == 'Y') {
            	jqTds[15].innerHTML = '<input type="checkbox" id="checkboxdata20" checked class="form-control " value="' + aData[15] + '">';
            } else {
            	jqTds[15].innerHTML = '<input type="checkbox" id="checkboxdata20" class="form-control" value="' + aData[15] + '">';
            }
            
            if (aData[16] == 'Y') {
            	jqTds[16].innerHTML = '<input type="checkbox" id="checkboxdata22" checked class="form-control " value="' + aData[16] + '">';
            } else {
            	jqTds[16].innerHTML = '<input type="checkbox" id="checkboxdata22" class="form-control" value="' + aData[16] + '">';
            }
            if (aData[17] == 'Y') {
            	jqTds[17].innerHTML = '<input type="checkbox" id="checkboxdata23" checked class="form-control " value="' + aData[17] + '">';
            } else {
            	jqTds[17].innerHTML = '<input type="checkbox" id="checkboxdata23" class="form-control" value="' + aData[17] + '">';
            }
            jqTds[18].innerHTML = '<input type="text" class="form-control input-small" value="' + aData[18] + '">';
            jqTds[19].innerHTML = '<input type="text" class="form-control input-small" value="' + aData[19] + '">';
            jqTds[20].innerHTML = '<input type="text" class="form-control input-small" value="' + aData[20] + '">';
            jqTds[21].innerHTML = '<input type="text" class="form-control input-small" value="' + aData[21] + '">';
            jqTds[22].innerHTML = '<input type="text" class="form-control input-small" value="' + aData[22] + '">';
            jqTds[23].innerHTML = '<input type="text" class="form-control input-small" value="' + aData[23] + '">';
            jqTds[24].innerHTML = '<a class="edit" href="">Save</a>';
            jqTds[25].innerHTML = '<a class="cancel" href="">Cancel</a>';
            oTable.fnDraw();
        }

        function saveRow(oTable, nRow) {
            var jqInputs = $('input', nRow);
            if ($('#checkboxdata3').is(":checked"))
            {
            	jqInputs[3].value = 'Y';
            } else {
            	jqInputs[3].value = 'N';
            }
            if ($('#checkboxdata4').is(":checked"))
            {
            	jqInputs[4].value = 'Y';
            } else {
            	jqInputs[4].value = 'N';
            }
            if ($('#checkboxdata5').is(":checked"))
            {
            	jqInputs[5].value = 'Y';
            } else {
            	jqInputs[5].value = 'N';
            }
            if ($('#checkboxdata6').is(":checked"))
            {
            	jqInputs[6].value = 'Y';
            } else {
            	jqInputs[6].value = 'N';
            }
            if ($('#checkboxdata7').is(":checked"))
            {
            	jqInputs[7].value = 'Y';
            } else {
            	jqInputs[7].value = 'N';
            }
            if ($('#checkboxdata11').is(":checked"))
            {
            	jqInputs[8].value = 'Y';
            } else {
            	jqInputs[8].value = 'N';
            }
            if ($('#checkboxdata12').is(":checked"))
            {
            	jqInputs[9].value = 'Y';
            } else {
            	jqInputs[9].value = 'N';
            }
            if ($('#checkboxdata13').is(":checked"))
            {
            	jqInputs[10].value = 'Y';
            } else {
            	jqInputs[10].value = 'N';
            }
            if ($('#checkboxdata14').is(":checked"))
            {
            	jqInputs[11].value = 'Y';
            } else {
            	jqInputs[11].value = 'N';
            }
            if ($('#checkboxdata15').is(":checked"))
            {
            	jqInputs[12].value = 'Y';
            } else {
            	jqInputs[12].value = 'N';
            }
            if ($('#checkboxdata16').is(":checked"))
            {
            	jqInputs[13].value = 'Y';
            } else {
            	jqInputs[13].value = 'N';
            }
            if ($('#checkboxdata18').is(":checked"))
            {
            	jqInputs[14].value = 'Y';
            } else {
            	jqInputs[14].value = 'N';
            }
            if ($('#checkboxdata20').is(":checked"))
            {
            	jqInputs[15].value = 'Y';
            } else {
            	jqInputs[15].value = 'N';
            }
            if ($('#checkboxdata22').is(":checked"))
            {
            	jqInputs[16].value = 'Y';
            } else {
            	jqInputs[16].value = 'N';
            }
            if ($('#checkboxdata23').is(":checked"))
            {
            	jqInputs[17].value = 'Y';
            } else {
            	jqInputs[17].value = 'N';
            }
            oTable.fnUpdate(jqInputs[0].value, nRow, 0, false);
            oTable.fnUpdate(jqInputs[1].value, nRow, 1, false);
            oTable.fnUpdate(jqInputs[2].value, nRow, 2, false);
            oTable.fnUpdate(jqInputs[3].value, nRow, 3, false);
            oTable.fnUpdate(jqInputs[4].value, nRow, 4, false);
            oTable.fnUpdate(jqInputs[5].value, nRow, 5, false);
            oTable.fnUpdate(jqInputs[6].value, nRow, 6, false);
            oTable.fnUpdate(jqInputs[7].value, nRow, 7, false);
            oTable.fnUpdate(jqInputs[8].value, nRow, 8, false);
            oTable.fnUpdate(jqInputs[9].value, nRow, 9, false);
            oTable.fnUpdate(jqInputs[10].value, nRow, 10, false);
            oTable.fnUpdate(jqInputs[11].value, nRow, 11, false);
            oTable.fnUpdate(jqInputs[12].value, nRow, 12, false);
            oTable.fnUpdate(jqInputs[13].value, nRow, 13, false);
            oTable.fnUpdate(jqInputs[14].value, nRow, 14, false);
            oTable.fnUpdate(jqInputs[15].value, nRow, 15, false);
            oTable.fnUpdate(jqInputs[16].value, nRow, 16, false);
            oTable.fnUpdate(jqInputs[17].value, nRow, 17, false);
            oTable.fnUpdate(jqInputs[18].value, nRow, 18, false);
            oTable.fnUpdate(jqInputs[19].value, nRow, 19, false);
            oTable.fnUpdate(jqInputs[20].value, nRow, 20, false);
            oTable.fnUpdate(jqInputs[21].value, nRow, 21, false);
            oTable.fnUpdate(jqInputs[22].value, nRow, 22, false);
            oTable.fnUpdate(jqInputs[23].value, nRow, 23, false);
            oTable.fnUpdate('<a class="edit" href="">Edit</a>', nRow, 24, false);
            // oTable.fnUpdate('<a class="delete" href="">Delete</a>', nRow, 7, false);
            oTable.fnDraw();
            
            // console.log(globalSourceItem);
            //ajax call to sync data
            // var kbe = $this.closest('.source-item').find('.source-kbe').html();
            // var no_error = 1;
            // var kbe = $("#KBEid").prop("checked") ? 'Y': 'N';
            // var dgrp = $("#dgroupid").prop("checked") ? 'Y': 'N';
            // var dkey = $("#dupkeyid").prop("checked") ? 'Y': 'N';
            // var targeturl = $("#targeturl").val();
            
            var listDataDefinition = {
                itemid: globalSourceItem.substr(globalSourceItem.indexOf("-") + 1),
                columnName: jqInputs[0].value,
                displayName: jqInputs[1].value,
                format: jqInputs[2].value,
                nonNull: jqInputs[3].value,
                primaryKey: jqInputs[4].value,
                hashValue: jqInputs[5].value,
                numericalStat: jqInputs[6].value ,
                stringStat: jqInputs[7].value,
               kbe: jqInputs[8].value,
               dgroup: jqInputs[9].value,
               dupkey: jqInputs[10].value,
               measurement: jqInputs[11].value,
               incrementalCol:jqInputs[12].value,
               recordAnomaly:jqInputs[13].value,
               dataDrift:jqInputs[14].value,
               outOfNormStat:jqInputs[15].value,
            	isMasked:jqInputs[16].value,
            	partitionBy:jqInputs[17].value,
            	 numericalThreshold:jqInputs[18].value,
                 stringStatThreshold:jqInputs[19].value,
                 nullCountThreshold:jqInputs[20].value,
                 recordAnomalyThreshold:jqInputs[21].value,
                 dataDriftThreshold:jqInputs[22].value,
                 outOfNormStatThreshold:jqInputs[23].value
                /*kbe: jqInputs[3].value,
                dgroup: jqInputs[4].value,
                dupkey: jqInputs[5].value ,
                measurement: jqInputs[6].value ,*/
                //times: new Date().getTime()
            };
             console.log(listDataDefinition);
             
            $.ajax({
                url:'./ajax',
                type: 'POST',
                datatype: 'json',
                data:JSON.stringify(listDataDefinition),
                contentType:"application/json",
                //cache:false,
                //dataType: "json", 	//Expected data format from server
                success: function(data) {
                	//alert(data);
                    if (data!="") {
                        toastr.info('Item updated successfully');
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
                	
                					//					toastr.info('Item updated successfully');
                		toastr.info('There seems to be a network problem. Please try again in some time.');
                }
            });
            
        }

        function cancelEditRow(oTable, nRow) {
            var jqInputs = $('input', nRow);
            oTable.fnUpdate(jqInputs[0].value, nRow, 0, false);
            oTable.fnUpdate(jqInputs[1].value, nRow, 1, false);
            oTable.fnUpdate(jqInputs[2].value, nRow, 2, false);
            oTable.fnUpdate(jqInputs[3].value, nRow, 3, false);
            oTable.fnUpdate(jqInputs[4].value, nRow, 4, false);
            oTable.fnUpdate(jqInputs[5].value, nRow, 5, false);
            oTable.fnUpdate(jqInputs[6].value, nRow, 6, false);
            oTable.fnUpdate('<a class="edit" href="">Edit</a>', nRow, 7, false);
            oTable.fnDraw();
        }

        var table = $('#sample_editable_1');

        var oTable = table.dataTable({

            // Uncomment below line("dom" parameter) to fix the dropdown overflow issue in the datatable cells. The default datatable layout
            // setup uses scrollable div(table-scrollable) with overflow:auto to enable vertical scroll(see: assets/global/plugins/datatables/plugins/bootstrap/dataTables.bootstrap.js). 
            // So when dropdowns used the scrollable div should be removed. 
            //"dom": "<'row'<'col-md-6 col-sm-12'l><'col-md-6 col-sm-12'f>r>t<'row'<'col-md-5 col-sm-12'i><'col-md-7 col-sm-12'p>>",

            "lengthMenu": [
                [5, 15, 20, -1],
                [5, 15, 20, "All"] // change per page values here
            ],

            // Or you can use remote translation file
            //"language": {
            //   url: '//cdn.datatables.net/plug-ins/3cfcc339e89/i18n/Portuguese.json'
            //},

            // set the initial value
            "pageLength": 10,

            "scrollX": true,

            "language": {
                "lengthMenu": " _MENU_ records"
            },
            "columnDefs": [{ // set default column settings
                'orderable': true,
                'targets': [0]
            }, {
                "searchable": true,
                "targets": [0]
            }],
            "order": [
                [0, "asc"]
            ] // set first column as a default sort by asc
        });

        var tableWrapper = $("#sample_editable_1_wrapper");

        var nEditing = null;
        var nNew = false;

        $('#sample_editable_1_new').click(function (e) {
            e.preventDefault();

            if (nNew && nEditing) {
                if (confirm("Previous row not saved. Do you want to save it ?")) {
                    saveRow(oTable, nEditing); // save
                    $(nEditing).find("td:first").html("Untitled");
                    nEditing = null;
                    nNew = false;

                } else {
                    oTable.fnDeleteRow(nEditing); // cancel
                    nEditing = null;
                    nNew = false;
                    
                    return;
                }
            }

            var aiNew = oTable.fnAddData(['', '', '', '', '', '','']);
            var nRow = oTable.fnGetNodes(aiNew[0]);
            editRow(oTable, nRow);
            nEditing = nRow;
            nNew = true;
        });

        table.on('click', '.delete', function (e) {
            e.preventDefault();

            if (confirm("Are you sure to delete this row ?") == false) {
                return;
            }

            var nRow = $(this).parents('tr')[0];
            oTable.fnDeleteRow(nRow);
            alert("Deleted! Do not forget to do some ajax to sync with backend :)");
        });

        table.on('click', '.cancel', function (e) {
            e.preventDefault();
            if (nNew) {
                oTable.fnDeleteRow(nEditing);
                nEditing = null;
                nNew = false;
            } else {
                restoreRow(oTable, nEditing);
                nEditing = null;
            }
        });

        var globalSourceItem;

        table.on('click', '.edit', function (e) {
            e.preventDefault();

            /* Get the row as a parent of the link that was clicked on */
            var nRow = $(this).parents('tr')[0];

            if (nEditing !== null && nEditing != nRow) {
                console.log($(this).html());
                /* Currently editing - but not this row - restore the old before continuing to edit mode */
                restoreRow(oTable, nEditing);
                editRow(oTable, nRow);
                nEditing = nRow;
            } else if (nEditing == nRow && this.innerHTML == "Save") {
                /* Editing this row and want to save it */
                saveRow(oTable, nEditing);
                nEditing = null;
                // alert("Updated! Do not forget to do some ajax to sync with backend :)");
            } else {
                /* No edit in progress - let's start one */
                // console.log($(this).html());
                globalSourceItem = $(this).attr('id');
                editRow(oTable, nRow);
                nEditing = nRow;
            }
        });
    }

    return {

        //main function to initiate the module
        init: function () {
            handleTable();
        }

    };

}();

jQuery(document).ready(function() {
    TableDatatablesEditable.init();
});