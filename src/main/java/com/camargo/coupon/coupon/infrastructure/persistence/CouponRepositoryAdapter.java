package com.camargo.coupon.coupon.infrastructure.persistence;

import com.camargo.coupon.coupon.domain.Coupon;
import com.camargo.coupon.coupon.domain.CouponRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class CouponRepositoryAdapter implements CouponRepository {

    private final JpaCouponRepository repository;
    private final CouponPersistenceMapper mapper;

    public CouponRepositoryAdapter(
            JpaCouponRepository repository,
            CouponPersistenceMapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Coupon> findById(UUID id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Coupon save(Coupon coupon) {
        var entity = mapper.toEntity(coupon);
        var saved = repository.save(entity);

        return mapper.toDomain(saved);
    }
}
