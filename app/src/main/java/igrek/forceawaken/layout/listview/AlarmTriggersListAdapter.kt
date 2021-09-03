package igrek.forceawaken.layout.listview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import igrek.forceawaken.R
import igrek.forceawaken.alarm.AlarmTrigger
import java.util.*

class AlarmTriggersListAdapter internal constructor(
    context: Context,
    _dataSource: List<AlarmTrigger>?
) : ArrayAdapter<AlarmTrigger>(context, 0, ArrayList()) {

    private var dataSource: List<AlarmTrigger>? = null
    private val inflater: LayoutInflater
    private var onMore: ((AlarmTrigger) -> Unit)? = null

    init {
        var dataSource = _dataSource
        if (dataSource == null)
            dataSource = ArrayList()
        this.dataSource = dataSource
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    fun setDataSource(dataSource: List<AlarmTrigger>) {
        this.dataSource = dataSource
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): AlarmTrigger? {
        return dataSource?.get(position)
    }

    override fun getCount(): Int {
        return dataSource?.size ?: 0
    }

    override fun getItemId(position: Int): Long {
        if (position < 0)
            return -1
        return if (position >= dataSource!!.size) -1 else position.toLong()
    }

    fun setOnMoreListener(onMore: (item: AlarmTrigger) -> Unit) {
        this.onMore = onMore
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = dataSource!![position]
        return createItemView(item, parent)
    }

    private fun createItemView(item: AlarmTrigger, parent: ViewGroup): View {
        val itemView = inflater.inflate(R.layout.list_item_alarm_config, parent, false)
        val itemTitleLabel = itemView.findViewById<TextView>(R.id.itemAlarmTitleLabel)

        itemTitleLabel.text = item.toString()

        itemView.findViewById<ImageButton>(R.id.itemAlarmMoreButton).setOnClickListener {
            onMore?.invoke(item)
        }
        return itemView
    }
}