package com.klimovmax.myapplication21;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.klimovmax.myapplication21.adapter.ImageAdapter;
import com.klimovmax.myapplication21.utils.MyConstants;

import java.util.ArrayList;
import java.util.List;

public class ShowLayoutActivity extends AppCompatActivity {
    private TextView tvTitle, tvPrice, tvTel, tvDisc,tvEmail;
    private ImageView imMain;
    private List<String> imagesUris;
    private ImageAdapter imAdapter;
    private TextView tvImagesCounter;
    private String tel;
    private Integer value;
    private EditText edTel;
    private DbManager dbManager;
   ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_layout_activity);
        init();
    }

    private void init() {

        imagesUris = new ArrayList<>();
        ViewPager vp = findViewById(R.id.view_pager);
        imAdapter = new ImageAdapter(this);
        dbManager = new DbManager(null,this);

        vp.setAdapter(imAdapter);
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                String dataText = position + 1 + "/" + imagesUris.size();
                tvImagesCounter.setText(dataText);


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tvImagesCounter = findViewById(R.id.tvImagesCounter);
        tvTitle = findViewById(R.id.tvTitle1);
        tvTel = findViewById(R.id.tvTel1);
        tvPrice = findViewById(R.id.tvPrice1);
        tvEmail = findViewById(R.id.tvEmail);
        tvDisc = findViewById(R.id.tvDisc1);
        imMain = findViewById(R.id.imageViewPager);
        // if(getIntent() != null)
        // {
        Intent i = getIntent();
//        NewPost newPost = (NewPost)i.getSerializableExtra(MyConstants.NEW_POST_INTENT);

        tvTitle.setText(i.getStringExtra(MyConstants.TITLE));
        tvTel.setText(i.getStringExtra(MyConstants.TEL));
        tvEmail.setText(i.getStringExtra(MyConstants.EMAIL));
        tvPrice.setText(i.getStringExtra(MyConstants.PRICE));
        tvDisc.setText(i.getStringExtra(MyConstants.DISC));

        NewPost post = new NewPost();
        post.setTel(tvTel.getText().toString());
        tel = post.getTel();


        String[] images = new String[3];
        images[0] = i.getStringExtra(MyConstants.IMAGE_ID);
        images[1] = i.getStringExtra(MyConstants.IMAGE_ID2);
        images[2] = i.getStringExtra(MyConstants.IMAGE_ID3);


        for (String s : images) {
            if (!s.equals("null")) imagesUris.add(s);
        }
        imAdapter.updateImages(imagesUris);
        String dataText;
        if (imagesUris.size() > 0) {
            dataText = 1 + "/" + imagesUris.size();

        } else {
            dataText = 0 + "/" + imagesUris.size();

        }

        tvImagesCounter.setText(dataText);

        //Picasso.get().load(i.getStringExtra(MyConstants.IMAGE_ID)).into(imMain);


        //}

    }

    public void onClickCall(View view) {


        String telTemp = "tel:" + tel;
        Intent iCall = new Intent(Intent.ACTION_DIAL);
        iCall.setData(Uri.parse(telTemp));
        startActivity(iCall);

    }

    public void onClickSendEmail(View view){
        NewPost post = new NewPost();
        post.setEmail(tvEmail.getText().toString());
        //dbManager.updateTotalEmails();
        Intent iEmail = new Intent(Intent.ACTION_SEND);
        iEmail.setType("message/rfc822");
        iEmail.putExtra(Intent.EXTRA_EMAIL,new String[]{post.getEmail()});
        iEmail.putExtra(Intent.EXTRA_SUBJECT,"Объявление в MLG ADS");
        iEmail.putExtra(Intent.EXTRA_TEXT,"Мне интересно Ваше объявление!");

        try {
            startActivity(Intent.createChooser(iEmail,"Open with"));
        } catch (ActivityNotFoundException e)
        {
            Toast.makeText(this,"У Вас нет приложения для отправки письма.", Toast.LENGTH_SHORT).show();
        }

    }

}