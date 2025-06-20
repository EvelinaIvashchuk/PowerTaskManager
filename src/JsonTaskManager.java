import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages JSON serialization and deserialization of tasks.
 * Provides functionality to save tasks to a file and load tasks from a file.
 */
public class JsonTaskManager {
    private static final String DEFAULT_FILE_PATH = "tasks.json";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Creates a new JsonTaskManager.
     */
    public JsonTaskManager() {
        // No initialization needed
    }

    /**
     * Saves a list of tasks to a JSON file.
     *
     * @param tasks The tasks to save
     * @param filePath The path to the file (optional, uses default if not provided)
     * @return true if the tasks were saved successfully, false otherwise
     */
    public boolean saveTasks(List<Task> tasks, String filePath) {
        String path = (filePath != null && !filePath.isEmpty()) ? filePath : DEFAULT_FILE_PATH;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write(tasksToJson(tasks));
            return true;
        } catch (IOException e) {
            System.err.println("Error saving tasks to file: " + e.getMessage());
            return false;
        }
    }

    /**
     * Overloaded method to save tasks using the default file path.
     *
     * @param tasks The tasks to save
     * @return true if the tasks were saved successfully, false otherwise
     */
    public boolean saveTasks(List<Task> tasks) {
        return saveTasks(tasks, DEFAULT_FILE_PATH);
    }

    /**
     * Loads tasks from a JSON file.
     *
     * @param filePath The path to the file (optional, uses default if not provided)
     * @return The list of tasks loaded from the file, or an empty list if the file doesn't exist or an error occurs
     */
    public List<Task> loadTasks(String filePath) {
        String path = (filePath != null && !filePath.isEmpty()) ? filePath : DEFAULT_FILE_PATH;
        File file = new File(path);

        if (!file.exists()) {
            System.out.println("File does not exist: " + path);
            return new ArrayList<>();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
            return jsonToTasks(jsonContent.toString());
        } catch (IOException e) {
            System.err.println("Error loading tasks from file: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Overloaded method to load tasks using the default file path.
     *
     * @return The list of tasks loaded from the file, or an empty list if the file doesn't exist or an error occurs
     */
    public List<Task> loadTasks() {
        return loadTasks(DEFAULT_FILE_PATH);
    }

    /**
     * Converts a list of tasks to a JSON string.
     *
     * @param tasks The tasks to convert
     * @return A JSON string representing the tasks
     */
    private String tasksToJson(List<Task> tasks) {
        StringBuilder json = new StringBuilder();
        json.append("[\n");

        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            json.append("  {\n");
            json.append("    \"id\": ").append(task.getId()).append(",\n");
            json.append("    \"title\": \"").append(escapeJson(task.getTitle())).append("\",\n");
            json.append("    \"description\": \"").append(escapeJson(task.getDescription())).append("\",\n");
            json.append("    \"deadline\": \"").append(task.getDeadline().format(DATE_FORMATTER)).append("\",\n");
            json.append("    \"priority\": \"").append(task.getPriority()).append("\",\n");
            json.append("    \"status\": \"").append(task.getStatus()).append("\"\n");
            json.append("  }");

            if (i < tasks.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }

        json.append("]");
        return json.toString();
    }

    /**
     * Converts a JSON string to a list of tasks.
     *
     * @param json The JSON string to convert
     * @return A list of tasks
     */
    private List<Task> jsonToTasks(String json) {
        List<Task> tasks = new ArrayList<>();

        // Simple JSON parsing without external libraries
        // This is a basic implementation and might not handle all edge cases
        try {
            if (json.trim().startsWith("[") && json.trim().endsWith("]")) {
                // Remove the outer brackets
                json = json.trim().substring(1, json.trim().length() - 1).trim();

                // Split by object delimiter
                String[] taskObjects = splitJsonObjects(json);

                for (String taskObject : taskObjects) {
                    if (!taskObject.trim().isEmpty()) {
                        Task task = parseTaskObject(taskObject.trim());
                        if (task != null) {
                            tasks.add(task);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
        }

        return tasks;
    }

    /**
     * Splits a JSON string into individual object strings.
     *
     * @param json The JSON string to split
     * @return An array of object strings
     */
    private String[] splitJsonObjects(String json) {
        List<String> objects = new ArrayList<>();
        int depth = 0;
        StringBuilder currentObject = new StringBuilder();

        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);

            if (c == '{') {
                depth++;
                currentObject.append(c);
            } else if (c == '}') {
                depth--;
                currentObject.append(c);

                if (depth == 0) {
                    objects.add(currentObject.toString());
                    currentObject = new StringBuilder();

                    // Skip the comma and whitespace after the object
                    while (i + 1 < json.length() && (json.charAt(i + 1) == ',' || Character.isWhitespace(json.charAt(i + 1)))) {
                        i++;
                    }
                }
            } else if (depth > 0) {
                currentObject.append(c);
            }
        }

        return objects.toArray(new String[0]);
    }

    /**
     * Parses a JSON object string into a Task object.
     *
     * @param json The JSON object string to parse
     * @return A Task object, or null if parsing fails
     */
    private Task parseTaskObject(String json) {
        try {
            // Remove the outer braces
            json = json.substring(1, json.length() - 1).trim();

            int id = -1;
            String title = "";
            String description = "";
            LocalDateTime deadline = null;
            Task.Priority priority = Task.Priority.MEDIUM;
            Task.Status status = Task.Status.TODO;

            // Split by properties
            String[] properties = json.split(",(?=\\s*\"[^\"]+\"\\s*:)");

            for (String property : properties) {
                String[] keyValue = property.split(":", 2);
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim().replace("\"", "");
                    String value = keyValue[1].trim();

                    // Remove quotes from string values
                    if (value.startsWith("\"") && value.endsWith("\"")) {
                        value = value.substring(1, value.length() - 1);
                    }

                    switch (key) {
                        case "id":
                            id = Integer.parseInt(value);
                            break;
                        case "title":
                            title = value;
                            break;
                        case "description":
                            description = value;
                            break;
                        case "deadline":
                            deadline = LocalDateTime.parse(value, DATE_FORMATTER);
                            break;
                        case "priority":
                            priority = Task.Priority.valueOf(value);
                            break;
                        case "status":
                            status = Task.Status.valueOf(value);
                            break;
                    }
                }
            }

            // Create a new task with the parsed values
            // Note: This assumes the Task class has a constructor that accepts all these parameters
            // If not, we'll need to create the task and set its properties individually
            Task task = new Task(title, description, deadline, priority);
            task.setStatus(status);

            // Handle the ID (this might need adjustment based on how Task IDs are managed)
            // For now, we'll just create the task and assume the ID is handled internally

            return task;
        } catch (Exception e) {
            System.err.println("Error parsing task object: " + e.getMessage());
            return null;
        }
    }

    /**
     * Escapes special characters in a string for JSON.
     *
     * @param input The string to escape
     * @return The escaped string
     */
    private String escapeJson(String input) {
        if (input == null) {
            return "";
        }

        return input.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}
