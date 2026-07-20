package com.ltfullstack.borrowingservice.command.data;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BorrowingRepository extends JpaRepository<Borrowing,String> {
    List<Borrowing> findByEmployeeId(String employeeId);
    List<Borrowing> findByStatus(String status);
    List<Borrowing> findByBookIdAndStatus(String bookId, String status);
    boolean existsByBookIdAndStatus(String bookId, String status);
    boolean existsByEmployeeIdAndBookIdAndStatus(String employeeId, String bookId, String status);
}
