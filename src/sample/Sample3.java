package sample;

public class Sample3 {

	public static int fn1(int a, int b){
		int c=0;
		if(b<5){
			if(b>-1){
				c=a/b;
			}
		}
		return c;
	}
	
	public static void main(String[] args) {
		fn1(5,0);

	}

}
