package com.example.newproj;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.MediaStoreSignature;
import com.bumptech.glide.signature.ObjectKey;
import com.example.newproj.models.CurrentUser;
import com.example.newproj.models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.FileOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class UserScreenActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;
    private TextView textDog, textType, textAge, textEmail, textUserName,textAddress;
    private String name;
    private ImageView editButton,profilePic;
    private Uri image_uri;
    String cameraPermissions[];
    String storagePermissions[];
    ProgressDialog pd;
    StorageReference storageRefrence;
    String imagePath,oldPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myprofile);

        textDog = findViewById(R.id.dogName);
        textType = findViewById(R.id.typeName);
        textAge = findViewById(R.id.profileAge);
        textEmail = findViewById(R.id.profileEmail);
        textAddress=findViewById(R.id.myAddress);
        textUserName = findViewById(R.id.userName);
        editButton = findViewById(R.id.editProfile);
        profilePic = findViewById(R.id.userImage);

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        pd = new ProgressDialog(this);
        pd.setMessage("Uploading Photo...");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        storageRefrence = FirebaseStorage.getInstance().getReference();

        DocumentReference user = db.collection("users").document(CurrentUser.currentUserEmail);
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task< DocumentSnapshot > task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    textUserName.setText(doc.get("Name").toString() + " " + doc.get("LastName").toString());
                    textAge.setText(doc.get("Age").toString());
                    textEmail.setText(doc.get("Email").toString());
                    textDog.setText(doc.get("DogName").toString());
                    textAddress.setText(doc.get("Address").toString());
                    textType.setText(doc.get("DogType").toString());
                    oldPath = doc.get("Image").toString();
                    StorageReference storageRef;
                    storageRef = storageRefrence.child(doc.get("Image").toString());
                    Glide.with(UserScreenActivity.this)
                           .load(storageRef)
                          .into(profilePic);

                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToEditProfile();
            }
        });


      profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePicDialog();
            }
        });


    }

   private void pickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Temp pic");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Temp Description");
        image_uri = UserScreenActivity.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent,IMAGE_PICK_CAMERA_CODE);

    }

    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,IMAGE_PICK_GALLERY_CODE);
    }


    private boolean checkStoragePermissions(){
       boolean result = ContextCompat.checkSelfPermission(UserScreenActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
               == (PackageManager.PERMISSION_GRANTED);
       return result;
   }

   private void requestStoragePermission(){
       ActivityCompat.requestPermissions(UserScreenActivity.this,storagePermissions,STORAGE_REQUEST_CODE);
   }

    private boolean checkCameraPermissions(){
        boolean result1 = ContextCompat.checkSelfPermission(UserScreenActivity.this,Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);

        boolean result2 = ContextCompat.checkSelfPermission(UserScreenActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);

        return result1&&result2;
    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(UserScreenActivity.this,cameraPermissions,CAMERA_REQUEST_CODE);
    }

    private void showImagePicDialog(){
        String options[] = {"Camera","Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(UserScreenActivity.this);
        builder.setTitle("Pick Image");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0){
                    if(!checkCameraPermissions()){
                        requestCameraPermission();
                   }
                   else{
                        pickFromCamera();
                    }
                }
                else if(which == 1){
                    if(!checkStoragePermissions()){
                        requestStoragePermission();
                    }
                    else{
                        pickFromGallery();
                    }
                }
            }
        });
        builder.create().show();
    }

    private void goToEditProfile() {
        Intent intent = new Intent(this, EditProfileActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
       switch(requestCode){
           case CAMERA_REQUEST_CODE:{
               if(grantResults.length>0){
                   boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                   boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                   if(cameraAccepted && writeStorageAccepted){
                       pickFromCamera();
                   }
                   else{
                       Toast.makeText(UserScreenActivity.this,"Please enable camera and storage",Toast.LENGTH_SHORT).show();
                   }
               }
           }
           case STORAGE_REQUEST_CODE:{
               if(grantResults.length>0){
                   boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                   if(writeStorageAccepted){
                       pickFromGallery();
                   }
                   else{
                       Toast.makeText(UserScreenActivity.this,"Please enable storage",Toast.LENGTH_SHORT).show();
                   }
               }
           }
       }
       super.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }


    @Override
    protected void onActivityResult(final int requestCode, int resultCode, @Nullable final Intent data) {
        if(resultCode == RESULT_OK){
            StorageReference delete = storageRefrence.child(oldPath);
            delete.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(requestCode == IMAGE_PICK_GALLERY_CODE){
                        image_uri = data.getData();
                        uploadProfilePhoto(image_uri);

                    }
                    if(requestCode == IMAGE_PICK_CAMERA_CODE){
                        Toast.makeText(UserScreenActivity.this,"need to upload now...",Toast.LENGTH_SHORT).show();
                        uploadProfilePhoto(image_uri);
                    }
                }
            });

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfilePhoto(Uri uri) {
        pd.show();
        imagePath = uri.getPath();
        final StorageReference storageRef = storageRefrence.child(CurrentUser.currentUserEmail+"/"+imagePath);
        storageRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while(!uriTask.isSuccessful());
                final Uri downloadUri = uriTask.getResult();
                if(uriTask.isSuccessful()){
                    HashMap<String,Object> results = new HashMap<>();
                    results.put("Image",CurrentUser.currentUserEmail+"/"+imagePath);
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("users").document(CurrentUser.currentUserEmail).update(results).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pd.dismiss();
                            Toast.makeText(UserScreenActivity.this,"Image Updated",Toast.LENGTH_SHORT).show();
                            Glide.with(UserScreenActivity.this)
                                    .load(storageRef)
                                    .into(profilePic);
                            Intent intent = new Intent(UserScreenActivity.this, UserScreenActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    Toast.makeText(UserScreenActivity.this,"Error Updating...",Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                else{
                    pd.dismiss();
                    Toast.makeText(UserScreenActivity.this,"error",Toast.LENGTH_SHORT);
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(UserScreenActivity.this,e.getMessage(),Toast.LENGTH_SHORT);
                    }
                });
    }
    private String getRightAngleImage(String photoPath) {

        try {
            ExifInterface ei = new ExifInterface(photoPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int degree = 0;

            switch (orientation) {
                case ExifInterface.ORIENTATION_NORMAL:
                    degree = 0;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                case ExifInterface.ORIENTATION_UNDEFINED:
                    degree = 0;
                    break;
                default:
                    degree = 90;
            }

            return rotateImage(degree,photoPath);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return photoPath;
    }

    private String rotateImage(int degree, String imagePath){

        if(degree<=0){
            return imagePath;
        }
        try{
            Bitmap b= BitmapFactory.decodeFile(imagePath);

            Matrix matrix = new Matrix();
            if(b.getWidth()>b.getHeight()){
                matrix.setRotate(degree);
                b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(),
                        matrix, true);
            }

            FileOutputStream fOut = new FileOutputStream(imagePath);
            String imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
            String imageType = imageName.substring(imageName.lastIndexOf(".") + 1);

            FileOutputStream out = new FileOutputStream(imagePath);
            if (imageType.equalsIgnoreCase("png")) {
                b.compress(Bitmap.CompressFormat.PNG, 100, out);
            }else if (imageType.equalsIgnoreCase("jpeg")|| imageType.equalsIgnoreCase("jpg")) {
                b.compress(Bitmap.CompressFormat.JPEG, 100, out);
            }
            fOut.flush();
            fOut.close();

            b.recycle();
        }catch (Exception e){
            e.printStackTrace();
        }
        return imagePath;
    }






}


