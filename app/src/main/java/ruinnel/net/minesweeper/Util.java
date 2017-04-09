package ruinnel.net.minesweeper;

import ruinnel.net.minesweeper.bean.Point;

/**
 * Created by ruinnel on 2017. 4. 8..
 */
public class Util {

  public static Point convert(int position, int size) {
    int x = position % size;
    int y = position / size;
    return new Point(x, y);
  }

  public static int convert(Point point) {
    return (point.y * 10) + point.x;
  }
}
