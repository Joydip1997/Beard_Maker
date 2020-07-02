package com.androdude.beardmaker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.androdude.beardmaker.Adapters.propsAdapter
import com.androdude.beardmaker.Adapters.propsItemAdapter
import com.androdude.beardmaker.ModelClass.propsClass
import com.androdude.beardmaker.ModelClass.propsItemClass
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import ja.burhanrashid52.photoeditor.PhotoEditor
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {


    lateinit var  mPhotoEditor : PhotoEditor
    val PICK_IMAGE_REQUEST = 100
    val ASK_STORAGE_PERMISSIONS = 101
    val OPEN_CAMERA_REQUEST = 102
    lateinit var uri : Uri
    lateinit var mInterstitialAd: InterstitialAd


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        setAds()

        createDirIfNotExists("/Beard Maker")

        val mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = "ca-app-pub-5483101987186950/1785265594"
        mInterstitialAd.loadAd(AdRequest.Builder().build())


        fab1.setOnClickListener{
            startActivity(Intent(applicationContext,ChooseActivity::class.java).putExtra("is_camera",true))
        }
        fab2.setOnClickListener{
            startActivity(Intent(applicationContext,ChooseActivity::class.java).putExtra("is_gallery",true))
        }
        createImageView(this)
        if(mPhotoEditor != null)
        {
            fab3.setOnClickListener{
                mPhotoEditor.redo()
            }
            fab4.setOnClickListener{
                mPhotoEditor.undo()
            }
            fab5.setOnClickListener{
                if (mInterstitialAd.isLoaded) {
                    mInterstitialAd.show()
                } else {
                    Log.d("TAG", "The interstitial wasn't loaded yet.")
                }
                val rand = Random()
                var n=rand.nextInt()
                n+=1
                val filePath = "/storage/emulated/0/Beard Maker/img"+n+".jpg"

                mPhotoEditor.saveAsFile(filePath,object : PhotoEditor.OnSaveListener
                {
                    override fun onSuccess(imagePath: String) {
                        val bmp = BitmapFactory.decodeFile(filePath)
                        photoEditorView.source.setImageBitmap(bmp)
                        photoEditorView.setOnClickListener {
                            uri = Uri.parse(getImgUri())
                            photoEditorView.source.setImageURI(uri)
                            Toast.makeText(applicationContext,"Photo Restored",Toast.LENGTH_SHORT).show()
                        }
                        Toast.makeText(applicationContext,"Image Saved At Beard Master Folder",Toast.LENGTH_SHORT).show()
                    }

                    override fun onFailure(exception: Exception) {
                        Toast.makeText(applicationContext,exception.toString(),Toast.LENGTH_SHORT).show()
                    }

                })

            }
        }



        uri = Uri.parse(getImgUri())
        photoEditorView.source.setImageURI(uri)


        //Props Item View
        val propList = ArrayList<propsItemClass>()

        val pAdapter = propsItemAdapter(propList,this)
        propsItemView.adapter=pAdapter
        propsItemView.layoutManager= LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)

        pAdapter.setOnItemClickListener(object : propsItemAdapter.onItemClickListener
        {
            override fun getPos(pos: Int) {
                val mIcon = BitmapFactory.decodeResource(getResources(),propList[pos].id)
                mPhotoEditor.addImage(mIcon)

            }

        })

        getPropsItemList(propList,0)
        pAdapter.notifyDataSetChanged()

        //Props View
        val mList = ArrayList<propsClass>()
        val mAdapter = propsAdapter(mList,applicationContext)
        props_rView.adapter=mAdapter
        props_rView.layoutManager= LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)

        getPropsList(mList)


        mAdapter.setOnClickListener(object : propsAdapter.onClickListener
        {
            override fun getPos(pos: Int) {
               val name = mList[pos].title
                when(name.toLowerCase())
                {
                    "beard" ->    {
                        propList.clear()

                        getPropsItemList(propList,0)
                        pAdapter.notifyDataSetChanged()
                    }
                    "glasses" ->    {
                        propList.clear()
                        getPropsItemList(propList,1)
                        pAdapter.notifyDataSetChanged()
                    }
                    "hair" ->    {
                        propList.clear()
                        getPropsItemList(propList,2)
                        pAdapter.notifyDataSetChanged()
                    }

                    "tattoos" ->    {
                        propList.clear()
                        getPropsItemList(propList,3)
                        pAdapter.notifyDataSetChanged()
                    }
                    "extras" ->    {
                        propList.clear()
                        getPropsItemList(propList,4)
                        pAdapter.notifyDataSetChanged()
                    }
                }
            }

        })











    }

    fun setAds()
    {
        MobileAds.initialize(this) {}
        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = "ca-app-pub-3940256099942544/1033173712"
        mInterstitialAd.loadAd(AdRequest.Builder().build())
        if (mInterstitialAd.isLoaded) {
            mInterstitialAd.show()
        } else {
            Log.d("ADD", "The interstitial wasn't loaded yet.")
        }
    }

    private fun getPropsList(mList: ArrayList<propsClass>) {
        mList.add(propsClass("Beard",R.drawable.beard1))
        mList.add(propsClass("Glasses",R.drawable.glass1))
        mList.add(propsClass("Hair",R.drawable.hair1))
        mList.add(propsClass("Tattoos",R.drawable.tattoos1))
        mList.add(propsClass("Extras",R.drawable.extras1))

    }

    private fun getPropsItemList(mList: ArrayList<propsItemClass>,num : Int) {

            when(num)
            {
                0->
                {
                  mList.add(propsItemClass(R.drawable.beard1))
                    mList.add(propsItemClass(R.drawable.beard2))
                    mList.add(propsItemClass(R.drawable.beard3))
                    mList.add(propsItemClass(R.drawable.beard4))
                    mList.add(propsItemClass(R.drawable.beard5))
                    mList.add(propsItemClass(R.drawable.beard6))
                    mList.add(propsItemClass(R.drawable.beard7))
                    mList.add(propsItemClass(R.drawable.beard8))
                    mList.add(propsItemClass(R.drawable.beard9))
                    mList.add(propsItemClass(R.drawable.beard10))
                    mList.add(propsItemClass(R.drawable.beard11))
                    mList.add(propsItemClass(R.drawable.beard12))
                    mList.add(propsItemClass(R.drawable.beard13))
                    mList.add(propsItemClass(R.drawable.beard14))
                    mList.add(propsItemClass(R.drawable.beard15))
                    mList.add(propsItemClass(R.drawable.beard16))
                    mList.add(propsItemClass(R.drawable.beard17))
                    mList.add(propsItemClass(R.drawable.beard18))
                    mList.add(propsItemClass(R.drawable.beard19))
                    mList.add(propsItemClass(R.drawable.beard20))
                    mList.add(propsItemClass(R.drawable.beard21))
                    mList.add(propsItemClass(R.drawable.beard22))
                    mList.add(propsItemClass(R.drawable.beard23))
                    mList.add(propsItemClass(R.drawable.beard24))
                    mList.add(propsItemClass(R.drawable.beard25))
                    mList.add(propsItemClass(R.drawable.beard26))
                    mList.add(propsItemClass(R.drawable.beard27))
                    mList.add(propsItemClass(R.drawable.beard28))
                    mList.add(propsItemClass(R.drawable.beard29))
                    mList.add(propsItemClass(R.drawable.beard30))
                    mList.add(propsItemClass(R.drawable.beard31))
                    mList.add(propsItemClass(R.drawable.beard32))
                    mList.add(propsItemClass(R.drawable.beard33))
                    mList.add(propsItemClass(R.drawable.beard34))
                    mList.add(propsItemClass(R.drawable.beard35))
                }
                1->
                {
                    mList.add(propsItemClass(R.drawable.glass1))
                    mList.add(propsItemClass(R.drawable.glass2))
                    mList.add(propsItemClass(R.drawable.glass3))
                    mList.add(propsItemClass(R.drawable.glass4))
                    mList.add(propsItemClass(R.drawable.glass5))
                    mList.add(propsItemClass(R.drawable.glass6))
                    mList.add(propsItemClass(R.drawable.glass7))
                    mList.add(propsItemClass(R.drawable.glass8))
                    mList.add(propsItemClass(R.drawable.glass9))
                    mList.add(propsItemClass(R.drawable.glass10))
                    mList.add(propsItemClass(R.drawable.glass11))
                    mList.add(propsItemClass(R.drawable.glass12))
                    mList.add(propsItemClass(R.drawable.glass13))
                    mList.add(propsItemClass(R.drawable.glass14))
                    mList.add(propsItemClass(R.drawable.glass15))
                    mList.add(propsItemClass(R.drawable.glass16))
                    mList.add(propsItemClass(R.drawable.glass17))
                    mList.add(propsItemClass(R.drawable.glass18))
                    mList.add(propsItemClass(R.drawable.glass19))
                    mList.add(propsItemClass(R.drawable.glass20))
                }

                2->
                {
                    mList.add(propsItemClass(R.drawable.hair1))
                    mList.add(propsItemClass(R.drawable.hair2))
                    mList.add(propsItemClass(R.drawable.hair3))
                    mList.add(propsItemClass(R.drawable.hair4))
                    mList.add(propsItemClass(R.drawable.hair5))
                    mList.add(propsItemClass(R.drawable.hair6))
                    mList.add(propsItemClass(R.drawable.hair7))
                    mList.add(propsItemClass(R.drawable.hair8))
                    mList.add(propsItemClass(R.drawable.hair9))
                    mList.add(propsItemClass(R.drawable.hair10))
                    mList.add(propsItemClass(R.drawable.hair11))
                    mList.add(propsItemClass(R.drawable.hair12))
                    mList.add(propsItemClass(R.drawable.hair13))
                    mList.add(propsItemClass(R.drawable.hair14))
                    mList.add(propsItemClass(R.drawable.hair15))
                    mList.add(propsItemClass(R.drawable.hair16))
                    mList.add(propsItemClass(R.drawable.hair17))
                    mList.add(propsItemClass(R.drawable.hair18))
                    mList.add(propsItemClass(R.drawable.hair19))
                    mList.add(propsItemClass(R.drawable.hair20))
                    mList.add(propsItemClass(R.drawable.hair21))
                    mList.add(propsItemClass(R.drawable.hair22))
                    mList.add(propsItemClass(R.drawable.hair23))
                    mList.add(propsItemClass(R.drawable.hair24))
                    mList.add(propsItemClass(R.drawable.hair25))
                    mList.add(propsItemClass(R.drawable.hair26))
                    mList.add(propsItemClass(R.drawable.hair27))
                    mList.add(propsItemClass(R.drawable.hair28))
                    mList.add(propsItemClass(R.drawable.hair29))
                    mList.add(propsItemClass(R.drawable.hair30))
                    mList.add(propsItemClass(R.drawable.hair31))
                    mList.add(propsItemClass(R.drawable.hair32))
                    mList.add(propsItemClass(R.drawable.hair35))
                    mList.add(propsItemClass(R.drawable.hair36))
                    mList.add(propsItemClass(R.drawable.hair37))
                    mList.add(propsItemClass(R.drawable.hair37))
                    mList.add(propsItemClass(R.drawable.hair38))
                    mList.add(propsItemClass(R.drawable.hair39))
                    mList.add(propsItemClass(R.drawable.hair40))
                    mList.add(propsItemClass(R.drawable.hair41))
                    mList.add(propsItemClass(R.drawable.hair42))
                    mList.add(propsItemClass(R.drawable.hair43))
                }


                3->
                {
                    mList.add(propsItemClass(R.drawable.tattoos1))
                    mList.add(propsItemClass(R.drawable.tattoos2))
                    mList.add(propsItemClass(R.drawable.tattoos3))
                    mList.add(propsItemClass(R.drawable.tattoos4))
                    mList.add(propsItemClass(R.drawable.tattoos5))
                    mList.add(propsItemClass(R.drawable.tattoos6))
                    mList.add(propsItemClass(R.drawable.tattoos7))
                    mList.add(propsItemClass(R.drawable.tattoos8))
                    mList.add(propsItemClass(R.drawable.tattoos9))
                    mList.add(propsItemClass(R.drawable.tattoos10))
                    mList.add(propsItemClass(R.drawable.tattoos11))
                    mList.add(propsItemClass(R.drawable.tattoos12))
                    mList.add(propsItemClass(R.drawable.tattoos13))
                    mList.add(propsItemClass(R.drawable.tattoos14))
                    mList.add(propsItemClass(R.drawable.tattoos15))
                    mList.add(propsItemClass(R.drawable.tattoos16))
                    mList.add(propsItemClass(R.drawable.tattoos17))
                    mList.add(propsItemClass(R.drawable.tattoos18))
                    mList.add(propsItemClass(R.drawable.tattoos19))
                    mList.add(propsItemClass(R.drawable.tattoos20))
                }

                4->
                {
                    mList.add(propsItemClass(R.drawable.extras1))
                    mList.add(propsItemClass(R.drawable.extras2))
                    mList.add(propsItemClass(R.drawable.extras3))
                    mList.add(propsItemClass(R.drawable.extras4))
                    mList.add(propsItemClass(R.drawable.extras5))
                    mList.add(propsItemClass(R.drawable.extras6))
                    mList.add(propsItemClass(R.drawable.extras7))
                    mList.add(propsItemClass(R.drawable.extras8))
                    mList.add(propsItemClass(R.drawable.extras9))
                    mList.add(propsItemClass(R.drawable.extras10))
                    mList.add(propsItemClass(R.drawable.extras11))
                    mList.add(propsItemClass(R.drawable.extras12))
                    mList.add(propsItemClass(R.drawable.extras13))
                    mList.add(propsItemClass(R.drawable.extras14))
                    mList.add(propsItemClass(R.drawable.extras15))
                    mList.add(propsItemClass(R.drawable.extras16))
                    mList.add(propsItemClass(R.drawable.extras17))
                    mList.add(propsItemClass(R.drawable.extras18))
                    mList.add(propsItemClass(R.drawable.extras19))
                    mList.add(propsItemClass(R.drawable.extras21))
                    mList.add(propsItemClass(R.drawable.extras22))
                    mList.add(propsItemClass(R.drawable.extras23))
                    mList.add(propsItemClass(R.drawable.extras24))
                    mList.add(propsItemClass(R.drawable.extras25))
                    mList.add(propsItemClass(R.drawable.extras26))
                    mList.add(propsItemClass(R.drawable.extras27))
                    mList.add(propsItemClass(R.drawable.extras28))
                    mList.add(propsItemClass(R.drawable.extras29))
                    mList.add(propsItemClass(R.drawable.extras30))
                    mList.add(propsItemClass(R.drawable.extras31))
                    mList.add(propsItemClass(R.drawable.extras32))
                    mList.add(propsItemClass(R.drawable.extras33))
                    mList.add(propsItemClass(R.drawable.extras34))
                    mList.add(propsItemClass(R.drawable.extras35))
                    mList.add(propsItemClass(R.drawable.extras36))
                    mList.add(propsItemClass(R.drawable.extras37))
                    mList.add(propsItemClass(R.drawable.extras38))
                    mList.add(propsItemClass(R.drawable.extras39))
                    mList.add(propsItemClass(R.drawable.extras40))
                    mList.add(propsItemClass(R.drawable.extras41))
                    mList.add(propsItemClass(R.drawable.extras43))

                }
            }

    }



    private fun createImageView(context: Context)
    {
            mPhotoEditor = PhotoEditor.Builder(context, photoEditorView)
            .setPinchTextScalable(true)
                .build()
    }

    private fun storeSharedPrefs(toString: String) {
        val editor = getSharedPreferences(packageName, Context.MODE_PRIVATE).edit()
        editor.putString("imguri", toString)
        editor.apply()
    }

    private fun getImgUri() : String? {
        val prefs =
            getSharedPreferences(packageName, Context.MODE_PRIVATE)

           return prefs.getString("imguri", "No name defined") //"No name defined" is the default value.

    }

    fun decodeBase64(input: String?): Bitmap? {
        val decodedByte = Base64.decode(input, 0)
        return BitmapFactory
            .decodeByteArray(decodedByte, 0, decodedByte.size)
    }




    //Create Directory If Not Exist
    fun createDirIfNotExists(path: String?): Boolean {
        var ret = true
        val file =
            File(Environment.getExternalStorageDirectory(), path)
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e("TravellerLog :: ", "Problem creating Image folder")
                ret = false
            }
        }
        return ret
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(applicationContext,ChooseActivity::class.java))
    }


}
