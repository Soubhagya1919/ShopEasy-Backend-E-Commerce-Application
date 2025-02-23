package com.soubhagya.electronic.store.services;

import com.soubhagya.electronic.store.dtos.CreateOrderRequest;
import com.soubhagya.electronic.store.dtos.OrderDto;
import com.soubhagya.electronic.store.dtos.OrderUpdateRequest;
import com.soubhagya.electronic.store.dtos.PageableResponse;

import java.util.List;

/**
 * Service interface for managing orders within an electronic store.
 * This interface provides methods to handle operations related to
 * order creation, retrieval, updating, and deletion. It supports
 * administrator functionalities for updating order and payment status,
 * and allows users to update billing details.
 */
public interface OrderService {

    OrderDto getOrder(String orderId);

    //create order
    OrderDto createOrder(CreateOrderRequest orderRequest);

    //remove order
    void removeOrder(String orderId);

    //get orders of user
    List<OrderDto> getOrdersOfUser(String userId);

    //get orders
    PageableResponse<OrderDto> getOrders(int pageNumber, int pageSize, String sortBy, String sortDir);

    // Admin update order status and payment status
    OrderDto updateOrderStatusAndPayment(String orderId, String orderStatus, String paymentStatus);

    // User update billing details
    OrderDto updateBillingDetails(String orderId, String billingName, String billingPhone, String billingAddress);

    OrderDto updateOrder(String orderId, OrderUpdateRequest request);

    OrderDto updateOrder(String orderId, OrderDto request);
}
