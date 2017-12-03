package com.mu.smartdiffer;
//import org.apache.lucene.queryparser.classic.ParseException;


import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
/**
 * Created by arulsmv on 2/12/17.
 */
public class Gitdiffer {
    public Gitdiffer() {
    }
    public static void main(String[] args) {
        Gitdiffer differ = new Gitdiffer();
        boolean success = true;
        ArgumentParser argumentParser = ArgumentParsers.newFor("differ")
                .build().defaultHelp(true)
                .description("Differ");
        argumentParser.addArgument("-f", "--from")
                .help("From commit id");
        argumentParser.addArgument("-t", "--to")
                .help("To commit id");
        argumentParser.addArgument("-o","--out" )
                .help("output file");
        Namespace ns = null;
        try {
            ns = argumentParser.parseArgs(args);
        } catch (ArgumentParserException e) {
            argumentParser.handleError(e);
            System.exit(1);
        }

        Runtime rt = Runtime.getRuntime();
        Process proc = null;
        try {
            proc = rt.exec("git diff " + ns.getString("from")
                    + " " + ns.getString("to"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream stdin = proc.getInputStream();
        InputStreamReader isr = new InputStreamReader(stdin);
        BufferedReader br = new BufferedReader(isr);

        List<String> output = new ArrayList<String>();
                ;
        String line = null;
        try {
            while ( (line = br.readLine()) != null){
                output.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            int exitval= proc.waitFor();
            System.out.print(output.toString());
            String file1;
            String file2;
            for (/*String */line : output) {

                if (line.startsWith("---")) {
                    String [] parts = line.split(" ");
                    assert parts.length == 2;
                    file1 = parts[1].replaceFirst("a/" ,"");
                }
                if( line.startsWith("+++")) {
                String [] parts = line.split(" ");
                assert parts.length == 2;
                file2 = parts[1].replaceFirst("b/" ,"");
                }
                if (line.startsWith("@@")) {
                    String [] parts = line.split(" ");
                    assert parts.length == 2;
                    file2 = parts[1].replaceFirst("b/" ,"");
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
