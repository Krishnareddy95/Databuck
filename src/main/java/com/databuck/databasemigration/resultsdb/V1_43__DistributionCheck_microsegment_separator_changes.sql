drop procedure if exists dummy_do_not_use;

delimiter $$
create procedure dummy_do_not_use()
begin
	
	if exists (SELECT 1 FROM DATA_QUALITY_DATA_DRIFT_COUNT_SUMMARY
                           WHERE (dGroupCol LIKE "%-%" OR dGroupVal LIKE "%-%") AND (dGroupCol NOT LIKE "%?::?%" OR dGroupVal NOT LIKE "%?::?%")) then
        UPDATE DATA_QUALITY_DATA_DRIFT_COUNT_SUMMARY SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal,"NON-INDIVIDUAL","NON#INDIVIDUAL"),"CRD-CCB","CRD#CCB"),"CRD-IB","CRD#IB") WHERE (dGroupVal LIKE "%-%") AND (dGroupVal NOT LIKE "%?::?%");
		UPDATE DATA_QUALITY_DATA_DRIFT_COUNT_SUMMARY SET dGroupCol = REPLACE(dGroupCol, "-", "?::?"), dGroupVal=REPLACE(dGroupVal, "-", "?::?") WHERE (dGroupCol LIKE "%-%" OR dGroupVal LIKE "%-%") AND (dGroupCol NOT LIKE "%?::?%" OR dGroupVal NOT LIKE "%?::?%");
		UPDATE DATA_QUALITY_DATA_DRIFT_COUNT_SUMMARY SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal, "NON#INDIVIDUAL","NON-INDIVIDUAL"),"CRD#CCB","CRD-CCB"),"CRD#IB","CRD-IB") WHERE (dGroupVal LIKE "%?::?%") AND (dGroupVal NOT LIKE "%-%");
	end if;

    if exists (SELECT 1 FROM DATA_QUALITY_DATA_DRIFT_SUMMARY
                           WHERE (dGroupCol LIKE "%-%" OR dGroupVal LIKE "%-%") AND (dGroupCol NOT LIKE "%?::?%" OR dGroupVal NOT LIKE "%?::?%")) then
         UPDATE DATA_QUALITY_DATA_DRIFT_SUMMARY SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal,"NON-INDIVIDUAL","NON#INDIVIDUAL"),"CRD-CCB","CRD#CCB"),"CRD-IB","CRD#IB") WHERE (dGroupVal LIKE "%-%") AND (dGroupVal NOT LIKE "%?::?%");
         UPDATE DATA_QUALITY_DATA_DRIFT_SUMMARY SET dGroupCol = REPLACE(dGroupCol, "-", "?::?"), dGroupVal=REPLACE(dGroupVal, "-", "?::?") WHERE (dGroupCol LIKE "%-%" OR dGroupVal LIKE "%-%") AND (dGroupCol NOT LIKE "%?::?%" OR dGroupVal NOT LIKE "%?::?%");
         UPDATE DATA_QUALITY_DATA_DRIFT_SUMMARY SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal, "NON#INDIVIDUAL","NON-INDIVIDUAL"),"CRD#CCB","CRD-CCB"),"CRD#IB","CRD-IB") WHERE (dGroupVal LIKE "%?::?%") AND (dGroupVal NOT LIKE "%-%");
    end if;

    if exists (SELECT 1 FROM DATA_QUALITY_DATA_DRIFT
                            WHERE (dGroupCol LIKE "%-%" OR dGroupVal LIKE "%-%") AND (dGroupCol NOT LIKE "%?::?%" OR dGroupVal NOT LIKE "%?::?%")) then
         UPDATE DATA_QUALITY_DATA_DRIFT SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal,"NON-INDIVIDUAL","NON#INDIVIDUAL"),"CRD-CCB","CRD#CCB"),"CRD-IB","CRD#IB") WHERE (dGroupVal LIKE "%-%") AND (dGroupVal NOT LIKE "%?::?%");
         UPDATE DATA_QUALITY_DATA_DRIFT SET dGroupCol = REPLACE(dGroupCol, "-", "?::?"), dGroupVal=REPLACE(dGroupVal, "-", "?::?") WHERE (dGroupCol LIKE "%-%" OR dGroupVal LIKE "%-%") AND (dGroupCol NOT LIKE "%?::?%" OR dGroupVal NOT LIKE "%?::?%");
         UPDATE DATA_QUALITY_DATA_DRIFT SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal, "NON#INDIVIDUAL","NON-INDIVIDUAL"),"CRD#CCB","CRD-CCB"),"CRD#IB","CRD-IB") WHERE (dGroupVal LIKE "%?::?%") AND (dGroupVal NOT LIKE "%-%");
    end if;

     if exists (SELECT 1 FROM DATA_QUALITY_DateRule_FailedRecords
                             WHERE (dGroupCol LIKE "%-%" OR dGroupVal LIKE "%-%") AND (dGroupCol NOT LIKE "%?::?%" OR dGroupVal NOT LIKE "%?::?%")) then
         UPDATE DATA_QUALITY_DateRule_FailedRecords SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal,"NON-INDIVIDUAL","NON#INDIVIDUAL"),"CRD-CCB","CRD#CCB"),"CRD-IB","CRD#IB") WHERE (dGroupVal LIKE "%-%") AND (dGroupVal NOT LIKE "%?::?%");
         UPDATE DATA_QUALITY_DateRule_FailedRecords SET dGroupCol = REPLACE(dGroupCol, "-", "?::?"), dGroupVal=REPLACE(dGroupVal, "-", "?::?") WHERE (dGroupCol LIKE "%-%" OR dGroupVal LIKE "%-%") AND (dGroupCol NOT LIKE "%?::?%" OR dGroupVal NOT LIKE "%?::?%");
         UPDATE DATA_QUALITY_DateRule_FailedRecords SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal, "NON#INDIVIDUAL","NON-INDIVIDUAL"),"CRD#CCB","CRD-CCB"),"CRD#IB","CRD-IB") WHERE (dGroupVal LIKE "%?::?%") AND (dGroupVal NOT LIKE "%-%");
     end if;

     if exists (SELECT 1 FROM DATA_QUALITY_DateRule_Reference
                              WHERE (dGroupCol LIKE "%-%" OR dGroupVal LIKE "%-%") AND (dGroupCol NOT LIKE "%?::?%" OR dGroupVal NOT LIKE "%?::?%")) then
          UPDATE DATA_QUALITY_DateRule_Reference SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal,"NON-INDIVIDUAL","NON#INDIVIDUAL"),"CRD-CCB","CRD#CCB"),"CRD-IB","CRD#IB") WHERE (dGroupVal LIKE "%-%") AND (dGroupVal NOT LIKE "%?::?%");
          UPDATE DATA_QUALITY_DateRule_Reference SET dGroupCol = REPLACE(dGroupCol, "-", "?::?"), dGroupVal=REPLACE(dGroupVal, "-", "?::?") WHERE (dGroupCol LIKE "%-%" OR dGroupVal LIKE "%-%") AND (dGroupCol NOT LIKE "%?::?%" OR dGroupVal NOT LIKE "%?::?%");
          UPDATE DATA_QUALITY_DateRule_Reference SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal, "NON#INDIVIDUAL","NON-INDIVIDUAL"),"CRD#CCB","CRD-CCB"),"CRD#IB","CRD-IB") WHERE (dGroupVal LIKE "%?::?%") AND (dGroupVal NOT LIKE "%-%");
     end if;

     if exists (SELECT 1 FROM DATA_QUALITY_Duplicate_Check_Summary
                              WHERE (dGroupCol LIKE "%-%" OR dGroupVal LIKE "%-%") AND (dGroupCol NOT LIKE "%?::?%" OR dGroupVal NOT LIKE "%?::?%")) then
          UPDATE DATA_QUALITY_Duplicate_Check_Summary SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal,"NON-INDIVIDUAL","NON#INDIVIDUAL"),"CRD-CCB","CRD#CCB"),"CRD-IB","CRD#IB") WHERE (dGroupVal LIKE "%-%") AND (dGroupVal NOT LIKE "%?::?%");
          UPDATE DATA_QUALITY_Duplicate_Check_Summary SET dGroupCol = REPLACE(dGroupCol, "-", "?::?"), dGroupVal=REPLACE(dGroupVal, "-", "?::?") WHERE (dGroupCol LIKE "%-%" OR dGroupVal LIKE "%-%") AND (dGroupCol NOT LIKE "%?::?%" OR dGroupVal NOT LIKE "%?::?%");
          UPDATE DATA_QUALITY_Duplicate_Check_Summary SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal, "NON#INDIVIDUAL","NON-INDIVIDUAL"),"CRD#CCB","CRD-CCB"),"CRD#IB","CRD-IB") WHERE (dGroupVal LIKE "%?::?%") AND (dGroupVal NOT LIKE "%-%");
     end if;

     if exists (SELECT 1 FROM DATA_QUALITY_GlobalRules
                              WHERE (dGroupCol LIKE "%-%" OR dGroupVal LIKE "%-%") AND (dGroupCol NOT LIKE "%?::?%" OR dGroupVal NOT LIKE "%?::?%")) then
          UPDATE DATA_QUALITY_GlobalRules SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal,"NON-INDIVIDUAL","NON#INDIVIDUAL"),"CRD-CCB","CRD#CCB"),"CRD-IB","CRD#IB") WHERE (dGroupVal LIKE "%-%") AND (dGroupVal NOT LIKE "%?::?%");
          UPDATE DATA_QUALITY_GlobalRules SET dGroupCol = REPLACE(dGroupCol, "-", "?::?"), dGroupVal=REPLACE(dGroupVal, "-", "?::?") WHERE (dGroupCol LIKE "%-%" OR dGroupVal LIKE "%-%") AND (dGroupCol NOT LIKE "%?::?%" OR dGroupVal NOT LIKE "%?::?%");
          UPDATE DATA_QUALITY_GlobalRules SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal, "NON#INDIVIDUAL","NON-INDIVIDUAL"),"CRD#CCB","CRD-CCB"),"CRD#IB","CRD-IB") WHERE (dGroupVal LIKE "%?::?%") AND (dGroupVal NOT LIKE "%-%");
     end if;

     if exists (SELECT 1 FROM DATA_QUALITY_Column_Summary
                              WHERE (dGroupCol LIKE "%-%" OR dGroupVal LIKE "%-%") AND (dGroupCol NOT LIKE "%?::?%" OR dGroupVal NOT LIKE "%?::?%")) then
          UPDATE DATA_QUALITY_Column_Summary SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal,"NON-INDIVIDUAL","NON#INDIVIDUAL"),"CRD-CCB","CRD#CCB"),"CRD-IB","CRD#IB") WHERE (dGroupVal LIKE "%-%") AND (dGroupVal NOT LIKE "%?::?%");
          UPDATE DATA_QUALITY_Column_Summary SET dGroupCol = REPLACE(dGroupCol, "-", "?::?"), dGroupVal=REPLACE(dGroupVal, "-", "?::?") WHERE (dGroupCol LIKE "%-%" OR dGroupVal LIKE "%-%") AND (dGroupCol NOT LIKE "%?::?%" OR dGroupVal NOT LIKE "%?::?%");
          UPDATE DATA_QUALITY_Column_Summary SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal, "NON#INDIVIDUAL","NON-INDIVIDUAL"),"CRD#CCB","CRD-CCB"),"CRD#IB","CRD-IB") WHERE (dGroupVal LIKE "%?::?%") AND (dGroupVal NOT LIKE "%-%");
     end if;

     if exists (SELECT 1 FROM DATA_QUALITY_Record_Anomaly
                              WHERE (dGroupCol LIKE "%-%" OR dGroupVal LIKE "%-%") AND (dGroupCol NOT LIKE "%?::?%" OR dGroupVal NOT LIKE "%?::?%")) then
          UPDATE DATA_QUALITY_Record_Anomaly SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal,"NON-INDIVIDUAL","NON#INDIVIDUAL"),"CRD-CCB","CRD#CCB"),"CRD-IB","CRD#IB") WHERE (dGroupVal LIKE "%-%") AND (dGroupVal NOT LIKE "%?::?%");
          UPDATE DATA_QUALITY_Record_Anomaly SET dGroupCol = REPLACE(dGroupCol, "-", "?::?"), dGroupVal=REPLACE(dGroupVal, "-", "?::?") WHERE (dGroupCol LIKE "%-%" OR dGroupVal LIKE "%-%") AND (dGroupCol NOT LIKE "%?::?%" OR dGroupVal NOT LIKE "%?::?%");
          UPDATE DATA_QUALITY_Record_Anomaly SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal, "NON#INDIVIDUAL","NON-INDIVIDUAL"),"CRD#CCB","CRD-CCB"),"CRD#IB","CRD-IB") WHERE (dGroupVal LIKE "%?::?%") AND (dGroupVal NOT LIKE "%-%");
     end if;

    if exists (SELECT 1 FROM DATA_QUALITY_Transaction_Detail_All
                              WHERE (dGroupCol LIKE "%-%" OR dGroupVal LIKE "%-%") AND (dGroupCol NOT LIKE "%?::?%" OR dGroupVal NOT LIKE "%?::?%")) then
          UPDATE DATA_QUALITY_Transaction_Detail_All SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal,"NON-INDIVIDUAL","NON#INDIVIDUAL"),"CRD-CCB","CRD#CCB"),"CRD-IB","CRD#IB") WHERE (dGroupVal LIKE "%-%") AND (dGroupVal NOT LIKE "%?::?%");
          UPDATE DATA_QUALITY_Transaction_Detail_All SET dGroupCol = REPLACE(dGroupCol, "-", "?::?"), dGroupVal=REPLACE(dGroupVal, "-", "?::?") WHERE (dGroupCol LIKE "%-%" OR dGroupVal LIKE "%-%") AND (dGroupCol NOT LIKE "%?::?%" OR dGroupVal NOT LIKE "%?::?%");
          UPDATE DATA_QUALITY_Transaction_Detail_All SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal, "NON#INDIVIDUAL","NON-INDIVIDUAL"),"CRD#CCB","CRD-CCB"),"CRD#IB","CRD-IB") WHERE (dGroupVal LIKE "%?::?%") AND (dGroupVal NOT LIKE "%-%");
    end if;

    if exists (SELECT 1 FROM DATA_QUALITY_Transaction_Detail_Identity
                              WHERE (dGroupCol LIKE "%-%" OR dGroupVal LIKE "%-%") AND (dGroupCol NOT LIKE "%?::?%" OR dGroupVal NOT LIKE "%?::?%")) then
          UPDATE DATA_QUALITY_Transaction_Detail_Identity SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal,"NON-INDIVIDUAL","NON#INDIVIDUAL"),"CRD-CCB","CRD#CCB"),"CRD-IB","CRD#IB") WHERE (dGroupVal LIKE "%-%") AND (dGroupVal NOT LIKE "%?::?%");
          UPDATE DATA_QUALITY_Transaction_Detail_Identity SET dGroupCol = REPLACE(dGroupCol, "-", "?::?"), dGroupVal=REPLACE(dGroupVal, "-", "?::?") WHERE (dGroupCol LIKE "%-%" OR dGroupVal LIKE "%-%") AND (dGroupCol NOT LIKE "%?::?%" OR dGroupVal NOT LIKE "%?::?%");
          UPDATE DATA_QUALITY_Transaction_Detail_Identity SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal, "NON#INDIVIDUAL","NON-INDIVIDUAL"),"CRD#CCB","CRD-CCB"),"CRD#IB","CRD-IB") WHERE (dGroupVal LIKE "%?::?%") AND (dGroupVal NOT LIKE "%-%");
    end if;

    if exists (SELECT 1 FROM DATA_QUALITY_Transactionset_sum_A1
                              WHERE (dGroupCol LIKE "%-%" OR dGroupVal LIKE "%-%") AND (dGroupCol NOT LIKE "%?::?%" OR dGroupVal NOT LIKE "%?::?%")) then
          UPDATE DATA_QUALITY_Transactionset_sum_A1 SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal,"NON-INDIVIDUAL","NON#INDIVIDUAL"),"CRD-CCB","CRD#CCB"),"CRD-IB","CRD#IB") WHERE (dGroupVal LIKE "%-%") AND (dGroupVal NOT LIKE "%?::?%");
          UPDATE DATA_QUALITY_Transactionset_sum_A1 SET dGroupCol = REPLACE(dGroupCol, "-", "?::?"), dGroupVal=REPLACE(dGroupVal, "-", "?::?") WHERE (dGroupCol LIKE "%-%" OR dGroupVal LIKE "%-%") AND (dGroupCol NOT LIKE "%?::?%" OR dGroupVal NOT LIKE "%?::?%");
          UPDATE DATA_QUALITY_Transactionset_sum_A1 SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal, "NON#INDIVIDUAL","NON-INDIVIDUAL"),"CRD#CCB","CRD-CCB"),"CRD#IB","CRD-IB") WHERE (dGroupVal LIKE "%?::?%") AND (dGroupVal NOT LIKE "%-%");
    end if;

    if exists (SELECT 1 FROM DATA_QUALITY_Transactionset_sum_dgroup
                              WHERE (dGroupCol LIKE "%-%" OR dGroupVal LIKE "%-%") AND (dGroupCol NOT LIKE "%?::?%" OR dGroupVal NOT LIKE "%?::?%")) then
          UPDATE DATA_QUALITY_Transactionset_sum_dgroup SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal,"NON-INDIVIDUAL","NON#INDIVIDUAL"),"CRD-CCB","CRD#CCB"),"CRD-IB","CRD#IB") WHERE (dGroupVal LIKE "%-%") AND (dGroupVal NOT LIKE "%?::?%");
          UPDATE DATA_QUALITY_Transactionset_sum_dgroup SET dGroupCol = REPLACE(dGroupCol, "-", "?::?"), dGroupVal=REPLACE(dGroupVal, "-", "?::?") WHERE (dGroupCol LIKE "%-%" OR dGroupVal LIKE "%-%") AND (dGroupCol NOT LIKE "%?::?%" OR dGroupVal NOT LIKE "%?::?%");
          UPDATE DATA_QUALITY_Transactionset_sum_dgroup SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal, "NON#INDIVIDUAL","NON-INDIVIDUAL"),"CRD#CCB","CRD-CCB"),"CRD#IB","CRD-IB") WHERE (dGroupVal LIKE "%?::?%") AND (dGroupVal NOT LIKE "%-%");
    end if;

    if exists (SELECT 1 FROM DATA_QUALITY_Custom_Column_Summary
                              WHERE (dGroupCol LIKE "%-%" OR dGroupVal LIKE "%-%") AND (dGroupCol NOT LIKE "%?::?%" OR dGroupVal NOT LIKE "%?::?%")) then
          UPDATE DATA_QUALITY_Custom_Column_Summary SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal,"NON-INDIVIDUAL","NON#INDIVIDUAL"),"CRD-CCB","CRD#CCB"),"CRD-IB","CRD#IB") WHERE (dGroupVal LIKE "%-%") AND (dGroupVal NOT LIKE "%?::?%");
          UPDATE DATA_QUALITY_Custom_Column_Summary SET dGroupCol = REPLACE(dGroupCol, "-", "?::?"), dGroupVal=REPLACE(dGroupVal, "-", "?::?") WHERE (dGroupCol LIKE "%-%" OR dGroupVal LIKE "%-%") AND (dGroupCol NOT LIKE "%?::?%" OR dGroupVal NOT LIKE "%?::?%");
          UPDATE DATA_QUALITY_Custom_Column_Summary SET dGroupVal = REPLACE(REPLACE(REPLACE(dGroupVal, "NON#INDIVIDUAL","NON-INDIVIDUAL"),"CRD#CCB","CRD-CCB"),"CRD#IB","CRD-IB") WHERE (dGroupVal LIKE "%?::?%") AND (dGroupVal NOT LIKE "%-%");
    end if;

end $$
delimiter ;

call dummy_do_not_use;
drop procedure if exists dummy_do_not_use;
