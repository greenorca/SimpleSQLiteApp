package com.green_orca.android.demo2018a2.model;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.green_orca.android.demo2018a2.R;

import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ProductViewHolder> {
    private List<Product> mDataset;
    private final String LOGTAG = getClass().getSimpleName();
    private static MyClickListener myClickListener;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ProductViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener{
        // each data item is just a string in this case
        public TextView txtProductIdView;
        public TextView txtProductNameView;
        public TextView txtProductQuantityView;

        public ProductViewHolder(View v) {
            super(v);
            txtProductIdView = v.findViewById(R.id.txtProductId);
            txtProductNameView = v.findViewById(R.id.txtProductName);
            txtProductQuantityView = v.findViewById(R.id.txtProductQuantity);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            myClickListener.onItemClick(getPosition(), view);
        }
    }

    public void refresh(List<Product> myDataset){
        mDataset = myDataset;
        this.notifyDataSetChanged();
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyRecyclerViewAdapter(List<Product> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyRecyclerViewAdapter.ProductViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        Log.d(LOGTAG, "inside onCreateViewHolder() fun:");
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.person_item_layout, parent, false);
        Log.d(LOGTAG, "inside onCreateViewHolder() fun, stage 2:");

        ProductViewHolder vh = new ProductViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        Log.d(LOGTAG, "inside onBindViewHolder() fun: "+holder.txtProductIdView);

        Product p = mDataset.get(position);
        Log.d(LOGTAG,"printing product:"+p.getID()+"name: "+ p.getProductName()+", qnty: "+p.getQuantity());
        holder.txtProductIdView.setText(""+p.getID());
        holder.txtProductNameView.setText("" + p.getProductName());
        holder.txtProductQuantityView.setText("Anzahl: "+p.getQuantity());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void addItem(Product dataObj, int index) {
        mDataset.add(dataObj);
        notifyItemInserted(index);
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}
