package scut.carson_ho.timeaxle_view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Carson_Ho on 17/6/8.
 */
public class MyAdapterTwo extends RecyclerView.Adapter {
    private LayoutInflater inflater;
    private int itemWidth;
    private ArrayList<HashMap<String, Object>> listItem;
    private static final int TYPE_TOP = 0x0000;
    private static final int TYPE_NORMAL= 0x0001;

    //构造函数，传入数据
    public MyAdapterTwo(Context context, ArrayList<HashMap<String, Object>> listItem,int itemWidth) {
        inflater = LayoutInflater.from(context);
        this.itemWidth = itemWidth;
        this.listItem = listItem;
    }


    //定义Viewholder
    class MyViewholder extends RecyclerView.ViewHolder  {
        private TextView Title, ItemtextTime,tvTopLine,tvBottomLine;

        public MyViewholder(View root) {
            super(root);
            Title = (TextView) root.findViewById(R.id.Itemtitle);
            ItemtextTime = (TextView) root.findViewById(R.id.ItemtextTime);
            tvTopLine = (TextView) root.findViewById(R.id.tvTopLine);
            tvBottomLine = (TextView) root.findViewById(R.id.tvBottomLine);
        }

    }
    //重写onCreateViewHolder方法，返回一个自定义的ViewHolder
    @Override
    public MyViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_cell_two, parent, false);
        view.getLayoutParams().width = itemWidth;//设置单个条目宽度
        MyViewholder holder = new MyViewholder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        MyViewholder vh = (MyViewholder) holder;
        if (getItemViewType(position) == TYPE_TOP) {
            // 第一行头的竖线不显示
            vh.tvTopLine.setVisibility(View.INVISIBLE);
        } else if (getItemViewType(position) == TYPE_NORMAL) {
            vh.tvTopLine.setVisibility(View.VISIBLE);
        }
        //最后一项时，竖线不再显示
        if (position == listItem.size() - 1) {
            vh.tvBottomLine.setVisibility(View.INVISIBLE);
        }else {
            vh.tvBottomLine.setVisibility(View.VISIBLE);
        }
        // 绑定数据到ViewHolder里面
        vh.Title.setText((String) listItem.get(position).get("ItemTitle"));
        vh.ItemtextTime.setText((String) listItem.get(position).get("ItemText"));
        vh.Title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick((String) listItem.get(position).get("ItemTitle"),0);
            }
        });

    }

    //返回Item数目
    @Override
    public int getItemCount() {
        return listItem.size();
    }
    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_TOP;
        }
        return TYPE_NORMAL;
    }
    private OnItemClickListener mClickListener;
    public interface OnItemClickListener {
        void onItemClick(String view,int posion);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mClickListener = listener;
    }
}
