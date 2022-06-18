package com.klimovmax.myapplication21;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.klimovmax.myapplication21.adapter.ImageAdapter;
import com.klimovmax.myapplication21.screens.ChooseImageActivity;
import com.klimovmax.myapplication21.utils.ImagesManager;
import com.klimovmax.myapplication21.utils.MyConstants;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditActivity extends AppCompatActivity {
    private StorageReference mStorageRef;
    private final int MAX_UPLOAD_IMAGE_SIZE = 1920;
    private String[] uploadUri = new String[3];
    private String[] uploadNewUri = new String[3];
    private Spinner spinner;
    private DatabaseReference dRef;
    private FirebaseAuth mAuth;
    private EditText edTitle,edPrice,edTel,edDisc,edEmailAd;
    private boolean edit_state = false;
    private boolean is_image_update = false;
    private String temp_cat = "";
    private String temp_uid = "";
    private String temp_time = "";
    private String temp_key = "";
    private String temp_total_views = "";
    private ProgressDialog pd;
    private int load_image_counter = 0;
    private List<String> imagesUris;
    private ImageAdapter imAdapter;
    private TextView tvImagesCounter;
    private ViewPager vp;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_layout);
        init();
    }
    private void init()
    {
        tvImagesCounter = findViewById(R.id.tvImagesCounter);
        imagesUris=new ArrayList<>();
        vp = findViewById(R.id.view_pager2);
        imAdapter= new ImageAdapter(this);

        vp.setAdapter(imAdapter);
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position)
            {
                String dataText = position + 1 + "/" + imagesUris.size();
                tvImagesCounter.setText(dataText);


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        uploadUri[0] = "null";
        uploadUri[1] = "null";
        uploadUri[2] = "null";
        pd = new ProgressDialog(this);
        pd.setMessage("Загрузка...");
        edTitle=findViewById(R.id.edTitle);
        edEmailAd=findViewById(R.id.edEmailAd);
        edTel=findViewById(R.id.edTel);
        edPrice=findViewById(R.id.edPrice);
        edDisc=findViewById(R.id.edDisc);



        spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.category_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);
        mStorageRef = FirebaseStorage.getInstance().getReference("Images");

        geMyIntent();

    }
    private void geMyIntent()
    {
        if(getIntent() != null)
        {
            Intent i = getIntent();
            edit_state = i.getBooleanExtra(MyConstants.EDIT_STATE,false);
            if(edit_state)setDataAds(i);

        }

    }
    private void setDataAds(Intent i)
    {
       //Picasso.get().load(i.getStringExtra(MyConstants.IMAGE_ID)).into(imItem);
        //NewPost newPost = (NewPost) i.getSerializableExtra(MyConstants.NEW_POST_INTENT);
        edTel.setText(i.getStringExtra(MyConstants.TEL));
        edTitle.setText(i.getStringExtra(MyConstants.TITLE));
        edPrice.setText(i.getStringExtra(MyConstants.PRICE));
        edDisc.setText(i.getStringExtra(MyConstants.DISC));
        temp_cat= i.getStringExtra(MyConstants.CAT);
        temp_key=i.getStringExtra(MyConstants.KEY);
        temp_time= i.getStringExtra(MyConstants.TIME);
        temp_uid= i.getStringExtra(MyConstants.UID);
        temp_total_views= i.getStringExtra(MyConstants.TOTAL_VIEWS);

        uploadUri[0]= i.getStringExtra(MyConstants.IMAGE_ID);
        uploadUri[1]= i.getStringExtra(MyConstants.IMAGE_ID2);
        uploadUri[2]= i.getStringExtra(MyConstants.IMAGE_ID3);
        for(String s: uploadUri)
        {
            if(!s.equals("null")) imagesUris.add(s);
        }
        imAdapter.updateImages(imagesUris);

        String dataText;
        if(imagesUris.size() >0){
            dataText= 1  + "/" + imagesUris.size();

        }
        else
        {
            dataText= 0  + "/" + imagesUris.size();

        }

        tvImagesCounter.setText(dataText);



    }



    private void uploadImage()
    {
        if(load_image_counter< uploadUri.length)
        {
            if (!uploadUri[load_image_counter].equals("null"))
            {

                Bitmap bitMap = null;
                try {
                    bitMap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(uploadUri[load_image_counter]));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                assert bitMap != null;
               // bitMap = ImagesManager.resizeImage(bitMap,MAX_UPLOAD_IMAGE_SIZE);
                bitMap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                byte[] byteArray = out.toByteArray();
                final StorageReference mRef = mStorageRef.child(System.currentTimeMillis() + "_image");
                UploadTask up = mRef.putBytes(byteArray);
                Task<Uri> task = up.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        return mRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                      //  if(task.getResult() == null)return;
                        uploadUri[load_image_counter] = task.getResult().toString();
                        assert uploadUri != null;
                        load_image_counter++;
                        if (load_image_counter < uploadUri.length) {
                            uploadImage();
                        } else {
                            savePost();
                            Toast.makeText(EditActivity.this, "Объявление загружено.", Toast.LENGTH_SHORT).show();
                            finish();
                        }


                    }
                });//.addOnFailureListener(new OnFailureListener() {
                //@Override
                //public void //onFailure(@NonNull Exception e) {

                // }
                //})
            } else {
                load_image_counter++;
                uploadImage();
            }
        }
        else
        {
            savePost();
            finish();
        }
    }
    private void uploadUpdateImage()
    {

        Bitmap bitMap = null;
        if(load_image_counter < uploadUri.length) {
            //1 условие - если ссылка на старой странице равна на новой
            if(uploadUri[load_image_counter].equals(uploadNewUri[load_image_counter]))
            {
                load_image_counter++;
                uploadUpdateImage();
            }
            //2 - если ссылка на второй странице не равна первой и ссылка на новой позиции не null
            else if(!uploadUri[load_image_counter].equals(uploadNewUri[load_image_counter]) &&
                    !uploadNewUri[load_image_counter].equals("null"))
            {
                try {
                    bitMap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(uploadNewUri[load_image_counter]));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            //3 условие - если удаляет старую картинку
            else if(!uploadUri[load_image_counter].equals("null") && uploadNewUri[load_image_counter].equals("null"))
            {
               StorageReference mRef = FirebaseStorage.getInstance().getReferenceFromUrl(uploadUri[load_image_counter]);
               mRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {
                       uploadUri[load_image_counter]="null";
                       load_image_counter++;
                       if(load_image_counter<uploadUri.length){
                           uploadUpdateImage();

                       }
                       else {
                           updatePost();

                       }

                   }
               });

            }
            //


            ByteArrayOutputStream out = new ByteArrayOutputStream();
//            assert bitMap != null;
           // bitMap = ImagesManager.resizeImage(bitMap.compress(Bitmap.CompressFormat.JPEG,100,out));
            bitMap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            byte[] byteArray = out.toByteArray();
            final StorageReference mRef;

            if(!uploadUri[load_image_counter].equals("null"))
            {
                //условие 2 вариант 1 - если сссылка на старой позиции не равна null то перезаписываем старую на новую
                mRef = FirebaseStorage.getInstance().getReferenceFromUrl(uploadUri[load_image_counter]);
            }

            else
            {
                //условие 2 вариант 2 - если сссылка на старой позиции равна null то записываем новую картинку на firebase
                mRef = mStorageRef.child(System.currentTimeMillis() + "_image");
            }


            UploadTask up = mRef.putBytes(byteArray);
            Task<Uri> task = up.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    return mRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    uploadUri[load_image_counter] = task.getResult().toString();
                   load_image_counter++;
                   if(load_image_counter<uploadUri.length){
                       uploadUpdateImage();

                   }
                   else {
                       updatePost();

                   }





                }
            })//.addOnFailureListener(new OnFailureListener() {
                    //@Override
                    //public void //onFailure(@NonNull Exception e) {

                    // }
                    //})
                    ;
        }
        else
        {
            updatePost();

        }

    }
    public void onClickSavePost(View view)
    {
        pd.show();
            if(!edit_state) {
            uploadImage();
        }
        else
        {
            if(is_image_update)
            {
                uploadUpdateImage();
            }
            else
            {
                updatePost();
            }

        }


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 15  && data != null)
        {
            if(resultCode == RESULT_OK)
            {

                is_image_update=true;
                imagesUris.clear();

                for(String s : getUrisFromChoose(data)) {
                    if (!s.equals("null")) imagesUris.add(s);
                }
                imAdapter.updateImages(imagesUris) ;
                String dataText;
                if(imagesUris.size()>0) {
                    dataText = vp.getCurrentItem() + 1 + "/" + imagesUris.size();
                }
                else
                {
                    dataText = 0 + "/" + imagesUris.size();

                }
                tvImagesCounter.setText(dataText);

            }


        }

    }


    private String[] getUrisFromChoose(Intent data) {
        if (edit_state) {
            uploadNewUri[0] = data.getStringExtra("uriMain");
            uploadNewUri[1] = data.getStringExtra("uri2");
            uploadNewUri[2] = data.getStringExtra("uri3");
            return uploadNewUri;
        } else {
            uploadUri[0] = data.getStringExtra("uriMain");
            uploadUri[1] = data.getStringExtra("uri2");
            uploadUri[2] = data.getStringExtra("uri3");
            return uploadUri;
        }


    }
    public void onClickImage(View view)
    {

        Intent i = new Intent(EditActivity.this, ChooseImageActivity.class);

            i.putExtra(MyConstants.IMAGE_ID, uploadUri[0]);
            i.putExtra(MyConstants.IMAGE_ID2, uploadUri[1]);
            i.putExtra(MyConstants.IMAGE_ID3, uploadUri[2]);

        startActivityForResult(i,15);
    }

    private void updatePost()
    {
        dRef = FirebaseDatabase.getInstance().getReference(temp_cat);
        NewPost post = new NewPost();
            post.setImageId(uploadUri[0]);
            post.setImageId2(uploadUri[1]);
            post.setImageId3(uploadUri[2]);
            post.setTitle(edTitle.getText().toString());
            post.setEmail(edEmailAd.getText().toString());
            post.setTel(edTel.getText().toString());
            post.setPrice(edPrice.getText().toString());
            post.setDisc(edDisc.getText().toString());
            post.setKey(temp_key);
            post.setCat(temp_cat);
            post.setTime(temp_time);
            post.setUid(temp_uid);
            post.setTotal_views(temp_total_views);
            dRef.child(temp_key).child("tabla").setValue(post)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(EditActivity.this, "Объявление отредактировано.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });




    }
    private void savePost()
    {
        dRef = FirebaseDatabase.getInstance().getReference(spinner.getSelectedItem().toString());
        mAuth = FirebaseAuth.getInstance();
        //if(mAuth.getUid() != null)
        //{
            String key = dRef.push().getKey();
            NewPost post = new NewPost();

            post.setImageId(uploadUri[0]);
            post.setImageId2(uploadUri[1]);
            post.setImageId3(uploadUri[2]);
            post.setTitle(edTitle.getText().toString());
            post.setTel(edTel.getText().toString());
            post.setPrice(edPrice.getText().toString());
            post.setDisc(edDisc.getText().toString());
            post.setEmail(edEmailAd.getText().toString());
            post.setKey(key);
            post.setCat(spinner.getSelectedItem().toString());
            post.setTime(String.valueOf(System.currentTimeMillis()));
            post.setUid(mAuth.getUid());
            post.setTotal_views("0");

            dRef.child(key).child("tabla").setValue(post);
            Intent i = new Intent();
            i.putExtra("cat",spinner.getSelectedItem().toString());
            setResult(RESULT_OK,i);
        //}

    }

    @Override
    protected void onPause() {
        super.onPause();
        pd.dismiss();
    }
}