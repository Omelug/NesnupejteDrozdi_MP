package drozdi.game;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class Test {

	//walls
	@Getter @Setter
	private static boolean hitBoxPlayer = true;
	@Getter @Setter
	private static boolean hitBoxTower = true;
	@Getter @Setter
	private static boolean hitBoxCheckpoint = true;
	@Getter @Setter
	private static boolean linesTower = false;
	@Getter @Setter
	private static boolean linesSlug = true;
	@Getter @Setter
	private static boolean hitBoxBullets = true;
	@Getter @Setter
	private static boolean hitBoxScreen = true;
}
//System.out.println("d");