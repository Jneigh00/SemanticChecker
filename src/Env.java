import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class Env
{

    public HashMap<String, Object> linkedSym;
    public Env prev;
    public ParseTreeInfo.FuncDeclInfo test;
    public Env(Env prev)
    {
        this.prev = prev;
        linkedSym = new HashMap<>();
    }
    public void Put(String name, Object value)
    {
        linkedSym.put(name, value);
    }
    public Object Get(String name)
    {
        // this is a fake implementation
        // For the real implementation, I recommend to return a class object
        //   since the identifier's type can be variable or function
        //   whose detailed attributes will be different
        Object retVal = linkedSym.get(name);
        if( prev != null && retVal == null){
            retVal = prev.Get(name);
        }

        return retVal;
    }
}
