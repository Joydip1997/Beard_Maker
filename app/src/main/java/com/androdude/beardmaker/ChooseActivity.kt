package com.androdude.beardmaker



import android.Manifest
import android.R.attr
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.androdude.mp3converter.ModelClass.appMetaData
import com.androdude.mp3converter.ModelClass.appUpdatedData
import com.google.android.gms.ads.*
import com.google.firebase.database.*
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_choose.*
import kotlinx.android.synthetic.main.update_dialog.view.*


class ChooseActivity : AppCompatActivity() {

    //Global Variables
    val PICK_IMAGE_REQUEST = 100
    val ASK_STORAGE_PERMISSIONS = 101
    val OPEN_CAMERA_REQUEST = 102


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose)


        setBannerAds()
        askpermissions()
        checkIfUpdate()

       val mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = "ca-app-pub-5483101987186950/1785265594"
        mInterstitialAd.loadAd(AdRequest.Builder().build())


        open_camera.setOnClickListener {
            if (mInterstitialAd.isLoaded) {
                mInterstitialAd.show()
            } else {
                Log.d("TAG", "The interstitial wasn't loaded yet.")
            }
            takePhoto()
        }
        open_gallery.setOnClickListener {
            if (mInterstitialAd.isLoaded) {
                mInterstitialAd.show()
            } else {
                Log.d("TAG", "The interstitial wasn't loaded yet.")
            }
            takePhoto()
        }

        if (intent.getBooleanExtra("is_camera", false)) {
            getImage()
        }
        if (intent.getBooleanExtra("is_gallery", false)) {
            getImage()
        }


    }


    fun takePhoto()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED )
            {
                getImage()
            }else
            {
                askpermissions()
            }
        }
        else
        {
            getImage()
        }
    }

    //Check The App Version
    fun checkNewVersion(version : String)
    {
        val myref1 = FirebaseDatabase.getInstance().getReference("updates").child("child")
        val myref2 = FirebaseDatabase.getInstance().getReference("updates").child("whatsnew")
        myref1.addValueEventListener(object : ValueEventListener
        {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                for(c in p0.children)
                {
                    val obj = c.getValue(appMetaData::class.java)
                    println(obj!!.app)
                    checkAppDetails(version,obj.app,myref2)


                }

            }

        })
    }

    //Check If Update Is Available
    private fun checkIfUpdate() {
        var version : String ?= null


        try {
            val pInfo: PackageInfo = applicationContext.getPackageManager().getPackageInfo(applicationContext.packageName, 0)
            version = pInfo.versionName
            val versionNum = pInfo.versionCode
            checkNewVersion(version)



        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    //Check The App Details
    private fun checkAppDetails(version: String?, app: String?, myref2: DatabaseReference) {
        Log.i("VER",version)
        if(version!=app)
        {
            myref2.addValueEventListener(object : ValueEventListener
            {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    for(k in p0.children)
                    {
                        val obj = k.getValue(appUpdatedData::class.java)
                        updateDialog(obj!!.t1,obj.t2,obj.t3,obj.link)
                    }
                }

            })


        }
        else
        {
            Toast.makeText(applicationContext,"App Is Updated",Toast.LENGTH_SHORT).show()
        }


    }

    private fun openLinks() {
        google_play.setOnClickListener{
            redirectUrl(resources.getString(R.string.googlePlayStore))
        }
        github.setOnClickListener{
            redirectUrl(resources.getString(R.string.githubUrl))
        }
        youtube.setOnClickListener{
            redirectUrl(resources.getString(R.string.youtube))
        }
        website.setOnClickListener{
            redirectUrl(resources.getString(R.string.website))
        }



    }

    //Redirect To Google PlatStore
    private fun redirectUrl(updateUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }



    //Update Dialog
    fun updateDialog( t1 : String?, t2 : String?, t3 : String?, link : String?)
    {

        val alertDialog = AlertDialog.Builder(this)
        val mView = layoutInflater.inflate(R.layout.update_dialog,null,false)




        alertDialog.setView(mView)
        val AlertDialog = alertDialog.create()


        mView.update_now_bttn.setOnClickListener {
            redirectStore(link!!)
            AlertDialog.dismiss()
        }
        mView.cancel_bttn.setOnClickListener {
            AlertDialog.dismiss()
        }
        mView.t1.setOnClickListener {

        }
        mView.version_num.setOnClickListener {

        }
        mView.view.setOnClickListener {

        }

        mView.update1_tv.setText(t1)
        mView.update2_tv.setText(t2)
        mView.update3_tv.setText(t3)
        AlertDialog.show()


    }

    //Redirect To Google PlatStore
    private fun redirectStore(updateUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }


    //Pick Images From Gallery
    fun getImage() {
        CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .start(this)
    }

    fun setBannerAds()
    {

        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }









    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode === CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode === Activity.RESULT_OK) {
                val resultUri: Uri = result.uri
                storeSharedPrefs(resultUri.toString())
                startActivity(Intent(applicationContext,MainActivity::class.java))
            } else if (resultCode === CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }



    }















    private fun storeSharedPrefs(toString: String) {
        val editor = getSharedPreferences(packageName, Context.MODE_PRIVATE).edit()
        editor.putString("imguri", toString)
        editor.apply()
    }


    //Ask Runtime Permissions
    fun askpermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {

            } else {
                val permissions = arrayOf(
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
                requestPermissions(permissions, ASK_STORAGE_PERMISSIONS)
            }
        } else {

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ASK_STORAGE_PERMISSIONS) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, "Please Enable The Permissions", Toast.LENGTH_SHORT).show();
            }
        }
    }





}
