package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwner_Id(Long ownerId);

    List<Item> findAllByItemRequest_Id(Long requestId);

    List<Item> findAllByItemRequest_IdIn(List<Long> requestIds);

    @Query(" select i from Item i " +
            " where lower(i.name) like lower(concat('%', :text, '%'))\n" +
            " or lower(i.description) like lower(concat('%', :text, '%'))")

    List<Item> search(String text);
}