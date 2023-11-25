package drozdi.gameobjects;

import drozdi.gameobjects.walls.Wall;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.awt.geom.Point2D;

public class GameCubeFloat{
  @Getter @Setter
  private Point2D.Float position;
  @Getter @Setter
  private Point2D.Float size;

}
