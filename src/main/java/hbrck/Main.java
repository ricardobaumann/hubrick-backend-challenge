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
        String path = "/Users/ricardobaumann/projects/hubrick-backend-challenge/data";
        EventHandler eventHandler = new EventHandler();
        ExecutorService threadPool = Executors.newFixedThreadPool(3);


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
