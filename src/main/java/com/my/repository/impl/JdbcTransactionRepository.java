package com.my.repository.impl;

import com.my.annotation.Loggable;
import com.my.dto.ExpenseAnalysisDto;
import com.my.model.Transaction;
import com.my.model.TransactionFilter;
import com.my.model.TransactionType;
import com.my.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Loggable
@Repository
public class JdbcTransactionRepository implements TransactionRepository {
    private final JdbcTemplate jdbcTemplate;
    @Value("${datasource.schema}")
    private String schema;

    public JdbcTransactionRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Transaction> getAll() {
        String query = "SELECT t.id, t.amount, t.description, t.date, t.type, tc.id AS category_id, u.id AS user_id " +
                       "FROM " + schema + ".transaction t " +
                       "JOIN " + schema + ".transaction_category tc ON t.category_id = tc.id " +
                       "JOIN " + schema + ".user u ON t.user_id = u.id";
        return jdbcTemplate.query(query, (rs, rowNum) ->
                new Transaction(
                        rs.getLong("id"),
                        rs.getDate("date").toLocalDate(),
                        TransactionType.valueOf(rs.getString("type")),
                        rs.getBigDecimal("amount"),
                        rs.getString("description"),
                        rs.getLong("category_id"),
                        rs.getLong("user_id")
                ));
    }

    @Override
    public List<Transaction> getAll(TransactionFilter filter) {
        List<Transaction> transactions = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT t.id, t.amount, t.description, t.date, t.type, tc.id AS category_id, u.id AS user_id " +
                                                "FROM " + schema + ".transaction t " +
                                                "JOIN " + schema + ".transaction_category tc ON t.category_id = tc.id " +
                                                "JOIN " + schema + ".user u ON t.user_id = u.id");

        List<Object> parameters = new ArrayList<>();

        if (filter != null) {
            if (filter.userId() != null) {
                query.append(" AND t.user_id = ?");
                parameters.add(filter.userId());
            }
            if (filter.categoryId() != null) {
                query.append(" AND t.category_id = ?");
                parameters.add(filter.categoryId());
            }
            if (filter.date() != null) {
                query.append(" AND t.date = ?");
                parameters.add(Date.valueOf(filter.date()));
            }
            if (filter.from() != null) {
                query.append(" AND t.date >= ?");
                parameters.add(Date.valueOf(filter.from()));
            }
            if (filter.to() != null) {
                query.append(" AND t.date <= ?");
                parameters.add(Date.valueOf(filter.to()));
            }
            if (filter.type() != null) {
                query.append(" AND t.type = ?");
                parameters.add(filter.type().name());
            }
        }

