package app.controllers.teamf;

import app.entities.teamf.Task;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.services.teamf.TaskService;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;

import java.util.Map;

public class TaskController {
    private ConnectionPool connectionPool;
    private TaskService taskService;

    public TaskController(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
        this.taskService = new TaskService(connectionPool);

    }

    public void setRoutes(JavalinConfig config) {
        config.routes.get("/hokusfokus", ctx -> ctx.render("teamf/frontpage.html"));
        config.routes.post("/hokusfokus/create", ctx -> createTask(ctx));
        config.routes.get("/hokusfokus/task/{taskid}", ctx -> startTask(ctx));
        config.routes.post("/hokusfokus/task/{taskid}/stopped", ctx -> stopTask(ctx));
    }

    public void createTask(Context ctx) throws DatabaseException {
        String taskName = ctx.formParam("taskName");
        String taskDescription = ctx.formParam("taskDescription");
        int taskSetTime = Integer.parseInt(ctx.formParam("tasksettime"));
        Task task = null;
        try {
            task = taskService.createTask(taskName, taskDescription, taskSetTime);
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
        int taskid = task.getTaskId();
        ctx.sessionAttribute("taskid", taskid);
        String taskPath = String.format("/hokusfokus/task/%s", taskid);
        ctx.redirect(taskPath);
    }

    public void startTask(Context ctx) throws DatabaseException {
        Task task = getTaskByID(ctx);
        ctx.render("teamf/task.html", Map.of("task", task));
    }

    public void stopTask(Context ctx) throws DatabaseException {
        Task task = getTaskByID(ctx);
        taskService.stopTask(task);
        ctx.redirect("/hokusfokus");
    }

    public Task getTaskByID(Context ctx) throws DatabaseException {
        String path_taskid = ctx.pathParam("taskid");
        int taskid = Integer.parseInt(path_taskid);
        Task task = taskService.getTaskByID(taskid);
        return task;
    }
}
