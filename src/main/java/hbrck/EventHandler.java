package hbrck;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EventHandler {

    private static final String SEPARATOR = ",";

    private Map<String, Employee> employees = new ConcurrentHashMap<>();
    private Set<String> departments = new LinkedHashSet<>();

    public void storeAge(String line) {
        String[] parts = line.split(SEPARATOR);
        String name = parts[0];
        int age = Integer.parseInt(parts[1]);
        Employee employee = getEmployee(name);
        employee.setAge(age);
        System.out.println(String.format("Age [%s] for employee [%s] processed successfully",age, employee.getName()));
    }

    private Employee getEmployee(String name) {
        return employees.computeIfAbsent(name, Employee::new);
    }

    public void triggerDepartment(String line) {
        departments.add(line);
        System.out.println(String.format("Department %s processed successfully", line));
    }

    public void triggerEmployee(String line) {
        //first column contains position of department in alphabetically sorted department list, followed by employee name and salary
        String[] parts = line.split(SEPARATOR);
        if (parts.length<3) {
            System.err.println(String.format("Skipping employee line [%s] due to line format exception", line));
            return;
        }
        int departmentPosition = Integer.parseInt(parts[0]);
        String name = parts[1];
        Employee employee = getEmployee(name);
        try {
            BigDecimal salary = new BigDecimal(parts[2]);
            employee.setSalary(salary);
        } catch (NumberFormatException e) {
            System.err.println(String.format("Skipping employee line [%s] due to salary format error", line));
            return;
        }
        employee.setDepartmentPosition(departmentPosition);
        System.out.println(String.format("Employee %s processed successfully",employee));
    }
}
