package com.library.repository;

import com.library.model.HoldRequest;
import java.util.List;
import java.util.Optional;

public interface HoldRequestRepository {
    List<HoldRequest> findAll();
    Optional<HoldRequest> findById(String id);
    List<HoldRequest> findByBookIsbn(String bookIsbn);
    List<HoldRequest> findByUserId(String userId);
    void save(HoldRequest holdRequest);
    void update(HoldRequest holdRequest);
    void deleteById(String id);
}
