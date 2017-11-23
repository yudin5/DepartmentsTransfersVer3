import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;

public class TransfersTestDrive {
    public static void main(String[] args) {
        // Проверяем аргументы
        //if (args[0] == null || args[1] == null || args.length != 2) {
        //    System.out.println("Введите корректные пути имен файлов");
        //}
        //else {
            //String inputFileName = args[0];
            //String outputFileName = args[1]
            String inputFileName = "C:/Test/data.txt"; // Для целей теста
            String outputFileName = "C:/Test/result.txt"; // Для целей теста

            try {
                // Создаем нашу орг.структуру посредством чтения файла
                ArrayList<Department> departments = readFile(inputFileName);
                // Вычисляем возможные переводы в отделах
                ArrayList<String> dispositions = makeDispositions(departments);
                // Выводим полученные результаты в файл
                writeResultToFile(departments, dispositions, outputFileName);
            } catch (IOException ioEx) {
                System.out.println("Ошибка во время чтения / записи файла.");
            } catch (NumberFormatException nfEx) {
                System.out.println("Ошибка с представлением зарплаты.");
            }
            //}
    }

    static void writeResultToFile(ArrayList<Department> departments, ArrayList<String> dispositions, String outputFileName) throws IOException {
        // Формируем выходной файл
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(outputFileName));
            // Проходимся по списку отделов и выводим среднюю зарплату в каждом
            for (Department dpt : departments) {
                fileWriter.write("Средняя зарплата в департаменте " + dpt.getName() +
                        " равна " + dpt.getAverageSalary());
                fileWriter.newLine();
            }

            if (dispositions.size() == 0) {
                System.out.println("Допустимые перестановки отсутствуют.");
            } else {
                fileWriter.newLine();
                fileWriter.write("Чтобы увеличить средние ЗП, возможны следующие варианты переводов: \r\n");
                for (String line : dispositions) {
                    fileWriter.write(line + "\r\n");
                }
            }

            fileWriter.close();
            System.out.println("Готово. Проверьте файл с результатом");
    }

    static ArrayList<Department> readFile(String inputFileName) throws IOException, NumberFormatException {
        ArrayList<Department> departments = new ArrayList<>();
        // Проходимся по файлу, сплитим по разделителю, заполняем списки отделов и сотрудников
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFileName), "windows-1251"));
            while(fileReader.ready()) {
                String nextLine = fileReader.readLine();
                if (nextLine != null && nextLine.trim().length() != 0) {
                    String[] data = nextLine.split(";");
                    if (data.length != 3) { // Проверка на количество элементов (должно быть 3)
                        System.out.println("Неверный формат данных.");
                        System.out.println("Данные должны быть указаны в формате <<Фамилия; Отдел; Зарплата(число)>>");
                        System.out.println("Ошибка в строке: \'" + nextLine + "\'");
                        throw new IOException();
                    }

                    String nextEmployeeName = data[0].trim(); // Фамилия очередного сотрудника
                    if (nextEmployeeName.equals(null) || nextEmployeeName.trim().length() == 0) {
                        System.out.println("Отсутствует ФИО сотрудника.");
                        System.out.println("Ошибка в строке: \'" + nextLine + "\'");
                        throw new IOException();
                    }

                    String nextEmployeeDpt = data[1].trim(); // Департамент очередного сотрудника
                    if (nextEmployeeDpt.equals(null) || nextEmployeeDpt.trim().length() == 0) {
                        System.out.println("Отсутствует название департамента сотрудника.");
                        System.out.println("Ошибка в строке: \'" + nextLine + "\'");
                        throw new IOException();
                    }

                    BigDecimal nextEmployeeSalary = new BigDecimal(data[2].trim()); // Зарплата очередного сотрудника
                    if (nextEmployeeSalary.compareTo(new BigDecimal(0)) < 0) {
                        System.out.println("Зарплата не может быть отрицательным числом");
                        System.out.println("Ошибка в строке: \'" + nextLine + "\'");
                        throw new NumberFormatException();
                    }

                    // Теперь, когда все данные есть, создаем очередного сотрудника
                    Employee nextEmployee = new Employee(nextEmployeeName, nextEmployeeSalary);

                    // Ищем его отдел. Предварительно создаем новый отдел, полагая, что его еще нет.
                    Department nextDepartment = new Department(nextEmployeeDpt);
                    boolean contains = false; // Полагаем, что его нет в списке
                    for (Department dpt : departments) { // Проходимся по списку, ищем этот отдел
                        if (dpt.getName().equals(nextDepartment.getName())) {
                            contains = true;
                            dpt.addToEmployeeList(nextEmployee); // Отдел найден, просто добавляем туда сотрудника
                        }
                    }

                    // Если после цикла отдел не найден, то добавляем этот новый отдел в список,
                    // заранее добавив в отдел сотрудника.
                    if (!contains) {
                        nextDepartment.addToEmployeeList(nextEmployee);
                        departments.add(nextDepartment);
                    }
                }
            }
        return departments;
    }

    static ArrayList<String> makeDispositions(ArrayList<Department> departments) {
        // Вычисляем перевод одного сотрудника
        // Для этого необходимы 2 условия:
        // 1. Чтобы его ЗП была меньше средней по его отделу
        // 2. Его ЗП должна быть больше средней по другому отделу
        ArrayList<String> dispositions = new ArrayList<>();
        for (Department dpt : departments) {
            BigDecimal averageDptSalary = dpt.getAverageSalary(); // Средняя ЗП отдела
            for (Employee employee : dpt.getListOfEmployees()) { // Пройдемся по сотрудникам текущего отдела
                if (employee.getSalary().compareTo(averageDptSalary) > 0) break; // Если его ЗП больше средней, то останов
                for (Department dptToTransfer : departments) { // Снова перебираем средние ЗП всех отделов
                    if (employee.getSalary().compareTo(dptToTransfer.getAverageSalary()) > 0) {
                        // Оба условия выполнены, добавляем сотрудника в список возможных перестановок
                        dispositions.add("Сотрудник " + employee.getName().toUpperCase() +
                                " из <" + dpt.getName() +
                                "> в ----->  <" + dptToTransfer.getName() + ">");
                    }
                }
            }
        }
        return dispositions;
    }
}
