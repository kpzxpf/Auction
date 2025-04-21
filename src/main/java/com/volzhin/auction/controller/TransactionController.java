package com.volzhin.auction.controller;

import com.volzhin.auction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/transaction")
public class TransactionController {
    private final TransactionService transactionService;

    @PutMapping("/{id}/finish")
    public ResponseEntity<?> finishLotManually(@PathVariable Long id) {
        transactionService.finishLotById(id);
        return ResponseEntity.ok().build();
    }
}
