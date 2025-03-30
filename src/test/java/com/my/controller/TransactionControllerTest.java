package com.my.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.my.AbstractTestContainer;
import com.my.dto.BalanceResponseDto;
import com.my.dto.ErrorResponseDto;
import com.my.dto.ExpenseAnalysisDto;
import com.my.dto.ExpensesResponseDto;
import com.my.dto.FullReportResponseDto;
import com.my.dto.IncomeResponseDto;
import com.my.dto.TransactionRequestDto;
import com.my.dto.TransactionResponseDto;
import com.my.model.TransactionType;
import com.my.repository.impl.JdbcTransactionRepository;
import com.my.service.UserManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class TransactionControllerTest extends AbstractTestContainer {
    private final JdbcTransactionRepository transactionRepository;
    private final TransactionController transactionController;
    private final ExceptionHandlerController exceptionHandlerController;

    @Autowired
    public TransactionControllerTest(JdbcTransactionRepository transactionRepository, TransactionController transactionController, ExceptionHandlerController exceptionHandlerController) {
        this.transactionRepository = transactionRepository;
        this.transactionController = transactionController;
        this.exceptionHandlerController = exceptionHandlerController;
    }

    @BeforeEach
    void beforeEach() {
        setUpMockMvc(transactionController, exceptionHandlerController);
    }

    @Test
    void whenGetAllTransactions_thenReturnAllTransactions() throws Exception {
        int count = transactionRepository.getAll().size();
        var actualResponse = performRequest(HttpMethod.GET, API_URL_TRANSACTION, HttpStatus.OK);
        List<TransactionResponseDto> response = fromResponse(actualResponse, new TypeReference<>() {
        });

        assertThat(response).hasSize(count);
    }

    @Test
    void whenGetAllTransactions_withFilterUserId_thenReturnFilteredTransactions() throws Exception {
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("userId", "2");

        var actualResponse = performRequest(HttpMethod.GET, API_URL_TRANSACTION, HttpStatus.OK, pathVariables);
        List<TransactionResponseDto> response = fromResponse(actualResponse, new TypeReference<>() {
        });

        assertThat(response).isNotEmpty().allMatch(t -> t.getUser().getId() == 2);
    }

    @Test
    void whenGetAllTransactions_withCategoryId_thenReturnFilteredTransactions() throws Exception {
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("categoryId", "1");

        var actualResponse = performRequest(HttpMethod.GET, API_URL_TRANSACTION, HttpStatus.OK, pathVariables);
        List<TransactionResponseDto> response = fromResponse(actualResponse, new TypeReference<>() {
        });

        assertThat(response).isNotEmpty().allMatch(t -> t.getCategory().getId() == 1);
    }

    @Test
    void whenGetAllTransactions_withType_thenReturnFilteredTransactions() throws Exception {
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("type", TransactionType.EXPENSE.name());

        var actualResponse = performRequest(HttpMethod.GET, API_URL_TRANSACTION, HttpStatus.OK, pathVariables);
        List<TransactionResponseDto> response = fromResponse(actualResponse, new TypeReference<>() {
        });

        assertThat(response).isNotEmpty().allMatch(t -> t.getType() == TransactionType.EXPENSE);
    }

    @Test
    void whenGetAllTransactions_withDatePeriod_thenReturnFilteredTransactions() throws Exception {
        LocalDate from = LocalDate.of(2025, 1, 9);
        LocalDate to = LocalDate.of(2025, 1, 27);

        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("from", String.valueOf(from));
        pathVariables.put("to", String.valueOf(to));

        var actualResponse = performRequest(HttpMethod.GET, API_URL_TRANSACTION, HttpStatus.OK, pathVariables);
        List<TransactionResponseDto> response = fromResponse(actualResponse, new TypeReference<>() {
        });

        assertThat(response).isNotEmpty().allMatch(t -> {
            LocalDate date = LocalDate.parse(t.getDate(), DateTimeFormatter.ofPattern( "d.M.yyyy" ));
            return !date.isBefore(from) && !date.isAfter(to);
        });
    }

    @Test
    void whenGetTransactionById_thenReturnTransaction() throws Exception {
        var actualResponse = performRequest(HttpMethod.GET, API_URL_TRANSACTION + "/" + TRANSACTION_ID, HttpStatus.OK);
        TransactionResponseDto response = fromResponse(actualResponse, TransactionResponseDto.class);
        assertAll(
                () -> assertThat(response.getId()).isEqualTo(TRANSACTION_ID),
                () -> assertThat(response.getType()).isEqualTo(TRANSACTION_TYPE),
                () -> assertThat(response.getAmount()).isEqualTo(TRANSACTION_AMOUNT),
                () -> assertThat(response.getDescription()).isEqualTo(TRANSACTION_DESCRIPTION),
                () -> assertThat(response.getCategory().getId()).isEqualTo(TRANSACTION_CATEGORY_ID),
                () -> assertThat(response.getUser().getId()).isEqualTo(TRANSACTION_USER_ID)
        );
    }

    @Test
    void whenGetTransactionById_withWrongId_thenReturnNotFound() throws Exception {
        performRequest(HttpMethod.GET, API_URL_TRANSACTION + "/" + WRONG_ID, HttpStatus.NOT_FOUND);
    }

    @Test
    void whenAddNewTransaction_thenReturnNewTransaction() throws Exception {
        UserManager.setLoggedInUser(getUserRole(USER_ID));
        int count = transactionRepository.getAll().size();
        TransactionRequestDto request = new TransactionRequestDto(
                NEW_TRANSACTION_DATE,
                NEW_TRANSACTION_TYPE,
                NEW_TRANSACTION_AMOUNT,
                NEW_TRANSACTION_DESCRIPTION,
                NEW_TRANSACTION_CATEGORY_ID);
        var actualResponse = performRequest(HttpMethod.POST, API_URL_TRANSACTION, request, HttpStatus.OK);
        TransactionResponseDto response = fromResponse(actualResponse, TransactionResponseDto.class);

        assertAll(
                () -> assertThat(response.getDate()).isEqualTo(NEW_TRANSACTION_DATE),
                () -> assertThat(response.getType().name()).isEqualTo(NEW_TRANSACTION_TYPE),
                () -> assertThat(response.getAmount()).isEqualTo(NEW_TRANSACTION_AMOUNT),
                () -> assertThat(response.getDescription()).isEqualTo(NEW_TRANSACTION_DESCRIPTION),
                () -> assertThat(response.getCategory().getId()).isEqualTo(NEW_TRANSACTION_CATEGORY_ID),
                () -> assertThat(response.getUser().getId()).isEqualTo(USER_ID),
                () -> assertThat(transactionRepository.getAll()).hasSize(count + 1)
        );
    }

    @Test
    void whenAddNewTransaction_withWrongType_thenReturnBadRequest() throws Exception {
        UserManager.setLoggedInUser(getUserRole(USER_ID));
        TransactionRequestDto request = new TransactionRequestDto(
                NEW_TRANSACTION_DATE,
                NEW_TRANSACTION_TYPE + 1,
                NEW_TRANSACTION_AMOUNT,
                NEW_TRANSACTION_DESCRIPTION,
                NEW_TRANSACTION_CATEGORY_ID);
        performRequest(HttpMethod.POST, API_URL_TRANSACTION, request, HttpStatus.BAD_REQUEST);
    }

    @Test
    void whenAddNewTransaction_withWrongAmount_thenReturnBadRequest() throws Exception {
        UserManager.setLoggedInUser(getUserRole(USER_ID));
        TransactionRequestDto request = new TransactionRequestDto(
                NEW_TRANSACTION_DATE,
                NEW_TRANSACTION_TYPE,
                BigDecimal.ZERO,
                NEW_TRANSACTION_DESCRIPTION,
                NEW_TRANSACTION_CATEGORY_ID);
        performRequest(HttpMethod.POST, API_URL_TRANSACTION, request, HttpStatus.BAD_REQUEST);
    }

    @Test
    void whenAddNewTransaction_withWrongFields_thenReturnBadRequest() throws Exception {
        UserManager.setLoggedInUser(getUserRole(USER_ID));
        TransactionRequestDto request = new TransactionRequestDto(
                "",
                "",
                null,
                "",
                null);
        var actualResponse = performRequest(HttpMethod.POST, API_URL_TRANSACTION, request, HttpStatus.BAD_REQUEST);
        ErrorResponseDto response = fromResponse(actualResponse, ErrorResponseDto.class);

        assertThat(response.getErrorMessage()).contains(TransactionRequestDto.Fields.date,
                TransactionRequestDto.Fields.type,
                TransactionRequestDto.Fields.amount,
                TransactionRequestDto.Fields.description,
                TransactionRequestDto.Fields.categoryId);
    }

    @Test
    void whenUpdateTransaction_theReturnUpdatedTransaction() throws Exception {
        TransactionRequestDto request = new TransactionRequestDto();
        request.setAmount(NEW_TRANSACTION_AMOUNT);
        request.setDescription(TRANSACTION_DESCRIPTION_FOR_UPDATE + 1);
        request.setCategoryId(NEW_TRANSACTION_CATEGORY_ID);
        var actualResponse = performRequest(HttpMethod.PATCH, API_URL_TRANSACTION + "/" + TRANSACTION_ID_FOR_UPDATE, request, HttpStatus.OK);
        TransactionResponseDto response = fromResponse(actualResponse, TransactionResponseDto.class);

        assertAll(
                () -> assertThat(response.getDate()).isEqualTo(TRANSACTION_DATE_FOR_UPDATE),
                () -> assertThat(response.getType()).isEqualTo(TRANSACTION_TYPE_FOR_UPDATE),
                () -> assertThat(response.getAmount()).isEqualTo(NEW_TRANSACTION_AMOUNT),
                () -> assertThat(response.getDescription()).isEqualTo(TRANSACTION_DESCRIPTION_FOR_UPDATE + 1),
                () -> assertThat(response.getCategory().getId()).isEqualTo(NEW_TRANSACTION_CATEGORY_ID),
                () -> assertThat(response.getUser().getId()).isEqualTo(TRANSACTION_USER_ID_FOR_UPDATE)
        );
    }

    @Test
    void whenUpdateTransaction_withAmount_theReturnUpdatedTransaction() throws Exception {
        TransactionRequestDto request = new TransactionRequestDto();
        request.setAmount(TRANSACTION_AMOUNT_6.add(NEW_TRANSACTION_AMOUNT));
        var actualResponse = performRequest(HttpMethod.PATCH, API_URL_TRANSACTION + "/" + TRANSACTION_ID_6, request, HttpStatus.OK);
        TransactionResponseDto response = fromResponse(actualResponse, TransactionResponseDto.class);

        assertAll(
                () -> assertThat(response.getDate()).isEqualTo(TRANSACTION_DATE_6),
                () -> assertThat(response.getType()).isEqualTo(TRANSACTION_TYPE_6),
                () -> assertThat(response.getAmount()).isEqualTo(TRANSACTION_AMOUNT_6.add(NEW_TRANSACTION_AMOUNT)),
                () -> assertThat(response.getDescription()).isEqualTo(TRANSACTION_DESCRIPTION_6),
                () -> assertThat(response.getCategory().getId()).isEqualTo(TRANSACTION_CATEGORY_ID_6),
                () -> assertThat(response.getUser().getId()).isEqualTo(TRANSACTION_USER_ID_6)
        );
    }

    @Test
    void whenUpdateTransaction_withWrongId_theReturnBadRequest() throws Exception {
        TransactionRequestDto request = new TransactionRequestDto();
        request.setAmount(TRANSACTION_AMOUNT_6.add(NEW_TRANSACTION_AMOUNT));
        performRequest(HttpMethod.PATCH, API_URL_TRANSACTION + "/" + WRONG_ID, request, HttpStatus.NOT_FOUND);
    }

    @Test
    void whenDeleteTransaction_thenReturnOk() throws Exception {
        int count = transactionRepository.getAll().size();
        performRequest(HttpMethod.DELETE, API_URL_TRANSACTION + "/" + TRANSACTION_ID_FOR_DELETE, HttpStatus.OK);

        assertThat(transactionRepository.getAll()).hasSize(count - 1);
    }

    @Test
    void whenGetBalance_thenReturnBalance() throws Exception {
        UserManager.setLoggedInUser(getUserRole(USER_ID_FOR_UPDATE));
        var actualResponse = performRequest(HttpMethod.GET, API_URL_TRANSACTION_BALANCE, HttpStatus.OK);
        BalanceResponseDto response = fromResponse(actualResponse, BalanceResponseDto.class);

        assertThat(response.balance()).isEqualByComparingTo(new BigDecimal("440"));
    }

    @Test
    void whenGetIncome_thenReturnIncome() throws Exception {
        UserManager.setLoggedInUser(getUserRole(USER_ID_FOR_UPDATE));
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("from", "2025-01-09");
        pathVariables.put("to", "2025-01-27");
        var actualResponse = performRequest(HttpMethod.GET, API_URL_TRANSACTION_INCOME, HttpStatus.OK, pathVariables);
        IncomeResponseDto response = fromResponse(actualResponse, IncomeResponseDto.class);

        assertThat(response.totalIncome()).isEqualByComparingTo(new BigDecimal("300"));
    }

    @Test
    void whenGetTotalExpenses_thenReturnTotalExpenses() throws Exception {
        UserManager.setLoggedInUser(getUserRole(USER_ID_FOR_UPDATE));
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("from", "2025-01-05");
        pathVariables.put("to", "2025-01-27");
        var actualResponse = performRequest(HttpMethod.GET, API_URL_TRANSACTION_TOTAL_EXPENSES, HttpStatus.OK, pathVariables);
        ExpensesResponseDto response = fromResponse(actualResponse, ExpensesResponseDto.class);

        assertThat(response.totalExpenses()).isEqualByComparingTo(new BigDecimal("110"));
    }

    @Test
    void whenGetMonthExpenses_thenReturnMonthExpenses() throws Exception {
        UserManager.setLoggedInUser(getUserRole(USER_ID_FOR_UPDATE));
        var actualResponse = performRequest(HttpMethod.GET, API_URL_TRANSACTION_MONTH_EXPENSES, HttpStatus.OK);
        ExpensesResponseDto response = fromResponse(actualResponse, ExpensesResponseDto.class);

        assertThat(response.totalExpenses()).isEqualByComparingTo(new BigDecimal("0"));
    }

    @Test
    void whenGetAnalyze_thenReturnAnalyze() throws Exception {
        UserManager.setLoggedInUser(getUserRole(USER_ID_FOR_UPDATE));
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("from", "2025-01-01");
        pathVariables.put("to", "2025-01-29");
        var actualResponse = performRequest(HttpMethod.GET, API_URL_TRANSACTION_ANALYZE, HttpStatus.OK, pathVariables);
        List<ExpenseAnalysisDto> response = fromResponse(actualResponse, new TypeReference<>() {
        });

        assertThat(response).contains(new ExpenseAnalysisDto("Investments", new BigDecimal("260.00")));
    }

    @Test
    void whenGetReport_thenReturnReport() throws Exception {
        UserManager.setLoggedInUser(getUserRole(USER_ID_FOR_UPDATE));
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("from", "2025-01-01");
        pathVariables.put("to", "2025-01-29");
        var actualResponse = performRequest(HttpMethod.GET, API_URL_TRANSACTION_REPORT, HttpStatus.OK, pathVariables);
        FullReportResponseDto response = fromResponse(actualResponse, FullReportResponseDto.class);

        assertAll(
                () -> assertThat(response.balance().balance()).isEqualByComparingTo(new BigDecimal("440")),
                () -> assertThat(response.expenses().totalExpenses()).isEqualByComparingTo(new BigDecimal("260")),
                () -> assertThat(response.income().totalIncome()).isEqualByComparingTo(new BigDecimal("700"))
        );
    }
}
