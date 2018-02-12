package hbrck;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

public class OutputService {
    public void writeIncomeByDept(Map<String, BigDecimal> medianIncomeByDepartment, String path) throws IOException {
        String content = mapToCSV(medianIncomeByDepartment);
        Files.write(Paths.get(path,"income-by-department.csv"),content.getBytes());
    }

    private String mapToCSV(Map<?, ?> medianIncomeByDepartment) {
        return medianIncomeByDepartment.entrySet().stream().map(entry -> entry.getKey().toString().concat(",")
                .concat(entry.getValue().toString())).collect(Collectors.joining("\r\n"));
    }

    public void writeIcome95ByDept(Map<String, BigDecimal> percentileByDepartment, String path) throws IOException {
        String content = mapToCSV(percentileByDepartment);
        Files.write(Paths.get(path,"income-95-by-department.csv"),content.getBytes());
    }

    public void writeIncomeAvgByAgeRange(Map<Integer, OptionalDouble> avgIncomeByAgeRange, String path) throws IOException {
        String content = mapToCSV(avgIncomeByAgeRange);
        Files.write(Paths.get(path,"income-average-by-age-range.csv"),content.getBytes());
    }

    public void writeEmployeeAgeByDept(Map<String, BigDecimal> employeeAgeByDept, String path) throws IOException {
        String content = mapToCSV(employeeAgeByDept);
        Files.write(Paths.get(path,"employee-age-by-department.csv"),content.getBytes());
    }
}
