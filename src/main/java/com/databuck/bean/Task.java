package com.databuck.bean;

import java.io.Serializable;
/**
 * 
 * this class is used for view module
 * in Adminstration
 * @author appzop6
 *
 */
public class Task implements Serializable {

	private long idTask;
	private String taskName;

	public long getIdTask() {
		return idTask;
	}

	public void setIdTask(long idTask) {
		this.idTask = idTask;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public Task() {

		this.idTask = idTask;
		this.taskName = taskName;
	}

	@Override
	public String toString() {
		return "Task [idTask=" + idTask + ", taskName=" + taskName + "]";
	}
	

}
