public class SemanticChecker {
    public static void main(String[] args) throws Exception
    {
        //  java.io.Reader r = new java.io.StringReader
        //  (""
        //  +"func testfunc()->int\n"
        //  +"{\n"
        //  +"    var int a;\n"
        //  +"    a <- 1;\n"
        //  +"    return a;\n"
        //  +"}\n"
        //  +"func main()->int\n"
        //  +"{\n"
        //  +"    var int a;\n"
        //  +"    a <- call testfunc();"
        //  +"    return 0;\n"
        //  +"}\n"
        //  );

          if(args.length == 0)
          args = new String[]
         {
             "/Users/justinneigh/Downloads/Homework5_470/src/minc/simple case"
         };

        if(args.length <= 0)
            return;
        String minicpath = args[0];
        java.io.Reader r = new java.io.FileReader(minicpath);

        Compiler compiler = new Compiler(r);

        compiler.Parse();
    }
}
