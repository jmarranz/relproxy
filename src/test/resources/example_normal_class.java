
    // Test comment

  /* Another test comment
 *
 */

// Add this import to test if a complete class declaration is detected when imports present
//import example.javashellex.JProxyShellExample;

public class example_normal_class
{
    public static void main(String[] args)
    {
        String msg = args[0] + args[1];
        System.out.println(msg);

        System.out.println("example_normal_class 1 ");

        example.javashellex.JProxyShellExample.exec();
    }
}
