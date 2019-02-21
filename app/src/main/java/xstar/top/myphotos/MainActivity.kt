package xstar.top.myphotos

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import top.xstar.photolibrary.HelloC
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    val compositeDisposable = CompositeDisposable()

    var photoBitmap: Bitmap? = null
    var imgAdaper: ImgAdapter? = null
    val IMAGES_REQUEST_CODE = 0x233
    val PERMISSION_INSTALL_REQUEST_CODE = 0x234
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Bug54971Workaround.assitActivity(this)
        begPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, requestCode = PERMISSION_INSTALL_REQUEST_CODE)
        images.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, null)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGES_REQUEST_CODE)
        }
        if (Build.VERSION.SDK_INT >= 21) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.navigationBarColor = 0
        } else if (Build.VERSION.SDK_INT >= 19) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        }
        photoBitmap = BitmapFactory.decodeResource(resources, R.mipmap.lam)
//        Flowable.just(0).map { photoBitmap?.pencil() }.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe { it?.let { main_photo.setBitmap(it) } }
        main_photo.layoutParams.width = Const.SCREEN_W
        main_photo.layoutParams.height = Const.SCREEN_H
        photo_transforms.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)//横向
        val tramsList = listOf<PhotoTrans>(PhotoTrans("原图", 0), PhotoTrans("灰度", Const.PHOTO_TRANS_GRAY), PhotoTrans("素描", Const.PHOTO_TRANS_SKETCH), PhotoTrans("铅笔画", Const.PHOTO_TRANS_PENCIL))
        imgAdaper = ImgAdapter(photoBitmap!!)
        imgAdaper?.itemList = tramsList
        imgAdaper?.onItemClickListener = itemClick
        photo_transforms.adapter = imgAdaper
        val dis = Flowable.timer(300, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe {
            val colors = intArrayOf(Color.BLUE, Color.CYAN, Color.DKGRAY, Color.GRAY, Color.RED, Color.GREEN).iterator()
            Log.e("main_photo", String.format("[%d,%d,%d,%d]", main_photo.left, main_photo.top, main_photo.right, main_photo.bottom))
            var p = main_photo?.parent
            val rect = Rect()
            while (p != null) {
                if (p is View) {
                    p.getDrawingRect(rect)
                }
                p = p.parent
            }
        }
        compositeDisposable.add(dis)
        Log.e("JNI", HelloC.hello())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                IMAGES_REQUEST_CODE -> {
                    data?.let {
                        photoBitmap = MediaStore.Images.Media.getBitmap(contentResolver, it.data)
                        main_photo.setBitmap(photoBitmap!!)
                        imgAdaper?.srcBitmap = photoBitmap
                        imgAdaper?.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    class ImgAdapter constructor(bitmap: Bitmap) : BaseAdapter<PhotoTrans>() {
        var srcBitmap: Bitmap? = null
        var imgW: Int = 0

        init {
            layout = R.layout.item_photo_transform
            srcBitmap = bitmap
            imgW = Const.SCREEN_W.div(3)
        }

        override fun onBindView(holder: BaseVH?, position: Int) {
            holder?.find<ImageView>(R.id.img)?.layoutParams?.width = imgW
            holder?.find<ImageView>(R.id.img)?.layoutParams?.height = imgW
            val trans = itemList?.get(position)
            trans?.let {
                Flowable.just(it.transCode).map {
                    val bimap: Bitmap?
                    when (it) {
                        Const.PHOTO_TRANS_GRAY -> bimap = srcBitmap?.gray()
                        Const.PHOTO_TRANS_SKETCH -> bimap = srcBitmap?.sketch(2)
                        Const.PHOTO_TRANS_PENCIL -> bimap = srcBitmap?.pencil(30)
                        else ->
                            bimap = srcBitmap
                    }
                    bimap
                }.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe { it?.let { holder?.find<ImageView>(R.id.img)?.setImageBitmap(it) } }
                holder?.find<TextView>(R.id.name)?.text = it.transName
            }
        }

    }

    val itemClick = object : BaseAdapter.OnItemClickListener {
        override fun onItemClick(adapter: RecyclerView.Adapter<BaseVH>, view: View, position: Int) {
            val img = view.find<ImageView>(R.id.img).drawable
            if (img is BitmapDrawable) {
                main_photo.setBitmap(img.bitmap)
            }
        }
    }

    val colorMatrix = ColorMatrix(floatArrayOf(
            0.9F, 0f, 0.5f, 0f, 0f, //A
            0f, 0.5F, 0f, 0.6f, 0f, //R
            0f, 0f, 0.5F, 0.3f, 0f, //G
            0f, 0f, 0f, 1f, 0f     //B
    ))

    var permissionResult: (String, Boolean) -> Unit = { p, b -> }
    fun begPermissions(permission: String, requestCode: Int, result: (String, Boolean) -> Unit = permissionResult) {
        permissionResult = result
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(arrayOf(permission), requestCode)
                return
            }
        }
        result(permission, true)
    }

}
