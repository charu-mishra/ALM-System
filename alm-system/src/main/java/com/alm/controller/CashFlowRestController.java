package com.alm.controller;

import java.sql.SQLException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alm.model.CashFlow;

@RestController
@RequestMapping("/api/cash-flows")
public class CashFlowRestController {
    private final CashFlowController cashFlowDao;

    public CashFlowRestController(CashFlowController cashFlowDao) {
        this.cashFlowDao = cashFlowDao;
    }

    @GetMapping
    public List<CashFlow> findAll(@RequestParam(required = false) Integer assetId,
                                  @RequestParam(required = false) Integer liabilityId) throws SQLException {
        if (assetId != null) {
            return cashFlowDao.findByAssetId(assetId);
        }
        if (liabilityId != null) {
            return cashFlowDao.findByLiabilityId(liabilityId);
        }
        return cashFlowDao.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CashFlow> findById(@PathVariable int id) throws SQLException {
        return cashFlowDao.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CashFlow> create(@RequestBody CashFlow cashFlow) throws SQLException {
        System.out.println("Creating CashFlow: " + cashFlow);
        cashFlowDao.create(cashFlow);
        return ResponseEntity.status(201).body(cashFlow);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CashFlow> update(@PathVariable int id, @RequestBody CashFlow cashFlow) throws SQLException {
        cashFlow.setFlowId(id);
        return cashFlowDao.update(cashFlow)
                ? ResponseEntity.ok(cashFlow)
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) throws SQLException {
        return cashFlowDao.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
