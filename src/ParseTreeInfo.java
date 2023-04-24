import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class ParseTreeInfo
{
    // Use this classes to store information into parse tree node (subclasses of ParseTree.Node)
    // You should not modify ParseTree.java
    public static class TypeSpecInfo
    {
    }
    public static class ProgramInfo
    {
    }
    public static class FuncDeclInfo
    {
        String name;
        ParseTree.TypeSpec returnType;
        ArrayList<ParseTree.Param> params;
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
    }
}