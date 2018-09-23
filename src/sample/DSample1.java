package sample;

public class DSample1 {

	public static int div(int a, int b){
		/*int c;
		if(b!=0){
			c=a/b;
		}
		else{
			c=a/b;
		}
		return c;*/
		while(b!=0){
			b=b-1;
		}
		return 0;
	}
	

	/*public static int evil(int a, int b){
		int c;
		if(b!=0){
			c=a+b;
		}
		c=a/b;
		return c;
	}*/

	public static void main(String[] args) {
		div(2,0);
		//evil(2,0);
	}

}
