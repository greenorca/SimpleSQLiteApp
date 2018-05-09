package com.green_orca.android.demo2018a2;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.green_orca.android.demo2018a2.model.*;

import com.green_orca.android.demo2018a2.model.MyDBHandler;
import com.green_orca.android.demo2018a2.model.Product;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView idView;
    EditText productBox;
    EditText quantityBox;
    RecyclerView mRecyclerView;
    Button btnNew, btnSave;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private final String LOGTAG = getClass().getSimpleName();
    private Product currentProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if (savedInstanceState == null) {
            //getFragmentManager().beginTransaction()
            //        .add(R.id.container, new PlaceholderFragment()).commit();
        }

        mRecyclerView = (RecyclerView)findViewById(R.id.myRecyclerView);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        Log.d(LOGTAG, "successfully created DBHandler instance");
        List<Product> liste = dbHandler.getAllProducts();
        Log.d(LOGTAG, "fetched "+liste.size()+" products");

        mAdapter = new MyRecyclerViewAdapter(liste);
        Log.d(LOGTAG, "created mAdapter: "+mAdapter.toString());
        mRecyclerView.setAdapter(mAdapter);

        btnNew = (Button)findViewById(R.id.buttonNew);
        btnSave = (Button)findViewById(R.id.buttonSave);
    }

    private void updateSelectedProduct(int position){
        Log.d(LOGTAG, "updating selected item, ID: "+position);
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        currentProduct = dbHandler.getProduct(position);
        Log.d(LOGTAG, "fetched item: "+currentProduct);

        if (currentProduct != null){
            productBox.setText(currentProduct.getProductName());
            quantityBox.setText(""+currentProduct.getQuantity());
            idView.setText(""+currentProduct.getID());
            btnSave.setText("Update");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        ((MyRecyclerViewAdapter) mAdapter).setOnItemClickListener(new
              MyRecyclerViewAdapter.MyClickListener() {
                  @Override
                  public void onItemClick(int position, View v) {
                      Log.i(LOGTAG, " Clicked on Item " + position+", view: "+v);
                      String id = ((TextView)v.findViewById(R.id.txtProductId)).getText().toString();
                      updateSelectedProduct(Integer.parseInt(id));
                  }
              });
    }
    @Override
    protected void onStart()
    {
        super.onStart();
        currentProduct = new Product();
        idView = (TextView) findViewById(R.id.txtViewProductID);
        productBox = (EditText) findViewById(R.id.editTextProductName);
        quantityBox =
                (EditText) findViewById(R.id.editTextQuantity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.activity_database,menu);
        return true;
    }

    /**
     * updates or saves current product
     * @param view
     */
    public void updateProduct(View view){
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        boolean isNewProduct = currentProduct.getProductName()==null ||
                currentProduct.getProductName().equals("");
        int quantity = 0;
        try {
            Log.d(LOGTAG, "Quantity_txt: "+quantityBox.getText().toString());
            quantity = Integer.parseInt(quantityBox.getText().toString());
            Log.d(LOGTAG, "Quantity_int: "+quantity);
            Log.d(LOGTAG, "Quantity_txt: "+quantityBox.getText().toString());
            if (quantity < 0){
                Toast.makeText(this, "Error, quantity must be at least 0", Toast.LENGTH_LONG).show();
                quantityBox.setError("quantity must be greater or equal 0");
                return;
            }
        } catch(NumberFormatException ex){
            quantityBox.setError("quantity must be greater or equal 0");
            return;
        }
        currentProduct.setQuantity(quantity);
        String productName = productBox.getText().toString();
        if (productName.trim().length()<1){
            productBox.setError("invalid name: "+productName);
            return;
        }
        currentProduct.setProductName(productName);
        if (!isNewProduct)
            dbHandler.updateProduct(currentProduct);
        else
            dbHandler.addProduct(currentProduct);

        ((MyRecyclerViewAdapter)mRecyclerView.getAdapter()).refresh(dbHandler.getAllProducts());
        Toast.makeText(this, "Product updated", Toast.LENGTH_LONG).show();

    }

    public void newProduct (View view) {
        currentProduct = new Product();
        productBox.setText("");
        quantityBox.setText("");
        btnSave.setText("Save");
        productBox.requestFocus();
    }

    public void lookupProduct (View view) {
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);

        Product product =
                dbHandler.findProduct(productBox.getText().toString());

        if (product != null) {
            currentProduct = product;
            idView.setText(String.valueOf(product.getID()));

            quantityBox.setText(String.valueOf(product.getQuantity()));
        } else {
            idView.setText("No Match Found");
        }
    }

    public void removeProduct (View view) {
        MyDBHandler dbHandler = new MyDBHandler(this, null,
                null, 1);

        boolean result = dbHandler.deleteProduct(
                productBox.getText().toString());

        if (result)
        {
            idView.setText("Record Deleted");
            productBox.setText("");
            quantityBox.setText("");
            currentProduct = null;
                    ((MyRecyclerViewAdapter)mRecyclerView.getAdapter()).refresh(dbHandler.getAllProducts());

        }
        else
            idView.setText("No Match Found");
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
