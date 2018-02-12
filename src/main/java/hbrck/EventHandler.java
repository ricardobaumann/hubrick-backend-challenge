package hbrck;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EventHandler {

    private static final String SEPARATOR = ",";
    private static final int AVG_FACTOR = 10;

    private static final Function<Map.Entry<Integer, List<Employee>>, OptionalDouble> AVG = entry -> entry.getValue()
            .stream().mapToDouble(Employee::getAge).average();

    private Map<String, Employee> employees = new ConcurrentHashMap<>();
    private List<String> departments = new ArrayList<>();
    private Map<String, List<Employee>> employeesByDept;

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

    public Map<String, BigDecimal> getMedianIncomeByDepartment() {
        Map<String, List<Employee>> employeesByDept = getEmployeesByDept();

        return employeesByDept.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> median(entry.getValue().stream().map(Employee::getSalary).filter(Objects::nonNull).collect(Collectors.toList()))));

    }

    public Map<String, BigDecimal> get95PercentileByDepartment() {
        Map<String, List<Employee>> employeesByDept = getEmployeesByDept();

        return employeesByDept.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> nineFivePerc(entry.getValue().stream().map(Employee::getSalary).filter(Objects::nonNull).collect(Collectors.toList()))));

    }

    public Map<Integer, OptionalDouble> getAvgIncomeByAgeRange() {

        Map<Integer, List<Employee>> byAgeRange = employees.values().stream()
                .collect(Collectors.groupingBy(employee -> employee.getAge() / AVG_FACTOR));

        return byAgeRange.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, AVG));

    }

    public Map<String, BigDecimal> getEmployeeAgeByDept() {

        return getEmployeesByDept().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> median(entry.getValue()
                        .stream().map(Employee::getAge).map(BigDecimal::new).collect(Collectors.toList()))));
    }

    private synchronized Map<String,List<Employee>> getEmployeesByDept() {
        if (employeesByDept==null) {
            Collections.sort(departments);
            departments = departments.stream().distinct().collect(Collectors.toList());
            final int deptSize = departments.size();
            employeesByDept = employees.values().stream().collect(Collectors.groupingBy(employee -> {
                if (employee.getDepartmentPosition()>deptSize-1) {
                    return "Unknown";
                }
                return departments.get(employee.getDepartmentPosition());
            }));
        }
        return employeesByDept;
    }
    private static BigDecimal nineFivePerc(List<BigDecimal> values) {

        if (values ==null || values.isEmpty()) {
            return BigDecimal.ZERO;
        }
        Collections.sort(values);
        double nineFiveSum = 0.95 * values.stream().mapToDouble(BigDecimal::doubleValue).sum();

        double sumSoFar = 0;
        for (BigDecimal value: values) {
            sumSoFar+= value.doubleValue();
            if (sumSoFar >= nineFiveSum) {
                return value;
            }
        }

        return values.get(values.size()-1);
    }

    private static BigDecimal median(List<BigDecimal> values)
    {
        if (values ==null || values.isEmpty()) {
            return BigDecimal.ZERO;
        }
        Collections.sort(values);
        int size = values.size();
        int middle = size/ 2;
        if (size % 2 == 0)
        {
            BigDecimal left = values.get(middle - 1);
            BigDecimal right = values.get(middle);
            return (left.add(right)).divide(new BigDecimal(2), RoundingMode.UP);
        }
        else
        {
            return values.get(middle);
        }
    }
}
