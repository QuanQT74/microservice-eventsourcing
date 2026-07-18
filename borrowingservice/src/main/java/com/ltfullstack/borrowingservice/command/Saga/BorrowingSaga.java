package com.ltfullstack.borrowingservice.command.Saga;

import com.ltfullstack.borrowingservice.command.command.DeleteBorrowingCommand;
import com.ltfullstack.borrowingservice.command.event.BorrowingCreatedEvent;
import com.ltfullstack.borrowingservice.command.event.BorrowingDeleteEvent;
import com.ltfullstack.borrowingservice.command.event.BorrowingReturnedEvent;
import com.ltfullstack.commonservice.command.BookRollBackStatusCommand;
import com.ltfullstack.commonservice.command.UpdateStatusBookCommand;
import com.ltfullstack.commonservice.event.BookUpadateStatusEvent;
import com.ltfullstack.commonservice.model.BookResponseCommandModel;
import com.ltfullstack.commonservice.model.EmployeeResponseCommandModel;
import com.ltfullstack.commonservice.queries.GetBookDetailQuery;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;
import com.ltfullstack.commonservice.queries.GetDetailEmployeeQuery;

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
                SagaLifecycle.associateWith("bookId", event.getBookId());
                UpdateStatusBookCommand  updateStatusBookCommand= new UpdateStatusBookCommand(event.getBookId(),false, event.getEmployeeId(), event.getId());
                commandGateway.sendAndWait(updateStatusBookCommand);
            }

        }catch (Exception ex){
            rollbackBrroowingRecord(event.getId());
            log.error(ex.getMessage());
        }
    }
    @SagaEventHandler(associationProperty = "bookId")
    private void Handle(BookUpadateStatusEvent event){
        log.info("BorrowingUpdateStatusEvent in saga for BookId : " + event.getBookId() + "EmployeeId:" +event.getEmployeeId());

        try{
            GetDetailEmployeeQuery getDetailEmployeeQuery = new GetDetailEmployeeQuery(event.getEmployeeId());
            EmployeeResponseCommandModel responseCommandModel = queryGateway.query(getDetailEmployeeQuery,ResponseTypes.instanceOf(EmployeeResponseCommandModel.class)).join();
            if(responseCommandModel.getIsDisciplined()){
                throw new Exception("Nhân Viên Kĩ Luật ");
            }
            else {
                log.info("Da Muon Sach Thanh Cong - KHONG end saga, cho doi return");
                // KHONG end saga - tiep tuc de xu ly return
            }
        }catch (Exception ex){
            roolbackBookStatus(event.getBookId(),event.getEmployeeId(),event.getBorrwingId());
            log.error(ex.getMessage());
        }

    }

    private void rollbackBrroowingRecord(String id){
        DeleteBorrowingCommand deleteBorrowingCommand = new DeleteBorrowingCommand(id);
        commandGateway.sendAndWait(deleteBorrowingCommand);
    }

    private void roolbackBookStatus(String bookId,String employeeId , String borrowingId){
        SagaLifecycle.associateWith("bookId",bookId);
        BookRollBackStatusCommand bookRollBackStatusCommand = new BookRollBackStatusCommand(bookId,true,employeeId,borrowingId);
        commandGateway.sendAndWait(bookRollBackStatusCommand);
    }

    // BookUpadateStatusEvent handled by Handle() above

    @SagaEventHandler(associationProperty = "bookId")
    private void handle(BorrowingReturnedEvent event) {
        log.info("Return book - updating book status to AVAILABLE for BookId: " + event.getBookId());

        UpdateStatusBookCommand command = new UpdateStatusBookCommand(
            event.getBookId(),
            true,        // true = AVAILABLE
            null,        // không cần employeeId khi trả
            event.getId()
        );
        commandGateway.send(command);
        SagaLifecycle.end();
    }

    @SagaEventHandler(associationProperty = "id")
    @EndSaga
    private void handle(BorrowingDeleteEvent event){
        log.info("BookRollBackStatusCommand in saga for BookId" + event.getId());
        SagaLifecycle.end();
    }

}
