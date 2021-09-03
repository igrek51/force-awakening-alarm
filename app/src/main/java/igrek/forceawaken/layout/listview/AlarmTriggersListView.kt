package igrek.forceawaken.layout.listview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import igrek.forceawaken.alarm.AlarmTrigger

class AlarmTriggersListView : ListView, AdapterView.OnItemClickListener,
    AdapterView.OnItemLongClickListener {

    private var itemAdapter: AlarmTriggersListAdapter? = null
    private var onClick: ((AlarmTrigger) -> Unit)? = null
    private var onLongClick: ((AlarmTrigger) -> Unit)? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    fun init(
        context: Context,
        onClick: (item: AlarmTrigger) -> Unit,
        onLongClick: (item: AlarmTrigger) -> Unit,
        onMore: (item: AlarmTrigger) -> Unit
    ) {
        this.onClick = onClick
        this.onLongClick = onLongClick
        onItemClickListener = this
        onItemLongClickListener = this
        choiceMode = CHOICE_MODE_SINGLE
        itemAdapter = AlarmTriggersListAdapter(context, null)
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

    fun setItems(items: List<AlarmTrigger>) {
        itemAdapter!!.setDataSource(items)
        invalidate()
    }
}
