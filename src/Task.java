import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task in the task management system.
 */
public class Task {
    private static int nextId = 1;
    private final int id;
    private String title;
    private String description;
    private LocalDateTime deadline;
    private Priority priority;
    private Status status;

    /**
     * Priority levels for tasks.
     */
    public enum Priority {
        LOW, MEDIUM, HIGH
    }

    /**
     * Status options for tasks.
     */
    public enum Status {
        TODO, IN_PROGRESS, DONE
    }

    /**
     * Creates a new task with the specified details.
     *
     * @param title       The title of the task
     * @param description The detailed description of the task
     * @param deadline    The deadline for the task
     * @param priority    The priority level of the task
     */
    public Task(String title, String description, LocalDateTime deadline, Priority priority) {
        this.id = nextId++;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.priority = priority;
        this.status = Status.TODO;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return String.format("Завдання #%d: %s [%s]\n" +
                        "Опис: %s\n" +
                        "Дедлайн: %s\n" +
                        "Пріоритет: %s\n" +
                        "Статус: %s",
                id, title, getUkrainianStatus(status), description, deadline.format(formatter), getUkrainianPriority(priority), getUkrainianStatus(status));
    }

    /**
     * Gets the Ukrainian translation of the priority.
     *
     * @param priority The priority enum value
     * @return The priority in Ukrainian
     */
    private String getUkrainianPriority(Priority priority) {
        switch (priority) {
            case LOW:
                return "НИЗЬКИЙ";
            case MEDIUM:
                return "СЕРЕДНІЙ";
            case HIGH:
                return "ВИСОКИЙ";
            default:
                return priority.toString();
        }
    }

    /**
     * Gets the Ukrainian translation of the status.
     *
     * @param status The status enum value
     * @return The status in Ukrainian
     */
    private String getUkrainianStatus(Status status) {
        switch (status) {
            case TODO:
                return "ДО ВИКОНАННЯ";
            case IN_PROGRESS:
                return "В ПРОЦЕСІ";
            case DONE:
                return "ВИКОНАНО";
            default:
                return status.toString();
        }
    }
}
