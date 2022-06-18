package com.klimovmax.myapplication21;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.klimovmax.myapplication21.accountHelper.AccountHelper;
import com.klimovmax.myapplication21.adapter.DataSender;
import com.klimovmax.myapplication21.adapter.PostAdapter;
import com.klimovmax.myapplication21.utils.MyConstants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


// 37  - oshibka
//44
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView nav_view;
    private DrawerLayout drawerLayout;
    private FirebaseAuth mAuth;
    private TextView userEmail;
    private AlertDialog dialog;
    private Toolbar toolbar;
    private PostAdapter.OnItemClickCustom onItemClickCustom;
    private RecyclerView rcView;
    private PostAdapter postAdapter;
    private DataSender dataSender;
    private DbManager dbManager;
    public static String MAUTH = "";
    private String current_cat = "myAds";
    private  final int EDIT_RES = 12;
    private AdView adView;
    private AccountHelper accountHelper;
    private ImageView imPhoto;







    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        adAds();
        init();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account!=null) Picasso.get().load(account.getPhotoUrl()).into(imPhoto);
        if(adView != null)
        {
            adView.resume();
        }
        if(current_cat.equals("myAds"))
        {
            dbManager.getMyAdsDataFromDb(mAuth.getUid());

        }
        else
        {
            dbManager.getDataFromDb(current_cat,"1");
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if(adView != null)
        {
            adView.pause();
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if(adView != null)
        {
            adView.destroy();
        }
    }

    private void init()
    {

        setOnItemClickCustom();
        rcView = findViewById(R.id.rcView);
        rcView.setLayoutManager(new LinearLayoutManager(this));
        List<NewPost> arrayPost = new ArrayList<>();
        postAdapter = new PostAdapter(arrayPost,this,onItemClickCustom);
        rcView.setAdapter(postAdapter);

        nav_view = findViewById(R.id.nav_view);
        imPhoto = nav_view.getHeaderView(0).findViewById(R.id.imPhoto);
        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.main_menu);
        onToolbarItemClick();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.toogle_open,R.string.toogle_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        nav_view.setNavigationItemSelectedListener(this);
        userEmail = nav_view.getHeaderView(0).findViewById(R.id.tvEmail);
        mAuth = FirebaseAuth.getInstance();
        accountHelper = new AccountHelper(mAuth,this);

        // цвет Ввстроенного меню
        Menu menu = nav_view.getMenu();
        MenuItem categoryAccountItem =menu.findItem(R.id.accountCatId);
        SpannableString sp = new SpannableString(categoryAccountItem.getTitle());
        sp.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)),0,sp.length(),0);
        categoryAccountItem.setTitle(sp);




        getDataDB();
        dbManager= new DbManager(dataSender,this);

        postAdapter.setDbManager(dbManager);


    }


    private void getDataDB()
    {
        dataSender = new DataSender() {
            @Override
            public void onDataRecieved(List<NewPost> listData)
            {
                Collections.reverse(listData);
                postAdapter.updateAdapter(listData);

            }
        };

    }

    private void setOnItemClickCustom()
    {
        onItemClickCustom = new PostAdapter.OnItemClickCustom() {
            @Override
            public void onItemSelected(int position) {

            }
        };
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case EDIT_RES:
                if( resultCode == RESULT_OK && data != null)
            {
                current_cat=data.getStringExtra("cat");

            }break;
            case AccountHelper.GOOGLE_SIGN_IN_CODE:
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    if(account!=null){
                        Picasso.get().load(account.getPhotoUrl()).into(imPhoto);
                        accountHelper.signInFirebaseGoogle(account.getIdToken(),0);
                    }

                } catch (ApiException e)  {
                    e.printStackTrace();
                }

                break;
            case AccountHelper.GOOGLE_SIGN_IN_LINK_CODE:
                Task<GoogleSignInAccount> task2 = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    GoogleSignInAccount account = task2.getResult(ApiException.class);
                    if(account!=null)accountHelper.signInFirebaseGoogle(account.getIdToken(),1);
                } catch (ApiException e) {
                    e.printStackTrace();
                }

                break;
        }

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        getUserData();

    }


    public void getUserData()
    {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null)
        {
            userEmail.setText(currentUser.getEmail());
            MAUTH = mAuth.getUid();
        }
        else
        {
            userEmail.setText(R.string.sign_in_our_up);
            MAUTH = mAuth.getUid();
        }

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        int id = item.getItemId();
        switch (id) {
            case R.id.id_my_ads:
                current_cat="myAds";
                dbManager.getMyAdsDataFromDb(mAuth.getUid());
                break;
               //case R.id.id_dif:
                //current_cat= MyConstants.DIF_CAT;
               // dbManager.getDataFromDbDif();
                //break;
            case R.id.id_cars_ads:
                current_cat="Транспорт";
                dbManager.getDataFromDb(current_cat,"0");
                break;
            case R.id.id_business_ads:
                current_cat="Готовый бизнес";
                dbManager.getDataFromDb(current_cat,"0");
                break;
            case R.id.id_uslugi_ads:
                current_cat="Услуги";
                dbManager.getDataFromDb(current_cat,"0");
                break;
            case R.id.id_house_ads:
                current_cat="Недвижимость";
                dbManager.getDataFromDb(current_cat,"0");
                break;
            case R.id.id_dm_ads:
                current_cat="Бытовая техника";
                dbManager.getDataFromDb(current_cat,"0");
                break;
            case R.id.id_pc_ads:
                current_cat="Компьютеры";
                dbManager.getDataFromDb(current_cat,"0");
                break;
            case R.id.id_smartphone_ads:
                current_cat="Смартфоны";
                dbManager.getDataFromDb(current_cat,"0");
                break;



            case R.id.id_sign_in:
                signUpDialog(R.string.sign_in, R.string.sign_in_button,R.string.google_sign_in, 1);
                break;
            case R.id.id_sign_up:
                signUpDialog(R.string.sign_up, R.string.sign_up_button,R.string.google_sign_up, 0);
                break;
            case R.id.id_sign_out:
                accountHelper.signOut();
                imPhoto.setImageResource(android.R.color.transparent);
                break;

        }
        return true;

    }

    private void signUpDialog(int title, int buttonTitle,int bTitle, final int index)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.sign_up_layout, null);
        dialogBuilder.setView(dialogView);
        TextView titleTextView = dialogView.findViewById(R.id.tvAlertTitle);
        TextView tvMessage = dialogView.findViewById(R.id.tvMessage);
        titleTextView.setText(title);
        Button bSignUpEmail = dialogView.findViewById(R.id.buttonSignUp);
        SignInButton bSignUpGoogle = dialogView.findViewById(R.id.bSignGoogle);
        Button bForgetPassword = dialogView.findViewById(R.id.bForgetPassword);
        switch (index){
            case 0:
                bForgetPassword.setVisibility(View.GONE);
                break;
            case 1:
                bForgetPassword.setVisibility(View.VISIBLE);
                break;
        }
        EditText edEmail = dialogView.findViewById(R.id.edEmail);
        EditText edPassword = dialogView.findViewById(R.id.edPassword);
        bSignUpEmail.setText(buttonTitle);
        bSignUpEmail.setOnClickListener(v -> {
            if (index == 0) {
                accountHelper.signUp(edEmail.getText().toString(), edPassword.getText().toString());
            } else {
                accountHelper.signIn(edEmail.getText().toString(),edPassword.getText().toString());
            }
            dialog.dismiss();

        });
        bSignUpGoogle.setOnClickListener(v -> {

            if(mAuth.getCurrentUser() !=null){
                dialog.dismiss();
               return;
            }else {

                accountHelper.signInGoogle(AccountHelper.GOOGLE_SIGN_IN_CODE);

            }
            dialog.dismiss();

        });
        dialog = dialogBuilder.create();
        dialog.show();


        bForgetPassword.setOnClickListener(v -> {

            if(edPassword.isShown()){
            edPassword.setVisibility(View.GONE);
            bSignUpEmail.setVisibility(View.GONE);
            bSignUpGoogle.setVisibility(View.GONE);
            titleTextView.setText(R.string.forget_password);
            bForgetPassword.setText(R.string.sent_reset_password);
            tvMessage.setVisibility(View.VISIBLE);
            tvMessage.setText(R.string.forget_password_message);
            } else
            {
                if(!edEmail.getText().toString().equals(""))
                {
                FirebaseAuth.getInstance().sendPasswordResetEmail(edEmail.getText().toString())
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(MainActivity.this, R.string.email_is_sent, Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                            else
                            {
                                Toast.makeText(MainActivity.this, "Ошибка.", Toast.LENGTH_SHORT).show();
                            }
                        });
                }else
                {
                    Toast.makeText(MainActivity.this, "Email пустой!", Toast.LENGTH_SHORT).show();
                }


            }




        });
        //dialog = dialogBuilder.create();
        if(dialog.getWindow() !=null) dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();


    }


    private void adAds()
    {
        MobileAds.initialize(this);
        adView=findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

    }
    private void onToolbarItemClick()
    {
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.new_Ad){
                    if(mAuth.getCurrentUser() != null){
                        //if(mAuth.getCurrentUser().isEmailVerified()){
                        Intent i = new Intent(MainActivity.this,EditActivity.class);
                        startActivityForResult(i,EDIT_RES);
                        // }
                        // else
                        //  {
                        //  accountHelper.showDialogNotVerified(R.string.alert,R.string.email_not_verified);

                        // }
                    }


                }
                return false; 
            }
        });
    }

}
