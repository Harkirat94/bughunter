package sample;

public class Sample5 {

	public static int fn1(int a, int b){
		int c=0;
		if(b<10){
			if(b>0){
				c=a/b;
			}
		}
		c=a/b;
		return c;
	}
	
	public static void main(String[] args) {
		fn1(5,0);

	}

}
