package com.my.servlet;

import com.my.annotation.Audition;
import com.my.annotation.Loggable;
import com.my.dto.TransactionCategoryRequestDto;
import com.my.dto.TransactionCategoryResponseDto;
import com.my.exception.ArgumentNotValidException;
import com.my.exception.EntityNotFoundException;
import com.my.exception.TransactionCategoryException;
import com.my.service.TransactionCategoryService;
import com.my.service.impl.TransactionCategoryServiceImpl;
import com.my.util.Validation;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Loggable
@Audition
@WebServlet("/category")
public class TransactionCategoryServlet extends HttpServlet {
    private final ServletUtils servletUtils;
    private final TransactionCategoryService transactionCategoryService;

    public TransactionCategoryServlet() {
        this(new TransactionCategoryServiceImpl());
    }

    public TransactionCategoryServlet(TransactionCategoryService transactionCategoryService) {
        this.transactionCategoryService = transactionCategoryService;
        this.servletUtils = new ServletUtils();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        servletUtils.setJsonContentType(resp);
        try {
            Optional<Long> id = servletUtils.getId(req);
            if (id.isEmpty()) {
                findAll(resp);
            } else {
                findById(resp, id.get());
            }
        } catch (EntityNotFoundException e) {
            servletUtils.handleError(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (SQLException | IOException e) {
            servletUtils.handleError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred. Please try again later.");
        }
    }

    private void findAll(HttpServletResponse resp) throws IOException, SQLException {
        List<TransactionCategoryResponseDto> transactionCategories = transactionCategoryService.getAll();
        servletUtils.writeResponse(resp, transactionCategories);
    }

    private void findById(HttpServletResponse resp, long id) throws SQLException, IOException {
        TransactionCategoryResponseDto transactionCategory = transactionCategoryService.getById(id);
        servletUtils.writeResponse(resp, transactionCategory);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        servletUtils.setJsonContentType(resp);
        if (!servletUtils.checkAuthentication(resp)) {
            return;
        }
        int contentLength = req.getContentLength();
        if (contentLength <= 0) {
            servletUtils.handleError(resp, HttpServletResponse.SC_BAD_REQUEST, "Тело запроса пустое");
            return;
        }
        try {
            save(req, resp);
        } catch (TransactionCategoryException e) {
            servletUtils.handleError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (ArgumentNotValidException e) {
            servletUtils.handleError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (SQLException | IOException e) {
            servletUtils.handleError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error during saving");
        }
    }

    private void save(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        TransactionCategoryRequestDto transactionCategoryRequestDto = servletUtils.readRequestBody(req, TransactionCategoryRequestDto.class);
        Validation.validationCategory(transactionCategoryRequestDto);
        TransactionCategoryResponseDto savedCategory = transactionCategoryService.save(transactionCategoryRequestDto);
        servletUtils.writeResponse(resp, savedCategory);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        servletUtils.setJsonContentType(resp);
        if (!servletUtils.checkAuthentication(resp)) {
            return;
        }
        try {
            Optional<Long> id = servletUtils.getId(req);
            if (id.isEmpty()) {
                servletUtils.handleError(resp, HttpServletResponse.SC_BAD_REQUEST, "Неверно указан id");
                return;
            }
            update(req, resp, id.get());
        } catch (EntityNotFoundException e) {
            servletUtils.handleError(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (TransactionCategoryException e) {
            servletUtils.handleError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (ArgumentNotValidException e) {
            servletUtils.handleError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (SQLException | IOException e) {
            servletUtils.handleError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred. Please try again later.");
        }
    }

    private void update(HttpServletRequest req, HttpServletResponse resp, long id) throws IOException, SQLException {
        TransactionCategoryRequestDto transactionCategoryRequestDto = servletUtils.readRequestBody(req, TransactionCategoryRequestDto.class);
        Validation.validationCategory(transactionCategoryRequestDto);
        TransactionCategoryResponseDto updatedTransactionCategory = transactionCategoryService.update(id, transactionCategoryRequestDto);
        servletUtils.writeResponse(resp, updatedTransactionCategory);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        servletUtils.setJsonContentType(resp);
        if (!servletUtils.checkAuthentication(resp)) {
            return;
        }
        try {
            Optional<Long> id = servletUtils.getId(req);
            if (id.isEmpty()) {
                servletUtils.handleError(resp, HttpServletResponse.SC_BAD_REQUEST, "Неверно указан id");
                return;
            }
            delete(resp, id.get());
        } catch (SQLException e) {
            servletUtils.handleError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred. Please try again later.");
        }
    }

    private void delete(HttpServletResponse resp, long id) throws SQLException {
        boolean deleted = transactionCategoryService.deleteById(id);
        if (deleted) {
            resp.setStatus(HttpServletResponse.SC_OK);
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
