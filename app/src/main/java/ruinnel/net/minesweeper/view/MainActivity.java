package ruinnel.net.minesweeper.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.common.collect.Lists;
import ruinnel.net.minesweeper.Generator;
import ruinnel.net.minesweeper.R;
import ruinnel.net.minesweeper.Util;
import ruinnel.net.minesweeper.bean.Cell;
import ruinnel.net.minesweeper.view.adapter.GridAdapter;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import java.util.List;

public class MainActivity extends AppCompatActivity {
  private static final String TAG = MainActivity.class.getSimpleName();

  private static final int SIZE = 10;
  private static final int COUNT = 10;

  @BindView(R.id.recycler_grid)
  RecyclerView mGrid;

  @BindView(R.id.progress)
  ProgressBar mProgress;

  private GridAdapter mGridAdapter;

  private Snackbar mSnackbar;
  private boolean mLoadComplete = false;
  private boolean mGameOver = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    ButterKnife.bind(this);

    List<Cell> datas = Lists.newArrayList();
    mGridAdapter = new GridAdapter(this, datas);
    mGrid.setLayoutManager(new GridLayoutManager(this, SIZE));
    mGrid.setAdapter(mGridAdapter);

    reload();

    mGridAdapter.getObservable()
      .subscribe(event -> {
        if (mLoadComplete && !mGameOver) {
          showSnackbar("isLongClick = " + event.isLongClick);
          Cell cell = event.cell;
          if (cell.isMine && !event.isLongClick) {
            mGameOver = true;
          } else if (event.isLongClick) {
            cell.isChecked = !cell.isChecked;
          } else {
            cell.isEnable = false;
          }

          // refresh view
          int position = Util.convert(cell.point);
          mGridAdapter.notifyItemChanged(position);

          // check finish
          if (mGameOver) {
            showAlertDialog(R.string.game_over);
            mGridAdapter.setShowValue(true);
          } else if (mGridAdapter.checkFinish(COUNT)) {
            showAlertDialog(R.string.game_finish);
          }
          //showSnackbar(String.format("%d", position) + ", isLongClick = " + event.isLongClick);
        } else {
          showSnackbar(R.string.now_loading);
        }
      });
  }

  private void reload() {
    mLoadComplete = false;
    mGameOver = false;
    mGridAdapter.clear();
    mGridAdapter.setShowValue(false);
    mGridAdapter.notifyDataSetChanged();
    mProgress.setVisibility(View.VISIBLE);

    Generator.make(SIZE, COUNT)
      .subscribeOn(Schedulers.computation())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe(new Observer<Cell>() {
        @Override
        public void onCompleted() {
          mLoadComplete = true;
          Log.i(TAG, "onComplete");
          mProgress.setVisibility(View.GONE);
        }

        @Override
        public void onError(Throwable e) {
          Log.i(TAG, "onError");
        }

        @Override
        public void onNext(Cell cell) {
          mGridAdapter.addCell(cell);
          mGridAdapter.notifyItemInserted(mGridAdapter.getItemCount() - 1);
        }
      });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu, menu);
    return super.onCreateOptionsMenu(menu);
  }

  /* Called whenever we call invalidateOptionsMenu() */
  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    return super.onPrepareOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_refresh: {
        reload();
      }
      break;
    }
    return super.onOptionsItemSelected(item);
  }

  private void showAlertDialog(int strRes) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(R.string.alert)
      .setMessage(strRes)
      .setPositiveButton(R.string.confirm, (dialogInterface, i) -> {
        // close dialog..
      }).show();
  }

  private void showSnackbar(int strResId) {
    showSnackbar(getString(strResId), null);
  }

  private void showSnackbar(String str) {
    showSnackbar(str, null);
  }

  private void showSnackbar(int strResId, View.OnClickListener listener) {
    showSnackbar(getString(strResId), listener);
  }

  private void showSnackbar(CharSequence msg, final View.OnClickListener listener) {
    if (mSnackbar != null) {
      mSnackbar.dismiss();
    }
    View contentView = getWindow().findViewById(android.R.id.content);
    mSnackbar = Snackbar.make(contentView, msg, Snackbar.LENGTH_INDEFINITE);
    mSnackbar.setAction(R.string.close, view -> {
      if (listener == null) {
        mSnackbar.dismiss();
      } else {
        listener.onClick(view);
      }
    });
    mSnackbar.setDuration(Snackbar.LENGTH_LONG);
    mSnackbar.show();
  }
}
