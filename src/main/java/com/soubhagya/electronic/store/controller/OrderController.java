package com.soubhagya.electronic.store.controller;

import com.soubhagya.electronic.store.dtos.ApiResponseMessage;
import com.soubhagya.electronic.store.dtos.CreateOrderRequest;
import com.soubhagya.electronic.store.dtos.OrderDto;
import com.soubhagya.electronic.store.dtos.PageableResponse;
import com.soubhagya.electronic.store.services.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * The OrderController class provides REST endpoints for managing orders
 * within the application. It includes operations for creating, removing,
 * and updating orders, as well as retrieval of orders.
 *
 * This controller is secured using predefined security roles and handles
 * the order-related HTTP requests.
 */
@RestController
@RequestMapping("/orders")
@SecurityRequirement(name = "scheme1")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Creates a new order based on the provided order request details.
     * This method is accessible to users with roles 'NORMAL' and 'ADMIN'.
     *
     * @param createOrderRequest the details of the order to be created
     * @return a ResponseEntity containing the created OrderDto and an HTTP status code of CREATED
     */
    @PreAuthorize("hasAnyRole('NORMAL', 'ADMIN')")
    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@RequestBody @Valid CreateOrderRequest createOrderRequest){
        OrderDto order = orderService.createOrder(createOrderRequest);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    /**
     * Removes an order specified by the given order ID.
     *
     * @param orderId the ID of the order to be removed
     * @return a ResponseEntity containing an ApiResponseMessage indicating the result of the operation
     */
    @PreAuthorize(("hasRole('ADMIN')"))
    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponseMessage> removeOrder(@PathVariable String orderId){
        orderService.removeOrder(orderId);
        ApiResponseMessage responseMessage = ApiResponseMessage.builder()
                .status(HttpStatus.OK)
                .success(true)
                .message("order is deleted successfully !!")
                .build();

        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    /**
     * Retrieves a list of orders for a specific user.
     * This endpoint requires the user to have either 'NORMAL' or 'ADMIN' role.
     *
     * @param userId the ID of the user whose orders are to be retrieved
     * @return ResponseEntity containing a list of OrderDto objects and an HTTP status code
     */
    @PreAuthorize("hasAnyRole('NORMAL', 'ADMIN')")
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<OrderDto>> getOrdersOfUser(@PathVariable String userId){
        List<OrderDto> ordersOfUser = orderService.getOrdersOfUser(userId);
        return new ResponseEntity<>(ordersOfUser, HttpStatus.OK);
    }

    /**
     * Retrieves a paginated and sorted list of orders.
     * Only users with the 'ADMIN' role are authorized to access this method.
     *
     * @param pageNumber the page number of the orders to be retrieved, defaults to 0 if not provided
     * @param pageSize the number of orders per page, defaults to 10 if not provided
     * @param sortBy the field by which to sort the orders, defaults to 'orderedDate' if not provided
     * @param sortDir the direction of sort, can be 'asc' or 'desc', defaults to 'desc' if not provided
     * @return a ResponseEntity containing a paginated response of OrderDto objects
     */
    @PreAuthorize(("hasRole('ADMIN')"))
    @GetMapping
    public ResponseEntity<PageableResponse<OrderDto>> getOrders(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "orderedDate", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc", required = false) String sortDir
    )
    {
        PageableResponse<OrderDto> orders = orderService.getOrders(pageNumber, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    /**
     * Updates the status and payment status of a specified order.
     *
     * @param orderId       the ID of the order to update
     * @param orderStatus   the new status of the order
     * @param paymentStatus the new payment status of the order
     * @return a ResponseEntity containing the updated OrderDto and HTTP status OK
     */
    @PutMapping("/admin/{orderId}")
    public ResponseEntity<OrderDto> updateOrderStatusAndPayment(
            @PathVariable String orderId,
            @RequestParam("orderStatus") String orderStatus,
            @RequestParam("paymentStatus") String paymentStatus) {

        OrderDto updatedOrder = orderService.updateOrderStatusAndPayment(orderId, orderStatus, paymentStatus);
        return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
    }

    /**
     * Updates the billing details for a specific order.
     *
     * @param orderId the unique identifier of the order to be updated
     * @param billingName the new billing name to be associated with the order
     * @param billingPhone the new billing phone number to be associated with the order
     * @param billingAddress the new billing address to be associated with the order
     * @return a ResponseEntity containing the updated OrderDto object and an HTTP status of OK
     */
    @PutMapping("/user/{orderId}")
    public ResponseEntity<OrderDto> updateBillingDetails(
            @PathVariable String orderId,
            @RequestParam("billingName") String billingName,
            @RequestParam("billingPhone") String billingPhone,
            @RequestParam("billingAddress") String billingAddress) {

        OrderDto updatedOrder = orderService.updateBillingDetails(orderId, billingName, billingPhone, billingAddress);
        return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
    }

}
