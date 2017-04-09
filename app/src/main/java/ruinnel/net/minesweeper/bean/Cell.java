package ruinnel.net.minesweeper.bean;

/**
 * Created by ruinnel on 2017. 4. 8..
 */
public class Cell {
  public Point point;
  public boolean isMine;
  public boolean isEnable;
  public boolean isChecked;
  public int value;

  @Override
  public String toString() {
    return "Cell{" +
      "point=" + point +
      ", isMine=" + isMine +
      ", isEnable=" + isEnable +
      ", isChecked=" + isChecked +
      ", value=" + value +
      '}';
  }
}
