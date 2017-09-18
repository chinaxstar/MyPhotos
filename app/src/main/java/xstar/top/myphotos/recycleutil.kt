package xstar.top.myphotos

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * @author: xstar
 * @since: 2017-09-18.
 */
class BaseVH constructor(item: View) : RecyclerView.ViewHolder(item) {
    fun <T : View> find(resId: Int): T {
        return itemView.findViewById(resId) as T
    }
}

open abstract class BaseAdapter<T> : RecyclerView.Adapter<BaseVH>() {
    var itemList: List<T>? = null
    var layout: Int? = 0
    var footLayout: Int? = 0
    var inflate: LayoutInflater? = null
    var hasFooter = false
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BaseVH {
        if (inflate == null) inflate = LayoutInflater.from(parent?.context)
        return BaseVH(inflate!!.inflate(viewType, parent, false))
    }

    override fun onBindViewHolder(holder: BaseVH?, position: Int) {
        if (hasFooter && position == itemCount - 1) {
            holder?.itemView?.setOnClickListener { onFootClickListener?.onItemClick(this, it, position) }
            onFootBind(holder, position)
        } else {
            holder?.itemView?.setOnClickListener { onItemClickListener?.onItemClick(this, it, position) }
            onBindView(holder, position)
        }
    }

    open fun onFootBind(holder: BaseVH?, position: Int) {}
    abstract fun onBindView(holder: BaseVH?, position: Int)

    override fun getItemCount(): Int {
        val size = itemList?.size ?: 0
        if (hasFooter) size.plus(1)
        return size
    }

    var onItemClickListener: OnItemClickListener? = null
    var onFootClickListener: OnItemClickListener? = null

    open interface OnItemClickListener {
        open fun onItemClick(adapter: RecyclerView.Adapter<BaseVH>, view: View, position: Int)
    }

    override fun getItemViewType(position: Int): Int {
        var lay = layout
        if (hasFooter && footLayout != 0 && position == itemCount - 1) lay = footLayout
        return lay!!
    }

}