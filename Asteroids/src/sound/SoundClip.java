package sound;

public class SoundClip
{

	private String path;
	
	public SoundClip(String path)
	{
		this.path = path;
	}
	
	public void play()
	{
		System.out.println("play sound: \"" + path + "\"");
	}
	
	public void stop()
	{
		
		
	}
}
