package com.soubhagya.electronic.store.services.impl;

import com.soubhagya.electronic.store.dtos.CreateOrderRequest;
import com.soubhagya.electronic.store.dtos.OrderDto;
import com.soubhagya.electronic.store.dtos.OrderUpdateRequest;
import com.soubhagya.electronic.store.dtos.PageableResponse;
import com.soubhagya.electronic.store.entities.*;
import com.soubhagya.electronic.store.exceptions.BadApiRequestException;
import com.soubhagya.electronic.store.exceptions.ResourceNotFoundException;
import com.soubhagya.electronic.store.helper.Helper;
import com.soubhagya.electronic.store.repositories.CartItemRepository;
import com.soubhagya.electronic.store.repositories.CartRepository;
import com.soubhagya.electronic.store.repositories.OrderRepository;
import com.soubhagya.electronic.store.repositories.UserRepository;
import com.soubhagya.electronic.store.services.OrderService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Service implementation for managing order operations.
 * This class performs CRUD operations on orders and provides additional
 * functionalities like updating order status, payment status, and billing details.
 */
@Service
public class OrderServiceImpl implements OrderService {

    private final UserRepository userRepository;

    private final OrderRepository orderRepository;

    private final CartItemRepository cartItemRepository;

    private final ModelMapper modelMapper;

    private final CartRepository cartRepository;

    public OrderServiceImpl(UserRepository userRepository, OrderRepository orderRepository, ModelMapper modelMapper, CartRepository cartRepository, CartItemRepository cartItemRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.modelMapper = modelMapper;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    /**
     * Retrieves an order by its unique identifier and maps it to an OrderDto.
     *
     * @param orderId the unique identifier of the order to be retrieved
     * @return an OrderDto object containing the order details
     * @throws ResourceNotFoundException if no order is found with the provided orderId
     */
    @Override
    public OrderDto getOrder(String orderId) {
        Order order = this.orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found !!"));
        return this.modelMapper.map(order, OrderDto.class);
    }

    /**
     * Creates a new order based on the provided order request details.
     * This involves fetching user and cart information, validating the items in the cart,
     * and transferring the cart items to the order. The cart is cleared after the order is created.
     *
     * @param orderRequest the request object containing order details such as user ID, cart ID, billing information, payment status, and order status
     * @return an OrderDto object that represents the newly created order
     * @throws ResourceNotFoundException if the user or cart cannot be found with the provided IDs
     * @throws BadApiRequestException if the cart is empty and cannot be converted to an order
     */
    @Transactional
    @Override
    public OrderDto createOrder(CreateOrderRequest orderRequest) {
        //fetch user
        String userId = orderRequest.getUserId();
        String cartId = orderRequest.getCartId();

        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with given id !!"));

        //fetch cart
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart with given id not found on server !!"));

        List<CartItem> cartItems = cart.getItems();

        if(cartItems.isEmpty()){
            throw new BadApiRequestException("Invalid number of items in cart !!");
        }

        Order order = Order.builder().
                billingName(orderRequest.getBillingName()).
                billingPhone(orderRequest.getBillingPhone()).
                billingAddress(orderRequest.getBillingAddress())
                .orderedDate(new Date())
                .deliveredDate(null)
                .paymentStatus(orderRequest.getPaymentStatus())
                .orderStatus(orderRequest.getOrderStatus())
                .orderId(UUID.randomUUID().toString())
                .user(user).build();

        AtomicReference<Double> orderAmount = new AtomicReference<>(0.0D);
        List<OrderItem> orderItems = cartItems.stream().map(cartItem -> {

            //CartItem -> OrderItem
            OrderItem orderItem = OrderItem.builder()
                    .quantity(cartItem.getQuantity())
                    .product(cartItem.getProduct())
                    .totalPrice(cartItem.getQuantity() * cartItem.getProduct().getDiscountedPrice())
                    .order(order)
                    .build();
            orderAmount.set(orderAmount.get() + orderItem.getTotalPrice());
            return orderItem;
        }).collect(Collectors.toList());

        order.setOrderItems(orderItems);
        order.setOrderAmount(orderAmount.get());

        //cart.getItems().clear();
        //cartRepository.save(cart);

        // Batch delete all items in the cart
        cartItemRepository.deleteByCart(cart);

        Order savedOrder = orderRepository.save(order);

        return modelMapper.map(savedOrder, OrderDto.class);
    }

    /**
     * Removes an order from the repository based on the given order ID.
     * If the order does not exist, a ResourceNotFoundException is thrown.
     *
     * @param orderId the unique identifier of the order to be removed
     */
    @Override
    public void removeOrder(String orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order is not found !!"));
        orderRepository.delete(order);
    }

    /**
     * Retrieves a list of orders associated with a specific user.
     *
     * @param userId the unique identifier of the user whose orders are to be retrieved
     * @return a list of OrderDto objects representing the orders of the specified user
     * @throws ResourceNotFoundException if a user with the specified userId does not exist
     */
    @Override
    public List<OrderDto> getOrdersOfUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User id not found !!"));
        List<Order> orderOfUser = orderRepository.findByUser(user);

        return orderOfUser.
                stream().
                map(order -> modelMapper.map(order, OrderDto.class)).
                collect(Collectors.toList());
    }

