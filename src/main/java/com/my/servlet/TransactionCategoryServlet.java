package com.my.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.my.dto.TransactionCategoryRequestDto;
import com.my.dto.TransactionCategoryResponseDto;
import com.my.service.TransactionCategoryService;
import com.my.service.impl.TransactionCategoryServiceImpl;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/category")
public class TransactionCategoryServlet extends HttpServlet {
    private final ObjectMapper objectMapper;
    private final TransactionCategoryService transactionCategoryService;

    public TransactionCategoryServlet() {
        this(new TransactionCategoryServiceImpl());
    }

    public TransactionCategoryServlet(TransactionCategoryService transactionCategoryService) {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        this.transactionCategoryService = transactionCategoryService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String reqId = req.getParameter("id");

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");

        try {
            if (reqId == null) {
                findAll(resp);
            } else {
                long id = Long.parseLong(reqId);
                findById(resp, id);
            }
        } catch (SQLException | IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void findAll(HttpServletResponse resp) throws IOException, SQLException {
        List<TransactionCategoryResponseDto> transactionCategories = transactionCategoryService.getAll();
        String foundCategories = objectMapper.writeValueAsString(transactionCategories);
        resp.getWriter().write(foundCategories);
    }

    private void findById(HttpServletResponse resp, long id) throws SQLException, IOException {
        TransactionCategoryResponseDto transactionCategory = transactionCategoryService.getById(id);
        String foundCategory = objectMapper.writeValueAsString(transactionCategory);
        resp.getWriter().write(foundCategory);
        if (transactionCategory == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        int contentLength = req.getContentLength();
        if (contentLength <= 0) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");

        try {
            save(req, resp);
        } catch (SQLException | IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void save(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        TransactionCategoryRequestDto transactionCategory = objectMapper.readValue(req.getReader(), TransactionCategoryRequestDto.class);
        TransactionCategoryResponseDto savedCategory = transactionCategoryService.save(transactionCategory);
        resp.getWriter().write(objectMapper.writeValueAsString(savedCategory));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        String reqId = req.getParameter("id");

        if (reqId == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");

        try {
            long id = Long.parseLong(reqId);
            update(req, resp, id);
        } catch (SQLException | IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void update(HttpServletRequest req, HttpServletResponse resp, long id) throws IOException, SQLException {
        TransactionCategoryRequestDto transactionCategory = objectMapper.readValue(req.getReader(), TransactionCategoryRequestDto.class);
        TransactionCategoryResponseDto updatedTransactionCategory = transactionCategoryService.update(id, transactionCategory);
        resp.getWriter().write(objectMapper.writeValueAsString(updatedTransactionCategory));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        String reqId = req.getParameter("id");

        if (reqId == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            long id = Long.parseLong(reqId);
            delete(resp, id);
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
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
