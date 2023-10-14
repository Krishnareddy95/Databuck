drop procedure if exists dummy_do_not_use;
create procedure dummy_do_not_use()

language plpgsql
as $$

     declare sSelectedDatabase varchar(100) := (select current_database());

    begin
           if exists (select 1 from  DATA_QUALITY_DATA_DRIFT_COUNT_SUMMARY where (dGroupCol LIKE '%-%' OR dGroupVal LIKE '%-%') AND (dGroupCol NOT LIKE '%?::?%' OR dGroupVal NOT LIKE '%?::?%'))  then
                UPDATE DATA_QUALITY_DATA_DRIFT_COUNT_SUMMARY SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal,'NON-INDIVIDUAL','NON#INDIVIDUAL'),'CRD-CCB','CRD#CCB'),'CRD-IB','CRD#IB') WHERE (dGroupVal LIKE '%-%') AND (dGroupVal NOT LIKE '%?::?%');
                UPDATE DATA_QUALITY_DATA_DRIFT_COUNT_SUMMARY SET dGroupCol = REPLACE(dGroupCol, '-', '?::?'), dGroupVal = REPLACE(dGroupVal, '-', '?::?') WHERE (dGroupCol LIKE '%-%' OR dGroupVal LIKE '%-%') AND (dGroupCol NOT LIKE '%?::?%' OR dGroupVal NOT LIKE '%?::?%');
                UPDATE DATA_QUALITY_DATA_DRIFT_COUNT_SUMMARY SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal, 'NON#INDIVIDUAL','NON-INDIVIDUAL'),'CRD#CCB','CRD-CCB'),'CRD#IB','CRD-IB') WHERE (dGroupVal LIKE '%?::?%') AND (dGroupVal NOT LIKE '%-%');
           end if;

           if exists (select 1 from  DATA_QUALITY_DATA_DRIFT_SUMMARY where (dGroupCol LIKE '%-%' OR dGroupVal LIKE '%-%') AND (dGroupCol NOT LIKE '%?::?%' OR dGroupVal NOT LIKE '%?::?%'))  then
               UPDATE DATA_QUALITY_DATA_DRIFT_SUMMARY SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal,'NON-INDIVIDUAL','NON#INDIVIDUAL'),'CRD-CCB','CRD#CCB'),'CRD-IB','CRD#IB') WHERE (dGroupVal LIKE '%-%') AND (dGroupVal NOT LIKE '%?::?%');
               UPDATE DATA_QUALITY_DATA_DRIFT_SUMMARY SET dGroupCol = REPLACE(dGroupCol, '-', '?::?'), dGroupVal = REPLACE(dGroupVal, '-', '?::?') WHERE (dGroupCol LIKE '%-%' OR dGroupVal LIKE '%-%') AND (dGroupCol NOT LIKE '%?::?%' OR dGroupVal NOT LIKE '%?::?%');
               UPDATE DATA_QUALITY_DATA_DRIFT_SUMMARY SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal, 'NON#INDIVIDUAL','NON-INDIVIDUAL'),'CRD#CCB','CRD-CCB'),'CRD#IB','CRD-IB') WHERE (dGroupVal LIKE '%?::?%') AND (dGroupVal NOT LIKE '%-%');
           end if;

            if exists (select 1 from  DATA_QUALITY_DATA_DRIFT where (dGroupCol LIKE '%-%' OR dGroupVal LIKE '%-%') AND (dGroupCol NOT LIKE '%?::?%' OR dGroupVal NOT LIKE '%?::?%'))  then
                  UPDATE DATA_QUALITY_DATA_DRIFT SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal,'NON-INDIVIDUAL','NON#INDIVIDUAL'),'CRD-CCB','CRD#CCB'),'CRD-IB','CRD#IB') WHERE (dGroupVal LIKE '%-%') AND (dGroupVal NOT LIKE '%?::?%');
                  UPDATE DATA_QUALITY_DATA_DRIFT SET dGroupCol = REPLACE(dGroupCol, '-', '?::?'), dGroupVal = REPLACE(dGroupVal, '-', '?::?') WHERE (dGroupCol LIKE '%-%' OR dGroupVal LIKE '%-%') AND (dGroupCol NOT LIKE '%?::?%' OR dGroupVal NOT LIKE '%?::?%');
                  UPDATE DATA_QUALITY_DATA_DRIFT SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal, 'NON#INDIVIDUAL','NON-INDIVIDUAL'),'CRD#CCB','CRD-CCB'),'CRD#IB','CRD-IB') WHERE (dGroupVal LIKE '%?::?%') AND (dGroupVal NOT LIKE '%-%');
            end if;

            if exists (select 1 from  DATA_QUALITY_DateRule_FailedRecords where (dGroupCol LIKE '%-%' OR dGroupVal LIKE '%-%') AND (dGroupCol NOT LIKE '%?::?%' OR dGroupVal NOT LIKE '%?::?%'))  then
                  UPDATE DATA_QUALITY_DateRule_FailedRecords SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal,'NON-INDIVIDUAL','NON#INDIVIDUAL'),'CRD-CCB','CRD#CCB'),'CRD-IB','CRD#IB') WHERE (dGroupVal LIKE '%-%') AND (dGroupVal NOT LIKE '%?::?%');
                  UPDATE DATA_QUALITY_DateRule_FailedRecords SET dGroupCol = REPLACE(dGroupCol, '-', '?::?'), dGroupVal = REPLACE(dGroupVal, '-', '?::?') WHERE (dGroupCol LIKE '%-%' OR dGroupVal LIKE '%-%') AND (dGroupCol NOT LIKE '%?::?%' OR dGroupVal NOT LIKE '%?::?%');
                  UPDATE DATA_QUALITY_DateRule_FailedRecords SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal, 'NON#INDIVIDUAL','NON-INDIVIDUAL'),'CRD#CCB','CRD-CCB'),'CRD#IB','CRD-IB') WHERE (dGroupVal LIKE '%?::?%') AND (dGroupVal NOT LIKE '%-%');
            end if;

            if exists (select 1 from  DATA_QUALITY_DateRule_Reference where (dGroupCol LIKE '%-%' OR dGroupVal LIKE '%-%') AND (dGroupCol NOT LIKE '%?::?%' OR dGroupVal NOT LIKE '%?::?%'))  then
                  UPDATE DATA_QUALITY_DateRule_Reference SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal,'NON-INDIVIDUAL','NON#INDIVIDUAL'),'CRD-CCB','CRD#CCB'),'CRD-IB','CRD#IB') WHERE (dGroupVal LIKE '%-%') AND (dGroupVal NOT LIKE '%?::?%');
                  UPDATE DATA_QUALITY_DateRule_Reference SET dGroupCol = REPLACE(dGroupCol, '-', '?::?'), dGroupVal = REPLACE(dGroupVal, '-', '?::?') WHERE (dGroupCol LIKE '%-%' OR dGroupVal LIKE '%-%') AND (dGroupCol NOT LIKE '%?::?%' OR dGroupVal NOT LIKE '%?::?%');
                  UPDATE DATA_QUALITY_DateRule_Reference SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal, 'NON#INDIVIDUAL','NON-INDIVIDUAL'),'CRD#CCB','CRD-CCB'),'CRD#IB','CRD-IB') WHERE (dGroupVal LIKE '%?::?%') AND (dGroupVal NOT LIKE '%-%');
            end if;

            if exists (select 1 from  DATA_QUALITY_Duplicate_Check_Summary where (dGroupCol LIKE '%-%' OR dGroupVal LIKE '%-%') AND (dGroupCol NOT LIKE '%?::?%' OR dGroupVal NOT LIKE '%?::?%'))  then
                  UPDATE DATA_QUALITY_Duplicate_Check_Summary SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal,'NON-INDIVIDUAL','NON#INDIVIDUAL'),'CRD-CCB','CRD#CCB'),'CRD-IB','CRD#IB') WHERE (dGroupVal LIKE '%-%') AND (dGroupVal NOT LIKE '%?::?%');
                  UPDATE DATA_QUALITY_Duplicate_Check_Summary SET dGroupCol = REPLACE(dGroupCol, '-', '?::?'), dGroupVal = REPLACE(dGroupVal, '-', '?::?') WHERE (dGroupCol LIKE '%-%' OR dGroupVal LIKE '%-%') AND (dGroupCol NOT LIKE '%?::?%' OR dGroupVal NOT LIKE '%?::?%');
                  UPDATE DATA_QUALITY_Duplicate_Check_Summary SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal, 'NON#INDIVIDUAL','NON-INDIVIDUAL'),'CRD#CCB','CRD-CCB'),'CRD#IB','CRD-IB') WHERE (dGroupVal LIKE '%?::?%') AND (dGroupVal NOT LIKE '%-%');
            end if;

           if exists (select 1 from  DATA_QUALITY_GlobalRules where (dGroupCol LIKE '%-%' OR dGroupVal LIKE '%-%') AND (dGroupCol NOT LIKE '%?::?%' OR dGroupVal NOT LIKE '%?::?%'))  then
                  UPDATE DATA_QUALITY_GlobalRules SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal,'NON-INDIVIDUAL','NON#INDIVIDUAL'),'CRD-CCB','CRD#CCB'),'CRD-IB','CRD#IB') WHERE (dGroupVal LIKE '%-%') AND (dGroupVal NOT LIKE '%?::?%');
                  UPDATE DATA_QUALITY_GlobalRules SET dGroupCol = REPLACE(dGroupCol, '-', '?::?'), dGroupVal = REPLACE(dGroupVal, '-', '?::?') WHERE (dGroupCol LIKE '%-%' OR dGroupVal LIKE '%-%') AND (dGroupCol NOT LIKE '%?::?%' OR dGroupVal NOT LIKE '%?::?%');
                  UPDATE DATA_QUALITY_GlobalRules SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal, 'NON#INDIVIDUAL','NON-INDIVIDUAL'),'CRD#CCB','CRD-CCB'),'CRD#IB','CRD-IB') WHERE (dGroupVal LIKE '%?::?%') AND (dGroupVal NOT LIKE '%-%');
           end if;

          if exists (select 1 from  DATA_QUALITY_Column_Summary where (dGroupCol LIKE '%-%' OR dGroupVal LIKE '%-%') AND (dGroupCol NOT LIKE '%?::?%' OR dGroupVal NOT LIKE '%?::?%'))  then
                 UPDATE DATA_QUALITY_Column_Summary SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal,'NON-INDIVIDUAL','NON#INDIVIDUAL'),'CRD-CCB','CRD#CCB'),'CRD-IB','CRD#IB') WHERE (dGroupVal LIKE '%-%') AND (dGroupVal NOT LIKE '%?::?%');
                 UPDATE DATA_QUALITY_Column_Summary SET dGroupCol = REPLACE(dGroupCol, '-', '?::?'), dGroupVal = REPLACE(dGroupVal, '-', '?::?') WHERE (dGroupCol LIKE '%-%' OR dGroupVal LIKE '%-%') AND (dGroupCol NOT LIKE '%?::?%' OR dGroupVal NOT LIKE '%?::?%');
                 UPDATE DATA_QUALITY_Column_Summary SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal, 'NON#INDIVIDUAL','NON-INDIVIDUAL'),'CRD#CCB','CRD-CCB'),'CRD#IB','CRD-IB') WHERE (dGroupVal LIKE '%?::?%') AND (dGroupVal NOT LIKE '%-%');
          end if;

          if exists (select 1 from  DATA_QUALITY_Record_Anomaly where (dGroupCol LIKE '%-%' OR dGroupVal LIKE '%-%') AND (dGroupCol NOT LIKE '%?::?%' OR dGroupVal NOT LIKE '%?::?%'))  then
               UPDATE DATA_QUALITY_Record_Anomaly SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal,'NON-INDIVIDUAL','NON#INDIVIDUAL'),'CRD-CCB','CRD#CCB'),'CRD-IB','CRD#IB') WHERE (dGroupVal LIKE '%-%') AND (dGroupVal NOT LIKE '%?::?%');
               UPDATE DATA_QUALITY_Record_Anomaly SET dGroupCol = REPLACE(dGroupCol, '-', '?::?'), dGroupVal = REPLACE(dGroupVal, '-', '?::?') WHERE (dGroupCol LIKE '%-%' OR dGroupVal LIKE '%-%') AND (dGroupCol NOT LIKE '%?::?%' OR dGroupVal NOT LIKE '%?::?%');
               UPDATE DATA_QUALITY_Record_Anomaly SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal, 'NON#INDIVIDUAL','NON-INDIVIDUAL'),'CRD#CCB','CRD-CCB'),'CRD#IB','CRD-IB') WHERE (dGroupVal LIKE '%?::?%') AND (dGroupVal NOT LIKE '%-%');
          end if;

          if exists (select 1 from  DATA_QUALITY_Transaction_Detail_All where (dGroupCol LIKE '%-%' OR dGroupVal LIKE '%-%') AND (dGroupCol NOT LIKE '%?::?%' OR dGroupVal NOT LIKE '%?::?%'))  then
               UPDATE DATA_QUALITY_Transaction_Detail_All SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal,'NON-INDIVIDUAL','NON#INDIVIDUAL'),'CRD-CCB','CRD#CCB'),'CRD-IB','CRD#IB') WHERE (dGroupVal LIKE '%-%') AND (dGroupVal NOT LIKE '%?::?%');
               UPDATE DATA_QUALITY_Transaction_Detail_All SET dGroupCol = REPLACE(dGroupCol, '-', '?::?'), dGroupVal = REPLACE(dGroupVal, '-', '?::?') WHERE (dGroupCol LIKE '%-%' OR dGroupVal LIKE '%-%') AND (dGroupCol NOT LIKE '%?::?%' OR dGroupVal NOT LIKE '%?::?%');
               UPDATE DATA_QUALITY_Transaction_Detail_All SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal, 'NON#INDIVIDUAL','NON-INDIVIDUAL'),'CRD#CCB','CRD-CCB'),'CRD#IB','CRD-IB') WHERE (dGroupVal LIKE '%?::?%') AND (dGroupVal NOT LIKE '%-%');
          end if;

          if exists (select 1 from  DATA_QUALITY_Transaction_Detail_Identity where (dGroupCol LIKE '%-%' OR dGroupVal LIKE '%-%') AND (dGroupCol NOT LIKE '%?::?%' OR dGroupVal NOT LIKE '%?::?%'))  then
               UPDATE DATA_QUALITY_Transaction_Detail_Identity SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal,'NON-INDIVIDUAL','NON#INDIVIDUAL'),'CRD-CCB','CRD#CCB'),'CRD-IB','CRD#IB') WHERE (dGroupVal LIKE '%-%') AND (dGroupVal NOT LIKE '%?::?%');
               UPDATE DATA_QUALITY_Transaction_Detail_Identity SET dGroupCol = REPLACE(dGroupCol, '-', '?::?'), dGroupVal = REPLACE(dGroupVal, '-', '?::?') WHERE (dGroupCol LIKE '%-%' OR dGroupVal LIKE '%-%') AND (dGroupCol NOT LIKE '%?::?%' OR dGroupVal NOT LIKE '%?::?%');
               UPDATE DATA_QUALITY_Transaction_Detail_Identity SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal, 'NON#INDIVIDUAL','NON-INDIVIDUAL'),'CRD#CCB','CRD-CCB'),'CRD#IB','CRD-IB') WHERE (dGroupVal LIKE '%?::?%') AND (dGroupVal NOT LIKE '%-%');
          end if;

          if exists (select 1 from  DATA_QUALITY_Transactionset_sum_A1 where (dGroupCol LIKE '%-%' OR dGroupVal LIKE '%-%') AND (dGroupCol NOT LIKE '%?::?%' OR dGroupVal NOT LIKE '%?::?%'))  then
               UPDATE DATA_QUALITY_Transactionset_sum_A1 SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal,'NON-INDIVIDUAL','NON#INDIVIDUAL'),'CRD-CCB','CRD#CCB'),'CRD-IB','CRD#IB') WHERE (dGroupVal LIKE '%-%') AND (dGroupVal NOT LIKE '%?::?%');
               UPDATE DATA_QUALITY_Transactionset_sum_A1 SET dGroupCol = REPLACE(dGroupCol, '-', '?::?'), dGroupVal = REPLACE(dGroupVal, '-', '?::?') WHERE (dGroupCol LIKE '%-%' OR dGroupVal LIKE '%-%') AND (dGroupCol NOT LIKE '%?::?%' OR dGroupVal NOT LIKE '%?::?%');
               UPDATE DATA_QUALITY_Transactionset_sum_A1 SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal, 'NON#INDIVIDUAL','NON-INDIVIDUAL'),'CRD#CCB','CRD-CCB'),'CRD#IB','CRD-IB') WHERE (dGroupVal LIKE '%?::?%') AND (dGroupVal NOT LIKE '%-%');
          end if;

          if exists (select 1 from  DATA_QUALITY_Transactionset_sum_dgroup where (dGroupCol LIKE '%-%' OR dGroupVal LIKE '%-%') AND (dGroupCol NOT LIKE '%?::?%' OR dGroupVal NOT LIKE '%?::?%'))  then
               UPDATE DATA_QUALITY_Transactionset_sum_dgroup SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal,'NON-INDIVIDUAL','NON#INDIVIDUAL'),'CRD-CCB','CRD#CCB'),'CRD-IB','CRD#IB') WHERE (dGroupVal LIKE '%-%') AND (dGroupVal NOT LIKE '%?::?%');
               UPDATE DATA_QUALITY_Transactionset_sum_dgroup SET dGroupCol = REPLACE(dGroupCol, '-', '?::?'), dGroupVal = REPLACE(dGroupVal, '-', '?::?') WHERE (dGroupCol LIKE '%-%' OR dGroupVal LIKE '%-%') AND (dGroupCol NOT LIKE '%?::?%' OR dGroupVal NOT LIKE '%?::?%');
               UPDATE DATA_QUALITY_Transactionset_sum_dgroup SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal, 'NON#INDIVIDUAL','NON-INDIVIDUAL'),'CRD#CCB','CRD-CCB'),'CRD#IB','CRD-IB') WHERE (dGroupVal LIKE '%?::?%') AND (dGroupVal NOT LIKE '%-%');
          end if;

          if exists (select 1 from  DATA_QUALITY_Custom_Column_Summary where (dGroupCol LIKE '%-%' OR dGroupVal LIKE '%-%') AND (dGroupCol NOT LIKE '%?::?%' OR dGroupVal NOT LIKE '%?::?%'))  then
               UPDATE DATA_QUALITY_Custom_Column_Summary SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal,'NON-INDIVIDUAL','NON#INDIVIDUAL'),'CRD-CCB','CRD#CCB'),'CRD-IB','CRD#IB') WHERE (dGroupVal LIKE '%-%') AND (dGroupVal NOT LIKE '%?::?%');
               UPDATE DATA_QUALITY_Custom_Column_Summary SET dGroupCol = REPLACE(dGroupCol, '-', '?::?'), dGroupVal = REPLACE(dGroupVal, '-', '?::?') WHERE (dGroupCol LIKE '%-%' OR dGroupVal LIKE '%-%') AND (dGroupCol NOT LIKE '%?::?%' OR dGroupVal NOT LIKE '%?::?%');
               UPDATE DATA_QUALITY_Custom_Column_Summary SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal, 'NON#INDIVIDUAL','NON-INDIVIDUAL'),'CRD#CCB','CRD-CCB'),'CRD#IB','CRD-IB') WHERE (dGroupVal LIKE '%?::?%') AND (dGroupVal NOT LIKE '%-%');
          end if;

    end $$;


call dummy_do_not_use();
drop procedure if exists dummy_do_not_use;






