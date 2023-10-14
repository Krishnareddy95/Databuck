
drop procedure if exists dummy_do_not_use;
create procedure dummy_do_not_use()

language plpgsql
as $$

     declare sSelectedDatabase varchar(100) := (select current_database());

    begin
           if exists (select 1 from  staging_listapplicationsrulescatalog where rule_type ='Record Anomaly Check') then
            UPDATE staging_listapplicationsrulescatalog set rule_type ='Value Anomaly Check' where rule_type ='Record Anomaly Check';
            end if;
            
            if exists (select 1 from  listapplicationsrulescatalog where rule_type ='Record Anomaly Check') then
            UPDATE listapplicationsrulescatalog set rule_type ='Value Anomaly Check' where rule_type ='Record Anomaly Check';
            end if;
    end $$;

call dummy_do_not_use();
drop procedure if exists dummy_do_not_use;