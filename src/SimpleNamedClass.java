import com.melanistics.Init.Start;
import com.naronco.cubeshaft.Cubeshaft;
import com.naronco.cubeshaft.entity.TileEntity;
import com.naronco.cubeshaft.level.tile.Tile;

public class SimpleNamedClass {
	public static final Tile SimpleNamedTile = new SinpleNamedBlock(30, 7);

	@Start
	public void SimpleNamedMethode() {
		System.err.println("I live !!!");
		Cubeshaft.game.player.inventory.add(SimpleNamedTile.id);
		Cubeshaft.ticker.addRunnable(new UselessClass4());
		System.err.println("I die ???");
	}
}
