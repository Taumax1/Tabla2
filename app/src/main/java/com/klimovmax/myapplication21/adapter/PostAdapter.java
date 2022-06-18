package com.klimovmax.myapplication21.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.klimovmax.myapplication21.DbManager;
import com.klimovmax.myapplication21.EditActivity;
import com.klimovmax.myapplication21.MainActivity;
import com.klimovmax.myapplication21.NewPost;
import com.klimovmax.myapplication21.R;
import com.klimovmax.myapplication21.ShowLayoutActivity;
import com.klimovmax.myapplication21.utils.MyConstants;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolderData> {
    private List<NewPost> arrayPost;
    private Context context;
    private OnItemClickCustom onItemClickCustom;
    private DbManager dbManager;

    public PostAdapter(List<NewPost> arrayPost, Context context, OnItemClickCustom onItemClickCustom) {
        this.arrayPost = arrayPost;
        this.context = context;
        this.onItemClickCustom = onItemClickCustom;
       this.dbManager = dbManager;

    }

    @NonNull
    @Override
    public ViewHolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ads,parent,false);
        return new ViewHolderData(view,onItemClickCustom);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderData holder, int position)
    {
        holder.setData(arrayPost.get(position));
    }

    @Override
    public int getItemCount() {
        return arrayPost.size();
    }

    public class ViewHolderData extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView tvPriceTel,tvDisc,tvTitle,tvTotalViews;
        private ImageView imAds;
        private LinearLayout edit_layout;
        private ImageButton deleteButton,editButton;
        private OnItemClickCustom onItemClickCustom;

        public ViewHolderData(@NonNull View itemView, OnItemClickCustom onItemClickCustom) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvTotalViews = itemView.findViewById(R.id.tvTotalViews);
            tvPriceTel = itemView.findViewById(R.id.tvPiceTel);
            tvDisc = itemView.findViewById(R.id.tvDisc);
            imAds = itemView.findViewById(R.id.imAds);
            edit_layout = itemView.findViewById(R.id.edit_layout);
            deleteButton = itemView.findViewById(R.id.inDeleteItem);
            editButton=itemView.findViewById(R.id.imEditItem);
            itemView.setOnClickListener(this);
            this.onItemClickCustom = onItemClickCustom;

        }
        public void setData(NewPost newPost)
        {
            if(newPost.getUid().equals(MainActivity.MAUTH))
            {
                edit_layout.setVisibility(View.VISIBLE);

            }
            else
            {
                edit_layout.setVisibility(View.GONE);

            }
            Picasso.get().load(newPost.getImageId()).into(imAds);
            tvTitle.setText(newPost.getTitle());
            String price_tel = "Цена:  "+ newPost.getPrice() + "   Телефон" + " " + newPost.getTel();
            tvPriceTel.setText(price_tel);
            String textDisc;
            if(newPost.getDisc().length()>50){
                textDisc= newPost.getDisc().substring(0,50)  + "...";
            }
            else 
            {
                textDisc = newPost.getDisc();
            }
            tvDisc.setText(textDisc);
            tvTotalViews.setText(newPost.getTotal_views());


            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {

                    deleteDialog(newPost,getAdapterPosition());


                }
            });
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {

                        Intent i = new Intent(context, EditActivity.class);
                        i.putExtra(MyConstants.IMAGE_ID,newPost.getImageId());
                        i.putExtra(MyConstants.IMAGE_ID2,newPost.getImageId2());
                        i.putExtra(MyConstants.IMAGE_ID3,newPost.getImageId3());
                        i.putExtra(MyConstants.PRICE,newPost.getPrice());
                        i.putExtra(MyConstants.TEL,newPost.getTel());
                        i.putExtra(MyConstants.TIME,newPost.getTime());
                        i.putExtra(MyConstants.CAT,newPost.getCat());
                        i.putExtra(MyConstants.TITLE,newPost.getTitle());
                        i.putExtra(MyConstants.UID,newPost.getUid());
                        i.putExtra(MyConstants.DISC,newPost.getDisc());
                        i.putExtra(MyConstants.TOTAL_VIEWS,newPost.getTotal_views());
                        i.putExtra(MyConstants.KEY,newPost.getKey());
                        i.putExtra(MyConstants.EMAIL,newPost.getEmail());
                        i.putExtra(MyConstants.EDIT_STATE,true);
                        context.startActivity(i);




                }
            });

        }

        @Override
        public void onClick(View v)
        {
            NewPost newPost= arrayPost.get(getAdapterPosition());
            dbManager.updateTotalViews(newPost);
            Intent i = new Intent(context, ShowLayoutActivity.class);
            i.putExtra(MyConstants.IMAGE_ID,newPost.getImageId());
            i.putExtra(MyConstants.IMAGE_ID2,newPost.getImageId2());
            i.putExtra(MyConstants.IMAGE_ID3,newPost.getImageId3());
            i.putExtra(MyConstants.TITLE,newPost.getTitle());
            i.putExtra(MyConstants.EMAIL,newPost.getEmail());
            i.putExtra(MyConstants.PRICE,newPost.getPrice());
            i.putExtra(MyConstants.TEL,newPost.getTel());
            i.putExtra(MyConstants.DISC,newPost.getDisc());
            context.startActivity(i);
            onItemClickCustom.onItemSelected(getAdapterPosition());

        }
    }
    private void deleteDialog(final NewPost newPost,int position)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(R.string.delete_title);
        builder.setMessage(R.string.delete_message);
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {


            }
        });
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dbManager.deleteItem(newPost);
                arrayPost.remove(position);
                notifyItemRemoved(position);


            }
        });

        builder.show();
    }

    public interface OnItemClickCustom
    {
         void onItemSelected(int position);
    }
    public void updateAdapter(List<NewPost> listData)
    {
        arrayPost.clear();
        arrayPost.addAll(listData);
        notifyDataSetChanged();

    }
    public void setDbManager(DbManager dbManager)
    {
        this.dbManager =dbManager;

    }
}
