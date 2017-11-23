import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class Department {
    private String name;
    private ArrayList<Employee> listOfEmployees;

    public Department(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Employee> getListOfEmployees() {
        if (listOfEmployees == null) {
            listOfEmployees = new ArrayList<>();
        }
        return listOfEmployees;
    }

    public void addToEmployeeList(Employee employee) {
        getListOfEmployees().add(employee);
    }

    public void showAllEmployees() { // Используется для целей тестирования
        System.out.println("Список сотрудников отдела " + this.getName() + ":");
        listOfEmployees = getListOfEmployees();
        for (Employee employee : listOfEmployees) {
            System.out.print("ФИО: " + employee.getName() + " - ");
            System.out.println("Зарплата: " + employee.getSalary());
        }
        System.out.println();
    }

    public BigDecimal getAverageSalary() {
        BigDecimal sum = new BigDecimal(0);
        listOfEmployees = getListOfEmployees();
        for (Employee employee : listOfEmployees) {
            sum = sum.add(employee.getSalary());
        }
        BigDecimal size = new BigDecimal(listOfEmployees.size());
        return sum.divide(size, 2, RoundingMode.HALF_EVEN);
    }
}
