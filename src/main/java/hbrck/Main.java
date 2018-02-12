package hbrck;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class Main {
    public static void main(String[] args) {
        if (args.length<2) {
            System.err.println("give me the input and output folders first!!!");
            return;
        }
        String path = args[0];
        String output = args[1];
        EventHandler eventHandler = new EventHandler();
        ExecutorService threadPool = Executors.newFixedThreadPool(4);

        CompletableFuture<Void> futures = CompletableFuture.allOf(
                CompletableFuture.runAsync(() -> {
                    Main.process(path, "ages.csv", eventHandler::storeAge);
                }, threadPool),
                CompletableFuture.runAsync(() -> {
                    Main.process(path, "departments.csv", eventHandler::triggerDepartment);
                }, threadPool),
                CompletableFuture.runAsync(() -> {
                    Main.process(path, "employees.csv", eventHandler::triggerEmployee);
                }, threadPool)
        );

        futures.join();

        OutputService outputService = new OutputService();

        futures = CompletableFuture.allOf(
                CompletableFuture.runAsync(() -> {
                    try {
                        outputService.writeIncomeByDept(eventHandler.getMedianIncomeByDepartment(), output);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }, threadPool),
                CompletableFuture.runAsync(() -> {
                    try {
                        outputService.writeIcome95ByDept(eventHandler.get95PercentileByDepartment(), output);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }, threadPool),
                CompletableFuture.runAsync(() -> {
                    try {
                        outputService.writeIncomeAvgByAgeRange(eventHandler.getAvgIncomeByAgeRange(), output);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }, threadPool),
                CompletableFuture.runAsync(() -> {
                    try {
                        outputService.writeEmployeeAgeByDept(eventHandler.getEmployeeAgeByDept(), output);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }, threadPool)
        );

        futures.join();
        System.out.println("Output files written");

        threadPool.shutdown();

    }

    private static void process(String path,String file, Consumer<String> consumer) {
        try {
            Files.lines(Paths.get(String.format("%s/%s", path,file)))
                    .forEach(consumer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
