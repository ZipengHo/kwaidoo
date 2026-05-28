package cell.bap.servlet;

import java.io.PrintWriter;
import java.util.Locale;

import bap.cells.BasicCell;

public class CPrintWriter extends BasicCell implements IPrintWriter
{

    /**
    * 
    */
    private static final long serialVersionUID = 5739272539387145406L;

    private transient PrintWriter original;

    public CPrintWriter(PrintWriter original)
    {
        this.original = original;
    }

    public void flush()
    {
        original.flush();
    }

    public boolean checkError()
    {
        return original.checkError();
    }

    public CPrintWriter write(int c)
    {
        original.write(c);
        return this;
    }

    public CPrintWriter write(char[] buf, int off, int len)
    {
        original.write(buf, off, len);
        return this;
    }

    public CPrintWriter write(char[] buf)
    {
        original.write(buf);
        return this;
    }

    public CPrintWriter write(String s, int off, int len)
    {
        original.write(s, off, len);
        return this;
    }

    public CPrintWriter write(String s)
    {
        original.write(s);
        return this;
    }

    public CPrintWriter print(boolean b)
    {
        original.print(b);
        return this;
    }

    public CPrintWriter print(char c)
    {
        original.print(c);
        return this;
    }

    public CPrintWriter print(long l)
    {
        original.print(l);
        return this;
    }

    public CPrintWriter print(double d)
    {
        original.print(d);
        return this;
    }

    public CPrintWriter print(char[] s)
    {
        original.print(s);
        return this;
    }

    public CPrintWriter print(String s)
    {
        original.print(s);
        return this;
    }

    public CPrintWriter println()
    {
        original.println();
        return this;
    }

    public CPrintWriter println(boolean x)
    {
        original.println(x);
        return this;
    }

    public CPrintWriter println(char x)
    {
        original.println(x);
        return this;
    }

    public CPrintWriter println(long x)
    {
        original.println(x);
        return this;
    }

    public CPrintWriter println(double x)
    {
        original.println(x);
        return this;
    }

    public CPrintWriter println(char[] x)
    {
        original.println(x);
        return this;
    }

    public CPrintWriter println(String x)
    {
        original.println(x);
        return this;
    }

    public CPrintWriter printf(String format, Object... args)
    {
        original.printf(format, args);
        return this;
    }

    public CPrintWriter printf(Locale l, String format, Object... args)
    {
        original.printf(l, format, args);
        return this;
    }

    public CPrintWriter format(String format, Object... args)
    {
        original.format(format, args);
        return this;
    }

    public CPrintWriter format(Locale l, String format, Object... args)
    {
        original.format(l, format, args);
        return this;
    }

    public CPrintWriter append(CharSequence csq)
    {
        original.append(csq);
        return this;
    }

    public CPrintWriter append(CharSequence csq, int start, int end)
    {
        original.append(csq, start, end);
        return this;
    }

    public CPrintWriter append(char c)
    {
        original.append(c);
        return this;
    }

    // 获取原始的PrintWriter对象
    public PrintWriter getOriginal()
    {
        return original;
    }

    @Override
    public void onClose()
    {
    }
}
