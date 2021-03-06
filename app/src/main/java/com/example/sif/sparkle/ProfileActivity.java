package com.example.sif.sparkle;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;


public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private RoundedImageView profileWrapper,profile;
    private TextView name,pass,mob,email,add1,add2,city,state,pin;
    private View layout_profile;
    private RelativeLayout passLayout;
    private Button changePassword,cancel,cancel1,editProfileButton;
    private EditText oldpass,newpass1,newpass2;
    private TextInputLayout oldpassLayout,newPass1Layout,newPass2Layout;
    private View confirmDialog,editProfileLayout;
    private LayoutInflater inflater;
    private AlertDialog alertDialog;
    private SharedPreferences sharedPreferences;
    private FloatingActionButton editFab,editProfilePic;
    private ProgressDialog pd;
    private Bitmap bitmap;
    private ImageButton editProfilePicButton;
    private ImageView inflateProfileImage;

    private int PICK_IMAGE_REQUEST = 1;
    private final static String CHANGE_PASS_URL="https://techstart.000webhostapp.com/change_pass.php";
    private final static String UPLOAD_IMAGE_URL="https://techstart.000webhostapp.com/upload_image.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        inflater = LayoutInflater.from(this);
        confirmDialog=inflater.inflate(R.layout.change_pass_layout,null);
        editProfileLayout=inflater.inflate(R.layout.profile_picture_layout,null);


        //Initialization
        profile=(RoundedImageView)findViewById(R.id.circleView);
        profileWrapper=(RoundedImageView)findViewById(R.id.circleViewOverlay);
        layout_profile=findViewById(R.id.layout_profile);
        name=(TextView)layout_profile.findViewById(R.id.tv_name);
        email=(TextView)layout_profile.findViewById(R.id.tv_email);
        pass=(TextView)layout_profile.findViewById(R.id.tv_changePassword);
        mob=(TextView)layout_profile.findViewById(R.id.tv_phone);
        add1=(TextView)layout_profile.findViewById(R.id.tv_add1);
        add2=(TextView)layout_profile.findViewById(R.id.tv_add2);
        city=(TextView)layout_profile.findViewById(R.id.tv_city);
        state=(TextView)layout_profile.findViewById(R.id.tv_state);
        pin=(TextView)layout_profile.findViewById(R.id.tv_pin);
        passLayout=(RelativeLayout)layout_profile.findViewById(R.id.rl_passLayout);


        oldpassLayout=(TextInputLayout)confirmDialog.findViewById(R.id.oldpassWrapper);
        newPass1Layout=(TextInputLayout)confirmDialog.findViewById(R.id.newpass1Wrapper);
        newPass2Layout=(TextInputLayout)confirmDialog.findViewById(R.id.newpass2Wrapper);
        changePassword=(Button)confirmDialog.findViewById(R.id.btn_change_pass);
        oldpass=(EditText)confirmDialog.findViewById(R.id.et_oldpass);
        newpass1=(EditText)confirmDialog.findViewById(R.id.et_newpass1);
        newpass2=(EditText)confirmDialog.findViewById(R.id.et_newpass2);
        cancel=(Button)confirmDialog.findViewById(R.id.btn_cancel);
        editFab=(FloatingActionButton)findViewById(R.id.fab_edit_profile);
        editProfilePic=(FloatingActionButton)findViewById(R.id.fab_edit_profileImage);
        pd=new ProgressDialog(this);

        cancel1=(Button)editProfileLayout.findViewById(R.id.btn_cancel);
        editProfileButton=(Button)editProfileLayout.findViewById(R.id.btn_inflate_profile_save);
        editProfilePicButton=(ImageButton)editProfileLayout.findViewById(R.id.ib_inflate_profie_edit);
        inflateProfileImage=(ImageView)editProfileLayout.findViewById(R.id.iv_inflate_profile);

        passLayout.setOnClickListener(this);

        changePassword.setOnClickListener(this);

        editFab.setOnClickListener(this);
        editProfilePic.setOnClickListener(this);

        profileWrapper.setImageResource(R.drawable.white);


        pass.setOnClickListener(this);
        cancel.setOnClickListener(this);
        cancel1.setOnClickListener(this);
        editProfileButton.setOnClickListener(this);
        editProfilePicButton.setOnClickListener(this);

        pd.setMessage("Please wait...");
        sharedPreferences=getApplicationContext()
                .getSharedPreferences(getString(R.string.shared_pref),MODE_PRIVATE);

        if(!sharedPreferences.getString("url","0").equals("0")) {
            String url=sharedPreferences.getString("url","0");
            setProfileImage(url);
        }
        else {
            profile.setImageResource(R.drawable.default_profile_pic);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        sharedPreferences=getApplicationContext()
                .getSharedPreferences(getString(R.string.shared_pref),MODE_PRIVATE);

        name.setText(sharedPreferences.getString("name","ABC"));
        email.setText(sharedPreferences.getString("email","abc@xyz.com"));
        mob.setText(sharedPreferences.getString("contact","0000000000"));
        add1.setText(sharedPreferences.getString("add1","Add1"));
        add2.setText(sharedPreferences.getString("add2","Add2"));
        city.setText(sharedPreferences.getString("city","city"));
        state.setText(sharedPreferences.getString("state","state"));
        pin.setText(sharedPreferences.getString("pin","pin"));
    }

    private void setProfileImage(String url) {

        if(!url.equals("0")){
            Picasso.with(this).invalidate(url);
            Picasso.with(this)
                    .load(url)
                    .placeholder(R.drawable.default_profile_pic) // optional
                    .error(R.drawable.break_profile)// optional
                    .into(profile);
        }
        else {
            profile.setImageResource(R.drawable.default_profile_pic);
        }
    }

    private void loadProfilePicFromNetwork(String url){
        Picasso.with(this)
                .load(url)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .placeholder(R.drawable.default_profile_pic) // optional
                .error(R.drawable.break_profile)// optional
                .into(profile);
    }

    @Override
    public void onClick(View v) {
        int id= v.getId();

        if(id==R.id.btn_change_pass){
            hideKeyboard();
            if(ifValidInput()){
                //change password
                changePass();
                hideKeyboard();
            }
            else {
                Toast.makeText(ProfileActivity.this,"Please enter a valid input",Toast.LENGTH_SHORT).show();
            }
            return;
        }

        if(id==R.id.rl_passLayout||id==R.id.tv_changePassword) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            if(confirmDialog.getParent()!=null)
                ((ViewGroup)confirmDialog.getParent()).removeView(confirmDialog);
            alert.setView(confirmDialog);
            alertDialog = alert.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.setCancelable(false);
            alertDialog.show();

            oldpass.setText("");
            newpass1.setText("");
            newpass2.setText("");
        }

        if(id==R.id.btn_cancel){
            hideKeyboard();
            alertDialog.dismiss();
        }

        if(id==R.id.fab_edit_profile){
            Intent i=new Intent(ProfileActivity.this,EditProfile.class);
            startActivity(i);
        }

        if(id==R.id.fab_edit_profileImage){
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            if(editProfileLayout.getParent()!=null)
                ((ViewGroup)editProfileLayout.getParent()).removeView(editProfileLayout);
            alert.setView(editProfileLayout);
            alertDialog = alert.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.setCancelable(false);
            alertDialog.show();
            Bitmap bitmap = ((BitmapDrawable)profile.getDrawable()).getBitmap();
            inflateProfileImage.setImageBitmap(bitmap);
        }
        if(id==R.id.btn_inflate_profile_save){
            if(bitmap!=null) {
                uploadImage();
                alertDialog.dismiss();
            }
            else {
                Toast.makeText(this,"Select an image first",Toast.LENGTH_SHORT).show();
            }
        }
        if(id==R.id.ib_inflate_profie_edit){
            showFileChooser();
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                inflateProfileImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void uploadImage(){
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this,"Uploading...","Please wait...",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_IMAGE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        profile.setImageBitmap(bitmap);
                        //Showing toast message of the response
                        try {
                            JSONObject jsonObject=new JSONObject(s);
                            if(jsonObject.getString("status").equals("ok")){
                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                editor.putString("url",jsonObject.getString("url"));
                                editor.putString("img_string",jsonObject.getString("img_string"));
                                editor.commit();
                                loadProfilePicFromNetwork(jsonObject.getString("url"));
                            }else{
                                Toast.makeText(ProfileActivity.this, jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(ProfileActivity.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = getStringImage(bitmap);

                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put("image", image);
                params.put("uid", sharedPreferences.getString("uid","-1"));

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    private void changePass() {
        String enteredPass=oldpass.getText().toString().trim();
        String password=sharedPreferences.getString("pass","000");
        if(password.equals("000")){
            alertDialog.dismiss();
            Toast.makeText(ProfileActivity.this,"Some Error Occured",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!password.equals(enteredPass)){
            Toast.makeText(ProfileActivity.this,"Wrong old password",Toast.LENGTH_SHORT).show();
            oldpassLayout.setError("Wrong Password");
            return;
        }
        if(password.equals(enteredPass)){
            alertDialog.dismiss();

            pd.show();
            StringRequest stringRequest=new StringRequest(Request.Method.POST, CHANGE_PASS_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if(jsonObject.getString("status").equals("ok")&&
                                        jsonObject.getString("uid").equals(sharedPreferences.getString("uid",""))) {
                                    SharedPreferences.Editor editor=sharedPreferences.edit();
                                    editor.putString("pass",jsonObject.getString("pass"));
                                    editor.commit();
                                    pd.dismiss();
                                    Toast.makeText(ProfileActivity.this,
                                            "Password changed", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    String err=jsonObject.getString("error");
                                    pd.dismiss();
                                    Toast.makeText(ProfileActivity.this,
                                            err, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                pd.dismiss();
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pd.dismiss();
                    Toast.makeText(ProfileActivity.this,getString(R.string.err_connection),Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String ,String> params=new HashMap<>();
                    params.put("pass",newpass1.getText().toString().trim());
                    params.put("uid",sharedPreferences.getString("uid","-1"));
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);

        }
    }

    private boolean ifValidInput() {
            if(!validateOldPass())
                return false;
            if(!validatePassword())
                return false;
            if(!validateCpass())
                return false;

            return true;
    }

    private boolean validateCpass() {
        String password = newpass1.getText().toString().trim();
        String cpassword=newpass2.getText().toString().trim();

        if(!password.equals(cpassword)){
            newPass2Layout.setError(getString(R.string.err_msg_cpass));
            requestFocus(newpass2);
            return false;
        }
        else {
            newPass2Layout.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePassword() {
        String password = newpass1.getText().toString().trim();

        if(password.isEmpty() || !isValidPassword(password)){
            newPass1Layout.setError(getString(R.string.err_msg_pass));
            requestFocus(newpass1);
            return false;
        }
        else {
            newPass1Layout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateOldPass() {
        String password = oldpass.getText().toString().trim();

        if (password.isEmpty() || !isValidPassword(password)) {
            oldpassLayout.setError(getString(R.string.err_msg_pass));
            requestFocus(oldpass);
            return false;
        } else {
            oldpassLayout.setErrorEnabled(false);
        }
        return true;
    }

    private boolean isValidPassword(String password) {
        return password.length()>=8;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}
