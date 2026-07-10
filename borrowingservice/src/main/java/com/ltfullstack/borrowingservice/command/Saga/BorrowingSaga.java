package com.ltfullstack.borrowingservice.command.Saga;

import com.ltfullstack.borrowingservice.command.command.DeleteBorrowingCommand;
import com.ltfullstack.borrowingservice.command.event.BorrowingCreatedEvent;
import com.ltfullstack.commonservice.command.UpdateStatusBookCommand;
import com.ltfullstack.commonservice.event.BookUpadateStatusEvent;
import com.ltfullstack.commonservice.model.BookResponseCommandModel;
import com.ltfullstack.commonservice.queries.GetBookDetailQuery;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Saga
public class BorrowingSaga {
    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private transient QueryGateway queryGateway;

    @StartSaga
    @SagaEventHandler(associationProperty = "bookId")
    private  void Handle(BorrowingCreatedEvent event){
        log.info("BorrowingCreatedEvent in saga for BookId : " + event.getBookId() + "EmployeeId:" +event.getEmployeeId());
        try{

            GetBookDetailQuery getBookDetailQuery = new GetBookDetailQuery(event.getBookId());
            BookResponseCommandModel bookResponseCommandModel = queryGateway.query(getBookDetailQuery, ResponseTypes.instanceOf(BookResponseCommandModel.class)).join();
            if(!bookResponseCommandModel.getIsReady()){
                throw new RuntimeException("Sach da co nguoi muon");
            }
            else{
                UpdateStatusBookCommand  updateStatusBookCommand= new UpdateStatusBookCommand(event.getBookId(),false, event.getEmployeeId(), event.getId());
                commandGateway.sendAndWait(updateStatusBookCommand);
            }

        }catch (Exception ex){
            rollbackBrroowingRecord(event.getId());
            log.error(ex.getMessage());
        }
    }
    @StartSaga
    @SagaEventHandler(associationProperty = "bookId")
    private void Handle(BookUpadateStatusEvent event){
        log.info("BorrowingUpdateStatusEvent in saga for BookId : " + event.getBookId() + "EmployeeId:" +event.getEmployeeId());
        SagaLifecycle.end();

    }

    private void rollbackBrroowingRecord(String id){
        DeleteBorrowingCommand deleteBorrowingCommand = new DeleteBorrowingCommand(id);
        commandGateway.sendAndWait(deleteBorrowingCommand);
        SagaLifecycle.end();
    }

}
