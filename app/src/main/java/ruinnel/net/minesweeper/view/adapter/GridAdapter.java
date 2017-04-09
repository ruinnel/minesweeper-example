package ruinnel.net.minesweeper.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import ruinnel.net.minesweeper.R;
import ruinnel.net.minesweeper.bean.Cell;
import ruinnel.net.minesweeper.bean.Event;
import rx.Observable;
import rx.subjects.PublishSubject;

import java.util.Iterator;
import java.util.List;

/**
 * Created by ruinnel on 2017. 4. 8..
 */
public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> {
  private static final String TAG = GridAdapter.class.getSimpleName();

  private final Context mContext;
  private final PublishSubject<Event> mSubject;
  private List<Cell> mDatas;
  private boolean mShowAll;

  public GridAdapter(Context context, List<Cell> datas) {
    this.mContext = context;
    this.mDatas = datas;
    this.mSubject = PublishSubject.create();
    this.mShowAll = false;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cell, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    Cell item = mDatas.get(position);
    holder.btnCell.setEnabled(item.isEnable);
    holder.layout.setEnabled(item.isEnable);

    if (item.isChecked) {
      holder.btnCell.setVisibility(View.GONE);
      holder.imgMine.setVisibility(View.VISIBLE);
      holder.imgMine.setImageResource(R.drawable.ic_check_circle_black_18dp);
    } else {
      if (item.isEnable) {
        holder.btnCell.setVisibility(View.VISIBLE);
        holder.imgMine.setVisibility(View.GONE);
        holder.btnCell.setText("");
      } else {
        if (item.isMine) {
          holder.btnCell.setVisibility(View.GONE);
          holder.imgMine.setVisibility(View.VISIBLE);
        } else {
          holder.btnCell.setVisibility(View.VISIBLE);
          holder.imgMine.setVisibility(View.GONE);
          holder.btnCell.setText(String.valueOf(item.value));
        }
      }
    }

    if (mShowAll) {
      if (item.isMine) {
        holder.btnCell.setVisibility(View.GONE);
        holder.imgMine.setVisibility(View.VISIBLE);
      } else {
        holder.btnCell.setVisibility(View.VISIBLE);
        holder.imgMine.setVisibility(View.GONE);
        holder.btnCell.setText(String.valueOf(item.value));
      }
    }

    holder.btnCell.setOnClickListener(view -> mSubject.onNext(new Event(false, item)));
    holder.imgMine.setOnLongClickListener(view -> {
      mSubject.onNext(new Event(true, item));
      return true;
    });
    holder.btnCell.setOnLongClickListener(view -> {
      mSubject.onNext(new Event(true, item));
      return true;
    });
  }

  @Override
  public int getItemCount() {
    return mDatas.size();
  }

  public void clear() {
    mDatas.clear();
  }

  public void setShowValue(boolean show) {
    mShowAll = show;
    notifyDataSetChanged();
  }

  public boolean checkFinish(int count) {
    int checkedMine = 0;
    int unknownCount = 0;
    Iterator<Cell> itr = mDatas.iterator();
    while (itr.hasNext()) {
      Cell cell = itr.next();
      if (cell.isChecked && cell.isMine) {
        checkedMine++;
      } else {
        if (cell.isEnable) {
          unknownCount++;
        }
      }
    }

    return unknownCount == 0 && checkedMine == count;
  }

  public void addCell(Cell cell) {
    this.mDatas.add(cell);
  }

  public Observable<Event> getObservable() {
    return this.mSubject;
  }

  class ViewHolder extends RecyclerView.ViewHolder {

    LinearLayout layout;
    Button btnCell;
    ImageView imgMine;

    public ViewHolder(View itemView) {
      super(itemView);
      layout = (LinearLayout) itemView.findViewById(R.id.layout_cell);
      btnCell = (Button) itemView.findViewById(R.id.btn_cell);
      imgMine = (ImageView) itemView.findViewById(R.id.img_mine);
    }
  }
}
