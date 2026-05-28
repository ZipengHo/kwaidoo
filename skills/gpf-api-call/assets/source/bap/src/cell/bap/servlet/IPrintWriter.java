package cell.bap.servlet;

import java.util.Locale;

import cell.ResourceCellIntf;

public interface IPrintWriter extends ResourceCellIntf
{

        void flush();

        boolean checkError();

        IPrintWriter write(int c);

        IPrintWriter write(char[] buf, int off, int len);

        IPrintWriter write(char[] buf);

        IPrintWriter write(String s, int off, int len);

        IPrintWriter write(String s);

        IPrintWriter print(boolean b);

        IPrintWriter print(char c);

        IPrintWriter print(long l);

        IPrintWriter print(double d);

        IPrintWriter print(char[] s);

        IPrintWriter print(String s);

        IPrintWriter println();

        IPrintWriter println(boolean x);

        IPrintWriter println(char x);

        IPrintWriter println(long x);

        IPrintWriter println(double x);

        IPrintWriter println(char[] x);

        IPrintWriter println(String x);

        IPrintWriter printf(String format, Object... args);

        IPrintWriter printf(Locale l, String format, Object... args);

        IPrintWriter format(String format, Object... args);

        IPrintWriter format(Locale l, String format, Object... args);

        IPrintWriter append(CharSequence csq);

        IPrintWriter append(CharSequence csq, int start, int end);

        IPrintWriter append(char c);
    }
