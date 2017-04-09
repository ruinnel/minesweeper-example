package ruinnel.net.minesweeper;

import com.google.common.primitives.Ints;
import ruinnel.net.minesweeper.bean.Cell;
import ruinnel.net.minesweeper.bean.Point;
import rx.Observable;
import rx.functions.Func1;

import java.security.SecureRandom;


/**
 * Created by ruinnel on 2017. 4. 8..
 */
public class Generator {
  public static Observable<Cell> make(int size, int count) {
    final int MINE = -1;
    int[][] array = new int[size][size]; // fill zero.. (java spec...)
    SecureRandom random = new SecureRandom();

    int mineCount = 0;
    while (mineCount < count) {
      int mineIdx = random.nextInt(100);
      Point point = Util.convert(mineIdx, size);
      if (array[point.y][point.x] != MINE) {
        array[point.y][point.x] = -1;

        for (int y = point.y - 1; y <= point.y + 1; y++) {
          for (int x = point.x - 1; x <= point.x + 1; x++) {
            if (0 <= x && x < size && 0 <= y && y < size) {
              if (array[y][x] != MINE) {
                array[y][x]++;
              }
            }
          }
        }

        mineCount++;
      }
    }

    return Observable.from(Ints.asList(Ints.concat(array)))
      .map(new Func1<Integer, Cell>() {
        private int counter;

        @Override
        public Cell call(Integer value) {
          Cell cell = new Cell();
          cell.point = Util.convert(counter++, size);
          cell.isMine = value == MINE;
          cell.value = value;
          cell.isEnable = true;
          cell.isChecked = false;
          return cell;
        }
      });
  }
}
