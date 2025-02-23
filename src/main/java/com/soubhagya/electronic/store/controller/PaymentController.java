package com.soubhagya.electronic.store.controller;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.soubhagya.electronic.store.dtos.OrderDto;
import com.soubhagya.electronic.store.dtos.UserDto;
import com.soubhagya.electronic.store.services.OrderService;
import com.soubhagya.electronic.store.services.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

/**
 * REST controller for managing payments using Razorpay.
 * This controller provides endpoints for initiating and capturing payments related to orders.
 */
@RestController
@SecurityRequirement(name="scheme1")
@RequestMapping("/payments")
public class PaymentController {

    /**
     * Service for handling user-related operations.
     * This is injected into the PaymentController to facilitate
     * accessing user details based on email for processing payments.
     */
    @Autowired
    private UserService userService;


    /**
     * A service for managing order-related operations.
     * Used to handle order creation, retrieval, and updates within the application.
     * This service is typically used for processing and managing orders.
     */
    @Autowired
    private OrderService orderService;

    /**
     * The API key for authenticating with the Razorpay service.
     * This key is used to authorize and initiate transactions
     * with Razorpay from the application. It is injected from
     * the application's configuration properties.
     */
    @Value("${razorpayKey}")
    private String key;
    /**
     * Holds the Razorpay secret key used for authenticating API requests.
     * This property is injected from the application configuration where it is specified using
     * the "razorpaySecret" configuration key.
     */
    @Value("${razorpaySecret}")
    private String secret;

    /**
     * Initiates a payment process for the specified order using the Razorpay payment gateway.
     *
     * @param orderId the identifier of the order for which payment is to be initiated
     * @param principal the Principal object containing the security context of the current user
     * @return a ResponseEntity containing either the details of the created Razorpay order and status upon success
     *         or an error message if the order creation fails
     */
    @PostMapping("/initiate-payment/{orderId}")
    public ResponseEntity<?> initiatePayment(@PathVariable String orderId, Principal principal) {


        UserDto userDto = this.userService.getUserByEmail(principal.getName());
        OrderDto ourOrder = this.orderService.getOrder(orderId);

        //razorpay api to create order


        try {
            RazorpayClient razorpayClient = new RazorpayClient(key, secret);
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", ourOrder.getOrderAmount() * 100);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "receipt_orderId");

            // create order
            Order order = razorpayClient.orders.create(orderRequest);
            //save the order id to backend
            ourOrder.setRazoryPayOrderId(order.get("id"));
//            ourOrder.setPaymentStatus(order.get("status").toString().toUpperCase());
            this.orderService.updateOrder(ourOrder.getOrderId(), ourOrder);

            System.out.println(order);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "orderId", ourOrder.getOrderId(),
                    "razorpayOrderId", ourOrder.getRazoryPayOrderId(),
                    "amount", ourOrder.getOrderAmount(),
                    "paymentStatus", ourOrder.getPaymentStatus()


            ));


        } catch (RazorpayException ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error in creating order !!"));
        }

    }

    /**
     * Verifies the payment signature using Razorpay and updates the payment status of the given order.
     *
     * @param data A map containing the payment details including razorpayOrderId, razorpayPaymentId, and razorpayPaymentSignature.
     * @param orderId The unique identifier of the order for which the payment is being verified.
     * @return A ResponseEntity containing a success message and status of payment signature verification.
     *         If the signature is verified successfully, the response will include signatureVerified as true; otherwise false.
     * @throws RuntimeException if there is an exception while verifying the payment.
     */
    @PostMapping("/capture/{orderId}")
    public ResponseEntity<?> verifyAndSavePayment(
            @RequestBody Map<String, Object> data,
            @PathVariable String orderId
    ) {

        String razorpayOrderId = data.get("razorpayOrderId").toString();
        String razorpayPaymentId = data.get("razorpayPaymentId").toString();
        String razorpayPaymentSignature = data.get("razorpayPaymentSignature").toString();

        OrderDto order = this.orderService.getOrder(orderId);
        order.setPaymentStatus("PAID");
//        order.setRazoryPayOrderId(razorpayPaymentId);
        order.setPaymentId(razorpayPaymentId);
//        store data as per your need
        this.orderService.updateOrder(orderId, order);


        try {
            RazorpayClient razorpayClient = new RazorpayClient(key, secret);
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", razorpayOrderId);
            options.put("razorpay_payment_id", razorpayPaymentId);
            options.put("razorpay_signature", razorpayPaymentSignature);

            boolean b = Utils.verifyPaymentSignature(options, secret);
            if (b) {
                System.out.println("payment signature verified");
                return new ResponseEntity<>(
                        Map.of(
                                "message", "Payment Done",
                                "success", true,
                                "signatureVerified", true
                        )
                        , HttpStatus.OK);
            } else {
                System.out.println("payment signature verified");
                return new ResponseEntity<>(
                        Map.of(
                                "message", "Payment done",
                                "success", true,
                                "signatureVerified", false
                        )
                        , HttpStatus.OK);
            }

        } catch (RazorpayException e) {
            throw new RuntimeException(e);
        }


    }
}

