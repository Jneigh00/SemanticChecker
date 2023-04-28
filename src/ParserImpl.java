import javax.naming.spi.ObjectFactory;
import java.awt.image.AreaAveragingScaleFilter;
import java.util.*;
import java.util.HashMap;

@SuppressWarnings("unchecked")
public class ParserImpl
{
    public static Boolean _debug = true;
    void Debug(String message)
    {
        if(_debug)
            System.out.println(message);
    }

    // This is for chained symbol table.
    // This includes the global scope only at this moment.
    Env env = new Env(null);
    // this stores the root of parse tree, which will be used to print parse tree and run the parse tree
    ParseTree.Program parsetree_program = null;
    String retType = "";
    ParseTree.Program program____decllist(Object s1) throws Exception
    {
        // 1. check if decllist has main function having no parameters and returns int type
        // 2. assign the root, whose type is ParseTree.Program, to parsetree_program
        ArrayList<ParseTree.FuncDecl> decllist = (ArrayList<ParseTree.FuncDecl>)s1;
        parsetree_program = new ParseTree.Program(decllist);
        for(int i = 0; i<decllist.size(); i++){
            ParseTree.FuncDecl funcDecl = decllist.get(i);
            if(funcDecl.ident.equals("main") && funcDecl.rettype.typename.equals("int") && funcDecl.params.size() ==0){
                return  parsetree_program;
            }
        }
        throw new Exception("The program must have one main function that returns int type and has no parameters.");
    }

