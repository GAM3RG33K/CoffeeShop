package com.happyworks.hd.coffeeshop;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;


/**
 * This is main activity which will be the entry point for the app.
 * <p>
 * In this activity user can do following:
 * <ol>
 * <li>User can select the number of coffee to order</li>
 * <li>User can see the price of a single coffee</li>
 * <li>A Bonus Feature: User can keep the +/- button pressed to increase/decrease
 * the number of coffee automatically</li>
 * </ol>
 */
public class MainActivity extends AppCompatActivity {


    //this will show the quantity of coffee selected for order
    TextView quantityTextView;
    //this will show the base price of the coffee
    TextView coffeePriceTextView;

    //this is order button which will redirect user to final order activity
    Button orderButton;

    //this button will decrease the number of coffee ordered,
    //only if the count is greater than zero
    Button minusButton;

    //this button will increase the number of coffee ordered
    Button plusButton;

    //this variable is set to final as the price of the coffee does not vary
    private static final int COFFEE_BASE_PRICE = 10;

    //counter will hold the number of coffee to be ordered
    private static int counter = 0;

    //this is used for the bonus feature
    static boolean longPressEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //finding and setting object with view
        quantityTextView = findViewById(R.id.quantityTextView);
        coffeePriceTextView = findViewById(R.id.coffeeBasePriceTextView);


        orderButton = findViewById(R.id.orderButton);
        minusButton = findViewById(R.id.minusButton);
        plusButton = findViewById(R.id.plusButton);


        //setting coffee price in the text view
        coffeePriceTextView.setText(NumberFormat.getCurrencyInstance().format(COFFEE_BASE_PRICE));

        //setting the onCLick listener for order button
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeOrder();

            }
        });

        //setting the click listener for minus button
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //this will only decrease the number if the counter is greater than 0
                if (counter > 0) {
                    counter--;
                    setQuantity(counter);
                }
            }
        });

        //this method is used for bonus feature
        //this will allow user to keep the button pressed to automatically decrease
        // the number of coffees(counter)
        minusButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //here only a thread will start which decreases the counter
                longPressEnabled = true;
                startLongPressThread(false);
                return false;
            }
        });

        //this will is used to detect when user will take the finger away from the button
        // this will be used to stop the thread which decreases the counter
        minusButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    longPressEnabled = false;
                }
                return false;
            }
        });


        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counter++;
                setQuantity(counter);
            }
        });


        //this method is used for bonus feature
        //this will allow user to keep the button pressed to automatically increase
        // the number of coffees(counter)
        plusButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //here only a thread will start which increases the counter
                longPressEnabled = true;
                startLongPressThread(true);
                return false;
            }
        });

        //this will is used to detect when user will take the finger away from the button
        // this will be used to stop the thread which increases the counter
        plusButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    longPressEnabled = false;
                }
                return false;
            }
        });
    }

    /**
     * This method will create a new thread to change the counter based on boolean value passed
     * @param isIncrement true if counter is to be increased or false to be decreased
     */
    private void startLongPressThread(final boolean isIncrement) {
        Thread longPressThread = new Thread(new Runnable() {
            @Override
            public void run() {
                //this while loop keeps iterating until the longPressedEnabled is not set to false
                while (longPressEnabled) {
                    try {
                        //this will allow user to increase/decrease the count with comfort
                        //as processors are powerful enough to increase count by thousands in a single click
                        //sleeping the current thread for 300 milliseconds will allows slow pace of increment/decrement

                        Thread.sleep(300);

                        //check if the user has long pressed the + button or the - button
                        if (isIncrement) {
                            counter++;
                        } else if (counter > 0) {
                            //this means that user has long pressed the - button and the counter is > 0
                            counter--;
                        } else if (counter == 0 && !isIncrement) {
                            //this means that user has long pressed the - button but the counter is = 0
                            //so we will stop the while loop from here as we can not allow decrement below 0
                            longPressEnabled = false;
                        }
                        //this method will update the counter value in the text view between the + and - button
                        setQuantity(counter);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        //what we did above was just defining a thread
        //it will not work until we start it

        //starting the thread.
        longPressThread.start();
    }

    /**
     * In this method we will fetch the data set by user for the order
     * and pass it to another activity which will show the final order summery
     */
    private void placeOrder() {

        //create an intent from this activity to another activity
        //1st param this activity's context (For ex. CurrentActivityName.this)
        //2nd param another activity's class object (For ex. AnotherActivityName.class)
        Intent intent = new Intent(MainActivity.this, FinalOrderActivity.class);

        //add necessary order data to show in other activity
        intent.putExtra("count", counter);
        intent.putExtra("price", Float.valueOf(COFFEE_BASE_PRICE));

        //start activity
        startActivity(intent);
    }


    /**
     * this method is used to set the counter value in the quantityTextView
     * @param quantity number of coffees to order
     */
    public void setQuantity(final int quantity) {

        //first we need to check if the textView is initialized or not

        //if not then do nothing
        if (quantityTextView != null) {
            //cover the view related operations inside run on UI thread
            //to ensure that app does not crash
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    quantityTextView.setText(String.valueOf(quantity));
                }
            });
        }

    }

    /**
     * this method is used to reset order data
     */
    private void reset() {
        setQuantity(0);
        counter = 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //added reset button to reset order data
        menu.add(0, 1, 0, "Reset");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            //call reset() when reset is selected from the option menu
            reset();
        }
        return super.onOptionsItemSelected(item);
    }
}
