package com.klimovmax.myapplication21;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.klimovmax.myapplication21.adapter.DataSender;
import com.klimovmax.myapplication21.utils.MyConstants;

import java.util.ArrayList;
import java.util.List;

public class DbManager {
    private Context context;
    private Query mQuery;
    private List<NewPost> newPostList;
    private DataSender dataSender;
    private FirebaseDatabase db;
    private FirebaseStorage fs;
    private int category_ads_counter = 0;
    private String[] category_ads = {"Транспорт","Компьютеры","Смартфоны",
            "Недвижимость","Бытовая техника","Готовый бизнес","Услуги","Разное"};
    private int deleteImageCounter = 0;

    public void  deleteItem(final NewPost newPost)
    {
        StorageReference sRef=null;
        switch (deleteImageCounter)
        {
            case 0:
                if(!newPost.getImageId().equals("null"))
                {
                    sRef= fs.getReferenceFromUrl(newPost.getImageId());
                }
                else
                {
                    deleteImageCounter++;
                    deleteItem(newPost);
                }
                break;
            case 1:
                if(!newPost.getImageId2().equals("null"))
                {
                    sRef= fs.getReferenceFromUrl(newPost.getImageId2());
                }
                else
                {
                    deleteImageCounter++;
                    deleteItem(newPost);
                }
                break;
            case 2:
                if(!newPost.getImageId3().equals("null"))
                {
                    sRef= fs.getReferenceFromUrl(newPost.getImageId3());
                }
                else
                {
                    deleteDBItem(newPost);
                    sRef = null;
                    deleteImageCounter=0;
                }
                break;

        }
        if(sRef == null)return;;
        sRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid)
            {
                deleteImageCounter++;
                if(deleteImageCounter <3)
                {
                    deleteItem(newPost);

                }
                else
                {
                    deleteImageCounter=0;deleteDBItem(newPost);

                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Toast.makeText(context, "Ошибка! Картинка не удалилась", Toast.LENGTH_SHORT).show();

            }
        });
    }
    private void deleteDBItem(NewPost newPost)
    {
        DatabaseReference dbRef= db.getReference(newPost.getCat());
        dbRef.child(newPost.getKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, R.string.item_deleted, Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Ошибка! Обьявление не удалено", Toast.LENGTH_SHORT).show();
            }
        });


    }
    public void updateTotalViews(final NewPost newPost)
    {
        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference(newPost.getCat());
        int total_views;
        try
        {
            total_views = Integer.parseInt(newPost.getTotal_views());

        }
        catch (NumberFormatException e)
        {
            total_views = 0;

        }
        total_views++;
        dRef.child(newPost.getKey()).child("tabla/total_views").setValue(String.valueOf(total_views));

    }
    public void updateTotalEmails(final NewPost newPost)
    {
        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference(newPost.getCat());
        int total_emails;
        try
        {
            total_emails = Integer.parseInt(newPost.getTotal_emails());

        }
        catch (NumberFormatException e)
        {
            total_emails = 0;

        }
        total_emails++;
        dRef.child(newPost.getKey()).child("tabla/total_emails").setValue(String.valueOf(total_emails));

    }
    public void updateTotalCalls(final NewPost newPost)
    {
        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference(newPost.getCat());
        int total_calls;
        try
        {
            total_calls = Integer.parseInt(newPost.getTotal_calls());

        }
        catch (NumberFormatException e)
        {
            total_calls = 0;

        }
        total_calls++;
        dRef.child(newPost.getKey()).child("tabla/total_calls").setValue(String.valueOf(total_calls));

    }
    public DbManager(DataSender dataSender,Context context) {
        this.dataSender = dataSender;
        this.context = context;
        newPostList = new ArrayList<>();
        db = FirebaseDatabase.getInstance();
        fs = FirebaseStorage.getInstance();
    }

    public void getDataFromDb(String cat,String lastTime)
    {
        DatabaseReference dbRef =db.getReference(cat);
        mQuery = dbRef.orderByChild("tabla/time");
        readDataUpdate();

    }

    public void getMyAdsDataFromDb(String uid)
    {

        if(newPostList.size()>0) newPostList.clear();
        DatabaseReference dbRef =db.getReference(category_ads[0]);
        mQuery = dbRef.orderByChild("tabla/uid").equalTo(uid);
        readMyAdsDataUpdate(uid);
        category_ads_counter++;

    }
    public void  readDataUpdate()
    {
        mQuery.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(newPostList.size() >0 ) newPostList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {



                    NewPost newPost = ds.child("tabla").getValue(NewPost.class);
                    newPostList.add(newPost);


                }
                dataSender.onDataRecieved(newPostList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void  readMyAdsDataUpdate(final String uid)
    {
        mQuery.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

                for(DataSnapshot ds : dataSnapshot.getChildren())
                {

                    NewPost newPost = ds.child("tabla").getValue(NewPost.class);
                    newPostList.add(newPost);


                }

                if(category_ads_counter>6){
                    dataSender.onDataRecieved(newPostList);
                    newPostList.clear();
                    category_ads_counter=0;
                }
                else
                {
                    DatabaseReference dbRef =db.getReference(category_ads[category_ads_counter]);
                    mQuery = dbRef.orderByChild("tabla/uid").equalTo(uid);
                    readMyAdsDataUpdate(uid);
                    category_ads_counter++;

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
