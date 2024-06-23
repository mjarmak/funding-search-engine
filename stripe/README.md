To run stripe locally, open cmd in the stripe.exe directory and run the following command:
```
stripe login
./stripe listen --forward-to localhost:8080/api/v1/webhook
```

Trigger the webhook by running the following command:
```
./stripe trigger payment_intent.succeeded
./stripe trigger customer.subscription.deleted
```


### Try it out
Click the checkout button to be redirected to the Stripe Checkout page. Use any of these test cards to simulate a payment.

```
Payment succeeds 4242 4242 4242 4242
Payment requires authentication 4000 0025 0000 3155
Payment is declined 4000 0000 0000 9995
```
