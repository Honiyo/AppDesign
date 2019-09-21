package com.example.caculator;

import java.util.function.DoubleUnaryOperator;
import java.util.Stack;

public class Seqstack {

    public int instack(char c)
    {
        int x=0;
        switch (c)
        {
            case '=':x = 0; break;
            case '(':x = 1; break;
            case '*':
            case '/':
            case '%':x = 5; break;
            case '+':
            case '-':x = 3; break;
            case ')':x = 6; break;
        }
        return x;
    }
    public int outstack(char c)
    {
        int z=0;
        switch (c)
        {
            case '=':z = 0; break;
            case '(':z = 6; break;
            case '*':
            case '/':
            case '%':z = 4; break;
            case '+':
            case '-':z = 2; break;
            case ')':z = 1; break;
        }
        return z;
    }
    public int calculate(char []exp)
    {
        Stack<Character> s=new Stack<Character>();
        Stack<Integer> d=new Stack<Integer>();
        char ch = '=', ch1=' ', op=' ';
        int i = 0, count = 0,re=0,left=0, right=0,val=0, value=0;
        s.push(ch);
        while (s.empty() == false )
        {
            if (Character.isDigit(exp[i]))
            {
                d.push(exp[i] - '0');
                count += 1;
                if (!Character.isDigit(exp[i + 1]))
                {
                    for (int k = 0; k < count; k++)
                    {
                        val=d.pop();
                        re += val*Math.pow(10, k);
                    }
                    d.push(re);
                }
            }
            else
            {
                re = 0;
                count = 0;
                ch1=s.peek();
                while (instack(ch1) > outstack(exp[i]))
                {
                    right=d.pop();
                    left=d.pop();
                    op=s.pop();
                    switch (op)
                    {
                        case '+':value = left + right; d.push(value); break;
                        case '-':value = left - right; d.push(value); break;
                        case '*':value = left * right; d.push(value); break;
                        case '/':
                            if (right == 0.0)
                            {
                                value=999999999;
                                d.push(value);
                            }
                            else{ value = left / right; d.push(value); }
                            break;
                    }
                    ch1=s.peek();
                }
                if (instack(ch1) < outstack(exp[i]))
                {
                    s.push(exp[i]);
                }
                else
                {
                    op=s.pop();
                }

            }
            i++;
        }
        return d.peek();
    }

    public double calculate1(char []exp)
    {
        Stack<Character> s=new Stack<Character>();  //存放操作符的栈
        Stack<Double> d=new Stack<Double>();  //存放操作数的栈
        char ch = '=', ch1=' ', op=' ';    //op操作符
        int i = 0;  //循环参数
        double temp1=0,left=0, right=0,val=0, value=0;
        String temp0="";
        s.push(ch);
        while (s.empty() == false )
        {
            if (Character.isDigit(exp[i])||exp[i]=='.')  //如果exp[i]是数字或者是小数点
            {
                if(Character.isDigit(exp[i+1])||exp[i+1]=='.')
                    temp0+=exp[i];
                else
                {
                    temp0+=exp[i];
                    temp1=Double.parseDouble(temp0);
                    d.push(temp1);
                    temp0="";
                }
            }
            else
            {
                ch1=s.peek();
                while (instack(ch1) > outstack(exp[i]))
                {
                    right=d.pop();
                    left=d.pop();
                    op=s.pop();
                    switch (op)
                    {
                        case '+':value = left + right; d.push(value); break;
                        case '-':value = left - right; d.push(value); break;
                        case '*':value = left * right; d.push(value); break;
                        case '/':
                            if (right == 0.0)
                            {
                                value=999999999;
                            }
                            else{ value = left / right; d.push(value); }
                            break;
                    }
                    ch1=s.peek();
                }
                if (instack(ch1) < outstack(exp[i]))
                {
                    s.push(exp[i]);
                }
                else
                {
                    op=s.pop();
                }

            }
            i++;
        }
        return d.peek();
    }
}
