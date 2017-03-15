import com.javachina.kit.Utils;

public class MDTest {

	public static void main(String[] args) {
		String processed = Utils.markdown2html("## Hello World\n [baidu](http://www.baidu.com)");
		System.out.println(processed);
	}

}
