package com.siemens.internship;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@SpringBootTest
class InternshipApplicationTests {

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private ItemService itemService;

	@Test
	void test_add(){
		Item item = new Item(null, "name", "description", "status", "example@email.com");
		itemRepository.save(item);
		assert itemRepository.count() == 1;
		assert itemRepository.findById(item.getId()).get().getStatus() == "status";
	}

	@Test
	void test_delete(){
		Item item = new Item(null, "name", "description", "status", "example@email.com");
		Item item1 = new Item(null, "name1", "description1", "status1", "example1@email.com");

		itemRepository.save(item);
		itemRepository.save(item1);
		assert itemRepository.count() == 3;

		itemRepository.deleteById(item.getId());
		assert itemRepository.count() == 2;
		assert itemRepository.findById(item.getId()).equals(Optional.empty()) ;
	}

	@Test
	void test_processItemAsync() throws ExecutionException, InterruptedException {
		Item item = new Item(null, "name", "description", "status", "example@email.com");
		Item item1 = new Item(null, "name1", "description1", "status1", "example1@email.com");

		itemRepository.save(item);
		itemRepository.save(item1);

		CompletableFuture<List<Item>> future = itemService.processItemsAsync();

		List<Item> itemList = future.get();

		assert itemList != null;

		for(long id : itemRepository.findAllIds()){
			assert itemRepository.findById(id).get().getStatus() == "PROCESSED";
		}

	}

}
