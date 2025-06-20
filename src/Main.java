import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Main class for the Power Task Manager application.
 * Provides a menu-driven interface for managing tasks.
 */
public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final TaskManager taskManager = new TaskManager();
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static final JsonTaskManager jsonTaskManager = new JsonTaskManager();

    public static void main(String[] args) {
        boolean running = true;

        System.out.println("Ласкаво просимо до Менеджера Завдань!");

        while (running) {
            displayMenu();
            int choice = getIntInput("Введіть ваш вибір: ");

            switch (choice) {
                case 1:
                    createTask();
                    break;
                case 2:
                    listTasks();
                    break;
                case 3:
                    updateTask();
                    break;
                case 4:
                    deleteTask();
                    break;
                case 5:
                    searchTasks();
                    break;
                case 6:
                    sortTasks();
                    break;
                case 7:
                    saveTasksToFile();
                    break;
                case 8:
                    loadTasksFromFile();
                    break;
                case 0:
                    running = false;
                    System.out.println("Дякуємо за використання Менеджера Завдань. До побачення!");
                    break;
                default:
                    System.out.println("Невірний вибір. Будь ласка, спробуйте ще раз.");
            }
        }

        scanner.close();
    }

    /**
     * Displays the main menu of the application.
     */
    private static void displayMenu() {
        System.out.println("\n===== МЕНЕДЖЕР ЗАВДАНЬ =====");
        System.out.println("1. Створити нове завдання");
        System.out.println("2. Переглянути всі завдання");
        System.out.println("3. Оновити завдання");
        System.out.println("4. Видалити завдання");
        System.out.println("5. Пошук завдань");
        System.out.println("6. Сортувати завдання");
        System.out.println("7. Зберегти завдання у файл");
        System.out.println("8. Завантажити завдання з файлу");
        System.out.println("0. Вихід");
        System.out.println("==============================");
    }

    /**
     * Gets integer input from the user.
     *
     * @param prompt The prompt to display to the user
     * @return The integer entered by the user
     */
    private static int getIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Будь ласка, введіть коректне число.");
            }
        }
    }

    /**
     * Gets a date and time input from the user.
     *
     * @param prompt The prompt to display to the user
     * @return The LocalDateTime entered by the user
     */
    private static LocalDateTime getDateTimeInput(String prompt) {
        while (true) {
            System.out.print(prompt + " (формат: yyyy-MM-dd HH:mm): ");
            try {
                String input = scanner.nextLine().trim();
                return LocalDateTime.parse(input, dateTimeFormatter);
            } catch (DateTimeParseException e) {
                System.out.println("Невірний формат дати. Будь ласка, використовуйте формат yyyy-MM-dd HH:mm.");
            }
        }
    }

    /**
     * Creates a new task based on user input.
     */
    private static void createTask() {
        System.out.println("\n----- Створення нового завдання -----");

        System.out.print("Введіть назву завдання: ");
        String title = scanner.nextLine().trim();

        System.out.print("Введіть опис завдання: ");
        String description = scanner.nextLine().trim();

        LocalDateTime deadline = getDateTimeInput("Введіть дедлайн завдання");

        System.out.println("Виберіть рівень пріоритету:");
        System.out.println("1. НИЗЬКИЙ");
        System.out.println("2. СЕРЕДНІЙ");
        System.out.println("3. ВИСОКИЙ");

        Task.Priority priority;
        while (true) {
            int priorityChoice = getIntInput("Введіть ваш вибір (1-3): ");
            if (priorityChoice >= 1 && priorityChoice <= 3) {
                priority = Task.Priority.values()[priorityChoice - 1];
                break;
            } else {
                System.out.println("Невірний вибір. Будь ласка, введіть число від 1 до 3.");
            }
        }

        Task task = taskManager.createTask(title, description, deadline, priority);
        System.out.println("Завдання успішно створено!");
        System.out.println(task);
    }

    /**
     * Lists all tasks or displays a message if no tasks exist.
     */
    private static void listTasks() {
        System.out.println("\n----- Список завдань -----");
        List<Task> tasks = taskManager.getAllTasks();

        if (tasks.isEmpty()) {
            System.out.println("Завдань не знайдено.");
            return;
        }

        for (Task task : tasks) {
            System.out.println(task);
            System.out.println("--------------------");
        }
    }

    /**
     * Updates an existing task based on user input.
     */
    private static void updateTask() {
        System.out.println("\n----- Оновлення завдання -----");

        int id = getIntInput("Введіть ID завдання для оновлення: ");
        Task task = taskManager.getTaskById(id);

        if (task == null) {
            System.out.println("Завдання не знайдено.");
            return;
        }

        System.out.println("Поточні деталі завдання:");
        System.out.println(task);

        System.out.print("Введіть нову назву (або натисніть Enter, щоб залишити поточну): ");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) {
            title = task.getTitle();
        }

        System.out.print("Введіть новий опис (або натисніть Enter, щоб залишити поточний): ");
        String description = scanner.nextLine().trim();
        if (description.isEmpty()) {
            description = task.getDescription();
        }

        LocalDateTime deadline;
        System.out.print("Оновити дедлайн? (т/н): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("т")) {
            deadline = getDateTimeInput("Введіть новий дедлайн");
        } else {
            deadline = task.getDeadline();
        }

        Task.Priority priority = task.getPriority();
        System.out.print("Оновити пріоритет? (т/н): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("т")) {
            System.out.println("Виберіть новий рівень пріоритету:");
            System.out.println("1. НИЗЬКИЙ");
            System.out.println("2. СЕРЕДНІЙ");
            System.out.println("3. ВИСОКИЙ");

            while (true) {
                int priorityChoice = getIntInput("Введіть ваш вибір (1-3): ");
                if (priorityChoice >= 1 && priorityChoice <= 3) {
                    priority = Task.Priority.values()[priorityChoice - 1];
                    break;
                } else {
                    System.out.println("Невірний вибір. Будь ласка, введіть число від 1 до 3.");
                }
            }
        }

        Task.Status status = task.getStatus();
        System.out.print("Оновити статус? (т/н): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("т")) {
            System.out.println("Виберіть новий статус:");
            System.out.println("1. ДО ВИКОНАННЯ");
            System.out.println("2. В ПРОЦЕСІ");
            System.out.println("3. ВИКОНАНО");

            while (true) {
                int statusChoice = getIntInput("Введіть ваш вибір (1-3): ");
                if (statusChoice >= 1 && statusChoice <= 3) {
                    status = Task.Status.values()[statusChoice - 1];
                    break;
                } else {
                    System.out.println("Невірний вибір. Будь ласка, введіть число від 1 до 3.");
                }
            }
        }

        boolean updated = taskManager.updateTask(id, title, description, deadline, priority, status);
        if (updated) {
            System.out.println("Завдання успішно оновлено!");
            System.out.println(taskManager.getTaskById(id));
        } else {
            System.out.println("Не вдалося оновити завдання.");
        }
    }

    /**
     * Deletes a task based on user input.
     */
    private static void deleteTask() {
        System.out.println("\n----- Видалення завдання -----");

        int id = getIntInput("Введіть ID завдання для видалення: ");
        Task task = taskManager.getTaskById(id);

        if (task == null) {
            System.out.println("Завдання не знайдено.");
            return;
        }

        System.out.println("Завдання для видалення:");
        System.out.println(task);

        System.out.print("Ви впевнені, що хочете видалити це завдання? (т/н): ");
        String confirmation = scanner.nextLine().trim();

        if (confirmation.equalsIgnoreCase("т")) {
            boolean deleted = taskManager.deleteTask(id);
            if (deleted) {
                System.out.println("Завдання успішно видалено!");
            } else {
                System.out.println("Не вдалося видалити завдання.");
            }
        } else {
            System.out.println("Видалення скасовано.");
        }
    }

    /**
     * Searches for tasks based on a keyword or specific fields.
     */
    private static void searchTasks() {
        System.out.println("\n----- Пошук завдань -----");

        System.out.println("Виберіть тип пошуку:");
        System.out.println("1. Пошук за ключовим словом");
        System.out.println("2. Пошук за полями");

        int searchTypeChoice = getIntInput("Введіть ваш вибір (1-2): ");

        List<Task> results;

        if (searchTypeChoice == 1) {
            // Search by keyword (existing functionality)
            System.out.print("Введіть ключове слово для пошуку: ");
            String keyword = scanner.nextLine().trim();

            results = taskManager.searchTasks(keyword);

            if (results.isEmpty()) {
                System.out.println("Не знайдено завдань, що відповідають '" + keyword + "'.");
                return;
            }

            System.out.println("Результати пошуку для '" + keyword + "':");
        } else if (searchTypeChoice == 2) {
            // Search by specific fields
            System.out.println("\n----- Пошук за полями -----");
            System.out.println("Залиште поле порожнім, щоб ігнорувати його при пошуку.");

            // Get title input
            System.out.print("Введіть назву завдання: ");
            String title = scanner.nextLine().trim();
            if (title.isEmpty()) {
                title = null;
            }

            // Get status input
            Task.Status status = null;
            System.out.print("Шукати за статусом? (т/н): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("т")) {
                System.out.println("Виберіть статус:");
                System.out.println("1. ДО ВИКОНАННЯ");
                System.out.println("2. В ПРОЦЕСІ");
                System.out.println("3. ВИКОНАНО");

                int statusChoice = getIntInput("Введіть ваш вибір (1-3): ");
                if (statusChoice >= 1 && statusChoice <= 3) {
                    status = Task.Status.values()[statusChoice - 1];
                }
            }

            // Get priority input
            Task.Priority priority = null;
            System.out.print("Шукати за пріоритетом? (т/н): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("т")) {
                System.out.println("Виберіть пріоритет:");
                System.out.println("1. НИЗЬКИЙ");
                System.out.println("2. СЕРЕДНІЙ");
                System.out.println("3. ВИСОКИЙ");

                int priorityChoice = getIntInput("Введіть ваш вибір (1-3): ");
                if (priorityChoice >= 1 && priorityChoice <= 3) {
                    priority = Task.Priority.values()[priorityChoice - 1];
                }
            }

            results = taskManager.searchTasksByFields(title, status, priority);

            if (results.isEmpty()) {
                System.out.println("Не знайдено завдань, що відповідають заданим критеріям.");
                return;
            }

            System.out.println("Результати пошуку за заданими критеріями:");
        } else {
            System.out.println("Невірний вибір. Повернення до головного меню.");
            return;
        }

        // Display results
        for (Task task : results) {
            System.out.println(task);
            System.out.println("--------------------");
        }
    }

    /**
     * Sorts and displays tasks based on user-selected criteria.
     */
    private static void sortTasks() {
        System.out.println("\n----- Сортування завдань -----");

        System.out.println("Виберіть критерій сортування:");
        System.out.println("1. Дедлайн");
        System.out.println("2. Пріоритет");
        System.out.println("3. Статус");
        System.out.println("4. Назва");

        int criteriaChoice = getIntInput("Введіть ваш вибір (1-4): ");
        String criteria;

        switch (criteriaChoice) {
            case 1:
                criteria = "deadline";
                break;
            case 2:
                criteria = "priority";
                break;
            case 3:
                criteria = "status";
                break;
            case 4:
                criteria = "title";
                break;
            default:
                System.out.println("Невірний вибір. Використовується значення за замовчуванням (дедлайн).");
                criteria = "deadline";
                break;
        }

        System.out.println("Виберіть порядок сортування:");
        System.out.println("1. За зростанням");
        System.out.println("2. За спаданням");

        int orderChoice = getIntInput("Введіть ваш вибір (1-2): ");
        boolean ascending = orderChoice != 2;

        List<Task> sortedTasks = taskManager.sortTasks(criteria, ascending);

        if (sortedTasks.isEmpty()) {
            System.out.println("Немає завдань для сортування.");
            return;
        }

        System.out.println("Відсортовані завдання за " + getUkrainianCriteria(criteria) + " (" + (ascending ? "за зростанням" : "за спаданням") + "):");
        for (Task task : sortedTasks) {
            System.out.println(task);
            System.out.println("--------------------");
        }
    }

    /**
     * Gets the Ukrainian translation of the sorting criteria.
     *
     * @param criteria The criteria in English
     * @return The criteria in Ukrainian
     */
    private static String getUkrainianCriteria(String criteria) {
        switch (criteria.toLowerCase()) {
            case "deadline":
                return "дедлайном";
            case "priority":
                return "пріоритетом";
            case "status":
                return "статусом";
            case "title":
                return "назвою";
            default:
                return criteria;
        }
    }

    /**
     * Saves tasks to a JSON file.
     */
    private static void saveTasksToFile() {
        System.out.println("\n----- Збереження завдань у файл -----");

        List<Task> tasks = taskManager.getAllTasks();

        if (tasks.isEmpty()) {
            System.out.println("Немає завдань для збереження.");
            return;
        }

        System.out.print("Введіть шлях до файлу (або натисніть Enter для використання шляху за замовчуванням): ");
        String filePath = scanner.nextLine().trim();

        boolean success;
        if (filePath.isEmpty()) {
            success = jsonTaskManager.saveTasks(tasks);
        } else {
            success = jsonTaskManager.saveTasks(tasks, filePath);
        }

        if (success) {
            System.out.println("Завдання успішно збережено у файл.");
        } else {
            System.out.println("Помилка при збереженні завдань у файл.");
        }
    }

    /**
     * Loads tasks from a JSON file.
     */
    private static void loadTasksFromFile() {
        System.out.println("\n----- Завантаження завдань з файлу -----");

        System.out.print("Введіть шлях до файлу (або натисніть Enter для використання шляху за замовчуванням): ");
        String filePath = scanner.nextLine().trim();

        List<Task> loadedTasks;
        if (filePath.isEmpty()) {
            loadedTasks = jsonTaskManager.loadTasks();
        } else {
            loadedTasks = jsonTaskManager.loadTasks(filePath);
        }

        if (loadedTasks.isEmpty()) {
            System.out.println("Не вдалося завантажити завдання з файлу або файл порожній.");
            return;
        }

        System.out.println("Завантажено " + loadedTasks.size() + " завдань.");
        System.out.print("Бажаєте додати завантажені завдання до поточного списку? (т/н): ");
        String choice = scanner.nextLine().trim();

        if (choice.equalsIgnoreCase("т")) {
            for (Task task : loadedTasks) {
                taskManager.createTask(
                    task.getTitle(),
                    task.getDescription(),
                    task.getDeadline(),
                    task.getPriority()
                );
            }
            System.out.println("Завдання успішно додано до поточного списку.");
        } else {
            System.out.println("Завантаження скасовано.");
        }
    }
}
