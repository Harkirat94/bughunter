package sample;

public class Sample2 {

	public static int fn1(int a, int b){
		int c = 0;
		while(b!=0){
			c=a/b;
		}
		return c;
	}
	
	public static void main(String[] args) {
		fn1(5,0);

	}

}
