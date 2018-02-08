package hbrck;

import java.math.BigDecimal;

public class Employee {
    private String name;

    private int age;

    private int departmentPosition;

    private BigDecimal salary;

    public Employee(String name) {
        setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getDepartmentPosition() {
        return departmentPosition;
    }

    public void setDepartmentPosition(int departmentPosition) {
        this.departmentPosition = departmentPosition;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", departmentPosition=" + departmentPosition +
                ", salary=" + salary +
                '}';
    }
}
