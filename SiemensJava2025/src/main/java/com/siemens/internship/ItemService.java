package com.siemens.internship;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

@Service
public class ItemService {
    @Autowired
    private ItemRepository itemRepository;
    private static ExecutorService executor = Executors.newFixedThreadPool(10);
    private List<Item> processedItems = new ArrayList<>();


    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    public Optional<Item> findById(Long id) {
        return itemRepository.findById(id);
    }

    public Item save(Item item) {
        return itemRepository.save(item);
    }

    public void deleteById(Long id) {
        itemRepository.deleteById(id);
    }


    /**
     * Your Tasks
     * Identify all concurrency and asynchronous programming issues in the code
     * Fix the implementation to ensure:
     * All items are properly processed before the CompletableFuture completes
     * Thread safety for all shared state
     * Proper error handling and propagation
     * Efficient use of system resources
     * Correct use of Spring's @Async annotation
     * Add appropriate comments explaining your changes and why they fix the issues
     * Write a brief explanation of what was wrong with the original implementation
     *
     * Hints
     * Consider how CompletableFuture composition can help coordinate multiple async operations
     * Think about appropriate thread-safe collections
     * Examine how errors are handled and propagated
     * Consider the interaction between Spring's @Async and CompletableFuture
     */
    @Async
    public CompletableFuture<List<Item>> processItemsAsync() {
        // modified the method to return CompletableFuture to make it  run asynchronously
        List<Long> itemIds = itemRepository.findAllIds();

        List<CompletableFuture<Item>> futures = new ArrayList<>();
        // for every id, we create an asynchronous task to process each item
        for (Long id : itemIds) {
            CompletableFuture<Item> future = CompletableFuture.supplyAsync(() -> {
                try {
                    Thread.sleep(100);

                    Optional<Item> item = itemRepository.findById(id);
                    if(item.isPresent()) {
                        Item item1 = item.get();
                        item1.setStatus("PROCESSED");
                        return itemRepository.save(item1);
                        // process the item, if we did not found it return null
                    }
                    return null;
                } catch (InterruptedException e) {
                    // propagate the  exception
                    throw new RuntimeException(e);
                }
            }, executor);
            futures.add(future);//collect all futures
        }

        //wait for all the tasks to complete
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> {
                    for (CompletableFuture<Item> future : futures) {
                        try {
                            Item item = future.get();
                            processedItems.add(item);
                            // add item to processed list
                        }
                        catch (InterruptedException | ExecutionException e) {
                            throw new RuntimeException(e);
                            // if an item fails propagate the error
                        }
                    }
                    return processedItems;
                });
    }

}
// modified the processItemsAsync, now it is thread safe because of the supplyAsync,
// each item is processed in a separate thread, avoids race conditions all futures are collected and awaited for
// only after that we process the result(CompletableFuture.allOf)
// exceptions are propagated to caller