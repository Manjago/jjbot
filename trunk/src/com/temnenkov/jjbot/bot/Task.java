package com.temnenkov.jjbot.bot;

import org.joda.time.DateTime;

public class Task implements Comparable<Task> {

	public enum TaskType {
		SENDMSG, CHECKALIVE, EXPORTLOG
	}

	private final DateTime execDate;
	private final TaskType taskType;

	public Task(TaskType taskType, DateTime execDate) {
		super();
		this.execDate = execDate;
		this.taskType = taskType;
	}

	public Task(TaskType taskType, int mills) {
		this(taskType, new DateTime().plusMillis(mills));
	}
	
	@Override
	public int compareTo(Task o) {
		return getExecDate().compareTo(o.getExecDate());
	}

	public DateTime getExecDate() {
		return execDate;
	}

	public TaskType getTaskType() {
		return taskType;
	}

	@Override
	public String toString() {
		return "Task [execDate=" + execDate + ", taskType=" + taskType + "]";
	}

}
