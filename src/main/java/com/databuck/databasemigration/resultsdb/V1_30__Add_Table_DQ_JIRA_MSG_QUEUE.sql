/* ---------Creating table DQ_JIRA_MSG_QUEUE to save jira ticket publish details ----------- */

create table if not exists DQ_JIRA_MSG_QUEUE(
  id int(11) NOT NULL AUTO_INCREMENT,
  msg_body text NOT NULL,
  ticket_process_status varchar(20) NOT NULL default 'N',
  ticket_submit_status varchar(20) NOT NULL default 'N',
  createdAt datetime,
  PRIMARY KEY (id)
) engine=innodb auto_increment=1 default charset=latin1;