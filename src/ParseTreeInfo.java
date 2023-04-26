import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class ParseTreeInfo
{
    // Use this classes to store information into parse tree node (subclasses of ParseTree.Node)
    // You should not modify ParseTree.java
    public static class TypeSpecInfo
    {
        ArrayList<ParseTree.Param> params;
        public String returnType;
        public String name;
    }
    public static class ProgramInfo
    {
    }
    public static class FuncDeclInfo
    {
        public String name;
        public ParseTree.TypeSpec returnType;
        public ArrayList<ParseTree.Param> params;
    }
    public static class ParamInfo
    {
    }
    public static class LocalDeclInfo
    {
    }
    public static class StmtStmtInfo
    {
    }
    public static class ArgInfo
    {
    }
    public static class ExprInfo
    {
        String name;
        String primType;
        String retType;
        String value;
    }
}
