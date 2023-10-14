/* Insert defect codes to rule_business_attribute_mapping */

drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	declare sSelectedDatabase varchar(100) default '';
	select database() into sSelectedDatabase;

	start transaction;

         /* Delete all Records From rule_business_attribute_mapping table */
   	if exists (select 1 from information_schema.tables where table_schema = sSelectedDatabase and lower(table_name) = 'rule_business_attribute_mapping') then

                    delete from rule_business_attribute_mapping where id != 0;

     end if;

        /* inserting records to rule_business_attribute_mapping */

    if exists (select 1 from information_schema.tables where table_schema = sSelectedDatabase and lower(table_name) = 'rule_business_attribute_mapping') then

       INSERT INTO rule_business_attribute_mapping (rule_id, idApp, business_attribute_id) SELECT l.rule_reference AS rule_id, l.idApp,dc.row_id AS business_attribute_id
         from listApplicationsRulesCatalog l INNER JOIN dimension d ON d.idDimension=l.dimension_id INNER JOIN defect_codes dc ON dc.dimension_id=l.dimension_id AND dc.defect_code=l.defect_code
         WHERE l.dimension_id!=0 AND l.defect_code!='';
    end if;

    commit;

end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;
