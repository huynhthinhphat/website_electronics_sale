package com.tip.b18.electronicsales.repositories;

import com.tip.b18.electronicsales.entities.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {
    Cart findByAccountId(UUID accountId);
}
