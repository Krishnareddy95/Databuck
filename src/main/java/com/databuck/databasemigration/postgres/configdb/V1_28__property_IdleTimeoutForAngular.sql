drop procedure if exists dummy_do_not_use;
create procedure dummy_do_not_use()

language plpgsql
as $$

     declare sSelectedDatabase varchar(100) := (select current_database());

begin

    if not exists (select 1 from databuck_property_details where property_category_id =(select property_category_id from databuck_properties_master where property_category_name='appdb') and property_name='idle_time_out') then
        insert into databuck_property_details(property_category_id,property_name,property_value,description,is_mandatory_field,is_password_field,is_value_encrypted,property_default_value,property_data_type,prop_requires_restart,last_updated_at) values((select property_category_id from databuck_properties_master where property_category_name='appdb'),'idle_time_out','15','Property to set idle timeout in minutes for angular UI','N','N','N','15','integer','Y',NOW());
    end if;

end $$;

call dummy_do_not_use();
drop procedure if exists dummy_do_not_use;


