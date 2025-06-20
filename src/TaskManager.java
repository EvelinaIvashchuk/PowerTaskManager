import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages a collection of tasks and provides CRUD operations.
 */
public class TaskManager {
    private final List<Task> tasks;

    /**
     * Creates a new TaskManager with an empty task list.
     */
    public TaskManager() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a new task and adds it to the task list.
     *
     * @param title       The title of the task
     * @param description The detailed description of the task
     * @param deadline    The deadline for the task
     * @param priority    The priority level of the task
     * @return The newly created task
     */
    public Task createTask(String title, String description, LocalDateTime deadline, Task.Priority priority) {
        Task task = new Task(title, description, deadline, priority);
        tasks.add(task);
        return task;
    }

    /**
     * Returns all tasks in the task list.
     *
     * @return A list of all tasks
     */
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks);
    }

    /**
     * Finds a task by its ID.
     *
     * @param id The ID of the task to find
     * @return The task with the specified ID, or null if not found
     */
    public Task getTaskById(int id) {
        return tasks.stream()
                .filter(task -> task.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Updates an existing task.
     *
     * @param id          The ID of the task to update
     * @param title       The new title
     * @param description The new description
     * @param deadline    The new deadline
     * @param priority    The new priority
     * @param status      The new status
     * @return true if the task was updated, false if the task was not found
     */
    public boolean updateTask(int id, String title, String description, LocalDateTime deadline, 
                             Task.Priority priority, Task.Status status) {
        Task task = getTaskById(id);
        if (task == null) {
            return false;
        }

        task.setTitle(title);
        task.setDescription(description);
        task.setDeadline(deadline);
        task.setPriority(priority);
        task.setStatus(status);
        return true;
    }

    /**
     * Deletes a task by its ID.
     *
     * @param id The ID of the task to delete
     * @return true if the task was deleted, false if the task was not found
     */
    public boolean deleteTask(int id) {
        return tasks.removeIf(task -> task.getId() == id);
    }

    /**
     * Searches for tasks containing the specified keyword in their title or description.
     *
     * @param keyword The keyword to search for
     * @return A list of tasks matching the search criteria
     */
    public List<Task> searchTasks(String keyword) {
        String lowercaseKeyword = keyword.toLowerCase();
        return tasks.stream()
                .filter(task -> 
                    task.getTitle().toLowerCase().contains(lowercaseKeyword) || 
                    task.getDescription().toLowerCase().contains(lowercaseKeyword))
                .collect(Collectors.toList());
    }

    /**
     * Searches for tasks by specific fields.
     *
     * @param title The title to search for (can be null or empty to ignore this field)
     * @param status The status to search for (can be null to ignore this field)
     * @param priority The priority to search for (can be null to ignore this field)
     * @return A list of tasks matching the search criteria
     */
    public List<Task> searchTasksByFields(String title, Task.Status status, Task.Priority priority) {
        return tasks.stream()
                .filter(task -> 
                    (title == null || title.isEmpty() || task.getTitle().toLowerCase().contains(title.toLowerCase())) &&
                    (status == null || task.getStatus() == status) &&
                    (priority == null || task.getPriority() == priority))
                .collect(Collectors.toList());
    }

    /**
     * Sorts tasks by the specified criteria.
     *
     * @param criteria The criteria to sort by (e.g., "deadline", "priority", "status")
     * @param ascending Whether to sort in ascending order
     * @return A sorted list of tasks
     */
    public List<Task> sortTasks(String criteria, boolean ascending) {
        Comparator<Task> comparator;

        switch (criteria.toLowerCase()) {
            case "deadline":
                comparator = Comparator.comparing(Task::getDeadline);
                break;
            case "priority":
                comparator = Comparator.comparing(Task::getPriority);
                break;
            case "status":
                comparator = Comparator.comparing(Task::getStatus);
                break;
            case "title":
                comparator = Comparator.comparing(Task::getTitle);
                break;
            default:
                comparator = Comparator.comparing(Task::getId);
                break;
        }

        if (!ascending) {
            comparator = comparator.reversed();
        }

        return tasks.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }
}
