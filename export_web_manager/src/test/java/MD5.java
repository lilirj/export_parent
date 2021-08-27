import org.apache.shiro.crypto.hash.Md5Hash;

public class MD5 {
    public static void main(String[] args) {
        System.out.println(new Md5Hash("4399","lili@exerises.com").toString());
    }
}
