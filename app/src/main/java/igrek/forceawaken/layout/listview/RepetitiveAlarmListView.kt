package igrek.forceawaken.layout.listview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import igrek.forceawaken.alarm.RepetitiveAlarm

class RepetitiveAlarmListView : ListView, AdapterView.OnItemClickListener,
    AdapterView.OnItemLongClickListener {

    private var itemAdapter: RepetitiveAlarmListAdapter? = null
    private var onClick: ((RepetitiveAlarm) -> Unit)? = null
    private var onLongClick: ((RepetitiveAlarm) -> Unit)? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    fun init(
        context: Context,
        onClick: (item: RepetitiveAlarm) -> Unit = {},
        onLongClick: (item: RepetitiveAlarm) -> Unit = {},
        onMore: (item: RepetitiveAlarm) -> Unit = {},
    ) {
        this.onClick = onClick
        this.onLongClick = onLongClick
        onItemClickListener = this
        onItemLongClickListener = this
        choiceMode = CHOICE_MODE_SINGLE
        itemAdapter = RepetitiveAlarmListAdapter(context, null)
        itemAdapter!!.setOnMoreListener(onMore)
        adapter = itemAdapter
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val item = itemAdapter!!.getItem(position)
        onClick?.invoke(item!!)
    }

    override fun onItemLongClick(
        parent: AdapterView<*>,
        view: View,
        position: Int,
        id: Long
    ): Boolean {
        val item = itemAdapter!!.getItem(position)
        onLongClick?.invoke(item!!)
        return true
    }

    fun setItems(items: List<RepetitiveAlarm>) {
        itemAdapter!!.setDataSource(items)
        invalidate()
    }
}
