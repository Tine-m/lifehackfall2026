package app.persistence.teamf;

import app.entities.teamf.Task;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class TaskMapper {

    ConnectionPool connectionPool;
    private static final Logger logger =
            LoggerFactory.getLogger(TaskMapper.class);

    public TaskMapper(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
    }


    public Task createTask(Task task) throws DatabaseException {
        String query = "INSERT INTO teamf_tasks (taskname, taskdescription, tasksettime, taskdate, taskstarttime) " +
                " VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = connectionPool.getConnection();
             // Det er her vi får taskID fra PostGres (som så bliver smidt ind i Stringen/PreparedStatement?)
             // Skal læse lidt mere på præcis hvad den her linje kode gør
             PreparedStatement stm = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            // Så smider vi resten af værdierne fra User entiteten ind
            stm.setString(1, task.getTaskName());
            stm.setString(2, task.getTaskDescription());
            stm.setObject(3, task.getTaskSetTime());
            stm.setObject(4, task.getTaskDate());
            stm.setObject(5, task.getTaskStartTime());
            stm.executeUpdate();
            // Så vi kører faktisk først vores PreparedStatement her og gemmer resultatet inde i rs som er type ResultSet
            //      som vores PreparedStatement returnerer.
            try (ResultSet rs = stm.getGeneratedKeys()) {
                // hvis rs faktisk returnerer noget og vi har en linje
                if (rs.next()) {
                    //sæt vores taskID til det nummer som vi har fået returneret
                    task.setId(rs.getInt("taskid"));
                }
                //ellers så kast en fejl
                else throw new DatabaseException("Tasken kunne ikke oprettes");
            }
        }
        //Hvis der kommer en SQL exception så fang fejlen og kast en besked
        catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DatabaseException("Tasken blev ikke gemt ");
        }
        return task;
    }

    public Task getTaskByID(int id) throws DatabaseException {
        String query = "SELECT taskid, taskname, taskdescription, tasksettime, taskdate, taskstarttime FROM teamf_tasks WHERE taskid = ?";
        Task task = null;
        try(Connection connection = connectionPool.getConnection();
            PreparedStatement stm = connection.prepareStatement(query)){
            stm.setInt(1,id);
            try(ResultSet rs = stm.executeQuery()){
                if(rs.next()){
                    int taskID = rs.getInt("taskid");
                    String taskName = rs.getString("taskname");
                    String taskDescription = rs.getString("taskdescription");
                    int taskSetTime = Integer.parseInt(rs.getString("tasksettime"));
                    LocalDate taskDate = LocalDate.parse(rs.getString("taskdate"));
                    LocalTime taskStart = LocalTime.parse(rs.getString("taskstarttime"));
                    task = new Task(taskID, taskName, taskDescription, taskSetTime, taskDate, taskStart);
                }
            }
            return task;
        }

        catch (SQLException e){
            logger.error(e.getMessage());
            throw new DatabaseException("Vi kan ikke søge efter tasks lige nu");
        }
    }

    public void stopTask(Task task){
        int taskid = task.getTaskId();
        task.setTaskStop();
        LocalDate taskStopDate = task.getTaskStopDate();
        LocalTime taskStopTime = task.getTaskStopTime();
        /*
        String query = "UPDATE teamf_tasks SET taskstoptime = ?, taskstopdate = ? WHERE taskid = ?";
        try(Connection connection = ConnectionPool.getConnection();
            PreparedStatement stm = connection.prepareStatement(query)){
            stm.setInt(1,id);
            try(ResultSet rs = stm.executeQuery()){
                if(rs.next()){
                    int taskID = rs.getInt("taskid");
                    String taskName = rs.getString("taskname");
                    String taskDescription = rs.getString("taskdescription");
                    int taskSetTime = Integer.parseInt(rs.getString("tasksettime"));
                    LocalDate taskDate = LocalDate.parse(rs.getString("taskdate"));
                    LocalTime taskStart = LocalTime.parse(rs.getString("taskstarttime"));
                    task = new Task(taskID, taskName, taskDescription, taskSetTime, taskDate, taskStart);
                }
            }
            return task;
        }

         */
    }

}
