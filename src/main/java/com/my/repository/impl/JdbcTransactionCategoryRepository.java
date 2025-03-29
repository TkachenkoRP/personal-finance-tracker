package com.my.repository.impl;

import com.my.annotation.Loggable;
import com.my.model.TransactionCategory;
import com.my.repository.TransactionCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Loggable
@Repository
public class JdbcTransactionCategoryRepository implements TransactionCategoryRepository {
    private final JdbcTemplate jdbcTemplate;
    @Value("${datasource.schema}")
    private String schema;

    @Autowired
    public JdbcTransactionCategoryRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public boolean existsByCategoryNameIgnoreCase(String categoryName) {
        String query = "SELECT COUNT(*) FROM " + schema + ".transaction_category WHERE LOWER(category_name) = LOWER(?)";
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, categoryName);
        return count != null && count > 0;
    }

    @Override
    public List<TransactionCategory> getAll() {
        String query = "SELECT id, category_name FROM " + schema + ".transaction_category";
        return jdbcTemplate.query(query, (rs, rowNum) -> mapCategory(rs));
    }

    @Override
    public Optional<TransactionCategory> getById(Long id) {
        String query = "SELECT id, category_name FROM " + schema + ".transaction_category WHERE id = ?";
        try {
            TransactionCategory transactionCategory = jdbcTemplate.queryForObject(query, (rs, rowNum) -> mapCategory(rs), id);
            return Optional.ofNullable(transactionCategory);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    private TransactionCategory mapCategory(ResultSet rs) throws SQLException {
        return new TransactionCategory(
                rs.getLong("id"),
                rs.getString("category_name")
        );
    }

    @Override
    public TransactionCategory save(TransactionCategory entity) {
        String query = "INSERT INTO " + schema + ".transaction_category (category_name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int update = jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, entity.getCategoryName());
            return ps;
        }, keyHolder);
        if (update > 0) {
            List<Map<String, Object>> keys = keyHolder.getKeyList();
            if (!keys.isEmpty()) {
                Map<String, Object> generatedKey = keys.get(0);
                Number generatedId = (Number) generatedKey.get("id");
                if (generatedId != null) {
                    long id = generatedId.longValue();
                    entity.setId(id);
                    return entity;
                }
            }
        }
        return null;
    }

    @Override
    public TransactionCategory update(TransactionCategory entity) {
        String query = "UPDATE " + schema + ".transaction_category SET category_name = ? WHERE id = ?";
        int update = jdbcTemplate.update(query, entity.getCategoryName(), entity.getId());
        if (update > 0) {
            return entity;
        }
        return null;
    }

    @Override
    public boolean deleteById(Long id) {
        String query = "DELETE FROM " + schema + ".transaction_category WHERE id = ?";
        int update = jdbcTemplate.update(query, id);
        return update > 0;
    }
}
