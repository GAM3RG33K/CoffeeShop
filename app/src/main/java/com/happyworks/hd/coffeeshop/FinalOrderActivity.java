package com.happyworks.hd.coffeeshop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;

/**
 * This activity is last activity which user will See.
 * Here user can do following:
 * <ol>
 * <li>User can check the number of coffee to order</li>
 * <li>A Bonus Feature: User can click on the coffee image to get a 10% discount</li>
 * <li>User can go back to previous screen by pressing the back button of the device</li>
 * </ol>
 */
public class FinalOrderActivity extends AppCompatActivity {


    //this will show the final order price to be paid by the customer
    TextView totalPriceTextView;

    //this will show the coffee image
    ImageView coffeeImageView;

    //this will hold number of coffees being ordered
    static int counter;

    //this will hold price of the coffee at the time of the order
    static float price;

    //this is used in the bonus feature to apply discount
    static double discountMultiplier = 1;

    //this will be used to store data passed with activity intent
    static Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_order);


        //finding and setting objects with views
        totalPriceTextView = (TextView) findViewById(R.id.totalPriceTextView);
        coffeeImageView = (ImageView) findViewById(R.id.coffeeImageView);

        //fetching and storing the extra data sent with the intent
        extras = getIntent().getExtras();

        counter = extras.getInt("count");
        price = extras.getFloat("price");

        //setting the imageView to be clickable to allow discount
        coffeeImageView.setClickable(true);

        //setting the onClick listener to apply discount
        coffeeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //this is used to hold text based the conditions that are satisfied below
                String toastText;

                //this condition will make sure that the discount is only applied once
                if (discountMultiplier == 1) {
                    discountMultiplier = 0.9;
                    toastText = "Congratulations, discount applied on this order..";
                } else {
                    toastText = "Discount already applied on this order..Sorry!!";
                }
                setTotalPrice(counter * price * discountMultiplier);
                //show toast message
                Toast.makeText(FinalOrderActivity.this, toastText,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * This method will show the total price of the order in the textView
     *
     * @param totalPrice total price of the coffees ordered
     */
    public void setTotalPrice(final double totalPrice) {

        //check for null object
        if (totalPriceTextView != null) {

            //run on ui thread to ensure that app is not crashed
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    totalPriceTextView.setText(NumberFormat.getCurrencyInstance().format(totalPrice));
                }
            });

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //setting the and updating the price in the on resume method if the user
        if (extras != null) {
            setTotalPrice(counter * price * discountMultiplier);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