    ParseTree.Program program___eps() throws Exception{
        return parsetree_program;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ArrayList<ParseTree.FuncDecl> decllist____decllist_decl(Object s1, Object s2) throws Exception
    {
        ArrayList<ParseTree.FuncDecl> decllist = (ArrayList<ParseTree.FuncDecl>)s1;
        ParseTree.FuncDecl                decl = (ParseTree.FuncDecl           )s2;
        decllist.add(decl);
        return decllist;
    }
    ArrayList<ParseTree.FuncDecl> decllist____eps() throws Exception
    {
        return new ArrayList<ParseTree.FuncDecl>();
    }
    ParseTree.FuncDecl decl____fundecl(Object s1) throws Exception
    {
        return (ParseTree.FuncDecl)s1;
    }
    ParseTree.TypeSpec primtype____INT() throws Exception
    {
        ParseTree.TypeSpec typespec = new ParseTree.TypeSpec("int");
        return typespec;
    }
    ParseTree.TypeSpec primtype___BOOL() throws Exception{
        ParseTree.TypeSpec typeSpec = new ParseTree.TypeSpec("bool");
        return typeSpec;
    }
    ParseTree.TypeSpec typespec____primtype(Object s1)
    {
        ParseTree.TypeSpec primtype = (ParseTree.TypeSpec)s1;
        return primtype;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ParseTree.FuncDecl fundecl____FUNC_IDENT_LPAREN_params_RPAREN_FUNCRET_primtype_BEGIN_localdecls_10X_stmtlist_END(Object s2, Object s4, Object s7, Object s9) throws Exception
    {
        // 1. add function_type_info object (name, return type, params) into the global scope of env
        Token token = (Token) s2;
        String name = token.lexeme;
        ParseTree.TypeSpec retType = (ParseTree.TypeSpec) s7;
        ArrayList<ParseTree.Param> params = (ArrayList<ParseTree.Param>) s4;
        ArrayList<ParseTree.LocalDecl> localdecls = (ArrayList<ParseTree.LocalDecl>) s9;

        if(env.Get(token.lexeme) != null){
            throw new Exception("[Error at " + token.lineno + ":" + (token.column - 5) + "] The function " + token.lexeme + "() is already defined.");
        }

        ParseTree.TypeSpec tempStore = new ParseTree.TypeSpec(token.lexeme);
        retType.info = new ParseTreeInfo.TypeSpecInfo();
        retType.info.params = params;
        retType.info.name = token.lexeme;
        retType.info.returnType = retType.typename;
        retType.info.isFunc = true;
        env.Put(token.lexeme, retType);

        // 2. create a new symbol table on top of env
        env = new Env(env);
        // 3. add parameters into top-local scope of env
        for(int i = 0; i < params.size(); i++){
            ParseTree.TypeSpec temp = new ParseTree.TypeSpec( params.get(i).typespec.typename);
            ParseTree.Param param = params.get(i);
            param.typespec.info.isFunc = false;
            env.Put(param.ident, param.typespec);
        }
        for(int i = 0; i < localdecls.size(); i++){
            ParseTree.TypeSpec temp = new ParseTree.TypeSpec( localdecls.get(i).typespec.typename);
            if(env.Get(localdecls.get(i).ident) != null && ((ParseTree.TypeSpec) env.Get(localdecls.get(i).ident)).info.isFunc == false){
                throw new Exception("[Error at " + token.lineno + ":" + token.column + "] The identifier " + localdecls.get(i).ident +" is already defined. ");
            }
            ParseTree.LocalDecl localDecl = localdecls.get(i);
            localDecl.typespec.info.isFunc = false;
            env.Put(localDecl.ident, localDecl.typespec);

        }
        Object id_type = env.Get(token.lexeme);

        // 4. etc.

       return null;
    }
    ParseTree.FuncDecl fundecl____FUNC_IDENT_LPAREN_params_RPAREN_FUNCRET_primtype_BEGIN_localdecls_X10_stmtlist_END(Object s2, Object s4, Object s7, Object s9, Object s11, Object s12) throws Exception
    {
        // 1. check if this function has at least one return type
        // 2. etc.
        // 3. create and return funcdecl node
        Token                            id         = (Token                           )s2;
        ArrayList<ParseTree.Param>       params     = (ArrayList<ParseTree.Param>      )s4;
        ParseTree.TypeSpec               rettype    = (ParseTree.TypeSpec              )s7;
        ArrayList<ParseTree.LocalDecl>   localdecls = (ArrayList<ParseTree.LocalDecl>  )s9;
        ArrayList<ParseTree.Stmt>        stmtlist   = (ArrayList<ParseTree.Stmt>       )s11;
        Token                            end        = (Token                           )s12;
        ParseTree.FuncDecl funcdecl = new ParseTree.FuncDecl(id.lexeme, rettype, params, localdecls, stmtlist);
        boolean noRetType = false;
        //Loop over stmtlist check if the stmt you are on is an instance of return statement, if it is, compare its type to the return type of the funciton
        for(int i = 0; i<stmtlist.size(); i++){
             ParseTree.Stmt curr = stmtlist.get(i);
             if(curr instanceof ParseTree.ReturnStmt){
                 noRetType = true;
                 ParseTree.ReturnStmt retStmt = (ParseTree.ReturnStmt) curr;
                 if(!(retStmt.expr.info.primType.equals(rettype.typename))){ //
                     throw new Exception("[Error at " + id.lineno + ":" + id.column + "]  The type of returning value ("  +retStmt.expr.info.primType + ") should match with the return type (" + rettype.typename  +") of the function main().");
                 }
             }
             else if(curr instanceof ParseTree.IfStmt){
                 ParseTree.Stmt thenstmt =  ((ParseTree.IfStmt) curr).thenstmt;
                 ParseTree.Stmt elsestmt =  ((ParseTree.IfStmt) curr).elsestmt;
                 if( elsestmt instanceof ParseTree.ReturnStmt){ //
                     noRetType = true;
                     if(!((ParseTree.ReturnStmt) elsestmt).expr.info.primType.equals(rettype.typename)){
                         throw new Exception("[Error at " + id.lineno + ":" + id.column + "]  The type of returning value ("  + ((ParseTree.ReturnStmt) elsestmt).expr.info.primType + ") should match with the return type (" + rettype.typename  +") of the function main().");
                     }
                 }
                 else if(thenstmt instanceof ParseTree.ReturnStmt){
                     noRetType = true;
                     if(!((ParseTree.ReturnStmt) thenstmt).expr.info.primType.equals(rettype.typename)){
                         throw new Exception("[Error at " + id.lineno + ":" + id.column + "]  The type of returning value ("  + ((ParseTree.ReturnStmt) thenstmt).expr.info.primType + ") should match with the return type (" + rettype.typename  +") of the function main().");
                     }

                 }
             }
             else if(curr instanceof ParseTree.WhileStmt){
                 noRetType = true;
                 ParseTree.Stmt whileStmt = ((ParseTree.WhileStmt) curr).stmt;
                 if(whileStmt instanceof ParseTree.ReturnStmt){
                     noRetType = true;
                     if(!((ParseTree.ReturnStmt) whileStmt).expr.info.primType.equals(rettype.typename)){
                         throw new Exception("[Error at " + id.lineno + ":" + id.column + "]  The type of returning value ("  + ((ParseTree.ReturnStmt) whileStmt).expr.info.primType + ") should match with the return type (" + rettype.typename  +") of the function main().");
                     }
                 }
             }
        }
        if(noRetType == false){
            throw new Exception("[Error at " + end.lineno + ":" + end.column + "] The function " + id.lexeme +"() should return at least one " + rettype.typename + " value." );
        }
        env = env.prev;
       return funcdecl;
    }
    ArrayList<ParseTree.Param> params____param_list(Object s1){
        ArrayList<ParseTree.Param> params = (ArrayList<ParseTree.Param>) s1;
        return params;
    }
    ArrayList<ParseTree.Param> params____eps() throws Exception
    {
        return new ArrayList<ParseTree.Param>();
    }

    ArrayList<ParseTree.Stmt> stmtlist____stmtlist_stmt(Object s1, Object s2) throws Exception
    {
        ArrayList<ParseTree.Stmt> stmtlist = (ArrayList<ParseTree.Stmt>)s1;
        ParseTree.Stmt            stmt     = (ParseTree.Stmt           )s2;
        stmtlist.add(stmt);
        return stmtlist;
    }
    ArrayList<ParseTree.Stmt> stmtlist____eps() throws Exception
    {
        return new ArrayList<ParseTree.Stmt>();
    }

    ParseTree.Stmt stmt____assign_stmt  (Object s1) throws Exception
    {
        assert(s1 instanceof ParseTree.AssignStmt);
        return (ParseTree.Stmt)s1;
    }
    ParseTree.Stmt stmt____return_stmt(Object s1) throws Exception
    {
        assert(s1 instanceof ParseTree.ReturnStmt);
        return (ParseTree.Stmt)s1;
    }
    ParseTree.Stmt stmt____print_stmt(Object s1) throws Exception{
        assert(s1 instanceof ParseTree.PrintStmt);
        return (ParseTree.PrintStmt) s1;
    }
    ParseTree.Stmt stmt____if_stmt(Object s1) throws Exception{
        assert(s1 instanceof ParseTree.IfStmt);
        return (ParseTree.IfStmt) s1;
    }
    ParseTree.Stmt stmt____while_stmt(Object s1) throws Exception{
        assert(s1 instanceof ParseTree.WhileStmt);
        return  (ParseTree.WhileStmt)s1;
    }
    ParseTree.Stmt stmt____compound_stmt(Object s1) throws Exception{
        assert(s1 instanceof ParseTree.CompoundStmt);
        return  (ParseTree.CompoundStmt) s1;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ParseTree.AssignStmt assignstmt____IDENT_ASSIGN_expr_SEMI(Object s1, Object s2, Object s3) throws Exception
    {
        // 1. check if ident.value_type matches with expr.value_type
        // 2. etc.
        // e. create and return node
        Token          id     = (Token         )s1;
        Token          assign = (Token         )s2;
        ParseTree.Expr expr   = (ParseTree.Expr)s3;

        if(env.Get(id.lexeme) == null){
            throw new Exception("[Error at " + id.lineno + ":" + id.column + "] Cannot use an undefined variable " + id.lexeme );
        }
        String id_type = env.Get(id.lexeme).typename;

       if(expr.info.primType.equals("int") && id_type.equals("int")){
           ParseTree.AssignStmt stmt = new ParseTree.AssignStmt(id.lexeme, expr);
           stmt.ident_reladdr = 1;
           return stmt;
       }
       if(expr.info.primType.equals("bool") && id_type.equals("bool")){
           ParseTree.AssignStmt stmt = new ParseTree.AssignStmt(id.lexeme, expr);
           stmt.ident_reladdr = 1;
           return stmt;
       }
        throw new Exception("[Error at " + assign.lineno + ":" + assign.column + "] Cannot assign " + expr.info.primType  + " value to " +  id_type  +" variable "  + id.lexeme + ".");
    }
    ParseTree.ReturnStmt returnstmt____RETURN_expr_SEMI(Object s2) throws Exception
    {
        // 1. check if expr.value_type matches with the current function return type
        // 2. etc.
        // 3. create and return node
        ParseTree.Expr expr = (ParseTree.Expr)s2;
        return new ParseTree.ReturnStmt(expr);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ArrayList<ParseTree.LocalDecl> localdecls____localdecls_localdecl(Object s1, Object s2)
    {
        ArrayList<ParseTree.LocalDecl> localdecls = (ArrayList<ParseTree.LocalDecl>)s1;
        ParseTree.LocalDecl            localdecl  = (ParseTree.LocalDecl           )s2;
        localdecls.add(localdecl);
        return localdecls;
    }
    ArrayList<ParseTree.LocalDecl> localdecls____eps() throws Exception
    {
        return new ArrayList<ParseTree.LocalDecl>();
    }
    ParseTree.LocalDecl localdecl____VAR_typespec_IDENT_SEMI(Object s2, Object s3)
    {
        ParseTree.TypeSpec typespec = (ParseTree.TypeSpec)s2;
        Token              id       = (Token             )s3;
        ParseTree.LocalDecl localdecl = new ParseTree.LocalDecl(id.lexeme, typespec);
        localdecl.reladdr = 1;
        return localdecl;
    }
    ArrayList<ParseTree.Arg> args____arg_lists(Object s1) throws Exception{
        ArrayList<ParseTree.Arg> argList1 =(ArrayList<ParseTree.Arg>) s1;
        return argList1;
    }
    ArrayList<ParseTree.Arg> args____eps() throws Exception
    {
        return new ArrayList<ParseTree.Arg>();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ParseTree.ExprAdd expr____expr_ADD_expr(Object s1, Object s2, Object s3) throws Exception
    {
        // 1. check if expr1.value_type matches with the expr2.value_type
        // 2. etc.
        // 3. create and return node that has value_type
        ParseTree.Expr expr1 = (ParseTree.Expr)s1;
        Token          oper  = (Token         )s2;
        ParseTree.Expr expr2 = (ParseTree.Expr)s3;
        // check if expr1.type matches with expr2.type
        ParseTree.ExprAdd addExpr = new ParseTree.ExprAdd(expr1,expr2);
        if((expr1.info.primType.equals("int")) && (expr2.info.primType.equals("int"))){
            addExpr.info = new ParseTreeInfo.ExprInfo();
            addExpr.info.primType = "int";
            return addExpr;
        }

        throw new Exception("[Error at " + oper.lineno + ":" + oper.column + "] Cannot perform bool + int.");
    }
    ParseTree.ExprSub expr____expr_SUB_expr(Object s1, Object s2, Object s3) throws Exception{
        ParseTree.Expr expr1 = (ParseTree.Expr) s1;
        Token oper = (Token) s2;
        ParseTree.Expr expr2 =(ParseTree.Expr) s3;
        ParseTree.ExprSub subExpr = new ParseTree.ExprSub(expr1,expr2);
        if((expr1.info.primType.equals("int")) && (expr2.info.primType.equals("int"))){
            subExpr.info = new ParseTreeInfo.ExprInfo();
            subExpr.info.primType = "int";
            return subExpr;
        }
        throw new Exception("Semantic Error in exprSUB");
    }

    ParseTree.ExprEq expr____expr_EQ_expr(Object s1, Object s2, Object s3) throws Exception
    {
        // 1. check if expr1.value_type matches with the expr2.value_type
        // 2. etc.
        // 3. create and return node that has value_type

        ParseTree.Expr expr1 = (ParseTree.Expr)s1;
        Token          oper  = (Token         )s2;
        ParseTree.Expr expr2 = (ParseTree.Expr)s3;
        // check if expr1.type matches with expr2.type
        ParseTree.ExprEq subExpr = new ParseTree.ExprEq(expr1,expr2);
        if((expr1.info.primType.equals("int")) && (expr2.info.primType.equals("int"))){
            subExpr.info = new ParseTreeInfo.ExprInfo();
            subExpr.info.primType = "bool";
            return subExpr;
        }
        if((expr1.info.primType.equals("bool")) &&(expr2.info.primType.equals("bool"))){
            subExpr.info = new ParseTreeInfo.ExprInfo();
            subExpr.info.primType = "bool";
            return subExpr;
        }
        throw new Exception("Semantic Error in exprEQ");
    }
    ParseTree.ExprMul expr____expr_MUL_expr(Object s1, Object s2, Object s3) throws Exception{
        ParseTree.Expr expr1 = (ParseTree.Expr) s1;
        Token oper = (Token) s2;
        ParseTree.Expr expr2 =(ParseTree.Expr) s3;
        ParseTree.ExprMul subExpr = new ParseTree.ExprMul(expr1,expr2);
        if((expr1.info.primType.equals("int")) && (expr2.info.primType.equals("int"))){
            subExpr.info = new ParseTreeInfo.ExprInfo();
            subExpr.info.primType = "int";
            return subExpr;
        }
        throw new Exception("[Error at " + oper.lineno + ":" + oper.column + "] Cannot perform int * bool.");
    }
    ParseTree.ExprDiv expr____expr_DIV_expr(Object s1, Object s2, Object s3) throws Exception{
        ParseTree.Expr expr1 = (ParseTree.Expr) s1;
        Token oper = (Token) s2;
        ParseTree.Expr expr2 =(ParseTree.Expr) s3;
        ParseTree.ExprDiv subExpr = new ParseTree.ExprDiv(expr1,expr2);
        if((expr1.info.primType.equals("int")) && (expr2.info.primType.equals("int"))){
            subExpr.info = new ParseTreeInfo.ExprInfo();
            subExpr.info.primType = "int";
            return subExpr;
        }
        throw new Exception("semantic Error in DIV");
    }
    ParseTree.ExprMod expr____expr_MOD_expr(Object s1, Object s2, Object s3) throws Exception{
        ParseTree.Expr expr1 = (ParseTree.Expr) s1;
        Token oper = (Token) s2;
        ParseTree.Expr expr2 =(ParseTree.Expr) s3;
        ParseTree.ExprMod subExpr = new ParseTree.ExprMod(expr1,expr2);
        if((expr1.info.primType.equals("int")) && (expr2.info.primType.equals("int"))){
            subExpr.info = new ParseTreeInfo.ExprInfo();
            subExpr.info.primType = "int";
            return subExpr;
        }
        throw new Exception("semantic Error in ExprMod");

    }
    ParseTree.ExprNe expr____expr_NE_expr(Object s1, Object s2, Object s3) throws Exception{
        ParseTree.Expr expr1 = (ParseTree.Expr) s1;
        Token oper = (Token) s2;
        ParseTree.Expr expr2 =(ParseTree.Expr) s3;
        ParseTree.ExprNe subExpr = new ParseTree.ExprNe(expr1,expr2);
        if((expr1.info.primType.equals("int")) && (expr2.info.primType.equals("int"))){
            subExpr.info = new ParseTreeInfo.ExprInfo();
            subExpr.info.primType = "bool";
            return subExpr;
        }
        if((expr1.info.primType.equals("bool")) && (expr2.info.primType.equals("bool"))){
            subExpr.info = new ParseTreeInfo.ExprInfo();
            subExpr.info.primType = "bool";
            return subExpr;
        }
        throw new Exception("[Error at " + oper.lineno + ":" + oper.column + "] Cannot perform bool != int.");
    }
    ParseTree.ExprLe expr____expr_LE_expr(Object s1, Object s2, Object s3) throws Exception{
        ParseTree.Expr expr1 = (ParseTree.Expr) s1;
        Token oper = (Token) s2;
        ParseTree.Expr expr2 =(ParseTree.Expr) s3;
        ParseTree.ExprLe subExpr = new ParseTree.ExprLe(expr1,expr2);
        if((expr1.info.primType.equals("int")) && (expr2.info.primType.equals("int"))){
            subExpr.info = new ParseTreeInfo.ExprInfo();
            subExpr.info.primType = "bool";
            return subExpr;
        }
        throw new Exception("[Error at " + oper.lineno + ":" + oper.column + "] Cannot perform bool <= bool.");
    }
    ParseTree.ExprLt expr____expr_LT_expr(Object s1, Object s2, Object s3) throws Exception{
        ParseTree.Expr expr1 = (ParseTree.Expr) s1;
        Token oper = (Token) s2;
        ParseTree.Expr expr2 =(ParseTree.Expr) s3;
        ParseTree.ExprLt subExpr = new ParseTree.ExprLt(expr1,expr2);
        if((expr1.info.primType.equals("int")) && (expr2.info.primType.equals("int"))){
            subExpr.info = new ParseTreeInfo.ExprInfo();
            subExpr.info.primType = "bool";
            return subExpr;
        }
        throw new Exception("[Error at " + oper.lineno + ":" + oper.column + "] Cannot perform int < bool.");
    }
    ParseTree.ExprGe expr____expr_GE_expr(Object s1, Object s2, Object s3) throws Exception{
        ParseTree.Expr expr1 = (ParseTree.Expr) s1;
        Token oper = (Token) s2;
        ParseTree.Expr expr2 =(ParseTree.Expr) s3;
        ParseTree.ExprGe subExpr = new ParseTree.ExprGe(expr1,expr2);
        if((expr1.info.primType.equals("int")) && (expr2.info.primType.equals("int"))){
            subExpr.info = new ParseTreeInfo.ExprInfo();
            subExpr.info.primType = "bool";
            return subExpr;
        }
        throw new Exception("semantic Error in ExprGE");
    }
    ParseTree.ExprGt expr____expr_GT_expr(Object s1, Object s2, Object s3) throws Exception{
        ParseTree.Expr expr1 = (ParseTree.Expr) s1;
        Token oper = (Token) s2;
        ParseTree.Expr expr2 =(ParseTree.Expr) s3;
        ParseTree.ExprGt subExpr = new ParseTree.ExprGt(expr1,expr2);
        if((expr1.info.primType.equals("int")) && (expr2.info.primType.equals("int"))){
            subExpr.info = new ParseTreeInfo.ExprInfo();
            subExpr.info.primType = "bool";
            return subExpr;
        }
        throw new Exception("semantic Error in ExprGt");
    }
    ParseTree.ExprAnd expr____expr_AND_expr(Object s1, Object s2, Object s3) throws Exception{
        ParseTree.Expr expr1 = (ParseTree.Expr) s1;
        Token oper = (Token) s2;
        ParseTree.Expr expr2 =(ParseTree.Expr) s3;
        ParseTree.ExprAnd andExpr = new ParseTree.ExprAnd(expr1,expr2);
        if((expr1.info.primType.equals("bool")) && (expr2.info.primType.equals("bool"))){
            andExpr.info = new ParseTreeInfo.ExprInfo();
            andExpr.info.primType = "bool";
            return andExpr;
        }
        throw new Exception("[Error at " + oper.lineno + ":" + oper.column + "] Cannot perform bool and int.");
    }
    ParseTree.ExprOr expr____expr_OR_expr(Object s1, Object s2, Object s3) throws Exception{
        ParseTree.Expr expr1 = (ParseTree.Expr) s1;
        Token oper = (Token) s2;
        ParseTree.Expr expr2 =(ParseTree.Expr) s3;
        ParseTree.ExprOr subExpr = new ParseTree.ExprOr(expr1,expr2);
        if((expr1.info.primType.equals("bool")) && (expr2.info.primType.equals("bool"))){
            subExpr.info = new ParseTreeInfo.ExprInfo();
            subExpr.info.primType = "bool";
            return subExpr;
        }
        throw new Exception("semantic Error in ExprOr");
    }
    ParseTree.ExprNot expr____NOT_expr(Object s1, Object s2) throws Exception{
        Token notToken = (Token) s1;
        ParseTree.Expr expr2 = (ParseTree.Expr) s2;
        ParseTree.ExprNot notExpr = new ParseTree.ExprNot(expr2);
        if(expr2.info.primType.equals("int")){
            throw new Exception("[Error at " + notToken.lineno + ":" + notToken.column + "] Cannot perform not int.");
        }
        notExpr.info.primType = "bool";
        return notExpr;
    }
    ParseTree.ExprParen expr____LPAREN_expr_RPAREN(Object s1, Object s2, Object s3) throws Exception
    {
        // 1. create and return node whose value_type is the same to the expr.value_type
        Token          lparen = (Token         )s1;
        ParseTree.Expr expr   = (ParseTree.Expr)s2;
        Token          rparen = (Token         )s3;
        ParseTree.ExprParen exprParen = new ParseTree.ExprParen(expr);
        exprParen.info.primType = expr.info.primType;
        return exprParen;

    }
    ParseTree.ExprIdent expr____IDENT(Object s1) throws Exception
    {
        // 1. check if id.lexeme can be found in chained symbol tables
        // 2. check if it is variable type
        // 3. etc.
        // 4. create and return node that has the value_type of the id.lexeme
        Token id = (Token)s1;
        if(env.Get(id.lexeme) == null){
                throw new Exception("[Error at " + id.lineno + ":" + id.column + "] Cannot use and undefined variable " + id.lexeme + ".");
        }
        if(env.Get(id.lexeme).info.isFunc){
            throw new Exception("[Error at " + id.lineno + ":" + id.column + "] Cannot use the function " + id.lexeme + "() as a variable.");
        }
        ParseTree.ExprIdent expr = new ParseTree.ExprIdent(id.lexeme);
        expr.info = new ParseTreeInfo.ExprInfo();
        expr.info.name = id.lexeme;
        expr.info.primType = env.Get(id.lexeme).typename;
        expr.reladdr = 1;
        return expr;
    }
    ParseTree.ExprIntLit expr____INTLIT(Object s1) throws Exception
    {
        // 1. create and return node that has int type
        Token token = (Token)s1;
        int value = Integer.parseInt(token.lexeme);
        ParseTree.ExprIntLit intExpr = new ParseTree.ExprIntLit(value);
        intExpr.info.primType = "int";
        intExpr.info.value = Integer.toString(value);
        return intExpr;
    }
    ParseTree.ExprBoolLit expr____BOOL_LIT(Object s1) throws Exception{
        Token token = (Token) s1;
        boolean bolVal = Boolean.parseBoolean(token.lexeme);
        ParseTree.ExprBoolLit boolExpr = new ParseTree.ExprBoolLit(bolVal);
        boolExpr.info.primType = "bool";
        boolExpr.info.value = Boolean.toString(bolVal);
        return boolExpr;
    }
    ParseTree.ExprCall expr____CALL_IDENT_LPAREN_args_RPAREN(Object s2, Object s4) throws Exception
    {
        // 1. check if id.lexeme can be found in chained symbol tables
        // 2. check if it is function type
        // 3. check if the number and types of env(id.lexeme).params match with those of args
        // 4. etc.
        // 5. create and return node that has the value_type of env(id.lexeme).return_type
        Token                    id   = (Token                   )s2;
        ArrayList<ParseTree.Arg> args = (ArrayList<ParseTree.Arg>)s4;
        ParseTree.TypeSpec func_attr = env.Get(id.lexeme);

        if(env.Get(id.lexeme) == null){
            throw new Exception("[Error at " + id.lineno + ":" + (id.column - id.lexeme.length() -1) + "] Cannot use an undefined function " + id.lexeme + "()");
        }


        ParseTree.TypeSpec pulled = env.Get(id.lexeme);
        if(pulled.info.params == null){
            if(func_attr.info.isFunc == false){
                throw new Exception("[Error at " + id.lineno + ":" + (id.column - id.lexeme.length() -1) + "] Cannot use the function " + id.lexeme + "()  as a variable.");
            }
        }
        else{
            int paramsSize = pulled.info.params.size();
            int argsSize = args.size();

            if(paramsSize != argsSize){
                throw new Exception("[Error at " + id.lineno + ":" + (id.column - id.lexeme.length() -1) + "] Cannot pass the incorrect number of arguments to " + id.lexeme + "()");
            }

            for(int i = 0; i < argsSize; i++){
                ParseTree.Arg currArg = args.get(i);
                ParseTree.Param currParam = pulled.info.params.get(i);
                if(!currArg.expr.info.primType.equals(currParam.typespec.typename)){
                    String printNum = "";
                    if(i == 0)
                        printNum = "1st";
                    else if(i == 1)
                        printNum = "2nd";
                    else if(i == 2)
                        printNum = "3rd";
                    else
                        printNum = i + "th";
                    throw new Exception("[Error at " + id.lineno + ":" + (id.column - id.lexeme.length() - 1) + "] The "+ printNum + " argument of the function " + id.lexeme + "() should be " + currParam.typespec.typename + " type." );
                }
            }
        }


        ParseTree.ExprCall call = new ParseTree.ExprCall(env.Get(id.lexeme).info.name, args);
        call.info.primType = env.Get(id.lexeme).info.returnType;
        call.info.name = env.Get(id.lexeme).info.name;
        return call;
    }


    ParseTree.IfStmt if_stmt____IF_LPAREN_expr_RPAREN_stmt_ELSE_stmt(Object s2,Object s3, Object s5, Object s7) throws Exception{
        ParseTree.Expr cond = (ParseTree.Expr) s3;
        Token notToken = (Token) s2;
        if(!cond.info.primType.equals("bool")){
            throw new Exception("[Error at " + notToken.lineno + ":" + (notToken.column + 2) + "] Cannot perform not int.");
        }
        ParseTree.Stmt thenStmt = (ParseTree.Stmt) s5;
        ParseTree.Stmt elseStmt = (ParseTree.Stmt) s7;
        return new ParseTree.IfStmt(cond, thenStmt, elseStmt);
    }

    ArrayList<ParseTree.Param> param_list____param_list_COMMA_param(Object s1, Object s3) throws Exception{
        ArrayList<ParseTree.Param> paramList1 = (ArrayList<ParseTree.Param>) s1;
        ParseTree.Param param1 = (ParseTree.Param) s3;
        paramList1.add(param1);
        return paramList1;
    }

    ArrayList<ParseTree.Param> param_list____param(Object s1) throws Exception{
        ParseTree.Param param1 = (ParseTree.Param) s1;
        ArrayList<ParseTree.Param> paramList1 = new ArrayList<>();
        paramList1.add(param1);
        return paramList1;
    }

    ParseTree.Param param____VAR_type_spec_IDENT (Object s2, Object s3) throws Exception{
        Token ident1 = (Token) s3;
        ParseTree.TypeSpec typeSpec1 = (ParseTree.TypeSpec) s2;
        return new ParseTree.Param(ident1.lexeme, typeSpec1);
    }

    ArrayList<ParseTree.Arg> arg_list____arg_list_COMMA_expr(Object s1, Object s3) throws Exception{
        ParseTree.Expr expr1 = (ParseTree.Expr) s3;
        ArrayList<ParseTree.Arg> argList1 = (ArrayList<ParseTree.Arg>) s1;
        argList1.add(new ParseTree.Arg(expr1));
        return argList1;
    }

    ArrayList<ParseTree.Arg> arg_list____expr(Object s1) throws Exception{
        ParseTree.Expr expr1 = (ParseTree.Expr) s1;
        ArrayList<ParseTree.Arg> argLis1 = new ArrayList<>();
        argLis1.add(new ParseTree.Arg(expr1));
        return argLis1;
    }

    ParseTree.PrintStmt print_stmt____PRINT_expr_SEMI(Object s1) throws Exception{
        ParseTree.Expr expr1 = (ParseTree.Expr) s1;
        return new ParseTree.PrintStmt(expr1);
    }
    ParseTree.WhileStmt while_stmt____WHILE_LPAREN_expr_RPAREN_stmt(Object s3, Object s4, Object s5) throws Exception{
        ParseTree.Expr expr1 = (ParseTree.Expr) s3;
        ParseTree.Stmt stmt1 = (ParseTree.Stmt) s5;
        Token notToken = (Token) s4;
        if(!expr1.info.primType.equals("bool")){
            throw new Exception("[Error at " + notToken.lineno + ":" + (notToken.column - notToken.lexeme.length() -2) + "] Cannot perform not int.");
        }
        return new ParseTree.WhileStmt(expr1, stmt1);
    }
    ParseTree.CompoundStmt compound_stmt____BEGIN_localDecls_stmtList_End(Object s1 ,Object s2){
        ArrayList<ParseTree.LocalDecl> localDecls = (ArrayList<ParseTree.LocalDecl>)s2;

        env = new Env(env);

        for(int i = 0; i < localDecls.size(); i++){
            ParseTree.LocalDecl temp = localDecls.get(i);
            temp.typespec.info.isFunc = false;
            env.Put(temp.ident, temp.typespec);
        }

        return null;
    }

    ParseTree.CompoundStmt compound_stmt____BEGIN_localDecls_stmtList_End(Object s1, Object s2, Object s3){
        ArrayList<ParseTree.LocalDecl> localDecls = (ArrayList<ParseTree.LocalDecl>)s2;
        ArrayList<ParseTree.Stmt> stmts = (ArrayList<ParseTree.Stmt>)s3;

        ParseTree.CompoundStmt compoundStmt = new ParseTree.CompoundStmt(localDecls, stmts);
        env = env.prev;

        return compoundStmt;
    }


}