        return jdbcTemplate.query(query.toString(), parameters.toArray(), (rs, rowNum) ->
                new Transaction(
                        rs.getLong("id"),
                        rs.getDate("date").toLocalDate(),
                        TransactionType.valueOf(rs.getString("type")),
                        rs.getBigDecimal("amount"),
                        rs.getString("description"),
                        rs.getLong("category_id"),
                        rs.getLong("user_id")
                ));
    }

    @Override
    public Optional<Transaction> getById(Long id) {
        String query = "SELECT t.id, t.amount, t.description, t.date, t.type, tc.id AS category_id, u.id AS user_id " +
                       "FROM " + schema + ".transaction t " +
                       "JOIN " + schema + ".transaction_category tc ON t.category_id = tc.id " +
                       "JOIN " + schema + ".user u ON t.user_id = u.id " +
                       "WHERE t.id = ?";
        return Optional.ofNullable(jdbcTemplate.queryForObject(query, new Object[]{id}, (rs, rowNum) ->
                new Transaction(
                        rs.getLong("id"),
                        rs.getDate("date").toLocalDate(),
                        TransactionType.valueOf(rs.getString("type")),
                        rs.getBigDecimal("amount"),
                        rs.getString("description"),
                        rs.getLong("category_id"),
                        rs.getLong("user_id")
                )));
    }

    @Override
    public Transaction save(Transaction entity) {
        String query = "INSERT INTO " + schema + ".transaction (amount, description, date, type, category_id, user_id) VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int update = jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setBigDecimal(1, entity.getAmount());
            ps.setString(2, entity.getDescription());
            ps.setDate(3, Date.valueOf(entity.getDate()));
            ps.setString(4, entity.getType().name());
            ps.setLong(5, entity.getCategoryId());
            ps.setLong(6, entity.getUserId());
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
    public Transaction update(Transaction entity) {
        String query = "UPDATE " + schema + ".transaction SET amount = ?, description = ?, category_id = ? WHERE id = ?";
        int update = jdbcTemplate.update(query,
                entity.getAmount(),
                entity.getDescription(),
                entity.getCategoryId(),
                entity.getId());
        if (update > 0) {
            return entity;
        }
        return null;
    }

    @Override
    public boolean deleteById(Long id) {
        String query = "DELETE FROM " + schema + ".transaction WHERE id = ?";
        int update = jdbcTemplate.update(query, id);
        return update > 0;
    }

    @Override
    public BigDecimal getMonthExpense(Long userId) {
        BigDecimal totalExpense = BigDecimal.ZERO;
        LocalDate currentDate = LocalDate.now();
        int currentMonth = currentDate.getMonthValue();
        int currentYear = currentDate.getYear();

        String query = "SELECT SUM(amount) AS total_expense " +
                       "FROM " + schema + ".transaction t " +
                       "WHERE t.user_id = ? AND t.type = ? " +
                       "AND EXTRACT(MONTH FROM t.date) = ? " +
                       "AND EXTRACT(YEAR FROM t.date) = ?";

        return jdbcTemplate.queryForObject(query, new Object[]{userId, TransactionType.EXPENSE.name(), currentMonth, currentYear}, BigDecimal.class);
    }

    @Override
    public BigDecimal getBalance(Long userId) {
        BigDecimal balance = BigDecimal.ZERO;

        String query = "SELECT SUM(CASE WHEN t.type = ? THEN t.amount ELSE -t.amount END) AS balance " +
                       "FROM " + schema + ".transaction t " +
                       "WHERE t.user_id = ?";

        return jdbcTemplate.queryForObject(query, new Object[]{TransactionType.INCOME.name(), userId}, BigDecimal.class);
    }

    @Override
    public BigDecimal getTotalIncome(Long userId, LocalDate from, LocalDate to) {
        BigDecimal totalIncome = BigDecimal.ZERO;

        String query = "SELECT SUM(amount) AS total_income " +
                       "FROM " + schema + ".transaction t " +
                       "WHERE t.user_id = ? AND t.type = ? " +
                       "AND t.date >= ? AND t.date <= ?";

        return jdbcTemplate.queryForObject(query, new Object[]{userId, TransactionType.INCOME.name(), Date.valueOf(from), Date.valueOf(to)}, BigDecimal.class);
    }

    @Override
    public BigDecimal getTotalExpenses(Long userId, LocalDate from, LocalDate to) {
        BigDecimal totalExpenses = BigDecimal.ZERO;

        String query = "SELECT SUM(amount) AS total_expenses " +
                       "FROM " + schema + ".transaction t " +
                       "WHERE t.user_id = ? AND t.type = ? " +
                       "AND t.date >= ? AND t.date <= ?";

        return jdbcTemplate.queryForObject(query, new Object[]{userId, TransactionType.EXPENSE.name(), Date.valueOf(from), Date.valueOf(to)}, BigDecimal.class);
    }

    @Override
    public List<ExpenseAnalysisDto> analyzeExpensesByCategory(Long userId, LocalDate from, LocalDate to) {
        List<ExpenseAnalysisDto> result = new ArrayList<>();

        String query = "SELECT tc.category_name, SUM(t.amount) AS total_expenses " +
                       "FROM " + schema + ".transaction t " +
                       "JOIN " + schema + ".transaction_category tc ON t.category_id = tc.id " +
                       "WHERE t.user_id = ? AND t.type = ? " +
                       "AND t.date >= ? AND t.date <= ? " +
                       "GROUP BY tc.category_name";

        return jdbcTemplate.query(query, new Object[]{userId, TransactionType.EXPENSE.name(), Date.valueOf(from), Date.valueOf(to)},
                (rs, rowNum) -> new ExpenseAnalysisDto(
                        rs.getString("category_name"),
                        rs.getBigDecimal("total_expenses")
                ));
    }

    @Override
    public BigDecimal getGoalExceeded(Long userId, Long transactionCategoryId) {
        BigDecimal totalIncome = BigDecimal.ZERO;

        String query = "SELECT SUM(amount) AS total_income " +
                       "FROM " + schema + ".transaction t " +
                       "WHERE t.user_id = ? AND t.category_id = ? AND t.type = ?";

        return jdbcTemplate.queryForObject(query, new Object[]{userId, transactionCategoryId, TransactionType.INCOME.name()}, BigDecimal.class);
    }
}
