package com.ltfullstack.bookservice.command.data;

import org.apache.coyote.http11.filters.SavedRequestInputFilter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book,String> {

}
