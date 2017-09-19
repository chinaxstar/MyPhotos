package xstar.top.myphotos

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.ImageView
import android.widget.TextView
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var photoBitmap: Bitmap? = null
    var imgAdaper = lazy { ImgAdapter(photoBitmap!!) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        photoBitmap = BitmapFactory.decodeResource(resources, R.mipmap.sheep)
        Flowable.just(0).map { photoBitmap?.pencil(2) }.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe { it?.let { main_photo.setBitmap(it) } }

        photo_transforms.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)//横向
        val tramsList = listOf<PhotoTrans>(PhotoTrans("原图", 0), PhotoTrans("灰度", Const.PHOTO_TRANS_GRAY), PhotoTrans("素描", Const.PHOTO_TRANS_SKETCH), PhotoTrans("铅笔画", Const.PHOTO_TRANS_PENCIL))
        imgAdaper.value.itemList = tramsList
        photo_transforms.adapter = imgAdaper.value
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
                        Const.PHOTO_TRANS_SKETCH -> bimap = srcBitmap?.sketch(10)
                        Const.PHOTO_TRANS_PENCIL -> bimap = srcBitmap?.pencil(10)
                        else ->
                            bimap = srcBitmap
                    }
                    bimap
                }.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe { it?.let { holder?.find<ImageView>(R.id.img)?.setImageBitmap(it) } }
                holder?.find<TextView>(R.id.name)?.text = it.transName
            }
        }

    }

}
