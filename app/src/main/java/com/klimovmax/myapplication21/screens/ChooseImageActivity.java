package com.klimovmax.myapplication21.screens;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.klimovmax.myapplication21.R;
import com.klimovmax.myapplication21.utils.MyConstants;
import com.squareup.picasso.Picasso;

public class ChooseImageActivity extends AppCompatActivity {

    private  String[] uris = new String[3];
    private ImageView imMain,im2,im3;
    private ImageView[] imagesViews = new ImageView[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_image);
        init();
        getMyIntent();

    }
    private void init()
    {
        imMain= findViewById(R.id.mainImage);
        im2= findViewById(R.id.image2);
        im3= findViewById(R.id.image3);
        uris[0]="null";
        uris[1]="null";
        uris[2]="null";
        imagesViews[0]=imMain;
        imagesViews[1]=im2;
        imagesViews[2]=im3;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK  && data != null && data.getData() != null)
        {
           switch (requestCode)
           {
               case 1:
                   uris[0] = data.getData().toString();
                   imMain.setImageURI(data.getData());
                   break;
               case 2:
                   uris[1] = data.getData().toString();
                   im2.setImageURI(data.getData());
                   break;
               case 3:
                   uris[2]=data.getData().toString();
                   im3.setImageURI(data.getData());
                   break;
           }

        }
    }

    public void onClickMainImage(View view)
    {
        getImage(1);

    }
    public void onClickImage2(View view)
    {
        getImage(2);

    }public void onClickImage3(View view)
    {
        getImage(3);

    }public void onClickBack(View view)
    {
        Intent i = new Intent();
        i.putExtra("uriMain",uris[0]);
        i.putExtra("uri2",uris[1]);
        i.putExtra("uri3",uris[2]);
        setResult(RESULT_OK,i);
        finish();

    }
    private void getImage(int index)
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);

        startActivityForResult(intent,index);
    }
    private void getMyIntent()
    {

        Intent i = getIntent();
        if(i != null)
       {



            uris[0] = i.getStringExtra(MyConstants.IMAGE_ID);
            uris[1] = i.getStringExtra(MyConstants.IMAGE_ID2);
            uris[2] = i.getStringExtra(MyConstants.IMAGE_ID3);

            setImages(uris);

        }
    }
    private void setImages(String[] uris)
    {

        for(int i = 0; i < uris.length; i++)
        {
            if(!uris[i].equals("null"))showImages(uris[i],i);


        }



    }
    private void showImages(String uri,int pos)
    {
        if(uri.substring(0,5).equals("http"))
        {
            Picasso.get().load(uri).into(imagesViews[pos]);
        }
        else
        {
            imagesViews[pos].setImageURI(Uri.parse(uri));
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public void onClickDeleteMainImage(View view)
    {
        imMain.setImageResource(android.R.drawable.ic_menu_add);
        uris[0] = "null";

    }

    public void onClickDeleteImage2(View view)
    {
        im2.setImageResource(android.R.drawable.ic_menu_add);
        uris[1] = "null";
    }

    public void onClickDeleteImage3(View view)

    {
        im3.setImageResource(android.R.drawable.ic_menu_add);
        uris[2] = "null";
    }
}