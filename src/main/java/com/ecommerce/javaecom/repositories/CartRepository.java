package com.ecommerce.javaecom.repositories;

import com.ecommerce.javaecom.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query("SELECT c from Cart c WHERE c.user.email = ?1")
    Cart findCartByEmail(String email);

    @Query("SELECT c from Cart c WHERE c.user.email = ?1 AND c.id = ?2")
    Cart findCartByEmailAndCartId(String email, Long cartId);

    @Query("SELECT c from Cart c JOIN  FETCH  c.cartItems ci JOIN FETCH ci.product p WHERE p.id =?1")
    List<Cart> findCartsByProductId(Long productId);
}
