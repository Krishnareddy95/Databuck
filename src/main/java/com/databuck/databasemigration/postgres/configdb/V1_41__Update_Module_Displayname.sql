/*
 * Update displayName of Module.
 */

drop procedure if exists dummy_do_not_use;
create procedure dummy_do_not_use()

language plpgsql
as $$

     declare sSelectedDatabase varchar(100) := (select current_database());

    begin
           if exists (select 1 from  Module where taskName ='Tasks') then
            UPDATE Module set displayName ='Run and Schedule' where taskName ='Tasks';
            end if;
            
            if exists (select 1 from  Module where taskName ='User Settings') then
            UPDATE Module set displayName ='Settings' where taskName ='User Settings';
            end if;
    end $$;

call dummy_do_not_use();
drop procedure if exists dummy_do_not_use;