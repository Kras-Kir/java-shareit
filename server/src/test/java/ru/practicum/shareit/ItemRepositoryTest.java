package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void findByOwnerIdOrderByIdAsc() {
        // given
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@email.com");
        em.persist(owner);

        Item item1 = new Item();
        item1.setName("Item 1");
        item1.setDescription("Description 1");
        item1.setAvailable(true);
        item1.setOwner(owner);
        em.persist(item1);

        Item item2 = new Item();
        item2.setName("Item 2");
        item2.setDescription("Description 2");
        item2.setAvailable(true);
        item2.setOwner(owner);
        em.persist(item2);

        em.flush();

        // when
        List<Item> foundItems = itemRepository.findByOwnerIdOrderById(owner.getId());

        // then
        assertThat(foundItems).hasSize(2);
        assertThat(foundItems.get(0).getName()).isEqualTo("Item 1");
        assertThat(foundItems.get(1).getName()).isEqualTo("Item 2");
    }

    @Test
    void searchAvailableItems() {
        // given
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@email.com");
        em.persist(owner);

        Item item1 = new Item();
        item1.setName("Дрель");
        item1.setDescription("Аккумуляторная дрель");
        item1.setAvailable(true);
        item1.setOwner(owner);
        em.persist(item1);

        Item item2 = new Item();
        item2.setName("Молоток");
        item2.setDescription("Строительный молоток");
        item2.setAvailable(false); // недоступен
        item2.setOwner(owner);
        em.persist(item2);

        em.flush();

        // when
        List<Item> foundItems = itemRepository.searchAvailableItems("дрель");

        // then
        assertThat(foundItems).hasSize(1);
        assertThat(foundItems.get(0).getName()).isEqualTo("Дрель");
    }
}
