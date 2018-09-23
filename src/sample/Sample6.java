package sample;

public class Sample6 {

	public static int fn1(int a, int b){
		int c=0;
		if(a<5){
			if(b<6){
					if(b>-1){
						c=a/b;
		
				}
					c=a+b;
			}
		}
		return c;
	}
	
	public static void main(String[] args) {
		fn1(5,0);

	}

}
