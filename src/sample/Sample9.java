package sample;

public class Sample9 {

	public static int div(int a, int b){
		int c=0;
		if(b!=0){
			c=a/b;
		}
		return c;
	}

	public static void main(String[] args) {
		div(5,0);
	}
	
}
