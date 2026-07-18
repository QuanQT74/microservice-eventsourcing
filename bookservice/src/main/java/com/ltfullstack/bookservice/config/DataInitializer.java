package com.ltfullstack.bookservice.config;

import com.ltfullstack.bookservice.command.command.CreateCommandBook;
import com.ltfullstack.bookservice.command.data.BookRepository;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    @ConditionalOnProperty(name = "app.init-data", havingValue = "true", matchIfMissing = false)
    CommandLineRunner initBooks(CommandGateway commandGateway, BookRepository bookRepository) {
        return args -> {
            List<?> existingBooks = bookRepository.findAll();
            if (!existingBooks.isEmpty()) {
                System.out.println("Books already exist, skipping initialization");
                return;
            }

            commandGateway.sendAndWait(new CreateCommandBook(
                "book-001", "Clean Code", "Robert C. Martin", true,
                "https://images.unsplash.com/photo-1532012197267-da84d127e765?w=400&h=600&fit=crop"));

            commandGateway.sendAndWait(new CreateCommandBook(
                "book-002", "The Pragmatic Programmer", "David Thomas, Andrew Hunt", true,
                "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=400&h=600&fit=crop"));

            commandGateway.sendAndWait(new CreateCommandBook(
                "book-003", "Design Patterns", "Gang of Four", true,
                "https://images.unsplash.com/photo-1497633762265-9d179a990aa6?w=400&h=600&fit=crop"));

            commandGateway.sendAndWait(new CreateCommandBook(
                "book-004", "Refactoring", "Martin Fowler", true,
                "https://images.unsplash.com/photo-1512820790803-83ca734da794?w=400&h=600&fit=crop"));

            commandGateway.sendAndWait(new CreateCommandBook(
                "book-005", "Effective Java", "Joshua Bloch", true,
                "https://images.unsplash.com/photo-1543002588-bfa74002ed7e?w=400&h=600&fit=crop"));

            commandGateway.sendAndWait(new CreateCommandBook(
                "book-006", "Spring in Action", "Craig Walls", true,
                "https://images.unsplash.com/photo-1516979187457-637abb4f9353?w=400&h=600&fit=crop"));

            commandGateway.sendAndWait(new CreateCommandBook(
                "book-007", "Microservices Patterns", "Chris Richardson", true,
                "https://images.unsplash.com/photo-1558494949-ef010cbdcc31?w=400&h=600&fit=crop"));

            commandGateway.sendAndWait(new CreateCommandBook(
                "book-008", "Domain-Driven Design", "Eric Evans", true,
                "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&h=600&fit=crop"));

            System.out.println("Initialized 8 sample books via Axon");
        };
    }
}
