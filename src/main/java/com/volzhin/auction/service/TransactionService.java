package com.volzhin.auction.service;

import com.volzhin.auction.dto.event.DeleteLotEvent;
import com.volzhin.auction.entity.Transaction;
import com.volzhin.auction.entity.User;
import com.volzhin.auction.entity.bid.Bid;
import com.volzhin.auction.entity.lot.Lot;
import com.volzhin.auction.producer.DeleteLotProducer;
import com.volzhin.auction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final LotService lotService;
    private final UserService userService;
    private final BidService bidService;
    private final TransactionRepository transactionRepository;
    private final DeleteLotProducer deleteLotProducer;

    @Transactional
    public void finishLotById(long id) {
        Lot lot = lotService.findById(id);
        Optional<Bid> winningBidOpt = bidService.getWinningBidForLot(id);

        if (winningBidOpt.isEmpty()) {
            lot.setStatus(Lot.Status.closed);
            lotService.save(lot);
            return;
        }

        Bid winningBid = winningBidOpt.get();
        User buyer = winningBid.getUser();
        User seller = lot.getSeller();
        BigDecimal finalPrice = winningBid.getAmount();

        userService.decreaseBalance(buyer.getId(), finalPrice);
        transactionRepository.save(Transaction.builder()
                .user(buyer)
                .amount(finalPrice)
                .lot(lot)
                .type(Transaction.Type.payment)
                .build());

        userService.increaseBalance(seller.getId(), finalPrice);
        transactionRepository.save(Transaction.builder()
                .user(seller)
                .amount(finalPrice)
                .lot(lot)
                .type(Transaction.Type.refund)
                .build());

        lot.setStatus(Lot.Status.sold);
        deleteLotProducer.send(new DeleteLotEvent(lot.getId()));
        lotService.save(lot);
    }
}
