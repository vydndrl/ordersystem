package beyond.ordersystem.product.repository;

import beyond.ordersystem.member.domain.Member;
import beyond.ordersystem.product.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findAll (Specification<Product> specification, Pageable pageable);
}
