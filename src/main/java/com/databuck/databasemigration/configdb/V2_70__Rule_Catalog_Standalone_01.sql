/* 
	30-March-2021 : Wells Fargo standalone (all in DataBuck) apporval process to be sync up in Rule catalog UI
*/
set @sListReference = 'DQ_RULE_CATALOG_STATUS';
set @nListRowId = (select
	case when exists (select 1 from app_option_list where upper(list_reference) = @sListReference) > 0 then (select row_id from app_option_list where upper(list_reference) = @sListReference)
		else -1
	end); 	

/* Disable or make old status code as inactive so will not fetched to UI and delete old one to make insert script simple */
start transaction;

update app_option_list_elements
set active = 0
where elements2app_list = @nListRowId
and   element_reference not in ('CREATED', 'UNIT_TESTING_READY', 'UNIT_TESTING_COMPLETE', 'APPROVED_FOR_PRODUCTION', 'DEACTIVATED');

delete from app_option_list_elements
where elements2app_list = @nListRowId
and   element_reference in ('CREATED', 'UNIT_TESTING_READY', 'UNIT_TESTING_COMPLETE', 'APPROVED_FOR_PRODUCTION', 'DEACTIVATED');

commit;

/* Now insert new status codes */
start transaction;

insert into app_option_list_elements 
	(elements2app_list, element_reference, element_text, is_default, position, active) 
values 
	(@nListRowId, 'CREATED','CREATED',0,1,1);

insert into app_option_list_elements 
	(elements2app_list, element_reference, element_text, is_default, position, active) 
values 
	(@nListRowId, 'UNIT_TESTING_READY','UNIT TESTING READY',0,2,1);

insert into app_option_list_elements 
	(elements2app_list, element_reference, element_text, is_default, position, active) 
values 
	(@nListRowId, 'UNIT_TESTING_COMPLETE','UNIT TESTING COMPLETE',0,3,1);

insert into app_option_list_elements 
	(elements2app_list, element_reference, element_text, is_default, position, active) 
values 
	(@nListRowId, 'APPROVED_FOR_PRODUCTION','APPROVED FOR PRODUCTION',0,4,1);

insert into app_option_list_elements 
	(elements2app_list, element_reference, element_text, is_default, position, active) 
values 
	(@nListRowId, 'DEACTIVATED','DEACTIVATED',0,5,1);

commit;

/* Place holder for updating old data in UAT in environment as per discussion with Wells Fargo and Dutta */




