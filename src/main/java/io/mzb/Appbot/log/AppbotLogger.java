package io.mzb.Appbot.log;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AppbotLogger extends PrintStream {

    /*
    This class just overrides the print stream
    so make all console output also get printed to a file
     */

    private PrintStream console;

    /**
     * Logger init
     * @param printStream The file print stream for the log
     * @param console The print stream for the console
     * @throws FileNotFoundException The log file is not found, should be created first!
     */
    public AppbotLogger(PrintStream printStream, PrintStream console) throws FileNotFoundException {
        super(printStream);
        this.console = console;
    }

    @Override
    public void print(boolean b) {
        handleOut(b);
    }

    @Override
    public void print(char c) {
        handleOut(c);
    }

    @Override
    public void print(double d) {
        handleOut(d);
    }

    @Override
    public void print(float f) {
        handleOut(f);
    }

    @Override
    public void print(int i) {
        handleOut(i);
    }

    @Override
    public void print(long l) {
        handleOut(l);
    }

    @Override
    public void print(Object obj) {
        handleOut(obj);
    }

    @Override
    public void print(char[] s) {
        handleOut(s);
    }

    @Override
    public void println() {
        handleOut("");
    }

    @Override
    public void println(boolean x) {
        handleOut(x);
    }

    @Override
    public void println(char x) {
        handleOut(x);
    }

    @Override
    public void println(int x) {
        handleOut(x);
    }

    @Override
    public void println(long x) {
        handleOut(x);
    }

    @Override
    public void println(float x) {
        handleOut(x);
    }

    @Override
    public void println(double x) {
        handleOut(x);
    }

    @Override
    public void println(char[] x) {
        handleOut(x);
    }

    @Override
    public void println(String x) {
        handleOut(x);
    }

    @Override
    public void println(Object x) {
        handleOut(x);
    }

    @Override
    public PrintStream printf(String format, Object... args) {
        handleOut(String.format(format, args));
        return this;
    }

    @Override
    public PrintStream printf(Locale l, String format, Object... args) {
        handleOut(String.format(format, args));
        return this;
    }

    /**
     * Handles the output to the console and the file
     * Inserts the correct time and thread the message was sent from
     * @param print Object to be printed
     */
    private void handleOut(Object print) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // [Current Date] [Thread Name] Log Information
        String current = String.format("[%s] [%s] %s", sdf.format(new Date()), Thread.currentThread().getName(), print.toString());
        // Print to file
        super.println(current);
        // Print to console
        console.println(current);
    }

}
