package MapEditor;
@SuppressWarnings("serial")
public class Main
{
	public static void main(String[] args) throws InterruptedException
	{
		Game game = new Game();
		game.setIsLooping(false);

		while(game.getIsLooping())
		{
			game.update();
			//
			Thread.sleep(10);
		}
	}
}