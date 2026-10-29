package app.services.teamf;

import app.entities.teamf.Task;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.teamf.TaskMapper;

import java.time.LocalDate;
import java.time.LocalTime;


public class TaskService {
    private ConnectionPool connectionPool;
    private TaskMapper taskMapper;

    public TaskService(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
        taskMapper = new TaskMapper(connectionPool);
    }

    public Task createTask(String taskName, String taskDescription, int taskSetTime) throws DatabaseException {
        // Hvis vi vil have DateTime skal det gøres her, da det er forretningslogik. Kan ikke smides i datamapper
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();
        return taskMapper.createTask(new Task(taskName, taskDescription, taskSetTime, date, time));
    }

    public Task getTaskByID(int taskid) throws DatabaseException {
        return taskMapper.getTaskByID(taskid);
    }

    public void stopTask(Task task){
        taskMapper.stopTask(task);
    }
}
