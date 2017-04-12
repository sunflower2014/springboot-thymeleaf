
public class Main {
	public static Main m = new Main();
	
	{
		System.out.println("NON-STATIC BLOCK\n");
	}
	
	static {
		System.out.println("STATIC BLOCK\n");
	}
	
	public Main() {
		System.out.println("MAIN CONSTRUCTOR\n");
	}
	
	public static void main(String... args) {
		System.out.println("MAIN METHOD\n");
	}

}
