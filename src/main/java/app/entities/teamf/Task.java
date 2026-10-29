package app.entities.teamf;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

public class Task {
    private int task_id;
    private String taskName;
    private String taskDescription;
    private int taskSetTime;
    private LocalDate taskDate;
    private LocalTime taskStartTime;
    private LocalDate taskStopDate;
    private LocalTime taskStopTime;
    private String taskStatus;
    // Remaining time?
    // Status, resultat, færdig/ikkefærdig?

    public Task() {
    }

    public Task(String taskName, String taskDescription){
        // 0 er en placeholder, PostGres Opretter taskID for os
        this(0, taskName, taskDescription);
    }

    public Task(int task_id, String taskName, String taskDescription) {
        this.task_id = task_id;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
    }

    public Task(String taskName, String taskDescription, LocalDate taskDate, LocalTime taskStartTime) {
        this(0, taskName, taskDescription, taskDate, taskStartTime);
    }

    public Task(int task_id, String taskName, String taskDescription, LocalDate taskDate, LocalTime taskStartTime) {
        this.task_id = task_id;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskDate = taskDate;
        this.taskStartTime = taskStartTime;
    }

    public Task(String taskName, String taskDescription, int taskSetTime, LocalDate taskDate, LocalTime taskStartTime) {
        this(0, taskName, taskDescription, taskSetTime, taskDate, taskStartTime);
    }

    public Task(int task_id, String taskName, String taskDescription, int taskSetTime, LocalDate taskDate, LocalTime taskStartTime) {
        this.task_id = task_id;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskSetTime = taskSetTime;
        this.taskDate = taskDate;
        this.taskStartTime = taskStartTime;
    }

    public int getTaskId() {
        return task_id;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public LocalDate getTaskDate(){
        return taskDate;
    }

    public LocalTime getTaskStartTime(){
        return taskStartTime;
    }

    public void setId(int task_id){
        this.task_id = task_id;
    }

    public Object getTaskSetTime() {
        return taskSetTime;
    }

    public void setTaskStop(){
        if (taskStopTime == null && taskStopDate == null){
            this.taskStopDate = LocalDate.now();
            this.taskStopTime = LocalTime.now();
            // Er doven så tager kun højde for tid og ikke dato
            Duration diff = Duration.between(taskStopTime, taskStartTime);
            if (diff.toMinutes() < taskSetTime){
                this.taskStatus = "CANCELLED";
            }
            else if (diff.toMinutes() == taskSetTime){
                this.taskStatus = "COMPLETED";
            }
            else {
                this.taskStatus = "EXPIRED";
            }
        }
    }
    public LocalDate getTaskStopDate(){
        return taskStopDate;
    }

    public LocalTime getTaskStopTime(){
        return taskStopTime;
    }
}