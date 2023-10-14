/*
 * Update description of csvWriteLimit.
 */

drop procedure if exists dummy_do_not_use;
create procedure dummy_do_not_use()

language plpgsql
as $$

     declare sSelectedDatabase varchar(100) := (select current_database());

    begin
           if exists (select 1 from  databuck_property_details where property_name ='csvWriteLimit') then
            update databuck_property_details set description ='Property to specify number of output/result records written into csv file' where property_name ='csvWriteLimit';
            end if;
    end $$;

call dummy_do_not_use();
drop procedure if exists dummy_do_not_use;