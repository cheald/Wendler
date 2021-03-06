package se.johan.wendler.ui.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import se.johan.wendler.R;
import se.johan.wendler.model.ListItem;

/**
 * Adapter for the items in the NavigationDrawer.
 */
public class DrawerAdapter extends BaseAdapter {

    private Context mContext;
    private final ArrayList<ListItem> mListItems;
    private int mSelectedIndex = -1;

    /**
     * Constructor for the adapter.
     */
    public DrawerAdapter(Context context, ArrayList<ListItem> listItems) {
        mListItems = listItems;
        mContext = context;
    }

    /**
     * Return the number of items in the list.
     */
    @Override
    public int getCount() {
        return mListItems.size();
    }

    /**
     * Return the item at a given position.
     */
    @Override
    public Object getItem(int position) {
        return mListItems.get(position);
    }

    /**
     * Return the item id of a given position.
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * Return the view of the item.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.drawer_item, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.text);
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            holder.divider = convertView.findViewById(R.id.divider);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ListItem item = mListItems.get(position);

        holder.text.setText(item.getTitle(mContext));
        holder.icon.setImageDrawable(item.getIcon(mContext));

        holder.text.setTextColor(getTextColor(position));
        holder.icon.setColorFilter(getIconColor(position), PorterDuff.Mode.MULTIPLY);
        holder.divider.setVisibility(item.hasDivider() ? View.VISIBLE : View.GONE);
        return convertView;
    }

    /**
     * Return if a given position is selected. If it's not selected select it.
     */
    public boolean isPositionSelected(int position) {
        boolean isSelected = true;

        if (mSelectedIndex != position) {
            isSelected = false;
            mSelectedIndex = position;
            notifyDataSetChanged();
        }
        return isSelected;
    }

    /**
     * Returns the color to use for the selected item.
     */
    private int getTextColor(int position) {
        return position == mSelectedIndex
                ? mContext.getResources().getColor(R.color.theme_color)
                : mContext.getResources().getColor(R.color.text_title_color);
    }

    /**
     * Returns the color to use for the selected item.
     */
    private int getIconColor(int position) {
        return position == mSelectedIndex
                ? mContext.getResources().getColor(R.color.theme_color)
                : mContext.getResources().getColor(R.color.text_subtitle_color);
    }

    /**
     * ViewHolder used to increase performance.
     */
    private static class ViewHolder {
        TextView text;
        ImageView icon;
        View divider;
    }
}
