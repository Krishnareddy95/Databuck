create procedure dummy_do_not_use()
language plpgsql
as $$

     declare sSelectedDatabase varchar(100) := (select current_database());

begin

    UPDATE listSchedule SET domain_id = (select ds.domain_id FROM domain_to_project ds JOIN listschedule ls ON ls.project_id= ds.project_id and ls.domain_id =0);
   end $$;

call dummy_do_not_use();
drop procedure if exists dummy_do_not_use;