    /**
     * Retrieves a paginated list of orders with sorting options.
     *
     * @param pageNumber the page number to retrieve, zero-based
     * @param pageSize the size of the page to retrieve
     * @param sortBy the field by which the results should be sorted
     * @param sortDir the direction of the sort, either "asc" for ascending or "desc" for descending
     * @return a PageableResponse containing a list of OrderDto objects representing the orders
     */
    @Override
    public PageableResponse<OrderDto> getOrders(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("desc")) ? (Sort.by(sortBy).descending()) : (Sort.by(sortBy).ascending());
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Order> page = orderRepository.findAll(pageable);
        return Helper.getPageableResponse(page, OrderDto.class);
    }

    /**
     * Updates the status and payment status of an existing order.
     *
     * @param orderId the unique identifier of the order to be updated
     * @param orderStatus the new status to set for the order
     * @param paymentStatus the new payment status to set for the order
     * @return an OrderDto object containing the updated order information
     * @throws ResourceNotFoundException if no order is found with the specified orderId
     */
    @Transactional
    @Override
    public OrderDto updateOrderStatusAndPayment(String orderId, String orderStatus, String paymentStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with given ID"));

        // Update order status and payment status
        order.setOrderStatus(orderStatus);
        order.setPaymentStatus(paymentStatus);

        // Save updated order
        Order updatedOrder = orderRepository.save(order);

        return modelMapper.map(updatedOrder, OrderDto.class);
    }

    /**
     * Updates the billing details of an existing order identified by its ID.
     *
     * @param orderId the unique identifier of the order whose billing details are to be updated
     * @param billingName the new billing name to be set for the order
     * @param billingPhone the new billing phone number to be set for the order
     * @param billingAddress the new billing address to be set for the order
     * @return an OrderDto object containing the updated billing details
     */
    @Transactional
    @Override
    public OrderDto updateBillingDetails(String orderId, String billingName, String billingPhone, String billingAddress) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with given ID"));

        // Update billing details
        order.setBillingName(billingName);
        order.setBillingPhone(billingPhone);
        order.setBillingAddress(billingAddress);

        // Save updated order
        Order updatedOrder = orderRepository.save(order);

        return modelMapper.map(updatedOrder, OrderDto.class);
    }

    /**
     * Updates an existing order with the provided update request data.
     *
     * @param orderId the unique identifier of the order to be updated
     * @param request an object containing the order update data, including billing information,
     *                payment status, order status, and delivery date
     * @return an OrderDto object representing the updated order
     * @throws BadApiRequestException if no order with the specified orderId is found
     */
    @Override
    public OrderDto updateOrder(String orderId, OrderUpdateRequest request) {

        //get the order
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new BadApiRequestException("Invalid update data"));
        order.setBillingName(request.getBillingName());
        order.setBillingPhone(request.getBillingPhone());
        order.setBillingAddress(request.getBillingAddress());
        order.setPaymentStatus(request.getPaymentStatus());
        order.setOrderStatus(request.getOrderStatus());
        order.setDeliveredDate(request.getDeliveredDate());
        Order updatedOrder = orderRepository.save(order);
        return modelMapper.map(updatedOrder, OrderDto.class);
    }

    /**
     * Updates an existing order with the given information.
     *
     * @param orderId the ID of the order to be updated
     * @param request the OrderDto object containing updated order details
     * @return the updated OrderDto object reflecting the changes
     * @throws BadApiRequestException if the orderId is invalid or does not exist
     */
    @Override
    public OrderDto updateOrder(String orderId, OrderDto request) {
        //get the order
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new BadApiRequestException("Invalid update data"));
        order.setBillingName(request.getBillingName());
        order.setBillingPhone(request.getBillingPhone());
        order.setBillingAddress(request.getBillingAddress());
        order.setPaymentStatus(request.getPaymentStatus());
        order.setOrderStatus(request.getOrderStatus());
        order.setDeliveredDate(request.getDeliveredDate());
        order.setRazoryPayOrderId(request.getRazoryPayOrderId());
        order.setPaymentId(request.getPaymentId());
        Order updatedOrder = orderRepository.save(order);
        return modelMapper.map(updatedOrder, OrderDto.class);
    }

}
